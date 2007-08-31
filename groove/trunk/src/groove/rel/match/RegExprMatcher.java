/*
 * $Id: RegExprMatcher.java,v 1.5 2007-08-31 10:23:22 rensink Exp $
 */
package groove.rel.match;

import groove.graph.Label;
import groove.graph.Node;
import groove.graph.match.DefaultMatcher;
import groove.graph.match.SearchPlanFactory;
import groove.rel.VarMorphism;
import groove.rel.VarNodeEdgeHashMap;
import groove.rel.VarNodeEdgeMap;

import java.util.Map;

/**
 * Simulation that takes regular expressions into account.
 * @author Arend Rensink
 * @version $Revision: 1.5 $
 */
@Deprecated
public class RegExprMatcher extends DefaultMatcher {
	static private final SearchPlanFactory searchPlanFactory = new RegExprSearchPlanFactory();
	/**
     * Creates a simulation on the basis of a given regular expression morphism.
	 * @param injective flag indicating that the mething should be injective
     */
    public RegExprMatcher(VarMorphism mapping, boolean injective) {
        super(mapping, injective);
        putAllVar(mapping.getValuation());
    }

    /** Specializes the type of the super method. */
    @Override
    public VarNodeEdgeMap getSingularMap() {
    	return (VarNodeEdgeMap) super.getSingularMap();
    }
    
    /**
     * Returns the (partial) variable map in this simulation.
     * The map goes from the variables in the domain graph to the labels
     * of the codomain graph. 
     */
    public Map<String, Label> getValuation() {
    	return getSingularMap().getValuation();
    }
    
    /**
     * Returns the value of a given variable, according to
     * the valuation in this simulation.
     */
    public Label getVar(String var) {
        return getValuation().get(var);
    }

    /**
     * Inserts a value for a variable into the valuation map.
     * If the variable already had a valuation, differing from the new one,
     * an {@link IllegalStateException} is thrown.
     * @see #getVar(String)
     */
    public Label putVar(String var, Label value) {
        Label oldImage = getValuation().put(var, value);
        if (oldImage != null && !value.equals(oldImage)) {
            throw new IllegalStateException();
        }
        return oldImage;
    }

    /**
     * Copies a given valuation mapping to the valuation in this simulation.
     * Iterates over <code>valuation.entrySet()</code> and invokes {@link #putVar(String, Label)}
     * for each entry.
     */
    public void putAllVar(Map<String, Label> valuation) {
    	for (Map.Entry<String,Label> idEntry: valuation.entrySet()) {
            putVar(idEntry.getKey(), idEntry.getValue());
        }
    }
    
	/**
	 * This implementation returns a {@link VarNodeEdgeMap} that also includes
	 * the valuation of the simulation.
	 */
    @Override
    protected VarNodeEdgeMap createSingularMap() {
    	if (isInjective()) {
    		// returns a hash map that maintains the usedNodes
    		// when nodes are added or removed.
    		return new VarNodeEdgeHashMap() {
				@Override
				protected Map<Node, Node> createNodeMap() {
					return createUsedNodeSensitiveNodeMap();
				}
    		};
    	} else {
    		return new VarNodeEdgeHashMap();
    	}
    }
    
    /** This implementation returns a {@link RegExprSearchPlanFactory}. */
    @Override
	protected SearchPlanFactory getSearchPlanFactory() {
		return searchPlanFactory;
	}
}