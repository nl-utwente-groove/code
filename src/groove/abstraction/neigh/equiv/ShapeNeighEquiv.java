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
import groove.abstraction.neigh.Util;
import groove.abstraction.neigh.shape.EdgeSignature;
import groove.abstraction.neigh.shape.Shape;
import groove.abstraction.neigh.shape.ShapeNode;
import groove.graph.TypeLabel;
import groove.trans.HostGraph;
import groove.trans.HostNode;

/**
 * This class implements the neighbourhood equivalence relation on shapes.
 * See Def. 19 on pg. 15 of the technical report "Graph Abstraction and
 * Abstract Graph Transformation."
 * 
 * @author Eduardo Zambon
 */
public final class ShapeNeighEquiv extends GraphNeighEquiv {

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** Default constructor. */
    public ShapeNeighEquiv(HostGraph graph, int radius) {
        super(graph, radius);
        assert graph instanceof Shape : "Invalid argument type!";
    }

    // ------------------------------------------------------------------------
    // Static Methods
    // ------------------------------------------------------------------------

    /**
     * Returns the bounded sum of the multiplicities of the edge
     * signatures defined by the given node, label, and set of equivalence
     * classes. See item 6 of Def. 22 on page 17 of the Technical Report for
     * more details.
     */
    public static Multiplicity getEdgeSetMult(Shape shape, ShapeNode node,
            TypeLabel label, EquivRelation<ShapeNode> kSet,
            EdgeMultDir direction) {
        int i = 0;
        int j = 0;
        for (EquivClass<ShapeNode> k : kSet) {
            EdgeSignature es =
                shape.getEdgeSignature(direction, node, label, k);
            Multiplicity mult = shape.getEdgeSigMult(es);
            i = Multiplicity.add(i, mult.getLowerBound());
            j = Multiplicity.add(j, mult.getUpperBound());
        }
        return Multiplicity.approx(i, j, MultKind.EDGE_MULT);
    }

    // ------------------------------------------------------------------------
    // Overridden methods
    // ------------------------------------------------------------------------

    /**
     * Returns true if the two given nodes are still equivalent in the next
     * iteration. This method implements the second item of Def. 19 (see
     * comment on the class definition, top of this file).
     */
    @Override
    boolean areStillEquivalent(HostNode n0, HostNode n1) {
        Shape shape = (Shape) this.graph;
        EquivRelation<ShapeNode> kSet = new EquivRelation<ShapeNode>();
        boolean equiv = true;
        // For all labels.
        labelLoop: for (TypeLabel label : Util.getBinaryLabels(this.graph)) {
            // For all equivalence classes.
            for (EquivClass<HostNode> ec : this) {
                // Compute the set of equivalence classes from the shape that
                // we need to consider.
                for (EquivClass<ShapeNode> possibleK : shape.getEquivRelation()) {
                    if (ec.containsAll(possibleK)) {
                        kSet.add(possibleK);
                    }
                }

                // Calculate the sums.
                Multiplicity n0OutMultSum =
                    getEdgeSetMult(shape, (ShapeNode) n0, label, kSet, OUTGOING);
                Multiplicity n1OutMultSum =
                    getEdgeSetMult(shape, (ShapeNode) n1, label, kSet, OUTGOING);
                Multiplicity n0InMultSum =
                    getEdgeSetMult(shape, (ShapeNode) n0, label, kSet, INCOMING);
                Multiplicity n1InMultSum =
                    getEdgeSetMult(shape, (ShapeNode) n1, label, kSet, INCOMING);

                // Compare the sums.
                equiv =
                    equiv && n0OutMultSum.equals(n1OutMultSum)
                        && n0InMultSum.equals(n1InMultSum);
                if (!equiv) {
                    break labelLoop;
                }
                kSet.clear();
            }
        }
        return equiv;
    }

}
