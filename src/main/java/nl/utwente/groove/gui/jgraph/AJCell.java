/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.gui.jgraph;

import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jdt.annotation.NonNull;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultGraphCell;

import nl.utwente.groove.graph.Edge;
import nl.utwente.groove.graph.EdgeComparator;
import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.graph.Node;
import nl.utwente.groove.gui.layout.JEdgeLayout;
import nl.utwente.groove.gui.layout.JVertexLayout;
import nl.utwente.groove.gui.look.Look;
import nl.utwente.groove.gui.look.VisualKey;
import nl.utwente.groove.gui.look.VisualKey.Nature;
import nl.utwente.groove.gui.look.VisualMap;

/**
 * Abstract JCell implementation, providing some of the basic functionality.
 * @author Arend Rensink
 * @version $Revision$
 */
public abstract class AJCell<G extends @NonNull Graph,JG extends JGraph<G>,JM extends JModel<G>>
    extends DefaultGraphCell implements JCell<G> {
    /**
     * Constructs a new, uninitialised cell.
     * Call {@link #setJModel(JModel)} to initialise to a given model.
     */
    protected AJCell() {
        this.staleKeys = EnumSet.noneOf(VisualKey.class);
        this.staleKeys.addAll(Arrays.asList(VisualKey.refreshables()));
        this.visuals = new VisualMap();
    }

    @SuppressWarnings("unchecked")
    @Override
    public JG getJGraph() {
        // if this is called early, maybe there is no JModel yet
        return getJModel() == null
            ? null
            : (JG) getJModel().getJGraph();
    }

    /** Sets a new JModel for this cell. */
    @Override
    @SuppressWarnings("unchecked")
    public void setJModel(JModel<G> jModel) {
        this.jModel = (JM) jModel;
    }

    @Override
    public JM getJModel() {
        return this.jModel;
    }

    /** The fixed jModel to which this jVertex belongs. */
    private JM jModel;

    /**
     * Returns the (possibly {@code null}) layout information stored for
     * a given graph node.
     */
    final protected JVertexLayout getLayout(Node node) {
        return getJModel().getLayoutMap().getLayout(node);
    }

    /**
     * Returns the (possibly {@code null}) layout information stored for
     * a given graph edge.
     */
    final protected JEdgeLayout getLayout(Edge edge) {
        return getJModel().getLayoutMap().getLayout(edge);
    }

    /** Sets or resets all auxiliary data structures to their initial values. */
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
        if (oldVisuals != null) {
            this.visuals.putAll(oldVisuals);
        }
        // this is necessary because the call may have been cloned, in case the staleKeys
        // set is aliased
        this.staleKeys = EnumSet.copyOf(this.staleKeys);
        this.staleKeys.addAll(Arrays.asList(VisualKey.refreshables()));
        this.looksChanged = true;
        this.errors = null;
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
        if (this.edges == null) {
            this.edges = createEdgeSet();
        }
        return this.edges;
    }

    /** Set of graph edges wrapped by this JCell. */
    private Set<Edge> edges;

    /** Sets or resets a transient look value. */
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
        if (this.looks == null) {
            this.looks = EnumSet.noneOf(Look.class);
            Set<Look> structuralLook = getStructuralLooks();
            this.looks.addAll(structuralLook);
            this.looksChanged = true;
        }
        return this.looks;
    }

    /** The looks of this JCell. */
    private Set<Look> looks;

    /** Flag indicating that {@link #looks} has changed. */
    private boolean looksChanged;

    /** Creator method for the (fixed) structural looks of this JCell. */
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

    /** Refreshes the looks-related visuals. */
    private void refreshLooks() {
        if (this.looksChanged || this.looks == null) {
            // refresh the derived part of the visual map
            this.visuals.setLooks(getLooks());
            this.looksChanged = false;
        }
    }

    /** Refreshes the value for a given key, refreshing it if necessary and possible. */
    private void refreshVisual(VisualKey key) {
        key.getRefresher().ifPresent(r -> {
            if (this.staleKeys.remove(key)) {
                this.visuals.put(key, r.get(getJGraph(), this));
            }
        });
    }

    /** The visual aspects of this JVertex. */
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

    /** The set of (refreshable) visual keys to be refreshed. */
    private Set<VisualKey> staleKeys;

    @Override
    public boolean hasErrors() {
        boolean result = false;
        if (this.errors != null) {
            result = !this.errors.isEmpty();
        }
        return result;
    }

    @Override
    public AspectJCellErrors getErrors() {
        if (this.errors == null) {
            this.errors = new AspectJCellErrors((AspectJCell) this);
        }
        return this.errors;
    }

    /** Object containing this cell's errors, if any. */
    private AspectJCellErrors errors;

    @Override
    public AttributeMap getAttributes() {
        return getVisuals().getAttributes();
    }

    /** Returns a label-sorted set of edges. */
    protected <E extends Edge> Set<E> createEdgeSet() {
        return new TreeSet<>(edgeComparator());
    }

    /** Returns the comparator for the edge set wrapped in this jCell. */
    protected <E extends Edge> Comparator<E> edgeComparator() {
        return EdgeComparator.instance();
    }
}
