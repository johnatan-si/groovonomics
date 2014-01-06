import groovy.json.JsonSlurper

def agregateFile = new File("data/class_agregate/agregate_by_size.json")
def agregate = new JsonSlurper().parseText(agregateFile.text)

def visibility = ["public", "private", "protected", "localVariable"]

visibility.each { givenVisibility ->
	
	println givenVisibility
	
	agregate.each { b ->
		def s = 0f
		def d = 0f
		
		b.data.each { k, v ->
			if(k.startsWith(givenVisibility)) {
				try {
					s += v.s
					d += v.d
				} catch(e) {}
			}			
		}
		
		println "$s\t$d"
	}

}