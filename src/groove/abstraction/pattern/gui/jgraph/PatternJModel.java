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
import groove.abstraction.pattern.shape.AbstractPatternEdge;
import groove.abstraction.pattern.shape.AbstractPatternGraph;
import groove.abstraction.pattern.shape.AbstractPatternNode;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Node;
import groove.gui.jgraph.GraphJCell;
import groove.gui.jgraph.GraphJEdge;
import groove.gui.jgraph.GraphJModel;
import groove.gui.jgraph.GraphJVertex;
import groove.gui.jgraph.JAttr;
import groove.gui.layout.JVertexLayout;
import groove.trans.HostEdge;
import groove.trans.HostFactory;
import groove.trans.HostGraph;
import groove.trans.HostNode;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.ParentMap;

/**
 * A JGraph model for pattern shapes.
 * 
 * @author Eduardo Zambon
 */
public class PatternJModel extends GraphJModel<Node,Edge> {

    /** Prototype for creating new pattern nodes. */
    private final PatternJVertex pJVertexProt;
    /** Prototype for creating new pattern edges. */
    private final PatternJEdge pJEdgeProt;

    /**
     * Map from graph nodes to JGraph cells.
     */
    private Map<Node,PatternJVertex> pNodeJCellMap =
        new HashMap<Node,PatternJVertex>();
    /**
     * Map from graph edges to JGraph cells.
     */
    private Map<Edge,PatternJEdge> pEdgeJCellMap =
        new HashMap<Edge,PatternJEdge>();

    /**
     * Map that stores the containment relation between equivalence classes
     * and shape nodes.
     */
    private ParentMap parentMap;

    private HostFactory hostFactory;

    /** Creates a new jModel with the given prototypes. */
    PatternJModel(GraphJVertex sJVertexProt, GraphJEdge sJEdgeProt,
            PatternJVertex pJVertexProt, PatternJEdge pJEdgeProt) {
        super(sJVertexProt, sJEdgeProt);
        this.pJVertexProt = pJVertexProt;
        this.pJEdgeProt = pJEdgeProt;
    }

    /**
     * Returns the root nodes of the pattern graph. This is NOT the same as
     * the roots from the JModel.
     */
    public List<GraphJCell> getPatternRoots() {
        List<GraphJCell> result = new ArrayList<GraphJCell>();
        for (Node pNode : getGraph().getLayerNodes(0)) {
            result.add(this.pNodeJCellMap.get(pNode));
        }
        return result;
    }

    @Override
    public AbstractPatternGraph<?,?> getGraph() {
        return (AbstractPatternGraph<?,?>) super.getGraph();
    }

    @Override
    protected void prepareLoad(Graph<Node,Edge> graph) {
        super.prepareLoad(graph);
        this.pNodeJCellMap.clear();
        this.pEdgeJCellMap.clear();
        this.hostFactory = HostFactory.newInstance();
    }

    @Override
    public GraphJCell getJCellForEdge(Edge edge) {
        if (isPatternTyped(edge)) {
            return this.pEdgeJCellMap.get(edge);
        } else {
            return super.getJCellForEdge(edge);
        }
    }

    @Override
    public GraphJVertex getJCellForNode(Node node) {
        if (isPatternTyped(node)) {
            return this.pNodeJCellMap.get(node);
        } else {
            return super.getJCellForNode(node);
        }
    }

    @Override
    protected GraphJVertex addNode(Node node) {
        if (!isPatternTyped(node)) {
            return super.addNode(node);
        }
        PatternJVertex pJVertex = computeJVertex((AbstractPatternNode) node);
        // we add nodes in front of the list to get them in front of the display
        this.addedJCells.add(0, pJVertex);
        this.pNodeJCellMap.put(node, pJVertex);
        return pJVertex;
    }

    @Override
    protected GraphJCell addEdge(Edge edge, boolean mergeBidirectional) {
        if (!isPatternTyped(edge)) {
            return super.addEdge(edge, mergeBidirectional);
        }

        AbstractPatternEdge<?> pEdge = (AbstractPatternEdge<?>) edge;
        // check if edge was processed earlier
        if (this.edgeJCellMap.containsKey(edge)) {
            return this.edgeJCellMap.get(edge);
        }
        Node source = edge.source();
        Node target = edge.target();
        // maybe a JEdge between this source and target is already in the
        // JGraph
        Set<GraphJEdge> outJEdges = this.addedOutJEdges.get(source);
        if (outJEdges == null) {
            this.addedOutJEdges.put(source, outJEdges =
                new HashSet<GraphJEdge>());
        }
        for (GraphJEdge jEdge : outJEdges) {
            if (jEdge.getTargetNode() == target
                && isLayoutCompatible(jEdge, edge) && jEdge.addEdge(edge)) {
                // yes, the edge could be added here; we're done
                this.edgeJCellMap.put(edge, jEdge);
                return jEdge;
            }
        }
        // none of the above: so create a new JEdge
        PatternJEdge jEdge = computeJEdge(pEdge);
        // put the edge at the end to make sure it goes to the back
        this.addedJCells.add(jEdge);
        // store mapping of edge to jedge(s)
        this.edgeJCellMap.put(edge, jEdge);
        outJEdges.add(jEdge);
        // verification
        GraphJVertex sourceNode = getJCellForNode(source);
        assert sourceNode != null : "No vertex for source node of " + edge;
        GraphJVertex targetPort = getJCellForNode(target);
        assert targetPort != null : "No vertex for target node of " + edge;
        this.connections.connect(jEdge, sourceNode.getPort(),
            targetPort.getPort());
        return jEdge;
    }

    private boolean isPatternTyped(Node node) {
        return node instanceof AbstractPatternNode;
    }

    private boolean isPatternTyped(Edge edge) {
        return edge instanceof AbstractPatternEdge<?>;
    }

    private PatternJVertex computeJVertex(AbstractPatternNode pNode) {
        PatternJVertex result = createJVertex(pNode);
        result.refreshAttributes();
        if (GraphConstants.isMoveable(result.getAttributes())) {
            JVertexLayout layout = getLayoutMap().getLayout(pNode);
            if (layout != null) {
                result.getAttributes().applyMap(layout.toJAttr());
            } else {
                Rectangle newBounds =
                    new Rectangle(this.nodeX, this.nodeY,
                        JAttr.DEFAULT_NODE_BOUNDS.width,
                        JAttr.DEFAULT_NODE_BOUNDS.height);
                GraphConstants.setBounds(result.getAttributes(), newBounds);
                this.nodeX = randomCoordinate();
                this.nodeY = randomCoordinate();
            }
        }
        createPattern(pNode, result);
        return result;
    }

    private void createPattern(AbstractPatternNode pNode,
            PatternJVertex pJVertex) {
        HostGraph pattern = pNode.getPattern();
        Map<HostNode,HostNode> nodeMap = new MyHashMap<HostNode,HostNode>();
        for (HostNode sNode : pattern.nodeSet()) {
            HostNode newSNode = this.hostFactory.createNode();
            nodeMap.put(sNode, newSNode);
            GraphJVertex sJVertex = addNode(newSNode);
            this.parentMap.addEntry(sJVertex, pJVertex);
        }
        for (HostEdge sEdge : pattern.edgeSet()) {
            HostNode source = nodeMap.get(sEdge.source());
            HostNode target = nodeMap.get(sEdge.target());
            HostEdge newSEdge =
                this.hostFactory.createEdge(source, sEdge.label(), target);
            GraphJCell sJEdge = addEdge(newSEdge, false);
            this.parentMap.addEntry(sJEdge, pJVertex);
        }
    }

    private PatternJEdge computeJEdge(AbstractPatternEdge<?> pEdge) {
        PatternJEdge result = createJEdge(pEdge);
        result.setBidirectional(false);
        result.refreshAttributes();
        return result;
    }

    /**
     * Factory method for jgraph nodes.
     * @param pNode graph node for which a corresponding j-node is to be created
     */
    protected PatternJVertex createJVertex(AbstractPatternNode pNode) {
        return this.pJVertexProt.newJVertex(this, pNode);
    }

    /**
     * Factory method for jgraph edges.
     * 
     * @param pEdge graph edge for which a corresponding JEdge is to be created;
     * may be {@code null} if there is initially no edge
     */
    protected PatternJEdge createJEdge(AbstractPatternEdge<?> pEdge) {
        return this.pJEdgeProt.newJEdge(this, pEdge);
    }

    @Override
    protected void prepareInsert() {
        super.prepareInsert();
        this.parentMap = new ParentMap();
    }

    @Override
    protected void doInsert(boolean replace, boolean toBack) {
        Object[] addedCells = this.addedJCells.toArray();
        Object[] removedCells = replace ? getRoots().toArray() : null;
        createEdit(addedCells, removedCells, null, this.connections,
            this.parentMap, null).execute();
        if (toBack) {
            toBack(addedCells);
        }
    }

}
