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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.annotation.Help;
import nl.utwente.groove.annotation.HelpMap;
import nl.utwente.groove.explore.feature.ExploreKey;
import nl.utwente.groove.explore.feature.Setting;
import nl.utwente.groove.grammar.GrammarProperties;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.grammar.model.SettingsContent;
import nl.utwente.groove.grammar.model.SettingsModel;
import nl.utwente.groove.grammar.model.SettingsSchema;
import nl.utwente.groove.util.QualName;
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
        return check(grammar, props, null);
    }

    @Override
    public FormatErrorSet check(@Nullable GrammarModel grammar, SettingsContent content) {
        return check(grammar, content.properties(), content);
    }

    /**
     * Checks a set of entries, optionally accompanied by the content they were
     * parsed from. If the content is given, every error that is about a single
     * entry — an unknown key, an unparsable value, or a grammar-dependent
     * content error — carries the position of that entry. The cross-key
     * consistency and engine-realisability errors are about a combination of
     * entries and get no position.
     */
    private FormatErrorSet check(@Nullable GrammarModel grammar, Properties props,
                                 @Nullable SettingsContent content) {
        ExploreConfig config;
        try {
            config = content == null
                ? ExploreConfig.fromProperties(props)
                : ExploreConfig.fromProperties(content);
        } catch (FormatException exc) {
            return exc.getErrors();
        }
        FormatErrorSet result = config.check();
        if (result.isEmpty()) {
            // a consistent configuration may still be unrealisable by the engine
            try {
                ExploreTypeConverter.toExploreType(config);
            } catch (FormatException exc) {
                result.addAll(exc.getErrors());
            }
        }
        if (grammar != null) {
            for (ExploreKey key : ExploreKey.values()) {
                var errors = ExploreConfigChecker.check(grammar, key, config.get(key));
                result.addAll(errors.extend(SettingsContent.numbers(content, key.getName())));
            }
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
    public boolean isActivatable() {
        return true;
    }

    @Override
    public boolean isActive(GrammarModel grammar, QualName name) {
        // the exploration property holds the local name within the folder
        var local = grammar.getProperties().getExplorationName();
        return local != null && name.equals(getResourceName(local));
    }

    @Override
    public void setActive(GrammarProperties properties, QualName name, boolean active) {
        if (active) {
            properties.setExplorationName(getLocalName(name));
        } else {
            properties.removeExplorationName();
        }
    }

    @Override
    public String getActivationText(boolean activate) {
        return activate
            ? "Set as the grammar's default exploration"
            : "Unset as the grammar's default exploration";
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
        var result = this.docMap;
        if (result == null) {
            this.docMap = result = computeDocMap();
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

    /**
     * Returns the text of an {@code explore} settings resource updated to
     * express a given configuration. If an original text is given, the update
     * is by targeted line edits, so that comments, ordering and hand-written
     * entries survive: the (first) line of each key whose setting differs is
     * replaced — a key reverting to its default keeps its line, with the
     * default spelled out — and missing non-default keys are appended;
     * otherwise a fresh minimal resource text is generated.
     */
    public static String setConfigText(@Nullable String oldText, ExploreConfig config) {
        if (oldText == null) {
            StringBuilder result = new StringBuilder();
            result.append(SettingsModel.SCHEMA_KEY).append(" = ").append(NAME).append('\n');
            for (ExploreKey key : ExploreKey.values()) {
                if (!config.isDefault(key)) {
                    result
                        .append(key.getName())
                        .append(" = ")
                        .append(key.parser().unparse(config.get(key)))
                        .append('\n');
                }
            }
            return result.toString();
        }
        List<String> lines = new ArrayList<>(Arrays.asList(oldText.split("\\R", -1)));
        // non-default keys that (so far) have no line of their own
        Set<ExploreKey> missing = EnumSet.noneOf(ExploreKey.class);
        for (ExploreKey key : ExploreKey.values()) {
            if (!config.isDefault(key)) {
                missing.add(key);
            }
        }
        Set<ExploreKey> seen = EnumSet.noneOf(ExploreKey.class);
        for (int i = 0; i < lines.size(); i++) {
            String trimmed = lines.get(i).trim();
            for (ExploreKey key : ExploreKey.values()) {
                String quotedName = Pattern.quote(key.getName());
                if (seen.contains(key) || !trimmed.matches(quotedName + "\\s*[=:].*")) {
                    continue;
                }
                seen.add(key);
                missing.remove(key);
                String oldValue = trimmed.replaceFirst("^" + quotedName + "\\s*[=:]\\s*", "");
                Setting desired = config.get(key);
                boolean same;
                try {
                    same = key.parser().parse(oldValue).equals(desired);
                } catch (FormatException exc) {
                    same = false;
                }
                if (!same) {
                    // a key reverting to its default keeps its line, with the
                    // default spelled out
                    String newValue = config.isDefault(key)
                        ? key.getDefaultKind().getName()
                        : key.parser().unparse(desired);
                    lines.set(i, key.getName() + " = " + newValue);
                }
                break;
            }
        }
        // strip a single trailing empty line before appending, restore after
        boolean endedWithNewline = !lines.isEmpty() && lines.get(lines.size() - 1).isEmpty();
        if (endedWithNewline) {
            lines.remove(lines.size() - 1);
        }
        for (ExploreKey key : ExploreKey.values()) {
            if (missing.contains(key)) {
                lines
                    .add(key.getName() + " = " + key.parser().unparse(config.get(key)));
            }
        }
        return String.join("\n", lines) + "\n";
    }

    /** The singleton instance of this schema. */
    public static final ExploreConfigSchema INSTANCE = new ExploreConfigSchema();
    /** The name of this schema, doubling as the top-level folder its settings
     * resources live in. Aliases the grammar-level constant, which is the
     * source of truth so that the {@code exploration} property can be
     * resolved without the grammar layer referencing this class. */
    public static final String NAME = GrammarModel.EXPLORE_SCHEMA_NAME;

    /** Service provider contributing {@link #INSTANCE} to the schema registry. */
    public static class Provider implements SettingsSchema.Provider {
        @Override
        public SettingsSchema getSchema() {
            return INSTANCE;
        }
    }
}
