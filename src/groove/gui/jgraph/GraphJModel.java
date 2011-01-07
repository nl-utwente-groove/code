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
 * $Id: GraphJModel.java,v 1.21 2008-02-29 11:02:19 fladder Exp $
 */

package groove.gui.jgraph;

import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.Graph;
import groove.graph.GraphInfo;
import groove.graph.Label;
import groove.graph.Node;
import groove.gui.Options;
import groove.gui.layout.JCellLayout;
import groove.gui.layout.JEdgeLayout;
import groove.gui.layout.JVertexLayout;
import groove.gui.layout.LayoutMap;
import groove.util.ObservableSet;

import java.awt.Rectangle;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.swing.SwingUtilities;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.ConnectionSet;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.GraphConstants;

/**
 * Implements jgraph's GraphModel interface on top of a groove graph. The
 * resulting GraphModel should only be edited through the Graph interface.
 * @author Arend Rensink
 * @version $Revision$
 */
public class GraphJModel<N extends Node,E extends Edge<N>> extends
        DefaultGraphModel {
    /**
     * Creates a new GraphJModel instance on top of a given Graph, with given
     * node and edge attributes, and an indication whether self-edges should be
     * displayed as node labels. The node and edge attribute maps are cloned.
     * @param options specifies options for the visual display If false, node
     *        labels are used to display self edges.
     * @require graph != null, nodeAttr != null, edgeAttr != null;
     */
    protected GraphJModel(Options options) {
        this.options = options == null ? new Options() : options;
    }

    /**
     * Constructor for a dummy (empty) model.
     */
    GraphJModel() {
        this.options = null;
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
    public void addLayoutable(GraphJCell jCell) {
        assert contains(jCell) : "Cell " + jCell + " is not in model";
        this.layoutableJCells.add(jCell);
    }

    /**
     * Removes a j-cell to the layoutable cells of this j-model. The j-cell is
     * required to be in the model already.
     * @param jCell the cell to be made non-layoutable
     */
    public void removeLayoutable(GraphJCell jCell) {
        assert contains(jCell) : "Cell " + jCell + " is not in model";
        this.layoutableJCells.remove(jCell);
    }

    /** Specialises the type to a list of {@link GraphJCell}s. */
    @Override
    @SuppressWarnings("unchecked")
    public List<? extends GraphJCell> getRoots() {
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
     * Notifies the graph model listeners of a change in a set of cells, by
     * firing a graph changed update with a {@link RefreshEdit} over the set.
     * @param jCellSet the set of cells to be refreshed
     * @see org.jgraph.graph.DefaultGraphModel#fireGraphChanged(Object,
     *      org.jgraph.event.GraphModelEvent.GraphModelChange)
     */
    public void refresh(final Collection<? extends GraphJCell> jCellSet) {
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
     * @see GraphJCell#isGrayedOut()
     */
    public void changeGrayedOut(Set<GraphJCell> jCells, boolean grayedOut) {
        Set<GraphJCell> changedJCells = new HashSet<GraphJCell>();
        for (GraphJCell jCell : jCells) {
            if (jCell.setGrayedOut(grayedOut)) {
                changedJCells.add(jCell);
                if (grayedOut) {
                    // also gray out incident edges
                    if (!isEdge(jCell)) {
                        Iterator<?> jEdgeIter =
                            ((GraphJVertex) jCell).getPort().edges();
                        while (jEdgeIter.hasNext()) {
                            GraphJEdge jEdge = (GraphJEdge) jEdgeIter.next();
                            if (jEdge.setGrayedOut(true)) {
                                changedJCells.add(jEdge);
                            }
                        }
                    }
                } else {
                    // also revive end nodes
                    if (isEdge(jCell)) {
                        GraphJCell sourceJVertex =
                            ((GraphJEdge) jCell).getSourceVertex();
                        if (sourceJVertex.setGrayedOut(false)) {
                            changedJCells.add(sourceJVertex);
                        }
                        GraphJCell targetJVertex =
                            ((GraphJEdge) jCell).getTargetVertex();
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
     * @see GraphJCell#isGrayedOut()
     */
    public void setGrayedOut(Set<? extends GraphJCell> jCells) {
        Set<GraphJCell> changedJCells = new HashSet<GraphJCell>();
        // copy the old set of grayed-out cells
        for (GraphJCell root : getRoots()) {
            if (root.setGrayedOut(false)) {
                changedJCells.add(root);
            }
        }
        for (GraphJCell jCell : jCells) {
            if (jCell.setGrayedOut(true)) {
                // the cell should be either added or removed from the changed
                // cells
                if (!changedJCells.add(jCell)) {
                    changedJCells.remove(jCell);
                }
                // also gray out incident edges
                if (jCell instanceof GraphJVertex) {
                    Iterator<?> jEdgeIter =
                        ((GraphJVertex) jCell).getPort().edges();
                    while (jEdgeIter.hasNext()) {
                        GraphJEdge jEdge = (GraphJEdge) jEdgeIter.next();
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
    public void setEmphasised(Set<? extends GraphJCell> jCellSet) {
        Set<GraphJCell> changedJCells = new HashSet<GraphJCell>();
        for (GraphJCell root : getRoots()) {
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
        setEmphasised(Collections.<GraphJCell>emptySet());
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
        if (node instanceof GraphJCell) {
            result = ((GraphJCell) node).getAttributes();
            if (result == null) {
                if (node instanceof GraphJVertex) {
                    result = ((GraphJVertex) node).createAttributes(this);
                } else {
                    result = ((GraphJEdge) node).createAttributes(this);
                }
            }
        } else {
            result = super.getAttributes(node);
        }
        assert result != null : String.format("Cell %s has no attributes", node);
        return result;
    }

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
        public RefreshEdit(Collection<? extends GraphJCell> refreshedJCells) {
            super(null, null, null, null, null);
            this.refreshedJCells = refreshedJCells;
        }

        /**
         * Returns the set of jcells to be refreshed.
         */
        public Collection<? extends GraphJCell> getRefreshedJCells() {
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
        private final Collection<? extends GraphJCell> refreshedJCells;
    }

    /**
     * If the name is not explicitly set, obtains the name of the underlying
     * graph as set in the graph properties.
     */
    public String getName() {
        return getGraph() == null ? null : getGraph().getName();
    }

    /**
     * Returns the underlying Graph of this GraphModel.
     * @ensure result != null
     */
    public Graph<N,E> getGraph() {
        return this.graph;
    }

    /** 
     * Returns the (non-{@code null}) layout map of the graph.
     * This is retrieved from {@link GraphInfo#getLayoutMap(Graph)}. 
     */
    public LayoutMap<N,E> getLayoutMap() {
        return this.layoutMap;
    }

    /** 
     * Changes the underlying graph to the one passed in as a parameter.
     * Note that this should only be done as part of an action that also
     * changes the {@link GraphJCell}s of the {@link GraphJModel}, as well as the
     * mapping from graph elements to {@link GraphJCell}s. 
     */
    void setGraph(Graph<N,E> graph) {
        this.graph = graph;
    }

    /** 
     * Changes the underlying graph to the one passed in as a parameter.
     * Note that this should only be done as part of an action that also
     * changes the {@link GraphJCell}s of the {@link GraphJModel}, as well as the
     * mapping from graph elements to {@link GraphJCell}s. 
     */
    void setGraph(Graph<N,E> graph, Map<N,? extends GraphJVertex> nodeJCellMap,
            Map<E,? extends GraphJCell> edgeJCellMap) {
        setGraph(graph);
        this.nodeJCellMap.clear();
        this.nodeJCellMap.putAll(nodeJCellMap);
        this.edgeJCellMap.clear();
        this.edgeJCellMap.putAll(edgeJCellMap);
    }

    /**
     * Loads in a given graph, adding any nodes and edges not yet in this
     * model. Also adds the model as a listener to the graph again. This may be
     * necessary if the model was removed as a graph listener, for instance for
     * the sake of efficiency.
     */
    public void loadGraph(Graph<N,E> graph) {
        this.graph = graph;
        LayoutMap<N,E> layoutMap = GraphInfo.getLayoutMap(graph);
        this.layoutMap = layoutMap == null ? new LayoutMap<N,E>() : layoutMap;
        this.nodeJCellMap.clear();
        this.edgeJCellMap.clear();
        // add nodes from Graph to GraphModel
        prepareInsert();
        for (N node : graph.nodeSet()) {
            addNode(node);
        }
        for (E edge : graph.edgeSet()) {
            addEdge(edge);
        }
        doInsert(true, false);
    }

    /**
     * Returns the set of graph edges between two given graph nodes.
     */
    public Set<E> getEdgesBetween(N source, N target) {
        Set<E> result = new HashSet<E>();
        for (Map.Entry<E,? extends GraphJCell> cellEntry : this.edgeJCellMap.entrySet()) {
            Object cell = cellEntry.getValue();
            if (cell instanceof GraphJEdge) {
                GraphJEdge jEdge = (GraphJEdge) cell;
                if (jEdge.getSourceNode() == source
                    && jEdge.getTargetNode() == target) {
                    result.add(cellEntry.getKey());
                }
            }
        }
        return result;
    }

    /**
     * Returns the {@link GraphJCell}associated with a given graph element. The
     * result is a {@link GraphJVertex}for which the graph element is the
     * underlying node or self-edge, or a {@link GraphJEdge}for which the graph
     * element is an underlying edge.
     * @param elem the graph element for which the jcell is requested
     * @return the jcell associated with <tt>elem</tt>
     */
    public GraphJCell getJCell(Element elem) {
        if (elem instanceof Node) {
            return getJCellForNode((Node) elem);
        } else {
            return getJCellForEdge((Edge<?>) elem);
        }
    }

    /**
     * Returns the <tt>JNode</tt> or <tt>JEdge</tt> associated with a given
     * edge. The method returns a <tt>JNode</tt> if and only if <tt>edge</tt> is
     * a self-edge and <tt>showNodeIdentities</tt> does not hold.
     * @param edge the graph edge we're interested in
     * @return the <tt>JNode</tt> or <tt>JEdge</tt> modelling <tt>edge</tt>
     * @ensure result instanceof JNode && result.labels().contains(edge.label())
     *         || result instanceof JEdge &&
     *         result.labels().contains(edge.label())
     */
    public GraphJCell getJCellForEdge(Edge<?> edge) {
        return this.edgeJCellMap.get(edge);
    }

    /**
     * Returns the JNode associated with a given node.
     * @param node the graph node we're interested in
     * @return the JNode modelling node (if node is known)
     * @ensure result == null || result.getUserObject() == node
     */
    public GraphJVertex getJCellForNode(Node node) {
        return this.nodeJCellMap.get(node);
    }

    /** Stores the layout from the JModel back into the graph. */
    @SuppressWarnings("unchecked")
    public void synchroniseLayout(GraphJCell jCell) {
        LayoutMap<N,E> currentLayout = GraphInfo.getLayoutMap(getGraph());
        // create the layout map if it does not yet exist
        if (currentLayout == null) {
            currentLayout = new LayoutMap<N,E>();
            GraphInfo.setLayoutMap(getGraph(), currentLayout);
        }
        if (jCell instanceof GraphJEdge) {
            for (Edge<?> edge : ((GraphJEdge) jCell).getEdges()) {
                currentLayout.putEdge((E) edge, jCell.getAttributes());
            }
        } else {
            currentLayout.putNode((N) ((GraphJVertex) jCell).getNode(),
                jCell.getAttributes());
        }
    }

    /**
     * Creates a j-cell corresponding to a given node in the graph. Adds the
     * j-cell to {@link #addedJCells}, and updates {@link #nodeJCellMap}.
     */
    protected GraphJVertex addNode(N node) {
        GraphJVertex jVertex = computeJVertex(node);
        // we add nodes in front of the list to get them in front of the display
        this.addedJCells.add(0, jVertex);
        this.nodeJCellMap.put(node, jVertex);
        return jVertex;
    }

    /**
     * Creates a j-cell corresponding to a given graph edge. This may be a
     * j-vertex, if the edge can be graphically depicted by that vertex; or an
     * existing j-edge, if the edge can be represented by it. Otherwise, it will
     * be a new j-edge.
     */
    protected GraphJCell addEdge(E edge) {
        if (isUnaryEdge(edge)) {
            GraphJVertex jVertex = getJCellForNode(edge.source());
            if (jVertex.addSelfEdge(edge)) {
                // yes, the edge could be added here; we're done
                this.edgeJCellMap.put(edge, jVertex);
                return jVertex;
            }
        }
        N source = edge.source();
        N target = edge.target();
        // maybe a JEdge between this source and target is already in the
        // JGraph
        Set<GraphJEdge> outJEdges = this.addedOutJEdges.get(source);
        if (outJEdges == null) {
            this.addedOutJEdges.put(source, outJEdges =
                new HashSet<GraphJEdge>());
        }
        for (GraphJEdge jEdge : outJEdges) {
            if (jEdge.getTargetNode() == target
                && isLayoutCompatible(jEdge, edge) && jEdge.addEdge(edge)) {
                // yes, the edge could be added here; we're done
                this.edgeJCellMap.put(edge, jEdge);
                return jEdge;
            }
        }
        // none of the above: so create a new JEdge
        GraphJEdge jEdge = computeJEdge(edge);
        // put the edge at the end to make sure it goes to the back
        this.addedJCells.add(jEdge);
        outJEdges.add(jEdge);
        this.edgeJCellMap.put(edge, jEdge);
        GraphJVertex sourceNode = getJCellForNode(source);
        assert sourceNode != null : "No vertex for source node of " + edge;
        GraphJVertex targetPort = getJCellForNode(target);
        assert targetPort != null : "No vertex for target node of " + edge;
        this.connections.connect(jEdge, sourceNode.getPort(),
            targetPort.getPort());
        return jEdge;
    }

    /**
     * Tests if a given edge may be added to its source vertex.
     */
    protected boolean isUnaryEdge(E edge) {
        return !edge.label().isBinary();
    }

    /**
     * Tests if a given edge may be added to an existing jedge, as far as the
     * available layout information is concerned. The two are compatible if the
     * layout information for the edge equals that for the (edges contained in
     * the) jedge, or both are <tt>null</tt>.
     * @param jEdge the jedge to which the edge is about to be added
     * @param edge the edge that is investigated for compatibility
     */
    protected boolean isLayoutCompatible(GraphJEdge jEdge, E edge) {
        JCellLayout edgeLayout = this.layoutMap.getLayout(edge);
        @SuppressWarnings("unchecked")
        JCellLayout jEdgeLayout = this.layoutMap.getLayout((E) jEdge.getEdge());
        if (edgeLayout == null) {
            return jEdgeLayout == null;
        } else {
            return jEdgeLayout != null && edgeLayout.equals(jEdgeLayout);
        }
    }

    /**
     * Creates a new j-edge using {@link #createJEdge(Edge)}, and sets the
     * attributes using {@link GraphJEdge#createAttributes()} and adds available
     * layout information from the layout map stored in this model.
     * @param edge graph edge for which a corresponding j-edge is to be created
     */
    protected GraphJEdge computeJEdge(E edge) {
        GraphJEdge result = createJEdge(edge);
        result.getAttributes().applyMap(result.createAttributes(this));
        JEdgeLayout layout = this.layoutMap.getLayout(edge);
        if (layout != null) {
            result.getAttributes().applyMap(layout.toJAttr());
        }
        return result;
    }

    /**
     * Creates a new j-vertex using {@link #createJVertex(Node)}, and sets the
     * attributes using {@link GraphJVertex#createAttributes()} and adds available
     * layout information from the layout map stored in this model; or adds a
     * random position otherwise.
     * @param node graph node for which a corresponding j-vertex is to be
     *        created
     */
    protected GraphJVertex computeJVertex(N node) {
        GraphJVertex result = createJVertex(node);
        result.getAttributes().applyMap(result.createAttributes(this));
        if (GraphConstants.isMoveable(result.getAttributes())) {
            JVertexLayout layout = this.layoutMap.getLayout(node);
            if (layout != null) {
                result.getAttributes().applyMap(layout.toJAttr());
            } else {
                this.layoutableJCells.add(result);
                Rectangle newBounds =
                    new Rectangle(this.nodeX, this.nodeY,
                        JAttr.DEFAULT_NODE_BOUNDS.width,
                        JAttr.DEFAULT_NODE_BOUNDS.height);
                GraphConstants.setBounds(result.getAttributes(), newBounds);
                this.nodeX = randomCoordinate();
                this.nodeY = randomCoordinate();
            }
        }
        return result;
    }

    /**
     * Factory method for jgraph edges.
     * 
     * @param edge graph edge for which a corresponding j-edge is to be created
     * @return j-edge corresponding to <tt>edge</tt>
     * @ensure <tt>result.getEdgeSet().contains(edge)</tt>
     */
    protected GraphJEdge createJEdge(E edge) {
        return new GraphJEdge(this, edge);
    }

    /**
     * Factory method for jgraph nodes.
     * @param node graph node for which a corresponding j-node is to be created
     * @return j-node corresponding to <tt>node</tt>
     * @ensure <tt>result.getNode().equals(node)</tt>
     */
    protected GraphJVertex createJVertex(N node) {
        return new GraphJVertex(this, node, true);
    }

    /**
     * Sets the transient variables (cells, attributes and connections) to fresh
     * (empty) initial values.
     */
    protected void prepareInsert() {
        this.addedJCells.clear();
        this.addedOutJEdges.clear();
        this.connections = new ConnectionSet();
    }

    /**
     * Executes the insertion prepared by node and edge additions.
     * Optionally sends the new elements to the back
     * @param replace if {@code true}, the old roots should be deleted
     */
    protected void doInsert(boolean replace, boolean toBack) {
        Object[] addedCells = this.addedJCells.toArray();
        Object[] removedCells = replace ? getRoots().toArray() : null;
        createEdit(addedCells, removedCells, null, this.connections, null, null).execute();
        if (toBack) {
            // new edges should be behind the nodes
            toBack(addedCells);
        }
    }

    /**
     * Returns a random number bounded by <tt>toJCellMap.size()</tt>. Used to
     * generate a random position for any added j-vertex without layout
     * information.
     */
    private int randomCoordinate() {
        return randomGenerator.nextInt((this.nodeJCellMap.size() + this.edgeJCellMap.size()) * 5 + 1);
    }

    /**
     * Indicates whether aspect prefixes should be shown for nodes and edges.
     */
    boolean isShowNodeIdentities() {
        return getOptionValue(Options.SHOW_NODE_IDS_OPTION);
    }

    /**
     * Indicates whether unfiltered edges to filtered nodes should remain
     * visible.
     */
    boolean isShowUnfilteredEdges() {
        return getOptionValue(Options.SHOW_UNFILTERED_EDGES_OPTION);
    }

    /**
     * Indicates whether self-edges should be shown as node labels.
     */
    boolean isShowVertexLabels() {
        return getOptionValue(Options.SHOW_VERTEX_LABELS_OPTION);
    }

    /**
     * Indicates whether anchors should be shown in the rule and lts views.
     */
    boolean isShowAnchors() {
        return getOptionValue(Options.SHOW_ANCHORS_OPTION);
    }

    /**
     * Set of j-cells that were inserted in the model since the last time
     * <tt>{@link #setLayedOut(boolean)}</tt> was called.
     */
    protected final Set<GraphJCell> layoutableJCells =
        new HashSet<GraphJCell>();
    /** Set of options values to control the display. May be <code>null</code>. */
    private final Options options;
    /** Set of labels that is currently filtered from view. */
    private ObservableSet<Label> filteredLabels;
    /**
     * The underlying Graph of this GraphModel.
     * @invariant graph != null
     */
    private Graph<N,E> graph;
    /**
     * The layout map for the underlying graph. It maps {@link Element}s to
     * {@link JCellLayout}s. This is set to an empty map if the graph is not a
     * layed out graph.
     */
    private LayoutMap<N,E> layoutMap;
    /**
     * Map from graph nodes to JGraph cells.
     */
    private Map<N,GraphJVertex> nodeJCellMap = new HashMap<N,GraphJVertex>();
    /**
     * Map from graph edges to JGraph cells.
     */
    private Map<E,GraphJCell> edgeJCellMap = new HashMap<E,GraphJCell>();

    /**
     * Mapping from graph nodes to JEdges for outgoing edges.
     * Used in the process of constructing a GraphJModel.
     */
    private final Map<N,Set<GraphJEdge>> addedOutJEdges =
        new HashMap<N,Set<GraphJEdge>>();
    /**
     * Set of GraphModel cells. Used in the process of constructing a
     * GraphJModel.
     * @invariant addedCells \subseteq org.jgraph.graph.DefaultGraphCell
     */
    private final List<GraphJCell> addedJCells = new LinkedList<GraphJCell>();
    /**
     * Set of GraphModel connections. Used in the process of constructing a
     * GraphJModel.
     */
    private ConnectionSet connections;

    /**
     * Counter to provide the x-coordinate of fresh nodes with fresh values
     */
    private transient int nodeX;
    /**
     * Counter to provide the y-coordinate of fresh nodes with fresh values
     */
    private transient int nodeY;

    /**
     * Creates a new GraphJModel instance on top of a given Graph.
     * Self-edges will be
     * displayed as node labels.
     * 
     * @param graph the underlying Graph
     * @param options display options
     */
    static public <N extends Node,E extends Edge<N>> GraphJModel<N,E> newInstance(
            Graph<N,E> graph, Options options) {
        GraphJModel<N,E> result = new GraphJModel<N,E>(options);
        return result;
    }

    /** Random generator for coordinates of new nodes. */
    private static final Random randomGenerator = new Random();
}