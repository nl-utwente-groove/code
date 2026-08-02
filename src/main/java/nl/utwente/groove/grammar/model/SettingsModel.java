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

import static nl.utwente.groove.grammar.model.ResourceKind.SETTINGS;

import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;

import org.eclipse.jdt.annotation.NonNullByDefault;

import nl.utwente.groove.grammar.QualName;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Model of a settings resource: a text in Java properties syntax, whose schema
 * is determined by the leading segment of the resource name — the top-level
 * folder, or for a top-level file its own name (the singleton form of a
 * schema). An optional {@link #SCHEMA_KEY} entry may re-declare the schema for
 * the reader's benefit; if present it must agree with the name.
 * Settings do not contribute to grammar compilation; errors in a settings
 * resource are reported on the resource itself.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public class SettingsModel extends TextBasedModel<Settings> {
    /**
     * Constructs a settings model from a given settings text.
     * @param grammar the grammar model to which this settings resource belongs
     * @param name the name of the settings resource
     * @param program the settings text; non-{@code null}
     */
    public SettingsModel(GrammarModel grammar, QualName name, String program) {
        super(grammar, SETTINGS, name, program);
    }

    @Override
    Settings compute() throws FormatException {
        Properties props = new Properties();
        try {
            props.load(new StringReader(getProgram()));
        } catch (IOException exc) {
            throw new FormatException("Can't parse settings file: %s", exc.getMessage());
        }
        String schemaName = getQualName().get(0);
        var schema = SettingsSchemas.get(schemaName);
        if (schema == null) {
            throw new FormatException(
                "Unknown settings schema '%s' (the leading name segment; known schemas: %s)",
                schemaName, String.join(", ", SettingsSchemas.getNames()));
        }
        String declared = props.getProperty(SCHEMA_KEY);
        if (declared != null && !declared.trim().equals(schemaName)) {
            throw new FormatException(
                "Declared schema '%s' differs from schema '%s' implied by the resource name",
                declared.trim(), schemaName);
        }
        schema.check(props).throwException();
        return new Settings(schema, props);
    }

    /** Optional settings key re-declaring the schema of a settings resource;
     * if present, it must agree with the leading segment of the resource name. */
    public static final String SCHEMA_KEY = "$schema";
}
