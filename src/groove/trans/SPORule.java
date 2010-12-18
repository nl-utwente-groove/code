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
import groove.control.CtrlType;
import groove.control.CtrlVar;
import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.GraphProperties;
import groove.graph.algebra.VariableNode;
import groove.match.MatchStrategy;
import groove.match.SearchPlanStrategy;
import groove.rel.LabelVar;
import groove.rel.VarSupport;
import groove.util.Groove;
import groove.util.NestedIterator;
import groove.util.TransformIterator;
import groove.view.FormatException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Default model of a graph production rule. This implementation assumes simple
 * graphs, and yields <tt>DefaultTransformation</tt>s.
 * @author Arend Rensink
 * @version $Revision$
 */
public class SPORule extends PositiveCondition<RuleMatch> implements Rule {
    /**
     * @param name the name of the new rule
     * @param lhs the left hand side graph of the rule
     * @param rhs the right hand side graph of the rule
     * @param morphism the mapping from the LHS to the RHS
     * @param ruleProperties the rule properties
     * @param systemProperties the global grammar properties
     */
    public SPORule(RuleName name, RuleGraph lhs, RuleGraph rhs,
            RuleToRuleMap morphism, GraphProperties ruleProperties,
            SystemProperties systemProperties) {
        this(name, lhs, rhs, morphism, null, null, ruleProperties,
            systemProperties);
    }

    /**
     * Constructs a rule that is a sub-condition of another rule. The
     * information should be completed lated by a call to
     * {@link #setParent(SPORule, int[])}.
     * @param name the name of the new rule
     * @param lhs the left hand side graph of the rule
     * @param rhs the right hand side graph of the rule
     * @param morphism the mapping from the LHS to the RHS
     * @param rootMap pattern map leading into the LHS
     * @param coRootMap map of creator nodes in the parent rule to creator nodes
     *        of this rule
     * @param ruleProperties the rule properties
     * @param systemProperties the global grammar properties
     */
    public SPORule(RuleName name, RuleGraph lhs, RuleGraph rhs,
            RuleToRuleMap morphism, RuleToRuleMap rootMap,
            RuleToRuleMap coRootMap, GraphProperties ruleProperties,
            SystemProperties systemProperties) {
        super(name, lhs, rootMap, systemProperties);
        this.coRootMap = coRootMap == null ? new RuleToRuleMap() : coRootMap;
        this.lhs = lhs;
        this.rhs = rhs;
        this.morphism = morphism;
        this.ruleProperties = ruleProperties;
        assert coRootMap == null
            || rhs().nodeSet().containsAll(coRootMap.nodeMap().values()) : String.format(
            "RHS nodes %s do not contain all co-root values %s",
            rhs().nodeSet(), coRootMap.nodeMap().values());
    }

    /** Sets the priority of this rule. */
    public void setPriority(int priority) {
        testFixed(false);
        this.ruleProperties.setPriority(priority);
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
    public void setParent(SPORule parent, int[] level) {
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
        this.level = level;
    }

    @Override
    public String getTransitionLabel() {
        String result = this.ruleProperties.getTransitionLabel();
        if (result == null) {
            result = this.getName().toString();
        }
        return result;
    }

    /**
     * @param label the label to be set.
     */
    public void setTransitionLabel(String label) {
        this.ruleProperties.setTransitionLabel(label);
    }

    /**
     * Returns the parent rule of this rule. The parent may be this rule itself.
     */
    public SPORule getParent() {
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
    public SPORule getTop() {
        if (isTop()) {
            return this;
        } else {
            return getParent().getTop();
        }
    }

    /**
     * Returns the nesting position of this rule in the rule hierarchy. Each
     * array element indicates a next level of the tree; the value is the order
     * index within the level. Thus, an empty array indicates this is a
     * top-level rule. Parent rule and level uniquely identify a rule.
     */
    public int[] getLevel() {
        if (this.level == null) {
            testFixed(true);
            this.level = new int[0];
        }
        return this.level;
    }

    /**
     * Returns the direct sub-rules of this rule, or the entire rule hierarchy.
     * @param recursive if <code>true</code>, returns the entire rule hierarchy
     *        (including this rule); otherwise, only returns the direct
     *        sub-rules.
     */
    public Collection<SPORule> getSubRules(boolean recursive) {
        Collection<SPORule> result = new TreeSet<SPORule>();
        if (recursive) {
            result.add(this);
        }
        for (SPORule subRule : getDirectSubRules()) {
            result.add(subRule);
            if (recursive) {
                result.addAll(subRule.getSubRules(true));
            }
        }
        return result;
    }

    /**
     * Indicates if this rule has sub-rules.
     */
    public boolean hasSubRules() {
        return !getDirectSubRules().isEmpty();
    }

    /**
     * Returns the direct sub-rules of this rule, i.e., the sub-rules that have
     * this rule as their parent.
     */
    private Collection<SPORule> getDirectSubRules() {
        if (this.directSubRules == null) {
            this.directSubRules = new TreeSet<SPORule>();
            for (AbstractCondition<?> condition : getSubConditions()) {
                for (AbstractCondition<?> subCondition : condition.getSubConditions()) {
                    if (subCondition instanceof SPORule) {
                        this.directSubRules.add((SPORule) subCondition);
                    }
                }
            }
        }
        return this.directSubRules;
    }

    /**
     * Sets the parameters of this rule. The rule can have numbered and hidden
     * parameters. Numbered parameters are divided into input (LHS) and output
     * (RHS-only) parameters, and are visible on the transition label.
     * @param sig the signature of the rule, i.e., the list of (visible) parameters
     * @param hiddenPars the set of hidden (i.e., unnumbered) parameter nodes
     */
    public void setSignature(List<CtrlPar.Var> sig, Set<RuleNode> hiddenPars) {
        this.sig = sig;
        this.hiddenPars = hiddenPars;
        List<CtrlPar.Var> derivedSig = new ArrayList<CtrlPar.Var>();
        for (int i = 0; i < sig.size(); i++) {
            String parName = "arg" + (i + 1);
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
        if (this.sig == null) {
            this.sig = Collections.emptyList();
        }
        return this.sig;
    }

    /** Returns, for a given index in the signature,
     * the corresponding index in the anchor 
     * or in the created nodes (if the parameter is a creator).
     * The latter are offset by the length of the anchor.
     */
    public int getParBinding(int i) {
        if (this.parBinding == null) {
            this.parBinding = computeParBinding();
        }
        return this.parBinding[i];
    }

    private int[] computeParBinding() {
        int[] result = new int[this.sig.size()];
        int anchorSize = anchor().length;
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
                binding = Arrays.asList(anchor()).indexOf(ruleNode);
                assert binding >= 0 : String.format(
                    "Node %s not in anchors %s", ruleNode,
                    Arrays.toString(anchor()));
            }
            result[i] = binding;
        }
        return result;
    }

    /**
     * Returns whether a numbered parameter is a creator parameter
     * @param param the number of the parameter under inquiry
     * @return the index of the parameter in the creatorNodes array if this 
     * parameter is a creator parameter, -1 otherwise
     */
    public int isCreatorParameter(int param) {
        RuleNode paramNode = getSignature().get(param - 1).getRuleNode();
        for (int i = 0; i < this.creatorNodes.length; i++) {
            if (this.creatorNodes[i] == paramNode) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns whether this rule has required input parameters. Parameters are 
     * required if they are on an isolated attribute node.
     * @return true if this rule has required input parameters, false otherwise
     */
    public boolean hasRequiredInputs() {
        boolean result = false;
        for (CtrlPar.Var par : getSignature()) {
            if (par.isInOnly()) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * Returns the set of hidden (i.e., unnumbered) parameter nodes of this
     * rule.
     */
    public Set<RuleNode> getHiddenPars() {
        return this.hiddenPars;
    }

    /** Creates the search plan using the rule's search plan factory. */
    public MatchStrategy<RuleToHostMap> getEventMatcher() {
        if (this.eventMatcher == null) {
            this.eventMatcher =
                getMatcherFactory().createMatcher(this,
                    getAnchorGraph().nodeSet(), getAnchorGraph().edgeSet(),
                    null);
        }
        return this.eventMatcher;
    }

    @Override
    public void resetMatcher() {
        this.eventMatcher = null;
        super.resetMatcher();
    }

    /** This implementation sets the anchor graph elements to relevant. */
    @Override
    MatchStrategy<RuleToHostMap> createMatcher() {
        Set<RuleNode> anchorNodes = new HashSet<RuleNode>();
        Set<RuleEdge> anchorEdges = new HashSet<RuleEdge>();
        if (getRootMap() != null) {
            anchorNodes.addAll(getRootMap().nodeMap().values());
            for (Edge edge : getRootMap().edgeMap().values()) {
                anchorEdges.add((RuleEdge) edge);
            }
        }
        for (CtrlPar.Var par : getSignature()) {
            if (par.isInOnly()) {
                anchorNodes.add(par.getRuleNode());
            }
        }
        return getMatcherFactory().createMatcher(this, anchorNodes,
            anchorEdges, getMatchRelevantNodes());
    }

    @Override
    public Iterator<RuleMatch> computeMatchIter(final HostGraph host,
            Iterator<RuleToHostMap> matchMapIter) {
        Iterator<RuleMatch> result = null;
        result =
            new NestedIterator<RuleMatch>(
                new TransformIterator<RuleToHostMap,Iterator<RuleMatch>>(
                    matchMapIter) {
                    @Override
                    public Iterator<RuleMatch> toOuter(RuleToHostMap matchMap) {
                        if (isValidMatchMap(host, matchMap)) {
                            return addSubMatches(host, createMatch(matchMap)).iterator();
                        } else {
                            return null;
                        }
                    }
                });
        return result;
    }

    /**
     * Returns a collection of matches extending a given match with matches for
     * the sub-conditions.
     */
    Collection<RuleMatch> addSubMatches(HostGraph host, RuleMatch simpleMatch) {
        Collection<RuleMatch> result = Collections.singleton(simpleMatch);
        RuleToHostMap matchMap = simpleMatch.getElementMap();
        for (AbstractCondition<?> condition : getComplexSubConditions()) {
            Iterable<? extends Match> subMatches =
                condition.getMatches(host, matchMap);
            Collection<RuleMatch> oldResult = result;
            result = new ArrayList<RuleMatch>();
            for (RuleMatch oldMatch : oldResult) {
                result.addAll(oldMatch.addSubMatchChoice(subMatches));
            }
        }
        return result;
    }

    /**
     * Callback factory method to create a match on the basis of a mapping of
     * this condition's target.
     * 
     * @param matchMap the mapping, presumably of the elements of
     *        {@link #getTarget()} into some host graph
     * @return a match constructed on the basis of <code>map</code>
     */
    @Override
    protected RuleMatch createMatch(RuleToHostMap matchMap) {
        return new RuleMatch(this, matchMap);
    }

    /**
     * Tests whether a given match map satisfies the additional constraints
     * imposed by this rule.
     * @param host the graph to be matched
     * @param matchMap the proposed map from {@link #getTarget()} to
     *        <code>host</code>
     * @return <code>true</code> if <code>matchMap</code> satisfies the
     *         constraints imposed by the rule (if any).
     */
    boolean isValidMatchMap(HostGraph host, RuleToHostMap matchMap) {
        boolean result = true;
        if (SystemProperties.isCheckDangling(getSystemProperties())) {
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

    public RuleGraph lhs() {
        return this.lhs;
    }

    public RuleGraph rhs() {
        return this.rhs;
    }

    public RuleToRuleMap getMorphism() {
        return this.morphism;
    }

    /**
     * Returns the array of elements that should be matched to have an
     * unambiguous rule event. This includes the eraser nodes (or incident edges
     * thereof), the eraser edges (or end nodes thereof) and the end nodes of
     * creator edges (insofar they are not creator nodes), as well as root node
     * images.
     */
    public Element[] anchor() {
        if (this.anchor == null) {
            // getTop().setAnchor(null);
            setAnchor(null);
        }
        return this.anchor;
    }

    /**
     * Sets the anchor of this rule recursively. Anchors of sub-rules that have
     * roots in this rule are added.
     * @param parentAnchor the collection of anchors from the parent rule; may
     *        be <code>null</code> if this rule is the top rule.
     */
    private void setAnchor(Collection<Element> parentAnchor) {
        Collection<Element> myAnchor =
            new TreeSet<Element>(Arrays.asList(computeNestedAnchor()));
        this.anchor = myAnchor.toArray(new Element[myAnchor.size()]);
    }

    /**
     * Callback method creating the anchors of this rule. Called from the
     * constructor. This implementation delegates to {@link #getAnchorFactory()}
     * .
     */
    private Element[] computeNestedAnchor() {
        return anchorFactory.newAnchors(this);
    }

    // -------------------- OBJECT OVERRIDES -----------------------------

    /**
     * @see Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder res =
            new StringBuilder(String.format("Rule %s, level %s, anchor %s%n",
                getName(), Groove.toString(Groove.toArray(getLevel())),
                Groove.toString(anchor())));
        res.append(String.format("LHS: %s%nRHS: %s%nMorphism: %s", lhs(),
            rhs(), getMorphism()));
        if (!getRootMap().isEmpty()) {
            res.append(String.format("%nRoot map: %s", getRootMap()));
        }
        if (!getCoRootMap().isEmpty()) {
            res.append(String.format("%nCo-root map: %s", getCoRootMap()));
        }
        if (!getSubConditions().isEmpty()) {
            res.append(String.format("%n----Subconditions of %s:", getName()));
            for (Condition subCondition : getSubConditions()) {
                res.append(String.format("%n%s", subCondition));
            }
            res.append(String.format("%n----End of %s", getName()));
        }
        return res.toString();
    }

    /**
     * Compares two rules on the basis of their nesting level, or failing that,
     * their names.
     */
    public int compareTo(Rule other) {
        int result = 0;
        if (!(other instanceof SPORule)) {
            // SPO rules come before others
            result = -1;
        } else if (!equals(other)) {
            // compare parent rules
            Rule otherParent = ((SPORule) other).getParent();
            if (equals(getParent())) {
                other = otherParent;
            } else {
                result = getParent().compareTo(otherParent);
            }
            if (result == 0) {
                // compare levels
                int[] level = getLevel();
                int[] otherLevel = ((SPORule) other).getLevel();
                for (int depth = 0; result == 0 && depth < level.length; depth++) {
                    if (depth == otherLevel.length) {
                        result = +1;
                    } else {
                        result = level[depth] - otherLevel[depth];
                    }
                }
            }
            if (result == 0) {
                // we have to rely on names, so they'd better be non-null
                result = getName().compareTo(other.getName());
            }
        }
        return result;
    }

    // ------------------- commands --------------------------

    public int getPriority() {
        return this.ruleProperties.getPriority();
    }

    public boolean isConfluent() {
        return this.ruleProperties.isConfluent();
    }

    /**
     * In addition to calling the super method, adds implicit NACs as dictated
     * by {@link SystemProperties#isCheckCreatorEdges()} and
     * {@link SystemProperties#isRhsAsNac()}.
     */
    @Override
    public void setFixed() throws FormatException {
        super.setFixed();
        if (PRINT && isTop()) {
            System.out.println(toString());
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
        result.removeAll(getRootMap().nodeMap().values());
        return result.toArray(new RuleNode[result.size()]);
    }

    /**
     * Indicates if this rule has mergers.
     * @invariant <tt>result == ! getMergeMap().isEmpty()</tt>
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
            result = hasMergingSubRules(this);
        }
        return result;
    }

    /**
     * Computes if a given condition has merging rules as sub-conditions.
     */
    private boolean hasMergingSubRules(Condition condition) {
        boolean result = false;
        for (Condition subCondition : condition.getSubConditions()) {
            if (subCondition instanceof Rule) {
                result = ((Rule) subCondition).hasMergers();
            } else {
                result = hasMergingSubRules(subCondition);
            }
            if (result) {
                break;
            }
        }
        return result;
    }

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
            result = hasModifyingSubRules(this);
        }
        return result;
    }

    /**
     * Computes if a given condition has modifying rules as sub-conditions.
     */
    private boolean hasModifyingSubRules(Condition condition) {
        boolean result = false;
        for (Condition subCondition : condition.getSubConditions()) {
            if (subCondition instanceof Rule) {
                result = ((Rule) subCondition).isModifying();
            } else {
                result = hasModifyingSubRules(subCondition);
            }
            if (result) {
                break;
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
            result = hasCreatingSubRules(this);
        }
        return result;
    }

    /**
     * Computes if a given condition has creating rules as sub-conditions.
     */
    private boolean hasCreatingSubRules(Condition condition) {
        boolean result = false;
        for (Condition subCondition : condition.getSubConditions()) {
            if (subCondition instanceof Rule) {
                result = ((Rule) subCondition).hasCreators();
            } else {
                result = hasCreatingSubRules(subCondition);
            }
            if (result) {
                break;
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
        eraserNonAnchorEdgeSet.removeAll(Arrays.asList(anchor()));
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
        // add inverse images of subrule modifier ends
        for (AbstractCondition<?> condition : getSubConditions()) {
            Set<RuleNode> childResult = new HashSet<RuleNode>();
            for (AbstractCondition<?> subCondition : condition.getSubConditions()) {
                if (subCondition instanceof SPORule) {
                    // translate anchor nodes from grandchild to child
                    Set<RuleNode> grandchildResult =
                        ((SPORule) subCondition).getModifierEnds();
                    Map<RuleNode,RuleNode> grandchildRootMap =
                        subCondition.getRootMap().nodeMap();
                    for (Map.Entry<RuleNode,RuleNode> rootEntry : grandchildRootMap.entrySet()) {
                        if (grandchildResult.contains(rootEntry.getValue())) {
                            childResult.add(rootEntry.getKey());
                        }
                    }
                    // check coroot map for mergers
                    Set<RuleNode> mergers = new HashSet<RuleNode>();
                    Map<RuleNode,RuleNode> inverseCoroots =
                        new HashMap<RuleNode,RuleNode>();
                    for (Map.Entry<RuleNode,RuleNode> coRootEntry : ((SPORule) subCondition).getCoRootMap().nodeMap().entrySet()) {
                        RuleNode coRootSource = coRootEntry.getKey();
                        RuleNode coRootTarget = coRootEntry.getValue();
                        if (inverseCoroots.containsKey(coRootTarget)) {
                            mergers.add(coRootSource);
                            mergers.add(inverseCoroots.get(coRootTarget));
                        } else {
                            inverseCoroots.put(coRootTarget, coRootSource);
                        }
                    }
                    // translate mergers to LHS
                    for (Map.Entry<RuleNode,RuleNode> lhsToRhsEntry : getMorphism().nodeMap().entrySet()) {
                        if (mergers.contains(lhsToRhsEntry.getValue())) {
                            result.add(lhsToRhsEntry.getKey());
                        }
                    }
                }
            }
            Map<RuleNode,RuleNode> childRootMap =
                condition.getRootMap().nodeMap();
            for (Map.Entry<RuleNode,RuleNode> rootEntry : childRootMap.entrySet()) {
                if (childResult.contains(rootEntry.getValue())) {
                    result.add(rootEntry.getKey());
                }
            }
        }
        assert lhs().nodeSet().containsAll(result) : String.format(
            "LHS node set %s does not contain all anchors in %s",
            lhs().nodeSet(), result);
        return result;
    }

    RuleToRuleMap getCoRootMap() {
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
        RuleGraph result = rhs().newGraph();
        result.addNodeSet(Arrays.asList(getCreatorNodes()));
        result.addEdgeSet(Arrays.asList(getCreatorEdges()));
        return result;
    }

    /**
     * Returns a partial map from the nodes of the creator graph (see
     * {@link #getCreatorGraph()}) that are not themselves creator nodes but are
     * the ends of creator edges, to the corresponding nodes of the LHS.
     */
    final RuleToRuleMap getCreatorMap() {
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
    private RuleToRuleMap computeCreatorMap() {
        // construct rhsOnlyMap
        RuleToRuleMap result = new RuleToRuleMap();
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
     * Lazily creates and returns the anchor graph of this rule. The anchor
     * graph is the smallest subgraph of the LHS that is necessary to apply the
     * rule. This means it contains all eraser edges and all variables and nodes
     * necessary for creation.
     */
    private RuleGraph getAnchorGraph() {
        if (this.anchorGraph == null) {
            this.anchorGraph = computeAnchorGraph();
        }
        return this.anchorGraph;
    }

    /**
     * Computes the anchor graph of this rule.
     * @see #getAnchorGraph()
     */
    private RuleGraph computeAnchorGraph() {
        RuleGraph result = lhs().newGraph();
        for (Element elem : anchor()) {
            if (elem instanceof RuleNode) {
                result.addNode((RuleNode) elem);
            } else {
                result.addEdge((RuleEdge) elem);
            }
        }
        // add the root map images
        result.addNodeSet(getRootMap().nodeMap().values());
        result.addEdgeSet(getRootMap().edgeMap().values());
        result.addEdgeSet(Arrays.asList(getEraserEdges()));
        return result;
    }

    /**
     * Lazily creates and returns the set of match-relevant nodes of this rule.
     * These are the nodes whose images are important to distinguish rule
     * matches. The set consists of the anchor nodes and the root sources of the
     * universal sub-conditions.
     */
    private Set<RuleNode> getMatchRelevantNodes() {
        if (this.matchRelevantNodes == null) {
            this.matchRelevantNodes = computeMatchRelevantGraph();
        }
        return this.matchRelevantNodes;
    }

    /**
     * Computes the match-relevant nodes of the left hand side.
     * @see #getMatchRelevantNodes()
     */
    private Set<RuleNode> computeMatchRelevantGraph() {
        Set<RuleNode> result = new HashSet<RuleNode>();
        for (Element elem : anchor()) {
            if (elem instanceof RuleNode) {
                result.add((RuleNode) elem);
            } else {
                result.add(((RuleEdge) elem).source());
                result.add(((RuleEdge) elem).target());
            }
        }
        // add the root map sources of the sub-conditions
        for (Condition subCondition : getSubConditions()) {
            if (subCondition instanceof ForallCondition) {
                result.addAll(subCondition.getRootMap().nodeMap().keySet());
            }
        }
        return result;
    }

    @Override
    protected void computeUnresolvedNodes() {
        super.computeUnresolvedNodes();
        Iterator<VariableNode> it = this.unresolvedVariableNodes.iterator();
        while (it.hasNext()) {
            RuleNode node = it.next();
            boolean resolved = false;
            for (CtrlPar.Var par : getSignature()) {
                if (par.getRuleNode() == node && !par.isOutOnly()) {
                    resolved = true;
                    break;
                }
            }
            if (resolved) {
                it.remove();
            }
        }
    }

    @Override
    public GraphProperties getRuleProperties() {
        return this.ruleProperties;
    }

    /**
     * The parent rule of this rule; may be <code>null</code>, if this is a
     * top-level rule.
     */
    private SPORule parent;
    /**
     * The collection of direct sub-rules of this rules. Lazily created by
     * {@link #getDirectSubRules()}.
     */
    private Collection<SPORule> directSubRules;
    /** The nesting level of this rule. */
    private int[] level;
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
    /**
     * The underlying production morphism.
     * @invariant ruleMorph : lhs --> rhs
     */
    private final RuleToRuleMap morphism;
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
    private final RuleToRuleMap coRootMap;
    /**
     * Smallest subgraph of the left hand side that is necessary to apply the
     * rule.
     */
    private RuleGraph anchorGraph;
    /**
     * Subgraph of the left hand containing all elements that are used to
     * distinguish matches.
     */
    private Set<RuleNode> matchRelevantNodes;
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
    private RuleToRuleMap creatorMap;
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
     * The set of anchors of this rule.
     */
    private Element[] anchor;
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

    private GraphProperties ruleProperties;

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
    /** The matcher for events of this rule. */
    private MatchStrategy<RuleToHostMap> eventMatcher;

    /** Returns the current anchor factory for all rules. */
    public static AnchorFactory<SPORule> getAnchorFactory() {
        return anchorFactory;
    }

    /**
     * Sets the anchor factory for all rules. Only affects rules created from
     * this moment on.
     */
    public static void setAnchorFactory(AnchorFactory<SPORule> anchorFactory) {
        SPORule.anchorFactory = anchorFactory;
    }

    /**
     * Returns the total time doing matching-related computations. This includes
     * time spent in certificate calculation.
     */
    static public long getMatchingTime() {
        return SearchPlanStrategy.searchFindReporter.getTotalTime();
    }

    /**
     * The factory used for creating rule anchors.
     */
    private static AnchorFactory<SPORule> anchorFactory =
        MinimalAnchorFactory.getInstance();
    /** Debug flag for the constructor. */
    private static final boolean PRINT = false;

}
