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
import groove.graph.Element;
import groove.graph.GraphInfo;
import groove.graph.GraphProperties;
import groove.graph.Label;
import groove.gui.Editor;
import groove.gui.layout.JEdgeLayout;
import groove.gui.layout.LayoutMap;

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
    public EditorJModel(Editor editor, DefaultGraph graph) {
        super(editor.getOptions());
        this.editor = editor;
        //        addGraphModelListener(this);
        load(graph);
    }

    @Override
    public void graphChanged(GraphModelEvent e) {
        System.out.println(e.getChange());
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<EditableJCell> getRoots() {
        return (List<EditableJCell>) super.getRoots();
    }

    /**
     * Replaces the content of this model by that of another. The cells of the
     * other model are copied, not aliased. Uses a single edit event to announce
     * the change to the listeners.
     */
    public void load(DefaultGraph graph) {
        Object[] oldRoots = getRoots().toArray();
        GraphJModel<?,?> jModel =
            GraphJModel.newInstance(graph, getOptions(), true);
        // map from the cells of jModel to their copies created for this model
        Map<JCell,JCell> toResultCellMap = new HashMap<JCell,JCell>();
        // the same for the ports
        Map<DefaultPort,DefaultPort> portMap =
            new HashMap<DefaultPort,DefaultPort>();
        // list of new jcells kept to make sure nodes go in front
        List<Object> newRoots = new ArrayList<Object>();
        ConnectionSet connections = new ConnectionSet();
        // first do the nodes
        for (GraphJCell<?,?> jCell : jModel.getRoots()) {
            if (jCell instanceof GraphJVertex) {
                GraphJVertex<?,?> jVertex = (GraphJVertex<?,?>) jCell;
                // create node image and attributes
                JVertex nodeImage = copyJVertex(jVertex);
                // add new port to port map (for correct edge cloning)
                portMap.put(jVertex.getPort(), nodeImage.getPort());
                toResultCellMap.put(jVertex, nodeImage);
                newRoots.add(nodeImage);
            }
        }
        // now do the edges
        for (GraphJCell<?,?> jCell : jModel.getRoots()) {
            if (jCell instanceof GraphJEdge) {
                GraphJEdge<?,?> jEdge = (GraphJEdge<?,?>) jCell;
                // create edge image and attributes
                JEdge edgeImage = copyJEdge(jEdge);
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
                    result.addEdge(node, label, node);
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
                    DefaultEdge edge = result.addEdge(source, label, target);
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
    private EditableJVertex copyJVertex(GraphJVertex<?,?> original) {
        EditableJVertex result = new EditableJVertex(this, original);
        result.getAttributes().applyMap(result.createAttributes(this));
        return result;
    }

    /**
     * Callback factory method for a j-edge instance for this j-model that is a
     * copy of an existing j-edge.
     */
    private EditableJEdge copyJEdge(GraphJEdge<?,?> original) {
        EditableJEdge result = new EditableJEdge(this, original);
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
}
