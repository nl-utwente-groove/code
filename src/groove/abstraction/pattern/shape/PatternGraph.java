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
package groove.abstraction.pattern.shape;

import groove.abstraction.MyHashMap;
import groove.graph.Edge;
import groove.graph.GraphRole;
import groove.graph.Node;
import groove.trans.DefaultHostGraph;
import groove.trans.HostEdge;
import groove.trans.HostGraph;
import groove.trans.HostNode;

import java.util.Map;

/**
 * Pattern graph.
 * 
 * @author Eduardo Zambon
 */
public class PatternGraph extends AbstractPatternGraph<PatternNode,PatternEdge> {

    // ------------------------------------------------------------------------
    // Object Fields
    // ------------------------------------------------------------------------

    /** Associated type graph. */
    private final TypeGraph type;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** Default constructor. */
    public PatternGraph(String name, TypeGraph type) {
        super(name);
        this.type = type;
    }

    /** Copying constructor. */
    protected PatternGraph(PatternGraph pGraph) {
        this(pGraph.getName(), pGraph.getTypeGraph());
        this.depth = pGraph.depth;
        for (PatternNode pNode : pGraph.nodeSet()) {
            addNode(pNode);
        }
        for (PatternEdge pEdge : pGraph.edgeSet()) {
            addEdge(pEdge);
        }
    }

    // ------------------------------------------------------------------------
    // Overridden methods
    // ------------------------------------------------------------------------

    @Override
    public GraphRole getRole() {
        return GraphRole.HOST;
    }

    @Override
    protected boolean isTypeCorrect(Node node) {
        return node instanceof PatternNode;
    }

    @Override
    protected boolean isTypeCorrect(Edge edge) {
        return edge instanceof PatternEdge;
    }

    @Override
    public PatternFactory getFactory() {
        return this.type.getPatternFactory();
    }

    @Override
    public PatternGraph clone() {
        return new PatternGraph(this);
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    /** Basic getter method. */
    public TypeGraph getTypeGraph() {
        return this.type;
    }

    /** Returns the simple graph obtained with flattening this pattern graph. */
    public HostGraph flatten() {
        assert isCommuting();
        HostGraph result = new DefaultHostGraph(getName());
        Map<PatternNode,HostNode> nodeMap =
            new MyHashMap<PatternNode,HostNode>();
        // Create the nodes in layer 0.
        for (PatternNode pNode : getLayerNodes(0)) {
            HostNode sNode = result.addNode();
            nodeMap.put(pNode, sNode);
            // Copy node labels.
            for (HostEdge sEdge : pNode.getPattern().edgeSet()) {
                result.addEdge(sNode, sEdge.label(), sNode);
            }
        }
        // Create the edges in layer 1.
        for (PatternNode pNode : getLayerNodes(1)) {
            HostEdge sEdge = pNode.getSimpleEdge();
            HostNode sSrc =
                nodeMap.get(getCoveringEdge(pNode, sEdge.source()).source());
            HostNode sTgt =
                nodeMap.get(getCoveringEdge(pNode, sEdge.target()).source());
            result.addEdge(sSrc, sEdge.label(), sTgt);
        }
        return result;
    }

    /** Returns a fresh pattern node. The node is not added to the shape. */
    public PatternNode createNode(TypeNode type) {
        return getFactory().createNode(type, nodeSet());
    }

    /** Returns a fresh pattern edge. The edge is not added to the shape. */
    public PatternEdge createEdge(PatternNode source, TypeEdge type,
            PatternNode target) {
        return getFactory().createEdge(source, type, target);
    }

}
