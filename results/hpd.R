#Function to return highest posterior density interval of a range of values
#Default is 95%, but this can be changed
hpd <- function(posterior, p=0.95){
	unname(sort(posterior)) -> sp
	round(length(sp)*p) -> n
	ints <- vector()
	for(i in 1:round(length(sp)*(1-p))){
		append(ints, sp[i+(n-1)]-sp[i]) -> ints
		}
	c(sp[which(ints == min(ints))], sp[which(ints == min(ints))+(n-1)]) -> ci
	#More than one range of values can contain >95% of the sample
	#usually not an issue, but happens a lot when the parameter or metric is discrete e.g. sampled ancestor counts
	#Tracer will only return the interval with the lowest bounds
	#This function returns all the intervals
	if(length(ci)>2){
		list() -> ci2
		ci[1:(length(ci)/2)] -> mins
		unique(mins) -> mins
		ci[(length(ci)/2+1):length(ci)] -> maxs
		unique(maxs) -> maxs
		for(i in 1:length(mins)){
			c(mins[i], maxs[i]) -> ci2[[i]]
		}
		return(ci2)
	}
	else{
		return(ci)
	}
}

