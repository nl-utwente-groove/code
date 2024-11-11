/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.grammar.type;

import static nl.utwente.groove.graph.EdgeRole.BINARY;

import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.grammar.AnchorKind;
import nl.utwente.groove.graph.AEdge;
import nl.utwente.groove.graph.Label;
import nl.utwente.groove.util.line.Line;

/**
 * Edges used in type graphs.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public class TypeEdge extends AEdge<TypeNode,TypeLabel> implements TypeElement {
    /**
     * Constructs a new type edge, with explicit multiplicity.
     * Don't call directly; use {@link TypeFactory} methods instead.
     * @see TypeFactory#createEdge(TypeNode, Label, TypeNode)
     */
    TypeEdge(TypeNode source, TypeLabel label, TypeNode target, TypeGraph graph) {
        super(source, label, target);
        assert graph != null;
        assert source.equals(target) || label.getRole() == BINARY : String
            .format("Can't create %s label %s between distinct nodes %s and %s",
                    label.getRole().getDescription(false), label, source, target);
        this.graph = graph;
        this.key = new TypeEdgeKey(this);
    }

    @Override
    public TypeEdgeKey key() {
        return this.key;
    }

    private final TypeEdgeKey key;

    @Override
    public boolean isSimple() {
        return true;
    }

    /** Indicates if this edge type is composite. */
    public final boolean isComposite() {
        return this.isComposite;
    }

    /** Flag indicating if this edge type is composite. */
    private boolean isComposite;

    /** Sets this edge type to abstract. */
    public final void setAbstract(boolean value) {
        this.abstractType = value;
    }

    /** Indicates if this edge type is abstract. */
    public final boolean isAbstract() {
        return this.abstractType;
    }

    /** Flag indicating if this edge type is abstract. */
    private boolean abstractType;

    /** Sets this edge type to composite. */
    public final void setComposite(boolean value) {
        this.isComposite = value;
    }

    /** Sets the 'in' multiplicity of this edge type. */
    public void setInMult(@Nullable Multiplicity inMult) {
        this.inMult = inMult;
    }

    /**
     * Gets the 'in' multiplicity of this edge type. May be <code>null</code>,
     * if no 'in' multiplicity exists.
     */
    public @Nullable Multiplicity getInMult() {
        return this.inMult;
    }

    /** In multiplicity of the edge type, if any. */
    private @Nullable Multiplicity inMult;

    /** Sets the 'out' multiplicity of this edge type. */
    public void setOutMult(@Nullable Multiplicity outMult) {
        this.outMult = outMult;
    }

    /**
     * Gets the 'out' multiplicity of this edge type. May be <code>null</code>,
     * if no 'out' multiplicity exists.
     */
    public @Nullable Multiplicity getOutMult() {
        return this.outMult;
    }

    /** Out multiplicity of the edge type, if any. */
    private @Nullable Multiplicity outMult;

    /**
     * Tests if another type edge is either equal to this one or,
     * if this one is abstract, is a subtype.
     */
    public boolean subsumes(TypeEdge other) {
        if (this == other) {
            return true;
        }
        return isAbstract() && getGraph().isSubtype(other, this);
    }

    @Override
    public Line toLine() {
        return label().toLine();
    }

    @Override
    public String toParsableString() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String text() {
        return label().text();
    }

    @Override
    public int compareTo(Label o) {
        assert o instanceof TypeElement;
        int result;
        if (o instanceof TypeNode) {
            result = -o.compareTo(this);
        } else {
            TypeEdge edge = (TypeEdge) o;
            result = source().compareTo(edge.source());
            if (result == 0) {
                result = label().compareTo(edge.label());
            }
        }
        return result;
    }

    @Override
    public TypeGraph getGraph() {
        return this.graph;
    }

    /** The type graph with which this edge is associated. */
    private final TypeGraph graph;

    @Override
    public Set<TypeEdge> getSubtypes() {
        return getGraph().getSubtypes(this);
    }

    @Override
    public Set<TypeEdge> getSupertypes() {
        return getGraph().getSupertypes(this);
    }

    @Override
    public AnchorKind getAnchorKind() {
        return AnchorKind.LABEL;
    }
}
