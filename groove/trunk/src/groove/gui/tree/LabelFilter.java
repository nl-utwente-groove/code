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
package groove.gui.tree;

import groove.graph.EdgeRole;
import groove.graph.Graph;
import groove.graph.Label;
import groove.gui.jgraph.JCell;
import groove.gui.look.VisualKey;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Observable;
import java.util.Set;

/**
 * Class that maintains a set of filtered entries
 * (either edge labels or type elements) as well as an inverse
 * mapping of those labels to {@link JCell}s bearing
 * the entries.
 * @author Arend Rensink
 * @version $Revision $
 */
public class LabelFilter<G extends Graph> extends Observable {
    /** Clears the inverse mapping from labels to {@link JCell}s. */
    public void clearJCells() {
        for (Set<JCell<G>> jCellSet : this.entryJCellMap.values()) {
            jCellSet.clear();
        }
        this.jCellEntryMap.clear();
    }

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
                result |= addEntry(entry);
                this.entryJCellMap.get(entry).add(jCell);
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
                result |= this.entryJCellMap.get(jCellEntry).remove(jCell);
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
                    result |= this.entryJCellMap.get(oldEntry).remove(jCell);
                }
            }
            // add the new entries
            for (Entry newEntry : newEntrySet) {
                if (!oldEntrySet.contains(newEntry)) {
                    result |= addEntry(newEntry);
                    this.entryJCellMap.get(newEntry).add(jCell);
                }
            }
        }
        return result;
    }

    /** Returns the set of {@link JCell}s for a given entry. */
    public Set<JCell<G>> getJCells(Entry entry) {
        return this.entryJCellMap.get(entry);
    }

    /** Indicates if there is at least one {@link JCell} with a given entry. */
    public boolean hasJCells(Entry entry) {
        Set<JCell<G>> jCells = getJCells(entry);
        return jCells != null && !jCells.isEmpty();
    }

    /**
     * Clears the entire filter, and resets it to label- or type-based.
     */
    public void clear() {
        this.selected.clear();
        this.entryJCellMap.clear();
        this.jCellEntryMap.clear();
        this.labelEntryMap.clear();
    }

    /** Adds an entry to those known in this filter. */
    public boolean addEntry(Label key) {
        return addEntry(getEntry(key));
    }

    /** Adds an entry to those known in this filter. */
    private boolean addEntry(Entry entry) {
        boolean result = false;
        Set<JCell<G>> cells = this.entryJCellMap.get(entry);
        if (cells == null) {
            this.entryJCellMap.put(entry, new HashSet<JCell<G>>());
            this.selected.add(entry);
            result = true;
        }
        return result;
    }

    /** Returns the set of all entries known to this filter. */
    public Set<Entry> getEntries() {
        return this.entryJCellMap.keySet();
    }

    /**
     * Sets the selection status of a given label, and notifies
     * the observers of the changed {@link JCell}s.
     */
    public void setSelected(Entry label, boolean selected) {
        Set<JCell<G>> changedCells = getSelection(label, selected);
        notifyIfNonempty(changedCells);
    }

    /**
     * Sets the selection status of a given set of labels, and notifies
     * the observers of the changed {@link JCell}s.
     */
    public void setSelected(Collection<Entry> entries, boolean selected) {
        Set<JCell<G>> changedCells = new HashSet<>();
        for (Entry label : entries) {
            changedCells.addAll(getSelection(label, selected));
        }
        notifyIfNonempty(changedCells);
    }

    /**
     * Flips the selection status of a given label, and notifies
     * the observers of the changed {@link JCell}s.
     */
    public void changeSelected(Entry entry) {
        Set<JCell<G>> changedCells = getSelection(entry, !isSelected(entry));
        notifyIfNonempty(changedCells);
    }

    /**
     * Flips the selection status of a given set of labels, and notifies
     * the observers of the changed {@link JCell}s.
     */
    public void changeSelected(Collection<Entry> entries) {
        Set<JCell<G>> changedCells = new HashSet<>();
        for (Entry entry : entries) {
            changedCells.addAll(getSelection(entry, !isSelected(entry)));
        }
        notifyIfNonempty(changedCells);
    }

    /**
     * Sets the selection status of a given entry, and
     * returns the corresponding set of {@link JCell}s.
     */
    private Set<JCell<G>> getSelection(Entry entry, boolean selected) {
        assert this.entryJCellMap.containsKey(entry) : String.format("Label %s unknown in map %s",
            entry, this.entryJCellMap);
        Set<JCell<G>> result = this.entryJCellMap.get(entry);
        if (result == null) {
            result = Collections.<JCell<G>>emptySet();
        } else if (selected) {
            this.selected.add(entry);
        } else {
            this.selected.remove(entry);
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
                jCell.setStale(changedKeys);
                Iterator<? extends JCell<G>> iter = jCell.getContext();
                while (iter.hasNext()) {
                    iter.next().setStale(changedKeys);
                }
            }
            setChanged();
            notifyObservers(changedCells);
        }
    }

    /** Indicates if a given entry is currently selected. */
    public boolean isSelected(Entry entry) {
        return !this.entryJCellMap.containsKey(entry) || this.selected.contains(entry);
    }

    /** Indicates if at least one of a given set of entries is currently selected. */
    public boolean isSelected(Set<Entry> entries) {
        boolean result = false;
        for (Entry entry : entries) {
            if (isSelected(entry)) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * Indicates if a given jCell is currently filtered,
     * according to the entry selection.
     * This is the case if a node type entry is unselected, and either unfiltered
     * edges need not be shown or all or all edge entries are also unselected.
     * @param jCell the jCell for which the test is performed
     * @param showUnfilteredEdges if {@code true}, the jCell is only filtered
     * if all entries are unselected
     * @return {@code true} if {@code jCell} is filtered
     */
    public boolean isFiltered(JCell<G> jCell, boolean showUnfilteredEdges) {
        boolean result;
        boolean hasUnfilteredElements = false;
        boolean hasFilteredNodeTypes = false;
        Set<Entry> entrySet = getEntries(jCell);
        for (Entry entry : entrySet) {
            if (isSelected(entry)) {
                hasUnfilteredElements = true;
            } else {
                // the entry is unselected
                if (entry.getLabel().getRole() == EdgeRole.NODE_TYPE) {
                    hasFilteredNodeTypes = true;
                }
            }
        }
        if (hasFilteredNodeTypes && !showUnfilteredEdges) {
            result = true;
        } else if (hasUnfilteredElements) {
            result = false;
        } else {
            result = !entrySet.isEmpty();
        }
        return result;
    }

    /** Lazily creates and returns a filter entry based on a given element. */
    public Entry getEntry(Label key) {
        LabelEntry result = this.labelEntryMap.get(key);
        if (result == null) {
            this.labelEntryMap.put(key, result = createEntry(key));
        }
        return result;
    }

    /** Constructs a filter entry from a given object. */
    private LabelEntry createEntry(Label label) {
        return new LabelEntry(label);
    }

    /** Set of currently selected (i.e., visible) labels. */
    private final Set<Entry> selected = new HashSet<>();
    /** Mapping from entries to {@link JCell}s with that entry. */
    private final Map<Entry,Set<JCell<G>>> entryJCellMap = new HashMap<>();
    /** Inverse mapping of {@link #entryJCellMap}. */
    private final Map<JCell<G>,Set<Entry>> jCellEntryMap = new HashMap<>();
    /** Mapping from known labels to corresponding label entries. */
    private final Map<Label,LabelEntry> labelEntryMap = new HashMap<>();
    /** The keys that may change if a filter is (de)selected. */
    private static VisualKey[] changedKeys = new VisualKey[] {VisualKey.VISIBLE, VisualKey.LABEL,
        VisualKey.NODE_SIZE, VisualKey.TEXT_SIZE};

    /** Type of the keys in a label filter. */
    public static interface Entry extends Comparable<Entry> {
        /** Retrieves the label of the entry. */
        public Label getLabel();
    }

    /** Filter entry wrapping a label. */
    public static class LabelEntry implements Entry {
        /** Constructs a fresh label entry from a given label. */
        public LabelEntry(Label label) {
            this.label = label;
        }

        @Override
        public Label getLabel() {
            return this.label;
        }

        @Override
        public int compareTo(Entry o) {
            assert o instanceof LabelEntry;
            return getLabel().compareTo(o.getLabel());
        }

        @Override
        public int hashCode() {
            return this.label.getRole().hashCode() ^ this.label.text().hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof LabelEntry)) {
                return false;
            }
            Label otherLabel = ((LabelEntry) obj).getLabel();
            if (getLabel().getRole() != otherLabel.getRole()) {
                return false;
            }
            if (!getLabel().text().equals(otherLabel.text())) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return this.label.toString();
        }

        /** The label wrapped in this entry. */
        private final Label label;
    }
}
