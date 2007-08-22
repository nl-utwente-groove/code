/*
 * $Id: DefaultConditionSearchPlanFactory.java,v 1.7 2007-08-22 15:05:03 rensink Exp $
 */
package groove.trans.match;

import groove.graph.Edge;
import groove.graph.Node;
import groove.graph.match.EdgeSearchItem;
import groove.graph.match.NodeSearchItem;
import groove.graph.match.ProductEdgeSearchItem;
import groove.graph.match.SearchItem;
import groove.graph.match.ValueNodeSearchItem;
import groove.rel.RegExpr;
import groove.rel.RegExprLabel;
import groove.rel.match.RegExprSearchPlanFactory;
import groove.trans.DefaultGraphCondition;
import groove.trans.GraphCondition;
import groove.trans.SystemProperties;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Strategy that yields the edges in order of ascending indegree of
 * their source nodes.
 * The idea is that the "roots" of a graph (those starting in nodes with
 * small indegree) are likely to give a better immediate reduction of
 * the number of possible matches.
 * Furthermore, regular expression edges are saved to the last.
 * @author Arend Rensink
 * @version $Revision: 1.7 $
 */
public class DefaultConditionSearchPlanFactory extends RegExprSearchPlanFactory implements ConditionSearchPlanFactory {
	/**
	 * Takes control labels into account if there are any,
	 * and adds embargo tests to the schedule. 
	 */
    public List<SearchItem> createSearchPlan(GraphCondition condition) {
    	return createSearchPlan(condition, condition.getPattern().nodeMap().values(), condition.getContext().edgeSet());
    }

	/**
	 * Takes control labels into account if there are any,
	 * and adds embargo tests to the schedule. 
	 */
    public List<SearchItem> createSearchPlan(GraphCondition condition, Collection<? extends Node> boundNodes, Collection<? extends Edge> boundEdges) {
//    	Graph subject = condition.getTarget();
//    	Set<Node> nodeSet = new HashSet<Node>(subject.nodeSet());
//    	nodeSet.removeAll(boundNodes);
//    	Set<Edge> edgeSet = new HashSet<Edge>(subject.edgeSet());
//    	edgeSet.removeAll(boundEdges);
    	PlanData planData = new GrammarPlanData(condition, boundNodes, boundEdges);
    	List<SearchItem> result = planData.getPlan();
    	return result;
    }
    /**
     * Plan data extension based on a graph grammar.
     * Additionally it takes the control labels of the grammar into account.
     * @author Arend Rensink
     * @version $Revision $
     */
    protected class GrammarPlanData extends PlanData {
        /** 
         * Constructs a fresh instance of the plan data,
         * based on a given set of system properties, and sets
         * of already matched nodes and edges. 
         * @param condition the graph condition for which we develop the search plan
         * @param boundNodes the set of pre-matched nodes
         * @param boundEdges the set of pre-matched edges
         */
        protected GrammarPlanData(GraphCondition condition, Collection<? extends Node> boundNodes, Collection<? extends Edge> boundEdges) {
            super(condition.getTarget(), boundNodes, boundEdges);
            this.condition = condition;
            this.boundNodes = boundNodes;
            this.boundEdges = boundEdges;
        }
        
        /**
         * In addition to calling the super method, adds edge and merge embargoes if the
         * condition if a {@link DefaultGraphCondition}.
         */
        @Override
        public List<SearchItem> getPlan() {
            List<SearchItem> result = super.getPlan();
            if (condition instanceof DefaultGraphCondition) {
                Set<Edge> negations = ((DefaultGraphCondition) condition).getNegations();
                if (negations != null) {
                    for (Edge embargoEdge : negations) {
                        addEdgeEmbargo(result, embargoEdge);
                    }
                }
                Set<Set<? extends Node>> injections = ((DefaultGraphCondition) condition).getInjections();
                if (injections != null) {
                    for (Set<? extends Node> injection : injections) {
                        addMergeEmbargo(result, injection);
                    }
                }
            }
            return result;
        }

        /**
         * Creates the comparators for the search plan.
         * Adds a comparator based on the control labels available in the grammar, if any.
         * @return a list of comparators determining the order in which edges should be matched
         */
        @Override
        protected List<Comparator<Edge>> createComparators() {
            List<Comparator<Edge>> result = super.createComparators();
            SystemProperties properties = condition.getProperties();
            if (properties != null) {
                List<String> controlLabels = properties.getControlLabels();
                List<String> commonLabels = properties.getCommonLabels();
                Comparator<Edge> labelComparator = new ControlLabelComparator(controlLabels, commonLabels);
                int position = 0;
                while (position < result.size() && !(result.get(position) instanceof IndegreeComparator)) {
                    position++;
                }
                result.add(position, labelComparator);
            }
            return result;
        }
//
//        /**
//         * Adds edge and merge embargo search items to an already existing search plan.
//         * @param condition the condition from which the embargoes are to be retrieved
//         * @param result the already computed search plan
//         */
//        private void addEmbargoes(DefaultGraphCondition condition, List<SearchItem> result) {
//            Set<Edge> negations = condition.getNegations();
//            if (negations != null) {
//                for (Edge embargoEdge : negations) {
//                    addEdgeEmbargo(result, embargoEdge);
//                }
//            }
//            Set<Set<? extends Node>> injections = condition.getInjections();
//            if (injections != null) {
//                for (Set<? extends Node> injection : injections) {
//                    addMergeEmbargo(result, injection);
//                }
//            }
//        }
    //
//      /**
//       * Creates the comparators for the search plan.
//       * Adds a comparator based on the control labels available in the grammar, if any.
//       * @param properties rule system properties, including common and control labels are to be found
//       * @param nodeSet the node set to be matched
//       * @param edgeSet the edge set to be matched
//       * @return a list of comparators determining the order in which edges should be matched
//       * @see #createComparators(Set, Set)
//       */
//      protected List<Comparator<Edge>> createComparators(SystemProperties properties, Set<? extends Node> nodeSet, Set<? extends Edge> edgeSet) {
//          List<Comparator<Edge>> result = super.createComparators(nodeSet, edgeSet);
//          if (properties != null) {
//              List<String> controlLabels = properties.getControlLabels();
//              List<String> commonLabels = properties.getCommonLabels();
//              Comparator<Edge> labelComparator = new ControlLabelComparator(controlLabels, commonLabels);
//              int position = 0;
//              while (position < result.size() && !(result.get(position) instanceof IndegreeComparator)) {
//                  position++;
//              }
//              result.add(position, labelComparator);
//          }
//          return result;
//      }
//        
        /**
         * Inserts an edge embargo search item at the appropriate place in a 
         * search plan, namely directly after all end nodes have been matched.
         * @param result the pre-existing search plan
         * @param embargoEdge the embargo edge to be inserted
         */
        private void addEdgeEmbargo(List<SearchItem> result, Edge embargoEdge) {
            Set<Node> endSet = new HashSet<Node>(Arrays.asList(embargoEdge.ends()));
            endSet.removeAll(boundNodes);
            // the set of variables possibly occurring in the edge
            Set<String> varSet = new HashSet<String>();
            RegExpr edgeExpr = getRegExpr(embargoEdge);
            if (edgeExpr != null) {
                varSet.addAll(edgeExpr.allVarSet());
                for (Edge preMatchedEdge: boundEdges) {
                    edgeExpr = getRegExpr(preMatchedEdge);
                    if (edgeExpr != null) {
                        varSet.removeAll(edgeExpr.boundVarSet());
                    }
                }
            }
            // look for first position in result after which all
            // the embargo's ends and variables have been scheduled
            int index = 0;
            while (index < result.size() && ! (endSet.isEmpty() && varSet.isEmpty())) {
                SearchItem next = result.get(index);
                if (next instanceof NodeSearchItem) {
                    endSet.remove(((NodeSearchItem) next).getNode());
                } else if (next instanceof ValueNodeSearchItem) {
                    endSet.remove(((ValueNodeSearchItem) next).getNode());
                } else if (next instanceof EdgeSearchItem) {
                    Edge edge = ((EdgeSearchItem) next).getEdge();
                    endSet.removeAll(Arrays.asList(edge.ends()));
                    edgeExpr = getRegExpr(edge);
                    if (edgeExpr != null) {
                        varSet.removeAll(edgeExpr.boundVarSet());
                    }
                } else if (next instanceof ProductEdgeSearchItem) {
                    Edge edge = ((ProductEdgeSearchItem) next).getEdge();
                    endSet.removeAll(Arrays.asList(edge.ends()));
                }
                index++;
            }
            if (!endSet.isEmpty() || !varSet.isEmpty()) {
                throw new IllegalStateException(String.format("Embargo edge %s cannot be acheduled in %s", embargoEdge, result));
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
         * @param injection the first node to be matched injectively
         */
        private void addMergeEmbargo(List<SearchItem> result, Set<? extends Node> injection) {
            Set<Node> nodeSet = new HashSet<Node>(injection);
            nodeSet.removeAll(boundNodes);
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
            result.add(index, createInjectionSearchItem(injection));
        }

        /** The graph condition for which we develop the plan. */
        private final GraphCondition condition;
        /** The set of pre-matched nodes. */
        private final Collection<? extends Node> boundNodes;
        /** The set of pre-matched edges. */
        private final Collection<? extends Edge> boundEdges;
    }
    
    /**
     * Edge comparator on the basis of lists of high- and low-priority labels.
     * Preference is given to labels occurring early in this list.
     * @author Arend Rensink
     * @version $Revision $
     */
    static private class ControlLabelComparator implements Comparator<Edge> {
        /**
         * Constructs a comparator on the basis of two lists of labels.
         * The first list contains high-priority labels, in the order of decreasing priority;
         * the second list low-priority labels, in order of increasing priority.
         * Labels not in either list have intermediate priority and are ordered
         * alphabetically.
         * @param high high-priority labels, in order of decreasing priority; may be <code>null</code>
         * @param low low-priority labels, in order of increasing priority; may be <code>null</code>
         */
        private ControlLabelComparator(List<String> high, List<String> low) {
            this.priorities = new HashMap<String, Integer>();
            if (high != null) {
                for (int i = 0; i < high.size(); i++) {
                    priorities.put(high.get(i), high.size() - i);
                }
            }
            if (low != null) {
                for (int i = 0; i < low.size(); i++) {
                    priorities.put(low.get(i), i - low.size());
                }
            }
        }

        /**
         * Favours the edge occurring earliest in the high-priority labels, or
         * latest in the low-priority labels. In case of equal priority, alphabetical ordering is used.
         */
        public int compare(Edge first, Edge second) {
            String firstLabel = first.label().text();
            String secondLabel = second.label().text();
            // compare edge priorities
            return getEdgePriority(firstLabel) - getEdgePriority(secondLabel);
        }
        
        /**
         * Returns the priority of an edge, judged by its label.
         */
        private int getEdgePriority(String edgeLabel) {
            Integer result = priorities.get(edgeLabel);
            if (result == null) {
                return 0;
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
}
