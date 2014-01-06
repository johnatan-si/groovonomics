package carlosgsouza.groovonomics.aws;
import com.amazonaws.auth.PropertiesCredentials
import com.amazonaws.services.sqs.AmazonSQSClient
import com.amazonaws.services.sqs.model.SendMessageRequest

class FillQueue {

	public static void main(args) {
		def credentials = new PropertiesCredentials(new File("/opt/groovonomics/conf/aws.properties"))
		AmazonSQSClient sqs = new AmazonSQSClient(credentials)
		
		def queueUrl = "https://sqs.us-east-1.amazonaws.com/525294860386/groovonomics-projects"
		
		def projectsListFile = new File("/opt/groovonomics/dataset/projects/list.txt")
		
		//["12chakram_LearnGrails", "166MMX_dpdr", "3musket33rs_jsonp"].each{ projectId ->
		
		def count = 0
		projectsListFile.eachLine { projectId ->
			println "${count++}\t$projectId"
			
			def sendMsgReq = new SendMessageRequest(queueUrl, projectId)
			sqs.sendMessage sendMsgReq
		}
	}
}
