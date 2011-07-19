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

import groove.algebra.SignatureKind;

import java.awt.Color;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;

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
    public TypeNode(int nr, TypeLabel type) {
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
        this(nr, TypeLabel.createLabel(EdgeRole.NODE_TYPE, "t0:" + nr));
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

    /** Returns the default type node for a given data signature. */
    public static TypeNode getDataType(SignatureKind signature) {
        return dataTypeMap.get(signature);
    }

    /** Mapping from signatures to corresponding type nodes. */
    private static final Map<SignatureKind,TypeNode> dataTypeMap =
        new EnumMap<SignatureKind,TypeNode>(SignatureKind.class);
    static {
        for (SignatureKind sig : EnumSet.allOf(SignatureKind.class)) {
            dataTypeMap.put(sig, new TypeNode(0, TypeLabel.getLabel(sig)));
        }
    }
    /** Type node for non-data nodes in an untyped setting. */
    public static final TypeNode TOP_NODE = new TypeNode(0, TypeLabel.NODE);
    /** Type node for data nodes in an untyped setting. */
    public static final TypeNode TOP_DATA = new TypeNode(0, TypeLabel.DATA);
}
