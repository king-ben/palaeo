library(ape)
library(phytools)
library(ggplot2)
source("hpd.R")
source("tip.height.R")
#Read trees from analysis with linked tip dates, remove burnin
#Read log file and extract offsets
read.nexus("palaeo.trees") -> palaeotrees
palaeotrees[201:2001] -> palaeotrees
read.table("palaeo.log", header=T) -> palaeologfile
palaeologfile$offset[201:2001] -> poffsets
#Read trees from analysis with independent tip dates
read.nexus("sa.trees") -> satrees
satrees[201:2001] -> satrees
read.table("sa.log", header=T) -> salogfile
salogfile$offset[201:2001] -> soffsets

#extract height of fossil sites from linked tip-date analysis, calculate age from offset
sapply(palaeotrees, tip.height, tip="Brochoadmones_milesi") -> MOTH
MOTH+poffsets->MOTH_age

sapply(palaeotrees, tip.height, tip="Climatius_reticulatus") -> Turin
Turin+poffsets->Turin_age

#Extract height of taxa from independent tip-date analysis, calculate age from offset
sapply(satrees, tip.height, tip="Climatius_reticulatus") -> Climatius
Climatius+soffsets->Climatius_age

sapply(satrees, tip.height, tip="Euthacanthus_macnicoli") -> Euthacanthus
Euthacanthus+soffsets->Euthacanthus_age

sapply(satrees, tip.height, tip="Ischnacanthus_gracilis") -> Ischnacanthus
Ischnacanthus+soffsets->Ischnacanthus_age

sapply(satrees, tip.height, tip="Mesacanthus_mitchelli") -> Mesacanthus
Mesacanthus+soffsets->Mesacanthus_age

sapply(satrees, tip.height, tip="Parexus_recurvus") -> Parexus
Parexus+soffsets->Parexus_age

sapply(satrees, tip.height, tip="Brochoadmones_milesi") -> Brochoadmones
Brochoadmones+soffsets->Brochoadmones_age

sapply(satrees, tip.height, tip="Cassidiceps_vermiculatus") -> Cassidiceps
Cassidiceps+soffsets->Cassidiceps_age

sapply(satrees, tip.height, tip="Gladiobranchus_probaton") -> Gladiobranchus
Gladiobranchus+soffsets->Gladiobranchus_age

sapply(satrees, tip.height, tip="Kathemacanthus_rosulentus") -> Kathemacanthus
Kathemacanthus+soffsets->Kathemacanthus_age

sapply(satrees, tip.height, tip="Lupopsyrus_pygmaeus") -> Lupopsyrus
Lupopsyrus+soffsets->Lupopsyrus_age

sapply(satrees, tip.height, tip="Obtusacanthus_corroconis") -> Obtusacanthus
Obtusacanthus+soffsets->Obtusacanthus_age

sapply(satrees, tip.height, tip="Promesacanthus_eppleri") -> Promesacanthus
Promesacanthus+soffsets->Promesacanthus_age

sapply(satrees, tip.height, tip="Tetanopsyrus_lindoei_breviacanthias") -> Tetanopsyrus
Tetanopsyrus+soffsets->Tetanopsyrus_age

#Cleveland line plots of hpd intervals using ggplot2
ranges <- data.frame(
tax=c("MOTH", "Tetanopsyrus", "Promesacanthus", "Obtusacanthus", "Lupopsyrus", "Kathemacanthus", "Gladiobranchus",
"Cassidiceps", "Brochoadmones", "Turin Hill", "Parexus", "Mesacanthus", "Ischnacanthus", "Euthacanthus", "Climatius"),

Age=c(median(MOTH_age), median(Tetanopsyrus_age), median(Promesacanthus_age), median(Obtusacanthus_age), median(Lupopsyrus_age), median(Kathemacanthus_age),
median(Gladiobranchus_age), median(Cassidiceps_age), median(Brochoadmones_age), median(Turin_age), median(Parexus_age), median(Mesacanthus_age),
median(Ischnacanthus_age), median(Euthacanthus_age), median(Climatius_age)),

lower=c(hpd(MOTH_age)[1], hpd(Tetanopsyrus_age)[1], hpd(Promesacanthus_age)[1], hpd(Obtusacanthus_age)[1], hpd(Lupopsyrus_age)[1], hpd(Kathemacanthus_age)[1],
hpd(Gladiobranchus_age)[1], hpd(Cassidiceps_age)[1], hpd(Brochoadmones_age)[1], hpd(Turin_age)[1], hpd(Parexus_age)[1], hpd(Mesacanthus_age)[1],
hpd(Ischnacanthus_age)[1], hpd(Euthacanthus_age)[1], hpd(Climatius_age)[1]),

upper=c(hpd(MOTH_age)[2], hpd(Tetanopsyrus_age)[2], hpd(Promesacanthus_age)[2], hpd(Obtusacanthus_age)[2], hpd(Lupopsyrus_age)[2], hpd(Kathemacanthus_age)[2],
hpd(Gladiobranchus_age)[2], hpd(Cassidiceps_age)[2], hpd(Brochoadmones_age)[2], hpd(Turin_age)[2], hpd(Parexus_age)[2], hpd(Mesacanthus_age)[2],
hpd(Ischnacanthus_age)[2], hpd(Euthacanthus_age)[2], hpd(Climatius_age)[2])
)


p <- ggplot(data=ranges, mapping=aes(x=factor(tax, levels=unique(tax)), y=Age))
pdf("acanthodian_ranges.pdf")
p+geom_pointrange(mapping=aes(ymin=lower, ymax=upper), lwd=2, fatten=1.3,
colour=c(rgb(0.8,0.4,0.05, 1), rep(rgb(1,0.6,0.05, 0.4), 8), rgb(0.13,0.5,0.1, 1), rep(rgb(0.13,0.7,0.1, 0.4), 5)))+coord_flip()+
theme(axis.title.y = element_blank(), axis.text.y=element_text(face=c("bold", rep("italic", 8), "bold", rep("italic", 5))))+
scale_y_continuous(breaks=seq(412, 418, 2)) + labs(y="Age (millions of years before present)")
dev.off()
#save estimates
write.csv(ranges, file="acanthodian_ranges.csv")

#Calculate span of age estimates of the fossils in each generation when tip dates are independent
#Also calculate mean age for each site
MOTH_ages<-rbind(Tetanopsyrus_age, Promesacanthus_age, Obtusacanthus_age, Lupopsyrus_age, Kathemacanthus_age, Gladiobranchus_age, Cassidiceps_age, Brochoadmones_age)
Turin_ages<- rbind(Parexus_age, Mesacanthus_age, Ischnacanthus_age, Euthacanthus_age, Climatius_age)
span <- function(values){range(values)[2]-range(values)[1]}
MOTH_ranges <- apply(MOTH_ages, 2, span)
Turin_ranges <- apply(Turin_ages, 2, span)
MOTH_means <- apply(MOTH_ages, 2, mean)
Turin_means <- apply(Turin_ages, 2, mean)

#Save various useful statistics
sink("estimates.txt")
print("mean range of estimated ages for MOTH taxa")
mean(MOTH_ranges)
cat("\n")
print("mean range of estimated ages for Turin Hill taxa")
mean(Turin_ranges)
cat("\n")
print("median and hpd of MOTH mean from analysis using independent tip dates")
median(MOTH_means)
hpd(MOTH_means)
cat("\n")
print("median and hpd of MOTH mean from analysis using linked tip dates")
median(MOTH_age)
hpd(MOTH_age)
cat("\n")
print("median and hpd of Turin Hill mean from analysis using independent tip dates")
median(Turin_means)
hpd(Turin_means)
cat("\n")
print("median and hpd of Turin Hill mean from analysis using linked tip dates")
median(Turin_age)
hpd(Turin_age)
cat("\n")
print("posterior probability Turin Hill mean older than MOTH mean in analysis using independent tip dates")
length(which(Turin_means-MOTH_means > 0 ))/length(MOTH_means)
cat("\n")
print("posterior probability Turin Hill older than MOTH in analysis using linked tip dates")
length(which(Turin_age-MOTH_age > 0 ))/length(MOTH_age)
cat("\n")
sink()

#plot consensus trees
consensus(palaeotrees, p=0.5) -> pmr
consensus(satrees, p=0.5) -> smr

pac <- extract.clade(pmr, findMRCA(pmr, tips=c("Euthacanthus_macnicoli", "Promesacanthus_eppleri")))
sac <- extract.clade(smr, findMRCA(smr, tips=c("Euthacanthus_macnicoli", "Promesacanthus_eppleri")))

col <- rep("black", length(pac$tip.label))
col[which(pac$tip.label == "Cassidiceps_vermiculatus")] <- "red"

pdf(file="palaeo_tree.pdf")
plot(pac, tip.col=col)
dev.off()

pdf(file="sa_tree.pdf")
plot(sac, tip.col=col)
dev.off()
