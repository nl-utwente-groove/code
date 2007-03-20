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
 * $Id: GraphJVertex.java,v 1.1.1.1 2007-03-20 10:05:31 kastenberg Exp $
 */
package groove.gui.jgraph;

import groove.graph.Edge;
import groove.graph.Node;
import groove.util.Converter;

import java.util.Collections;
import java.util.Set;

/**
 * Extends DefaultGraphCell to use a Node as user object but
 * send the toString method to a set of self-edge labels.
 * Provides a convenience method to retrieve the user object as a Node.
 * Also provides a single default port for the graph cell,
 * and a convenience method to retrieve it.
 */
public class GraphJVertex extends JVertex {
	/** HTML tag to make text italic. */
    protected static Converter.HTMLTag italicTag = Converter.createHtmlTag("i");
    /**
     * Constructs a jnode on top of a graph node.
     * @param jModel the model in which this vertex exists
     * @param node the underlying graph node for this model node.
     * @ensure getUserObject() == node, labels().isEmpty()
     */
    protected GraphJVertex(GraphJModel jModel, Node node, boolean allowsSelfEdges) {
        this.jModel = jModel;
        this.node = node;
        this.allowsSelfEdges = allowsSelfEdges;
    }

    /**
     * Constructs a model node on top of a graph node.
     * @param jModel the model in which this vertex exists
     * @param node the underlying graph node for this model node.
     * Note that this may be null.
     * @ensure getUserObject() == node, labels().isEmpty()
     */
    protected GraphJVertex(GraphJModel jModel, Node node) {
        this(jModel, node, true);
    }

    /**
     * Convenience method to retrieve this model node's user object as a Node.
     * @return this model node's user object as a Node
     * @ensure if getUserObject() instanceof Node then result == getUserObject()
     */
    public Node getNode() {
        return node;
    }
    
    /**
     * This implementation returns the node identity (set italic) if
     * required according to {@link #isShowNodeIdentity()}, followed by
     * the user object.
     */
    public String getHtmlText() {
    	String result = "";
    	// show the node identity if required
    	if (isShowNodeIdentity()) {
    		result = italicTag.on(node.toString());
    	}
    	String labels = getUserObject().toString();
    	// add the labels if nonempty
    	if (labels.length() > 0) {
    		// add a separator between node identity and label
    		if (result.length() > 0) {
                result += Converter.HTML_LINEBREAK; //HORIZONTAL_LINE;
    		}
    		result += strongTag.on(labels, true);
    	}
    	return result;
    }
    
    /**
     * Indicates if the text of this vertex should include the identity
     * of the underlying node.
     */
    protected boolean isShowNodeIdentity() {
    	// delegate the question to the j-model
    	return jModel.isShowNodeIdentities();
    }
    
    /**
     * This implementation forwards the query to the underlying graph node.
     * @see #getNode()
     */
    public String toString() {
    	return node.toString();
	}

//
//    /**
//     * Returns a description of the underlying label set.
//     * If isShowNodeIdentities(), returns a description of the
//     * underlying graph node instead.
//     */
//    public String toString() {
//        if (allowsSelfEdges) {
//            return strongTag.on(super.toString());
//        } else {
//            return italicTag.on(node.toString());
//        }
//    }

    /**
     * This implementation does nothing: setting the user object directly is
     * not the right way to go about it.
     */
    public void setUserObject(Object value) {
    	// does nothing
    }

    /**
     * 
     */
    public JUserObject<Edge> getUserObject() {
    	return (JUserObject<Edge>) super.getUserObject();
    }
    
    /**
     * Returns an unmodifiable view on the underlying edge set.
     */
    public Set<Edge> getSelfEdgeSet() {
        return Collections.unmodifiableSet(getUserObject());
    }

    /**
     * Adds an edge to the underlying self-edge set, if the edge is appropriate.
     * Indicates in its return value if the edge has indeed been added.
     * @param edge the edge to be added
     * @return <tt>true</tt> if the edge has been added; <tt>false</tt> if <tt>edge</tt>
     * is not compatible with this j-vertex and cannot be added.
     * This implementation returns <tt>true</tt> always.
     * @require <tt>edge.source() == edge.target() == getNode()</tt>
     * @ensure if <tt>result</tt> then <tt>edges().contains(edge)</tt>
     */
    public boolean addSelfEdge(Edge edge) {
        if (allowsSelfEdges) {
            getUserObject().add(edge);
            return true;
        } else {
            return false;
        }
    }

    /** This implementation includes the node number of the underlying node. */
    @Override
	protected String getNodeDescription() {
		return "Node "+italicTag.on(getNode());
	}

	/**
     * Removes an edge from the underlying edge set.
     * @param edge the edge to be removed
     * @ensure ! edges().contains(edge)
     */
    public void removeSelfEdge(Edge edge) {
        getUserObject().remove(edge);
    }
    

    protected JUserObject<Edge> createUserObject() {
    	return new JUserObject<Edge>(JUserObject.NEWLINE) {
            protected String getLabel(Edge obj) {
            	return obj.label().text();
            }
        };
    }
    
    /** The model in which this vertex exists. */
    private final GraphJModel jModel;
    /** An indicator whether the node may be used to store self-edges */
    private final boolean allowsSelfEdges;
    /** The graph node modelled by this jgraph node. */
    private final Node node;
}