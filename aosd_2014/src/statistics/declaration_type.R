library(psych)
library(sm)
library(ggplot2)
library(MASS)
library(grid)
library(BaylorEdPsych)

setwd("~/workspace_gg/groovonomics/aosd_2014/analysis")

data_all<-read.table("parsed/declaration_by_type.txt", header=T)
data_all<-data_all[data_all$loc>0 & !is.na(data_all$all), ]
describe(data_all)

languages<-read.table("parsed/languages.txt")
languages$V2 <- factor(languages$V1, as.character(languages$V1))

declaration_metadata<-read.table("parsed/declaration_metadata.txt", header=T)
declaration_metadata<-declaration_metadata[declaration_metadata$loc>0 & !is.na(data_all$all), ]
describe(declaration_metadata)

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
data_size[data_size$loc<=2000 | data_size$commits<=100 | data_size$age<=100, ]$condition="1-small"
data_size[data_size$loc>2000 & data_size$commits>100 & data_size$age>100, ]$condition="2-big"


smallData=data_size[data_size$condition=="1-small", ]
bigData=data_size[data_size$condition=="2-big", ]

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
		
		"all",

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
 	values<-replace(values, values==1.0, 0.999)
 	
 	plot<-ggplot(values, aes_string(x=colname)) + 
 		geom_histogram(binwidth=0.05) + 
 		ylab("Number of projects") + 
 		xlab(paste("Relative usage of types")) + 
 		xlim(0,1.00) +
 		theme(plot.margin=unit(c(0,0,0,0),"mm"))
	
	ggsave(path=paste("result/", folder, "/histograms/", sep=""), filename=paste(index, "_", gsub("\n", "_", declarationTypeStr), ".png", sep=""), plot, height=2.5, width=5)
}


plotDeclarationTypeHistogramOfData<-function(data, folder){
	for(it in filterColumnsWithData(data, i$all:i$public )) {
		print(it)
		plotDeclarationTypeHistogram(data, folder, it )		
	}
}

uTestElementsOfASample<-function(data, folder, description, columns) {
	
	filename=paste("result/", folder, "/u-test/", gsub(" ", "_", description), ".txt", sep="")
	print(paste("Processing", filename))
	
	
	result = data.frame(sample1=character(0), sample2=character(0), pvalue=numeric(0), conf.int.min=numeric(0), conf.int.max=numeric(0))
	
	for(i in columns) { 
		for(j in columns) { 
			d_i=data[!is.na(data[,i]),i]
			d_j=data[!is.na(data[,j]),j]
			
			test<-wilcox.test(d_i, d_j, conf.int=T, conf.level=0.999)
			
			p=round(test$p.value, 4)
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
			test<-wilcox.test(d_1, d_2, conf.int=T, conf.level=0.999)
			print(test)
			
			p=round(test$p.value, 4)
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
			labs(y=paste("Relative usage of types"), x="") + 
			scale_fill_grey(start=0.4, end=1, name="", labels=labels) +
			theme(legend.position="bottom", axis.title.y=element_blank(), plot.margin=unit(c(0,0,0,0),"mm"))
			
	n <- length(unique(d$condition))
	height <- 1.0 + 0.3 * length(columns) * n
	
			
	ggsave(path=paste("result/", folder, "/comparison/boxplots", sep=""), filename=paste(columns, "_", gsub(" ", "_", description), ".png", sep=""), plot, width=4.5, height=max(3.0, height))
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
			labs(y=paste("Relative usage of types")) +
			theme(axis.title.y=element_blank(), plot.margin=unit(c(0,0,0,0),"mm"))
	
	ggsave(path=paste("result/", folder, "/boxplots/",  sep=""), filename=paste(columns, "_", gsub(" ", "_", description), ".png", sep=""), plot, width=5, height=0.8 +length(columns)*0.3)
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
	
	# uTestSamples(scriptData, classData, "script", "class", "script", c(i$all, i$localVariable:i$methodParameter))
	
	# Programmers background
	comparisonBoxPlot(data_background_all, "background", c("Dynamically\nTyped Only", "Statically and\nDynamically Typed", "Statically\nTyped Only"), "all declarations",			i$all)
	comparisonBoxPlot(data_background_all, "background", c("Dynamically\nTyped Only", "Statically and\nDynamically Typed", "Statically\nTyped Only"), "declarations by type",		i$localVariable:i$field)
	comparisonBoxPlot(data_background_all, "background", c("Dynamically\nTyped Only", "Statically and\nDynamically Typed", "Statically\nTyped Only"), "returns of methods",			i$privateMethodReturn:i$publicMethodReturn)
	comparisonBoxPlot(data_background_all, "background", c("Dynamically\nTyped Only", "Statically and\nDynamically Typed", "Statically\nTyped Only"), "parameters of methods",		i$privateMethodParameter:i$publicMethodParameter)
	comparisonBoxPlot(data_background_all, "background", c("Dynamically\nTyped Only", "Statically and\nDynamically Typed", "Statically\nTyped Only"), "parameters of constructors",	i$privateConstructorParameter:i$publicConstructorParameter)
	comparisonBoxPlot(data_background_all, "background", c("Dynamically\nTyped Only", "Statically and\nDynamically Typed", "Statically\nTyped Only"), "fields", 					i$privateField:i$publicField)
	comparisonBoxPlot(data_background_all, "background", c("Dynamically\nTyped Only", "Statically and\nDynamically Typed", "Statically\nTyped Only"), "declarations by visibility",	i$private:i$public)
	
	# uTestSamples(staticBackgroundData, dynamicBackgroundData,			"static", "dynamic",			"background", i$all:i$public)
	# uTestSamples(staticBackgroundData, staticAndDynamicBackgroundData,	"static", "static-and-dynamic",	"background", i$all:i$public)
	# uTestSamples(staticAndDynamicBackgroundData, dynamicBackgroundData,	"static-and-dynamic", "dynamic", "background", i$all:i$public)
	
	# Size
	comparisonBoxPlot(data_size, "size", c("Other Projects", "Mature Projects"), "all declarations",			i$all)
	comparisonBoxPlot(data_size, "size", c("Other Projects", "Mature Projects"), "declarations by type",		i$localVariable:i$field)
	comparisonBoxPlot(data_size, "size", c("Other Projects", "Mature Projects"), "returns of methods",			i$privateMethodReturn:i$publicMethodReturn)
	comparisonBoxPlot(data_size, "size", c("Other Projects", "Mature Projects"), "parameters of methods",		i$privateMethodParameter:i$publicMethodParameter)
	comparisonBoxPlot(data_size, "size", c("Other Projects", "Mature Projects"), "parameters of constructors",	i$privateConstructorParameter:i$publicConstructorParameter)
	comparisonBoxPlot(data_size, "size", c("Other Projects", "Mature Projects"), "fields", 					i$privateField:i$publicField)
	comparisonBoxPlot(data_size, "size", c("Other Projects", "Mature Projects"), "declarations by visibility",	i$private:i$public)
	
	# uTestSamples(bigData, smallData,	"big", "small", "size", i$all:i$public)

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
characterizeDataset<-function(data, folder) {
	result = data.frame(element=character(0), mean=numeric(0), sd=numeric(0), q1=numeric(0), q3=numeric(0), max=numeric(0), total=numeric(0))
	
	for(c in 2:4) { 
		d=data[!is.na(data[c]), c]
		
		element=colnames(data)[c]
		
		mean=round(mean(d), 2)
		sd=round(sd(d), 2)
		q1=round(quantile(d, .25), 2)
		median=round(median(d), 2)
		q3=round(quantile(d, .75), 2)
		max=round(max(d), 2)
		total=sum(d)
		
		result <- rbind(result, data.frame(element=element, mean=mean, sd=sd, q1=q1, median=median, q3=q3, max=max, total=total) )
	}
	
	write.matrix(result ,file=paste("result/", folder, "/characterization/metrics.txt", sep=""))
	write(nrow(data) ,file=paste("result/", folder, "/characterization/count.txt", sep=""))
}

analyzeSample<-function(data, description) {
	# characterizeDataset(data, description)
	# plotDeclarationTypeHistogramOfData(data, description)
	compareAllElementsOfASample(data, description)	
}

analyzeSample(data_all, "all")

analyzeSample(smallData, "size/small")
analyzeSample(bigData, "size/big")

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
	ggsave(path=paste("result/", sep=""), filename="change_commits_distribution.png", height=2.2, width=4.4)
}
plotSpearmanDistributionHistogram(change_commit_spearman)

characterizeDeclarationMetadata<-function() {
	result = data.frame(element=character(0), mean=numeric(0), sd=numeric(0), q1=numeric(0), q3=numeric(0), max=numeric(0), total=numeric(0))
	
	for(c in i$all:i$public) { 
		d=declaration_metadata[!is.na(declaration_metadata[c]), c]
		
		element=colnames(declaration_metadata)[c]
		
		mean=round(mean(d), 2)
		sd=round(sd(d), 2)
		q1=round(quantile(d, .25), 2)
		median=round(median(d), 2)
		q3=round(quantile(d, .75), 2)
		max=round(max(d), 2)
		total=sum(d)
		
		result <- rbind(result, data.frame(element=element, mean=mean, sd=sd, q1=q1, median=median, q3=q3, max=max, total=total) )
	}
	
	write.matrix(result ,file=paste("result/all/characterization/declarations.txt", sep=""))
}
characterizeDeclarationMetadata()

plotLanguageDistribution<-function(){
	plot<-qplot(V2, data=languages) + 
			coord_flip() +
			ylab(paste("Number of Developers")) + 
			theme(plot.margin=unit(c(0,5,0,0),"mm"), axis.title.y=element_blank())
			
	ggsave(path=paste("result/", sep=""), filename="languages.png", height=3.5, width=4.4)
}
plotLanguageDistribution()




anova<-function(d, folder) {
	aov.model <- aov(U~T, data=d)	
	
	sink(file=paste("result/all/anova/", folder, "/summary.txt", sep=""))
	print(summary(aov.model))
	sink()
		
	sink(file=paste("result/all/anova/", folder, "/eta_sq.txt", sep=""))
	print(EtaSq(aov.model))
	sink()
		
	sink(file=paste("result/all/anova/", folder, "/tukey_hsd.txt", sep=""))
	print(TukeyHSD(aov.model, conf.level=0.999))
	sink()
}


anovaDeclarationType<-function() {
	lv <- data_all[!is.na(data_all[,6]),6]
	mr <- data_all[!is.na(data_all[,7]),7]
	mp <- data_all[!is.na(data_all[,8]),8]
	cp <- data_all[!is.na(data_all[,9]),9]
	fd <- data_all[!is.na(data_all[,10]),10]

	d <- data.frame(
			U=c(lv, mr, mp, cp, fd),
			T=factor(rep(c("LV", "MR", "MP", "CP", "Fd"), times=c( length(lv), length(mr), length(mp), length(cp), length(fd) ) ))
		)
		
	anova(d, "declaration_type")
}

anovaDeclarationVisibility<-function() {
	pri <- data_all[!is.na(data_all[,23]),23]
	pro <- data_all[!is.na(data_all[,24]),24]
	pub <- data_all[!is.na(data_all[,25]),25]

	d <- data.frame(
			U=c(pri, pro, pub),
			T=factor(rep(c("pri", "pro", "pub"), times=c( length(pri), length(pro), length(pub) ) ))
		)
	anova(d, "visibility")
}

anovaDeclarationType()
anovaDeclarationVisibility()



factorialAnova<-function(d, folder) {
	aov.model <- aov(U~C*T, data=d)	
	
	sink(file=paste("result/", folder, "/summary.txt", sep=""))
	print(summary(aov.model))
	sink()
	
	sink(file=paste("result/", folder, "/eta_sq.txt", sep=""))
	print(EtaSq(aov.model))
	sink()
	
	sink(file=paste("result/", folder, "/tukey_hsd.txt", sep=""))
	print(TukeyHSD(aov.model, conf.level=0.999))
	sink()
}

factorialAnovaTests<-function() {
	lv <- data_tests_all[!is.na(data_tests_all[,6]), 6]
	mr <- data_tests_all[!is.na(data_tests_all[,7]), 7]
	mp <- data_tests_all[!is.na(data_tests_all[,8]), 8]
	cp <- data_tests_all[!is.na(data_tests_all[,9]), 9]
	fd <- data_tests_all[!is.na(data_tests_all[,10]),10]
	

	lv_condition <- factor(data_tests_all[!is.na(data_tests_all[,6]), 26], levels=c("test", "not-test"), labels=c("test", "not-test"))
	mr_condition <- factor(data_tests_all[!is.na(data_tests_all[,7]), 26], levels=c("test", "not-test"), labels=c("test", "not-test"))
	mp_condition <- factor(data_tests_all[!is.na(data_tests_all[,8]), 26], levels=c("test", "not-test"), labels=c("test", "not-test"))
	cp_condition <- factor(data_tests_all[!is.na(data_tests_all[,9]), 26], levels=c("test", "not-test"), labels=c("test", "not-test"))
	fd_condition <- factor(data_tests_all[!is.na(data_tests_all[,10]),26], levels=c("test", "not-test"), labels=c("test", "not-test"))

	d <- data.frame(
			U=c(lv, mr, mp, cp, fd),
			C=factor(c(lv_condition, mr_condition, mp_condition, cp_condition, fd_condition), levels=1:2, labels=c("test","not-test")),
			T=factor(rep(c("LV", "MR", "MP", "CP", "Fd"), times=c( length(lv), length(mr), length(mp), length(cp), length(fd) ) ))
		)
		
	factorialAnova(d, "test/comparison/anova")
}
factorialAnovaTests()

factorialAnovaScripts<-function() {
	lv <- data_scripts_all[!is.na(data_scripts_all[,6]), 6]
	mr <- data_scripts_all[!is.na(data_scripts_all[,7]), 7]
	mp <- data_scripts_all[!is.na(data_scripts_all[,8]), 8]
	

	lv_condition <- factor(data_scripts_all[!is.na(data_scripts_all[,6]), 26], levels=c("script", "not-script"), labels=c("script", "not-script"))
	mr_condition <- factor(data_scripts_all[!is.na(data_scripts_all[,7]), 26], levels=c("script", "not-script"), labels=c("script", "not-script"))
	mp_condition <- factor(data_scripts_all[!is.na(data_scripts_all[,8]), 26], levels=c("script", "not-script"), labels=c("script", "not-script"))

	d <- data.frame(
			U=c(lv, mr, mp),
			C=factor(c(lv_condition, mr_condition, mp_condition), levels=1:2, labels=c("script","not-script")),
			T=factor(rep(c("LV", "MR", "MP"), times=c( length(lv), length(mr), length(mp) ) ))
		)
		
	factorialAnova(d, "script/comparison/anova")
}
factorialAnovaScripts()

factorialAnovaBackground<-function() {
	lv <- data_background_all[!is.na(data_background_all[,6]), 6]
	mr <- data_background_all[!is.na(data_background_all[,7]), 7]
	mp <- data_background_all[!is.na(data_background_all[,8]), 8]
	cp <- data_background_all[!is.na(data_background_all[,9]), 9]
	fd <- data_background_all[!is.na(data_background_all[,10]),10]
	

	lv_condition <- factor(data_background_all[!is.na(data_background_all[,6]), 26], levels=c("static-only", "dynamic-only", "static-and-dynamic"), labels=c("static-only", "dynamic-only", "static-and-dynamic"))
	mr_condition <- factor(data_background_all[!is.na(data_background_all[,7]), 26], levels=c("static-only", "dynamic-only", "static-and-dynamic"), labels=c("static-only", "dynamic-only", "static-and-dynamic"))
	mp_condition <- factor(data_background_all[!is.na(data_background_all[,8]), 26], levels=c("static-only", "dynamic-only", "static-and-dynamic"), labels=c("static-only", "dynamic-only", "static-and-dynamic"))
	cp_condition <- factor(data_background_all[!is.na(data_background_all[,9]), 26], levels=c("static-only", "dynamic-only", "static-and-dynamic"), labels=c("static-only", "dynamic-only", "static-and-dynamic"))
	fd_condition <- factor(data_background_all[!is.na(data_background_all[,10]),26], levels=c("static-only", "dynamic-only", "static-and-dynamic"), labels=c("static-only", "dynamic-only", "static-and-dynamic"))

	d <- data.frame(
			U=c(lv, mr, mp, cp, fd),
			C=factor(c(lv_condition, mr_condition, mp_condition, cp_condition, fd_condition), levels=1:3, labels=c("static-only", "dynamic-only", "static-and-dynamic")),
			T=factor(rep(c("LV", "MR", "MP", "CP", "Fd"), times=c( length(lv), length(mr), length(mp), length(cp), length(fd) ) ))
		)
		
	factorialAnova(d, "background/comparison/anova/type")
}
factorialAnovaBackground()

factorialAnovaSize<-function() {
	lv <- data_size[!is.na(data_size[,6]), 6]
	mr <- data_size[!is.na(data_size[,7]), 7]
	mp <- data_size[!is.na(data_size[,8]), 8]
	cp <- data_size[!is.na(data_size[,9]), 9]
	fd <- data_size[!is.na(data_size[,10]),10]
	

	lv_condition <- factor(data_size[!is.na(data_size[,6]), 26], levels=c("1-small", "2-big"), labels=c("1-small", "2-big"))
	mr_condition <- factor(data_size[!is.na(data_size[,7]), 26], levels=c("1-small", "2-big"), labels=c("1-small", "2-big"))
	mp_condition <- factor(data_size[!is.na(data_size[,8]), 26], levels=c("1-small", "2-big"), labels=c("1-small", "2-big"))
	cp_condition <- factor(data_size[!is.na(data_size[,9]), 26], levels=c("1-small", "2-big"), labels=c("1-small", "2-big"))
	fd_condition <- factor(data_size[!is.na(data_size[,10]),26], levels=c("1-small", "2-big"), labels=c("1-small", "2-big"))

	d <- data.frame(
			U=c(lv, mr, mp, cp, fd),
			C=factor(c(lv_condition, mr_condition, mp_condition, cp_condition, fd_condition), levels=1:2, labels=c("1-small","2-big")),
			T=factor(rep(c("LV", "MR", "MP", "CP", "Fd"), times=c( length(lv), length(mr), length(mp), length(cp), length(fd) ) ))
		)
		
	factorialAnova(d, "size")
}
factorialAnovaSize()

factorialAnovaBackgroundByVisibility<-function() {
	pri <- data_background_all[!is.na(data_background_all[,23]),23]
	pro <- data_background_all[!is.na(data_background_all[,24]),24]
	pub <- data_background_all[!is.na(data_background_all[,25]),25]
	
	pri_condition <- factor(data_background_all[!is.na(data_background_all[,23]),26], levels=c("static-only", "dynamic-only", "static-and-dynamic"), labels=c("static-only", "dynamic-only", "static-and-dynamic"))
	pro_condition <- factor(data_background_all[!is.na(data_background_all[,24]),26], levels=c("static-only", "dynamic-only", "static-and-dynamic"), labels=c("static-only", "dynamic-only", "static-and-dynamic"))
	pub_condition <- factor(data_background_all[!is.na(data_background_all[,25]),26], levels=c("static-only", "dynamic-only", "static-and-dynamic"), labels=c("static-only", "dynamic-only", "static-and-dynamic"))
	

	d <- data.frame(
			U=c(pri, pro, pub),
			C=factor(c(pri_condition, pro_condition, pub_condition), levels=1:3, labels=c("static-only", "dynamic-only", "static-and-dynamic")),
			T=factor(rep(c("pri", "pro", "pub"), times=c( length(pri), length(pro), length(pub) ) ))
		)
		
	factorialAnova(d, "background/comparison/anova/visibility")
}
factorialAnovaBackgroundByVisibility()