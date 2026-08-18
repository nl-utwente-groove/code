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
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.explore.feature.Bound;
import nl.utwente.groove.explore.feature.Cost;
import nl.utwente.groove.explore.feature.Count;
import nl.utwente.groove.explore.feature.ExploreKey;
import nl.utwente.groove.explore.feature.Frontier;
import nl.utwente.groove.explore.feature.Goal;
import nl.utwente.groove.explore.feature.NextState;
import nl.utwente.groove.explore.feature.Outcome;
import nl.utwente.groove.explore.feature.Setting;
import nl.utwente.groove.explore.feature.Shape;
import nl.utwente.groove.explore.ExploreType;
import nl.utwente.groove.explore.feature.Successor;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.grammar.model.Settings;
import nl.utwente.groove.grammar.model.SettingsContent;
import nl.utwente.groove.grammar.model.SettingsModel;
import nl.utwente.groove.util.Exceptions;
import nl.utwente.groove.util.QualName;
import nl.utwente.groove.util.parse.FormatErrorSet;
import nl.utwente.groove.util.parse.FormatException;
import nl.utwente.groove.util.parse.Parser;
import nl.utwente.groove.util.parse.StringHandler;

/**
 * Exploration configuration: a mapping from every {@link ExploreKey} to a
 * {@link Setting} for that key. A freshly created configuration maps every key
 * to its default setting; the textual form (see {@link #unparse()} and
 * {@link #parse(String)}) only lists the non-default entries, so the default
 * configuration is represented by the empty string.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public class ExploreConfig {
    /** Creates a configuration in which every key has its default setting. */
    public ExploreConfig() {
        this.map = new EnumMap<>(ExploreKey.class);
        for (var key : ExploreKey.values()) {
            this.map.put(key, key.getDefaultSetting());
        }
    }

    /** Creates a copy of a given configuration.
     * (The settings themselves are immutable, so a shallow copy suffices.)
     */
    public ExploreConfig(ExploreConfig original) {
        this.map = new EnumMap<>(original.map);
    }

    /** Returns the current setting for a given key. */
    public Setting get(ExploreKey key) {
        var result = this.map.get(key);
        assert result != null;
        return result;
    }

    /** Returns the kind of the current setting for a given key. */
    public Setting.Kind getKind(ExploreKey key) {
        return get(key).kind();
    }

    /**
     * Changes the setting for a given key.
     * @return the previous setting for the key
     * @throws IllegalArgumentException if the setting's kind does not belong to the key
     */
    public Setting put(ExploreKey key, Setting setting) throws IllegalArgumentException {
        if (!key.getKindType().isInstance(setting.kind())) {
            throw Exceptions
                .illegalArg("Setting kind '%s' does not belong to key '%s'", setting.kind(),
                            key.getName());
        }
        var result = this.map.put(key, setting);
        assert result != null;
        return result;
    }

    /** Tests whether a given key currently has its default setting. */
    public boolean isDefault(ExploreKey key) {
        return key.getDefaultSetting().equals(get(key));
    }

    /** The settings, one per key. */
    private final Map<ExploreKey,Setting> map;

    /**
     * Checks the cross-key consistency of this configuration, as prescribed by
     * the exploration feature model.
     * @return the (possibly empty) set of consistency errors
     */
    public FormatErrorSet check() {
        var result = new FormatErrorSet();
        var next = getKind(ExploreKey.NEXT);
        var successor = getKind(ExploreKey.SUCCESSOR);
        if (getKind(ExploreKey.FRONTIER) != Frontier.SINGLE) {
            // with a single-state frontier, the next-state selection is irrelevant
            if (next == NextState.OLDEST && successor != Successor.ALL
                && successor != Successor.ALL_RANDOM) {
                result
                    .add("Next-state selection '%s' requires all successors to be generated",
                         NextState.OLDEST.getName());
            }
            if (next == NextState.RANDOM && successor != Successor.ALL) {
                result
                    .add("Next-state selection '%s' requires successor selection '%s'",
                         NextState.RANDOM.getName(), Successor.ALL.getName());
            }
        }
        if (getKind(ExploreKey.FRONTIER) == Frontier.BEAM
            && get(ExploreKey.FRONTIER).content() instanceof Integer size && size < 2) {
            result.add("Beam frontier size %s should be larger than 1", size);
        }
        var goal = getKind(ExploreKey.GOAL);
        if ((goal == Goal.NONE || goal == Goal.ANY || goal == Goal.FINAL)
            && getKind(ExploreKey.OUTCOME) == Outcome.VIOLATE) {
            result
                .add("Goal '%s' cannot be combined with outcome '%s'", goal.getName(),
                     Outcome.VIOLATE.getName());
        }
        if (goal == Goal.NONE && getKind(ExploreKey.COUNT) != Count.ALL) {
            result
                .add("Goal '%s' yields no results, so result count must be '%s'",
                     Goal.NONE.getName(), Count.ALL.getName());
        }
        if (goal == Goal.NONE && getKind(ExploreKey.SHAPE) == Shape.TRACE) {
            result
                .add("Goal '%s' yields no results, so there are no traces to collect",
                     Goal.NONE.getName());
        }
        if (getKind(ExploreKey.COUNT) == Count.COUNT
            && get(ExploreKey.COUNT).content() instanceof Integer count && count < 2) {
            result.add("Result count %s should be larger than 1", count);
        }
        if (getKind(ExploreKey.BOUND) == Bound.COST && getKind(ExploreKey.COST) == Cost.NONE) {
            result.add("Bound '%s' requires a transition cost", Bound.COST.getName());
        }
        return result;
    }

    /**
     * Converts this configuration to its textual form: a space-separated list
     * of <i>key</i>{@code =}<i>value</i> pairs for the non-default entries.
     * Values containing spaces are quoted.
     */
    public String unparse() {
        StringBuilder result = new StringBuilder();
        for (var entry : this.map.entrySet()) {
            var key = entry.getKey();
            if (isDefault(key)) {
                continue;
            }
            if (result.length() > 0) {
                result.append(' ');
            }
            result.append(key.getName());
            result.append(ASSIGN);
            String value = key.parser().unparse(entry.getValue());
            if (value.chars().anyMatch(c -> Character.isWhitespace(c) || c == QUOTE)) {
                value = StringHandler.toQuoted(value, QUOTE);
            }
            result.append(value);
        }
        return result.toString();
    }

    /**
     * Parses a configuration from its textual form (see {@link #unparse()}).
     * Keys that do not occur in the text get their default setting.
     * @throws FormatException if the text contains an unknown or duplicate
     * key, or a value that is not parsable for its key
     */
    public static ExploreConfig parse(String text) throws FormatException {
        var result = new ExploreConfig();
        var errors = new FormatErrorSet();
        var seen = EnumSet.noneOf(ExploreKey.class);
        for (String token : splitTokens(text)) {
            int pos = token.indexOf(ASSIGN);
            if (pos < 0) {
                errors.add("Token '%s' is not of the form key%svalue", token, ASSIGN);
                continue;
            }
            String name = token.substring(0, pos);
            ExploreKey key = keyMap.get(name);
            if (key == null) {
                errors.add("Unknown exploration key '%s'", name);
                continue;
            }
            if (!seen.add(key)) {
                errors.add("Duplicate exploration key '%s'", name);
                continue;
            }
            String value = token.substring(pos + 1);
            if (!value.isEmpty() && value.charAt(0) == QUOTE) {
                value = StringHandler.toUnquoted(value, QUOTE);
            }
            putParsed(result, errors, key, value);
        }
        errors.throwException();
        return result;
    }

    /**
     * Splits a configuration text into its whitespace-separated tokens,
     * treating (escape-aware) quoted sections as atomic. Deliberately not
     * based on {@link StringHandler#splitExpr}, which also treats bracket
     * characters specially and so would choke on values containing (say)
     * comparison operators.
     */
    private static List<String> splitTokens(String text) throws FormatException {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean quoted = false;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (quoted && c == StringHandler.ESCAPE_CHAR && i + 1 < text.length()) {
                current.append(c);
                current.append(text.charAt(i + 1));
                i++;
            } else if (c == QUOTE) {
                quoted = !quoted;
                current.append(c);
            } else if (Character.isWhitespace(c) && !quoted) {
                if (current.length() > 0) {
                    result.add(current.toString());
                    current.setLength(0);
                }
            } else {
                current.append(c);
            }
        }
        if (quoted) {
            throw new FormatException("Unbalanced quote in '%s'", text);
        }
        if (current.length() > 0) {
            result.add(current.toString());
        }
        return result;
    }

    /**
     * Returns the default exploration configuration of a given grammar: the
     * content of the settings resource named by the {@code exploration}
     * property, the configuration equivalent of the legacy exploration
     * strategy key if only that is set and expressible, or the default
     * configuration otherwise (including when the reference does not
     * resolve; those errors are reported through the property checker).
     * This is the configuration-expressible projection of
     * {@link ExploreType#ofGrammar}: a legacy key holding one of the
     * dedicated exploration types projects to the default configuration.
     */
    public static ExploreConfig ofGrammar(GrammarModel grammar) {
        var local = grammar.getProperties().getExplorationName();
        if (local == null) {
            // fall back to the legacy key, if its value is expressible
            return ExploreType
                .ofLegacy(grammar.getProperties()) instanceof ConfiguredExploreType configured
                    ? new ExploreConfig(configured.getConfig())
                    : new ExploreConfig();
        }
        try {
            return ofResource(grammar, local);
        } catch (FormatException exc) {
            // reported on the resource and by the property checker
            return new ExploreConfig();
        }
    }

    /**
     * Resolves an exploration reference to the content of the referenced
     * settings resource of a given grammar.
     * @param localName the local name held by the {@code exploration}
     * property, i.e., the resource name within the {@code explore} folder
     * @throws FormatException if the resource is missing or erroneous
     */
    public static ExploreConfig ofResource(GrammarModel grammar,
                                           QualName localName) throws FormatException {
        Settings settings = grammar.getExploreSettings(localName).toResource();
        // a resource inside the 'explore' folder is of the explore schema by
        // construction; a contradicting declaration fails toResource() above
        assert settings.getSchema() == ExploreConfigSchema.INSTANCE;
        return fromProperties(settings.getProperties());
    }

    /**
     * Parses a configuration from java-properties entries, as stored in an
     * {@code explore} settings resource: one entry per exploration key, with
     * the same value syntax as in the single-line form (but no quoting, since
     * every entry has its own line). Keys that do not occur get their default
     * setting; the reserved {@code $schema} entry is ignored.
     * @throws FormatException if the entries contain an unknown key or a
     * value that is not parsable for its key
     */
    public static ExploreConfig fromProperties(Properties props) throws FormatException {
        return fromProperties(props, null);
    }

    /**
     * Parses a configuration from the parsed content of an {@code explore}
     * settings resource. Behaves as {@link #fromProperties(Properties)},
     * except that every error carries the position of the entry it is about.
     * @throws FormatException if the entries contain an unknown key or a
     * value that is not parsable for its key
     */
    public static ExploreConfig fromProperties(SettingsContent content) throws FormatException {
        return fromProperties(content.properties(), content);
    }

    /**
     * Parses a configuration from java-properties entries, optionally
     * accompanied by the content they were parsed from; if the content is
     * given, the errors carry the position of the entry they are about.
     */
    private static ExploreConfig fromProperties(Properties props,
                                                @Nullable SettingsContent content) throws FormatException {
        var result = new ExploreConfig();
        var errors = new FormatErrorSet();
        // process the entries in alphabetical order, for deterministic
        // error order (Properties iterates hash-ordered)
        List<String> names = new ArrayList<>(props.stringPropertyNames());
        Collections.sort(names);
        for (String name : names) {
            if (name.equals(SettingsModel.SCHEMA_KEY)) {
                continue;
            }
            var numbers = SettingsContent.numbers(content, name);
            ExploreKey key = keyMap.get(name);
            if (key == null) {
                errors.add("Unknown exploration key '%s'", name, numbers);
                continue;
            }
            var value = props.getProperty(name);
            assert value != null; // name is one of the property names of props
            putParsed(result, errors, key, value.trim(), numbers);
        }
        errors.throwException();
        return result;
    }

    /**
     * Parses the value for a given key and enters the resulting setting into
     * a configuration, adding any parse errors — extended with the given
     * context arguments — to the error set.
     */
    private static void putParsed(ExploreConfig result, FormatErrorSet errors, ExploreKey key,
                                  String value, Object... context) {
        try {
            result.put(key, key.parser().parse(value));
        } catch (FormatException exc) {
            errors.addAll(exc.getErrors().extend(context));
        }
    }

    /** Returns a parser for configurations, with the default configuration as default value. */
    public static Parser<ExploreConfig> parser() {
        return PARSER;
    }

    private static final Parser<ExploreConfig> PARSER = new Parser.AParser<>(
        "Space-separated list of <i>key</i>=<i>value</i> exploration"
            + " settings; see the exploration dialog for the keys and values",
        new ExploreConfig()) {
        @Override
        public ExploreConfig parse(String input) throws FormatException {
            return ExploreConfig.parse(input);
        }

        @Override
        public <V extends ExploreConfig> String unparse(V value) {
            return value.unparse();
        }
    };

    @Override
    public String toString() {
        return "ExploreConfig[" + unparse() + "]";
    }

    @Override
    public int hashCode() {
        return this.map.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ExploreConfig other)) {
            return false;
        }
        return this.map.equals(other.map);
    }

    /** Separator between key name and value in the textual form. */
    private static final char ASSIGN = '=';
    /** Quote character used for values containing spaces. */
    private static final char QUOTE = '"';

    /** Mapping from key names to keys. */
    private static final Map<String,@Nullable ExploreKey> keyMap = new HashMap<>();
    static {
        for (var key : ExploreKey.values()) {
            keyMap.put(key.getName(), key);
        }
    }
}
