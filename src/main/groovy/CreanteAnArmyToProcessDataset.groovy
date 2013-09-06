import org.apache.commons.codec.binary.Base64

import com.amazonaws.auth.PropertiesCredentials
import com.amazonaws.services.ec2.AmazonEC2Client
import com.amazonaws.services.ec2.model.CreateTagsRequest
import com.amazonaws.services.ec2.model.MonitorInstancesRequest
import com.amazonaws.services.ec2.model.RunInstancesRequest
import com.amazonaws.services.ec2.model.RunInstancesResult
import com.amazonaws.services.ec2.model.Tag


public class CreateAnArmyToProcessDataset {
	
	public static void main(args) {
		
		def ARMY_SIZE = 3
		
		def credentials = new PropertiesCredentials(new File("/opt/groovonomics/conf/aws.properties"))
		AmazonEC2Client ec2 = new AmazonEC2Client(credentials)
		
		def command = 
"""#!/bin/bash -ex
exec > >(tee /var/log/user-data.log|logger -t user-data -s 2>/dev/console) 2>&1

export PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/usr/games:/usr/local/games:/opt/gradle-1.5/bin/

cd /opt/groovonomics/src
git pull

gradle analyzeTypeUsage
"""
			
		def userData = new String(Base64.encodeBase64(command.bytes))
		
		RunInstancesRequest runInstancesRequest = new RunInstancesRequest();
		runInstancesRequest.withImageId("ami-f73c739e")
				.withInstanceType("t1.micro")
				.withMinCount(ARMY_SIZE)
				.withMaxCount(ARMY_SIZE)
				.withKeyName("carlos")
				.withSecurityGroups("carlos-personal")
				.withUserData(userData)
		
		RunInstancesResult runInstances = ec2.runInstances runInstancesRequest
		
		def count = 1
		runInstances.reservation.instances.each { instance ->
			CreateTagsRequest createTagsRequest = new CreateTagsRequest()
			createTagsRequest.withResources(instance.instanceId).withTags(new Tag("Name", "groovonomics.soldier.${count++}"))
			ec2.createTags createTagsRequest
			
			def detailedMonitoringRequest = new MonitorInstancesRequest()
			detailedMonitoringRequest.withInstanceIds(instance.instanceId)
			ec2.monitorInstances detailedMonitoringRequest
		}
	}
}