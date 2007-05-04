/**
 * 
 */
package groove.graph;

import groove.trans.Rule;
import groove.util.ListComparator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
		SortedSet<String> result = new TreeSet<String>(new ListComparator<String>(DEFAULT_KEYS));
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
	/** Array of keys, in order of display appearance. */
	static public final List<String> DEFAULT_KEYS = Collections.unmodifiableList(Arrays.asList(PRIORITY_KEY, ENABLED_KEY));
//	/** Map of keys to display priority, initialised from {@link #DEFAULT_KEYS}. */
//	static private final Map<String,Integer> knownKeyIndexMap = new HashMap<String,Integer>();
//	
//	static {
//		for (int i = 0; i < DEFAULT_KEYS.size(); i++) {
//			knownKeyIndexMap.put(DEFAULT_KEYS.get(i), i);
//		}
//	}
//	
//	/** The fixed comparator. */
//	static private final KeyComparator keyComparator = new KeyComparator();
//	
//	/** 
//	 * Compares two strings as if they were property keys.
//	 * The known keys come first, in order of appearance in #kno; if this
//	 * makes no differe,ce alphabetical ordering is used.
//	 */
//	static private class KeyComparator implements Comparator<String> {
//		/** 
//		 * First compares the strings as to their position in #known, then 
//		 * in alphabetical order.
//		 */
//		public int compare(String o1, String o2) {
//			Integer index1Value = knownKeyIndexMap.get(o1);
//			int index1 = index1Value == null ? Integer.MAX_VALUE : index1Value;
//			Integer index2Value = knownKeyIndexMap.get(o2);
//			int index2 = index2Value == null ? Integer.MAX_VALUE : index2Value;
//			int result = index1 - index2;
//			if (result == 0) {
//				result = o1.compareTo(o2);
//			}
//			return result;
//		}
//	}
	
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
}
