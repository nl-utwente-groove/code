package groove.trans;

import groove.util.Groove;
import groove.util.Property;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.InvalidPropertiesFormatException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * Properties class for graph production systems.
 * @author Arend Rensink
 * @version $Revision $
 */
public class SystemProperties extends java.util.Properties {
    /**
     * Freezes the properties object, after which changing any properties
     * becomes illegal.
     */
    public void setFixed() {
        this.fixed = true;
    }

    /**
     * Indicates if the LTS labels should contain transition parameters. Default
     * value: <code>false</code>.
     */
    public boolean isUseParameters() {
        String params = getProperty(SystemProperties.PARAMETERS_KEY);
        return params != null
            && (new Boolean(params) || params.equals(SystemProperties.PARAMETERS_YES));
    }

    /**
     * Indicates if the LTS labels should be surrounded by angular brackets.
     * Default value: <code>false</code>.
     */
    public boolean isShowTransitionBrackets() {
        String property = getProperty(SystemProperties.TRANSITION_BRACKETS_KEY);
        return property != null
            && (new Boolean(property) || property.equals(SystemProperties.TRANSITION_BRACKETS_YES));
    }

    /** Sets the {@link #PARAMETERS_KEY} property to the given value * */
    public void setUseParameters(boolean useParameters) {
        setProperty(PARAMETERS_KEY, "" + useParameters);
    }

    /**
     * Returns a list of control labels, according to the
     * {@link #CONTROL_LABELS_KEY} property of the rule system.
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
        setProperty(CONTROL_LABELS_KEY, Groove.toString(
            controlLabels.toArray(), "", "", " "));
    }

    /**
     * Returns a list of common labels, according to the
     * {@link #COMMON_LABELS_KEY} property of the rule system.
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
        setProperty(COMMON_LABELS_KEY, Groove.toString(commonLabels.toArray(),
            "", "", " "));
    }

    /**
     * Sets the injectivity property to a certain value.
     * @param injective if <code>true</code>, non-injective matches are
     *        disallowed
     */
    public void setInjective(boolean injective) {
        setProperty(INJECTIVE_KEY, "" + injective);
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
     * @param dangling if <code>true</code>, matches with dangling edges are
     *        disallowed
     */
    public void setCheckDangling(boolean dangling) {
        setProperty(DANGLING_KEY, "" + dangling);
    }

    /**
     * Returns the value of the dangling edge property.
     * @return if <code>true</code>, matches with dangling edges are
     *         disallowed.
     */
    public boolean isCheckDangling() {
        String result = getProperty(DANGLING_KEY);
        return result != null && new Boolean(result);
    }

    /**
     * Sets the creator edge check to a certain value.
     * @param check if <code>true</code>, creator edges are treated as
     *        negative application conditions
     */
    public void setCheckCreatorEdges(boolean check) {
        setProperty(CREATOR_EDGE_KEY, "" + check);
    }

    /**
     * Returns the value of the creator edge check property.
     * @return if <code>true</code>, creator edges are treated as negative
     *         application conditions
     */
    public boolean isCheckCreatorEdges() {
        String result = getProperty(CREATOR_EDGE_KEY);
        return result != null && new Boolean(result);
    }

    /**
     * Sets the graph isomorphism check to a certain value.
     * @param check if <code>true</code>, state graphs are compared up to
     *        isomorphism
     */
    public void setCheckIsomorphism(boolean check) {
        setProperty(ISOMORPHISM_KEY, "" + check);
    }

    /**
     * Returns the value of the graph isomorphism check property.
     * @return if <code>true</code>, state graphs are compared up to
     *         isomorphism
     */
    public boolean isCheckIsomorphism() {
        String result = getProperty(ISOMORPHISM_KEY);
        return result == null || new Boolean(result);
    }

    /**
     * Sets the RHS-as-NAC property to a certain value.
     * @param value if <code>true</code>, the RHS is treated as a negative
     *        application condition, preventing the same rule instance from
     *        being applied twice in a row
     */
    public void setRhsAsNac(boolean value) {
        setProperty(RHS_AS_NAC_KEY, "" + value);
    }

    /**
     * Returns the value of the RHS-as-NAC property.
     * @return if <code>true</code>, the RHS is treated as a negative
     *         application condition, preventing the same rule instance from
     *         being applied twice in a row
     */
    public boolean isRhsAsNac() {
        String result = getProperty(RHS_AS_NAC_KEY);
        return result != null && new Boolean(result);
    }

    /**
     * Before calling the super method, tests if the properties are fixed and
     * throws an {@link IllegalStateException} if this is the case.
     * @throws IllegalStateException if the graph has been fixesd.
     * @see #setFixed()
     */
    @Override
    public synchronized Object setProperty(String key, String value) {
        testFixed();
        return super.setProperty(key, value);
    }

    /**
     * Before calling the super method, tests if the properties are fixed and
     * throws an {@link IllegalStateException} if this is the case.
     * @throws IllegalStateException if the graph has been fixed.
     * @see #setFixed()
     */
    @Override
    public synchronized void load(InputStream inStream) throws IOException {
        testFixed();
        super.load(inStream);
    }

    /**
     * Before calling the super method, tests if the properties are fixed and
     * throws an {@link IllegalStateException} if this is the case.
     * @throws IllegalStateException if the graph has been fixed.
     * @see #setFixed()
     */
    @Override
    public synchronized void loadFromXML(InputStream in) throws IOException,
        InvalidPropertiesFormatException {
        testFixed();
        super.loadFromXML(in);
    }

    /**
     * Before calling the super method, tests if the properties are fixed and
     * throws an {@link IllegalStateException} if this is the case.
     * @throws IllegalStateException if the graph has been fixed.
     * @see #setFixed()
     */
    @Override
    public synchronized void clear() {
        testFixed();
        super.clear();
    }

    /**
     * Before calling the super method, tests if the properties are fixed and
     * throws an {@link IllegalStateException} if this is the case.
     * @throws IllegalStateException if the graph has been fixed.
     * @see #setFixed()
     */
    @Override
    public synchronized Object put(Object key, Object value) {
        testFixed();
        return super.put(key, value);
    }

    /**
     * Before calling the super method, tests if the properties are fixed and
     * throws an {@link IllegalStateException} if this is the case.
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
    public Set<Entry<Object,Object>> entrySet() {
        return Collections.unmodifiableSet(super.entrySet());
    }

    /**
     * Tests if the graph has been fixed, and throws an exception if this is the
     * case.
     * @see #setFixed()
     * @throws IllegalStateException if the graph has been fixed.
     */
    private void testFixed() throws IllegalStateException {
        if (this.fixed) {
            throw new IllegalStateException("Cannot change fixed properties");
        }
    }

    /**
     * Flag to indicate that the properties have been frozen.
     */
    private boolean fixed;

    /**
     * Returns a default, fixed properties object, with a given value for
     * attribute support.
     */
    static public SystemProperties getInstance() {
        return instance;
    }

    /**
     * Tests whether {@link #isCheckDangling()} holds for a given properties
     * object. If the properties object is <code>null</code>, the method
     * returns <code>false</code>.
     * @param properties the properties to be tested; may be <code>null</code>
     * @return <true> if <code>properties</code> is not <code>null</code>
     *         and satisfies {@link #isCheckDangling()}
     */
    static public boolean isCheckDangling(SystemProperties properties) {
        return properties != null && properties.isCheckDangling();
    }

    /**
     * Name of the file containing the used control program. Will only be loaded
     * when the file exists in the grammar directory.
     */
    static public final String CONTROL_PROGRAM_KEY = "controlProgram";

    /**
     * Property name of the list of control labels of a graph grammar. The
     * control labels are those labels which should be matched first for optimal
     * performance, presumably because they occur infrequently or indicate a
     * place where rules are likely to be applicable.
     */
    static public final String CONTROL_LABELS_KEY = "controlLabels";

    /**
     * Property name of the list of common labels of a graph grammar. The
     * control labels are those labels which should be matched last for optimal
     * performance, presumably because they occur frequently.
     */
    static public final String COMMON_LABELS_KEY = "commonLabels";

    /** (User) Property that holds the grammar history (max 10 separated by ',') * */
    static public final String HISTORY_KEY = "open_history";

    /** (User) Property that holds the user settings separated by ',') **/
    static public final String USER_SETTINGS = "userSettings";

    /**
     * Property that determines if transition parameters are included in the LTS
     * transition labels
     */
    static public final String PARAMETERS_KEY = "transitionParameters";

    /** Value of {@link #PARAMETERS_KEY} that means parameters are used * */
    static public final String PARAMETERS_YES = "1";

    /** Value of {@link #PARAMETERS_KEY} that means parameters are not used * */
    static public final String PARAMETERS_NO = "0";

    /**
     * Property that determines if transition parameters are included in the LTS
     * transition labels
     */
    static public final String TRANSITION_BRACKETS_KEY = "transitionBrackets";

    /**
     * Value of {@link #TRANSITION_BRACKETS_KEY} that means transition brackets
     * are included *
     */
    static public final String TRANSITION_BRACKETS_YES = "1";

    /**
     * Value of {@link #TRANSITION_BRACKETS_KEY} that means transition brackets
     * are not included *
     */
    static public final String TRANSITION_BRACKETS_NO = "0";

    /**
     * Property name of the injectivity of the rule system. If <code>true</code>,
     * all rules should be matched injectively. Default is <code>false</code>.
     */
    static public final String INJECTIVE_KEY = "matchInjective";
    /**
     * Property name of the dangling edge check. If <code>true</code>, all
     * matches that leave dangling edges are invalid. Default is
     * <code>false</code>.
     */
    static public final String DANGLING_KEY = "checkDangling";
    /**
     * Property name of the creator edge check. If <code>true</code>, creator
     * edges are implicitly treated as (individual) NACs. Default is
     * <code>false</code>.
     */
    static public final String CREATOR_EDGE_KEY = "checkCreatorEdges";
    /**
     * Property name of the isomorphism check. If <code>true</code>, state
     * graphs are compared up to isomorphism; otherwise, they are compared up to
     * equality. Default is <code>true</code>.
     */
    static public final String ISOMORPHISM_KEY = "checkIsomorphism";
    /**
     * Property name of the RHS-as-NAC property. If <code>true</code>, each
     * RHS is implicitly treated as a NAC. Default is <code>false</code>.
     */
    static public final String RHS_AS_NAC_KEY = "rhsIsNAC";
    /**
     * Property name for one-line comments on the graph production system.
     */
    static public final String REMARK_KEY = "remark";
    /**
     * List of system-defined keys, in the order in which they are to appear in
     * a properties editor.
     */
    static public final Map<String,Property<String>> DEFAULT_KEYS;

    static {
        Map<String,Property<String>> defaultKeys =
            new LinkedHashMap<String,Property<String>>();
        defaultKeys.put(REMARK_KEY, new Property.True<String>(
            "A one-line description of the graph production system"));
        // String attributesDescription = String.format("'%s' for default
        // attributes", ATTRIBUTES_YES);
        // StringBuilder attributesCommentBuilder = new StringBuilder();
        // attributesCommentBuilder.append("Indicates whether the graphs and
        // rules are attributed\n");
        // attributesCommentBuilder.append(String.format("Use '%s' for default
        // attributes, '%s' or empty for no attributes",
        // ATTRIBUTES_YES, ATTRIBUTES_NO));
        // String attributesComment =
        // Converter.HTML_TAG.on(Converter.toHtml(attributesCommentBuilder)).toString();
        // defaultKeys.put(ATTRIBUTES_KEY, new
        // Property<String>(attributesDescription, attributesComment) {
        // @Override
        // public boolean isSatisfied(String value) {
        // return value.equals(ATTRIBUTES_YES) || value.equals(ATTRIBUTES_NO);
        // }
        // });
        defaultKeys.put(INJECTIVE_KEY, new Property.IsBoolean(
            "Flag controlling if matches should be injective", true));
        defaultKeys.put(
            DANGLING_KEY,
            new Property.IsBoolean(
                "Flag controlling if dangling edges should be forbidden rather than deleted",
                true));
        defaultKeys.put(
            CREATOR_EDGE_KEY,
            new Property.IsBoolean(
                "Flag controlling if creator edges should be treated as implicit NACs",
                true));
        defaultKeys.put(RHS_AS_NAC_KEY,
            new Property.IsBoolean(
                "Flag controlling if RHSs should be treated as implicit NACs",
                true));
        defaultKeys.put(ISOMORPHISM_KEY,
            new Property.IsBoolean(
                "Flag controlling state graphs are checked up to isomorphism",
                true));
        defaultKeys.put(TRANSITION_BRACKETS_KEY, new IsExtendedBoolean(
            "Flag controlling if transition labels should be bracketed"));
        defaultKeys.put(
            PARAMETERS_KEY,
            new IsExtendedBoolean(
                "Flag controlling if transition labels should include rule parameters"));
        defaultKeys.put(CONTROL_LABELS_KEY, new Property.True<String>(
            "A list of rare labels, used to optimise rule matching"));
        defaultKeys.put(COMMON_LABELS_KEY, new Property.True<String>(
            "A list of frequent labels, used to optimise rule matching"));
        DEFAULT_KEYS = Collections.unmodifiableMap(defaultKeys);
    }

    /**
     * One-line regular expression describing the system properties, with a
     * parameter position for the name of the rule system.
     */
    static public final String DESCRIPTION = "Rule system properties for %s";
    /** Map storing default property instances. */
    static private SystemProperties instance = new SystemProperties();
    //	
    // static {
    // // initialise the instance map
    // for (boolean attributed: new boolean[] { true, false } ) {
    // SystemProperties properties = new SystemProperties();
    // properties.setAttributed(attributed);
    // properties.setFixed();
    // instances.put(attributed, properties);
    // }
    // }

    /**
     * The default rule properties: not attributed and no control or common
     * labels.
     */
    static public final SystemProperties DEFAULT_PROPERTIES = getInstance();

    /**
     * Extends the {@link Property.IsBoolean} class by also allowing positive
     * numerical values to stand for <code>true</code>, and <code>0</code>
     * for <code>false</code>. Used for compatibility purposes (these
     * properties at some point only accepted numerical values).
     */
    static private class IsExtendedBoolean extends Property.IsBoolean {
        /**
         * Constructs an extended boolean property, with a comment used to
         * describe the expected value.
         */
        public IsExtendedBoolean(String comment) {
            super(comment, true);
        }

        @Override
        public boolean isSatisfied(String value) {
            try {
                return super.isSatisfied(value) || Integer.parseInt(value) > 0;
            } catch (NumberFormatException exc) {
                return false;
            }
        }
    }
}
