library(XML)
source("operators.xml.R")
operators.xml(taxonfile="taxa.txt", sitesfile="sites.txt", outputfile="earlyverts_operators.xml", offset=T)
#full version with all options
#operators.xml(taxonfile="earlyverts_taxa.txt", sitesfile="earlyverts_sites.txt", outputfile="earlyverts_operators.xml", treeid="tree", offset=T, offsetid="offset_tree")
