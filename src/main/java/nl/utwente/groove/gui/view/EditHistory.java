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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.util.AIGenerated;

/**
 * The undo history of an edited {@link GraphViewModel}: the edits done, the
 * edits undone, and the dirt they leave relative to the last save. An edit is
 * recorded after the view model has applied it; undo and redo ask the view
 * model to revert or apply it again. A gesture that makes several changes
 * records them as one edit by recording inside a {@link #record(Runnable)}.
 * <p>
 * The dirt count is the number of edits the model is removed from its saved
 * state (negative after undoing past the save); the minor-dirt flag holds as
 * long as all of that dirt is minor (layout only, see
 * {@link GraphEdit#isMinor()}), which lets a save skip the grammar reload.
 * @param <G> the type of graph displayed
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
@NonNullByDefault
public class EditHistory<G extends Graph> {
    /** Creates an empty history for a given view model. */
    EditHistory(GraphViewModel<G> viewModel) {
        this.viewModel = viewModel;
    }

    private final GraphViewModel<G> viewModel;

    /**
     * Records an edit that the view model has just applied, discarding the
     * redo stack; a compound recording in progress collects it instead.
     * Empty edits are ignored.
     */
    public void recorded(GraphEdit<G> edit) {
        if (edit.isEmpty()) {
            return;
        }
        var compound = this.compound;
        if (compound != null) {
            compound.add(edit);
            return;
        }
        this.undoStack.push(List.of(edit));
        this.redoStack.clear();
        addDirt(edit.isMinor());
        fireChanged();
    }

    /**
     * Runs an action whose edits are recorded as one compound edit, undone and
     * redone together. Recordings may nest; the outermost one commits.
     */
    public void record(Runnable action) {
        var outer = this.compound;
        List<GraphEdit<G>> compound = outer == null
            ? new ArrayList<>()
            : outer;
        this.compound = compound;
        try {
            action.run();
        } finally {
            this.compound = outer;
        }
        if (outer == null && !compound.isEmpty()) {
            this.undoStack.push(List.copyOf(compound));
            this.redoStack.clear();
            addDirt(compound.stream().allMatch(GraphEdit::isMinor));
            fireChanged();
        }
    }

    /** The edits of the compound recording in progress; {@code null} if there is none. */
    private @Nullable List<GraphEdit<G>> compound;

    /** Indicates if there is an edit to undo. */
    public boolean canUndo() {
        return !this.undoStack.isEmpty();
    }

    /** Indicates if there is an edit to redo. */
    public boolean canRedo() {
        return !this.redoStack.isEmpty();
    }

    /** Reverts the last edit done, if any; a pending insertion is settled first. */
    public void undo() {
        this.viewModel.settlePendingInsertion();
        var edits = this.undoStack.poll();
        if (edits != null) {
            for (int i = edits.size() - 1; i >= 0; i--) {
                this.viewModel.apply(edits.get(i), false);
            }
            this.redoStack.push(edits);
            this.dirtMinor &= edits.stream().allMatch(GraphEdit::isMinor);
            this.dirtCount--;
            fireChanged();
        }
    }

    /** Applies the last edit undone again, if any; a pending insertion is settled first. */
    public void redo() {
        this.viewModel.settlePendingInsertion();
        var edits = this.redoStack.poll();
        if (edits != null) {
            for (var edit : edits) {
                this.viewModel.apply(edit, true);
            }
            this.undoStack.push(edits);
            this.dirtMinor &= edits.stream().allMatch(GraphEdit::isMinor);
            this.dirtCount++;
            fireChanged();
        }
    }

    /** Forgets all edits, keeping the dirt. */
    public void discardAllEdits() {
        this.undoStack.clear();
        this.redoStack.clear();
        fireChanged();
    }

    private final Deque<List<GraphEdit<G>>> undoStack = new ArrayDeque<>();
    private final Deque<List<GraphEdit<G>>> redoStack = new ArrayDeque<>();

    // ---------- dirt ----------

    /** Indicates if the model differs from its saved state. */
    public boolean isDirty() {
        return this.dirtCount != 0;
    }

    /** Indicates if all dirt is minor (layout only). */
    public boolean isDirtMinor() {
        return this.dirtMinor;
    }

    /** Declares the model saved: no dirt. */
    public void setClean() {
        this.dirtCount = 0;
        this.dirtMinor = true;
        fireChanged();
    }

    /**
     * Adds dirt without an edit, as a change outside the history does
     * (the properties panel). Dirt that was negative cannot be undone away
     * any more and so turns positive.
     */
    public void markDirty(boolean minor) {
        addDirt(minor);
        fireChanged();
    }

    private void addDirt(boolean minor) {
        this.dirtCount = Math.abs(this.dirtCount) + 1;
        this.dirtMinor &= minor;
    }

    private int dirtCount;
    private boolean dirtMinor = true;

    // ---------- listeners ----------

    /** Adds a listener, notified after every change of the history or the dirt. */
    public void addListener(Runnable listener) {
        this.listeners.add(listener);
    }

    /** Removes a listener. */
    public void removeListener(Runnable listener) {
        this.listeners.remove(listener);
    }

    private void fireChanged() {
        for (var listener : List.copyOf(this.listeners)) {
            listener.run();
        }
    }

    private final List<Runnable> listeners = new ArrayList<>();
}
