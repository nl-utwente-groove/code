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
import groove.util.Converter;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;

/**
 * Extends DefaultGraphCell to use a Node as user object but send the toString
 * method to a set of self-edge labels. Provides a convenience method to
 * retrieve the user object as a Node. Also provides a single default port for
 * the graph cell, and a convenience method to retrieve it.
 */
public class GraphJVertex extends DefaultGraphCell implements GraphJCell {
    /**
     * Constructs a jnode on top of a graph node.
     * @param jGraph the {@link JGraph} in which this vertex exists; if {@code null}, the
     * JVertex can only be used as a prototype
     * @param node the underlying graph node for this model node
     * @param vertexLabelled flag to indicate if the vertex should be labelled.
     * @ensure getUserObject() == node, labels().isEmpty()
     */
    GraphJVertex(JGraph jGraph, Node node, boolean vertexLabelled) {
        this.jGraph = jGraph;
        add(new DefaultPort());
        this.node = node;
        this.vertexLabelled = vertexLabelled;
    }

    /**
     * Constructs a model node on top of a graph node.
     * @param jGraph the {@link JGraph} in which this vertex exists
     * @param node the underlying graph node for this model node. Note that this
     *        may be null.
     * @ensure getUserObject() == node, labels().isEmpty()
     */
    GraphJVertex(JGraph jGraph, Node node) {
        this(jGraph, node, true);
    }

    /** Returns the {@link JGraph} associated with this vertex. */
    public JGraph getJGraph() {
        return this.jGraph;
    }

    /**
     * Sets the node wrapped in this GraphJVertex<?,?> to a new one,
     * and clears the set of self-edges. 
     */
    void reset(Node node) {
        this.node = node;
        this.jVertexLabels.clear();
    }

    /** 
     * The cloned object is equal to this one after a reset. 
     */
    @Override
    public GraphJVertex clone() {
        GraphJVertex clone = (GraphJVertex) super.clone();
        clone.jVertexLabels = new TreeSet<Edge<?>>();
        return clone;
    }

    /** 
     * Factory method, in case this object is used as a prototype.
     * Returns a fresh {@link GraphJEdge} of the same type as this one. 
     */
    public GraphJVertex newJVertex(Node node) {
        return new GraphJVertex(getJGraph(), node, this.vertexLabelled);
    }

    /**
     * Returns the graph node wrapped by this {@link GraphJVertex}.
     */
    public Node getNode() {
        return this.node;
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
    public boolean addJVertexLabel(Edge<?> edge) {
        if (isJVertexLabel(edge)) {
            this.jVertexLabels.add(edge);
            return true;
        } else {
            return false;
        }
    }

    /** Tests if a given edge can be added as label to this {@link GraphJVertex}. */
    protected boolean isJVertexLabel(Edge<?> edge) {
        return !edge.isBinary() || this.vertexLabelled
            && edge.source() == edge.target()
            && getJGraph().isShowLoopsAsNodeLabels();
    }

    /** Tests if a given edge's layout attributes
    protected boolean hasLayout(Edge<?> edge) {
        
    }
    
    /**
     * Returns an unmodifiable view on the self edges.
     * These are the edges added using {@link #addJVertexLabel(Edge)}.
     */
    public Set<? extends Edge<?>> getJVertexLabels() {
        return Collections.unmodifiableSet(this.jVertexLabels);
    }

    @Override
    public boolean isVisible() {
        return !isFiltered() || getJGraph().isShowUnfilteredEdges()
            && hasVisibleIncidentEdge();
    }

    /**
     * Callback method to test if this node has an incident edge
     * with nonempty (unfiltered) label text, as determined
     * by {@link GraphJEdge#getLines()}.
     * This is to determine the visibility of the node.
     */
    protected boolean hasVisibleIncidentEdge() {
        boolean result = false;
        for (Object jEdge : getPort().getEdges()) {
            if (!((GraphJEdge) jEdge).getLines().isEmpty()) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * Indicates if all list labels on this node are filtered (and therefore
     * invisible), or at least one node type is filtered.
     */
    protected boolean isFiltered() {
        boolean result = true;
        for (Label label : getListLabels()) {
            if (getJGraph().isFiltering(label)) {
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
        result.addAll(getNodeIdLines());
        // only add edges that have an unfiltered label
        for (Edge<?> edge : getJVertexLabels()) {
            if (!isFiltered(edge)) {
                result.add(new StringBuilder(getLine(edge)));
            }
        }
        return result;
    }

    /** 
     * Returns the (possibly empty) list of lines 
     * describing the node identity, if this is to be shown
     * according to the current setting.
     * @see JGraph#isShowNodeIdentities()
     */
    final protected List<StringBuilder> getNodeIdLines() {
        List<StringBuilder> result = new ArrayList<StringBuilder>();
        if (getJGraph().isShowNodeIdentities()) {
            String id = getNodeIdString();
            if (id != null) {
                result.add(ITALIC_TAG.on(new StringBuilder(id)));
            }
        }
        return result;
    }

    /** 
     * Tests if a given edge is currently being filtered.
     * This is the case if at least one of the list labels on it
     * (as returned by {@link #getListLabels()})
     * is being filtered.
     */
    final protected boolean isFiltered(Edge<?> edge) {
        boolean result = false;
        for (Label label : getListLabels(edge)) {
            if (getJGraph().isFiltering(label)) {
                result = true;
                break;
            }
        }
        return result;
    }

    /** 
     * Returns the text to be shown for the node label of a given edge.
     * This implementation delegates to {@link Edge#label()}. 
     */
    protected StringBuilder getLine(Edge<?> edge) {
        StringBuilder result = new StringBuilder(edge.label().text());
        Converter.toHtml(result);
        return result;
    }

    /**
     * This implementation returns a special constant label in case the node is
     * a constant, followed by the self-edge labels and data-edge labels; or
     * {@link GraphJCell#NO_LABEL} if the result would otherwise be empty.
     */
    public Collection<? extends Label> getListLabels() {
        Collection<Label> result = new ArrayList<Label>();
        for (Edge<?> edge : getJVertexLabels()) {
            result.addAll(getListLabels(edge));
        }
        if (getJVertexLabels().isEmpty()) {
            result.add(NO_LABEL);
        }
        return result;
    }

    /** This implementation delegates to {@link Edge#label()}. */
    protected Set<? extends Label> getListLabels(Edge<?> edge) {
        return Collections.singleton(edge.label());
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

    /**
     * Returns this graph node's one and only port.
     */
    public DefaultPort getPort() {
        return (DefaultPort) getFirstChild();
    }

    /** Returns the number with which this vertex was initialised. */
    public int getNumber() {
        return getNode().getNumber();
    }

    /**
     * Returns HTML-formatted text, without a surrounding HTML tag.
     */
    public String getText() {
        StringBuilder result = new StringBuilder();
        for (StringBuilder line : getLines()) {
            if (result.length() > 0) {
                result.append(Converter.HTML_LINEBREAK);
            }
            result.append(line);
        }
        return result.toString();
    }

    @Override
    public String toString() {
        return String.format("JVertex %d with labels %s", getNumber(),
            getListLabels());
    }

    /**
     * Returns the tool tip text for this vertex.
     */
    public String getToolTipText() {
        return Converter.HTML_TAG.on(getNodeDescription()).toString();
    }

    @Override
    public void refreshAttributes() {
        AttributeMap result = createAttributes();
        if (!getJGraph().isShowBackground()) {
            GraphConstants.setBackground(result, Color.WHITE);
        }
        if (isGrayedOut()) {
            result.applyMap(JAttr.GRAYED_OUT_ATTR);
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
        return JAttr.DEFAULT_NODE_ATTR.clone();
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

    private final JGraph jGraph;
    private boolean layoutable;
    private boolean grayedOut;
    /**
     * An indicator whether the vertex can be labelled (otherwise labels are
     * self-edges).
     */
    private final boolean vertexLabelled;
    /** The graph node modelled by this jgraph node. */
    private Node node;
    /** Set of graph edges mapped to this JEdge. */
    private Set<Edge<?>> jVertexLabels = new TreeSet<Edge<?>>();

    /** Returns a prototype {@link GraphJVertex} for a given {@link JGraph}. */
    public static GraphJVertex getPrototype(JGraph jGraph) {
        return new GraphJVertex(jGraph, null);
    }
}