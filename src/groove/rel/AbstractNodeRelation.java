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
 * $Id: AbstractNodeRelation.java,v 1.3 2008-01-30 09:32:28 iovka Exp $
 */
package groove.rel;

import groove.graph.DefaultLabel;
import groove.graph.Edge;
import groove.graph.GraphShape;
import groove.graph.Label;
import groove.graph.Node;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * 
 * @author Arend Rensink
 * @version $Revision$
 */
public abstract class AbstractNodeRelation implements NodeRelation {
    /**
     * Creates a node relation based on a given graph. This means the graph
     * edges will be the basic units from which the relation is built.
     * @param graph the label-to-pairs mapping of this relation
     */
    protected AbstractNodeRelation(GraphShape graph) {
        this.graph = graph;
    }

    public Set<? extends Edge> getAllRelated() {
        return Collections.unmodifiableSet(getRelatedSet());
    }

    public boolean addRelated(Node pre, Node post) {
        return addRelated(createRelated(pre, post));
    }

    /**
     * Creates a new instance using {@link #newInstance()} and fills it using
     * {@link #addRelated(Collection)} from the label-to-pair sets map.
     */
    public NodeRelation newInstance(Label label) {
        NodeRelation result = newInstance();
        Collection<? extends Edge> relatedSet = getRelatedSet(label);
        if (relatedSet != null) {
            result.addRelated(relatedSet);
        }
        return result;
    }

    public NodeRelation createIdentityRelation() {
        NodeRelation result = newInstance();
        result.addSelfRelated(getGraph().nodeSet());
        return result;
    }

    /**
     * Creates a fresh relation consisting of all the edges of the underlying
     * graph.
     */
    public NodeRelation createMaximalRelation() {
        NodeRelation result = newInstance();
        result.addRelated(this.graph.edgeSet());
        return result;
    }

    /**
     * This implementation makes a copy of <tt>this</tt> (using {@link #copy()})
     * and invokes <tt>doThen(other)</tt> upon the copy.
     * @see #doThen(NodeRelation)
     */
    public NodeRelation getThen(NodeRelation other) {
        NodeRelation result = copy();
        result.doThen(other);
        return result;
    }

    /**
     * This implementation returns <tt>other.getThen(this)</tt>.
     */
    public NodeRelation getAfter(NodeRelation other) {
        return other.getThen(this);
    }

    /**
     * This implementation makes a copy of <tt>this</tt> (using {@link #copy()})
     * and invokes <tt>doOr(other)</tt> upon the copy.
     * @see #doOr(NodeRelation)
     */
    public NodeRelation getOr(NodeRelation other) {
        NodeRelation result = copy();
        result.doOr(other);
        return result;
    }

    /**
     * This implementation creates a copy of <tt>this</tt> and invokes
     * {@link #doTransitiveClosure()} upon that.
     * @see #doTransitiveClosure()
     */
    public NodeRelation getTransitiveClosure() {
        NodeRelation result = copy();
        result.doTransitiveClosure();
        return result;
    }

    /**
     * This implementation repeatedly takes the union of <tt>this</tt> with the
     * concatenation of <tt>this</tt> and it's reflexive closure, until that no
     * longer changes the relation.
     */
    public boolean doTransitiveClosure() {
        boolean result = false;
        boolean unstable = true;
        while (unstable) {
            unstable = doOrThen();
            result |= unstable;
        }
        return result;
    }

    /**
     * This implementation creates a copy of <tt>this</tt> and invokes
     * <tt>doReflexiveClosure()</tt> upon that.
     * @see #doReflexiveClosure()
     */
    public NodeRelation getReflexiveClosure() {
        NodeRelation result = copy();
        result.doReflexiveClosure();
        return result;
    }

    /**
     * This implementation takes the union (using
     * <tt>doOr(EdgeBasedRelation)</tt> of <tt>this</tt> and the identity
     * relation over <tt>universe()</tt> (obtained by <tt>getIdentity(Set)</tt>.
     */
    public boolean doReflexiveClosure() {
        return doOr(createIdentityRelation());
    }

    public boolean addRelated(Collection<? extends Edge> edgeSet) {
        boolean result = false;
        for (Edge edge : edgeSet) {
            result |= addRelated(edge);
        }
        return result;
    }

    public boolean addSelfRelated(Collection<? extends Node> nodeSet) {
        boolean result = false;
        for (Node node : nodeSet) {
            result |= addSelfRelated(node);
        }
        return result;
    }

    /**
     * Factory method: creates a {@link RelationEdge} object consisting of the
     * given pre- and post-image.
     * @param pre the pre-image of the new related obiect
     * @param post the post-image of the new related object
     * @return a new related object base on <tt>pre</tt> and <tt>post</tt>
     */
    protected RelationEdge<Node> createRelated(Node pre, Node post) {
        return new RelationEdge<Node>(pre, RelationType.VALUATION, post);
    }

    /**
     * Callback method returning the set of relateds having a certain string as
     * label. May return <code>null</code>. This implementation converts the
     * string to a {@link DefaultLabel} and then queries the graph.
     */
    protected Collection<? extends Edge> getRelatedSet(Label label) {
        return this.graph.labelEdgeSet(2, label);
    }

    /**
     * Returns the stored label-to-pair sets map. The map is set at construction
     * time.
     */
    public GraphShape getGraph() {
        return this.graph;
    }

    /**
     * Computes the disjunction of this relation and its composition with
     * another. Convenience method for <code>doOr(copy().doThen(other))</code>
     * provided for efficieny.
     */
    abstract protected boolean doOrThen();

    /**
     * Returns a shared view upon the set of related object in this relation.
     */
    abstract protected Set<Edge> getRelatedSet();

    /**
     * The underlying graph for this relation.
     */
    private final GraphShape graph;
}