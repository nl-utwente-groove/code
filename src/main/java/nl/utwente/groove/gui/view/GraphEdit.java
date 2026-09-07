/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2026
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
package nl.utwente.groove.gui.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;

import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.gui.look.VisualKey;
import nl.utwente.groove.gui.look.VisualMap;
import nl.utwente.groove.gui.view.CellStore.Connection;
import nl.utwente.groove.util.AIGenerated;

/**
 * One undoable change to the cells of a {@link GraphViewModel}: cells inserted
 * (with the connections of the inserted edges), cells removed (with the
 * connections they had, so that undo re-inserts the same cell objects), visual
 * changes and editable-label changes, each with their old and new values.
 * The edit is a value; the view model applies and reverts it, see
 * {@link GraphViewModel#apply(GraphEdit, boolean)}.
 * @param <G> the type of graph displayed
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
@NonNullByDefault
public final class GraphEdit<G extends Graph> {
    /** Creates an empty edit; the parts are added through the {@code with} methods. */
    public GraphEdit() {
        // empty
    }

    /** Adds inserted cells to this edit. */
    public GraphEdit<G> withInserted(List<? extends ViewVertex<G>> vertices,
                                     List<? extends ViewEdge<G>> edges,
                                     List<Connection<G>> connections) {
        this.insertedVertices.addAll(vertices);
        this.insertedEdges.addAll(edges);
        this.insertedConnections.addAll(connections);
        return this;
    }

    /** Adds removed cells to this edit, with the connections the edges among them had. */
    public GraphEdit<G> withRemoved(List<? extends ViewVertex<G>> vertices,
                                    List<? extends ViewEdge<G>> edges,
                                    List<Connection<G>> connections) {
        this.removedVertices.addAll(vertices);
        this.removedEdges.addAll(edges);
        this.removedConnections.addAll(connections);
        return this;
    }

    /**
     * Adds a visual change to this edit.
     * @param cell the changed cell
     * @param oldVisuals the old values of the changed keys
     * @param newVisuals the new values of the changed keys
     */
    public GraphEdit<G> withVisuals(ViewCell<G> cell, VisualMap oldVisuals, VisualMap newVisuals) {
        this.visualChanges.put(cell, new VisualChange(oldVisuals, newVisuals));
        return this;
    }

    /** Adds an editable-label change to this edit. */
    public GraphEdit<G> withLabels(ViewCell<G> cell, EditableLabels oldLabels,
                                   EditableLabels newLabels) {
        this.labelChanges.put(cell, new LabelChange(oldLabels, newLabels));
        return this;
    }

    /** Returns the inserted vertices. */
    public List<ViewVertex<G>> getInsertedVertices() {
        return Collections.unmodifiableList(this.insertedVertices);
    }

    /** Returns the inserted edges. */
    public List<ViewEdge<G>> getInsertedEdges() {
        return Collections.unmodifiableList(this.insertedEdges);
    }

    /** Returns the connections of the inserted edges. */
    public List<Connection<G>> getInsertedConnections() {
        return Collections.unmodifiableList(this.insertedConnections);
    }

    /** Returns the removed vertices. */
    public List<ViewVertex<G>> getRemovedVertices() {
        return Collections.unmodifiableList(this.removedVertices);
    }

    /** Returns the removed edges. */
    public List<ViewEdge<G>> getRemovedEdges() {
        return Collections.unmodifiableList(this.removedEdges);
    }

    /** Returns the connections the removed edges had. */
    public List<Connection<G>> getRemovedConnections() {
        return Collections.unmodifiableList(this.removedConnections);
    }

    /** Returns the removed cells, edges first. */
    public List<ViewCell<G>> getRemovedCells() {
        List<ViewCell<G>> result = new ArrayList<>(this.removedEdges);
        result.addAll(this.removedVertices);
        return result;
    }

    /** Returns the inserted cells, edges first. */
    public List<ViewCell<G>> getInsertedCells() {
        List<ViewCell<G>> result = new ArrayList<>(this.insertedEdges);
        result.addAll(this.insertedVertices);
        return result;
    }

    /** Returns the visual changes, per cell. */
    public Map<ViewCell<G>,VisualChange> getVisualChanges() {
        return Collections.unmodifiableMap(this.visualChanges);
    }

    /** Returns the editable-label changes, per cell. */
    public Map<ViewCell<G>,LabelChange> getLabelChanges() {
        return Collections.unmodifiableMap(this.labelChanges);
    }

    /** Returns the new or old visuals of the visual changes, as one map. */
    public Map<ViewCell<G>,VisualMap> getVisuals(boolean forward) {
        Map<ViewCell<G>,VisualMap> result = new LinkedHashMap<>();
        for (var entry : this.visualChanges.entrySet()) {
            result.put(entry.getKey(), entry.getValue().get(forward));
        }
        return result;
    }

    /** Indicates if this edit changes nothing. */
    public boolean isEmpty() {
        return !isStructural() && this.visualChanges.isEmpty() && this.labelChanges.isEmpty();
    }

    /** Indicates if this edit inserts or removes cells. */
    public boolean isStructural() {
        return !this.insertedVertices.isEmpty() || !this.insertedEdges.isEmpty()
            || !this.removedVertices.isEmpty() || !this.removedEdges.isEmpty();
    }

    /**
     * Indicates if this edit is minor: it changes only visuals, not the structure
     * or the labels, so that the graph does not need to be rebuilt from the cells.
     */
    public boolean isMinor() {
        return !isStructural() && this.labelChanges.isEmpty();
    }

    /** Indicates if this edit changes visuals of a given key on any cell. */
    public boolean changes(VisualKey key) {
        return this.visualChanges.values().stream().anyMatch(c -> c.newVisuals().containsKey(key));
    }

    @Override
    public String toString() {
        return String
            .format("Edit[+%d vertices, +%d edges, -%d vertices, -%d edges, %d visual, %d label]",
                    this.insertedVertices.size(), this.insertedEdges.size(),
                    this.removedVertices.size(), this.removedEdges.size(),
                    this.visualChanges.size(), this.labelChanges.size());
    }

    private final List<ViewVertex<G>> insertedVertices = new ArrayList<>();
    private final List<ViewEdge<G>> insertedEdges = new ArrayList<>();
    private final List<Connection<G>> insertedConnections = new ArrayList<>();
    private final List<ViewVertex<G>> removedVertices = new ArrayList<>();
    private final List<ViewEdge<G>> removedEdges = new ArrayList<>();
    private final List<Connection<G>> removedConnections = new ArrayList<>();
    private final Map<ViewCell<G>,VisualChange> visualChanges = new LinkedHashMap<>();
    private final Map<ViewCell<G>,LabelChange> labelChanges = new LinkedHashMap<>();

    /** Old and new values of the changed visual keys of one cell. */
    public record VisualChange(VisualMap oldVisuals, VisualMap newVisuals) {
        /** Returns the new visuals if {@code forward} holds, else the old ones. */
        public VisualMap get(boolean forward) {
            return forward
                ? newVisuals()
                : oldVisuals();
        }
    }

    /** Old and new editable labels of one cell. */
    public record LabelChange(EditableLabels oldLabels, EditableLabels newLabels) {
        /** Returns the new labels if {@code forward} holds, else the old ones. */
        public EditableLabels get(boolean forward) {
            return forward
                ? newLabels()
                : oldLabels();
        }
    }
}
