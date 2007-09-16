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
 * $Id: DefaultEdge.java,v 1.7 2007-09-16 21:44:23 rensink Exp $
 */
package groove.graph;

import groove.util.TreeHashSet3;

import java.util.HashMap;
import java.util.Map;

/**
 * Default implementation of an (immutable) graph edge, as a triple consisting of
 * source and target nodes and an arbitrary label.
 * @author Arend Rensink
 * @version $Revision: 1.7 $ $Date: 2007-09-16 21:44:23 $
 */
final public class DefaultEdge extends AbstractBinaryEdge {
	/**
     * Constructs a new edge on the basis of a given source, label text and target.
     * The label created will be a {@link DefaultLabel}.
     * @param source source node of the new edge
     * @param text label text of the new edge
     * @param target target node of the new edge
     * @require <tt>source != null && text != null && target != null</tt>
     * @ensure <tt>source()==source</tt>,
     *         <tt>label().text().equals(text)</tt>,
     *         <tt>target()==target </tt>
     */
    private DefaultEdge(Node source, String text, Node target) {
        this(source,DefaultLabel.createLabel(text),target);
    }

    /**
     * Constructs a new edge on the basis of a given source, label and target.
     * @param source source node of the new edge
     * @param label label of the new edge
     * @param target target node of the new edge
     * @require <tt>source != null && target != null</tt>
     * @ensure <tt>source()==source</tt>,
     *         <tt>label()==label</tt>,
     *         <tt>target()==target </tt>
     */
    private DefaultEdge(Node source, Label label, Node target) {
        super(source, label, target);
    }

    // ----------------- Element methods ----------------------------

    /**
     * This implementation returns a {@link DefaultEdge}.
     */
    @Override
    @Deprecated
    public BinaryEdge newEdge(Node source, Label label, Node target) {
        return DefaultEdge.createEdge(source, label, target);
    }

    /** 
     * For efficiency, this implementation tests for object equality.
     * It is, however, considered an error if two distinct {@link DefaultEdge} objects have the 
     * same source and target nodes and the same label.
     */
    @Override
    public boolean equals(Object obj) {
        boolean result = this == obj;
        assert result || !super.equals(obj) : String.format("Distinct edges with same appearance (%s)", toString());
        return result;
    }

    /**
     * Creates an default edge from a given source node, label text and target node.
     * To save space, a set of standard instances is kept internally, and consulted
     * to return the same object whenever an edge is requested with the same end
     * nodes and label text.
     * @param source the source node of the new edge; should not be <code>null</code>
     * @param text the text of the new edge; should not be <code>null</code>
     * @param target the target node of the new edge; should not be <code>null</code>
     * @return an edge based on <code>source</code>, <code>text</code> and <code>target</code>;
     * the label is a {@link DefaultLabel}
     * @see #createEdge(Node, Label, Node)
     */
    static public DefaultEdge createEdge(Node source, String text, Node target) {
        return DefaultEdge.createEdge(source, DefaultLabel.createLabel(text), target);
    }
    
    /**
     * Creates an default edge from a given source node, label and target node.
     * To save space, a set of standard instances is kept internally, and consulted
     * to return the same object whenever an edge is requested with the same end
     * nodes and label text.
     * @param source the source node of the new edge; should not be <code>null</code>
     * @param label for the new edge; should not be <code>null</code>
     * @param target the target node of the new edge; should not be <code>null</code>
     * @return an edge based on <code>source</code>, <code>label</code> and <code>target</code>
     * @see #createEdge(Node, String, Node)
     */
    static public DefaultEdge createEdge(Node source, Label label, Node target) {
        assert source != null : "Source node of default edge should not be null";
        assert target != null : "Target node of default edge should not be null";
        assert label != null : "Label of default edge should not be null";
        DefaultEdge edge = new DefaultEdge(source, label, target);
        DefaultEdge result = DefaultEdge.edgeSet.put(edge);
        if (result == null) {
            result = edge;
//            DefaultEdge.edgeMap.put(edge, result);
        }
        return result;
    }

    /**
     * Returns the total number of default edges created.
     */
    static public int getEdgeCount() {
        return edgeSet.size();
    }

    /** Clears the store of canonical edges. */
    static public void clearEdgeMap() {
        edgeSet.clear();
    }
    
//    /**
//     * An identity map, mapping previously created instances of {@link DefaultEdge}
//     * to themselves. Used to ensure that edge objects are reused.
//     */
//    static private final Map<DefaultEdge,DefaultEdge> edgeMap = new HashMap<DefaultEdge,DefaultEdge>();
    /**
     * A identity map, mapping previously created instances of {@link DefaultEdge}
     * to themselves. Used to ensure that edge objects are reused.
     */
    static private final TreeHashSet3<DefaultEdge> edgeSet = new TreeHashSet3<DefaultEdge>(new TreeHashSet3.Equator() {
		public boolean areEqual(Object o1, Object o2) {
			DefaultEdge e1 = (DefaultEdge) o1;
			DefaultEdge e2 = (DefaultEdge) o2;
			return e1.source().equals(e2.source()) && e1.target().equals(e2.target()) && e1.label().equals(e2.label());
		}

		public int getCode(Object key) {
			return key.hashCode();
		}    	
    });
}
