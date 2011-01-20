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
import groove.trans.SystemProperties;

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
    private final SystemProperties properties;
    private final boolean injective;
    private final boolean ignoreNeg;

    static private SearchPlanEngine instance;

    /**
     * Factory method for creating a search engine that uses Search Plan for matching.
     * 
     * @param properties system properties determining some choices in the engine,
     * such as injectivity of the matching.
     * @return An instance of the SearchPlan matching engine
     */
    public static SearchPlanEngine getInstance(SystemProperties properties) {
        if (instance == null || instance.getProperties() != properties) {
            instance = new SearchPlanEngine(properties);
        }
        return instance;
    }

    private SearchPlanEngine(SystemProperties properties) {
        this.properties = properties;
        this.injective = properties.isInjective();
        this.ignoreNeg = false;
    }

    @Override
    public SearchPlanStrategy createMatcher(Condition condition,
            Collection<RuleNode> anchorNodes, Collection<RuleEdge> anchorEdges,
            Collection<RuleNode> relevantNodes) {
        SystemProperties properties = condition.getSystemProperties();
        return ConditionSearchPlanFactory.getInstance(properties.isInjective(),
            properties.getAlgebraFamily()).createMatcher(condition,
            anchorNodes, anchorEdges, relevantNodes);
    }

    @Override
    public SearchPlanStrategy createMatcher(RuleGraph graph,
            Collection<RuleNode> anchorNodes, Collection<RuleEdge> anchorEdges,
            LabelStore labelStore) {
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

    /** Returns the system properties associated with this engine. */
    public SystemProperties getProperties() {
        return this.properties;
    }
}
