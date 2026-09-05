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

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.control.instance.Frame;
import nl.utwente.groove.graph.Edge;
import nl.utwente.groove.graph.Label;
import nl.utwente.groove.gui.look.Look;
import nl.utwente.groove.gui.look.VisualKey;
import nl.utwente.groove.gui.view.LTSGraphViewModel;
import nl.utwente.groove.gui.view.LTSViewVertex;
import nl.utwente.groove.lts.GTS;
import nl.utwente.groove.lts.GraphState;
import nl.utwente.groove.lts.StartGraphState;
import nl.utwente.groove.lts.StateProperty;
import nl.utwente.groove.util.HTMLConverter;

/**
 * Vertex cell of an LTS, wrapping a graph state.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public class LTSVertexCell extends AViewVertex<GTS> implements LTSViewVertex {
    /** Constructs a vertex cell for a given view model. */
    public LTSVertexCell(LTSGraphViewModel viewModel) {
        super(viewModel);
    }

    @Override
    public LTSGraphViewModel getViewModel() {
        return (LTSGraphViewModel) super.getViewModel();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Iterator<? extends LTSEdgeCell> getContext() {
        return (Iterator<? extends LTSEdgeCell>) super.getContext();
    }

    @Override
    public GraphState getNode() {
        return (GraphState) super.getNode();
    }

    @Override
    public void initialise() {
        super.initialise();
        this.visibleFlag = true;
        this.outCount = -1;
        GraphState state = getNode();
        setLook(Look.OPEN, !state.isClosed());
        setLook(Look.ABSENT, state.isAbsent());
        setLook(Look.RECIPE, state.isInner());
        setLook(Look.TRANSIENT, state.isTransient());
        setLook(Look.FINAL, state.isFinal());
        setLook(Look.RESULT, isResult());
        setLook(Look.ERROR, state.isError());
    }

    @Override
    public void addEdge(Edge edge) {
        super.addEdge(edge);
        setStale(VisualKey.LABEL);
        setStale(VisualKey.TEXT_SIZE);
        setStale(VisualKey.NODE_SIZE);
    }

    @Override
    public boolean setVisibleFlag(boolean visible) {
        boolean result = this.visibleFlag != visible;
        if (result) {
            this.visibleFlag = visible;
            setStale(VisualKey.VISIBLE);
        }
        return result;
    }

    @Override
    public boolean hasVisibleFlag() {
        return this.visibleFlag;
    }

    /** Flag indicating that this state is set to be visible. */
    private boolean visibleFlag;

    @Override
    public boolean isAllOutVisible() {
        return getNode().isFull() && getOutCount() == getOutVisibleCount();
    }

    /** Returns the number of outgoing transitions of the state, of the shown class. */
    private int getOutCount() {
        if (this.outCount < 0) {
            this.outCount = getNode()
                .getTransitions(getViewModel().getController().getTransitionClass())
                .size();
        }
        return this.outCount;
    }

    /** Cached number of outgoing transitions; negative until computed. */
    private int outCount;

    /** Returns the number of outgoing transitions that are visible. */
    private int getOutVisibleCount() {
        return this.outVisibles + getEdges().size();
    }

    /** Registers a change in the number of visible outgoing transitions. */
    void changeOutVisible(boolean visible, int count) {
        boolean oldAllOutVisible = isAllOutVisible();
        if (visible) {
            this.outVisibles += count;
        } else {
            this.outVisibles -= count;
        }
        if (isAllOutVisible() != oldAllOutVisible) {
            setStale(VisualKey.LABEL);
            setStale(VisualKey.TEXT_SIZE);
            setStale(VisualKey.NODE_SIZE);
        }
    }

    /** The number of visible outgoing transitions not shown as self-loops. */
    private int outVisibles;

    /** Sets the edge cell through which this state was first reached. */
    void setParentEdge(LTSEdgeCell parent) {
        this.parentEdge = parent;
    }

    /**
     * Returns the edge cell through which this state was first reached,
     * or the first incoming non-loop edge cell if none was registered;
     * {@code null} for the start state.
     */
    public @Nullable LTSEdgeCell getParentEdge() {
        LTSEdgeCell result = this.parentEdge;
        if (result == null && !(getNode() instanceof StartGraphState)) {
            Iterator<? extends LTSEdgeCell> iter = getContext();
            while (iter.hasNext()) {
                LTSEdgeCell edge = iter.next();
                if (edge.getTargetVertex() == this && edge.getSourceVertex() != this) {
                    result = edge;
                    break;
                }
            }
        }
        return result;
    }

    /** The edge cell through which this state was first reached, if registered. */
    private @Nullable LTSEdgeCell parentEdge;

    @Override
    protected Collection<? extends Label> getNodeKeys() {
        var result = new ArrayList<Label>();
        getNode().getSatisfiedProps().stream().map(StateProperty::getLabel).forEach(result::add);
        return result;
    }

    @Override
    protected StringBuilder getNodeDescription() {
        StringBuilder result = new StringBuilder("State ");
        result.append(HTMLConverter.UNDERLINE_TAG.on(getNode()));
        Frame frame = getNode().getPrimeFrame();
        if (!frame.isStart()) {
            result.append(" with control state ");
            result.append(HTMLConverter.UNDERLINE_TAG.on(frame));
        }
        return result;
    }

    /** Indicates if the state is part of the current exploration result. */
    public boolean isResult() {
        return getViewModel().getController().isResult(getNode());
    }

    @Override
    public boolean hasErrors() {
        return getNode().isError();
    }

    @Override
    public boolean isStart() {
        GTS gts = getNode().getGTS();
        return gts.startState().equals(getNode());
    }

    @Override
    public boolean isClosed() {
        return getNode().isClosed();
    }

    @Override
    public boolean isFinal() {
        return getNode().isFinal();
    }

    @Override
    public @Nullable String getNodeIdString() {
        String result = super.getNodeIdString();
        Frame frame = getNode().getPrimeFrame();
        if (!frame.isStart()) {
            result += "|" + frame.toString();
        }
        return result;
    }

    /** Indicates if this state is currently active. */
    final boolean isActive() {
        return getLooks().contains(Look.ACTIVE);
    }

    @Override
    public final boolean setActive(boolean active) {
        return setLook(Look.ACTIVE, active);
    }

    @Override
    protected Set<Look> getStructuralLooks() {
        return EnumSet
            .of(isStart()
                ? Look.START
                : Look.STATE);
    }
}
