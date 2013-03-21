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
package groove.gui.jgraph;

import static groove.graph.EdgeRole.BINARY;
import static groove.io.HTMLConverter.ITALIC_TAG;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Label;
import groove.graph.Node;
import groove.io.HTMLConverter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.jgraph.graph.DefaultPort;

/**
 * Generic abstract JCell subclass implementing the {@link JVertex} interface.
 * @param <G> the graph type for which the JVertex is intended
 * @author rensink
 * @version $Revision $
 */
public abstract class AJVertex<G extends Graph,JG extends JGraph<G>,JM extends JModel<G>,JE extends JEdge<G>>
        extends AJCell<G,JG,JM> implements JVertex<G> {
    /**
     * Constructs a fresh, uninitialised JVertex.
     * Call {@link #setJModel(JModel)} and {@link #setNode(Node)}
     * to initialise.
     */
    protected AJVertex() {
        add(new DefaultPort());
    }

    @Override
    @SuppressWarnings("unchecked")
    public Set<JE> getContext() {
        return getPort().getEdges();
    }

    /**
     * Sets a new node in this JVertex, and resets all other structures
     * to their initial values.
     */
    @Override
    public void setNode(Node node) {
        this.node = node;
        initialise();
    }

    @Override
    public Node getNode() {
        return this.node;
    }

    /** The graph node modelled by this jgraph node. */
    private Node node;

    /**
     * Returns this graph node's one and only port.
     */
    @Override
    public DefaultPort getPort() {
        return (DefaultPort) getFirstChild();
    }

    /** 
     * The cloned object is equal to this one after a reset. 
     */
    @Override
    public JVertex<G> clone() {
        @SuppressWarnings("unchecked")
        AJVertex<G,JG,JM,JE> clone = (AJVertex<G,JG,JM,JE>) super.clone();
        clone.initialise();
        return clone;
    }

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

    @Override
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
    @Override
    public String getNodeIdString() {
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
    @Override
    public int getNumber() {
        return getNode().getNumber();
    }

    @Override
    public String toString() {
        return String.format("%s %d with labels %s",
            getClass().getSimpleName(), getNumber(), getKeys());
    }

    /**
     * Returns the tool tip text for this vertex.
     */
    public String getToolTipText() {
        return HTMLConverter.HTML_TAG.on(getNodeDescription()).toString();
    }
}
