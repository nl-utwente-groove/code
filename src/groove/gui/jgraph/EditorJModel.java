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

import groove.gui.Editor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.ConnectionSet;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;

/**
 * A jmodel dedicated towards the editor. In particular, node and edge
 * attributes are set to editable, and the model has a name. Moreover, there is
 * some control as to the possible source and target points of new edges.
 * @author Arend Rensink
 * @version $Revision$
 */
public class EditorJModel extends JModel {
    /**
     * Creates a new, anonymous editor as a copy of a given j-model.
     * @param editor the associated editor
     */
    public EditorJModel(Editor editor) {
        super(editor.getOptions());
        this.editor = editor;
        JModel jModel = editor.getModel();
        if (jModel != null) {
            replace(jModel);
        }
    }

    /**
     * Replaces the content of this model by that of another. The cells of the
     * other model are copied, not aliased. Uses a single edit event to announce
     * the change to the listeners.
     */
    public void replace(JModel jModel) {
        Object[] oldRoots = getRoots(this);
        // map from the cells of jModel to their copies created for this model
        Map<JCell,JCell> toResultCellMap = new HashMap<JCell,JCell>();
        // the same for the ports
        Map<DefaultPort,DefaultPort> portMap =
            new HashMap<DefaultPort,DefaultPort>();
        // list of new jcells kept to make sure nodes go in front
        List<Object> newRoots = new ArrayList<Object>();
        ConnectionSet connections = new ConnectionSet();
        // new connections
        List<?> rootCells = jModel.getRoots();
        // first do the nodes
        for (Object jCell : rootCells) {
            if (!(jCell instanceof DefaultEdge)) {
                assert jCell instanceof GraphJVertex;
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
        for (Object jCell : rootCells) {
            if (jCell instanceof DefaultEdge) {
                assert jCell instanceof GraphJEdge;
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
        for (JCell cell : this.layoutableJCells) {
            // edges without extra points are not layoutable
            if (!(cell instanceof JEdge)
                || GraphConstants.getPoints(((JEdge) cell).getAttributes()).size() > 2) {
                this.layoutableJCells.add(toResultCellMap.get(cell));
            }
        }
        setProperties(jModel.getProperties());
        setName(jModel.getName());
    }

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
            ((EditableJVertex) result).getUserObject().setNumber(
                createNewNodeNr());
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

    @Override
    public boolean hasError(JCell cell) {
        return this.editor.hasError(cell);
    }

    /**
     * Callback factory method for a j-vertex instance for this j-model that is
     * a copy of an existing j-vertex.
     */
    private EditableJVertex copyJVertex(JVertex original) {
        EditableJVertex result = new EditableJVertex(original);
        result.getAttributes().applyMap(createJVertexAttr(result));
        return result;
    }

    /**
     * Callback factory method for a j-edge instance for this j-model that is a
     * copy of an existing j-edge.
     */
    private EditableJEdge copyJEdge(JEdge original) {
        EditableJEdge result = new EditableJEdge(original);
        result.getAttributes().applyMap(createJEdgeAttr(result));
        return result;
    }

    /**
     * Callback factory method to create an editable j-vertex. The return value
     * has attributes initialised through
     * {@link JModel#createJVertexAttr(JVertex)}.
     */
    EditableJVertex computeJVertex() {
        EditableJVertex result = new EditableJVertex(createNewNodeNr());
        result.getAttributes().applyMap(createJVertexAttr(result));
        return result;
    }

    /**
     * Callback factory method to create an editable j-edge. The return value
     * has attributes initialised through {@link JModel#createJEdgeAttr(JEdge)}.
     */
    EditableJEdge computeJEdge() {
        EditableJEdge result = new EditableJEdge();
        result.getAttributes().applyMap(createJEdgeAttr(result));
        return result;
    }

    /**
     * Overwrites the method to set the editable and moveable attributes to
     * <tt>true</tt>.
     */
    @Override
    protected AttributeMap createJVertexAttr(JVertex cell) {
        AttributeMap result = super.createJVertexAttr(cell);
        GraphConstants.setEditable(result, true);
        GraphConstants.setMoveable(result, true);
        return result;
    }

    /**
     * Overwrites the method to set the editable (dis)connectable attributes to
     * <tt>true</tt>.
     */
    @Override
    protected AttributeMap createJEdgeAttr(JEdge edge) {
        AttributeMap result = super.createJEdgeAttr(edge);
        GraphConstants.setEditable(result, true);
        GraphConstants.setConnectable(result, true);
        GraphConstants.setDisconnectable(result, true);
        return result;
    }

    /** The associated editor. */
    private final Editor editor;
    /** The set of used node numbers. */
    private Set<Integer> usedNrs;
}
