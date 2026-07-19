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

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.util.Exceptions;
import nl.utwente.groove.util.parse.FormatErrorSet;
import nl.utwente.groove.util.parse.FormatException;
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
            throw Exceptions.illegalArg("Setting kind '%s' does not belong to key '%s'",
                                        setting.kind(), key.getName());
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
        if ((goal == Goal.NONE || goal == Goal.FINAL)
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
        if (getKind(ExploreKey.COUNT) == Count.COUNT
            && get(ExploreKey.COUNT).content() instanceof Integer count && count < 2) {
            result.add("Result count %s should be larger than 1", count);
        }
        if (getKind(ExploreKey.BOUND) == Bound.COST && getKind(ExploreKey.COST) == Cost.NONE) {
            result
                .add("Bound '%s' requires a transition cost", Bound.COST.getName());
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
            if (value.chars().anyMatch(Character::isWhitespace)) {
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
        var seen = new EnumMap<ExploreKey,Boolean>(ExploreKey.class);
        for (String token : StringHandler.splitExpr(text, " ")) {
            if (token.isEmpty()) {
                continue;
            }
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
            if (seen.put(key, Boolean.TRUE) != null) {
                errors.add("Duplicate exploration key '%s'", name);
                continue;
            }
            String value = token.substring(pos + 1);
            if (!value.isEmpty() && value.charAt(0) == QUOTE) {
                value = StringHandler.toUnquoted(value, QUOTE);
            }
            try {
                result.put(key, key.parser().parse(value));
            } catch (FormatException exc) {
                errors.addAll(exc.getErrors());
            }
        }
        errors.throwException();
        return result;
    }

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
    private static final Map<String,ExploreKey> keyMap = new HashMap<>();
    static {
        for (var key : ExploreKey.values()) {
            keyMap.put(key.getName(), key);
        }
    }
}
