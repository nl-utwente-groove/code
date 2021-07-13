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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;

import groove.grammar.aspect.AspectEdge;
import groove.grammar.aspect.AspectGraph;
import groove.grammar.aspect.AspectNode;
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
        this.nodeMap = new LinkedHashMap<>();
        this.tuples = new ArrayList<>();
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
        assert!hasNode(id);
        // detect a suffix that represents a number
        boolean digitFound = false;
        int nodeNr = 0;
        int unit = 1;
        int charIx;
        for (charIx = id.length() - 1; charIx >= 0
            && Character.isDigit(id.charAt(charIx)); charIx--) {
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
            if (edge.label()
                .text()
                .equals(text)
                && edge.target()
                    .equals(target)) {
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
        List<AttrNode> nodes = new ArrayList<>(nodeIds.size());
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
        AttrToGraphMap<N,E> map = new AttrToGraphMap<>(target.getFactory());
        for (AttrNode node : nodeSet()) {
            N nodeImage = target.addNode(node.getNumber());
            map.putNode(node, nodeImage);
        }
        for (AttrEdge edge : edgeSet()) {
            @Nullable E edgeImage = map.mapEdge(edge);
            assert edgeImage != null; // map is constructed to be total on nodes
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

    // ------------------------------------------------------------------------
    // Static fields
    // ------------------------------------------------------------------------

    private static class AspectToAttrMap
        extends AElementMap<AspectNode,AspectEdge,AttrNode,AttrEdge> {
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

    private static class AttrToGraphMap<N extends Node,E extends Edge>
        extends AElementMap<AttrNode,AttrEdge,N,E> {
        /** Constructs a new, empty map. */
        public AttrToGraphMap(ElementFactory<N,E> factory) {
            super(factory);
        }
    }
}
