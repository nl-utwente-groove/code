/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on an 
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific 
 * language governing permissions and limitations under the License.
 *
 * $Id: ConditionSearchPlanFactory.java,v 1.17 2007-10-10 08:59:50 rensink Exp $
 */
package groove.match;

import groove.graph.Edge;
import groove.graph.Node;
import groove.graph.NodeEdgeMap;
import groove.trans.AbstractCondition;
import groove.trans.Condition;
import groove.trans.EdgeEmbargo;
import groove.trans.MergeEmbargo;
import groove.trans.NotCondition;
import groove.trans.SystemProperties;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * Factory that adds to a graph search plan the following items the search items for the simple negative conditions (edge and merge embargoes).
 * @author Arend Rensink
 * @version $Revision: 1.17 $
 */
public class ConditionSearchPlanFactory extends GraphSearchPlanFactory {
    /** 
     * Private constructor. 
     * This is a singleton class; get the instance through {@link #getInstance(boolean)}.
     * @param injective if <code>true</code>, the factory produces injective matchers only
     */
    private ConditionSearchPlanFactory(boolean injective) {
        super(injective, false);
    }

    /** 
     * Factory method returning a search plan for a graph condition.
     * This extends the ordinary search plan with negative tests, and
     * Takes control and common labels into account (if any).
     * @param condition the condition for which a search plan is to be constructed
     * @deprecated Use {@link #createMatcher(Condition)} instead
     */
    @Deprecated
    public SearchPlanStrategy createMatcher(groove.trans.GraphCondition condition) {
        NodeEdgeMap patternMap = condition.getPattern().elementMap();
        return createMatcher(condition, patternMap.nodeMap().values(), patternMap.edgeMap().values());
    }

    /** 
     * Factory method returning a search plan for a graph condition.
     * This extends the ordinary search plan with negative tests, and
     * Takes control and common labels into account (if any).
     * @param condition the condition for which a search plan is to be constructed
     */
    public SearchPlanStrategy createMatcher(Condition condition) {
    	NodeEdgeMap patternMap = condition.getRootMap();
    	return createMatcher(condition, patternMap.nodeMap().values(), patternMap.edgeMap().values());
    }

    /** 
     * Factory method returning a search plan for a graph condition,
     * taking into account that a certain set of nodes and edges has been matched already.
     * This extends the ordinary search plan with negative tests.
     * Takes control and common labels into account (if any).
     * @param condition the condition for which a search plan is to be constructed
     * @param anchorNodes the nodes of the condition that have been matched already
     * @param anchorEdges the edges of the condition that have been matched already
     * @deprecated Use {@link #createMatcher(Condition,Collection,Collection)} instead
     */
    @Deprecated
    public SearchPlanStrategy createMatcher(groove.trans.GraphCondition condition, Collection<? extends Node> anchorNodes, Collection<? extends Edge> anchorEdges) {
        PlanData planData = new OldGrammarPlanData(condition);
        SearchPlanStrategy result = new SearchPlanStrategy(condition.getTarget(), planData.getPlan(anchorNodes, anchorEdges), isInjective());
        if (PRINT) {
            System.out.print(String.format("%nPlan for %s, prematched nodes %s, prematched edges %s:%n    %s", condition.getName(), anchorNodes, anchorEdges, result));
        }
        result.setFixed();
        return result;
    }

    /** 
     * Factory method returning a search plan for a graph condition,
     * taking into account that a certain set of nodes and edges has been matched already.
     * This extends the ordinary search plan with negative tests.
     * Takes control and common labels into account (if any).
     * @param condition the condition for which a search plan is to be constructed
     * @param anchorNodes the nodes of the condition that have been matched already
     * @param anchorEdges the edges of the condition that have been matched already
     */
    public SearchPlanStrategy createMatcher(Condition condition, Collection<? extends Node> anchorNodes, Collection<? extends Edge> anchorEdges) {
    	PlanData planData = new GrammarPlanData(condition);
    	SearchPlanStrategy result = new SearchPlanStrategy(condition.getTarget(), planData.getPlan(anchorNodes, anchorEdges), isInjective());
        if (PRINT) {
            System.out.print(String.format("%nPlan for %s, prematched nodes %s, prematched edges %s:%n    %s", condition.getName(), anchorNodes, anchorEdges, result));
        }
        result.setFixed();
        return result;
    }
    

    /** Returns the singleton instance of this factory class. */
    static public ConditionSearchPlanFactory getInstance(boolean injective) {
        return injective ? injectiveInstance : nonInjectiveInstance;
    }
    
    /** Instance of this factory for non-injective matchings. */
    static private final ConditionSearchPlanFactory nonInjectiveInstance = new ConditionSearchPlanFactory(false);
    /** The fixed, singleton instance of this factory for injective matchings. */
    static private final ConditionSearchPlanFactory injectiveInstance = new ConditionSearchPlanFactory(true);

    /** Flag to control search plan printing. */
    static private final boolean PRINT = false;
    
    /**
     * Plan data extension based on a graph condition.
     * Additionally it takes the control labels of the condition into account.
     * @author Arend Rensink
     * @version $Revision $
     */
    @Deprecated
    class OldGrammarPlanData extends PlanData {
        /** 
         * Constructs a fresh instance of the plan data,
         * based on a given set of system properties, and sets
         * of already matched nodes and edges. 
         * @param condition the graph condition for which we develop the search plan
         */
        OldGrammarPlanData(groove.trans.GraphCondition condition) {
            super(condition.getTarget());
            this.condition = condition;
        }

        /**
         * Adds embargo and injection search items to the super result.
         * @param anchorNodes the set of pre-matched nodes
         * @param anchorEdges the set of pre-matched edges
         */
        @Override 
        Collection<SearchItem> computeSearchItems(Collection<? extends Node> anchorNodes, Collection<? extends Edge> anchorEdges) {
            Collection<SearchItem> result = super.computeSearchItems(anchorNodes, anchorEdges);
            if (condition instanceof groove.trans.DefaultGraphCondition) {
                Set<Edge> negations = ((groove.trans.DefaultGraphCondition) condition).getNegations();
                if (negations != null) {
                    for (Edge embargoEdge : negations) {
                        result.add(createNegatedSearchItem(createEdgeSearchItem(embargoEdge)));
                    }
                }
                Set<Set<? extends Node>> injections = ((groove.trans.DefaultGraphCondition) condition).getInjections();
                if (injections != null) {
                    for (Set<? extends Node> injection : injections) {
                        result.add(createInjectionSearchItem(injection));
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
        @Override Collection<Comparator<SearchItem>> computeComparators() {
            Collection<Comparator<SearchItem>> result = super.computeComparators();
            SystemProperties properties = condition.getProperties();
            if (properties != null) {
                List<String> controlLabels = properties.getControlLabels();
                List<String> commonLabels = properties.getCommonLabels();
                result.add(new FrequencyComparator(controlLabels, commonLabels));
            }
            return result;
        }

        /** The graph condition for which we develop the plan. */
        private final groove.trans.GraphCondition condition;
    }
    
    /**
     * Plan data extension based on a graph condition.
     * Additionally it takes the control labels of the condition into account.
     * @author Arend Rensink
     * @version $Revision $
     */
    class GrammarPlanData extends PlanData {
        /** 
         * Constructs a fresh instance of the plan data,
         * based on a given set of system properties, and sets
         * of already matched nodes and edges. 
         * @param condition the graph condition for which we develop the search plan
         */
        GrammarPlanData(Condition condition) {
            super(condition.getTarget());
            this.condition = condition;
        }

        /**
         * Adds embargo and injection search items to the super result.
         * @param anchorNodes the set of pre-matched nodes
         * @param anchorEdges the set of pre-matched edges
         */
        @Override 
        Collection<SearchItem> computeSearchItems(Collection<? extends Node> anchorNodes, Collection<? extends Edge> anchorEdges) {
            Collection<SearchItem> result = super.computeSearchItems(anchorNodes, anchorEdges);
                for (Condition subCondition: ((AbstractCondition<?>) condition).getSubConditions()) {
                    if (subCondition instanceof MergeEmbargo) {
                        Node node1 = ((MergeEmbargo) subCondition).node1();
                        Node node2 = ((MergeEmbargo) subCondition).node2();
                        result.add(createInjectionSearchItem(node1, node2));
                    } else if (subCondition instanceof EdgeEmbargo) {
                        Edge embargoEdge = ((EdgeEmbargo) subCondition).getEmbargoEdge();
                        result.add(createNegatedSearchItem(createEdgeSearchItem(embargoEdge)));
                    } else if (subCondition instanceof NotCondition) {
                        result.add(new ConditionSearchItem(subCondition));
                    }
            }
            return result;
        }

        /**
         * Creates the comparators for the search plan.
         * Adds a comparator based on the control labels available in the grammar, if any.
         * @return a list of comparators determining the order in which edges should be matched
         */
        @Override Collection<Comparator<SearchItem>> computeComparators() {
            Collection<Comparator<SearchItem>> result = super.computeComparators();
            SystemProperties properties = condition.getProperties();
            if (properties != null) {
                List<String> controlLabels = properties.getControlLabels();
                List<String> commonLabels = properties.getCommonLabels();
                result.add(new FrequencyComparator(controlLabels, commonLabels));
            }
            return result;
        }

        /** The graph condition for which we develop the plan. */
        private final Condition condition;
    }
}
