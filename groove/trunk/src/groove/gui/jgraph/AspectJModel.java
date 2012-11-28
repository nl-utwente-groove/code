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

import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.Graph;
import groove.graph.GraphInfo;
import groove.graph.GraphProperties;
import groove.graph.GraphRole;
import groove.graph.ImplicitTypeGraph;
import groove.graph.Node;
import groove.graph.TypeGraph;
import groove.gui.Options;
import groove.gui.layout.JEdgeLayout;
import groove.gui.layout.LayoutMap;
import groove.gui.look.VisualMap;
import groove.util.ChangeCount;
import groove.util.ChangeCount.Derived;
import groove.util.Groove;
import groove.view.FormatError;
import groove.view.FormatException;
import groove.view.GrammarModel;
import groove.view.GraphBasedModel;
import groove.view.ResourceModel;
import groove.view.TypeModel;
import groove.view.aspect.AspectEdge;
import groove.view.aspect.AspectGraph;
import groove.view.aspect.AspectKind;
import groove.view.aspect.AspectNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.undo.UndoableEdit;

import org.jgraph.event.GraphModelEvent.GraphModelChange;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.ConnectionSet;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.ParentMap;

/**
 * Implements jgraph's GraphModel interface on top of a {@link ResourceModel}. This is
 * used to visualise rules and attributed graphs.
 * @author Arend Rensink
 * @version $Revision: 2982 $
 */
final public class AspectJModel extends GraphJModel<AspectNode,AspectEdge> {
    /** 
     * Creates an new model, initially without a graph or grammar loaded.
     * Call {@link #setGrammar(GrammarModel)} to complete construction.
     */
    AspectJModel(AspectJGraph jGraph) {
        super(jGraph);
    }

    @Override
    public AspectJGraph getJGraph() {
        return (AspectJGraph) super.getJGraph();
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

    /** Sets a grammar model, with respect to which typing is resolved. */
    void setGrammar(GrammarModel grammar) {
        assert this.grammar == null && grammar != null;
        this.grammar = grammar;
    }

    /** Returns the (possibly {@code null}) grammar set for this model. */
    GrammarModel getGrammar() {
        return this.grammar;
    }

    /** The associated system properties. */
    private GrammarModel grammar;

    @Override
    public AspectJCell getJCell(Element elem) {
        return (AspectJCell) super.getJCell(elem);
    }

    /** Specialises the return type. */
    @Override
    public AspectJCell getJCellForEdge(Edge edge) {
        return (AspectJCell) super.getJCellForEdge(edge);
    }

    /** Specialises the return type. */
    @Override
    public AspectJVertex getJCellForNode(Node node) {
        return (AspectJVertex) super.getJCellForNode(node);
    }

    @Override
    public void loadGraph(Graph<AspectNode,AspectEdge> graph) {
        setLoading(true);
        super.loadGraph(graph);
        for (AspectJCell root : getRoots()) {
            root.saveToUserObject();
        }
        loadViewErrors();
        this.properties = GraphInfo.getProperties(graph, false);
        this.jModelModCount.increase();
        this.graphModCount.increase();
        setLoading(false);
    }

    /** 
     * Clones this model, and initialises the new model with the given
     * argument graph.
     */
    public AspectJModel cloneWithNewGraph(Graph<AspectNode,AspectEdge> graph) {
        AspectJModel result = getJGraph().newModel();
        if (getGrammar() != null) {
            result.setGrammar(getGrammar());
        }
        result.beingEdited = this.beingEdited;
        result.loadGraph(graph);
        return result;
    }

    /** 
     * Reconstructs the aspect graph on the basis of the current
     * content of the JModel.
     * This method should be called immediately after the changes to
     * the JModel have been made, but before any graph listeners are 
     * notified.
     */
    public void syncGraph() {
        if (isLoading()) {
            return;
        }
        GraphRole role = getGraph().getRole();
        Map<AspectNode,AspectJVertex> nodeJVertexMap =
            new HashMap<AspectNode,AspectJVertex>();
        Map<AspectEdge,GraphJCell> edgeJCellMap =
            new HashMap<AspectEdge,GraphJCell>();
        AspectGraph graph = new AspectGraph(getName(), role);
        for (GraphJCell jCell : getRoots()) {
            if (jCell instanceof AspectJVertex) {
                AspectJVertex jVertex = (AspectJVertex) jCell;
                jVertex.loadFromUserObject(role);
                graph.addNode(jVertex.getNode());
                nodeJVertexMap.put(jVertex.getNode(), jVertex);
                for (AspectEdge edge : jVertex.getEdges()) {
                    edgeJCellMap.put(edge, jVertex);
                    graph.addEdge(edge);
                }
            }
        }
        for (GraphJCell jCell : getRoots()) {
            if (jCell instanceof AspectJEdge) {
                AspectJEdge jEdge = (AspectJEdge) jCell;
                jEdge.loadFromUserObject(role);
                for (AspectEdge edge : jEdge.getEdges()) {
                    edgeJCellMap.put(edge, jEdge);
                    graph.addEdge(edge);
                }
            }
        }
        for (AspectJVertex jVertex : nodeJVertexMap.values()) {
            jVertex.setNodeFixed();
        }
        // collect the layout information
        LayoutMap<AspectNode,AspectEdge> layoutMap =
            new LayoutMap<AspectNode,AspectEdge>();
        for (GraphJCell jCell : getRoots()) {
            if (jCell instanceof AspectJVertex) {
                AspectJVertex jVertex = (AspectJVertex) jCell;
                layoutMap.putNode(jVertex.getNode(), jVertex.getVisuals());
            } else {
                AspectJEdge jEdge = (AspectJEdge) jCell;
                VisualMap visuals = jEdge.getVisuals();
                if (!JEdgeLayout.newInstance(visuals).isDefault()) {
                    for (AspectEdge edge : jEdge.getEdges()) {
                        layoutMap.putEdge(edge, visuals);
                    }
                }
            }
        }
        GraphInfo.setLayoutMap(graph, layoutMap);
        GraphInfo.setProperties(graph, getProperties());
        graph.setFixed();
        setGraph(graph, nodeJVertexMap, edgeJCellMap);
        this.jModelModCount.increase();
        this.graphModCount.increase();
        loadViewErrors();
        if (GUI_DEBUG) {
            System.out.printf("Graph resynchronised with model %s%n", getName());
            Groove.printStackTrace(System.out, false);
        }
    }

    /** 
     * Sets the extra-error flags of all the cells, based
     * on the errors in the view.
     */
    public void loadViewErrors() {
        if (getGrammar() == null) {
            return;
        }
        for (AspectJCell jCell : getRoots()) {
            jCell.getErrors().clear(false);
        }
        this.errorMap.clear();
        for (FormatError error : getResourceModel().getErrors()) {
            for (Element errorObject : error.getElements()) {
                AspectJCell errorCell = getJCell(errorObject);
                if (errorCell == null && errorObject instanceof Edge) {
                    errorCell = getJCell(((Edge) errorObject).source());
                }
                if (errorCell != null) {
                    this.errorMap.put(error, errorCell);
                    errorCell.getErrors().addError(error, true);
                }
            }
        }
    }

    /** Returns an up-to-date resource model for the graph being edited here. */
    public GraphBasedModel<?> getResourceModel() {
        return this.resource.getValue();
    }

    /** Returns the type graph associated with this jModel, if any. */
    public TypeGraph getTypeGraph() {
        return this.typeGraph.getValue();
    }

    /** 
     * Returns the mapping from errors to JCells with that error
     * computed during the last call to {@link #loadGraph(Graph)} 
     * or {@link #syncGraph()}.
     */
    public Map<FormatError,AspectJCell> getErrorMap() {
        return this.errorMap;
    }

    /** Changes the name of the model (and the underlying graph). */
    public void setName(String name) {
        setGraph(getGraph().rename(name));
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
     * Enable bidirectional edges to be merged, if the aspect graph is a host
     * graph, and the grammar property is set to true.
     */
    @Override
    public boolean mergeBidirectionalEdges() {
        if (this.beingEdited || getGraph().getRole() != GraphRole.HOST) {
            return false;
        } else {
            return getJGraph().getOptionValue(
                Options.SHOW_BIDIRECTIONAL_EDGES_OPTION);
        }
    }

    /** Change the {@link #beingEdited} flag. */
    public void setBeingEdited(boolean flag) {
        this.beingEdited = flag;
    }

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

    @SuppressWarnings("rawtypes")
    @Override
    public void insert(Object[] roots, Map attributes, ConnectionSet cs,
            ParentMap pm, UndoableEdit[] edits) {
        Set<Object> insertables = new LinkedHashSet<Object>();
        // only copy edges whose source and target ports are connected
        for (Object root : roots) {
            boolean insert = true;
            if (root instanceof AspectJEdge) {
                AspectJEdge jEdge = (AspectJEdge) root;
                DefaultPort sourcePort = (DefaultPort) cs.getPort(jEdge, true);
                DefaultPort targetPort = (DefaultPort) cs.getPort(jEdge, false);
                insert = sourcePort != null && targetPort != null;
            }
            if (insert) {
                insertables.add(root);
            } else {
                // if the root is not copied over, remove it from the attribute map
                // to avoid its being flagged as a changed element
                attributes.remove(root);
            }
        }
        // adjust the connection set by removing all connections for edges
        // that were just removed
        if (cs != null) {
            Iterator it = cs.connections();
            while (it.hasNext()) {
                ConnectionSet.Connection conn =
                    (ConnectionSet.Connection) it.next();
                if (!insertables.contains(conn.getEdge())) {
                    it.remove();
                }
            }
        }
        super.insert(insertables.toArray(), attributes, cs, pm, edits);
    }

    @Override
    public Map<?,?> cloneCells(Object[] cells) {
        Map<?,?> result = super.cloneCells(cells);
        // assign new node numbers to the JVertices
        collectNodeNrs();
        // we reuse the JCells to keep their connection and user object intact;
        // however, all auxiliary structures need to be cleared
        List<AspectJVertex> newJVertices = new ArrayList<AspectJVertex>();
        for (Object jCell : result.values()) {
            if (jCell instanceof AspectJVertex) {
                AspectJVertex jVertex = ((AspectJVertex) jCell);
                jVertex.setJModel(this);
                jVertex.setNode(createAspectNode());
                newJVertices.add(jVertex);
            } else if (jCell instanceof AspectJEdge) {
                AspectJEdge jEdge = (AspectJEdge) jCell;
                jEdge.setJModel(this);
            }
        }
        for (AspectJVertex jVertex : newJVertices) {
            jVertex.setNodeFixed();
        }
        resetNodeNrs();
        return result;
    }

    /**
     * Returns a modification counter of this jModel.
     */
    final ChangeCount getModCount() {
        return this.jModelModCount;
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
        // synchronise the graph to match the edits,
        // unless the model is busy loading the graph
        if (!isLoading()) {
            // only reload if the edit changed the graph structure
            // (and not just the layout)
            boolean changed =
                edit.getInserted() != null && edit.getInserted().length > 0
                    || edit.getRemoved() != null
                    && edit.getRemoved().length > 0
                    || edit.getConnectionSet() != null
                    && !edit.getConnectionSet().isEmpty();
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
        if (GUI_DEBUG) {
            System.out.printf("Firing graph change in %s%n", getName());
            Groove.printStackTrace(System.out, false);
        }
        super.fireGraphChanged(source, edit);
    }

    /** Indicates if the model is currently executing {@link #loadGraph(Graph)}. */
    @Override
    final protected boolean isLoading() {
        return this.loading;
    }

    private void setLoading(boolean loading) {
        this.loading = loading;
    }

    /**
     * Callback factory method to create an empty, editable j-edge.
     */
    AspectJEdge computeJEdge() {
        AspectJEdge result = (AspectJEdge) createJEdge(null);
        // add a single, empty label so the edge will be displayed
        result.getUserObject().add("");
        return result;
    }

    /** 
     * Creates a new aspect node, with a fresh node number and
     * the graph role taken from the editor.
     */
    AspectNode createAspectNode() {
        return new AspectNode(createNewNodeNr(), getGraph().getRole());
    }

    /** Initialises the set {@link #usedNrs} with the currently used node numbers. */
    private boolean collectNodeNrs() {
        boolean result = this.usedNrs == null;
        if (result) {
            this.usedNrs = new HashSet<Integer>();
            for (Object root : getRoots()) {
                if (root instanceof GraphJVertex) {
                    this.usedNrs.add(((GraphJVertex) root).getNumber());
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

    /** Counter of the modifications to the jModel. */
    private final ChangeCount jModelModCount = new ChangeCount();
    /** Counter of the modifications to the graph. */
    private final ChangeCount graphModCount = new ChangeCount();
    /** Flag to indicate if the graph is being edited or not. */
    private boolean beingEdited = false;

    /** The resource model of the graph being edited. */
    private Derived<GraphBasedModel<?>> resource =
        new Derived<GraphBasedModel<?>>(this.graphModCount) {
            @Override
            protected GraphBasedModel<?> computeValue() {
                return getGrammar().createGraphModel(getGraph());
            }
        };

    /** The type graph of the graph being edited. */
    private Derived<TypeGraph> typeGraph = new Derived<TypeGraph>(
        this.graphModCount) {
        @Override
        protected TypeGraph computeValue() {
            TypeGraph result;
            GraphBasedModel<?> resourceModel = getResourceModel();
            if (resourceModel instanceof TypeModel) {
                try {
                    result = ((TypeModel) resourceModel).toResource();
                } catch (FormatException e) {
                    result =
                        ImplicitTypeGraph.newInstance(resourceModel.getLabels());
                }
            } else {
                result = getGrammar().getTypeGraph();
            }
            return result;
        }
    };

    /** Properties map of the graph being displayed or edited. */
    private GraphProperties properties;
    /** Mapping from errors to affected cells. */
    private Map<FormatError,AspectJCell> errorMap =
        new HashMap<FormatError,AspectJCell>();
    /** The set of used node numbers. */
    private Set<Integer> usedNrs;
    /** Flag indicating that we are loading a new aspect graph,
     * so we don't have to parse it.
     */
    private boolean loading;

    /** Role names (for the tool tips). */
    static final Map<AspectKind,String> ROLE_NAMES =
        new EnumMap<AspectKind,String>(AspectKind.class);
    /** Role descriptions (for the tool tips). */
    static final Map<AspectKind,String> ROLE_DESCRIPTIONS =
        new EnumMap<AspectKind,String>(AspectKind.class);

    static private final boolean GUI_DEBUG = false;

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