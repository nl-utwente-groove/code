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
 * $Id: EditorJModel.java,v 1.9 2008-01-30 09:33:13 iovka Exp $
 */
package groove.gui.jgraph;

import groove.graph.DefaultEdge;
import groove.graph.DefaultGraph;
import groove.graph.DefaultNode;
import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.GraphInfo;
import groove.graph.GraphProperties;
import groove.graph.GraphRole;
import groove.graph.Label;
import groove.graph.Node;
import groove.gui.Editor;
import groove.gui.layout.JEdgeLayout;
import groove.gui.layout.LayoutMap;
import groove.view.FormatException;
import groove.view.aspect.AspectEdge;
import groove.view.aspect.AspectElement;
import groove.view.aspect.AspectGraph;
import groove.view.aspect.AspectLabel;
import groove.view.aspect.AspectNode;
import groove.view.aspect.AspectParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgraph.event.GraphModelEvent;
import org.jgraph.event.GraphModelListener;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.ConnectionSet;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;

/**
 * A jmodel dedicated towards the editor. In particular, node and edge
 * attributes are set to editable, and the model has a name. Moreover, there is
 * some control as to the possible source and target points of new edges.
 * @author Arend Rensink
 * @version $Revision$
 */
public class EditorJModel extends JModel implements GraphModelListener {
    /**
     * Creates a new editor JModel for a given graph
     * @param editor the associated editor
     * @param graph the graph to be displayed; non-{@code null}
     */
    public EditorJModel(Editor editor, AspectGraph graph) {
        super(editor.getOptions());
        setName(graph.getName());
        this.editor = editor;
        addGraphModelListener(this);
        loadGraph(graph);
    }

    @Override
    public void graphChanged(GraphModelEvent e) {
        if (!this.loading && !(e.getChange() instanceof JModel.RefreshEdit)) {
            //            parseGraph();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<EditableJCell> getRoots() {
        return (List<EditableJCell>) super.getRoots();
    }

    /**
     * Replaces the content of this model by a given aspect graph.
     * The graph as well as the map from graph elements to model JCells
     * can be retrieved afterwards by {@link #getGraph()} and
     * {@link #getElementMap()}.
     */
    public void loadGraph(AspectGraph graph) {
        this.loading = true;
        Object[] oldRoots = getRoots().toArray();
        AspectJModel jModel = createJModel(graph);
        Map<AspectElement,EditableJCell> elementMap =
            new HashMap<AspectElement,EditableJCell>();
        jModel.setForEditor();
        // map from the cells of jModel to their copies created for this model
        Map<JCell,EditableJCell> toResultCellMap =
            new HashMap<JCell,EditableJCell>();
        // the same for the ports
        Map<DefaultPort,DefaultPort> portMap =
            new HashMap<DefaultPort,DefaultPort>();
        // list of new jcells kept to make sure nodes go in front
        List<Object> newRoots = new ArrayList<Object>();
        ConnectionSet connections = new ConnectionSet();
        // first do the nodes
        for (AspectNode node : graph.nodeSet()) {
            AspectJVertex jVertex =
                (AspectJVertex) jModel.getJCellForNode(node);
            // create node image and attributes
            EditableJVertex nodeImage = copyJVertex(jVertex);
            // add new port to port map (for correct edge cloning)
            portMap.put(jVertex.getPort(), nodeImage.getPort());
            toResultCellMap.put(jVertex, nodeImage);
            elementMap.put(node, nodeImage);
            newRoots.add(nodeImage);
        }
        // now do the edges
        for (AspectEdge edge : graph.edgeSet()) {
            JCell jCell = jModel.getJCellForEdge(edge);
            if (jCell instanceof AspectJVertex) {
                elementMap.put(edge, toResultCellMap.get(jCell));
            } else if (jCell.isVisible() && jCell instanceof AspectJEdge) {
                AspectJEdge jEdge = (AspectJEdge) jCell;
                // create edge image and attributes
                EditableJEdge edgeImage = copyJEdge(jEdge);
                // connect up edge image
                assert jEdge.getSource() != null : "Edge " + jEdge
                    + " has no source";
                connections.connect(edgeImage, portMap.get(jEdge.getSource()),
                    true);
                if (jEdge.getTarget() != null) {
                    connections.connect(edgeImage,
                        portMap.get(jEdge.getTarget()), false);
                }
                toResultCellMap.put(jEdge, edgeImage);
                elementMap.put(edge, edgeImage);
                newRoots.add(0, edgeImage);
            }
        }
        // ok, now let the new graph model have it
        edit(newRoots.toArray(), oldRoots, null, connections, null, null);
        // copy the layoutables
        this.layoutableJCells.clear();
        for (JCell cell : jModel.layoutableJCells) {
            // edges without extra points are not layoutable
            if (!(cell instanceof JEdge)
                || GraphConstants.getPoints(((JEdge) cell).getAttributes()).size() > 2) {
                this.layoutableJCells.add(toResultCellMap.get(cell));
            }
        }
        setProperties(GraphInfo.getProperties(
            ((GraphJModel<?,?>) jModel).getGraph(), false));
        setName(jModel.getName());
        this.graph = graph;
        this.elementMap = elementMap;
        this.loading = false;
    }

    /**
     * Creates an appropriate JModel for a given aspect graph.
     */
    private AspectJModel createJModel(AspectGraph graph) {
        AspectJModel jModel = AspectJModel.newInstance(graph, getOptions());
        jModel.setForEditor();
        return jModel;
    }

    /** 
     * Converts this model to a plain Groove graph.
     * @see #toPlainGraph(Map)
     */
    public DefaultGraph toPlainGraph() {
        Map<Element,EditableJCell> dummyMap =
            new HashMap<Element,EditableJCell>();
        return toPlainGraph(dummyMap);
    }

    /**
     * Converts this j-model to a plain groove graph. Layout information is also
     * transferred. A plain graph is one in which the nodes and edges are
     * {@link DefaultNode}s and {@link DefaultEdge}s, and all further
     * information is in the labels.
     * @param elementMap receives the mapping from elements of the new graph
     * to root cells of this model
     */
    public DefaultGraph toPlainGraph(Map<Element,EditableJCell> elementMap) {
        DefaultGraph result = new DefaultGraph(getName());
        LayoutMap<DefaultNode,DefaultEdge> layoutMap =
            new LayoutMap<DefaultNode,DefaultEdge>();
        Map<JVertex,DefaultNode> nodeMap = new HashMap<JVertex,DefaultNode>();

        // Create nodes
        for (Object root : getRoots()) {
            if (root instanceof EditableJVertex) {
                EditableJVertex jVertex = (EditableJVertex) root;
                DefaultNode node = result.addNode(jVertex.getNumber());
                nodeMap.put(jVertex, node);
                elementMap.put(node, jVertex);
                layoutMap.putNode(node, jVertex.getAttributes());
                for (Label label : jVertex.getUserObject()) {
                    result.addEdge(node, label.toString(), node);
                }
            }
        }

        // Create Edges
        for (Object root : getRoots()) {
            if (root instanceof JEdge) {
                EditableJEdge jEdge = (EditableJEdge) root;
                DefaultNode source = nodeMap.get(jEdge.getSourceVertex());
                DefaultNode target = nodeMap.get(jEdge.getTargetVertex());
                assert target != null : "Edge with empty target: " + root;
                assert source != null : "Edge with empty source: " + root;
                AttributeMap edgeAttr = jEdge.getAttributes();
                // test if the edge attributes are default
                boolean attrIsDefault =
                    JEdgeLayout.newInstance(edgeAttr).isDefault();
                // parse edge text into label set
                for (Label label : jEdge.getUserObject()) {
                    DefaultEdge edge =
                        result.addEdge(source, label.toString(), target);
                    // add layout information if there is anything to be noted
                    // about the edge
                    if (!attrIsDefault) {
                        layoutMap.putEdge(edge, edgeAttr);
                    }
                    elementMap.put(edge, jEdge);
                }
            }
        }
        GraphInfo.setLayoutMap(result, layoutMap);
        GraphInfo.setProperties(result, getProperties());
        result.setRole(this.editor.getRole());
        return result;
    }

    /** 
     * Parses the current content of the model into an aspect graph.
     * Sets the attributes of the model accordingly.
     * The parse result can be retrieved using {@link #getGraph()} 
     * and {@link #getElementMap()}.
     */
    public void parseGraph() {
        // reset the errors
        for (EditableJCell jCell : getRoots()) {
            jCell.setError(false);
        }
        Map<AspectElement,EditableJCell> elementMap =
            new HashMap<AspectElement,EditableJCell>();
        AspectGraph graph = toAspectGraph(elementMap);
        AspectJModel jModel = createJModel(graph);
        for (Map.Entry<AspectElement,EditableJCell> entry : elementMap.entrySet()) {
            AspectElement element = entry.getKey();
            EditableJCell editableJCell = entry.getValue();
            // errors accumulate in the editable cell
            if (element.hasErrors()) {
                editableJCell.setError(true);
            }
            if (element instanceof Node) {
                AspectJVertex jCell =
                    (AspectJVertex) jModel.getJCellForNode((Node) element);
                ((EditableJVertex) entry.getValue()).setProxy(jCell);
            } else {
                JCell jCell = jModel.getJCellForEdge((Edge<?>) element);
                if (jCell instanceof AspectJEdge) {
                    ((EditableJEdge) entry.getValue()).setProxy((AspectJEdge) jCell);
                }
            }
        }
        this.graph = graph;
        this.elementMap = elementMap;
    }

    /**
     * Converts this j-model to an aspect graph. Layout information is also
     * transferred.
     * @param elementMap receives the mapping from elements of the new graph
     * to root cells of this model
     */
    private AspectGraph toAspectGraph(
            Map<AspectElement,EditableJCell> elementMap) {
        GraphRole graphRole = this.editor.getRole();
        AspectParser labelParser = AspectParser.getInstance(graphRole);
        AspectGraph result = new AspectGraph(getName(), graphRole);
        LayoutMap<AspectNode,AspectEdge> layoutMap =
            new LayoutMap<AspectNode,AspectEdge>();
        Map<JVertex,AspectNode> nodeMap = new HashMap<JVertex,AspectNode>();

        // Create nodes
        for (Object root : getRoots()) {
            if (root instanceof EditableJVertex) {
                EditableJVertex jVertex = (EditableJVertex) root;
                AspectNode node = result.addNode(jVertex.getNumber());
                nodeMap.put(jVertex, node);
                elementMap.put(node, jVertex);
                layoutMap.putNode(node, jVertex.getAttributes());
                for (Label label : jVertex.getUserObject()) {
                    AspectLabel newLabel = labelParser.parse(label.toString());
                    if (newLabel.isNodeOnly()) {
                        try {
                            node.setAspects(newLabel);
                        } catch (FormatException e) {
                            // do nothing
                            // (the error is recorded in the node itself)
                        }
                    } else {
                        result.addEdge(node, newLabel, node);
                    }
                }
            }
        }

        // Create Edges
        for (Object root : getRoots()) {
            if (root instanceof JEdge) {
                EditableJEdge jEdge = (EditableJEdge) root;
                AspectNode source = nodeMap.get(jEdge.getSourceVertex());
                AspectNode target = nodeMap.get(jEdge.getTargetVertex());
                assert target != null : "Edge with empty target: " + root;
                assert source != null : "Edge with empty source: " + root;
                AttributeMap edgeAttr = jEdge.getAttributes();
                // test if the edge attributes are default
                boolean attrIsDefault =
                    JEdgeLayout.newInstance(edgeAttr).isDefault();
                // parse edge text into label set
                for (Label label : jEdge.getUserObject()) {
                    AspectLabel newLabel = labelParser.parse(label.toString());
                    AspectEdge edge = result.addEdge(source, newLabel, target);
                    // add layout information if there is anything to be noted
                    // about the edge
                    if (!attrIsDefault) {
                        layoutMap.putEdge(edge, edgeAttr);
                    }
                    elementMap.put(edge, jEdge);
                }
            }
        }
        result.setFixed();
        GraphInfo.setLayoutMap(result, layoutMap);
        GraphInfo.setProperties(result, getProperties());
        return result;
    }

    /** 
     * Returns the aspect graph set using {@link #loadGraph(AspectGraph)}
     * or resulting from or to {@link #parseGraph()}, whichever came
     * last.
     */
    public AspectGraph getGraph() {
        return this.graph;
    }

    /** 
     * Returns the element map set using {@link #loadGraph(AspectGraph)}
     * or resulting from or to {@link #parseGraph()}, whichever came
     * last.
     */
    public Map<AspectElement,EditableJCell> getElementMap() {
        return this.elementMap;
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
            return ((EditableContent) userObject).clone();
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

    /**
     * Callback factory method for a j-vertex instance for this j-model that is
     * a copy of an existing j-vertex.
     */
    private EditableJVertex copyJVertex(AspectJVertex original) {
        return new EditableJVertex(this, original);
    }

    /**
     * Callback factory method for a j-edge instance for this j-model that is a
     * copy of an existing j-edge.
     */
    private EditableJEdge copyJEdge(AspectJEdge original) {
        EditableJEdge result = new EditableJEdge(this, original);
        //        result.setProxy(original);
        result.getAttributes().applyMap(result.createAttributes());
        return result;
    }

    /**
     * Callback factory method to create an editable j-vertex. The return value
     * has attributes initialised through
     * {@link JVertex#createAttributes(JModel)}.
     */
    EditableJVertex computeJVertex() {
        EditableJVertex result = new EditableJVertex(this, createNewNodeNr());
        result.getAttributes().applyMap(result.createAttributes(this));
        return result;
    }

    /**
     * Callback factory method to create an editable j-edge. The return value
     * has attributes initialised through {@link JEdge#createAttributes()}.
     */
    EditableJEdge computeJEdge() {
        EditableJEdge result = new EditableJEdge(this);
        result.getAttributes().applyMap(result.createAttributes());
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
    private AspectGraph graph;
    private Map<AspectElement,EditableJCell> elementMap;
}
