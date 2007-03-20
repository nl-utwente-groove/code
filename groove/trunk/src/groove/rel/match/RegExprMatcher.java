/*
 * $Id: RegExprMatcher.java,v 1.1.1.2 2007-03-20 10:42:54 kastenberg Exp $
 */
package groove.rel.match;

import groove.graph.Label;
import groove.graph.match.DefaultMatcher;
import groove.graph.match.SearchPlanFactory;
import groove.rel.VarMorphism;
import groove.rel.VarNodeEdgeHashMap;
import groove.rel.VarNodeEdgeMap;

import java.util.Map;

/**
 * Simulation from a {@link groove.rel.VarGraph} in a {@link groove.graph.Graph}. 
 * @author Arend Rensink
 * @version $Revision: 1.1.1.2 $
 */
public class RegExprMatcher extends DefaultMatcher {
	static private final SearchPlanFactory searchPlanFactory = new RegExprSearchPlanFactory();
	/**
     * Creates a simulation on the basis of a given regular expression morphism.
     */
    public RegExprMatcher(VarMorphism mapping) {
        super(mapping);
        putAllVar(mapping.getValuation());
    }

    /** Specializes the type of the super method. */
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
//
//    /**
//     * This implementation returns a {@link VarNodeEdgeMap} that also includes the 
//     * valuation of the simulation.
//     */
//    public VarNodeEdgeMap getSingularMap() {
//        return new MyVarNodeEdgeMap();
//    }

    //    /**
	//     * The internal relation factory.
	//     * Initialized lazily in {@link #getRelationFactory()}.
	//     */
	//    private NodeRelation factory;
	//    
	//    /**
	//     * The internal relation calculator.
	//     * Initialized lazily in {@link #getRelationCalculator()}.
	//     */
	//    private RelationCalculator calculator;
	//    /**
	//     * Mapping from (wildcard) identitiers to labels, discovered during this simulation.
	//     */
	//    private Map<String,Label> valuation;
    
	/**
	 * This implementation returns a {@link VarNodeEdgeMap} that also includes
	 * the valuation of the simulation.
	 */
    protected VarNodeEdgeMap createSingularMap() {
        return new VarNodeEdgeHashMap();
    }
    
    /** This implementation returns a {@link RegExprSearchPlanFactory}. */
    @Override
	protected SearchPlanFactory getSearchPlanFactory() {
		return searchPlanFactory;
	}
//
//	@Override
//	protected SearchItem createEdgeSearchItem(Edge edge) {
//    	if (edge instanceof VarEdge) {
//    		return new VarEdgeSearchItem((VarEdge) edge);
//    	} else if (edge.label() instanceof RegExprLabel) {
//    		return new RegExprEdgeSearchItem(edge);
//    	} else {
//    		return super.createEdgeSearchItem(edge);
//    	}
//	}
//
//	/**
//     * Returns the set of composite elements matching a given domain edge.
//     * @param key the edge on whose label the match should be based; may be a <code>DefaultLabel</code>
//     * or a <code>RegExprLabel</code>
//     * @return the set of edges (out of the values of <code>codLabelEdgeMap</code>)
//     * that match <code>label</code>
//     */
//    protected Iterator<? extends Edge> getEdgeMatches(Edge key) {
//        if (key instanceof VarEdge) {
//            return getVarEdgeMatches((VarEdge) key);
//        } else if (key.label() instanceof RegExprLabel) {
//            return getRegExprMatches(key);
//        } else {
//            return super.getEdgeMatches(key);
//        }
//    }
//
//    /**
//     * Returns the elements of the codomain matching a given edge wrapping a regular expression.
//     */
//    protected Iterator<? extends Edge> getRegExprMatches(Edge edgeKey) {
//    	NodeEdgeMap elementMap = getSingularMap();
//    	Node imageSource = elementMap.getNode(edgeKey.source());
//    	Set<Node> imageSourceSet = imageSource == null ? null : Collections.singleton(imageSource);
//    	Node imageTarget = elementMap.getNode(edgeKey.opposite());
//    	Set<Node> imageTargetSet = imageTarget == null ? null : Collections.singleton(imageTarget);
//        RegExprLabel label = (RegExprLabel) edgeKey.label();
//        Automaton labelAutomaton = label.getAutomaton();
//        NodeRelation matches;
//        if (labelAutomaton instanceof VarAutomaton) {
//            matches = ((VarAutomaton) labelAutomaton).getMatches(cod(), imageSourceSet, imageTargetSet, getValuation());            
//        } else {
//            matches = labelAutomaton.getMatches(cod(), imageSourceSet, imageTargetSet);
//        }
//        return filterEnds(matches.getAllRelated().iterator(), edgeKey);
//    }
//
//    /**
//     * Returns the elements of the codomain matching a given variable edge
//     */
//    protected Iterator<? extends Edge> getVarEdgeMatches(VarEdge edgeKey) {
//        final int arity = edgeKey.endCount();
//        Label varImage = getVar(edgeKey.var());
//        Iterator<? extends Edge> labelEdgeIter;
//        if (varImage != null) {
//            labelEdgeIter = cod().labelEdgeSet(arity, varImage).iterator();
//        } else {
//            labelEdgeIter = new FilterIterator<Edge>(cod().edgeSet().iterator()) {
//                /** Only allows the edges with the correct end count. */
//                protected boolean approves(Object obj) {
//                    return ((Edge) obj).endCount() == arity;
//                }
//            };
//        }
//        return filterEnds(labelEdgeIter, edgeKey);
//    }
//    
//    /**
//     * If the edge fits according to <code>super.addEdge(key,image)</code> 
//     * and the image contains a valuation, adds the valuation to the singular map
//     * and pushes the set of freshly valuated variables to a stack.
//     */
//    public boolean addEdge(Edge key, Edge image) {
//		boolean result;
//		if (super.addEdge(key, image)) {
//			if (key instanceof VarEdge) {
//				String var = ((VarEdge) key).var();
//				if (! getValuation().containsKey(var)) {
//					putVar(var, image.label());
//					varStack.push(Collections.singleton(var));
//				} else {
//					varStack.push(Collections.<String>emptySet());
//				}
//			} else if (image instanceof ValuationEdge) {
//				Map<String,Label> newValuation = ((ValuationEdge) image).getValue();
//				Set<String> freshVars = new HashSet<String>(newValuation.keySet());
//				freshVars.removeAll(getValuation().keySet());
//				putAllVar(newValuation);
//				varStack.push(freshVars);
//			}
//			result = true;
//		} else {
//			result = false;
//		}
//		return result;
//    }
//    
//    public void removeEdge(Edge key) {
//    	if (key instanceof VarEdge || getSingularMap().getEdge(key) instanceof ValuationEdge) {
//    		for (String var : varStack.pop()) {
//				getValuation().remove(var);
//			}
//		}
////    	super.removeEdge(key);
//    }
//
// /**
//     * If the changed image set is singular,
//     * registers any variable mappings that can be derived from it.
//     * Then invokes the <code>super</code> method.
//     */
//    protected void notifyEdgeChange(ImageSet<Edge> changed, Node trigger) {
//        if (changed.isSingular()) {
//        	Edge image = changed.getSingular();
//            Label imageLabel = image.label();
//            if (changed.getKey() instanceof VarEdge) {
//                putVar(((VarEdge) changed.getKey()).var(), imageLabel);
//            } else if (image instanceof ValuationEdge) {
//                putAllVar(((ValuationEdge) image).getValue());
//            }
//        }
//        super.notifyEdgeChange(changed, trigger);
//    }
//    
//    protected NodeRelation getRelationFactory() {
//        if (factory == null) {
//            factory = new SetNodeRelation(cod());
//        }
//        return factory;
//    }
//    
//    /**
//     * Returns the internal relation calculator.
//     */
//    protected RelationCalculator getRelationCalculator() {
//        if (calculator == null) {
//            calculator = new RelationCalculator(getRelationFactory());
//        }
//        return calculator;
//    }
//    
//    /**
//     * Stack of sets of variables receiving a value in tha course of a 
//     * regular expression matching.
//     */
//    private final Stack<Set<String>> varStack = new Stack<Set<String>>();
}