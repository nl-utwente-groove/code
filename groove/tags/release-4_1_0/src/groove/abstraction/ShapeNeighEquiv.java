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
package groove.abstraction;

import groove.graph.TypeLabel;
import groove.trans.HostGraph;
import groove.trans.HostNode;

import java.util.HashSet;
import java.util.Set;

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
    public ShapeNeighEquiv(HostGraph graph) {
        super(graph);
        assert graph instanceof Shape : "Invalid argument type!";
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
        Set<EquivClass<ShapeNode>> kSet = new HashSet<EquivClass<ShapeNode>>();
        boolean equiv = true;
        // For all labels.
        labelLoop: for (TypeLabel label : Util.binaryLabelSet(this.graph)) {
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
                    Multiplicity.sumOutMult(shape, (ShapeNode) n0, label, kSet);
                Multiplicity n1OutMultSum =
                    Multiplicity.sumOutMult(shape, (ShapeNode) n1, label, kSet);
                Multiplicity n0InMultSum =
                    Multiplicity.sumInMult(shape, (ShapeNode) n0, label, kSet);
                Multiplicity n1InMultSum =
                    Multiplicity.sumInMult(shape, (ShapeNode) n1, label, kSet);

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
