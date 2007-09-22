/* $Id: RegExprEdgeSearchItem.java,v 1.5 2007-09-22 09:10:36 rensink Exp $ */
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
import java.util.HashMap;
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
        this.allVars = label.getRegExpr().allVarSet();
        this.neededVars = new HashSet<String>(allVars);
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
    
    @Override
    public void activate(SearchPlanStrategy strategy) {
        super.activate(strategy);
        this.varIxMap = new HashMap<String,Integer>();
        for (String var: allVars) {
            varIxMap.put(var, strategy.getVarIx(var));
        }
    }

    /**
	 * The automaton that computes the matches for the underlying edge.
	 */
	private final Automaton labelAutomaton;
    /** The regular expression on the edge. */
    private final RegExpr edgeExpr;
    /** Collection of all variables occurring in the regular expression. */
    private final Set<String> allVars;
    /** Collection of variables bound by the regular expression. */
    private final Set<String> boundVars;
    /** Collection of variables used in the regular expression but not bound by it. */
    private final Set<String> neededVars;
    /** Mapping from variables to the corresponding indices in the result. */
    private Map<String,Integer> varIxMap;
    /** Record for the search item. */
    protected class RegExprEdgeRecord extends EdgeRecord {
        /** Constructs a new record, for a given matcher. */
        protected RegExprEdgeRecord(Search search) {
            super(search);
            assert varIxMap.keySet().containsAll(neededVars);
            freshVars = new HashSet<String>();
            for (String var: boundVars) {
                if (getSearch().getVar(varIxMap.get(var)) == null) {
                    freshVars.add(var);
                }
            }
        }

        /** In addition to the <code>super</code> method, initialises the set of fresh variables. */
        @Override
        void init() {
            super.init();
            valuation = new HashMap<String,Label>();
            for (String var: allVars) {
                Label image = getSearch().getVar(varIxMap.get(var));
                if (image != null) {
                    valuation.put(var, image);
                }
            }
        }

        /** This implementation returns <code>null</code>. */
        @Override
        final Label getPreMatchedLabel() {
            throw new UnsupportedOperationException();
        }

        /** This implementation returns <code>true</code> if the super implementation does,
         * and there are no fresh variables to be bound. 
         */
        @Override
        final boolean isPreDetermined() {
            return super.isPreDetermined() && freshVars.isEmpty();
        }

        /** Returns the first image according to {@link #computeMultiple()}, if any. */
        @Override
        Edge computePreDetermined() {
            Iterator<? extends Edge> imageIter = computeMultiple();
            if (imageIter.hasNext()) {
                return imageIter.next();
            } else {
                return null;
            }
        }

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
                getSearch().putVar(varIxMap.get(var), null);
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
                matches = ((VarAutomaton) labelAutomaton).getMatches(getTarget(), imageSourceSet, imageTargetSet, valuation);            
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
                    getSearch().putVar(varIxMap.get(var), valueEntry.getValue());
                }
            }
        }
        
        /** The set of bound variables that are not yet pre-matched. */
        private Set<String> freshVars;
        /** Valuation of the variables, insofar known at the time of matching this item. */
        private Map<String,Label> valuation;
    }
}
