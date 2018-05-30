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
 * $Id $
 */

package groove.match;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import groove.grammar.Condition;
import groove.grammar.host.HostEdge;
import groove.grammar.host.HostGraph;
import groove.grammar.host.HostNode;
import groove.grammar.rule.Anchor;
import groove.grammar.rule.LabelVar;
import groove.grammar.rule.RuleEdge;
import groove.grammar.rule.RuleNode;
import groove.grammar.rule.RuleToHostMap;
import groove.grammar.rule.Valuation;
import groove.util.Visitor;
import groove.util.Visitor.Collector;
import groove.util.Visitor.Finder;

/**
 * Strategy for finding tree matches of a condition in a host graph,
 * given an initial seed map for that condition.
 * @author Arend Rensink
 * @version $Revision $
 */
public class Matcher implements SearchStrategy {
    /** Constructs a fresh instance of this strategy, for a given
     * condition and sets of seeded (i.e., pre-matched) nodes and edges.
     * @param factory the matcher factory that has created this matcher object
     * @param condition the condition that the strategy will find matches for
     * @param seed set of nodes that will be pre-matched when the strategy is invoked
     */
    public Matcher(MatcherFactory factory, Condition condition, Anchor seed) {
        this.factory = factory;
        this.condition = condition;
        if (seed == null && condition.getOp()
            .hasPattern()) {
            seed = new Anchor();
            seed.addAll(condition.getInputNodes());
            seed.addAll(condition.getRoot()
                .edgeSet());
            seed.addAll(condition.getRoot()
                .varSet());
        }
        this.seed = seed;
    }

    /**
     * Returns the first match that satisfies a given property.
     * @param host the host graph into which the matching is to go
     * @param seedMap a predefined mapping to the elements of
     *        <code>host</code> that all the solutions should respect. May be
     *        <code>null</code> if there is no predefined mapping
     */
    public TreeMatch find(HostGraph host, RuleToHostMap seedMap) {
        Finder<TreeMatch> finder = this.finder.newInstance();
        TreeMatch result = traverse(host, seedMap, finder);
        finder.dispose();
        return result;
    }

    /**
     * Returns the list of all matches that satisfy a given property.
     * @param host the host graph into which the matching is to go
     * @param seedMap a predefined mapping to the elements of
     *        <code>host</code> that all the solutions should respect. May be
     *        <code>null</code> if there is no predefined mapping
     */
    public List<TreeMatch> findAll(HostGraph host, RuleToHostMap seedMap) {
        List<TreeMatch> result = new ArrayList<>();
        Collector<TreeMatch,List<TreeMatch>> collector = this.collector.newInstance(result);
        traverse(host, seedMap, collector);
        collector.dispose();
        return result;
    }

    /**
     * Traverses the matches, and calls a visit method on them.
     * The traversal stops when the visit method returns {@code false}.
     * The visitor is disposed afterwards.
     * @param host the host graph into which the matching is to go
     * @param seedMap a predefined mapping to the elements of
     *        <code>host</code> that all the solutions should respect. May be
     *        <code>null</code> if there is no predefined mapping
     * @param visitor the object whose visit method is invoked for all matches.
     * The visitor is reset after usage.
     * @return the result of the visitor after the traversal
     * @see Visitor#visit(Object)
     * @see Visitor#getResult()
     * @see Visitor#dispose()
     */
    @Override
    public <T> T traverse(HostGraph host, RuleToHostMap seedMap, Visitor<TreeMatch,T> visitor) {
        assert isCorrectSeeding(seedMap);
        assert host.getFactory()
            .getTypeFactory()
            .getGraph() == this.condition.getTypeGraph();
        return getSearchStrategy().traverse(host, seedMap, visitor);
    }

    /** Returns the condition that this strategy finds matches for. */
    public final Condition getCondition() {
        return this.condition;
    }

    /** Returns the subgraph that should be pre-matched when this strategy is invoked. */
    public final Anchor getSeed() {
        return this.seed;
    }

    @Override
    public SearchEngine getEngine() {
        return this.factory.getEngine();
    }

    /**
     * Tests if a given seed map precisely provides images for the
     * declared seed nodes and edges of this strategy.
     * The method either returns {@code true} or throws an
     * {@link IllegalArgumentException}, and so can be used in an assert
     * statement while giving rise to a useful error message.
     * @param seedMap the seed map to be tested
     * @return {@code true} if the seed map is correct
     * @throws IllegalArgumentException if the seed map does not contain the
     * correct elements
     */
    private final boolean isCorrectSeeding(RuleToHostMap seedMap) throws IllegalArgumentException {
        if (seedMap == null) {
            // the seed map is null, so there should not be any seed nodes or edges
            if (this.seed != null && !this.seed.isEmpty()) {
                throw new IllegalArgumentException("Unmatched seed keys: " + this.seed);
            }
        } else {
            if (!seedMap.nodeMap()
                .keySet()
                .equals(this.seed.nodeSet())) {
                // test for the difference between seed nodes and the seed map
                Set<RuleNode> seedNodes = new HashSet<>(this.seed.nodeSet());
                seedNodes.removeAll(seedMap.nodeMap()
                    .keySet());
                if (!seedNodes.isEmpty()) {
                    throw new IllegalArgumentException("Unmatched seed nodes: " + seedNodes);
                }
                Map<RuleNode,HostNode> seedNodeMap = new HashMap<>(seedMap.nodeMap());
                Set<RuleNode> seedNodeKeys = seedNodeMap.keySet();
                seedNodeKeys.removeAll(this.seed.nodeSet());
                for (RuleEdge edge : this.seed.edgeSet()) {
                    seedNodeKeys.remove(edge.source());
                    seedNodeKeys.remove(edge.target());
                }
                seedNodeKeys.retainAll(getCondition().getPattern()
                    .nodeSet());
                if (!seedNodeMap.isEmpty()) {
                    throw new IllegalArgumentException("Spurious node seeding: " + seedNodeMap);
                }
            }
            if (!seedMap.edgeMap()
                .keySet()
                .equals(this.seed.edgeSet())) {
                Set<RuleEdge> seedEdges = new HashSet<>(this.seed.edgeSet());
                seedEdges.removeAll(seedMap.edgeMap()
                    .keySet());
                if (!seedEdges.isEmpty()) {
                    throw new IllegalArgumentException("Unmatched seed edges: " + seedEdges);
                }
                Map<RuleEdge,HostEdge> seedEdgeMap = new HashMap<>(seedMap.edgeMap());
                seedEdgeMap.keySet()
                    .removeAll(this.seed.edgeSet());
                seedEdgeMap.keySet()
                    .retainAll(getCondition().getPattern()
                        .edgeSet());
                if (!seedEdges.isEmpty()) {
                    throw new IllegalArgumentException("Spurious edge seeding: " + seedEdgeMap);
                }
            }
            if (!seedMap.getValuation()
                .keySet()
                .equals(this.seed.varSet())) {
                Set<LabelVar> seedVars = new HashSet<>(this.seed.varSet());
                seedVars.removeAll(seedMap.getValuation()
                    .keySet());
                if (!seedVars.isEmpty()) {
                    throw new IllegalArgumentException("Unmatched seed variables: " + seedVars);
                }
                Valuation seedValuation = new Valuation(seedMap.getValuation());
                seedValuation.keySet()
                    .removeAll(this.seed.varSet());
                seedValuation.keySet()
                    .retainAll(getCondition().getPattern()
                        .varSet());
                if (!seedVars.isEmpty()) {
                    throw new IllegalArgumentException(
                        "Spurious variable seeding: " + seedValuation);
                }
            }
        }
        return true;
    }

    /**
     * Returns the inner search strategy responsible for the actual
     * searching. If required, the inner strategy is updated with respect to the
     * search engine wrapped in the matcher factory.
     */
    public final SearchStrategy getSearchStrategy() {
        if (this.inner == null || this.inner.getEngine() != getEngine()) {
            this.inner = getEngine().createMatcher(getCondition(), getSeed());
        }
        return this.inner;
    }

    private final MatcherFactory factory;
    private final Condition condition;
    private final Anchor seed;

    private SearchStrategy inner;
    /** Reusable finder for {@link #find(HostGraph, RuleToHostMap)}. */
    private final Finder<TreeMatch> finder = Visitor.newFinder(null);
    /** Reusable collector for {@link #findAll(HostGraph, RuleToHostMap)}. */
    private final Collector<TreeMatch,List<TreeMatch>> collector = Visitor.newCollector(null);
}
