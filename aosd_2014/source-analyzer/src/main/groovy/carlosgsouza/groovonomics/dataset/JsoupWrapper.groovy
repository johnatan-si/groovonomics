package carlosgsouza.groovonomics.dataset

import org.jsoup.Jsoup;

class JsoupWrapper {
	def parse(String uri) {
		def localFile = new File(uri)
		
		if(!localFile.exists()) {
			if(!uri.startsWith("http")) {
				uri = "http://github.com/$uri"
			}
			
			return Jsoup.connect(uri).get()
		} else {
			return Jsoup.parse(localFile, "UTF-8", "http://github.com/")
		}
	}
}
