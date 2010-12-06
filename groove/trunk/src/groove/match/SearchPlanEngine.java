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
 * $Id$
 */
package groove.match;

import groove.graph.LabelStore;
import groove.trans.Condition;
import groove.trans.RuleEdge;
import groove.trans.RuleGraph;
import groove.trans.RuleNode;

import java.util.Collection;

/**
 * This is the search engine that works based on the Search Plan 
 * algorithm. It is merely a wrapper that makes use of {@link GraphSearchPlanFactory}
 * and {@link ConditionSearchPlanFactory}.
 * 
 * @author Arash Jalali
 * @version $Revision $
 */
public class SearchPlanEngine extends SearchEngine<SearchPlanStrategy> {

    private boolean injective = false;
    private boolean ignoreNeg = false;

    static private final SearchPlanEngine[][] instances =
        new SearchPlanEngine[2][2];

    static {
        for (int injective = 0; injective <= 1; injective++) {
            for (int ignoreNeg = 0; ignoreNeg <= 1; ignoreNeg++) {
                instances[injective][ignoreNeg] =
                    new SearchPlanEngine(injective == 1, ignoreNeg == 1);
            }
        }
    }

    public static SearchPlanEngine getInstance(boolean injective) {
        return getInstance(injective, false);
    }

    public static SearchPlanEngine getInstance(boolean injective,
            boolean ignoreNeg) {
        return instances[injective ? 1 : 0][ignoreNeg ? 1 : 0];
    }

    private SearchPlanEngine(boolean injective, boolean ignoreNeg) {
        this.injective = injective;
        this.ignoreNeg = ignoreNeg;
    }

    @Override
    public SearchPlanStrategy createMatcher(Condition condition,
            Collection<RuleNode> anchorNodes,
            Collection<RuleEdge> anchorEdges,
            Collection<RuleNode> relevantNodes) {
        return ConditionSearchPlanFactory.getInstance(
            condition.getSystemProperties().isInjective()).createMatcher(
            condition, anchorNodes, anchorEdges, relevantNodes);
    }

    @Override
    public SearchPlanStrategy createMatcher(RuleGraph graph,
            Collection<RuleNode> anchorNodes,
            Collection<RuleEdge> anchorEdges, LabelStore labelStore) {
        return GraphSearchPlanFactory.getInstance(this.isInjective(),
            this.isIgnoreNeg()).createMatcher(graph, anchorNodes, anchorEdges,
            labelStore);
    }

    @Override
    public final boolean isIgnoreNeg() {
        return this.ignoreNeg;
    }

    @Override
    public boolean isInjective() {
        return this.injective;
    }

}
