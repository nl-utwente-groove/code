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
 * $Id: GraphJEdge.java,v 1.16 2008-01-09 16:16:06 rensink Exp $
 */
package groove.gui.jgraph;

import static groove.io.HTMLConverter.HTML_TAG;
import static groove.io.HTMLConverter.STRONG_TAG;
import groove.graph.Edge;
import groove.graph.Node;
import groove.io.HTMLConverter;
import groove.util.Groove;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.jgraph.graph.DefaultPort;

/**
 * Extends DefaultEdge to store a collection of graph Edges. The graph edges are
 * stored as a Set in the user object. In the latter case, toString() the user
 * object is the empty string.
 */
public class GraphJEdge extends AbstractJCell implements org.jgraph.graph.Edge {
    /**
     * Constructs an uninitialised model edge.
     */
    protected GraphJEdge() {
        // empty
    }

    @Override
    protected void initialise() {
        super.initialise();
        this.edges = null;
        this.source = null;
        this.target = null;
    }

    @Override
    public Collection<? extends GraphJCell> getContext() {
        if (isLoop()) {
            return Collections.singleton(getSourceVertex());
        } else {
            return Arrays.asList(getSourceVertex(), getTargetVertex());
        }
    }

    /** 
     * The cloned object is equal to this one after a reset. 
     */
    @Override
    public GraphJEdge clone() {
        GraphJEdge clone = (GraphJEdge) super.clone();
        clone.initialise();
        return clone;
    }

    /**
     * Returns the source of the edge.
     */
    public DefaultPort getSource() {
        return this.sourcePort;
    }

    /**
     * Returns the target of the edge.
     */
    public DefaultPort getTarget() {
        return this.targetPort;
    }

    /**
     * Sets the source of the edge.
     */
    public void setSource(Object port) {
        this.sourcePort = (DefaultPort) port;
    }

    /**
     * Returns the target of <code>edge</code>.
     */
    public void setTarget(Object port) {
        this.targetPort = (DefaultPort) port;
    }

    /** Source port of the edge. */
    private DefaultPort sourcePort;
    /** Target port of the edge. */
    private DefaultPort targetPort;

    /**
     * Returns the j-vertex that is the parent of the source port of this
     * j-edge.
     */
    public GraphJVertex getSourceVertex() {
        DefaultPort source = getSource();
        return source == null ? null : (GraphJVertex) source.getParent();
    }

    /**
     * Returns the j-vertex that is the parent of the target port of this
     * j-edge.
     */
    public GraphJVertex getTargetVertex() {
        DefaultPort target = getTarget();
        return target == null ? null : (GraphJVertex) target.getParent();
    }

    /**
     * Returns the common source of the underlying graph edges.
     */
    public Node getSourceNode() {
        if (this.source == null) {
            this.source = getSourceVertex().getNode();
        }
        return this.source;
    }

    /** Source node of the underlying graph edges. */
    private Node source;

    /**
     * Returns the common target of the underlying graph edges.
     */
    public Node getTargetNode() {
        if (this.target == null) {
            this.target = getTargetVertex().getNode();
        }
        return this.target;
    }

    /** Target node of the underlying graph edges. */
    private Node target;

    @Override
    public String toString() {
        return String.format("%s with labels %s", getClass().getName(),
            getKeys());
    }

    /**
     * Adds an edge to the underlying set of edges, if the edge is appropriate.
     * The edge should be compatible, as tested by {@link #isCompatible(Edge)}.
     * Indicates in its return value if the edge has indeed been added.
     * @param edge the edge to be added
     */
    @SuppressWarnings("unchecked")
    public void addEdge(Edge edge) {
        // the edge should be compatible, but don't assert this
        // as subclasses may choose to add incompatible edges while flagging an error
        if (getEdges().isEmpty()) {
            this.source = edge.source();
            this.target = edge.target();
        }
        // there may be an edge already present which is equal (according to equals)
        // but not the same as the new one; the new edge should override the old
        // To achieve this, we first remove the edge
        getEdges().remove(edge);
        ((Set<Edge>) getEdges()).add(edge);
    }

    /** Tests if a new edge is compatible with those already wrapped by this JEdge. */
    public boolean isCompatible(Edge edge) {
        return true;
    }

    /** Returns true if source and target node coincide. */
    public boolean isLoop() {
        return this.source == this.target;
    }

    /**
     * Returns the tool tip text for this edge.
     */
    public String getToolTipText() {
        return HTML_TAG.on(getEdgeDescription()).toString(); // +
        // getLabelDescription());
    }

    /**
     * Returns an unmodifiable view upon the set of underlying graph edges.
     */
    public Set<? extends Edge> getEdges() {
        if (this.edges == null) {
            this.edges = new TreeSet<Edge>();
        }
        return this.edges;
    }

    /**
     * Returns the first edge from the set of underlying edges.
     */
    public Edge getEdge() {
        return getEdges().isEmpty() ? null : getEdges().iterator().next();
    }

    /**
     * This implementation calls {@link #getKey(Edge)} on all edges in
     * {@link #getEdges()}.
     */
    public Collection<Edge> getKeys() {
        List<Edge> result = new ArrayList<Edge>();
        for (Edge edge : getEdges()) {
            Edge entry = getKey(edge);
            if (entry != null) {
                result.add(entry);
            }
        }
        return result;
    }

    /** 
     * Returns the tree entry for a given graph edge.
     * @return the entry foe {@code edge}; if {@code null}, the edge
     * has no corresponding tree entry 
     */
    public Edge getKey(Edge edge) {
        return edge;
    }

    StringBuilder getEdgeDescription() {
        StringBuilder result = getEdgeKindDescription();
        if (getKeys().size() > 1) {
            HTMLConverter.toUppercase(result, false);
            result.insert(0, "Multiple ");
            result.append("s");
        }
        String sourceIdentity = getSourceVertex().getNodeIdString();
        if (sourceIdentity != null) {
            result.append(" from ");
            result.append(HTMLConverter.ITALIC_TAG.on(sourceIdentity));
        }
        String targetIdentity = getTargetVertex().getNodeIdString();
        if (targetIdentity != null) {
            result.append(" to ");
            result.append(HTMLConverter.ITALIC_TAG.on(targetIdentity));
        }
        return result;
    }

    /**
     * Callback method from {@link #getEdgeDescription()} to describe the kind
     * of edge.
     */
    StringBuilder getEdgeKindDescription() {
        return new StringBuilder("Graph edge");
    }

    /**
     * Callback method from {@link #getToolTipText()} to describe the labels on
     * this edge.
     */
    String getLabelDescription() {
        StringBuffer result = new StringBuffer();
        String[] displayedLabels = new String[getKeys().size()];
        int labelIndex = 0;
        for (Object label : getKeys()) {
            displayedLabels[labelIndex] = STRONG_TAG.on(label.toString(), true);
            labelIndex++;
        }
        if (displayedLabels.length == 0) {
            result.append(" (unlabelled)");
        } else {
            result.append(", labelled ");
            result.append(Groove.toString(displayedLabels, "", "", ", ",
                " and "));
        }
        return result.toString();
    }

    /** Set of graph edges mapped to this JEdge. */
    private Set<Edge> edges;

    /** 
     * Returns a fresh, uninitialised instance.
     * Call {@link #setJModel(GraphJModel)} to initialise. 
     */
    public static GraphJEdge newInstance() {
        return new GraphJEdge();
    }

    /**
     * The string used to separate arguments when preparing for editing.
     */
    static public final String PRINT_SEPARATOR = ", ";
}