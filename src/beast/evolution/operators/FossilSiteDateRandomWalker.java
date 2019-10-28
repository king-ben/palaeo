package beast.evolution.operators;

import java.util.ArrayList;
import java.util.List;
import beast.core.Description;
import beast.core.Input;
import beast.evolution.tree.Node;
import beast.evolution.tree.FossilSiteSamplingDate;
import beast.evolution.tree.Tree;
import beast.evolution.tree.TreeWOffset;
import beast.util.Randomizer;


@Description("Takes an input of taxa from the same fossil site and moves their height simultaneously to a random value within a given range")

public class FossilSiteDateRandomWalker extends TipDatesRandomWalker {
	// Input an offset tree - optional for when the youngest tip ages are sampled
	public Input<TreeWOffset> treeWOffsetInput =
            new Input<TreeWOffset>("treeWOffset", "Optional fully extinct tree", (TreeWOffset)null);
	// The class TipDatesRandomWalker already has a taxonset input
	// Input sampling age range
    public Input<List<FossilSiteSamplingDate>> samplingDatesInput = new Input<>("FossilSiteSamplingDates",
            "List of sampling dates", new ArrayList<FossilSiteSamplingDate>());

    TreeWOffset combinedTree;
    
    @Override
    public void initAndValidate() {
    	combinedTree = treeWOffsetInput.get();
        if(combinedTree == null) {
        	combinedTree = new TreeWOffset();
        	combinedTree.setInputValue("tree", treeInput.get());
        	combinedTree.initAndValidate();
        }
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
        } else {
        	throw new IllegalArgumentException("FossilSiteDateRandomWalker operator requires a taxon set input");
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
            // Complicated stuff if selected taxon is a sampled ancestor
        	if ((node).isDirectAncestor()) {
        		// fake stores the parent node of the sampled ancestor
        		fake = node.getParent();
        		// Lower is the height of the descendant node from sampled ancestor
        		lower = combinedTree.getHeightOfNode(getOtherChild(fake, node).getNr());
        		// If the sampled ancestor is  at the root...
        		if (fake.getParent() != null) {
        			// The upper is the height of the parent
        			upper = combinedTree.getHeightOfNode(fake.getParent().getNr());
        			// If the sampled ancestor is at the root, then the upper value is unlimited
        		} else upper = Double.POSITIVE_INFINITY;
        		// if it is not a sampled ancestor
        	} else {
        		lower = 0.0;
        		upper = combinedTree.getHeightOfNode(node.getParent().getNr());
        	}
        	// The minimum upper and maximum lower across all the taxa are used
        	if(upper < minupper) {
        		minupper = upper;
        	}
        	if(lower > maxlower) {
        		maxlower = lower;
        	}
        }
        // Automatically rejected if new value is older than parent node
        if (proposedValue < maxlower || proposedValue > minupper) {
            return Double.NEGATIVE_INFINITY;
        }
        // Automatically rejected if new value is the same as the old one
        double testheight;
        Node node2;
        node2 = tree.getNode(taxonIndices[0]);
        testheight = combinedTree.getHeightOfNode(node2.getNr());
        if (testheight == proposedValue) {
            // this saves calculating the posterior
            return Double.NEGATIVE_INFINITY;
        }
        fake = null;
        i=0;
        // Proposal is looped across all taxa in the list, instead of picking a random one
        for( i=0; i<taxonIndices.length; i++) {
        	node = tree.getNode(taxonIndices[i]);
        	if ((node).isDirectAncestor()) {
        		fake = node.getParent();
        	}
        	// if it is a sampled ancestor, also set the height of the parent node
        	if (fake != null) {
        		combinedTree.setHeightOfNode(fake.getNr(), proposedValue);
        	}
        	// set the height of the tip
        	combinedTree.setHeightOfNode(node.getNr(), proposedValue);
        }
        return 0.0;
    }

    @Override
    public void optimize(double logAlpha) {
    }
}
