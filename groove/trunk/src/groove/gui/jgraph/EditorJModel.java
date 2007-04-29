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
 * $Id: EditorJModel.java,v 1.4 2007-04-29 09:22:22 rensink Exp $
 */
package groove.gui.jgraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.ConnectionSet;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;

/**
 * A jmodel dedicated towards the editor.
 * In particular, node and edge attributes are set to editable, and the model has a name.
 * Moreover, there is some control as to the possible source and target points
 * of new edges.
 * @author Arend Rensink
 * @version $Revision: 1.4 $
 */
public class EditorJModel extends JModel {
    /**
     * Creates an empty model with empty name.
     */
    public EditorJModel() {
        this("");
    }

    /**
     * Creates an empty model with a given name.
     * The name may be <tt>null</tt>, in which case the model is anonymous.
     * @ensure <tt>getName().equals(name)</tt>
     */
    public EditorJModel(String name) {
        setName(name);
    }

    /**
     * Creates a new, anonymous editor as a copy of a given j-model.
     * @param jModel the model to be copied.
     */
    public EditorJModel(GraphJModel jModel) {
        // map from the cells of jModel to their copies created for this model
        Map<JCell,JCell> toResultCellMap = new HashMap<JCell,JCell>();
        // list of new jcells kept to make sure nodes go in front
        List<Object> newRoots = new ArrayList<Object>();
        // the same for the ports
        Map<DefaultPort,DefaultPort> portMap = new HashMap<DefaultPort,DefaultPort>();
        ConnectionSet connections = new ConnectionSet();
        // new connections
        Object[] rootCells = getRoots(jModel);
        // first do the nodes
        for (int i = 0; i < rootCells.length; i++) {
            if (!(rootCells[i] instanceof DefaultEdge)) {
                assert rootCells[i] instanceof GraphJVertex;
                GraphJVertex node = (GraphJVertex) rootCells[i];
                // create node image and attributes
                JVertex nodeImage = copyJVertex(node);
                // add new port to port map (for correct edge cloning)
                portMap.put(node.getPort(), nodeImage.getPort());
                toResultCellMap.put(node, nodeImage);
                newRoots.add(nodeImage);
            }
        }
        // now do the edges
        for (int i = 0; i < rootCells.length; i++) {
            if (rootCells[i] instanceof DefaultEdge) {
                assert rootCells[i] instanceof GraphJEdge;
                GraphJEdge edge = (GraphJEdge) rootCells[i];
                // create edge image and attributes
                JEdge edgeImage = copyJEdge(edge);
//                assert GraphConstants.getPoints(edgeImage.getAttributes()).size() != 0 : "Edge "
//                        + edgeImage + " does not have points in attributes "
//                        + edgeImage.getAttributes();
                // connect up edge image
                assert edge.getSource() != null : "Edge " + edge + " has no source";
                connections.connect(edgeImage, portMap.get(edge.getSource()), true);
                if (edge.getTarget() != null)
                    connections.connect(edgeImage, portMap.get(edge.getTarget()), false);
                toResultCellMap.put(edge, edgeImage);
                newRoots.add(0,edgeImage);
            }
        }
        // ok, now let the new graph model have it
        insert(newRoots.toArray(), null, connections, null, null);
        // copy the layoutables
        for (JCell cell: layoutableJCells) {
            // edges without extra points are not layoutable
            if (!(cell instanceof JEdge)
                    || GraphConstants.getPoints(((JEdge) cell).getAttributes()).size() > 2) {
                layoutableJCells.add(toResultCellMap.get(cell));
            }
        }
        setProperties(jModel.getProperties());
        setName(jModel.getName());
    }

    /**
     * Creates a new editor model as a copy of a given jmodel, with a given name. 
     * The name may be <tt>null</tt> if the graph is to be anonymous.
     * @param name the name of the new j-model.
     * @param jModel the model to be copied.
     * @ensure <tt>getName().equals(name)</tt>
     */
    public EditorJModel(String name, GraphJModel jModel) {
        this(jModel);
        setName(name);
    }

    /**
	 * Replaces the content of this model by that of another.
	 * The cells of the other model are copied, not aliased.
	 * Uses a single edit event to announce the change to the listeners.
	 */
	public void replace(GraphJModel jModel) {
	    Object[] oldRoots = getRoots(this);
	    // map from the cells of jModel to their copies created for this model
	    Map<JCell,JCell> toResultCellMap = new HashMap<JCell,JCell>();
	    // the same for the ports
	    Map<DefaultPort,DefaultPort> portMap = new HashMap<DefaultPort,DefaultPort>();
	    // list of new jcells kept to make sure nodes go in front
	    List<Object> newRoots = new ArrayList<Object>();
	    ConnectionSet connections = new ConnectionSet();
	    // new connections
	    List<?> rootCells = jModel.getRoots();
	    // first do the nodes
	    for (Object jCell: rootCells) {
	        if (!(jCell instanceof DefaultEdge)) {
	            assert jCell instanceof GraphJVertex;
	            GraphJVertex jVertex = (GraphJVertex) jCell;
	            // create node image and attributes
	            JVertex nodeImage = copyJVertex(jVertex);
	            // add new port to port map (for correct edge cloning)
	            portMap.put(jVertex.getPort(), nodeImage.getPort());
	            toResultCellMap.put(jVertex, nodeImage);
	            newRoots.add(nodeImage);
	        }
	    }
	    // now do the edges
	    for (Object jCell: rootCells) {
	        if (jCell instanceof DefaultEdge) {
	            assert jCell instanceof GraphJEdge;
	            GraphJEdge jEdge = (GraphJEdge) jCell;
	            // create edge image and attributes
	            JEdge edgeImage = copyJEdge(jEdge);
	            // connect up edge image
	            assert jEdge.getSource() != null : "Edge " + jEdge + " has no source";
	            connections.connect(edgeImage, portMap.get(jEdge.getSource()), true);
	            if (jEdge.getTarget() != null)
	                connections.connect(edgeImage, portMap.get(jEdge.getTarget()), false);
	            toResultCellMap.put(jEdge, edgeImage);
	            newRoots.add(0,edgeImage);
	        }
	    }
	    // ok, now let the new graph model have it
	    GraphModelEdit edit = createEdit(newRoots.toArray(), oldRoots, null, connections, null, null);
	    edit.execute();
	    edit.end();
	    postEdit(edit);
	    // copy the layoutables
	    layoutableJCells.clear();
	    for (JCell cell: layoutableJCells) {
	        // edges without extra points are not layoutable
	        if (!(cell instanceof JEdge)
	                || GraphConstants.getPoints(((JEdge) cell).getAttributes()).size() > 2) {
	            layoutableJCells.add(toResultCellMap.get(cell));
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
    
    /** 
	 * Callback factory method for a j-vertex instance for this j-model
	 * that is a copy of an existing j-vertex. 
	 */
	protected EditableJVertex copyJVertex(JVertex original) {
		EditableJVertex result = new EditableJVertex(original);
		result.getAttributes().applyMap(createJVertexAttr(result));
		return result;
	}

	/** 
	 * Callback factory method for a j-edge instance for this j-model
	 * that is a copy of an existing j-edge. 
	 */
	protected EditableJEdge copyJEdge(JEdge original) {
		EditableJEdge result = new EditableJEdge(original);
		result.getAttributes().applyMap(createJEdgeAttr(result));
		return result;
	}
	
	/**
	 * Callback factory method to create an editable j-vertex.
	 * The return value has attributes initialised through {@link JModel#createJVertexAttr(JVertex)}.
	 */
	protected EditableJVertex computeJVertex() {
		EditableJVertex result = new EditableJVertex();
		result.getAttributes().applyMap(createJVertexAttr(result));
		return result;
	}

	/**
	 * Callback factory method to create an editable j-edge.
	 * The return value has attributes initialised through {@link JModel#createJEdgeAttr(JEdge)}.
	 */
	protected EditableJEdge computeJEdge() {
		EditableJEdge result = new EditableJEdge();
		result.getAttributes().applyMap(createJEdgeAttr(result));
		return result;
	}

	/**
	 * Overwrites the method to set the editable and moveable attributes to <tt>true</tt>.
	 */
	@Override
	protected AttributeMap createJVertexAttr(JVertex cell) {
	    AttributeMap result = super.createJVertexAttr(cell);
	    GraphConstants.setEditable(result, true);
	    GraphConstants.setMoveable(result, true);
	    return result;
	}

	/**
	 * Overwrites the method to set the editable (dis)connectable attributes to <tt>true</tt>.
	 */
	@Override
	protected AttributeMap createJEdgeAttr(JEdge edge) {
	    AttributeMap result = super.createJEdgeAttr(edge);
	    GraphConstants.setEditable(result, true);
	    GraphConstants.setConnectable(result, true);
	    GraphConstants.setDisconnectable(result, true);
	    return result;
	}
}
