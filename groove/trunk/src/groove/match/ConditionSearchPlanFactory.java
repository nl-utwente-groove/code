/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * $Id: ConditionSearchPlanFactory.java,v 1.23 2008-02-05 13:43:26 rensink Exp $
 */
package groove.match;

import groove.algebra.AlgebraRegister;
import groove.rel.LabelVar;
import groove.trans.AbstractCondition;
import groove.trans.Condition;
import groove.trans.EdgeEmbargo;
import groove.trans.MergeEmbargo;
import groove.trans.NotCondition;
import groove.trans.RuleEdge;
import groove.trans.RuleGraphMorphism;
import groove.trans.RuleNode;
import groove.trans.SystemProperties;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Factory that adds to a graph search plan the following items the search items
 * for the simple negative conditions (edge and merge embargoes).
 * @author Arend Rensink
 * @version $Revision$
 */
public class ConditionSearchPlanFactory extends GraphSearchPlanFactory {
    /**
     * Private constructor. Get the instance through
     * {@link #getInstance(boolean,String)}.
     * @param injective if <code>true</code>, the factory produces injective
     *        matchers only
     * @param algebraFamily the name of the set of algebras to be used in
     * data value manipulation
     * @see AlgebraRegister#getInstance(String)
     */
    private ConditionSearchPlanFactory(boolean injective, String algebraFamily) {
        super(injective, false, algebraFamily);
    }

    /**
     * Factory method returning a search plan for a graph condition. This
     * extends the ordinary search plan with negative tests, and Takes control
     * and common labels into account (if any).
     * @param condition the condition for which a search plan is to be
     *        constructed
     */
    public SearchPlanStrategy createMatcher(Condition condition) {
        return createMatcher(condition, null, null);
    }

    /**
     * Factory method returning a search plan for a graph condition, taking into
     * account that a certain set of nodes and edges has been matched already.
     * This extends the ordinary search plan with negative tests. Takes control
     * and common labels into account (if any).
     * @param condition the condition for which a search plan is to be
     *        constructed
     * @param anchorNodes the nodes of the condition that have been matched
     *        already
     * @param anchorEdges the edges of the condition that have been matched
     *        already
     */
    public SearchPlanStrategy createMatcher(Condition condition,
            Collection<RuleNode> anchorNodes, Collection<RuleEdge> anchorEdges) {
        return createMatcher(condition, anchorNodes, anchorEdges, null);
    }

    /**
     * Factory method returning a search plan for a graph condition, taking into
     * account that a certain set of nodes and edges has been matched already.
     * This extends the ordinary search plan with negative tests. Takes control
     * and common labels into account (if any).
     * @param condition the condition for which a search plan is to be
     *        constructed
     * @param anchorNodes the nodes of the condition that have been matched
     *        already; if <code>null</code>, the condition's pattern map values
     *        are used
     * @param anchorEdges the edges of the condition that have been matched
     *        already; if <code>null</code>, the condition's pattern map values
     *        are used
     * @param relevantNodes nodes from the condition whose image should be a
     *        distinguishing factor in the returned matches; if
     *        <code>null</code>, all nodes are relevant
     */
    public SearchPlanStrategy createMatcher(Condition condition,
            Collection<RuleNode> anchorNodes, Collection<RuleEdge> anchorEdges,
            Collection<RuleNode> relevantNodes) {
        assert (anchorNodes == null) == (anchorEdges == null) : "Anchor nodes and edges should be null simultaneously";
        if (anchorNodes == null) {
            RuleGraphMorphism patternMap = condition.getRootMap();
            anchorNodes = patternMap.nodeMap().values();
            anchorEdges = patternMap.edgeMap().values();
        }
        PlanData planData = new GrammarPlanData(condition);
        List<AbstractSearchItem> plan =
            planData.getPlan(anchorNodes, anchorEdges);
        if (relevantNodes != null) {
            Set<RuleNode> unboundRelevantNodes =
                new HashSet<RuleNode>(relevantNodes);
            Set<LabelVar> boundVars = new HashSet<LabelVar>();
            for (AbstractSearchItem item : plan) {
                item.setRelevant(unboundRelevantNodes.removeAll(item.bindsNodes())
                    | boundVars.addAll(item.bindsVars()));
            }
        }
        SearchPlanStrategy result =
            new SearchPlanStrategy(condition.getTarget(), plan, this.injective);
        if (PRINT) {
            System.out.print(String.format(
                "%nPlan for %s, prematched nodes %s, prematched edges %s:%n    %s",
                condition.getName(), anchorNodes, anchorEdges, result));
        }
        result.setFixed();
        return result;
    }

    /** Returns an instance of this factory class.
     * @param injective if <code>true</code>, the factory produces injective
     *        matchers only
     * @param algebraFamily the name of the set of algebras to be used in
     * data value manipulation
     * @see AlgebraRegister#getInstance(String)
     */
    static public ConditionSearchPlanFactory getInstance(boolean injective,
            String algebraFamily) {
        if (instance == null || instance.injective != injective
            || instance.algebraFamily.equals(algebraFamily)) {
            instance = new ConditionSearchPlanFactory(injective, algebraFamily);
        }
        return instance;
    }

    static private ConditionSearchPlanFactory instance;

    /** Flag to control search plan printing. */
    static private final boolean PRINT = false;

    /**
     * Plan data extension based on a graph condition. Additionally it takes the
     * control labels of the condition into account.
     * @author Arend Rensink
     * @version $Revision $
     */
    class GrammarPlanData extends PlanData {
        /**
         * Constructs a fresh instance of the plan data, based on a given set of
         * system properties, and sets of already matched nodes and edges.
         * @param condition the graph condition for which we develop the search
         *        plan
         */
        GrammarPlanData(Condition condition) {
            super(condition.getTarget(), condition.getLabelStore());
            this.condition = condition;
        }

        /**
         * Adds embargo and injection search items to the super result.
         * @param anchorNodes the set of pre-matched nodes
         * @param anchorEdges the set of pre-matched edges
         */
        @Override
        Collection<AbstractSearchItem> computeSearchItems(
                Collection<RuleNode> anchorNodes,
                Collection<RuleEdge> anchorEdges) {
            Collection<AbstractSearchItem> result =
                super.computeSearchItems(anchorNodes, anchorEdges);
            for (Condition subCondition : ((AbstractCondition<?>) this.condition).getSubConditions()) {
                if (subCondition instanceof MergeEmbargo) {
                    RuleNode node1 = ((MergeEmbargo) subCondition).node1();
                    RuleNode node2 = ((MergeEmbargo) subCondition).node2();
                    result.add(createInjectionSearchItem(node1, node2));
                } else if (subCondition instanceof EdgeEmbargo) {
                    RuleEdge embargoEdge =
                        ((EdgeEmbargo) subCondition).getEmbargoEdge();
                    result.add(createNegatedSearchItem(createEdgeSearchItem(embargoEdge)));
                } else if (subCondition instanceof NotCondition) {
                    result.add(new ConditionSearchItem(subCondition));
                }
            }
            return result;
        }

        /**
         * Creates the comparators for the search plan. Adds a comparator based
         * on the control labels available in the grammar, if any.
         * @return a list of comparators determining the order in which edges
         *         should be matched
         */
        @Override
        Collection<Comparator<SearchItem>> computeComparators() {
            Collection<Comparator<SearchItem>> result =
                super.computeComparators();
            SystemProperties properties = this.condition.getSystemProperties();
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
