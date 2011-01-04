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

import groove.graph.Graph;
import groove.graph.GraphInfo;
import groove.graph.GraphProperties;
import groove.graph.GraphRole;
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

import org.jgraph.graph.AttributeMap;

/**
 * Implements jgraph's GraphModel interface on top of a {@link View}. This is
 * used to visualise rules and attributed graphs.
 * @author Arend Rensink
 * @version $Revision: 2982 $
 */
final public class AJModel extends GraphJModel<AspectNode,AspectEdge> {

    // --------------------- INSTANCE DEFINITIONS ------------------------

    /**
     * Creates an empty instance.
     * Initialise with {@link #loadGraph(Graph)} before using.
     */
    AJModel(Options options) {
        super(options);
        this.editor = null;
    }

    /** Constructor for an editable model. */
    AJModel(Editor editor) {
        super(editor.getOptions());
        this.editor = editor;
    }

    /** Specialises the type to a list of {@link GraphJCell}s. */
    @Override
    @SuppressWarnings("unchecked")
    public List<? extends AJCell> getRoots() {
        return (List<? extends AJCell>) super.getRoots();
    }

    /** Specialises the return type. */
    @Override
    public AspectGraph getGraph() {
        return (AspectGraph) super.getGraph();
    }

    @Override
    public void loadGraph(Graph<AspectNode,AspectEdge> graph) {
        this.loading = true;
        super.loadGraph(graph);
        for (AJCell root : getRoots()) {
            root.saveToUserObject();
        }
        this.loading = false;
    }

    /** 
     * Parses the current content of the model into an aspect graph.
     */
    public void loadFromModel(GraphRole role) {
        if (this.loading) {
            return;
        }
        Map<AspectNode,AJVertex> nodeJVertexMap =
            new HashMap<AspectNode,AJVertex>();
        Map<AspectEdge,AJCell> edgeJCellMap = new HashMap<AspectEdge,AJCell>();
        AspectGraph graph = new AspectGraph(getName(), role);
        LayoutMap<AspectNode,AspectEdge> layoutMap =
            new LayoutMap<AspectNode,AspectEdge>();
        for (AJCell jCell : getRoots()) {
            if (jCell instanceof AJVertex) {
                AJVertex jVertex = (AJVertex) jCell;
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
        for (AJCell jCell : getRoots()) {
            if (jCell instanceof AJEdge) {
                AJEdge jEdge = (AJEdge) jCell;
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
        for (AJVertex jVertex : nodeJVertexMap.values()) {
            jVertex.setNodeFixed();
        }
        graph.setFixed();
        GraphInfo.setLayoutMap(graph, layoutMap);
        GraphInfo.setProperties(graph, getProperties());
        setGraph(graph, nodeJVertexMap, edgeJCellMap);
    }

    @Override
    boolean isForEditor() {
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
        return isForEditor() || getOptionValue(Options.SHOW_VALUE_NODES_OPTION);
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
            if (element instanceof AJVertex) {
                AJVertex cell = (AJVertex) element;
                removables.addAll(cell.getPort().getEdges());
            }
        }
        super.remove(removables.toArray());
    }

    @Override
    public Map<?,?> cloneCells(Object[] cells) {
        Map<?,?> result;
        collectNodeNrs();
        // the following will call cloneCell(Object) for each individual cell
        result = super.cloneCells(cells);
        resetNodeNrs();
        // load the cloned cells from their user objects
        for (Object cell : result.values()) {
            if (cell instanceof AJCell) {
                ((AJCell) cell).loadFromUserObject(this.editor.getRole());
            }
        }
        // now finish the cloning by fixing the cloned JVertices
        for (Object cell : result.values()) {
            if (cell instanceof AJVertex) {
                ((AJVertex) cell).setNodeFixed();
            }
        }
        return result;
    }

    @Override
    protected Object cloneCell(Object cell) {
        Object result = cell;
        if (cell instanceof AJVertex) {
            // clone the vertex, making certain of a new node number
            AJVertex clone = computeJVertex();
            clone.setUserObject(((AJVertex) cell).getUserObject());
            result = clone;
        } else if (cell instanceof AJEdge) {
            AJEdge clone = computeJEdge();
            clone.setUserObject(((AJEdge) cell).getUserObject());
            result = clone;
        }
        return result;
    }

    /**
     * Callback factory method to create an editable JVertex.
     * The vertex is initialised with a new node whose number is 
     * obtained through {@link #createNewNodeNr()}.
     */
    AJVertex computeJVertex() {
        AspectNode newNode =
            new AspectNode(createNewNodeNr(), this.editor.getRole());
        return createJVertex(newNode);
    }

    /**
     * Callback factory method to create an editable j-edge. The return value
     * has attributes initialised through {@link JEdge#createAttributes()}.
     */
    AJEdge computeJEdge() {
        return new AJEdge(this);
    }

    /**
     * Overwrites the method so as to return a rule vertex.
     * @require <tt>edge instanceof RuleGraph.RuleNode</tt>
     */
    @Override
    protected AJVertex createJVertex(AspectNode node) {
        return new AJVertex(this, node);
    }

    /**
     * Overwrites the method so as to return a rule edge.
     * @require <tt>edge instanceof RuleGraph.RuleEdge</tt>
     */
    @Override
    protected AJEdge createJEdge(AspectEdge edge) {
        return new AJEdge(this, edge);
    }

    /** Initialises the set {@link #usedNrs} with the currently used node numbers. */
    private boolean collectNodeNrs() {
        boolean result = this.usedNrs == null;
        if (result) {
            this.usedNrs = new HashSet<Integer>();
            for (Object root : getRoots()) {
                if (root instanceof JVertex) {
                    this.usedNrs.add(((JVertex) root).getNumber());
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
    static public AJModel newInstance(AspectGraph graph, Options options) {
        assert graph != null;
        AJModel result = new AJModel(options);
        result.loadGraph(graph);
        return result;
    }

    /**
     * Creates a new model instance for a given editor, and initialises it
     * to a given graph.
     */
    static public AJModel newInstance(Editor editor, AspectGraph graph) {
        assert graph != null;
        AJModel result = new AJModel(editor);
        result.loadGraph(graph);
        return result;
    }

    /** A fixed, empty model. */
    public static final AJModel EMPTY_JMODEL = newInstance(
        AspectGraph.newInstance("", GraphRole.HOST), null);

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