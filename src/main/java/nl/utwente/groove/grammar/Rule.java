// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2023 University of Twente

// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// http://www.apache.org/licenses/LICENSE-2.0

// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
// either express or implied. See the License for the specific
// language governing permissions and limitations under the License.
/*
 * $Id$
 */
package nl.utwente.groove.grammar;

import static nl.utwente.groove.util.Factory.lazy;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.algebra.AlgebraFamily;
import nl.utwente.groove.algebra.Operation;
import nl.utwente.groove.control.Binding;
import nl.utwente.groove.grammar.UnitPar.RulePar;
import nl.utwente.groove.grammar.host.HostEdge;
import nl.utwente.groove.grammar.host.HostGraph;
import nl.utwente.groove.grammar.host.HostNode;
import nl.utwente.groove.grammar.host.ValueNode;
import nl.utwente.groove.grammar.rule.Anchor;
import nl.utwente.groove.grammar.rule.DefaultRuleNode;
import nl.utwente.groove.grammar.rule.LabelVar;
import nl.utwente.groove.grammar.rule.MatchChecker;
import nl.utwente.groove.grammar.rule.OperatorNode;
import nl.utwente.groove.grammar.rule.RuleEdge;
import nl.utwente.groove.grammar.rule.RuleGraph;
import nl.utwente.groove.grammar.rule.RuleNode;
import nl.utwente.groove.grammar.rule.RuleToHostMap;
import nl.utwente.groove.grammar.type.TypeGraph;
import nl.utwente.groove.grammar.type.TypeGuard;
import nl.utwente.groove.graph.GraphProperties;
import nl.utwente.groove.graph.GraphProperties.Key;
import nl.utwente.groove.match.Matcher;
import nl.utwente.groove.match.MatcherFactory;
import nl.utwente.groove.match.SearchStrategy;
import nl.utwente.groove.match.TreeMatch;
import nl.utwente.groove.match.plan.PlanSearchStrategy;
import nl.utwente.groove.transform.Proof;
import nl.utwente.groove.util.Exceptions;
import nl.utwente.groove.util.Fixable;
import nl.utwente.groove.util.Visitor;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Type of a production rule. The rule essentially consists of a left hand
 * side graph, a right hand side graph, a rule morphism and a set of NACs.
 * @author Arend Rensink
 * @version $Revision$
 */
public class Rule implements Action, Fixable {
    /**
     * Constructs a rule that is a sub-condition of another rule.
     * @param condition the application condition of this rule
     * @param rhs the right hand side graph of the rule
     * @param coRoot map of creator nodes in the parent rule to creator nodes
     *        of this rule
     */
    public Rule(Condition condition, RuleGraph rhs, RuleGraph coRoot) {
        assert condition.getTypeGraph().getFactory() == rhs.getFactory().getTypeFactory()
            && (coRoot == null || rhs.getFactory() == coRoot.getFactory());
        this.condition = condition;
        this.qualName = QualName.parse(condition.getName());
        this.coRoot = coRoot;
        this.lhs = condition.getPattern();
        this.rhs = rhs;
        assert coRoot == null || rhs().nodeSet().containsAll(coRoot.nodeSet()) : String
            .format("RHS nodes %s do not contain all co-root values %s", rhs().nodeSet(),
                    coRoot.nodeSet());
    }

    /** Returns the condition with which this rule is associated. */
    public Condition getCondition() {
        return this.condition;
    }

    /** Application condition of this rule. */
    private final Condition condition;

    /** Returns the type graph of this rule. */
    public TypeGraph getTypeGraph() {
        return getCondition().getTypeGraph();
    }

    /** Returns root graph of the condition with which this rule is associated. */
    public RuleGraph getRoot() {
        return getCondition().getRoot();
    }

    /** Returns the mapping from the parent RHS to this rule's RHS. */
    final RuleGraph getCoRoot() {
        return this.coRoot;
    }

    /** Mapping from the context of this rule to the RHS. */
    private final RuleGraph coRoot;

    /**
     * Returns the left hand side of this Rule.
     * @ensure <tt>result == morphism().source()</tt>
     */
    public RuleGraph lhs() {
        return this.lhs;
    }

    /**
     * This production rule's left hand side.
     * @invariant lhs != null
     */
    private RuleGraph lhs;

    /**
     * Returns the right hand side of this Rule.
     * @ensure <tt>result == morphism().cod()</tt>
     */
    public RuleGraph rhs() {
        return this.rhs;
    }

    /**
     * This production rule's right hand side.
     * @invariant rhs != null
     */
    private RuleGraph rhs;

    /** Returns the qualified name of this rule (which equals the name of the associated condition). */
    @Override
    public QualName getQualName() {
        return this.qualName;
    }

    private final QualName qualName;

    /** Returns the system properties. */
    @Override
    public GrammarProperties getGrammarProperties() {
        return getCondition().getGrammarProperties();
    }

    /**
     * Sets the parent rule of this rule, together with the nesting level and
     * the co-root map.
     * @param parent the parent rule for this rule
     * @param level nesting level of this rule within the condition tree
     */
    public void setParent(Rule parent, int[] level) {
        testFixed(false);
        assert getCoRoot() != null : String
            .format("Sub-rule at level %s must have a non-trivial co-root map",
                    Arrays.toString(level));
        if (parent != null) {
            assert parent.rhs().nodeSet().containsAll(getCoRoot().nodeSet()) : String
                .format("Rule '%s': Parent nodes %s do not contain all co-roots %s", getQualName(),
                        parent.rhs().nodeSet(), getCoRoot().nodeSet());
        }
        this.parent = parent;
    }

    /**
     * Returns the parent rule of this rule. The parent may be this rule itself.
     */
    public Rule getParent() {
        if (this.parent == null) {
            testFixed(true);
            this.parent = this;
        }
        return this.parent;
    }

    /**
     * The parent rule of this rule; may be <code>null</code>, if this is a
     * top-level rule.
     */
    private Rule parent;

    /**
     * Sets the rule properties from a graph property map.
     */
    public void setProperties(GraphProperties properties) {
        testFixed(false);
        try {
            this.priority = properties.parseProperty(Key.PRIORITY).getInteger();
            this.transitionLabel = properties.parseProperty(Key.TRANSITION_LABEL).getString();
            this.formatString = properties.parseProperty(Key.FORMAT).getString();
        } catch (FormatException exc) {
            throw Exceptions.illegalState("Error in graph properties: %s", exc.getMessage());
        }
    }

    @Override
    public String getTransitionLabel() {
        var result = this.transitionLabel;
        if (result == null || result.isEmpty()) {
            result = getQualName().toString();
        }
        return result;
    }

    /** The optional transition label. */
    private @Nullable String transitionLabel;

    @Override
    public Optional<String> getFormatString() {
        var result = this.formatString;
        return result == null || result.isEmpty()
            ? Optional.empty()
            : Optional.of(result);
    }

    /** The optional format string. */
    private @Nullable String formatString;

    /**
     * Returns the priority of this object. A higher number means higher
     * priority, with {@link #DEFAULT_PRIORITY} the lowest.
     */
    @Override
    public int getPriority() {
        return this.priority;
    }

    /** The rule priority. */
    private int priority;

    /** Returns {@code true} if the rule (or any of its subrules)
     * contains indeterminate operators.
     * @see Operation#isIndeterminate()
     */
    public boolean isIndeterminate() {
        return !getIndeterminates().isEmpty()
            || getSubRules().stream().anyMatch(Rule::isIndeterminate);
    }

    /** Returns the list of operator nodes in the LHS with indeterminate operators. */
    public List<OperatorNode> getIndeterminates() {
        return this.indeterminates.get();
    }

    /** The lazily created list of operator nodes with indeterminate operators. */
    private final Supplier<List<OperatorNode>> indeterminates = lazy(this::computeIndeterminates);

    /** Computes the value for {@link #indeterminates}. */
    private List<OperatorNode> computeIndeterminates() {
        List<OperatorNode> result = new ArrayList<>();
        for (var lhsNode : lhs().nodeSet()) {
            if (lhsNode instanceof OperatorNode opNode) {
                if (opNode.getOperator().isIndeterminate()) {
                    result.add(opNode);
                }
            }
        }
        return result;
    }

    /** Sets the match filter method. */
    public void setMatchFilter(MatchChecker matchFilter) {
        this.matchFilter = matchFilter;
    }

    /** Returns the optional match filter method. */
    public Optional<MatchChecker> getMatchFilter() {
        return Optional.ofNullable(this.matchFilter);
    }

    private @Nullable MatchChecker matchFilter;

    /** Sets the dangling-edge check for matches of this rule. */
    public void setCheckDangling(boolean checkDangling) {
        this.checkDangling = checkDangling;
    }

    /** Indicates if this rule should check for dangling edges. */
    public boolean isCheckDangling() {
        return this.checkDangling;
    }

    /** Flag indicating whether rule applications should be checked for dangling edges. */
    private boolean checkDangling;

    /** Indicates if this is a top-level rule. */
    public boolean isTop() {
        return getParent() == this;
    }

    /** Returns the top rule of the hierarchy in which this rule is nested. */
    public Action getTop() {
        if (isTop()) {
            return this;
        } else {
            return getParent().getTop();
        }
    }

    Set<RuleNode> computeInputNodes() {
        Set<RuleNode> result = null;
        // if this is a top-level rule, the (only) input nodes
        // are the input-only parameter nodes
        if (isTop()) {
            result = new HashSet<>(getSignature()
                .stream()
                .filter(v -> v.isInOnly())
                .map(v -> v.getNode())
                .collect(Collectors.toSet()));
        }
        return result;
    }

    /**
     * Indicates if this rule has sub-rules.
     */
    public boolean hasSubRules() {
        assert isFixed();
        return !getSubRules().isEmpty();
    }

    /**
     * Adds a sub-rule to this rule.
     * The sub-rules are those connected to sub-conditions
     * in the associated condition tree.
     * @param subRule the new sub-rule
     */
    public void addSubRule(Rule subRule) {
        assert !isFixed();
        assert subRule.isFixed();
        getSubRules().add(subRule);
    }

    /**
     * Returns the direct sub-rules of this rule, i.e., the sub-rules that have
     * this rule as their parent.
     */
    public Collection<Rule> getSubRules() {
        return this.subRules.get();
    }

    /**
     * The collection of direct sub-rules of this rules. Lazily created by
     * {@link #getSubRules()}.
     */
    private final Supplier<Collection<Rule>> subRules = lazy(() -> {
        var result = new TreeSet<Rule>();
        getCondition()
            .getSubConditions()
            .stream()
            .flatMap(c -> c.getSubConditions().stream())
            .filter(c -> c.hasRule())
            .map(c -> c.getRule())
            .forEach(r -> result.add(r));
        return result;
    });

    /**
     * Sets the parameters of this rule. The rule can have numbered and hidden
     * parameters. Numbered parameters are divided into input (LHS) and output
     * (RHS-only) parameters, and are visible on the transition label.
     * @param parList the list of (visible) parameters
     * @param hiddenPars the set of hidden (i.e., unnumbered) parameter nodes
     */
    public void setSignature(Signature<UnitPar.RulePar> parList, Set<RuleNode> hiddenPars) {
        assert !isFixed();
        this.sig = parList;
        this.hiddenPars = hiddenPars;
        for (int i = 0; i < parList.size(); i++) {
            // add the LHS parameters to the root graph
            RuleNode parNode = parList.getPar(i).getNode();
            if (this.lhs.containsNode(parNode)) {
                this.condition.getRoot().addNode(parNode);
            }
        }
    }

    /** Returns the signature of the rule. */
    @Override
    public Signature<UnitPar.RulePar> getSignature() {
        assert isFixed();
        var result = this.sig;
        if (result == null) {
            this.sig = result = new Signature<>();
        }
        return result;
    }

    /** The signature of the rule. */
    private @Nullable Signature<UnitPar.RulePar> sig;

    /**
     * Returns the set of hidden (i.e., unnumbered) parameter nodes of this
     * rule.
     */
    Set<RuleNode> getHiddenPars() {
        return this.hiddenPars;
    }

    /**
     * Set of anonymous (unnumbered) parameters.
     */
    private Set<RuleNode> hiddenPars;

    /**
     * Returns, for a given index in the signature, the corresponding
     * anchor or creator source of the actual value.
     */
    public Binding getParBinding(int i) {
        return this.parBinding.get().get(i);
    }

    /**
     * Creates the connection between parameter positions and
     * anchor respectively created node indices.
     * @see #getParBinding(int)
     */
    private @NonNull List<Binding> computeParBinding() {
        List<Binding> result = new ArrayList<>();
        var creatorNodes = Arrays.asList(getCreatorNodes());
        for (RulePar par : getSignature()) {
            Binding binding;
            RuleNode ruleNode = par.getNode();
            if (par.isCreator() || par.isAsk()) {
                // look up the node in the creator nodes
                binding = Binding.creator(par, creatorNodes.indexOf(ruleNode));
            } else {
                // look up the node in the anchor
                int ix = getAnchor().indexOf(ruleNode);
                assert ix >= 0 : String.format("Node %s not in anchors %s", ruleNode, getAnchor());
                binding = Binding.anchor(par, ix);
            }
            result.add(binding);
        }
        return result;
    }

    /**
     * List of indices for the parameters, pointing either to the
     * anchor position or to the position in the created nodes list.
     * The latter are offset by the length of the anchor.
     */
    private final Supplier<List<Binding>> parBinding = lazy(this::computeParBinding);

    /**
     * Tests if this condition is ground and has a match to a given host graph.
     * Convenience method for <code>getMatchIter(host, null).hasNext()</code>
     */
    final public boolean hasMatch(HostGraph host) {
        return this.condition.isGround() && getMatch(host, null) != null;
    }

    @Override
    public Kind getKind() {
        return Kind.RULE;
    }

    /** Sets the action role of this rule. */
    public void setRole(Role role) {
        assert !isFixed();
        assert this.role == null && role != null;
        this.role = role;
    }

    @Override
    public Role getRole() {
        assert !this.role.isProperty() || isPropertyLike();
        return this.role;
    }

    private Role role;

    @Override
    public CheckPolicy getPolicy() {
        CheckPolicy result = getGrammarProperties().getRulePolicy().get(getQualName());
        if (result == null) {
            result = CheckPolicy.ERROR;
        }
        return result;
    }

    /** Indicates if this rule serves to test a property of a graph.
     * This is only the case if the rule is unmodifying, has no parameters (visible or hidden)
     * and has zero priority.
     */
    private boolean isPropertyLike() {
        boolean result = !isModifying() && getPriority() == 0 && getHiddenPars().isEmpty();
        if (result) {
            result = getSignature().stream().allMatch(v -> !v.isInOnly());
        }
        return result;
    }

    @Override
    public boolean isProperty() {
        return getRole().isProperty();
    }

    /**
     * Returns a match of this condition into a given host graph, given a
     * matching of the root graph.
     * @param host the graph in which the match is to be found
     * @param contextMap a matching of the root of this condition; may be
     *        <code>null</code> if the condition is ground.
     * @throws IllegalArgumentException if <code>patternMatch</code> is
     *         <code>null</code> and the condition is not ground, or if
     *         <code>patternMatch</code> is not compatible with the pattern
     *         graph
     */
    public Proof getMatch(HostGraph host, RuleToHostMap contextMap) {
        return traverseMatches(host, contextMap, Visitor.<Proof>newFinder(null));
    }

    /**
     * Returns the collection of all matches for a given host graph, given a
     * matching of the root context.
     * @param host the graph in which the match is to be found
     * @param contextMap a matching of the pattern of this condition; may be
     *        <code>null</code> if the condition is ground.
     * @throws IllegalArgumentException if <code>contextMap</code> is
     *         <code>null</code> and the condition is not ground, or if
     *         <code>contextMap</code> is not compatible with the root map
     */
    public Collection<Proof> getAllMatches(HostGraph host, RuleToHostMap contextMap) {
        List<Proof> result = new ArrayList<>();
        traverseMatches(host, contextMap, Visitor.newCollector(result));
        return result;
    }

    /**
     * Traverses the matches of this rule on a given host graph and for
     * a given context map, and calls the visitor's visit method on all
     * of them, until the first time the visitor returns
     * {@code false}.
     * @param host the graph in which the match is to be found
     * @param contextMap a matching of the pattern of this condition; may be
     *        <code>null</code> if the condition is ground.
     * @param visitor the visitor invoked for all the matches
     * @return the result of the visitor after the traversal
     * @throws IllegalArgumentException if <code>patternMatch</code> is
     *         <code>null</code> and the condition is not ground, or if
     *         <code>patternMatch</code> is not compatible with the pattern
     *         graph
     * @see Visitor#visit(Object)
     */
    public <R> R traverseMatches(final HostGraph host, RuleToHostMap contextMap,
                                 final Visitor<Proof,R> visitor) {
        assert isFixed();
        RuleToHostMap seedMap = contextMap == null
            ? host.getFactory().createRuleToHostMap()
            : contextMap;
        getMatcher(seedMap).traverse(host, contextMap, new Visitor<TreeMatch,R>() {
            @Override
            protected boolean process(TreeMatch match) {
                assert visitor.isContinue();
                if (isValidPatternMap(host, match.getPatternMap())) {
                    match.traverseProofs(visitor);
                }
                return visitor.isContinue();
            }
        });
        return visitor.getResult();
    }

    /**
     * Returns the match strategy for the target
     * pattern. First creates the strategy using
     * {@link #createMatcher(Anchor, boolean)} if that
     * has not been done.
     *
     * @param seedMap mapping from the seed elements to a host graph.
     *
     * @see #createMatcher(Anchor, boolean)
     */
    private SearchStrategy getMatcher(RuleToHostMap seedMap) {
        assert isTop();
        Matcher result;
        boolean simple = seedMap.getFactory().isSimple();
        Signature<UnitPar.RulePar> sig = getSignature();
        if (!sig.isEmpty()) {
            int sigSize = sig.size();
            BitSet initPars = new BitSet(sigSize);
            for (int i = 0; i < sigSize; i++) {
                // set initPars if the seed map contains a value
                // for this parameter
                initPars.set(i, seedMap.nodeMap().containsKey(sig.getPar(i).getNode()));
            }
            result = this.matcherMap.get(initPars);
            if (result == null) {
                Anchor seed = new Anchor(seedMap.nodeMap().keySet());
                this.matcherMap.put(initPars, result = createMatcher(seed, simple));
            }
        } else {
            result = getMatcher(simple);
        }
        return result;
    }

    /**
     * Lazily creates and returns a matcher for rule events of this rule. The
     * matcher will try to extend anchor maps to full matches. This is in
     * contrast with the normal (condition) matcher, which is based on the
     * images of the root map.
     */
    public Matcher getEventMatcher(boolean simple) {
        return simple
            ? this.simpleEventMatcher.get()
            : this.multiEeventMatcher.get();
    }

    /**
     * Callback method to create a match strategy. Typically invoked once, at
     * the first invocation of {@link #getMatcher(boolean)}. This implementation
     * retrieves its value from {@link #getMatcherFactory(boolean)}.
     * @param seed the pre-matched subgraph
     * @param simple indicates if the host graphs are simple or multi-graphs
     */
    private Matcher createMatcher(Anchor seed, boolean simple) {
        testFixed(true);
        return getMatcherFactory(simple).createMatcher(getCondition(), seed);
    }

    /** The matcher for events of this rule. */
    private final Supplier<Matcher> simpleEventMatcher
        = lazy(() -> createMatcher(getAnchor(), true));

    /** The matcher for events of this rule. */
    private final Supplier<Matcher> multiEeventMatcher
        = lazy(() -> createMatcher(getAnchor(), false));

    /**
     * Mapping from sets of initialised parameters to match strategies.
     */
    private final Map<BitSet,Matcher> matcherMap = new HashMap<>();

    /**
     * Returns a (precomputed) match strategy for the target
     * pattern, based on the rule seed.
     * @param simple indicates if the host graphs are simple or multi-graphs
     * @see #createMatcher(Anchor, boolean)
     */
    public Matcher getMatcher(boolean simple) {
        return simple
            ? this.simpleSeedMatcher.get()
            : this.multiSeedMatcher.get();
    }

    /**
     * The fixed simple matching strategy for this graph rule. Initially
     * <code>null</code>; set by {@link #getMatcher(boolean)} upon its first
     * invocation.
     */
    private final Supplier<Matcher> simpleSeedMatcher = lazy(() -> createMatcher(getSeed(), true));

    /**
     * The fixed multi-graph matching strategy for this graph rule. Initially
     * <code>null</code>; set by {@link #getMatcher(boolean)} upon its first
     * invocation.
     */
    private final Supplier<Matcher> multiSeedMatcher = lazy(() -> createMatcher(getSeed(), false));

    /** Returns a matcher factory, tuned to the properties of this rule. */
    private MatcherFactory getMatcherFactory(boolean simple) {
        return MatcherFactory.instance(simple);
    }

    /**
     * Tests whether a given match map satisfies the additional constraints
     * imposed by this rule.
     * @param host the graph to be matched
     * @param matchMap the proposed map from {@link #lhs()} to
     *        <code>host</code>
     * @return <code>true</code> if <code>matchMap</code> satisfies the
     *         constraints imposed by the rule (if any).
     */
    public boolean isValidPatternMap(HostGraph host, RuleToHostMap matchMap) {
        boolean result = true;
        if (isCheckDangling()) {
            result = satisfiesDangling(host, matchMap);
        }
        return result;
    }

    /**
     * Tests if a given (proposed) match into a host graph leaves dangling
     * edges.
     */
    private boolean satisfiesDangling(HostGraph host, RuleToHostMap match) {
        boolean result = true;
        for (RuleNode eraserNode : getEraserNodes()) {
            HostNode erasedNode = match.getNode(eraserNode);
            assert erasedNode != null;
            Set<HostEdge> danglingEdges = host
                .edgeSet(erasedNode)
                .stream()
                .filter(e -> !(e.target() instanceof ValueNode))
                .collect(Collectors.toSet());
            lhs()
                .edgeSet(eraserNode)
                .stream()
                .map(e -> match.getEdge(e))
                .filter(e -> !(e == null || e.target() instanceof ValueNode))
                .forEach(e -> danglingEdges.remove(e));
            if (!danglingEdges.isEmpty()) {
                result = false;
                break;
            }
        }
        return result;
    }

    /**
     * Adds entries to the colour map for this rule.
     * The changes are eventually pushed to the top rule in the hierarchy.
     */
    public void addColorMap(Map<RuleNode,Color> colorMap) {
        this.colorMap.putAll(colorMap);
    }

    /** Returns a mapping from RHS nodes to colours for those nodes. */
    public Map<RuleNode,Color> getColorMap() {
        return this.colorMap;
    }

    /** Mapping from rule nodes to explicitly declared colours. */
    private final Map<RuleNode,Color> colorMap = new HashMap<>();

    /** Returns the anchor of the rule. */
    public Anchor getAnchor() {
        return this.anchor.get();
    }

    /** The rule anchor. */
    private final Supplier<Anchor> anchor = lazy(() -> anchorFactory.newAnchor(this));

    /** Returns the seed of the rule. */
    public Anchor getSeed() {
        return this.seed.get();
    }

    /** The rule seed. */
    private final Supplier<Anchor> seed = lazy(() -> new Anchor(getRoot()));

    @Override
    public String toString() {
        StringBuilder res
            = new StringBuilder(String.format("Rule %s; anchor %s%n", getQualName(), getAnchor()));
        res.append(getCondition().toString("    "));
        return res.toString();
    }

    /**
     * Compares two actions on the basis of their names.
     */
    @Override
    public int compareTo(Action other) {
        return getQualName().compareTo(other.getQualName());
    }

    // ------------------- commands --------------------------

    /**
     * In addition to calling the super method, adds implicit NACs as dictated
     * by {@link GrammarProperties#isCheckCreatorEdges()} and
     * {@link GrammarProperties#isRhsAsNac()}.
     */
    @Override
    public boolean setFixed() throws FormatException {
        boolean result = !isFixed();
        if (result && !this.fixing) {
            try {
                this.fixing = true;
                getCondition().setFixed();
                if (PRINT && isTop()) {
                    System.out.println(toString());
                }
                // check if there is an oracle if one is needed
                boolean hasAskPars = getSignature().stream().anyMatch(v -> v.isAsk());
                if (hasAskPars && !getGrammarProperties().hasValueOracle()) {
                    throw new FormatException(
                        "Rule has on-demand parameter but no oracle has been installed");
                }
                // push the colour map to the top rule
                Rule parent = this.parent;
                while (parent != null && parent != this) {
                    parent.getColorMap().putAll(getColorMap());
                    parent = parent == parent.parent
                        ? null
                        : parent.parent;
                }
                this.fixed = true;
            } finally {
                this.fixing = false;
            }
        }
        return result;
    }

    @Override
    public boolean isFixed() {
        return this.fixing || this.fixed;
    }

    /** Flag indicating whether the rule has been fixed. */
    private boolean fixed;

    /** Flag indicating whether the rule is currently in the process of fixing. */
    private boolean fixing;

    /**
     * Checks this rule for compatibility with a chosen algebra.
     * Called before starting an exploration.
     */
    public void checkCombatible(AlgebraFamily family) throws FormatException {
        if (!family.supportsSymbolic()) {
            getCondition().checkResolution();
        }
    }

    /** Returns an array of nodes isolated in the left hand side. */
    final public RuleNode[] getIsolatedNodes() {
        return this.isolatedNodes.get();
    }

    /** Computes the array of nodes isolated in the left hand side. */
    private RuleNode[] computeIsolatedNodes() {
        testFixed(true);
        Set<RuleNode> result = new HashSet<>();
        for (RuleNode node : lhs().nodeSet()) {
            if (lhs().edgeSet(node).isEmpty()) {
                result.add(node);
            }
        }
        result.removeAll(this.condition.getRoot().nodeSet());
        return result.toArray(new RuleNode[result.size()]);
    }

    /**
     * The LHS nodes that do not have any incident edges in the LHS.
     */
    private final Supplier<RuleNode[]> isolatedNodes = lazy(this::computeIsolatedNodes);

    /**
     * Indicates if application of this rule actually changes the host graph. If
     * <code>false</code>, this means the rule is essentially a graph
     * condition.
     */
    public boolean isModifying() {
        return this.modifying.get();
    }

    /**
     * Computes if the rule is modifying or not.
     */
    private boolean computeIsModifying() {
        boolean result = getEraserEdges().length > 0 || getEraserNodes().length > 0 || hasMergers()
            || hasNodeCreators() || hasEdgeCreators() || !getColorMap().isEmpty();
        if (!result) {
            for (Rule subRule : getSubRules()) {
                result = subRule.isModifying();
                if (result) {
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Indicates if this rule makes changes to a graph at all.
     */
    private final Supplier<Boolean> modifying = lazy(this::computeIsModifying);

    /**
     * Returns an array of LHS nodes that are endpoints of eraser edges, creator
     * edges or mergers.
     */
    public final Set<RuleNode> getModifierEnds() {
        return this.modifierEnds.get();
    }

    /**
     * Computes the array of LHS nodes that are endpoints of eraser edges,
     * creator edges or mergers.
     */
    private Set<RuleNode> computeModifierEnds() {
        Set<RuleNode> result = new HashSet<>();
        // add the end nodes of eraser edges
        for (RuleEdge eraserEdge : getEraserEdges()) {
            result.add(eraserEdge.source());
            result.add(eraserEdge.target());
        }
        // add the end nodes of creator and merger edges
        for (RuleEdge rhsEdge : rhs().edgeSet()) {
            if (!lhs().containsEdge(rhsEdge)) {
                RuleNode source = rhsEdge.source();
                if (lhs().containsNode(source)) {
                    result.add(source);
                }
                RuleNode target = rhsEdge.target();
                if (lhs().containsNode(target)) {
                    result.add(target);
                }
            }
        }
        return result;
    }

    /**
     * The lhs nodes that are end points of eraser or creator edges or mergers,
     * either in this rule or one of its sub-rules.
     */
    private final Supplier<Set<RuleNode>> modifierEnds = lazy(this::computeModifierEnds);

    /**
     * Returns an array of variables that are used in erasers or creators.
     */
    final Set<LabelVar> getModifierVars() {
        return this.modifierVars.get();
    }

    /**
     * Computes the set of variables occurring in creators or erasers.
     */
    private Set<LabelVar> computeModifierVars() {
        Set<LabelVar> result = new HashSet<>();
        // add the variables of creators
        for (RuleEdge edge : getCreatorGraph().edgeSet()) {
            result.addAll(edge.label().allVarSet());
        }
        for (RuleNode node : getCreatorGraph().nodeSet()) {
            for (TypeGuard guard : node.getTypeGuards()) {
                result.add(guard.getVar());
            }
        }
        // add the variables of erasers
        for (RuleNode eraser : getEraserNodes()) {
            for (TypeGuard guard : eraser.getTypeGuards()) {
                result.add(guard.getVar());
            }
        }
        for (RuleEdge eraser : getEraserEdges()) {
            result.addAll(eraser.label().allVarSet());
        }
        return result;
    }

    /**
     * The lhs variables that occur in eraser or creator edges, either in this rule or
     * one of its sub-rules.
     */
    private final Supplier<Set<LabelVar>> modifierVars = lazy(this::computeModifierVars);

    /**
     * Indicates if the rule creates any nodes.
     */
    public boolean hasNodeCreators() {
        return this.hasNodeCreators.get();
    }

    private boolean computeHasNodeCreators() {
        boolean result = getCreatorNodes().length > 0;
        if (!result) {
            for (Rule subRule : getSubRules()) {
                result = subRule.hasNodeCreators();
                if (result) {
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Indicates if this rule has creator nodes.
     */
    private final Supplier<Boolean> hasNodeCreators = lazy(this::computeHasNodeCreators);

    /**
     * Returns the RHS nodes that are not images of an LHS node.
     */
    final public RuleNode[] getCreatorNodes() {
        return this.creatorNodes.get();
    }

    /**
     * Computes the creator (i.e., RHS-only) nodes.
     */
    private RuleNode[] computeCreatorNodes() {
        Set<RuleNode> result = new HashSet<>(rhs().nodeSet());
        result.removeAll(lhs().nodeSet());
        Rule parent = getParent();
        if (parent != null && parent != this) {
            result.removeAll(parent.rhs().nodeSet());
        }
        return result.toArray(new RuleNode[result.size()]);
    }

    /**
     * The rhs nodes that are not ruleMorph images
     * @invariant creatorNodes \subseteq rhs.nodeSet()
     */
    private final Supplier<RuleNode[]> creatorNodes = lazy(this::computeCreatorNodes);

    /**
     * Indicates if the rule creates any edges.
     */
    public boolean hasEdgeCreators() {
        return this.hasEdgeCreators.get();
    }

    private boolean computeHasEdgeCreators() {
        boolean result = getCreatorEdges().length > 0;
        if (!result) {
            for (Rule subRule : getSubRules()) {
                result = subRule.hasEdgeCreators();
                if (result) {
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Indicates if this rule has creator edges.
     */
    private final Supplier<Boolean> hasEdgeCreators = lazy(this::computeHasEdgeCreators);

    /**
     * Returns the RHS edges that are not images of an LHS edge.
     */
    final public RuleEdge[] getCreatorEdges() {
        return this.creatorEdges.get();
    }

    /**
     * Computes the creator (i.e., RHS-only) edges.
     */
    private RuleEdge[] computeCreatorEdges() {
        Set<RuleEdge> result = new HashSet<>(rhs().edgeSet());
        result.removeAll(lhs().edgeSet());
        Rule parent = getParent();
        if (parent != null && parent != this) {
            result.removeAll(parent.rhs().edgeSet());
        }
        result.removeAll(getLhsMergers());
        result.removeAll(getRhsMergers());
        return result.toArray(new RuleEdge[result.size()]);
    }

    /**
     * The rhs edges that are not ruleMorph images
     */
    private final Supplier<RuleEdge[]> creatorEdges = lazy(this::computeCreatorEdges);

    /**
     * Returns the creator edges between reader nodes.
     */
    final public RuleEdge[] getSimpleCreatorEdges() {
        return this.simpleCreatorEdges.get();
    }

    /**
     * Computes the creator edges between reader nodes.
     */
    private RuleEdge[] computeSimpleCreatorEdges() {
        List<RuleEdge> result = new ArrayList<>();
        Set<RuleNode> nonCreatorNodes = getCreatorEnds();
        // iterate over all creator edges
        for (RuleEdge edge : getCreatorEdges()) {
            // determine if this edge is simple
            if (nonCreatorNodes.contains(edge.source())
                && nonCreatorNodes.contains(edge.target())) {
                result.add(edge);
            }
        }
        return result.toArray(new RuleEdge[result.size()]);
    }

    /**
     * The rhs edges that are not ruleMorph images but with all ends morphism
     * images
     */
    private final Supplier<RuleEdge[]> simpleCreatorEdges = lazy(this::computeSimpleCreatorEdges);

    /**
     * Returns the creator edges that have at least one creator end.
     */
    public final Set<RuleEdge> getComplexCreatorEdges() {
        return this.complexCreatorEdges.get();
    }

    /**
     * Computes the creator edges that have at least one creator end.
     */
    private Set<RuleEdge> computeComplexCreatorEdges() {
        Set<RuleEdge> result = new HashSet<>(Arrays.asList(getCreatorEdges()));
        result.removeAll(Arrays.asList(getSimpleCreatorEdges()));
        return result;
    }

    /**
     * The rhs edges with at least one end not a morphism image
     */
    private final Supplier<Set<RuleEdge>> complexCreatorEdges
        = lazy(this::computeComplexCreatorEdges);

    /**
     * Returns the variables that occur in creator edges.
     * @see #getCreatorEdges()
     */
    public final LabelVar[] getCreatorVars() {
        return this.creatorVars.get();
    }

    /**
     * Computes the variables occurring in RHS nodes and edges.
     */
    private LabelVar[] computeCreatorVars() {
        var result = new ArrayList<LabelVar>();
        Arrays
            .stream(getCreatorEdges())
            .flatMap(edge -> edge.getTypeGuards().stream())
            .map(g -> g.getVar())
            .forEach(v -> result.add(v));
        Arrays
            .stream(getCreatorNodes())
            .flatMap(node -> node.getTypeGuards().stream())
            .map(g -> g.getVar())
            .forEach(v -> result.add(v));
        return result.toArray(new LabelVar[result.size()]);
    }

    /**
     * Variables occurring in the rhsOnlyEdges
     */
    private final Supplier<LabelVar[]> creatorVars = lazy(this::computeCreatorVars);

    /**
     * Returns a sub-graph of the RHS consisting of the creator nodes and the
     * creator edges with their endpoints.
     */
    final RuleGraph getCreatorGraph() {
        return this.creatorGraph.get();
    }

    /**
     * Computes a creator graph, consisting of the creator nodes together with
     * the creator edges and their endpoints.
     */
    private RuleGraph computeCreatorGraph() {
        RuleGraph result = rhs().newGraph(getQualName() + "(creators)");
        result.addNodeSet(Arrays.asList(getCreatorNodes()));
        for (RuleEdge e : getCreatorEdges()) {
            result.addEdgeContext(e);
        }
        return result;
    }

    /**
     * A sub-graph of the production rule's right hand side, consisting only of
     * the fresh nodes and edges.
     */
    private final Supplier<RuleGraph> creatorGraph = lazy(this::computeCreatorGraph);

    /**
     * Returns the RHS nodes that are not themselves creator nodes but are
     * the ends of creator edges.
     */
    public final Set<RuleNode> getCreatorEnds() {
        return this.creatorEnds.get();
    }

    /**
     * A map from the nodes of <tt>rhsOnlyGraph</tt> to <tt>lhs</tt>, which is
     * the restriction of the inverse of <tt>ruleMorph</tt> to
     * <tt>rhsOnlyGraph</tt>.
     */
    private final Supplier<Set<RuleNode>> creatorEnds = lazy(() -> {
        var result = new HashSet<>(getCreatorGraph().nodeSet());
        result.retainAll(lhs().nodeSet());
        return result;
    });

    /**
     * Indicates if the rule creates any nodes.
     */
    public boolean hasNodeErasers() {
        return this.hasNodeErasers.get();
    }

    private boolean computeHasNodeErasers() {
        boolean result = getEraserNodes().length > 0;
        if (!result) {
            for (Rule subRule : getSubRules()) {
                result = subRule.hasNodeErasers();
                if (result) {
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Indicates if this rule has eraser nodes.
     */
    private final Supplier<Boolean> hasNodeErasers = lazy(this::computeHasNodeErasers);

    /**
     * Returns the LHS nodes that are not mapped to the RHS.
     */
    public final DefaultRuleNode[] getEraserNodes() {
        return this.eraserNodes.get();
    }

    /**
     * Computes the eraser (i.e., lhs-only) nodes.
     */
    private DefaultRuleNode[] computeEraserNodes() {
        //testFixed(true);
        Set<RuleNode> result = new LinkedHashSet<>(lhs().nodeSet());
        result.removeAll(rhs().nodeSet());
        return result.toArray(new DefaultRuleNode[result.size()]);
    }

    /**
     * The lhs nodes that are not ruleMorph keys
     * @invariant lhsOnlyNodes \subseteq lhs.nodeSet()
     */
    private final Supplier<DefaultRuleNode[]> eraserNodes = lazy(this::computeEraserNodes);

    /**
     * Indicates if the rule creates any edges.
     */
    public boolean hasEdgeErasers() {
        return this.hasEdgeErasers.get();
    }

    private boolean computeHasEdgeErasers() {
        boolean result = getEraserEdges().length > 0;
        if (!result) {
            for (Rule subRule : getSubRules()) {
                result = subRule.hasEdgeErasers();
                if (result) {
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Indicates if this rule has eraser edges.
     */
    private final Supplier<Boolean> hasEdgeErasers = lazy(this::computeHasEdgeErasers);

    /** Returns the eraser (i.e., LHS-only) edges. */
    public final RuleEdge[] getEraserEdges() {
        return this.eraserEdges.get();
    }

    /**
     * Computes the eraser (i.e., LHS-only) edges.
     * Note: this does not include the incident edges of the eraser nodes.
     */
    private RuleEdge[] computeEraserEdges() {
        testFixed(true);
        Set<RuleEdge> result = new LinkedHashSet<>(lhs().edgeSet());
        result.removeAll(rhs().edgeSet());
        // also remove the incident edges of the lhs-only nodes
        for (RuleNode eraserNode : getEraserNodes()) {
            result.removeAll(lhs().edgeSet(eraserNode));
        }
        return result.toArray(new RuleEdge[result.size()]);
    }

    /**
     * The lhs edges that are not ruleMorph keys
     * @invariant lhsOnlyEdges \subseteq lhs.edgeSet()
     */
    private final Supplier<RuleEdge[]> eraserEdges = lazy(this::computeEraserEdges);

    /** Returns the eraser edges that are not themselves anchors. */
    public final RuleEdge[] getEraserNonAnchorEdges() {
        return this.eraserNonAnchorEdges.get();
    }

    /**
     * Computes the array of creator edges that are not themselves anchors.
     */
    private RuleEdge[] computeEraserNonAnchorEdges() {
        Set<RuleEdge> result = new LinkedHashSet<>(Arrays.asList(getEraserEdges()));
        result.removeAll(getAnchor().edgeSet());
        return result.toArray(new RuleEdge[result.size()]);
    }

    /**
     * The lhs edges that are not ruleMorph keys and are not anchors
     */
    private final Supplier<RuleEdge[]> eraserNonAnchorEdges
        = lazy(this::computeEraserNonAnchorEdges);

    /**
     * Indicates if this rule has mergers.
     */
    final public boolean hasMergers() {
        return this.hasMergers.get();
    }

    /**
     * Computes if the rule has mergers or not.
     */
    private boolean computeHasMergers() {
        boolean result = !getLhsMergeMap().isEmpty() || !getRhsMergeMap().isEmpty();
        if (!result) {
            for (Rule subRule : getSubRules()) {
                result = subRule.hasMergers();
                if (result) {
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Indicates if this rule has node mergers.
     */
    private final Supplier<Boolean> hasMergers = lazy(this::computeHasMergers);

    /** Returns the set of merger edges in the RHS of which both ends are in the LHS. */
    public final Set<RuleEdge> getLhsMergers() {
        return getMergers().lhsMergers();
    }

    /**
     * Returns the set of merger edges in the RHS of which at least one end
     * is a creator node.
     */
    public final Set<RuleEdge> getRhsMergers() {
        return getMergers().rhsMergers();
    }

    /**
     * Returns a map from each LHS node that is merged with
     * another to the LHS node it is merged with.
     */
    public final Map<RuleNode,RuleNode> getLhsMergeMap() {
        return getMergers().lhsMergeMap();
    }

    /**
     * Returns a map from merged nodes to their merge targets,
     * at least one of which is a creator node.
     */
    public final Map<RuleNode,RuleNode> getRhsMergeMap() {
        return getMergers().rhsMergeMap();
    }

    private Mergers getMergers() {
        return this.mergers.get();
    }

    private final Supplier<Mergers> mergers = lazy(() -> new Mergers(this));

    /** Merger information.
     * @param lhsMergers he set of merger edges in the RHS of which both ends are in the LHS
     * @param rhsMergers the set of merger edges in the RHS of which at least one end
     * is a creator node.
     * @param lhsMergeMap a mapping from merge sources to merge targets outside the LHS.
     * @param rhsMergeMap a map from merged nodes to their merge targets,
     * at least one of which is a creator node
     */
    static private record Mergers(Set<RuleEdge> lhsMergers, Set<RuleEdge> rhsMergers,
        Map<RuleNode,RuleNode> lhsMergeMap, Map<RuleNode,RuleNode> rhsMergeMap) {
        Mergers(Rule rule) {
            this(new HashSet<>(), new HashSet<>(), new HashMap<>(), new HashMap<>());
            init(rule);
        }

        private void init(Rule rule) {
            var lhs = rule.lhs();
            var rhs = rule.rhs();
            for (RuleEdge rhsEdge : rhs.edgeSet()) {
                if (rhsEdge.label().isEmpty() && !lhs.containsEdge(rhsEdge)) {
                    RuleNode source = rhsEdge.source();
                    RuleNode target = rhsEdge.target();
                    if (lhs.containsNode(source) && lhs.containsNode(target)) {
                        this.lhsMergeMap.put(source, target);
                        this.lhsMergers.add(rhsEdge);
                    } else {
                        this.rhsMergeMap.put(source, target);
                        this.rhsMergers.add(rhsEdge);
                    }
                }
            }
            closeMergeMap(this.lhsMergeMap);
            closeMergeMap(this.rhsMergeMap);
        }

        private void closeMergeMap(Map<RuleNode,RuleNode> mergeMap) {
            // transitively close the map
            // because we don't expect long chains of mergers,
            // we can sacrifice efficiency for brevity
            for (Map.Entry<RuleNode,RuleNode> mergeEntry : mergeMap.entrySet()) {
                RuleNode oldValue = mergeEntry.getValue();
                RuleNode newValue = oldValue;
                while (mergeMap.containsKey(newValue)) {
                    newValue = mergeMap.get(newValue);
                }
                mergeEntry.setValue(newValue);
            }
        }
    }

    /**
     * Indicates if this rule is part of a recipe.
     * @see #isPartial()
     */
    @Override
    public boolean isPartial() {
        return this.partial;
    }

    /**
     * Sets the rule to partial.
     * @see #isPartial()
     */
    public void setPartial() {
        this.partial = true;
    }

    /** Flag indicating that this rule is part of a recipe. */
    private boolean partial;

    /** Returns the current anchor factory for all rules. */
    public static AnchorFactory getAnchorFactory() {
        return anchorFactory;
    }

    /**
     * Sets the anchor factory for all rules. Only affects rules created from
     * this moment on.
     */
    public static void setAnchorFactory(AnchorFactory anchorFactory) {
        Rule.anchorFactory = anchorFactory;
    }

    /**
     * Returns the total time doing matching-related computations. This includes
     * time spent in certificate calculation.
     */
    static public long getMatchingTime() {
        return PlanSearchStrategy.searchFindReporter.getTotalTime();
    }

    /**
     * The factory used for creating rule anchors.
     */
    private static AnchorFactory anchorFactory = DefaultAnchorFactory.instance();
    /** Debug flag for the constructor. */
    private static final boolean PRINT = false;
}
