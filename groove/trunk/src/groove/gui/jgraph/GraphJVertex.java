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
import groove.control.CtrlState;
import groove.graph.Edge;
import groove.graph.Label;
import groove.graph.Node;
import groove.graph.TypeLabel;
import groove.graph.TypeNode;
import groove.graph.algebra.ProductNode;
import groove.graph.algebra.ValueNode;
import groove.graph.algebra.VariableNode;
import groove.lts.GraphState;
import groove.trans.RuleLabel;
import groove.util.Converter;
import groove.view.aspect.AspectEdge;
import groove.view.aspect.AspectValue;
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
public class GraphJVertex<N extends Node,E extends Edge> extends JVertex
        implements GraphJCell<N,E> {
    /**
     * Constructs a jnode on top of a graph node.
     * @param jModel the model in which this vertex exists
     * @param node the underlying graph node for this model node
     * @param vertexLabelled flag to indicate if the vertex should be labelled.
     * @ensure getUserObject() == node, labels().isEmpty()
     */
    GraphJVertex(GraphJModel<N,E> jModel, N node, boolean vertexLabelled) {
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
    GraphJVertex(GraphJModel<N,E> jModel, N node) {
        this(jModel, node, true);
    }

    /**
     * Convenience method to retrieve this model node's user object as a Node.
     * @return this model node's user object as a Node
     * @ensure if getUserObject() instanceof Node then result == getUserObject()
     */
    public N getNode() {
        return this.node;
    }

    /**
     * Returns the actual graph node <i>modelled</i> by the vertex' underlying
     * node. For this implementation this is the same as {@link #getNode()}.
     * @see #getNode()
     */
    public Node getActualNode() {
        return getNode();
    }

    @Override
    public boolean isVisible() {
        boolean result;
        if (isFiltered()) {
            result =
                this.jModel.isShowUnfilteredEdges() && hasVisibleIncidentEdge();
        } else if (isValueNode() && !this.jModel.isShowValueNodes()
            || isDataTypeNode()) {
            result = hasVisibleIncidentEdge();
        } else {
            result = true;
        }
        return result;
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
        boolean result;
        if (isValueNode()) {
            result = this.jModel.isFiltering(getValueLabel());
        } else if (getSelfEdges().isEmpty()) {
            result = this.jModel.isFiltering(NO_LABEL);
        } else {
            result = true;
            // filter if either there is a filtered node type,
            // or all self-edges are filtered
            for (E selfEdge : getSelfEdges()) {
                for (Label label : getListLabels(selfEdge)) {
                    if (this.jModel.isFiltering(label)) {
                        if (label.isNodeType()) {
                            result = true;
                            break;
                        }
                    } else {
                        result = false;
                    }
                }
            }
        }
        return result;
    }

    /** Constant and type nodes are only listable when data nodes are shown. */
    @Override
    public boolean isListable() {
        boolean result =
            this.jModel.isShowValueNodes() || !isValueNode()
                || !isDataTypeNode();
        return result;
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
                CtrlState ctrlState =
                    ((GraphState) getActualNode()).getCtrlState();
                if (ctrlState.getAut().getProgram() != null) {
                    result.add(new StringBuilder("ctrl: "
                        + Converter.toHtml(ctrlState.toString())));
                }
            }
        }
        // add the multiplicity information if appropriate
        // EDUARDO : HACK HACK HACK 
        /*if (this.jModel instanceof ShapeJModel) {
            Shape shape = (Shape) this.jModel.getGraph();
            String mult = shape.getNodeMult((ShapeNode) this.node).toString();
            result.add(Converter.createSpanTag("color: rgb(50,50,255)").on(
                ITALIC_TAG.on(new StringBuilder(mult))));
        }*/
        // add signature label for typed variable nodes
        if (getSignature() != null) {
            result.add(new StringBuilder(
                TypeLabel.toHtmlString(TypeLabel.createLabel(getSignature(),
                    Label.NODE_TYPE))));
        }
        for (E edge : getSelfEdges()) {
            if (!this.jModel.isFiltering(getLabel(edge))) {
                result.add(getLine(edge));
            }
        }
        for (E edge : getDataEdges()) {
            if (!this.jModel.isFiltering(getLabel(edge))) {
                result.add(getLine(edge));
            }
        }
        if (result.size() == 0 && isValueNode()) {
            result.add(new StringBuilder(getValueLabel().text()));
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
        Label edgeLabel = getLabel(edge);
        if (edge.target() == getNode()) {
            // use special node label prefixes to indicate edge role
            if (edge instanceof AspectEdge && !this.jModel.isShowAspects()) {
                AspectEdge aspectEdge = (AspectEdge) edge;
                AspectValue edgeRole = aspectEdge.getRole();
                AspectValue sourceRole = aspectEdge.source().getRole();
                if (edgeRole != null && !edgeRole.equals(sourceRole)) {
                    result.append(TypeLabel.toHtmlString(edgeLabel, edgeRole));
                }
            }
            if (result.length() == 0) {
                result.append(TypeLabel.toHtmlString(edgeLabel));
            }
            if (edgeLabel instanceof RuleLabel
                && !(((RuleLabel) edgeLabel).isSharp() || ((RuleLabel) edgeLabel).isAtom())
                || edge instanceof AspectEdge
                && ((AspectEdge) edge).isAbstract()) {
                result = Converter.ITALIC_TAG.on(result);
            }
        } else {
            // this is a binary edge displayed as a node label
            result.append(edgeLabel);
            @SuppressWarnings("unchecked")
            GraphJVertex<N,E> oppositeVertex =
                this.jModel.getJCellForNode((N) edge.target());
            Node actualTarget = oppositeVertex.getActualNode();
            if (actualTarget instanceof ValueNode) {
                result.append(ASSIGN_TEXT);
                result.append(((ValueNode) actualTarget).getSymbol());
            } else {
                result.append(TYPE_TEXT);
                result.append(((TypeNode) actualTarget).getType());
            }
            result = Converter.toHtml(result);
        }
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
        if (isValueNode()) {
            result.add(getValueLabel());
        }
        for (E edge : getSelfEdges()) {
            result.addAll(getListLabels(edge));
        }
        if (getSignature() != null) {
            result.add(TypeLabel.createLabel(getSignature(), Label.NODE_TYPE));
        } else if (getSelfEdges().isEmpty()) {
            result.add(NO_LABEL);
        }
        for (E edge : getDataEdges()) {
            result.addAll(getListLabels(edge));
        }
        return result;
    }

    /** This implementation delegates to {@link Edge#label()}. */
    public Set<? extends Label> getListLabels(E edge) {
        Set<? extends Label> result;
        Label label = getLabel(edge);
        if (label instanceof RuleLabel) {
            result = ((RuleLabel) label).getMatchExpr().getTypeLabels();
            if (result.isEmpty()) {
                result = Collections.singleton(NO_LABEL);
            }
        } else {
            result = Collections.singleton(label);
        }
        return result;
    }

    /**
     * This implementation adds a constant identifier to the labels in case the
     * node is a ValueNode.
     */
    public Collection<String> getPlainLabels() {
        Collection<String> result = new ArrayList<String>();
        if (isValueNode()) {
            Label symbol = getValueLabel();
            String prefix =
                AttributeAspect.getAttributeValueFor(getSignature()).getPrefix();
            result.add(prefix + symbol);
        }
        for (E edge : getSelfEdges()) {
            result.add(getPlainLabel(edge));
        }
        return result;
    }

    /**
     * This implementation calls {@link TypeLabel#toPrefixedString(Label)} on
     * the edge label.
     */
    public String getPlainLabel(E edge) {
        return TypeLabel.toPrefixedString(edge.label());
    }

    /**
     * Returns an ordered set of outgoing edges going to constants.
     */
    Set<E> getDataEdges() {
        Set<E> result = new TreeSet<E>();
        if (!this.jModel.isShowValueNodes()) {
            for (Object edgeObject : getPort().getEdges()) {
                @SuppressWarnings("unchecked")
                GraphJEdge<N,E> jEdge = (GraphJEdge<N,E>) edgeObject;
                if (jEdge.getSourceVertex() == this
                    && jEdge.isDataEdgeSourceLabel()) {
                    for (E edge : jEdge.getEdges()) {
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
    @SuppressWarnings("unchecked")
    @Override
    public EdgeContent<E> getUserObject() {
        return (EdgeContent<E>) super.getUserObject();
    }

    @Override
    EdgeContent<E> createUserObject() {
        EdgeContent<E> result = new EdgeContent<E>();
        result.setNumber(getNode().getNumber());
        return result;
    }

    /**
     * Returns an unmodifiable view on the self edges.
     * If {@link GraphJModel#isShowVertexLabels()} is set,
     * all edges with equal source and target and without explicit
     * layout information are regarded as self edges.
     */
    public Set<E> getSelfEdges() {
        if (this.jModel.isShowVertexLabels()) {
            // add self-edges without layout info
            Set<E> result = new TreeSet<E>(getUserObject());
            for (Object edgeObject : getPort().getEdges()) {
                @SuppressWarnings("unchecked")
                GraphJEdge<N,E> jEdge = (GraphJEdge<N,E>) edgeObject;
                if (this.jModel.isPotentialUnaryEdge(jEdge.getEdge())) {
                    result.addAll(jEdge.getEdges());
                }
            }
            return result;
        } else {
            return Collections.unmodifiableSet(getUserObject());
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
    boolean isDataTypeNode() {
        return getActualNode() instanceof TypeNode
            && ((TypeNode) getActualNode()).getType().isDataType();
    }

    /**
     * Callback method to determine whether the underlying graph node is data
     * attribute-related.
     */
    public boolean isValueNode() {
        return getActualNode() instanceof ValueNode;
    }

    /**
     * @return true if this node is a (variable or constant) value node, false otherwise.
     */
    public boolean isVariableNode() {
        return getActualNode() instanceof VariableNode;
    }

    /**
     * @return true if this node is a product node, false otherwise.
     */
    public boolean isProductNode() {
        return getActualNode() instanceof ProductNode;
    }

    /**
     * Callback method to determine whether the underlying graph node stores a
     * constant value.
     * @return <code>true</code> if {@link #getActualNode()} is a
     *         {@link ValueNode} storing a constant value.
     * @see #getValueLabel()
     */
    boolean hasValue() {
        return (getActualNode() instanceof ValueNode);
    }

    /**
     * Callback method to return the symbolic representation of the value stored
     * in the underlying graph node, in case the graph node is a value node.
     * @see ValueNode#getSymbol()
     */
    TypeLabel getValueLabel() {
        if (getActualNode() instanceof ValueNode) {
            return TypeLabel.createLabel(((ValueNode) getActualNode()).getSymbol());
        } else {
            return null;
        }
    }

    /**
     * Callback method to return the algebra to which the underlying value node
     * belongs, or <code>null</code> if the underlying node is not a value node.
     * This method returns <code>null</code> if and only if {@link #hasValue()}
     * holds.
     */
    String getSignature() {
        if (getActualNode() instanceof VariableNode) {
            return ((VariableNode) getActualNode()).getSignature();
        } else if (getActualNode() instanceof ValueNode) {
            return ((ValueNode) getActualNode()).getSignature();
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
     * @return A node descriptor, or <code>null</code> if the node has no proper
     *         identity
     */
    public String getNodeIdentity() {
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
            result.append("Constant");
        } else if (node instanceof VariableNode) {
            result.append("Variable");
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

    /** Returns the underlying GraphJModel. */
    GraphJModel<N,E> getGraphJModel() {
        return this.jModel;
    }

    /** The model in which this vertex exists. */
    private final GraphJModel<N,E> jModel;
    /**
     * An indicator whether the vertex can be labelled (otherwise labels are
     * self-edges).
     */
    private final boolean vertexLabelled;
    /** The graph node modelled by this jgraph node. */
    private final N node;

    static private final String ASSIGN_TEXT = " = ";
    static private final String TYPE_TEXT = ": ";
}