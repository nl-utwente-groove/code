// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente

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
 * $Id: SPORule.java,v 1.53 2008-03-05 16:52:12 rensink Exp $
 */
package groove.trans;

import groove.control.CtrlPar;
import groove.control.CtrlPar.Var;
import groove.control.CtrlType;
import groove.control.CtrlVar;
import groove.graph.GraphProperties;
import groove.match.Matcher;
import groove.match.MatcherFactory;
import groove.match.SearchStrategy;
import groove.match.TreeMatch;
import groove.match.plan.PlanSearchStrategy;
import groove.rel.LabelVar;
import groove.rel.VarSupport;
import groove.util.Fixable;
import groove.util.Groove;
import groove.util.Visitor;
import groove.view.FormatException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Type of a production rule. The rule essentially consists of a left hand
 * side graph, a right hand side graph, a rule morphism and a set of NACs. [AR:
 * In the future the interface might provide less functionality; instead there
 * will be a sub-interface GraphRule or similar. ]
 * @author Arend Rensink
 * @version $Revision$
 */
public class Rule implements Fixable, Comparable<Rule> {
    /**
     * @param condition the application condition of this rule
     * @param rhs the right hand side graph of the rule
     * @param morphism the mapping from the LHS to the RHS
     * @param ruleProperties the rule properties
     */
    public Rule(Condition condition, RuleGraph rhs, RuleGraphMorphism morphism,
            GraphProperties ruleProperties) {
        this(condition, rhs, morphism, null, ruleProperties);
    }

    /**
     * Constructs a rule that is a sub-condition of another rule.
     * @param condition the application condition of this rule
     * @param rhs the right hand side graph of the rule
     * @param morphism the mapping from the LHS to the RHS
     * @param coRootMap map of creator nodes in the parent rule to creator nodes
     *        of this rule
     * @param ruleProperties the rule properties
     */
    public Rule(Condition condition, RuleGraph rhs, RuleGraphMorphism morphism,
            RuleGraphMorphism coRootMap, GraphProperties ruleProperties) {
        this.condition = condition;
        this.coRootMap =
            coRootMap == null ? new RuleGraphMorphism() : coRootMap;
        this.lhs = condition.getPattern();
        this.rhs = rhs;
        this.morphism = morphism;
        this.ruleProperties = ruleProperties;
        assert coRootMap == null
            || rhs().nodeSet().containsAll(coRootMap.nodeMap().values()) : String.format(
            "RHS nodes %s do not contain all co-root values %s",
            rhs().nodeSet(), coRootMap.nodeMap().values());
    }

    /** Returns the condition with which this rule is associated. */
    public Condition getCondition() {
        return this.condition;
    }

    /** Returns the name of this rule (which equals the name of the associated condition). */
    public String getName() {
        return getCondition().getName();
    }

    /** Returns the system properties. */
    public SystemProperties getSystemProperties() {
        return getCondition().getSystemProperties();
    }

    /**
     * Returns the priority of this object. A higher number means higher
     * priority, with {@link #DEFAULT_PRIORITY} the lowest.
     */
    public int getPriority() {
        return this.ruleProperties.getPriority();
    }

    /** Sets the priority of this rule. */
    public void setPriority(int priority) {
        testFixed(false);
        this.ruleProperties.setPriority(priority);
    }

    /**
     * Indicates if this rule has a confluency property. If this method returns
     * <code>true</code>, this means the rule can be applied only once, to an
     * arbitrary match.
     */
    public boolean isConfluent() {
        return this.ruleProperties.isConfluent();
    }

    /** Sets the confluence of this rule. */
    public void setConfluent(boolean confluent) {
        testFixed(false);
        this.ruleProperties.setConfluent(confluent);
    }

    /**
     * Sets the parent rule of this rule, together with the nesting level and
     * the co-root map.
     * @param parent the parent rule for this rule
     * @param level nesting level of this rule within the condition tree
     */
    public void setParent(Rule parent, int[] level) {
        testFixed(false);
        assert this.coRootMap != null : String.format(
            "Sub-rule at level %s must have a non-trivial co-root map",
            Arrays.toString(level));
        if (parent != null) {
            assert parent.rhs().nodeSet().containsAll(
                this.coRootMap.nodeMap().keySet()) : String.format(
                "Rule '%s': Parent nodes %s do not contain all co-roots %s",
                getName(), parent.rhs().nodeSet(),
                this.coRootMap.nodeMap().keySet());
        }
        this.parent = parent;
    }

    /** 
     * Returns the label to be used in the LTS when this rule is applied.
     * Defaults to the rule name, if the property is undefined.  
     */
    public String getTransitionLabel() {
        String result = this.ruleProperties.getTransitionLabel();
        if (result == null) {
            result = getName().toString();
        }
        return result;
    }

    /**
     * Sets the transition label for this rule.
     * @see #getTransitionLabel()
     */
    public void setTransitionLabel(String label) {
        this.ruleProperties.setTransitionLabel(label);
    }

    /** 
     * Returns a format string for the standard output.
     * Whenever a transition with this rule is added to a GTS, a 
     * corresponding string is sent to the standard output.
     */
    public String getFormatString() {
        return this.ruleProperties.getFormatString();
    }

    /** Sets the format string for this rule. 
     * @see #getFormatString()
     */
    public void setFormatString(String format) {
        this.ruleProperties.setFormatString(format);
    }

    /** Sets the dangling-edge check for matches of this rule. */
    public void setCheckDangling(boolean checkDangling) {
        this.checkDangling = checkDangling;
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

    /** Indicates if this is a top-level rule. */
    public boolean isTop() {
        return getParent() == this;
    }

    /** Returns the top rule of the hierarchy in which this rule is nested. */
    public Rule getTop() {
        if (isTop()) {
            return this;
        } else {
            return getParent().getTop();
        }
    }

    //    @Override
    Set<RuleNode> computeInputNodes() {
        Set<RuleNode> result = null;
        // if this is a top-level rule, the (only) input nodes
        // are the input-only parameter nodes
        if (isTop()) {
            result = new HashSet<RuleNode>();
            for (Var var : getSignature()) {
                if (var.isInOnly()) {
                    result.add(var.getRuleNode());
                }
            }
            //        } else {
            //            result = super.computeInputNodes();
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
        if (this.subRules == null) {
            this.subRules = new TreeSet<Rule>();
            for (Condition condition : getCondition().getSubConditions()) {
                for (Condition subCondition : condition.getSubConditions()) {
                    if (subCondition.hasRule()) {
                        this.subRules.add(subCondition.getRule());
                    }
                }
            }
        }
        return this.subRules;
    }

    /**
     * Sets the parameters of this rule. The rule can have numbered and hidden
     * parameters. Numbered parameters are divided into input (LHS) and output
     * (RHS-only) parameters, and are visible on the transition label.
     * @param sig the signature of the rule, i.e., the list of (visible) parameters
     * @param hiddenPars the set of hidden (i.e., unnumbered) parameter nodes
     */
    public void setSignature(List<CtrlPar.Var> sig, Set<RuleNode> hiddenPars) {
        assert !isFixed();
        this.sig = sig;
        this.hiddenPars = hiddenPars;
        List<CtrlPar.Var> derivedSig = new ArrayList<CtrlPar.Var>();
        for (int i = 0; i < sig.size(); i++) {
            // add the LHS parameters to the root graph
            RuleNode parNode = sig.get(i).getRuleNode();
            if (this.lhs.containsNode(parNode)) {
                this.condition.getRoot().addNode(parNode);
            }
            String parName = "arg" + i;
            String parTypeName = sig.get(i).getType().toString();
            CtrlType parType = CtrlType.getType(parTypeName);
            CtrlVar var = new CtrlVar(parName, parType);
            CtrlPar.Var par;
            boolean inOnly = sig.get(i).isInOnly();
            boolean outOnly = sig.get(i).isOutOnly();
            if (!inOnly && !outOnly) {
                par = new CtrlPar.Var(var);
            } else {
                par = new CtrlPar.Var(var, inOnly);
            }
            derivedSig.add(par);
        }
        assert derivedSig.equals(sig) : String.format(
            "Declared signature %s differs from derived signature %s", sig,
            derivedSig);
    }

    /** Returns the signature of the rule. */
    public List<CtrlPar.Var> getSignature() {
        assert isFixed();
        if (this.sig == null) {
            this.sig = Collections.emptyList();
        }
        return this.sig;
    }

    /** Returns, for a given index in the signature,
     * the corresponding index in the anchor 
     * or in the created nodes (if the parameter is a creator).
     * The latter are offset by the anchor node count.
     */
    public int getParBinding(int i) {
        if (this.parBinding == null) {
            this.parBinding = computeParBinding();
        }
        return this.parBinding[i];
    }

    /**
     * Creates the connection between parameter positions and
     * anchor respectively created node indices.
     * @see #getParBinding(int)
     */
    private int[] computeParBinding() {
        int[] result = new int[this.sig.size()];
        int anchorSize = getAnchorNodes().length;
        for (int i = 0; i < this.sig.size(); i++) {
            CtrlPar.Var par = this.sig.get(i);
            int binding;
            RuleNode ruleNode = par.getRuleNode();
            if (par.isCreator()) {
                // look up the node in the creator nodes
                binding =
                    Arrays.asList(getCreatorNodes()).indexOf(ruleNode)
                        + anchorSize;
                assert binding >= anchorSize;
            } else {
                // look up the node in the anchor
                binding = Arrays.asList(getAnchorNodes()).indexOf(ruleNode);
                assert binding >= 0 : String.format(
                    "Node %s not in anchors %s", ruleNode,
                    Arrays.toString(getAnchorNodes()));
            }
            result[i] = binding;
        }
        return result;
    }

    /**
     * Returns the set of hidden (i.e., unnumbered) parameter nodes of this
     * rule.
     */
    Set<RuleNode> getHiddenPars() {
        return this.hiddenPars;
    }

    /**
     * Tests if this condition is ground and has a match to a given host graph.
     * Convenience method for <code>getMatchIter(host, null).hasNext()</code>
     */
    final public boolean hasMatch(HostGraph host) {
        return this.condition.isGround() && getMatch(host, null) != null;
    }

    /**
     * Reconstructs a proof for this rule's condition from a rule event.
     * This is only invoked on simple rules, i.e., without subrules.
     */
    public Proof getEventMatch(BasicEvent event, final HostGraph host) {
        Proof result =
            getEventMatcher().traverse(host, event.getAnchorMap(),
                new Visitor<TreeMatch,Proof>() {
                    @Override
                    protected boolean process(TreeMatch match) {
                        boolean result =
                            isValidPatternMap(host, match.getPatternMap());
                        if (result) {
                            // this is a simple event, so there are no subrules;
                            // the match consists only of the pattern map
                            setResult(createProof(match.getPatternMap()));
                        }
                        return result;
                    }
                });
        return result;
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
    public Collection<Proof> getAllMatches(HostGraph host,
            RuleToHostMap contextMap) {
        List<Proof> result = new ArrayList<Proof>();
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
    public <R> R traverseMatches(final HostGraph host,
            RuleToHostMap contextMap, final Visitor<Proof,R> visitor) {
        assert isFixed();
        RuleToHostMap seedMap =
            contextMap == null ? host.getFactory().createRuleToHostMap()
                    : contextMap;
        getMatcher(seedMap).traverse(host, contextMap,
            new Visitor<TreeMatch,R>() {
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
     * Callback factory method to create a match on the basis of a mapping of
     * this condition's target.
     * 
     * @param patternMap the mapping, presumably of the elements of
     *        {@link #lhs()} into some host graph
     * @return a match constructed on the basis of <code>map</code>
     */
    private Proof createProof(RuleToHostMap patternMap) {
        return new Proof(this.condition, patternMap);
    }

    /**
     * Lazily creates and returns a matcher for rule events of this rule. The
     * matcher will try to extend anchor maps to full matches. This is in 
     * contrast with the normal (condition) matcher, which is based on the
     * images of the root map.
     */
    public Matcher getEventMatcher() {
        if (this.eventMatcher == null) {
            // add the anchor edge ends to the seed nodes
            List<RuleNode> anchorNodes =
                new ArrayList<RuleNode>(Arrays.asList(getAnchorNodes()));
            List<RuleEdge> anchorEdges = Arrays.asList(getAnchorEdges());
            for (RuleEdge edge : anchorEdges) {
                anchorNodes.add(edge.source());
                anchorNodes.add(edge.target());
            }
            this.eventMatcher = createMatcher(anchorNodes, anchorEdges);
        }
        return this.eventMatcher;
    }

    /**
     * Returns the match strategy for the target
     * pattern. First creates the strategy using 
     * {@link #createMatcher(Collection,Collection)} if that
     * has not been done.
     * 
     * @param seedMap mapping from the seed elements to a host graph.
     * 
     * @see #createMatcher(Collection, Collection)
     */
    private SearchStrategy getMatcher(RuleToHostMap seedMap) {
        assert isTop();
        Matcher result;
        if (getSignature().size() > 0) {
            int sigSize = getSignature().size();
            BitSet initPars = new BitSet(sigSize);
            for (int i = 0; i < sigSize; i++) {
                // set initPars if the seed map contains a value
                // for this parameter
                initPars.set(
                    i,
                    seedMap.containsNodeKey(getSignature().get(i).getRuleNode()));
            }
            result = this.matcherMap.get(initPars);
            if (result == null) {
                this.matcherMap.put(
                    initPars,
                    result =
                        createMatcher(seedMap.nodeMap().keySet(),
                            seedMap.edgeMap().keySet()));
            }
        } else {
            result = getMatcher();
        }
        return result;
    }

    /**
     * Returns a (precomputed) match strategy for the target
     * pattern, given a seed map.
     * @see #createMatcher(Collection, Collection)
     */
    public Matcher getMatcher() {
        if (this.matcher == null) {
            this.matcher =
                createMatcher(this.condition.getRoot().nodeSet(),
                    this.condition.getRoot().edgeSet());
        }
        return this.matcher;
    }

    /**
     * Callback method to create a match strategy. Typically invoked once, at
     * the first invocation of {@link #getMatcher()}. This implementation
     * retrieves its value from {@link #getMatcherFactory()}.
     * @param seedNodes the pre-matched rule nodes
     * @param seedEdges the pre-matched rule edges
     */
    private Matcher createMatcher(Collection<RuleNode> seedNodes,
            Collection<RuleEdge> seedEdges) {
        testFixed(true);
        return getMatcherFactory().createMatcher(getCondition(), seedNodes,
            seedEdges);
    }

    /** Returns a matcher factory, tuned to the properties of this condition. */
    private MatcherFactory getMatcherFactory() {
        return MatcherFactory.instance();
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
        if (this.checkDangling) {
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
            Set<HostEdge> danglingEdges =
                new HashSet<HostEdge>(host.edgeSet(erasedNode));
            for (RuleEdge eraserEdge : lhs().edgeSet(eraserNode)) {
                boolean removed =
                    danglingEdges.remove(match.getEdge(eraserEdge));
                assert removed : String.format(
                    "Match %s not present in incident edges %s",
                    match.getEdge(eraserEdge), host.edgeSet(erasedNode));
            }
            if (!danglingEdges.isEmpty()) {
                result = false;
                break;
            }
        }
        return result;
    }

    /**
     * Returns the left hand side of this Rule.
     * @ensure <tt>result == morphism().source()</tt>
     */
    public RuleGraph lhs() {
        return this.lhs;
    }

    /**
     * Returns the right hand side of this Rule.
     * @ensure <tt>result == morphism().cod()</tt>
     */
    public RuleGraph rhs() {
        return this.rhs;
    }

    /**
     * Returns the rule morphism, which is the partial morphism from LHS to RHS.
     * @see #lhs()
     * @see #rhs()
     */
    public RuleGraphMorphism getMorphism() {
        return this.morphism;
    }

    /** Returns the anchor nodes. */
    public RuleNode[] getAnchorNodes() {
        if (this.anchorNodes == null) {
            initAnchor();
        }
        return this.anchorNodes;
    }

    /** Returns the anchor edges. */
    public RuleEdge[] getAnchorEdges() {
        if (this.anchorEdges == null) {
            initAnchor();
        }
        return this.anchorEdges;
    }

    /**
     * Initialises the anchor nodes and edges for this rule.
     */
    private void initAnchor() {
        Collection<RuleElement> anchor =
            new TreeSet<RuleElement>(
                Arrays.asList(anchorFactory.newAnchors(this)));
        List<RuleNode> nodeResult = new ArrayList<RuleNode>(anchor.size());
        List<RuleEdge> edgeResult = new ArrayList<RuleEdge>(anchor.size());
        for (RuleElement elem : anchor) {
            if (elem instanceof RuleNode) {
                nodeResult.add((RuleNode) elem);
            } else {
                edgeResult.add((RuleEdge) elem);
            }
        }
        this.anchorNodes = nodeResult.toArray(new RuleNode[nodeResult.size()]);
        this.anchorEdges = edgeResult.toArray(new RuleEdge[edgeResult.size()]);
    }

    @Override
    public String toString() {
        StringBuilder res =
            new StringBuilder(String.format(
                "Rule %s; anchor nodes: %s, edges: %s%n", getName(),
                Groove.toString(getAnchorNodes()),
                Groove.toString(getAnchorEdges())));
        res.append(getCondition().toString("    "));
        return res.toString();
    }

    /**
     * Compares two rules on the basis of their names.
     */
    public int compareTo(Rule other) {
        return getName().compareTo(other.getName());
    }

    // ------------------- commands --------------------------

    /**
     * In addition to calling the super method, adds implicit NACs as dictated
     * by {@link SystemProperties#isCheckCreatorEdges()} and
     * {@link SystemProperties#isRhsAsNac()}.
     */
    @Override
    public void setFixed() throws FormatException {
        if (!this.fixed && !this.fixing) {
            this.fixing = true;
            this.fixed = true;
            getCondition().setFixed();
            if (PRINT && isTop()) {
                System.out.println(toString());
            }
            this.fixing = false;
        }
    }

    @Override
    public boolean isFixed() {
        return this.fixed;
    }

    /**
     * Tests if the condition is fixed or not. Throws an exception if the
     * fixedness does not coincide with the given value.
     * 
     * @param value the expected fixedness state
     * @throws IllegalStateException if {@link #isFixed()} does not yield
     *         <code>value</code>
     */
    @Override
    public void testFixed(boolean value) throws IllegalStateException {
        if (isFixed() != value) {
            String message;
            if (value) {
                message = "Rule should be fixed";
            } else {
                message = "Rule should not be fixed";
            }
            throw new IllegalStateException(message);
        }
    }

    /** Returns an array of nodes isolated in the left hand side. */
    final public RuleNode[] getIsolatedNodes() {
        if (this.isolatedNodes == null) {
            this.isolatedNodes = computeIsolatedNodes();
        }
        return this.isolatedNodes;
    }

    /** Computes the array of nodes isolated in the left hand side. */
    private RuleNode[] computeIsolatedNodes() {
        testFixed(true);
        Set<RuleNode> result = new HashSet<RuleNode>();
        for (RuleNode node : lhs().nodeSet()) {
            if (lhs().edgeSet(node).isEmpty()) {
                result.add(node);
            }
        }
        result.removeAll(this.condition.getRoot().nodeSet());
        return result.toArray(new RuleNode[result.size()]);
    }

    /**
     * Indicates if this rule has mergers.
     */
    final public boolean hasMergers() {
        if (!this.hasMergersSet) {
            this.hasMergers = computeHasMergers();
            this.hasMergersSet = true;
        }
        return this.hasMergers;
    }

    /**
     * Computes if the rule has mergers or not.
     */
    private boolean computeHasMergers() {
        boolean result = !getMergeMap().isEmpty();
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
     * Indicates if application of this rule actually changes the host graph. If
     * <code>false</code>, this means the rule is essentially a graph
     * condition.
     */
    public boolean isModifying() {
        if (!this.modifyingSet) {
            this.modifying = computeIsModifying();
            this.modifyingSet = true;
        }
        return this.modifying;
    }

    /**
     * Computes if the rule is modifying or not.
     */
    private boolean computeIsModifying() {
        boolean result =
            getEraserEdges().length > 0 || getEraserNodes().length > 0
                || hasMergers() || hasCreators();
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
     * Indicates if the rule creates any nodes or edges.
     */
    public boolean hasCreators() {
        if (!this.hasCreatorsSet) {
            this.hasCreators = computeHasCreators();
            this.hasCreatorsSet = true;
        }
        return this.hasCreators;
    }

    private boolean computeHasCreators() {
        boolean result =
            getCreatorNodes().length + getCreatorEdges().length > 0;
        if (!result) {
            for (Rule subRule : getSubRules()) {
                result = subRule.hasCreators();
                if (result) {
                    break;
                }
            }
        }
        return result;
    }

    /** Returns the eraser (i.e., LHS-only) edges. */
    final RuleEdge[] getEraserEdges() {
        if (this.eraserEdges == null) {
            this.eraserEdges = computeEraserEdges();
        }
        return this.eraserEdges;
    }

    /**
     * Computes the eraser (i.e., LHS-only) edges.
     */
    private RuleEdge[] computeEraserEdges() {
        testFixed(true);
        Set<RuleEdge> eraserEdgeSet = new HashSet<RuleEdge>(lhs().edgeSet());
        eraserEdgeSet.removeAll(getMorphism().edgeMap().keySet());
        // also remove the incident edges of the lhs-only nodes
        for (RuleNode eraserNode : getEraserNodes()) {
            eraserEdgeSet.removeAll(lhs().edgeSet(eraserNode));
        }
        return eraserEdgeSet.toArray(new RuleEdge[eraserEdgeSet.size()]);
    }

    /** Returns the eraser edges that are not themselves anchors. */
    final RuleEdge[] getEraserNonAnchorEdges() {
        if (this.eraserNonAnchorEdges == null) {
            this.eraserNonAnchorEdges = computeEraserNonAnchorEdges();
        }
        return this.eraserNonAnchorEdges;
    }

    /**
     * Computes the array of creator edges that are not themselves anchors.
     */
    private RuleEdge[] computeEraserNonAnchorEdges() {
        Set<RuleEdge> eraserNonAnchorEdgeSet =
            new HashSet<RuleEdge>(Arrays.asList(getEraserEdges()));
        eraserNonAnchorEdgeSet.removeAll(Arrays.asList(getAnchorEdges()));
        return eraserNonAnchorEdgeSet.toArray(new RuleEdge[eraserNonAnchorEdgeSet.size()]);
    }

    /**
     * Returns the LHS nodes that are not mapped to the RHS.
     */
    final RuleNode[] getEraserNodes() {
        if (this.eraserNodes == null) {
            this.eraserNodes = computeEraserNodes();
        }
        return this.eraserNodes;
    }

    /**
     * Computes the eraser (i.e., lhs-only) nodes.
     */
    private RuleNode[] computeEraserNodes() {
        //testFixed(true);
        Set<RuleNode> eraserNodeSet = new HashSet<RuleNode>(lhs().nodeSet());
        eraserNodeSet.removeAll(getMorphism().nodeMap().keySet());
        // eraserNodeSet.removeAll(getCoRootMap().nodeMap().values());
        return eraserNodeSet.toArray(new RuleNode[eraserNodeSet.size()]);
    }

    /**
     * Returns an array of LHS nodes that are endpoints of eraser edges, creator
     * edges or mergers, either in this rule or one of its sub-rules.
     */
    final Set<RuleNode> getModifierEnds() {
        if (this.modifierEnds == null) {
            this.modifierEnds = computeModifierEnds();
        }
        return this.modifierEnds;
    }

    /**
     * Computes the array of LHS nodes that are endpoints of eraser edges,
     * creator edges or mergers, either in this rule or one of its sub-rules.
     */
    private Set<RuleNode> computeModifierEnds() {
        Set<RuleNode> result = new HashSet<RuleNode>();
        // add the end nodes of creator edges
        Set<RuleNode> creatorNodes = getCreatorGraph().nodeSet();
        for (Map.Entry<RuleNode,RuleNode> ruleMorphNodeEntry : getMorphism().nodeMap().entrySet()) {
            if (creatorNodes.contains(ruleMorphNodeEntry.getValue())) {
                result.add(ruleMorphNodeEntry.getKey());
            }
        }
        // add the end nodes of eraser edges
        for (RuleEdge eraserEdge : getEraserEdges()) {
            RuleNode end = eraserEdge.source();
            if (getMorphism().containsNodeKey(end)) {
                result.add(end);
            }
            end = eraserEdge.target();
            if (getMorphism().containsNodeKey(end)) {
                result.add(end);
            }
        }
        // add merged nodes
        result.addAll(getMergeMap().keySet());
        assert lhs().nodeSet().containsAll(result) : String.format(
            "LHS node set %s does not contain all anchors in %s",
            lhs().nodeSet(), result);
        return result;
    }

    RuleGraphMorphism getCoRootMap() {
        return this.coRootMap;
    }

    /**
     * Returns the creator edges between reader nodes.
     */
    final RuleEdge[] getSimpleCreatorEdges() {
        if (this.simpleCreatorEdges == null) {
            this.simpleCreatorEdges = computeSimpleCreatorEdges();
        }
        return this.simpleCreatorEdges;
    }

    /**
     * Computes the creator edges between reader nodes.
     */
    private RuleEdge[] computeSimpleCreatorEdges() {
        List<RuleEdge> result = new ArrayList<RuleEdge>();
        Set<RuleNode> nonCreatorNodes = getCreatorMap().nodeMap().keySet();
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
     * Returns the creator edges that have at least one creator end.
     */
    public final Set<RuleEdge> getComplexCreatorEdges() {
        if (this.complexCreatorEdges == null) {
            this.complexCreatorEdges = computeComplexCreatorEdges();
        }
        return this.complexCreatorEdges;
    }

    /**
     * Computes the creator edges that have at least one creator end.
     */
    private Set<RuleEdge> computeComplexCreatorEdges() {
        Set<RuleEdge> result =
            new HashSet<RuleEdge>(Arrays.asList(getCreatorEdges()));
        result.removeAll(Arrays.asList(getSimpleCreatorEdges()));
        return result;
    }

    /**
     * Returns the RHS edges that are not images of an LHS edge.
     */
    final RuleEdge[] getCreatorEdges() {
        if (this.creatorEdges == null) {
            this.creatorEdges = computeCreatorEdges();
        }
        return this.creatorEdges;
    }

    /**
     * Computes the creator (i.e., RHS-only) edges.
     */
    private RuleEdge[] computeCreatorEdges() {
        Set<RuleEdge> result = new HashSet<RuleEdge>(rhs().edgeSet());
        result.removeAll(getMorphism().edgeMap().values());
        result.removeAll(getCoRootMap().edgeMap().values());
        return result.toArray(new RuleEdge[result.size()]);
    }

    /**
     * Returns the RHS nodes that are not images of an LHS node.
     */
    final public RuleNode[] getCreatorNodes() {
        if (this.creatorNodes == null) {
            this.creatorNodes = computeCreatorNodes();
        }
        return this.creatorNodes;
    }

    /**
     * Computes the creator (i.e., RHS-only) nodes.
     */
    private RuleNode[] computeCreatorNodes() {
        Set<RuleNode> result = new HashSet<RuleNode>(rhs().nodeSet());
        result.removeAll(getMorphism().nodeMap().values());
        result.removeAll(getCoRootMap().nodeMap().values());
        return result.toArray(new RuleNode[result.size()]);
    }

    /**
     * Returns the variables that occur in creator edges.
     * @see #getCreatorEdges()
     */
    final LabelVar[] getCreatorVars() {
        if (this.creatorVars == null) {
            this.creatorVars = computeCreatorVars();
        }
        return this.creatorVars;
    }

    /**
     * Computes the variables occurring in RHS edges.
     */
    private LabelVar[] computeCreatorVars() {
        Set<LabelVar> creatorVarSet = new HashSet<LabelVar>();
        for (int i = 0; i < getCreatorEdges().length; i++) {
            RuleEdge creatorEdge = getCreatorEdges()[i];
            LabelVar creatorVar = creatorEdge.label().getWildcardId();
            if (creatorVar != null) {
                creatorVarSet.add(creatorVar);
            }
        }
        return creatorVarSet.toArray(new LabelVar[creatorVarSet.size()]);
    }

    /**
     * Returns a sub-graph of the RHS consisting of the creator nodes and the
     * creator edges with their endpoints.
     */
    final RuleGraph getCreatorGraph() {
        if (this.creatorGraph == null) {
            this.creatorGraph = computeCreatorGraph();
        }
        return this.creatorGraph;
    }

    /**
     * Computes a creator graph, consisting of the creator nodes together with
     * the creator edges and their endpoints.
     */
    private RuleGraph computeCreatorGraph() {
        RuleGraph result = rhs().newGraph(getName() + "(creators)");
        result.addNodeSet(Arrays.asList(getCreatorNodes()));
        result.addEdgeSet(Arrays.asList(getCreatorEdges()));
        return result;
    }

    /**
     * Returns a partial map from the nodes of the creator graph (see
     * {@link #getCreatorGraph()}) that are not themselves creator nodes but are
     * the ends of creator edges, to the corresponding nodes of the LHS.
     */
    final RuleGraphMorphism getCreatorMap() {
        if (this.creatorMap == null) {
            this.creatorMap = computeCreatorMap();
        }
        return this.creatorMap;
    }

    /**
     * Computes a value for the creator map. The creator map maps the endpoints
     * of creator edges that are not themselves creator nodes to one of their
     * pre-images.
     */
    private RuleGraphMorphism computeCreatorMap() {
        // construct rhsOnlyMap
        RuleGraphMorphism result = new RuleGraphMorphism();
        Set<? extends RuleNode> creatorNodes = getCreatorGraph().nodeSet();
        for (Map.Entry<RuleNode,RuleNode> nodeEntry : getMorphism().nodeMap().entrySet()) {
            if (creatorNodes.contains(nodeEntry.getValue())) {
                result.putNode(nodeEntry.getValue(), nodeEntry.getKey());
            }
        }
        return result;
    }

    /**
     * Returns a map from LHS nodes that are merged to those LHS nodes they are
     * merged with.
     */
    final Map<RuleNode,RuleNode> getMergeMap() {
        if (this.mergeMap == null) {
            this.mergeMap = computeMergeMap();
        }
        return this.mergeMap;
    }

    /**
     * Computes the merge map, which maps each LHS node that is merged with
     * others to the LHS node it is merged with.
     */
    private Map<RuleNode,RuleNode> computeMergeMap() {
        testFixed(true);
        Map<RuleNode,RuleNode> result = new HashMap<RuleNode,RuleNode>();
        Map<RuleNode,RuleNode> rhsToLhsMap = new HashMap<RuleNode,RuleNode>();
        for (Map.Entry<RuleNode,RuleNode> nodeEntry : getMorphism().nodeMap().entrySet()) {
            RuleNode mergeTarget = rhsToLhsMap.get(nodeEntry.getValue());
            if (mergeTarget == null) {
                mergeTarget = nodeEntry.getKey();
                rhsToLhsMap.put(nodeEntry.getValue(), mergeTarget);
            } else {
                result.put(nodeEntry.getKey(), mergeTarget);
                // the merge target is also merged
                // maybe we do this more than once, but that's negligible
                result.put(mergeTarget, mergeTarget);
            }
        }
        return result;
    }

    /**
     * Array of LHS edges that bind variables. An edge is said to bind a
     * variable if it carries a regular expression which, when it matches, must
     * provide a value for at least one variable.
     */
    final RuleEdge[] getSimpleVarEdges() {
        if (this.varEdges == null) {
            this.varEdges = computeSimpleVarEdges();
        }
        return this.varEdges;
    }

    /**
     * Computes the set of variable-binding edges occurring in the lhs.
     */
    private RuleEdge[] computeSimpleVarEdges() {
        return VarSupport.getSimpleVarEdges(lhs()).toArray(new RuleEdge[0]);
    }

    /**
     * Returns the properties of the rule.
     */
    public GraphProperties getRuleProperties() {
        return this.ruleProperties;
    }

    private final Condition condition;
    /**
     * The parent rule of this rule; may be <code>null</code>, if this is a
     * top-level rule.
     */
    private Rule parent;
    /**
     * The collection of direct sub-rules of this rules. Lazily created by
     * {@link #getSubRules()}.
     */
    private Collection<Rule> subRules;
    /**
     * Indicates if this rule has node mergers.
     */
    private boolean hasMergers;
    /** Flag indicating if the {@link #hasMergers} has been computed. */
    private boolean hasMergersSet;
    /**
     * Indicates if this rule has creator edges or nodes.
     * @invariant <tt>hasCreators == ! ruleMorph.isSurjective()</tt>
     */
    private boolean hasCreators;
    /** Flag indicating if the {@link #hasCreators} has been computed. */
    private boolean hasCreatorsSet;
    /**
     * Indicates if this rule makes changes to a graph at all.
     */
    private boolean modifying;
    /**
     * Indicates if the {@link #modifying} variable has been computed
     */
    private boolean modifyingSet;
    //    /** Flag indicating if this rule is now fixed, i.e., unchangeable. */
    //    private boolean fixed;
    /**
     * The underlying production morphism.
     * @invariant ruleMorph : lhs --> rhs
     */
    private final RuleGraphMorphism morphism;
    /**
     * This production rule's left hand side.
     * @invariant lhs != null
     */
    private RuleGraph lhs;
    /**
     * This production rule's right hand side.
     * @invariant rhs != null
     */
    private RuleGraph rhs;
    /** Mapping from the context of this rule to the RHS. */
    private final RuleGraphMorphism coRootMap;
    /** Flag indicating whether rule applications should be checked for dangling edges. */
    private boolean checkDangling;
    /** Flag indicating whether the rule has been fixed. */
    private boolean fixed;
    /** Flag indicating whether the rule is currently in the process of fixing. */
    private boolean fixing;
    /**
     * A sub-graph of the production rule's right hand side, consisting only of
     * the fresh nodes and edges.
     */
    private RuleGraph creatorGraph;
    /**
     * A map from the nodes of <tt>rhsOnlyGraph</tt> to <tt>lhs</tt>, which is
     * the restriction of the inverse of <tt>ruleMorph</tt> to
     * <tt>rhsOnlyGraph</tt>.
     */
    private RuleGraphMorphism creatorMap;
    /**
     * The lhs nodes that are not ruleMorph keys
     * @invariant lhsOnlyNodes \subseteq lhs.nodeSet()
     */
    private RuleNode[] eraserNodes;
    /**
     * The lhs edges that are not ruleMorph keys
     * @invariant lhsOnlyEdges \subseteq lhs.edgeSet()
     */
    private RuleEdge[] eraserEdges;
    /**
     * The set of anchor nodes of this rule.
     */
    private RuleNode[] anchorNodes;
    /**
     * The set of anchor edges of this rule.
     */
    private RuleEdge[] anchorEdges;
    /**
     * The lhs edges that are not ruleMorph keys and are not anchors
     */
    private RuleEdge[] eraserNonAnchorEdges;
    /**
     * The lhs edges containing bound variables.
     */
    private RuleEdge[] varEdges;
    /**
     * The lhs nodes that are end points of eraser edges, either in this rule or
     * one of its sub-rules.
     */
    private Set<RuleNode> modifierEnds;
    /**
     * The LHS nodes that do not have any incident edges in the LHS.
     */
    private RuleNode[] isolatedNodes;
    /**
     * The rhs nodes that are not ruleMorph images
     * @invariant creatorNodes \subseteq rhs.nodeSet()
     */
    private RuleNode[] creatorNodes;

    /**
     * The rhs edges that are not ruleMorph images
     */
    private RuleEdge[] creatorEdges;
    /**
     * The rhs edges that are not ruleMorph images but with all ends morphism
     * images
     */
    private RuleEdge[] simpleCreatorEdges;
    /**
     * The rhs edges with at least one end not a morphism image
     */
    private Set<RuleEdge> complexCreatorEdges;
    /**
     * Variables occurring in the rhsOnlyEdges
     */
    private LabelVar[] creatorVars;
    /**
     * A partial mapping from LHS nodes to RHS nodes, indicating which nodes are
     * merged and which nodes are deleted.
     */
    private Map<RuleNode,RuleNode> mergeMap;

    private final GraphProperties ruleProperties;

    /** The signature of the rule. */
    private List<CtrlPar.Var> sig;
    /** 
     * List of indices for the parameters, pointing either to the
     * anchor position or to the position in the created nodes list.
     * The latter are offset by the length of the anchor.
     */
    private int[] parBinding;
    /**
     * Set of anonymous (unnumbered) parameters.
     */
    private Set<RuleNode> hiddenPars;

    /**
     * The fixed matching strategy for this graph condition. Initially
     * <code>null</code>; set by {@link #getMatcher()} upon its first
     * invocation.
     */
    private Matcher matcher;

    /**
     * Mapping from sets of initialised parameters to match strategies.
     */
    private final Map<BitSet,Matcher> matcherMap =
        new HashMap<BitSet,Matcher>();

    /** The matcher for events of this rule. */
    private Matcher eventMatcher;

    /** Returns the current anchor factory for all rules. */
    public static AnchorFactory<Rule> getAnchorFactory() {
        return anchorFactory;
    }

    /**
     * Sets the anchor factory for all rules. Only affects rules created from
     * this moment on.
     */
    public static void setAnchorFactory(AnchorFactory<Rule> anchorFactory) {
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
    private static AnchorFactory<Rule> anchorFactory =
        MinimalAnchorFactory.getInstance();
    /** Debug flag for the constructor. */
    private static final boolean PRINT = false;
    /**
     * The lowest rule priority, which is also the default value if no explicit
     * priority is given.
     */
    static public final int DEFAULT_PRIORITY = 0;
    /**
     * A comparator for priorities, encoded as {@link Integer} objects. This
     * implementation orders priorities from high to low.
     */
    static final public Comparator<Integer> PRIORITY_COMPARATOR =
        new Comparator<Integer>() {
            public int compare(Integer o1, Integer o2) {
                return o2.intValue() - o1.intValue();
            }

        };
}
