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

import groove.trans.Action;
import groove.trans.Rule;
import groove.util.DefaultFixable;
import groove.util.ExprParser;
import groove.util.Fixable;
import groove.util.ListComparator;
import groove.util.Property;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

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
        return (GraphProperties) super.clone();
    }

    /**
     * Returns a sorted copy of the keys of this map. The sorting is according
     * to known keys first, then lexicographically.
     */
    public SortedSet<String> getPropertyKeys() {
        SortedSet<String> result =
            new TreeSet<String>(new ListComparator<String>(
                DEFAULT_USER_KEYS.keySet()));
        for (Object key : keySet()) {
            result.add((String) key);
        }
        return result;
    }

    /**
     * Returns the priority stored in this properties object, if any. The
     * priority is stored under key {@link #PRIORITY_KEY}.
     * @return The stored priority, or {@link Rule#DEFAULT_PRIORITY} if there is
     *         none
     */
    public int getPriority() {
        String result = getProperty(PRIORITY_KEY);
        if (result == null) {
            return Action.DEFAULT_PRIORITY;
        } else {
            return Integer.parseInt(result);
        }
    }

    /**
     * Sets a priority in this property object. The priority is stored under key
     * {@link #PRIORITY_KEY}.
     * @param priority the priority to be stored; non-negative
     * @return the previously stored priority, or {@link Rule#DEFAULT_PRIORITY}
     *         if there was none
     */
    public int setPriority(int priority) {
        String result;
        // if the new value is the default priority, remove the key instead
        if (priority == Action.DEFAULT_PRIORITY) {
            result = (String) remove(PRIORITY_KEY);
        } else {
            result = (String) super.setProperty(PRIORITY_KEY, "" + priority);
        }
        if (result == null) {
            return Action.DEFAULT_PRIORITY;
        } else {
            return Integer.parseInt(result);
        }
    }

    /**
     * Returns the enabled status stored in this properties object, if any. The
     * status is stored under key {@link #ENABLED_KEY}.
     * @return The stored enabled status, or <code>true</code> if there is
     *         none
     */
    public boolean isEnabled() {
        String result = getProperty(ENABLED_KEY);
        if (result == null) {
            return true;
        } else {
            return Boolean.parseBoolean(result);
        }
    }

    /**
     * Sets a enabled status in this property object. The status is stored under
     * key {@link #ENABLED_KEY}.
     * @param enabled the enabled status to be stored
     * @return the previously stored status, or <code>true</code> if there was
     *         none
     */
    public boolean setEnabled(boolean enabled) {
        String result;
        // if the new value is true (the default value), remove the key instead
        if (enabled) {
            result = (String) remove(ENABLED_KEY);
        } else {
            result = (String) super.setProperty(ENABLED_KEY, "" + enabled);
        }
        if (result == null) {
            return true;
        } else {
            return Boolean.parseBoolean(result);
        }
    }

    /**
     * Retrieves the {@link #REMARK_KEY} value in this properties object.
     * @return the current value for {@link #REMARK_KEY}; may be
     *         <code>null</code>
     */
    public String getRemark() {
        return getProperty(REMARK_KEY);
    }

    /**
     * Sets the {@link #REMARK_KEY} property to a given value.
     * @param remark the new remark; may be <code>null</code>
     * @return the previous value for {@link #REMARK_KEY}; may be
     *         <code>null</code>
     */
    public String setRemark(String remark) {
        if (remark == null) {
            return (String) remove(REMARK_KEY);
        } else {
            return (String) super.setProperty(REMARK_KEY, remark);
        }
    }

    /**
     * Retrieves the {@link #FORMAT_KEY} value in this properties object.
     * @return the current value for {@link #FORMAT_KEY}; may be
     *         <code>null</code>
     */
    public String getFormatString() {
        return getProperty(FORMAT_KEY);
    }

    /**
     * Sets the {@link #FORMAT_KEY} property to a given value.
     * @param format the new remark; may be <code>null</code>
     * @return the previous value for {@link #FORMAT_KEY}; may be
     *         <code>null</code>
     */
    public String setFormatString(String format) {
        if (format == null) {
            return (String) remove(FORMAT_KEY);
        } else {
            return (String) super.setProperty(FORMAT_KEY, format);
        }
    }

    /**
     * Retrieves the {@link #TRANSITION_LABEL_KEY} value in this properties
     * object.
     * @return the current value for {@link #TRANSITION_LABEL_KEY}; may be
     *         <code>null</code>
     */
    public String getTransitionLabel() {
        return getProperty(TRANSITION_LABEL_KEY);
    }

    /**
     * Sets the {@link #TRANSITION_LABEL_KEY} property to a given value.
     * @param label the new transition label; may be <code>null</code>
     * @return the previous value for {@link #TRANSITION_LABEL_KEY}; may be
     *         <code>null</code>
     */
    public String setTransitionLabel(String label) {
        if (label == null) {
            return (String) remove(TRANSITION_LABEL_KEY);
        } else {
            return (String) super.setProperty(TRANSITION_LABEL_KEY, label);
        }
    }

    /**
     * Returns the version number stored in this properties object.
     * @return the version property; <code>0</code> if the property is not
     *         set.
     * @see #VERSION_KEY
     */
    public String getVersion() {
        String version = getProperty(VERSION_KEY);
        return version;
    }

    /**
     * Sets the version property to a given value.
     * @return the previous value of the version property; <code>0</code> if
     *         the property was not set.
     * @see #VERSION_KEY
     */
    public String setVersion(String version) {
        String oldVersion;
        if (version != null) {
            oldVersion = (String) super.setProperty(VERSION_KEY, version);
        } else {
            oldVersion = (String) remove(VERSION_KEY);
        }
        return oldVersion;
    }

    @Override
    public Object setProperty(String key, String value) {
        testFixed(false);
        Object oldValue;
        if (!isSystemKey(key) && getDefaultValue(key).equals(value)) {
            oldValue = remove(key);
        } else {
            oldValue = super.setProperty(key, value);
        }
        return oldValue;
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
     * @param key the key of the property.
     * @return the default valued associated with the key.
     */
    static public String getDefaultValue(String key) {
        if (key.equals(PRIORITY_KEY)) {
            return Integer.toString(Action.DEFAULT_PRIORITY);
        } else if (key.equals(ENABLED_KEY)) {
            return Boolean.toString(true);
        } else {
            return "";
        }
    }

    /**
     * Returns the priority property from a given graph. The property is stored
     * under {@link #PRIORITY_KEY}. Yields {@link Rule#DEFAULT_PRIORITY} if the
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
     * stored under {@link #ENABLED_KEY}. Yields <code>true</code> if the
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
     * under {@link #REMARK_KEY}. Yields <code>null</code> if the graph has
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
     * under {@link #FORMAT_KEY}. Yields <code>null</code> if the graph has
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
     * under {@link #TRANSITION_LABEL_KEY}. Yields <code>null</code> if the
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
     * Tests if a given object is a valid user-defined property key. This
     * returns <code>true</code> if the key starts with
     * {@link #SYSTEM_KEY_START}.
     */
    static public boolean isSystemKey(String key) {
        return key != null && key.length() > 0
            && key.charAt(0) == SYSTEM_KEY_START;
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
     * key. This returns <code>true</code> if the object is a string that
     * passes {@link ExprParser#isIdentifier(String)}. System-defined property
     * keys will by convention start with {@link #SYSTEM_KEY_START}; the known
     * system keys are collected in {@link #SYSTEM_KEYS}.
     */
    static public boolean isValidKey(Object key) {
        return key instanceof String
            && (SYSTEM_KEYS.contains(key) || isValidUserKey((String) key));
    }

    /**
     * Key for rule priorities. The corresponding value should be an integer.
     */
    static public final String PRIORITY_KEY = "priority";
    /**
     * Rule enabledness key. The corresponding value should be a boolean.
     */
    static public final String ENABLED_KEY = "enabled";
    /**
     * Rule remark key. The corresponding value should be a string.
     */
    static public final String REMARK_KEY = "remark";
    /**
     * Format string for stdout.
     */
    static public final String FORMAT_KEY = "printFormat";
    /**
     * Transition label for the rule application.
     * The corresponding value should be a string.
     */
    static public final String TRANSITION_LABEL_KEY = "transitionLabel";

    /**
     * Start character that distinguishes user-defined property keys from system
     * keys. Any string starting with this character is a system key.
     */
    static public final char SYSTEM_KEY_START = '$';
    /**
     * Key for version property.
     */
    public static final String VERSION_KEY = SYSTEM_KEY_START + "version";
    /** The set of all special property keys. */
    public static final Set<String> SYSTEM_KEYS =
        Collections.unmodifiableSet(new TreeSet<String>(
            Arrays.asList(new String[] {VERSION_KEY})));
    /** Array of keys, in order of display appearance. */
    static public final Map<String,Property<String>> DEFAULT_USER_KEYS;

    static {
        Map<String,Property<String>> defaultKeys =
            new LinkedHashMap<String,Property<String>>();
        defaultKeys.put(REMARK_KEY, new Property<String>() {
            @Override
            public boolean isSatisfied(String value) {
                return value.trim().length() > 0;
            }

            @Override
            public String getDescription() {
                return "a one-line description of the purpose of the graph or rule";
            }

            @Override
            public String getComment() {
                return "A one-line description of the rule, shown e.g. as tool tip";
            }
        });
        defaultKeys.put(PRIORITY_KEY, new Property<String>() {
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
                return "a non-negative rule priority";
            }

            @Override
            public String getComment() {
                return "Higher-priority rules are evaluated first";
            }
        });
        defaultKeys.put(ENABLED_KEY, new Property<String>() {
            @Override
            public boolean isSatisfied(String value) {
                return value.equals("" + true) || value.equals("" + false);
            }

            @Override
            public String getDescription() {
                return "a boolean indicating rule enabledness";
            }

            @Override
            public String getComment() {
                return "Disabled rules are never evaluated";
            }
        });
        defaultKeys.put(TRANSITION_LABEL_KEY, new Property<String>() {
            @Override
            public boolean isSatisfied(String value) {
                return true;
            }

            @Override
            public String getDescription() {
                return "A string to be used as the transition label in the "
                    + "LTS. If empty, defaults to the rule name.";
            }

            @Override
            public String getComment() {
                return "A string to be used as the transition label in the "
                    + "LTS. If empty, defaults to the rule name.";
            }
        });
        defaultKeys.put(FORMAT_KEY, new Property<String>() {
            @Override
            public boolean isSatisfied(String value) {
                return value.trim().length() > 0;
            }

            @Override
            public String getDescription() {
                return "a format string for standard output on rule application";
            }

            @Override
            public String getComment() {
                return "If nonempty, rule application prints this (instantiated) string";
            }
        });
        DEFAULT_USER_KEYS = Collections.unmodifiableMap(defaultKeys);
    }
}
