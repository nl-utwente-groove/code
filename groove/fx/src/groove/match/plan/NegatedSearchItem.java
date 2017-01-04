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
 * $Id$
 */
package groove.match.plan;

import groove.grammar.host.HostGraph;
import groove.grammar.rule.LabelVar;
import groove.grammar.rule.RuleNode;
import groove.match.plan.PlanSearchStrategy.Search;

import java.util.Collection;
import java.util.HashSet;

/**
 * A search item that negates another search item.
 * @author Arend Rensink
 * @version $Revision $
 */
class NegatedSearchItem extends AbstractSearchItem {
    /**
     * Constructs a new search item. The item will match (precisely once) if and
     * only the underlying item does not match.
     * @param item the underlying, negated item
     */
    public NegatedSearchItem(SearchItem item) {
        this.inner = item;
        this.neededNodes = new HashSet<>(item.needsNodes());
        this.neededNodes.addAll(item.bindsNodes());
        this.neededVars = new HashSet<>(item.needsVars());
        this.neededVars.addAll(item.bindsVars());
    }

    @Override
    public NegatedSearchRecord createRecord(groove.match.plan.PlanSearchStrategy.Search search) {
        return new NegatedSearchRecord(search);
    }

    @Override
    public String toString() {
        return String.format("Negation of %s", this.inner);
    }

    /**
     * Returns the inner condition's needed nodes.
     */
    @Override
    public Collection<RuleNode> needsNodes() {
        return this.neededNodes;
    }

    /**
     * Returns the inner condition's needed variables.
     */
    @Override
    public Collection<LabelVar> needsVars() {
        return this.neededVars;
    }

    /** This implementation returns {@code true} if the inner item does so. */
    @Override
    public boolean isTestsNodes() {
        return this.inner.isTestsNodes();
    }

    /**
     * Since the order of negated search items does not influence the match, all
     * of them have the same rating.
     * @return <code>0</code> always
     */
    @Override
    int getRating() {
        return 0;
    }

    /** This implementation propagates the call to the inner item. */
    @Override
    public void activate(PlanSearchStrategy strategy) {
        this.inner.activate(strategy);
    }

    @Override
    public int compareTo(SearchItem item) {
        int result = super.compareTo(item);
        if (result != 0) {
            return result;
        }
        NegatedSearchItem other = (NegatedSearchItem) item;
        return this.inner.compareTo(other.inner);
    }

    @Override
    int computeHashCode() {
        return super.computeHashCode() + 31 * this.inner.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        return this.inner.equals(((NegatedSearchItem) obj).inner);
    }

    /**
     * The inner search item, for which we test for the negation.
     */
    final SearchItem inner;
    /** Union of the needed and bound nodes of the inner condition. */
    private final Collection<RuleNode> neededNodes;
    /** Union of the needed and bound variables of the inner condition. */
    private final Collection<LabelVar> neededVars;

    /** Record for the negated search item. */
    private class NegatedSearchRecord extends SingularRecord {
        /** Constructs a new record, for a given matcher. */
        NegatedSearchRecord(Search search) {
            super(search);
            this.innerRecord = NegatedSearchItem.this.inner.createRecord(search);
        }

        @Override
        public void initialise(HostGraph host) {
            super.initialise(host);
            this.innerRecord.initialise(host);
        }

        /**
         * Tests if the inner record can be satisfied; if so, it is undone
         * immediately to avoid lasting effects.
         */
        @Override
        boolean find() {
            boolean result = !this.innerRecord.next();
            this.innerRecord.reset();
            return result;
        }

        @Override
        void erase() {
            // There is nothing to be erased
        }

        @Override
        boolean write() {
            // There is nothing to be written
            return true;
        }

        /**
         * The record of the inner (negated) item.
         */
        private final SearchItem.Record innerRecord;
    }
}
