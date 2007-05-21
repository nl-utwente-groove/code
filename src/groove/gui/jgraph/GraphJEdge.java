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
 * $Id: GraphJEdge.java,v 1.5 2007-05-21 22:19:16 rensink Exp $
 */
package groove.gui.jgraph;

import groove.graph.BinaryEdge;
import groove.graph.Edge;
import groove.graph.Node;
import groove.graph.algebra.AlgebraEdge;
import groove.graph.algebra.ProductEdge;
import groove.util.Converter;

import java.util.Collections;
import java.util.Set;

/**
 * Extends DefaultEdge to store a collection of graph Edges. The graph edges are stored as a Set in
 * the user object. In the latter case, toString() the user object is the empty string.
 */
public class GraphJEdge extends JEdge {
    /**
     * Constructs a model edge based on a graph edge.
     * The graph edge is required to have at least arity two; yet we cannot
     * rely on it being a {@link groove.graph.BinaryEdge}, it might be regular with some
     * pseudo-ends or it might be a {@link groove.view.aspect.AspectEdge}. 
     * @param edge the underlying graph edge of this model edge.
     * @require <tt>edge != null && edge.endCount() >= 0</tt> 
     * @ensure labels().size()==1, labels().contains(edge.label)
     *         source() == edge.source(), target() == edge.target()
     * @throws IllegalArgumentException if <code>edge.endCount() < 2</code> 
     */
    GraphJEdge(BinaryEdge edge) {
        this.source = edge.end(BinaryEdge.SOURCE_INDEX);
        this.target = edge.end(BinaryEdge.TARGET_INDEX);
        getUserObject().add(edge);
        getUserObject().setAllowEmptyLabelSet(false);
    }

    /**
     * Returns the common source of the underlying graph edges.
     */
    public Node getSourceNode() {
        return source;
    }

    /**
     * Returns the common target of the underlying graph edges.
     */
    public Node getTargetNode() {
        return target;
    }

    /**
     * Callback method to yield a string description of the source node.
     */
    String getSourceIdentity() {
        return source.toString();
    }

    /**
     * Callback method to yield a string description of the target node.
     */
    String getTargetIdentity() {
        return target.toString();
    }

    /**
     * Specialises the return type.
     */
    @Override
    public GraphJVertex getSourceVertex() {
        return (GraphJVertex) super.getSourceVertex();
    }

    /**
     * Specialises the return type.
     */
    @Override
    public GraphJVertex getTargetVertex() {
        return (GraphJVertex) super.getTargetVertex();
    }

	/**
     * Returns an unmodifiable view upon the set of underlying graph edges.
     */
    public Set<? extends Edge> getEdgeSet() {
        return Collections.unmodifiableSet(getUserObject());
    }

    /**
     * Returns an arbitrary edge from the set of underlying edges.
     */
    public BinaryEdge getEdge() {
        return getUserObject().iterator().next();
    }
    
    /** 
     * Returns the actual graph edge <i>modelled</i> by this j-edge.
     * For this implementation this is the same as {@link #getEdge()}.
     * @see #getEdge()
     */
    Edge getActualEdge() {
    	return getEdge();
    }
    
    /** 
     * This implementation returns the label text of the object
     * (which is known to be an edge).
     */
	@Override
	public String getLabel(Object object) {
		return ((Edge) object).label().text();
	}

	/** Specialises the return type of the method. */
    @Override
	public JUserObject<BinaryEdge> getUserObject() {
		return (JUserObject<BinaryEdge>) super.getUserObject();
	}
//
//    @Override
//	protected JUserObject<Edge> createUserObject() {
//    	return new JUserObject<Edge>(this, PRINT_SEPARATOR, false) {
//    	    /**
//    	     * Returns a collection of strings describing the objects contained in this user object.
//    	     * @return the string descriptions of the objects contained in this collection
//    	     * @ensure all elements of <tt>result</tt> are instances of <tt>String</tt>.
//    	     */
//    	    public Collection<String> getLabelSet() {
//    	        Set<String> result = new LinkedHashSet<String>();
//    	        for (T label: this) {
//    	        	result.add(getLabel(label));
//    	        }
//    	        return result;
//    	    }
//
//    	    @Override
//            public String getLabel(Edge edge) {
//            	return GraphJEdge.this.getLabel(edge);
//            }
//        };
//    }
    
    /**
     * This implementation does nothing: setting the user object directly is
     * not the right way to go about it.
     * Instead use <code>{@link #addEdge}</code> and <code>{@link #removeEdge}</code>.
     */
    @Override
    public void setUserObject(Object value) {
    	// does nothing
    }

    /**
     * Adds an edge to the underlying set of edges, if the edge is appropriate.
     * Indicates in its return value if the edge has indeed been added.
     * @param edge the edge to be added
     * @return <tt>true</tt> if the edge has been added; <tt>false</tt> if <tt>edge</tt>
     * is not compatible with this j-edge and cannot be added.
     * This implementation returns <tt>true</tt> always.
     * @require <tt>edge.source() == getSourceNode</tt> and <tt>edge.target() == getTargetNode()</tt>
     * @ensure if <tt>result</tt> then <tt>getEdgeSet().contains(edge)</tt>
     */
    public boolean addEdge(BinaryEdge edge) {
        getUserObject().add(edge);
        return true;
    }
    
    /**
     * Adds an edge to the set underlying graph edges.
     */
    public void removeEdge(Edge edge) {
        getUserObject().remove(edge);
    }

    @Override
	StringBuilder getEdgeDescription() {
    	StringBuilder result = super.getEdgeDescription();
    	result.append(" from ");
    	result.append(italicTag.on(getSourceIdentity()));
    	result.append(" to ");
    	result.append(italicTag.on(getTargetIdentity()));
    	return result;
	}
    
    /** This implementation recognises argument and operation edges. */
	@Override
	StringBuilder getEdgeKindDescription() {
		if (getActualEdge() instanceof AlgebraEdge) {
			return new StringBuilder("Argument edge");
		} else if (getActualEdge() instanceof ProductEdge) {
			return new StringBuilder("Operation edge");
		} else {
			return new StringBuilder("Edge");
		}
	}

	/** Source node of the underlying graph edges. */
    private final Node source;
    /** Target node of the underlying graph edges. */
    private final Node target;
    
    /**
     * HTML formatting tag for the tool tip text
     */
    static protected final Converter.HTMLTag italicTag = Converter.createHtmlTag("i");
}