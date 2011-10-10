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
package groove.abstraction.neigh.gui.jgraph;

import gnu.trove.THashMap;
import groove.abstraction.neigh.Multiplicity.EdgeMultDir;
import groove.abstraction.neigh.Util;
import groove.abstraction.neigh.equiv.EquivClass;
import groove.abstraction.neigh.equiv.EquivRelation;
import groove.abstraction.neigh.shape.EdgeSignature;
import groove.abstraction.neigh.shape.Shape;
import groove.abstraction.neigh.shape.ShapeEdge;
import groove.abstraction.neigh.shape.ShapeNode;
import groove.graph.Graph;
import groove.gui.jgraph.GraphJCell;
import groove.gui.jgraph.GraphJModel;
import groove.util.Duo;

import java.awt.geom.Point2D;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.ParentMap;

/**
 * A JGraph model for Shapes.
 * 
 * @author Eduardo Zambon
 */
public class ShapeJModel extends GraphJModel<ShapeNode,ShapeEdge> {

    private static final Point2D[] labelPositions = {
        new Point2D.Double(GraphConstants.PERMILLE * 90 / 100, -10),
        new Point2D.Double(GraphConstants.PERMILLE * 10 / 100, -10)};

    /**
     * Map that stores the containment relation between equivalence classes
     * and shape nodes.
     */
    private ParentMap parentMap;
    /** Prototype for creating new equivalence classes JCells. */
    private final EquivClassJCell ecJCellProt;
    /** Map from edge signatures to outgoing ports. */
    private final THashMap<EdgeSignature,ShapeJPort> outEsMap;
    /** Map from edge signatures to incoming ports. */
    private final THashMap<EdgeSignature,ShapeJPort> inEsMap;

    /** Creates a new jModel with the given prototypes. */
    ShapeJModel(ShapeJVertex jVertexProt, ShapeJEdge jEdgeProt,
            EquivClassJCell ecJCellProt) {
        super(jVertexProt, jEdgeProt);
        this.ecJCellProt = ecJCellProt;
        this.outEsMap = new THashMap<EdgeSignature,ShapeJPort>();
        this.inEsMap = new THashMap<EdgeSignature,ShapeJPort>();
    }

    @Override
    public Shape getGraph() {
        return (Shape) super.getGraph();
    }

    @Override
    public void loadGraph(Graph<ShapeNode,ShapeEdge> graph) {
        // Prepare the object fields.
        this.setVetoFireGraphChanged(true);
        this.parentMap = new ParentMap();
        this.outEsMap.clear();
        this.inEsMap.clear();

        // Ensure that the super class fields are also prepared.
        this.prepareLoad(graph);
        this.prepareInsert();

        // Now load the shape elements in the proper order.
        this.createNodes();
        this.createEdgeSigPorts();
        this.createEdges();
        this.createEdgeMults();
        this.createEquivClasses();

        // Loading is done.
        this.setVetoFireGraphChanged(false);

        // Call the jGraph method to perform the edit with all changes.
        this.doInsert(true, true);
    }

    /**
     * Executes the insertion prepared by node and edge additions.
     * Optionally sends the new elements to the back
     * @param replace if {@code true}, the old roots should be deleted
     */
    @Override
    protected void doInsert(boolean replace, boolean toBack) {
        Object[] addedCells = this.addedJCells.toArray();
        Object[] removedCells = replace ? getRoots().toArray() : null;
        createEdit(addedCells, removedCells, null, this.connections,
            this.parentMap, null).execute();
        if (toBack) {
            // new edges should be behind the nodes
            toBack(addedCells);
        }
    }

    @Override
    protected GraphJCell addEdge(ShapeEdge edge) {
        GraphJCell jCell = super.addEdge(edge);
        if (jCell instanceof ShapeJEdge) {
            ShapeJEdge jEdge = (ShapeJEdge) jCell;
            Shape shape = this.getGraph();
            EdgeSignature outEs =
                shape.getEdgeSignature(edge, EdgeMultDir.OUTGOING);
            EdgeSignature inEs =
                shape.getEdgeSignature(edge, EdgeMultDir.INCOMING);
            ShapeJPort srcPort = this.getPort(outEs, EdgeMultDir.OUTGOING);
            ShapeJPort tgtPort = this.getPort(inEs, EdgeMultDir.INCOMING);
            assert srcPort != null && tgtPort != null;
            this.connections.connect(jEdge, srcPort, tgtPort);
        }
        return jCell;
    }

    /** Returns the proper port map accordingly to the given direction. */
    private THashMap<EdgeSignature,ShapeJPort> getEsMap(EdgeMultDir direction) {
        THashMap<EdgeSignature,ShapeJPort> result = null;
        switch (direction) {
        case OUTGOING:
            result = this.outEsMap;
            break;
        case INCOMING:
            result = this.inEsMap;
            break;
        default:
            assert false;
        }
        return result;
    }

    /** Returns the port mapped to the given edge signature. */
    public ShapeJPort getPort(EdgeSignature es, EdgeMultDir direction) {
        ShapeJPort result = this.getEsMap(direction).get(es);
        assert result != null;
        return result;
    }

    private void createNodes() {
        for (ShapeNode node : this.getGraph().nodeSet()) {
            addNode(node);
        }
    }

    private void createEdgeSigPorts() {
        Shape shape = this.getGraph();
        for (EdgeMultDir direction : EdgeMultDir.values()) {
            for (EdgeSignature es : shape.getEdgeMultMapKeys(direction)) {
                ShapeJVertex vertex =
                    (ShapeJVertex) this.getJCellForNode(es.getNode());
                boolean alwaysMovable = shape.isEdgeSigUnique(es);
                ShapeJPort port = new ShapeJPort(vertex, es, alwaysMovable);
                this.getEsMap(direction).put(es, port);
            }
        }
    }

    private void createEdges() {
        for (ShapeEdge edge : this.getGraph().edgeSet()) {
            addEdge(edge);
        }
    }

    private void createEdgeMults() {
        Shape shape = this.getGraph();
        for (ShapeEdge edgeS : Util.getBinaryEdges(shape)) {
            ShapeJEdge jEdge = (ShapeJEdge) this.getJCellForEdge(edgeS);
            Duo<String> duo = shape.getEdgeMultLabels(edgeS);
            String labels[] = new String[2];
            labels[0] = duo.one();
            if (!"".equals(labels[0])) {
                jEdge.setMainTgt(true);
            }
            labels[1] = duo.two();
            if (!"".equals(labels[1])) {
                jEdge.setMainSrc(true);
            }
            AttributeMap attrMap = jEdge.getAttributes();
            GraphConstants.setExtraLabelPositions(attrMap, labelPositions);
            GraphConstants.setExtraLabels(attrMap, labels);
        }
    }

    private void createEquivClasses() {
        Shape shape = this.getGraph();
        EquivRelation<ShapeNode> er = shape.getEquivRelation();
        for (EquivClass<ShapeNode> ec : er) {
            EquivClassJCell ecJCell = this.ecJCellProt.newJCell(ec);
            for (ShapeNode node : ec) {
                this.parentMap.addEntry(this.getJCellForNode(node), ecJCell);
            }
            this.addedJCells.add(ecJCell);
        }
    }

}
