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

import groove.abstraction.MyHashMap;
import groove.abstraction.neigh.EdgeMultDir;
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

import java.util.Map;

import org.jgraph.graph.ParentMap;

/**
 * A JGraph model for Shapes.
 * 
 * @author Eduardo Zambon
 */
public class ShapeJModel extends GraphJModel<ShapeNode,ShapeEdge> {

    /**
     * Map that stores the containment relation between equivalence classes
     * and shape nodes.
     */
    private ParentMap parentMap;
    /** Map from edge signatures to outgoing ports. */
    private final Map<EdgeSignature,ShapeJPort> esMap;

    /** Creates a new jModel with the given prototypes. */
    ShapeJModel(ShapeJGraph jGraph) {
        super(jGraph);
        this.esMap = new MyHashMap<EdgeSignature,ShapeJPort>();
    }

    @Override
    public Shape getGraph() {
        return (Shape) super.getGraph();
    }

    @Override
    public void loadGraph(Graph<ShapeNode,ShapeEdge> graph) {
        // Prepare the object fields.
        setVetoFireGraphChanged(true);
        this.parentMap = new ParentMap();
        this.esMap.clear();

        // Ensure that the super class fields are also prepared.
        prepareLoad(graph);
        prepareInsert();

        // Now load the shape elements in the proper order.
        createNodes();
        createEdgeSigPorts();
        createEdges();
        createEdgeMults();
        createEquivClasses();

        // Loading is done.
        setVetoFireGraphChanged(false);

        // Call the jGraph method to perform the edit with all changes.
        doInsert(true);
    }

    @Override
    protected ParentMap getParentMap() {
        return this.parentMap;
    }

    @Override
    protected GraphJCell addEdge(ShapeEdge edge) {
        GraphJCell jCell = super.addEdge(edge);
        if (jCell instanceof ShapeJEdge) {
            ShapeJEdge jEdge = (ShapeJEdge) jCell;
            Shape shape = getGraph();
            EdgeSignature outEs =
                shape.getEdgeSignature(edge, EdgeMultDir.OUTGOING);
            EdgeSignature inEs =
                shape.getEdgeSignature(edge, EdgeMultDir.INCOMING);
            ShapeJPort srcPort = getPort(outEs);
            ShapeJPort tgtPort = getPort(inEs);
            assert srcPort != null && tgtPort != null;
            this.connections.connect(jEdge, srcPort, tgtPort);
        }
        return jCell;
    }

    /** Returns the port mapped to the given edge signature. */
    public ShapeJPort getPort(EdgeSignature es) {
        ShapeJPort result = this.esMap.get(es);
        assert result != null;
        return result;
    }

    private void createNodes() {
        for (ShapeNode node : getGraph().nodeSet()) {
            addNode(node);
        }
    }

    private void createEdgeSigPorts() {
        Shape shape = getGraph();
        for (EdgeSignature es : shape.getEdgeSigSet()) {
            ShapeJVertex vertex = (ShapeJVertex) getJCellForNode(es.getNode());
            boolean alwaysMovable = shape.isEdgeSigUnique(es);
            ShapeJPort port = new ShapeJPort(vertex, es, alwaysMovable);
            this.esMap.put(es, port);
        }
    }

    private void createEdges() {
        for (ShapeEdge edge : getGraph().edgeSet()) {
            addEdge(edge);
        }
    }

    private void createEdgeMults() {
        Shape shape = this.getGraph();
        for (ShapeEdge edgeS : Util.getBinaryEdges(shape)) {
            ShapeJEdge jEdge = (ShapeJEdge) getJCellForEdge(edgeS);
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
        }
    }

    private void createEquivClasses() {
        Shape shape = this.getGraph();
        EquivRelation<ShapeNode> er = shape.getEquivRelation();
        for (EquivClass<ShapeNode> ec : er) {
            EcJVertex ecJCell = EcJVertex.newInstance();
            ecJCell.setJModel(this);
            for (ShapeNode node : ec) {
                this.parentMap.addEntry(getJCellForNode(node), ecJCell);
            }
            this.addedJCells.add(ecJCell);
        }
    }

}
