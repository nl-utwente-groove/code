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
import groove.gui.dialog.PropertyKey;
import groove.util.DefaultFixable;
import groove.util.ExprParser;
import groove.util.Fixable;
import groove.util.Groove;
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

    /** Convenience method to retrieves a property by key value rather than string. */
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
                return "Non-negative number; higher priorities have precedence";
            }

            @Override
            public String getComment() {
                return "Higher-priority rules are evaluated first";
            }
        }),
        /** Rule enabledness. */
        ENABLED("enabled", "" + true, new Property<String>() {
            @Override
            public boolean isSatisfied(String value) {
                return value.equals("" + true) || value.equals("" + false);
            }

            @Override
            public String getDescription() {
                return "Boolean value";
            }

            @Override
            public String getComment() {
                return "Disabled rules are never evaluated";
            }
        }),
        /** User-defined comment. */
        REMARK("remark", "", new Property<String>() {
            @Override
            public boolean isSatisfied(String value) {
                return value.trim().length() > 0;
            }

            @Override
            public String getDescription() {
                return "One-line description of the purpose of the graph or rule";
            }

            @Override
            public String getComment() {
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
                return "Format string as in String.format, instantiated by rule parameters";
            }

            @Override
            public String getComment() {
                return "If nonempty, rule application prints this (instantiated) string on System.out";
            }
        }, false),
        /** Alternative transition label. */
        INJECTIVE("injective", "" + false, new Property<String>() {
            @Override
            public boolean isSatisfied(String value) {
                return value.equals("" + true) || value.equals("" + false);
            }

            @Override
            public String getDescription() {
                return "Flag controlling injective matching; false by default";
            }

            @Override
            public String getComment() {
                return "Boolean property determining if the rule is to be matched injectively."
                    + "Disregarded if injective matching is set on the grammar level.";
            }
        }),
        /** Alternative transition label. */
        TRANSITION_LABEL("transitionLabel", new Property<String>() {
            @Override
            public boolean isSatisfied(String value) {
                return true;
            }

            @Override
            public String getDescription() {
                return "Format string as in String.format, instantiated by rule parameters";
            }

            @Override
            public String getComment() {
                return "A string to be used as the transition label in the "
                        + "LTS. If empty, defaults to the rule name.";
            }
        }),
        /** Graph version. */
        VERSION("version", "", null, true);

        /** Defines a non-system property with a given name and format property. */
        private Key(String name, Property<String> format) {
            this(name, null, format, false);
        }

        /** Defines a non-system property with a given name, default value and format property. */
        private Key(String name, String defaultValue, Property<String> format) {
            this(name, defaultValue, format, false);
        }

        /** Defines a (system or non-system) property with a given name, default value and format property. */
        private Key(String name, String defaultValue, Property<String> format, boolean system) {
            this(name, Groove.unCamel(name, false), defaultValue, format, system);
        }

        /** Defines a (system or non-system) property with a given name, description, default value and format property. */
        private Key(String name, String description, String defaultValue, Property<String> format,
                boolean system) {
            this.name = (system ? SYSTEM_KEY_START : "") + name;
            this.defaultValue = defaultValue == null ? "" : defaultValue;
            this.description = description;
            this.system = system;
            this.format = format;
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public String getDescription() {
            return this.description;
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

        private final String name;
        private final String description;
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
