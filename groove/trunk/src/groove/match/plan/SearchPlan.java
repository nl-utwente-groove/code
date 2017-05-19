/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2010 University of Twente
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
package groove.match.plan;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import groove.grammar.Condition;
import groove.grammar.rule.Anchor;
import groove.grammar.rule.LabelVar;
import groove.grammar.rule.RuleNode;

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
        this.seed = seed;
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

    /** The condition for which this is the search plan. */
    private final Condition condition;
    /** The subgraph whose image is pre-matched before invoking the search plan. */
    private final Anchor seed;
    /** Direct dependencies of all search plan items. */
    private final List<Integer> dependencies = new ArrayList<>();
    /** Flag indicating that the search should be injective on non-attribute nodes. */
    private final boolean injective;

    /** Returns the last search item binding a given rule node. */
    public SearchItem getBinder(RuleNode node) {
        return this.nodeBinding.get(node);
    }

    /** Map from nodes to the (last) search item binding the nodes. */
    private final Map<RuleNode,SearchItem> nodeBinding = new HashMap<>();
}
