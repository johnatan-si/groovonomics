library(psych)
library(sm)

setwd("/Users/carlosgsouza/workspace_gg/groovonomics/aosd_2014/analysis")

data<-read.table("parsed/declaration_by_type.txt")
describe(data)
