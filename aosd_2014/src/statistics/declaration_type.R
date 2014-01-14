library(psych)
library(sm)
library(ggplot2)
library(MASS)

setwd("~/workspace_gg/groovonomics/aosd_2014/analysis")

data_all<-read.table("parsed/declaration_by_type.txt", header=T)
describe(data_all)

data_tests_all<-read.table("parsed/declaration_by_tests.txt", header=T)
describe(data_tests_all)

data_scripts_all<-read.table("parsed/declaration_by_scripts.txt", header=T)
describe(data_scripts_all)

data_background_all<-read.table("parsed/declaration_by_background.txt", header=T)
describe(data_background_all)

data=data_all
allData=data

matureData=data[data$loc>2000 & data$commits>100, ]
nonMatureData=data[data$loc<=2000 | data$commits<=100, ]

testData=data_tests_all[data_tests_all$condition=="test", ]
mainData=data_tests_all[data_tests_all$condition=="not-test", ]

scriptData=data_scripts_all[data_scripts_all$condition=="script", ]
classData=data_scripts_all[data_scripts_all$condition=="not-script", ]

staticBackgroundData=data_background_all[data_background_all$condition=="static-only", ]
dynamicBackgroundData=data_background_all[data_background_all$condition=="dynamic-only", ]
staticAndDynamicBackgroundData=data_background_all[data_background_all$condition=="static-and-dynamic", ]

data_size=data_all
data_size$condition=NA
data_size[data_size$loc<=200, ]$condition="1-small"
data_size[data_size$loc>200 & data_size$loc<=2000, ]$condition="2-medium"
data_size[data_size$loc>2000 & data_size$loc<=20000, ]$condition="3-big"
data_size[data_size$loc>20000, ]$condition="4-huge"

smallData=data_size[data_size$condition=="1-small", ]
mediumData=data_size[data_size$condition=="2-medium", ]
bigData=data_size[data_size$condition=="3-big", ]
hugeData=data_size[data_size$condition=="4-huge", ]

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

	
	ggsave(path=paste("result/", folder, "/histograms/", sep=""), filename=paste(index, "_", gsub(" ", "_", declarationTypeStr), ".png", sep=""), plot, height=3, width=7)
}

plotDeclarationTypeHistogramOfData<-function(data, folder){
	for(it in filterColumnsWithData(data, i$localVariable:i$public )) {
		print(it)
		plotDeclarationTypeHistogram(data, folder, it )		
	}
}


# Uses Mann Whitney tests to compare if two samples are equal
uTestElementsOfASample<-function(data, folder, description, columns) {
	result = data.frame(sample1=character(0), sample2=character(0), pvalue=numeric(0), conf.int.min=numeric(0), conf.int.max=numeric(0))
	row = 1
	
	for(i in columns) { 
		for(j in columns) { 
			if(i < j) {
				d_i=data[!is.na(data[,i]),i]
				d_j=data[!is.na(data[,i]),j]
				
				test<-wilcox.test(d_i, d_j, conf.int=T)
				print(test)
				
				p=round(test$p.value, 3)
				conf.int.min=round(test$conf.int[1], 3)
				conf.int.max=round(test$conf.int[2], 3)
				
				result <- rbind(result, data.frame(sample1=colnames(data)[i], sample2=colnames(data)[j], pvalue=p, conf.int.min=conf.int.min, conf.int.max=conf.int.max))
				
				row=row+1
			}
		}
	}
	
	write.matrix(result ,file=paste("result/", folder, "/u-test/", gsub(" ", "_", description), ".txt", sep=""))
}

uTestSamples<-function(data1, data2, data1Description, data2Description, folder, columns) {
	print(paste("Comparing", data1Description, "and", data2Description))
	
	result = data.frame(element=character(0), pvalue=numeric(0), conf.int.min=numeric(0), conf.int.max=numeric(0))
	
	for(c in columns) { 
		element=colnames(data1)[c]
		print(element)
		
		d_1=data1[!is.na(data1[,c]),c]
		d_2=data2[!is.na(data2[,c]),c]
		
		if(length(d_1) > 1 && length(d_2) > 1) {
			test<-wilcox.test(d_1, d_2, conf.int=T)
			print(test)
			
			
			p=round(test$p.value, 3)
			conf.int.min=round(test$conf.int[1], 3)
			conf.int.max=round(test$conf.int[2], 3)
			
			result <- rbind(result, data.frame(element=element, pvalue=p, conf.int.min=conf.int.min, conf.int.max=conf.int.max))
		}
	}
	
	write.matrix(result ,file=paste("result/", folder, "/comparison/u-test/", data1Description, "_", data2Description, ".txt", sep=""))
}
uTestSamples(smallData, mediumData, "1-small", "2-medium", "size", i$all:i$public)
	
boxPlot<-function(data, folder, description, columns) {
	d <- data.frame(label=character(0), value=numeric(0))
	
	for(c in columns) {
		d <- rbind( d, data.frame(label=label[c], value=data[!is.na(data[c]), c]) )
	}
	
	plot<-ggplot(d, aes(label, value)) + 
			geom_boxplot(notch=T) + 
			coord_flip() + 
			labs(y=paste("Use of types in", description), x="")
	
	ggsave(path=paste("result/", folder, "/boxplots/",  sep=""), filename=paste(columns, "_", gsub(" ", "_", description), ".png", sep=""), plot, width=7, height=length(columns))
}

comparisonBoxPlot<-function(data, folder, labels, description, columns) {
	d <- data.frame(label=character(0), value=numeric(0), condition=character(0))
	
	for(c in columns) {
		filteredData <- data[!is.na(data[c]), ]
		d <- rbind( d, data.frame( label=label[c], value=filteredData[,c], condition=filteredData$condition ) )
	}
	
	plot<-ggplot(d, aes(label, value, fill=condition)) + 
			geom_boxplot() + 
			coord_flip() + 
			labs(y=paste("Use of types in", description), x="") + 
			scale_fill_grey(start=0.25, end=1, name="", labels=labels) +
			theme(legend.position="bottom")
			
	ggsave(path=paste("result/", folder, "/comparison/boxplots", sep=""), filename=paste(columns, "_", gsub(" ", "_", description), ".png", sep=""), plot, width=7, height=max(3.0, 1.5*length(columns)))
}

compareAllSamples<-function() {
	
	# Tests classes X Main classes
	comparisonBoxPlot(data_tests_all, "test", c("Main classes", "Test classes"), "all declarations",			i$all)
	comparisonBoxPlot(data_tests_all, "test", c("Main classes", "Test classes"), "declarations by type",		i$localVariable:i$field)
	comparisonBoxPlot(data_tests_all, "test", c("Main classes", "Test classes"), "returns of methods",			i$privateMethodReturn:i$publicMethodReturn)
	comparisonBoxPlot(data_tests_all, "test", c("Main classes", "Test classes"), "parameters of methods",		i$privateMethodParameter:i$publicMethodParameter)
	comparisonBoxPlot(data_tests_all, "test", c("Main classes", "Test classes"), "parameters of constructors",	i$privateConstructorParameter:i$publicConstructorParameter)
	comparisonBoxPlot(data_tests_all, "test", c("Main classes", "Test classes"), "fields", 						i$privateField:i$publicField)
	comparisonBoxPlot(data_tests_all, "test", c("Main classes", "Test classes"), "declarations by visibiltiy",	i$private:i$public)
	
	uTestSamples(testData, mainData, "test", "main", "test", i$all:i$public)
	
	# Scripts X Classes
	comparisonBoxPlot(data_scripts_all, "script", c("Class files", "Script files"), "all declarations",		i$all)
	comparisonBoxPlot(data_scripts_all, "script", c("Class files", "Script files"), "declarations by type",	i$localVariable:i$methodParameter)
	
	uTestSamples(scriptData, classData, "script", "class", "script", c(i$all, i$localVariable:i$methodParameter))
	
	# Programmers background
	comparisonBoxPlot(data_background_all, "background", c("Dynamically\nTyped Only", "Statically and\nDynamically Typed", "Statically\nTyped Only"), "all declarations",			i$all)
	comparisonBoxPlot(data_background_all, "background", c("Dynamically\nTyped Only", "Statically and\nDynamically Typed", "Statically\nTyped Only"), "declarations by type",		i$localVariable:i$field)
	comparisonBoxPlot(data_background_all, "background", c("Dynamically\nTyped Only", "Statically and\nDynamically Typed", "Statically\nTyped Only"), "returns of methods",			i$privateMethodReturn:i$publicMethodReturn)
	comparisonBoxPlot(data_background_all, "background", c("Dynamically\nTyped Only", "Statically and\nDynamically Typed", "Statically\nTyped Only"), "parameters of methods",		i$privateMethodParameter:i$publicMethodParameter)
	comparisonBoxPlot(data_background_all, "background", c("Dynamically\nTyped Only", "Statically and\nDynamically Typed", "Statically\nTyped Only"), "parameters of constructors",	i$privateConstructorParameter:i$publicConstructorParameter)
	comparisonBoxPlot(data_background_all, "background", c("Dynamically\nTyped Only", "Statically and\nDynamically Typed", "Statically\nTyped Only"), "fields", 					i$privateField:i$publicField)
	comparisonBoxPlot(data_background_all, "background", c("Dynamically\nTyped Only", "Statically and\nDynamically Typed", "Statically\nTyped Only"), "declarations by visibiltiy",	i$private:i$public)
	
	uTestSamples(staticBackgroundData, dynamicBackgroundData,			"static", "dynamic",			"background", i$all:i$public)
	uTestSamples(staticBackgroundData, staticAndDynamicBackgroundData,	"static", "static-and-dynamic",	"background", i$all:i$public)
	uTestSamples(dynamicBackgroundData, staticAndDynamicBackgroundData,	"dynamic", "static-and-dynamic","background", i$all:i$public)
	
	# Size
	comparisonBoxPlot(data_size, "size", c("Small\nProjects", "Medium\nProjects", "Big\nProjects", "Very Big\nProjects"), "all declarations",			i$all)
	comparisonBoxPlot(data_size, "size", c("Small\nProjects", "Medium\nProjects", "Big\nProjects", "Very Big\nProjects"), "declarations by type",		i$localVariable:i$field)
	comparisonBoxPlot(data_size, "size", c("Small\nProjects", "Medium\nProjects", "Big\nProjects", "Very Big\nProjects"), "returns of methods",			i$privateMethodReturn:i$publicMethodReturn)
	comparisonBoxPlot(data_size, "size", c("Small\nProjects", "Medium\nProjects", "Big\nProjects", "Very Big\nProjects"), "parameters of methods",		i$privateMethodParameter:i$publicMethodParameter)
	comparisonBoxPlot(data_size, "size", c("Small\nProjects", "Medium\nProjects", "Big\nProjects", "Very Big\nProjects"), "parameters of constructors",	i$privateConstructorParameter:i$publicConstructorParameter)
	comparisonBoxPlot(data_size, "size", c("Small\nProjects", "Medium\nProjects", "Big\nProjects", "Very Big\nProjects"), "fields", 					i$privateField:i$publicField)
	comparisonBoxPlot(data_size, "size", c("Small\nProjects", "Medium\nProjects", "Big\nProjects", "Very Big\nProjects"), "declarations by visibiltiy",	i$private:i$public)

	uTestSamples(smallData, mediumData, "1-small", "2-medium", "size", i$all:i$public)
	uTestSamples(smallData, bigData,	"1-small", "3-big", "size", i$all:i$public)
	uTestSamples(smallData, hugeData,	"1-small", "4-huge", "size", i$all:i$public)
	uTestSamples(mediumData, bigData,	"2-medium", "3-big", "size", i$all:i$public)
	uTestSamples(mediumData, hugeData,	"2-medium", "4-huge", "size", i$all:i$public)
	uTestSamples(bigData, hugeData,		"3-big", "4-huge", "size", i$all:i$public)

}

filterColumnsWithData<-function(data, columns) {
	columnsWithData=numeric()
	for(c in columns) {
		if(length(data[!is.na(data[, c]), c]) > 1) {
			columnsWithData <- c(columnsWithData, c)
		}
	}
	columnsWithData
}

compareElementsOfASample<-function(data, folder, description, columnsToCompare) {
	print(paste("Comparing samples of", description))
	
	columnsWithData<-filterColumnsWithData(data, columnsToCompare)
	
	if(length(columnsWithData) > 0) {
		uTestElementsOfASample(data, folder, description, columnsWithData)
		boxPlot(data, folder, description, columnsWithData)	
	}
}

compareAllElementsOfASample<-function(data, folder) {
	print(paste("Processing", folder, "data"))
	
	compareElementsOfASample(data, folder, "declarations by type",			i$localVariable:i$field)
	compareElementsOfASample(data, folder, "returns of methods",			i$privateMethodReturn:i$publicMethodReturn)
	compareElementsOfASample(data, folder, "parameters of methods",			i$privateMethodParameter:i$publicMethodParameter)
	compareElementsOfASample(data, folder, "parameters of constructors",	i$privateConstructorParameter:i$publicConstructorParameter)
	compareElementsOfASample(data, folder, "fields", 						i$privateField:i$publicField)
	compareElementsOfASample(data, folder, "declarations by visibiltiy", 	i$private:i$public)
}

analyzeSample<-function(data, description) {
	plotDeclarationTypeHistogramOfData(data, description)
	compareAllElementsOfASample(data, description)	
}

boxPlot(data, "all", "all combinations", i$localVariable:i$public)
boxPlot(matureData, "mature", "all combinations", i$localVariable:i$public)
boxPlot(nonMatureData, "non-mature", "all combinations", i$localVariable:i$public)

analyzeSample(allData, "all")

analyzeSample(smallData, "size/small")
analyzeSample(mediumData, "size/medium")
analyzeSample(bigData, "size/big")
analyzeSample(hugeData, "size/huge")

analyzeSample(testData, "test/test")
analyzeSample(mainData, "test/main")

analyzeSample(scriptData, "script/script")
analyzeSample(classData, "script/class")

analyzeSample(staticBackgroundData, "background/static-only")
analyzeSample(dynamicBackgroundData, "background/dynamic-only")
analyzeSample(staticAndDynamicBackgroundData, "background/static-and-dynamic")

analyzeSample(staticAndDynamicBackgroundData, "background/static-and-dynamic")

compareAllSamples()


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