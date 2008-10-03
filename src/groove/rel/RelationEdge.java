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
 * $Id: RelationEdge.java,v 1.7 2008-01-30 09:32:32 iovka Exp $
 */
package groove.rel;

import groove.graph.AbstractBinaryEdge;
import groove.graph.DefaultLabel;
import groove.graph.Edge;
import groove.graph.Label;
import groove.graph.Node;

/**
 * An edge class that corresponds to a relation between nodes, rather than
 * a real edge of a graph. The label indicates the relation type; two edges 
 * are considered the same if they have the same end nodes and relation type.
 * @author Arend Rensink
 * @version $Revision $
 */
public class RelationEdge<V> extends AbstractBinaryEdge<Node,Label,Node> {
	/** Yields a string description of a {@link RelationType} value. */
	public static String typeToString(RelationType type) {
		return "["+type.getText()+"]";
	}

	/**
	 * Constructs a relation edge of a given type, with associated value <code>null</code>.
	 * @param source source node of the edge
	 * @param type type of the relation edge
	 * @param target target node of the edge
	 */
	public RelationEdge(Node source, RelationType type, Node target) {
		this(source, type, target, null);
	}

	/**
	 * Constructs a relation edge of a given type, with a given associated value.
	 * @param source source node of the edge
	 * @param type type of the relation edge
	 * @param target target node of the edge
	 * @param value associated value
	 */
	public RelationEdge(Node source, RelationType type, Node target, V value) {
		super(source, DefaultLabel.createLabel(typeToString(type)), target);
		this.value = value;
		this.type = type;
	}

	/**
	 * Returns the value associated with this edge.
	 * May be <code>null</code>.
	 */
	public V getValue() {
		return value;
	}
	
	/**
	 * Returns the type of relation of this edge.
	 */
	public RelationType getType() {
		return type;
	}

	/** Includes the hash code for the type and the value. */
	@Override
	protected int computeHashCode() {
		return super.computeHashCode() + (value == null ? 0 : value.hashCode()) + type.hashCode();
	}

	/**
	 * Apart from the super method, also tests for value equality.
	 * @see #isValueEqual(RelationEdge)
	 */
	@Override
	public boolean equals(Object obj) {
		return isTypeEqual(obj) && isEndEqual((Edge) obj) && isLabelEqual((Edge) obj) && isValueEqual((RelationEdge<?>) obj);
	}

	/**
	 * Tests for instance of {@link RelationType}
	 */
	@Override
	protected boolean isTypeEqual(Object obj) {
		return obj instanceof RelationEdge;
	}

	/** Callback method for testing equality of type and value. */
	protected boolean isValueEqual(RelationEdge<?> other) {
		return type.equals(other.getType()) && (value == null ? other.value == null : value.equals(other.getValue()));
	}

	/**
	 * The type of relation of this edge.
	 */
	private final V value;	
	
	/**
	 * The type of relation of this edge.
	 */
	private final RelationType type;
}
