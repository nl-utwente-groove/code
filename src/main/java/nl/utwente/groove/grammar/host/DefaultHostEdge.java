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
package nl.utwente.groove.grammar.host;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.grammar.AnchorKind;
import nl.utwente.groove.grammar.type.TypeEdge;
import nl.utwente.groove.grammar.type.TypeLabel;
import nl.utwente.groove.graph.AEdge;

/**
 * Class that implements the edges of a host graph.
 * @author Arend Rensink
 */
@NonNullByDefault
public class DefaultHostEdge extends AEdge<HostNode,TypeLabel> implements HostEdge {
    /** Constructor for a typed edge.
     * @param simple indicates if this is a simple or multi-edge.
     */
    protected DefaultHostEdge(HostNode source, TypeEdge type, HostNode target, int nr,
                              boolean simple) {
        super(source, type.label(), target, nr);
        this.type = type;
        this.simple = simple;
    }

    // ------------------------------------------------------------------------
    // Overridden methods
    // ------------------------------------------------------------------------

    @Override
    public boolean isSimple() {
        return this.simple;
    }

    @Override
    protected boolean isTypeEqual(@Nullable Object obj) {
        return obj instanceof DefaultHostEdge;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        DefaultHostEdge other = (DefaultHostEdge) obj;
        if (getType() != other.getType()) {
            return false;
        }
        return true;
    }

    @Override
    public TypeEdge getType() {
        return this.type;
    }

    @Override
    public AnchorKind getAnchorKind() {
        return AnchorKind.EDGE;
    }

    /** Flag indicating whether this is a simple or multi-edge. */
    private final boolean simple;
    /** Non-{@code null} type of this edge. */
    private final TypeEdge type;
}
