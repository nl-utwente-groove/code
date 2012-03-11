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

import groove.abstraction.neigh.Multiplicity;
import groove.abstraction.neigh.Multiplicity.EdgeMultDir;
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
    Map<HostNode,NodeInfo> computeNodeToInfoMap() {
        Map<HostNode,NodeInfo> result = createNodeToInfoMap();
        Map<HostNode,EquivClass<HostNode>> nodeToCellMap = getNodeToCellMap();
        // iterate over the edge signatures of the shape to calculate the multiplicities
        Shape shape = (Shape) this.graph;
        for (Map.Entry<EdgeSignature,Multiplicity> esEntry : shape.getEdgeMultMap().entrySet()) {
            EdgeSignature es = esEntry.getKey();
            NodeInfo info = result.get(es.getNode());
            EquivClass<HostNode> targetEc =
                nodeToCellMap.get(es.getEquivClass().iterator().next());
            info.add(es.getDirection(), es.getLabel(), targetEc,
                esEntry.getValue());
        }
        return result;
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    /**
     * Returns the new multiplicity for the edge signature formed by the
     * given parameters.
     */
    public Multiplicity getMultSum(EdgeMultDir direction, ShapeNode node,
            TypeLabel label, EquivClass<? extends HostNode> ec) {
        Map<EquivClass<HostNode>,Multiplicity> ecMap =
            getNodeToInfoMap().get(node).get(direction).get(label);
        Multiplicity result = ecMap == null ? null : ecMap.get(ec);
        return result == null ? Multiplicity.ZERO_EDGE_MULT : result;
    }
}
