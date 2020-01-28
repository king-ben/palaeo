#Returns the height of a taxon
#requires phytools library
tip.height <- function(tree, tip){return(max(nodeHeights(tree))-nodeheight(tree, which(tree$tip.label == tip)))}

