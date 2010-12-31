/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
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
 * $Id: JModel.java,v 1.22 2008-02-05 13:28:03 rensink Exp $
 */
package groove.gui.jgraph;

import groove.graph.Label;
import groove.gui.Options;
import groove.util.ObservableSet;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.SwingUtilities;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;

/**
 * Extends jgraph's DefaultGraphModel with the following:
 * <ul>
 * <li>A (settable) indicator whether the model has been layed out.
 * <li>An indicator regarding the description text of model nodes.
 * <li>A method for retrieving tool tip text for JGraph cells.
 * <li>A mapping of graph nodes and edges to the model cells representing them.
 * <li>Model vertex and model edge specializations that store graph nodes and
 * edges as user objects and provide easy retrieval.
 * </ul>
 * Instances of JModel are attribute stores.
 * @author Arend Rensink
 * @version $Revision$
 */
abstract public class JModel extends DefaultGraphModel {
    /**
     * Constructs a new JModel, displaying self-edges through JNode labels.
     * @param options the options for the new model
     */
    public JModel(Options options) {
        this.options = options == null ? new Options() : options;
    }

    /** Returns a (possibly <code>null</code>) name of this model. */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the options associated with this object.
     */
    public final Options getOptions() {
        return this.options;
    }

    /**
     * Retrieves the value for a given option from the options object, or
     * <code>null</code> if the options are not set (i.e., <code>null</code>).
     * @param option the name of the option
     */
    public boolean getOptionValue(String option) {
        return getOptions().getItem(option).isEnabled()
            && getOptions().isSelected(option);
    }

    /** Indicates if nodes should determine their own background colour. */
    public boolean isShowBackground() {
        return getOptionValue(Options.SHOW_BACKGROUND_OPTION);
    }

    /**
     * Sets the name of this j-model to a given name. The name may be
     * <tt>null</tt> if the model is anonymous.
     * @see #getName()
     */
    public final void setName(String name) {
        this.name = name;
    }

    /**
     * Indicates whether this graph model has been layed out (by any layouter).
     * @see #setLayedOut
     */
    public boolean isLayedOut() {
        return this.layoutableJCells.isEmpty();
    }

    /**
     * Sets the layed-out property. This method is called after layout has been
     * finished.
     * @param layedOut indication whether the graph has been layed out
     * @ensure <tt>isLayedOut() == layedOut</tt>
     * @see #isLayedOut
     */
    public void setLayedOut(boolean layedOut) {
        if (layedOut) {
            this.layoutableJCells.clear();
        }
    }

    /**
     * Adds a j-cell to the layoutable cells of this j-model. The j-cell is
     * required to be in the model already.
     * @param jCell the cell to be made layoutable
     */
    public void addLayoutable(JCell jCell) {
        assert contains(jCell) : "Cell " + jCell + " is not in model";
        this.layoutableJCells.add(jCell);
    }

    /**
     * Removes a j-cell to the layoutable cells of this j-model. The j-cell is
     * required to be in the model already.
     * @param jCell the cell to be made non-layoutable
     */
    public void removeLayoutable(JCell jCell) {
        assert contains(jCell) : "Cell " + jCell + " is not in model";
        this.layoutableJCells.remove(jCell);
    }

    /** Specialises the type to a list of {@link JCell}s. */
    @Override
    @SuppressWarnings("unchecked")
    public List<? extends JCell> getRoots() {
        return super.getRoots();
    }

    /**
     * Sets all jcells to unmovable, except those that have been added since the
     * last layout action. This is done in preparation for layouting.
     * @return the number of objects left to layout
     */
    public int freeze() {
        int result = 0;
        @SuppressWarnings("unchecked")
        Iterator<DefaultGraphCell> rootsIter = this.roots.iterator();
        while (rootsIter.hasNext()) {
            DefaultGraphCell root = rootsIter.next();
            boolean layoutable = this.layoutableJCells.contains(root);
            GraphConstants.setMoveable(root.getAttributes(), layoutable);
            if (layoutable) {
                result++;
            }
        }
        return result;
    }

    /**
     * Overrides the method so also incident edges of removed nodes are removed.
     */
    @Override
    @SuppressWarnings("unchecked")
    public void remove(Object[] roots) {
        List<Object> removables = new LinkedList<Object>();
        for (Object element : roots) {
            DefaultGraphCell cell = (DefaultGraphCell) element;
            if (cell.getChildCount() > 0) {
                DefaultPort port = (DefaultPort) cell.getChildAt(0);
                removables.addAll(port.getEdges());
            }
        }
        // add the roots to the incident edge list, so the remove is done in one
        // go
        for (Object element : roots) {
            removables.add(element);
        }
        super.remove(removables.toArray());
    }

    /**
     * Notifies the graph model listeners of a change in a set of cells, by
     * firing a graph changed update with a {@link RefreshEdit} over the set.
     * @param jCellSet the set of cells to be refreshed
     * @see org.jgraph.graph.DefaultGraphModel#fireGraphChanged(Object,
     *      org.jgraph.event.GraphModelEvent.GraphModelChange)
     */
    public void refresh(final Collection<? extends JCell> jCellSet) {
        if (!jCellSet.isEmpty()) {
            // do it now if we are on the event dispatch thread
            if (SwingUtilities.isEventDispatchThread()) {
                fireGraphChanged(this, new RefreshEdit(jCellSet));
            } else {
                // otherwise, defer to avoid concurrency problems
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        fireGraphChanged(this, new RefreshEdit(jCellSet));
                    }
                });
            }
        }
    }

    /**
     * Notifies the listeners that something has changed in the model (or in the
     * view of the model).
     */
    public void refresh() {
        refresh(getRoots());
    }

    /**
     * Returns the set of labels that is currently filtered from view. If
     * <code>null</code>, no filtering is going on.
     */
    public final ObservableSet<Label> getFilteredLabels() {
        return this.filteredLabels;
    }

    /**
     * Sets filtering on a given set of labels. Filtered labels will be set to
     * invisible in the {@link JGraph}.
     */
    public final void setFilteredLabels(ObservableSet<Label> filteredLabels) {
        this.filteredLabels = filteredLabels;
    }

    /**
     * Indicates if a given label is currently being filtered from view. This is
     * the case if it is in the set of filtered labels.
     */
    public boolean isFiltering(Label label) {
        return this.filteredLabels != null
            && this.filteredLabels.contains(label);
    }

    /**
     * Changes the grayed-out status of a given set of jgraph cells.
     * @param jCells the cells whose hiding status is to be changed
     * @param grayedOut the new grayed-out status of the cell
     * @see JCell#isGrayedOut()
     */
    public void changeGrayedOut(Set<JCell> jCells, boolean grayedOut) {
        Set<JCell> changedJCells = new HashSet<JCell>();
        for (JCell jCell : jCells) {
            if (jCell.setGrayedOut(grayedOut)) {
                changedJCells.add(jCell);
                if (grayedOut) {
                    // also gray out incident edges
                    if (!isEdge(jCell)) {
                        Iterator<?> jEdgeIter =
                            ((JVertex) jCell).getPort().edges();
                        while (jEdgeIter.hasNext()) {
                            JEdge jEdge = (JEdge) jEdgeIter.next();
                            if (jEdge.setGrayedOut(true)) {
                                changedJCells.add(jEdge);
                            }
                        }
                    }
                } else {
                    // also revive end nodes
                    if (isEdge(jCell)) {
                        JCell sourceJVertex = ((JEdge) jCell).getSourceVertex();
                        if (sourceJVertex.setGrayedOut(false)) {
                            changedJCells.add(sourceJVertex);
                        }
                        JCell targetJVertex = ((JEdge) jCell).getTargetVertex();
                        if (targetJVertex.setGrayedOut(false)) {
                            changedJCells.add(targetJVertex);
                        }
                    }
                }
            }
        }
        refresh(changedJCells);
        createLayerEdit(changedJCells.toArray(), GraphModelLayerEdit.BACK).execute();
    }

    /**
     * Sets the grayed-out cells to a given set.
     * @param jCells the cells to be grayed out
     * @see JCell#isGrayedOut()
     */
    public void setGrayedOut(Set<JCell> jCells) {
        Set<JCell> changedJCells = new HashSet<JCell>();
        // copy the old set of grayed-out cells
        for (JCell root : getRoots()) {
            if (root.setGrayedOut(false)) {
                changedJCells.add(root);
            }
        }
        for (JCell jCell : jCells) {
            if (jCell.setGrayedOut(true)) {
                // the cell should be either added or removed from the changed
                // cells
                if (!changedJCells.add(jCell)) {
                    changedJCells.remove(jCell);
                }
                // also gray out incident edges
                if (jCell instanceof JVertex) {
                    Iterator<?> jEdgeIter = ((JVertex) jCell).getPort().edges();
                    while (jEdgeIter.hasNext()) {
                        JEdge jEdge = (JEdge) jEdgeIter.next();
                        if (jEdge.setGrayedOut(true)) {
                            // the cell should be either added or removed from
                            // the changed cells
                            if (!changedJCells.add(jEdge)) {
                                changedJCells.remove(jEdge);
                            }
                        }
                    }
                }
            }
        }
        refresh(changedJCells);
        createLayerEdit(changedJCells.toArray(), GraphModelLayerEdit.BACK).execute();
    }

    /**
     * Sets the set of emphasised jcells.
     * @param jCellSet the set of jcells to be emphasised. Should not be
     *        <tt>null</tt>.
     */
    public void setEmphasised(Set<JCell> jCellSet) {
        Set<JCell> changedJCells = new HashSet<JCell>();
        for (JCell root : getRoots()) {
            if (root.setEmphasised(jCellSet.contains(root))) {
                changedJCells.add(root);
            }
        }
        refresh(changedJCells);
    }

    /**
     * Clears the currently emphasised nodes.
     */
    public void clearEmphasised() {
        setEmphasised(Collections.<JCell>emptySet());
    }

    /**
     * Invokes {@link JCellContent#clone()} to do the job.
     */
    @Override
    protected Object cloneUserObject(Object userObject) {
        if (userObject == null) {
            return null;
        } else {
            return ((JCellContent<?>) userObject).clone();
        }
    }

    /**
     * Returns the map of attribute changes needed to gray-out a jcell. This
     * implementation returns {@link JAttr#GRAYED_OUT_ATTR}.
     */
    protected AttributeMap getGrayedOutAttr() {
        return JAttr.GRAYED_OUT_ATTR;
    }

    @Override
    public AttributeMap getAttributes(Object node) {
        AttributeMap result;
        if (node instanceof JCell) {
            result = ((JCell) node).getAttributes();
            if (result == null) {
                if (node instanceof JVertex) {
                    result = ((JVertex) node).createAttributes(this);
                } else {
                    result = ((JEdge) node).createAttributes(this);
                }
            }
        } else {
            result = super.getAttributes(node);
        }
        assert result != null : String.format("Cell %s has no attributes", node);
        return result;
    }

    /**
     * Set of j-cells that were inserted in the model since the last time
     * <tt>{@link #setLayedOut(boolean)}</tt> was called.
     */
    protected final Set<JCell> layoutableJCells = new HashSet<JCell>();
    /** Set of options values to control the display. May be <code>null</code>. */
    private final Options options;
    /** Set of labels that is currently filtered from view. */
    private ObservableSet<Label> filteredLabels;
    /**
     * The name of this model.
     */
    private String name;

    /**
     * Special graph model edit that does not signal any actual change but
     * merely passes along a set of cells whose views need to be refreshed due
     * to some hiding or emphasis action.
     * @author Arend Rensink
     * @version $Revision$
     */
    public class RefreshEdit extends GraphModelEdit {
        /**
         * Constructs a new edit based on a given set of jcells.
         * @param refreshedJCells the set of jcells to be refreshed
         */
        public RefreshEdit(Collection<? extends JCell> refreshedJCells) {
            super(null, null, null, null, null);
            this.refreshedJCells = refreshedJCells;
        }

        /**
         * Returns the set of jcells to be refreshed.
         */
        public Collection<? extends JCell> getRefreshedJCells() {
            return this.refreshedJCells;
        }

        @Override
        public Object[] getChanged() {
            if (this.changed == null) {
                this.changed = this.refreshedJCells.toArray();
            }
            return this.changed;
        }

        /** The set of cells that this event reports on refreshing. */
        private final Collection<? extends JCell> refreshedJCells;
    }
}