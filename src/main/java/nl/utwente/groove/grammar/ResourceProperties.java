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
package nl.utwente.groove.grammar;

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
import nl.utwente.groove.grammar.rule.MethodName;
import nl.utwente.groove.grammar.rule.MethodName.Language;
import nl.utwente.groove.grammar.rule.MethodNameParser;
import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.Factory;
import nl.utwente.groove.util.Properties;
import nl.utwente.groove.util.Strings;
import nl.utwente.groove.util.parse.FormatChecker;
import nl.utwente.groove.util.parse.FormatErrorSet;
import nl.utwente.groove.util.parse.FormatException;
import nl.utwente.groove.util.parse.Parser;
import nl.utwente.groove.util.parse.StringParser;

/**
 * Specialised properties class for graph-based grammar resources
 * (rules in particular, but also host and type graphs).
 * This is stored as part of the graph info of the resource's aspect graph.
 * @author Arend Rensink
 * @version $Revision$
 */
public class ResourceProperties extends Properties {
    /** Constructs an empty properties object. */
    public ResourceProperties() {
        super(Key.class);
    }

    /** Constructs a properties object initialised on a given map. */
    public ResourceProperties(ResourceProperties properties) {
        this();
        putAll(properties);
    }

    @Override
    public Optional<Key> getKey(String name) {
        return Optional.ofNullable(nameKeyMap.get().get(name));
    }

    @Override
    public synchronized ResourceProperties clone() {
        return new ResourceProperties(this);
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

    /** Predefined resource property keys. */
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
            + "Default for parameterless, unmodifying rules", Role.VALUE_TYPE),

        /** Match filter. */
        FILTER("matchFilter",
            "<body>Boolean method or predicate that filters the matches of the rule. A match is only considered if the method returns <code>true</code>.<br>"
                + "Format: <tt>lang:name</tt> where the optional <tt>lang</tt> is the name of a language (by default Java) and <tt>name</tt> the fully qualified method name.<br>"
                + "The method may optionally take parameters of type <tt>HostGraph</tt> and <tt>RuleEvent</tt><br/>"
                + "Supported languages are: <tt>" + Strings.toString(Language.values(), "", "", ", ")
                + "</tt>",
            MethodName.VALUE_TYPE),

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
        VERSION("$version", "Graph version", ValueType.STRING),

        /** Master random seed recorded for a saved LTS. */
        RANDOM_SEED("$randomSeed",
            "<body>Master random seed in effect when the LTS was explored; "
                + "recorded only if the exploration actually drew randomness. "
                + "<br>Pass it to a new run (Generator option <tt>-seed</tt> or "
                + "<tt>-Dgroove.randomSeed=...</tt>) to reproduce the LTS.",
            ValueType.STRING);

        /**
         * Constructor for a key with a plain string value
         * @param name name of the key; should be an identifier possibly prefixed by #SYSTEM_KEY_PREFIX
         * @param explanation short explanation of the meaning of the key
         */
        private Key(String name, String explanation, ValueType<?> keyType) {
            this(name, null, explanation, keyType);
        }

        /**
         * Constructor for a key with a plain string value
         * @param name name of the key; should be an identifier possibly prefixed by #SYSTEM_KEY_PREFIX
         * @param keyPhrase user-readable version of the name; if {@code null},
         * the key phrase is constructed from {@code name}
         * @param explanation short explanation of the meaning of the key
         */
        private Key(String name, String keyPhrase, String explanation, ValueType<?> keyType) {
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
        public ValueType<?> getKeyType() {
            return this.valueType;
        }

        private final ValueType<?> valueType;

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

    /** Checker interface for resource property keys. */
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
        var formatString = v.value(ValueType.STRING);
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
        = Factory.lazy(ResourceProperties::createNameKeyMap);

    static private Map<String,Key> createNameKeyMap() {
        var result = new HashMap<String,Key>();
        Arrays.stream(Key.values()).forEach(k -> result.put(k.getName(), k));
        return result;
    }

    /**
     * Returns the resource properties of a given graph.
     * @param graph the queried graph; non-{@code null}
     * @return the properties object of the queried graph, or a fixed empty
     * properties object if the graph has none
     */
    public static ResourceProperties getProperties(Graph graph) {
        Properties result = graph.hasInfo()
            ? graph.getInfo().getProperties()
            : null;
        return result == null
            ? EMPTY_PROPERTIES
            : (ResourceProperties) result;
    }

    /**
     * Convenience method to set the resource properties of a given graph.
     * The graph will receive a copy of the properties passed in.
     * @param graph the graph to be modified; non-{@code null}
     * @param properties the new properties map; non-{@code null}
     */
    public static void setProperties(Graph graph, ResourceProperties properties) {
        assert !graph.isFixed();
        graph.getInfo().setProperties(new ResourceProperties(properties));
    }

    /**
     * Returns the role of a given rule graph.
     * @param graph the queried graph; non-{@code null}
     * @return the role; non-{@code null}
     * @see Key#ROLE
     */
    static public Optional<Role> getRole(Graph graph) {
        return getProperty(graph, Key.ROLE).value(Role.VALUE_TYPE);
    }

    /**
     * Sets the role of a given rule graph to a certain value.
     * @param graph the graph to be modified; non-{@code null} and non-fixed
     * @param role the new role; non-{@code null}
     */
    static public void setRole(Graph graph, Role role) {
        setProperty(graph, Key.ROLE, role);
    }

    /**
     * Returns the priority property of a given graph. The priority is a non-negative number.
     * Yields the default priority {@code 0} if the priority has not been set explicitly.
     * @param graph the queried graph; non-{@code null}
     * @return the non-negative priority of {@code graph}
     * @see Key#PRIORITY
     */
    static public int getPriority(Graph graph) {
        return getProperty(graph, Key.PRIORITY).value(ValueType.INTEGER);
    }

    /**
     * Sets the priority of a given graph to a certain value.
     * @param graph the graph to be modified; non-{@code null} and non-fixed
     * @param priority the new priority value; should be non-negative
     */
    static public void setPriority(Graph graph, int priority) {
        setProperty(graph, Key.PRIORITY, priority);
    }

    /**
     * Returns the enabledness property of a given graph.
     * Yields <code>true</code> by default.
     * @param graph the queried graph; non-{@code null}
     * @see Key#ENABLED
     */
    static public boolean isEnabled(Graph graph) {
        return getProperty(graph, Key.ENABLED).value(ValueType.BOOLEAN);
    }

    /**
     * Sets the enabledness of a given graph to a certain value.
     * @param graph the graph to be modified; non-{@code null} and non-fixed
     * @param enabled the new enabledness value
     */
    static public void setEnabled(Graph graph, boolean enabled) {
        setProperty(graph, Key.ENABLED, enabled);
    }

    /**
     * Returns the injectivity property of a given graph.
     * Yields <code>false</code> by default.
     * @param graph the queried graph; non-{@code null}
     * @see Key#INJECTIVE
     */
    static public boolean isInjective(Graph graph) {
        return getProperty(graph, Key.INJECTIVE).value(ValueType.BOOLEAN);
    }

    /**
     * Sets the injectivity of a given graph to a certain value.
     * @param graph the graph to be modified; non-{@code null} and non-fixed
     * @param injective the new injectivity value
     */
    static public void setInjective(Graph graph, boolean injective) {
        setProperty(graph, Key.INJECTIVE, injective);
    }

    /**
     * Returns the remark property from a given graph.
     * Yields the empty string by default.
     * @param graph the queried graph; non-{@code null}
     * @see Key#REMARK
     */
    static public String getRemark(Graph graph) {
        return getProperty(graph, Key.REMARK).value(ValueType.STRING);
    }

    /**
     * Sets the remark for a given graph to a certain value.
     * @param graph the graph to be modified; non-{@code null} and non-fixed
     * @param remark the remark for this graph; non-{@code null}
     */
    static public void setRemark(Graph graph, String remark) {
        setProperty(graph, Key.REMARK, remark);
    }

    /**
     * Returns the string format property from a given graph.
     * Yields the empty string if the graph has
     * no explicitly set format string.
     * @param graph the queried graph; non-{@code null}
     * @see Key#FORMAT
     */
    static public String getFormatString(Graph graph) {
        return getProperty(graph, Key.FORMAT).value(ValueType.STRING);
    }

    /**
     * Sets the format string for a given graph to a certain value.
     * @param graph the graph to be modified; non-{@code null} and non-fixed
     * @param formatString the format string for this graph; may be {@code null}
     */
    static public void setFormatString(Graph graph, String formatString) {
        setProperty(graph, Key.FORMAT, formatString);
    }

    /**
     * Returns the transition label of a given graph.
     * Yields the empty string if the transition label has not been set explicitly
     * @param graph the queried graph; non-{@code null}
     * @see Key#TRANSITION_LABEL
     */
    static public String getTransitionLabel(Graph graph) {
        return getProperty(graph, Key.TRANSITION_LABEL).value(ValueType.STRING);
    }

    /**
     * Convenience method to set the transition label for a given graph to a certain value.
     * @param graph the graph to be modified; non-{@code null} and non-fixed
     * @param label the transition label for this graph; may be {@code null}
     */
    static public void setTransitionLabel(Graph graph, String label) {
        setProperty(graph, Key.TRANSITION_LABEL, label);
    }

    /**
     * Returns the version property from a given graph.
     * Yields the empty string if the graph has
     * no explicitly set version.
     * @param graph the queried graph; non-{@code null}
     * @see Key#VERSION
     */
    static public String getVersion(Graph graph) {
        return getProperty(graph, Key.VERSION).value(ValueType.STRING);
    }

    /**
     * Returns the random seed recorded for a given (LTS) graph.
     * @param graph the queried graph; non-{@code null}
     * @return the recorded seed, or {@link Optional#empty()} if the graph has
     * no recorded seed
     * @see Key#RANDOM_SEED
     */
    @AIGenerated("Claude Fable 5, 2026-08")
    static public Optional<String> getRandomSeed(Graph graph) {
        String result = getProperty(graph, Key.RANDOM_SEED).value(ValueType.STRING);
        return result.isEmpty()
            ? Optional.empty()
            : Optional.of(result);
    }

    /**
     * Records the random seed for a given (LTS) graph.
     * The seed is stored in decimal form, as accepted by the Generator
     * {@code -seed} option and the {@code groove.randomSeed} system property.
     * @param graph the graph to be modified; non-{@code null} and non-fixed
     * @param seed the master seed to be recorded
     * @see Key#RANDOM_SEED
     */
    @AIGenerated("Claude Fable 5, 2026-08")
    static public void setRandomSeed(Graph graph, long seed) {
        setProperty(graph, Key.RANDOM_SEED, Long.toString(seed));
    }

    /**
     * Convenience method to retrieve a resource property from a given graph.
     * @param graph the queried graph; non-{@code null}
     * @return the stored or default property value for the given key;
     * non-{@code null}
     */
    private static Entry getProperty(Graph graph, Key key) {
        Entry result = null;
        try {
            var properties = graph.hasInfo()
                ? graph.getInfo().getProperties()
                : null;
            if (properties != null) {
                result = properties.parseProperty(key);
            }
        } catch (FormatException exc) {
            // do nothing; default value set below
        }
        if (result == null) {
            result = key.parser().getDefaultValue();
        }
        return result;
    }

    /**
     * Convenience method to change a resource property of a given graph.
     * Creates the properties object if the graph does not yet have one.
     * @param graph the graph to be modified; non-{@code null} and non-fixed
     */
    private static void setProperty(Graph graph, Key key, Object value) {
        var info = graph.getInfo();
        var properties = info.getProperties();
        if (properties == null) {
            properties = new ResourceProperties();
            info.setProperties(properties);
        }
        properties.storeValue(key, value);
    }

    /** Constant empty properties object. */
    private static final ResourceProperties EMPTY_PROPERTIES;

    static {
        EMPTY_PROPERTIES = new ResourceProperties();
        EMPTY_PROPERTIES.setFixed();
    }
}
