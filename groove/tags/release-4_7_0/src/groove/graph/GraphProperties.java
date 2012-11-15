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

import static groove.graph.GraphProperties.Key.ENABLED;
import static groove.graph.GraphProperties.Key.FORMAT;
import static groove.graph.GraphProperties.Key.PRIORITY;
import static groove.graph.GraphProperties.Key.REMARK;
import static groove.graph.GraphProperties.Key.TRANSITION_LABEL;
import static groove.graph.GraphProperties.Key.VERSION;
import groove.gui.dialog.PropertyKey;
import groove.trans.Action;
import groove.trans.Rule;
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

    /**
     * Returns the priority stored in this properties object, if any. The
     * priority is stored under key {@link #PRIORITY}.
     * @return The stored priority, or {@link Rule#DEFAULT_PRIORITY} if there is
     *         none
     */
    public int getPriority() {
        return Integer.parseInt(getProperty(PRIORITY));
    }

    /**
     * Sets a priority in this property object. The priority is stored under key
     * {@link #PRIORITY}.
     * @param priority the priority to be stored; non-negative
     * @return the previously stored priority, or {@link Rule#DEFAULT_PRIORITY}
     *         if there was none
     */
    public int setPriority(int priority) {
        String result = setProperty(PRIORITY, Integer.toString(priority));
        if (result == null) {
            return Action.DEFAULT_PRIORITY;
        } else {
            return Integer.parseInt(result);
        }
    }

    /**
     * Returns the enabled status stored in this properties object, if any. The
     * status is stored under key {@link #ENABLED}.
     * @return The stored enabled status, or <code>true</code> if there is
     *         none
     */
    public boolean isEnabled() {
        return Boolean.parseBoolean(getProperty(ENABLED));
    }

    /**
     * Sets a enabled status in this property object. The status is stored under
     * key {@link #ENABLED}.
     * @param enabled the enabled status to be stored
     * @return the previously stored status, or <code>true</code> if there was
     *         none
     */
    public boolean setEnabled(boolean enabled) {
        String result = setProperty(ENABLED, Boolean.toString(enabled));
        if (result == null) {
            result = ENABLED.getDefaultValue();
        }
        return Boolean.parseBoolean(result);
    }

    /**
     * Retrieves the {@link #REMARK} value in this properties object.
     * @return the current value for {@link #REMARK}; may be
     *         <code>null</code>
     */
    public String getRemark() {
        return getProperty(REMARK);
    }

    /**
     * Sets the {@link #REMARK} property to a given value.
     * @param remark the new remark; may be <code>null</code>
     * @return the previous value for {@link #REMARK}; may be
     *         <code>null</code>
     */
    public String setRemark(String remark) {
        return setProperty(REMARK, remark);
    }

    /**
     * Retrieves the {@link #FORMAT} value in this properties object.
     * @return the current value for {@link #FORMAT}; may be
     *         <code>null</code>
     */
    public String getFormatString() {
        return getProperty(FORMAT);
    }

    /**
     * Sets the {@link #FORMAT} property to a given value.
     * @param format the new remark; may be <code>null</code>
     * @return the previous value for {@link #FORMAT}; may be
     *         <code>null</code>
     */
    public String setFormatString(String format) {
        return setProperty(FORMAT, format);
    }

    /**
     * Retrieves the {@link #TRANSITION_LABEL} value in this properties
     * object.
     * @return the current value for {@link #TRANSITION_LABEL}; may be
     *         <code>null</code>
     */
    public String getTransitionLabel() {
        return getProperty(TRANSITION_LABEL);
    }

    /**
     * Sets the {@link #TRANSITION_LABEL} property to a given value.
     * @param label the new transition label; may be <code>null</code>
     * @return the previous value for {@link #TRANSITION_LABEL}; may be
     *         <code>null</code>
     */
    public String setTransitionLabel(String label) {
        return setProperty(TRANSITION_LABEL, label);
    }

    /**
     * Returns the version number stored in this properties object.
     * @return the version property; <code>0</code> if the property is not
     *         set.
     * @see #VERSION
     */
    public String getVersion() {
        String version = getProperty(VERSION);
        return version;
    }

    /**
     * Sets the version property to a given value.
     * @return the previous value of the version property; <code>0</code> if
     *         the property was not set.
     * @see #VERSION
     */
    public String setVersion(String version) {
        return setProperty(VERSION, version);
    }

    @Override
    public String setProperty(String keyword, String value) {
        testFixed(false);
        String oldValue;
        Key key = KEYS.get(keyword);
        if (value == null) {
            oldValue = (String) remove(keyword);
        } else if (key != null && !key.isSystem()
            && key.getDefaultValue().equals(value)) {
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
     * Returns the priority property from a given graph. The property is stored
     * under {@link #PRIORITY}. Yields {@link Rule#DEFAULT_PRIORITY} if the
     * graph has no properties, or the properties contain no priority value.
     * @see #getPriority()
     */
    static public int getPriority(Graph<?,?> graph) {
        GraphProperties properties = GraphInfo.getProperties(graph, false);
        if (properties == null) {
            return Action.DEFAULT_PRIORITY;
        } else {
            return properties.getPriority();
        }
    }

    /**
     * Returns the enabledness property from a given graph. The property is
     * stored under {@link #ENABLED}. Yields <code>true</code> if the
     * graph has no properties.
     * @see #isEnabled()
     */
    static public boolean isEnabled(Graph<?,?> graph) {
        if (graph == null) {
            return false;
        }
        GraphProperties properties = GraphInfo.getProperties(graph, false);
        if (properties == null) {
            return true;
        } else {
            return properties.isEnabled();
        }
    }

    /**
     * Returns the remark property from a given graph. The property is stored
     * under {@link #REMARK}. Yields <code>null</code> if the graph has
     * no properties, or the properties contain no remark value.
     * @see #getRemark()
     */
    static public String getRemark(Graph<?,?> graph) {
        GraphProperties properties = GraphInfo.getProperties(graph, false);
        if (properties == null) {
            return null;
        } else {
            return properties.getRemark();
        }
    }

    /**
     * Returns the string format property from a given graph. The property is stored
     * under {@link #FORMAT}. Yields <code>null</code> if the graph has
     * no properties, or the properties contain no remark value.
     * @see #getFormatString()
     */
    static public String getFormatString(Graph<?,?> graph) {
        GraphProperties properties = GraphInfo.getProperties(graph, false);
        if (properties == null) {
            return null;
        } else {
            return properties.getFormatString();
        }
    }

    /**
     * Returns the transition label from a given graph. The property is stored
     * under {@link #TRANSITION_LABEL}. Yields <code>null</code> if the
     * graph has no properties, or the properties contain no label value.
     * @see #getTransitionLabel()
     */
    static public String getTransitionLabel(Graph<?,?> graph) {
        GraphProperties properties = GraphInfo.getProperties(graph, false);
        if (properties == null) {
            return null;
        } else {
            return properties.getTransitionLabel();
        }
    }

    /**
     * Tests if a given string is a valid system-defined property key.
     */
    static public boolean isSystemKey(String keyword) {
        return KEYS.containsKey(keyword) && KEYS.get(keyword).isSystem();
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
    static public boolean isValidKey(Object key) {
        return key instanceof String
            && (isSystemKey((String) key) || isValidUserKey((String) key));
    }

    /** Mapping from graph property key names to keys. */
    public final static Map<String,Key> KEYS;

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
        PRIORITY("priority", Integer.toString(Action.DEFAULT_PRIORITY),
                new Property<String>() {
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
        ENABLED("enabled", Boolean.toString(true), new Property<String>() {
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
        REMARK("remark", new Property<String>() {
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
        FORMAT("printFormat", null, "Output format", null,
                new Property<String>() {
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
        private Key(String name, String defaultValue, Property<String> format,
                boolean system) {
            this(name, null, Groove.unCamel(name, false), defaultValue, format,
                system);
        }

        /** Defines a (system or non-system) property with a given name, default value and format property. */
        private Key(String name, String category, String description,
                String defaultValue, Property<String> format, boolean system) {
            this.name = (system ? SYSTEM_KEY_START : "") + name;
            this.defaultValue = defaultValue == null ? "" : defaultValue;
            this.category = category;
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
        public String getCategory() {
            return this.category;
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
        private final String category;
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
