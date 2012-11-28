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
 * $Id: NegatedSearchItem.java,v 1.10 2008-01-30 09:33:29 iovka Exp $
 */
package groove.abstraction.pattern.match;

import groove.abstraction.MyHashSet;
import groove.abstraction.pattern.match.Matcher.Search;
import groove.abstraction.pattern.shape.PatternGraph;
import groove.abstraction.pattern.trans.RuleEdge;
import groove.abstraction.pattern.trans.RuleNode;

import java.util.Collection;
import java.util.Collections;

/**
 * A search item that negates two edge search items.
 * @author Arend Rensink and Eduardo Zambon
 */
final class NegatedSearchItem extends SearchItem {

    /** The edges composing this item. */
    private final RuleEdge edge1;
    private final RuleEdge edge2;
    /** The inner search items, for which we test for the negation. */
    final SearchItem inner1;
    final SearchItem inner2;
    /** Needed source nodes. */
    private final Collection<RuleNode> neededNodes;
    /** Binded target node. */
    private final Collection<RuleNode> boundNode;
    /** Binded edges. */
    private final Collection<RuleEdge> boundEdges;

    /**
     * Constructs a new search item. The item will match (precisely once) if and
     * only the underlying item does not match.
     */
    public NegatedSearchItem(RuleEdge edge1, RuleEdge edge2) {
        assert edge1.target().equals(edge2.target());

        this.edge1 = edge1;
        this.edge2 = edge2;
        this.inner1 = new PatternEdgeSearchItem(edge1);
        this.inner2 = new PatternEdgeSearchItem(edge2);

        this.neededNodes = new MyHashSet<RuleNode>();
        this.neededNodes.add(this.edge1.source());
        this.neededNodes.add(this.edge2.source());

        this.boundNode = Collections.singleton(this.edge1.target());

        this.boundEdges = new MyHashSet<RuleEdge>();
        this.boundEdges.add(edge1);
        this.boundEdges.add(edge2);
    }

    @Override
    public NegatedSearchRecord createRecord(Search search) {
        return new NegatedSearchRecord(search);
    }

    @Override
    public String toString() {
        return String.format("Negation of [%s && %s]", this.inner1, this.inner2);
    }

    @Override
    public Collection<RuleNode> needsNodes() {
        return this.neededNodes;
    }

    @Override
    public Collection<RuleNode> bindsNodes() {
        return this.boundNode;
    }

    @Override
    public Collection<RuleEdge> bindsEdges() {
        return this.boundEdges;
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
    public void activate(Matcher matcher) {
        this.inner1.activate(matcher);
        this.inner2.activate(matcher);
    }

    /** Record for the negated search item. */
    private class NegatedSearchRecord extends SingularRecord {

        /** The record of the inner (negated) item. */
        private final SearchItem.Record innerRecord1;
        private final SearchItem.Record innerRecord2;

        /** Constructs a new record, for a given matcher. */
        NegatedSearchRecord(Search search) {
            super(search);
            this.innerRecord1 =
                NegatedSearchItem.this.inner1.createRecord(search);
            this.innerRecord2 =
                NegatedSearchItem.this.inner2.createRecord(search);
        }

        @Override
        public void initialise(PatternGraph host) {
            super.initialise(host);
            this.innerRecord1.initialise(host);
            this.innerRecord2.initialise(host);
        }

        /**
         * Tests if the inner record can be satisfied; if so, it is undone
         * immediately to avoid lasting effects.
         */
        @Override
        boolean find() {
            boolean edge1Found = false;
            boolean edge2Found = false;

            edge1Found = this.innerRecord1.next();
            while (edge1Found) {
                edge2Found = this.innerRecord2.next();
                if (edge2Found) {
                    break;
                } else {
                    this.innerRecord2.reset();
                    edge1Found = this.innerRecord1.next();
                }
            }

            this.innerRecord1.reset();
            this.innerRecord2.reset();

            return !(edge1Found && edge2Found);
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

    }

}
