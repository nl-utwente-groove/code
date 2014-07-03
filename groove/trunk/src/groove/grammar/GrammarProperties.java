package groove.grammar;

import groove.algebra.AlgebraFamily;
import groove.explore.Exploration;
import groove.grammar.model.FormatErrorSet;
import groove.grammar.model.FormatException;
import groove.grammar.model.GrammarModel;
import groove.grammar.model.ResourceKind;
import groove.grammar.type.TypeLabel;
import groove.util.Groove;
import groove.util.Parser;
import groove.util.Properties;
import groove.util.PropertyKey;
import groove.util.ThreeValued;
import groove.util.Version;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Properties class for graph production systems.
 * @author Arend Rensink
 * @version $Revision $
 */
public class GrammarProperties extends Properties {
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
        super(Key.class);
        if (useCurrentGrooveVersion) {
            this.setCurrentVersionProperties();
            setShowLoopsAsLabels(false);
        } else {
            this.setGrooveVersion(Version.getInitialGrooveVersion());
            this.setGrammarVersion(Version.getInitialGrammarVersion());
        }
    }

    /** Constructs a non-fixed clone of a given properties object. */
    private GrammarProperties(GrammarProperties original) {
        super(Key.class);
        putAll(original);
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
            && this.getGrammarVersion().equals(Version.getCurrentGrammarVersion());
    }

    /**
     * Indicates if the LTS labels should be surrounded by angular brackets.
     * Default value: <code>true</code>.
     */
    public boolean isShowLoopsAsLabels() {
        return (Boolean) parseProperty(Key.LOOPS_AS_LABELS);
    }

    /**
     * Indicates if the LTS labels should be surrounded by angular brackets.
     * Default value: <code>true</code>.
     */
    public void setShowLoopsAsLabels(boolean show) {
        storeProperty(Key.LOOPS_AS_LABELS, show);
    }

    /**
     * Indicates if the LTS labels should contain transition parameters. Default
     * value: <code>false</code>.
     */
    public ThreeValued isUseParameters() {
        return (ThreeValued) parseProperty(Key.TRANSITION_PARAMETERS);
    }

    /** Sets the {@link Key#TRANSITION_PARAMETERS} property to the given value * */
    public void setUseParameters(ThreeValued useParameters) {
        storeProperty(Key.TRANSITION_PARAMETERS, useParameters);
    }

    /** Sets the {@link Key#GROOVE_VERSION} property to the given value */
    public void setGrooveVersion(String version) {
        storeProperty(Key.GROOVE_VERSION, version);
    }

    /**
     * @return the version of Groove that created the grammar.
     */
    public String getGrooveVersion() {
        return (String) parseProperty(Key.GROOVE_VERSION);
    }

    /** Sets the {@link Key#GRAMMAR_VERSION} property to the given value */
    public void setGrammarVersion(String version) {
        storeProperty(Key.GRAMMAR_VERSION, version);
    }

    /**
     * @return the version of the grammar.
     */
    public String getGrammarVersion() {
        return (String) parseProperty(Key.GRAMMAR_VERSION);
    }

    /**
     * Returns a list of control labels, according to the
     * {@link Key#CONTROL_LABELS} property of the rule system.
     * @see Key#CONTROL_LABELS
     */
    @SuppressWarnings("unchecked")
    public List<String> getControlLabels() {
        return (List<String>) parseProperty(Key.CONTROL_LABELS);
    }

    /**
     * Sets the control labels property.
     * @see Key#CONTROL_LABELS
     */
    public void setControlLabels(List<String> controlLabels) {
        storeProperty(Key.CONTROL_LABELS, controlLabels);
    }

    /**
     * Sets the deadlock check policy.
     * @param policy the policy to be used for deadlock checking.
     * @see Key#DEAD_POLICY
     */
    public void setDeadPolicy(CheckPolicy policy) {
        storeProperty(Key.DEAD_POLICY, policy);
    }

    /**
     * Returns the deadlock check policy of the rule system.
     * @see Key#DEAD_POLICY
     */
    public CheckPolicy getDeadPolicy() {
        return (CheckPolicy) parseProperty(Key.DEAD_POLICY);
    }

    /**
     * Sets the typecheck policy.
     * @param policy the policy to be used for type checking.
     * @see Key#TYPE_POLICY
     */
    public void setTypePolicy(CheckPolicy policy) {
        storeProperty(Key.TYPE_POLICY, policy);
    }

    /**
     * Returns the type check policy of the rule system.
     * @see Key#TYPE_POLICY
     */
    public CheckPolicy getTypePolicy() {
        return (CheckPolicy) parseProperty(Key.TYPE_POLICY);
    }

    /**
     * Returns a list of common labels, according to the
     * {@link Key#COMMON_LABELS} property of the rule system.
     * @see Key#COMMON_LABELS
     */
    @SuppressWarnings("unchecked")
    public List<String> getCommonLabels() {
        return (List<String>) parseProperty(Key.COMMON_LABELS);
    }

    /**
     * Sets the common labels property.
     * @see Key#COMMON_LABELS
     */
    public void setCommonLabels(List<String> commonLabels) {
        storeProperty(Key.COMMON_LABELS, Groove.toString(commonLabels.toArray(), "", "", " "));
    }

    /**
     * Sets the injectivity property to a certain value.
     * @param injective if <code>true</code>, non-injective matches are
     *        disallowed
     */
    public void setInjective(boolean injective) {
        storeProperty(Key.INJECTIVE, injective);
    }

    /**
     * Returns the value of the injectivity property.
     * @return if <code>true</code>, non-injective matches are disallowed
     */
    public boolean isInjective() {
        return (Boolean) parseProperty(Key.INJECTIVE);
    }

    /**
     * Sets the dangling edge check to a certain value.
     * @param dangling if <code>true</code>, matches with dangling edges are
     *        disallowed
     */
    public void setCheckDangling(boolean dangling) {
        storeProperty(Key.DANGLING, dangling);
    }

    /**
     * Returns the value of the dangling edge property.
     * @return if <code>true</code>, matches with dangling edges are disallowed.
     */
    public boolean isCheckDangling() {
        return (Boolean) parseProperty(Key.DANGLING);
    }

    /**
     * Sets the exploration strategy to a certain value.
     * @param strategy the new exploration strategy
     */
    public void setExploration(Exploration strategy) {
        storeProperty(Key.EXPLORATION, strategy);
    }

    /**
     * Returns the exploration strategy, or <code>null</code> if there
     * is no strategy set.
     */
    public Exploration getExploration() {
        return (Exploration) parseProperty(Key.EXPLORATION);
    }

    /**
     * Sets the active names property of a given resource kind.
     * @param kind the resource kind to set the names for
     * @param names the (non-{@code null}, but possible empty) list of names of the active resources
     */
    public void setActiveNames(ResourceKind kind, Collection<String> names) {
        assert kind != ResourceKind.RULE;
        storeProperty(resourceKeyMap.get(kind), names);
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
        @SuppressWarnings("unchecked")
        List<String> names = (List<String>) parseProperty(resourceKeyMap.get(kind));
        return new TreeSet<String>(names);
    }

    /**
     * Sets the algebra family to a given value.
     */
    public void setAlgebraFamily(AlgebraFamily family) {
        storeProperty(Key.ALGEBRA, family);
    }

    /**
     * Returns the selected algebra family.
     * @return the selected algebra family, or {@link AlgebraFamily#DEFAULT}
     * if none is selected.
     */
    public AlgebraFamily getAlgebraFamily() {
        return (AlgebraFamily) parseProperty(Key.ALGEBRA);
    }

    /**
     * Sets the creator edge check to a certain value.
     * @param check if <code>true</code>, creator edges are treated as negative
     *        application conditions
     */
    public void setCheckCreatorEdges(boolean check) {
        storeProperty(Key.CREATOR_EDGE, check);
    }

    /**
     * Returns the value of the creator edge check property.
     * @return if <code>true</code>, creator edges are treated as negative
     *         application conditions
     */
    public boolean isCheckCreatorEdges() {
        return (Boolean) parseProperty(Key.CREATOR_EDGE);
    }

    /**
     * Sets the graph isomorphism check to a certain value.
     * @param check if <code>true</code>, state graphs are compared up to
     *        isomorphism
     */
    public void setCheckIsomorphism(boolean check) {
        storeProperty(Key.ISOMORPHISM, check);
    }

    /**
     * Returns the value of the graph isomorphism check property.
     * @return if <code>true</code>, state graphs are compared up to isomorphism
     */
    public boolean isCheckIsomorphism() {
        return (Boolean) parseProperty(Key.ISOMORPHISM);
    }

    /**
     * Returns the value of the RHS-as-NAC property.
     * @return if <code>true</code>, the RHS is treated as a negative
     *         application condition, preventing the same rule instance from
     *         being applied twice in a row
     */
    public boolean isRhsAsNac() {
        return (Boolean) parseProperty(Key.RHS_AS_NAC);
    }

    /**
     * Sets the RHS-as-NAC property to a certain value.
     * @param value if <code>true</code>, the RHS is treated as a negative
     *        application condition, preventing the same rule instance from
     *        being applied twice in a row
     */
    public void setRhsAsNac(boolean value) {
        storeProperty(Key.RHS_AS_NAC, value);
    }

    /**
     * Returns a list of node labels that are to be used in the abstraction.
     * @see Key#ABSTRACTION_LABELS
     */
    @SuppressWarnings("unchecked")
    public List<String> getAbstractionLabels() {
        return (List<String>) parseProperty(Key.ABSTRACTION_LABELS);
    }

    /**
     * Sets the abstraction labels property.
     * @see Key#ABSTRACTION_LABELS
     */
    public void setAbstractionLabels(List<String> abstractionLabels) {
        storeProperty(Key.ABSTRACTION_LABELS, abstractionLabels);
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
     * Checks if the stored properties are valid in a given grammar.
     */
    public void check(GrammarModel grammar) throws FormatException {
        FormatErrorSet errors = new FormatErrorSet();
        for (ResourceKind kind : ResourceKind.values()) {
            switch (kind) {
            case CONTROL:
            case HOST:
            case PROLOG:
                for (String name : getActiveNames(kind)) {
                    if (!grammar.getNames(kind).contains(name)) {
                        errors.add("'%s' is not an existing %s", name, kind.getDescription());
                    }
                }
            }
        }
        errors.throwException();
    }

    /** Returns a non-fixed clone of the properties. */
    @Override
    public GrammarProperties clone() {
        return new GrammarProperties(this);
    }

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

    /** Grammar property keys. */
    public static enum Key implements PropertyKey {
        /** Property name for the GROOVE version. */
        GROOVE_VERSION("grooveVersion", true, "The Groove version that created this grammar"),
        /** Property name for the Grammar version. */
        GRAMMAR_VERSION("grammarVersion", true, "The version of this grammar"),
        /** One-line documentation comment on the graph production system. */
        REMARK("remark", "A one-line description of the graph production system"),

        /** Property name for the algebra to be used during simulation. */
        ALGEBRA(
            "algebraFamily",
            "<body>Algebra used for attributes"
                + "<li>- <i>default</i>: java-based values (<tt>int</tt>, <tt>boolean</tt>, <tt>String</tt>, <tt>double</tt>"
                + "<li>- <i>big</i>: arbitrary-precision values (<tt>BigInteger</tt>, <tt>boolean</tt>, <tt>String</tt>, <tt>BigDecimal</tt>)"
                + "<li>- <i>point</i>: a single value for every type (so all values are equal)"
                + "<li>- <i>term</i>: symbolic term representations",
            new Parser.EnumParser<AlgebraFamily>(AlgebraFamily.class, AlgebraFamily.DEFAULT)),

        /**
         * Flag determining the injectivity of the rule system. If <code>true</code>,
         * all rules should be matched injectively. Default is <code>false</code>.
         */
        INJECTIVE("matchInjective",
            "<body>Flag controlling if all rules should be matched injectively. "
                + "<p>If true, overrules the local rule injectivity property", Parser.boolFalse),

        /**
         * Dangling edge check. If <code>true</code>, all
         * matches that leave dangling edges are invalid. Default is
         * <code>false</code>.
         */
        DANGLING("checkDangling",
            "Flag controlling if dangling edges should be forbidden rather than deleted",
            Parser.boolFalse),

        /**
         * Creator edge check. If <code>true</code>, creator
         * edges are implicitly treated as (individual) NACs. Default is
         * <code>false</code>.
         */
        CREATOR_EDGE("checkCreatorEdges",
            "Flag controlling if creator edges should be treated as implicit NACs",
            Parser.boolFalse),

        /**
         * RHS-as-NAC property. If <code>true</code>, each RHS
         * is implicitly treated as a NAC. Default is <code>false</code>.
         */
        RHS_AS_NAC("rhsIsNAC", "Flag controlling if RHSs should be treated as implicit NACs",
            Parser.boolFalse),

        /**
         * Isomorphism check. If <code>true</code>, state
         * graphs are compared up to isomorphism; otherwise, they are compared up to
         * equality. Default is <code>true</code>.
         */
        ISOMORPHISM("checkIsomorphism",
            "Flag controlling whether states are checked up to isomorphism", Parser.boolTrue),

        /**
         * Space-separated list of active start graph names.
         */
        START_GRAPH_NAMES("startGraph", "List of active start graph names", Parser.splitter),

        /**
         * Name of the active control program.
         */
        CONTROL_NAMES("controlProgram", "List of enabled control programs", Parser.splitter),

        /**
         * Space-separated list of active type graph names.
         */
        TYPE_NAMES("typeGraph", "List of active type graph names", Parser.splitter),

        /**
         * Space-separated list of active prolog program names.
         */
        PROLOG_NAMES("prolog", "List of active prolog program names", Parser.splitter),

        /** Policy for dealing with type violations. */
        TYPE_POLICY(
            "typePolicy",
            "<body>Flag controlling how dynamic type violations (multiplicities, composites) are dealt with."
                + "<li>- <i>none</i>: dynamic type constraints are not checked"
                + "<li>- <i>error</i>: dynamic type violations are flagged as errors"
                + "<li>- <i>absence</i>: dynamic type violations cause the state to be removed from the 'real' state space",
            new Parser.EnumParser<CheckPolicy>(CheckPolicy.class, CheckPolicy.ERROR)),

        /** Policy for dealing with deadlocks. */
        DEAD_POLICY("deadlockPolicy", "Flag controlling how deadlocked states are dealt with."
            + "<br>(A state is considered deadlocked if no scheduled transformer is applicable.)"
            + "<li>- <i>none</i>: deadlocks are not checked"
            + "<li>- <i>error</i>: deadlocks are flagged as errors",
            new Parser.EnumParser<CheckPolicy>(CheckPolicy.class, CheckPolicy.NONE, "none",
                "error", null)),

        /**
         * Exploration strategy description.
         */
        EXPLORATION("explorationStrategy", "Default exploration strategy for this grammar",
            Exploration.parser()),

        /**
         * Space-separated list of control labels of a graph grammar. The
         * control labels are those labels which should be matched first for optimal
         * performance, presumably because they occur infrequently or indicate a
         * place where rules are likely to be applicable.
         */
        CONTROL_LABELS("controlLabels", "List of rare labels, used to optimise rule matching",
            Parser.splitter),

        /**
         * Space-separated list of common labels of a graph grammar. The
         * control labels are those labels which should be matched last for optimal
         * performance, presumably because they occur frequently.
         */
        COMMON_LABELS("commonLabels", "List of frequent labels, used to optimise rule matching",
            Parser.splitter),

        /**
         * Space-separated list of abstraction node labels of a graph grammar.
         * These labels are used to define the level zero neighbourhood relation
         * between nodes.
         */
        ABSTRACTION_LABELS("abstractionLabels",
            "List of node labels, used by neighbourhood abstraction", Parser.splitter),

        /**
         * Flag that determines if transition parameters are included in the LTS
         * transition labels
         */
        TRANSITION_PARAMETERS("transitionParameters", false, "Show parameters",
            "Flag controlling if transition labels should include rule parameters",
            new Parser.EnumParser<ThreeValued>(ThreeValued.class, ThreeValued.SOME)),

        /**
         * Flag that determines if (binary) loops can be shown as vertex labels.
         */
        LOOPS_AS_LABELS("loopsAsLabels",
            "Flag controlling if binary self-edges may be shown as vertex labels", Parser.boolTrue), ;

        /**
         * Constructor for a key with a plain string value
         * @param name name of the key; should be an identifier possibly prefixed by #SYSTEM_KEY_PREFIX
         * @param explanation short explanation of the meaning of the key
         */
        private Key(String name, String explanation) {
            this(name, false, null, explanation, null);
        }

        /**
         * Constructor for a key with a plain string value
         * @param name name of the key; should be an identifier possibly prefixed by #SYSTEM_KEY_PREFIX
         * @param explanation short explanation of the meaning of the key
         */
        private Key(String name, boolean system, String explanation) {
            this(name, system, null, explanation, null);
        }

        /**
         * Constructor for a key with values parsed by a given parser
         * @param name name of the key; should be an identifier possibly prefixed by #SYSTEM_KEY_PREFIX
         * @param explanation short explanation of the meaning of the key
         * @param parser parser for values for this key; if {@code null},
         * {@link Parser#identity} is used
         */
        private Key(String name, String explanation, Parser<?> parser) {
            this(name, false, null, explanation, parser);
        }

        /**
         * Constructor for a key with a plain string value
         * @param name name of the key; should be an identifier possibly prefixed by #SYSTEM_KEY_PREFIX
         * @param system flag indicating this is a system key
         * @param keyPhrase user-readable version of the name; if {@code null},
         * the key phrase is constructed from {@code name}
         * @param explanation short explanation of the meaning of the key
         */
        private Key(String name, boolean system, String keyPhrase, String explanation,
            Parser<?> parser) {
            this.name = name;
            this.system = system;
            this.keyPhrase = keyPhrase == null ? Groove.unCamel(name, false) : keyPhrase;
            this.explanation = explanation;
            this.parser = parser == null ? Parser.identity : parser;
        }

        @Override
        public String getName() {
            return this.name;
        }

        private final String name;

        @Override
        public String getExplanation() {
            return this.explanation;
        }

        private final String explanation;

        @Override
        public boolean isSystem() {
            return this.system;
        }

        private final boolean system;

        @Override
        public String getKeyPhrase() {
            return this.keyPhrase;
        }

        private final String keyPhrase;

        @Override
        public Parser<?> parser() {
            return this.parser;
        }

        private final Parser<?> parser;

        /** Returns the key with a given name, if any. */
        public static Key getKey(String name) {
            return keyMap.get(name);
        }

        /**
         * List of system-defined keys, in the order in which they are to appear in
         * a properties editor.
         */
        static private final Map<String,Key> keyMap;

        static {
            Map<String,Key> defaultKeys = new LinkedHashMap<String,Key>();
            for (Key key : Key.values()) {
                defaultKeys.put(key.getName(), key);
            }
            keyMap = Collections.unmodifiableMap(defaultKeys);
        }
    }
}
