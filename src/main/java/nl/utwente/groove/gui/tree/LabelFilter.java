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
package nl.utwente.groove.gui.tree;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.graph.Label;
import nl.utwente.groove.gui.jgraph.JCell;
import nl.utwente.groove.gui.jgraph.JVertex;
import nl.utwente.groove.gui.look.VisualKey;
import nl.utwente.groove.util.Counter;
import nl.utwente.groove.util.Observable;

/**
 * Class that maintains a set of filtered entries
 * (either edge labels or type elements) as well as an inverse
 * mapping of those labels to {@link JCell}s bearing
 * the entries.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
abstract class LabelFilter<G extends Graph,E extends LabelEntry> extends Observable {
    /** Returns the filter entries on a given jCell. */
    public Set<E> getEntries(JCell<G> jCell) {
        Set<E> result = this.jCellEntryMap.get(jCell);
        if (result == null) {
            addJCell(jCell);
            result = this.jCellEntryMap.get(jCell);
            assert result != null; // due to addJCell
        }
        return result;
    }

    /** Computes the filter entries for a given jCell. */
    private Set<E> computeEntries(JCell<G> jCell) {
        Set<E> result = new HashSet<>();
        for (Label key : jCell.getKeys()) {
            result.add(getEntry(key));
        }
        return result;
    }

    /**
     * Adds a {@link JCell} and all corresponding entries to the filter.
     * @return {@code true} if any entries were added
     */
    public boolean addJCell(JCell<G> jCell) {
        boolean result = false;
        if (this.jCellEntryMap.containsKey(jCell)) {
            // a known cell; modify rather than add
            result = modifyJCell(jCell);
        } else {
            // a new cell; add it to the map
            Set<E> entries = computeEntries(jCell);
            this.jCellEntryMap.put(jCell, entries);
            // also modify the inverse map
            for (LabelEntry entry : entries) {
                result |= this.entryDataMap.get(entry).add(jCell);
            }
        }
        return result;
    }

    /**
     * Removes a {@link JCell} from the inverse mapping.
     * @return {@code true} if any entries were removed
     */
    public boolean removeJCell(JCell<G> jCell) {
        boolean result = false;
        Set<E> jCellEntries = this.jCellEntryMap.remove(jCell);
        if (jCellEntries != null) {
            for (LabelEntry jCellEntry : jCellEntries) {
                result |= this.entryDataMap.get(jCellEntry).remove(jCell);
            }
        }
        return result;
    }

    /**
     * Modifies the inverse mapping for a given {@link JCell}.
     * @return {@code true} if any entries were added or removed
     */
    public boolean modifyJCell(JCell<G> jCell) {
        boolean result = false;
        // it may happen that the cell is already removed,
        // for instance when the filter has been reinitialised in the course
        // of an undo operation. In that case, do nothing
        if (this.jCellEntryMap.containsKey(jCell)) {
            Set<E> newEntrySet = computeEntries(jCell);
            Set<E> oldEntrySet = this.jCellEntryMap.put(jCell, newEntrySet);
            assert oldEntrySet != null; // due to containsKey test above
            // remove the obsolete entries
            for (LabelEntry oldEntry : oldEntrySet) {
                if (!newEntrySet.contains(oldEntry)) {
                    result |= this.entryDataMap.get(oldEntry).remove(jCell);
                }
            }
            // add the new entries
            for (LabelEntry newEntry : newEntrySet) {
                if (!oldEntrySet.contains(newEntry)) {
                    result |= this.entryDataMap.get(newEntry).add(jCell);
                }
            }
        }
        return result;
    }

    /** Returns the set of {@link JCell}s for a given entry. */
    public Set<JCell<G>> getJCells(LabelEntry entry) {
        return this.entryDataMap.get(entry).jCells();
    }

    /** Returns the number of instances for a given entry. */
    public int getCount(LabelEntry entry) {
        return this.entryDataMap.get(entry).count().value();
    }

    /** Indicates if there is at least one {@link JCell} with a given entry. */
    public boolean hasJCells(LabelEntry entry) {
        return !getJCells(entry).isEmpty();
    }

    /**
     * Clears the entire filter.
     */
    public void clear() {
        this.jCellEntryMap.clear();
        this.entryDataMap.clear();
    }

    /** Returns the set of all entries known to this filter. */
    public Set<E> getEntries() {
        return this.entryDataMap.keySet();
    }

    /** Lazily creates and returns a filter entry based on a given label key. */
    abstract protected E getEntry(Label key);

    /** Adds a newly created entry to the data structures of this filter.
     * Should be called directly after creation of the entry.
     */
    @SuppressWarnings("null")
    void registerEntry(E entry) {
        var old = this.entryDataMap.put(entry, new EntryData(entry));
        assert old == null : "Duplicate label entry for %s (existing entry contained %s)"
            .formatted(entry, old);
    }

    /** Mapping from entries to {@link JCell}s with that entry. */
    private final Map<E,EntryData> entryDataMap = new HashMap<>();
    /** Inverse mapping of {@link #entryDataMap}. */
    private final Map<JCell<G>,@Nullable Set<E>> jCellEntryMap = new HashMap<>();

    /** Convenience method to return the JCells for a given label.
     * @see #getEntry(Label)
     * @see #getJCells(LabelEntry)
     */
    public Set<JCell<G>> getJCells(Label label) {
        return getJCells(getEntry(label));
    }

    /** Convenience method to test whether there are JCells for a given label.
     * @see #getEntry(Label)
     * @see #getJCells(LabelEntry)
     */
    public boolean hasJCells(Label label) {
        return !getJCells(label).isEmpty();
    }

    /**
     * Sets the selection status of a given label, and notifies
     * the observers of the changed {@link JCell}s.
     */
    public void setSelected(LabelEntry entry, boolean selected) {
        Set<JCell<G>> changedCells = setSelection(entry, selected);
        notifyIfNonempty(changedCells);
    }

    /**
     * Sets the selection status of a given set of labels, and notifies
     * the observers of the changed {@link JCell}s.
     */
    public void setSelected(Collection<LabelEntry> entries, boolean selected) {
        Set<JCell<G>> changedCells = new HashSet<>();
        for (LabelEntry entry : entries) {
            changedCells.addAll(setSelection(entry, selected));
        }
        notifyIfNonempty(changedCells);
    }

    /**
     * Flips the selection status of a given label, and notifies
     * the observers of the changed {@link JCell}s.
     */
    public void changeSelected(LabelEntry entry) {
        Set<JCell<G>> changedCells = setSelection(entry, !entry.isSelected());
        notifyIfNonempty(changedCells);
    }

    /**
     * Flips the selection status of a given set of labels, and notifies
     * the observers of the changed {@link JCell}s.
     */
    public void changeSelected(Collection<LabelEntry> entries) {
        Set<JCell<G>> changedCells = new HashSet<>();
        for (LabelEntry entry : entries) {
            changedCells.addAll(setSelection(entry, !entry.isSelected()));
        }
        notifyIfNonempty(changedCells);
    }

    /**
     * Sets the selection status of a given entry, and
     * returns the set of {@link JCell}s for which this results in a change.
     */
    protected Set<JCell<G>> setSelection(LabelEntry entry, boolean selected) {
        Set<JCell<G>> result = Collections.<JCell<G>>emptySet();
        var data = this.entryDataMap.get(entry);
        assert data != null : String.format("Label %s unknown in map %s", entry, this.entryDataMap);
        if (entry.setSelected(selected)) {
            result = data.jCells();
        }
        return result;
    }

    /**
     * Notifies the observers of a set of changed cells,
     * if the set is not {@code null} and not empty.
     */
    private void notifyIfNonempty(@Nullable Set<JCell<G>> changedCells) {
        if (changedCells != null && !changedCells.isEmpty()) {
            // stale the visibility of the affected cells
            for (JCell<G> jCell : changedCells) {
                jCell.setStale(AFFECTED_KEYS);
                Iterator<? extends JCell<G>> iter = jCell.getContext();
                while (iter.hasNext()) {
                    iter.next().setStale(AFFECTED_KEYS);
                }
            }
            notifyObservers(changedCells);
        }
    }

    /**
     * Indicates if a given jCell is currently visible,
     * according to the entry selection.
     * This is the case if no node type entry is actively filtered, and either unfiltered
     * edges need not be shown or all or all edge entries are also unselected.
     * @param jCell the jCell for which the test is performed
     * @return {@code true} if {@code jCell} is visible
     */
    public boolean isIncluded(JCell<G> jCell) {
        boolean activeShow = false;
        boolean activeHide = false;
        boolean passiveHide = false;
        boolean anyEntry = false;
        boolean isNode = jCell instanceof JVertex;
        for (var entry : getEntries(jCell)) {
            anyEntry = true;
            if (entry.isPassive()) {
                passiveHide |= !entry.isSelected();
            } else if (entry.isSelected()) {
                activeShow = true;
                break;
            } else if (entry.isForNode() == isNode) {
                activeHide = true;
            }
        }
        if (!anyEntry || activeShow) {
            return true;
        } else if (activeHide) {
            return false;
        } else {
            return !passiveHide;
        }
    }

    /** The keys that may change if a filter is (de)selected. */
    private static VisualKey[] AFFECTED_KEYS
        = {VisualKey.VISIBLE, VisualKey.LABEL, VisualKey.NODE_SIZE, VisualKey.TEXT_SIZE};

    /** Record for the data stored for a given entry.
     * We can't use Java records because they are static and therefore don't know the type parameter G
     */
    private class EntryData {
        /**
         * Creates a record for a given entry.
         */
        public EntryData(LabelEntry entry) {
            this.entry = entry;
            this.jCells = new HashSet<>();
            this.count = new Counter();
        }

        private final LabelEntry entry;

        /** Returns the set of cells in this data object. */
        Set<JCell<G>> jCells() {
            return this.jCells;
        }

        private final Set<JCell<G>> jCells;

        /** Returns the number of cells with this entry's label as primary key. */
        Counter count() {
            return this.count;
        }

        private final Counter count;

        /** Updates the record by adding a given cell.
         * @return {@code true} if the data was change by the operation
         */
        boolean add(JCell<G> jCell) {
            boolean result = this.jCells.add(jCell);
            if (result && jCell.getLabels().stream().anyMatch(this.entry::matches)) {
                this.count.increase();
            }
            return result;
        }

        /** Updates the record by removing a given cell.
         * @return {@code true} if the data was change by the operation
         */
        boolean remove(JCell<G> jCell) {
            boolean result = this.jCells.remove(jCell);
            if (result && jCell.getLabels().stream().anyMatch(this.entry::matches)) {
                this.count.decrease();
            }
            return result;
        }
    }
}
