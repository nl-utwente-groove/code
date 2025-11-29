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
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.util.Exceptions;
import nl.utwente.groove.util.parse.IdValidator;

/**
 * User-defined, possibly named predicate for a graph state.
 * @param name the name of the graph state: either {@code null} or a non-empty identifier prepended by {@link #PREFIX}.
 * @param prop the property tested for
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public record UserStateProperty(String name, String description, Predicate<GraphState> prop)
    implements StateProperty {
    /** Constructs a new property, with a given name and predicate.
     * The name should <i>not</i> include the {@link #PREFIX}
     * @param name name of the property: a non-empty identifier
     * not starting with {@link #PREFIX}
     * @param description description of the property
     * @param prop the wrapped predicate
     * @throws IllegalArgumentException if the name is not well-formatted
     */
    public UserStateProperty(String name, String description, Predicate<GraphState> prop) {
        if (name.isEmpty()) {
            throw Exceptions.illegalArg("Property name '%s' should not be empty");
        } else if (name.startsWith(PREFIX)) {
            throw Exceptions
                .illegalArg("Property name '%s' should not start with %s", name, PREFIX);
        } else if (!IdValidator.JAVA_ID.isValid(name)) {
            throw Exceptions.illegalArg("Property name '%s' should be an identifier");
        }
        this.name = PREFIX + name;
        this.description = description;
        this.prop = prop;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public boolean test(GraphState t) {
        return this.prop.test(t);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof UserStateProperty other)) {
            return false;
        }
        return name().equals(other.name());
    }

    @Override
    public int hashCode() {
        return name().hashCode();
    }

    @Override
    public String toString() {
        return name();
    }
}
