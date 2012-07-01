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

import groove.trans.AnchorKind;

import java.util.Collections;
import java.util.Set;

/**
 * Edges used in type graphs.
 * @author Arend Rensink
 * @version $Revision $
 */
public class TypeEdge extends AbstractEdge<TypeNode,TypeLabel> implements
        TypeElement {
    /**
     * Constructs a new type edge, with explicit multiplicity.
     * Don't call directly; use {@link TypeFactory} methods instead.
     * @see TypeFactory#createEdge(TypeNode, Label, TypeNode)
     */
    TypeEdge(TypeNode source, TypeLabel label, TypeNode target, TypeGraph graph) {
        super(source, label, target);
        assert source.equals(target) || label.isBinary() : String.format(
            "Can't create %s label %s between distinct nodes %s and %s",
            label.getRole().getDescription(false), label, source, target);
        this.graph = graph;
    }

    /** Indicates if this edge type is abstract. */
    public final boolean isAbstract() {
        return this.abstractType;
    }

    /** Sets this edge type to abstract. */
    public final void setAbstract(boolean value) {
        this.abstractType = value;
    }

    /**
     * Gets the 'in' multiplicity of this edge type. May be <code>null</code>,
     * if no 'in' multiplicity exists.
     */
    public Multiplicity getInMult() {
        return this.inMult;
    }

    /**
     * Gets the 'out' multiplicity of this edge type. May be <code>null</code>,
     * if no 'out' multiplicity exists.
     */
    public Multiplicity getOutMult() {
        return this.outMult;
    }

    /** Sets the 'in' multiplicity of this edge type. */
    public void setInMult(Multiplicity inMult) {
        this.inMult = inMult;
    }

    /** Sets the 'out' multiplicity of this edge type. */
    public void setOutMult(Multiplicity outMult) {
        this.outMult = outMult;
    }

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
    public TypeGraph getGraph() {
        return this.graph;
    }

    @Override
    public boolean hasGraph() {
        return getGraph() != null;
    }

    @Override
    public Set<TypeEdge> getSubtypes() {
        if (hasGraph()) {
            return getGraph().getSubtypes(this);
        } else {
            return Collections.singleton(this);
        }
    }

    @Override
    public Set<TypeEdge> getSupertypes() {
        if (hasGraph()) {
            return getGraph().getSupertypes(this);
        } else {
            return Collections.singleton(this);
        }
    }

    @Override
    public AnchorKind getAnchorKind() {
        return AnchorKind.LABEL;
    }

    /** The type graph with which this edge is associated. */
    private final TypeGraph graph;

    /** Flag indicating if this edge type is abstract. */
    private boolean abstractType;

    /** In multiplicity of the edge type, if any. */
    private Multiplicity inMult;

    /** Out multiplicity of the edge type, if any. */
    private Multiplicity outMult;
}