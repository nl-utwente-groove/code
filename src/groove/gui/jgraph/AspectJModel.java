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
 * $Id: AspectJModel.java,v 1.35 2008-03-13 14:40:32 rensink Exp $
 */
package groove.gui.jgraph;

import static groove.view.aspect.AspectKind.REMARK;
import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.Graph;
import groove.graph.GraphInfo;
import groove.graph.GraphProperties;
import groove.graph.GraphRole;
import groove.graph.Node;
import groove.gui.Editor;
import groove.gui.Options;
import groove.gui.layout.JEdgeLayout;
import groove.gui.layout.LayoutMap;
import groove.view.View;
import groove.view.aspect.AspectEdge;
import groove.view.aspect.AspectGraph;
import groove.view.aspect.AspectKind;
import groove.view.aspect.AspectNode;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgraph.event.GraphModelEvent.GraphModelChange;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.GraphConstants;

/**
 * Implements jgraph's GraphModel interface on top of a {@link View}. This is
 * used to visualise rules and attributed graphs.
 * @author Arend Rensink
 * @version $Revision: 2982 $
 */
final public class AspectJModel extends GraphJModel<AspectNode,AspectEdge> {

    // --------------------- INSTANCE DEFINITIONS ------------------------

    /**
     * Creates an empty instance.
     * Initialise with {@link #loadGraph(Graph)} before using.
     */
    AspectJModel(Options options) {
        super(options);
        this.editor = null;
    }

    /** Constructor for an editable model. */
    AspectJModel(Editor editor) {
        super(editor.getOptions());
        this.editor = editor;
    }

    /** Specialises the type to a list of {@link GraphJCell}s. */
    @Override
    @SuppressWarnings("unchecked")
    public List<? extends AspectJCell> getRoots() {
        return (List<? extends AspectJCell>) super.getRoots();
    }

    /** Specialises the return type. */
    @Override
    public AspectGraph getGraph() {
        return (AspectGraph) super.getGraph();
    }

    /** Specialises the return type. */
    @SuppressWarnings("unchecked")
    @Override
    public Set<AspectJCell> getJCellSet(Set<Element> elemSet) {
        return (Set<AspectJCell>) super.getJCellSet(elemSet);
    }

    /** Specialises the return type. */
    @Override
    public AspectJCell getJCellForEdge(Edge<?> edge) {
        return (AspectJCell) super.getJCellForEdge(edge);
    }

    /** Specialises the return type. */
    @Override
    public AspectJVertex getJCellForNode(Node node) {
        return (AspectJVertex) super.getJCellForNode(node);
    }

    @Override
    public void loadGraph(Graph<AspectNode,AspectEdge> graph) {
        this.loading = true;
        super.loadGraph(graph);
        for (AspectJCell root : getRoots()) {
            root.saveToUserObject();
        }
        this.loading = false;
    }

    /** 
     * Reconstructs the aspect graph on the basis of the current
     * content of the JModel.
     * This method should be called immediately after the changes to
     * the JModel have been made, but before any graph listeners are 
     * notified.
     */
    public void syncGraph() {
        if (this.loading) {
            return;
        }
        GraphRole role = this.editor.getRole();
        Map<AspectNode,AspectJVertex> nodeJVertexMap =
            new HashMap<AspectNode,AspectJVertex>();
        Map<AspectEdge,GraphJCell> edgeJCellMap =
            new HashMap<AspectEdge,GraphJCell>();
        AspectGraph graph = new AspectGraph(getName(), role);
        LayoutMap<AspectNode,AspectEdge> layoutMap =
            new LayoutMap<AspectNode,AspectEdge>();
        for (GraphJCell jCell : getRoots()) {
            if (jCell instanceof AspectJVertex) {
                AspectJVertex jVertex = (AspectJVertex) jCell;
                jVertex.loadFromUserObject(role);
                graph.addNode(jVertex.getNode());
                nodeJVertexMap.put(jVertex.getNode(), jVertex);
                for (AspectEdge edge : jVertex.getSelfEdges()) {
                    edgeJCellMap.put(edge, jVertex);
                    graph.addEdge(edge);
                }
                layoutMap.putNode(jVertex.getNode(), jVertex.getAttributes());
            }
        }
        for (GraphJCell jCell : getRoots()) {
            if (jCell instanceof AspectJEdge) {
                AspectJEdge jEdge = (AspectJEdge) jCell;
                jEdge.loadFromUserObject(role);
                AttributeMap edgeAttr = jEdge.getAttributes();
                boolean attrIsDefault =
                    JEdgeLayout.newInstance(edgeAttr).isDefault();
                for (AspectEdge edge : jEdge.getEdges()) {
                    edgeJCellMap.put(edge, jEdge);
                    graph.addEdge(edge);
                    // add layout information if there is anything to be noted
                    // about the edge
                    if (!attrIsDefault) {
                        layoutMap.putEdge(edge, edgeAttr);
                    }
                }
            }
        }
        for (AspectJVertex jVertex : nodeJVertexMap.values()) {
            jVertex.setNodeFixed();
        }
        graph.setFixed();
        GraphInfo.setLayoutMap(graph, layoutMap);
        GraphInfo.setProperties(graph, getProperties());
        setGraph(graph, nodeJVertexMap, edgeJCellMap);
    }

    /** Changes the name of the model (and the underlying graph). */
    public void setName(String name) {
        setGraph(getGraph().rename(name));
    }

    /** Indicates that the JModel is editable. */
    boolean isEditing() {
        return this.editor != null;
    }

    /**
     * Indicates whether aspect prefixes should be shown for nodes and edges.
     */
    public final boolean isShowRemarks() {
        return getOptionValue(Options.SHOW_REMARKS_OPTION);
    }

    /**
     * Indicates whether aspect prefixes should be shown for nodes and edges.
     */
    public final boolean isShowAspects() {
        return getOptionValue(Options.SHOW_ASPECTS_OPTION);
    }

    /**
     * Indicates whether data nodes should be shown in the JGraph.
     * This is certainly the case if this model is editable.
     */
    public final boolean isShowValueNodes() {
        return isEditing() || getOptionValue(Options.SHOW_VALUE_NODES_OPTION);
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

    /** Properties map of the graph being displayed or edited. */
    private GraphProperties properties;

    /**
     * New source is only acceptable if not <tt>null</tt>.
     */
    @Override
    public boolean acceptsSource(Object edge, Object port) {
        return port != null;// && port != ((JEdge) edge).getTarget();
    }

    /**
     * Overrides the method so also incident edges of removed nodes are removed.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void remove(Object[] roots) {
        List<Object> removables = new LinkedList<Object>(Arrays.asList(roots));
        for (Object element : roots) {
            if (element instanceof AspectJVertex) {
                AspectJVertex cell = (AspectJVertex) element;
                removables.addAll(cell.getPort().getEdges());
            }
        }
        super.remove(removables.toArray());
    }

    @Override
    public Map<?,?> cloneCells(Object[] cells) {
        Map<?,?> result = super.cloneCells(cells);
        // assign new node numbers to the JVertices
        collectNodeNrs();
        for (Object cell : result.values()) {
            if (cell instanceof AspectJVertex) {
                ((AspectJVertex) cell).reset(createAspectNode());
            }
        }
        resetNodeNrs();
        return result;
    }

    /** 
     * We override this method to ensure that the aspect graph
     * remains in sync with any changes made to the JModel, <i>before</i>
     * the listeners are notified of the changes.
     * If a relevant change was made, {@link #syncGraph()}
     * is invoked.
     */
    @Override
    protected void fireGraphChanged(Object source, GraphModelChange edit) {
        if (!this.loading) {
            // only reload if the edit changed the graph structure
            // (and not just the layout)
            boolean changed =
                edit.getInserted() != null || edit.getRemoved() != null
                    || edit.getConnectionSet() != null;
            // only user object changes in the attribute should trigger a reload
            if (!changed && edit.getAttributes() != null) {
                for (Object attrValue : ((Map<?,?>) edit.getAttributes()).values()) {
                    // the user object changed if the attribute map contains an
                    // entry for the VALUE key
                    AttributeMap attrMap = (AttributeMap) attrValue;
                    if (attrMap.containsKey(GraphConstants.VALUE)) {
                        changed = true;
                        break;
                    }
                }
            }
            if (changed) {
                syncGraph();
            }
        }
        super.fireGraphChanged(source, edit);
    }

    /**
     * Callback factory method to create an empty, editable j-edge.
     */
    AspectJEdge computeJEdge() {
        AspectJEdge result = new AspectJEdge(this);
        // add a single, empty label so the edge will be displayed
        result.getUserObject().add("");
        return result;
    }

    /**
     * Tests if a given edge may be added to its source vertex.
     */
    @Override
    protected boolean isUnaryEdge(AspectEdge edge) {
        boolean result;
        boolean unLayedoutSelfEdge =
            edge != null && edge.source() == edge.target()
                && getLayoutMap().getLayout(edge) == null;
        if (isEditing()) {
            result = unLayedoutSelfEdge;
        } else {
            result =
                !edge.isBinary() || unLayedoutSelfEdge
                    && edge.getKind() == REMARK;
        }
        return result;
    }

    /**
     * Callback factory method to create an editable GraphJVertex<?,?>.
     * The vertex is initialised with a new node 
     * obtained through {@link #createAspectNode()}.
     */
    AspectJVertex computeJVertex() {
        return createJVertex(createAspectNode());
    }

    /**
     * Overwrites the method so as to return a rule vertex.
     * @require <tt>edge instanceof RuleGraph.RuleNode</tt>
     */
    @Override
    protected AspectJVertex createJVertex(AspectNode node) {
        return new AspectJVertex(this, node);
    }

    /**
     * Overwrites the method so as to return a rule edge.
     * @require <tt>edge instanceof RuleGraph.RuleEdge</tt>
     */
    @Override
    protected AspectJEdge createJEdge(AspectEdge edge) {
        return new AspectJEdge(this, edge);
    }

    /** 
     * Creates a new aspect node, with a fresh node number and
     * the graph role taken from the editor.
     */
    private AspectNode createAspectNode() {
        assert isEditing();
        return new AspectNode(createNewNodeNr(), this.editor.getRole());
    }

    /** Initialises the set {@link #usedNrs} with the currently used node numbers. */
    private boolean collectNodeNrs() {
        boolean result = this.usedNrs == null;
        if (result) {
            this.usedNrs = new HashSet<Integer>();
            for (Object root : getRoots()) {
                if (root instanceof GraphJVertex<?,?>) {
                    this.usedNrs.add(((GraphJVertex<?,?>) root).getNumber());
                }
            }
        }
        return result;
    }

    /** Resets the set of used node numbers to {@code null}. */
    private void resetNodeNrs() {
        this.usedNrs = null;
    }

    /**
     * Returns the first non-negative number that is not used as a node number
     * in this model.
     */
    private int createNewNodeNr() {
        int result = 0;
        boolean collect = collectNodeNrs();
        // search for an unused node number
        while (this.usedNrs.contains(result)) {
            result++;
        }
        if (collect) {
            resetNodeNrs();
        } else {
            this.usedNrs.add(result);
        }
        return result;
    }

    /** The associated editor. */
    private final Editor editor;
    /** The set of used node numbers. */
    private Set<Integer> usedNrs;
    /** Flag indicating that we are loading a new aspect graph,
     * so we don't have to parse it.
     */
    private boolean loading;

    /**
     * Creates a new model instance for a given aspect graph.
     */
    static public AspectJModel newInstance(AspectGraph graph, Options options) {
        assert graph != null;
        AspectJModel result = new AspectJModel(options);
        result.loadGraph(graph);
        return result;
    }

    /**
     * Creates a new model instance for a given editor, and initialises it
     * to a given graph.
     */
    static public AspectJModel newInstance(Editor editor, AspectGraph graph) {
        assert graph != null;
        AspectJModel result = new AspectJModel(editor);
        result.loadGraph(graph);
        return result;
    }

    /** A fixed, empty model. */
    public static final AspectJModel EMPTY_JMODEL = newInstance(
        AspectGraph.emptyGraph("", GraphRole.HOST), null);

    /** Role names (for the tool tips). */
    static final Map<AspectKind,String> ROLE_NAMES =
        new EnumMap<AspectKind,String>(AspectKind.class);
    /** Role descriptions (for the tool tips). */
    static final Map<AspectKind,String> ROLE_DESCRIPTIONS =
        new EnumMap<AspectKind,String>(AspectKind.class);

    static {
        ROLE_NAMES.put(AspectKind.EMBARGO, "Embargo");
        ROLE_NAMES.put(AspectKind.READER, "Reader");
        ROLE_NAMES.put(AspectKind.CREATOR, "Creator");
        ROLE_NAMES.put(AspectKind.ADDER, "Adder");
        ROLE_NAMES.put(AspectKind.ERASER, "Eraser");
        ROLE_NAMES.put(AspectKind.REMARK, "Remark");

        ROLE_DESCRIPTIONS.put(AspectKind.EMBARGO,
            "Must be absent from a graph for this rule to apply");
        ROLE_DESCRIPTIONS.put(AspectKind.READER,
            "Must be matched for this rule to apply");
        ROLE_DESCRIPTIONS.put(AspectKind.CREATOR,
            "Will be created by applying this rule");
        ROLE_DESCRIPTIONS.put(
            AspectKind.ADDER,
            "Must be absent from a graph for this rule to apply, and will be created when applying this rule");
        ROLE_DESCRIPTIONS.put(AspectKind.ERASER,
            "Will be deleted by applying this rule");
        ROLE_DESCRIPTIONS.put(AspectKind.REMARK,
            "Has no effect on the execution of the rule");
    }
}