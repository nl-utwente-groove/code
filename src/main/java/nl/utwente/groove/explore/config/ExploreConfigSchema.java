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
package nl.utwente.groove.explore.config;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.annotation.Help;
import nl.utwente.groove.annotation.HelpMap;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.grammar.model.SettingsSchema;
import nl.utwente.groove.util.parse.FormatErrorSet;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Settings schema of the exploration configuration: an {@code explore}
 * settings resource holds one {@link ExploreConfig} in properties syntax,
 * with one entry per non-default {@link ExploreKey}.
 * The schema checks entry structure, the cross-key consistency rules of the
 * feature model and (given a grammar) the grammar-dependent entry contents
 * such as rule names, condition formulas and edge labels.
 * @author Arend Rensink
 */
@NonNullByDefault
public class ExploreConfigSchema implements SettingsSchema {
    private ExploreConfigSchema() {
        // singleton class
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public FormatErrorSet check(Properties props) {
        return check(null, props);
    }

    @Override
    public FormatErrorSet check(@Nullable GrammarModel grammar, Properties props) {
        ExploreConfig config;
        try {
            config = ExploreConfig.fromProperties(props);
        } catch (FormatException exc) {
            return exc.getErrors();
        }
        FormatErrorSet result = config.check();
        if (grammar != null) {
            result.addAll(ExploreConfigChecker.check(grammar, config));
        }
        return result;
    }

    @Override
    public Set<ResourceKind> getDependencies() {
        // rule names and enabledness (which involves the properties) and
        // edge labels are validated against the grammar
        return Set.of(ResourceKind.RULE, ResourceKind.TYPE, ResourceKind.PROPERTIES);
    }

    @Override
    public String getExplanation() {
        return "Exploration configuration: one entry per feature of the exploration "
            + "feature model, with values of the form kind[:content]. Absent entries "
            + "take their default value. See the exploration dialog for an "
            + "interactive editor of the same configuration.";
    }

    @Override
    public String getNewText() {
        StringBuilder result = new StringBuilder(SettingsSchema.super.getNewText());
        result.append("# Available keys (uncomment and adapt):\n");
        for (ExploreKey key : ExploreKey.values()) {
            result
                .append("# ")
                .append(key.getName())
                .append(" = ")
                .append(key.getDefaultKind().getName())
                .append('\n');
        }
        return result.toString();
    }

    @Override
    public HelpMap getHelpMap() {
        var result = docMap;
        if (result == null) {
            docMap = result = computeDocMap();
        }
        return result;
    }

    private @Nullable HelpMap docMap;

    private static HelpMap computeDocMap() {
        var result = new HelpMap();
        Map<String,String> tokenMap = new LinkedHashMap<>();
        for (ExploreKey key : ExploreKey.values()) {
            tokenMap.put(key.getName(), key.getName());
        }
        for (ExploreKey key : ExploreKey.values()) {
            Help help = new Help(tokenMap);
            help.setSyntax(key.getName() + " = value");
            help.setHeader(key.getExplanation());
            // the parser description lists the kinds with their content syntax
            help.setBody(key.parser().getDescription().replaceFirst("^<body>", ""));
            result.add(help);
        }
        return result;
    }

    /** The singleton instance of this schema. */
    public static final ExploreConfigSchema INSTANCE = new ExploreConfigSchema();
    /** The name of this schema, doubling as the leading name segment of its
     * settings resources. */
    public static final String NAME = "explore";
}
