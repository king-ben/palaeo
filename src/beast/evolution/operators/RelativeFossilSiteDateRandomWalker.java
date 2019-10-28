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



@Description("An extended version of FossilSiteDateRandomWalker that ensures proposals are not older/younger than reference taxa")

public class RelativeFossilSiteDateRandomWalker extends TipDatesRandomWalker {
	public Input<TreeWOffset> treeWOffsetInput =
            new Input<TreeWOffset>("treeWOffset", "Optional fully extinct tree", (TreeWOffset)null);
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

    @Override
    public void initAndValidate() {
    	
    	combinedTree = treeWOffsetInput.get();
        if(combinedTree == null) {
        	combinedTree = new TreeWOffset();
        	combinedTree.setInputValue("tree", treeInput.get());
        	combinedTree.initAndValidate();
        }
    	
        if (m_taxonsetInput.get() != null) {
            
            List<String> sTaxaNames = new ArrayList<String>();
            for (String sTaxon : treeInput.get().getTaxaNames()) {
                sTaxaNames.add(sTaxon);
            }
            List<String> set = m_taxonsetInput.get().asStringList();
            int nNrOfTaxa = set.size();
            taxonIndices = new int[nNrOfTaxa];
            int k = 0;
            for (String sTaxon : set) {
                int iTaxon = sTaxaNames.indexOf(sTaxon);
                if (iTaxon < 0) {
                    throw new IllegalArgumentException("Cannot find taxon " + sTaxon + " in tree");
                }
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
    
    @Override
    public double proposal() {

        Tree tree = combinedTree.getTree();
        
        double proposedValue = 0.0;	
        FossilSiteSamplingDate taxonSamplingDate = samplingDatesInput.get().get(0);
        double range = taxonSamplingDate.getUpper() - taxonSamplingDate.getLower();           
        proposedValue = taxonSamplingDate.getLower() + Randomizer.nextDouble() * range;
        Node node;
        Node fake = null;
        double lower, upper;
        double maxlower = 0.0;
        double minupper = Double.POSITIVE_INFINITY;
        int i;
        for(i=0; i<taxonIndices.length; i++) {
        	node = tree.getNode(taxonIndices[i]);
        	if ((node).isDirectAncestor()) {
        		fake = node.getParent();
        		lower = combinedTree.getHeightOfNode(getOtherChild(fake, node).getNr());
        		if (fake.getParent() != null) {
        			upper = combinedTree.getHeightOfNode(fake.getParent().getNr());
        		} else upper = Double.POSITIVE_INFINITY;
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
        // New code for this operator
        // Proposal rejected if the proposed value is older than the input taxon from an older layer
        if(taxonbelow.get() != null) {
        	belownode = tree.getNode(belowindex);
        	belowheight = combinedTree.getHeightOfNode(belownode.getNr());
        	if(belowheight <= proposedValue) {
        		return Double.NEGATIVE_INFINITY;
        	}
        }
        // Proposal rejected if the proposed value is younger than the input taxon from an younger layer
        if(taxonabove.get() != null) {
        	abovenode = tree.getNode(aboveindex);
        	aboveheight = combinedTree.getHeightOfNode(abovenode.getNr());
        	if(aboveheight >= proposedValue) {
        		return Double.NEGATIVE_INFINITY;
        	}
        }

        if (proposedValue < maxlower || proposedValue > minupper) {
            return Double.NEGATIVE_INFINITY;
        }
        double testheight;
        Node node2;
        node2 = tree.getNode(taxonIndices[0]);
        testheight = combinedTree.getHeightOfNode(node2.getNr());
        if (testheight == proposedValue) {
            return Double.NEGATIVE_INFINITY;
        }
        fake = null;
        i=0;
        for( i=0; i<taxonIndices.length; i++) {
        	node = tree.getNode(taxonIndices[i]);
        	if ((node).isDirectAncestor()) {
        		fake = node.getParent();
        	}
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
