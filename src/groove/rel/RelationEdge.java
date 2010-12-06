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
 * $Id: RelationEdge.java,v 1.7 2008-01-30 09:32:32 iovka Exp $
 */
package groove.rel;

import groove.graph.AbstractEdge;
import groove.graph.DefaultLabel;
import groove.graph.Edge;
import groove.graph.Label;
import groove.graph.Node;

import java.util.Map;

/**
 * An edge class that corresponds to a relation between nodes, rather than a
 * real edge of a graph. The label indicates the relation type; two edges are
 * considered the same if they have the same end nodes and relation type.
 * @author Arend Rensink
 * @version $Revision $
 */
public final class RelationEdge extends AbstractEdge<Node,Label,Node> {
    /**
     * Constructs a relation edge of a given type, with associated value
     * <code>null</code>.
     * @param source source node of the edge
     * @param target target node of the edge
     */
    public RelationEdge(Node source, Node target) {
        this(source, target, null);
    }

    /**
     * Constructs a relation edge of a given type, with a given associated
     * value.
     * @param source source node of the edge
     * @param target target node of the edge
     * @param value associated value
     */
    public RelationEdge(Node source, Node target, Map<LabelVar,Label> value) {
        super(source, DefaultLabel.createLabel("match"), target);
        this.value = value;
    }

    /**
     * Returns the value associated with this edge. May be <code>null</code>.
     */
    public Map<LabelVar,Label> getValue() {
        return this.value;
    }

    /** Includes the hash code for the type and the value. */
    @Override
    protected int computeHashCode() {
        return super.computeHashCode()
            + (this.value == null ? 0 : this.value.hashCode());
    }

    /**
     * Apart from the super method, also tests for value equality.
     * @see #isValueEqual(RelationEdge)
     */
    @Override
    public boolean equals(Object obj) {
        return isTypeEqual(obj) && isEndEqual((Edge) obj)
            && isLabelEqual((Edge) obj) && isValueEqual((RelationEdge) obj);
    }

    @Override
    protected boolean isTypeEqual(Object obj) {
        return obj instanceof RelationEdge;
    }

    /** Callback method for testing equality of type and value. */
    protected boolean isValueEqual(RelationEdge other) {
        return this.value == null ? other.value == null
                : this.value.equals(other.getValue());
    }

    /**
     * The type of relation of this edge.
     */
    private final Map<LabelVar,Label> value;
}
