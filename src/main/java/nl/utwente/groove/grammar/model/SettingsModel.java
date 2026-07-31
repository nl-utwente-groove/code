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
 * Model of a settings resource: a text in Java properties syntax, with a
 * reserved {@link #SCHEMA_KEY} entry naming the schema its entries are
 * checked against.
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
        String schemaName = props.getProperty(SCHEMA_KEY);
        if (schemaName == null) {
            throw new FormatException("Settings file must declare a %s key", SCHEMA_KEY);
        }
        var schema = SettingsSchemas.get(schemaName);
        if (schema == null) {
            throw new FormatException("Unknown settings schema '%s' (known schemas: %s)", schemaName,
                String.join(", ", SettingsSchemas.getNames()));
        }
        schema.check(props).throwException();
        return new Settings(schema, props);
    }

    /** Reserved settings key declaring the schema of a settings resource. */
    public static final String SCHEMA_KEY = "$schema";
}
