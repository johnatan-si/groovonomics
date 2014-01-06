import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*
import groovy.transform.Field
import groovyx.net.http.*

import org.apache.commons.lang.exception.ExceptionUtils

// Configuration
@Field def baseFolder = new File("/opt/groovonomics/")
@Field def basePath = baseFolder.absolutePath
@Field def issuesFolder = new File(baseFolder, "issues")

@Field def basicAuth = new File(baseFolder, "conf/github.conf").text
@Field def requestHeaders = ["Authorization":"Basic $basicAuth", "User-Agent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_5) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31"]
@Field def gitHubClient = new RESTClient("https://api.github.com")

@Field def totalIssues = 0
@Field def metadataFolder = new File("${basePath}/dataset/projects/metadata/")

def i = 0
metadataFolder.eachFile {
	log "${i}/7269"
	getIssues((it.name =~ /(.*)\.json/)[0][1])
	
	waitABit()
}

log "Total Issues = $totalIssues"

def waitABit() {
	def sleepTime = 1000 + new Random().nextInt(2000)
	
	log "Sleeping for $sleepTime"
	Thread.sleep(sleepTime)
}

def getIssues(owner_name) {
	log "Getting issues of project $owner_name"
	
	def owner = owner_name.substring(0, owner_name.lastIndexOf("_"))
	def name = owner_name.substring(owner_name.lastIndexOf("_")+1)
	
	def outFolder = new File(issuesFolder, owner_name)
	
	try {
		
		def result = []
		
		if(!outFolder.exists()) {
			outFolder.mkdirs()
		}
		
		// Get issues
		def pageNo = 1
		def response = requestIssues(owner, name, pageNo)
		result.addAll response.data
		
		while(thereAreMoreResults(response)) {
			pageNo++
			
			response = requestIssues(owner, name, pageNo)
			result.addAll response.data
		}
		
		log "Exprting $result.size results of project $owner_name"
		result.each {
			new File(outFolder, "${it.id.toString()}.json") << it.toString()
		}
		
		totalIssues += result.size()
		
	}
	catch(e) {
		log "ERROR [project:$owner_name] $e.message"
		new File(outFolder, "error.log") << ExceptionUtils.getStackTrace(e)	
	}
}

def thereAreMoreResults(response) {
	response.headers.'Link'?.contains('rel="next"')
}

def requestIssues(owner, name, pageNo) {
	log "Requesting issues of ${owner}_${name} [page=$pageNo]"
	
	gitHubClient.get(path: "/repos/$owner/$name/issues", query:[state:"closed", page:pageNo], headers:requestHeaders, contentType:ContentType.JSON)
}

def exec(cmd) {
	def proc = cmd.execute()
	proc.consumeProcessOutput(System.out, System.err)
	proc.waitForOrKill(3600000)
}

def log(msg) {
	println "[GROOVONOMICS] $msg"
}