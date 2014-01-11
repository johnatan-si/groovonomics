package carlosgsouza.groovonomics.aws

public class DynamoLog {
	
	private ARN = "arn:aws:dynamodb:us-east-1:525294860386:table/groovonomics.log"

	def id = java.net.InetAddress.getLocalHost().getHostName()

	public DynamoLog() {
		println id
	}

	public static void main(args) {
		new DynamoLog()
	}
	
}
