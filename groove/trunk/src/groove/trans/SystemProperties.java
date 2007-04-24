package groove.trans;

import groove.util.Groove;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;
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
     * @see #ATTRIBUTE_SUPPORT
     * @see #ATTRIBUTES_YES
     */
    public boolean isAttributed() {
    	String attributed = getProperty(SystemProperties.ATTRIBUTE_SUPPORT);
    	return attributed != null && attributed.equals(SystemProperties.ATTRIBUTES_YES);
    }
    
    /**
     * Sets the attributed propery to a given value.
     * @param attributed <code>true</code> if the rules have attributes
     */
    public void setAttributed(boolean attributed) {
    	setProperty(ATTRIBUTE_SUPPORT, attributed ? ATTRIBUTES_YES : ATTRIBUTES_NO);
    }

    /** 
     * Returns a list of control labels, according to the {@link #CONTROL_LABELS}
     * property of the rule system.
     * @see #CONTROL_LABELS
     */
    public List<String> getControlLabels() {
    	String controlLabels = getProperty(SystemProperties.CONTROL_LABELS);
    	if (controlLabels == null) {
    		return Collections.emptyList();
    	} else {
    		return Arrays.asList(controlLabels.split("\\s"));
    	}
    }

    /** 
     * Sets the control labels property.
     * @see #CONTROL_LABELS
     */
    public void setControlLabels(List<String> controlLabels) {
    	setProperty(CONTROL_LABELS, Groove.toString(controlLabels.toArray(), "", "", " "));
    }

    /** 
     * Returns a list of common labels, according to the {@link #COMMON_LABELS}
     * property of the rule system.
     * @see #COMMON_LABELS
     */
    public List<String> getCommonLabels() {
    	String commonLabels = getProperty(SystemProperties.COMMON_LABELS);
    	if (commonLabels == null) {
    		return Collections.emptyList();
    	} else {
    		return Arrays.asList(commonLabels.split("\\s"));
    	}
    }

    /** 
     * Sets the common labels property.
     * @see #COMMON_LABELS
     */
    public void setCommonLabels(List<String> commonLabels) {
    	setProperty(COMMON_LABELS, Groove.toString(commonLabels.toArray(), "", "", " "));
    }

    /** 
     * Returns a list of graph property names, according to the {@link #GRAPH_PROPERTIES}
     * property of the rule system.
     * @see #GRAPH_PROPERTIES
     */
    public List<String> getGraphProperties() {
    	String graphProperties = getProperty(GRAPH_PROPERTIES);
    	if (graphProperties == null) {
    		return Collections.emptyList();
    	} else {
    		return Arrays.asList(graphProperties.split("\\s"));
    	}
    }

    /** 
     * Sets the graph properties property.
     * @see #GRAPH_PROPERTIES
     */
    public void setGraphProperties(List<String> graphProperties) {
    	setProperty(GRAPH_PROPERTIES, Groove.toString(graphProperties.toArray(), "", "", " "));
    }

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
//	/**
//	 * Comment for <code>ruleSystem</code>
//	 */
//	private final RuleSystem ruleSystem;
	/**
	 * Property name of the list of control labels of a graph grammar.
     * The control labels are those labels which should be matched first
     * for optimal performance, presumably because they occur infrequently
     * or indicate a place where rules are likely to be applicable.
   	 */
	static public final String CONTROL_LABELS = "controlLabels";
	/**
	 * Property name of the list of common labels of a graph grammar.
     * The control labels are those labels which should be matched last
     * for optimal performance, presumably because they occur frequently.
	 */
	static public final String COMMON_LABELS = "commonLabels";
	/** 
	 * Property that determines if the graph grammar uses attributes.
	 * @see #ATTRIBUTES_YES
	 */
	static public final String ATTRIBUTE_SUPPORT = "attributeSupport";
	/**
	 * Value of {@link #ATTRIBUTE_SUPPORT} that means attributes are used.
	 */
	static public final String ATTRIBUTES_YES = "1";
	/**
	 * Value of {@link #ATTRIBUTE_SUPPORT} that means attributes are not used.
	 */
	static public final String ATTRIBUTES_NO = "0";
	/** 
	 * Property that determines the graph properties that can be stored.
	 */
	static public final String GRAPH_PROPERTIES = "graphProperties";
	
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
