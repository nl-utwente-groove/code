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
 * $Id: GraphJEdge.java,v 1.1.1.2 2007-03-20 10:42:46 kastenberg Exp $
 */
package groove.gui.jgraph;

import groove.graph.BinaryEdge;
import groove.graph.Edge;
import groove.graph.Node;
import groove.util.Converter;

import java.util.Collections;
import java.util.Set;

/**
 * Extends DefaultEdge to store a collection of graph Edges. The graph edges are stored as a Set in
 * the user object. In the latter case, toString() the user object is the empty string.
 */
public class GraphJEdge extends JEdge {
    /**
     * HTML formatting tag for the tool tip text
     */
    static protected final Converter.HTMLTag italicTag = Converter.createHtmlTag("i");

    /**
     * Constructs a model edge based on a graph edge.
     * The graph edge is required to have at least arity two; yet we cannot
     * rely on it being a {@link groove.graph.BinaryEdge}, it might be regular with some
     * pseudo-ends or it might be a {@link groove.trans.view.RuleEdge}. 
     * @param edge the underlying graph edge of this model edge.
     * @require <tt>edge != null && edge.endCount() >= 0</tt> 
     * @ensure labels().size()==1, labels().contains(edge.label)
     *         source() == edge.source(), target() == edge.target()
     * @throws IllegalArgumentException if <code>edge.endCount() < 2</code> 
     */
    protected GraphJEdge(Edge edge) {
//        super(new JUserObject(PRINT_SEPARATOR, EDIT_SEPARATOR) {
//            protected String getLabel(Object obj) {
//                assert obj instanceof Edge : "Edge set contains "+obj;
//            	return ((Edge) obj).label().text();
//            }
//            
//            protected Object createObject(String value) {
//                throw new UnsupportedOperationException();
//            }
//        });
        this.source = edge.end(BinaryEdge.SOURCE_INDEX);
        this.target = edge.end(BinaryEdge.TARGET_INDEX);
        getUserObject().add(edge);
        getUserObject().setAllowEmptyLabelSet(false);
//        update();
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
     * Returns an unmodifiable view upon the set of underlying graph edges.
     */
    public Set<Edge> getEdgeSet() {
        return Collections.unmodifiableSet(getUserObject());
    }

    /**
     * Returns an arbitrary edge from the set of underlying edges.
     */
    public Edge getEdge() {
        return getUserObject().iterator().next();
    }
    
    /** Specialises the return type of the method. */
    @Override
	public JUserObject<Edge> getUserObject() {
		return (JUserObject<Edge>) super.getUserObject();
	}

	protected JUserObject<Edge> createUserObject() {
    	return new JUserObject<Edge>(PRINT_SEPARATOR, false) {
            protected String getLabel(Edge obj) {
            	return obj.label().text();
            }
        };
    }
    
    /**
     * This implementation does nothing: setting the user object directly is
     * not the right way to go about it.
     * Instead use <code>{@link #addEdge}</code> and <code>{@link #removeEdge}</code>.
     */
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
    public boolean addEdge(Edge edge) {
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
	protected String getEdgeDescription() {
    	StringBuffer result = new StringBuffer(super.getEdgeDescription());
    	result.append(" from "+italicTag.on(getSourceNode()));
    	result.append(" to "+italicTag.on(getTargetNode()));
    	return result.toString();
	}

	/** Source node of the underlying graph edges. */
    private final Node source;
    /** Target node of the underlying graph edges. */
    private final Node target;
}