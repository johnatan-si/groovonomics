import org.apache.commons.codec.binary.Base64

import com.amazonaws.auth.PropertiesCredentials
import com.amazonaws.services.ec2.AmazonEC2Client
import com.amazonaws.services.ec2.model.CreateTagsRequest
import com.amazonaws.services.ec2.model.Instance
import com.amazonaws.services.ec2.model.RunInstancesRequest
import com.amazonaws.services.ec2.model.RunInstancesResult
import com.amazonaws.services.ec2.model.Tag

/*
 * Starts a lot of ec2 instances to process the dataset in parallel 
 */

def totalInstances = 19

def credentials = new PropertiesCredentials(new File("/opt/groovonomics/conf/aws.properties"))
AmazonEC2Client ec2 = new AmazonEC2Client(credentials)

def githubKeys = ["Y2FybG9zZ3NvdXphOmdpUjE3Jl9i", "Y2FybG9zYWdzZml0ZWM6Z2lSMTcmX2I", "Y2FybG9zYWdzOmdpUjE3Jl9i", "Y2FybG9zcGFtOmdpUjE3Jl9i"]

totalInstances.times {
	def key = githubKeys[githubKeys.size() % 4]
	
	def command = 
	"""#!/bin/sh	
sudo ubuntu

cd ~ubuntu/groovonomics
git pull

s3cmd get --config=/home/ubuntu/.s3cfg  s3://carlosgsouza.groovonomics/dataset/list/${it}.txt /opt/groovonomics/dataset/list/ --force

cp ~ubuntu/.s3cfg ~/
date >> /opt/groovonomics/log.${it}.txt 
for i in `cat /opt/groovonomics/dataset/list/${it}.txt`; do 
	groovy -cp ~ubuntu/groovonomics/build/libs/groovonomics-dependencies.jar ~ubuntu/groovonomics/scripts/carlosgsouza/groovonomics/test_coverage/GetProjectData.groovy \$i >> /opt/groovonomics/log.${it}.txt \$i 2>> /opt/groovonomics/log.${it}.txt; 
	s3cmd --config=/home/ubuntu/.s3cfg put /opt/groovonomics/log.${it}.txt s3://carlosgsouza.groovonomics/dataset/projects/log/ 
done; 
date >> /opt/groovonomics/log.${it}.txt
s3cmd --config=/home/ubuntu/.s3cfg put /opt/groovonomics/log.${it}.txt s3://carlosgsouza.groovonomics/dataset/projects/log/  
	"""
	
	def userData = new String(Base64.encodeBase64(command.bytes))
	
	RunInstancesRequest runInstancesRequest = new RunInstancesRequest();
	runInstancesRequest.withImageId("ami-913e52f8")
			.withInstanceType("t1.micro")
			.withMinCount(1)
			.withMaxCount(1)
			.withKeyName("carlos")
			.withSecurityGroups("carlos-personal")
			.withUserData(userData)

	RunInstancesResult runInstances = ec2.runInstances runInstancesRequest
	Instance instance = runInstances.reservation.instances[0]

	CreateTagsRequest createTagsRequest = new CreateTagsRequest()
	createTagsRequest.withResources(instance.instanceId)
			.withTags(new Tag("Name", "groovonomics.soldier.${it}"))

	ec2.createTags createTagsRequest 
}
