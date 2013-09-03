import com.amazonaws.auth.PropertiesCredentials
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.RestoreObjectRequest

def credentials = new PropertiesCredentials(new File("/opt/groovonomics/conf/aws.properties"))
AmazonS3Client s3 = new AmazonS3Client(credentials)

//def projectsListFile = new File("/opt/groovonomics/dataset/projects/list.txt")
//projectsListFile.eachLine 

["12chakram_LearnGrails", "166MMX_dpdr", "3musket33rs_jsonp"].each{ projectId ->
	def restoreReq = new RestoreObjectRequest("carlosgsouza.groovonomics", "dataset/projects/source/${projectId}.zip", 10)
	s3.restoreObject(restoreReq)
}

