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
package nl.utwente.groove.io.external.format.ecore;

import java.util.Properties;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.annotation.HelpMap;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.grammar.model.SettingsContent;
import nl.utwente.groove.grammar.model.SettingsSchema;
import nl.utwente.groove.util.parse.FormatErrorSet;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Settings schema of the Ecore mapping: the vocabulary of {@link EcoreMapping},
 * against which the {@code ecore} settings resource is checked.
 * The schema validates choice keys and values only; the Ecore element paths
 * embedded in the keys are resolved by the porter, against the metamodel in
 * hand (this is what keeps entries for other metamodels harmless).
 * @author Arend Rensink
 */
@NonNullByDefault
public class EcoreMappingSchema implements SettingsSchema {
    private EcoreMappingSchema() {
        // singleton class
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public boolean isSingular() {
        // the porter requires a unique mapping resource; see EcoreMapping#of
        return true;
    }

    @Override
    public FormatErrorSet check(Properties props) {
        try {
            new EcoreMapping(props);
            return new FormatErrorSet();
        } catch (FormatException exc) {
            return exc.getErrors();
        }
    }

    @Override
    public FormatErrorSet check(@Nullable GrammarModel grammar, SettingsContent content) {
        // all mapping errors are about a single entry, so they can all carry
        // the position of the key they are about
        try {
            new EcoreMapping(content);
            return new FormatErrorSet();
        } catch (FormatException exc) {
            return exc.getErrors();
        }
    }

    @Override
    public String getExplanation() {
        return "Configuration of the Ecore import and export: global encoding options "
            + "plus per-element overrides, whose keys start with a (package-qualified) "
            + "Ecore element path. Entries that do not resolve against the metamodel "
            + "at hand are ignored, so one resource can serve several metamodels.";
    }

    @Override
    public String getNewText() {
        StringBuilder result = new StringBuilder(SettingsSchema.super.getNewText());
        result.append("# Available keys (uncomment and adapt):\n");
        for (EcoreKey key : EcoreKey.values()) {
            result.append("# ").append(key.templateLine()).append('\n');
        }
        return result.toString();
    }

    @Override
    public HelpMap getHelpMap() {
        return EcoreKey.getDocMap();
    }

    /** The singleton instance of this schema. */
    public static final EcoreMappingSchema INSTANCE = new EcoreMappingSchema();
    /** The name of this schema, as declared by the {@code $schema} entry of
     * its settings resources. */
    public static final String NAME = "ecore";

    /** Service provider contributing {@link #INSTANCE} to the schema registry. */
    public static class Provider implements SettingsSchema.Provider {
        @Override
        public SettingsSchema getSchema() {
            return INSTANCE;
        }
    }
}
