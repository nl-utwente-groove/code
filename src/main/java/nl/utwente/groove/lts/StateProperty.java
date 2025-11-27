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
package nl.utwente.groove.lts;

import java.util.function.Predicate;

import org.eclipse.jdt.annotation.NonNullByDefault;

import nl.utwente.groove.graph.FlagLabel;
import nl.utwente.groove.graph.Label;

/**
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public interface StateProperty extends Predicate<GraphState>, Comparable<StateProperty> {
    @Override
    default public int compareTo(StateProperty o) {
        return name().compareTo(o.name());
    }

    /** Returns the name of this property.
     * This is guaranteed to start with #PREFIX followed by a non-empty identifier not starting with #PREFIX.
     */
    public String name();

    /** Returns the name of this property.
     * This is guaranteed to start with #PREFIX followed by a non-empty identifier not starting with #PREFIX.
     */
    default public Label label() {
        return new FlagLabel(name());
    }

    /** Default prefix of all property names. */
    static public final String PREFIX = "$";

    /** Tests if a given name is a state property name
     * (meaning that it starts with {@link #PREFIX}).
     */
    static public boolean isProperty(String name) {
        return name.startsWith(PREFIX);
    }
}
