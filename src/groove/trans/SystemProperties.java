package groove.trans;

import groove.algebra.AlgebraFamily;
import groove.explore.Exploration;
import groove.graph.LabelStore;
import groove.graph.TypeLabel;
import groove.io.FileType;
import groove.util.Fixable;
import groove.util.Groove;
import groove.util.Property;
import groove.util.Version;
import groove.view.FormatException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.InvalidPropertiesFormatException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

/**
 * Properties class for graph production systems.
 * @author Arend Rensink
 * @version $Revision $
 */
public class SystemProperties extends java.util.Properties implements Fixable {

    /**
     * Default constructor.
     */
    public SystemProperties() {
        this(false);
    }

    /**
     * Constructor that sets the grammar properties.  
     */
    public SystemProperties(boolean useCurrentGrooveVersion) {
        super();
        if (useCurrentGrooveVersion) {
            this.setCurrentVersionProperties();
            setShowLoopsAsLabels(false);
        } else {
            this.setGrooveVersion(Version.getInitialGrooveVersion());
            this.setGrammarVersion(Version.getInitialGrammarVersion());
        }
    }

    /**
     * Set version properties to the latest version.
     */
    public void setCurrentVersionProperties() {
        this.setGrooveVersion(Version.getCurrentGrooveVersion());
        this.setGrammarVersion(Version.getCurrentGrammarVersion());
    }

    /**
     * @return <code>true</code> if the version numbers are set to the current
     * version of the tool. <code>false</code> otherwise.
     */
    public boolean isCurrentVersionProperties() {
        return this.getGrooveVersion().equals(Version.getCurrentGrooveVersion())
            && this.getGrammarVersion().equals(
                Version.getCurrentGrammarVersion());
    }

    /**
     * Indicates if the LTS labels should be surrounded by angular brackets.
     * Default value: <code>true</code>.
     */
    public boolean isShowLoopsAsLabels() {
        String property = getProperty(Key.LOOPS_AS_LABELS);
        return property == null || Boolean.valueOf(property);
    }

    /**
     * Indicates if the LTS labels should be surrounded by angular brackets.
     * Default value: <code>true</code>.
     */
    public void setShowLoopsAsLabels(boolean show) {
        setProperty(Key.LOOPS_AS_LABELS, "" + show);
    }

    /**
     * Indicates if the LTS labels should be surrounded by angular brackets.
     * Default value: <code>false</code>.
     */
    public boolean isShowTransitionBrackets() {
        String property = getProperty(Key.TRANSITION_BRACKETS);
        return property != null
            && (Boolean.valueOf(property) || property.equals(NUMERIC_YES));
    }

    /**
     * Indicates if the LTS labels should contain transition parameters. Default
     * value: <code>false</code>.
     */
    public boolean isUseParameters() {
        String params = getProperty(Key.TRANSITION_PARAMETERS);
        return params != null
            && (Boolean.valueOf(params) || params.equals(NUMERIC_YES));
    }

    /** Sets the {@link Key#TRANSITION_PARAMETERS} property to the given value * */
    public void setUseParameters(boolean useParameters) {
        setProperty(Key.TRANSITION_PARAMETERS, "" + useParameters);
    }

    /** Sets the {@link Key#GROOVE_VERSION} property to the given value */
    public void setGrooveVersion(String version) {
        setProperty(Key.GROOVE_VERSION, version);
    }

    /**
     * @return the version of Groove that created the grammar.
     */
    public String getGrooveVersion() {
        return getProperty(Key.GROOVE_VERSION);
    }

    /** Sets the {@link Key#GRAMMAR_VERSION} property to the given value */
    public void setGrammarVersion(String version) {
        setProperty(Key.GRAMMAR_VERSION, version);
    }

    /**
     * @return the version of the grammar.
     */
    public String getGrammarVersion() {
        return getProperty(Key.GRAMMAR_VERSION);
    }

    /**
     * Returns a list of control labels, according to the
     * {@link Key#CONTROL_LABELS} property of the rule system.
     * @see Key#CONTROL_LABELS
     */
    public List<String> getControlLabels() {
        String controlLabels = getProperty(Key.CONTROL_LABELS);
        if (controlLabels == null) {
            return Collections.emptyList();
        } else {
            return Arrays.asList(controlLabels.split("\\s"));
        }
    }

    /**
     * Sets the control labels property.
     * @see Key#CONTROL_LABELS
     */
    public void setControlLabels(List<String> controlLabels) {
        setProperty(Key.CONTROL_LABELS,
            Groove.toString(controlLabels.toArray(), "", "", " "));
    }

    /**
     * Returns a list of common labels, according to the
     * {@link Key#COMMON_LABELS} property of the rule system.
     * @see Key#COMMON_LABELS
     */
    public List<String> getCommonLabels() {
        String commonLabels = getProperty(Key.COMMON_LABELS);
        if (commonLabels == null) {
            return Collections.emptyList();
        } else {
            return Arrays.asList(commonLabels.split("\\s"));
        }
    }

    /**
     * Sets the common labels property.
     * @see Key#COMMON_LABELS
     */
    public void setCommonLabels(List<String> commonLabels) {
        setProperty(Key.COMMON_LABELS,
            Groove.toString(commonLabels.toArray(), "", "", " "));
    }

    /**
     * Returns the string description of the subtype relation, or the empty
     * string if the property is not set.
     * @see Key#SUBTYPE
     */
    public String getSubtypes() {
        String result = getProperty(Key.SUBTYPE);
        return result == null ? "" : result;
    }

    /**
     * Sets the subtype property.
     * @see Key#SUBTYPE
     */
    public void setSubtypes(String subtypes) {
        setProperty(Key.SUBTYPE, subtypes);
    }

    /**
     * Sets the injectivity property to a certain value.
     * @param injective if <code>true</code>, non-injective matches are
     *        disallowed
     */
    public void setInjective(boolean injective) {
        setProperty(Key.INJECTIVE, "" + injective);
    }

    /**
     * Returns the value of the injectivity property.
     * @return if <code>true</code>, non-injective matches are disallowed
     */
    public boolean isInjective() {
        String result = getProperty(Key.INJECTIVE);
        return result != null && Boolean.valueOf(result);
    }

    /**
     * Sets the dangling edge check to a certain value.
     * @param dangling if <code>true</code>, matches with dangling edges are
     *        disallowed
     */
    public void setCheckDangling(boolean dangling) {
        setProperty(Key.DANGLING, "" + dangling);
    }

    /**
     * Returns the value of the dangling edge property.
     * @return if <code>true</code>, matches with dangling edges are disallowed.
     */
    public boolean isCheckDangling() {
        String result = getProperty(Key.DANGLING);
        return result != null && Boolean.valueOf(result);
    }

    /**
     * Sets the control program name to a certain value.
     * @param program the new control program name
     */
    public void setControlName(String program) {
        setProperty(Key.CONTROL_NAMES, program);
    }

    /**
     * Returns the control program name, or <code>null</code> if there
     * is no name set.
     */
    public String getControlName() {
        // for compatibility, strip the extension from the stored control name
        String result = getProperty(Key.CONTROL_NAMES);
        if (result != null) {
            result = FileType.CONTROL_FILTER.stripExtension(result);
        }
        return stringOrNull(result);
    }

    /**
     * Sets the exploration strategy to a certain value.
     * @param strategy the new exploration strategy
     */
    public void setExploration(String strategy) {
        setProperty(Key.EXPLORATION, strategy);
    }

    /**
     * Returns the exploration strategy, or <code>null</code> if there
     * is no strategy set.
     */
    public String getExploration() {
        String result = getProperty(Key.EXPLORATION);
        return stringOrNull(result);
    }

    /**
     * Sets the type graph names property.
     * @param types the list of type graphs that are in use.
     */
    public void setPrologNames(Collection<String> types) {
        setProperty(Key.PROLOG_NAMES,
            Groove.toString(types.toArray(), "", "", " "));
    }

    /**
     * Returns a list of type graph names that are in use.
     */
    public Set<String> getPrologNames() {
        String programs = getProperty(Key.PROLOG_NAMES);
        if (programs == null || "".equals(programs)) {
            return Collections.emptySet();
        } else {
            return new TreeSet<String>(Arrays.asList(programs.split("\\s")));
        }
    }

    /**
     * Sets the type graph names property.
     * @param types the list of type graphs that are in use.
     */
    public void setTypeNames(Collection<String> types) {
        setProperty(Key.TYPE_NAMES,
            Groove.toString(types.toArray(), "", "", " "));
    }

    /**
     * Returns a list of type graph names that are in use.
     */
    public Set<String> getTypeNames() {
        String types = getProperty(Key.TYPE_NAMES);
        if (types == null || "".equals(types)) {
            return Collections.emptySet();
        } else {
            return new TreeSet<String>(Arrays.asList(types.split("\\s")));
        }
    }

    /**
     * Sets the algebra family to a given value.
     */
    public void setAlgebra(String family) {
        setProperty(Key.ALGEBRA, family);
    }

    /** 
     * Returns the selected algebra family.
     * @return the selected algebra family, or {@link AlgebraFamily#DEFAULT_ALGEBRAS}
     * if none is selected. 
     */
    public String getAlgebraFamily() {
        String result = getProperty(Key.ALGEBRA);
        return result == null ? AlgebraFamily.DEFAULT_ALGEBRAS : result;
    }

    /**
     * Sets the creator edge check to a certain value.
     * @param check if <code>true</code>, creator edges are treated as negative
     *        application conditions
     */
    public void setCheckCreatorEdges(boolean check) {
        setProperty(Key.CREATOR_EDGE, "" + check);
    }

    /**
     * Returns the value of the creator edge check property.
     * @return if <code>true</code>, creator edges are treated as negative
     *         application conditions
     */
    public boolean isCheckCreatorEdges() {
        String result = getProperty(Key.CREATOR_EDGE);
        return result != null && Boolean.valueOf(result);
    }

    /**
     * Sets the graph isomorphism check to a certain value.
     * @param check if <code>true</code>, state graphs are compared up to
     *        isomorphism
     */
    public void setCheckIsomorphism(boolean check) {
        setProperty(Key.ISOMORPHISM, "" + check);
    }

    /**
     * Returns the value of the graph isomorphism check property.
     * @return if <code>true</code>, state graphs are compared up to isomorphism
     */
    public boolean isCheckIsomorphism() {
        String result = getProperty(Key.ISOMORPHISM);
        return result == null || Boolean.valueOf(result);
    }

    /**
     * Returns the value of the RHS-as-NAC property.
     * @return if <code>true</code>, the RHS is treated as a negative
     *         application condition, preventing the same rule instance from
     *         being applied twice in a row
     */
    public boolean isRhsAsNac() {
        String result = getProperty(Key.RHS_AS_NAC);
        return result != null && Boolean.valueOf(result);
    }

    /**
     * Sets the RHS-as-NAC property to a certain value.
     * @param value if <code>true</code>, the RHS is treated as a negative
     *        application condition, preventing the same rule instance from
     *        being applied twice in a row
     */
    public void setRhsAsNac(boolean value) {
        setProperty(Key.RHS_AS_NAC, "" + value);
    }

    /**
     * Returns a list of node labels that are to be used in the abstraction.
     * @see Key#ABSTRACTION_LABELS
     */
    public List<String> getAbstractionLabels() {
        String abstractionLabels = getProperty(Key.ABSTRACTION_LABELS);
        if (abstractionLabels == null) {
            return Collections.emptyList();
        } else {
            return Arrays.asList(abstractionLabels.split("\\s"));
        }
    }

    /**
     * Sets the abstraction labels property.
     * @see Key#ABSTRACTION_LABELS
     */
    public void setAbstractionLabels(List<String> abstractionLabels) {
        setProperty(Key.ABSTRACTION_LABELS,
            Groove.toString(abstractionLabels.toArray(), "", "", " "));
    }

    /**
     * Before calling the super method, tests if the properties are fixed and
     * throws an {@link IllegalStateException} if this is the case.
     * @throws IllegalStateException if the graph has been fixed.
     * @see #setFixed()
     */
    @Override
    public synchronized Object setProperty(String key, String value) {
        testFixed(false);
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
        testFixed(false);
        clear();
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
        testFixed(false);
        clear();
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
        testFixed(false);
        super.clear();
    }

    /** Returns a non-fixed clone of the properties. */
    @Override
    public SystemProperties clone() {
        SystemProperties result = (SystemProperties) super.clone();
        result.fixed = false;
        return result;
    }

    /**
     * Returns a clone of this properties object where all occurrences of a
     * given label are replaced by a new label.
     * @param oldLabel the label to be replaced
     * @param newLabel the new value for {@code oldLabel}
     * @return a clone of these properties, or the properties themselves if
     *         {@code oldLabel} did not occur
     */
    public SystemProperties relabel(TypeLabel oldLabel, TypeLabel newLabel) {
        SystemProperties result = clone();
        boolean hasChanged = false;
        String oldText = oldLabel.text();
        // change the control labels
        List<String> controlLabels = getControlLabels();
        List<String> newControlLabels = new ArrayList<String>(controlLabels);
        if (controlLabels != null && controlLabels.contains(oldText)) {
            int index = controlLabels.indexOf(oldText);
            newControlLabels.set(index, newLabel.text());
            result.setControlLabels(newControlLabels);
            hasChanged = true;
        }
        // change the common labels
        List<String> commonLabels = getControlLabels();
        List<String> newCommonLabels = new ArrayList<String>(commonLabels);
        if (commonLabels != null && commonLabels.contains(oldText)) {
            int index = commonLabels.indexOf(oldText);
            newCommonLabels.set(index, newLabel.text());
            result.setCommonLabels(newCommonLabels);
            hasChanged = true;
        }
        // change the subtype relation
        if (getTypeNames().isEmpty()) {
            try {
                LabelStore subtypeStore =
                    LabelStore.createLabelStore(getSubtypes());
                LabelStore newSubtypeStore =
                    subtypeStore.relabel(oldLabel, newLabel);
                if (subtypeStore != newSubtypeStore) {
                    result.setSubtypes(newSubtypeStore.toDirectSubtypeString());
                    hasChanged = true;
                }
            } catch (FormatException exc) {
                assert false : String.format(
                    "Subtype string '%s' gives rise to format error: %s",
                    getSubtypes(), exc.getMessage());
            }
        }
        return hasChanged ? result : this;
    }

    /**
     * Before calling the super method, tests if the properties are fixed and
     * throws an {@link IllegalStateException} if this is the case.
     * @throws IllegalStateException if the graph has been fixed.
     * @see #setFixed()
     */
    @Override
    public synchronized Object put(Object key, Object value) {
        testFixed(false);
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
        testFixed(false);
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
     * Freezes the properties object, after which changing any properties
     * becomes illegal.
     */
    public void setFixed() {
        this.fixed = true;
    }

    /**
     * Indicates if the properties are fixed. If so, any attempt to modify any
     * of the properties will result in an {@link IllegalStateException}.
     * @return <code>true</code> if the properties are fixed.
     */
    public boolean isFixed() {
        return this.fixed;
    }

    public void testFixed(boolean fixed) throws IllegalStateException {
        if (this.fixed != fixed) {
            throw new IllegalStateException(String.format(
                "Expected fixed = %b", fixed));
        }
    }

    /** Retrieves a property by key. */
    private String getProperty(Key key) {
        return getProperty(key.getText());
    }

    /** Changes a property by key. */
    private void setProperty(Key key, String value) {
        setProperty(key.getText(), value);
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
     * object. If the properties object is <code>null</code>, the method returns
     * <code>false</code>.
     * @param properties the properties to be tested; may be <code>null</code>
     * @return <true> if <code>properties</code> is not <code>null</code> and
     *         satisfies {@link #isCheckDangling()}
     */
    static public boolean isCheckDangling(SystemProperties properties) {
        return properties != null && properties.isCheckDangling();
    }

    static String stringOrNull(String input) {
        return input == null || input.length() == 0 ? null : input;
    }

    /** Alternate key value for true. */
    static public final String NUMERIC_YES = "1";

    /** Alternate key value for false. */
    static public final String NUMERIC_NO = "0";

    /**
     * List of system-defined keys, in the order in which they are to appear in
     * a properties editor.
     */
    static public final Map<String,Property<String>> DEFAULT_KEYS;

    static {
        Map<String,Property<String>> defaultKeys =
            new LinkedHashMap<String,Property<String>>();
        for (Key key : EnumSet.allOf(Key.class)) {
            defaultKeys.put(key.getText(), key.getProperty());
        }
        DEFAULT_KEYS = Collections.unmodifiableMap(defaultKeys);
    }

    /** Map storing default property instances. */
    static private SystemProperties instance = new SystemProperties();

    /**
     * The default rule properties: not attributed and no control or common
     * labels.
     */
    static public final SystemProperties DEFAULT_PROPERTIES = getInstance();

    /**
     * Extends the {@link groove.util.Property.IsBoolean} class by also
     * allowing positive numerical values to stand for <code>true</code>, and
     * <code>0</code> for <code>false</code>. Used for compatibility purposes
     * (these properties at some point only accepted numerical values).
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
                return super.isSatisfied(value) || Integer.parseInt(value) >= 0;
            } catch (NumberFormatException exc) {
                return false;
            }
        }
    }

    /**
     * Property testing if the value of {@link Key#SUBTYPE} is correctly
     * formatted.
     */
    static private class IsSubtypeString extends Property<String> {
        /**
         * Returns an instance of this property, with appropriate description
         * and comment.
         */
        public IsSubtypeString() {
            super(
                "string of the form 'type > sub [, sub]* [; type > sub [, sub]*]*",
                "Specifies the subtype relation");
        }

        @Override
        public boolean isSatisfied(String value) {
            boolean result = true;
            try {
                LabelStore.parseDirectSubtypeString(value);
            } catch (FormatException exc) {
                result = false;
            }
            return result;
        }
    }

    /**
     * Property testing if the value of {@link Key#EXPLORATION} is correctly
     * formatted.
     */
    static private class IsExplorationString extends Property<String> {
        /**
         * Returns an instance of this property, with appropriate description
         * and comment.
         */
        public IsExplorationString() {
            super(Exploration.SYNTAX_MESSAGE);
        }

        @Override
        public boolean isSatisfied(String value) {
            boolean result = true;
            try {
                Exploration.parse(value);
            } catch (FormatException exc) {
                result = false;
            }
            return result;
        }
    }

    /** Type of property keys. */
    public static enum Key {
        /**
         * Property name for the GROOVE version.
         */
        GROOVE_VERSION("grooveVersion", PropertyKind.UNMODIFIABLE,
                "The Groove version that created this grammar"),
        /**
         * Property name for the Grammar version.
         */
        GRAMMAR_VERSION("grammarVersion", PropertyKind.UNMODIFIABLE,
                "The version of this grammar"),
        /**
         * One-line documentation comment on the graph production system.
         */
        REMARK("remark",
                "A one-line description of the graph production system"),
        /**
         * List of subtypes of a graph grammar. The property must
         * be formatted according to {@link LabelStore#addDirectSubtypes(String)}.
         */
        SUBTYPE("subtypes", PropertyKind.BOOLEAN,
                "Algebra family that should be used in simulation (empty for default)"),
        /**
         * Property name for the algebra to be used during simulation.
         */
        ALGEBRA("algebraFamily", PropertyKind.ALGEBRA,
                "Flag controlling if matches should be injective"),
        /**
         * Flag determining the injectivity of the rule system. If <code>true</code>
         * , all rules should be matched injectively. Default is <code>false</code>.
         */
        INJECTIVE("matchInjective", PropertyKind.BOOLEAN,
                "Flag controlling if matches should be injective"),
        /**
         * Dangling edge check. If <code>true</code>, all
         * matches that leave dangling edges are invalid. Default is
         * <code>false</code>.
         */
        DANGLING("checkDangling", PropertyKind.BOOLEAN,
                "Flag controlling if dangling edges should be forbidden rather than deleted"),
        /**
         * Creator edge check. If <code>true</code>, creator
         * edges are implicitly treated as (individual) NACs. Default is
         * <code>false</code>.
         */
        CREATOR_EDGE("checkCreatorEdges", PropertyKind.BOOLEAN,
                "Flag controlling if creator edges should be treated as implicit NACs"),
        /**
         * HS-as-NAC property. If <code>true</code>, each RHS
         * is implicitly treated as a NAC. Default is <code>false</code>.
         */
        RHS_AS_NAC("rhsIsNAC", PropertyKind.BOOLEAN,
                "Flag controlling if RHSs should be treated as implicit NACs"),
        /**
         * Isomorphism check. If <code>true</code>, state
         * graphs are compared up to isomorphism; otherwise, they are compared up to
         * equality. Default is <code>true</code>.
         */
        ISOMORPHISM("checkIsomorphism", PropertyKind.BOOLEAN,
                "Flag controlling state graphs are checked up to isomorphism"),
        /**
         * Name of the active control program.
         */
        CONTROL_NAMES("controlProgram",
                "Name of the control program (default: '%s')"),
        /**
         * Space-separated list of active type graph names.
         */
        TYPE_NAMES("typeGraph",
                "Space-separated list of active type graph names"),
        /**
         * Space-separated list of active prolog program names.
         */
        PROLOG_NAMES("prolog",
                "Space-separated list of active prolog program names"),
        /**
         * Space-separated list of control labels of a graph grammar. The
         * control labels are those labels which should be matched first for optimal
         * performance, presumably because they occur infrequently or indicate a
         * place where rules are likely to be applicable.
         */
        CONTROL_LABELS("controlLabels",
                "A list of rare labels, used to optimise rule matching"),
        /**
         * Space-separated list of common labels of a graph grammar. The
         * control labels are those labels which should be matched last for optimal
         * performance, presumably because they occur frequently.
         */
        COMMON_LABELS("commonLabels",
                "A list of frequent labels, used to optimise rule matching"),
        /**
         * Space-separated list of abstraction node labels of a graph grammar.
         * These labels are used to define the level zero neighbourhood relation
         * between nodes.
         */
        ABSTRACTION_LABELS("abstractionLabels",
                "A list of node labels, used by neighbourhood abstraction"),
        /**
         * Exploration strategy description.
         */
        EXPLORATION("explorationStrategy", PropertyKind.EXPLORATION,
                "Default exploration strtategy for this grammar"),
        /**
         * Flag that determines if transition parameters are included in the LTS
         * transition labels
         */
        TRANSITION_BRACKETS("transitionBrackets",
                PropertyKind.EXTENDED_BOOLEAN,
                "Flag controlling if transition labels should be bracketed"),
        /**
         * Flag that determines if transition parameters are included in the LTS
         * transition labels
         */
        TRANSITION_PARAMETERS("transitionParameters",
                PropertyKind.EXTENDED_BOOLEAN,
                "Flag controlling if transition labels should include rule parameters"),
        /**
         * Flag that determines if (binary) loops can be shown as vertex labels.
         */
        LOOPS_AS_LABELS("loopsAsLabels", PropertyKind.BOOLEAN,
                "Flag controlling if binary self-edges may be shown as vertex labels");

        private Key(String text, String comment) {
            this(text, PropertyKind.TRUE, comment);
        }

        private Key(String text, PropertyKind property, String comment) {
            this.text = text;
            this.property = property.newInstance(comment);
        }

        /** Returns the text of this key. */
        public String getText() {
            return this.text;
        }

        /** Returns the syntax check property. */
        public Property<String> getProperty() {
            return this.property;
        }

        private final String text;
        private final Property<String> property;

        /** Syntactic property checked on the values entered for a property key. */
        private enum PropertyKind {
            TRUE {
                @Override
                public Property<String> newInstance(String comment) {
                    return new Property.True<String>(comment);
                }
            },
            BOOLEAN {
                @Override
                public Property<String> newInstance(String comment) {
                    return new Property.IsBoolean(comment, true);
                }
            },
            EXTENDED_BOOLEAN {
                @Override
                public Property<String> newInstance(String comment) {
                    return new IsExtendedBoolean(comment);
                }
            },
            ALGEBRA {
                @Override
                public Property<String> newInstance(String comment) {
                    return new Property.Choice<String>(comment,
                        AlgebraFamily.DEFAULT_ALGEBRAS,
                        AlgebraFamily.POINT_ALGEBRAS,
                        AlgebraFamily.BIG_ALGEBRAS);
                }
            },
            SUBTYPE {
                @Override
                public Property<String> newInstance(String comment) {
                    return new IsSubtypeString();
                }
            },
            EXPLORATION {
                @Override
                public Property<String> newInstance(String comment) {
                    return new IsExplorationString();
                }
            },
            UNMODIFIABLE {
                @Override
                public Property<String> newInstance(String comment) {
                    return new Property.Unmodifiable<String>(comment);
                }
            };

            abstract public Property<String> newInstance(String comment);
        }
    }
}
