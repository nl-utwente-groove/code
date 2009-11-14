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

import groove.graph.DefaultEdge;
import groove.graph.DefaultGraph;
import groove.graph.DefaultLabel;
import groove.graph.DefaultNode;
import groove.graph.Edge;
import groove.graph.GraphInfo;
import groove.graph.GraphProperties;
import groove.graph.Label;
import groove.graph.Node;
import groove.gui.Options;
import groove.gui.layout.JEdgeLayout;
import groove.gui.layout.LayoutMap;
import groove.util.ObservableSet;

import java.awt.Color;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.SwingUtilities;

import org.jgraph.event.GraphModelEvent;
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
     * Constructs a new JModel with given default node and edge attributes,
     * possibly showing node identities.
     * @param defaultNodeAttr the default node attributes for this model
     * @param defaultEdgeAttr the default node attributes for this model
     */
    public JModel(AttributeMap defaultNodeAttr, AttributeMap defaultEdgeAttr,
            Options options) {
        this.defaultNodeAttr = defaultNodeAttr;
        this.defaultEdgeAttr = defaultEdgeAttr;
        this.options = options;
    }

    /**
     * Constructs a new JModel, displaying self-edges through JNode labels. The
     * default node and edge identities are set through
     * {@link JAttr#DEFAULT_NODE_ATTR} and {@link JAttr#DEFAULT_EDGE_ATTR}.
     * @ensure !isLayedOut(), !isShowNodeIdentities()
     * @param options the options for the new model
     */
    public JModel(Options options) {
        this(JAttr.DEFAULT_NODE_ATTR, JAttr.DEFAULT_EDGE_ATTR, options);
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
     * Sets the name of this j-model to a given name. The name may be
     * <tt>null</tt> if the model is anonymous.
     * @see #getName()
     */
    public final void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the properties associated with this j-model.
     */
    public final GraphProperties getProperties() {
        if (this.properties == null) {
            this.properties = new GraphProperties();
        }
        return this.properties;
    }

    /**
     * Sets the properties of this j-model to a given properties map.
     */
    public final void setProperties(GraphProperties properties) {
        this.properties = properties;
    }

    /**
     * Returns a tool tip text for a given graph cell. Does not test if the cell
     * is hidden.
     * @param jCell the graph cell (of this graph model) for which a tool tip is
     *        to be returned
     * @require cell != null
     */
    public String getToolTipText(JCell jCell) {
        if (jCell != null && jCell.isVisible()) {
            return jCell.getToolTipText();
        } else {
            return null;
        }
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
     * Callback method to determine whether a cell should in principle be
     * moveable.
     */
    public boolean isMoveable(JCell jCell) {
        return true;
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
     * Converts this j-model to a plain groove graph. Layout information is also
     * transferred. A plain graph is one in which the nodes and edges are
     * {@link DefaultNode}s and {@link DefaultEdge}s, and all further
     * information is in the labels.
     */
    public groove.graph.Graph toPlainGraph() {
        groove.graph.Graph result = new DefaultGraph();
        LayoutMap<Node,Edge> layoutMap = new LayoutMap<Node,Edge>();
        Map<JVertex,Node> nodeMap = new HashMap<JVertex,Node>();

        // Create nodes
        for (Object root : getRoots()) {
            if (root instanceof JVertex) {
                Node node = addFreshNode(result, ((JVertex) root));
                nodeMap.put((JVertex) root, node);
                layoutMap.putNode(node, ((JVertex) root).getAttributes());
                for (String label : ((JVertex) root).getPlainLabels()) {
                    result.addEdge(node, DefaultLabel.createLabel(label), node);
                }
            }
        }

        // Create Edges
        for (Object root : getRoots()) {
            if (root instanceof JEdge) {
                JEdge jEdge = (JEdge) root;
                Node source = nodeMap.get(jEdge.getSourceVertex());
                Node target = nodeMap.get(jEdge.getTargetVertex());
                assert target != null : "Edge with empty target: " + root;
                assert source != null : "Edge with empty source: " + root;
                AttributeMap edgeAttr = jEdge.getAttributes();
                // test if the edge attributes are default
                boolean attrIsDefault =
                    JEdgeLayout.newInstance(edgeAttr).isDefault();
                // parse edge text into label set
                for (String label : jEdge.getPlainLabels()) {
                    Edge edge =
                        result.addEdge(source, DefaultLabel.createLabel(label),
                            target);
                    // add layout information if there is anything to be noted
                    // about the edge
                    if (!attrIsDefault) {
                        layoutMap.putEdge(edge, edgeAttr);
                    }
                }
            }
        }
        GraphInfo.setLayoutMap(result, layoutMap);
        GraphInfo.setProperties(result, getProperties());
        GraphInfo.setName(result, getName());
        return result;
    }

    /**
     * Callback factory method to add a fresh node to a given graph, reflecting
     * a JVertex. Subclasses may use this to determine the node number.
     */
    protected Node addFreshNode(groove.graph.Graph result, JVertex root) {
        return result.addNode();
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
    public void refresh(final Collection<JCell> jCellSet) {
        if (!jCellSet.isEmpty()) {
            // do it now if we are on the event dispatch thread
            if (SwingUtilities.isEventDispatchThread()) {
                fireGraphChanged(this, new RefreshEdit(jCellSet));
            } else {
                // otherwise, defer to avoid concurrency problems
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        fireGraphChanged(new RefreshEdit(jCellSet));
                    }
                });
            }
        }
    }

    /**
     * Notifies the listeners that something has changed in the model (or in the
     * view of the model).
     */
    @SuppressWarnings("unchecked")
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
        // if (this.filteredLabels != null) {
        // this.filteredLabels.deleteObserver(getRefreshListener());
        // }
        this.filteredLabels = filteredLabels;
        // if (filteredLabels != null) {
        // filteredLabels.addObserver(getRefreshListener());
        // }
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
     * Tests the grayed-out status of a given jgraph cell.
     * @param cell the cell that is to be tested
     * @return <tt>true</tt> if the cell is currently grayed-out
     * @see #changeGrayedOut(Set,boolean)
     */
    public boolean isGrayedOut(JCell cell) {
        return this.grayedOutJCells.contains(cell);
    }

    /**
     * Changes the grayed-out status of a given set of jgraph cells.
     * @param jCells the cells whose hiding status is to be changed
     * @param grayedOut the new grayed-out status of the cell
     * @see #isGrayedOut(JCell)
     */
    public void changeGrayedOut(Set<JCell> jCells, boolean grayedOut) {
        Set<JCell> changedJCells = new HashSet<JCell>();
        for (JCell jCell : jCells) {
            if (grayedOut != isGrayedOut(jCell)) {
                if (grayedOut) {
                    this.grayedOutJCells.add(jCell);
                    changedJCells.add(jCell);
                    // also gray out incident edges
                    if (!isEdge(jCell)) {
                        Iterator<?> jEdgeIter =
                            ((JVertex) jCell).getPort().edges();
                        while (jEdgeIter.hasNext()) {
                            JEdge jEdge = (JEdge) jEdgeIter.next();
                            if (this.grayedOutJCells.add(jEdge)) {
                                changedJCells.add(jEdge);
                            }
                        }
                    }
                } else {
                    this.grayedOutJCells.remove(jCell);
                    changedJCells.add(jCell);
                    // also revive end nodes
                    if (isEdge(jCell)) {
                        JCell sourceJVertex = ((JEdge) jCell).getSourceVertex();
                        if (this.grayedOutJCells.remove(sourceJVertex)) {
                            changedJCells.add(sourceJVertex);
                        }
                        JCell targetJVertex = ((JEdge) jCell).getTargetVertex();
                        if (this.grayedOutJCells.remove(targetJVertex)) {
                            changedJCells.add(targetJVertex);
                        }
                    }
                }
            }
        }
        refresh(changedJCells);
        createLayerEdit(this.grayedOutJCells.toArray(),
            GraphModelLayerEdit.BACK).execute();
    }

    /**
     * Sets the grayed-out cells to a given set.
     * @param jCells the cells to be grayed out
     * @see #isGrayedOut(JCell)
     */
    public void setGrayedOut(Set<JCell> jCells) {
        // copy the old set of grayed-out cells
        Set<JCell> changedJCells = new HashSet<JCell>(this.grayedOutJCells);
        this.grayedOutJCells.clear();
        for (JCell jCell : jCells) {
            if (this.grayedOutJCells.add(jCell)) {
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
                        if (this.grayedOutJCells.add(jEdge)) {
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
        createLayerEdit(this.grayedOutJCells.toArray(),
            GraphModelLayerEdit.BACK).execute();
    }

    /**
     * Returns the number of grayed-out cells.
     */
    public Set<JCell> getGrayedOut() {
        return this.grayedOutJCells;
    }

    /**
     * Tests if a given jcell is currently emphasized. Note that emphasis may
     * also exist for hidden cells, even if this is not visible.
     * 
     * @param jCell the jcell that is tested for emphasis
     * @return <tt>true</tt> if <tt>jcell</tt> is currently emphasized
     */
    public boolean isEmphasized(JCell jCell) {
        return this.emphJCells.contains(jCell);
    }

    /**
     * Sets the set of emphasised jcells.
     * @param jCellSet the set of jcells to be emphasised. Should not be
     *        <tt>null</tt>.
     */
    public void setEmphasized(Set<JCell> jCellSet) {
        Set<JCell> changedEmphJCells = new HashSet<JCell>(this.emphJCells);
        changedEmphJCells.addAll(jCellSet);
        this.emphJCells.clear();
        this.emphJCells.addAll(jCellSet);
        refresh(changedEmphJCells);
    }

    /**
     * Clears the currently emphasised nodes.
     */
    public void clearEmphasized() {
        Set<JCell> oldEmphSet = new HashSet<JCell>(this.emphJCells);
        this.emphJCells.clear();
        refresh(oldEmphSet);
    }

    /**
     * Invokes {@link JCellContent#clone()} to do the job.
     */
    @Override
    @SuppressWarnings("unchecked")
    protected Object cloneUserObject(Object userObject) {
        if (userObject == null) {
            return null;
        } else {
            return ((JCellContent) userObject).clone();
        }
    }

    /**
     * Retrieves the value for a given option from the options object, or
     * <code>null</code> if the options are not set (i.e., <code>null</code>).
     * @param option the name of the option
     */
    protected boolean getOptionValue(String option) {
        return this.options != null && this.options.isSelected(option);
    }

    /**
     * Returns the map of attribute changes needed to emphasize a jvertex. This
     * implementation returns {@link JAttr#EMPH_NODE_CHANGE}.
     * @param cell the vertex to be emphasized
     */
    protected AttributeMap getJVertexEmphAttr(JVertex cell) {
        return JAttr.EMPH_NODE_CHANGE;
    }

    /**
     * Returns the map of attribute changes needed to emphasize a jedge. This
     * implementation returns {@link JAttr#EMPH_EDGE_CHANGE}.
     */
    protected AttributeMap getJEdgeEmphAttr(JEdge jEdge) {
        return JAttr.EMPH_EDGE_CHANGE;
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
                    result = createJVertexAttr((JVertex) node);
                } else {
                    result = createJEdgeAttr((JEdge) node);
                }
            }
        } else {
            result = super.getAttributes(node);
        }
        assert result != null : String.format("Cell %s has no attributes", node);
        return result;
    }

    /**
     * Returns a freshly cloned attribute map for a given vertex. This
     * implementation returns the default attributes, set at construction time.
     * 
     * @param jVertex the j-vertex for which the attributes are to be created
     */
    protected AttributeMap createJVertexAttr(JVertex jVertex) {
        AttributeMap result = (AttributeMap) this.defaultNodeAttr.clone();
        maybeResetBackground(result);
        return result;
    }

    /**
     * Resets the background colour in a certain attribute to
     * {@link Color#WHITE} if the options demand this.
     */
    protected void maybeResetBackground(AttributeMap attributes) {
        if (!this.options.isSelected(Options.SHOW_BACKGROUND_OPTION)) {
            GraphConstants.setBackground(attributes, Color.WHITE);
        }
    }

    /**
     * Returns a freshly cloned attribute map for a given jgraph edge. This
     * implementation returns the default attributes set at construction time.
     * @param jEdge the jedge for which the attributes are to be created
     */
    protected AttributeMap createJEdgeAttr(JEdge jEdge) {
        AttributeMap result = (AttributeMap) this.defaultEdgeAttr.clone();
        return result;
    }

    /**
     * Returns a fresh copy of the attribute map for a given jgraph cell, and
     * adds the hidden and emphasised attributes to it.
     */
    protected AttributeMap createTransientJAttr(JCell jCell) {
        AttributeMap result = new AttributeMap();
        if (jCell instanceof JEdge) {
            result = createJEdgeAttr((JEdge) jCell);
            if (isEmphasized(jCell)) {
                result.applyMap(getJEdgeEmphAttr((JEdge) jCell));
            }
        } else {
            result = createJVertexAttr((JVertex) jCell);
            if (isEmphasized(jCell)) {
                result.applyMap(getJVertexEmphAttr((JVertex) jCell));
            }
        }
        if (isGrayedOut(jCell)) {
            result.applyMap(getGrayedOutAttr());
        }
        return result;
    }

    /**
     * Calls {@link DefaultGraphModel}
     * {@link #fireGraphChanged(Object, org.jgraph.event.GraphModelEvent.GraphModelChange)}
     * with <code>this</code> as first parameter.
     */
    void fireGraphChanged(GraphModelEvent.GraphModelChange edit) {
        super.fireGraphChanged(this, edit);
    }

    /**
     * Standard node attributes used in this graph model. Set in the
     * constructor.
     */
    protected final AttributeMap defaultNodeAttr;
    /**
     * Standard edge attributes used in this graph model. Set in the
     * constructor.
     */
    protected final AttributeMap defaultEdgeAttr;

    /**
     * The set of currently hidden jcells.
     */
    protected final Set<JCell> grayedOutJCells = new HashSet<JCell>();

    /** The set of currently emphasized j-cells. */
    protected final Set<JCell> emphJCells = new HashSet<JCell>();

    /**
     * Set of j-cells that were inserted in the model since the last time
     * <tt>{@link #setLayedOut(boolean)}</tt> was called.
     */
    protected final Set<JCell> layoutableJCells = new HashSet<JCell>();
    /** Set of options values to control the display. May be <code>null</code>. */
    private final Options options;
    /** Set of labels that is currently filtered from view. */
    private ObservableSet<Label> filteredLabels;
    /** Properties map of the graph being displayed or edited. */
    private GraphProperties properties;
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
        public RefreshEdit(Collection<JCell> refreshedJCells) {
            super(null, null, null, null, null);
            this.refreshedJCells = refreshedJCells;
        }

        /**
         * Returns the set of jcells to be refreshed.
         */
        public Collection<JCell> getRefreshedJCells() {
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
        private final Collection<JCell> refreshedJCells;
    }
}