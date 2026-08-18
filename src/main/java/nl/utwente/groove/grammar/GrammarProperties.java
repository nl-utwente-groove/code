package nl.utwente.groove.grammar;

import static nl.utwente.groove.grammar.GrammarKey.RULE_ENABLING;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.algebra.AlgebraFamily;
import nl.utwente.groove.grammar.CheckPolicy.PolicyMap;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.grammar.type.TypeLabel;
import nl.utwente.groove.transform.oracle.DefaultOracle;
import nl.utwente.groove.transform.oracle.ValueOracle;
import nl.utwente.groove.transform.oracle.ValueOracleFactory;
import nl.utwente.groove.transform.oracle.ValueOracleKind;
import nl.utwente.groove.util.Properties;
import nl.utwente.groove.util.QualName;
import nl.utwente.groove.util.ThreeValued;
import nl.utwente.groove.util.Version;
import nl.utwente.groove.util.collect.DeltaMap;
import nl.utwente.groove.util.parse.FormatChecker;
import nl.utwente.groove.util.parse.FormatError;
import nl.utwente.groove.util.parse.FormatErrorSet;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Properties class for graph production systems.
 * @author Arend Rensink
 * @version $Revision$
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
        super(GrammarKey.class);
        if (useCurrentGrooveVersion) {
            this.setCurrentVersionProperties();
            setShowLoopsAsLabels(false);
        } else {
            this.setGrooveVersion(Version.getInitialGrooveVersion());
            this.setGrammarVersion(Version.getInitialGrammarVersion());
        }
    }

    /** Constructs a non-fixed clone of a given properties object. */
    public GrammarProperties(GrammarProperties original) {
        super(GrammarKey.class);
        putAll(original);
    }

    /** Returns a map from property keys to checkers driven by a given grammar model. */
    public CheckerMap getCheckers(final GrammarModel grammar) {
        var result = new CheckerMap();
        for (final var key : GrammarKey.values()) {
            FormatChecker<String> checker = v -> {
                try {
                    return key.check(grammar, key.parse(v));
                } catch (FormatException exc) {
                    return exc.getErrors();
                }
            };
            result.put(key, checker);
        }
        return result;
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
        return parsePropertyOrDefault(GrammarKey.LOOPS_AS_LABELS).value(ValueType.BOOLEAN);
    }

    /**
     * Indicates if the LTS labels should be surrounded by angular brackets.
     * Default value: <code>true</code>.
     */
    public void setShowLoopsAsLabels(boolean show) {
        storeValue(GrammarKey.LOOPS_AS_LABELS, show);
    }

    /**
     * Indicates if states should store output parameters for group calls. Default
     * value: {@code false}
     */
    public boolean isStoreOutPars() {
        return parsePropertyOrDefault(GrammarKey.STORE_OUT_PARS).value(ValueType.BOOLEAN);
    }

    /** Sets the {@link GrammarKey#STORE_OUT_PARS} property to the given value * */
    public void setStoreOutPars(boolean store) {
        storeValue(GrammarKey.STORE_OUT_PARS, store);
    }

    /**
     * Indicates if the LTS labels should contain transition parameters. Default
     * value: {@link ThreeValued#FALSE}.
     */
    public ThreeValued isUseParameters() {
        return parsePropertyOrDefault(GrammarKey.TRANSITION_PARAMETERS)
            .value(ValueType.THREE_VALUED);
    }

    /** Sets the {@link GrammarKey#TRANSITION_PARAMETERS} property to the given value * */
    public void setUseParameters(ThreeValued useParameters) {
        storeValue(GrammarKey.TRANSITION_PARAMETERS, useParameters);
    }

    /** Sets the {@link GrammarKey#GROOVE_VERSION} property to the given value */
    public void setGrooveVersion(String version) {
        storeValue(GrammarKey.GROOVE_VERSION, version);
    }

    /**
     * Returns the version of Groove that created the grammar.
     */
    public String getGrooveVersion() {
        return parsePropertyOrDefault(GrammarKey.GROOVE_VERSION).value(ValueType.STRING);
    }

    /** Sets the {@link GrammarKey#GRAMMAR_VERSION} property to the given value */
    public void setGrammarVersion(String version) {
        storeValue(GrammarKey.GRAMMAR_VERSION, version);
    }

    /**
     * Returns the version of the grammar.
     */
    public String getGrammarVersion() {
        return parsePropertyOrDefault(GrammarKey.GRAMMAR_VERSION).value(ValueType.STRING);
    }

    /**
     * Returns the location of the grammar.
     */
    public Path getLocation() {
        return parsePropertyOrDefault(GrammarKey.LOCATION).value(ValueType.PATH);
    }

    /** Sets the {@link GrammarKey#LOCATION} property to the given value. */
    public void setLocation(Path path) {
        storeValue(GrammarKey.LOCATION, path);
    }

    /**
     * Returns a list of control labels, according to the
     * {@link GrammarKey#CONTROL_LABELS} property of the rule system.
     * @see GrammarKey#CONTROL_LABELS
     */
    public List<String> getControlLabels() {
        return parsePropertyOrDefault(GrammarKey.CONTROL_LABELS).value(ValueType.STRING_LIST);
    }

    /**
     * Sets the control labels property.
     * @see GrammarKey#CONTROL_LABELS
     */
    public void setControlLabels(List<String> controlLabels) {
        storeValue(GrammarKey.CONTROL_LABELS, controlLabels);
    }

    /**
     * Sets the rule application policy map.
     * @param policy the policy map to be used for rule application.
     * @see GrammarKey#ACTION_POLICY
     */
    public void setRulePolicy(PolicyMap policy) {
        storeValue(GrammarKey.ACTION_POLICY, policy);
    }

    /**
     * Returns the rule application policy map of the rule system.
     * @see GrammarKey#ACTION_POLICY
     */
    public PolicyMap getRulePolicy() {
        return parsePropertyOrDefault(GrammarKey.ACTION_POLICY).value(PolicyMap.VALUE_TYPE);
    }

    /**
     * Sets the deadlock check policy.
     * @param policy the policy to be used for deadlock checking.
     * @see GrammarKey#DEAD_POLICY
     */
    public void setDeadPolicy(CheckPolicy policy) {
        storeValue(GrammarKey.DEAD_POLICY, policy);
    }

    /**
     * Returns the deadlock check policy of the rule system.
     * @see GrammarKey#DEAD_POLICY
     */
    public CheckPolicy getDeadPolicy() {
        return parsePropertyOrDefault(GrammarKey.DEAD_POLICY).value(CheckPolicy.VALUE_TYPE);
    }

    /**
     * Sets the typecheck policy.
     * @param policy the policy to be used for type checking.
     * @see GrammarKey#TYPE_POLICY
     */
    public void setTypePolicy(CheckPolicy policy) {
        storeValue(GrammarKey.TYPE_POLICY, policy);
    }

    /**
     * Returns the type check policy of the rule system.
     * @see GrammarKey#TYPE_POLICY
     */
    public CheckPolicy getTypePolicy() {
        return parsePropertyOrDefault(GrammarKey.TYPE_POLICY).value(CheckPolicy.VALUE_TYPE);
    }

    /**
     * Returns a list of common labels, according to the
     * {@link GrammarKey#COMMON_LABELS} property of the rule system.
     * @see GrammarKey#COMMON_LABELS
     */
    public List<String> getCommonLabels() {
        return parsePropertyOrDefault(GrammarKey.COMMON_LABELS).value(ValueType.STRING_LIST);
    }

    /**
     * Sets the common labels property.
     * @see GrammarKey#COMMON_LABELS
     */
    public void setCommonLabels(List<String> commonLabels) {
        storeValue(GrammarKey.COMMON_LABELS, commonLabels);
    }

    /**
     * Sets the injectivity property to a certain value.
     * @param injective if <code>true</code>, non-injective matches are
     *        disallowed
     */
    public void setInjective(boolean injective) {
        storeValue(GrammarKey.INJECTIVE, injective);
    }

    /**
     * Returns the value of the injectivity property.
     * @return if <code>true</code>, non-injective matches are disallowed
     */
    public boolean isInjective() {
        return parsePropertyOrDefault(GrammarKey.INJECTIVE).value(ValueType.BOOLEAN);
    }

    /**
     * Sets the parallel edge mode to a given value.
     * @param mode the new parallel edge mode
     */
    public void setParallelMode(ParallelMode mode) {
        storeValue(GrammarKey.PARALLEL, mode);
    }

    /**
     * Returns the value of the parallel edge mode property, determining
     * whether host and rule graphs are multigraphs (with parallel edges) and,
     * if so, whether they are transformed under SPO or DPO semantics.
     */
    public ParallelMode getParallelMode() {
        return parsePropertyOrDefault(GrammarKey.PARALLEL).value(ParallelMode.VALUE_TYPE);
    }

    /**
     * Sets the ignore-regular-expressions property to a given value.
     * @param ignore if <code>true</code>, rules in which a composite regular
     * expression may match a path through an erased edge are accepted.
     */
    public void setIgnoreRegExp(boolean ignore) {
        storeValue(GrammarKey.IGNORE_REG_EXP, ignore);
    }

    /**
     * Returns the value of the ignore-regular-expressions property.
     * @return if <code>true</code>, rules in which a composite regular
     * expression may match a path through an erased edge are accepted
     * rather than reported as errors.
     */
    public boolean isIgnoreRegExp() {
        return parsePropertyOrDefault(GrammarKey.IGNORE_REG_EXP).value(ValueType.BOOLEAN);
    }

    /**
     * Sets the dangling edge check to a certain value.
     * @param dangling if <code>true</code>, matches with dangling edges are
     *        disallowed
     */
    public void setCheckDangling(boolean dangling) {
        storeValue(GrammarKey.DANGLING, dangling);
    }

    /**
     * Returns the value of the dangling edge property.
     * The dangling condition is part of the DPO gluing condition, so it is
     * implied (regardless of the property value) if the parallel edge mode
     * is {@link ParallelMode#DPO}.
     * @return if <code>true</code>, matches with dangling edges are disallowed.
     */
    public boolean isCheckDangling() {
        return parsePropertyOrDefault(GrammarKey.DANGLING).value(ValueType.BOOLEAN)
            || getParallelMode().isDPO();
    }

    /**
     * Returns the <i>local</i> name of the settings resource holding the
     * default exploration configuration — the name within the {@code explore}
     * folder, so {@code fast} for the resource {@code explore.fast} — or
     * {@code null} if the property is unset or unparsable. Resolution of the
     * name against the settings resources happens in the explore layer
     * (see {@code ExploreType#ofGrammar(GrammarModel)}).
     */
    public @Nullable QualName getExplorationName() {
        if (!containsKey(GrammarKey.EXPLORE_CONFIG)) {
            return null;
        }
        try {
            return parseProperty(GrammarKey.EXPLORE_CONFIG)
                .value(ValueType.QUAL_NAME)
                .orElse(null);
        } catch (FormatException exc) {
            // reported by the key checker
            return null;
        }
    }

    /**
     * Sets the local name of the settings resource holding the default
     * exploration configuration (see {@link #getExplorationName()}), removing
     * any stored legacy exploration strategy it supersedes.
     * @param name the name of the resource within the {@code explore} folder
     */
    public void setExplorationName(QualName name) {
        storeValue(GrammarKey.EXPLORE_CONFIG, Optional.of(name));
        remove(GrammarKey.EXPLORATION);
    }

    /**
     * Removes the exploration reference (see {@link #getExplorationName()}),
     * reverting the grammar to the legacy or default exploration.
     */
    public void removeExplorationName() {
        remove(GrammarKey.EXPLORE_CONFIG);
    }

    /**
     * Sets the active names property of a given resource kind.
     * @param kind the resource kind to set the names for
     * @param names the (non-{@code null}, but possible empty) list of names of the active resources
     */
    public void setActiveNames(ResourceKind kind, List<QualName> names) {
        assert kind != ResourceKind.RULE;
        // the kinds without an active-names key (see #getActiveNames) never get here,
        // as their set of active names is invariably empty
        assert resourceKeyMap
            .containsKey(kind) : String.format("Resource kind %s has no active names", kind);
        storeValue(resourceKeyMap.get(kind), names);
    }

    /**
     * Returns a list of active resource names of a given kind.
     * @param kind the queried resource kind
     * @return a (non-{@code null}, but possibly empty) set of active names
     */
    public Set<QualName> getActiveNames(ResourceKind kind) {
        var names = switch (kind) {
        case GROOVY, PROPERTIES, RULE, SETTINGS -> Collections.<QualName>emptyList();
        default -> parsePropertyOrDefault(resourceKeyMap.get(kind)).value(ValueType.QUAL_NAME_LIST);
        };
        return new TreeSet<>(names);
    }

    /**
     * Sets the disabled rules property.
     * @param enabling the (non-{@code null}, but possible empty) rule delta map
     */
    public void setRuleEnabling(DeltaMap<QualName> enabling) {
        storeValue(RULE_ENABLING, enabling);
    }

    /**
     * Returns a list of explicitly disabled rules.
     * @return a (non-{@code null}, but possibly empty) rule delta map
     */
    public DeltaMap<QualName> getRuleEnabling() {
        return parsePropertyOrDefault(RULE_ENABLING).value(ValueType.QUAL_NAME_DELTA_MAP);
    }

    /**
     * Sets the algebra family to a given value.
     */
    public void setAlgebraFamily(AlgebraFamily family) {
        storeValue(GrammarKey.ALGEBRA, family);
    }

    /**
     * Returns the selected algebra family.
     * @return the selected algebra family, or {@link AlgebraFamily#DEFAULT}
     * if none is selected.
     */
    public AlgebraFamily getAlgebraFamily() {
        return parsePropertyOrDefault(GrammarKey.ALGEBRA).value(AlgebraFamily.VALUE_TYPE);
    }

    /**
     * Returns the user-defined operations class.
     */
    public List<QualName> getUserOperations() {
        return parsePropertyOrDefault(GrammarKey.USER_OPS).value(ValueType.QUAL_NAME_LIST);
    }

    /**
     * Indicates if there is an installed value oracle.
     */
    public boolean hasValueOracle() {
        return getValueOracleFactory().getKind() != ValueOracleKind.NONE;
    }

    /**
     * Returns the installed value oracle.
     */
    public ValueOracleFactory getValueOracleFactory() {
        if (getAlgebraFamily() == AlgebraFamily.POINT) {
            // with the point algebra, any value will do and is the same
            // so we can just take the default value
            return DefaultOracle.instance();
        } else {
            return parsePropertyOrDefault(GrammarKey.ORACLE).value(ValueOracleFactory.VALUE_TYPE);
        }
    }

    /**
     * Returns an instance of the installed value oracle for the current grammar properties.
     */
    public ValueOracle getValueOracle() throws FormatException {
        return getValueOracleFactory().instance(this);
    }

    /**
     * Sets the creator edge check to a certain value.
     * @param check if <code>true</code>, creator edges are treated as negative
     *        application conditions
     */
    public void setCheckCreatorEdges(boolean check) {
        storeValue(GrammarKey.CREATOR_EDGE, check);
    }

    /**
     * Returns the value of the creator edge check property.
     * @return if <code>true</code>, creator edges are treated as negative
     *         application conditions
     */
    public boolean isCheckCreatorEdges() {
        return parsePropertyOrDefault(GrammarKey.CREATOR_EDGE).value(ValueType.BOOLEAN);
    }

    /**
     * Sets the graph isomorphism check to a certain value.
     * @param check if <code>true</code>, state graphs are compared up to
     *        isomorphism
     */
    public void setCheckIsomorphism(boolean check) {
        storeValue(GrammarKey.ISOMORPHISM, check);
    }

    /**
     * Returns the value of the graph isomorphism check property.
     * @return if <code>true</code>, state graphs are compared up to isomorphism
     */
    public boolean isCheckIsomorphism() {
        return parsePropertyOrDefault(GrammarKey.ISOMORPHISM).value(ValueType.BOOLEAN);
    }

    /**
     * Returns the value of the RHS-as-NAC property.
     * @return if <code>true</code>, the RHS is treated as a negative
     *         application condition, preventing the same rule instance from
     *         being applied twice in a row
     */
    public boolean isRhsAsNac() {
        return parsePropertyOrDefault(GrammarKey.RHS_AS_NAC).value(ValueType.BOOLEAN);
    }

    /**
     * Sets the RHS-as-NAC property to a certain value.
     * @param value if <code>true</code>, the RHS is treated as a negative
     *        application condition, preventing the same rule instance from
     *        being applied twice in a row
     */
    public void setRhsAsNac(boolean value) {
        storeValue(GrammarKey.RHS_AS_NAC, value);
    }

    /**
     * Sets the use of stored node IDs to a certain value.
     * @param check if <code>true</code>, stored node IDs are used to generate node numbers
     */
    public void setUseStoredNodeIds(boolean check) {
        storeValue(GrammarKey.USE_STORED_NODE_IDS, check);
    }

    /**
     * Returns the use of stored node IDs.
     * @return if <code>true</code>, stored node IDs are used to generate node numbers
     */
    public boolean isUseStoredNodeIds() {
        return parsePropertyOrDefault(GrammarKey.USE_STORED_NODE_IDS).value(ValueType.BOOLEAN);
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
        String oldText = oldLabel.toParsableString();
        // change the control labels
        List<String> controlLabels = getControlLabels();
        if (controlLabels != null && controlLabels.contains(oldText)) {
            List<String> newControlLabels = new ArrayList<>(controlLabels);
            int index = controlLabels.indexOf(oldText);
            newControlLabels.set(index, newLabel.toParsableString());
            result.setControlLabels(newControlLabels);
            hasChanged = true;
        }
        // change the common labels
        List<String> commonLabels = getCommonLabels();
        if (commonLabels != null && commonLabels.contains(oldText)) {
            List<String> newCommonLabels = new ArrayList<>(commonLabels);
            int index = commonLabels.indexOf(oldText);
            newCommonLabels.set(index, newLabel.toParsableString());
            result.setCommonLabels(newCommonLabels);
            hasChanged = true;
        }
        return hasChanged
            ? result
            : this;
    }

    /**
     * Returns a clone of this properties object where all occurrences of a
     * given collection of resource names are deleted.
     * @param names the names to be deleted
     * @return a clone of these properties, or the properties themselves if
     *         {@code names} did not occur
     */
    public GrammarProperties deleteResources(ResourceKind kind, Collection<QualName> names) {
        GrammarProperties result = clone();
        boolean hasChanged = false;
        var activeNames = new HashSet<>(getActiveNames(kind));
        if (activeNames.removeAll(names)) {
            hasChanged = true;
            var orderedActiveNames = new ArrayList<>(activeNames);
            orderedActiveNames.sort(Comparator.naturalOrder());
            result.setActiveNames(kind, orderedActiveNames);
            hasChanged = true;
        }
        // change the control labels
        if (kind == ResourceKind.RULE) {
            var actionPolicy = new PolicyMap();
            actionPolicy.putAll(getRulePolicy());
            var policyChanged = actionPolicy.keySet().removeAll(names);
            if (policyChanged) {
                result.setRulePolicy(actionPolicy);
                hasChanged = true;
            }
        }
        return hasChanged
            ? result
            : this;
    }

    /**
     * Returns a clone of this properties object where all occurrences of a
     * given resource name are replaced by a new name.
     * @param oldName the name to be replaced
     * @param newName the new value for {@code oldName}
     * @return a clone of these properties, or the properties themselves if
     *         {@code oldName} did not occur
     */
    public GrammarProperties renameResource(ResourceKind kind, QualName oldName, QualName newName) {
        GrammarProperties result = clone();
        boolean hasChanged = false;
        var activeNames = new HashSet<>(getActiveNames(kind));
        if (activeNames.remove(oldName)) {
            activeNames.add(newName);
            var orderedActiveNames = new ArrayList<>(activeNames);
            orderedActiveNames.sort(Comparator.naturalOrder());
            result.setActiveNames(kind, orderedActiveNames);
            hasChanged = true;
        }
        // change the control labels
        if (kind == ResourceKind.RULE) {
            // change rule names in the policy map
            var actionPolicy = new PolicyMap();
            actionPolicy.putAll(getRulePolicy());
            var namePolicy = actionPolicy.remove(oldName);
            if (namePolicy != null) {
                actionPolicy.put(newName, namePolicy);
                result.setRulePolicy(actionPolicy);
                hasChanged = true;
            }
            // change rule names in the disabled rules
            var ruleEnabling = new DeltaMap<>(getRuleEnabling());
            var delta = ruleEnabling.remove(oldName);
            if (delta != null) {
                ruleEnabling.set(newName, delta);
                result.setRuleEnabling(ruleEnabling);
                hasChanged = true;
            }
        }
        return hasChanged
            ? result
            : this;
    }

    /**
     * Checks if the stored properties are valid in a given grammar.
     */
    public void check(GrammarModel grammar) throws FormatException {
        FormatErrorSet errors = new FormatErrorSet();
        for (GrammarKey key : GrammarKey.values()) {
            try {
                var property = getProperty(key);
                var result = key
                    .parse(property == null
                        ? ""
                        : property);
                for (FormatError error : key.check(grammar, result)) {
                    errors.add("Error in property key '%s': %s", key.getKeyPhrase(), error, key);
                }
            } catch (FormatException exc) {
                errors
                    .add("Error in property key '%s': %s", key.getKeyPhrase(), exc.getMessage(),
                         key);
            }
        }
        errors.throwException();
    }

    /** Tests if the grammar properties specify any remove policies. */
    public boolean hasRemovePolicies() {
        if (getTypePolicy() == CheckPolicy.REMOVE) {
            return true;
        }
        if (getRulePolicy().containsValue(CheckPolicy.REMOVE)) {
            return true;
        }
        return false;
    }

    /** Returns the set of keys for which this properties object differs from another. */
    public Set<GrammarKey> getChanges(GrammarProperties properties) {
        var result = EnumSet.noneOf(GrammarKey.class);
        for (var key : GrammarKey.values()) {
            if (!Objects.equals(getProperty(key), properties.getProperty(key))) {
                result.add(key);
            }
        }
        return result;
    }

    /** Returns a non-fixed clone of the properties. */
    @Override
    public GrammarProperties clone() {
        return new GrammarProperties(this);
    }

    @Override
    public Optional<GrammarKey> getKey(String name) {
        return GrammarKey.getKey(name);
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

    /** Mapping from resource kinds to corresponding property keys. */
    static private final Map<ResourceKind,GrammarKey> resourceKeyMap
        = new EnumMap<>(ResourceKind.class);

    static {
        resourceKeyMap.put(ResourceKind.TYPE, GrammarKey.TYPE_NAMES);
        resourceKeyMap.put(ResourceKind.CONTROL, GrammarKey.CONTROL_NAMES);
        resourceKeyMap.put(ResourceKind.PROLOG, GrammarKey.PROLOG_NAMES);
        resourceKeyMap.put(ResourceKind.HOST, GrammarKey.START_GRAPH_NAMES);
    }

    /** Repair this properties bundle, as necessitated because of version changes. */
    public GrammarProperties repairVersion() {
        GrammarProperties result = this;
        String version = getGrammarVersion();
        if (Version.compareGrammarVersions(version, Version.GRAMMAR_VERSION_3_4) == -1) {
            result = result.clone();
            result.remove(GrammarKey.ATTRIBUTE_SUPPORT);
            result.remove(GrammarKey.TRANSITION_BRACKETS);
            // convert numeric value of TRANSITION_PARAMETERS
            GrammarKey paramsKey = GrammarKey.TRANSITION_PARAMETERS;
            String paramsVal = getProperty(paramsKey.getName());
            if (paramsVal != null && !paramsKey.parser().accepts(paramsVal)) {
                try {
                    int paramsIntVal = Integer.parseInt(paramsVal);
                    result
                        .setUseParameters(paramsIntVal == 0
                            ? ThreeValued.FALSE
                            : ThreeValued.TRUE);
                } catch (NumberFormatException exc) {
                    // it was not a number either; remove the key altogether
                    result.remove(paramsKey.getName());
                }
            }
        }
        if (Version.compareGrammarVersions(version, Version.GRAMMAR_VERSION_3_10) == -1) {
            result = result.clone();
            result.setUseStoredNodeIds(true);
        }
        // Note: the legacy exploration strategy key is NOT converted here.
        // The 'exploration' key names a settings resource, and creating a
        // resource is beyond a properties-level repair; the read-time
        // fallback (see ExploreType#ofLegacy) interprets the legacy key
        // indefinitely, and the first dialog save migrates it.
        return result;
    }

    /** Returns a clone of this properties bundle where the derived properties have been added. */
    public GrammarProperties addDerivedProperties(Path location) {
        GrammarProperties result = clone();
        result.setLocation(location);
        return result;
    }

    /** Returns a clone of a given properties bundle where all derived properties have been removed. */
    public GrammarProperties removeDerivedProperties() {
        GrammarProperties result = clone();
        for (GrammarKey key : GrammarKey.values()) {
            if (key.isDerived()) {
                result.remove(key.toString());
            }
        }
        return result;
    }

}
