package carlosgsouza.groovonomics.aws;
import org.apache.log4j.Logger
import org.apache.log4j.varia.NullAppender

import com.amazonaws.auth.PropertiesCredentials
import com.amazonaws.services.sqs.AmazonSQSClient
import com.amazonaws.services.sqs.model.SendMessageRequest

class FillQueue {

	public static void main(args) {
		removeAnnoyingLogs()
		
		def credentials = new PropertiesCredentials(new File("/opt/groovonomics/conf/aws.properties"))
		AmazonSQSClient sqs = new AmazonSQSClient(credentials)
		
		def queueUrl = "https://sqs.us-east-1.amazonaws.com/525294860386/groovonomics-projects"
		
		def projects = new File("../../analysis/data/dataset/project_ids.txt").readLines()
		
		def count = 0
		projects.each { projectId ->
			println "${count++}/$projects.size\t$projectId"
			
			def sendMsgReq = new SendMessageRequest(queueUrl, projectId)
			sqs.sendMessage sendMsgReq
		}
	}
	
	private static removeAnnoyingLogs() {
		Logger.rootLogger.removeAllAppenders();
		Logger.rootLogger.addAppender(new NullAppender());
	}
}
