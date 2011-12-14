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
        for (Set<GraphJCell> jCellSet : this.labelCellMap.values()) {
            jCellSet.clear();
        }
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
        boolean result = addLabel(label);
        Set<GraphJCell> currentCells = this.labelCellMap.get(label);
        currentCells.add(jCell);
        return result;
    }

    /** 
     * Removes a {@link GraphJCell} from the inverse mapping.
     * @return {@code true} if any labels were removed
     */
    public boolean removeJCell(GraphJCell jCell) {
        boolean result = false;
        Iterator<Map.Entry<Label,Set<GraphJCell>>> labelIter =
            this.labelCellMap.entrySet().iterator();
        while (labelIter.hasNext()) {
            Map.Entry<Label,Set<GraphJCell>> labelEntry = labelIter.next();
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
        Iterator<Map.Entry<Label,Set<GraphJCell>>> labelIter =
            this.labelCellMap.entrySet().iterator();
        while (labelIter.hasNext()) {
            Map.Entry<Label,Set<GraphJCell>> labelEntry = labelIter.next();
            Label label = labelEntry.getKey();
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
    public Set<GraphJCell> getJCells(Label label) {
        return this.labelCellMap.get(label);
    }

    /** Indicates if there is at least one {@link GraphJCell} bearing a given label. */
    public boolean hasJCells(Label label) {
        Set<GraphJCell> jCells = getJCells(label);
        return jCells != null && !jCells.isEmpty();
    }

    /** Clears the entire filter. */
    public void clear() {
        this.selected.clear();
        this.labelCellMap.clear();
    }

    /** Adds a label to those known in this filter. */
    public boolean addLabel(Label label) {
        boolean result = false;
        Set<GraphJCell> labelledCells = this.labelCellMap.get(label);
        if (labelledCells == null) {
            this.labelCellMap.put(label, new HashSet<GraphJCell>());
            this.selected.add(label);
            result = true;
        }
        return result;
    }

    /** Returns the set of all labels known to this filter. */
    public Set<Label> getLabels() {
        return this.labelCellMap.keySet();
    }

    /** 
     * Sets the selection status of a given label, and notifies
     * the observers of the changed {@link GraphJCell}s.
     */
    public void setSelected(Label label, boolean selected) {
        Set<GraphJCell> changedCells = getSelection(label, selected);
        notifyIfNonempty(changedCells);
    }

    /** 
     * Sets the selection status of a given set of labels, and notifies
     * the observers of the changed {@link GraphJCell}s.
     */
    public void setSelected(Collection<Label> labels, boolean selected) {
        Set<GraphJCell> changedCells = new HashSet<GraphJCell>();
        for (Label label : labels) {
            changedCells.addAll(getSelection(label, selected));
        }
        notifyIfNonempty(changedCells);
    }

    /** 
     * Flips the selection status of a given label, and notifies
     * the observers of the changed {@link GraphJCell}s.
     */
    public void changeSelected(Label label) {
        Set<GraphJCell> changedCells = getSelection(label, !isSelected(label));
        notifyIfNonempty(changedCells);
    }

    /** 
     * Flips the selection status of a given set of labels, and notifies
     * the observers of the changed {@link GraphJCell}s.
     */
    public void changeSelected(Collection<Label> labels) {
        Set<GraphJCell> changedCells = new HashSet<GraphJCell>();
        for (Label label : labels) {
            changedCells.addAll(getSelection(label, !isSelected(label)));
        }
        notifyIfNonempty(changedCells);
    }

    /** 
     * Sets the selection status of a given label, and 
     * returns the corresponding set of {@link GraphJCell}s.
     */
    private Set<GraphJCell> getSelection(Label label, boolean selected) {
        assert this.labelCellMap.containsKey(label) : String.format(
            "Label %s unknown in map %s", label, this.labelCellMap);
        Set<GraphJCell> result = this.labelCellMap.get(label);
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
    public boolean isSelected(Label label) {
        //        assert this.labelCellMap.containsKey(label) : String.format(
        //            "Label %s unknown in map %s", label, this.labelCellMap);
        return !this.labelCellMap.containsKey(label)
            || this.selected.contains(label);
    }

    /** Set of currently selected (i.e., visible) labels. */
    private final Set<Label> selected = new HashSet<Label>();
    /** Mapping from labels to {@link GraphJCell}s bearing that label. */
    private final Map<Label,Set<GraphJCell>> labelCellMap =
        new HashMap<Label,Set<GraphJCell>>();
}
