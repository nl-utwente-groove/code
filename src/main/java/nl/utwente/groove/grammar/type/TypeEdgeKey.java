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

/**
 * Distinguishing elements of a type edge, namely source node label, consisting of
 * source node label, target node label and edge label
 * @author Arend Rensink
 * @version $Revision$
 */
public record TypeEdgeKey(TypeLabel sourceLabel, TypeLabel edgeLabel, TypeLabel targetLabel)
    implements TypeKey {
    /** Constructs a key from a given type edge by using its source, edge and target label. */
    public TypeEdgeKey(TypeEdge edge) {
        this(edge.source().label(), edge.label(), edge.target().label());
    }

    @Override
    public TypeLabel label() {
        return edgeLabel();
    }
}
