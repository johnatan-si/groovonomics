import groovy.json.JsonSlurper

import java.text.SimpleDateFormat

import org.apache.commons.math3.stat.correlation.SpearmansCorrelation
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics

public class DynamicTypeVersusSize {
	
	JsonSlurper slurper = new JsonSlurper()
	SimpleDateFormat sdf = new SimpleDateFormat("y-M-dh:m:s")
	
	def makeMeSmile() {
		def metadataFolder = new File("/Users/carlosgsouza/workspace_gg/groovonomics/articles/aosd_2014/data/metadata")
		def analyzedDataFolder = new File("/Users/carlosgsouza/workspace_gg/groovonomics/articles/aosd_2014/data/type_usage/projects")
		def outputFolder = new File("/Users/carlosgsouza/workspace_gg/groovonomics/articles/aosd_2014/data/type_usage/size/")
		
		outputFolder.deleteDir()
		outputFolder.mkdirs()
		
		def declaration_dynamicUsage = [:]
		
		metadataFolder.eachFile { projectFile ->
			def projectMetadata = slurper.parseText(projectFile.text)
			
			def size = projectMetadata.loc.toInteger()
			if(size == 0) {
				return
			}

			try {
				def analyzedData = slurper.parseText(new File(analyzedDataFolder, projectFile.name).text)
				def declaration_data = analyzedData.findAll { it.key.startsWith("public") || it.key.startsWith("private") || it.key.startsWith("protected") || it.key.startsWith("localVariable") }
				
				declaration_data.each { declaration, data ->
					// This means there is no declarations of this type
					if(data.DRelative == 0 && data.SRelative == 0) {
						return
					}
					
					// The log groups projects in "buckets" on a log scale
					def logSize = (int)Math.log(size)/Math.log(2)
					// Group all projects with loc <= 128
					logSize = logSize < 7 ? 6 : logSize
					
					declaration_dynamicUsage[declaration] = declaration_dynamicUsage[declaration] ?: [:]
					declaration_dynamicUsage[declaration][logSize] = declaration_dynamicUsage[declaration][logSize] ?: new DescriptiveStatistics()
					
					declaration_dynamicUsage[declaration][logSize].addValue(data.DRelative)
				}
				
			} catch(FileNotFoundException e) {
			}
		}
		
		declaration_dynamicUsage.each { declaration, dynamicUsage ->
			def outputForDeclaration = new File(outputFolder, "${declaration}.txt")
			
			dynamicUsage.sort { it.key }.each { size, DRelativeStats ->
				def sizeDescription = (size == 6) ? "[0, 128]" : "]${(int)Math.pow(2, size)}, ${(int)Math.pow(2, size+1)}]" 
				outputForDeclaration << "${sizeDescription}\t${(int)100*DRelativeStats.mean}\n"
			}
		}
	}
	
	def getSizeArrayFor(usages) {
		def result = new double[usages.size()]
		
		usages.size().times {
			result[it] = it
		}
		return result 
	}
	
	double[] toDoubleArray(list) {
		def result = new double[list.size()]
		
		list.size().times {
			result[it] = list[it].toDouble()
		}
		return result
	}
	
	public static void main(String[] args) {
		def agregator = new DynamicTypeVersusSize()
		agregator.makeMeSmile()
	}
}
