/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.grammar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.algebra.AlgebraFamily;
import nl.utwente.groove.algebra.UserSignature;
import nl.utwente.groove.explore.ExploreType;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.grammar.model.RuleModel;
import nl.utwente.groove.transform.oracle.ValueOracleKind;
import nl.utwente.groove.util.DocumentedEnum;
import nl.utwente.groove.util.Factory;
import nl.utwente.groove.util.Groove;
import nl.utwente.groove.util.Properties;
import nl.utwente.groove.util.Properties.Entry;
import nl.utwente.groove.util.Properties.KeyParser;
import nl.utwente.groove.util.Properties.ValueType;
import nl.utwente.groove.util.Strings;
import nl.utwente.groove.util.ThreeValued;
import nl.utwente.groove.util.parse.FormatErrorSet;
import nl.utwente.groove.util.parse.FormatException;
import nl.utwente.groove.util.parse.Parser;
import nl.utwente.groove.util.parse.StringParser;

/** Grammar property keys. */
public enum GrammarKey implements Properties.Key, GrammarChecker {
    /** Property name for the GROOVE version. */
    GROOVE_VERSION("grooveVersion", "The Groove version that created this grammar",
        ValueType.STRING),

    /** Property name for the Grammar version. */
    GRAMMAR_VERSION("grammarVersion", "The version of this grammar", ValueType.STRING),

    /** Location of this Grammar. */
    LOCATION("location", "The place in the file system where this grammar is stored",
        ValueType.PATH),

    /** One-line documentation comment on the graph production system. */
    REMARK("remark", "A one-line description of the graph production system", ValueType.STRING),

    /** Property name for the algebra to be used during simulation. */
    ALGEBRA("algebraFamily",
        "<body>Algebra used for attributes" + DocumentedEnum.document(AlgebraFamily.class),
        ValueType.ALGEBRA_FAMILY),

    /** Property name for the value oracle to be used for matching unbound value parameters. */
    ORACLE("valueOracle",
        "Source of values for unbound value parameters"
            + DocumentedEnum.document(ValueOracleKind.class)
            + "<p>If the algebra family is set to <i>point</i>, the oracle is disregarded",
        ValueType.ORACLE_FACTORY),

    /** Name of a class containing user-defined algebraic operations. */
    USER_OPS("userOperations",
        "Qualified class name of a class containing used-defined data operations. "
            + "<p>Static methods annotated with @UserOperation can be used in rules.",
        ValueType.STRING),
    /**
     * Flag determining the injectivity of the rule system. If <code>true</code>,
     * all rules should be matched injectively. Default is <code>false</code>.
     */
    INJECTIVE("matchInjective",
        "<body>Flag controlling if all rules should be matched injectively. "
            + "<p>If true, overrules the local rule injectivity property",
        ValueType.BOOLEAN),
    /**
     * Flag determining whether multi-sorted graphs (with parallel edges) are used for transformation.
     * Default if {@code false}.
     */
    PARALLEL("parallelEdges",
        "Flag controlling if the host graphs may have parallel edges; in other words, "
            + "if they are multi-sorted graphs. If false (the default), simple graphs are used instead.",
        ValueType.BOOLEAN),
    /**
     * Dangling edge check. If <code>true</code>, all
     * matches that leave dangling edges are invalid. Default is
     * <code>false</code>.
     */
    DANGLING("checkDangling",
        "Flag controlling if dangling edges should be forbidden rather than deleted",
        ValueType.BOOLEAN),

    /**
     * Creator edge check. If <code>true</code>, creator
     * edges are implicitly treated as (individual) NACs. Default is
     * <code>false</code>.
     */
    CREATOR_EDGE("checkCreatorEdges",
        "Flag controlling if creator edges should be treated as implicit NACs", ValueType.BOOLEAN),

    /**
     * RHS-as-NAC property. If <code>true</code>, each RHS
     * is implicitly treated as a NAC. Default is <code>false</code>.
     */
    RHS_AS_NAC("rhsIsNAC", "Flag controlling if RHSs should be treated as implicit NACs",
        ValueType.BOOLEAN),

    /**
     * Isomorphism check. If <code>true</code>, state
     * graphs are compared up to isomorphism; otherwise, they are compared up to
     * equality. Default is <code>true</code>.
     */
    ISOMORPHISM("checkIsomorphism", "Flag controlling whether states are checked up to isomorphism",
        ValueType.BOOLEAN),

    /**
     * Space-separated list of active start graph names.
     */
    START_GRAPH_NAMES("startGraph", "List of active start graph names", ValueType.QUAL_NAME_LIST),

    /**
     * Name of the active control program.
     */
    CONTROL_NAMES("controlProgram", "List of enabled control programs", ValueType.QUAL_NAME_LIST),

    /**
     * Space-separated list of active type graph names.
     */
    TYPE_NAMES("typeGraph", "List of active type graph names", ValueType.QUAL_NAME_LIST),

    /**
     * Space-separated list of active prolog program names.
     */
    PROLOG_NAMES("prolog", "List of active prolog program names", ValueType.QUAL_NAME_LIST),

    /** Policy for rule application. */
    ACTION_POLICY("actionPolicy",
        "<body>List of <i>key=value</i> pairs, where <i>key</i> is an action name and <i>value</i> is one of:"
            + "<li> - <i>off</i>: the action is disabled (overrules the <b>enabled</b> property)"
            + "<li> - <i>silent</i>: the constraint is checked and flagged on the state as a condition"
            + "<li> - <i>error</i> (default): applicability is an error"
            + "<li> - <i>remove</i>: applicability causes the state to be removed from the state space"
            + "<p>The last three are only valid for forbidden and invariant properties",
        ValueType.POLICY_MAP),

    /** Policy for dealing with type violations. */
    TYPE_POLICY("typePolicy",
        "<body>Flag controlling how dynamic type constraints (multiplicities, composites) are dealt with."
            + "<li>- <i>off</i>: dynamic type constraints are not checked"
            + "<li>- <i>error</i> (default): dynamic type violations are flagged as errors"
            + "<li>- <i>remove</i>: dynamic type violations cause the state to be removed from the state space",
        ValueType.CHECK_POLICY),

    /** Policy for dealing with deadlocks. */
    DEAD_POLICY("deadlockPolicy",
        "Flag controlling how deadlocked states are dealt with."
            + "<br>(A state is considered deadlocked if no scheduled transformer is applicable.)"
            + "<li>- <i>off</i> (default): deadlocks are not checked"
            + "<li>- <i>error</i>: deadlocks are flagged as errors",
        ValueType.CHECK_POLICY),

    /**
     * Exploration strategy description.
     */
    EXPLORATION("explorationStrategy", "Default exploration strategy for this grammar",
        ValueType.EXPLORE_TYPE),

    /** Flag that determines if output parameters are added to group calls. */
    STORE_OUT_PARS("storeOutParameters",
        "Flag controlling if output parameters are stored for implicit group calls."
            + "<li>- <i>false</i> (default): no output parameters are stored, hence transition arguments revert to don't-care"
            + "<li>- <i>true</i>: output parameters are stored and included in transition arguments, increasing the state space",
        ValueType.BOOLEAN),

    /**
     * Space-separated list of control labels of a graph grammar. The
     * control labels are those labels which should be matched first for optimal
     * performance, presumably because they occur infrequently or indicate a
     * place where rules are likely to be applicable.
     */
    CONTROL_LABELS("controlLabels", "List of rare labels, used to optimise rule matching",
        ValueType.STRING_LIST),

    /**
     * Space-separated list of common labels of a graph grammar. The
     * control labels are those labels which should be matched last for optimal
     * performance, presumably because they occur frequently.
     */
    COMMON_LABELS("commonLabels", "List of frequent labels, used to optimise rule matching",
        ValueType.STRING_LIST),

    /**
     * Flag that determines if transition parameters are included in the LTS
     * transition labels
     */
    TRANSITION_PARAMETERS("transitionParameters", "Show parameters",
        "Flag controlling if transition labels should include a (possibly empty) argument list. Possibly values:"
            + "<li>- <i>false</i>: no arguments are displayed"
            + "<li>- <i>some</i> (default): arguments are only displayed for rules with parameters and for recipes"
            + "<li>- <i>true</i>: arguments are always displayed, also for rules without parameters",
        ValueType.THREE_VALUED),

    /**
     * Flag that determines if (binary) loops can be shown as vertex labels.
     */
    LOOPS_AS_LABELS("loopsAsLabels",
        "Flag controlling if binary self-edges may be shown as vertex labels", ValueType.BOOLEAN),

    /** Flag that determines if node numbers in loaded graphs are based on the stored (GXL) node ids. */
    USE_STORED_NODE_IDS("useStoredNodeIDs",
        "Flag controlling if node numbers in graphs are based on the stored (GXL) node ids",
        ValueType.BOOLEAN),;

    /**
     * Constructor for a key with values parsed by a given parser
     * @param name name of the key; should be an identifier possibly prefixed by #SYSTEM_KEY_PREFIX
     * @param explanation short explanation of the meaning of the key
     * {@link StringParser#identity()} is used
     */
    private GrammarKey(String name, String explanation, ValueType keyType) {
        this(name, null, explanation, keyType);
    }

    /**
     * Constructor for a key with a plain string value
     * @param name name of the key; should be an identifier possibly prefixed by #SYSTEM_KEY_PREFIX
     * @param keyPhrase user-readable version of the name; if {@code null},
     * the key phrase is constructed from {@code name}
     * @param explanation short explanation of the meaning of the key
     * {@link StringParser#identity()} is used
     */
    private GrammarKey(String name, String keyPhrase, String explanation, ValueType keyType) {
        this.name = name;
        this.keyPhrase = keyPhrase == null
            ? Strings.unCamel(name, false)
            : keyPhrase;
        this.explanation = explanation;
        this.keyType = keyType;
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
        return isDerived() || this == GROOVE_VERSION || this == GRAMMAR_VERSION;
    }

    @Override
    public boolean isDerived() {
        return this == LOCATION;
    }

    @Override
    public String getKeyPhrase() {
        return this.keyPhrase;
    }

    private final String keyPhrase;

    @Override
    public KeyParser parser() {
        var result = this.parser;
        if (result == null) {
            var inner = switch (this) {
            case ALGEBRA -> new Parser.EnumParser<>(AlgebraFamily.class, AlgebraFamily.DEFAULT);
            case COMMON_LABELS, CONTROL_LABELS -> Parser.splitter;
            case CREATOR_EDGE, PARALLEL, DANGLING, RHS_AS_NAC, INJECTIVE, STORE_OUT_PARS, USE_STORED_NODE_IDS -> Parser.boolFalse;
            case ISOMORPHISM, LOOPS_AS_LABELS -> Parser.boolTrue;
            case START_GRAPH_NAMES, CONTROL_NAMES, TYPE_NAMES, PROLOG_NAMES -> QualName
                .listParser();
            case TYPE_POLICY -> new Parser.EnumParser<>(CheckPolicy.class, CheckPolicy.ERROR,
                convert("off", null, "error", "remove"));
            case ACTION_POLICY -> CheckPolicy.multiParser;
            case DEAD_POLICY -> new Parser.EnumParser<>(CheckPolicy.class, CheckPolicy.OFF,
                convert("off", null, "error", null));
            case EXPLORATION -> ExploreType.parser();
            case TRANSITION_PARAMETERS -> new Parser.EnumParser<>(ThreeValued.class,
                ThreeValued.SOME, true);
            case LOCATION -> Parser.path;
            case ORACLE -> OracleParser.instance();
            default -> StringParser.identity();
            };
            this.parser = result = new KeyParser(this, inner);
        }
        return result;
    }

    private KeyParser parser;

    @Override
    public FormatErrorSet apply(GrammarModel grammar, Entry value) {
        return this.checker.get().apply(grammar, value);
    }

    /** Lazily created checker for values of this key. */
    private final Factory<GrammarChecker> checker = Factory.lazy(this::computeChecker);

    /** Computes the value for {@link #checker}. */
    private GrammarChecker computeChecker() {
        return switch (this) {
        case ACTION_POLICY -> ActionPolicyChecker.instance;
        case CONTROL_NAMES -> ResourceChecker.get(ResourceKind.CONTROL);
        case ORACLE -> oracleChecker;
        case PROLOG_NAMES -> ResourceChecker.get(ResourceKind.PROLOG);
        case START_GRAPH_NAMES -> ResourceChecker.get(ResourceKind.HOST);
        case TYPE_NAMES -> ResourceChecker.get(ResourceKind.TYPE);
        case USER_OPS -> UserOperationsChecker.instance;
        default -> trueChecker;
        };
    }

    @Override
    public ValueType getKeyType() {
        return this.keyType;
    }

    private final ValueType keyType;

    @Override
    public Entry wrap(Object value) throws IllegalArgumentException {
        return new Entry(this, value);
    }

    @Override
    public boolean isNotable() {
        return switch (this) {
        case ACTION_POLICY, ALGEBRA, CREATOR_EDGE, DANGLING, DEAD_POLICY, INJECTIVE, ISOMORPHISM, ORACLE, RHS_AS_NAC, STORE_OUT_PARS, TRANSITION_PARAMETERS, TYPE_POLICY -> true;
        default -> false;
        };
    }

    /** Returns the grammar keys that require a full grammar reload upon value changes. */
    static public Set<GrammarKey> getReloadKeys() {
        return reloadKeys.get();
    }

    /** Lazily created set of keys that require a full grammar reload when their value changes. */
    static private Factory<Set<GrammarKey>> reloadKeys = Factory.lazy(GrammarKey::createReloadKeys);

    /** Computes the value for {@link #reloadKeys}. */
    static private Set<GrammarKey> createReloadKeys() {
        return EnumSet.of(USE_STORED_NODE_IDS, USER_OPS);
    }

    /** Returns the grammar key with a given name, if any; or {@code null} if the name is not a recognisable key */
    static public Optional<GrammarKey> getKey(String name) {
        return Optional.ofNullable(nameKeyMap.get().get(name));
    }

    /** Mapping from key names (as in {@link GrammarKey#getName()}) to keys. */
    static private final Factory<Map<String,GrammarKey>> nameKeyMap
        = Factory.lazy(GrammarKey::createNameKeyMap);

    /** Creator methods for the {@link #nameKeyMap}*/
    static private Map<String,GrammarKey> createNameKeyMap() {
        var result = new HashMap<String,GrammarKey>();
        Arrays.stream(GrammarKey.values()).forEach(k -> result.put(k.getName(), k));
        return result;
    }

    /** Name of deprecated key for attribute support. */
    static public final String ATTRIBUTE_SUPPORT = "attributeSupport";
    /** Name of deprecated key for transition brackets. */
    static public final String TRANSITION_BRACKETS = "transitionBrackets";

    /** Checks whether a value is a list of names of a given resource kind. */
    private static class ResourceChecker implements GrammarChecker {
        ResourceChecker(ResourceKind kind) {
            this.kind = kind;
        }

        /** Returns the resource kind being checked. */
        public ResourceKind getKind() {
            return this.kind;
        }

        private final ResourceKind kind;

        @Override
        public FormatErrorSet apply(GrammarModel grammar, GrammarProperties.Entry value) {
            var unknowns = new ArrayList<>(value.getQualNameList());
            var result = new FormatErrorSet();
            unknowns.removeAll(grammar.getResourceMap(getKind()).keySet());
            if (!unknowns.isEmpty()) {
                result
                    .add("Unknown %s name%s %s", Strings.toLower(getKind().getName()),
                         unknowns.size() == 1
                             ? ""
                             : "s",
                         Groove.toString(unknowns.toArray(), "'", "'", "', '", "' and '"));
            }
            return result;
        }

        /** Returns the singleton checker for a given resource kind. */
        public static ResourceChecker get(ResourceKind kind) {
            return resourceMap.get().get(kind);
        }

        /** Lazily computed mapping from resource kinds to their checkers. */
        static private Factory<Map<ResourceKind,ResourceChecker>> resourceMap
            = Factory.lazy(ResourceChecker::createResourceMap);

        /** Computes the value for #resourceMap. */
        static private Map<ResourceKind,ResourceChecker> createResourceMap() {
            Map<ResourceKind,ResourceChecker> result = new EnumMap<>(ResourceKind.class);
            for (ResourceKind kind : ResourceKind.values()) {
                switch (kind) {
                case CONTROL:
                case HOST:
                case PROLOG:
                case TYPE:
                    result.put(kind, new ResourceChecker(kind));
                    break;
                default:
                    // there is no checker
                }
            }
            return result;
        }

    }

    /** Checks the value for the {@link GrammarKey#ACTION_POLICY} key. */
    private static class ActionPolicyChecker extends ResourceChecker {
        public ActionPolicyChecker() {
            super(ResourceKind.RULE);
        }

        @Override
        public FormatErrorSet apply(GrammarModel grammar, Entry value) {
            FormatErrorSet result = new FormatErrorSet();
            List<QualName> unknowns = new ArrayList<>();
            var map = value.getPolicyMap();
            for (Map.Entry<QualName,CheckPolicy> entry : map.entrySet()) {
                QualName name = entry.getKey();
                RuleModel rule = grammar.getRuleModel(name);
                if (rule == null) {
                    unknowns.add(name);
                } else {
                    CheckPolicy policy = entry.getValue();
                    policy
                        .isFor(rule.getRole())
                        .ifPresent(e -> result
                            .add("Policy '%s' is unsuitable for %s '%s': %s", policy.getName(),
                                 rule.getRole(), rule.getQualName(), e));
                }
            }
            if (!unknowns.isEmpty()) {
                result
                    .add("Unknown %s name%s %s", Strings.toLower(getKind().getName()),
                         unknowns.size() == 1
                             ? ""
                             : "s",
                         Groove.toString(unknowns.toArray(), "'", "'", "', '", "' and '"));
            }
            return result;
        }

        public static ActionPolicyChecker instance = new ActionPolicyChecker();
    }

    /** Checks whether the user-defined operations conflict with the algebra family. */
    private static class UserOperationsChecker implements GrammarChecker {
        @Override
        public FormatErrorSet apply(GrammarModel grammar, Entry value) {
            FormatErrorSet result = new FormatErrorSet();
            var family = grammar.getProperties().getAlgebraFamily();
            String className = value.getString();
            if (!className.isEmpty()) {
                try {
                    UserSignature.loadUserClass(className);
                } catch (FormatException exc) {
                    result.addAll(exc.getErrors());
                }
                if (family != AlgebraFamily.DEFAULT && family != AlgebraFamily.POINT) {
                    result
                        .add("User-defined operations cannot be used with '%s' algebra family",
                             family.getName());
                }
            }
            return result;
        }

        /** The singleton instance of this class. */
        public static UserOperationsChecker instance = new UserOperationsChecker();
    }

    /** Checker that tests whether a value oracle can be generated from the grammar properties. */
    private static GrammarChecker oracleChecker = (g, v) -> {
        FormatErrorSet result = new FormatErrorSet();
        try {
            g.getProperties().getValueOracle();
        } catch (FormatException exc) {
            result.addAll(exc.getErrors());
        }
        return result;
    };

    /** Checker that always returns the empty error set. */
    private static GrammarChecker trueChecker = (g, v) -> FormatErrorSet.EMPTY;

    /** Workaround for apparent null annotation bug. */
    private static @Nullable String[] convert(@Nullable String... strings) {
        return strings;
    }
}