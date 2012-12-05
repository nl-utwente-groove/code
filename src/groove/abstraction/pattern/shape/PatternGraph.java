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
import groove.abstraction.pattern.match.Match;
import groove.graph.Edge;
import groove.graph.Node;
import groove.trans.DefaultHostGraph;
import groove.trans.HostEdge;
import groove.trans.HostGraph;
import groove.trans.HostNode;
import groove.util.Duo;
import groove.util.Pair;
import groove.util.UnmodifiableSetView;

import java.util.Map;
import java.util.Set;

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
    private final PatternFactory factory;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** Private constructor. Used to initialise the fields. */
    private PatternGraph(String name, TypeGraph type, PatternFactory factory) {
        super(name);
        this.type = type;
        this.factory = factory;
    }

    /** Default constructor. Uses the factory of the type graph. */
    PatternGraph(String name, TypeGraph type) {
        super(name);
        this.type = type;
        this.factory = type.getPatternFactory();
    }

    /** Factory constructor. Used when a factory creates a new graph. */
    PatternGraph(TypeGraph type, PatternFactory factory) {
        this("", type, factory);
    }

    /** Copying constructor. */
    protected PatternGraph(PatternGraph pGraph) {
        this(pGraph.getName(), pGraph.getTypeGraph());
        this.depth = pGraph.depth;
        for (PatternNode pNode : pGraph.nodeSet()) {
            addNode(pNode);
        }
        for (PatternEdge pEdge : pGraph.edgeSet()) {
            addEdgeContext(pEdge);
        }
    }

    // ------------------------------------------------------------------------
    // Overridden methods
    // ------------------------------------------------------------------------

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
        return this.factory;
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

    /** Returns a fresh pattern node. The node is not added to the graph. */
    public PatternNode createNode(TypeNode type) {
        return getFactory().createNode(type, nodeSet());
    }

    /** Returns a fresh pattern edge. The edge is not added to the graph. */
    public PatternEdge createEdge(PatternNode source, TypeEdge type,
            PatternNode target) {
        return getFactory().createEdge(source, type, target);
    }

    /**
     * Returns a list of pattern edges incoming into the given node, with the
     * proper given type.
     */
    public Set<PatternEdge> getInEdgesWithType(PatternNode node,
            final TypeEdge edgeType) {
        return new UnmodifiableSetView<PatternEdge>(inEdgeSet(node)) {
            @Override
            public boolean approves(Object obj) {
                if (!(obj instanceof PatternEdge)) {
                    return false;
                }
                PatternEdge pEdge = (PatternEdge) obj;
                return pEdge.getType() == edgeType;
            }
        };
    }

    /**
     * Returns true if there exists at least one pattern edge incoming to the 
     * given node, with the proper given type. 
     */
    public boolean hasInEdgeWithType(PatternNode node, TypeEdge edgeType) {
        return getInEdgesWithType(node, edgeType).size() > 0;
    }

    /** Returns the set of incoming type edges for the given node. */
    public Set<TypeEdge> getIncomingEdgeTypes(PatternNode pNode) {
        return getTypeGraph().inEdgeSet(pNode.getType());
    }

    /** Returns the duo of incoming type edges for the given node. */
    public Duo<TypeEdge> getDuoIncomingEdgeTypes(PatternNode pNode) {
        return getTypeGraph().getIncomingEdges(pNode.getType());
    }

    /**
     * Returns true if the given node is uniquely covered by the incoming edge
     * morphisms. Uniqueness corresponds to the absence of distinct incoming
     * edges of the same type. While this is condition is always satisfied in
     * pattern graphs, in pattern shapes it may be falsified due to pattern
     * collapsing.  
     */
    public boolean isUniquelyCovered(PatternNode node) {
        // Check the type graph for the incoming types.
        for (TypeEdge edgeType : getTypeGraph().inEdgeSet(node.getType())) {
            if (getInEdgesWithType(node, edgeType).size() > 1) {
                return false;
            }
        }
        return true;
    }

    /** Pattern deletion operation. */
    public boolean deletePattern(PatternNode node) {
        if (!containsNode(node)) {
            // The node may already be deleted by a domino removal.
            return false;
        }
        for (PatternNode delNode : getDownwardTraversal(node)) {
            assert isUniquelyCovered(delNode);
            removeNodeContext(delNode);
        }
        return true;
    }

    /** Pattern addition operation for layer 0. */
    public PatternNode addNodePattern(TypeNode tNode) {
        assert tNode.isNodePattern();
        PatternNode pNode = createNode(tNode);
        addNode(pNode);
        return pNode;
    }

    /** Pattern addition operation for layer 1. */
    public Pair<PatternNode,Duo<PatternEdge>> addEdgePattern(TypeEdge m1,
            TypeEdge m2, PatternNode p1, PatternNode p2) {
        assert p1.isNodePattern() && p2.isNodePattern();
        return addPattern(m1, m2, p1, p2);
    }

    /** Callback method to establish the pre-conditions for closure. */
    public void prepareClosure(Match match) {
        // Empty by design. To be overloaded in subclasses.
    }

    /** Pattern closure. */
    public Pair<PatternNode,Duo<PatternEdge>> closePattern(TypeEdge m1,
            TypeEdge m2, PatternNode p1, PatternNode p2) {
        assert isUniquelyCovered(p1) && isUniquelyCovered(p2);
        return addPattern(m1, m2, p1, p2);
    }

    /** Common method for addition and closure operation. */
    private Pair<PatternNode,Duo<PatternEdge>> addPattern(TypeEdge m1,
            TypeEdge m2, PatternNode p1, PatternNode p2) {
        assert containsNode(p1) && containsNode(p2);
        assert m1.target().equals(m2.target());
        assert m1.source().equals(p1.getType());
        assert m2.source().equals(p2.getType());

        TypeNode tNode = m1.target();
        PatternNode pNode = createNode(tNode);
        addNode(pNode);

        PatternEdge d1 = createEdge(p1, m1, pNode);
        PatternEdge d2 = createEdge(p2, m2, pNode);
        addEdgeContext(d1);
        addEdgeContext(d2);

        Duo<PatternEdge> duo = new Duo<PatternEdge>(d1, d2);
        return new Pair<PatternNode,Duo<PatternEdge>>(pNode, duo);
    }

}
