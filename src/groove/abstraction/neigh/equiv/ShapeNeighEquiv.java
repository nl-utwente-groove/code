/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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
package groove.abstraction.neigh.equiv;

import static groove.abstraction.neigh.Multiplicity.EdgeMultDir.INCOMING;
import static groove.abstraction.neigh.Multiplicity.EdgeMultDir.OUTGOING;
import groove.abstraction.neigh.Multiplicity;
import groove.abstraction.neigh.Multiplicity.EdgeMultDir;
import groove.abstraction.neigh.Multiplicity.MultKind;
import groove.abstraction.neigh.MyHashMap;
import groove.abstraction.neigh.shape.EdgeSignature;
import groove.abstraction.neigh.shape.Shape;
import groove.abstraction.neigh.shape.ShapeNode;
import groove.graph.TypeLabel;
import groove.trans.HostGraph;
import groove.trans.HostNode;

import java.util.Map;

/**
 * This class implements the neighbourhood equivalence relation on shapes.
 * See Def. 19 on pg. 15 of the technical report "Graph Abstraction and
 * Abstract Graph Transformation."
 * 
 * @author Eduardo Zambon
 */
public final class ShapeNeighEquiv extends GraphNeighEquiv {

    // ------------------------------------------------------------------------
    // Object Fields
    // ------------------------------------------------------------------------

    /**
     * Map from edge signatures to multiplicities, used to improve performance
     * while computing the neighbourhood equivalence.
     */
    private Map<EdgeSignature,Multiplicity> multMap;

    /** Constant for the zero edge multiplicity. */
    private static final Multiplicity ZERO_EDGE_MULT =
        Multiplicity.getMultiplicity(0, 0, MultKind.EDGE_MULT);

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** Default constructor, delegates to super. */
    public ShapeNeighEquiv(HostGraph graph, int radius) {
        super(graph, radius);
        assert graph instanceof Shape : "Invalid argument type!";
    }

    // ------------------------------------------------------------------------
    // Overridden methods
    // ------------------------------------------------------------------------

    @Override
    void prepareRefinement() {
        Shape shape = (Shape) this.graph;
        this.multMap = new MyHashMap<EdgeSignature,Multiplicity>();
        // Reverse map from subsets of the elements that are in this
        // equivalence relation to their super set.
        Map<EquivClass<ShapeNode>,EquivClass<HostNode>> reverseKMap =
            new MyHashMap<EquivClass<ShapeNode>,EquivClass<HostNode>>();
        kLoop: for (EquivClass<ShapeNode> k : shape.getEquivRelation()) {
            for (EquivClass<HostNode> ec : this) {
                if (ec.containsAll(k)) {
                    reverseKMap.put(k, ec);
                    continue kLoop;
                }
            }
        }

        for (EdgeMultDir direction : EdgeMultDir.values()) {
            for (EdgeSignature esS : shape.getEdgeMultMapKeys(direction)) {
                EdgeSignature es =
                    new EdgeSignature(direction, esS.getNode(), esS.getLabel(),
                        reverseKMap.get(esS.getEquivClass()));
                Multiplicity accMult = this.multMap.get(es);
                if (accMult == null) {
                    accMult = ZERO_EDGE_MULT;
                }
                Multiplicity mult = shape.getEdgeSigMult(esS);
                accMult = accMult.add(mult);
                this.multMap.put(es, accMult);
            }
        }
    }

    /**
     * Returns true if the two given nodes are still equivalent in the next
     * iteration. This method implements the second item of Def. 19 (see
     * comment on the class definition, top of this file).
     */
    @Override
    boolean areStillEquivalent(HostNode n0, HostNode n1) {
        boolean equiv = true;
        // For all labels.
        labelLoop: for (TypeLabel label : this.binaryLabels) {
            // For all equivalence classes.
            for (EquivClass<HostNode> ec : this) {
                Multiplicity n0OutMultSum =
                    this.getMultSum(OUTGOING, (ShapeNode) n0, label, ec);
                Multiplicity n1OutMultSum =
                    this.getMultSum(OUTGOING, (ShapeNode) n1, label, ec);
                // Compare the sums.
                if (!n0OutMultSum.equals(n1OutMultSum)) {
                    equiv = false;
                    break labelLoop;
                }
                Multiplicity n0InMultSum =
                    this.getMultSum(INCOMING, (ShapeNode) n0, label, ec);
                Multiplicity n1InMultSum =
                    this.getMultSum(INCOMING, (ShapeNode) n1, label, ec);
                // Compare the sums.
                if (!n0InMultSum.equals(n1InMultSum)) {
                    equiv = false;
                    break labelLoop;
                }
            }
        }
        return equiv;
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    /**
     * Returns the multiplicity sum for the edge signature formed by the
     * given parameters.
     */
    public Multiplicity getMultSum(EdgeMultDir direction, ShapeNode node,
            TypeLabel label, EquivClass<? extends HostNode> ec) {
        EdgeSignature es = new EdgeSignature(direction, node, label, ec);
        Multiplicity result = this.multMap.get(es);
        return result == null ? ZERO_EDGE_MULT : result;
    }
}
