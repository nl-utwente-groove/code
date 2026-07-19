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
package nl.utwente.groove.match.plan;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nl.utwente.groove.grammar.Condition;
import nl.utwente.groove.grammar.rule.Anchor;
import nl.utwente.groove.grammar.rule.LabelVar;
import nl.utwente.groove.grammar.rule.RuleEdge;
import nl.utwente.groove.grammar.rule.RuleLabel;
import nl.utwente.groove.grammar.rule.RuleNode;

/** List of search items with backwards dependencies. */
public class SearchPlan extends ArrayList<AbstractSearchItem> {
    /**
     * Constructs a search plan with given injectivity.
     * @param seed subgraph whose image is pre-matched before invoking the search plan
     * @param injective flag indicating that the match should be injective
     */
    public SearchPlan(Condition condition, Anchor seed, boolean injective) {
        this.condition = condition;
        this.injective = injective;
        // for simple patterns, node injectivity implies edge injectivity;
        // only non-simple patterns (with parallel edges) need the edge check
        var pattern = condition.getPattern();
        this.edgeInjective = injective && pattern != null && !pattern.isSimple();
        this.eraserConflicts = computeEraserConflicts();
        this.seed = seed;
    }

    /**
     * Computes, for the relevant edges of the condition pattern, the set of other
     * pattern edges with which they may not share an image: eraser edges must
     * be matched injectively with respect to all other edges, so that pushout
     * complements are unique (the DPO identification condition). Under
     * injective matching this holds automatically, so the map is only filled
     * for non-injective matching of a rule condition with eraser edges.
     * The map is symmetric: it contains entries for the eraser edges themselves
     * as well as for their potential conflict partners, so the check can be
     * performed by whichever edge is bound later in the search.
     */
    private Map<RuleEdge,Set<RuleEdge>> computeEraserConflicts() {
        Map<RuleEdge,Set<RuleEdge>> result = Collections.emptyMap();
        var rule = this.condition.getRule();
        var pattern = this.condition.getPattern();
        if (!this.injective && rule != null && pattern != null) {
            for (RuleEdge eraser : rule.getEraserEdges()) {
                for (RuleEdge other : pattern.edgeSet()) {
                    if (other != eraser && canShareImage(eraser, other)) {
                        if (result.isEmpty()) {
                            result = new LinkedHashMap<>();
                        }
                        result.computeIfAbsent(eraser, e -> new LinkedHashSet<>()).add(other);
                        result.computeIfAbsent(other, e -> new LinkedHashSet<>()).add(eraser);
                    }
                }
            }
        }
        return result;
    }

    /**
     * Conservatively determines if two distinct rule edges may be matched to
     * the same host edge. Only edges that are bound to a single host edge
     * image are considered; regular expression and (in)equality edges have no
     * edge image.
     */
    private static boolean canShareImage(RuleEdge one, RuleEdge two) {
        RuleLabel oneLabel = one.label();
        RuleLabel twoLabel = two.label();
        if (!hasEdgeImage(oneLabel) || !hasEdgeImage(twoLabel)) {
            return false;
        }
        if (oneLabel.getRole() != twoLabel.getRole()) {
            return false;
        }
        if (oneLabel.isWildcard() || twoLabel.isWildcard()) {
            return true;
        }
        var oneType = one.getType();
        var twoType = two.getType();
        if (oneType != null && twoType != null) {
            // images of differently-labelled edges are always distinct
            return oneType.label().equals(twoType.label());
        }
        return oneLabel.equals(twoLabel);
    }

    /** Indicates if edges with a given label are matched to a single host edge image. */
    private static boolean hasEdgeImage(RuleLabel label) {
        return label.isSharp() || label.isAtom() || label.isWildcard();
    }

    /** Returns the condition for which this is the search plan. */
    public final Condition getCondition() {
        return this.condition;
    }

    /** Returns the (non-{@code null}) seed for this search plan. */
    public final Anchor getSeed() {
        return this.seed;
    }

    /** Constructs dependency information, in addition to appending the search item. */
    @Override
    public boolean add(AbstractSearchItem e) {
        int position = size();
        boolean result = super.add(e);
        // collection of direct dependencies of the new search item
        int depend = -1;
        Set<RuleNode> usedNodes = new HashSet<>(e.needsNodes());
        usedNodes.addAll(e.bindsNodes());
        Set<LabelVar> usedVars = new HashSet<>(e.needsVars());
        usedVars.addAll(e.bindsVars());
        for (int i = 0; i < position; i++) {
            // set a dependency if the item at position i binds a required node or variable
            // NOTE: the use of the non-short-circuit logic operator '|' is
            // intentional!
            if (usedNodes.removeAll(get(i).bindsNodes()) | usedVars.removeAll(get(i).bindsVars())) {
                depend = i;
            }
        }
        // add dependencies due to injective matching
        if (this.injective) {
            // cumulative set of nodes bound by search items up to i
            Set<RuleNode> boundNodes = new HashSet<>();
            // for each item, whether it binds new nodes
            BitSet bindsNewNodes = new BitSet();
            for (int i = 0; i <= position; i++) {
                bindsNewNodes.set(i, boundNodes.addAll(get(i).bindsNodes()));
            }
            if (bindsNewNodes.get(position) || e.isTestsNodes()) {
                // the new item depends on all other items that bind new nodes
                for (int i = 0; i < position; i++) {
                    if (bindsNewNodes.get(i)) {
                        depend = i;
                    }
                }
            }
        }
        // add dependencies due to edge-injective matching:
        // a failure to bind an edge can be resolved by backtracking to any
        // earlier item that binds an edge (which may free up the edge image)
        if (this.edgeInjective && !e.bindsEdges().isEmpty()) {
            for (int i = 0; i < position; i++) {
                if (!get(i).bindsEdges().isEmpty()) {
                    depend = Math.max(depend, i);
                }
            }
        }
        // add dependencies due to eraser-edge injectivity:
        // a binding refused because of a conflicting edge image can be
        // resolved by backtracking to the item that bound the conflicting edge
        if (!this.eraserConflicts.isEmpty()) {
            Set<RuleEdge> conflicts = new HashSet<>();
            for (RuleEdge edge : e.bindsEdges()) {
                var edgeConflicts = this.eraserConflicts.get(edge);
                if (edgeConflicts != null) {
                    conflicts.addAll(edgeConflicts);
                }
            }
            if (!conflicts.isEmpty()) {
                for (int i = 0; i < position; i++) {
                    if (!Collections.disjoint(get(i).bindsEdges(), conflicts)) {
                        depend = Math.max(depend, i);
                    }
                }
            }
        }
        e.bindsNodes()
            .stream()
            .forEach(n -> this.nodeBinding.put(n, e));
        assert areDisjoint(usedNodes, e.needsNodes()) : String
            .format("Required node(s) %s not all bound in search plan %s", e.needsNodes(), this);
        assert areDisjoint(usedVars, e.needsVars()) : String.format(
            "Required label variable(s) %s not all bound in search plan %s", e.needsVars(), this);
        this.dependencies.add(depend);
        // transitively close the indirect dependencies
        return result;
    }

    /** Tests if two sets are disjoint. */
    private <X> boolean areDisjoint(Collection<X> set1, Collection<X> set2) {
        Set<X> copy = new HashSet<>(set1);
        return !copy.removeAll(set2);
    }

    @Override
    public AbstractSearchItem set(int index, AbstractSearchItem element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(int index, AbstractSearchItem element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AbstractSearchItem remove(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int index, Collection<? extends AbstractSearchItem> c) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the index of the last predecessor on the result of which this one
     * depends for its matching, or {@code -1} if there is no such dependency.
     */
    public int getDependency(int i) {
        return this.dependencies.get(i);
    }

    /** Indicates if the search is injective. */
    public boolean isInjective() {
        return this.injective;
    }

    /**
     * Returns the (symmetric) map from pattern edges to the sets of other
     * pattern edges with which they may not share an image, due to the
     * injective matching of eraser edges.
     * The map is empty if the search is injective or the condition has no
     * eraser edges.
     */
    public Map<RuleEdge,Set<RuleEdge>> getEraserConflicts() {
        return this.eraserConflicts;
    }

    /** The condition for which this is the search plan. */
    private final Condition condition;
    /** The subgraph whose image is pre-matched before invoking the search plan. */
    private final Anchor seed;
    /** Direct dependencies of all search plan items. */
    private final List<Integer> dependencies = new ArrayList<>();
    /** Flag indicating that the search should be injective on non-attribute nodes. */
    private final boolean injective;
    /** Flag indicating that the search should also be injective on edges;
     * set for injective matching of a non-simple pattern. */
    private final boolean edgeInjective;
    /** Symmetric map from pattern edges to the sets of other pattern edges
     * with which they may not share an image, due to the injective matching
     * of eraser edges. */
    private final Map<RuleEdge,Set<RuleEdge>> eraserConflicts;

    /** Returns the last search item binding a given rule node. */
    public SearchItem getBinder(RuleNode node) {
        return this.nodeBinding.get(node);
    }

    /** Map from nodes to the (last) search item binding the nodes. */
    private final Map<RuleNode,SearchItem> nodeBinding = new HashMap<>();
}
