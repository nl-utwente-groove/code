package groove.trans;

import groove.calc.Property;
import groove.util.Converter;
import groove.util.Groove;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * 
 * @author Arend Rensink
 * @version $Revision $
 */
public class SystemProperties extends java.util.Properties {
//	/** 
//	 * Constructs an empty properties object for a given rule system.
//	 * @param system the rule system with which these properties are associated
//	 */
//	SystemProperties(RuleSystem system) {
////		this.ruleSystem = system;
//		// empty
//	}
//	
//	/** Constructs an empty properties object, not associated to any rule system. */
//	public SystemProperties() {
//		this(null);
//		// empty
//	}
//	
	/** 
	 * Freezes the properties object, after which 
	 * changing any properties becomes illegal.
	 */
	public void setFixed() {
		fixed = true;
	}
	
    /** 
     * Indicates if the rule system is attributed, according to the
     * properties. 
     * @see #ATTRIBUTES_KEY
     * @see #ATTRIBUTES_YES
     */
    public boolean isAttributed() {
    	String attributed = getProperty(SystemProperties.ATTRIBUTES_KEY);
    	return attributed != null && attributed.equals(SystemProperties.ATTRIBUTES_YES);
    }
    
    /**
     * Sets the attributed propery to a given value.
     * @param attributed <code>true</code> if the rules have attributes
     */
    public void setAttributed(boolean attributed) {
    	setProperty(ATTRIBUTES_KEY, attributed ? ATTRIBUTES_YES : ATTRIBUTES_NO);
    }

    /** 
     * Returns a list of control labels, according to the {@link #CONTROL_LABELS_KEY}
     * property of the rule system.
     * @see #CONTROL_LABELS_KEY
     */
    public List<String> getControlLabels() {
    	String controlLabels = getProperty(SystemProperties.CONTROL_LABELS_KEY);
    	if (controlLabels == null) {
    		return Collections.emptyList();
    	} else {
    		return Arrays.asList(controlLabels.split("\\s"));
    	}
    }

    /** 
     * Sets the control labels property.
     * @see #CONTROL_LABELS_KEY
     */
    public void setControlLabels(List<String> controlLabels) {
    	setProperty(CONTROL_LABELS_KEY, Groove.toString(controlLabels.toArray(), "", "", " "));
    }

    /** 
     * Returns a list of common labels, according to the {@link #COMMON_LABELS_KEY}
     * property of the rule system.
     * @see #COMMON_LABELS_KEY
     */
    public List<String> getCommonLabels() {
    	String commonLabels = getProperty(SystemProperties.COMMON_LABELS_KEY);
    	if (commonLabels == null) {
    		return Collections.emptyList();
    	} else {
    		return Arrays.asList(commonLabels.split("\\s"));
    	}
    }

    /** 
     * Sets the common labels property.
     * @see #COMMON_LABELS_KEY
     */
    public void setCommonLabels(List<String> commonLabels) {
    	setProperty(COMMON_LABELS_KEY, Groove.toString(commonLabels.toArray(), "", "", " "));
    }
    
    /** 
     * Sets the injectivity property to a certain value.
     * @param injective if <code>true</code>, non-injective matches are disallowed
     */
    public void setInjective(boolean injective) {
    	setProperty(INJECTIVE_KEY, ""+injective);
    }
    
    /**
     * Returns the value of the injectivity property.
     * @return if <code>true</code>, non-injective matches are disallowed
     */
    public boolean isInjective() {
    	String result = getProperty(INJECTIVE_KEY);
    	return result != null && new Boolean(result);
    }
    
    /** 
     * Sets the dangling edge check to a certain value.
     * @param dangling if <code>true</code>, matches with dangling edges are disallowed
     */
    public void setCheckDangling(boolean dangling) {
    	setProperty(DANGLING_KEY, ""+dangling);
    }
    
    /**
     * Returns the value of the dangling edge property.
     * @return if <code>true</code>, matches with dangling edges are disallowed.
     */
    public boolean isCheckDangling() {
    	String result = getProperty(DANGLING_KEY);
    	return result != null && new Boolean(result);
    }
    
//
//    /** 
//     * Returns a list of graph property names, according to the {@link #GRAPH_PROPERTIES}
//     * property of the rule system.
//     * @see #GRAPH_PROPERTIES
//     */
//    public List<String> getGraphProperties() {
//    	String graphProperties = getProperty(GRAPH_PROPERTIES);
//    	if (graphProperties == null) {
//    		return Collections.emptyList();
//    	} else {
//    		return Arrays.asList(graphProperties.split("\\s"));
//    	}
//    }
//
//    /** 
//     * Sets the graph properties property.
//     * @see #GRAPH_PROPERTIES
//     */
//    public void setGraphProperties(List<String> graphProperties) {
//    	setProperty(GRAPH_PROPERTIES, Groove.toString(graphProperties.toArray(), "", "", " "));
//    }

	/** 
	 * Before calling the super method, tests if the properties are fixed
	 * and throws an {@link IllegalStateException} if this is the case.
	 * @throws IllegalStateException if the graph has been fixesd.
	 * @see #setFixed()
	 */
	@Override
	public synchronized Object setProperty(String key, String value) {
		testFixed();
		return super.setProperty(key, value);
	}

	/** 
	 * Before calling the super method, tests if the properties are fixed
	 * and throws an {@link IllegalStateException} if this is the case.
	 * @throws IllegalStateException if the graph has been fixed.
	 * @see #setFixed()
	 */
    @Override
	public synchronized void load(InputStream inStream) throws IOException {
		testFixed();
		super.load(inStream);
	}

	/** 
	 * Before calling the super method, tests if the properties are fixed
	 * and throws an {@link IllegalStateException} if this is the case.
	 * @throws IllegalStateException if the graph has been fixed.
	 * @see #setFixed()
	 */
	@Override
	public synchronized void loadFromXML(InputStream in) throws IOException, InvalidPropertiesFormatException {
		testFixed();
		super.loadFromXML(in);
	}

	/** 
	 * Before calling the super method, tests if the properties are fixed
	 * and throws an {@link IllegalStateException} if this is the case.
	 * @throws IllegalStateException if the graph has been fixed.
	 * @see #setFixed()
	 */
	@Override
	public synchronized void clear() {
		testFixed();
		super.clear();
	}

	/** 
	 * Before calling the super method, tests if the properties are fixed
	 * and throws an {@link IllegalStateException} if this is the case.
	 * @throws IllegalStateException if the graph has been fixed.
	 * @see #setFixed()
	 */
	@Override
	public synchronized Object put(Object key, Object value) {
		testFixed();
		return super.put(key, value);
	}

	/** 
	 * Before calling the super method, tests if the properties are fixed
	 * and throws an {@link IllegalStateException} if this is the case.
	 * @throws IllegalStateException if the graph has been fixed.
	 * @see #setFixed()
	 */
	@Override
	public synchronized Object remove(Object key) {
		testFixed();
		return super.remove(key);
	}

	/** Returns an unmodifiable set. */
	@Override
	public Set<Object> keySet() {
		return Collections.unmodifiableSet(super.keySet());
	}

	/** Returns an unmodifiable set. */
	@Override
	public Set<Entry<Object, Object>> entrySet() {
		return Collections.unmodifiableSet(super.entrySet());
	}

	/**
     * Tests if the graph has been fixed, and throws an exception if this
     * is the case.
     * @see #setFixed()
     * @throws IllegalStateException if the graph has been fixed.
     */
    private void testFixed() throws IllegalStateException {
    	if (fixed) {
    		throw new IllegalStateException("Cannot change fixed properties");
    	}
    }
    
    /** 
     * Flag to indicate that the properties have been frozen.
     */
    private boolean fixed;
    /**
	 * Property name of the list of control labels of a graph grammar.
     * The control labels are those labels which should be matched first
     * for optimal performance, presumably because they occur infrequently
     * or indicate a place where rules are likely to be applicable.
   	 */
	static public final String CONTROL_LABELS_KEY = "controlLabels";
	/**
	 * Property name of the list of common labels of a graph grammar.
     * The control labels are those labels which should be matched last
     * for optimal performance, presumably because they occur frequently.
	 */
	static public final String COMMON_LABELS_KEY = "commonLabels";
	/** 
	 * Property that determines if the graph grammar uses attributes.
	 * @see #ATTRIBUTES_YES
	 */
	static public final String ATTRIBUTES_KEY = "attributeSupport";
	/**
	 * Value of {@link #ATTRIBUTES_KEY} that means attributes are used.
	 */
	static public final String ATTRIBUTES_YES = "1";
	/**
	 * Value of {@link #ATTRIBUTES_KEY} that means attributes are not used.
	 */
	static public final String ATTRIBUTES_NO = "0";
	/** 
	 * Property name of the injectivity of the rule system.
	 * If <code>true</code>, all rules should be matched injectively.
	 * Default is <code>false</code>.
	 */
	static public final String INJECTIVE_KEY = "matchInjective";
	/** 
	 * Property name of the dankling edge check.
	 * If <code>true</code>, all matches that leave dangling edges are invalid.
	 * Default is <code>false</code>.
	 */
	static public final String DANGLING_KEY = "checkDangling";
//	/** 
//	 * Property that determines the graph properties that can be stored.
//	 */
//	static public final String GRAPH_PROPERTIES = "graphProperties";
	
	/**
	 * List of system-defined keys, in the order in which they are to appear in a properties editor. 
	 */
	static public final Map<String,Property<String>> DEFAULT_KEYS;
	
	static {
		String attributesDescription = String.format("'%s' for default attributes", ATTRIBUTES_YES);
		StringBuilder attributesCommentBuilder = new StringBuilder();
		attributesCommentBuilder.append("Indicates whether the graphs and rules are attributed\n");
		attributesCommentBuilder.append(String.format("Use '%s' for default attributes, '%s' or empty for no attributes",
		ATTRIBUTES_YES, ATTRIBUTES_NO));
		String attributesComment = Converter.HTML_TAG.on(Converter.toHtml(attributesCommentBuilder)).toString();
		Map<String,Property<String>> defaultKeys = new LinkedHashMap<String,Property<String>>();
		defaultKeys.put(ATTRIBUTES_KEY, new Property<String>(attributesDescription, attributesComment) {
			@Override
			public boolean isSatisfied(String value) {
				return value.equals(ATTRIBUTES_YES) || value.equals(ATTRIBUTES_NO);
			}
		});
		defaultKeys.put(CONTROL_LABELS_KEY, new Property.True<String>("A list of rare labels, used to optimise rule matching"));
		defaultKeys.put(COMMON_LABELS_KEY, new Property.True<String>("A list of frequent labels, used to optimise rule matching"));
		defaultKeys.put(INJECTIVE_KEY, new Property.IsBoolean("Flag controlling if matches should be injective", true));
		DEFAULT_KEYS = Collections.unmodifiableMap(defaultKeys);
	}
	
	/** 
	 * One-line regular expression describing the system properties,
	 * with a parameter position for the name of the rule system.
	 */ 
	static public final String DESCRIPTION = "Rule system properties for %s";
	/** Map storing default property instances. */
	static private final Map<Boolean,SystemProperties> instances = new HashMap<Boolean,SystemProperties>();
	
	static {
		// initialize the instance map
		for (boolean attributed: new boolean[] { true, false } ) {
			SystemProperties properties = new SystemProperties();
			properties.setAttributed(attributed);
			properties.setFixed();
			instances.put(attributed, properties);
		}
	}
	
	/** 
	 * Returns a default, fixed properties object, with a given value for
	 * attribute support.
	 * @param attributed <code>true</code> if the attributed property of the
	 * returned properties object is to be set
	 */
	static public SystemProperties getInstance(boolean attributed) {
		return instances.get(attributed);
	}

	/** 
	 * The default rule properties: not attributed, no control or common labels,
	 * and a {@link DefaultRuleFactory}. 
	 */
	static public final SystemProperties DEFAULT_PROPERTIES = getInstance(false);

}
