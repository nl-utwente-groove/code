/* $Id: RegExprEdgeSearchItem.java,v 1.3 2007-08-30 15:18:18 rensink Exp $ */
package groove.match;

import groove.graph.Edge;
import groove.graph.Label;
import groove.graph.Node;
import groove.match.SearchPlanStrategy.Search;
import groove.rel.Automaton;
import groove.rel.NodeRelation;
import groove.rel.RegExpr;
import groove.rel.RegExprLabel;
import groove.rel.ValuationEdge;
import groove.rel.VarAutomaton;
import groove.util.FilterIterator;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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

        /** In addition to the <code>super</code> method, initialises the set of fresh variables. */
        @Override
        void init() {
            super.init();
            freshVars = new HashSet<String>(boundVars);
            freshVars.removeAll(getResult().getValuation().keySet());
        }

        /** This implementation returns <code>null</code>. */
        @Override
        final Label getPreMatchedLabel() {
            throw new UnsupportedOperationException();
        }

        /** This implementation returns <code>false</code>. */
        @Override
        final boolean isPreDetermined() {
            return false;
        }
//
//        @Override
//        boolean select(Edge image) {
//            boolean result = super.select(image);
//            if (result && image instanceof ValuationEdge) {
//                for (Map.Entry<String,Label> valueEntry: ((ValuationEdge) image).getValue().entrySet()) {
//                    String var = valueEntry.getKey();
//                    if (freshVars.contains(var)) {
//                        getResult().putVar(var, valueEntry.getValue());
//                    }
//                }
//            } 
//            return result;
//        }

        /**
         * Since the image is not really an edge of the underlying graph, 
         * don't set it in the result map.
         */
        @Override
        void setEdge(Edge image) {
            // does nothing
        }

        @Override
        void resetLabel() {
            for (String var : freshVars) {
                getResult().getValuation().remove(var);
            }
        }
        
        /** Since edge images are not put into the result map, there is nothing to undo. */
        @Override
        void resetEdge() {
            // empty
        }

        /** 
         * Computes the image set by querying the automaton derived
         * for the edge label.
         */
        @Override
        Iterator< ? extends Edge> computeMultiple() {
            final Node imageSource = getPreMatchedSource();
            Set<Node> imageSourceSet = imageSource == null ? null : Collections.singleton(imageSource);
            final Node imageTarget = getPreMatchedTarget();
            Set<Node> imageTargetSet = imageTarget == null ? null : Collections.singleton(imageTarget);
            NodeRelation matches;
            if (labelAutomaton instanceof VarAutomaton) {
                matches = ((VarAutomaton) labelAutomaton).getMatches(getTarget(), imageSourceSet, imageTargetSet, getResult().getValuation());            
            } else {
                matches = labelAutomaton.getMatches(getTarget(), imageSourceSet, imageTargetSet);
            }
            return new FilterIterator<Edge>(matches.getAllRelated().iterator()) {
                @Override
                protected boolean approves(Object obj) {
                    Edge image = (Edge) obj;
                    boolean result = true;
                    // select the source only if it was not pre-matched
                    if (imageSource == null) {
                        result = setEnd(Edge.SOURCE_INDEX, image);
                    }
                    // select the target only if it was not pre-matched
                    if (result && imageTarget == null) {
                        result = setEnd(Edge.TARGET_INDEX, image);
                    }
                    // select the variables if there are any
                    if (result && image instanceof ValuationEdge) {
                        selectVars(((ValuationEdge) image).getValue());
                    }
                    return result;
                }
            };
        }
        
        /**
         * Inserts a valuation for the fresh variables into the result map.
         */
        private void selectVars(Map<String,Label> valuation) {
            for (Map.Entry<String,Label> valueEntry: valuation.entrySet()) {
                String var = valueEntry.getKey();
                if (freshVars.contains(var)) {
                    getResult().putVar(var, valueEntry.getValue());
                }
            }
        }
        
        /** The set of bound variables that are not yet pre-matched. */
        private Set<String> freshVars;
    }
}
