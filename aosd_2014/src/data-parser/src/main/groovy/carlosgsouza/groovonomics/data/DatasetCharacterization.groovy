package carlosgsouza.groovonomics.data;
import groovy.json.JsonSlurper

import java.text.SimpleDateFormat

public class AgregateDataFromClasses {
	
	JsonSlurper slurper = new JsonSlurper()
	SimpleDateFormat sdf = new SimpleDateFormat("y-M-dh:m:s")
	
	def rockTheWorld() {
		def metadataFolder = new File("/Users/carlosgsouza/workspace_gg/groovonomics/articles/aosd_2014/data/metadata")
		
		def locDistributionFile = new File("/Users/carlosgsouza/workspace_gg/groovonomics/articles/aosd_2014/data/loc_distribution.txt")
		locDistributionFile.delete()
		
		def developersDistributionFile = new File("/Users/carlosgsouza/workspace_gg/groovonomics/articles/aosd_2014/data/developers_distribution.txt")
		developersDistributionFile.delete()
		
		def ageDistributionFile = new File("/Users/carlosgsouza/workspace_gg/groovonomics/articles/aosd_2014/data/age_distribution.txt")
		ageDistributionFile.delete()
		
		def size_counter = [:]
		def developers_counter = [:]
		def age_counter = [:]
		
		def numberOfProjects = 0
		
		metadataFolder.eachFile { projectFile ->
			def projectMetadata = slurper.parseText(projectFile.text)
			
			def size = projectMetadata.loc.toInteger()
			size_counter[size] = size_counter[size] ?: 0
			size_counter[size]++
					
			def developers = projectMetadata.contributors.size()
			developers_counter[developers] = developers_counter[developers] ?: 0
			developers_counter[developers]++
					
			def age = sdf.parse(projectMetadata.updatedAt - 'T' - 'Z') - sdf.parse(projectMetadata.createdAt - 'T' - 'Z')
			age = Math.round(Math.max(0, age)/30)
			age_counter[age] = age_counter[age] ?: 0
			age_counter[age]++
			
			numberOfProjects++
		}
		
		def totalSoFar = 0
		size_counter.sort().each { size, counter ->
			totalSoFar += counter
			
			locDistributionFile << "$size\t${totalSoFar/numberOfProjects}\n"
			counter++
		}
		
		developers_counter.sort().each { developers, counter ->
			developersDistributionFile << "$developers\t${counter/numberOfProjects}\n"
			counter++
		}
		
		totalSoFar = 0
		age_counter.sort().each { age, counter ->
			totalSoFar += counter

			ageDistributionFile << "$age\t${totalSoFar/numberOfProjects}\n"
			counter++
		}
	}

	
	public static void main(String[] args) {
		def agregator = new AgregateDataFromClasses()
		agregator.rockTheWorld()
	}
}
