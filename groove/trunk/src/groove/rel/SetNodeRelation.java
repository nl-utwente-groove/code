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
 * $Id: SetNodeRelation.java,v 1.4 2008-01-30 09:32:27 iovka Exp $
 */
package groove.rel;

import groove.graph.Edge;
import groove.graph.GraphShape;
import groove.graph.Node;

import java.util.HashSet;
import java.util.Set;

/**
 * 
 * @author Arend Rensink
 * @version $Revision$
 */
public class SetNodeRelation extends AbstractNodeRelation {
    /**
     * Constructs a relation over a given universe.
     */
    public SetNodeRelation(GraphShape graph) {
        super(graph);
    }

    public boolean addRelated(Edge edge) {
        return this.relatedSet.add(edge);
    }

    public boolean addSelfRelated(Node node) {
        return this.relatedSet.add(createRelated(node, node));
    }

    public NodeRelation copy() {
        SetNodeRelation result = (SetNodeRelation) newInstance();
        result.relatedSet.addAll(this.relatedSet);
        return result;
    }

    public NodeRelation newInstance() {
        return new SetNodeRelation(getGraph());
    }

    public NodeRelation doThen(NodeRelation other) {
        assert other instanceof SetNodeRelation;
        this.relatedSet = new HashSet<Edge>();
        for (Edge oldRel : this.relatedSet) {
            for (Edge otherRel : ((SetNodeRelation) other).getRelatedSet()) {
                if (otherRel.source().equals(oldRel.target())) {
                    RelationEdge<Node> newRel =
                        createRelated(oldRel.source(), otherRel.target());
                    this.relatedSet.add(newRel);
                }
            }
        }
        return this;
    }

    @Override
    protected boolean doOrThen() {
        boolean result = false;
        Set<Edge> oldRelatedSet = new HashSet<Edge>(this.relatedSet);
        for (Edge oldRel : oldRelatedSet) {
            for (Edge otherRel : oldRelatedSet) {
                if (otherRel.source().equals(oldRel.target())) {
                    RelationEdge<Node> newRel =
                        createRelated(oldRel.source(), otherRel.target());
                    result |= this.relatedSet.add(newRel);
                }
            }
        }
        return result;
    }

    public boolean doOr(NodeRelation other) {
        return this.relatedSet.addAll(other.getAllRelated());
    }

    /**
     * This implementation iterates over the set of related elements and adds
     * their inverse to a new relation.
     */
    public NodeRelation getInverse() {
        SetNodeRelation result = (SetNodeRelation) newInstance();
        for (Edge related : this.relatedSet) {
            result.getRelatedSet().add(
                createRelated(related.target(), related.source()));
        }
        return result;
    }

    public boolean isEmpty() {
        return this.relatedSet.isEmpty();
    }

    /**
     * Delegates the method to the underlying set of related objects.
     */
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof SetNodeRelation)
            && this.relatedSet.equals(((SetNodeRelation) obj).relatedSet);
    }

    /**
     * Delegates the method to the underlying set of related objects.
     */
    @Override
    public int hashCode() {
        return this.relatedSet.hashCode();
    }

    /**
     * Delegates the method to the underlying set of related objects.
     */
    @Override
    public String toString() {
        return this.relatedSet.toString();
    }

    /**
     * Returns a shared view upon the set of related object in this relation.
     */
    @Override
    protected Set<Edge> getRelatedSet() {
        return this.relatedSet;
    }

    /** The set of related nodes, stored as edges. */
    private Set<Edge> relatedSet = new HashSet<Edge>();
}
