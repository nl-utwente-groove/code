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
import groove.util.Converter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Extends DefaultEdge to store a collection of graph Edges. The graph edges are
 * stored as a Set in the user object. In the latter case, toString() the user
 * object is the empty string.
 */
public class GraphJEdge<N extends Node,E extends Edge<N>> extends JEdge
        implements GraphJCell<N,E> {
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
    GraphJEdge(GraphJModel<N,E> jModel, E edge) {
        super(jModel);
        this.source = edge.source();
        this.target = edge.target();
        getUserObject().add(edge);
    }

    /** Returns the {@link JModel} associated with this {@link JEdge}. */
    @SuppressWarnings("unchecked")
    @Override
    public GraphJModel<N,E> getJModel() {
        return (GraphJModel<N,E>) super.getJModel();
    }

    /**
     * Returns <code>true</code> if the super method does so, and the edge has
     * at least one non-filtered list label, and all end nodes are visible.
     */
    @Override
    public boolean isVisible() {
        boolean result =
            super.isVisible() && !isSourceLabel() && !getLines().isEmpty();
        if (result && !getJModel().isShowUnfilteredEdges()) {
            result =
                getSourceVertex().isVisible()
                    && (isSourceLabel() || getTargetVertex().isVisible());
        }
        return result;
    }

    /**
     * Indicates if this edge is a self-edge that can be shown as a label on its
     * source vertex. This is the case if {@link GraphJModel#isShowVertexLabels()} and
     * {@link GraphJModel#isPotentialUnaryEdge(Edge)} hold for this edge.
     */
    public boolean isSourceLabel() {
        return getJModel().isShowVertexLabels()
            && getJModel().isPotentialUnaryEdge(getEdge());
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
     * This implementation calls {@link #getLine(Edge)} on all edges in
     * {@link #getEdges()} that are not being filtered by the model
     * according to {@link JModel#isFiltering(Label)}.
     */
    public List<StringBuilder> getLines() {
        List<StringBuilder> result = new ArrayList<StringBuilder>();
        for (E edge : getEdges()) {
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
     * This implementation returns the text from {@link #getLabel(Edge)} wrapped
     * in a StringBuilder.
     */
    public StringBuilder getLine(E edge) {
        return new StringBuilder(getLabel(edge).text());
    }

    /**
     * This implementation calls {@link #getListLabels(Edge)} on all edges in
     * {@link #getEdges()}.
     */
    public Collection<? extends Label> getListLabels() {
        List<Label> result = new ArrayList<Label>();
        for (E edge : getEdges()) {
            result.addAll(getListLabels(edge));
        }
        return result;
    }

    /** Returns the listable labels on a given edge. */
    public Set<? extends Label> getListLabels(E edge) {
        return Collections.singleton(getLabel(edge));
    }

    /** This implementation delegates to {@link Edge#label()}. */
    public Label getLabel(E edge) {
        return edge.label();
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
     * the right way to go about it. Instead use <code>{@link #addEdge}.
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
        return getUserObject().add(edge);
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

    /** Source node of the underlying graph edges. */
    private final N source;
    /** Target node of the underlying graph edges. */
    private final N target;
}