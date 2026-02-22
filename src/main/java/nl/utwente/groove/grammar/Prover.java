/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.grammar;

import static nl.utwente.groove.util.Factory.lazy;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.grammar.host.HostEdge;
import nl.utwente.groove.grammar.host.HostGraph;
import nl.utwente.groove.grammar.host.HostNode;
import nl.utwente.groove.grammar.host.ValueNode;
import nl.utwente.groove.grammar.rule.Anchor;
import nl.utwente.groove.grammar.rule.MatchChecker;
import nl.utwente.groove.grammar.rule.RuleNode;
import nl.utwente.groove.grammar.rule.RuleToHostMap;
import nl.utwente.groove.match.Matcher;
import nl.utwente.groove.match.MatcherFactory;
import nl.utwente.groove.match.SearchStrategy;
import nl.utwente.groove.match.TreeMatch;
import nl.utwente.groove.transform.Proof;
import nl.utwente.groove.util.Visitor;

/**
 * Class to obtain proofs of nested conditions.
 * @author Arend Rensink
 * @version $Revision$
 */
public class Prover {
    /**
     * Constructs a proved for a given nested rule.
     */
    public Prover(Rule rule) {
        this.rule = rule;
    }

    /**
     * Returns the rule for which this is a prover.
     */
    public Rule getRule() {
        return this.rule;
    }

    private final Rule rule;

    /**
     * Returns the condition for which this is a prover.
     */
    public Condition getCondition() {
        return getRule().getCondition();
    }

    /** Sets the match filter method. */
    public void setMatchFilter(MatchChecker matchFilter) {
        this.matchFilter = matchFilter;
    }

    /** Returns the optional match filter method. */
    public Optional<MatchChecker> getMatchFilter() {
        return Optional.ofNullable(this.matchFilter);
    }

    private @Nullable MatchChecker matchFilter;

    /**
     * Tests if the condition is ground and has a proof for a given host graph.
     */
    final public boolean hasProof(HostGraph host) {
        return getCondition().getInputNodes().isEmpty() && getProof(host) != null;
    }

    /**
     * Returns a proof of this condition for a given host graph.
     * @param host the graph in which the match is to be found
     */
    public Proof getProof(HostGraph host) {
        return traverseMatches(host, null, Visitor.<Proof>newFinder(null));
    }

    /**
     * Returns the collection of all matches for a given host graph, given a
     * matching of the root context.
     * @param host the graph in which the match is to be found
     */
    public Collection<Proof> getAllMatches(HostGraph host) {
        List<Proof> result = new ArrayList<>();
        traverseMatches(host, null, Visitor.newCollector(result));
        return result;
    }

    /**
     * Traverses the matches of this rule on a given host graph and for
     * a given context map, and calls the visitor's visit method on all
     * of them, until the first time the visitor returns
     * {@code false}.
     * @param host the graph in which the match is to be found
     * @param contextMap a matching of the pattern of this condition; may be
     *        <code>null</code> if the condition is ground.
     * @param visitor the visitor invoked for all the matches
     * @return the result of the visitor after the traversal
     * @throws IllegalArgumentException if <code>patternMatch</code> is
     *         <code>null</code> and the condition is not ground, or if
     *         <code>patternMatch</code> is not compatible with the pattern
     *         graph
     * @see Visitor#visit(Object)
     */
    public <R> R traverseMatches(final HostGraph host, RuleToHostMap contextMap,
                                 final Visitor<Proof,R> visitor) {
        RuleToHostMap seedMap = contextMap == null
            ? host.getFactory().createRuleToHostMap()
            : contextMap;
        getMatcher(seedMap).traverse(host, contextMap, new Visitor<TreeMatch,R>() {
            @Override
            protected boolean process(TreeMatch match) {
                assert visitor.isContinue();
                if (isValidPatternMap(host, match.getPatternMap())) {
                    match.traverseProofs(visitor);
                }
                return visitor.isContinue();
            }
        });
        return visitor.getResult();
    }

    /**
     * Returns the match strategy for the target
     * pattern. First creates the strategy using
     * {@link #createMatcher(Anchor, boolean)} if that
     * has not been done.
     *
     * @param seedMap mapping from the seed elements to a host graph.
     *
     * @see #createMatcher(Anchor, boolean)
     */
    private SearchStrategy getMatcher(RuleToHostMap seedMap) {
        Matcher result;
        boolean simple = seedMap.getFactory().isSimple();
        Signature<UnitPar.RulePar> sig = getRule().getSignature();
        if (!sig.isEmpty()) {
            int sigSize = sig.size();
            BitSet initPars = new BitSet(sigSize);
            for (int i = 0; i < sigSize; i++) {
                // set initPars if the seed map contains a value
                // for this parameter
                initPars.set(i, seedMap.nodeMap().containsKey(sig.getPar(i).getNode()));
            }
            result = this.matcherMap.get(initPars);
            if (result == null) {
                Anchor seed = new Anchor(seedMap.nodeMap().keySet());
                this.matcherMap.put(initPars, result = createMatcher(seed, simple));
            }
        } else {
            result = getMatcher(simple);
        }
        return result;
    }

    /**
     * Callback method to create a match strategy. Typically invoked once, at
     * the first invocation of {@link #getMatcher(boolean)}.
     * @param seed the pre-matched subgraph
     * @param simple indicates if the host graphs are simple or multi-graphs
     */
    private Matcher createMatcher(Anchor seed, boolean simple) {
        return MatcherFactory.instance(simple).createMatcher(this.rule.getCondition(), seed);
    }

    /**
     * Mapping from sets of initialised parameters to match strategies.
     */
    private final Map<BitSet,Matcher> matcherMap = new HashMap<>();

    /**
     * Returns a (precomputed) match strategy for the target
     * pattern, based on the rule seed.
     * @param simple indicates if the host graphs are simple or multi-graphs
     * @see #createMatcher(Anchor, boolean)
     */
    public Matcher getMatcher(boolean simple) {
        return simple
            ? this.simpleSeedMatcher.get()
            : this.multiSeedMatcher.get();
    }

    /**
     * The fixed simple matching strategy for this graph rule. Initially
     * <code>null</code>; set by {@link #getMatcher(boolean)} upon its first
     * invocation.
     */
    private final Supplier<Matcher> simpleSeedMatcher
        = lazy(() -> createMatcher(getRule().getSeed(), true));

    /**
     * The fixed multi-graph matching strategy for this graph rule. Initially
     * <code>null</code>; set by {@link #getMatcher(boolean)} upon its first
     * invocation.
     */
    private final Supplier<Matcher> multiSeedMatcher
        = lazy(() -> createMatcher(getRule().getSeed(), false));

    /**
     * Lazily creates and returns a matcher for rule events of this rule. The
     * matcher will try to extend anchor maps to full matches. This is in
     * contrast with the normal (condition) matcher, which is based on the
     * images of the root map.
     */
    public Matcher getEventMatcher(boolean simple) {
        return simple
            ? this.simpleEventMatcher.get()
            : this.multiEeventMatcher.get();
    }

    /** The matcher for simple graph events of this rule. */
    private final Supplier<Matcher> simpleEventMatcher
        = lazy(() -> createMatcher(getRule().getAnchor(), true));

    /** The matcher for multi-graph events of this rule. */
    private final Supplier<Matcher> multiEeventMatcher
        = lazy(() -> createMatcher(getRule().getAnchor(), false));

    /**
     * Tests whether a given match map satisfies the additional constraints
     * imposed by this rule.
     * @param host the graph to be matched
     * @param matchMap the proposed map from the rule pattern to
     *        <code>host</code>
     * @return <code>true</code> if <code>matchMap</code> satisfies the
     *         constraints imposed by the rule (if any).
     */
    public boolean isValidPatternMap(HostGraph host, RuleToHostMap matchMap) {
        boolean result = true;
        if (getRule().isCheckDangling()) {
            result = satisfiesDangling(host, matchMap);
        }
        return result;
    }

    /**
     * Tests if a given (proposed) match into a host graph leaves dangling
     * edges.
     */
    private boolean satisfiesDangling(HostGraph host, RuleToHostMap match) {
        boolean result = true;
        for (RuleNode eraserNode : getRule().getEraserNodes()) {
            HostNode erasedNode = match.getNode(eraserNode);
            assert erasedNode != null;
            Set<HostEdge> danglingEdges = host
                .edgeSet(erasedNode)
                .stream()
                .filter(e -> !(e.target() instanceof ValueNode))
                .collect(Collectors.toSet());
            getRule()
                .lhs()
                .edgeSet(eraserNode)
                .stream()
                .map(e -> match.getEdge(e))
                .filter(e -> !(e == null || e.target() instanceof ValueNode))
                .forEach(e -> danglingEdges.remove(e));
            if (!danglingEdges.isEmpty()) {
                result = false;
                break;
            }
        }
        return result;
    }
}
