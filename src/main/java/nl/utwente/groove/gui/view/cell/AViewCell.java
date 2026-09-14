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

import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.graph.Edge;
import nl.utwente.groove.graph.EdgeComparator;
import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.graph.Node;
import nl.utwente.groove.graph.layout.EdgeLayout;
import nl.utwente.groove.graph.layout.NodeLayout;
import nl.utwente.groove.gui.look.Look;
import nl.utwente.groove.gui.look.VisualKey;
import nl.utwente.groove.gui.look.VisualKey.Nature;
import nl.utwente.groove.gui.look.VisualMap;
import nl.utwente.groove.gui.view.GraphCanvas;
import nl.utwente.groove.gui.view.GraphViewController;
import nl.utwente.groove.gui.view.GraphViewModel;
import nl.utwente.groove.gui.view.ViewCell;
import nl.utwente.groove.util.Exceptions;

/**
 * Backend-independent implementation of a view cell: the graph elements it wraps,
 * its looks and the visual map computed from them, and the bookkeeping of which
 * visual values are stale. A cell belongs to a view model, which creates it; a
 * backend shows it through an item of its own (a JGraph cell, a yFiles node or edge),
 * recorded in {@link #getItem()}.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public abstract class AViewCell<G extends Graph> implements ViewCell<G>, Cloneable {
    /** Constructs a cell for a given view model. */
    protected AViewCell(GraphViewModel<G> viewModel) {
        this.viewModel = viewModel;
        this.staleKeys = EnumSet.noneOf(VisualKey.class);
        this.staleKeys.addAll(Arrays.asList(VisualKey.refreshables()));
        this.visuals = new VisualMap();
    }

    @Override
    public GraphViewModel<G> getViewModel() {
        return this.viewModel;
    }

    /**
     * Rebinds this cell to another view model.
     * This happens to a cell pasted into another model than it was copied from.
     */
    public void setViewModel(GraphViewModel<G> viewModel) {
        this.viewModel = viewModel;
    }

    /** The view model this cell belongs to. */
    private GraphViewModel<G> viewModel;

    @Override
    public @Nullable GraphCanvas<G> getCanvas() {
        return getViewModel().getCanvas();
    }

    /** Returns the controller of the view model this cell belongs to. */
    protected GraphViewController<G> getController() {
        return getViewModel().getController();
    }

    /**
     * Returns the backend item showing this cell, if any:
     * an object of the backend's own graph representation.
     */
    public @Nullable Object getItem() {
        return this.item;
    }

    /** Sets the backend item showing this cell. */
    public void setItem(@Nullable Object item) {
        this.item = item;
    }

    /** The backend item showing this cell, if any. */
    private @Nullable Object item;

    /** Returns the stored layout of a given node, if any. */
    final protected @Nullable NodeLayout getLayout(Node node) {
        return getViewModel().getLayoutMap().getLayout(node);
    }

    /** Returns the stored layout of a given edge, if any. */
    final protected @Nullable EdgeLayout getLayout(Edge edge) {
        return getViewModel().getLayoutMap().getLayout(edge);
    }

    @Override
    public void initialise() {
        this.edges = null;
        boolean grayedOut = isGrayedOut();
        this.looks = null;
        if (grayedOut) {
            setGrayedOut(true);
        }
        VisualMap oldVisuals = this.visuals;
        this.visuals = new VisualMap();
        this.visuals.putAll(oldVisuals);
        // the cell may have been cloned, in which case the stale key set is aliased
        this.staleKeys = EnumSet.copyOf(this.staleKeys);
        this.staleKeys.addAll(Arrays.asList(VisualKey.refreshables()));
        this.looksChanged = true;
    }

    @Override
    public void addEdge(Edge edge) {
        // the edge should be compatible, but don't assert this
        // as subclasses may choose to add incompatible edges while flagging an error
        @SuppressWarnings("unchecked")
        Set<Edge> edges = (Set<Edge>) getEdges();
        // there may be an edge already present which is equal (according to equals)
        // but not the same as the new one; the new edge should override the old
        // To achieve this, we first remove the edge
        edges.remove(edge);
        edges.add(edge);
        setStale(VisualKey.LABEL);
        setStale(VisualKey.TEXT_SIZE);
        // Edge may have become bidirectional
        setStale(VisualKey.EDGE_SOURCE_SHAPE);
        setStale(VisualKey.EDGE_TARGET_SHAPE);
    }

    @Override
    public Set<? extends Edge> getEdges() {
        var result = this.edges;
        if (result == null) {
            this.edges = result = createEdgeSet();
        }
        return result;
    }

    /** The graph edges wrapped by this cell. */
    private @Nullable Set<Edge> edges;

    @Override
    public boolean setLook(Look look, boolean set) {
        assert !look.isStructural();
        boolean change = set
            ? getLooks().add(look)
            : getLooks().remove(look);
        if (change) {
            this.looksChanged = true;
        }
        return change;
    }

    @Override
    final public Set<Look> getLooks() {
        var result = this.looks;
        if (result == null) {
            this.looks = result = EnumSet.noneOf(Look.class);
            result.addAll(getStructuralLooks());
            this.looksChanged = true;
        }
        return result;
    }

    /** The looks of this cell; {@code null} until first requested. */
    private @Nullable Set<Look> looks;
    /** Flag indicating that the looks changed since the visuals were last refreshed. */
    private boolean looksChanged;

    /**
     * Returns the structural looks of this cell: those determined by the graph
     * elements it wraps, and hence never changed afterwards.
     */
    protected Set<Look> getStructuralLooks() {
        return EnumSet.of(Look.BASIC);
    }

    @Override
    final public void putVisual(VisualKey key, Object value) {
        assert key.getNature() != Nature.DERIVED;
        this.visuals.put(key, value);
        this.staleKeys.remove(key);
    }

    @Override
    final public void putVisuals(VisualMap map) {
        for (VisualKey key : map.keySet()) {
            if (key.getNature() != Nature.DERIVED) {
                putVisual(key, map.get(key));
            }
        }
    }

    @Override
    final public VisualMap getVisuals() {
        refreshLooks();
        if (!this.staleKeys.isEmpty()) {
            // refresh all
            for (VisualKey key : VisualKey.refreshables()) {
                refreshVisual(key);
            }
        }
        return this.visuals;
    }

    @Override
    public VisualMap getLayoutVisuals() {
        refreshLooks();
        VisualMap result = new VisualMap();
        for (var key : VisualKey.layouts()) {
            refreshVisual(key);
            result.put(key, this.visuals.get(key));
        }
        return result;
    }

    /** Refreshes the derived part of the visual map if the looks changed. */
    private void refreshLooks() {
        if (this.looksChanged || this.looks == null) {
            this.visuals.setLooks(getLooks());
            this.looksChanged = false;
        }
    }

    /** Refreshes a given (refreshable) visual value if it is stale. */
    private void refreshVisual(VisualKey key) {
        key.getRefresher().ifPresent(r -> {
            if (this.staleKeys.remove(key)) {
                this.visuals.put(key, r.get(getController(), this));
            }
        });
    }

    /** The visual map of this cell. */
    private VisualMap visuals;

    @Override
    public void setStale(VisualKey... keys) {
        for (VisualKey key : keys) {
            assert key.getNature() == Nature.REFRESHABLE;
            this.staleKeys.add(key);
        }
    }

    @Override
    public boolean isStale(VisualKey key) {
        return this.staleKeys.contains(key);
    }

    /** The refreshable visual keys whose values are stale. */
    private Set<VisualKey> staleKeys;

    /**
     * Clones this cell: a shallow copy, initialised afresh and without backend item.
     * Subclasses copy the fields that the clone must not share.
     */
    @Override
    public AViewCell<G> clone() {
        try {
            @SuppressWarnings("unchecked")
            AViewCell<G> result = (AViewCell<G>) super.clone();
            result.item = null;
            result.initialise();
            return result;
        } catch (CloneNotSupportedException exc) {
            throw Exceptions.unreachable();
        }
    }

    /** Factory method for the set of graph edges wrapped by this cell. */
    protected <E extends Edge> Set<E> createEdgeSet() {
        return new TreeSet<>(edgeComparator());
    }

    /** Returns the comparator ordering the graph edges wrapped by this cell. */
    protected <E extends Edge> Comparator<E> edgeComparator() {
        return EdgeComparator.instance();
    }
}
