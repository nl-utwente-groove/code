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
 * $Id$
 */
package groove.graph;

/**
 * Node in a type graph.
 * As added functionality w.r.t. default nodes, a type node stores its type
 * (which is a node type label).
 * @author Arend Rensink
 * @version $Revision $
 */
public class TypeNode implements Node {
    /** 
     * Constructs a new type node, with a given number and label.
     * The label must be a node type.
     * @see Label#isNodeType() 
     */
    public TypeNode(int nr, Label type) {
        assert type.isNodeType() : String.format(
            "Can't create type node for non-type label '%s'", type);
        this.nr = nr;
        this.type = type;
    }

    /** 
     * Creates a new type node, with a given number
     * and a type that is derived from the number.
     * @param nr the number of the new node
     */
    public TypeNode(int nr) {
        this(nr, DefaultLabel.createLabel("t0:" + nr, Label.NODE_TYPE));
    }

    /** 
     * Type nodes are equal if they have the same number.
     * However, it is an error to compare type nodes with the same number
     * and different types.
     */
    @Override
    public boolean equals(Object obj) {
        boolean result =
            obj instanceof TypeNode
                && ((TypeNode) obj).getNumber() == getNumber()
                && ((TypeNode) obj).getType().equals(getType());
        return result;
    }

    @Override
    public int hashCode() {
        return this.nr;
    }

    @Override
    public String toString() {
        return "T" + this.nr + ":" + this.type;
    }

    @Override
    public int getNumber() {
        return this.nr;
    }

    @Override
    public int compareTo(Element obj) {
        if (obj instanceof TypeNode) {
            return getNumber() - ((TypeNode) obj).getNumber();
        } else if (obj instanceof Node) {
            return getClass().toString().compareTo(obj.getClass().toString());
        } else {
            assert obj instanceof Edge;
            // nodes come before edges with the node as source
            return compareTo(((Edge) obj).source());
        }
    }

    /** Returns the type of this node. */
    public Label getType() {
        return this.type;
    }

    /** The number of this node. */
    private final int nr;
    /** The type of this node. */
    private final Label type;
}
