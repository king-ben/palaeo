package beast.evolution.operators;

import java.util.ArrayList;
import java.util.List;
import beast.core.Description;
import beast.core.Input;
import beast.evolution.tree.Node;
import beast.evolution.alignment.Taxon;
import beast.evolution.tree.FossilSiteSamplingDate;
import beast.evolution.tree.Tree;
import beast.evolution.tree.TreeWOffset;
import beast.util.Randomizer;



@Description("Takes an input of taxa from the same fossil site and moves their height simultaneously to a random value within a given range")

public class RelativeFossilSiteDateRandomWalker extends TipDatesRandomWalker {
	// Input the tree
	public Input<TreeWOffset> treeWOffsetInput =
            new Input<TreeWOffset>("treeWOffset", "Optional fully extinct tree", (TreeWOffset)null);
	// The class SamplingDate already has a taxonset input
    public Input<List<FossilSiteSamplingDate>> samplingDatesInput = new Input<>("FossilSiteSamplingDates",
            "List of sampling dates", new ArrayList<FossilSiteSamplingDate>());
    public Input<Taxon> taxonbelow = new Input<>("taxonbelow", "taxon from lower stratigraphic level", (Taxon)null);
    public Input<Taxon> taxonabove = new Input<>("taxonabove", "taxon from higher stratigraphic level", (Taxon)null);

    TreeWOffset combinedTree;
    int belowindex;
    int aboveindex;
    Node belownode;
    Node abovenode;
    double belowheight;
    double aboveheight;
    //String type = new String(reftype.get().toString());

    @Override
    public void initAndValidate() {
    	
        //type = reftype.get().toString();
    	combinedTree = treeWOffsetInput.get();
        if(combinedTree == null) {
        	combinedTree = new TreeWOffset();
        	combinedTree.setInputValue("tree", treeInput.get());
        	combinedTree.initAndValidate();
        }
    	
        //windowSize = windowSizeInput.get();
        //useGaussian = useGaussianInput.get();
        
        if (m_taxonsetInput.get() != null) {
            
        	// initially empty list for taxon names to be added to
            List<String> sTaxaNames = new ArrayList<String>();
            // Adds taxon names to the list
            for (String sTaxon : treeInput.get().getTaxaNames()) {
                sTaxaNames.add(sTaxon);
            }
            // set is the set of taxa to be sampled
            List<String> set = m_taxonsetInput.get().asStringList();
            // nNrOfTaxa stores the number of taxa to be sampled
            int nNrOfTaxa = set.size();
            // An array of integers corresponding to the taxon indices
            taxonIndices = new int[nNrOfTaxa];
            int k = 0;
            // For each taxon in the set
            for (String sTaxon : set) {
            	//Store the index of the taxon
                int iTaxon = sTaxaNames.indexOf(sTaxon);
                //Throws error if taxon in the set is spelled wrong
                if (iTaxon < 0) {
                    throw new IllegalArgumentException("Cannot find taxon " + sTaxon + " in tree");
                }
                //Places the index of the taxon into the list
                taxonIndices[k++] = iTaxon;
            }
            if(taxonbelow.get() != null) {
            	belowindex = sTaxaNames.indexOf(taxonbelow.get().toString());
            }
            if(taxonabove.get() != null) {
            	aboveindex = sTaxaNames.indexOf(taxonabove.get().toString());
            }
            
        } else {
        	throw new IllegalArgumentException("RelativeFossilSiteDateRandomWalker operator requires a taxon set input");
        }
    }
    
    //Instead of randomly selecting a tip (leaf node), this operator uses taxonIndices directly
    @Override
    public double proposal() {

        Tree tree = combinedTree.getTree();
        
        
        
        //This is where the new value is chosen
        double proposedValue = 0.0;	
        FossilSiteSamplingDate taxonSamplingDate = samplingDatesInput.get().get(0);
        double range = taxonSamplingDate.getUpper() - taxonSamplingDate.getLower();           
        proposedValue = taxonSamplingDate.getLower() + Randomizer.nextDouble() * range;
        
        // Note that tips are also called nodes in this
        Node node;
        Node fake = null;
        double lower, upper;
        double maxlower = 0.0;
        double minupper = Double.POSITIVE_INFINITY;
        // Complicated stuff ensues if selected taxon is a sampled ancestor
        int i;
        for(i=0; i<taxonIndices.length; i++) {
        	node = tree.getNode(taxonIndices[i]);
        	if ((node).isDirectAncestor()) {
        		// fake stores the parent node of the sampled ancestor
        		fake = node.getParent();
        		// Lower is the height of the descendant node from sampled ancestor
        		lower = combinedTree.getHeightOfNode(getOtherChild(fake, node).getNr());
        		// If the sampled ancestor is not at the root...
        		if (fake.getParent() != null) {
        			// The upper is the height of the parent
        			upper = combinedTree.getHeightOfNode(fake.getParent().getNr());
        			// If the sampled ancestor is at the root, then the upper value is unlimited
        		} else upper = Double.POSITIVE_INFINITY;
        		// much easier if it is not a sampled ancestor
        	} else {
        		lower = 0.0;
        		upper = combinedTree.getHeightOfNode(node.getParent().getNr());
        	}
        	if(upper < minupper) {
        		minupper = upper;
        	}
        	if(lower > maxlower) {
        		maxlower = lower;
        	}
        }
        
        if(taxonbelow.get() != null) {
        	belownode = tree.getNode(belowindex);
        	belowheight = combinedTree.getHeightOfNode(belownode.getNr());
        	if(belowheight <= proposedValue) {
        		return Double.NEGATIVE_INFINITY;
        	}
        }
        
        if(taxonabove.get() != null) {
        	abovenode = tree.getNode(aboveindex);
        	aboveheight = combinedTree.getHeightOfNode(abovenode.getNr());
        	if(aboveheight >= proposedValue) {
        		return Double.NEGATIVE_INFINITY;
        	}
        }

        // Automatically rejected if new value is older than parent node
        if (proposedValue < maxlower || proposedValue > minupper) {
            return Double.NEGATIVE_INFINITY;
        }
        // Automatically rejected if new value is the same as the old one
        // Come back to this
        double testheight;
        Node node2;
        node2 = tree.getNode(taxonIndices[0]);
        testheight = combinedTree.getHeightOfNode(node2.getNr());
        if (testheight == proposedValue) {
            // this saves calculating the posterior
            return Double.NEGATIVE_INFINITY;
        }
        //If the selected taxon is a sampled ancestor, then the parent node is given the new value
        fake = null;
        i=0;
        for( i=0; i<taxonIndices.length; i++) {
        	node = tree.getNode(taxonIndices[i]);
        	if ((node).isDirectAncestor()) {
        		fake = node.getParent();
        	}
        	// if it is a sampled ancestor, also set the height of the parent node
        	//setHeightOfNode is a method for the combinedTree object that sets the height of a selected node
            //loop from 0 to length of the set of taxa from the randomly selected fossil site
            // Note that this is also set for sampled ancestors (i.e. the node and the tip have to be moved)
        	if (fake != null) {
        		combinedTree.setHeightOfNode(fake.getNr(), proposedValue);
        	}
        	combinedTree.setHeightOfNode(node.getNr(), proposedValue);
        }
        return 0.0;
    }

    @Override
    public void optimize(double logAlpha) {
    }
}
