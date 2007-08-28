/* $Id: RegExprEdgeSearchItem.java,v 1.2 2007-08-28 22:01:24 rensink Exp $ */
package groove.match;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import groove.graph.Edge;
import groove.graph.Label;
import groove.graph.Node;
import groove.graph.NodeEdgeMap;
import groove.rel.Automaton;
import groove.rel.NodeRelation;
import groove.rel.RegExpr;
import groove.rel.RegExprLabel;
import groove.rel.ValuationEdge;
import groove.rel.VarAutomaton;

import static groove.match.SearchPlanStrategy.Search;

/**
 * A search item that searches an image for an edge.
 * @author Arend Rensink
 * @version $Revision $
 */
public class RegExprEdgeSearchItem extends EdgeSearchItem {

	/** 
	 * Constructs a new search item. The item will match 
	 * according to the regular expression on the edge label.
	 */
	public RegExprEdgeSearchItem(Edge edge) {
		super(edge);
        RegExprLabel label = (RegExprLabel) edge.label();
		this.labelAutomaton = label.getAutomaton();
        this.edgeExpr = label.getRegExpr();
        this.boundVars = label.getRegExpr().boundVarSet();
        this.neededVars = new HashSet<String>(label.getRegExpr().allVarSet());
        this.neededVars.removeAll(boundVars); 
	}
	
	@Override
	public EdgeRecord getRecord(Search search) {
		return new RegExprEdgeRecord(search);
	}
	
    /**
     * Returns the set of variables used but not bound in the regular expression.
     */
    @Override
    public Collection<String> needsVars() {
        return neededVars;
    }

    /**
     * Returns the set of variables bound in the regular expression.
     */
    @Override
    public Collection<String> bindsVars() {
        return boundVars;
    }

    /** Returns the regular expression on the edge. */
    public RegExpr getEdgeExpr() {
        return edgeExpr;
    }
    
    /**
	 * The automaton that computes the matches for the underlying edge.
	 */
	private final Automaton labelAutomaton;
    /** The regular expression on the edge. */
    private final RegExpr edgeExpr;
    /** Collection of variables bound by the regular expression. */
    private final Set<String> boundVars;
    /** Collection of variables used in the regular expression but not bound by it. */
    private final Set<String> neededVars;
    
    /** Record for the search item. */
    protected class RegExprEdgeRecord extends EdgeRecord {
        /** Constructs a new record, for a given matcher. */
        protected RegExprEdgeRecord(Search search) {
            super(search);
            assert getResult().getValuation().keySet().containsAll(neededVars);
        }

        @Override
        void init() {
            super.init();
            freshVars = new HashSet<String>(boundVars);
            freshVars.removeAll(getResult().getValuation().keySet());
        }

        /** This implementation returns <code>null</code>. */
        @Override
        Label getPreMatchedLabel() {
            return null;
        }

        /** This implementation returns <code>false</code>. */
        @Override
        boolean isPreDetermined() {
            return false;
        }

        @Override
        boolean select(Edge image) {
            boolean result = super.select(image);
            if (result && image instanceof ValuationEdge) {
                for (Map.Entry<String,Label> valueEntry: ((ValuationEdge) image).getValue().entrySet()) {
                    String var = valueEntry.getKey();
                    if (freshVars.contains(var)) {
                        getResult().putVar(var, valueEntry.getValue());
                    }
                }
//                if (image instanceof ValuationEdge) {
//                    // there's something more to be optimized here:
//                    // the search plan can predict wich variables are fresh
//                    // in the current valuation
//                    Map<String, Label> newValuation = ((ValuationEdge) image).getValue();
//                    freshVars = new HashSet<String>(newValuation.keySet());
//                    freshVars.removeAll(getResult().getValuation().keySet());
//                    getResult().putAllVar(newValuation);
//                }
            } 
            return result;
        }

        /**
         * Since the image is not really an edge of the underlying graph, 
         * don't set it in the result map.
         */
        @Override
        void selectEdge(Edge image) {
            // does nothing
        }

        @Override
        void undo() {
            super.undo();
            for (String var : freshVars) {
                getResult().getValuation().remove(var);
            }
        }
        
        /** Since edge images are not put into the result map, there is nothing to undo. */
        @Override
        void undoEdge() {
            // empty
        }

        /** 
         * Computes the image set by querying the automaton derived
         * for the edge label.
         */
        @Override
        Collection<? extends Edge> computeMultiple() {
            NodeEdgeMap elementMap = getResult();
            Node imageSource = elementMap.getNode(getEdge().source());
            Set<Node> imageSourceSet = imageSource == null ? null : Collections.singleton(imageSource);
            Node imageTarget = elementMap.getNode(getEdge().opposite());
            Set<Node> imageTargetSet = imageTarget == null ? null : Collections.singleton(imageTarget);
            NodeRelation matches;
            if (labelAutomaton instanceof VarAutomaton) {
                matches = ((VarAutomaton) labelAutomaton).getMatches(getTarget(), imageSourceSet, imageTargetSet, getResult().getValuation());            
            } else {
                matches = labelAutomaton.getMatches(getTarget(), imageSourceSet, imageTargetSet);
            }
            return matches.getAllRelated();
        }
        
        /** The set of bound variables that are not yet pre-matched. */
        private Set<String> freshVars;
    }
}
