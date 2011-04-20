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
package groove.match;

import groove.rel.LabelVar;
import groove.trans.RuleNode;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** List of search items with backwards dependencies. */
public class SearchPlan extends ArrayList<AbstractSearchItem> {
    /** Constructs dependency information, in addition to appending the search item. */
    @Override
    public boolean add(AbstractSearchItem e) {
        int position = size();
        boolean result = super.add(e);
        // collection of direct dependencies of the new search item
        BitSet directDepend = new BitSet();
        // collection of transitive dependencies of the new search item
        BitSet transDepend = new BitSet();
        Set<RuleNode> usedNodes = new HashSet<RuleNode>(e.needsNodes());
        usedNodes.addAll(e.bindsNodes());
        Set<LabelVar> usedVars = new HashSet<LabelVar>(e.needsVars());
        usedVars.addAll(e.bindsVars());
        for (int i = 0; i < position; i++) {
            // set a dependency if the item at position i binds a required node or variable
            if (usedNodes.removeAll(get(i).bindsNodes())
                | usedVars.removeAll(get(i).bindsVars())) {
                directDepend.set(i);
                transDepend.set(i);
                transDepend.or(this.transDependencies.get(i));
            }
        }
        assert !usedNodes.removeAll(e.needsNodes()) : String.format(
            "Required node(s) %s not all bound in search plan %s",
            e.needsNodes(), this);
        assert !usedVars.removeAll(e.needsVars()) : String.format(
            "Required label variable(s) %s not all bound in search plan %s",
            e.needsVars(), this);
        int[] dependArray = new int[directDepend.cardinality()];
        int next = -1;
        for (int i = 0; i < dependArray.length; i++) {
            next = directDepend.nextSetBit(next + 1);
            if (!transDepend.get(i)) {
                dependArray[i] = next;
            }
        }
        this.dependencies.add(dependArray);
        this.transDependencies.add(transDepend);
        return result;
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
     * Returns an array of (smaller) indices in the search plan 
     * on which the search item at a given index directly (i.e.,
     * non-transitively) depends.
     */
    public int[] getDependency(int i) {
        return this.dependencies.get(i);
    }

    /** Transitive dependencies of all search plan items. */
    private final List<BitSet> transDependencies = new ArrayList<BitSet>();
    /** Direct dependencies of all search plan items. */
    private final List<int[]> dependencies = new ArrayList<int[]>();
}
