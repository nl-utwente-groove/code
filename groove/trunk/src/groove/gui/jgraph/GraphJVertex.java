/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * $Id: GraphJVertex.java,v 1.27 2008-01-31 11:11:29 rensink Exp $
 */
package groove.gui.jgraph;

import static groove.util.Converter.ITALIC_TAG;
import groove.graph.Edge;
import groove.graph.Label;
import groove.graph.Node;
import groove.graph.TypeLabel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Extends DefaultGraphCell to use a Node as user object but send the toString
 * method to a set of self-edge labels. Provides a convenience method to
 * retrieve the user object as a Node. Also provides a single default port for
 * the graph cell, and a convenience method to retrieve it.
 */
public class GraphJVertex<N extends Node,E extends Edge<N>> extends JVertex
        implements GraphJCell<N,E> {
    /**
     * Constructs a jnode on top of a graph node.
     * @param jModel the model in which this vertex exists
     * @param node the underlying graph node for this model node
     * @param vertexLabelled flag to indicate if the vertex should be labelled.
     * @ensure getUserObject() == node, labels().isEmpty()
     */
    GraphJVertex(GraphJModel<N,E> jModel, N node, boolean vertexLabelled) {
        super(jModel, node.getNumber());
        this.node = node;
        this.vertexLabelled = vertexLabelled;
    }

    /** Returns the {@link JModel} associated with this vertex. */
    @SuppressWarnings("unchecked")
    @Override
    public GraphJModel<N,E> getJModel() {
        return (GraphJModel<N,E>) super.getJModel();
    }

    /**
     * Constructs a model node on top of a graph node.
     * @param jModel the model in which this vertex exists
     * @param node the underlying graph node for this model node. Note that this
     *        may be null.
     * @ensure getUserObject() == node, labels().isEmpty()
     */
    GraphJVertex(GraphJModel<N,E> jModel, N node) {
        this(jModel, node, true);
    }

    /**
     * Returns the graph node wrapped by this {@link JVertex}.
     */
    public N getNode() {
        return this.node;
    }

    @Override
    public boolean isVisible() {
        return !isFiltered() || getJModel().isShowUnfilteredEdges()
            && hasVisibleIncidentEdge();
    }

    /**
     * Tests if this node has a visible incident edge.
     */
    boolean hasVisibleIncidentEdge() {
        boolean result = false;
        Iterator<?> jEdgeIter = getPort().edges();
        while (!result && jEdgeIter.hasNext()) {
            GraphJEdge<?,?> jEdge = (GraphJEdge<?,?>) jEdgeIter.next();
            result =
                !jEdge.getLines().isEmpty()
                    && (jEdge.getSource() == this || !jEdge.isSourceLabel());
        }
        return result;
    }

    /**
     * Indicates if all list labels on this node are filtered (and therefore
     * invisible), or at least one node type is filtered.
     */
    private boolean isFiltered() {
        boolean result = true;
        for (Label label : getListLabels()) {
            if (getJModel().isFiltering(label)) {
                if (label.isNodeType()) {
                    result = true;
                    break;
                }
            } else {
                result = false;
            }
        }
        return result;
    }

    /** This implementation adds the data edges to the super result. */
    public List<StringBuilder> getLines() {
        List<StringBuilder> result = new LinkedList<StringBuilder>();
        // show the node identity if required
        if (getJModel().isShowNodeIdentities()) {
            String id = getNodeIdentity();
            if (id != null) {
                result.add(ITALIC_TAG.on(new StringBuilder(id)));
            }
        }
        for (E edge : getSelfEdges()) {
            // only add edges that have an unfiltered label
            boolean visible = false;
            for (Label label : getListLabels(edge)) {
                if (!getJModel().isFiltering(label)) {
                    visible = true;
                    break;
                }
            }
            if (visible) {
                result.add(getLine(edge));
            }
        }
        return result;
    }

    /**
     * This implementation returns the label text of the edge; moreover, if the
     * opposite end is not also this vertex, the line is turned into an
     * attribute-style assignment.
     */
    public StringBuilder getLine(E edge) {
        StringBuilder result = new StringBuilder();
        result.append(TypeLabel.toHtmlString(edge.label()));
        return result;
    }

    /** This implementation delegates to {@link Edge#label()}. */
    public Label getLabel(E edge) {
        return edge.label();
    }

    /**
     * This implementation returns a special constant label in case the node is
     * a constant, followed by the self-edge labels and data-edge labels; or
     * {@link JCell#NO_LABEL} if the result would otherwise be empty.
     */
    public Collection<? extends Label> getListLabels() {
        Collection<Label> result = new ArrayList<Label>();
        for (E edge : getSelfEdges()) {
            result.addAll(getListLabels(edge));
        }
        if (getSelfEdges().isEmpty()) {
            result.add(NO_LABEL);
        }
        return result;
    }

    /** This implementation delegates to {@link Edge#label()}. */
    public Set<? extends Label> getListLabels(E edge) {
        return Collections.singleton(edge.label());
    }

    /**
     * This implementation forwards the query to the underlying graph node.
     * @see #getNode()
     */
    @Override
    public String toString() {
        return "JVertex for " + getNode();
    }

    /**
     * Returns an unmodifiable view on the self edges.
     * If {@link GraphJModel#isShowVertexLabels()} is set,
     * all edges with equal source and target and without explicit
     * layout information are regarded as self edges.
     */
    public Set<E> getSelfEdges() {
        if (getJModel().isShowVertexLabels()) {
            // add self-edges without layout info
            Set<E> result = new TreeSet<E>(this.edges);
            for (Object edgeObject : getPort().getEdges()) {
                @SuppressWarnings("unchecked")
                GraphJEdge<N,E> jEdge = (GraphJEdge<N,E>) edgeObject;
                if (getJModel().isPotentialUnaryEdge(jEdge.getEdge())) {
                    result.addAll(jEdge.getEdges());
                }
            }
            return result;
        } else {
            return Collections.unmodifiableSet(this.edges);
        }
    }

    /**
     * Adds an edge to the underlying self-edge set, if the edge is appropriate.
     * Indicates in its return value if the edge has indeed been added.
     * @param edge the edge to be added
     * @return <tt>true</tt> if the edge has been added; <tt>false</tt> if
     *         <tt>edge</tt> is not compatible with this j-vertex and cannot be
     *         added.
     * @require <tt>edge.source() == edge.target() == getNode()</tt>
     * @ensure if <tt>result</tt> then <tt>edges().contains(edge)</tt>
     */
    public boolean addSelfEdge(E edge) {
        if (this.vertexLabelled && edge.source() == edge.target()) {
            this.edges.add(edge);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Callback method yielding a string description of the underlying node,
     * used for the node inscription in case node identities are to be shown.
     */
    public String getNodeIdentity() {
        return getNode().toString();
    }

    /** This implementation includes the node number of the underlying node. */
    @Override
    StringBuilder getNodeDescription() {
        StringBuilder result = new StringBuilder();
        result.append("Node");
        String id = getNodeIdentity();
        if (id != null) {
            result.append(" ");
            result.append(ITALIC_TAG.on(id));
        }
        return result;
    }

    /**
     * An indicator whether the vertex can be labelled (otherwise labels are
     * self-edges).
     */
    private final boolean vertexLabelled;
    /** The graph node modelled by this jgraph node. */
    private final N node;
    /** Set of graph edges mapped to this JEdge. */
    private final Set<E> edges = new TreeSet<E>();
}