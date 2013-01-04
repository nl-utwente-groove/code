package groove.trans;

import groove.algebra.AlgebraFamily;
import groove.explore.Exploration;
import groove.graph.TypeLabel;
import groove.gui.dialog.PropertyKey;
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
import java.util.EnumMap;
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
public class GrammarProperties extends java.util.Properties implements Fixable {

    /**
     * Default constructor.
     */
    public GrammarProperties() {
        this(false);
    }

    /**
     * Constructor that sets the grammar properties.  
     */
    public GrammarProperties(boolean useCurrentGrooveVersion) {
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
        return property == null || property.isEmpty()
            || Boolean.valueOf(property);
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
     * Sets the active names property of a given resource kind.
     * @param kind the resource kind to set the names for
     * @param names the (non-{@code null}, but possible empty) list of names of the active resources
     */
    public void setActiveNames(ResourceKind kind, Collection<String> names) {
        assert kind != ResourceKind.RULE;
        setProperty(resourceKeyMap.get(kind),
            Groove.toString(names.toArray(), "", "", " "));
    }

    /**
     * Returns a list of active resource names of a given kind.
     * @param kind the queried resource kind
     * @return a (non-{@code null}, but possibly empty) set of active names
     */
    public Set<String> getActiveNames(ResourceKind kind) {
        assert kind != ResourceKind.RULE;
        if (kind == ResourceKind.CONFIG || kind == ResourceKind.GROOVY) {
            return Collections.emptySet();
        }
        String names = getProperty(resourceKeyMap.get(kind));
        if (names == null || "".equals(names)) {
            return Collections.emptySet();
        } else {
            return new TreeSet<String>(Arrays.asList(names.split("\\s")));
        }
    }

    /**
     * Sets the algebra family to a given value.
     */
    public void setAlgebraFamily(AlgebraFamily family) {
        setProperty(Key.ALGEBRA, family.getName());
    }

    /** 
     * Returns the selected algebra family.
     * @return the selected algebra family, or {@link AlgebraFamily#DEFAULT}
     * if none is selected. 
     */
    public AlgebraFamily getAlgebraFamily() {
        String property = getProperty(Key.ALGEBRA);
        AlgebraFamily result =
            property == null || property.isEmpty() ? AlgebraFamily.DEFAULT
                    : AlgebraFamily.getInstance(property);
        assert result != null;
        return result;
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
        return result == null || result.isEmpty() || Boolean.valueOf(result);
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
    public GrammarProperties clone() {
        GrammarProperties result = (GrammarProperties) super.clone();
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
    public GrammarProperties relabel(TypeLabel oldLabel, TypeLabel newLabel) {
        GrammarProperties result = clone();
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
    public boolean setFixed() {
        boolean result = !isFixed();
        this.fixed = true;
        return result;
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
        return getProperty(key.getName());
    }

    /** Changes a property by key. */
    private void setProperty(Key key, String value) {
        setProperty(key.getName(), value);
    }

    /**
     * Flag to indicate that the properties have been frozen.
     */
    private boolean fixed;

    /**
     * Returns a default, fixed properties object, with a given value for
     * attribute support.
     */
    static public GrammarProperties getInstance() {
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
    static public boolean isCheckDangling(GrammarProperties properties) {
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
    static public final Map<String,Key> KEYS;

    static {
        Map<String,Key> defaultKeys = new LinkedHashMap<String,Key>();
        for (Key key : Key.values()) {
            defaultKeys.put(key.getName(), key);
        }
        KEYS = Collections.unmodifiableMap(defaultKeys);
    }

    /** Map storing default property instances. */
    static private GrammarProperties instance = new GrammarProperties();

    /**
     * The default rule properties: not attributed and no control or common
     * labels.
     */
    static public final GrammarProperties DEFAULT_PROPERTIES = getInstance();

    /** Mapping from resource kinds to corresponding property keys. */
    static private final Map<ResourceKind,Key> resourceKeyMap =
        new EnumMap<ResourceKind,GrammarProperties.Key>(ResourceKind.class);
    static {
        resourceKeyMap.put(ResourceKind.TYPE, Key.TYPE_NAMES);
        resourceKeyMap.put(ResourceKind.CONTROL, Key.CONTROL_NAMES);
        resourceKeyMap.put(ResourceKind.PROLOG, Key.PROLOG_NAMES);
        resourceKeyMap.put(ResourceKind.HOST, Key.START_GRAPH_NAMES);

    }

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
    public static enum Key implements PropertyKey {
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
         * Space-separated list of active start graph names.
         */
        START_GRAPH_NAMES("startGraph",
                "Space-separated list of active start graph names"),
        /**
         * Name of the active control program.
         */
        CONTROL_NAMES("controlProgram",
                "Space-separated list of enabled control programs"),
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
            this(text, null, Groove.unCamel(text, false), "", property, comment);
        }

        private Key(String text, String category, String description,
                String defaultValue, PropertyKind property, String comment) {
            this.name = text;
            this.category = category;
            this.description = description;
            this.defaultValue = defaultValue;
            this.format = property.newInstance(comment);
            this.system = property == PropertyKind.UNMODIFIABLE;
        }

        /** Returns the text of this key. */
        public String getName() {
            return this.name;
        }

        /** Returns the syntax check property. */
        public Property<String> getFormat() {
            return this.format;
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
        public String getDescription() {
            return this.description;
        }

        @Override
        public String getCategory() {
            return this.category;
        }

        private final String name;
        private final String description;
        private final String category;
        private final String defaultValue;
        private final Property<String> format;
        private final boolean system;

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
                        AlgebraFamily.DEFAULT.getName(),
                        AlgebraFamily.POINT.getName(),
                        AlgebraFamily.BIG.getName());
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
