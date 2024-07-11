/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2023
 * University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * $Id$
 */
package nl.utwente.groove.graph;

import java.util.Arrays;
import java.util.HashMap;
import java.util.IllegalFormatException;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

import nl.utwente.groove.grammar.Action.Role;
import nl.utwente.groove.grammar.aspect.AspectContent.IntegerContent;
import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.aspect.AspectKind.Category;
import nl.utwente.groove.grammar.rule.MethodName.Language;
import nl.utwente.groove.grammar.rule.MethodNameParser;
import nl.utwente.groove.util.Factory;
import nl.utwente.groove.util.Groove;
import nl.utwente.groove.util.Properties;
import nl.utwente.groove.util.Strings;
import nl.utwente.groove.util.parse.FormatChecker;
import nl.utwente.groove.util.parse.FormatErrorSet;
import nl.utwente.groove.util.parse.FormatException;
import nl.utwente.groove.util.parse.Parser;
import nl.utwente.groove.util.parse.StringParser;

/**
 * Specialised properties class for graphs. This can be stored as part of the
 * graph info.
 * @author Arend Rensink
 * @version $Revision$
 */
public class GraphProperties extends Properties {
    /** Constructs an empty properties object. */
    public GraphProperties() {
        super(Key.class);
    }

    /** Constructs a properties object initialised on a given map. */
    public GraphProperties(GraphProperties properties) {
        this();
        putAll(properties);
    }

    @Override
    public Optional<Key> getKey(String name) {
        return Optional.ofNullable(nameKeyMap.get().get(name));
    }

    @Override
    public synchronized GraphProperties clone() {
        return new GraphProperties(this);
    }

    /** Returns a map from property keys to checkers driven by a given grammar model. */
    public CheckerMap getCheckers(final AspectGraph graph) {
        var result = new CheckerMap();
        for (final var key : Key.values()) {
            FormatChecker<String> checker = v -> {
                try {
                    return key.check(graph, key.parse(v));
                } catch (FormatException exc) {
                    return exc.getErrors().extend(key);
                }
            };
            result.put(key, checker);
        }
        return result;
    }

    /** Predefined graph property keys. */
    public static enum Key implements Properties.Key, Checker {
        /** User-defined comment. */
        REMARK("remark", "One-line explanation of the rule, shown e.g. as tool tip",
            ValueType.STRING),

        /** Rule priority. */
        PRIORITY("priority", "Higher-priority rules are evaluated first.", ValueType.INTEGER),

        /** Rule enabledness. */
        ENABLED("enabled", "Disabled rules are never evaluated.", ValueType.BOOLEAN),

        /** Rule injectivity. */
        INJECTIVE("injective",
            "<body>Flag determining if the rule is to be matched injectively. "
                + "<br>Disregarded if injective matching is set on the grammar level.",
            ValueType.BOOLEAN),

        /** Action role. */
        ROLE("actionRole", "<body>Role of the action. Values are:"
            + "<li>- <i>transformer</i>: action that causes the graph to change; scheduled by the (im- or explicit) control. "
            + "Default for rules that modify the graph or have parameters"
            + "<li>- <i>forbidden</i>: forbidden graph pattern, dealt with as dictated by the violation policy"
            + "<li>- <i>invariant</i>: invariant graph property, dealt with as dictated by the violation policy"
            + "<li>- <i>condition</i>: unmodifying, parameterless action, checked at every state. "
            + "Default for parameterless, unmodifying rules", ValueType.ROLE),

        /** Match filter. */
        FILTER("matchFilter",
            "<body>Boolean method or predicate that filters the matches of the rule. A match is only considered if the method returns <code>true</code>.<br>"
                + "Format: <tt>lang:name</tt> where the optional <tt>lang</tt> is the name of a language (by default Java) and <tt>name</tt> the fully qualified method name.<br>"
                + "The method may optionally take parameters of type <tt>HostGraph</tt> and <tt>RuleEvent</tt><br/>"
                + "Supported languages are: <tt>" + Groove.toString(Language.values(), "", "", ", ")
                + "</tt>",
            ValueType.METHOD_NAME),

        /** Output line format. */
        FORMAT("printFormat",
            "<body>If nonempty, is printed on <tt>System.out</tt> upon every rule application. "
                + "<br>Optional format parameters as in <tt>String.format</tt> are instantiated with rule parameters.",
            ValueType.STRING),

        /** Alternative transition label. */
        TRANSITION_LABEL("transitionLabel",
            "<body>String to be used as the transition label in the LTS. "
                + "<p>If empty, defaults to the rule name."
                + "<br>Optional format parameters as in <tt>String.format</tt> are instantiated with rule parameters.",
            ValueType.STRING),

        /** Graph version. */
        VERSION("$version", "Graph version", ValueType.STRING);

        /**
         * Constructor for a key with a plain string value
         * @param name name of the key; should be an identifier possibly prefixed by #SYSTEM_KEY_PREFIX
         * @param explanation short explanation of the meaning of the key
         */
        private Key(String name, String explanation, ValueType keyType) {
            this(name, null, explanation, keyType);
        }

        /**
         * Constructor for a key with a plain string value
         * @param name name of the key; should be an identifier possibly prefixed by #SYSTEM_KEY_PREFIX
         * @param keyPhrase user-readable version of the name; if {@code null},
         * the key phrase is constructed from {@code name}
         * @param explanation short explanation of the meaning of the key
         */
        private Key(String name, String keyPhrase, String explanation, ValueType keyType) {
            this.name = name;
            this.system = name.startsWith(SYSTEM_KEY_PREFIX);
            if (keyPhrase == null) {
                String properName = name
                    .substring(this.system
                        ? SYSTEM_KEY_PREFIX.length()
                        : 0);
                this.keyPhrase = Strings.unCamel(properName, false);
            } else {
                this.keyPhrase = keyPhrase;
            }
            this.explanation = explanation;
            this.valueType = keyType;
        }

        @Override
        public String getName() {
            return this.name;
        }

        private final String name;

        @Override
        public String getExplanation() {
            return this.explanation;
        }

        private final String explanation;

        @Override
        public boolean isSystem() {
            return this.system;
        }

        private final boolean system;

        @Override
        public String getKeyPhrase() {
            return this.keyPhrase;
        }

        private final String keyPhrase;

        /** Returns the type of the values belonging to this key. */
        @Override
        public ValueType getKeyType() {
            return this.valueType;
        }

        private final ValueType valueType;

        @Override
        public KeyParser parser() {
            var result = this.parser;
            if (result == null) {
                var inner = switch (this) {
                case ENABLED -> Parser.boolTrue;
                case FILTER -> new Parser.OptionalParser<>(MethodNameParser.instance());
                case INJECTIVE -> Parser.boolFalse;
                case PRIORITY -> Parser.natural;
                case ROLE -> new Parser.OptionalParser<>(new EnumParser<>(Role.class));
                default -> StringParser.identity();
                };
                this.parser = result = new KeyParser(this, inner);
            }
            return result;
        }

        private KeyParser parser;

        @Override
        public FormatErrorSet apply(AspectGraph graph, Entry value) {
            return this.checker.get().apply(graph, value);
        }

        /** Lazily created checker for values of this key. */
        private final Factory<Checker> checker = Factory.lazy(this::computeChecker);

        /** Computes the value for {@link #checker}. */
        private Checker computeChecker() {
            return switch (this) {
            case FORMAT, TRANSITION_LABEL -> formatChecker;
            default -> trueChecker;
            };
        }

        @Override
        public boolean isNotable() {
            return switch (this) {
            case FILTER, FORMAT, INJECTIVE, TRANSITION_LABEL -> true;
            default -> false;
            };
        }

        /** Indicates if a given string corresponds to a property key. */
        static public boolean isKey(String key) {
            try {
                valueOf(key);
                return true;
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
    }

    /** Checker interface for graph property keys. */
    public interface Checker extends BiFunction<AspectGraph,Entry,FormatErrorSet> {
        /**
         * Checks the consistency of a property with a given model.
         * @return the (possibly empty) set of errors in the value
         */
        default public FormatErrorSet check(AspectGraph graph, Entry value) {
            return apply(graph, value);
        }
    }

    /** Checker that always returns the empty error set. */
    private static final Checker trueChecker = (g, v) -> FormatErrorSet.EMPTY;

    /** Creates a checker for a formatted output string that should be suitable for the
     * parameters of a rule model. */
    private static final Checker formatChecker = (g, v) -> {
        var result = new FormatErrorSet();
        // compute the max par: number occurring in the source graph
        @SuppressWarnings("null")
        var maxPar = g
            .nodeSet()
            .stream()
            .map(n -> n.get(Category.PARAM))
            .map(a -> a == null
                ? null
                : a.getContent())
            .map(c -> c instanceof IntegerContent i
                ? i.get() + 1
                : 0)
            .reduce((i1, i2) -> Math.max(i1, i2))
            .orElse(0);
        Object[] args = new Object[maxPar];
        Arrays.fill(args, "");
        var formatString = v.getString();
        try {
            String.format(formatString, args);
        } catch (IllegalFormatException exc) {
            result
                .add("Rule has %s parameters, but format string '%s' expects more", maxPar,
                     formatString, g);
        }
        return result;
    };

    /** Mapping from key names (as in {@link Key#getName()}) to keys. */
    static private final Factory<Map<String,Key>> nameKeyMap
        = Factory.lazy(GraphProperties::createNameKeyMap);

    static private Map<String,Key> createNameKeyMap() {
        var result = new HashMap<String,Key>();
        Arrays.stream(Key.values()).forEach(k -> result.put(k.getName(), k));
        return result;
    }
}
