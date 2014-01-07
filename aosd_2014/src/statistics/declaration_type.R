library(psych)
library(sm)
library(ggplot2)
library(MASS)

setwd("~/workspace_gg/groovonomics/aosd_2014/analysis")

data<-read.table("parsed/declaration_by_type.txt", header=T)
describe(data)

# Declaration Types
localVariable=data[!is.na(data$localVariable), c(2,3)]
methodReturn=data[!is.na(data$methodReturn), c(2,4)]
methodParameter=data[!is.na(data$methodParameter), c(2,5)]

privateMethodReturn=data[!is.na(data$privateMethodReturn), c(2,6)]
protectedMethodReturn=data[!is.na(data$protectedMethodReturn), c(2,7)]
publicMethodReturn=data[!is.na(data$publicMethodReturn), c(2,8)]

privateMethodParameter=data[!is.na(data$privateMethodParameter), c(2,9)]
protectedMethodParameter=data[!is.na(data$protectedMethodParameter), c(2,10)]
publicMethodParameter=data[!is.na(data$publicMethodParameter), c(2,11)]


plotDeclarationTypeHistogram<-function(data, declarationTypeStr){
	values<-data[,2]
	plot<-qplot(values, xlab=paste("Usage of types in",  declarationTypeStr, "per project"), ylab="Number of projects", binwidth=0.05)
	ggsave(path="result/", filename=paste("histogram_", gsub(" ", "_", declarationTypeStr), ".png", sep=""), plot, height=4, width=10)
}

plotDeclarationTypeHistogram(localVariable, "local variables")
plotDeclarationTypeHistogram(methodReturn, "returns of methods")
plotDeclarationTypeHistogram(methodParameter, "parameters of methods")
plotDeclarationTypeHistogram(privateMethodReturn, "returns of private methods")
plotDeclarationTypeHistogram(protectedMethodReturn, "returns of protected methods")
plotDeclarationTypeHistogram(publicMethodReturn, "returns of public methods")
plotDeclarationTypeHistogram(privateMethodParameter, "parameters of private methods")
plotDeclarationTypeHistogram(protectedMethodParameter, "parameters of protected methods")
plotDeclarationTypeHistogram(publicMethodParameter, "parameters of public methods")

# Is the usage of types/declaration type normally distributed?



# executeTTest<-function(data){
# 	t<-t.test(data[,2])
# 	t
# }
# 
# plotDeclarationTypeConfidenceInterval<-function(){
# 	executeTTest(localVariable)
# }
# plotDeclarationTypeConfidenceInterval()

# Correlation between usage of types in different declaration types
# Shows if I tend to use types in one kind of declaration, I tend to use types in others
cor(data[!is.na(data$localVariable)&!is.na(data$methodParameter)&!is.na(data$methodReturn), 3:5])
write.matrix(correlationOfDeclarationTypes ,file="result/correlationOfDeclarationTypes.txt")

wilcox.test(localVariable[,2])
