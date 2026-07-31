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

import java.util.Properties;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

/**
 * Compiled settings resource: a schema together with the entries checked
 * against it. The entries include the reserved
 * {@link SettingsModel#SCHEMA_KEY} key that selected the schema.
 * The interpretation of the entries is up to the clients of the schema.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public class Settings {
    /**
     * Constructs a settings object from a schema and a set of entries.
     * @param schema the schema the entries have been checked against
     * @param properties the settings entries
     */
    public Settings(SettingsSchema schema, Properties properties) {
        this.schema = schema;
        this.properties = properties;
    }

    /** Returns the schema of these settings. */
    public SettingsSchema getSchema() {
        return this.schema;
    }

    /** The schema of these settings. */
    private final SettingsSchema schema;

    /** Returns the entries of these settings. */
    public Properties getProperties() {
        return this.properties;
    }

    /**
     * Returns the value of a given settings key.
     * @return the value of {@code key}, or {@code null} if the key is not set
     */
    public @Nullable String getProperty(String key) {
        return this.properties.getProperty(key);
    }

    /** The entries of these settings. */
    private final Properties properties;

    @Override
    public String toString() {
        return getSchema().getName() + this.properties;
    }
}
