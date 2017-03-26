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

import groove.grammar.rule.RuleEdge;
import groove.grammar.rule.RuleNode;
import groove.graph.NodeComparator;
import groove.match.plan.PlanSearchStrategy.Search;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

/**
 * A search item that checks (in)equality of two node images.
 * @author Arend Rensink
 * @version $Revision $
 */
public class EqualitySearchItem extends AbstractSearchItem {
    /**
     * Constructs an equality test item, which checks for the (in)equality of the
     * match found so far. That is, the item will match if and only if two given
     * nodes are matched to the same or to distinct nodes.
     * @param edge the edge of which the end nodes should be matched injectively
     * @param equals flag that indicates if the node images should be equal or distinct
     */
    public EqualitySearchItem(RuleEdge edge, boolean equals) {
        assert edge.label().isEmpty() || edge.label().isNeg()
            && edge.label().getNegOperand().isEmpty();
        if (nodeComparator.compare(edge.source(), edge.target()) < 0) {
            this.node1 = edge.source();
            this.node2 = edge.target();
        } else {
            this.node1 = edge.target();
            this.node2 = edge.source();
        }
        this.equals = equals;
        this.neededNodes = new HashSet<>();
        this.neededNodes.add(this.node1);
        this.neededNodes.add(this.node2);
        this.boundEdges = Collections.singleton(edge);
    }

    @Override
    public EqualityRecord createRecord(groove.match.plan.PlanSearchStrategy.Search matcher) {
        return new EqualityRecord(matcher);
    }

    @Override
    public Collection<? extends RuleEdge> bindsEdges() {
        return this.boundEdges;
    }

    /**
     * Returns the set consisting of the nodes for which this item checks
     * equality.
     */
    @Override
    public Collection<RuleNode> needsNodes() {
        return this.neededNodes;
    }

    @Override
    public String toString() {
        return String.format("Test %s and %s for %s", this.node1, this.node2, this.equals
                ? "equality" : "inequality");
    }

    /**
     * Since the order of equality search items does not influence the match,
     * all of them have the same rating.
     * @return <code>0</code> always
     */
    @Override
    int getRating() {
        return 0;
    }

    @Override
    public int compareTo(SearchItem item) {
        int result = super.compareTo(item);
        if (result != 0) {
            return result;
        }
        EqualitySearchItem other = (EqualitySearchItem) item;
        result = nodeComparator.compare(this.node1, other.node1);
        if (result != 0) {
            return result;
        }
        result = nodeComparator.compare(this.node2, other.node2);
        return result;
    }

    @Override
    int computeHashCode() {
        int result = super.computeHashCode();
        result = 31 * this.node1.hashCode();
        result = 31 * this.node2.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        EqualitySearchItem other = (EqualitySearchItem) obj;
        if (!this.node1.equals(other.node1)) {
            return false;
        }
        if (!this.node2.equals(other.node2)) {
            return false;
        }
        return true;
    }

    @Override
    public void activate(PlanSearchStrategy strategy) {
        this.node1Ix = strategy.getNodeIx(this.node1);
        this.node2Ix = strategy.getNodeIx(this.node2);
    }

    /**
     * First node to be compared.
     */
    final RuleNode node1;
    /**
     * Second node to be compared.
     */
    final RuleNode node2;
    /** 
     * Flag indicating if the images of {@link #node1} and {@link #node2}
     * should be equal; otherwise, they should be distinct.
     */
    final boolean equals;
    /** Collection consisting of the equality edge. */
    private final Collection<RuleEdge> boundEdges;
    /** Collection consisting of <code>node1</code> and <code>node2</code>. */
    private final Collection<RuleNode> neededNodes;
    /** Node index (in the result) of {@link #node1}. */
    int node1Ix;
    /** Node index (in the result) of {@link #node2}. */
    int node2Ix;

    private static NodeComparator nodeComparator = NodeComparator.instance();

    /** The record for this search item. */
    private class EqualityRecord extends SingularRecord {
        /** Constructs a fresh record, for a given matcher. */
        EqualityRecord(Search search) {
            super(search);
            assert search.getNode(EqualitySearchItem.this.node1Ix) != null : String.format(
                "Merge embargo node %s not yet matched", EqualitySearchItem.this.node1);
            assert search.getNode(EqualitySearchItem.this.node2Ix) != null : String.format(
                "Merge embargo node %s not yet matched", EqualitySearchItem.this.node2);
        }

        @Override
        boolean find() {
            return EqualitySearchItem.this.equals == (this.search.getNode(EqualitySearchItem.this.node1Ix) == this.search.getNode(EqualitySearchItem.this.node2Ix));
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
