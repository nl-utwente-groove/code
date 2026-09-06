/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2023
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
 * $Id$
 */
package nl.utwente.groove.gui.view.cell;

import static nl.utwente.groove.util.HTMLConverter.HTML_TAG;
import static nl.utwente.groove.util.HTMLConverter.STRONG_TAG;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.grammar.rule.RuleEdge;
import nl.utwente.groove.grammar.rule.RuleLabel;
import nl.utwente.groove.graph.Edge;
import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.graph.Label;
import nl.utwente.groove.graph.Node;
import nl.utwente.groove.graph.layout.EdgeLayout;
import nl.utwente.groove.gui.look.Look;
import nl.utwente.groove.gui.look.MultiLabel.Direct;
import nl.utwente.groove.gui.look.Values;
import nl.utwente.groove.gui.look.VisualKey;
import nl.utwente.groove.gui.view.AspectViewCell;
import nl.utwente.groove.gui.view.GraphViewModel;
import nl.utwente.groove.gui.view.ViewEdge;
import nl.utwente.groove.util.HTMLConverter;
import nl.utwente.groove.util.Strings;
import nl.utwente.groove.util.parse.FormatError;

/**
 * Backend-independent implementation of an edge cell. The end vertices are set
 * when the edge is connected ({@link #setSource}, {@link #setTarget}); connecting
 * registers the edge in the vertices' context.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public abstract class AViewEdge<G extends Graph> extends AViewCell<G> implements ViewEdge<G> {
    /** Constructs an edge cell for a given view model. */
    protected AViewEdge(GraphViewModel<G> viewModel) {
        super(viewModel);
    }

    @Override
    public void initialise() {
        super.initialise();
        this.sourceNode = null;
        this.targetNode = null;
    }

    /* The clone starts unconnected; the backend reconnects it. */
    @Override
    public AViewEdge<G> clone() {
        AViewEdge<G> result = (AViewEdge<G>) super.clone();
        result.sourceVertex = null;
        result.targetVertex = null;
        return result;
    }

    @Override
    public @Nullable AViewVertex<G> getSourceVertex() {
        return this.sourceVertex;
    }

    @Override
    public @Nullable AViewVertex<G> getTargetVertex() {
        return this.targetVertex;
    }

    /**
     * Connects or disconnects the source end of this edge.
     * @param vertex the new source vertex, or {@code null} to disconnect
     */
    public void setSource(@Nullable AViewVertex<G> vertex) {
        assert this.sourceVertex == null || vertex == null;
        var old = this.sourceVertex;
        if (old != null) {
            old.removeContextEdge(this);
        }
        this.sourceVertex = vertex;
        if (vertex != null) {
            vertex.addContextEdge(this);
        }
        // the visibility of the edge depends on that of its ends
        setStale(VisualKey.VISIBLE);
    }

    /**
     * Connects or disconnects the target end of this edge.
     * @param vertex the new target vertex, or {@code null} to disconnect
     */
    public void setTarget(@Nullable AViewVertex<G> vertex) {
        assert this.targetVertex == null || vertex == null;
        var old = this.targetVertex;
        if (old != null) {
            old.removeContextEdge(this);
        }
        this.targetVertex = vertex;
        if (vertex != null) {
            vertex.addContextEdge(this);
        }
        setStale(VisualKey.VISIBLE);
    }

    /** The source vertex cell; {@code null} while unconnected. */
    private @Nullable AViewVertex<G> sourceVertex;
    /** The target vertex cell; {@code null} while unconnected. */
    private @Nullable AViewVertex<G> targetVertex;

    @Override
    public Node getSourceNode() {
        Node result = this.sourceNode;
        if (result == null) {
            var source = getSourceVertex();
            assert source != null; // method should not be invoked otherwise
            this.sourceNode = result = source.getNode();
        }
        return result;
    }

    /** The source node, cached from the first edge or the source vertex. */
    private @Nullable Node sourceNode;

    @Override
    public Node getTargetNode() {
        Node result = this.targetNode;
        if (result == null) {
            var target = getTargetVertex();
            assert target != null; // method should not be invoked otherwise
            this.targetNode = result = target.getNode();
        }
        return result;
    }

    /** The target node, cached from the first edge or the target vertex. */
    private @Nullable Node targetNode;

    @Override
    public String toString() {
        return String.format("%s wrapping %s", getClass().getSimpleName(), getEdges());
    }

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

    @Override
    public boolean isCompatible(Edge edge) {
        var viewModel = getViewModel();
        if (edge.source() == getSourceNode() && edge.target() == getTargetNode()
            && (viewModel.isMergeAllEdges() || !getLooks().contains(Look.BIDIRECTIONAL))) {
            return true;
        }
        if (edge.source() == getTargetNode() && edge.target() == getSourceNode()) {
            var myEdge = getEdge();
            return viewModel.isMergeBidirectionalEdges() && getEdges().size() == 1
                && myEdge != null && edge.label().equals(myEdge.label())
                || viewModel.isMergeAllEdges();
        }
        return false;
    }

    /** Tests if a given edge has a layout compatible with that of this cell's edge. */
    protected boolean isLayoutCompatible(Edge edge) {
        EdgeLayout edgeLayout = getLayout(edge);
        var myEdge = getEdge();
        EdgeLayout myLayout = myEdge == null
            ? null
            : getLayout(myEdge);
        if (myLayout == null) {
            return edgeLayout == null;
        }
        if (myLayout.equals(edgeLayout)) {
            return true;
        }
        if (myLayout.getPoints().size() == 2
            && (edgeLayout == null || edgeLayout.getPoints().size() == 2)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isLoop() {
        if (this.sourceNode == null && this.targetNode == null) {
            // no graph edge yet: judge by the connection
            return this.sourceVertex != null && this.sourceVertex == this.targetVertex;
        }
        return this.sourceNode == this.targetNode;
    }

    @Override
    public String getToolTipText() {
        return HTML_TAG.on(getEdgeDescription()).toString();
    }

    @Override
    public @Nullable Edge getEdge() {
        return getEdges().isEmpty()
            ? null
            : getEdges().iterator().next();
    }

    @Override
    public Direct getDirect(@Nullable Edge edge) {
        Direct result;
        boolean regular = false;
        if (edge instanceof RuleEdge r) {
            RuleLabel label = r.label();
            regular = label.isEmpty() || label.isNeg(n -> n.getOperand().isEmpty());
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
    public @Nullable Label getKey(Edge edge) {
        return edge.label();
    }

    /** Returns an HTML description of the edge, for the tool tip. */
    protected StringBuilder getEdgeDescription() {
        StringBuilder result = getEdgeKindDescription();
        if (getKeys().size() > 1) {
            HTMLConverter.toUppercase(result, false);
            result.insert(0, "Multiple ");
            result.append("s");
        }
        var source = getSourceVertex();
        String sourceIdentity = source == null
            ? null
            : source.getNodeIdString();
        if (sourceIdentity != null) {
            result.append(" from ");
            result.append(HTMLConverter.ITALIC_TAG.on(sourceIdentity));
        }
        var target = getTargetVertex();
        String targetIdentity = target == null
            ? null
            : target.getNodeIdString();
        if (targetIdentity != null) {
            result.append(" to ");
            result.append(HTMLConverter.ITALIC_TAG.on(targetIdentity));
        }
        if (this instanceof AspectViewCell aspectCell && hasErrors()) {
            HTMLConverter.HTMLTag errorTag
                = HTMLConverter.createColorTag(Values.ERROR_NORMAL_FOREGROUND);
            for (FormatError error : aspectCell.getErrors()) {
                result.append(HTMLConverter.HTML_LINEBREAK);
                result.append(errorTag.on(error));
            }
        }
        return result;
    }

    /** Returns a description of the kind of edge, for the tool tip. */
    protected StringBuilder getEdgeKindDescription() {
        return new StringBuilder("Graph edge");
    }

    /** Returns a description of the labels of this edge, for the tool tip. */
    protected String getLabelDescription() {
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
            result.append(Strings.toString(displayedLabels, ", ", " and "));
        }
        return result.toString();
    }

    @Override
    public Iterator<? extends AViewVertex<G>> getContext() {
        var source = getSourceVertex();
        var target = getTargetVertex();
        assert source != null && target != null; // should not be invoked otherwise
        Collection<AViewVertex<G>> result = isLoop()
            ? Collections.singletonList(source)
            : Arrays.asList(source, target);
        return result.iterator();
    }
}
