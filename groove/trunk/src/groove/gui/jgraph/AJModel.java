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

import groove.graph.Element;
import groove.graph.Graph;
import groove.graph.GraphInfo;
import groove.graph.GraphProperties;
import groove.graph.GraphRole;
import groove.gui.Editor;
import groove.gui.Options;
import groove.gui.layout.JEdgeLayout;
import groove.gui.layout.LayoutMap;
import groove.view.FormatError;
import groove.view.View;
import groove.view.aspect.AspectEdge;
import groove.view.aspect.AspectGraph;
import groove.view.aspect.AspectKind;
import groove.view.aspect.AspectNode;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
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
public class AJModel extends GraphJModel<AspectNode,AspectEdge> {

    // --------------------- INSTANCE DEFINITIONS ------------------------

    /**
     * Creates a new aspect model instance on top of a given aspectual view.
     */
    AJModel(AspectGraph graph, Options options) {
        super(graph, options);
        this.editor = null;
    }

    /** Constructor for a dummy model. */
    AJModel(Editor editor) {
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
        AspectGraph aspectGraph = (AspectGraph) graph;
        List<FormatError> graphErrors = aspectGraph.toView().getErrors();
        if (graphErrors != null) {
            for (FormatError error : graphErrors) {
                for (Element errorObject : error.getElements()) {
                    JCell errorCell = null;
                    if (errorObject instanceof AspectNode) {
                        errorCell = getJCellForNode((AspectNode) errorObject);
                    } else if (errorObject instanceof AspectEdge) {
                        errorCell = getJCell(errorObject);
                        if (errorCell instanceof AspectJEdge
                            && ((AspectJEdge) errorCell).isDataEdgeSourceLabel()) {
                            errorCell =
                                ((AspectJEdge) errorCell).getSourceVertex();
                        }
                    }
                    if (errorCell instanceof AspectJEdge) {
                        ((AspectJEdge) errorCell).setError(true);
                    } else if (errorCell instanceof AspectJVertex) {
                        ((AspectJVertex) errorCell).setError(true);
                    }
                }
            }
        }
        this.loading = false;
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
    final boolean isShowAspects() {
        return getOptionValue(Options.SHOW_ASPECTS_OPTION);
    }

    /**
     * Indicates whether data nodes should be shown in the rule and lts views.
     */
    boolean isShowValueNodes() {
        return isForEditor() || getOptionValue(Options.SHOW_VALUE_NODES_OPTION);
    }

    /**
     * Overwrites the method so as to return a rule vertex.
     * @require <tt>edge instanceof RuleGraph.RuleNode</tt>
     */
    @Override
    protected GraphJVertex<AspectNode,AspectEdge> createJVertex(AspectNode node) {
        return new AJVertex(this, node);
    }

    /**
     * Overwrites the method so as to return a rule edge.
     * @require <tt>edge instanceof RuleGraph.RuleEdge</tt>
     */
    @Override
    protected GraphJEdge<AspectNode,AspectEdge> createJEdge(AspectEdge edge) {
        return new AJEdge(this, edge);
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
                nodeJVertexMap.put(jVertex.getNode(), jVertex);
                for (AspectEdge edge : jVertex.getSelfEdges()) {
                    edgeJCellMap.put(edge, jVertex);
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
                    if (!attrIsDefault) {
                        layoutMap.putEdge(edge, edgeAttr);
                    }
                }
                // add layout information if there is anything to be noted
                // about the edge
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

    @Override
    public Map<?,?> cloneCells(Object[] cells) {
        Map<?,?> result;
        collectNodeNrs();
        result = super.cloneCells(cells);
        resetNodeNrs();
        return result;
    }

    @Override
    protected Object cloneCell(Object cell) {
        Object result = super.cloneCell(cell);
        if (cell instanceof EditableJVertex) {
            ((EditableJVertex) result).setNumber(createNewNodeNr());
        }
        return result;
    }

    @Override
    protected Object cloneUserObject(Object userObject) {
        if (userObject == null) {
            return null;
        } else {
            return ((StringObject) userObject).clone();
        }
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