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
 * Distinguishing elements from a {@link TypeElement}, consisting
 * of (only) node and edge type labels, without taking type graph identity
 * into account.
 * @author Arend Rensink
 * @version $Revision$
 */
public sealed interface TypeKey permits TypeEdgeKey, TypeNodeKey {
    /** Returns the main label of this type key. */
    TypeLabel label();
}
