library(psych)
library(sm)
library(ggplot2)
library(MASS)

setwd("~/workspace_gg/groovonomics/aosd_2014/analysis")

data<-read.table("parsed/declaration_by_type.txt", header=T)
describe(data)

matureData=data[data$loc>2000 & data$commits>100, ]
nonMatureData=data[data$loc<=2000 | data$commits<=100, ]

i<-data.frame(	
				projectId=1,
				loc=2,
				commits=3,
				age=4,

				all=5, 

				localVariable=6,
				methodReturn=7,
				methodParameter=8,
				constructorParameter=9,
				field=10,
				
				privateMethodReturn=11,
				protectedMethodReturn=12,
				publicMethodReturn=13,
				
				privateMethodParameter=14,
				protectedMethodParameter=15,
				publicMethodParameter=16,
				
				privateConstructorParameter=17,
				protectedConstructorParameter=18,
				publicConstructorParameter=19,
				
				privateField=20,
				protectedField=21,
				publicField=22,
				
				private=23,
				protected=24,
				public=25
			)
		
label<-c(
		"project id",
		"LOC",
		"number of commits",
		"age",
		
		"all types",

		"local variables",
		"returns of methods",
		"parameters of methods",
		"parameters of constructors",
		"fields",

		"returns of private methods",
		"returns of protected methods",
		"returns of public methods",

		"parameters of private methods",
		"parameters of protected methods",
		"parameters of public methods",

		"parameters of private constructors",
		"parameters of protected constructors",
		"parameters of public constructors",

		"private fields",
		"protected fields",
		"public fields",

		"private fields and methods",
		"protected fields and methods",
		"public fields and methods"
)

plotDeclarationTypeHistogram<-function(data, folder, index){
	declarationTypeStr<-label[index]
 	values<-data[!is.na(data[,index]),]
 	colname<-colnames(data)[index]
 	
 	plot<-ggplot(values, aes_string(x=colname)) + 
 		geom_histogram(binwidth=0.05) + 
 		ylab("Number of projects") + 
 		xlab(paste("Usage of types in declarations of",  declarationTypeStr, "per project")) + 
 		xlim(0,1.1)

	
	ggsave(path=paste("result/histograms/", folder, sep=""), filename=paste(index, "_", gsub(" ", "_", declarationTypeStr), ".png", sep=""), plot, height=3, width=7)
}

plotDeclarationTypeHistogramOfData<-function(data, folder){
	for(it in declarationDataRange) {
		print(it)
		plotDeclarationTypeHistogram(data, folder, it )		
	}
}
plotDeclarationTypeHistogramOfData(data, "all")
plotDeclarationTypeHistogramOfData(matureData, "mature")
plotDeclarationTypeHistogramOfData(nonMatureData, "non-mature")

# Uses Mann Whitney tests to compare if two samples are equal
uTest<-function(data, folder, description, columns) {
	result = data.frame(sample1=character(0), sample2=character(0), pvalue=numeric(0), difference=numeric(0))
	row = 1
	
	for(i in columns) { 
		for(j in columns) { 
			d_i=data[!is.na(data[i,]),i]
			d_j=data[!is.na(data[j,]),j]
			
			test<-wilcox.test(d_i, d_j, conf.int=T)
			print(test)
			
			p=test$p.value
			difference=test$conf.int
			
			result <- rbind(result, data.frame(sample1=colnames(data)[i], sample2=colnames(data)[j], pvalue=p, difference=difference))
			
			row=row+1
		}
	}
	
	write.matrix(result ,file=paste("result/u-test/", folder, "/", gsub(" ", "_", description), ".txt", sep=""))
}


boxPlot<-function(data, folder, description, columns) {
	d <- data.frame(label=character(0), value=numeric(0))
	
	for(c in columns) {
		d <- rbind( d, data.frame(label=label[c], value=data[!is.na(data[c]), c]) )
	}
	
	plot<-ggplot(d, aes(label, value)) + geom_boxplot(notch=T) + coord_flip() + labs(y="Use of types in declarations", x="")
	
	ggsave(path=paste("result/boxplots/", folder, sep=""), filename=paste(columns, "_", gsub(" ", "_", description), ".png", sep=""), plot, width=7, height=length(columns))
}

compareSamples<-function(data, folder, description, columnsToCompare) {
	print(paste("Comparing samples of", description))
	
	uTest(data, folder, description, columnsToCompare)
	boxPlot(data, folder, description, columnsToCompare)
}

compareAllSamples<-function(data, folder) {
	print(paste("Processing", folder, "data"))
	
	compareSamples(data, folder, "declaration by type",			i$localVariable:i$field)
	compareSamples(data, folder, "returns of methods",			i$privateMethodReturn:i$publicMethodReturn)
	compareSamples(data, folder, "parameters of methods",		i$privateMethodParameter:i$publicMethodParameter)
	compareSamples(data, folder, "parameters of constructors",	i$privateConstructorParameter:i$publicConstructorParameter)
	compareSamples(data, folder, "fields", 						i$privateField:i$publicField)
	compareSamples(data, folder, "declarations by visibiltiy", 	i$private:i$public)
}

boxPlot(data, "all", "all combinations", i$localVariable:i$public)
boxPlot(matureData, "mature", "all combinations", i$localVariable:i$public)
boxPlot(nonMatureData, "non-mature", "all combinations", i$localVariable:i$public)

compareAllSamples(data, "all")
compareAllSamples(matureData, "mature")
compareAllSamples(nonMatureData, "non-mature")

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