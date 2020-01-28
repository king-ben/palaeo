library(ape)
library(phytools)
source("tip.height.R")
#read trees, remove burnin
read.nexus("palaeo_prior.trees") -> trees
trees[201:2001] -> trees
#read log file, remove burnin, extract offset column
#The offset is the age of the youngest taxon in each generation, since this has a sampled age
#offset + height = age
read.table("palaeo_prior.log", header=T) -> logfile
logfile$offset[201:2001] -> offsets
#Extract height of relevant taxa and calculate age
sapply(trees, tip.height, tip="Benneviaspis_holtedahli") -> Benneviaspis
Benneviaspis+offsets -> Benneviaspis_age
sapply(trees, tip.height, tip="Waengsjoeaspis_excellens") -> Waengsjoeaspis
Waengsjoeaspis+offsets -> Waengsjoeaspis_age
#Plot histogram of ages
pdf("Spitzbergen")
hist(Waengsjoeaspis_age, col=rgb(0.3,0.3,1, 0.7), freq=F, main="Effective prior on site age", xlab="Age (millions of years before present)")
hist(Benneviaspis_age, add=T, col=rgb(0.1,0.5,1, 0.4), freq=F)
legend(legend=c("Fraenkelryggen Formation", "Ben Nevis Formation"), fill=c(rgb(0.3,0.3,1, 0.7), rgb(0.1,0.5,1, 0.4)), x="top", bty="n", cex=1.2)
dev.off()
