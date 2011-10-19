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

import static groove.abstraction.neigh.Multiplicity.MultKind.EDGE_MULT;
import groove.abstraction.neigh.Multiplicity;
import groove.abstraction.neigh.Multiplicity.EdgeMultDir;
import groove.abstraction.neigh.MyHashMap;
import groove.abstraction.neigh.MyHashSet;
import groove.abstraction.neigh.shape.EdgeSignature;
import groove.abstraction.neigh.shape.Shape;
import groove.abstraction.neigh.shape.ShapeEdge;
import groove.abstraction.neigh.shape.ShapeNode;
import groove.graph.TypeLabel;

import java.util.Map;
import java.util.Set;

/**
 * A collection of related edge signatures.
 * 
 * @author Eduardo Zambon
 */
public final class EdgeBundle {
    final EdgeMultDir direction;
    final ShapeNode node;
    final TypeLabel label;
    final EdgeSignature origEs;
    final Multiplicity origEsMult;
    final Map<EdgeSignature,Set<ShapeEdge>> splitEsMap;
    final Set<ShapeEdge> allEdges;
    final Set<ShapeEdge> possibleEdges;

    EdgeBundle(EdgeSignature origEs, Multiplicity origEsMult, ShapeNode node) {
        this.direction = origEs.getDirection();
        this.node = node;
        this.label = origEs.getLabel();
        this.origEs = origEs;
        this.origEsMult = origEsMult;
        this.splitEsMap = new MyHashMap<EdgeSignature,Set<ShapeEdge>>();
        this.allEdges = new MyHashSet<ShapeEdge>();
        this.possibleEdges = new MyHashSet<ShapeEdge>();
    }

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
    }

    @Override
    public String toString() {
        return this.direction + ":" + this.node + "-" + this.label + "-"
            + this.splitEsMap;
    }

    @Override
    public EdgeBundle clone() {
        return new EdgeBundle(this);
    }

    boolean isEqual(ShapeNode node, EdgeSignature origEs) {
        return this.node.equals(node) && this.origEs.equals(origEs);
    }

    Set<EdgeSignature> getSplitEsSet() {
        return this.splitEsMap.keySet();
    }

    Set<ShapeEdge> getSplitEsEdges(EdgeSignature splitEs) {
        Set<ShapeEdge> result = this.splitEsMap.get(splitEs);
        assert result != null;
        return result;
    }

    Set<ShapeEdge> getEdges() {
        return this.allEdges;
    }

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

    void addEdge(Shape shape, ShapeEdge edge, EdgeMultDir direction) {
        assert this.direction == direction;
        assert this.node.equals(edge.incident(direction));
        assert this.label.equals(edge.label());
        EdgeSignature splitEs = this.getEdgeSignature(shape, edge, direction);
        this.getSplitEsEdges(splitEs).add(edge);
        this.allEdges.add(edge);
    }

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

    boolean isNonSingular() {
        return this.splitEsMap.size() > 1;
    }

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
                    this.addEdge(shape, edge, this.direction);
                }
            }
        }
    }

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

    boolean complyToOriginalMult() {
        boolean result = true;
        if (!this.origEsMult.isUnbounded()) {
            // Count the number of split signatures.
            int ecCount = this.splitEsMap.size();
            Multiplicity oppMult =
                Multiplicity.approx(ecCount, ecCount, EDGE_MULT);
            if (!oppMult.le(this.origEsMult)) {
                // Too many signatures.
                result = false;
            }
        }
        return result;
    }

}
