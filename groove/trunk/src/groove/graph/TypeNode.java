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

import java.awt.Color;

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
     * Should only be called from {@link TypeFactory}.
     * @param nr the number of the type node
     * @param type the non-{@code null} type label; for untyped graphs, the
     * default is {@link TypeLabel#NODE}
     * @param graph the type graph to which this node belongs; may be {@code null}
     * @see Label#isNodeType() 
     */
    public TypeNode(int nr, TypeLabel type, TypeGraph graph) {
        assert type.isNodeType() : String.format(
            "Can't create type node for non-type label '%s'", type);
        this.nr = nr;
        this.type = type;
        this.graph = graph;
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
                && ((TypeNode) obj).getLabel().equals(getLabel());
        return result;
    }

    @Override
    public int hashCode() {
        return getNumber() ^ getLabel().hashCode();
    }

    @Override
    public String toString() {
        return getLabel().text();
    }

    @Override
    public int getNumber() {
        return this.nr;
    }

    @Override
    public int compareTo(Element obj) {
        if (obj instanceof TypeNode) {
            return getNumber() - ((TypeNode) obj).getNumber();
        } else {
            assert obj instanceof TypeEdge;
            // nodes come before edges with the node as source
            int result = compareTo(((TypeEdge) obj).source());
            if (result == 0) {
                result = -1;
            }
            return result;
        }
    }

    /** Returns the type of this node. */
    public TypeLabel getLabel() {
        return this.type;
    }

    /** Indicates if this node type is abstract. */
    public final boolean isAbstract() {
        return this.abstracted;
    }

    /** Sets this node type to abstract. */
    public final void setAbstract(boolean value) {
        this.abstracted = value;
    }

    /** Indicates if this node type is imported. */
    public final boolean isImported() {
        return this.imported;
    }

    /** Sets this node type to imported. */
    public final void setImported(boolean value) {
        this.imported = value;
    }

    /** Returns the (possibly {@code null}) label pattern associated with this type node. */
    public final LabelPattern getLabelPattern() {
        return this.pattern;
    }

    /** Sets the label pattern of this type node. */
    public final void setLabelPattern(LabelPattern pattern) {
        this.pattern = pattern;
    }

    /** Returns the (possibly {@code null}) colour of this type node. */
    public final Color getColor() {
        return this.colour;
    }

    /** Sets the colour of this type node. */
    public final void setColor(Color colour) {
        this.colour = colour;
    }

    /** 
     * Returns the type graph with which this node is associated, if any.
     * @return the associated type graph, or {@code null} if there is none.
     */
    public TypeGraph getGraph() {
        return this.graph;
    }

    /** The type graph with which this node is associated. */
    private final TypeGraph graph;

    /** Flag indicating if this node type is abstract. */
    private boolean abstracted;
    /** Flag indicating if this node type is imported from another type graph. */
    private boolean imported;
    /** The display colour of this node, if any. */
    private Color colour;
    /** The label pattern of this node, if any. */
    private LabelPattern pattern;
    /** The number of this node. */
    private final int nr;
    /** The type of this node. */
    private final TypeLabel type;
}
