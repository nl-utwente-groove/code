/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
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
package groove.graph;

import groove.grammar.Action;
import groove.grammar.Action.Role;
import groove.grammar.CheckPolicy;
import groove.gui.dialog.PropertyKey;
import groove.gui.look.Line;
import groove.io.HTMLConverter;
import groove.util.DefaultFixable;
import groove.util.ExprParser;
import groove.util.Fixable;
import groove.util.Groove;
import groove.util.Parser;
import groove.util.Property;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Specialised properties class for graphs. This can be stored as part of the
 * graph info.
 * @author Arend Rensink
 * @version $Revision$
 */
public class GraphProperties extends Properties implements Fixable {
    /** Constructs an empty properties object. */
    public GraphProperties() {
        // empty
    }

    /** Constructs a properties object initialised on a given map. */
    public GraphProperties(Map<? extends Object,? extends Object> properties) {
        putAll(properties);
    }

    /**
     * Before calling the super method, tests if the key is an allowed user
     * property key.
     * @throws IllegalArgumentException if <code>key</code> is disallowed
     *         according to #testKey
     */
    @Override
    public synchronized Object put(Object key, Object value) {
        if (!isValidKey(key)) {
            throw new IllegalArgumentException(String.format(
                "User property key '%s' not allowed: use identifier", key));
        }
        return super.put(key, value);
    }

    @Override
    public synchronized String toString() {
        StringBuffer result = new StringBuffer();
        if (isEmpty()) {
            result.append("No stored properties");
        } else {
            result.append("Properties:\n");
            for (Map.Entry<Object,Object> entry : entrySet()) {
                result.append("  " + entry + "\n");
            }
        }
        return result.toString();
    }

    @Override
    public synchronized GraphProperties clone() {
        return new GraphProperties(this);
    }

    /** Retrieves and parses the value for a given key. */
    public Object parseProperty(Key key) {
        String result = getProperty(key.getName());
        return key.parser().parse(result);
    }

    /** Stores a property value, converted to a parsable string. */
    public void storeProperty(Key key, Object value) {
        assert key.parser().isValue(value);
        String repr = key.parser().toParsableString(value);
        if (repr.length() == 0) {
            remove(key.getName());
        } else {
            super.setProperty(key.getName(), repr);
        }
    }

    /** Convenience method to retrieve a property by key value rather than string. */
    public String getProperty(Key key) {
        String result = getProperty(key.getName());
        if (result == null) {
            result = key.getDefaultValue();
        }
        return result;
    }

    @Override
    public String setProperty(String keyword, String value) {
        testFixed(false);
        String oldValue;
        Key key = KEYS.get(keyword);
        if (value == null) {
            oldValue = (String) remove(keyword);
        } else if (key != null && !key.isSystem() && key.getDefaultValue().equals(value)) {
            oldValue = (String) remove(keyword);
        } else {
            oldValue = (String) super.setProperty(keyword, value);
        }
        return oldValue;
    }

    /** Convenience method to set a property by key value rather than string. */
    public String setProperty(Key key, String value) {
        return setProperty(key.getName(), value);
    }

    @Override
    public boolean setFixed() {
        return this.fixable.setFixed();
    }

    @Override
    public boolean isFixed() {
        return this.fixable.isFixed();
    }

    @Override
    public void testFixed(boolean fixed) {
        this.fixable.testFixed(fixed);
    }

    /** Object to delegate the fixable functionality. */
    private final DefaultFixable fixable = new DefaultFixable();

    /**
     * Tests if a given string is a valid system-defined property key.
     */
    static private boolean isSystemKey(String keyword) {
        return isKey(keyword) && KEYS.get(keyword).isSystem();
    }

    /**
     * Tests if a given object is formatted as a system key. This returns
     * <code>true</code> if the key passes
     * {@link ExprParser#isIdentifier(String)}.
     */
    static public boolean isValidUserKey(String key) {
        return ExprParser.isIdentifier(key);
    }

    /**
     * Tests if a given object is a valid user-defined property key, or a system
     * key.
     */
    static private boolean isValidKey(Object key) {
        return key instanceof String && (isSystemKey((String) key) || isValidUserKey((String) key));
    }

    /** Returns the mapping from string values to property keys. */
    static public Map<String,Key> getKeyMap() {
        return Collections.unmodifiableMap(KEYS);
    }

    /** Indicates if a given string corresponds to a property key. */
    static public boolean isKey(String key) {
        return KEYS.containsKey(key);
    }

    /** Mapping from graph property key names to keys. */
    private final static Map<String,Key> KEYS;

    static {
        Map<String,Key> keys = new LinkedHashMap<String,Key>();
        for (Key key : Key.values()) {
            keys.put(key.getName(), key);
        }
        KEYS = Collections.unmodifiableMap(keys);
    }

    /** Predefined graph property keys. */
    public static enum Key implements PropertyKey {
        /** Rule priority. */
        PRIORITY("priority", Integer.toString(Action.DEFAULT_PRIORITY), new Property<String>() {
            @Override
            public boolean isSatisfied(String value) {
                try {
                    return Integer.parseInt(value) >= 0;
                } catch (NumberFormatException exc) {
                    return false;
                }
            }

            @Override
            public String getDescription() {
                return "A non-negative number; higher priorities have precedence";
            }

            @Override
            public String getExplanation() {
                return "Higher-priority rules are evaluated first";
            }
        }) {
            @Override
            public Parser<Integer> parser() {
                return Parser.natural;
            }
        },
        /** Rule enabledness. */
        ENABLED("enabled", "" + true, new Property<String>() {
            @Override
            public boolean isSatisfied(String value) {
                return value.equals("" + true) || value.equals("" + false);
            }

            @Override
            public String getDescription() {
                return "A boolean value (<strong>true</strong> by default)";
            }

            @Override
            public String getExplanation() {
                return "Disabled rules are never evaluated";
            }
        }) {
            @Override
            public Parser<Boolean> parser() {
                return Parser.boolTrue;
            }
        },
        /** Rule injectivity. */
        INJECTIVE("injective", "" + false, new Property<String>() {
            @Override
            public boolean isSatisfied(String value) {
                return value.equals("" + true) || value.equals("" + false);
            }

            @Override
            public String getDescription() {
                return "A boolean value (<strong>false</strong> by default)";
            }

            @Override
            public String getExplanation() {
                return "Boolean property determining if the rule is to be matched injectively."
                    + "Disregarded if injective matching is set on the grammar level.";
            }
        }) {
            @Override
            public Parser<Boolean> parser() {
                return Parser.boolFalse;
            }
        },
        /** Action role. */
        ROLE("actionRole", "", new Property<String>() {
            @Override
            public boolean isSatisfied(String value) {
                return value.length() == 0 || Role.toRole(value) != null;
            }

            @Override
            public String getDescription() {
                return "One of " + Groove.toString(Role.values(), "", "", ", ")
                    + HTMLConverter.HTML_LINEBREAK
                    + "or the empty string if the role should be automatically inferred";
            }

            @Override
            public String getExplanation() {
                return "Role of the action: either a transformer, or some kind of property";
            }
        }) {
            @Override
            public Parser<?> parser() {
                return this.parser;
            }

            private final Parser<Role> parser = new Parser.EnumParser<Role>(Role.class, null);
        },
        /** Policy for dealing with property violations. */
        VIOLATE_POLICY("violationPolicy", "", new Property<String>() {
            @Override
            public boolean isSatisfied(String value) {
                return value.equals("absence") || value.equals("error") || value.equals("none");
            }

            @Override
            public String getDescription() {
                return "One of 'absence' or 'error' (default)";
            }

            @Override
            public String getExplanation() {
                return "Flag controlling how type violations are dealt with.";
            }
        }) {
            @Override
            public Parser<?> parser() {
                return this.parser;
            }

            private final Parser<CheckPolicy> parser = new Parser.EnumParser<CheckPolicy>(
                CheckPolicy.class, CheckPolicy.ERROR);
        },
        /** User-defined comment. */
        REMARK("remark", "", new Property<String>() {
            @Override
            public boolean isSatisfied(String value) {
                return value.trim().length() > 0;
            }

            @Override
            public String getDescription() {
                return "A one-line description of the purpose of the graph or rule";
            }

            @Override
            public String getExplanation() {
                return "A one-line description of the rule, shown e.g. as tool tip";
            }
        }),
        /** Output line format. */
        FORMAT("printFormat", "Output format", null, new Property<String>() {
            @Override
            public boolean isSatisfied(String value) {
                return value.trim().length() > 0;
            }

            @Override
            public String getDescription() {
                return "A format string as in String.format, instantiated by rule parameters";
            }

            @Override
            public String getExplanation() {
                return "If nonempty, rule application prints this (instantiated) string on System.out";
            }
        }) {
            @Override
            public Parser<String> parser() {
                return Parser.trim;
            }
        },
        /** Alternative transition label. */
        TRANSITION_LABEL("transitionLabel", null, new Property<String>() {
            @Override
            public boolean isSatisfied(String value) {
                return true;
            }

            @Override
            public String getDescription() {
                return "A format string as in String.format, instantiated by rule parameters";
            }

            @Override
            public String getExplanation() {
                return "A string to be used as the transition label in the "
                    + "LTS. If empty, defaults to the rule name.";
            }
        }),
        /** Graph version. */
        VERSION("version");

        /** Defines a system property with a given name. */
        private Key(String name) {
            this(name, null, null, null, true);
        }

        /** Defines a non-system property with a given name, default value and format property. */
        private Key(String name, String defaultValue, Property<String> format) {
            this(name, null, defaultValue, format, false);
        }

        /** Defines a non-system property with a given name, description, default value and format property. */
        private Key(String name, String keyPhrase, String defaultValue, Property<String> format) {
            this(name, keyPhrase, defaultValue, format, false);
        }

        /** Defines a (system or non-system) property with a given name, description, default value and format property. */
        private Key(String name, String keyPhrase, String defaultValue, Property<String> format,
            boolean system) {
            this.name = (system ? SYSTEM_KEY_START : "") + name;
            this.defaultValue = defaultValue == null ? "" : defaultValue;
            this.keyPhrase = keyPhrase == null ? Groove.unCamel(name, false) : keyPhrase;
            this.system = system;
            this.format = format;
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public String getKeyPhrase() {
            return this.keyPhrase;
        }

        @Override
        public String getDefaultValue() {
            return this.defaultValue;
        }

        @Override
        public boolean isSystem() {
            return this.system;
        }

        @Override
        public Property<String> getFormat() {
            return this.format;
        }

        @Override
        public Line getExplanation() {
            return null;
        }

        @Override
        public Parser<?> parser() {
            return Parser.identity;
        }

        private final String name;
        private final String keyPhrase;
        private final String defaultValue;
        private final boolean system;
        private final Property<String> format;
        /**
         * Start character that distinguishes user-defined property keys from system
         * keys. Any string starting with this character is a system key.
         */
        static private final char SYSTEM_KEY_START = '$';
    }
}
