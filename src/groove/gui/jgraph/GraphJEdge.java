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
import groove.graph.Element;
import groove.graph.Node;
import groove.io.HTMLConverter;
import groove.util.Groove;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultPort;

/**
 * Extends DefaultEdge to store a collection of graph Edges. The graph edges are
 * stored as a Set in the user object. In the latter case, toString() the user
 * object is the empty string.
 */
public class GraphJEdge extends DefaultEdge implements GraphJCell {
    /**
     * Constructs an uninitialised model edge.
     */
    protected GraphJEdge(GraphJGraph jGraph, GraphJModel<?,?> jModel) {
        this.jGraph = jGraph;
        this.jModel = jModel;
    }

    /**
     * Constructs a model edge based on a graph edge.
     * @param edge the underlying graph edge of this model edge.
     */
    protected GraphJEdge(GraphJGraph jGraph, GraphJModel<?,?> jModel, Edge edge) {
        this(jGraph, jModel);
        this.source = edge.source();
        this.target = edge.target();
        this.edges.add(edge);
    }

    @Override
    public GraphJGraph getJGraph() {
        return this.jGraph;
    }

    @Override
    public GraphJModel<?,?> getJModel() {
        return this.jModel;
    }

    /**
     * This implementation delegates the method to the user object.
     */
    public String getText() {
        StringBuilder result = new StringBuilder();
        for (StringBuilder line : getLines()) {
            if (result.length() > 0) {
                result.append(PRINT_SEPARATOR);
            }
            result.append(line);
        }
        return result.toString();
    }

    @Override
    public String toString() {
        return String.format("%s with labels %s", getClass().getName(),
            getKeys());
    }

    /** 
     * Clears the set of graph edges wrapped in this JEdge,
     * and sets the source and target node from the source and target JVertex. 
     */
    void reset() {
        this.edges.clear();
        this.source = null;
        this.target = null;
    }

    /**
     * Adds an edge to the underlying set of edges, if the edge is appropriate.
     * Indicates in its return value if the edge has indeed been added.
     * @param edge the edge to be added
     * @return <tt>true</tt> if the edge has been added; <tt>false</tt> if
     *         <tt>edge</tt> is not compatible with this j-edge and cannot be
     *         added. This implementation returns <tt>true</tt> always.
     * @require <tt>edge.source() == getSourceNode</tt> and
     *          <tt>edge.target() == getTargetNode()</tt>
     * @ensure if <tt>result</tt> then <tt>getEdgeSet().contains(edge)</tt>
     */
    public boolean addEdge(Edge edge) {
        assert edge.source().equals(getSourceNode());
        assert edge.target().equals(getTargetNode());
        return this.edges.add(edge);
    }

    /** Replaces an edge with another, equal one. */
    void replaceEdge(Edge edge) {
        this.edges.remove(edge);
        this.edges.add(edge);
    }

    /** Returns true if source and target node coincide. */
    public boolean isLoop() {
        return this.source == this.target;
    }

    /** 
     * The cloned object is equal to this one after a reset. 
     */
    @Override
    public GraphJEdge clone() {
        GraphJEdge clone = (GraphJEdge) super.clone();
        clone.edges = new TreeSet<Edge>();
        return clone;
    }

    /** 
     * Factory method, in case this object is used as a prototype.
     * Returns a fresh {@link GraphJEdge} of the same type as this one. 
     */
    public GraphJEdge newJEdge(GraphJModel<?,?> jModel, Edge edge) {
        return new GraphJEdge(getJGraph(), jModel, edge);
    }

    /**
     * Returns the tool tip text for this edge.
     */
    public String getToolTipText() {
        return HTML_TAG.on(getEdgeDescription()).toString(); // +
        // getLabelDescription());
    }

    @Override
    public void refreshAttributes() {
        AttributeMap result = createAttributes();
        if (isGrayedOut()) {
            result.applyMap(GraphJGraph.GRAYED_OUT_ATTR);
        }
        if (getAttributes() != null) {
            getAttributes().applyMap(result);
        } else {
            setAttributes(result);
        }
    }

    /**
     * Callback method for creating the core attributes.
     * These might be modified by other parameters; don't call this
     * method directly.
     */
    protected AttributeMap createAttributes() {
        AttributeMap result = GraphJGraph.DEFAULT_EDGE_ATTR.clone();
        return result;
    }

    @Override
    final public boolean isLayoutable() {
        return this.layoutable;
    }

    @Override
    final public boolean setLayoutable(boolean layedOut) {
        boolean result = layedOut != this.layoutable;
        if (result) {
            this.layoutable = layedOut;
        }
        return result;
    }

    @Override
    final public boolean isGrayedOut() {
        return this.grayedOut;
    }

    @Override
    final public boolean setGrayedOut(boolean grayedOut) {
        boolean result = grayedOut != this.grayedOut;
        if (result) {
            this.grayedOut = grayedOut;
            refreshAttributes();
        }
        return result;
    }

    public boolean hasError() {
        return false;
    }

    /**
     * Returns <code>true</code> if the super method does so, and the edge has
     * at least one non-filtered list label, and all end nodes are visible.
     */
    @Override
    public boolean isVisible() {
        if (getSourceVertex() == null || !getSourceVertex().isVisible()) {
            return false;
        }
        if (getTargetVertex() == null || !getTargetVertex().isVisible()) {
            return false;
        }
        return !getJGraph().isFiltering(this);
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

    /**
     * Returns the common target of the underlying graph edges.
     */
    public Node getTargetNode() {
        if (this.target == null) {
            this.target = getTargetVertex().getNode();
        }
        return this.target;
    }

    /**
     * Returns the j-vertex that is the parent of the source port of this
     * j-edge.
     */
    public GraphJVertex getSourceVertex() {
        DefaultPort source = (DefaultPort) getSource();
        return source == null ? null : (GraphJVertex) source.getParent();
    }

    /**
     * Returns the j-vertex that is the parent of the target port of this
     * j-edge.
     */
    public GraphJVertex getTargetVertex() {
        DefaultPort target = (DefaultPort) getTarget();
        return target == null ? null : (GraphJVertex) target.getParent();
    }

    /**
     * Returns an unmodifiable view upon the set of underlying graph edges.
     */
    public Set<? extends Edge> getEdges() {
        return Collections.unmodifiableSet(this.edges);
    }

    /**
     * Returns the first edge from the set of underlying edges.
     */
    public Edge getEdge() {
        return this.edges.isEmpty() ? null : this.edges.iterator().next();
    }

    /**
     * This implementation calls {@link #getLine(Edge)} on all edges in
     * {@link #getEdges()} that are not being filtered by the model
     * according to {@link GraphJGraph#isFiltering(Element)}.
     */
    public List<StringBuilder> getLines() {
        List<StringBuilder> result = new ArrayList<StringBuilder>();
        for (Edge edge : getEdges()) {
            // only add edges that have an unfiltered label
            if (!getJGraph().isFiltering(edge)) {
                result.add(getLine(edge));
            }
        }
        return result;
    }

    /**
     * Callback method to retrieve the line (as it should appear in an 
     * edge label) from a given edge.
     * @see #getLines()
     */
    protected StringBuilder getLine(Edge edge) {
        return new StringBuilder(edge.label().text());
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

    /** Source node of the underlying graph edges. */
    private Node source;
    /** Target node of the underlying graph edges. */
    private Node target;
    /** Set of graph edges mapped to this JEdge. */
    private Set<Edge> edges = new TreeSet<Edge>();

    /** The fixed jGraph that displays this edge. */
    private final GraphJGraph jGraph;
    /** The fixed jModel to which this edge belongs. */
    private final GraphJModel<?,?> jModel;
    private boolean layoutable;
    private boolean grayedOut;

    /** Returns a prototype {@link GraphJEdge} for a given {@link GraphJGraph}. */
    public static GraphJEdge getPrototype(GraphJGraph jGraph) {
        return new GraphJEdge(jGraph, null);
    }

    /**
     * The string used to separate arguments when preparing for editing.
     */
    static public final String PRINT_SEPARATOR = ", ";
}