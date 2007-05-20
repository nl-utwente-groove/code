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
 * $Id: GraphJVertex.java,v 1.9 2007-05-20 07:17:49 rensink Exp $
 */
package groove.gui.jgraph;

import groove.algebra.Constant;
import groove.graph.Edge;
import groove.graph.Node;
import groove.graph.algebra.ValueNode;
import groove.util.Converter;
import groove.view.aspect.AttributeAspect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

/**
 * Extends DefaultGraphCell to use a Node as user object but
 * send the toString method to a set of self-edge labels.
 * Provides a convenience method to retrieve the user object as a Node.
 * Also provides a single default port for the graph cell,
 * and a convenience method to retrieve it.
 */
public class GraphJVertex extends JVertex {
    /**
     * Constructs a jnode on top of a graph node.
     * @param jModel the model in which this vertex exists
     * @param node the underlying graph node for this model node
     * @param vertexLabelled flag to indicate if the vertex can be labelled.
     * If not, then labels can be used to represent self-edges
     * @ensure getUserObject() == node, labels().isEmpty()
     */
    GraphJVertex(GraphJModel jModel, Node node, boolean vertexLabelled) {
        this.jModel = jModel;
        this.node = node;
        this.vertexLabelled = vertexLabelled;
    }

    /**
     * Constructs a model node on top of a graph node.
     * @param jModel the model in which this vertex exists
     * @param node the underlying graph node for this model node.
     * Note that this may be null.
     * @ensure getUserObject() == node, labels().isEmpty()
     */
    GraphJVertex(GraphJModel jModel, Node node) {
        this(jModel, node, false);
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
     * This method returns the actual graph node <i>modelled</i> by the vertex'
     * internal node.
     * For this implementation this is the same as {@link #getNode()}.
     */
    Node getActualNode() {
    	return getNode();
    }
    
    @Override
	public boolean isVisible() {
		return !isConstant() || jModel.isShowValueNodes();
	}

	/**
     * This implementation returns the node identity (set italic) if
     * required according to {@link #isShowNodeIdentity()}, followed by
     * the user object.
     */
    @Override
    public String getHtmlText() {
    	StringBuffer result = new StringBuffer();
    	// show the node identity if required
    	if (isShowNodeIdentity()) {
    		String id = getNodeIdentity();
    		if (id != null) {
    			result.append(italicTag.on(id));
    		}
    	}
    	String labels = getUserObject().toString();
    	// add the labels if nonempty
    	if (labels.length() > 0) {
    		// add a separator between node identity and label
    		if (result.length() > 0) {
                result.append(Converter.HTML_LINEBREAK); //HORIZONTAL_LINE;
    		}
    		result.append(Converter.toHtml(labels));
//    		result.append(strongTag.on(labels, true));
    	}
    	return result.toString();
    }

    /**
     * Indicates if the text of this vertex should include the identity
     * of the underlying node.
     * This implementation returns <code>true</code> if 
     * the model indicates that node identities are to be shown.
     * @see GraphJModel#isShowNodeIdentities()
     */
    boolean isShowNodeIdentity() {
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
	 * case the node is a non-variable ValueNode,
	 * and adds attribute labels if so required.
	 */
    @Override
	public Collection<String> getLabelSet() {
    	Collection<String> result;
    	String constantLabel = getConstantLabel();
		if (constantLabel == null) {
    		result = super.getLabelSet();
		} else {
    		// add the constant label in front of the existing labels
			result = new ArrayList<String>();
    		result.add(constantLabel);
    		result.addAll(super.getLabelSet());
    	}
		// add value attributes, if the model specifies this
		if (!jModel.isShowValueNodes()) {
			result.addAll(getDataLabels());
		}
		return result;
	}

	/** 
     * Returns a label derived from the constant represented by the actual node,
     * or <code>null</code> if the actual node does not represent a constant. 
     * Callback method used in {@link #getLabelSet()}.
     */
    String getConstantLabel() {
    	if (isConstant()) {
    		Constant constant = getConstant();
    		StringBuffer valueLabel = new StringBuffer();
			if (jModel.isShowAspects()) {
				String prefix = AttributeAspect.getValue(constant.algebra()).getPrefix();
				valueLabel.append(prefix);
			}
			valueLabel.append(constant);
    		// add the value label in front of the existing labels
    		return valueLabel.toString();
    	} else {
    		return null;
    	}
    }

	/**
	 * Returns an ordered set of labels derived from outgoing edges going to constants.
	 */
	Set<String> getDataLabels() {
		Set<String> dataLabels = new TreeSet<String>();
		for (Object edgeObject: getPort().getEdges()) {
			GraphJEdge jEdge = (GraphJEdge) edgeObject;
			if (jEdge.getSourceVertex() == this && jEdge.getTargetVertex().isConstant()) {
				for (Edge edge: jEdge.getEdgeSet()) {
					dataLabels.add(edge.label() + " = " + jEdge.getTargetVertex().getConstant());						
				}
			}
		}
		return dataLabels;
	}

	/** 
     * This implementation returns the label text of the object
     * (which is known to be an edge).
     */
	@Override
	public String getLabel(Object object) {
		Edge edge = (Edge) object;
		String text = edge.label().text();
		if (edge.opposite() == getNode()) {
			return text;
		} else {
			GraphJVertex oppositeVertex = jModel.getJVertex(edge.opposite());
			return text + " = " + oppositeVertex.getNodeIdentity();
		}
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
        if (!vertexLabelled) {
            getUserObject().add(edge);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Callback method to determine whether the underlying graph node 
     * stores a constant value.
     * @return <code>true</code> if {@link #getActualNode()} is a {@link ValueNode}
     * storing a constant value.
     * @see #getConstant()
     */
    boolean isConstant() {
    	return (getActualNode() instanceof ValueNode) && ((ValueNode) getActualNode()).hasValue();
    }
    
    /** 
     * Callback method to retrieve the constant stored in the underlying graph
     * node, in case the graph node is a constant value node.
     * @see ValueNode#getConstant()
     */
    Constant getConstant() {
    	if (getActualNode() instanceof ValueNode) {
    		return ((ValueNode) getActualNode()).getConstant();
    	} else {
    		return null;
    	}
    }

    /** 
     * Callback method yielding a string description of the underlying 
     * node, used for the node inscription in case node identities 
     * are to be shown.
     * If the node is a constant (see {@link #isConstant()}) the constant value
     * is returned; otherwise this implementation delegates to <code>getNode().toString()</code>.
     * The result may be <code>null</code>, if the node has no proper identity.
     * @return A node descriptor, or <code>null</code> if the node has no proper identity
     */
    String getNodeIdentity() {
    	if (isConstant()) {
    		return getConstant().toString();
    	} else if (getActualNode() == null) {
    		return null;
    	} else {
    		return getActualNode().toString();
    	}
    }
    
    /** This implementation includes the node number of the underlying node. */
    @Override
	String getNodeDescription() {
    	StringBuffer result = new StringBuffer("Node");
    	String id = getNodeIdentity();
    	if (id != null) {
    		result.append(" ");
    		result.append(italicTag.on(getNodeIdentity()));
    	}
		return result.toString();
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
    /** An indicator whether the vertex can be labelled (otherwise labels are self-edges). */
    private final boolean vertexLabelled;
    /** The graph node modelled by this jgraph node. */
    private final Node node;
	/** HTML tag to make text italic. */
    private static Converter.HTMLTag italicTag = Converter.createHtmlTag("i");
}