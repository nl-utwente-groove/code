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

import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;

import nl.utwente.groove.grammar.host.AnchorValue;
import nl.utwente.groove.graph.Element;
import nl.utwente.groove.graph.Label;

/** Superinterface for {@link TypeNode} and {@link TypeEdge}.
 *
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public interface TypeElement extends Element, AnchorValue, Label {
    /**
     * Returns the type label of this type element.
     * Note that for type edges, the label does not completely determine
     * the edge, whereas for type nodes it does.
     */
    default public TypeLabel label() {
        return key().label();
    }

    /** Returns the type key for this type element, consisting of
     * just the label information and not the type graph or sub- or supertypes.
     */
    public TypeKey key();

    /**
     * Returns the type graph to which this type element belongs.
     * @return the associated type graph; non-{@code null}
     */
    public TypeGraph getGraph();

    /** Returns the (reflexively and transitively closed) set of subtypes of this type element. */
    public Set<? extends TypeElement> getSubtypes();

    /** Returns the (reflexively and transitively closed) set of supertypes of this type element. */
    public Set<? extends TypeElement> getSupertypes();
}
