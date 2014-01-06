package carlosgsouza.groovonomics.aws;
import com.amazonaws.auth.PropertiesCredentials
import com.amazonaws.services.sqs.AmazonSQSClient
import com.amazonaws.services.sqs.model.DeleteMessageRequest
import com.amazonaws.services.sqs.model.Message
import com.amazonaws.services.sqs.model.ReceiveMessageRequest


class EmptyQueue {
	public static void main(args) {
		new EmptyQueue().doIt()
	}
	
	def doIt() {
		def sqs = new SQS()
		
		def msg = sqs.nextMessage
		while(msg) {
			sqs.deleteMessage msg
			msg = sqs.nextMessage
		}
	}
	
	class SQS {
		def credentials = new PropertiesCredentials(new File("/opt/groovonomics/conf/aws.properties"))
		def queueUrl = "https://sqs.us-east-1.amazonaws.com/525294860386/groovonomics-projects"
		
		AmazonSQSClient sqs = new AmazonSQSClient(credentials)
		
		def getNextMessage() {
			def recvMsgReq = new ReceiveMessageRequest(queueUrl)
			def recvMsgResult = sqs.receiveMessage(recvMsgReq)
			def msgs = recvMsgResult.messages
			
			return msgs ? msgs.first() : null
		}
		
		def deleteMessage(Message msg) {
			def deleteReq = new DeleteMessageRequest(queueUrl, msg.receiptHandle)
			sqs.deleteMessage(deleteReq)
		}
	}
}
