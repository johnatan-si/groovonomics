package carlosgsouza.groovonomics.aws

public class DynamoLog {
	
	private ARN = "arn:aws:dynamodb:us-east-1:525294860386:table/groovonomics.log"
	
	def id

	public DynamoLog() {
		if(id == null) {
			id = findInformationAboutThisEC2()
		}
	}
	
	def findInformationAboutThisEC2() {
		try {
			def metadataOutput = "ec2Metadata".execute()
			
			println metadataOutput
			
			println (metadataOutput =~ /^ami-id: (.+)$/)
			println (metadataOutput =~ /^ami-id: (.+)$/)[0]
			
			this.id = (metadataOutput =~ /^ami-id: (.+)$/)[0][0]
			
						
		} catch(Exception e) {
			this.id = "NA"
		}
	}

	public static void main(args) {
		new DynamoLog()
	}
	
}
