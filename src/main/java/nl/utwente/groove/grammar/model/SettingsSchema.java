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

import nl.utwente.groove.annotation.HelpMap;
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
     * Indicates if this schema is singular, meaning that it admits at most one
     * settings resource per grammar. All resources of an over-populated
     * singular schema are flagged with an error.
     * The default implementation returns {@code false}.
     */
    default public boolean isSingular() {
        return false;
    }

    /**
     * Returns a syntax help map documenting the keys of this schema, from
     * (HTML-formatted) key syntax lines to tool tips; shown in the settings
     * display. A schema whose keys are not a fixed set may document the key
     * forms rather than individual keys.
     * @return a (non-{@code null}, possibly empty) help map
     */
    default public HelpMap getHelpMap() {
        return new HelpMap();
    }

    /**
     * Returns a plain-text explanation of the general purpose of settings
     * resources of this schema, for user guidance; the empty string if there
     * is none. Used (among others) as the generated comment header of a new
     * settings resource.
     */
    default public String getExplanation() {
        return "";
    }

    /**
     * Returns the initial text for a new settings resource of this schema.
     * The default implementation generates the explanation as comment lines,
     * followed by a {@link SettingsModel#SCHEMA_KEY} entry; schemas with a
     * documented vocabulary are advised to append their keys as commented-out
     * example lines.
     */
    default public String getNewText() {
        StringBuilder result = new StringBuilder();
        // wrap the explanation into comment lines
        StringBuilder line = new StringBuilder();
        for (String word : getExplanation().split(" +")) {
            if (word.isEmpty()) {
                continue;
            }
            if (line.length() > 0 && line.length() + word.length() > 74) {
                result.append("# ").append(line).append('\n');
                line.setLength(0);
            }
            if (line.length() > 0) {
                line.append(' ');
            }
            line.append(word);
        }
        if (line.length() > 0) {
            result.append("# ").append(line).append('\n');
        }
        result.append(SettingsModel.SCHEMA_KEY).append(" = ").append(getName()).append('\n');
        return result.toString();
    }
}
