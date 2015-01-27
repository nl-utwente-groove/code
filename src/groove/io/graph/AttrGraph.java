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
package groove.io.graph;

import groove.abstraction.Multiplicity;
import groove.abstraction.Multiplicity.MultKind;
import groove.abstraction.neigh.EdgeMultDir;
import groove.abstraction.neigh.equiv.EquivClass;
import groove.abstraction.neigh.equiv.NodeEquivClass;
import groove.abstraction.neigh.shape.EdgeSignature;
import groove.abstraction.neigh.shape.Shape;
import groove.abstraction.neigh.shape.ShapeEdge;
import groove.abstraction.neigh.shape.ShapeFactory;
import groove.abstraction.neigh.shape.ShapeNode;
import groove.abstraction.pattern.shape.PatternEdge;
import groove.abstraction.pattern.shape.PatternFactory;
import groove.abstraction.pattern.shape.PatternNode;
import groove.abstraction.pattern.shape.PatternShape;
import groove.grammar.aspect.AspectEdge;
import groove.grammar.aspect.AspectGraph;
import groove.grammar.aspect.AspectNode;
import groove.grammar.host.HostEdge;
import groove.grammar.host.HostNode;
import groove.grammar.type.TypeGraph;
import groove.graph.AElementMap;
import groove.graph.Edge;
import groove.graph.ElementFactory;
import groove.graph.GEdge;
import groove.graph.GGraph;
import groove.graph.Graph;
import groove.graph.GraphInfo;
import groove.graph.GraphRole;
import groove.graph.Node;
import groove.graph.NodeSetEdgeSetGraph;
import groove.graph.plain.PlainGraph;
import groove.graph.plain.PlainLabel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Intermediate graph format used for loading and saving graphs.
 * Characteristics are:
 * <li> Nodes and edge may have string attributes
 * (corresponding to XML attributes).
 * <li> The graph maintains a
 * mapping from string identifiers to nodes.
 * <li> The graph maintains a set of node tuples, stored as lists of nodes.
 * (This is used to serialise shape equivalence relations.)
 * @author Arend Rensink
 * @version $Revision $
 */
public class AttrGraph extends NodeSetEdgeSetGraph<AttrNode,AttrEdge> {
    /**
     * Creates an empty graph with a given name.
     */
    public AttrGraph(String name) {
        super(name);
        this.nodeMap = new LinkedHashMap<String,AttrNode>();
        this.tuples = new ArrayList<AttrTuple>();
    }

    @Override
    public AttrGraph clone() {
        AttrGraph result = newGraph(getName());
        for (AttrNode node : nodeSet()) {
            result.addNode(node.clone());
        }
        for (AttrEdge edge : edgeSet()) {
            result.addEdge(edge.clone());
        }
        return result;
    }

    @Override
    public AttrGraph newGraph(String name) {
        return new AttrGraph(name);
    }

    @Override
    public ElementFactory<AttrNode,AttrEdge> getFactory() {
        return AttrFactory.instance();
    }

    @Override
    public boolean addNode(AttrNode node) {
        boolean result = super.addNode(node);
        if (result) {
            // adds the node to the identifier map
            // this may be overridden by a user-provided id by
            // using addNode(String) instead
            this.nodeMap.put(node.toString(), node);
        }
        return result;
    }

    /**
     * Adds a fresh node based on a given string id.
     * Attempts to extract a node number from the id; if that does not work,
     * or the node number has been used already, generates a fresh node.
     * @param id the (non-{@code null}, nonempty) node identifier; it is
     * assumed that no node with this id exists as yet
     * @return the existing or freshly created node
     */
    public AttrNode addNode(String id) {
        assert !hasNode(id);
        // detect a suffix that represents a number
        boolean digitFound = false;
        int nodeNr = 0;
        int unit = 1;
        int charIx;
        for (charIx = id.length() - 1; charIx >= 0 && Character.isDigit(id.charAt(charIx)); charIx--) {
            nodeNr += unit * (id.charAt(charIx) - '0');
            unit *= 10;
            digitFound = true;
        }
        AttrNode result = null;
        if (digitFound) {
            AttrNode node = getFactory().createNode(nodeNr);
            // tests if a node with this number exists already
            if (addNode(node)) {
                result = node;
            }
        }
        if (result == null) {
            result = addNode();
        }
        this.nodeMap.put(id, result);
        return result;
    }

    /**
     * Tests if a node with a given string identifier exists
     * @param id the (non-{@code null}, nonempty) node identifier
     * @return {@code true} if the graph contains a node with this identifier
     */
    public boolean hasNode(String id) {
        return this.nodeMap.containsKey(id);
    }

    /**
     * Returns the node corresponding to a given string identifier.
     * @param id the (non-{@code null}, nonempty) node identifier
     * @return the existing node, or {@code null} if no node with this identifier exists
     */
    public AttrNode getNode(String id) {
        return this.nodeMap.get(id);
    }

    /** Returns the edge in the graph with a given source, label text and target, if any.
     * @param source the non-{@code null} source node
     * @param text the non-{@code null} label text
     * @param target the non=-{@code null} target node
     * @return the edge in the graph with the given data, or {@code null} if there is none
     */
    public AttrEdge getEdge(AttrNode source, String text, AttrNode target) {
        AttrEdge result = null;
        for (AttrEdge edge : outEdgeSet(source)) {
            if (edge.label().text().equals(text) && edge.target().equals(target)) {
                result = edge;
                break;
            }
        }
        return result;
    }

    /**
     * Returns the mapping from string identifiers to nodes,
     * built up during calls to {@link #getNode(String)}.
     */
    public Map<String,AttrNode> getNodeMap() {
        return Collections.unmodifiableMap(this.nodeMap);
    }

    private final Map<String,AttrNode> nodeMap;

    /** Returns the role of this graph. */
    @Override
    public GraphRole getRole() {
        return this.role;
    }

    /** Sets the role of this graph. */
    public void setRole(GraphRole role) {
        testFixed(false);
        this.role = role;
    }

    private GraphRole role;

    /**
     * Adds a node tuple in the form of a list of node identifiers.
     * The identifiers must be known at the time of the call.
     * @param nodeIds a non-{@code null}, non-empty list of known node identifiers
     */
    public void addTuple(List<String> nodeIds) {
        List<AttrNode> nodes = new ArrayList<AttrNode>(nodeIds.size());
        for (String id : nodeIds) {
            AttrNode node = getNode(id);
            assert node != null : String.format("Unknown node id %s", id);
            nodes.add(node);
        }
        this.tuples.add(new AttrTuple(nodes));
    }

    /**
     * Adds a hyperedge in the form of a list of node identifiers.
     * The identifiers must be known at the time of the call.
     * @param tuple a non-{@code null} node tuple to be added
     */
    public void addTuple(AttrTuple tuple) {
        this.tuples.add(tuple);
    }

    /**
     * Returns the list of node tuples in this XML graph.
     */
    public List<AttrTuple> getTuples() {
        return Collections.unmodifiableList(this.tuples);
    }

    private final List<AttrTuple> tuples;

    /**
     * Copies the structure of this XML graph over to another graph.
     * Node numbers are preserved.
     * Any attributes and hyperedges of the XML graph are discarded.
     * If the target graph is not initially empty, this may mean that
     * copied nodes coincide with pre-existing nodes.
     * The target graph is left unfixed.
     * @param target the target of the copy operation; non-{@code null}
     */
    public <N extends Node,E extends GEdge<N>,G extends GGraph<N,E>> void copyTo(G target) {
        AttrToGraphMap<N,E> map = new AttrToGraphMap<N,E>(target.getFactory());
        for (AttrNode node : nodeSet()) {
            N nodeImage = target.addNode(node.getNumber());
            map.putNode(node, nodeImage);
        }
        for (AttrEdge edge : edgeSet()) {
            E edgeImage = map.mapEdge(edge);
            target.addEdge(edgeImage);
        }
        GraphInfo.transfer(this, target, map);
    }

    /**
     * Converts this XML graph to a plain graph.
     * Any attributes and hyperedges of the XML graph are discarded.
     */
    public PlainGraph toPlainGraph() {
        PlainGraph result = new PlainGraph(getName(), getRole());
        copyTo(result);
        result.setFixed();
        return result;
    }

    /**
     * Converts this XML graph to an aspect graph.
     * Any attributes and hyperedges of the XML graph are discarded.
     * @see AspectGraph#newInstance(Graph)
     */
    public AspectGraph toAspectGraph() {
        return AspectGraph.newInstance(this);
    }

    /** Converts this XML graph to a shape, under a given type graph. */
    public Shape toShape(TypeGraph typeGraph) {
        ShapeFactory shapeFactory = ShapeFactory.newInstance(typeGraph.getFactory());
        Shape result = new Shape(getName(), shapeFactory);
        AttrToShapeMap map = new AttrToShapeMap(shapeFactory);
        // add nodes
        for (AttrNode node : nodeSet()) {
            ShapeNode nodeImage = shapeFactory.createNode(node.getNumber());
            result.addNode(nodeImage);
            map.putNode(node, nodeImage);
            // set the node multiplicity
            String nodeMultStr = node.getAttribute(NODE_MULT_ATTR_NAME);
            Multiplicity nodeMult = getMultiplicity(nodeMultStr, MultKind.NODE_MULT);
            result.setNodeMult(nodeImage, nodeMult);
        }
        // add equivalence classes
        for (AttrTuple tuple : getTuples()) {
            NodeEquivClass<ShapeNode> ec = new NodeEquivClass<ShapeNode>(shapeFactory);
            for (AttrNode node : tuple.getNodes()) {
                ShapeNode nodeImage = map.getNode(node);
                ec.add(nodeImage);
            }
            result.getEquivRelation().add(ec);
        }
        // add edges
        for (AttrEdge edge : edgeSet()) {
            ShapeEdge edgeImage = map.mapEdge(edge);
            result.addEdge(edgeImage);
            // add multiplicities
            for (EdgeMultDir direction : EdgeMultDir.values()) {
                String attrName =
                    direction == EdgeMultDir.OUTGOING ? EDGE_OUT_MULT_ATTR_NAME
                        : EDGE_IN_MULT_ATTR_NAME;
                String multStr = edge.getAttribute(attrName);
                if (multStr != null) {
                    Multiplicity mult = getMultiplicity(multStr, MultKind.EDGE_MULT);
                    EdgeSignature es = result.getEdgeSignature(edgeImage, direction);
                    result.setEdgeSigMult(es, mult);
                }
            }
        }
        GraphInfo.transfer(this, result, map);
        return result;
    }

    /** Converts this XML graph to a pattern shape, under a given type graph. */
    public PatternShape toPattern(groove.abstraction.pattern.shape.TypeGraph typeGraph) {
        PatternShape result = new PatternShape(getName(), typeGraph);
        AttrToPatternMap map = new AttrToPatternMap(result.getFactory());
        // add nodes
        for (AttrNode node : nodeSet()) {
            PatternNode nodeImage = result.addNode(node.getNumber());
            result.addNode(nodeImage);
            map.putNode(node, nodeImage);
            // set the node multiplicity
            String nodeMultStr = node.getAttribute(NODE_MULT_ATTR_NAME);
            Multiplicity nodeMult = getMultiplicity(nodeMultStr, MultKind.NODE_MULT);
            result.setMult(nodeImage, nodeMult);
        }
        // add edges
        for (AttrEdge edge : edgeSet()) {
            PatternEdge edgeImage = map.mapEdge(edge);
            result.addEdge(edgeImage);
            // add multiplicities
            String multStr = edge.getAttribute(EDGE_MULT_ATTR_NAME);
            Multiplicity mult = getMultiplicity(multStr, MultKind.EDGE_MULT);
            result.setMult(edgeImage, mult);
        }
        GraphInfo.transfer(this, result, map);
        return result;
    }

    /** Converts the given string to a proper multiplicity, based on given kind. */
    private Multiplicity getMultiplicity(String multStr, MultKind kind) {
        String[] parts = multStr.split(" ");
        int lowerBound = Integer.parseInt(parts[0]);
        int upperBound;
        if ("w".equals(parts[1])) {
            upperBound = Multiplicity.OMEGA;
        } else {
            upperBound = Integer.parseInt(parts[1]);
        }
        return Multiplicity.getMultiplicity(lowerBound, upperBound, kind);
    }

    /**
     * Constructs an XML graph on the basis of a given aspect graph.
     * This operation is inverse to {@link #toAspectGraph()}.
     */
    public static AttrGraph newInstance(AspectGraph graph) {
        AttrGraph result = new AttrGraph(graph.getName());
        result.setRole(graph.getRole());
        AspectToAttrMap elementMap = new AspectToAttrMap();
        for (AspectNode node : graph.nodeSet()) {
            AttrNode nodeImage = result.addNode(node.getNumber());
            elementMap.putNode(node, nodeImage);
            for (PlainLabel label : node.getPlainLabels()) {
                result.addEdge(nodeImage, label, nodeImage);
            }
        }
        for (AspectEdge edge : graph.edgeSet()) {
            result.addEdgeContext(elementMap.mapEdge(edge));
        }
        GraphInfo.transfer(graph, result, elementMap);
        result.setFixed();
        return result;
    }

    /**
     * Construct an XML graph on the basis of a given shape.
     * This operation is inverse to {@link #toShape(TypeGraph)}.
     */
    public static AttrGraph newInstance(Shape shape) {
        AttrGraph result = new AttrGraph(shape.getName());
        result.setRole(GraphRole.HOST);
        ShapeToAttrMap map = new ShapeToAttrMap();
        // add the nodes
        for (ShapeNode node : shape.nodeSet()) {
            AttrNode nodeImage = result.addNode(node.getNumber());
            map.putNode(node, nodeImage);
            // add the multiplicity
            Multiplicity nodeMult = shape.getNodeMult(node);
            nodeImage.setAttribute(NODE_MULT_ATTR_NAME, nodeMult.toSerialString());
        }
        // add the tuples
        for (EquivClass<ShapeNode> ec : shape.getEquivRelation()) {
            result.addTuple(map.mapTuple(ec));
        }
        // add the edges
        for (ShapeEdge edge : shape.edgeSet()) {
            AttrEdge edgeImage = map.mapEdge(edge);
            result.addEdge(edgeImage);
            // add the edge multiplicities
            for (EdgeMultDir direction : EdgeMultDir.values()) {
                Multiplicity edgeMult = shape.getEdgeMult(edge, direction);
                String attrName =
                    direction == EdgeMultDir.OUTGOING ? EDGE_OUT_MULT_ATTR_NAME
                        : EDGE_IN_MULT_ATTR_NAME;
                edgeImage.setAttribute(attrName, edgeMult.toSerialString());
            }
        }
        GraphInfo.transfer(shape, result, map);
        result.setFixed();
        return result;
    }

    /**
     * Constructs an XML graph on the basis of a given pattern shape.
     * This operation is inverse to {@link #toPattern(groove.abstraction.pattern.shape.TypeGraph)}.
     */
    public static AttrGraph newInstance(PatternShape shape) {
        AttrGraph result = new AttrGraph(shape.getName());
        result.setRole(GraphRole.HOST);
        PatternToXmlMap map = new PatternToXmlMap();
        // add the nodes
        for (PatternNode node : shape.nodeSet()) {
            AttrNode nodeImage = result.addNode(node.getNumber());
            map.putNode(node, nodeImage);
            // add the multiplicity
            Multiplicity nodeMult = shape.getMult(node);
            nodeImage.setAttribute(NODE_MULT_ATTR_NAME, nodeMult.toSerialString());
        }
        // add the edges
        for (PatternEdge edge : shape.edgeSet()) {
            AttrEdge edgeImage = map.mapEdge(edge);
            result.addEdge(edgeImage);
            // add the edge multiplicities
            Multiplicity edgeMult = shape.getMult(edge);
            edgeImage.setAttribute(EDGE_MULT_ATTR_NAME, edgeMult.toSerialString());
        }
        GraphInfo.transfer(shape, result, map);
        result.setFixed();
        return result;
    }

    // ------------------------------------------------------------------------
    // Static fields
    // ------------------------------------------------------------------------

    /** Attribute name for node multiplicities. */
    private static final String NODE_MULT_ATTR_NAME = "nmult";
    /** Attribute name for out edge multiplicities. */
    private static final String EDGE_OUT_MULT_ATTR_NAME = "omult";
    /** Attribute name for in edge multiplicities. */
    private static final String EDGE_IN_MULT_ATTR_NAME = "imult";
    /** Attribute name for (undirected) edge multiplicities. */
    private static final String EDGE_MULT_ATTR_NAME = "emult";

    private static class AspectToAttrMap extends
        AElementMap<AspectNode,AspectEdge,AttrNode,AttrEdge> {
        /** Constructs a new, empty map. */
        public AspectToAttrMap() {
            super(AttrFactory.instance());
        }

        @Override
        public AttrEdge createImage(AspectEdge key) {
            AttrNode imageSource = getNode(key.source());
            if (imageSource == null) {
                return null;
            }
            AttrNode imageTarget = getNode(key.target());
            if (imageTarget == null) {
                return null;
            }
            return getFactory().createEdge(imageSource, key.getPlainLabel(), imageTarget);
        }
    }

    private static class AttrToGraphMap<N extends Node,E extends Edge> extends
        AElementMap<AttrNode,AttrEdge,N,E> {
        /** Constructs a new, empty map. */
        public AttrToGraphMap(ElementFactory<N,E> factory) {
            super(factory);
        }
    }

    private static class AttrToShapeMap extends AttrToGraphMap<HostNode,HostEdge> {
        /** Constructs a new, empty map. */
        public AttrToShapeMap(ShapeFactory factory) {
            super(factory);
        }

        @Override
        public ShapeNode getNode(Node key) {
            return (ShapeNode) super.getNode(key);
        }

        /** Specialises the return type. */
        @Override
        public ShapeEdge mapEdge(AttrEdge key) {
            return (ShapeEdge) super.mapEdge(key);
        }
    }

    private static class ShapeToAttrMap extends AElementMap<HostNode,HostEdge,AttrNode,AttrEdge> {
        /** Constructs a new, empty map. */
        public ShapeToAttrMap() {
            super(AttrFactory.instance());
        }

        /**
         * Returns the tuple that is the image of a given collection of
         * shape nodes.
         */
        public AttrTuple mapTuple(Collection<ShapeNode> nodes) {
            List<AttrNode> result = new ArrayList<AttrNode>(nodes.size());
            for (ShapeNode sn : nodes) {
                result.add(getNode(sn));
            }
            return new AttrTuple(result);
        }
    }

    private static class AttrToPatternMap extends AttrToGraphMap<PatternNode,PatternEdge> {
        /** Constructs a new, empty map. */
        public AttrToPatternMap(PatternFactory factory) {
            super(factory);
        }
    }

    private static class PatternToXmlMap extends
        AElementMap<PatternNode,PatternEdge,AttrNode,AttrEdge> {
        /** Constructs a new, empty map. */
        public PatternToXmlMap() {
            super(AttrFactory.instance());
        }
    }
}
