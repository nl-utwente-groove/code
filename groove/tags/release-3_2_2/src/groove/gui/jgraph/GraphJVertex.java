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
import groove.abs.AbstrGraph;
import groove.control.Location;
import groove.graph.Edge;
import groove.graph.Label;
import groove.graph.Node;
import groove.graph.algebra.ProductNode;
import groove.graph.algebra.ValueNode;
import groove.lts.GraphState;
import groove.util.Converter;
import groove.view.LabelParser;
import groove.view.RegExprLabelParser;
import groove.view.aspect.AttributeAspect;

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
public class GraphJVertex extends JVertex implements GraphJCell {
    /**
     * Constructs a jnode on top of a graph node.
     * @param jModel the model in which this vertex exists
     * @param node the underlying graph node for this model node
     * @param vertexLabelled flag to indicate if the vertex can be labelled. If
     *        not, then labels can be used to represent self-edges
     * @ensure getUserObject() == node, labels().isEmpty()
     */
    GraphJVertex(GraphJModel jModel, Node node, boolean vertexLabelled) {
        this.jModel = jModel;
        this.node = node;
        this.vertexLabelled = vertexLabelled;
    }

    /**
     * Constructs a model node on top of a graph node.
     * @param jModel the model in which this vertex exists
     * @param node the underlying graph node for this model node. Note that this
     *        may be null.
     * @ensure getUserObject() == node, labels().isEmpty()
     */
    GraphJVertex(GraphJModel jModel, Node node) {
        this(jModel, node, false);
    }

    /**
     * Convenience method to retrieve this model node's user object as a Node.
     * @return this model node's user object as a Node
     * @ensure if getUserObject() instanceof Node then result == getUserObject()
     */
    public Node getNode() {
        return this.node;
    }

    /**
     * Returns the actual graph node <i>modelled</i> by the vertex' underlying
     * node. For this implementation this is the same as {@link #getNode()}.
     * @see #getNode()
     */
    Node getActualNode() {
        return getNode();
    }

    @Override
    public boolean isVisible() {
        // first test if the node has unfiltered self-edges
        boolean result =
            !hasValue()
                || (isDataNode() ? this.jModel.isShowValueNodes()
                        : !isFiltered());
        Iterator<?> jEdgeIter = getPort().edges();
        while (!result && jEdgeIter.hasNext()) {
            GraphJEdge jEdge = (GraphJEdge) jEdgeIter.next();
            result =
                !jEdge.isFiltered()
                    && (jEdge.getSource() == this || !jEdge.isSourceLabel());
        }
        return result;
    }

    /**
     * Indicates if all self-edges on this node are filtered (and therefore
     * invisible).
     */
    private boolean isFiltered() {
        boolean result = !getSelfEdges().isEmpty();
        Iterator<? extends Edge> listLabelIter = getSelfEdges().iterator();
        while (result && listLabelIter.hasNext()) {
            result =
                this.jModel.isFiltering(getLabel(listLabelIter.next()).text());
        }
        return result;
    }

    /** Constant nodes are only listable when data nodes are shown. */
    @Override
    public boolean isListable() {
        return !hasValue() || this.jModel.isShowValueNodes();
    }

    /** This implementation adds the data edges to the super result. */
    public List<StringBuilder> getLines() {
        List<StringBuilder> result = new LinkedList<StringBuilder>();
        // show the node identity if required
        if (this.jModel.isShowNodeIdentities()) { // IOVKA showing node
            // identity
            String id = getNodeIdentity();
            if (id != null) {
                result.add(ITALIC_TAG.on(new StringBuilder(id)));
            }
            // REMOVE: this is a temp edit until tooltips work
            // to show control location in LTS states
            if (getActualNode() instanceof GraphState) {
                if (((GraphState) getActualNode()).getLocation() != null) {
                    Location l = ((GraphState) getActualNode()).getLocation();
                    result.add(new StringBuilder("ctrl: "
                        + Converter.toHtml(l.getName())));
                }
            }
        }
        // add the multiplicity information if appropriate
        if (this.jModel instanceof AbstrGraphJModel) {
            AbstrGraph graph = (AbstrGraph) this.jModel.getGraph();
            String mult = graph.multiplicityOf(this.node).toString();
            result.add(Converter.createSpanTag("color: rgb(50,50,255)").on(
                ITALIC_TAG.on(new StringBuilder(mult))));
        }

        for (Edge edge : getSelfEdges()) {
            if (!this.jModel.isFiltering(getLabel(edge).text())) {
                result.add(getLine(edge));
            }
        }
        for (Edge edge : getDataEdges()) {
            if (!this.jModel.isFiltering(getLabel(edge).text())) {
                result.add(getLine(edge));
            }
        }
        if (result.size() == 0 && hasValue()) {
            result.add(new StringBuilder(getValueSymbol()));
        }
        return result;
    }

    /**
     * This implementation returns the label text of the edge; moreover, if the
     * opposite end is not also this vertex, the line is turned into an
     * attribute-style assigment.
     */
    public StringBuilder getLine(Edge edge) {
        StringBuilder result = new StringBuilder();
        result.append(getLabel(edge));
        if (edge.opposite() != getNode()) {
            GraphJVertex oppositeVertex =
                this.jModel.getJVertex(edge.opposite());
            result.append(ASSIGN_TEXT);
            result.append(oppositeVertex.getValueSymbol());
        }
        return Converter.toHtml(result);
    }

    /**
     * This implementation returns a special constant label in case the node is
     * a constant, followed by the self-edge labels and data-edge labels; or
     * {@link JVertex#NO_LABEL} if the result would otherwise be empty.
     */
    public Collection<String> getListLabels() {
        Collection<String> result = new ArrayList<String>();
        if (hasValue()) {
            result.add(getValueSymbol());
        }
        for (Edge edge : getSelfEdges()) {
            result.add(getLabel(edge).text());
        }
        for (Edge edge : getDataEdges()) {
            result.add(getLabel(edge).text());
        }
        if (result.isEmpty()) {
            result.add(NO_LABEL);
        }
        return result;
    }

    /**
     * This implementation adds a constant identifier to the labels in case the
     * node is a ValueNode.
     */
    public Collection<String> getPlainLabels() {
        Collection<String> result = new ArrayList<String>();
        if (hasValue()) {
            String symbol = getValueSymbol();
            String prefix =
                AttributeAspect.getAttributeValueFor(getAlgebra()).getPrefix();
            result.add(prefix + symbol);
        }
        for (Edge edge : getSelfEdges()) {
            result.add(getPlainLabel(edge));
        }
        return result;
    }

    /** This implementation delegates to {@link Edge#label()}. */
    public Label getLabel(Edge edge) {
        return edge.label();
    }

    /**
     * This implementation returns <code>edge.label().text()</code>
     */
    public String getPlainLabel(Edge edge) {
        return edge.label().text();
    }

    /** Callback factory method to create a label parser for this jnode. */
    LabelParser createLabelParser() {
        return RegExprLabelParser.getInstance();
    }

    /**
     * Returns an ordered set of outgoing edges going to constants.
     */
    Set<Edge> getDataEdges() {
        Set<Edge> result = new TreeSet<Edge>();
        if (!this.jModel.isShowValueNodes()) {
            for (Object edgeObject : getPort().getEdges()) {
                GraphJEdge jEdge = (GraphJEdge) edgeObject;
                if (jEdge.getSourceVertex() == this
                    && jEdge.isDataEdgeSourceLabel()) {
                    for (Edge edge : jEdge.getEdges()) {
                        result.add(edge);
                    }
                }
            }
        }
        return result;
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
     * This implementation does nothing: setting the user object directly is not
     * the right way to go about it.
     */
    @Override
    public void setUserObject(Object value) {
        // does nothing
    }

    /**
     * Specialises the return type of the super method.
     */
    @Override
    public EdgeContent getUserObject() {
        return (EdgeContent) super.getUserObject();
    }

    @Override
    EdgeContent createUserObject() {
        return new EdgeContent();
    }

    /**
     * Returns an unmodifiable view on the underlying edge set.
     */
    public Set<? extends Edge> getSelfEdges() {
        return Collections.unmodifiableSet(getUserObject());
    }

    /**
     * Adds an edge to the underlying self-edge set, if the edge is appropriate.
     * Indicates in its return value if the edge has indeed been added.
     * @param edge the edge to be added
     * @return <tt>true</tt> if the edge has been added; <tt>false</tt> if
     *         <tt>edge</tt> is not compatible with this j-vertex and cannot
     *         be added.
     * @require <tt>edge.source() == edge.target() == getNode()</tt>
     * @ensure if <tt>result</tt> then <tt>edges().contains(edge)</tt>
     */
    public boolean addSelfEdge(Edge edge) {
        if (!this.vertexLabelled && edge.source() == edge.opposite()) {
            getUserObject().add(edge);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Callback method to determine whether the underlying graph node is data
     * attribute-related.
     */
    boolean isDataNode() {
        return getActualNode() instanceof ValueNode
            || getActualNode() instanceof ProductNode;
    }

    /**
     * Callback method to determine whether the underlying graph node stores a
     * constant value.
     * @return <code>true</code> if {@link #getActualNode()} is a
     *         {@link ValueNode} storing a constant value.
     * @see #getValueSymbol()
     */
    boolean hasValue() {
        return (getActualNode() instanceof ValueNode)
            && ((ValueNode) getActualNode()).hasValue();
    }

    /**
     * Callback method to return the value stored in the underlying graph node,
     * in case the graph node is a constant value node.
     * @see ValueNode#getValue()
     */
    String getValueSymbol() {
        if (getActualNode() instanceof ValueNode) {
            return ((ValueNode) getActualNode()).getSymbol();
        } else {
            return null;
        }
    }

    /**
     * Callback method to return the algebra to which the underlying value node
     * belongs, or <code>null</code> if the underlying node is not a value
     * node. This method returns <code>null</code> if and only if
     * {@link #hasValue()} holds.
     */
    groove.algebra.Algebra<?> getAlgebra() {
        if (getActualNode() instanceof ValueNode) {
            return ((ValueNode) getActualNode()).getAlgebra();
        } else {
            return null;
        }
    }

    /**
     * Callback method yielding a string description of the underlying node,
     * used for the node inscription in case node identities are to be shown. If
     * the node is a constant (see {@link #hasValue()}) the constant value is
     * returned; otherwise this implementation delegates to
     * <code>getNode().toString()</code>. The result may be <code>null</code>,
     * if the node has no proper identity.
     * @return A node descriptor, or <code>null</code> if the node has no
     *         proper identity
     */
    String getNodeIdentity() {
        // if (isConstant()) {
        // return getConstant().toString();
        // } else
        if (getActualNode() == null) {
            return null;
        } else {
            return getActualNode().toString();
        }
    }

    /** This implementation includes the node number of the underlying node. */
    @Override
    StringBuilder getNodeDescription() {
        StringBuilder result = new StringBuilder();
        Node node = getActualNode();
        if (node instanceof ValueNode) {
            if (((ValueNode) node).hasValue()) {
                result.append("Constant");
            } else {
                result.append("Variable");
            }
        } else if (node instanceof ProductNode) {
            result.append("Product");
        }
        if (result.length() == 0) {
            result.append("Node");
        } else {
            result.append(" node");
        }
        String id = getNodeIdentity();
        if (id != null) {
            result.append(" ");
            result.append(ITALIC_TAG.on(id));
        }
        return result;
    }

    /**
     * Removes an edge from the underlying edge set.
     * @param edge the edge to be removed
     * @ensure ! edges().contains(edge)
     */
    public void removeSelfEdge(Edge edge) {
        getUserObject().remove(edge);
    }

    /** The model in which this vertex exists. */
    private final GraphJModel jModel;
    /**
     * An indicator whether the vertex can be labelled (otherwise labels are
     * self-edges).
     */
    private final boolean vertexLabelled;
    /** The graph node modelled by this jgraph node. */
    private final Node node;

    static private final String ASSIGN_TEXT = " = ";

    /** Returns the underlying GraphJModel. */
    GraphJModel getGraphJModel() {
        return this.jModel;
    }

}