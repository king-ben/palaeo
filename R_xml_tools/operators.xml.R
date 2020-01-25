operators.xml <- function(taxonfile, sitesfile, outputfile, treeid="tree", offset=FALSE, offsetid="offset_tree"){
	
	######### INITIALISE ########
	
	#Read in the tables
	read.table(taxonfile, header=T) -> taxa
	read.table(sitesfile, header=T) -> sites
	#create list of sites
	unique(taxa$site) -> sitelist
	#Make a list, each item contains the taxa in each site
	allsites <- list()
	for(i in 1:length(sitelist)){
		allsites[i][[1]]<-as.character(taxa[which(taxa[,2] == sitelist[i]),1])
	}
	names(allsites) <- sitelist
	
	##### ERROR CHECKING #############
	
	#Check site names match between the two tables
	try(if(anyNA(match(sites$site, sitelist))) stop(paste("The following fossil site(s) in the sites file are not in the taxon file:", as.character(sites$site[which(is.na(match(sites$site, sitelist)))])), call.=FALSE))
	try(if(anyNA(match(sitelist, sites$site))) stop(paste("The following fossil site(s) in the taxon file are not in the sites file:", as.character(sitelist[which(is.na(match(sitelist, sites$site)))])), call.=FALSE))
	
	#Check names in the siteabove and siteblow columns match names in the site column
	try(if(
	anyNA(match(sites$sitebelow[which(is.na(sites$sitebelow)==FALSE)], sites$site)))
	stop("not all sites in the sitebelow column match known sites", call.=FALSE))

	try(if(
	anyNA(match(sites$siteabove[which(is.na(sites$siteabove)==FALSE)], sites$site)))
	stop("not all sites in the siteabove column match known sites", call.=FALSE))
	
	#Check that sites with a siteabove input have a reciprocal site below input, and vice versa
	which(is.na(sites$siteabove)==FALSE) -> l1
	if(length(l1)>0){
		which(is.na(sites$siteabove)==FALSE) -> l1
		for(i in 1:length(l1)){
		try(if(
		isTRUE(sites$site[l1[i]]==sites$sitebelow[which(sites$site == as.character(sites$siteabove[l1[i]]))])==FALSE)
		 stop(paste("Relative fossil sites must come in complementary pairs. the following site(s) with siteabove input do not conform to that:", as.character(sites$site[l1[i]])), call.=FALSE))
		}
	}
	which(is.na(sites$sitebelow)==FALSE) -> l2
	if(length(l2)>0){
		which(is.na(sites$sitebelow)==FALSE) -> l2
		for(i in 1:length(l2)){
		try(if(
		isTRUE(sites$site[l2[i]]==sites$siteabove[which(sites$site == as.character(sites$sitebelow[l2[i]]))])==FALSE)
		 stop(paste("Relative fossil sites must come in complementary pairs. the following site(s) with sitebelow input do not conform to that:", as.character(sites$site[l2[i]])), call.=FALSE))
		}
	}
		
	#####GENERATE XML ITEMS###########
	
	operators <- list()
	#For each site in sitelist...
	for(i in 1:length(sitelist)){
		operator <- newXMLNode("operator")
		#extract weights
		weight <- sites$weight[which(sites$site==sitelist[i])]
		#set spec. If neither siteabove nor siteblow is specified, this is FossilSiteDateRandomWalker, otherwise RelativeFossilSiteDateRandomWalker
		if(is.na(sites$siteabove[which(sites$site==sitelist[i])])==TRUE & is.na(sites$sitebelow[which(sites$site==sitelist[i])])==TRUE){
			spec="FossilSiteDateRandomWalker"
		} else{spec="RelativeFossilSiteDateRandomWalker"}
		#set sttributes for the operator
		xmlAttrs(operator) = c(id=names(allsites[i]), spec = spec, windowSize="10", tree=paste("@", treeid, sep=""), weight=weight)
		#if using offset, add treeWOffset attribute
		if(offset==TRUE){
			xmlAttrs(operator) = c(treeWOffset=paste("@", offsetid, sep=""))
		}
		#Make taxonset
		taxonset <- newXMLNode("taxonset", parent=operator)
		xmlAttrs(taxonset) = c(spec="TaxonSet")
		#For each taxon in the site...
		for(j in 1:length(allsites[[i]])){
			taxon <- newXMLNode("taxon", parent=taxonset)
			xmlAttrs(taxon) = c(idref=allsites[[i]][j])
		}
		#If siteabove is set, add the taxonabove xml node
		if(is.na(sites$siteabove[which(sites$site==sitelist[i])]) == FALSE){
			taxonabove <- newXMLNode("taxonabove", parent=operator)
			xmlAttrs(taxonabove) = c(idref=allsites[which(sitelist==as.character(sites$siteabove[which(sites$site==sitelist[i])]))][[1]][1])
		}
		#If sitebelow is set, add the taxonbelow xml node
		if(is.na(sites$sitebelow[which(sites$site==sitelist[i])]) == FALSE){
			taxonbelow <- newXMLNode("taxonbelow", parent=operator)
			xmlAttrs(taxonbelow) = c(idref=allsites[which(sitelist==as.character(sites$sitebelow[which(sites$site==sitelist[i])]))][[1]][1])
		}
		#make the FossilSiteSamplingDate xml node
		dates <- newXMLNode("FossilSiteSamplingDates", parent=operator)
		#extract the upper and lower values
		upper <- sites$upper[which(sites$site==sitelist[i])]
		lower <- sites$lower[which(sites$site==sitelist[i])]
		#set attibutes of FossilSiteSamplingDate
		xmlAttrs(dates) = c(id=paste(names(allsites[i]), "_age", sep=""), spec="beast.evolution.tree.FossilSiteSamplingDate", upper=upper, lower=lower)
		#save each operator in a list
		operator -> operators[[i]]
	}
	
	###### GENERATE OPUTPUT XML BLOCK
	
	sink(file=outputfile)
	for(i in 1:length(sitelist)){
		print(operators[[i]])
		cat("\n")
	}
	sink()
}	









