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
import java.util.stream.Collectors;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.util.QualName;
import nl.utwente.groove.util.parse.FormatError;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Model of a settings resource: a text in Java properties syntax, whose schema
 * is implied by the location of the resource — the top-level folder it lives
 * in, or (in the <i>singleton form</i>, reserved for a singular schema; see
 * {@link SettingsSchema#isSingular()}) the name of the top-level file itself.
 * An optional {@link #SCHEMA_KEY} entry may re-declare the schema for the
 * reader's benefit; if present it must agree with the implied schema.
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
        // recheck when a resource kind inspected by the schema changes
        var schema = SettingsSchemas.get(name.get(0));
        if (schema != null) {
            addDependencies(schema.getDependencies().toArray(new ResourceKind[0]));
        }
    }

    /**
     * Returns the schema name of this settings resource, being the leading
     * segment of the resource name: the top-level folder the resource lives
     * in, or its own name if it is a top-level file. The name is not
     * guaranteed to be that of a registered schema; see {@link #getSchema()}.
     */
    public String getSchemaName() {
        return getQualName().get(0);
    }

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
        SettingsContent content;
        try {
            content = new SettingsContent(getProgram());
        } catch (IOException exc) {
            throw new FormatException("Can't parse settings file: %s", exc.getMessage());
        }
        String schemaName = getSchemaName();
        var schema = SettingsSchemas.get(schemaName);
        if (schema == null) {
            throw new FormatException(
                "Unknown settings schema '%s' (the leading name segment; known schemas: %s)",
                schemaName, String.join(", ", SettingsSchemas.getNames()));
        }
        String declared = content.properties().getProperty(SCHEMA_KEY);
        if (declared != null && !declared.trim().equals(schemaName)) {
            // the mismatch is an error of the $schema line itself
            throw new FormatException(new FormatError(
                "Declared schema '%s' differs from the schema '%s' implied by the resource location",
                declared.trim(), schemaName, content.numbers(SCHEMA_KEY)));
        }
        if (getQualName().size() == 1 && !schema.isSingular()) {
            // the singleton form is reserved for singular schemas: any other
            // schema may have several resources, which need a folder to live in
            throw new FormatException(
                "Settings of schema '%s' must live inside the '%s' folder", schemaName,
                schemaName);
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
        schema.check(getGrammar(), content).throwException();
        return new Settings(schema, content.properties());
    }

    /** Optional settings key re-declaring the schema of a settings resource;
     * if present, it must agree with the schema implied by the location of the
     * resource (see {@link #getSchemaName()}). */
    public static final String SCHEMA_KEY = "$schema";
}
