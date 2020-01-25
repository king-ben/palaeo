Preparing the tip date input files
---------------------------
The input requires two tables: one in which each fossil is assigned to a site, and a second in which the age of each site is given.
In the first table, there should be one column called **taxon**, and another called **site**. Use simple site names without spaces.
![](doc/taxon_table.png width=100)

In the second table, the first column is for the **site** and needs to match the spelling in the first table. The second and third columns are for the **upper** and **lower** bounds. The fourth column is for the operator **weight** for each site. This is the relative amount of time the analysis will attempt to update the age value for that site. In general a weight of 1 or less will suffice for every site, but in certain circumstances (e.g. a site with a lot of taxa) it might be beneficial to increase the weight. The last two columns are for the RelativeFossilSiteDateRandomWalker class only. In this case, the Fraenkelryggen and Ben Nevis formations are given the same upper and lower bounds, but the Ben Nevis formation is constrained to always have a  date. The **siteabove** and **sitebelow** inputs are used for this. Note that above and below is in terms of stratigraphy i.e. the Fraenkelryggen formation is older than the Ben Nevis formation, so is placed in the sitebelow column for the Ben Nevis formation. These inputs should always come in reciprocal pairs. It is possible to have more than two sites in a sequence, in which case sites in the middle should have both a siteabove and a sitebelow input.
![](doc/sites_table.png width=100)

Generating the xml code
---------------------------
Now we will use R to generate the xml code for the operators on the age of each fossil site. You will need to install the R package **XML**. First, set a working directory that contains the **operators.xml.R** file which can be found [here](https://github.com/king-ben/palaeo/tree/master/R_xml_tools). In the example, the code is as follows:
```
library(XML)
source("operators.xml.R")
operators.xml(taxonfile="taxa.txt", sitesfile="sites.txt", outputfile="operators.xml", treeid="tree", offset=T, offsetid="offset_tree")
```
