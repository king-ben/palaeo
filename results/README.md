# Results
This folder contains the results and analysis scripts for the dataset of King et al. (2017).
Two main analyses are referred to. The **linked tip date** analysis uses operators from the package palaeo, to enforce taxa from the same fossil site to have the same age. The **independent tip date** analysis uses the operators from the sampled ancestors package, i.e. each taxon has independent sampling of age.

* `acanthodian_ranges.csv` : median and HPD intervals for MOTH and Turin Hill acanthodians from the independent tip-date analysis, and the sites themselves from the linked tip-date analysis.
* `acanthoian_ranges.pdf` : Fig. 2A from manuscript.
* `estimates.txt` : Various useful metrics regarding acanthodian age estimates.
* `Fig1.R` : Analysis script to produce figure 1B.
* `Fig2.R` : Analysis script to produce figure 2A,B and C, and various useful metrics.
* `hpd.R` : An R function to calculate highest posterior density intervals.
* `King17_palaeo.xml` : xml for focal analysis with tip ages linked within fossil sites.
* `King17_sa.xml` : xml for analysis with tip ages unlinked (i.e. standard sampled ancestor tip age operators).
* `palaeo_prior.log` : log file for linked tip date analysis when sampling from the prior.
* `palaeo_prior.trees` : trees file for linked tip date analysis when sampling from the prior.
* `palaeo_tree.pdf` : Figure 2C of Manuscript.
* `palaeo.log` : log file for focal linked tip date analysis. Needed to extract offsets to calculate age from height.
* `palaeo.trees` : trees file for focal linked tip date analysis.
* `sa_tree.pdf` : Figure 2B from manuscript.
* `sa.log` : log file for independent tip date analysis.
* `sa.trees` : trees file for independent tip date analysis.
* `Spitzbergen.pdf` : Figure 1B of manuscript.
* `tip.height.R` : R function to extract height of a given taxon in a tree.
