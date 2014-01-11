library(psych)
library(sm)
library(ggplot2)
library(MASS)

setwd("~/workspace_gg/groovonomics/aosd_2014/analysis")

data<-read.table("parsed/declaration_by_type.txt", header=T)
describe(data)

matureData=data[data$loc>2000 & data$commits>100, ]
nonMatureData=data[data$loc<=2000 | data$commits<=100, ]

plotDeclarationTypeHistogram<-function(data, folder, index, declarationTypeStr){
 	values<-data[!is.na(data[,index]),index]
	plot<-qplot(values, xlab=paste("Usage of types in",  declarationTypeStr, "per project"), ylab="Number of projects", binwidth=0.05)
	ggsave(path=paste("result/histograms/", folder, sep=""), filename=paste("histogram_", gsub(" ", "_", declarationTypeStr), ".png", sep=""), plot, height=4, width=10)
}

plotDeclarationTypeHistogramOfData<-function(data, folder){
	plotDeclarationTypeHistogram(data, folder, 4, "all declarations")
	plotDeclarationTypeHistogram(data, folder, 5, "local variables")
	plotDeclarationTypeHistogram(data, folder, 6, "returns of methods")
	plotDeclarationTypeHistogram(data, folder, 7, "parameters of methods")
	plotDeclarationTypeHistogram(data, folder, 8, "returns of private methods")
	plotDeclarationTypeHistogram(data, folder, 9, "returns of protected methods")
	plotDeclarationTypeHistogram(data, folder, 10, "returns of public methods")
	plotDeclarationTypeHistogram(data, folder, 11, "parameters of private methods")
	plotDeclarationTypeHistogram(data, folder, 12, "parameters of protected methods")
	plotDeclarationTypeHistogram(data, folder, 13, "parameters of public methods")
}
plotDeclarationTypeHistogramOfData(data, "all")
plotDeclarationTypeHistogramOfData(matureData, "mature")
plotDeclarationTypeHistogramOfData(nonMatureData, "non-mature")

# Uses Mann Whitney tests to compare if two samples are equal
compareSamples<-function(filename, columnsToCompare) {
	result = data.frame(sample1=character(0), sample2=character(0), pvalue=numeric(0))
	row = 1
	
	for(i in columnsToCompare) { 
		for(j in columnsToCompare) { 
			d_i=data[!is.na(data[i,]),c(i)]
			d_j=data[!is.na(data[j,]),c(j)]
			
			test<-wilcox.test(d_i, d_j, conf.int=T)
			print(test)
			
			p=test$p.value
			
			result <- rbind(result, data.frame(colnames(data)[i], colnames(data)[j], pvalue=p))
			
			row=row+1
		}
	}
	
	write.matrix(result ,file=paste("result/u-test/", filename, ".txt", sep=""))
}

compareSamples("declaration_type", 5:7)
compareSamples("method_return_visibility", 8:10)
compareSamples("method_parameter_visibility", 11:13)


# Quasi experiment Tests x Non Tests and Scripts x Classes
data_tests<-read.table("parsed/declaration_by_tests.txt", header=T)
data_scripts<-read.table("parsed/declaration_by_scripts.txt", header=T)
qplot(localVariable, data=data_scripts, ylab="Number of projects", binwidth=0.05, facets=condition~.)

# Quasi experiment - Programmers Background
data_background<-read.table("parsed/declaration_by_background.txt", header=T)
qplot(all, data=data_background, ylab="Number of projects", binwidth=0.05, facets=condition~.)

# Corrrelation between declarations and size
correlateLOCandCommits=function() {
	corLOC = data.frame(declaration=character(0), coefficient=numeric(0))
	corCommits = data.frame(declaration=character(0), coefficient=numeric(0))

	for(i in 4:13) {
		filteredData=data[ !is.na(data[, i]), ]
		declarationType=colnames(data)[i]
		
		rankLOC=cor(filteredData[,2], filteredData[, i], method="spearman")
		rankCommits=cor(filteredData[,3], filteredData[, i], method="spearman")
		
		corLOC=rbind(corLOC, data.frame(declarationType, rankLOC))
		corCommits=rbind(corCommits, data.frame(declarationType, rankCommits))
	}

	print(corLOC)
	write.matrix(corLOC ,"result/correlation/loc.txt")
	print(corCommits)
	write.matrix(corCommits ,"result/correlation/commits.txt")
}
correlateLOCandCommits()

# qplot(localVariable, data=data_scripts, ylab="Number of projects", binwidth=0.05, facets=condition~.)

# Correlation between usage of types in different declaration types
# Shows if I tend to use types in one kind of declaration, I tend to use types in others
cor(data[!is.na(data$localVariable)&!is.na(data$methodParameter)&!is.na(data$methodReturn), 3:5])
write.matrix(correlationOfDeclarationTypes ,file="result/correlationOfDeclarationTypes.txt")


# Mature Data Analysis


compareMatureProjectsToOthers=function() {
	result = data.frame(declaration=character(0), pvalue=numeric(0))

	for(i in 4:13) {
		md = matureData[!is.na(matureData[,i]), i]
		nmd = nonMatureData[!is.na(nonMatureData[,i]), i]
		
		declarationType=colnames(data)[i]
		
		p=wilcox.test(md, nmd)$p.value
		
		result <- rbind(result, data.frame(declration=declarationType, pvalue=p))
	}
	
	write.matrix(result ,"result/u-test/mature_and_others.txt")
}

compareMatureProjectsToOthers()