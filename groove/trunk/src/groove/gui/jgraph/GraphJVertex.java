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
import groove.graph.Label;
import groove.graph.Node;
import groove.io.HTMLConverter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

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
     * Sets a new node in this JVertex, and resets all other structures
     * to their initial values.
     */
    public void setNode(Node node) {
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
    public Collection<GraphJEdge> getContext() {
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

    /** Tests if a given edge can be added as label to this {@link GraphJVertex}. */
    @Override
    public boolean isCompatible(Edge edge) {
        if (getLayout(edge) != null) {
            return false;
        }
        if (edge.getRole() != BINARY) {
            return true;
        }
        return getJGraph().isShowLoopsAsNodeLabels()
            && edge.source() == edge.target() && edge.source() == getNode();
    }

    public Collection<? extends Label> getKeys() {
        Collection<Label> result = new ArrayList<Label>();
        for (Edge edge : getEdges()) {
            Label key = getKey(edge);
            if (key != null) {
                result.add(key);
            }
        }
        result.addAll(getNodeKeys(!result.isEmpty()));
        return result;
    }

    /**
     * Returns the keys associated with the node itself. 
     * @param hasEdgeKeys if {@code true}, this vertex has at least one edge key.
     */
    protected Collection<? extends Label> getNodeKeys(boolean hasEdgeKeys) {
        return Collections.emptySet();
    }

    /** This implementation delegates to {@link Edge#label()}. */
    public Label getKey(Edge edge) {
        return edge.label();
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