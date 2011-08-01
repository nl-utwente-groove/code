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

import groove.abstraction.neigh.equiv.EquivClass;
import groove.abstraction.neigh.equiv.EquivRelation;
import groove.abstraction.neigh.shape.Shape;
import groove.abstraction.neigh.shape.ShapeEdge;
import groove.abstraction.neigh.shape.ShapeNode;
import groove.graph.Graph;
import groove.gui.jgraph.GraphJEdge;
import groove.gui.jgraph.GraphJModel;
import groove.gui.jgraph.GraphJVertex;

import org.jgraph.graph.ParentMap;

/**
 * A JGraph model for Shapes.
 * 
 * @author Eduardo Zambon
 */
public class ShapeJModel extends GraphJModel<ShapeNode,ShapeEdge> {

    private boolean loading;
    private ParentMap parentMap;
    private EquivClassJCell ecJCellProt;

    /** Creates a new jModel with the given prototypes. */
    protected ShapeJModel(GraphJVertex jVertexProt, GraphJEdge jEdgeProt,
            EquivClassJCell ecJCellProt) {
        super(jVertexProt, jEdgeProt);
        this.ecJCellProt = ecJCellProt;
    }

    @Override
    public Shape getGraph() {
        return (Shape) super.getGraph();
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void loadGraph(Graph graph) {
        assert graph instanceof Shape;
        this.setVetoFireGraphChanged(true);
        this.loading = true;
        this.parentMap = new ParentMap();
        super.loadGraph(graph);
        this.createAdditionalElements();
        this.loading = false;
        this.setVetoFireGraphChanged(false);
        this.doInsert(true, false);
    }

    /**
     * Executes the insertion prepared by node and edge additions.
     * Optionally sends the new elements to the back
     * @param replace if {@code true}, the old roots should be deleted
     */
    @Override
    protected void doInsert(boolean replace, boolean toBack) {
        if (!this.loading) {
            Object[] addedCells = this.addedJCells.toArray();
            Object[] removedCells = replace ? getRoots().toArray() : null;
            createEdit(addedCells, removedCells, null, this.connections,
                this.parentMap, null).execute();
            if (toBack) {
                // new edges should be behind the nodes
                toBack(addedCells);
            }
        }
    }

    private void createAdditionalElements() {
        // this.createEdgeSigPorts();
        // this.createEdgeMults();
        this.createEquivClasses();
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
