# palaeo
BEAST2 tip-date operators for fossil sites and stratigraphic sequences.
This [BEAST 2](http://www.beast2.org) package provides MCMC proposals for fossil sites, in particular it allows sampling of tip dates while keeping fossils from the same site at the same age. It also allows correct ordering of fossil sites within a sequence, while still allowing them to have overlapping age uncertainty ranges.


Building package from source
----------------------------
Ensure Apache Ant is installed.
To build this package from source, download the source code and unzip the folder. In terminal, navigate to the archive and type "ant"
This will create a "dist" folder inside the archive, within which is a zip file containing the package.
Copy this zip file to the BEAST addon directory in your computer and unzip it. To find out where the addon directory is, open beauti, File - manage packages, click on the question mark in the bottom right corner. You may also need to clear the beast class path. This is possible in the file menu of beauti.

Archive Contents
----------------

* `README.md` : this file
* `build.xml` : Ant build script
* `/examples` : Example beast2 xml files
* `/src` : source files. See below for details.
* `version.xml` : BEAST package version file.

The Java packages in the palaeo BEAST2 package are: 

### `beast.evolution.operators`
* 'FossilSiteDateRandomWalker' - An operator that takes a list of taxa from a single site or layer, and a range of ages. All taxa in the taxon list will be assigned the same age with each move. Can take sampled ancestor or offset trees
* 'RelativeFossilSiteDateRandomWalker' - An extension of FossilSiteDateRandomWalker that allows fossil sites to be ordered relative to each other, while having overlapping possible age ranges. This is useful where dates are only known for the top and bottom of a stratigraphic sequence, and fossils are known from multiple layers within this sequence. the 'taxonbelow' input is a taxon from an older layer, while the 'taxonabove' input is from a younger layer. This operator should always be used in reciprocal pairs i.e. a younger and an older fossil site with reciprocal 'taxonbelow' and 'taxonabove' inputs. Middle layers, when there is more than two, can have both inputs

### `beast.math.distributions`
* 'RelativeAgePrior' - An alternative method for ordering the ages of taxa. Takes a 'younger' and 'older' input. Use of RelativeFossilSiteDateRandomWalker is probably advised instead, as it runs faster and is more flexible
