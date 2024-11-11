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

import org.eclipse.jdt.annotation.NonNull;

import nl.utwente.groove.graph.EdgeRole;
import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.graph.Label;
import nl.utwente.groove.gui.jgraph.JCell;
import nl.utwente.groove.gui.jgraph.JVertex;
import nl.utwente.groove.gui.look.VisualKey;
import nl.utwente.groove.util.Counter;
import nl.utwente.groove.util.Observable;
import nl.utwente.groove.util.line.Line;

/**
 * Class that maintains a set of filtered entries
 * (either edge labels or type elements) as well as an inverse
 * mapping of those labels to {@link JCell}s bearing
 * the entries.
 * @author Arend Rensink
 * @version $Revision$
 */
public class LabelFilter<G extends @NonNull Graph> extends Observable {
    /** Returns the filter entries on a given jCell. */
    public Set<Entry> getEntries(JCell<G> jCell) {
        Set<Entry> result = this.jCellEntryMap.get(jCell);
        if (result == null) {
            addJCell(jCell);
            result = this.jCellEntryMap.get(jCell);
        }
        return result;
    }

    /** Computes the filter entries for a given jCell. */
    private Set<Entry> computeEntries(JCell<G> jCell) {
        Set<Entry> result = new HashSet<>();
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
            Set<Entry> entries = computeEntries(jCell);
            this.jCellEntryMap.put(jCell, entries);
            // also modify the inverse map
            for (Entry entry : entries) {
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
        Set<Entry> jCellEntries = this.jCellEntryMap.remove(jCell);
        if (jCellEntries != null) {
            for (Entry jCellEntry : jCellEntries) {
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
            Set<Entry> newEntrySet = computeEntries(jCell);
            Set<Entry> oldEntrySet = this.jCellEntryMap.put(jCell, newEntrySet);
            // remove the obsolete entries
            for (Entry oldEntry : oldEntrySet) {
                if (!newEntrySet.contains(oldEntry)) {
                    result |= this.entryDataMap.get(oldEntry).remove(jCell);
                }
            }
            // add the new entries
            for (Entry newEntry : newEntrySet) {
                if (!oldEntrySet.contains(newEntry)) {
                    result |= this.entryDataMap.get(newEntry).add(jCell);
                }
            }
        }
        return result;
    }

    /** Returns the set of {@link JCell}s for a given entry. */
    public Set<JCell<G>> getJCells(Entry entry) {
        return this.entryDataMap.get(entry).jCells();
    }

    /** Returns the number of instances for a given entry. */
    public int getCount(Entry entry) {
        return this.entryDataMap.get(entry).count().value();
    }

    /** Indicates if there is at least one {@link JCell} with a given entry. */
    public boolean hasJCells(Entry entry) {
        return !getJCells(entry).isEmpty();
    }

    /**
     * Clears the entire filter.
     */
    public void clear() {
        this.jCellEntryMap.clear();
        this.entryDataMap.clear();
        this.labelEntryMap.clear();
        this.normalMap.clear();
    }

    /** Returns the set of all entries known to this filter. */
    public Set<Entry> getEntries() {
        return this.entryDataMap.keySet();
    }

    /** Lazily creates and returns a filter entry based on a given label key. */
    public Entry getEntry(Label key) {
        LabelEntry result = this.labelEntryMap.get(key);
        if (result == null) {
            result = new LabelEntry(key);
            // normalise the new entry
            if (this.normalMap.containsKey(result)) {
                result = this.normalMap.get(result);
            } else {
                this.normalMap.put(result, result);
                registerEntry(result);
            }
            this.labelEntryMap.put(key, result);
        }
        return result;
    }

    /** Adds a newly created entry to the data structures of this filter.
     * Should be called directly after creation of the entry.
     */
    void registerEntry(Entry entry) {
        var old = this.entryDataMap.put(entry, new EntryData(entry));
        assert old == null : "Duplicate label entry for %s (existing entry contained %s)"
            .formatted(entry, old);
    }

    /** Mapping from entries to {@link JCell}s with that entry. */
    private final Map<Entry,EntryData> entryDataMap = new HashMap<>();
    /** Inverse mapping of {@link #entryDataMap}. */
    private final Map<JCell<G>,Set<Entry>> jCellEntryMap = new HashMap<>();
    /** Mapping from known labels to corresponding label entries. */
    private final Map<Label,LabelEntry> labelEntryMap = new HashMap<>();
    /** Identity mapping for normalised entries. */
    private final Map<LabelEntry,LabelEntry> normalMap = new HashMap<>();

    /** Convenience method to return the JCells for a given label.
     * @see #getEntry(Label)
     * @see #getJCells(Entry)
     */
    public Set<JCell<G>> getJCells(Label label) {
        return getJCells(getEntry(label));
    }

    /** Convenience method to test whether there are JCells for a given label.
     * @see #getEntry(Label)
     * @see #getJCells(Entry)
     */
    public boolean hasJCells(Label label) {
        return !getJCells(label).isEmpty();
    }

    /**
     * Sets the selection status of a given label, and notifies
     * the observers of the changed {@link JCell}s.
     */
    public void setSelected(Entry entry, boolean selected) {
        Set<JCell<G>> changedCells = setSelection(entry, selected);
        notifyIfNonempty(changedCells);
    }

    /**
     * Sets the selection status of a given set of labels, and notifies
     * the observers of the changed {@link JCell}s.
     */
    public void setSelected(Collection<Entry> entries, boolean selected) {
        Set<JCell<G>> changedCells = new HashSet<>();
        for (Entry entry : entries) {
            changedCells.addAll(setSelection(entry, selected));
        }
        notifyIfNonempty(changedCells);
    }

    /**
     * Flips the selection status of a given label, and notifies
     * the observers of the changed {@link JCell}s.
     */
    public void changeSelected(Entry entry) {
        Set<JCell<G>> changedCells = setSelection(entry, !entry.isSelected());
        notifyIfNonempty(changedCells);
    }

    /**
     * Flips the selection status of a given set of labels, and notifies
     * the observers of the changed {@link JCell}s.
     */
    public void changeSelected(Collection<Entry> entries) {
        Set<JCell<G>> changedCells = new HashSet<>();
        for (Entry entry : entries) {
            changedCells.addAll(setSelection(entry, !entry.isSelected()));
        }
        notifyIfNonempty(changedCells);
    }

    /**
     * Sets the selection status of a given entry, and
     * returns the set of {@link JCell}s for which this results in a change.
     */
    protected Set<JCell<G>> setSelection(Entry entry, boolean selected) {
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
    private void notifyIfNonempty(Set<JCell<G>> changedCells) {
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
    class EntryData {
        /**
         * Creates a record for a given entry.
         */
        public EntryData(Entry entry) {
            this.entry = entry;
            this.jCells = new HashSet<>();
            this.count = new Counter();
        }

        private final Entry entry;

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

    /** Type of the keys in a label filter. */
    public static interface Entry extends Comparable<Entry> {
        /** Retrieves the line of the entry. */
        public Line getLine();

        /** Indicates if this entry is currently selected. */
        public boolean isSelected();

        /** Sets the selection status to a given value.
         * The return value indicates if the selection status was changed.
         */
        public boolean setSelected(boolean selected);

        /** Indicates if this entry is passive, i.e., it
         * does not enforce itse selection status on its parent or children.
         */
        public default boolean isPassive() {
            return false;
        }

        /** Signals that this is a filter entry for nodes. */
        public boolean isForNode();

        /** Indicates if a given label corresponds to the one wrapped in this {@link Entry}. */
        public boolean matches(Label label);
    }

    /** Filter entry wrapping a label. */
    public static class LabelEntry implements Entry {
        /** Constructs an initially selected fresh label entry from a given label. */
        public LabelEntry(Label label) {
            this.line = label.toLine();
            this.role = label.getRole();
            this.selected = true;
        }

        @Override
        public Line getLine() {
            return this.line;
        }

        /** The label wrapped in this entry. */
        private final Line line;

        @Override
        public boolean isForNode() {
            return this.role == EdgeRole.NODE_TYPE;
        }

        private final EdgeRole role;

        @Override
        public boolean isSelected() {
            return this.selected;
        }

        @Override
        public boolean setSelected(boolean selected) {
            boolean result = this.selected != selected;
            this.selected = selected;
            return result;
        }

        /** Flag indicating if this entry is currently selected. */
        private boolean selected;

        @Override
        public boolean matches(Label label) {
            return label.getRole() == this.role && label.text().equals(toString());
        }

        @Override
        public int compareTo(Entry o) {
            var other = (LabelEntry) o;
            var result = this.role.compareTo(other.role);
            if (result == 0) {
                result = toString().compareTo(other.toString());
            }
            return result;
        }

        @Override
        public int hashCode() {
            return this.role.hashCode() ^ this.line.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof LabelEntry other)) {
                return false;
            }
            if (this.role != other.role) {
                return false;
            }
            if (!toString().equals(other.toString())) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return this.line.toFlatString();
        }
    }
}
