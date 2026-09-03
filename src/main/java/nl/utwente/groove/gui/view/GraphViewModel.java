/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.gui.view;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.graph.Edge;
import nl.utwente.groove.graph.Element;
import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.graph.GraphInfo;
import nl.utwente.groove.graph.Node;
import nl.utwente.groove.graph.layout.LayoutMap;
import nl.utwente.groove.gui.jgraph.JModel;

/**
 * Library-independent content model of a graph view:
 * the displayed graph, its layout map, and the mapping from
 * graph elements to the cells that display them.
 * Owned by the (backend-specific) {@link JModel}, which keeps
 * delegating accessors; see {@code claude/jgraph-controller-split.md}.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public class GraphViewModel<G extends Graph> {
    /**
     * Returns the underlying graph of this view model.
     */
    public @Nullable G getGraph() {
        return this.graph;
    }

    /** Convenience method to retrieve the underlying graph as a non-{@code null} object. */
    public G getNonNullGraph() {
        var result = getGraph();
        assert result != null;
        return result;
    }

    /**
     * Changes the underlying graph to the one passed in as a parameter.
     * Note that this should only be done as part of an action that also
     * changes the cells of the view model, as well as the
     * mapping from graph elements to cells.
     */
    public void setGraph(G graph) {
        this.graph = graph;
        this.layoutMap = GraphInfo.getLayoutMap(graph);
    }

    /**
     * Prepares the view model for loading a new graph:
     * sets the graph and layout map, and clears the element-to-cell maps.
     */
    public void reset(G graph) {
        this.graph = graph;
        this.layoutMap = GraphInfo.getLayoutMap(graph);
        if (this.layoutMap == null) {
            this.layoutMap = graph.getInfo().getLayoutMap();
        }
        this.nodeJCellMap.clear();
        this.edgeJCellMap.clear();
    }

    /**
     * The underlying graph of this view model.
     */
    private @Nullable G graph;

    /**
     * Returns the (non-{@code null}) layout map of the graph.
     * This is retrieved from {@link GraphInfo#getLayoutMap(Graph)}.
     */
    public LayoutMap getLayoutMap() {
        var result = this.layoutMap;
        assert result != null; // set when the graph was loaded
        return result;
    }

    /**
     * The layout map for the underlying graph.
     * Set as soon as the graph is; {@code null} before that.
     */
    private @Nullable LayoutMap layoutMap;

    /** Stores the layout of a given cell back into the layout map of the graph. */
    public void synchroniseLayout(ViewCell<G> jCell) {
        LayoutMap layoutMap = getLayoutMap();
        assert layoutMap == GraphInfo.getLayoutMap(getGraph());
        if (jCell instanceof ViewEdge) {
            for (Edge edge : jCell.getEdges()) {
                layoutMap.putEdge(edge, jCell.getVisuals().toEdgeLayout());
            }
        } else if (jCell instanceof ViewVertex) {
            layoutMap.putNode(((ViewVertex<G>) jCell).getNode(), jCell.getVisuals().toNodeLayout());
        }
    }

    /** Returns the set of cells associated with a given collection
     * of graph elements.
     */
    public Set<@Nullable ViewCell<?>> getJCells(Collection<? extends Element> elements) {
        var result = new HashSet<@Nullable ViewCell<?>>();
        elements.stream().map(this::getJCell).forEach(result::add);
        return result;
    }

    /**
     * Returns the cell associated with a given graph element. The
     * result is a {@link ViewVertex} for which the graph element is the
     * underlying node or self-edge, or a {@link ViewEdge} for which the graph
     * element is an underlying edge.
     * @param elem the graph element for which the cell is requested
     * @return the cell associated with <tt>elem</tt>
     */
    public @Nullable ViewCell<G> getJCell(Element elem) {
        if (elem instanceof Node) {
            return getJCellForNode((Node) elem);
        } else {
            return getJCellForEdge((Edge) elem);
        }
    }

    /**
     * Returns the vertex or edge cell associated with a given
     * edge. The method returns a vertex if and only if <tt>edge</tt> is
     * a self-edge displayed as a node label.
     * @param edge the graph edge we're interested in
     * @return the cell displaying <tt>edge</tt>
     */
    public @Nullable ViewCell<G> getJCellForEdge(Edge edge) {
        return this.edgeJCellMap.get(edge);
    }

    /**
     * Returns the vertex cell associated with a given node.
     * @param node the graph node we're interested in
     * @return the cell displaying <tt>node</tt> (if the node is known)
     */
    public @Nullable ViewVertex<G> getJCellForNode(Node node) {
        return this.nodeJCellMap.get(node);
    }

    /**
     * Inserts a node-to-cell entry into the element-to-cell mapping.
     * @return the previous cell associated with the node, if any
     */
    public @Nullable ViewVertex<G> putNode(Node node, ViewVertex<G> jVertex) {
        return this.nodeJCellMap.put(node, jVertex);
    }

    /**
     * Inserts an edge-to-cell entry into the element-to-cell mapping.
     * @return the previous cell associated with the edge, if any
     */
    public @Nullable ViewCell<G> putEdge(Edge edge, ViewCell<G> jCell) {
        return this.edgeJCellMap.put(edge, jCell);
    }

    /**
     * Replaces the element-to-cell maps wholesale.
     * Used when the cells are the primary data from which the graph
     * is (re)constructed, as in the editor.
     */
    public void setJCellMaps(Map<? extends Node,? extends ViewVertex<G>> nodeJCellMap,
                             Map<? extends Edge,? extends ViewCell<G>> edgeJCellMap) {
        this.nodeJCellMap.clear();
        this.nodeJCellMap.putAll(nodeJCellMap);
        this.edgeJCellMap.clear();
        this.edgeJCellMap.putAll(edgeJCellMap);
    }

    /** Returns the set of graph nodes currently represented in this view model. */
    public Set<Node> getNodes() {
        return this.nodeJCellMap.keySet();
    }

    /** Returns the number of graph nodes currently represented in this view model. */
    public int nodeCount() {
        return this.nodeJCellMap.size();
    }

    /** Returns the size of the graph, as a sum of the number of nodes and edges. */
    public int size() {
        return this.nodeJCellMap.size() + this.edgeJCellMap.size();
    }

    /**
     * Map from graph nodes to the cells displaying them.
     */
    private final Map<Node,ViewVertex<G>> nodeJCellMap = new HashMap<>();
    /**
     * Map from graph edges to the cells displaying them.
     */
    private final Map<Edge,ViewCell<G>> edgeJCellMap = new HashMap<>();
}
