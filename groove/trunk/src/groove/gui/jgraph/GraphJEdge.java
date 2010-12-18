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

import groove.graph.Edge;
import groove.graph.Label;
import groove.graph.Node;
import groove.graph.TypeLabel;
import groove.graph.algebra.ArgumentEdge;
import groove.graph.algebra.OperatorEdge;
import groove.trans.RuleLabel;
import groove.util.Converter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Extends DefaultEdge to store a collection of graph Edges. The graph edges are
 * stored as a Set in the user object. In the latter case, toString() the user
 * object is the empty string.
 */
public class GraphJEdge<N extends Node,E extends Edge> extends JEdge implements
        GraphJCell<N,E> {
    /**
     * Constructs a model edge based on a graph edge. The graph edge is required
     * to have at least arity two; yet we cannot rely on it being a binary
     * edge, it might be regular with some pseudo-ends or it might be a
     * {@link groove.view.aspect.AspectEdge}.
     * @param edge the underlying graph edge of this model edge.
     * @require <tt>edge != null && edge.endCount() >= 0</tt>
     * @ensure labels().size()==1, labels().contains(edge.label) source() ==
     *         edge.source(), target() == edge.target()
     * @throws IllegalArgumentException if <code>edge.endCount() < 2</code>
     */
    @SuppressWarnings("unchecked")
    GraphJEdge(GraphJModel<N,E> jModel, E edge) {
        this.jModel = jModel;
        this.source = (N) edge.source();
        this.target = (N) edge.target();
        getUserObject().add(edge);
    }

    /**
     * Returns <code>true</code> if the super method does so, and the edge has
     * at least one non-filtered list label, and all end nodes are visible.
     */
    @Override
    public boolean isVisible() {
        boolean result = super.isVisible() && !isSourceLabel() && !isFiltered();
        if (result && !this.jModel.isShowUnfilteredEdges()) {
            result =
                getSourceVertex().isVisible()
                    && (isSourceLabel() || getTargetVertex().isVisible());
        }
        return result;
    }

    /**
     * Indicates if this edge is shown as a label on its source node, instead of
     * an explicit edge. This implementation returns <code>true</code> if either
     * {@link #isSelfEdgeSourceLabel()} or {@link #isDataEdgeSourceLabel()}
     * return <code>true</code>.
     */
    public boolean isSourceLabel() {
        return isSelfEdgeSourceLabel() || isDataEdgeSourceLabel();
    }

    /**
     * Indicates if this edge is a self-edge that can be shown as a label on its
     * source vertex. This is the case if the source node contains this edge in
     * its user object.
     * Callback method from {@link #isSourceLabel()}.
     */
    boolean isSelfEdgeSourceLabel() {
        return this.jModel.isShowVertexLabels()
            && this.jModel.isPotentialUnaryEdge(getEdge());
    }

    /**
     * Indicates if this edge has a value node target and can be used as a label
     * on its source node. This is the case if
     * {@link GraphJModel#isShowValueNodes()} holds, and
     * {@link GraphJVertex#hasValue()} holds for the target node. Callback
     * method from {@link #isSourceLabel()}.
     */
    public boolean isDataEdgeSourceLabel() {
        boolean result = !this.jModel.isShowValueNodes();
        if (result) {
            result =
                getTargetVertex().hasValue()
                    || getTargetVertex().isDataTypeNode();
        }
        return result;
    }

    /** Indicates if this edge is filtered (and therefore invisible). */
    boolean isFiltered() {
        boolean result;
        // we don't want vacuous filtering: there should be at least one
        // filtered label
        Collection<? extends Label> listLabels = getListLabels();
        if (listLabels.isEmpty()) {
            result = false;
        } else {
            result = true;
            Iterator<? extends Label> listLabelIter = listLabels.iterator();
            while (result && listLabelIter.hasNext()) {
                result = this.jModel.isFiltering(listLabelIter.next());
            }
        }
        return result;
    }

    /**
     * Returns the common source of the underlying graph edges.
     */
    public N getSourceNode() {
        return this.source;
    }

    /**
     * Returns the common target of the underlying graph edges.
     */
    public N getTargetNode() {
        return this.target;
    }

    /**
     * Specialises the return type.
     */
    @SuppressWarnings("unchecked")
    @Override
    public GraphJVertex<N,E> getSourceVertex() {
        return (GraphJVertex<N,E>) super.getSourceVertex();
    }

    /**
     * Specialises the return type.
     */
    @SuppressWarnings("unchecked")
    @Override
    public GraphJVertex<N,E> getTargetVertex() {
        return (GraphJVertex<N,E>) super.getTargetVertex();
    }

    /**
     * Returns an unmodifiable view upon the set of underlying graph edges.
     */
    public Set<E> getEdges() {
        return Collections.unmodifiableSet(getUserObject());
    }

    /**
     * Returns an arbitrary edge from the set of underlying edges.
     */
    public E getEdge() {
        return getUserObject().iterator().next();
    }

    /**
     * Returns the actual graph edge <i>modelled</i> by this j-edge. For this
     * implementation this is the same as {@link #getEdge()}.
     * @see #getEdge()
     */
    Edge getActualEdge() {
        return getEdge();
    }

    /** This implementation delegates to {@link Edge#label()}. */
    public Label getLabel(E edge) {
        return edge.label();
    }

    /**
     * This implementation calls {@link #getLine(Edge)} on all edges in
     * {@link #getUserObject()} that are not being filtered by the model
     * according to {@link JModel#isFiltering(Label)}.
     */
    public List<StringBuilder> getLines() {
        List<StringBuilder> result = new ArrayList<StringBuilder>();
        for (E edge : getUserObject()) {
            if (!this.jModel.isFiltering(getLabel(edge))) {
                result.add(getLine(edge));
            }
        }
        return result;
    }

    /**
     * This implementation returns the text from {@link #getLabel(Edge)} wrapped
     * in a StringBuilder.
     */
    public StringBuilder getLine(E edge) {
        return new StringBuilder(getLabel(edge).text());
    }

    /**
     * This implementation calls {@link #getListLabels(Edge)} on all edges in
     * {@link #getUserObject()}.
     */
    public Collection<? extends Label> getListLabels() {
        List<Label> result = new ArrayList<Label>();
        for (E edge : getUserObject()) {
            result.addAll(getListLabels(edge));
        }
        return result;
    }

    /** This implementation delegates to {@link Edge#label()}. */
    public Set<? extends Label> getListLabels(E edge) {
        Set<? extends Label> result;
        Label label = getLabel(edge);
        if (label instanceof RuleLabel
            && ((RuleLabel) label).getRegExpr() != null) {
            result = ((RuleLabel) label).getRegExpr().getTypeLabels();
        } else {
            result = Collections.singleton(label);
        }
        return result;
    }

    /**
     * This implementation calls {@link #getPlainLabel(Edge)} on all edges in
     * {@link #getUserObject()}.
     */
    public Collection<String> getPlainLabels() {
        List<String> result = new ArrayList<String>();
        for (E edge : getUserObject()) {
            result.add(getPlainLabel(edge));
        }
        return result;
    }

    /**
     * This implementation calls {@link TypeLabel#toPrefixedString(Label)}.
     */
    public String getPlainLabel(E edge) {
        return TypeLabel.toPrefixedString(edge.label());
    }

    /** Specialises the return type of the method. */
    @SuppressWarnings("unchecked")
    @Override
    public EdgeContent<E> getUserObject() {
        return (EdgeContent<E>) super.getUserObject();
    }

    @Override
    EdgeContent<E> createUserObject() {
        return new EdgeContent<E>();
    }

    /**
     * This implementation does nothing: setting the user object directly is not
     * the right way to go about it. Instead use <code>{@link #addEdge}</code>
     * and <code>{@link #removeEdge}</code>.
     */
    @Override
    public void setUserObject(Object value) {
        // does nothing
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
    public boolean addEdge(E edge) {
        boolean thisIsRegExpr = getEdge().label() instanceof RuleLabel;
        boolean edgeIsRegExpr = edge.label() instanceof RuleLabel;
        boolean result = (thisIsRegExpr == edgeIsRegExpr);
        if (result) {
            getUserObject().add(edge);
        }
        return true;
    }

    /**
     * Adds an edge to the set underlying graph edges.
     */
    public void removeEdge(E edge) {
        getUserObject().remove(edge);
    }

    @Override
    StringBuilder getEdgeDescription() {
        StringBuilder result = super.getEdgeDescription();
        String sourceIdentity = getSourceVertex().getNodeIdentity();
        if (sourceIdentity != null) {
            result.append(" from ");
            result.append(Converter.ITALIC_TAG.on(sourceIdentity));
        }
        String targetIdentity = getTargetVertex().getNodeIdentity();
        if (targetIdentity != null) {
            result.append(" to ");
            result.append(Converter.ITALIC_TAG.on(targetIdentity));
        }
        return result;
    }

    /** This implementation recognises argument and operation edges. */
    @Override
    StringBuilder getEdgeKindDescription() {
        if (getActualEdge() instanceof ArgumentEdge) {
            return new StringBuilder("Argument edge");
        } else if (getActualEdge() instanceof OperatorEdge) {
            return new StringBuilder("Operation edge");
        } else {
            return new StringBuilder("Edge");
        }
    }

    /**
     * Method to get the role of the edge.
     * @return a string description of the role of the edge
     */
    public String getRole() {
        return "Edge";
    }

    /** Underlying {@link JModel} of this edge. */
    private final GraphJModel<N,E> jModel;
    /** Source node of the underlying graph edges. */
    private final N source;
    /** Target node of the underlying graph edges. */
    private final N target;
}