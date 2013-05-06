import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*
import groovy.json.JsonBuilder
import groovy.json.JsonOutput
import groovyx.net.http.*

//import org.apache.log4j.Level
//import org.apache.log4j.LogManager
//import org.apache.log4j.Logger

/* 
 Retrieves the following data about a project
 - id
 - visibility
 - contributors
 - create/update dates
 - number of files
 - LOC
 - total size
 */


// Disable HTTPBuilder annoying log messages
//List<Logger> loggers = Collections.<Logger>list(LogManager.getCurrentLoggers());
//loggers.add(LogManager.getRootLogger());
//for ( Logger logger : loggers ) {
//	logger.setLevel(Level.OFF);
//}

try {
	// Configuration
	def baseFolder = new File("/opt/groovonomics/")
	def basePath = baseFolder.absolutePath
	def tempFolder = new File("/opt/groovonomics/temp/")
	def tempPath = tempFolder.absolutePath
	def basicAuth = new File(baseFolder, "conf/github.conf").text
	def gitHubClient = new RESTClient("https://api.github.com")
	def requestHeaders = ["Authorization":"Basic $basicAuth", "User-Agent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_5) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31"]

	// Clean work directory
	exec "rm -rf $tempPath"

	// Parse input
	def fullName = args.size() > 0 ? args[0] : "carlosgsouza/grails-karma"
	def owner = fullName.substring(0, fullName.lastIndexOf("/"))
	def name = fullName.substring(fullName.lastIndexOf("/")+1)
	def owner_name = "${owner}_${name}"

	def result = [:]

	// Get contributors list
	def response = gitHubClient.get(path: "/repos/$owner/$name/contributors", headers:requestHeaders, contentType:ContentType.JSON)
	result["contributors"] = response.data*.id

	// Get general data
	response = gitHubClient.get(path: "/repos/$owner/$name", headers:requestHeaders, contentType:ContentType.JSON)
	result["createdAt"] = response.data.created_at
	result["updatedAt"] = response.data.updated_at
	result["id"] = response.data.id
	result["isPrivate"] = response.data."private"

	// Clone to local file system
	def cloneUrl = response.data.clone_url
	def localPath = "$tempPath/$owner_name"
	exec "/usr/bin/git clone $cloneUrl $localPath"

	// Get the number of lines
	def out = new StringBuilder()
	def err = new StringBuilder()

	def proc = "/usr/bin/find $localPath -name *.groovy".execute() | "xargs cat".execute() | "wc -l".execute()
	proc.waitForProcessOutput(out, err)

	result["loc"] = out.toString().trim()

	// Get the number of files
	out = new StringBuilder()
	err = new StringBuilder()

	proc = "/usr/bin/find $localPath -name *.groovy".execute() | "wc -l".execute()
	proc.waitForProcessOutput(out, err)

	result["numberOfFiles"] = out.toString().trim()

	// Get the total size in Bytes
	out = new StringBuilder()
	err = new StringBuilder()

	proc = "/usr/bin/du -sk $localPath".execute()
	proc.waitForProcessOutput(out, err)
	def outStr = out.toString()

	result["totalSize"] = outStr.trim().substring(0, outStr.lastIndexOf("\t"))

	// Get the number of files
	out = new StringBuilder()
	err = new StringBuilder()

	proc = "/usr/bin/find $localPath -name *.groovy".execute() | "wc -l".execute()
	proc.waitForProcessOutput(out, err)

	result["numberOfFiles"] = out.toString().trim()

	// Write the result metadata to a file
	new File(tempPath, "${owner_name}.json") << JsonOutput.prettyPrint(new JsonBuilder(result).toString())

	// Zips the source files
	exec "/usr/bin/zip -r $tempPath/${owner_name}.zip $tempPath/${owner_name} >> ~/zip.txt"

	// Upload everything to S3
	exec "/usr/bin/s3cmd put -r $tempPath/${owner_name}.zip  s3://carlosgsouza.groovonomics/dataset/projects/source/ >> ~/s3.txt"
	exec "/usr/bin/s3cmd put -r $tempPath/${owner_name}.json s3://carlosgsouza.groovonomics/dataset/projects/metadata/ >> ~/s3.txt"
}
catch(e) {
	e.printStackTrace()	
}


def exec(cmd) {
	def proc = cmd.execute()
	proc.consumeProcessOutput(System.out, System.err)
	proc.waitForOrKill(3600000)
}

