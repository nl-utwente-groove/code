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
 * $Id: ConditionSearchPlanFactory.java,v 1.10 2007-09-22 09:10:36 rensink Exp $
 */
package groove.match;

import groove.graph.Edge;
import groove.graph.Node;
import groove.trans.DefaultGraphCondition;
import groove.trans.GraphCondition;
import groove.trans.SystemProperties;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * Factory that adds to a graph search plan the following items the search items for the simple negative conditions (edge and merge embargoes).
 * @author Arend Rensink
 * @version $Revision: 1.10 $
 */
public class ConditionSearchPlanFactory extends GraphSearchPlanFactory {
    /** 
     * Private, empty constructor.
     * This is a singleton class; get the instance through {@link #getInstance()}.
     */
    ConditionSearchPlanFactory() {
        // empty
    }

    /** 
     * Factory method returning a search plan for a graph condition.
     * This extends the ordinary search plan with negative tests, and
     * Takes control and common labels into account (if any).
     * @param condition the condition for which a search plan is to be constructed
     */
    public SearchPlanStrategy createMatcher(GraphCondition condition) {
    	return createMatcher(condition, condition.getPattern().nodeMap().values(), condition.getPattern().edgeMap().values());
    }

    /** 
     * Factory method returning a search plan for a graph condition,
     * taking into account that a certain set of nodes and edges has been matched already.
     * This extends the ordinary search plan with negative tests.
     * Takes control and common labels into account (if any).
     * @param condition the condition for which a search plan is to be constructed
     * @param preMatchedNodes the nodes of the condition that have been matched already
     * @param preMatchedEdges the edges of the condition that have been matched already
     */
    public SearchPlanStrategy createMatcher(GraphCondition condition, Collection<? extends Node> preMatchedNodes, Collection<? extends Edge> preMatchedEdges) {
    	PlanData planData = new GrammarPlanData(condition, preMatchedNodes, preMatchedEdges);
    	SearchPlanStrategy result = new SearchPlanStrategy(condition.getTarget(), planData.getPlan(), condition.getProperties().isInjective());
        if (PRINT) {
            System.out.print(String.format("%nPlan for %s, prematched nodes %s, prematched edges %s:%n    %s", condition.getName(), preMatchedNodes, preMatchedEdges, result));
        }
        result.setFixed();
        return result;
    }
    

    /** Returns the singleton instance of this factory class. */
    static public ConditionSearchPlanFactory getInstance() {
        return instance;
    }
    
    /** The fixed, singleton instance of this factory. */
    static private final ConditionSearchPlanFactory instance = new ConditionSearchPlanFactory();

    /** Flag to control search plan printing. */
    static private final boolean PRINT = false;
    
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
         * @param preMatchedNodes the set of pre-matched nodes
         * @param preMatchedEdges the set of pre-matched edges
         */
        GrammarPlanData(GraphCondition condition, Collection<? extends Node> preMatchedNodes, Collection<? extends Edge> preMatchedEdges) {
            super(condition.getTarget(), preMatchedNodes, preMatchedEdges);
            this.condition = condition;
        }

        /**
         * Adds embargo and injection search items to the super result.
         */
        @Override Collection<SearchItem> computeSearchItems() {
            Collection<SearchItem> result = super.computeSearchItems();
            if (condition instanceof DefaultGraphCondition) {
                Set<Edge> negations = ((DefaultGraphCondition) condition).getNegations();
                if (negations != null) {
                    for (Edge embargoEdge : negations) {
                        result.add(createNegatedSearchItem(createEdgeSearchItem(embargoEdge)));
                    }
                }
                Set<Set<? extends Node>> injections = ((DefaultGraphCondition) condition).getInjections();
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
        private final GraphCondition condition;
    }
}
