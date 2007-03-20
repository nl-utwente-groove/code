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
 * $Id: LabelList.java,v 1.1.1.1 2007-03-20 10:05:29 kastenberg Exp $
 */
package groove.gui;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import groove.gui.jgraph.JCell;
import groove.gui.jgraph.JGraph;
import groove.gui.jgraph.JModel;
import groove.gui.jgraph.JUserObject;
import groove.util.Bag;
import groove.util.TreeBag;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jgraph.event.GraphModelEvent;
import org.jgraph.event.GraphModelListener;
import org.jgraph.graph.GraphConstants;

/**
 * Scroll pane showing the list of labels currently appearing in the graph model.
 * @author Arend Rensink
 * @version $Revision: 1.1.1.1 $
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
            public void mousePressed(MouseEvent evt) {
                maybeShowPopup(evt);
            }

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
        return Collections.unmodifiableSet(labels.elementSet());
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
            addLabels(cell.getLabelSet());
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
        // the changed attributes may mean labels are added or removed
        // apparently we need getAttributes rather than getPreviousAttributes
        Map<?,Map<?,?>> previousAttributes = change.getAttributes();
        if (previousAttributes != null) {
        	for (Map.Entry<?,Map<?,?>> previousAttrEntry: previousAttributes.entrySet()) {
                if (previousAttrEntry.getKey() instanceof JCell) {
                    JCell cell = (JCell) previousAttrEntry.getKey();
                    Map<?,?> cellChange = previousAttrEntry.getValue();
                    JUserObject<?> previousUserObject = (JUserObject) cellChange
                            .get(GraphConstants.VALUE);
                    if (previousUserObject != null) {
                        changed |= removeLabels(previousUserObject.getLabelSet());
                        changed |= addLabels(cell.getLabelSet());
                    }
                }
            }
        }
        // added cells mean added labels
        Object[] addedArray = change.getInserted();
        if (addedArray != null) {
            for (int i = 0; i < addedArray.length; i++) {
                // the cell may be a port, so we have to check for JCell-hood
                if (addedArray[i] instanceof JCell) {
                    JCell cell = (JCell) addedArray[i];
                    changed |= addLabels(cell.getLabelSet());
                }
            }
        }
        // removed cells mean removed labels
        Object[] removedArray = change.getRemoved();
        if (removedArray != null) {
            for (int i = 0; i < removedArray.length; i++) {
                // the cell may be a port, so we have to check for JCell-hood
                if (removedArray[i] instanceof JCell) {
                    JCell cell = (JCell) removedArray[i];
                    changed |= removeLabels(cell.getLabelSet());
                }
            }
        }
        if (changed) {
            updateList();
        }
    }

    /**
     * Emphasizes/deemphasizes cells in the associated jmodel, based on the list selection.
     */
    public void valueChanged(ListSelectionEvent e) {
		Set<JCell> emphSet = new HashSet<JCell>();
		int i = getMinSelectionIndex();
		if (i >= 0) {
			while (i <= getMaxSelectionIndex()) {
				String label = (String) listModel.getElementAt(i);
				if (isSelectedIndex(i)) {
					emphSet.addAll(getJCellsForLabel(label));
				}
				i++;
			}
		}
		jmodel.setEmphasized(emphSet);
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
     * Adds a set of labels to the label set maintained in this list. If <tt>labelSet</tt> is
     * empty, adds {@link #NO_LABEL}instead. Returns <tt>true</tt> if the label element set was
     * changed as a result of this operation.
     */
    protected boolean addLabels(Collection<String> labelSet) {
        if (labelSet.isEmpty()) {
            return labels.add(NO_LABEL);
        } else {
            return labels.addAll(labelSet);
        }
    }

    /**
     * Removes a set of labels from the label set maintained in this list. If <tt>labelSet</tt> is
     * empty, removes {@link #NO_LABEL}instead. Returns <tt>true</tt> if the label element set
     * was changed as a result of this operation.
     */
    protected boolean removeLabels(Collection<String> labelSet) {
        if (labelSet.isEmpty()) {
            return labels.remove(NO_LABEL);
        } else {
            return labels.minus(labelSet);
        }
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
    protected final Bag<String> labels = new TreeBag<String>();

    /**
     * The background color of this component when it is enabled.
     */
    private Color enabledBackground;
}