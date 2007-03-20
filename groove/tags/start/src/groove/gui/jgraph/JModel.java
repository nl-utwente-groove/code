/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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
 * $Id: JModel.java,v 1.1.1.2 2007-03-20 10:42:47 kastenberg Exp $
 */
package groove.gui.jgraph;

import groove.graph.DefaultGraph;
import groove.graph.DefaultLabel;
import groove.graph.Edge;
import groove.graph.GraphInfo;
import groove.graph.Node;
import groove.gui.layout.JEdgeLayout;
import groove.gui.layout.LayoutMap;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.SwingUtilities;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultEdge;
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
 * <li>Model vertex and model edge specializations that store graph nodes and edges as user objects
 * and provide easy retrieval.
 * <ul>
 * Instances of JModel are attribute stores.
 * <p>
 * @author Arend Rensink
 * @version $Revision: 1.1.1.2 $
 */
abstract public class JModel extends DefaultGraphModel {
    /**
     * Special graph model edit that does not signal any actual change
     * but merely passes along a set of cells whose views need to be refreshed
     * due to some hiding or emphasis action.
     * @author Arend Rensink
     * @version $Revision: 1.1.1.2 $
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
            return refreshedJCells;
        }
        
        /** The set of cells that this event reports on refreshing. */
        private final Collection<JCell> refreshedJCells;
    }
    
    /**
     * Constructs a new JModel with given default node and edge attributes, possibly showing node identities.
     * @param defaultNodeAttr the default node attributes for this model
     * @param defaultEdgeAttr the default node attributes for this model
     * @param showNodeIdentities indicates whether JNode descriptions should be graph node
     *        identities. If false, JNode descriptions are (possibly empty) sets of self-edge
     *        labels.
     * @ensure !isLayedOut(), isShowNodeIdentities() == showNodeIdentities
     */
    public JModel(AttributeMap defaultNodeAttr, AttributeMap defaultEdgeAttr, boolean showNodeIdentities) {
        // give the graph model a linked list to speed up layer edits
        super();
        this.showNodeIdentities = showNodeIdentities;
        this.defaultNodeAttr = defaultNodeAttr;
        this.defaultEdgeAttr = defaultEdgeAttr;
    }

    /**
     * Constructs a new JModel with given default node and edge attributes,
     * displaying self-edges through JNode labels.
     * @param defaultNodeAttr the default node attributes for this model
     * @param defaultEdgeAttr the default node attributes for this model
     * @ensure !isLayedOut(), !isShowNodeIdentities()
     */
    public JModel(AttributeMap defaultNodeAttr, AttributeMap defaultEdgeAttr) {
        this(defaultNodeAttr, defaultEdgeAttr, false);
    }

    /**
     * Constructs a new JModel, possibly showing node identities.
     * The default node and edge identities are set through 
     * {@link JAttr#DEFAULT_NODE_ATTR} and {@link JAttr#DEFAULT_EDGE_ATTR}.
     * @ensure !isLayedOut(), !isShowNodeIdentities()
     */
    public JModel(boolean showNodeIdentities) {
        this(JAttr.DEFAULT_NODE_ATTR, JAttr.DEFAULT_EDGE_ATTR, showNodeIdentities);
    }

    /**
     * Constructs a new JModel, displaying self-edges through JNode labels.
     * The default node and edge identities are set through 
     * {@link JAttr#DEFAULT_NODE_ATTR} and {@link JAttr#DEFAULT_EDGE_ATTR}.
     * @ensure !isLayedOut(), !isShowNodeIdentities()
     */
    public JModel() {
        this(false);
    }

    /**
     * Returns a tool tip text for a given graph cell.
     * Does not test if the cell is hidden.
     * @param jCell the graph cell (of this graph model) for which a tool tip is to be returned
     * @require cell != null
     */
    public String getToolTipText(JCell jCell) {
        if (jCell != null) {
            return jCell.getToolTipText();
        } else {
            return null;
        }
    }

    /**
     * Indicates whether JNode descriptions should graph node identities. If false, JNode
     * descriptions are (possibly empty) sets of self-edge labels.
     */
    public boolean isShowNodeIdentities() {
        return showNodeIdentities;
    }

    /**
     * Indicates whether JNode descriptions should graph node identities. If false, JNode
     * descriptions are (possibly empty) sets of self-edge labels.
     */
    public void setShowNodeIdentities(boolean value) {
        showNodeIdentities = value;
    }

    /**
     * Indicates whether this graph model has been layed out (by any layouter).
     * @see #setLayedOut
     */
    public boolean isLayedOut() {
        return layoutableJCells.isEmpty();
    }

    /**
     * Sets the layed-out property. This method is called after layout has been finished.
     * @param layedOut indication whether the graph has been layed out
     * @ensure <tt>isLayedOut() == layedOut</tt>
     * @see #isLayedOut
     */
    public void setLayedOut(boolean layedOut) {
        if (layedOut) {
        layoutableJCells.clear();
        }
    }

    /**
     * Adds a j-cell to the layoutable cells of this j-model.
     * The j-cell is required to be in the model already.
     * @param jCell the cell to be made layoutable
     */
    public void addLayoutable(JCell jCell) {
        assert contains(jCell) : "Cell "+jCell+" is not in model";
        layoutableJCells.add(jCell);
    }

    /**
     * Removes a j-cell to the layoutable cells of this j-model.
     * The j-cell is required to be in the model already.
     * @param jCell the cell to be made non-layoutable
     */
    public void removeLayoutable(JCell jCell) {
        assert contains(jCell) : "Cell "+jCell+" is not in model";
        layoutableJCells.remove(jCell);
    }

    /**
     * Returns the set of jcells whose label sets contain at least one of the labels in a given set.
     * @param labelSet the set of labels looked for
     * @return the set of {@link JCell}s for which {@link  JCell#getLabelSet()}contains at least
     *         one of the elements of <tt>labelSet</tt>.
     */
    public Set<JCell> getJCellsForLabels(Set<String> labelSet) {
        Set<JCell> result = new HashSet<JCell>();
        for (int i = 0; i < getRootCount(); i++) {
            JCell jCell = (JCell) getRootAt(i);
            Iterator<String> jCellLabelIter = jCell.getLabelSet().iterator();
            boolean found = false;
            while (!found && jCellLabelIter.hasNext()) {
                Object jCellLabel = jCellLabelIter.next();
                if (labelSet.contains(jCellLabel)) {
                    result.add(jCell);
                    found = true;
                }
            }
        }
        return result;
    }

    /**
     * Sets all jcells to unmovable, except those that have been added since the last layout action.
     * This is done in preparation for layouting.
     */
    public void freeze() {
        Iterator<DefaultGraphCell> rootsIter = roots.iterator();
        while (rootsIter.hasNext()) {
            DefaultGraphCell root = rootsIter.next();
            GraphConstants.setMoveable(root.getAttributes(), layoutableJCells.contains(root));
        }
    }

    /**
     * Converts this j-model to a layed out groove graph.
     */
    public groove.graph.Graph toLayedOutGraph() {
        groove.graph.Graph result = new DefaultGraph();
        LayoutMap<Node,Edge> layoutMap = new LayoutMap<Node,Edge>();
        Map<JVertex,Node> nodeMap = new HashMap<JVertex,Node>();
        int rootCount = getRootCount();

        // Create nodes
        for (int i = 0; i < rootCount; i++) {
            Object root = getRootAt(i);
            if (root instanceof JVertex) {
                Node node = result.addNode();
                nodeMap.put((JVertex) root, node);
                layoutMap.putNode(node, ((JVertex) root).getAttributes());
                for (String label: getLabels((JVertex) root)) {
                    result.addEdge(node, DefaultLabel.createLabel(label), node);
                }
            }
        }

        // Create Edges
        for (int i = 0; i < rootCount; i++) {
            Object root = getRootAt(i);
            if (root instanceof JEdge) {
                Node source = nodeMap.get(getParent(getSource(root)));
                Node target = nodeMap.get(getParent(getTarget(root)));
                assert target != null : "Edge with empty target: "+root;
                assert source != null : "Edge with empty source: "+root;
                AttributeMap edgeAttr = ((JEdge) root).getAttributes();
                // test if the edge attributes are default
                boolean attrIsDefault = JEdgeLayout.newInstance(edgeAttr).isDefault();
                // parse edge text into label set
                for (String label: getLabels((JEdge) root)) {
                    Edge edge = result.addEdge(source, DefaultLabel.createLabel(label), target);
                    // add layout information if there is anything to be noted about the edge
                    if (! attrIsDefault) {
                    	layoutMap.putEdge(edge, edgeAttr);
                    }
                }
            }
        }
        GraphInfo.setLayoutMap(result, layoutMap);
        return result;
    }

    /**
     * Overrides the method so also incident edges of removed nodes are removed.
     */
    public void remove(Object[] roots) {
        List<Object> removables = new LinkedList<Object>();
        for (int i = 0; i < roots.length; i++) {
            DefaultGraphCell cell = (DefaultGraphCell) roots[i];
            if (cell.getChildCount() > 0) {
                DefaultPort port = (DefaultPort) cell.getChildAt(0);
                removables.addAll(port.getEdges());
            }
        }
        // add the roots to the incident edge list, so the remove is done in one go
        for (int i = 0; i < roots.length; i++) {
            removables.add(roots[i]);
        }
        super.remove(removables.toArray());
    }

    /**
     * Notifies the graph model listeners of a change in a set of cells, 
     * by firing a graph changed update with a {@link RefreshEdit} over the set.
     * @param jCellSet the set of cells to be refreshed
     * @see org.jgraph.graph.DefaultGraphModel#fireGraphChanged(Object, org.jgraph.event.GraphModelEvent.GraphModelChange)
     */
    public void refresh(final Collection<JCell> jCellSet) {
    	// do it now if we are on the event dispatch thread
    	if (SwingUtilities.isEventDispatchThread()) {
			fireGraphChanged(this, new RefreshEdit(jCellSet));
    	} else {
    		// otherwise, defer to avoid concurrency problems
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					fireGraphChanged(JModel.this, new RefreshEdit(jCellSet));
				}
			});
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
     * Tests the hiding status of a given jgraph cell.
     * @param cell the cell that is to be tested
     * @return <tt>true</tt> if the cell is currently hidden
     * @see #setHidden(Set,boolean)
     */
    public boolean isHidden(JCell cell) {
        return hiddenJCells.contains(cell);
    }

    /**
     * Hides or unhides a given jgraph cell. Hiding a cell means graying it out and making it
     * uneditable etc. Unhiding is the reverse operation. The hidden status of cells ican be tested
     * using <tt>isHidden(DefaultGraphCell)</tt>
     * @param cell the cell whose hidden status is to be changed
     * @param hidden the new hiding status of the cell
     * @ensure <tt>isHidden(cell) == hidden</tt>
     * @see #setHidden(Set,boolean)
     * @see #isHidden
     */
    public void setHidden(JCell cell, boolean hidden) {
        setHidden(Collections.singleton(cell), hidden);
    }

    /**
     * Hides or unhides a given set of jgraph cells. Hiding a cell means graying it out and making
     * it uneditable etc. Unhiding is the reverse operation.
     * @param jCells the cells whose hiding status is to be changed
     * @param hidden the new hiding status of the cell
     * @ensure if <tt>cells.contains(cell)</tt> then <tt>isHidden(cell) == hidden</tt>
     * @see #setHidden(JCell,boolean)
     * @see #isHidden
     */
    public void setHidden(Set<JCell> jCells, boolean hidden) {
        Set<JCell> changedJCells = new HashSet<JCell>();
        for (JCell jCell: jCells) {
            if (hidden != isHidden(jCell)) {
                if (hidden) {
                    hiddenJCells.add(jCell);
                    changedJCells.add(jCell);
                    // change.put(cell, createJAttr(cell));
                    // removeSelectionCell(cell);
                    if (!isEdge(jCell)) {
                        Iterator<?> jEdgeIter = ((JVertex) jCell).getPort().edges();
                        while (jEdgeIter.hasNext()) {
                            JEdge jEdge = (JEdge) jEdgeIter.next();
                            if (hiddenJCells.add(jEdge)) {
                                changedJCells.add(jEdge);
                            }
                        }
                    }
                } else {
                    hiddenJCells.remove(jCell);
                    changedJCells.add(jCell);
                    if (isEdge(jCell)) {
                        JCell sourceJVertex = (JCell) ((DefaultPort) ((DefaultEdge) jCell).getSource())
                                .getParent();
                        if (hiddenJCells.remove(sourceJVertex)) {
                            changedJCells.add(sourceJVertex);
                        }
                        JCell targetJVertex = (JCell) ((DefaultPort) ((DefaultEdge) jCell).getTarget())
                                .getParent();
                        if (hiddenJCells.remove(targetJVertex)) {
                            changedJCells.add(targetJVertex);
                        }
                    }
                }
            }
        }
        refresh(changedJCells);
		createLayerEdit(hiddenJCells.toArray(),
			GraphModelLayerEdit.BACK).execute();
    }
    
    /**
     * Returns the number of hidden cells.
     */
    public int getHiddenCount() {
        return hiddenJCells.size();
    }

    /**
     * Tests if a given jcell is currently emphasized. Note that emphasis may also exist for hidden
     * cells, even if this is not visible.
     * @param jCell the jcell that is tested for emphasis
     * @return <tt>true</tt> if <tt>jcell</tt> is currently emphasized
     */
    public boolean isEmphasized(JCell jCell) {
        return emphJCells.contains(jCell);
    }

    /**
     * Sets the set of emphasized jcells. First de-emphasizes the currently emphasized cells.
     * @param jCellSet the set of jcells to be emphasized. Should not be <tt>null</tt>.
     */
    public void setEmphasized(Set<JCell> jCellSet) {
        Set<JCell> changedEmphJCells = new HashSet<JCell>(emphJCells);
        changedEmphJCells.addAll(jCellSet);
        emphJCells.clear();
        emphJCells.addAll(jCellSet);
        refresh(changedEmphJCells);
    }
//
//    /**
//     * Changes the emphasis of a set of jcells.
//     * @param jCellSet the cet of cells to change
//     * @param emph a switch determining whether the cells are to be emphasized or deemphasized
//     */
//    public void changeEmphasized(Set<JCell> jCellSet, boolean emph) {
//        if (emph) {
//            emphJCells.addAll(jCellSet);
//        } else {
//            emphJCells.removeAll(jCellSet);
//        }
//        refresh(jCellSet);
//    }

    /**
     * Clears the currently emphasized nodes.
     */
    public void clearEmphasized() {
        Set<JCell> oldEmphSet = new HashSet<JCell>(emphJCells);
        emphJCells.clear();
        refresh(oldEmphSet);
    }
    
    /**
     * Invokes {@link JUserObject#clone()} to do the job.
	 */
	@Override
	protected Object cloneUserObject(Object userObject) {
        if (userObject == null) {
            return null;
        } else {
            return ((JUserObject) userObject).clone();
        }
	}

	/**
     * Returns the map of attribute changes needed to emphasize a jvertex.  
     * This implementation returns {@link JAttr#EMPH_NODE_CHANGE}. 
     * @param cell the vertex to be emphasized
     */
    protected AttributeMap getJVertexEmphAttr(JVertex cell) {
        return JAttr.EMPH_NODE_CHANGE;
    }

    /**
     * Returns the map of attribute changes needed to emphasize a jedge.  
     * This implementation returns {@link JAttr#EMPH_EDGE_CHANGE}. 
     */
    protected AttributeMap getJEdgeEmphAttr() {
        return JAttr.EMPH_EDGE_CHANGE;
    }

    /**
     * Returns the map of attribute changes needed to hide a jcell. 
     * This implementation returns {@link JAttr#HIDDEN_ATTR}. 
     */
    protected AttributeMap getHiddenAttr() {
        return JAttr.HIDDEN_ATTR;
    }

    /**
     * Collects the labels of a given j-vertex.
     * Callback method from {@link #toLayedOutGraph()}.
     * This implementation just returns the label set.
     */
    protected Collection<String> getLabels(JVertex jCell) {
        return jCell.getLabelSet();
    }

    /**
     * Collects the labels of a given j-edge.
     * Callback method from {@link #toLayedOutGraph()}.
     * This implementation just returns the label set.
     */
    protected Collection<String> getLabels(JEdge jEdge) {
        return jEdge.getLabelSet();
    }

    /**
     * Returns a freshly cloned attribute map for a given vertex. This implementation returns
     * the default attributes, set at construction time.
     * @param jVertex the j-vertex for which the attributes are to be created
     */
    protected AttributeMap createJVertexAttr(JVertex jVertex) {
        AttributeMap result = (AttributeMap) defaultNodeAttr.clone();
        return result;
    }

    /**
     * Returns a freshly cloned attribute map for a given jgraph edge. This implementation returns
     * the default attributes set at construction time.
     * @param jEdge the jedge for which the attributes are to be created
     */
    protected AttributeMap createJEdgeAttr(JEdge jEdge) {
        AttributeMap result = (AttributeMap) defaultEdgeAttr.clone();
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
                result.applyMap(getJEdgeEmphAttr());
            }
        } else {
            result = createJVertexAttr((JVertex) jCell);
            if (isEmphasized(jCell)) {
                result.applyMap(getJVertexEmphAttr((JVertex) jCell));
            }
        }
        if (isHidden(jCell)) {
            result.applyMap(getHiddenAttr());
        }
        return result;
    }
    
    /**
     * Property that indicates if node descriptions are identities. (If false, node descriptions are
     * possible empty sets of self-edge labels.)
     */
    private boolean showNodeIdentities;
    /**
     * Standard node attributes used in this graph model.
     * Set in the constructor.
     */
    protected final AttributeMap defaultNodeAttr;
    /**
     * Standard edge attributes used in this graph model.
     * Set in the constructor.
     */
    protected final AttributeMap defaultEdgeAttr;

    /**
     * The set of currently hidden jcells.
     */
    protected final Set<JCell> hiddenJCells = new HashSet<JCell>();

    /** The set of currently emphasized j-cells. */
    protected final Set<JCell> emphJCells = new HashSet<JCell>();

    /**
     * Set of j-cells that were inserted in the model since the last time
     * <tt>{@link #setLayedOut(boolean)}</tt> was called.
     */
    protected final Set<JCell> layoutableJCells = new HashSet<JCell>();
}