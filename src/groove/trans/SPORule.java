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

import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.Graph;
import groove.graph.GraphProperties;
import groove.graph.GraphShape;
import groove.graph.LabelStore;
import groove.graph.Morphism;
import groove.graph.Node;
import groove.graph.NodeEdgeHashMap;
import groove.graph.NodeEdgeMap;
import groove.graph.algebra.VariableNode;
import groove.match.MatchStrategy;
import groove.match.SearchPlanStrategy;
import groove.rel.RegExprLabel;
import groove.rel.VarNodeEdgeMap;
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
     * @param morph the morphism on which this production is to be based
     * @param name the name of the new rule
     * @param ruleProperties the rule properties
     * @param systemProperties the global grammar properties
     */
    public SPORule(Morphism morph, RuleName name,
            GraphProperties ruleProperties, SystemProperties systemProperties) {
        super(name, morph.dom(), null, null, systemProperties);
        this.morphism = morph;
        this.ruleProperties = ruleProperties;
        this.coRootMap = new NodeEdgeHashMap();
    }

    /**
     * Constructs a rule that is a sub-condition of another rule. The
     * information should be completed lated by a call to
     * {@link #setParent(SPORule, int[])}.
     * @param name the name of the new rule
     * @param morph the morphism on which this production is to be based
     * @param rootMap pattern map leading into the LHS
     * @param coRootMap map of creator nodes in the parent rule to creator nodes
     *        of this rule
     * @param labelStore label store specifying the subtype relation
     * @param ruleProperties the rule properties
     * @param systemProperties the global grammar properties
     */
    public SPORule(RuleName name, Morphism morph, NodeEdgeMap rootMap,
            NodeEdgeMap coRootMap, LabelStore labelStore,
            GraphProperties ruleProperties, SystemProperties systemProperties) {
        super(name, morph.dom(), rootMap, labelStore, systemProperties);
        this.coRootMap = coRootMap == null ? new NodeEdgeHashMap() : coRootMap;
        this.morphism = morph;
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
        assert parent.rhs().nodeSet().containsAll(
            this.coRootMap.nodeMap().keySet()) : String.format(
            "Rule '%s': Parent nodes %s do not contain all co-roots %s",
            getName(), parent.rhs().nodeSet(),
            this.coRootMap.nodeMap().keySet());
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
     * @param inPars an ordered list of input parameter nodes
     * @param outPars an ordered list of output parameter nodes
     * @param hiddenPars the set of hidden (i.e., unnumbered) parameter nodes
     */
    public void setParameters(List<Node> inPars, List<Node> outPars,
            Set<Node> hiddenPars) {
        testFixed(false);
        this.inPars = inPars;
        debug("set " + this.inPars.size() + " lhs params");
        this.outPars = outPars;
        debug("set " + this.outPars.size() + " creator params");
        this.hiddenPars = hiddenPars;
        initializeParameterTypes();
    }

    /**
     * Sets the specified parameter types as defined in the rule editor. Parameters
     * can be input, output or both, signified by ParameterAspect.PAR_IN_NAME, 
     * PAR_OUT_NAME and PAR_NAME, respectively.
     * @param specifiedParameterTypes a map that maps parameter numbers to parameter types
     */
    public void setSpecifiedParameterTypes(
            Map<Integer,Integer> specifiedParameterTypes) {
        this.specifiedParameterTypes = specifiedParameterTypes;
    }

    /**
     * Sets the types for attribute parameters as defined in the rule editor.
     * @param attributeParameterTypes maps Integers (parameter numbers) to Strings (parameter names)
     */
    public void setAttributeParameterTypes(
            Map<Integer,String> attributeParameterTypes) {
        this.attributeParameterTypes = attributeParameterTypes;
    }

    /**
     * Gets the type of the parameter specified
     * @param parameter the parameter number
     * @return a String describing the type
     */
    public String getAttributeParameterType(int parameter) {
        return this.attributeParameterTypes.get(parameter);
    }

    private void initializeParameterTypes() {
        for (int param = 1; param <= getVisibleParCount(); param++) {
            int result = PARAMETER_DOES_NOT_EXIST;

            // if it's in creatorParameters, it may only be an output node
            if (param > this.inPars.size()) {
                result = PARAMETER_OUTPUT;
            } else {

                // if it's in lhsParameters, it could be both
                Node n = this.inPars.get(param - 1);
                if (Arrays.binarySearch(getEraserNodes(), n) >= 0) {
                    result = PARAMETER_INPUT;
                } else {
                    result = PARAMETER_BOTH;
                }
            }

            this.parameterTypes.put(param, result);
        }
    }

    /**
     * Gets the parameter type from a numbered parameter
     * @param param the number of the parameter under inquiry
     * @return PARAMETER_INPUT, PARAMETER_OUTPUT, PARAMETER_BOTH
     */
    private int getParameterType(int param) {
        // check if this parameter even exists
        if (param > getVisibleParCount()) {
            debug("nonexistant parameter: " + param);
            return PARAMETER_DOES_NOT_EXIST;
        }

        return this.parameterTypes.get(param);
    }

    /**
     * Returns the Node from this rule which is associated with the given
     * parameter number
     * @param param the number of the parameter to find
     * @return the Node from this Rule that corresponds to param
     */
    public Node getParameter(int param) {
        if (getParameterType(param) == PARAMETER_INPUT
            || getParameterType(param) == PARAMETER_BOTH) {
            return this.inPars.get(param - 1);
        } else {
            return this.outPars.get(param - this.inPars.size() - 1);
        }
    }

    /**
     * Gets the number of parameters for a given type (input or output)
     * @param type either PARAMETER_INPUT or PARAMETER_OUTPUT
     * @return the number of parameters for this type
     */
    public int getNumberOfParameters(int type) {
        int count = 0;
        for (int thisType : this.parameterTypes.values()) {
            if (thisType == type || thisType == PARAMETER_BOTH) {
                count++;
            }
        }
        return count;
    }

    /**
     * Returns the total number of parameters for this rule.
     * @return the total number of parameters for this rule
     */
    public int getNumberOfParameters() {
        return this.specifiedParameterTypes.size();
    }

    /**
     * Returns whether a numbered parameter can be used as an output parameter
     * @param param the number of the parameter under inquiry
     * @return true if this parameter can be used as output parameter, false otherwise
     */
    public boolean isOutputParameter(int param) {
        int parType1 = getParameterType(param);
        int parType2 = this.specifiedParameterTypes.get(param);
        return (parType1 == PARAMETER_OUTPUT || parType1 == PARAMETER_BOTH)
            && (parType2 == PARAMETER_OUTPUT || parType2 == PARAMETER_BOTH);
    }

    /**
     * Returns whether a numbered parameter is a creator parameter
     * @param param the number of the parameter under inquiry
     * @return the index of the parameter in the creatorNodes array if this 
     * parameter is a creator parameter, -1 otherwise
     */
    public int isCreatorParameter(int param) {
        Node paramNode = this.getParameter(param);
        for (int i = 0; i < this.creatorNodes.length; i++) {
            if (this.creatorNodes[i] == paramNode) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns whether a numbered parameter can be used as an input parameter
     * @param param the number of the parameter under inquiry
     * @return true if this parameter can be used as input parameter
     */
    public boolean isInputParameter(int param) {
        int parType1 = getParameterType(param);
        int parType2 = this.specifiedParameterTypes.get(param);
        return (parType1 == PARAMETER_INPUT || parType1 == PARAMETER_BOTH)
            && (parType2 == PARAMETER_INPUT || parType2 == PARAMETER_BOTH);
    }

    /**
     * Returns whether the given parameter number is a required input parameter.
     * Parameters are required if they are on an isolated attribute node.
     * @param param the number of the parameter under inquiry
     * @return true if this is a required input parameter, false otherwise
     */
    public boolean isRequiredInput(int param) {
        return this.requiredInputs != null
            && this.requiredInputs.contains(getParameter(param));
    }

    /**
     * Returns whether this rule has required input parameters. Parameters are 
     * required if they are on an isolated attribute node.
     * @return true if this rule has required input parameters, false otherwise
     */
    public boolean hasRequiredInputs() {
        return this.requiredInputs != null && this.requiredInputs.size() > 0;
    }

    /**
     * Returns the number of visible (i.e., numbered) parameters of the rule.
     */
    public int getVisibleParCount() {
        return this.inPars.size() + this.outPars.size();
    }

    /** Returns the ordered list of input parameters. */
    public List<Node> getInPars() {
        return this.inPars;
    }

    /**
     * Returns the set of hidden (i.e., unnumbered) parameter nodes of this
     * rule.
     */
    public Set<Node> getHiddenPars() {
        return this.hiddenPars;
    }

    /** Creates the search plan using the rule's search plan factory. */
    public MatchStrategy<VarNodeEdgeMap> getEventMatcher() {
        if (this.eventMatcher == null) {
            this.eventMatcher =
                getMatcherFactory().createMatcher(this,
                    getAnchorGraph().nodeSet(), getAnchorGraph().edgeSet(),
                    null);
        }
        return this.eventMatcher;
    }

    /** This implementation sets the anchor graph elements to relevant. */
    @Override
    MatchStrategy<VarNodeEdgeMap> createMatcher() {
        Set<Node> anchorNodes = new HashSet<Node>();
        Set<Edge> anchorEdges = new HashSet<Edge>();
        if (getRootMap() != null) {
            anchorNodes.addAll(getRootMap().nodeMap().values());
            anchorEdges.addAll(getRootMap().edgeMap().values());
        }
        anchorNodes.addAll(getRequiredInputs());
        return getMatcherFactory().createMatcher(this, anchorNodes,
            anchorEdges, getMatchRelevantNodes());
    }

    @Override
    public Iterator<RuleMatch> computeMatchIter(final GraphShape host,
            Iterator<VarNodeEdgeMap> matchMapIter) {
        Iterator<RuleMatch> result = null;
        result =
            new NestedIterator<RuleMatch>(
                new TransformIterator<VarNodeEdgeMap,Iterator<RuleMatch>>(
                    matchMapIter) {
                    @Override
                    public Iterator<RuleMatch> toOuter(VarNodeEdgeMap matchMap) {
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
    Collection<RuleMatch> addSubMatches(GraphShape host, RuleMatch simpleMatch) {
        Collection<RuleMatch> result = Collections.singleton(simpleMatch);
        VarNodeEdgeMap matchMap = simpleMatch.getElementMap();
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
    protected RuleMatch createMatch(VarNodeEdgeMap matchMap) {
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
    boolean isValidMatchMap(GraphShape host, VarNodeEdgeMap matchMap) {
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
    private boolean satisfiesDangling(GraphShape host, VarNodeEdgeMap match) {
        boolean result = true;
        for (Node eraserNode : getEraserNodes()) {
            Node erasedNode = match.getNode(eraserNode);
            Set<Edge> danglingEdges =
                new HashSet<Edge>(host.edgeSet(erasedNode));
            for (Edge eraserEdge : lhs().edgeSet(eraserNode)) {
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

    public Graph lhs() {
        if (this.lhs == null) {
            this.lhs = getMorphism().dom();
        }
        return this.lhs;
    }

    public Graph rhs() {
        if (this.rhs == null) {
            this.rhs = getMorphism().cod();
        }
        return this.rhs;
    }

    public Morphism getMorphism() {
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
        this.anchor = myAnchor.toArray(new Element[0]);
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
            rhs(), getMorphism().elementMap()));
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
    final public Node[] getIsolatedNodes() {
        if (this.isolatedNodes == null) {
            this.isolatedNodes = computeIsolatedNodes();
        }
        return this.isolatedNodes;
    }

    /** Computes the array of nodes isolated in the left hand side. */
    private Node[] computeIsolatedNodes() {
        testFixed(true);
        Set<Node> result = new HashSet<Node>();
        for (Node node : lhs().nodeSet()) {
            if (lhs().edgeSet(node).isEmpty()) {
                result.add(node);
            }
        }
        result.removeAll(getRootMap().nodeMap().values());
        return result.toArray(new Node[0]);
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
    final Edge[] getEraserEdges() {
        if (this.eraserEdges == null) {
            this.eraserEdges = computeEraserEdges();
        }
        return this.eraserEdges;
    }

    /**
     * Computes the eraser (i.e., LHS-only) edges.
     */
    private Edge[] computeEraserEdges() {
        testFixed(true);
        Set<Edge> eraserEdgeSet = new HashSet<Edge>(lhs().edgeSet());
        eraserEdgeSet.removeAll(getMorphism().edgeMap().keySet());
        // also remove the incident edges of the lhs-only nodes
        for (Node eraserNode : getEraserNodes()) {
            eraserEdgeSet.removeAll(lhs().edgeSet(eraserNode));
        }
        return eraserEdgeSet.toArray(new Edge[0]);
    }

    /** Returns the eraser edges that are not themselves anchors. */
    final Edge[] getEraserNonAnchorEdges() {
        if (this.eraserNonAnchorEdges == null) {
            this.eraserNonAnchorEdges = computeEraserNonAnchorEdges();
        }
        return this.eraserNonAnchorEdges;
    }

    /**
     * Computes the array of creator edges that are not themselves anchors.
     */
    private Edge[] computeEraserNonAnchorEdges() {
        Set<Edge> eraserNonAnchorEdgeSet =
            new HashSet<Edge>(Arrays.asList(getEraserEdges()));
        eraserNonAnchorEdgeSet.removeAll(Arrays.asList(anchor()));
        return eraserNonAnchorEdgeSet.toArray(new Edge[0]);
    }

    /**
     * Returns the LHS nodes that are not mapped to the RHS.
     */
    final Node[] getEraserNodes() {
        if (this.eraserNodes == null) {
            this.eraserNodes = computeEraserNodes();
        }
        return this.eraserNodes;
    }

    /**
     * Computes the eraser (i.e., lhs-only) nodes.
     */
    private Node[] computeEraserNodes() {
        //testFixed(true);
        Set<Node> eraserNodeSet = new HashSet<Node>(lhs().nodeSet());
        eraserNodeSet.removeAll(getMorphism().nodeMap().keySet());
        // eraserNodeSet.removeAll(getCoRootMap().nodeMap().values());
        return eraserNodeSet.toArray(new Node[0]);
    }

    /**
     * Returns an array of LHS nodes that are endpoints of eraser edges, creator
     * edges or mergers, either in this rule or one of its sub-rules.
     */
    final Set<Node> getModifierEnds() {
        if (this.modifierEnds == null) {
            this.modifierEnds = computeModifierEnds();
        }
        return this.modifierEnds;
    }

    /**
     * Computes the array of LHS nodes that are endpoints of eraser edges,
     * creator edges or mergers, either in this rule or one of its sub-rules.
     */
    private Set<Node> computeModifierEnds() {
        Set<Node> result = new HashSet<Node>();
        // add the end nodes of creator edges
        Set<? extends Node> creatorNodes = getCreatorGraph().nodeSet();
        for (Map.Entry<Node,Node> ruleMorphNodeEntry : getMorphism().nodeMap().entrySet()) {
            if (creatorNodes.contains(ruleMorphNodeEntry.getValue())) {
                result.add(ruleMorphNodeEntry.getKey());
            }
        }
        // add the end nodes of eraser edges
        for (Edge eraserEdge : getEraserEdges()) {
            for (Node end : eraserEdge.ends()) {
                if (getMorphism().containsKey(end)) {
                    result.add(end);
                }
            }
        }
        // add merged nodes
        result.addAll(getMergeMap().keySet());
        // add inverse images of subrule modifier ends
        for (AbstractCondition<?> condition : getSubConditions()) {
            Set<Node> childResult = new HashSet<Node>();
            for (AbstractCondition<?> subCondition : condition.getSubConditions()) {
                if (subCondition instanceof SPORule) {
                    // translate anchor nodes from grandchild to child
                    Set<Node> grandchildResult =
                        ((SPORule) subCondition).getModifierEnds();
                    Map<Node,Node> grandchildRootMap =
                        subCondition.getRootMap().nodeMap();
                    for (Map.Entry<Node,Node> rootEntry : grandchildRootMap.entrySet()) {
                        if (grandchildResult.contains(rootEntry.getValue())) {
                            childResult.add(rootEntry.getKey());
                        }
                    }
                    // check coroot map for mergers
                    Set<Node> mergers = new HashSet<Node>();
                    Map<Node,Node> inverseCoroots = new HashMap<Node,Node>();
                    for (Map.Entry<Node,Node> coRootEntry : ((SPORule) subCondition).getCoRootMap().nodeMap().entrySet()) {
                        Node coRootSource = coRootEntry.getKey();
                        Node coRootTarget = coRootEntry.getValue();
                        if (inverseCoroots.containsKey(coRootTarget)) {
                            mergers.add(coRootSource);
                            mergers.add(inverseCoroots.get(coRootTarget));
                        } else {
                            inverseCoroots.put(coRootTarget, coRootSource);
                        }
                    }
                    // translate mergers to LHS
                    for (Map.Entry<Node,Node> lhsToRhsEntry : getMorphism().nodeMap().entrySet()) {
                        if (mergers.contains(lhsToRhsEntry.getValue())) {
                            result.add(lhsToRhsEntry.getKey());
                        }
                    }
                }
            }
            Map<Node,Node> childRootMap = condition.getRootMap().nodeMap();
            for (Map.Entry<Node,Node> rootEntry : childRootMap.entrySet()) {
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

    NodeEdgeMap getCoRootMap() {
        return this.coRootMap;
    }

    /**
     * Returns the creator edges between reader nodes.
     */
    final Edge[] getSimpleCreatorEdges() {
        if (this.simpleCreatorEdges == null) {
            this.simpleCreatorEdges = computeSimpleCreatorEdges();
        }
        return this.simpleCreatorEdges;
    }

    /**
     * Computes the creator edges between reader nodes.
     */
    private Edge[] computeSimpleCreatorEdges() {
        List<Edge> result = new ArrayList<Edge>();
        Set<Node> nonCreatorNodes = getCreatorMap().nodeMap().keySet();
        // iterate over all creator edges
        for (Edge edge : getCreatorEdges()) {
            // determine if this edge is simple
            if (nonCreatorNodes.containsAll(Arrays.asList(edge.ends()))) {
                result.add(edge);
            }
        }
        return result.toArray(new Edge[0]);
    }

    /**
     * Returns the creator edges that have at least one creator end.
     */
    public final Set<Edge> getComplexCreatorEdges() {
        if (this.complexCreatorEdges == null) {
            this.complexCreatorEdges = computeComplexCreatorEdges();
        }
        return this.complexCreatorEdges;
    }

    /**
     * Computes the creator edges that have at least one creator end.
     */
    private Set<Edge> computeComplexCreatorEdges() {
        Set<Edge> result = new HashSet<Edge>(Arrays.asList(getCreatorEdges()));
        result.removeAll(Arrays.asList(getSimpleCreatorEdges()));
        return result;
    }

    /**
     * Returns the RHS edges that are not images of an LHS edge.
     */
    final Edge[] getCreatorEdges() {
        if (this.creatorEdges == null) {
            this.creatorEdges = computeCreatorEdges();
        }
        return this.creatorEdges;
    }

    /**
     * Computes the creator (i.e., RHS-only) edges.
     */
    private Edge[] computeCreatorEdges() {
        Set<Edge> result = new HashSet<Edge>(rhs().edgeSet());
        result.removeAll(getMorphism().edgeMap().values());
        result.removeAll(getCoRootMap().edgeMap().values());
        return result.toArray(new Edge[0]);
    }

    /**
     * Returns the RHS nodes that are not images of an LHS node.
     */
    final public Node[] getCreatorNodes() {
        if (this.creatorNodes == null) {
            this.creatorNodes = computeCreatorNodes();
        }
        return this.creatorNodes;
    }

    /**
     * Computes the creator (i.e., RHS-only) nodes.
     */
    private Node[] computeCreatorNodes() {
        Set<Node> result = new HashSet<Node>(rhs().nodeSet());
        result.removeAll(getMorphism().nodeMap().values());
        result.removeAll(getCoRootMap().nodeMap().values());
        return result.toArray(new Node[0]);
    }

    /**
     * Returns the variables that occur in creator edges.
     * @see #getCreatorEdges()
     */
    final String[] getCreatorVars() {
        if (this.creatorVars == null) {
            this.creatorVars = computeCreatorVars();
        }
        return this.creatorVars;
    }

    /**
     * Computes the variables occurring in RHS edges.
     */
    private String[] computeCreatorVars() {
        Set<String> creatorVarSet = new HashSet<String>();
        for (int i = 0; i < getCreatorEdges().length; i++) {
            Edge creatorEdge = getCreatorEdges()[i];
            String creatorVar = RegExprLabel.getWildcardId(creatorEdge.label());
            if (creatorVar != null) {
                creatorVarSet.add(creatorVar);
            }
        }
        return creatorVarSet.toArray(new String[0]);
    }

    /**
     * Returns a sub-graph of the RHS consisting of the creator nodes and the
     * creator edges with their endpoints.
     */
    final Graph getCreatorGraph() {
        if (this.creatorGraph == null) {
            this.creatorGraph = computeCreatorGraph();
        }
        return this.creatorGraph;
    }

    /**
     * Computes a creator graph, consisting of the creator nodes together with
     * the creator edges and their endpoints.
     */
    private Graph computeCreatorGraph() {
        Graph result = rhs().newGraph();
        result.addNodeSet(Arrays.asList(getCreatorNodes()));
        result.addEdgeSet(Arrays.asList(getCreatorEdges()));
        return result;
    }

    /**
     * Returns a partial map from the nodes of the creator graph (see
     * {@link #getCreatorGraph()}) that are not themselves creator nodes but are
     * the ends of creator edges, to the corresponding nodes of the LHS.
     */
    final NodeEdgeMap getCreatorMap() {
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
    private NodeEdgeMap computeCreatorMap() {
        // construct rhsOnlyMap
        NodeEdgeMap result = new NodeEdgeHashMap();
        Set<? extends Node> creatorNodes = getCreatorGraph().nodeSet();
        for (Map.Entry<Node,Node> nodeEntry : getMorphism().nodeMap().entrySet()) {
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
    final Map<Node,Node> getMergeMap() {
        if (this.mergeMap == null) {
            this.mergeMap = computeMergeMap();
        }
        return this.mergeMap;
    }

    /**
     * Computes the merge map, which maps each LHS node that is merged with
     * others to the LHS node it is merged with.
     */
    private Map<Node,Node> computeMergeMap() {
        testFixed(true);
        Map<Node,Node> result = new HashMap<Node,Node>();
        Map<Node,Node> rhsToLhsMap = new HashMap<Node,Node>();
        for (Map.Entry<Node,Node> nodeEntry : getMorphism().elementMap().nodeMap().entrySet()) {
            Node mergeTarget = rhsToLhsMap.get(nodeEntry.getValue());
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
    final Edge[] getSimpleVarEdges() {
        if (this.varEdges == null) {
            this.varEdges = computeSimpleVarEdges();
        }
        return this.varEdges;
    }

    /**
     * Computes the set of variable-binding edges occurring in the lhs.
     */
    private Edge[] computeSimpleVarEdges() {
        return VarSupport.getSimpleVarEdges(lhs()).toArray(new Edge[0]);
    }

    /**
     * Lazily creates and returns the anchor graph of this rule. The anchor
     * graph is the smallest subgraph of the LHS that is necessary to apply the
     * rule. This means it contains all eraser edges and all variables and nodes
     * necessary for creation.
     */
    private Graph getAnchorGraph() {
        if (this.anchorGraph == null) {
            this.anchorGraph = computeAnchorGraph();
        }
        return this.anchorGraph;
    }

    /**
     * Computes the anchor graph of this rule.
     * @see #getAnchorGraph()
     */
    private Graph computeAnchorGraph() {
        Graph result = lhs().newGraph();
        for (Element elem : anchor()) {
            if (elem instanceof Node) {
                result.addNode((Node) elem);
            } else {
                result.addEdge((Edge) elem);
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
    private Set<Node> getMatchRelevantNodes() {
        if (this.matchRelevantNodes == null) {
            this.matchRelevantNodes = computeMatchRelevantGraph();
        }
        return this.matchRelevantNodes;
    }

    /**
     * Computes the match-relevant nodes of the left hand side.
     * @see #getMatchRelevantNodes()
     */
    private Set<Node> computeMatchRelevantGraph() {
        Set<Node> result = new HashSet<Node>();
        for (Element elem : anchor()) {
            if (elem instanceof Node) {
                result.add((Node) elem);
            } else {
                result.addAll(Arrays.asList(((Edge) elem).ends()));
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
            Node node = it.next();
            boolean resolved = false;
            for (int i = 1; i <= getVisibleParCount(); i++) {
                if (getParameter(i) == node && !isOutputParameter(i)) {
                    resolved = true;
                }
            }
            if (resolved) {
                it.remove();
            }
        }
    }

    Set<Node> getRequiredInputs() {
        if (this.requiredInputs == null) {
            this.requiredInputs = new HashSet<Node>();
        }
        return this.requiredInputs;
    }

    /**
     * Sets the requiredInputs to the given value.
     * @param requiredInputs a {@code Set<Node>} that describes which Nodes must
     * be given by a control program as input 
     */
    public void setRequiredInputs(Set<Node> requiredInputs) {
        this.requiredInputs = requiredInputs;
    }

    //
    // /**
    // * Initialises the parameter map.
    // * @see #getParameterMap()
    // */
    // public void setParameterMap(Map<Integer,Node> map) {
    // this.parameterNodeMap = map;
    // }
    //
    // /**
    // * Returns the parameter map of this rule. This is a map from parameter
    // numbers
    // * (in a consecutive range starting at 0) to parameter nodes, which
    // * are nodes in the LHS.
    // * @return the parameter map of this rule; may be <code>null</code>
    // */
    // public Map<Integer,Node> getParameterMap() {
    // return this.parameterNodeMap;
    // }

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
    private final Morphism morphism;
    /**
     * This production rule's left hand side.
     * @invariant lhs != null
     */
    private Graph lhs;
    /**
     * This production rule's right hand side.
     * @invariant rhs != null
     */
    private Graph rhs;
    /** Mapping from the context of this rule to the RHS. */
    private final NodeEdgeMap coRootMap;
    /**
     * Smallest subgraph of the left hand side that is necessary to apply the
     * rule.
     */
    private Graph anchorGraph;
    /**
     * Subgraph of the left hand containing all elements that are used to
     * distinguish matches.
     */
    private Set<Node> matchRelevantNodes;
    /**
     * A sub-graph of the production rule's right hand side, consisting only of
     * the fresh nodes and edges.
     */
    private Graph creatorGraph;
    /**
     * A map from the nodes of <tt>rhsOnlyGraph</tt> to <tt>lhs</tt>, which is
     * the restriction of the inverse of <tt>ruleMorph</tt> to
     * <tt>rhsOnlyGraph</tt>.
     */
    private NodeEdgeMap creatorMap;
    /**
     * The lhs nodes that are not ruleMorph keys
     * @invariant lhsOnlyNodes \subseteq lhs.nodeSet()
     */
    private Node[] eraserNodes;
    /**
     * The lhs edges that are not ruleMorph keys
     * @invariant lhsOnlyEdges \subseteq lhs.edgeSet()
     */
    private Edge[] eraserEdges;
    /**
     * The set of anchors of this rule.
     */
    private Element[] anchor;
    /**
     * The lhs edges that are not ruleMorph keys and are not anchors
     */
    private Edge[] eraserNonAnchorEdges;
    /**
     * The lhs edges containing bound variables.
     */
    private Edge[] varEdges;
    /**
     * The lhs nodes that are end points of eraser edges, either in this rule or
     * one of its sub-rules.
     */
    private Set<Node> modifierEnds;
    /**
     * The LHS nodes that do not have any incident edges in the LHS.
     */
    private Node[] isolatedNodes;
    /**
     * The rhs nodes that are not ruleMorph images
     * @invariant creatorNodes \subseteq rhs.nodeSet()
     */
    private Node[] creatorNodes;

    /**
     * The rhs edges that are not ruleMorph images
     */
    private Edge[] creatorEdges;
    /**
     * The rhs edges that are not ruleMorph images but with all ends morphism
     * images
     */
    private Edge[] simpleCreatorEdges;
    /**
     * The rhs edges with at least one end not a morphism image
     */
    private Set<Edge> complexCreatorEdges;
    /**
     * Variables occurring in the rhsOnlyEdges
     */
    private String[] creatorVars;
    /**
     * A partial mapping from LHS nodes to RHS nodes, indicating which nodes are
     * merged and which nodes are deleted.
     */
    private Map<Node,Node> mergeMap;

    private GraphProperties ruleProperties;

    /**
     * List of numbered parameters.
     */
    private List<Node> inPars;
    /**
     * List of numbered creator-parameters
     */
    private List<Node> outPars;
    /**
     * Set of anonymous (unnumbered) parameters.
     */
    private Set<Node> hiddenPars;
    /** The matcher for events of this rule. */
    private MatchStrategy<VarNodeEdgeMap> eventMatcher;

    //
    // /**
    // * Implementation of ParameterAspect stuff
    // */
    // private Map<Integer,Node> parameterNodeMap;

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

    private void debug(String msg) {
        // System.err.println("Variable debug (SPORule): "+msg);
    }

    /**
     * The factory used for creating rule anchors.
     */
    private static AnchorFactory<SPORule> anchorFactory =
        MinimalAnchorFactory.getInstance();
    /** Debug flag for the constructor. */
    private static final boolean PRINT = false;

    private final Map<Integer,Integer> parameterTypes =
        new HashMap<Integer,Integer>();

    private Map<Integer,Integer> specifiedParameterTypes;
    private Map<Integer,String> attributeParameterTypes;

    /**
     * The set of nodes required as input parameters from a control program.
     */
    protected Set<Node> requiredInputs;

    @Override
    public GraphProperties getRuleProperties() {
        return this.ruleProperties;
    }
}
