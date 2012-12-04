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

import groove.graph.Edge;
import groove.graph.EdgeRole;
import groove.graph.Element;
import groove.graph.Label;
import groove.graph.TypeEdge;
import groove.graph.TypeElement;
import groove.graph.TypeGraph;
import groove.graph.TypeLabel;
import groove.graph.TypeNode;
import groove.gui.jgraph.GraphJCell;
import groove.gui.jgraph.GraphJVertex;
import groove.gui.look.VisualKey;
import groove.view.aspect.AspectEdge;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Observable;
import java.util.Set;

/**
 * Class that maintains a set of filtered entries
 * (either edge labels or type elements) as well as an inverse
 * mapping of those labels to {@link GraphJCell}s bearing 
 * the entries.
 * @author Arend Rensink
 * @version $Revision $
 */
public class LabelFilter extends Observable {
    /** 
     * Indicates if the filter is label- or type-based.
     * @return {@code true} if the filter is label-based; {@code false}
     * if it is type-based.
     */
    public boolean isLabelBased() {
        return this.labelBased;
    }

    /** Clears the inverse mapping from labels to {@link GraphJCell}s. */
    public void clearJCells() {
        for (Set<GraphJCell> jCellSet : this.entryJCellMap.values()) {
            jCellSet.clear();
        }
        this.jCellEntryMap.clear();
    }

    /** Returns the filter entries on a given jCell. */
    public Set<Entry> getEntries(GraphJCell jCell) {
        Set<Entry> result = this.jCellEntryMap.get(jCell);
        if (result == null) {
            addJCell(jCell);
            result = this.jCellEntryMap.get(jCell);
        }
        return result;
    }

    /** Computes the filter entries for a given jCell. */
    private Set<Entry> computeEntries(GraphJCell jCell) {
        Set<Entry> result = new HashSet<LabelFilter.Entry>();
        // we only add a special entry for a node itself
        // if it is not explicitly typed and has no edge self-labels
        Element nodeKey = null;
        for (Element key : jCell.getKeys()) {
            if (isNodeKey(key)) {
                nodeKey = key;
            } else if (key instanceof TypeNode) {
                for (TypeNode superType : ((TypeNode) key).getSupertypes()) {
                    result.add(getEntry(superType));
                }
            } else {
                result.add(getEntry(key));
            }
        }
        if (result.isEmpty() && nodeKey != null) {
            result.add(getEntry(nodeKey));
        }
        return result;
    }

    /** 
     * Tests if a given key stands for a node itself, rather than an
     * explicit label on the node. 
     */
    private boolean isNodeKey(Element key) {
        boolean result;
        if (key instanceof TypeNode) {
            result = ((TypeNode) key).isTopType();
        } else if (key instanceof Edge) {
            result = ((Edge) key).label().equals(TypeLabel.NODE);
        } else {
            result = true;
        }
        return result;
    }

    /**
     * Adds a {@link GraphJCell} and all corresponding entries to the filter.
     * @return {@code true} if any entries were added
     */
    public boolean addJCell(GraphJCell jCell) {
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
     * Removes a {@link GraphJCell} from the inverse mapping.
     * @return {@code true} if any entries were removed
     */
    public boolean removeJCell(GraphJCell jCell) {
        boolean result = false;
        Set<Entry> jCellEntries = this.jCellEntryMap.remove(jCell);
        if (jCellEntries != null) {
            for (Entry jCellEntry : jCellEntries) {
                this.entryJCellMap.get(jCellEntry).remove(jCell);
            }
        }
        return result;
    }

    /**
     * Modifies the inverse mapping for a given {@link GraphJCell}.
     * @return {@code true} if any entries were added or removed
     */
    public boolean modifyJCell(GraphJCell jCell) {
        assert this.jCellEntryMap.containsKey(jCell);
        boolean result = false;
        Set<Entry> newEntrySet = computeEntries(jCell);
        Set<Entry> oldEntrySet = this.jCellEntryMap.put(jCell, newEntrySet);
        // remove the obsolete entries
        for (Entry oldEntry : oldEntrySet) {
            if (!newEntrySet.contains(oldEntry)) {
                this.entryJCellMap.get(oldEntry).remove(jCell);
            }
        }
        // add the new entries
        for (Entry newEntry : newEntrySet) {
            if (!oldEntrySet.contains(newEntry)) {
                result |= addEntry(newEntry);
                this.entryJCellMap.get(newEntry).add(jCell);
            }
        }
        return result;
    }

    /** Returns the set of {@link GraphJCell}s for a given entry. */
    public Set<GraphJCell> getJCells(Entry entry) {
        return this.entryJCellMap.get(entry);
    }

    /** Indicates if there is at least one {@link GraphJCell} with a given entry. */
    public boolean hasJCells(Entry entry) {
        Set<GraphJCell> jCells = getJCells(entry);
        return jCells != null && !jCells.isEmpty();
    }

    /** 
     * Clears the entire filter, and resets it to label- or type-based.
     * @param labelBased if {@code true}, the filter becomes label-based;
     * otherwise it becomes type-based
     */
    public void clear(boolean labelBased) {
        this.selected.clear();
        this.entryJCellMap.clear();
        this.jCellEntryMap.clear();
        this.labelEntryMap.clear();
        this.nodeTypeEntryMap.clear();
        this.edgeTypeEntryMap.clear();
        this.typeGraph = null;
        this.labelBased = labelBased;
    }

    /** Adds an entry to those known in this filter. */
    public boolean addEntry(Element key) {
        return addEntry(getEntry(key));
    }

    /** Adds an entry to those known in this filter. */
    private boolean addEntry(Entry entry) {
        boolean result = false;
        Set<GraphJCell> cells = this.entryJCellMap.get(entry);
        if (cells == null) {
            this.entryJCellMap.put(entry, new HashSet<GraphJCell>());
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
    public void setSelected(Collection<Entry> entries, boolean selected) {
        Set<GraphJCell> changedCells = new HashSet<GraphJCell>();
        for (Entry label : entries) {
            changedCells.addAll(getSelection(label, selected));
        }
        notifyIfNonempty(changedCells);
    }

    /** 
     * Flips the selection status of a given label, and notifies
     * the observers of the changed {@link GraphJCell}s.
     */
    public void changeSelected(Entry entry) {
        Set<GraphJCell> changedCells = getSelection(entry, !isSelected(entry));
        notifyIfNonempty(changedCells);
    }

    /** 
     * Flips the selection status of a given set of labels, and notifies
     * the observers of the changed {@link GraphJCell}s.
     */
    public void changeSelected(Collection<Entry> entries) {
        Set<GraphJCell> changedCells = new HashSet<GraphJCell>();
        for (Entry entry : entries) {
            changedCells.addAll(getSelection(entry, !isSelected(entry)));
        }
        notifyIfNonempty(changedCells);
    }

    /** 
     * Sets the selection status of a given entry, and 
     * returns the corresponding set of {@link GraphJCell}s.
     */
    private Set<GraphJCell> getSelection(Entry entry, boolean selected) {
        assert this.entryJCellMap.containsKey(entry) : String.format(
            "Label %s unknown in map %s", entry, this.entryJCellMap);
        Set<GraphJCell> result = this.entryJCellMap.get(entry);
        if (result == null) {
            result = Collections.<GraphJCell>emptySet();
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
    private void notifyIfNonempty(Set<GraphJCell> changedCells) {
        if (changedCells != null && !changedCells.isEmpty()) {
            // stale the visibility of the affected cells
            for (GraphJCell jCell : changedCells) {
                jCell.setStale(changedKeys);
                for (GraphJCell c : jCell.getContext()) {
                    c.setStale(changedKeys);
                }
            }
            setChanged();
            notifyObservers(changedCells);
        }
    }

    /** Indicates if a given entry is currently selected. */
    public boolean isSelected(Entry entry) {
        return !this.entryJCellMap.containsKey(entry)
            || this.selected.contains(entry);
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
    public boolean isFiltered(GraphJCell jCell, boolean showUnfilteredEdges) {
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
    public Entry getEntry(Element element) {
        Entry result = null;
        if (isLabelBased()) {
            Label key;
            if (element instanceof TypeElement) {
                key = ((TypeElement) element).label();
            } else if (element instanceof AspectEdge) {
                key = ((AspectEdge) element).getDisplayLabel();
            } else if (element instanceof Edge) {
                key = ((Edge) element).label();
            } else {
                key = GraphJVertex.NO_LABEL;
            }
            LabelEntry labelResult = this.labelEntryMap.get(key);
            if (labelResult == null) {
                this.labelEntryMap.put(key, labelResult = createEntry(key));
            }
            result = labelResult;
        } else if (element instanceof TypeNode) {
            TypeElement key = (TypeElement) element;
            TypeLabel keyLabel = ((TypeNode) element).label();
            TypeEntry typeResult = this.nodeTypeEntryMap.get(keyLabel);
            if (typeResult == null) {
                this.nodeTypeEntryMap.put(keyLabel, typeResult =
                    createEntry(key));
            }
            result = typeResult;
        } else if (element instanceof TypeEdge) {
            TypeEdge key = (TypeEdge) element;
            TypeLabel nodeKeyLabel = key.source().label();
            Map<TypeLabel,TypeEntry> entryMap =
                this.edgeTypeEntryMap.get(nodeKeyLabel);
            if (entryMap == null) {
                this.edgeTypeEntryMap.put(nodeKeyLabel, entryMap =
                    new HashMap<TypeLabel,LabelFilter.TypeEntry>());
            }
            TypeLabel edgeKeyLabel = key.label();
            TypeEntry typeResult = entryMap.get(edgeKeyLabel);
            if (typeResult == null) {
                entryMap.put(edgeKeyLabel, typeResult = createEntry(key));
            }
            result = typeResult;
        }
        return result;
    }

    /** Constructs a filter entry from a given object. */
    private LabelEntry createEntry(Label label) {
        assert isLabelBased();
        assert this.typeGraph == null;
        return new LabelEntry(label);
    }

    /** Constructs a filter entry from a given object. */
    private TypeEntry createEntry(TypeElement type) {
        assert !isLabelBased();
        TypeEntry result = new TypeEntry(type);
        assert isTypeGraphConsistent(result);
        return result;
    }

    /** Helper method to check that all type entries are based on the same type graph. */
    private boolean isTypeGraphConsistent(TypeEntry entry) {
        TypeGraph typeGraph = entry.getType().getGraph();
        if (this.typeGraph == null) {
            this.typeGraph = typeGraph;
            return true;
        } else {
            return this.typeGraph == typeGraph;
        }
    }

    /** Set of currently selected (i.e., visible) labels. */
    private final Set<Entry> selected = new HashSet<Entry>();
    /** Mapping from entries to {@link GraphJCell}s with that entry. */
    private final Map<Entry,Set<GraphJCell>> entryJCellMap =
        new HashMap<Entry,Set<GraphJCell>>();
    /** Inverse mapping of {@link #entryJCellMap}. */
    private final Map<GraphJCell,Set<Entry>> jCellEntryMap =
        new HashMap<GraphJCell,Set<Entry>>();
    /** Mapping from known labels to corresponding label entries. */
    private final Map<Label,LabelEntry> labelEntryMap =
        new HashMap<Label,LabelFilter.LabelEntry>();
    /** Mapping from known node type labels to corresponding node type entries. */
    private final Map<TypeLabel,TypeEntry> nodeTypeEntryMap =
        new HashMap<TypeLabel,LabelFilter.TypeEntry>();
    /** Mapping from known node type labels and edge type labels to corresponding edge type entries. */
    private final Map<TypeLabel,Map<TypeLabel,TypeEntry>> edgeTypeEntryMap =
        new HashMap<TypeLabel,Map<TypeLabel,TypeEntry>>();
    /** Flag indicating if the filter is label-based. */
    private boolean labelBased = true;
    /** Field used to test consistency of the type entries. */
    private TypeGraph typeGraph;
    /** The keys that may change if a filter is (de)selected. */
    private static VisualKey[] changedKeys = new VisualKey[] {
        VisualKey.VISIBLE, VisualKey.LABEL};

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
            return this.label.getRole().hashCode()
                ^ this.label.text().hashCode();
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

    /** Filter entry wrapping a label. */
    public static class TypeEntry implements Entry {
        /** Constructs a fresh label entry from a given label. */
        public TypeEntry(TypeElement type) {
            this.type = type;
        }

        /** Returns the type element wrapped in this entry. */
        public TypeElement getType() {
            return this.type;
        }

        @Override
        public Label getLabel() {
            return this.type.label();
        }

        @Override
        public int compareTo(Entry o) {
            TypeEntry other = (TypeEntry) o;
            TypeElement type = getType();
            TypeElement otherType = other.getType();
            if (type instanceof TypeNode) {
                return type.compareTo(otherType);
            }
            if (otherType instanceof TypeNode) {
                return otherType.compareTo(type);
            }
            TypeEdge edge = (TypeEdge) type;
            TypeEdge otherEdge = (TypeEdge) otherType;
            int result =
                edge.source().label().compareTo(otherEdge.source().label());
            if (result == 0) {
                result = edge.label().compareTo(otherEdge.label());
            }
            return result;
        }

        @Override
        public int hashCode() {
            if (this.type instanceof TypeNode) {
                return this.type.hashCode();
            } else {
                TypeEdge edge = (TypeEdge) this.type;
                return edge.source().label().hashCode()
                    ^ edge.label().hashCode();
            }
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof TypeEntry)) {
                return false;
            }
            // test for label equality to avoid 
            // comparing type elements from different type graphs
            TypeEntry other = (TypeEntry) obj;
            if (!this.type.label().equals(other.type.label())) {
                return false;
            }
            if (this.type instanceof TypeNode) {
                return other.type instanceof TypeNode;
            }
            if (other.type instanceof TypeNode) {
                return false;
            }
            TypeEdge edge = (TypeEdge) this.type;
            TypeEdge otherEdge = (TypeEdge) other.type;
            if (!edge.source().label().equals(otherEdge.source().label())) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return this.type.toString();
        }

        private final TypeElement type;
    }
}
