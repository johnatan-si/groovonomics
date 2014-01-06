import groovy.json.JsonSlurper

import org.apache.commons.math3.stat.correlation.SpearmansCorrelation
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics

import carlosgsouza.groovonomics.typing_usage.DeclarationCount

public class FileChangeCountVersusTypingSize {
	
	JsonSlurper slurper = new JsonSlurper()
	
	File classesFolder = new File("/Users/carlosgsouza/workspace_gg/groovonomics/articles/aosd_2014/data/type_usage/classes")
	File commitsFolder = new File("/opt/groovonomics/data/type_usage/commits")
	
	File outputFile = new File("/opt/groovonomics/sample_data/typing_usage/file_changes")
	
	def finishHim() {
		outputFile.delete()
		
		def project_commits_d = [:]
		def project_spearman = [:]
		
		def all_commits = 0L
		def max_commits = 0
		def all_files = 0
		
		commitsFolder.eachFile { projectCommitsFile ->
			try {
				def totalCommits = getTotalCommits(projectCommitsFile)
				if(totalCommits < 100) {
					return
				}
				
				all_commits += totalCommits
				max_commits = max_commits > totalCommits ? max_commits : totalCommits
				
				def file_commits = getFile_commits(projectCommitsFile)
				def file_d = getFile_d(projectCommitsFile)
				
				all_files += file_commits.size()
				
				def commits_d = getCommits_d(file_commits, file_d)
				
				project_commits_d[projectCommitsFile.name - ".json"] = commits_d 
				project_spearman[projectCommitsFile.name - ".json"] = getSpearman(commits_d)
			} catch(e) {
//				e.printStackTrace()
				// this usually happens because we don't have the classes information of some projects 
			}
		}
		
		def num_groups = 10
		
		def group_d = [:]
		num_groups.times {
			group_d[it] = new DescriptiveStatistics()
		}
		
		project_commits_d.each { project, commits_d ->
			
			def i = 0
			def total = commits_d.size()
			
			commits_d.each { commits, d ->
				def group = (int)(num_groups * i / total)
				
				group_d[group].addValue(d.mean)
				
				i++
			}
		}
		
		println "percentil\tmean\tmin\tmax\tstddev\tN"
		group_d.each { group, d ->
			def perc = (int)100 * (group/num_groups) 
			println "$perc\t${d.mean}\t${d.min}\t${d.max}\t${d.standardDeviation}\t${d.n}"
		}
		
		
		def spearman = new DescriptiveStatistics()
		project_spearman.sort{it.value}.each {
			if(it.value != Double.NaN) {
				spearman.addValue it.value
			}
		}
		
		spearman.values.each {
			println it	
		}
		
		println "Spearman"
		println "mean\tmin\tmax\tstddev\tN"
		println "${spearman.mean}\t${spearman.min}\t${spearman.max}\t${spearman.standardDeviation}\t${spearman.n}"
		
		println all_commits
		println max_commits
		println all_files
	}
	
	def getSpearman(commits_d) {
		def commits = new double[commits_d.size()]
		def d = new double[commits_d.size()]
		
		def i = 0
		commits_d.each {
			commits[i] = it.key.toDouble()
			d[i] = it.value.mean
			
			i++
		}
		
		return new SpearmansCorrelation().correlation(commits, d)
	}
	
	def getTotalCommits(projectCommitsFile) {
		def projectCommits = slurper.parseText(projectCommitsFile.text)
		return projectCommits.commits.toInteger()
	}
	
	def getCommits_d(file_commits, file_d) {
		def commits_d = [:]
		file_d.each { file, d ->
			def commits = file_commits[file]
			commits_d[commits.toInteger()] = commits_d[commits] ?: new DescriptiveStatistics()
			commits_d[commits.toInteger()].addValue(d)
		}
		
		return commits_d.sort{it.key}
	}
	
	def getFile_d(projectCommitsFile) {
		def projectId = projectCommitsFile.name - ".json"
		
		def result = [:]
		def classesFile = new File(classesFolder, projectCommitsFile.name)
		def projectClasses = slurper.parseText(classesFile.text)
		
		projectClasses.classes.each {
			def relativeLocation =  getRelativeLocation(it.location, projectId)
			
			def agregate = agregateUsage(it)
			if(agregate.s + agregate.d > 0) {
				result[relativeLocation] = agregate.DRelative
			}
		}
		
		return result
	}
	
	def agregateUsage(all) {
		def overall = new DeclarationCount()
		
		overall += all.publicMethodReturn
		overall += all.privateMethodReturn
		overall += all.protectedMethodReturn
		overall += all.publicField
		overall += all.privateField
		overall += all.protectedField
		overall += all.publicMethodParameter
		overall += all.privateMethodParameter
		overall += all.protectedMethodParameter
		overall += all.publicConstructorParameter
		overall += all.privateConstructorParameter
		overall += all.protectedConstructorParameter
		overall += all.localVariable
		
		return overall
	}
	
	def getFile_commits(projectCommitsFile) {
		def projectCommits = slurper.parseText(projectCommitsFile.text)
		return projectCommits.file_commits.sort{ it.value }
	}
	
	def getRelativeLocation(absolutePath, projectId) {
		try {
			def m = absolutePath =~ /.*$projectId(.*)/
			return m[0][1].substring(1)
		} catch(e) {
			return null
		}
	}
	
	
	public static void main(String[] args) {
		def agregator = new FileChangeCountVersusTypingSize()
		agregator.finishHim()
	}
}
