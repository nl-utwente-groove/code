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
import groove.util.Visitor;
import groove.view.FormatError;
import groove.view.FormatException;

import java.util.ArrayList;
import java.util.BitSet;
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
 * Class providing common functionality for {@link Condition} implementations.
 * @author Arend Rensink
 * @version $Revision$
 */
abstract public class AbstractCondition<M extends Match> implements Condition {
    /**
     * Constructs a (named) graph condition based on a given pattern graph
     * and root map.
     * @param name the name of the condition; may be <code>null</code>
     * @param pattern the graph to be matched
     * @param rootMap element map from the root to the seed elements of
     *        <code>pattern</code>; may be <code>null</code> if the condition is
     *        ground
     * @param properties properties for matching the condition
     */
    protected AbstractCondition(RuleName name, RuleGraph pattern,
            RuleGraphMorphism rootMap, SystemProperties properties) {
        this.rootMap = rootMap == null ? new RuleGraphMorphism() : rootMap;
        this.pattern = pattern;
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

    /** 
     * Returns the root nodes of this condition.
     * These are the nodes that it has in common with its parent
     * in the condition tree.
     */
    final public Set<RuleNode> getRootNodes() {
        if (this.rootNodes == null) {
            this.rootNodes = computeRootNodes();
        }
        return this.rootNodes;
    }

    /** 
     * Computes the root nodes of this condition.
     * These are the nodes that it has in common with its parent
     * in the condition tree.
     */
    Set<RuleNode> computeRootNodes() {
        return getRootMap().nodeMap().keySet();
    }

    /** 
     * Returns the root edges of this condition.
     * These are the edges that it has in common with its parent
     * in the condition tree.
     */
    final public Set<RuleEdge> getRootEdges() {
        if (this.rootEdges == null) {
            this.rootEdges = computeRootEdges();
        }
        return this.rootEdges;
    }

    /** 
     * Computes the root edges of this condition.
     * These are the edges that it has in common with its parent
     * in the condition tree.
     */
    Set<RuleEdge> computeRootEdges() {
        return getRootMap().edgeMap().keySet();
    }

    final public Set<LabelVar> getRootVars() {
        if (this.rootVars == null) {
            this.rootVars = new HashSet<LabelVar>();
            for (RuleEdge rootEdge : getRootEdges()) {
                this.rootVars.addAll(VarSupport.getAllVars(rootEdge));
            }
        }
        return this.rootVars;
    }

    /**
     * Returns the target set at construction time.
     */
    public RuleGraph getPattern() {
        return this.pattern;
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
        assert isFixed();
        return this.ground;
    }

    /**
     * This implementation does nothing
     */
    public void testConsistent() throws FormatException {
        // empty
    }

    /**
     * Tests if the pattern graph of the condition or any
     * of its subconditions contains nodes without incident edges
     * that are not part of the seed.
     */
    private boolean hasIsolatedNodes() {
        boolean result = false;
        // first test if the pattern has isolated nodes
        Set<RuleNode> freshTargetNodes =
            new HashSet<RuleNode>(getPattern().nodeSet());
        // subtract the seed nodes
        freshTargetNodes.removeAll(getRootMap().nodeMap().values());
        Iterator<RuleNode> nodeIter = freshTargetNodes.iterator();
        while (!result && nodeIter.hasNext()) {
            result = getPattern().edgeSet(nodeIter.next()).isEmpty();
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
        if (!(condition instanceof NotCondition)) {
            getComplexSubConditions().add((AbstractCondition<?>) condition);
        }
    }

    /**
     * Returns the set of sub-conditions that are <i>not</i>
     * {@link NotCondition}s.
     * These are the conditions that are not part of the search plan,
     * and so have to be matched explicitly.
     */
    @SuppressWarnings("unchecked")
    protected <C extends AbstractCondition<?>> Collection<C> getComplexSubConditions() {
        if (this.complexSubConditions == null) {
            this.complexSubConditions = new ArrayList<AbstractCondition<?>>();
        }
        return (Collection<C>) this.complexSubConditions;
    }

    /** Fixes this condition and all its subconditions. */
    public void setFixed() throws FormatException {
        if (!isFixed()) {
            this.fixed = true;
            this.ground = this.getRootNodes().isEmpty();
            getPattern().setFixed();
            testAlgebra();
            for (AbstractCondition<?> subCondition : getSubConditions()) {
                subCondition.setFixed();
            }
        }
    }

    public boolean isFixed() {
        return this.fixed;
    }

    final public boolean hasMatch(HostGraph host) {
        return isGround() && getMatch(host, null) != null;
    }

    @Override
    public M getMatch(HostGraph host, RuleToHostMap contextMap) {
        return traverseMatches(host, contextMap, Visitor.<M>newFinder(null));
    }

    @Override
    public Collection<M> getAllMatches(HostGraph host, RuleToHostMap contextMap) {
        List<M> result = new ArrayList<M>();
        traverseMatches(host, contextMap, Visitor.newCollector(result));
        return result;
    }

    /** 
     * Traverses and visits the matches of this condition
     * for a given host graph and context map.
     */
    abstract public <R> R traverseMatches(final HostGraph host,
            RuleToHostMap contextMap, final Visitor<M,R> visitor);

    /** 
     * Increments a given vector of values lexicographically,
     * up to a given maximum value at each index.
     * @param vector the vector to be incremented
     * @param size array of maximum values at each vector index
     * @return {@code true} if the new value is positive
     */
    final boolean incVector(int[] vector, int[] size) {
        boolean result;
        assert vector.length == size.length;
        int dim = size.length - 1;
        // search for the lest significant dimension for which the
        // vector value does not yet equal the row size
        while (dim >= 0 && vector[dim] == size[dim] - 1) {
            vector[dim] = 0;
            dim--;
        }
        result = dim >= 0;
        if (result) {
            vector[dim]++;
        }
        return result;
    }

    @Deprecated
    final public Iterator<M> getMatchIter(HostGraph host,
            RuleToHostMap contextMap) {
        Iterator<M> result;
        testFixed(true);
        RuleToHostMap seedMap = computeSeedMap(host, contextMap);
        if (seedMap == null) {
            // the context map could not be lifted to this condition
            result = Collections.<M>emptySet().iterator();
        } else {
            result =
                computeMatchIter(host, getMatcher().getMatchIter(host, seedMap));
        }
        return result;
    }

    /**
     * Returns an iterator over the matches into a given graph, based on a series
     * of pattern maps for this condition.
     */
    @Deprecated
    abstract Iterator<M> computeMatchIter(HostGraph host,
            Iterator<RuleToHostMap> patternMaps);

    /** Computes the seed map for a given context map. */
    final RuleToHostMap computeSeedMap(HostGraph host, RuleToHostMap contextMap) {
        if (isGround()) {
            return host.getFactory().createRuleToHostMap();
        } else {
            return createSeedMap(contextMap);
        }
    }

    /**
     * Factors a given matching of the condition root through this condition's
     * root map, to obtain a partial matching of {@link #getPattern()}. To be
     * precise, the result will map the seed of the condition to the host graph.
     * 
     * @return a mapping that, concatenated after this condition's root map, is
     *         a sub-map of <code>contextMap</code>; or <code>null</code> if
     *         there is no such mapping.
     */
    private RuleToHostMap createSeedMap(RuleToHostMap contextMap) {
        RuleToHostMap result = contextMap.newMap();
        for (RuleNode node : getRootNodes()) {
            if (!isSeedable(node)) {
                continue;
            }
            HostNode image = contextMap.getNode(node);
            if (image == null) {
                // this root is not a seed;
                // this may happen if it is an output parameter or count node
                continue;
            }
            RuleNode key = node;
            // result already contains an image for nodeKey
            // if it is not the same as the one we want to insert now,
            // stop the whole thing; otherwise we're fine
            Node oldImage = result.putNode(key, image);
            if (oldImage != null && !oldImage.equals(image)) {
                return null;
            }
        }
        for (RuleEdge edge : getRootEdges()) {
            if (!isSeedable(edge)) {
                continue;
            }
            HostEdge image = contextMap.mapEdge(edge);
            assert image != null : String.format(
                "Context map %s does not contain image for root %s",
                contextMap, edge);
            RuleEdge key = edge;
            // result already contains an image for nodeKey
            // if it is not the same as the one we want to insert now,
            // stop the whole thing; otherwise we're fine
            HostEdge oldImage = result.putEdge(key, image);
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
    boolean isSeedable(RuleNode node) {
        return !(node instanceof ProductNode) || node instanceof VariableNode;
    }

    /**
     * Tests is a give edge is a proper anchor, in the sense that it is matched
     * to an actual host graph edge. This fails to hold for {@link ArgumentEdge}
     * s and {@link OperatorEdge}s.
     */
    boolean isSeedable(RuleEdge edge) {
        return !(edge instanceof ArgumentEdge || edge instanceof OperatorEdge);
    }

    /**
     * Returns a (precomputed) match strategy for the target
     * pattern, given a seed map.
     * @see #createMatcher(Set, Set)
     */
    public MatchStrategy<RuleToHostMap> getMatcher() {
        if (this.matchStrategy == null) {
            this.matchStrategy = createMatcher(getRootNodes(), getRootEdges());
        }
        return this.matchStrategy;
    }

    /**
     * Callback method to create a match strategy. Typically invoked once, at
     * the first invocation of {@link #getMatcher()}. This implementation
     * retrieves its value from {@link #getMatcherFactory()}.
     * @param seedNodes the pre-matched rule nodes
     * @param seedEdges the pre-matched rule edges
     */
    MatchStrategy<RuleToHostMap> createMatcher(Set<RuleNode> seedNodes,
            Set<RuleEdge> seedEdges) {
        testFixed(true);
        return getMatcherFactory().createMatcher(this, seedNodes, seedEdges,
            getRelevantNodes());
    }

    /** Returns a matcher factory, tuned to the properties of this condition. */
    SearchEngine<? extends MatchStrategy<RuleToHostMap>> getMatcherFactory() {
        if (this.matcherFactory == null) {
            this.matcherFactory =
                groove.match.SearchEngineFactory.getInstance().getEngine(
                    getSystemProperties());
        }
        return this.matcherFactory;
    }

    /**
     * Forces the condition and all of its sub-conditions to re-acquire 
     * a new instance of its cached matcher object from the  
     * search engine factory. 
     * This is necessary to enable exploration strategies
     * to effectively change the matching engine factory.
     */
    final public void resetMatcher() {
        this.matcherFactory = null;
        invalidateMatchers();
        if (this.subConditions != null) {
            for (AbstractCondition<?> c : this.getSubConditions()) {
                c.resetMatcher();
            }
        }
    }

    /**
     * Resets the internally stored matchers, so that next time they will
     * be recreated from the (possibly changed) matcher factory.
     */
    void invalidateMatchers() {
        this.matchStrategy = null;
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
     * the root graph is empty), as determined by {@link #isGround()}.
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

    /**
     * Tests if the algebra part of the target graph can be matched. This
     * requires that there are no variable nodes that cannot be resolved, no
     * typing conflicts, and no missing arguments. This is checked at fixing
     * time of the condition.
     * @throws FormatException if the algebra part cannot be matched
     */
    private void testAlgebra() throws FormatException {
        Set<FormatError> errors = new TreeSet<FormatError>();
        computeUnresolvedNodes();
        stabilizeUnresolvedNodes();
        for (RuleNode node : this.unresolvedVariableNodes) {
            errors.add(new FormatError(
                "Cannot resolve attribute value node '%s'", node));
        }
        if (!this.unresolvedProductNodes.isEmpty()) {
            Map.Entry<ProductNode,BitSet> productEntry =
                this.unresolvedProductNodes.entrySet().iterator().next();
            ProductNode product = productEntry.getKey();
            BitSet arguments = productEntry.getValue();
            if (arguments.cardinality() != product.arity()) {
                arguments.flip(0, product.arity());
                errors.add(new FormatError(
                    "Argument edges %s of product node %s missing in sub-condition",
                    arguments, product));
            }
        }
        if (!errors.isEmpty()) {
            throw new FormatException(errors);
        }
    }

    /**
     * Calculates whether any VariableNode or ProductNode is unresolved,
     * in the sense of not having an incoming attribute edge or being
     * part of the seed
     */
    protected void computeUnresolvedNodes() {
        this.unresolvedVariableNodes = new HashSet<VariableNode>();
        this.unresolvedProductNodes = new HashMap<ProductNode,BitSet>();
        // test if product nodes have the required arguments
        for (RuleNode node : getPattern().nodeSet()) {
            if (node instanceof VariableNode
                && ((VariableNode) node).getConstant() == null) {
                boolean hasIncomingNonAttributeEdge = false;
                for (RuleEdge edge : getPattern().inEdgeSet(node)) {
                    if (edge.label().isMatchable()) {
                        hasIncomingNonAttributeEdge = true;
                    }
                }
                if (!hasIncomingNonAttributeEdge) {
                    this.unresolvedVariableNodes.add((VariableNode) node);
                }
            } else if (node instanceof ProductNode) {
                ProductNode product = (ProductNode) node;
                this.unresolvedProductNodes.put(product,
                    new BitSet(product.arity()));
            }
        }
        // remove the seed nodes
        this.unresolvedVariableNodes.removeAll(getRootMap().nodeMap().values());
        for (RuleNode node : getRootMap().nodeMap().values()) {
            this.unresolvedProductNodes.remove(node);
        }
    }

    /**
     * Iterates over unresolved nodes and removes them as necessary. This will 
     * look at each node in unresolvedProductNodes; if all of its arguments have
     * been resolved it will remove all of the product "targets" from the
     * {@code unresolvedVariableNodes} Set. It will keep doing this until both
     * collections are stable.
     * @throws FormatException if an error is detected during stabilisation
     */
    protected void stabilizeUnresolvedNodes() throws FormatException {
        // now resolve nodes until stable
        boolean stable = false;
        while (!stable) {
            stable = true;
            java.util.Iterator<Map.Entry<ProductNode,BitSet>> productIter =
                this.unresolvedProductNodes.entrySet().iterator();
            while (productIter.hasNext()) {
                Map.Entry<ProductNode,BitSet> productEntry = productIter.next();
                ProductNode product = productEntry.getKey();
                BitSet arguments = productEntry.getValue();
                for (RuleEdge edge : getPattern().outEdgeSet(product)) {
                    if (edge instanceof ArgumentEdge
                        && !this.unresolvedVariableNodes.contains(edge.target())) {
                        int argumentNumber = ((ArgumentEdge) edge).getNumber();
                        arguments.set(argumentNumber);
                    }
                }
                if (arguments.cardinality() == product.arity()) {
                    // the product node is resolved, so resolve the targets of
                    // the outgoing operations
                    for (RuleEdge edge : getPattern().outEdgeSet(product)) {
                        if (edge instanceof OperatorEdge) {
                            if (this.unresolvedVariableNodes.remove(((OperatorEdge) edge).target())) {
                                stable = false;
                            }
                        }
                    }
                    productIter.remove();
                }
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder res =
            new StringBuilder(String.format("Condition %s: ", getName()));
        res.append(String.format("Target: %s", getPattern()));
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
     * Lazily creates and returns the set of match-relevant nodes of this rule.
     * These are the nodes whose images are important to distinguish rule
     * matches. The set consists of the anchor nodes and the root sources of the
     * universal sub-conditions.
     */
    private Set<RuleNode> getRelevantNodes() {
        if (this.anchorNodes == null) {
            this.anchorNodes = computeRelevantNodes();
        }
        return this.anchorNodes;
    }

    /**
     * Computes the match-relevant nodes of this condition
     * @see #getRelevantNodes()
     */
    Set<RuleNode> computeRelevantNodes() {
        Set<RuleNode> result = new HashSet<RuleNode>();
        // add the root map sources of the sub-conditions
        for (AbstractCondition<?> subCondition : getComplexSubConditions()) {
            result.addAll(subCondition.getRootMap().nodeMap().keySet());
        }
        return result;
    }

    /**
     * The name of this condition. May be <code>code</code> null.
     */
    private RuleName name;

    /** The factory for match strategies. */
    private SearchEngine<? extends MatchStrategy<RuleToHostMap>> matcherFactory;
    /**
     * The fixed matching strategy for this graph condition. Initially
     * <code>null</code>; set by {@link #getMatcher()} upon its first
     * invocation.
     */
    private MatchStrategy<RuleToHostMap> matchStrategy;

    /** The collection of sub-conditions of this condition. */
    private Collection<AbstractCondition<?>> subConditions;

    /**
     * The sub-conditions that are not negative application conditions.
     */
    private Collection<AbstractCondition<?>> complexSubConditions;

    /** Flag indicating if this condition is now fixed, i.e., unchangeable. */
    private boolean fixed;
    /**
     * Flag indicating if the rule is ground.
     */
    private boolean ground;
    /**
     * The root map of this condition, i.e., the element map from the root
     * graph to the pattern graph.
     */
    private final RuleGraphMorphism rootMap;

    /** Set of all nodes this condition has in common with its parent. */
    private Set<RuleNode> rootNodes;

    /** Set of all variables occurring in root elements. */
    private Set<RuleEdge> rootEdges;

    /** Set of all variables occurring in root edges. */
    private Set<LabelVar> rootVars;

    /** The pattern graph of this morphism. */
    private final RuleGraph pattern;

    /**
     * Factory instance for creating the correct simulation.
     */
    protected final SystemProperties systemProperties;
    /** Subtyping relation, derived from the SystemProperties. */
    private LabelStore labelStore;

    /**
     * Set of VariableNodes that have not been resolved, i.e. variable nodes
     * that have no incoming match-edges.
     */
    protected Set<VariableNode> unresolvedVariableNodes;

    /**
     * A map of unresolved product nodes, i.e. product nodes which have 
     * unresolved arguments.
     */
    protected Map<ProductNode,BitSet> unresolvedProductNodes;

    /**
     * Subgraph of the left hand containing all elements that are used to
     * distinguish matches.
     */
    private Set<RuleNode> anchorNodes;
}