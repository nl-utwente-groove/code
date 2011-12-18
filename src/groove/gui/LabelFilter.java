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
package groove.gui;

import groove.graph.Label;
import groove.graph.TypeElement;
import groove.gui.jgraph.GraphJCell;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Observable;
import java.util.Set;

/**
 * Class that maintains a set of filtered labels
 * (either edge labels or type elements) as well as an inverse
 * mapping of those labels to {@link GraphJCell}s bearing 
 * the labels.
 * @author Arend Rensink
 * @version $Revision $
 */
public class LabelFilter extends Observable {
    /** Clears the inverse mapping from labels to {@link GraphJCell}s. */
    public void clearJCells() {
        for (Set<GraphJCell> jCellSet : this.entryCellMap.values()) {
            jCellSet.clear();
        }
    }

    /** Retrieves the filter entries on a given jCell. */
    public Set<Entry> getEntries(GraphJCell jCell) {
        Set<Entry> result = new HashSet<LabelFilter.Entry>();
        for (Label label : jCell.getListLabels()) {
            result.add(createEntry(label));
        }
        return result;
    }

    /** Adds a {@link GraphJCell} to the inverse mapping. 
     * @return {@code true} if any labels were removed
     */
    public boolean addJCell(GraphJCell jCell) {
        boolean result = false;
        for (Label label : jCell.getListLabels()) {
            result |= addToLabels(jCell, label);
        }
        return result;
    }

    /**
     * Adds a cell-label pair to the label map. If the label does not yet exist
     * in the map, inserts it. The return value indicates if the label had to be
     * created.
     */
    private boolean addToLabels(GraphJCell jCell, Label label) {
        Entry entry = createEntry(label);
        boolean result = addEntry(entry);
        Set<GraphJCell> currentCells = this.entryCellMap.get(entry);
        currentCells.add(jCell);
        return result;
    }

    /** 
     * Removes a {@link GraphJCell} from the inverse mapping.
     * @return {@code true} if any labels were removed
     */
    public boolean removeJCell(GraphJCell jCell) {
        boolean result = false;
        Iterator<Map.Entry<Entry,Set<GraphJCell>>> labelIter =
            this.entryCellMap.entrySet().iterator();
        while (labelIter.hasNext()) {
            Map.Entry<Entry,Set<GraphJCell>> labelEntry = labelIter.next();
            Set<GraphJCell> cellSet = labelEntry.getValue();
            if (cellSet.remove(jCell) && cellSet.isEmpty()) {
                labelIter.remove();
                result = true;
            }
        }
        return result;
    }

    /**
     * Modifies the inverse mapping for a given {@link GraphJCell}.
     * @return {@code true} if any labels were added or removed
     */
    public boolean modifyJCell(GraphJCell jCell) {
        boolean result = false;
        Set<Label> newLabelSet = new HashSet<Label>(jCell.getListLabels());
        // go over the existing label map
        Iterator<Map.Entry<Entry,Set<GraphJCell>>> labelIter =
            this.entryCellMap.entrySet().iterator();
        while (labelIter.hasNext()) {
            Map.Entry<Entry,Set<GraphJCell>> labelEntry = labelIter.next();
            Entry label = labelEntry.getKey();
            Set<GraphJCell> cellSet = labelEntry.getValue();
            if (newLabelSet.remove(label)) {
                // the cell should be in the set
                cellSet.add(jCell);
            } else if (cellSet.remove(jCell) && cellSet.isEmpty()) {
                // the cell was in the set but shouldn't have been,
                // and the set is now empty
                labelIter.remove();
                result = true;
            }
        }
        // any new labels left over were not in the label map; add them
        for (Label label : newLabelSet) {
            addToLabels(jCell, label);
        }
        return result;
    }

    /** Returns the set of {@link GraphJCell}s bearing a given label. */
    public Set<GraphJCell> getJCells(Entry entry) {
        return this.entryCellMap.get(entry);
    }

    /** Indicates if there is at least one {@link GraphJCell} bearing a given label. */
    public boolean hasJCells(Entry entry) {
        Set<GraphJCell> jCells = getJCells(entry);
        return jCells != null && !jCells.isEmpty();
    }

    /** Clears the entire filter. */
    public void clear() {
        this.selected.clear();
        this.entryCellMap.clear();
    }

    /** Adds a label entry to those known in this filter. */
    public boolean addLabel(Label label) {
        return addEntry(createEntry(label));
    }

    /** Adds an entry to those known in this filter. */
    public boolean addEntry(Entry entry) {
        boolean result = false;
        Set<GraphJCell> cells = this.entryCellMap.get(entry);
        if (cells == null) {
            this.entryCellMap.put(entry, new HashSet<GraphJCell>());
            this.selected.add(entry);
            result = true;
        }
        return result;
    }

    /** Returns the set of all labels known to this filter. */
    public Set<Entry> getEntries() {
        return this.entryCellMap.keySet();
    }

    /** 
     * Sets the selection status of a given label, and notifies
     * the observers of the changed {@link GraphJCell}s.
     */
    public void setSelected(Entry label, boolean selected) {
        Set<GraphJCell> changedCells = getSelection(label, selected);
        notifyIfNonempty(changedCells);
    }

    /** 
     * Sets the selection status of a given set of labels, and notifies
     * the observers of the changed {@link GraphJCell}s.
     */
    public void setSelected(Collection<Entry> labels, boolean selected) {
        Set<GraphJCell> changedCells = new HashSet<GraphJCell>();
        for (Entry label : labels) {
            changedCells.addAll(getSelection(label, selected));
        }
        notifyIfNonempty(changedCells);
    }

    /** 
     * Flips the selection status of a given label, and notifies
     * the observers of the changed {@link GraphJCell}s.
     */
    public void changeSelected(Entry label) {
        Set<GraphJCell> changedCells = getSelection(label, !isSelected(label));
        notifyIfNonempty(changedCells);
    }

    /** 
     * Flips the selection status of a given set of labels, and notifies
     * the observers of the changed {@link GraphJCell}s.
     */
    public void changeSelected(Collection<Entry> labels) {
        Set<GraphJCell> changedCells = new HashSet<GraphJCell>();
        for (Entry label : labels) {
            changedCells.addAll(getSelection(label, !isSelected(label)));
        }
        notifyIfNonempty(changedCells);
    }

    /** 
     * Sets the selection status of a given label, and 
     * returns the corresponding set of {@link GraphJCell}s.
     */
    private Set<GraphJCell> getSelection(Entry label, boolean selected) {
        assert this.entryCellMap.containsKey(label) : String.format(
            "Label %s unknown in map %s", label, this.entryCellMap);
        Set<GraphJCell> result = this.entryCellMap.get(label);
        if (selected) {
            this.selected.add(label);
        } else {
            this.selected.remove(label);
        }
        return result == null ? Collections.<GraphJCell>emptySet() : result;
    }

    /** 
     * Notifies the observers of a set of changed cells,
     * if the set is not {@code null} and not empty.
     */
    private void notifyIfNonempty(Set<GraphJCell> changedCells) {
        if (changedCells != null && !changedCells.isEmpty()) {
            setChanged();
            notifyObservers(changedCells);
        }
    }

    /** Indicates if a given label is currently selected. */
    public boolean isSelected(Entry entry) {
        return !this.entryCellMap.containsKey(entry)
            || this.selected.contains(entry);
    }

    /** Constructs a filter entry from a given object. */
    public Entry createEntry(Label label) {
        return new LabelEntry(label);
    }

    /** Set of currently selected (i.e., visible) labels. */
    private final Set<Entry> selected = new HashSet<Entry>();
    /** Mapping from labels to {@link GraphJCell}s bearing that label. */
    private final Map<Entry,Set<GraphJCell>> entryCellMap =
        new HashMap<Entry,Set<GraphJCell>>();

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
            return this.label.hashCode();
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
            LabelEntry other = (LabelEntry) obj;
            return this.label.equals(other.label);
        }

        @Override
        public String toString() {
            return this.label.toString();
        }

        /** The label wrapped in this entry. */
        private final Label label;
    }

    /** Filter entry wrapping a label. */
    public static class TypeEntry implements Entry {
        /** Constructs a fresh label entry from a given label. */
        public TypeEntry(TypeElement element) {
            this.element = element;
        }

        /** Returns the type element wrapped in this entry. */
        public TypeElement getElement() {
            return this.element;
        }

        @Override
        public Label getLabel() {
            return this.element.label();
        }

        @Override
        public int compareTo(Entry o) {
            assert o instanceof LabelEntry;
            return getElement().compareTo(((TypeEntry) o).getElement());
        }

        @Override
        public int hashCode() {
            return this.element.hashCode();
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
            TypeEntry other = (TypeEntry) obj;
            return this.element.equals(other.element);
        }

        @Override
        public String toString() {
            return this.element.toString();
        }

        private final TypeElement element;
    }
}
