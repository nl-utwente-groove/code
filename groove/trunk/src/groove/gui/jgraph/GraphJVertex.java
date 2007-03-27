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
 * $Id: GraphJVertex.java,v 1.2 2007-03-27 14:18:29 rensink Exp $
 */
package groove.gui.jgraph;

import groove.algebra.Constant;
import groove.graph.Edge;
import groove.graph.Node;
import groove.graph.algebra.ValueNode;
import groove.util.Converter;

import java.util.ArrayList;
import java.util.Collection;
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
    @Override
    public String getHtmlText() {
    	String result = "";
    	// show the node identity if required
    	if (isShowNodeIdentity()) {
    		result = italicTag.on(getNodeIdentity());
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
     * Callback mathod to yield a string description of the underlying 
     * node, used for the node inscription in case node identities 
     * are to be shown.
     * This implementation delegates to <code>getNode().toString()</code>.
     */
    protected String getNodeIdentity() {
    	return node.toString();
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
    @Override
    public String toString() {
    	return getNode().toString();
	}

	/**
	 * This implementation adds a constant identifier to the labels in
	 * case the node is a non-variable ValueNode.
	 */
    @Override
	public Collection<String> getLabelSet() {
    	String valueLabel = null;
    	if (getNode() instanceof ValueNode) {
    		Constant value = ((ValueNode) getNode()).getConstant();
    		if (value != null) {
    			valueLabel = value.toString();
    			if (jModel.isShowAspects()) {
    				valueLabel = value.prefix()+valueLabel;
    			}
    		}
    	}
    	if (valueLabel == null) {
    		return super.getLabelSet();
    	} else {
    		// add the value label in front of the existing labels
    		Collection<String> result = new ArrayList<String>();
    		result.add(valueLabel);
    		result.addAll(super.getLabelSet());
    		return result;
    	}
	}

	/** 
     * This implementation returns the label text of the object
     * (which is known to be an edge).
     */
	@Override
	public String getLabel(Object object) {
		return ((Edge) object).label().text();
	}
	

    /**
     * This implementation does nothing: setting the user object directly is
     * not the right way to go about it.
     */
    @Override
    public void setUserObject(Object value) {
    	// does nothing
    }

    /**
     * Specialises the return type of the super method.
     */
    @Override
    public JUserObject<Edge> getUserObject() {
    	return (JUserObject<Edge>) super.getUserObject();
    }
    
    /**
     * Returns an unmodifiable view on the underlying edge set.
     */
    public Set<? extends Edge> getSelfEdgeSet() {
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
		return "Node "+italicTag.on(getNodeIdentity());
	}

	/**
     * Removes an edge from the underlying edge set.
     * @param edge the edge to be removed
     * @ensure ! edges().contains(edge)
     */
    public void removeSelfEdge(Edge edge) {
        getUserObject().remove(edge);
    }

    /** The model in which this vertex exists. */
    private final GraphJModel jModel;
    /** An indicator whether the node may be used to store self-edges */
    private final boolean allowsSelfEdges;
    /** The graph node modelled by this jgraph node. */
    private final Node node;
}