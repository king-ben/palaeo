package beast.math.distributions;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import beast.core.Description;
import beast.core.Distribution;
import beast.core.Input;
import beast.core.Input.Validate;
import beast.core.State;
import beast.evolution.alignment.Taxon;
import beast.evolution.tree.Node;
import beast.evolution.tree.Tree;
import beast.evolution.tree.TreeWOffset;

@Description("Forces the age of one taxon to be older than the age of another")
public class RelativeAgePrior extends Distribution {
	public Input<TreeWOffset> treeWOffsetInput =
            new Input<TreeWOffset>("treeWOffset", "Optional fully extinct tree", (TreeWOffset)null);
	//public final Input<Tree> treeInput = new Input<>("tree", "the tree containing the taxa", Validate.REQUIRED); 
	final public Input<Tree> treeInput = new Input<>("tree", "beast.tree on which this operation is performed", Validate.REQUIRED);
	public Input<Taxon> oldTaxon = new Input<> ("older", "The older taxon");
	public Input<Taxon> youngTaxon = new Input<> ("younger", "The younger taxon");
	
	//Tree tree;
	TreeWOffset treeW;
	
	int youngerIndex;
	int olderIndex;
	//int nrOfTaxa = -1;
	double youngerHeight;
	double olderHeight;
	Node youngerNode;
    Node olderNode;
	@Override
	public void initAndValidate() {
		//tree = treeInput.get();
		treeW = treeWOffsetInput.get();
		if(treeW == null) {
        	treeW = new TreeWOffset();
        	treeW.setInputValue("tree", treeInput.get());
        	treeW.initAndValidate();
        }
		List<String> taxaNames = new ArrayList<String>();
        // Adds taxon names to the list
        for (String sTaxon : treeInput.get().getTaxaNames()) {
            taxaNames.add(sTaxon);
        }
        youngerIndex = taxaNames.indexOf(youngTaxon.get().toString());
        olderIndex = taxaNames.indexOf(oldTaxon.get().toString());
		//calculateLogP();
        //nrOfTaxa = taxaNames.size();
        //int[] taxonIndices = new int[nrOfTaxa];
        
	}
	
	
	
	@Override
    public double calculateLogP() {
    	//initAndValidate();
		
		Tree tree = treeW.getTree();

        youngerNode = tree.getNode(youngerIndex);
        olderNode = tree.getNode(olderIndex);
        youngerHeight = treeW.getHeightOfNode(youngerNode.getNr());
        olderHeight = treeW.getHeightOfNode(olderNode.getNr());
        //calculateLogP();
    	logP = 0.0;
    	if(olderHeight < youngerHeight) {
    		logP = Double.NEGATIVE_INFINITY;
    		return Double.NEGATIVE_INFINITY;
    	}
    	return(logP);
		
	}

	@Override
	public List<String> getArguments() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getConditions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void sample(State state, Random random) {
		// TODO Auto-generated method stub
		
	}
}
