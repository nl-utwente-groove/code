/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
package groove.abstraction.pattern.match;

import groove.abstraction.MyHashSet;
import groove.abstraction.pattern.trans.PatternRule;
import groove.abstraction.pattern.trans.RuleNode;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * List of search items with backwards dependencies.
 * @author Eduardo Zambon
 */
public final class PatternSearchPlan extends ArrayList<SearchItem> {

    /** The rule for which this is the search plan. */
    private final PatternRule pRule;
    /** Direct dependencies of all search plan items. */
    private final List<Integer> dependencies;

    /** Basic constructor. */
    public PatternSearchPlan(PatternRule pRule) {
        this.pRule = pRule;
        this.dependencies = new ArrayList<Integer>();
        computeSearchItems();
    }

    /** Constructs dependency information, in addition to appending the search item. */
    @Override
    public boolean add(SearchItem e) {
        int position = size();
        boolean result = super.add(e);
        // Collection of direct dependencies of the new search item.
        int depend = -1;
        Set<RuleNode> usedNodes = new MyHashSet<RuleNode>();
        usedNodes.addAll(e.needsNodes());
        usedNodes.addAll(e.bindsNodes());
        for (int i = 0; i < position; i++) {
            // Set a dependency if the item at position i binds a required node.
            if (usedNodes.removeAll(get(i).bindsNodes())) {
                depend = i;
            }
        }
        // Add dependencies due to injective matching.
        // Cumulative set of nodes bound by search items up to i.
        Set<RuleNode> boundNodes = new MyHashSet<RuleNode>();
        // For each item, whether it binds new nodes.
        BitSet bindsNewNodes = new BitSet();
        for (int i = 0; i <= position; i++) {
            bindsNewNodes.set(i, boundNodes.addAll(get(i).bindsNodes()));
        }
        if (bindsNewNodes.get(position)) {
            // The new item depends on all other items that bind new nodes.
            for (int i = 0; i < position; i++) {
                if (bindsNewNodes.get(i)) {
                    depend = i;
                }
            }
        }
        // Transitively close the indirect dependencies.
        this.dependencies.add(depend);
        return result;
    }

    /** Basic getter method. */
    public PatternRule getRule() {
        return this.pRule;
    }

    /**
     * Returns the index of the last predecessor on the result of which this one 
     * depends for its matching, or {@code -1} if there is no such dependency.
     */
    public int getDependency(int i) {
        return this.dependencies.get(i);
    }

    private void computeSearchItems() {
        // EDUARDO: Implement this...
    }

    @Override
    public SearchItem set(int index, SearchItem element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(int index, SearchItem element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SearchItem remove(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int index, Collection<? extends SearchItem> c) {
        throw new UnsupportedOperationException();
    }

}
