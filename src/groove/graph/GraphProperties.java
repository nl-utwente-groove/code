/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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
package groove.graph;

import groove.calc.Property;
import groove.trans.Rule;
import groove.util.ListComparator;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Specialised properties class for graphs.
 * This can be stored as part of the graph info.
 * @author Arend Rensink
 * @version $Revision$
 */
public class GraphProperties extends Properties {
	/** Constructs an empty properties object. */
	public GraphProperties() {
		// empty
	}
	
	/** Constructs a properties object initialised on a given map. */
	public GraphProperties(Map<? extends Object,? extends Object> properties) {
		putAll(properties);
	}
	
	@Override
	public synchronized String toString() {
		StringBuffer result = new StringBuffer();
		if (isEmpty()) {
			result.append("No stored properties");
		} else {
			result.append("Properties:\n");
			for (Map.Entry<Object,Object> entry: entrySet()) {
				result.append("  "+entry+"\n");
			}
		}
		return result.toString();
	}

	/** 
	 * Returns a sorted copy of the keys of this map.
	 * The sorting is according to known keys first, then lexicographically.
	 */
	public SortedSet<String> getPropertyKeys() {
		SortedSet<String> result = new TreeSet<String>(new ListComparator<String>(DEFAULT_KEYS.keySet()));
		for (Object key: keySet()) {
			result.add((String) key);
		}
		return result;
	}
//	
//	/**
//	 * Returns the name stored in this properties object, if any.
//	 * The name is stored under key {@link #NAME_KEY}.
//	 * @return The stored name, or <code>null</code> if there is none
//	 */
//	public String getName() {
//		return getProperty(NAME_KEY);
//	}
//	
//	/**
//	 * Sets a name in this property object.
//	 * The name is stored under key {@link #NAME_KEY}.
//	 * @param name the name to be stored; non-<code>null</code>
//	 * @return the previously stored name, or <code>null</code> if there was none
//	 */
//	public String setName(String name) {
//		return (String) setProperty(NAME_KEY, name);
//	}
	
	/**
	 * Returns the priority stored in this properties object, if any.
	 * The priority is stored under key {@link #PRIORITY_KEY}.
	 * @return The stored priority, or {@link Rule#DEFAULT_PRIORITY} if there is none
	 */
	public int getPriority() {
		String result = getProperty(PRIORITY_KEY);
		if (result == null) {
			return Rule.DEFAULT_PRIORITY;
		} else {
			return Integer.parseInt(result);
		}
	}
	
	/**
	 * Sets a priority in this property object.
	 * The priority is stored under key {@link #PRIORITY_KEY}.
	 * @param priority the priority to be stored; non-negative
	 * @return the previously stored priority, or {@link Rule#DEFAULT_PRIORITY} if there was none
	 */
	public int setPriority(int priority) {
		String result;
		// if the new value is the default priority, remove the key instead
		if (priority == Rule.DEFAULT_PRIORITY) {
			result = (String) remove(PRIORITY_KEY);
		} else {
			result = (String) setProperty(PRIORITY_KEY, ""+priority);
		}
		if (result == null) {
			return Rule.DEFAULT_PRIORITY;
		} else {
			return Integer.parseInt(result);
		}
	}

	/**
	 * Returns the enabled status stored in this properties object, if any.
	 * The status is stored under key {@link #ENABLED_KEY}.
	 * @return The stored enabled status, or <code>true</code> if there is none
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
	 * Sets a enabled status in this property object.
	 * The status is stored under key {@link #ENABLED_KEY}.
	 * @param enabled the enabled status to be stored
	 * @return the previously stored status, or <code>true</code> if there was none
	 */
	public boolean setEnabled(boolean enabled) {
		String result;
		// if the new value is true (the default value), remove the key instead
		if (enabled) {
			result = (String) remove(ENABLED_KEY);
		} else {
			result = (String) setProperty(ENABLED_KEY, ""+enabled);
		}
		if (result == null) {
			return true;
		} else {
			return Boolean.parseBoolean(result);
		}
	}

	/**
	 * Retrieves the {@link #REMARK_KEY} value in this properties object.
	 * @return the current value for {@link #REMARK_KEY}; may be <code>null</code>
	 */
	public String getRemark() {
		return getProperty(REMARK_KEY);
	}
	
	/** 
	 * Sets the {@link #REMARK_KEY} property to a given value.
	 * @param remark the new remark; may be <code>null</code>
	 * @return the previous value for {@link #REMARK_KEY}; may be <code>null</code>
	 */
	public String setRemark(String remark) {
		return (String) setProperty(REMARK_KEY, remark);
	}
//	
//	/** 
//	 * Returns a static comparator, which orders property keys so that
//	 * the known keys come first, in a fixed order, followed by the user-defined
//	 * keys, in alphabetical order.
//	 */
//	static public Comparator<String> getKeyComparator() {
//		return keyComparator;
//	}
//	
//	/** 
//	 * Key for graph names.
//	 * The value should be an identifier with alphanumeric characters, 
//	 * hyphens and underscores. 
//	 */
//	static public final String NAME_KEY = "name";
	/**
	 * Key for rule priorities. 
	 * The corresponding value should be an integer.
	 */
	static public final String PRIORITY_KEY = "priority";
    /**
     * Rule enabledness key.
     * The corresponding value should be a boolean.
     */
    static public final String ENABLED_KEY = "enabled";
    /**
     * Rule remark key.
     * The corresponding value should be a string.
     */
    static public final String REMARK_KEY = "remark";
	/** Array of keys, in order of display appearance. */
	static public final Map<String,Property<String>> DEFAULT_KEYS;
	
	static {
		Map<String,Property<String>> defaultKeys = new LinkedHashMap<String,Property<String>>();
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
                return value.equals(""+true) || value.equals(""+false);
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
		DEFAULT_KEYS = Collections.unmodifiableMap(defaultKeys);
	}
	
	/** 
	 * Returns the priority property from a given graph.
	 * The property is stored under {@link #PRIORITY_KEY}.
	 * Yields {@link Rule#DEFAULT_PRIORITY} if the graph has no properties,
	 * or the properties contain no priority value.
	 * @see #getPriority()
	 */
	static public int getPriority(GraphShape graph) {
		GraphProperties properties = GraphInfo.getProperties(graph, false);
		if (properties == null) {
			return Rule.DEFAULT_PRIORITY;
		} else {
			return properties.getPriority();
		}
	}

	/** 
	 * Returns the enabledness property from a given graph.
	 * The property is stored under {@link #ENABLED_KEY}.
	 * Yields <code>true</code> if the graph has no properties,
	 * or the properties contain no enabledness value.
	 * @see #isEnabled()
	 */
	static public boolean isEnabled(GraphShape graph) {
		GraphProperties properties = GraphInfo.getProperties(graph, false);
		if (properties == null) {
			return true;
		} else {
			return properties.isEnabled();
		}
	}

	/** 
	 * Returns the remark property from a given graph.
	 * The property is stored under {@link #REMARK_KEY}.
	 * Yields <code>null</code> if the graph has no properties,
	 * or the properties contain no remark value.
	 * @see #getRemark()
	 */
	static public String getRemark(GraphShape graph) {
		GraphProperties properties = GraphInfo.getProperties(graph, false);
		if (properties == null) {
			return null;
		} else {
			return properties.getRemark();
		}
	}
}
