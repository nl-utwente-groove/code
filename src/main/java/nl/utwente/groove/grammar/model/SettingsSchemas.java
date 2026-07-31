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
package nl.utwente.groove.grammar.model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.util.Exceptions;

/**
 * Registry of the known settings schemas, keyed by schema name.
 * A settings family is added to GROOVE by registering its schema here,
 * rather than by extending an enumeration.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public class SettingsSchemas {
    private SettingsSchemas() {
        // no instances of this utility class
    }

    /**
     * Registers a schema under its own name.
     * @throws IllegalArgumentException if a different schema is already
     * registered under that name
     */
    static public void register(SettingsSchema schema) {
        var old = schemaMap.put(schema.getName(), schema);
        if (old != null && old != schema) {
            throw Exceptions.illegalArg("Duplicate settings schema '%s'", schema.getName());
        }
    }

    /**
     * Returns the schema registered under a given name.
     * @return the schema with the given name, or {@code null} if there is none
     */
    static public @Nullable SettingsSchema get(String name) {
        return schemaMap.get(name);
    }

    /** Returns the names of all registered schemas, in registration order. */
    static public Set<String> getNames() {
        return Collections.unmodifiableSet(schemaMap.keySet());
    }

    /** Mapping from schema names to schemas, in registration order. */
    static private final Map<String,@Nullable SettingsSchema> schemaMap = new LinkedHashMap<>();
}
