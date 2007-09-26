/* $Id: RegExprEdgeSearchItem.java,v 1.9 2007-09-26 08:30:24 rensink Exp $ */
package groove.match;

import groove.graph.BinaryEdge;
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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A search item that searches an image for an edge.
 * @author Arend Rensink
 * @version $Revision $
 */
public class RegExprEdgeSearchItem extends Edge2SearchItem {
	/** 
	 * Constructs a new search item. The item will match 
	 * according to the regular expression on the edge label.
	 */
	public RegExprEdgeSearchItem(BinaryEdge edge) {
		super(edge);
        RegExprLabel label = (RegExprLabel) edge.label();
		this.labelAutomaton = label.getAutomaton();
        this.edgeExpr = label.getRegExpr();
        this.boundVars = label.getRegExpr().boundVarSet();
        this.allVars = label.getRegExpr().allVarSet();
        this.neededVars = new HashSet<String>(allVars);
        this.neededVars.removeAll(boundVars); 
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
    
    /** This implementation returns the empty set. */
    @Override
	public Collection<? extends Edge> bindsEdges() {
		return Collections.emptySet();
	}

	@Override
    public void activate(SearchPlanStrategy strategy) {
        super.activate(strategy);
        this.allVarsFound = true;
        this.varIxMap = new HashMap<String,Integer>();
        for (String var: allVars) {
        	allVarsFound &= strategy.isVarFound(var);
            varIxMap.put(var, strategy.getVarIx(var));
        }
    }

	/** This implementation returns <code>false</code>. */
	@Override
	boolean isPreMatched(Search search) {
		return false;
	}
	
	@Override
	boolean isSingular(Search search) {
		return super.isSingular(search) && allVarsFound;
	}
	
	@Override
	SingularRecord createSingularRecord(Search search) {
		return new RegExprEdgeSingularRecord(search);
	}
	
	@Override
	MultipleRecord createMultipleRecord(Search search) {
		return new RegExprEdgeMultipleRecord(search);
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
    /** 
     * Mapping indicating is all variables in the regular expression have
     * been found before the search item is invoked.
     */
    private boolean allVarsFound;

    class RegExprEdgeSingularRecord extends SingularRecord {
        /** Constructs a new record, for a given matcher. */
        RegExprEdgeSingularRecord(Search search) {
            super(search);
            this.sourcePreMatch = search.getNodePreMatch(sourceIx);
            this.targetPreMatch = search.getNodePreMatch(targetIx);
            assert varIxMap.keySet().containsAll(neededVars);
        }

        @Override
        boolean set() {
            Map<String,Label> valuation = new HashMap<String,Label>();
            for (String var: allVars) {
                Label image = search.getVar(varIxMap.get(var));
                assert image != null;
                valuation.put(var, image);
            }
            return !computeRelation(valuation).isEmpty();
        }

        /** 
         * Computes the image set by querying the automaton derived
         * for the edge label.
         */
        private NodeRelation computeRelation(Map<String,Label> valuation) {
            NodeRelation result;
        	Node sourceFind = sourcePreMatch;
        	if (sourceFind == null && sourceFound) {
        		sourceFind = search.getNode(sourceIx);
        	}
            Set<Node> imageSourceSet = Collections.singleton(sourceFind);
        	Node targetFind = targetPreMatch;
        	if (targetFind == null && targetFound) {
        		targetFind = search.getNode(targetIx);
        	}
            Set<Node> imageTargetSet = Collections.singleton(targetFind);
            if (labelAutomaton instanceof VarAutomaton) {
                result = ((VarAutomaton) labelAutomaton).getMatches(host, imageSourceSet, imageTargetSet, valuation);            
            } else {
                result = labelAutomaton.getMatches(host, imageSourceSet, imageTargetSet);
            }
            return result;
        }
        
        /** Pre-matched source image, if any. */
        private final Node sourcePreMatch;
        /** Pre-matched target image, if any. */
        private final Node targetPreMatch;
    }
    
    class RegExprEdgeMultipleRecord extends Edge2MultipleRecord {
        /** Constructs a new record, for a given matcher. */
        RegExprEdgeMultipleRecord(Search search) {
            super(search);
            assert varIxMap.keySet().containsAll(neededVars);
            freshVars = new HashSet<String>();
            for (String var: boundVars) {
                if (search.getVar(varIxMap.get(var)) == null) {
                    freshVars.add(var);
                }
            }
        }

        /** 
         * Computes the image set by querying the automaton derived
         * for the edge label.
         */
        @Override
        void initImages() {
            Set<Node> imageSourceSet = sourceFind == null ? null : Collections.singleton(sourceFind);
            Set<Node> imageTargetSet = targetFind == null ? null : Collections.singleton(targetFind);
            NodeRelation matches;
            if (labelAutomaton instanceof VarAutomaton) {
                Map<String,Label> valuation = new HashMap<String,Label>();
                for (String var: allVars) {
                    if (! freshVars.contains(var)) {
                        valuation.put(var, search.getVar(varIxMap.get(var)));
                    }
                }
                matches = ((VarAutomaton) labelAutomaton).getMatches(host, imageSourceSet, imageTargetSet, valuation);            
            } else {
                matches = labelAutomaton.getMatches(host, imageSourceSet, imageTargetSet);
            }
            initImages(matches.getAllRelated(), false, false, false, false);
        }
        
        @Override
		boolean setImage(Edge image) {
			boolean result = super.setImage(image);
			if (result && ! freshVars.isEmpty()) {
			    Map<String,Label> valuation = ((ValuationEdge) image).getValue();
	            for (String var: freshVars) {
	            	search.putVar(varIxMap.get(var), valuation.get(var));
	            }
			}
			return result;
		}

        
        @Override
        public void reset() {
            super.reset();
            for (String var: freshVars) {
                search.putVar(varIxMap.get(var), null);
            }
        }

        /** The set of bound variables that are not yet pre-matched. */
        private Set<String> freshVars;
    }
}
