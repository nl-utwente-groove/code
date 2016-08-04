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

import static groove.io.HTMLConverter.HTML_TAG;
import static groove.io.HTMLConverter.STRONG_TAG;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.jgraph.graph.DefaultPort;

import groove.grammar.rule.RuleEdge;
import groove.grammar.rule.RuleLabel;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Label;
import groove.graph.Node;
import groove.gui.layout.JEdgeLayout;
import groove.gui.look.Look;
import groove.gui.look.MultiLabel.Direct;
import groove.gui.look.Values;
import groove.io.HTMLConverter;
import groove.util.Groove;
import groove.util.parse.FormatError;

/**
 * Generic abstract JCell subclass implementing the {@link JEdge} interface.
 * @param <G> the graph type for which the JCell is intended
 * @author rensink
 * @version $Revision $
 */
abstract public class AJEdge<G extends Graph,JG extends JGraph<G>,JM extends JModel<G>,JV extends JVertex<G>>
    extends AJCell<G,JG,JM> implements org.jgraph.graph.Edge, JEdge<G> {
    /**
     * Constructs an uninitialised model edge.
     */
    protected AJEdge() {
        // empty
    }

    @Override
    public void initialise() {
        super.initialise();
        this.sourceNode = null;
        this.targetNode = null;
    }

    /**
     * The cloned object is equal to this one after a reset.
     */
    @Override
    public JEdge<G> clone() {
        @SuppressWarnings("unchecked") AJEdge<G,JG,JM,JV> clone =
            (AJEdge<G,JG,JM,JV>) super.clone();
        clone.initialise();
        return clone;
    }

    /**
     * Returns the source of the edge.
     */
    @Override
    public DefaultPort getSource() {
        return this.sourcePort;
    }

    /**
     * Returns the target of the edge.
     */
    @Override
    public DefaultPort getTarget() {
        return this.targetPort;
    }

    /**
     * Sets the source of the edge.
     */
    @Override
    public void setSource(Object port) {
        assert this.sourcePort == null || port == null;
        this.sourcePort = (DefaultPort) port;
    }

    /**
     * Returns the target of <code>edge</code>.
     */
    @Override
    public void setTarget(Object port) {
        assert this.targetPort == null || port == null;
        this.targetPort = (DefaultPort) port;
    }

    /** Source port of the edge. */
    private DefaultPort sourcePort;
    /** Target port of the edge. */
    private DefaultPort targetPort;

    /**
     * Returns the common source of the underlying graph edges.
     * Should only be involked after initialisation, when the edge source is initialised.
     */
    @Override
    public @NonNull Node getSourceNode() {
        Node result = this.sourceNode;
        if (result == null) {
            @Nullable JV source = getSourceVertex();
            assert source != null; // method should not be invoked otherwise
            this.sourceNode = result = source.getNode();
        }
        return result;
    }

    /** Source node of the underlying graph edges. */
    private Node sourceNode;

    /**
     * Returns the common target of the underlying graph edges.
     */
    @Override
    public @NonNull Node getTargetNode() {
        Node result = this.targetNode;
        if (result == null) {
            @Nullable JV target = getTargetVertex();
            assert target != null; // method should not be invoked otherwise
            this.targetNode = result = target.getNode();
        }
        return result;
    }

    /** Target node of the underlying graph edges. */
    private Node targetNode;

    @Override
    public String toString() {
        return String.format("%s wrapping %s", getClass().getSimpleName(), getEdges());
    }

    /**
     * Adds an edge to the underlying set of edges, if the edge is appropriate.
     * The edge should be compatible, as tested by {@link #isCompatible(Edge)}.
     * Indicates in its return value if the edge has indeed been added.
     * @param edge the edge to be added
     */
    @Override
    public void addEdge(Edge edge) {
        if (getEdges().isEmpty()) {
            this.sourceNode = edge.source();
            this.targetNode = edge.target();
        }
        super.addEdge(edge);
        Direct direct = getDirect(edge);
        if (direct == Direct.NONE) {
            setLook(Look.NO_ARROW, true);
        } else if (direct == Direct.BACKWARD) {
            setLook(Look.BIDIRECTIONAL, true);
        }
    }

    /** Tests if a new edge is compatible with those already wrapped by this JEdge. */
    @Override
    public boolean isCompatible(Edge edge) {
        //        if (!isLayoutCompatible(edge)) {
        //            return false;
        //        }
        if (edge.source() == getSourceNode() && edge.target() == getTargetNode()
            && (getJModel().isMergeAllEdges() || !getLooks().contains(Look.BIDIRECTIONAL))) {
            return true;
        }
        if (edge.source() == getTargetNode() && edge.target() == getSourceNode()) {
            return getJModel().isMergeBidirectionalEdges() && getEdges().size() == 1 && edge.label()
                .equals(getEdge().label()) || getJModel().isMergeAllEdges();
        }
        return false;
    }

    /** Tests if the layout data of a graph edge is compatible with
     * that of this JEdge, so that the edge can be added.
     */
    protected boolean isLayoutCompatible(Edge edge) {
        JEdgeLayout edgeLayout = getLayout(edge);
        JEdgeLayout myLayout = getLayout(getEdge());
        if (myLayout == null) {
            return edgeLayout == null;
        }
        if (myLayout.equals(edgeLayout)) {
            return true;
        }
        if (myLayout.getPoints()
            .size() == 2
            && (edgeLayout == null || edgeLayout.getPoints()
                .size() == 2)) {
            return true;
        }
        return false;
    }

    /** Returns true if source and target node coincide. */
    @Override
    public boolean isLoop() {
        return this.sourceNode == this.targetNode;
    }

    /**
     * Returns the tool tip text for this edge.
     */
    @Override
    public String getToolTipText() {
        return HTML_TAG.on(getEdgeDescription())
            .toString(); // +
        // getLabelDescription());
    }

    /**
     * Returns the first edge from the set of underlying edges.
     */
    @Override
    public Edge getEdge() {
        return getEdges().isEmpty() ? null : getEdges().iterator()
            .next();
    }

    /**
     * Determines the direction corresponding to a given edge
     * wrapped into this JEdge, to be displayed on the JEdge label.
     * This is {@link Direct#NONE} if {@link JGraph#isShowArrowsOnLabels()}
     * is {@code false}, otherwise {@link Direct#BIDIRECTIONAL} if the edge
     * look is {@link Look#BIDIRECTIONAL}; otherwise it is determined
     * by the relative direction of the edge with respect to this JEdge.
     * @param edge the edge of which the direction should be returned; if {@code null},
     * it is assumed to be a forward edge
     */
    @Override
    public Direct getDirect(Edge edge) {
        Direct result;
        boolean regular = false;
        if (edge instanceof RuleEdge) {
            RuleLabel label = ((RuleEdge) edge).label();
            regular = label.isEmpty() || label.isNeg() && label.getNegOperand()
                .isEmpty();
        }
        if (regular) {
            result = Direct.NONE;
        } else if (edge == null || getSourceNode().equals(edge.source())) {
            result = Direct.FORWARD;
        } else {
            result = Direct.BACKWARD;
        }
        return result;
    }

    /**
     * This implementation calls {@link #getKey(Edge)} on all edges in
     * {@link #getEdges()}.
     */
    @Override
    public Collection<? extends Label> getKeys() {
        List<Label> result = new ArrayList<>();
        for (Edge edge : getEdges()) {
            Label entry = getKey(edge);
            if (entry != null) {
                result.add(entry);
            }
        }
        return result;
    }

    @Override
    public Label getKey(Edge edge) {
        return edge.label();
    }

    StringBuilder getEdgeDescription() {
        StringBuilder result = getEdgeKindDescription();
        if (getKeys().size() > 1) {
            HTMLConverter.toUppercase(result, false);
            result.insert(0, "Multiple ");
            result.append("s");
        }
        @Nullable JV source = getSourceVertex();
        String sourceIdentity = source == null ? null : source.getNodeIdString();
        if (sourceIdentity != null) {
            result.append(" from ");
            result.append(HTMLConverter.ITALIC_TAG.on(sourceIdentity));
        }
        @Nullable JV target = getTargetVertex();
        String targetIdentity = target == null ? null : target.getNodeIdString();
        if (targetIdentity != null) {
            result.append(" to ");
            result.append(HTMLConverter.ITALIC_TAG.on(targetIdentity));
        }
        if (hasErrors()) {
            HTMLConverter.HTMLTag errorTag =
                HTMLConverter.createColorTag(Values.ERROR_NORMAL_FOREGROUND);
            for (FormatError error : getErrors()) {
                result.append(HTMLConverter.HTML_LINEBREAK);
                result.append(errorTag.on(error));
            }
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
            result.append(Groove.toString(displayedLabels, "", "", ", ", " and "));
        }
        return result.toString();
    }

    @SuppressWarnings("unchecked")
    @Override
    public JV getSourceVertex() {
        DefaultPort source = getSource();
        return source == null ? null : (JV) source.getParent();
    }

    @SuppressWarnings("unchecked")
    @Override
    public JV getTargetVertex() {
        DefaultPort target = getTarget();
        return target == null ? null : (JV) target.getParent();
    }

    @Override
    public Iterator<JV> getContext() {
        Iterator<JV> result;
        JV source = getSourceVertex();
        JV target = getTargetVertex();
        assert source != null && target != null; // should not be invoked otherwise
        if (isLoop()) {
            return Collections.singletonList(source)
                .iterator();
        } else {
            result = Arrays.asList(source, target)
                .iterator();
        }
        return result;
    }
}
