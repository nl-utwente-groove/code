/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * $Id: AbstractCondition.java,v 1.15 2008-02-29 11:02:20 fladder Exp $
 */
package groove.trans;

import groove.graph.Edge;
import groove.graph.LabelStore;
import groove.graph.Node;
import groove.graph.TypeLabel;
import groove.graph.algebra.ArgumentEdge;
import groove.graph.algebra.OperatorEdge;
import groove.graph.algebra.ProductNode;
import groove.graph.algebra.VariableNode;
import groove.match.MatchStrategy;
import groove.match.SearchEngine;
import groove.rel.LabelVar;
import groove.rel.VarSupport;
import groove.view.FormatException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author Arend Rensink
 * @version $Revision$
 */
abstract public class AbstractCondition<M extends Match> implements Condition {
    /**
     * Constructs a (named) graph condition based on a given graph to be matched
     * and root map.
     * @param name the name of the condition; may be <code>null</code>
     * @param target the graph to be matched
     * @param rootMap element map from the context to the anchor elements of
     *        <code>target</code>; may be <code>null</code> if the condition is
     *        ground
     * @param properties properties for matching the condition
     */
    protected AbstractCondition(RuleName name, RuleGraph target,
            RuleGraphMorphism rootMap, SystemProperties properties) {
        this.ground = (rootMap == null);
        this.rootMap = this.ground ? new RuleGraphMorphism() : rootMap;
        this.target = target;
        this.systemProperties = properties;
        this.name = name;
    }

    /**
     * Returns the properties set at construction time.
     */
    public SystemProperties getSystemProperties() {
        return this.systemProperties;
    }

    @Override
    public void setLabelStore(LabelStore labelStore) {
        assert labelStore != null;
        this.labelStore = labelStore;
        for (Condition sub : getSubConditions()) {
            sub.setLabelStore(labelStore);
        }
    }

    @Override
    public LabelStore getLabelStore() {
        return this.labelStore;
    }

    public RuleGraphMorphism getRootMap() {
        return this.rootMap;
    }

    public Set<LabelVar> getRootVars() {
        if (this.rootVars == null) {
            this.rootVars = new HashSet<LabelVar>();
            for (RuleEdge rootEdge : getRootMap().edgeMap().keySet()) {
                this.rootVars.addAll(VarSupport.getAllVars(rootEdge));
            }
        }
        return this.rootVars;
    }

    /**
     * Returns the target set at construction time.
     */
    public RuleGraph getTarget() {
        return this.target;
    }

    /**
     * Returns the name set at construction time.
     */
    public RuleName getName() {
        return this.name;
    }

    /**
     * Sets the name of this condition, if the condition is not fixed. The name
     * is assumed to be as yet unset.
     */
    public void setName(RuleName name) {
        testFixed(false);
        assert this.name == null : String.format(
            "Condition name already set to %s", name);
        this.name = name;
    }

    public boolean isGround() {
        return this.ground;
    }

    /**
     * This implementation does nothing
     */
    public void testConsistent() throws FormatException {
        // String attributeKey = SystemProperties.ATTRIBUTES_KEY;
        // String attributeProperty = getProperties().getProperty(attributeKey);
        // if (getProperties().isAttributed()) {
        // if (hasIsolatedNodes()) {
        // throw new FormatException(
        // "Condition tests isolated nodes, conflicting with \"%s=%s\"",
        // attributeKey, attributeProperty);
        // }
        // } else if (hasAttributes()) {
        // // if(getProperties().useParameters()) {
        // // throw new FormatException(
        // // "LTS Parameters are enabled without support for attributes");
        // // }
        // //
        // if (attributeProperty == null) {
        // throw new FormatException(
        // "Condition uses attributes, but \"%s\" not declared",
        // attributeKey);
        // } else {
        // throw new FormatException(
        // "Condition uses attributes, violating \"%s=%s\"",
        // attributeKey, attributeProperty);
        // }
        // }
    }

    //
    // /**
    // * Returns <code>true</code> if the target graph of the condition contains
    // * {@link ValueNode}s, or the negative conjunct is attributed.
    // */
    // private boolean hasAttributes() {
    // boolean result = ValueNode.hasValueNodes(getTarget());
    // if (result) {
    // Iterator<AbstractCondition<?>> subConditionIter =
    // getSubConditions().iterator();
    // while (!result && subConditionIter.hasNext()) {
    // result = subConditionIter.next().hasAttributes();
    // }
    // }
    // return result;
    // }

    /**
     * Tests if the target graph of the condition contains nodes without
     * incident edges.
     */
    private boolean hasIsolatedNodes() {
        boolean result = false;
        // first test if the pattern target has isolated nodes
        Set<RuleNode> freshTargetNodes =
            new HashSet<RuleNode>(getTarget().nodeSet());
        freshTargetNodes.removeAll(getRootMap().nodeMap().values());
        Iterator<RuleNode> nodeIter = freshTargetNodes.iterator();
        while (!result && nodeIter.hasNext()) {
            result = getTarget().edgeSet(nodeIter.next()).isEmpty();
        }
        if (!result) {
            // now recursively test the sub-conditions
            Iterator<AbstractCondition<?>> subConditionIter =
                getSubConditions().iterator();
            while (!result && subConditionIter.hasNext()) {
                result = subConditionIter.next().hasIsolatedNodes();
            }
        }
        return result;
    }

    public Collection<AbstractCondition<?>> getSubConditions() {
        if (this.subConditions == null) {
            this.subConditions = new ArrayList<AbstractCondition<?>>();
        }
        return this.subConditions;
    }

    public void addSubCondition(Condition condition) {
        testFixed(false);
        assert condition instanceof AbstractCondition<?> : String.format(
            "Condition %s should be an AbstractCondition", condition);
        if (this.labelStore != null) {
            condition.setLabelStore(this.labelStore);
        }
        getSubConditions().add((AbstractCondition<?>) condition);
    }

    /** Fixes the sub-predicate and this morphism. */
    public void setFixed() throws FormatException {
        if (!isFixed()) {
            this.fixed = true;
            getTarget().setFixed();
            for (AbstractCondition<?> subCondition : getSubConditions()) {
                subCondition.setFixed();
            }
        }
    }

    public boolean isFixed() {
        return this.fixed;
    }

    final public boolean hasMatch(HostGraph host) {
        return isGround() && getMatchIter(host, null).hasNext();
    }

    /**
     * Returns an iterable wrapping a call to
     * {@link #getMatchIter(HostGraph, RuleToHostMap)}.
     */
    public Iterable<M> getMatches(final HostGraph host,
            final RuleToHostMap contextMap) {
        return new Iterable<M>() {
            public Iterator<M> iterator() {
                return getMatchIter(host, contextMap);
            }
        };
    }

    final public Iterator<M> getMatchIter(HostGraph host,
            RuleToHostMap contextMap) {
        Iterator<M> result = null;
        testFixed(true);
        // lift the pattern match to a pre-match of this condition's target
        final RuleToHostMap anchorMap;
        if (contextMap == null) {
            testGround();
            anchorMap = host.getFactory().createRuleToHostMap();
        } else {
            if (isGround()) {
                anchorMap = contextMap.clone();
            } else {
                anchorMap = createAnchorMap(contextMap);
            }
        }
        if (anchorMap == null) {
            // the context map could not be lifted to this condition
            result = Collections.<M>emptySet().iterator();
        } else {
            result =
                computeMatchIter(host,
                    getMatcher().getMatchIter(host, anchorMap));
        }
        return result;
    }

    /**
     * Returns an iterator over the matches for a given graph, based on a series
     * of match maps for this condition.
     */
    abstract Iterator<M> computeMatchIter(HostGraph host,
            Iterator<RuleToHostMap> matchMaps);

    /**
     * Factors given matching of the condition context through this condition's
     * root map, to obtain a matching of {@link #getTarget()}.
     * 
     * @return a mapping that, concatenated after this condition's root map, is
     *         a sub-map of <code>contextMap</code>; or <code>null</code> if
     *         there is no such mapping.
     */
    final RuleToHostMap createAnchorMap(RuleToHostMap contextMap) {
        RuleToHostMap result = contextMap.newMap();
        for (Map.Entry<RuleNode,RuleNode> entry : getRootMap().nodeMap().entrySet()) {
            if (!isAnchorable(entry.getKey())) {
                continue;
            }
            HostNode image = contextMap.getNode(entry.getKey());
            assert image != null : String.format(
                "Context map %s in condition '%s' does not contain image for root %s",
                contextMap, getName(), entry.getKey());
            RuleNode key = entry.getValue();
            // result already contains an image for nodeKey
            // if it is not the same as the one we want to insert now,
            // stop the whole thing; otherwise we're fine
            Node oldImage = result.putNode(key, image);
            if (oldImage != null && !oldImage.equals(image)) {
                return null;
            }
        }
        for (Map.Entry<RuleEdge,RuleEdge> entry : getRootMap().edgeMap().entrySet()) {
            if (!isAnchorable(entry.getKey())) {
                continue;
            }
            HostEdge image = contextMap.mapEdge(entry.getKey());
            assert image != null : String.format(
                "Context map %s does not contain image for root %s",
                contextMap, entry.getKey());
            RuleEdge key = entry.getValue();
            // result already contains an image for nodeKey
            // if it is not the same as the one we want to insert now,
            // stop the whole thing; otherwise we're fine
            Edge oldImage = result.putEdge(key, image);
            if (oldImage != null && !oldImage.equals(image)) {
                return null;
            }
        }
        for (LabelVar var : getRootVars()) {
            TypeLabel image = contextMap.getVar(var);
            if (image == null) {
                return null;
            } else {
                result.putVar(var, image);
            }
        }
        return result;
    }

    /**
     * Tests is a give node can serve proper anchor, in the sense that it is
     * matched to an actual host graph node. This fails to hold for
     * {@link ProductNode}s that are not {@link VariableNode}s.
     */
    boolean isAnchorable(RuleNode node) {
        return !(node instanceof ProductNode) || node instanceof VariableNode;
    }

    /**
     * Tests is a give edge is a proper anchor, in the sense that it is matched
     * to an actual host graph edge. This fails to hold for {@link ArgumentEdge}
     * s and {@link OperatorEdge}s.
     */
    boolean isAnchorable(Edge edge) {
        return !(edge instanceof ArgumentEdge || edge instanceof OperatorEdge);
    }

    /**
     * Forces the condition and all of its sub-conditions to re-acquire 
     * a new instance of it's cached matcher object from the matching 
     * search engine. 
     * 
     * This method had to be added to enable exploration strategies
     * to lock down to a specific matching engine and in turn enable them
     * to tell all rules and (sub)conditions in the GTS to use the matcher 
     * that corresponds with the locked-down engine.
     * 
     */
    public void resetMatcher() {
        this.matchStrategy = null;
        if (this.subConditions != null) {
            for (AbstractCondition<?> c : this.getSubConditions()) {
                c.resetMatcher();
            }
        }
    }

    /**
     * Returns the precomputed matching order for the elements of the target
     * pattern. First creates the order using {@link #createMatcher()} if that
     * has not been done.
     * 
     * @see #createMatcher()
     */
    final public MatchStrategy<RuleToHostMap> getMatcher() {
        if (this.matchStrategy == null) {
            this.matchStrategy = createMatcher();
        }
        return this.matchStrategy;
    }

    /**
     * Callback method to create a matching factory. Typically invoked once, at
     * the first invocation of {@link #getMatcher()}. This implementation
     * retrieves its value from {@link #getMatcherFactory()}.
     */
    MatchStrategy<RuleToHostMap> createMatcher() {
        testFixed(true);
        return getMatcherFactory().createMatcher(this);
    }

    /** Returns a matcher factory, tuned to the injectivity of this condition. */
    SearchEngine<? extends MatchStrategy<RuleToHostMap>> getMatcherFactory() {
        //return groove.match.ConditionSearchPlanFactory.getInstance();
        return groove.match.SearchEngineFactory.getInstance().getEngine(
            getSystemProperties().isInjective());
    }

    /**
     * Tests if the condition is fixed or not. Throws an exception if the
     * fixedness does not coincide with the given value.
     * 
     * @param value the expected fixedness state
     * @throws IllegalStateException if {@link #isFixed()} does not yield
     *         <code>value</code>
     */
    public void testFixed(boolean value) throws IllegalStateException {
        if (isFixed() != value) {
            String message;
            if (value) {
                message = "Graph condition should be fixed in this state";
            } else {
                message = "Graph condition should not be fixed in this state";
            }
            throw new IllegalStateException(message);
        }
    }

    /**
     * Tests if the condition can be used to tests on graphs rather than
     * morphisms. This is the case if and only if the condition is ground (i.e.,
     * the context graph is empty), as determined by {@link #isGround()}.
     * 
     * @throws IllegalStateException if this condition is not ground.
     * @see #isGround()
     */
    void testGround() throws IllegalStateException {
        if (!isGround()) {
            throw new IllegalStateException(
                "Method only allowed on ground condition");
        }
    }

    @Override
    public String toString() {
        StringBuilder res =
            new StringBuilder(String.format("Condition %s: ", getName()));
        res.append(String.format("Target: %s", getTarget()));
        if (!getRootMap().isEmpty()) {
            res.append(String.format("%nRoot map: %s", getRootMap()));
        }
        if (!getSubConditions().isEmpty()) {
            res.append(String.format("%nSubconditions:"));
            for (Condition subCondition : getSubConditions()) {
                res.append(String.format("%n    %s", subCondition));
            }
        }
        return res.toString();
    }

    /**
     * The name of this condition. May be <code>code</code> null.
     */
    private RuleName name;

    /**
     * The fixed matching strategy for this graph condition. Initially
     * <code>null</code>; set by {@link #getMatcher()} upon its first
     * invocation.
     */
    private MatchStrategy<RuleToHostMap> matchStrategy;

    /** The collection of sub-conditions of this condition. */
    private Collection<AbstractCondition<?>> subConditions;

    /** Flag indicating if this condition is now fixed, i.e., unchangeable. */
    private boolean fixed;
    /**
     * Flag indicating if the rule is ground.
     */
    private final boolean ground;
    /**
     * The pattern map of this condition, i.e., the element map from the context
     * graph to the target graph.
     */
    private final RuleGraphMorphism rootMap;

    /** Set of all variables occurring in root elements. */
    private Set<LabelVar> rootVars;

    /** The target graph of this morphism. */
    private final RuleGraph target;

    /**
     * Factory instance for creating the correct simulation.
     */
    protected final SystemProperties systemProperties;
    /** Subtyping relation, derived from the SystemProperties. */
    private LabelStore labelStore;
}