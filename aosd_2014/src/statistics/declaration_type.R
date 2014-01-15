library(psych)
library(sm)
library(ggplot2)
library(MASS)
library(grid)

setwd("~/workspace_gg/groovonomics/aosd_2014/analysis")

data_all<-read.table("parsed/declaration_by_type.txt", header=T)
data_all<-data_all[data_all$loc>0 & !is.na(data_all$all), ]
describe(data_all)

data_tests_all<-read.table("parsed/declaration_by_tests.txt", header=T)
data_tests_all<-data_tests_all[data_tests_all$loc>0, ]
describe(data_tests_all)

data_scripts_all<-read.table("parsed/declaration_by_scripts.txt", header=T)
data_scripts_all<-data_scripts_all[data_scripts_all$loc>0, ]
describe(data_scripts_all)

data_background_all<-read.table("parsed/declaration_by_background.txt", header=T)
data_background_all<-data_background_all[data_background_all$loc>0, ]
describe(data_background_all)

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

change_commit_spearman = read.table("parsed/change_commit_spearman.txt")

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

		"Local\nVariable",
		"Method\nReturn",
		"Method\nParameter",
		"Constructor\nParameter",
		"Field",

		"Private",
		"Protected",
		"Public",

		"Private",
		"Protected",
		"Public",

		"Private",
		"Protected",
		"Public",

		"Private",
		"Protected",
		"Public",

		"Private",
		"Protected",
		"Public"
)

plotDeclarationTypeHistogram<-function(data, folder, index){
	declarationTypeStr<-label[index]
 	values<-data[!is.na(data[,index]),]
 	colname<-colnames(data)[index]
 	
 	# Unify the intervals of [0.95,1.0[ and [1.0,1.05[ since this last one only contains values == 1.0.
 	# This is used on these histograms only
 	values<-replace(values, values==1.0, 0.99)
 	
 	plot<-ggplot(values, aes_string(x=colname)) + 
 		geom_histogram(binwidth=0.05) + 
 		ylab("Number of projects") + 
 		xlab(paste("Relative use of types in declarations")) + 
 		xlim(0,1.00) +
 		theme(plot.margin=unit(c(0,0,0,0),"mm"))
	
	ggsave(path=paste("result/", folder, "/histograms/", sep=""), filename=paste(index, "_", gsub(" ", "_", declarationTypeStr), ".png", sep=""), plot, height=2.5, width=5)
}


plotDeclarationTypeHistogramOfData<-function(data, folder){
	for(it in filterColumnsWithData(data, i$all:i$public )) {
		print(it)
		plotDeclarationTypeHistogram(data, folder, it )		
	}
}
plotDeclarationTypeHistogram(data_all, "all", 5)

uTestElementsOfASample<-function(data, folder, description, columns) {
	
	filename=paste("result/", folder, "/u-test/", gsub(" ", "_", description), ".txt", sep="")
	print(paste("Processing", filename))
	
	
	result = data.frame(sample1=character(0), sample2=character(0), pvalue=numeric(0), conf.int.min=numeric(0), conf.int.max=numeric(0))
	
	for(i in columns) { 
		for(j in columns) { 
			d_i=data[!is.na(data[,i]),i]
			d_j=data[!is.na(data[,j]),j]
			
			test<-wilcox.test(d_i, d_j, conf.int=T)
			
			p=round(test$p.value, 3)
			conf.int.min=round(test$conf.int[1], 2)
			conf.int.max=round(test$conf.int[2], 2)
			
			result <- rbind(result, data.frame(sample1=colnames(data)[i], sample2=colnames(data)[j], pvalue=p, conf.int.min=conf.int.min, conf.int.max=conf.int.max))
		}
	}
	
	write.matrix(result ,file=filename)
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

comparisonBoxPlot<-function(data, folder, labels, description, columns) {
	d <- data.frame(label=character(0), value=numeric(0), condition=character(0))
	
	for(c in columns) {
		filteredData <- data[!is.na(data[c]), ]
		d <- rbind( d, data.frame( label=label[c], value=filteredData[,c], condition=filteredData$condition ) )
	}
	
	plot<-ggplot(d, aes(label, value, fill=condition)) + 
			geom_boxplot(outlier.size=0) + 
			coord_flip() + 
			labs(y=paste("Use of types in", description), x="") + 
			scale_fill_grey(start=0.4, end=1, name="", labels=labels) +
			theme(legend.position="bottom", axis.title.y=element_blank(), plot.margin=unit(c(0,0,0,0),"mm"))
			
	ggsave(path=paste("result/", folder, "/comparison/boxplots", sep=""), filename=paste(columns, "_", gsub(" ", "_", description), ".png", sep=""), plot, width=4.5, height=max(3.0, 1.2*length(columns)*length(unique(d$condition))/2))
}

compareAllSamples<-function() {
	
	# Tests classes X Main classes
	comparisonBoxPlot(data_tests_all, "test", c("Main classes", "Test classes"), "all declarations",			i$all)
	comparisonBoxPlot(data_tests_all, "test", c("Main classes", "Test classes"), "declarations by type",		i$localVariable:i$field)
	comparisonBoxPlot(data_tests_all, "test", c("Main classes", "Test classes"), "returns of methods",			i$privateMethodReturn:i$publicMethodReturn)
	comparisonBoxPlot(data_tests_all, "test", c("Main classes", "Test classes"), "parameters of methods",		i$privateMethodParameter:i$publicMethodParameter)
	comparisonBoxPlot(data_tests_all, "test", c("Main classes", "Test classes"), "parameters of constructors",	i$privateConstructorParameter:i$publicConstructorParameter)
	comparisonBoxPlot(data_tests_all, "test", c("Main classes", "Test classes"), "fields", 						i$privateField:i$publicField)
	comparisonBoxPlot(data_tests_all, "test", c("Main classes", "Test classes"), "declarations by visibility",	i$private:i$public)
	
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
	comparisonBoxPlot(data_background_all, "background", c("Dynamically\nTyped Only", "Statically and\nDynamically Typed", "Statically\nTyped Only"), "declarations by visibility",	i$private:i$public)
	
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
	comparisonBoxPlot(data_size, "size", c("Small\nProjects", "Medium\nProjects", "Big\nProjects", "Very Big\nProjects"), "declarations by visibility",	i$private:i$public)

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
		# uTestElementsOfASample(data, folder, description, columnsWithData)
		# boxPlot(data, folder, description, columnsWithData)	
		getDescriptiveStatistics(data, folder, description, columnsWithData)	
	}
}

getDescriptiveStatistics<-function(data, folder, description, columns){
	result = data.frame(element=character(0), n=numeric(0), mean=numeric(0), median=numeric(0), sd=numeric(0))
	
	for(c in columns) { 
		d=data[!is.na(data[c]), c]
		
		element=colnames(data)[c]
		n=length(d)
		mean=round(mean(d), 2)
		sd=round(sd(d), 2)
		median=round(median(d), 2)
		
		result <- rbind(result, data.frame(element=element, n=n, mean=mean, median=median, sd=sd) )
	}
	
	write.matrix(result ,file=paste("result/", folder, "/descriptive/", gsub(" ", "_", description), ".txt", sep=""))
}

compareAllElementsOfASample<-function(data, folder) {
	print(paste("Processing", folder, "data"))
	
	compareElementsOfASample(data, folder, "declarations by type",			i$localVariable:i$field)
	compareElementsOfASample(data, folder, "returns of methods",			i$privateMethodReturn:i$publicMethodReturn)
	compareElementsOfASample(data, folder, "parameters of methods",			i$privateMethodParameter:i$publicMethodParameter)
	compareElementsOfASample(data, folder, "parameters of constructors",	i$privateConstructorParameter:i$publicConstructorParameter)
	compareElementsOfASample(data, folder, "fields", 						i$privateField:i$publicField)
	compareElementsOfASample(data, folder, "declarations by visibility", 	i$private:i$public)
}
	
boxPlot<-function(data, folder, description, columns) {
	d <- data.frame(label=character(0), value=numeric(0))
	
	for(c in columns) {
		d <- rbind( d, data.frame(label=label[c], value=data[!is.na(data[c]), c]) )
	}
	
	plot<-ggplot(d, aes(label, value)) + 
			geom_boxplot(outlier.size=0) + 
			coord_flip() + 
			labs(y=paste("Use of types in", description)) +
			theme(axis.title.y=element_blank(), plot.margin=unit(c(0,0,0,0),"mm"))
	
	ggsave(path=paste("result/", folder, "/boxplots/",  sep=""), filename=paste(columns, "_", gsub(" ", "_", description), ".png", sep=""), plot, width=5, height=length(columns)*0.6)
}



analyzeSample<-function(data, description) {
	# plotDeclarationTypeHistogramOfData(data, description)
	compareAllElementsOfASample(data, description)	
}

analyzeSample(data_all, "all")

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


compareAllSamples()


# Corrrelation between declarations and size
correlateLOCandCommits=function() {
	corLOC = data.frame(declaration=character(0), coefficient=numeric(0), p.value=numeric(0))
	corCommits = data.frame(declaration=character(0), coefficient=numeric(0), p.value=numeric(0))
	corAge = data.frame(declaration=character(0), coefficient=numeric(0), p.value=numeric(0))

	for(i in i$localVariable:i$public) {
		filteredData=data_all[ !is.na(data_all[, i]), ]
		declarationType=colnames(data_all)[i]
		
		
		locTest=cor.test(filteredData[,2], filteredData[, i], method="spearman")
		corLOC=rbind(corLOC, data.frame(declaration=declarationType, coefficient=round(locTest$estimate, 3), p.value=round(locTest$p.value, 3)))
		
		commitsTest=cor.test(filteredData[,3], filteredData[, i], method="spearman")
		corCommits =rbind(corCommits, data.frame(declaration=declarationType, coefficient=round(commitsTest $estimate, 3), p.value=round(commitsTest $p.value, 3)))
		
		ageTest=cor.test(filteredData[,4], filteredData[, i], method="spearman")
		corAge=rbind(corAge, data.frame(declaration=declarationType, coefficient=round(ageTest$estimate, 3), p.value=round(ageTest$p.value, 3)))		
	}

	print(corLOC)
	write.matrix(corLOC ,"result/all/correlations/loc.txt")
	print(corCommits)
	write.matrix(corCommits ,"result/all/correlations/commits.txt")
	print(corAge)
	write.matrix(corAge ,"result/all/correlations/age.txt")
}
correlateLOCandCommits()


correlationOfDeclarationsOfAProject<-function(description, columns) {
	print(paste("Calculating intracorrelation of ", description))
	
	result = data.frame(element1=character(0), element2=character(0), correlation=numeric(0), p.value=numeric(0))
	
	for(i in columns) { 
		for(j in columns) { 
			if(i < j) {
				e_i = colnames(data_all)[i]
				e_j = colnames(data_all)[j]
				
				print(paste(e_i, "and", e_j))
				
				cd = data_all[!is.na(data_all[,i])&!is.na(data_all[,j]), ]
				
				test=cor.test(cd[,i], cd[,j], method="spearman")
				result=rbind(result, data.frame(element1=e_i, element2=e_j, coefficient=round(test$estimate, 3), p.value=round(test$p.value, 3)))
			}
		}
	}
	
	print(result)
	write.matrix(result, file=paste("result/all/intracorrelations/", description, ".txt", sep=""))
}

correlationOfDeclarationsOfAProject("type",						i$localVariable:i$field)
correlationOfDeclarationsOfAProject("method_return",			i$privateMethodReturn:i$publicMethodReturn)
correlationOfDeclarationsOfAProject("method_parameter",			i$privateMethodParameter:i$publicMethodParameter)
correlationOfDeclarationsOfAProject("constructor_parameter",	i$privateConstructorParameter:i$publicConstructorParameter)
correlationOfDeclarationsOfAProject("fields", 					i$privateField:i$publicField)
correlationOfDeclarationsOfAProject("visibility", 				i$private:i$public)

plotSpearmanDistributionHistogram<-function(data){
	print(describe(data))
	
	data = replace(data, data==1.0, 0.99)
	data = replace(data, data==-1.0, -0.99)
	plot<-ggplot(data, aes(x=V1)) + 
 		stat_ecdf() + 
 		ylab("Projects") + 
 		xlab(paste("Spearman Correlation")) + 
 		xlim(-1.00,1.00) +
 		theme(plot.margin=unit(c(0,0,0,0),"mm"))
	ggsave(path=paste("result/", sep=""), filename="change_commits_distribution.png", height=2, width=4)
}
plotSpearmanDistributionHistogram(change_commit_spearman)