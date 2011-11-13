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
package groove.abstraction.neigh.trans;

import groove.abstraction.neigh.Multiplicity;
import groove.abstraction.neigh.Multiplicity.EdgeMultDir;
import groove.abstraction.neigh.MyHashMap;
import groove.abstraction.neigh.MyHashSet;
import groove.abstraction.neigh.shape.EdgeSignature;
import groove.abstraction.neigh.shape.Shape;
import groove.abstraction.neigh.shape.ShapeEdge;
import groove.abstraction.neigh.shape.ShapeNode;
import groove.graph.TypeLabel;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * An edge bundle is a collection of split edge signatures that stem from one
 * single original signature that could no longer be used to represent all its
 * edges. This a key structure during the materialisation process since it
 * stores information that still can't be put in the shape that is being
 * materialised.
 * 
 * @author Eduardo Zambon
 */
public final class EdgeBundle {

    // ------------------------------------------------------------------------
    // Object fields
    // ------------------------------------------------------------------------

    /**
     * Direction of this edge bundle. Taken from the original edge signature.
     */
    final EdgeMultDir direction;
    /**
     * Node of this bundle. All split edge signatures have this node and thus
     * also all edges are incident to this node (accordingly to the direction).
     * Note that this node may differ from the node in the original edge
     * signature. This is caused, for example, by node materialisation and
     * node splitting.
     */
    final ShapeNode node;
    /**
     * Label of this edge bundle. Taken from the original edge signature.
     */
    final TypeLabel label;
    /**
     * Original edge signature that had to be split and thus created this
     * bundle.
     */
    final EdgeSignature origEs;
    /**
     * Multiplicity of the original edge signature. The sum of the split
     * signatures must always be equal to this original multiplicity.
     */
    final Multiplicity origEsMult;
    /**
     * Map from split edge signatures to their associated edges in the shape.
     * Used to speed-up the code since it avoids expensive look-ups in the
     * shape structure.
     */
    final Map<EdgeSignature,Set<ShapeEdge>> splitEsMap;
    /**
     * Set of all edges from all split edge signatures. Corresponds to the
     * union of all values of the split map.
     */
    final Set<ShapeEdge> allEdges;
    /**
     * Set of possible edges (i.e., edges not in the shape) of the
     * materialisation that will be present in the final configuration.
     */
    final Set<ShapeEdge> possibleEdges;
    /**
     * Hash code of the bundle, computed by the constructor.
     */
    private final int hashCode;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /**
     * Default constructor. Fills all fields (since they're all final) and
     * computes the hash code for the object.
     */
    EdgeBundle(EdgeSignature origEs, Multiplicity origEsMult, ShapeNode node) {
        this.direction = origEs.getDirection();
        this.node = node;
        this.label = origEs.getLabel();
        this.origEs = origEs;
        this.origEsMult = origEsMult;
        this.splitEsMap = new MyHashMap<EdgeSignature,Set<ShapeEdge>>();
        this.allEdges = new MyHashSet<ShapeEdge>();
        this.possibleEdges = new MyHashSet<ShapeEdge>();
        this.hashCode = this.computeHashCode();
    }

    /** Copying constructor. Used in cloning. */
    EdgeBundle(EdgeBundle bundle) {
        this.direction = bundle.direction;
        this.node = bundle.node;
        this.label = bundle.label;
        this.origEs = bundle.origEs;
        this.origEsMult = bundle.origEsMult;
        this.splitEsMap = new MyHashMap<EdgeSignature,Set<ShapeEdge>>();
        for (EdgeSignature es : bundle.splitEsMap.keySet()) {
            Set<ShapeEdge> edgeSet = new MyHashSet<ShapeEdge>();
            edgeSet.addAll(bundle.splitEsMap.get(es));
            this.splitEsMap.put(es, edgeSet);
        }
        this.allEdges = new MyHashSet<ShapeEdge>();
        this.allEdges.addAll(bundle.allEdges);
        this.possibleEdges = new MyHashSet<ShapeEdge>();
        this.possibleEdges.addAll(bundle.possibleEdges);
        this.hashCode = bundle.hashCode;
    }

    // ------------------------------------------------------------------------
    // Overriden methods
    // ------------------------------------------------------------------------

    @Override
    public String toString() {
        return this.direction + ":" + this.node + "-" + this.label + "-"
            + this.splitEsMap;
    }

    /** Deep copies all structures so the bundle can be modified. */
    @Override
    public EdgeBundle clone() {
        return new EdgeBundle(this);
    }

    /** Compares only the fields that are used to compute the hash code. */
    @Override
    public boolean equals(Object o) {
        boolean result;
        if (this == o) {
            result = true;
        } else if (!(o instanceof EdgeBundle)) {
            result = false;
        } else {
            EdgeBundle bundle = (EdgeBundle) o;
            result =
                this.direction.equals(bundle.direction)
                    && this.node.equals(bundle.node)
                    && this.label.equals(bundle.label)
                    && this.origEs.equals(bundle);
        }
        // Check for consistency between equals and hashCode.
        assert (!result || this.hashCode() == o.hashCode());
        return result;
    }

    @Override
    public int hashCode() {
        return this.hashCode;
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    /**
     * Uses the information from the original edge signature and the bundle
     * node.
     */
    private int computeHashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.direction.hashCode();
        result = prime * result + this.node.hashCode();
        result = prime * result + this.label.hashCode();
        result = prime * result + this.origEs.hashCode();
        return result;
    }

    /** Same comparisons of equals method but without another bundle. */
    boolean isEqual(ShapeNode node, EdgeSignature origEs) {
        return this.node.equals(node) && this.origEs.equals(origEs);
    }

    /** Returns the set of split edge signatures of this bundle. */
    Set<EdgeSignature> getSplitEsSet() {
        return this.splitEsMap.keySet();
    }

    /** Returns the set of edges of the given split edge signature. */
    Set<ShapeEdge> getSplitEsEdges(EdgeSignature splitEs) {
        Set<ShapeEdge> result = this.splitEsMap.get(splitEs);
        assert result != null;
        return result;
    }

    /** Returns all edges of this bundle. */
    Set<ShapeEdge> getEdges() {
        return this.allEdges;
    }

    /** Returns the total number of edges of this bundle. */
    int getEdgesCount() {
        return this.allEdges.size();
    }

    /**
     * Looks at the keys of the split map for an edge signature compatible
     * with the given edge. If no suitable edge signature is found, returns
     * null.
     */
    EdgeSignature maybeGetEdgeSignature(ShapeEdge edge) {
        EdgeSignature result = null;
        for (EdgeSignature splitEs : this.getSplitEsSet()) {
            if (splitEs.contains(edge)) {
                result = splitEs;
                break;
            }
        }
        return result;
    }

    /**
     * Looks at the keys of the split map for an edge signature compatible
     * with the given edge. If no suitable edge signature is found, returns
     * the edge signature retrieved from the shape.
     * The returned signature is stored in the bundle.
     */
    EdgeSignature getEdgeSignature(Shape shape, ShapeEdge edge,
            EdgeMultDir direction) {
        EdgeSignature result = this.maybeGetEdgeSignature(edge);
        if (result == null) {
            result = shape.getEdgeSignature(edge, direction);
            this.splitEsMap.put(result, new MyHashSet<ShapeEdge>());
        }
        return result;
    }

    /**
     * Adds the given edge to the bundle and updates all proper structures.
     * Fails in an assertion if the given edge is not compatible with the
     * bundle.
     */
    void addEdge(Shape shape, ShapeEdge edge) {
        assert this.node.equals(edge.incident(this.direction));
        assert this.label.equals(edge.label());
        if (this.allEdges.contains(edge)) {
            return;
        }
        EdgeSignature splitEs =
            this.getEdgeSignature(shape, edge, this.direction);
        this.getSplitEsEdges(splitEs).add(edge);
        this.allEdges.add(edge);
    }

    /** Adds the given edge to the set of possible edges of the bundle. */
    void setEdgeAsFixed(ShapeEdge edge) {
        this.possibleEdges.add(edge);
    }

    /**
     * Removes the given edge from the bundle. Fails in an assertion if the
     * edge is not present in the first place.
     */
    void removeEdge(ShapeEdge edge) {
        assert this.allEdges.contains(edge);
        EdgeSignature splitEs = this.maybeGetEdgeSignature(edge);
        assert splitEs != null;
        Set<ShapeEdge> splitEsEdges = this.getSplitEsEdges(splitEs);
        splitEsEdges.remove(edge);
        if (splitEsEdges.isEmpty()) {
            this.splitEsMap.remove(splitEs);
        }
        this.allEdges.remove(edge);
        this.possibleEdges.remove(edge);
    }

    /** Returns true if the bundle has more than one split edge signature. */
    boolean isNonSingular() {
        return this.splitEsMap.size() > 1;
    }

    /** Returns true if the given edge is in the set of possible edges. */
    boolean isFixed(ShapeEdge edge, EdgeMultDir direction, Shape shape) {
        boolean result = false;
        ShapeNode opposite = edge.opposite(direction);
        if (shape.getNodeMult(opposite).isOne()
            && this.possibleEdges.contains(edge)) {
            result = true;
        }
        return result;
    }

    /**
     * Computes and stores the additional edges from the shape in the given
     * materialisation that are part of this bundle.
     */
    void computeAdditionalEdges(Materialisation mat) {
        Shape shape = mat.getShape();
        for (ShapeEdge edge : shape.binaryEdgeSet(this.node, this.direction)) {
            if (this.allEdges.contains(edge)) {
                continue;
            }
            if (edge.label().equals(this.label)) {
                EdgeSignature es = shape.getEdgeSignature(edge, this.direction);
                EdgeSignature otherOrigEs =
                    mat.getShapeMorphism().getEdgeSignature(
                        mat.getOriginalShape(), es);
                if (otherOrigEs.equals(this.origEs)) {
                    this.addEdge(shape, edge);
                }
            }
        }
    }

    /**
     * Updates the bundle structures from the given sets constructed from the
     * solution of a first stage equation system.
     * 
     * @param shape the shape that is being materialised.
     * @param zeroEdges the set of edges with multiplicity zero in the solution.
     * @param positiveEdges the set of edges with positive multiplicity in the
     *                       solution.
     */
    void updateFromSolution(Shape shape, Set<ShapeEdge> zeroEdges,
            Set<ShapeEdge> positiveEdges) {
        for (ShapeEdge zeroEdge : zeroEdges) {
            if (this.allEdges.contains(zeroEdge)) {
                this.removeEdge(zeroEdge);
            }
        }
        for (ShapeEdge positiveEdge : positiveEdges) {
            if (this.allEdges.contains(positiveEdge)
                && !shape.containsEdge(positiveEdge)) {
                this.possibleEdges.add(positiveEdge);
            }
        }
    }

    /**
     * Updates the bundle structures to reflect the changes in the given
     * materialisation object. For example, when nodes are split, the edge
     * signatures change, and therefore have to be updated.
     */
    void update(Materialisation mat) {
        Shape shape = mat.getShape();
        // First remove signatures that are no longer present in the shape.
        // This may happen due to node splits.
        Iterator<EdgeSignature> iter = this.splitEsMap.keySet().iterator();
        while (iter.hasNext()) {
            EdgeSignature splitEs = iter.next();
            if (!shape.hasEdgeSignature(splitEs)) {
                this.allEdges.removeAll(this.getSplitEsEdges(splitEs));
                iter.remove();
            }
        }
        // Now search for the additional signatures.
        this.computeAdditionalEdges(mat);
    }
}
