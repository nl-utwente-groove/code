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
import java.util.stream.Collectors;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.grammar.QualName;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Model of a settings resource: a text in Java properties syntax, whose schema
 * is declared by its {@link #SCHEMA_KEY} entry. Resource names are free; only
 * for a text without a {@link #SCHEMA_KEY} entry does the leading segment of
 * the resource name — the top-level folder, or for a top-level file its own
 * name — serve as a fallback schema name.
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
        // the program of a model is immutable, so the schema name is fixed
        String declared = null;
        try {
            Properties props = new Properties();
            props.load(new StringReader(program));
            declared = props.getProperty(SCHEMA_KEY);
        } catch (IOException exc) {
            // an unparseable text declares nothing; compute() reports the error
        }
        this.schemaDeclared = declared != null;
        this.schemaName = declared == null
            ? name.get(0)
            : declared.trim();
        // recheck when a resource kind inspected by the schema changes
        var schema = getSchema();
        if (schema != null) {
            addDependencies(schema.getDependencies().toArray(new ResourceKind[0]));
        }
    }

    /**
     * Returns the schema name of this settings resource: the value of the
     * {@link #SCHEMA_KEY} entry of its text if there is one, and otherwise the
     * leading segment of the resource name. The name is not guaranteed to be
     * that of a registered schema; see {@link #getSchema()}.
     */
    public String getSchemaName() {
        return this.schemaName;
    }

    /** The resolved schema name; see {@link #getSchemaName()}. */
    private final String schemaName;

    /** Flag indicating that {@link #schemaName} was declared by the
     * {@link #SCHEMA_KEY} entry rather than implied by the resource name. */
    private final boolean schemaDeclared;

    /**
     * Returns the schema of this settings resource, being the one registered
     * under {@link #getSchemaName()}; {@code null} if there is no such schema.
     */
    public @Nullable SettingsSchema getSchema() {
        return SettingsSchemas.get(getSchemaName());
    }

    /**
     * Indicates if this settings resource is currently active, as determined
     * by its schema (see {@link SettingsSchema#isActive}). A resource of an
     * unknown schema counts as inactive.
     */
    @Override
    public boolean isActive() {
        var schema = getSchema();
        return schema != null && schema.isActive(getGrammar(), getQualName());
    }

    @Override
    Settings compute() throws FormatException {
        Properties props = new Properties();
        try {
            props.load(new StringReader(getProgram()));
        } catch (IOException exc) {
            throw new FormatException("Can't parse settings file: %s", exc.getMessage());
        }
        String schemaName = getSchemaName();
        var schema = SettingsSchemas.get(schemaName);
        if (schema == null) {
            throw new FormatException("Unknown settings schema '%s' (%s; known schemas: %s)",
                schemaName, this.schemaDeclared
                    ? "declared by the '" + SCHEMA_KEY + "' entry"
                    : "the leading name segment",
                String.join(", ", SettingsSchemas.getNames()));
        }
        if (schema.isSingular()) {
            var candidates = SettingsSchemas.getResourceNames(getGrammar(), schema);
            if (candidates.size() > 1) {
                throw new FormatException(
                    "Schema '%s' admits only one settings resource per grammar; found %s",
                    schemaName,
                    candidates.stream().map(QualName::toString).collect(Collectors.joining(", ")));
            }
        }
        schema.check(getGrammar(), props).throwException();
        return new Settings(schema, props);
    }

    /** Settings key declaring the schema of a settings resource; if absent,
     * the leading segment of the resource name is used instead. */
    public static final String SCHEMA_KEY = "$schema";
}
