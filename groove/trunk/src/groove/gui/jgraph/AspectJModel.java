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
import groove.graph.Node;
import groove.graph.TypeGraph;
import groove.gui.Editor;
import groove.gui.layout.JEdgeLayout;
import groove.gui.layout.LayoutMap;
import groove.util.Groove;
import groove.view.FormatError;
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
    /** 
     * Creates an new model, initially without a graph loaded.
     * @param editor if non-{@code null}, the model is editable
     */
    AspectJModel(AspectJVertex jVertexProt, AspectJEdge jEdgeProt, Editor editor) {
        super(jVertexProt, jEdgeProt);
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

    @Override
    public AspectJCell getJCell(Element elem) {
        return (AspectJCell) super.getJCell(elem);
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
        loadViewErrors();
        this.properties = GraphInfo.getProperties(graph, false);
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
        GraphRole role =
            this.editor == null ? getGraph().getRole() : this.editor.getRole();
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
                for (AspectEdge edge : jVertex.getJVertexLabels()) {
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
        GraphInfo.setLayoutMap(graph, layoutMap);
        GraphInfo.setProperties(graph, getProperties());
        graph.setFixed();
        setGraph(graph, nodeJVertexMap, edgeJCellMap);
        loadViewErrors();
        if (GUI_DEBUG) {
            System.out.printf("Graph resynchronised with model %s%n", getName());
            Groove.printStackTrace(System.out, false);
        }
    }

    /** Sets a type graph for this JModel.
     * The type graph is needed to correctly compute the errors in the graph.
     * @param type the new type graph; may be {@code null}
     */
    public void setType(TypeGraph type) {
        this.type = type;
        if (getGraph() != null) {
            syncGraph();
        }
    }

    /** 
     * Sets the extra-error flags of all the cells, based
     * on the errors in the view.
     */
    private void loadViewErrors() {
        for (AspectJCell jCell : getRoots()) {
            jCell.setExtraError(false);
        }
        this.errorMap.clear();
        View<?> view = getGraph().toView();
        if (this.type != null) {
            view.setType(this.type);
        }
        for (FormatError error : view.getErrors()) {
            for (Element errorObject : error.getElements()) {
                AspectJCell errorCell = getJCell(errorObject);
                if (errorCell == null && errorObject instanceof Edge) {
                    errorCell = getJCell(((Edge<?>) errorObject).source());
                }
                if (errorCell != null) {
                    this.errorMap.put(error, errorCell);
                    errorCell.setExtraError(true);
                    break;
                }
            }
        }
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
     * Sets the properties of this j-model to a given properties map.
     */
    public final void setProperties(GraphProperties properties) {
        this.properties = properties;
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
        // synchronise the graph to match the edits,
        // unless the model is busy loading the graph
        if (!this.loading) {
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
     * Callback factory method to create an editable GraphJVertex<?,?>.
     * The vertex is initialised with a new node 
     * obtained through {@link #createAspectNode()}.
     */
    AspectJVertex computeJVertex() {
        return (AspectJVertex) createJVertex(createAspectNode());
    }

    /** 
     * Creates a new aspect node, with a fresh node number and
     * the graph role taken from the editor.
     */
    private AspectNode createAspectNode() {
        return new AspectNode(createNewNodeNr(), this.editor.getRole());
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

    /** The associated editor. */
    private final Editor editor;
    /** 
     * The (possibly {@code null}) type graph, needed to correctly 
     * compute the errors in the graph.
     */
    private TypeGraph type;
    /** Properties map of the graph being displayed or edited. */
    private GraphProperties properties;
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