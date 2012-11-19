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

import static groove.graph.EdgeRole.BINARY;
import static groove.io.HTMLConverter.ITALIC_TAG;
import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.Node;
import groove.gui.look.VisualKey;
import groove.io.HTMLConverter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import org.jgraph.graph.DefaultPort;

/**
 * JGraph vertex wrapping a single graph node and a set of graph edges.
 * Uses a single port to connect all JEdges.
 * @author Arend Rensink
 * @version $Revision $
 */
public class GraphJVertex extends AbstractJCell {
    /**
     * Constructs a fresh, uninitialised JVertex.
     * Call {@link #setJModel(GraphJModel)} and {@link #setNode(Node)}
     * to initialise.
     */
    protected GraphJVertex() {
        add(new DefaultPort());
    }

    /**
     * Constructs a fresh JVertex, for a given JModel.
     * After construction and before invoking any other method, {@link #setNode(Node)}
     * should be called to provide a node
     * @param jModel the graph model to which this node is connected
     */
    protected GraphJVertex(GraphJModel<?,?> jModel) {
        this();
        setJModel(jModel);
    }

    @Override
    protected void initialise() {
        super.initialise();
        this.edges = new TreeSet<Edge>();
    }

    /**
     * Sets a new node in this JVertex, and resets all other structures
     * to their initial values.
     */
    final public void setNode(Node node) {
        this.node = node;
        initialise();
    }

    /**
     * Returns the graph node wrapped by this {@link GraphJVertex}.
     */
    public Node getNode() {
        return this.node;
    }

    /** The graph node modelled by this jgraph node. */
    private Node node;

    /**
     * Returns this graph node's one and only port.
     */
    public DefaultPort getPort() {
        return (DefaultPort) getFirstChild();
    }

    /** Returns an iterator over the current incident JEdges of this JVertex. */
    @SuppressWarnings("unchecked")
    public Set<GraphJEdge> getJEdges() {
        return getPort().getEdges();
    }

    @Override
    public Collection<? extends GraphJCell> getContext() {
        return getJEdges();
    }

    /** 
     * The cloned object is equal to this one after a reset. 
     */
    @Override
    public GraphJVertex clone() {
        GraphJVertex clone = (GraphJVertex) super.clone();
        clone.initialise();
        return clone;
    }

    /**
     * Adds an edge to the underlying self-edge set, if the edge is appropriate.
     * Indicates in its return value if the edge has indeed been added.
     * @param edge the edge to be added; it is assumed that this is a loop 
     * on the node of this JVertex
     * @return <tt>true</tt> if the edge has been added; <tt>false</tt> if
     *         <tt>edge</tt> is not compatible with this j-vertex and cannot be
     *         added.
     */
    public boolean addEdge(Edge edge) {
        assert edge.source() == edge.target() && edge.source() == getNode();
        if (isCompatible(edge)) {
            this.edges.add(edge);
            setStale(VisualKey.COLOR);
            return true;
        } else {
            return false;
        }
    }

    /** Tests if a given edge can be added as label to this {@link GraphJVertex}. */
    protected boolean isCompatible(Edge edge) {
        return edge.getRole() != BINARY
            || getJGraph().isShowLoopsAsNodeLabels();
    }

    /**
     * Returns the set of graph edges wrapped in this JVertex.
     */
    public Set<? extends Edge> getEdges() {
        if (this.edges == null) {
            this.edges = new TreeSet<Edge>();
        }
        return this.edges;
    }

    /** Set of graph edges mapped to this JEdge. */
    private Set<Edge> edges = new TreeSet<Edge>();

    /** 
     * Tests if a given edge is currently being filtered.
     * This is the case if at least one of the list labels on it
     * (as returned by {@link #getKeys()})
     * is being filtered.
     */
    final protected boolean isFiltered(Edge edge) {
        Edge key = getKey(edge);
        return key != null && getJGraph().isFiltering(key);
    }

    /**
     * This implementation returns a special constant label in case the node is
     * a constant, followed by the self-edge labels and data-edge labels; or
     * {@link GraphJCell#NO_LABEL} if the result would otherwise be empty.
     */
    public Collection<Element> getKeys() {
        Collection<Element> result = new ArrayList<Element>();
        for (Edge edge : getEdges()) {
            Edge key = getKey(edge);
            if (key != null) {
                result.add(key);
            }
        }
        if (result.isEmpty() && hasNodeKey()) {
            result.add(getNodeKey());
        }
        return result;
    }

    /** Tests if this vertex has a special key standing only for the node. */
    private boolean hasNodeKey() {
        return getNodeKey() != null;
    }

    /** Returns the key associated with the node itself. */
    protected Node getNodeKey() {
        return getNode();
    }

    /** This implementation delegates to {@link Edge#label()}. */
    protected Edge getKey(Edge edge) {
        return edge;
    }

    /**
     * Callback method yielding a string description of the underlying node,
     * used for the node inscription in case node identities are to be shown.
     * Subclasses may return {@code null} if there is no useful node identity.
     */
    protected String getNodeIdString() {
        return getNode().toString();
    }

    /** This implementation includes the node number of the underlying node. */
    StringBuilder getNodeDescription() {
        StringBuilder result = new StringBuilder();
        result.append("Node");
        String id = getNodeIdString();
        if (id != null) {
            result.append(" ");
            result.append(ITALIC_TAG.on(id));
        }
        return result;
    }

    /** Returns the number with which this vertex was initialised. */
    public int getNumber() {
        return getNode().getNumber();
    }

    @Override
    public String toString() {
        return String.format("JVertex %d with labels %s", getNumber(),
            getKeys());
    }

    /**
     * Returns the tool tip text for this vertex.
     */
    public String getToolTipText() {
        return HTMLConverter.HTML_TAG.on(getNodeDescription()).toString();
    }

    /** 
     * Returns a fresh, uninitialised instance of this class.
     * Call {@link #setJModel(GraphJModel)} and {@link #setNode(Node)}
     * to initialise.
     */
    public static GraphJVertex newInstance() {
        return new GraphJVertex();
    }
}