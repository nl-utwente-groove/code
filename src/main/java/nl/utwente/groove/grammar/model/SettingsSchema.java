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
import java.util.Map;
import java.util.Properties;

import org.eclipse.jdt.annotation.NonNullByDefault;

import nl.utwente.groove.util.parse.FormatErrorSet;

/**
 * Vocabulary of a settings resource: the set of keys it may declare, and the
 * admissible values of those keys.
 * A schema is identified by its name, which is the value of the reserved
 * {@link SettingsModel#SCHEMA_KEY} key of the settings resources using it.
 * Schemas are made known to the settings mechanism by registering them with
 * {@link SettingsSchemas}.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public interface SettingsSchema {
    /** Returns the name of this schema, under which it is registered. */
    public String getName();

    /**
     * Checks a set of settings entries against this schema.
     * The reserved {@link SettingsModel#SCHEMA_KEY} entry is included in the
     * properties, and should be disregarded by the implementation.
     * @param props the entries to be checked; non-{@code null}
     * @return a (non-{@code null}, possibly empty) set of errors in the entries
     */
    public FormatErrorSet check(Properties props);

    /**
     * Returns a map from the keys of this schema to their documentation.
     * Only intended for user guidance; a schema whose keys are not a fixed set
     * may well document none of them.
     * @return a (non-{@code null}, possibly empty) map from keys to descriptions
     */
    default public Map<String,String> getKeyDocMap() {
        return Collections.emptyMap();
    }
}
