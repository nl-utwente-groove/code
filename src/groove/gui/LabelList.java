// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente
 
// Licensed under the Apache License, Version 2.0 (the "License"); 
// you may not use this file except in compliance with the License. 
// You may obtain a copy of the License at 
// http://www.apache.org/licenses/LICENSE-2.0 
 
// Unless required by applicable law or agreed to in writing, 
// software distributed under the License is distributed on an 
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
// either express or implied. See the License for the specific 
// language governing permissions and limitations under the License.
/*
 * $Id: LabelList.java,v 1.6 2007-04-12 16:14:52 rensink Exp $
 */
package groove.gui;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import groove.gui.jgraph.JCell;
import groove.gui.jgraph.JGraph;
import groove.gui.jgraph.JModel;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jgraph.event.GraphModelEvent;
import org.jgraph.event.GraphModelListener;

/**
 * Scroll pane showing the list of labels currently appearing in the graph model.
 * @author Arend Rensink
 * @version $Revision: 1.6 $
 */
public class LabelList extends JList implements GraphModelListener, ListSelectionListener {
    /** Pseudo-label maintained in this list for cells with an empty label set. */
    static public final String NO_LABEL = "\u0000";

    /**
     * Constructs a label list associated with a given jgraph. Gets the labels from the model and
     * adds them to this label list.
     * @param jgraph the jgraph with which this list is to be associated
     */
    public LabelList(JGraph jgraph) {
        // initialize the list model
        this.listModel = new DefaultListModel();
        setModel(listModel);
        // change the cell renderer so it adds a space in front of the labels
        setCellRenderer(new DefaultListCellRenderer() {
        	@Override
            public void setText(String text) {
                if (text.equals(NO_LABEL)) {
                    setForeground(specialForeground);
                    super.setText(" " + Options.NO_LABEL_TEXT + " ");
                } else if (text.length() == 0) {
                    super.setText(" " + Options.EMPTY_LABEL_TEXT + " ");
                    setForeground(specialForeground);
                } else {
                    super.setText(" " + text + " ");
                    setForeground(standardForeground);
                }
            }

            private final Color standardForeground = getForeground();

            private final Color specialForeground = Color.LIGHT_GRAY;
        });

        this.jgraph = jgraph;
        this.jmodel = jgraph.getModel();
        this.jmodel.addGraphModelListener(this);

        // take care of the popup menu
        popupMenu = createPopupMenu();
        addMouseListener(new MouseAdapter() {
        	@Override
            public void mousePressed(MouseEvent evt) {
                maybeShowPopup(evt);
            }

        	@Override
            public void mouseReleased(MouseEvent evt) {
                maybeShowPopup(evt);
            }

            private void maybeShowPopup(MouseEvent evt) {
                if (evt.isPopupTrigger()) {
                    popupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
                }
            }
        });
        // add a mouse listener to the jgraph to clear the selction of this list
        // as soon as the mouse is pressed in the jgraph
        jgraph.addMouseListener(new MouseAdapter() {
        	@Override
            public void mousePressed(MouseEvent evt) {
                if (evt.getButton() == MouseEvent.BUTTON1 && !isSelectionEmpty()) {
                    clearSelection();
                }
            }
        });
        setEnabled(false);
    }

    /**
     * Returns the jgraph with which this label list is associated.
     */
    public JGraph getJGraph() {
        return jgraph;
    }

    /**
     * Returns an unmodifiable view on the label set maintained by this label list.
     */
    public Collection<String> getLabels() {
        return Collections.unmodifiableSet(labels.keySet());
    }

    /**
     * Replaces the jmodel on which this label list is based with the (supposedly new) model in the
     * associated jgraph. Gets the labels from the model and adds them to this label list.
     */
    public void updateModel() {
        jmodel.removeGraphModelListener(this);
        jmodel = jgraph.getModel();
        labels.clear();
        jmodel.addGraphModelListener(this);
        for (int i = 0; i < jmodel.getRootCount(); i++) {
            JCell cell = (JCell) jmodel.getRootAt(i);
            if (isListable(cell)) {
            	addToLabels(cell);
            }
        }
        updateList();
        setEnabled(true);
    }

    /**
     * Returns the set of jcells whose label sets contain a given label.
     * @param label the label looked for; may equal {@link #NO_LABEL}.
     * @return the set of {@link JCell}s for which {@link  JCell#getLabelSet()}contains
     *         <tt>label</tt>, or for which it is empty if <tt>label</tt> equals
     *         {@link #NO_LABEL}.
     */
    public Set<JCell> getJCellsForLabel(Object label) {
        Set<JCell> result = new HashSet<JCell>();
        for (int i = 0; i < jmodel.getRootCount(); i++) {
            Object jCell = jmodel.getRootAt(i);
            assert jCell instanceof JCell : "Model cell " + jCell + " of type " + jCell.getClass();
            if (jCell instanceof JCell) {
                Collection<String> jCellLabelSet = ((JCell) jCell).getLabelSet();
                if (label.equals(NO_LABEL) && jCellLabelSet.isEmpty()
                        || jCellLabelSet.contains(label)) {
                    result.add((JCell) jCell);
                }
            }
        }
        return result;
    }

    /**
     * In addition to delegating the method to <tt>super</tt>, sets the background color to
     * <tt>null</tt> when disabled and back to the default when enabled.
     */
	@Override
    public void setEnabled(boolean enabled) {
        if (enabled != isEnabled()) {
            if (!enabled) {
                enabledBackground = getBackground();
                setBackground(null);
            } else if (enabledBackground != null) {
                setBackground(enabledBackground);
            }
        }
        super.setEnabled(enabled);
    }

    /**
     * Updates the label list according to the change event.
     */
    public void graphChanged(GraphModelEvent e) {
        boolean changed = false;
        GraphModelEvent.GraphModelChange change = e.getChange();
		if (change instanceof JModel.RefreshEdit) {
			changed = processRefresh((JModel.RefreshEdit) change, changed);
		} else {
	        changed = processRegularEdit(change, changed);
		}
        if (changed) {
            updateList();
        }
    }

    /**
	 * Records the changes imposed by a graph change that is
	 * not a {@link JModel.RefreshEdit}.
	 */
	private boolean processRegularEdit(GraphModelEvent.GraphModelChange change, boolean changed) {
		Map changeMap = change.getAttributes();
		if (changeMap != null) {
			for (Object changeEntry : changeMap.entrySet()) {
				Object obj = ((Map.Entry) changeEntry).getKey();
				if (isListable(obj)) { //&& attributes.containsKey(GraphConstants.VALUE)) {
					changed |= modifyLabels((JCell) obj);
				}
			}
		}
		// added cells mean added labels
		Object[] addedArray = change.getInserted();
		if (addedArray != null) {
			for (int i = 0; i < addedArray.length; i++) {
				// the cell may be a port, so we have to check for
				// JCell-hood
				if (isListable(addedArray[i])) {
					JCell cell = (JCell) addedArray[i];
					changed |= addToLabels(cell);
				}
			}
		}
		// removed cells mean removed labels
		Object[] removedArray = change.getRemoved();
		if (removedArray != null) {
			for (int i = 0; i < removedArray.length; i++) {
				// the cell may be a port, so we have to check for
				// JCell-hood
				if (isListable(removedArray[i])) {
					JCell cell = (JCell) removedArray[i];
					changed |= removeFromLabels(cell);
				}
			}
		}
		return changed;
	}

	/**
	 * Processes the changes of a {@link JModel.RefreshEdit}.
	 */
	private boolean processRefresh(JModel.RefreshEdit change, boolean changed) {
		for (JCell cell : change.getRefreshedJCells()) {
			if (isListable(cell)) {
				changed |= modifyLabels(cell);
			}
		}
		return changed;
	}

    /** 
	 * Callback method to determine whether a given cell should be included
	 * in the label list. 
	 * This should only be the case if the cell is a {@link JCell} for
	 * which {@link JCell#isListable()} holds.
	 */
	private boolean isListable(Object cell) {
		return cell instanceof JCell && ((JCell) cell).isListable();
	}

	/**
	 * Emphasizes/deemphasizes cells in the associated jmodel, based on the list
	 * selection.
	 */
    public void valueChanged(ListSelectionEvent e) {
//    	if (!valueChangeUnderway) {
//			valueChangeUnderway = true;
			Set<JCell> emphSet = new HashSet<JCell>();
			int i = getMinSelectionIndex();
			if (i >= 0) {
				while (i <= getMaxSelectionIndex()) {
					String label = (String) listModel.getElementAt(i);
					if (isSelectedIndex(i)) {
						emphSet.addAll(labels.get(label));
					}
					i++;
				}
			}
			jmodel.setEmphasized(emphSet);
//			valueChangeUnderway = false;
//		}
	}

    /**
	 * Updates the list from the internally kept label collection.
	 */
    protected void updateList() {
        // temporarily remove this component as selection listener
        removeListSelectionListener(this);
        // clear the selection first
        clearSelection();
        // clear the list
        listModel.clear();
        for (String label: getLabels()) {
            listModel.addElement(label);
        }
        // reinstate this component as selection listener
        addListSelectionListener(this);
    }

    /**
     * Creates a popup menu, consisting of show and hide actions.
     */
    protected JPopupMenu createPopupMenu() {
        return new ShowHideMenu(jgraph).getPopupMenu();
    }

    /**
     * Adds a cell to the label map.
     * This means that for all labels of the cell, the cell is inserted
     * in that label's image.
     * The return value indicates if any labels were added
     */
    protected boolean addToLabels(JCell cell) {
    	boolean result = false;
    	Collection<String> labelSet = cell.getLabelSet();
    	if (labelSet.isEmpty()) {
    		result |= addToLabels(cell, NO_LABEL);
    	} else {
    		for (String label: labelSet) {
    			result |= addToLabels(cell, label);
    		}
    	}
    	return result;
    }
    
    /**
     * Adds a cell-label pair to the label map.
     * If the label does not yet exist in the map, insetrs it.
     * The return value indicates if the label had to be created.
     */
    private boolean addToLabels(JCell cell, String label) {
    	boolean result = false;
    	Set<JCell> currentCells = labels.get(label);
    	if (currentCells == null) {
    		currentCells = new HashSet<JCell>();
    		labels.put(label, currentCells);
    		result = true;
    	}
    	currentCells.add(cell);
    	return result;
    }

    /**
     * Removes a cell from the values of the label map, and removes a label
     * if there are no cells left for it. The return value indicates if there
     * were any labels removed.
     */
    protected boolean removeFromLabels(JCell cell) {
    	boolean result = false;
    	Iterator<Map.Entry<String,Set<JCell>>> labelIter = labels.entrySet().iterator();
    	while (labelIter.hasNext()) {
    		Map.Entry<String,Set<JCell>> labelEntry = labelIter.next();
    		Set<JCell> cellSet = labelEntry.getValue();
    		if (cellSet.remove(cell) && cellSet.isEmpty()) {
    			labelIter.remove();
    			result = true;
    		}
    	}
    	return result;
    }

    /**
     * Modifies the presence of the cell in the label map. 
     * The return value indicates if there
     * were any labels added or removed.
     */
    protected boolean modifyLabels(JCell cell) {
    	boolean result = false;
    	// create the set of all labels for which cell should appear in the label map
    	Set<String> newLabelSet = new HashSet<String>(cell.getLabelSet());
    	if (newLabelSet.isEmpty()) {
    		newLabelSet.add(NO_LABEL);
    	}
    	// go over the existing label map
    	Iterator<Map.Entry<String,Set<JCell>>> labelIter = labels.entrySet().iterator();
    	while (labelIter.hasNext()) {
    		Map.Entry<String,Set<JCell>> labelEntry = labelIter.next();
    		String label = labelEntry.getKey();
    		Set<JCell> cellSet = labelEntry.getValue();
    		if (newLabelSet.remove(label)) {
    			// the cell should be in the set
    			cellSet.add(cell);
    		} else if (cellSet.remove(cell) && cellSet.isEmpty()) {
    			// the cell was in the set but shouldn't have been,
    			// and the set is now empty
    			labelIter.remove();
    			result = true;
    		}
    	}
    	// any new labels left over were not in the label map; add them
    	for (String label: newLabelSet) {
    		Set<JCell> newCells = new HashSet<JCell>();
    		newCells.add(cell);
    		labels.put(label, newCells);
    		result = true;
    	}
    	return result;
    }
    
    /**
     * The list model used for the JList.
     * @require <tt>listModel == listComponent.getModel()</tt>
     */
    protected final DefaultListModel listModel;

    /**
     * The popup menu for this label list.
     * @invariant popupMenu != null
     */
    protected final JPopupMenu popupMenu;
    /**
     * The {@link JGraph}associated to this label list.
     */
    protected final JGraph jgraph;

    /**
     * The {@link JModel}currently being viewed by this label list.
     */
    protected JModel jmodel;

    /**
     * The bag of labels in this jmodel.
     */
    protected final Map<String,Set<JCell>> labels = new TreeMap<String,Set<JCell>>();

    /**
     * The background color of this component when it is enabled.
     */
    private Color enabledBackground;
}