/*
 * $Id: DefaultConditionSearchPlanFactory.java,v 1.3 2007-03-30 15:50:44 rensink Exp $
 */
package groove.trans.match;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Label;
import groove.graph.Morphism;
import groove.graph.Node;
import groove.graph.match.EdgeSearchItem;
import groove.graph.match.NodeSearchItem;
import groove.graph.match.SearchItem;
import groove.rel.RegExpr;
import groove.rel.RegExprLabel;
import groove.rel.match.RegExprSearchPlanFactory;
import groove.trans.DefaultGraphCondition;
import groove.trans.GraphCondition;
import groove.trans.GraphGrammar;
import groove.trans.Rule;

/**
 * Strategy that yields the edges in order of ascending indegree of
 * their source nodes.
 * The idea is that the "roots" of a graph (those starting in nodes with
 * small indegree) are likely to give a better immediate reduction of
 * the number of possible matches.
 * Furthermore, regular expression edges are saved to the last.
 * @author Arend Rensink
 * @version $Revision: 1.3 $
 */
public class DefaultConditionSearchPlanFactory extends RegExprSearchPlanFactory implements ConditionSearchPlanFactory {
	/**
	 * Edge comparator on the basis of a list of control labels.
	 * Preference is given to labels occurring early in this list.
	 * @author Arend Rensink
	 * @version $Revision $
	 */
    private class ControlLabelComparator implements Comparator<Edge> {
    	/**
    	 * Constructs a comparator on the basis of a given list of labels.
    	 */
    	private ControlLabelComparator(List<String> hint) {
			this.priorities = new HashMap<String, Integer>();
			for (int hintIndex = 0; hintIndex < hint.size(); hintIndex++) {
				priorities.put(hint.get(hintIndex), new Integer(hintIndex));
			}
		}

    	/**
    	 * Favours the edge occurring earliest in the list of control labels.
    	 */
    	public int compare(Edge first, Edge second) {
			// compare edge priorities (lower = better)
			return getEdgePriority(second) - getEdgePriority(first);
		}
        
        /**
         * Returns the priority of an edge, judged by its label.
         * @see #getPriority(Label)
         */
        private int getEdgePriority(Edge edge) {
        	Integer result = priorities.get(edge.label().text());
        	if (result == null) {
        		return Integer.MAX_VALUE;
        	} else {
        		return result;
        	}
        }

        /**
         * The priorities assigned to labels, on the basis of the list of labels
         * passed in at construction time.
         */
    	private final Map<String,Integer> priorities;
    }
    
    /**
     * Plan data extension based on a graph grammar.
     * Additionally it takes the control labels of the grammar into account.
     * @author Arend Rensink
     * @version $Revision $
     */
    protected class GrammarPlanData extends PlanData {
    	protected GrammarPlanData(GraphGrammar grammar, Set<? extends Node> nodeSet, Set<? extends Edge> edgeSet) {
    		super(nodeSet, edgeSet);
    		this.grammar = grammar;
    	}
    	
    	/**
    	 * Calls {@link DefaultConditionSearchPlanFactory#createComparators(GraphGrammar, Set, Set)}.
    	 */
    	@Override
		protected List<Comparator<Edge>> computeComparators() {
			return DefaultConditionSearchPlanFactory.this.createComparators(grammar, nodeSet, edgeSet);
		}

    	/**
    	 * The grammar for this plan data.
    	 */
		protected final GraphGrammar grammar;
    }

	/**
	 * Takes control labels into account if there are any,
	 * and adds embargo tests to the schedule. 
	 */
    public List<SearchItem> createSearchPlan(GraphCondition condition) {
    	return createSearchPlan(condition, Collections.<Node>emptySet(), Collections.<Edge>emptySet());
    }

	/**
	 * Takes control labels into account if there are any,
	 * and adds embargo tests to the schedule. 
	 */
    public List<SearchItem> createSearchPlan(GraphCondition condition, Set<Node> preMatchedNodes, Set<Edge> preMatchedEdges) {
    	Morphism morphism = condition.getPattern();
    	Graph subject = condition.getTarget();
    	Set<Node> nodeSet = new HashSet<Node>(subject.nodeSet());
    	nodeSet.removeAll(preMatchedNodes);
    	nodeSet.removeAll(morphism.nodeMap().values());
    	Set<Edge> edgeSet = new HashSet<Edge>(subject.edgeSet());
    	edgeSet.removeAll(preMatchedEdges);
    	edgeSet.removeAll(morphism.edgeMap().values());
    	PlanData planData;
    	if (condition instanceof Rule) {
    		planData = new GrammarPlanData(((Rule) condition).getGrammar(), nodeSet, edgeSet);
    	} else {
    		planData = new PlanData(nodeSet, edgeSet);
    	}
    	List<SearchItem> result = planData.getPlan();
    	if (condition instanceof DefaultGraphCondition) {
    		addEmbargoes((DefaultGraphCondition) condition, result, preMatchedNodes, preMatchedEdges);
    	}
    	return result;
    }

	/**
	 * Adds edge and merge embargo search items to an already existing search plan.
	 * @param condition the condition from which the embargoes are to be retrieved
	 * @param result the already computed search plan
	 * @param prematchedNodes the nodes that are already matched (and hence not in <code>result</code>)
	 * @param preMatchedEdges the edges that are already matched (and hence not in <code>result</code>)
	 */
	private void addEmbargoes(DefaultGraphCondition condition, List<SearchItem> result, Set<Node> preMatchedNodes, Set<Edge> preMatchedEdges) {
		Map<Node, Collection<Edge>> embargoMap = condition.getNegationMap();
		if (embargoMap != null) {
			for (Map.Entry<Node, Collection<Edge>> embargoEntry : embargoMap.entrySet()) {
				Node source = embargoEntry.getKey();
				for (Edge embargoEdge : embargoEntry.getValue()) {
					if (source.equals(embargoEdge.source())) {
						addEdgeEmbargo(result, embargoEdge, preMatchedNodes, preMatchedEdges);
					}
				}
			}
		}
		Map<Node, Set<Node>> injectionMap = condition.getInjectionMap();
		if (injectionMap != null) {
			for (Map.Entry<Node, Set<Node>> injectionEntry : injectionMap.entrySet()) {
				Node node1 = injectionEntry.getKey();
				for (Node node2 : injectionEntry.getValue()) {
					if (node1.compareTo(node2) < 0) {
						addMergeEmbargo(result, node1, node2, preMatchedNodes);
					}
				}
			}
		}
	}

	/**
	 * Creates the comparators for the search plan.
	 * Adds a comparator based on the control labels available in the grammar, if any.
	 * @param grammar the grammar in which the control labels are to be found
	 * @param nodeSet the node set to be matched
	 * @param edgeSet the edge set to be matched
	 * @return a list of comparators determining the order in which edges should be matched
	 * @see #createComparators(Set, Set)
	 */
	protected List<Comparator<Edge>> createComparators(GraphGrammar grammar, Set<? extends Node> nodeSet, Set<? extends Edge> edgeSet) {
		List<Comparator<Edge>> result = super.createComparators(nodeSet, edgeSet);
		if (grammar != null) {
			List<String> controlLabels = grammar.getControlLabels();
			result.add(0, new ControlLabelComparator(controlLabels));
		}
		return result;
	}
    
	/**
	 * Inserts an edge embargo search item at the appropriate place in a 
	 * search plan, namely directly after all end nodes have been matched.
	 * @param result the pre-existing search plan
	 * @param embargoEdge the embargo edge to be inserted
	 * @param prematchedNodes the nodes that are already matched (and hence not in <code>result</code>)
	 * @param preMatchedEdges the edges that are already matched (and hence not in <code>result</code>)
	 */
    protected void addEdgeEmbargo(List<SearchItem> result, Edge embargoEdge, Set<Node> preMatchedNodes, Set<Edge> preMatchedEdges) {
    	Set<Node> endSet = new HashSet<Node>();
    	for (Node end: embargoEdge.ends()) {
        	if (!preMatchedNodes.contains(end)) {
        		endSet.add(end);
        	}
    	}
    	// the set of variables possibly occurring in the edge
    	Set<String> varSet = new HashSet<String>();
    	RegExpr edgeExpr = getRegExpr(embargoEdge);
    	if (edgeExpr != null) {
    		varSet.addAll(edgeExpr.allVarSet());
    		for (Edge preMatchedEdge: preMatchedEdges) {
    			edgeExpr = getRegExpr(preMatchedEdge);
    			if (edgeExpr != null) {
    				varSet.removeAll(edgeExpr.boundVarSet());
    			}
    		}
    	}
    	int index = 0;
    	while (! endSet.isEmpty() || ! varSet.isEmpty()) {
    		SearchItem next = result.get(index);
    		if (next instanceof NodeSearchItem) {
    			endSet.remove(((NodeSearchItem) next).getNode());
    		} else if (next instanceof EdgeSearchItem) {
    			Edge edge = ((EdgeSearchItem) next).getEdge();
    			endSet.removeAll(Arrays.asList(edge.ends()));
    			edgeExpr = getRegExpr(edge);
    			if (edgeExpr != null) {
    				varSet.removeAll(edgeExpr.boundVarSet());
    			}
    		}
    		index++;
    	}
    	result.add(index, createNegatedSearchItem(createEdgeSearchItem(embargoEdge, null)));
    }
    
    /** 
     * Returns the regular expression on a given edge label, if any,
     * or <code>null</code> otherwise.
     */
    private RegExpr getRegExpr(Edge edge) {
    	return RegExprLabel.getRegExpr(edge.label());
    }
    
   	/**
	 * Inserts a merge embargo search item at the appropriate place in a 
	 * search plan, namely directly after the nodes have been matched.
	 * @param result the pre-existing search plan
	 * @param node1 the first node to be matched injectively
	 * @param node2 the second node to be matched injectively
	 */
    protected void addMergeEmbargo(List<SearchItem> result, Node node1, Node node2, Set<Node> preMatchedNodes) {
    	Set<Node> nodeSet = new HashSet<Node>();
    	if (!preMatchedNodes.contains(node1)) {
    		nodeSet.add(node1);
    	}
    	if (!preMatchedNodes.contains(node2)) {
    		nodeSet.add(node2);
    	}
    	int index = 0;
    	while (! nodeSet.isEmpty()) {
    		SearchItem next = result.get(index);
    		if (next instanceof NodeSearchItem) {
    			nodeSet.remove(((NodeSearchItem) next).getNode());
    		} else if (next instanceof EdgeSearchItem) {
    			for (Node end: ((EdgeSearchItem) next).getEdge().ends()) {
    				nodeSet.remove(end);
    			}
    		}
    		index++;
    	}
    	result.add(index, createInjectionSearchItem(node1, node2));
    }
    
    /**
     * Callback factory method for a negated search item.
     * @param inner the internal search item which this one negates
     * @return an instance of {@link NegatedSearchItem}
     */
    protected NegatedSearchItem createNegatedSearchItem(SearchItem inner) {
    	return new NegatedSearchItem(inner);
    }
    
    /**
     * Callback factory method for an injection search item.
     * @param node1 the first node to be matched injectively
     * @param node2 the second node to be matched injectively
     * @return an instance of {@link InjectionSearchItem}
     */
    protected InjectionSearchItem createInjectionSearchItem(Node node1, Node node2) {
    	return new InjectionSearchItem(node1, node2);
    }    
}
