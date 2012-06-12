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
package groove.abstraction.pattern.gui.jgraph;

import groove.abstraction.MyHashMap;
import groove.abstraction.pattern.shape.AbstractPatternNode;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Node;
import groove.gui.jgraph.GraphJCell;
import groove.gui.jgraph.GraphJEdge;
import groove.gui.jgraph.GraphJModel;
import groove.gui.jgraph.GraphJVertex;
import groove.trans.HostEdge;
import groove.trans.HostGraph;
import groove.trans.HostNode;

import java.util.Map;

import org.jgraph.graph.ParentMap;

/**
 * A JGraph model for pattern shapes.
 * 
 * @author Eduardo Zambon
 */
public class PatternJModel extends GraphJModel<Node,Edge> {

    /**
     * Map that stores the containment relation between equivalence classes
     * and shape nodes.
     */
    private ParentMap parentMap;
    /** Prototype for creating new pattern nodes. */
    private final PatternJVertex pJVertexProt;
    /** Prototype for creating new pattern edges. */
    private final PatternJEdge pJEdgeProt;
    /** Map from pattern nodes PatternJVertex. */
    private final Map<Node,PatternJVertex> nodeMap;

    /** Creates a new jModel with the given prototypes. */
    PatternJModel(GraphJVertex sJVertexProt, GraphJEdge sJEdgeProt,
            PatternJVertex pJVertexProt, PatternJEdge pJEdgeProt) {
        super(sJVertexProt, sJEdgeProt);
        this.pJVertexProt = pJVertexProt;
        this.pJEdgeProt = pJEdgeProt;
        this.nodeMap = new MyHashMap<Node,PatternJVertex>();
    }

    @Override
    public void synchroniseLayout(GraphJCell jCell) {
        // Do nothing because we don't have a graph info.
    }

    @Override
    public void loadGraph(Graph<Node,Edge> graph) {
        // Prepare the object fields.
        setVetoFireGraphChanged(true);
        this.parentMap = new ParentMap();
        this.nodeMap.clear();

        // Ensure that the super class fields are also prepared.
        prepareLoad(graph);
        prepareInsert();

        // Now load the shape elements in the proper order.
        createNodes(graph);
        createEdges(graph);

        // Loading is done.
        setVetoFireGraphChanged(false);

        // Call the jGraph method to perform the edit with all changes.
        doInsert(true, true);
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

    private void createNodes(Graph<Node,Edge> graph) {
        for (Node pNode : graph.nodeSet()) {
            PatternJVertex pJVertex = this.pJVertexProt.newJVertex(this, pNode);
            HostGraph pattern = ((AbstractPatternNode) pNode).getPattern();
            for (HostNode sNode : pattern.nodeSet()) {
                GraphJVertex sJVertex = addNode(sNode);
                this.parentMap.addEntry(sJVertex, pJVertex);
            }
            for (HostEdge sEdge : pattern.edgeSet()) {
                GraphJCell sJEdge = addEdge(sEdge, false);
                this.parentMap.addEntry(sJEdge, pJVertex);
            }
            this.addedJCells.add(pJVertex);
            this.nodeMap.put(pNode, pJVertex);
        }
    }

    private void createEdges(Graph<Node,Edge> graph) {
        for (Edge pEdge : graph.edgeSet()) {
            PatternJEdge pJEdge = this.pJEdgeProt.newJEdge(this, pEdge);
            /*pJEdge.setSource(this.nodeMap.get(pEdge.source()).getPort());
            pJEdge.setTarget(this.nodeMap.get(pEdge.target()).getPort());*/
            this.addedJCells.add(pJEdge);
        }
    }
}
