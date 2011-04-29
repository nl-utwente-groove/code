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
import groove.graph.algebra.OperatorEdge;
import groove.graph.algebra.ProductNode;
import groove.graph.algebra.VariableNode;
import groove.util.Fixable;
import groove.view.FormatError;
import groove.view.FormatException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Type of conditions over graphs.
 * A condition is a hierarchical structure, the levels of which are
 * alternating between existentially and universally matched patterns.
 * The patterns on different levels are connected by morphisms, which
 * may merge or rename nodes.
 * <p>
 * A condition consists of the following elements:
 * <ul>
 * <li> The <i>root</i>: the parent graph in the condition hierarchy.
 * A condition can only be matched relative to a match of its root. A condition
 * is called <i>ground</i> if its root is the empty graph.
 * <li> The <i>pattern</i>: the graph describing the structure that is to
 * be matched in a host graph. The (implicit) morphism between the root and
 * the pattern is based on node and edge identity, and is not explicitly
 * stored.
 * <li> The <i>seed</i>: the intersection of the root and the pattern.
 * The seed is thus the subgraph of the pattern that is pre-matched
 * before the condition itself is matched.
 * <li> The <i>anchor</i>: the subgraph of the pattern whose exact image in the
 * host graph is relevant. This includes at least the seed and the elements mapped
 * to the next levels in the condition tree.
 * <li> The <i>subconditions</i>: the next levels in the condition tree. Each
 * subcondition has the pattern of this condition as its root.
 * </ul>
 * The following concepts play a role when matching a condition:
 * <ul>
 * <li> A <i>context map</i>: Mapping from the root to a host graph
 * <li> A <i>seed map</i>: Mapping from the seed to a host graph. This is
 * derived from a context map using the condition's root map
 * <li> A <i>pattern map</i>: Mapping from the pattern to the host graph. This
 * is determined by searching for an extension to a seed map.
 * </ul>
 * @author Arend Rensink
 * @version $Revision$
 */
abstract public class Condition implements Fixable {
    /**
     * Constructs a (named) graph condition based on a given pattern graph
     * and root graph.
     * @param name the name of the condition; may be <code>null</code>
     * @param pattern the graph to be matched
     * @param rootGraph the root graph of the condition; may be <code>null</code> if the condition is
     *        ground
     * @param properties properties for matching the condition
     */
    protected Condition(RuleName name, RuleGraph pattern, RuleGraph rootGraph,
            SystemProperties properties) {
        this.rootGraph =
            rootGraph == null ? pattern.newGraph((name == null ? "root"
                    : name.toString() + "-root")) : rootGraph;
        this.pattern = pattern;
        this.systemProperties = properties;
        this.name = name;
        this.subConditions = new ArrayList<Condition>();
    }

    /** Returns the secondary properties of this graph condition. */
    public SystemProperties getSystemProperties() {
        return this.systemProperties;
    }

    /**
     * Sets the label store of this graph condition.
     */
    public void setLabelStore(LabelStore labelStore) {
        assert labelStore != null;
        this.labelStore = labelStore;
        for (Condition sub : getSubConditions()) {
            sub.setLabelStore(labelStore);
        }
    }

    /**
     * Returns the label store of this graph condition.
     * The label store must be set before the graph is fixed.
     */
    public LabelStore getLabelStore() {
        return this.labelStore;
    }

    /** 
     * Returns the root graph of this condition.
     * The root graph is the subgraph of the pattern that the
     * condition has in common with its parent in the condition tree.
     */
    public RuleGraph getRoot() {
        return this.rootGraph;
    }

    /**
     * Returns the subset of the root nodes that are certainly
     * bound before the condition has to be matched.
     */
    final public Set<RuleNode> getInputNodes() {
        if (this.inputNodes == null) {
            this.inputNodes = computeInputNodes();
        }
        return this.inputNodes;
    }

    /**
     * Computes the set of input nodes nodes of this condition. 
     * These are the nodes that are certainly
     * bound before the condition has to be matched.
     */
    Set<RuleNode> computeInputNodes() {
        return new HashSet<RuleNode>(this.rootGraph.nodeSet());
    }

    /**
     * Returns the pattern of the condition, i.e., the structure that
     * the condition actually tests for.
     */
    public RuleGraph getPattern() {
        return this.pattern;
    }

    /**
     * Returns the name of this condition. A return value of <code>null</code>
     * indicates that the condition is unnamed.
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

    /**
     * Indicates if this condition is closed, which is to say that it has
     * an empty root.
     * @return <code>true</code> if this condition has an empty root.
     */
    public boolean isGround() {
        assert isFixed();
        return this.ground;
    }

    /**
     * Returns the collection of sub-conditions of this graph condition. The
     * intended interpretation of the sub-conditions (as conjuncts or disjuncts)
     * depends on this condition.
     */
    public Collection<Condition> getSubConditions() {
        return this.subConditions;
    }

    /**
     * Adds a sub-condition to this graph condition.
     * The sub-condition should already be fixed.
     * @param condition the condition to be added
     * @see #getSubConditions()
     */
    public void addSubCondition(Condition condition) {
        condition.testFixed(true);
        testFixed(false);
        if (this.labelStore != null) {
            condition.setLabelStore(this.labelStore);
        }
        getSubConditions().add(condition);
        if (getRule() != null) {
            for (Rule subRule : condition.getTopRules()) {
                getRule().addDirectSubRule(subRule);
            }
        }
    }

    /** Returns the collection of top-level (sub)rules of this condition.
     * This is either the rule associated with this condition (if any),
     * or, recursively, with any of its subconditions.
     * @return the collection of top-level (sub)rules of this condition
     */
    private List<Rule> getTopRules() {
        List<Rule> result = new ArrayList<Rule>();
        if (getRule() == null) {
            for (Condition subCond : getSubConditions()) {
                result.addAll(subCond.getTopRules());
            }
        } else {
            result.add(getRule());
        }
        return result;
    }

    /** Fixes this condition and all its subconditions. */
    public void setFixed() throws FormatException {
        if (!isFixed()) {
            for (Condition subCondition : getSubConditions()) {
                subCondition.testFixed(true);
            }
            this.fixed = true;
            this.ground = this.getRoot().isEmpty();
            getPattern().setFixed();
            testAlgebra();
            if (getRule() != null) {
                getRule().setFixed();
            }
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
        Map<VariableNode,List<Set<VariableNode>>> resolverMap =
            createResolvers();
        stabilise(resolverMap);
        //        computeUnresolvedNodes();
        //        stabilizeUnresolvedNodes();
        for (RuleNode node : resolverMap.keySet()) {
            errors.add(new FormatError(
                "Cannot resolve attribute value node '%s'", node));
        }
        //        for (RuleNode node : this.unresolvedVariableNodes) {
        //            errors.add(new FormatError(
        //                "Cannot resolve attribute value node '%s'", node));
        //        }
        //        if (!this.unresolvedProductNodes.isEmpty()) {
        //            Map.Entry<ProductNode,BitSet> productEntry =
        //                this.unresolvedProductNodes.entrySet().iterator().next();
        //            ProductNode product = productEntry.getKey();
        //            BitSet arguments = productEntry.getValue();
        //            if (arguments.cardinality() != product.arity()) {
        //                arguments.flip(0, product.arity());
        //                errors.add(new FormatError(
        //                    "Argument edges %s of product node %s missing in sub-condition",
        //                    arguments, product));
        //            }
        //        }
        if (!errors.isEmpty()) {
            throw new FormatException(errors);
        }
    }

    /** 
     * Creates a mapping from unresolved variables to potential resolvers.
     * Each resolver is a set of variables that all have to be resolved in order
     * for the key to be resolved.
     */
    Map<VariableNode,List<Set<VariableNode>>> createResolvers() {
        Map<VariableNode,List<Set<VariableNode>>> result =
            new HashMap<VariableNode,List<Set<VariableNode>>>();
        // Set of variable nodes already found to have been resolved
        Set<VariableNode> resolved = new HashSet<VariableNode>();
        for (RuleNode node : getInputNodes()) {
            if (node instanceof VariableNode) {
                resolved.add((VariableNode) node);
            }
        }
        // Set of variable nodes needing resolution
        for (RuleNode node : getPattern().nodeSet()) {
            if (node instanceof VariableNode
                && ((VariableNode) node).getConstant() == null
                && !resolved.contains(node)) {
                result.put((VariableNode) node,
                    new ArrayList<Set<VariableNode>>());
            }
        }
        // first collect the count nodes of subconditions
        for (Condition subCondition : getSubConditions()) {
            VariableNode countNode = subCondition.getCountNode();
            // check if the condition has a non-constant count node
            if (countNode != null && countNode.getConstant() == null) {
                Set<VariableNode> resolver = new HashSet<VariableNode>();
                // add the unresolved root nodes of the subcondition to the resolver
                for (RuleNode rootNode : subCondition.getInputNodes()) {
                    if (rootNode instanceof VariableNode
                        && ((VariableNode) rootNode).getConstant() == null) {
                        resolver.add((VariableNode) rootNode);
                    }
                }
                resolver.removeAll(resolved);
                if (resolver.isEmpty()) {
                    resolved.add(countNode);
                } else {
                    addResolver(result, countNode, resolver);
                }
            }
        }
        // now add resolvers due to product nodes
        for (RuleNode node : result.keySet()) {
            if (node instanceof VariableNode
                && ((VariableNode) node).getConstant() == null) {
                VariableNode varNode = (VariableNode) node;
                for (RuleEdge edge : getPattern().inEdgeSet(node)) {
                    if (edge.label().isMatchable()) {
                        resolved.add(varNode);
                    } else if (edge instanceof OperatorEdge) {
                        ProductNode source = ((OperatorEdge) edge).source();
                        // collect the argument nodes
                        Set<VariableNode> resolver =
                            new HashSet<VariableNode>();
                        for (VariableNode arg : source.getArguments()) {
                            if (arg.getSymbol() == null) {
                                resolver.add(arg);
                            }
                        }
                        resolver.removeAll(resolved);
                        if (resolver.isEmpty()) {
                            resolved.add(varNode);
                        } else {
                            result.get(varNode).add(resolver);
                        }
                    }
                }
            }
        }
        for (VariableNode node : resolved) {
            result.remove(node);
        }
        return result;
    }

    /** Adds a value to the list of values for a given key. */
    private <K,V> void addResolver(Map<K,List<V>> map, K key, V value) {
        List<V> entry = map.get(key);
        if (entry == null) {
            map.put(key, entry = new ArrayList<V>());
        }
        entry.add(value);
    }

    /** Removes entries from the resolver map if they have an empty resolver. */
    private void stabilise(Map<VariableNode,List<Set<VariableNode>>> resolverMap) {
        boolean stable = false;
        // repeat until no more changes
        while (!stable) {
            stable = true;
            // iterate over all resolver lists
            Iterator<List<Set<VariableNode>>> iter =
                resolverMap.values().iterator();
            while (iter.hasNext()) {
                // try each resolver in turn
                for (Set<VariableNode> resolver : iter.next()) {
                    // restrict to the unresolved nodes
                    resolver.retainAll(resolverMap.keySet());
                    // if there are no unresolved nodes, the entry may be removed
                    if (resolver.isEmpty()) {
                        iter.remove();
                        stable = false;
                        break;
                    }
                }
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder res =
            new StringBuilder(String.format("%s condition %s: ",
                getMode().getName(), getName()));
        res.append(String.format("Target: %s", getPattern()));
        if (!getRoot().isEmpty()) {
            res.append(String.format("%nRoot graph: %s", getRoot()));
        }
        if (!getSubConditions().isEmpty()) {
            res.append(String.format("%nSubconditions:"));
            for (Condition subCondition : getSubConditions()) {
                res.append(String.format("%n    %s", subCondition));
            }
        }
        return res.toString();
    }

    /** Returns the mode of this condition. */
    public abstract Mode getMode();

    /** Sets a count node for this universal condition. 
     * @see #getCountNode() */
    public void setCountNode(VariableNode countNode) {
        assert !isFixed();
        this.countNode = countNode;
    }

    /** 
     * Returns the count node of this universal condition, if any.
     * The count node is bound to the number of matches of the condition.
     */
    public VariableNode getCountNode() {
        return this.countNode;
    }

    /** Sets this universal condition to positive (meaning that
     * it should have at least one match). */
    public void setPositive() {
        assert !isFixed();
        this.positive = true;
    }

    /**
     * Indicates if this condition is positive. A universal condition is
     * positive if it cannot be vacuously fulfilled; i.e., there must always be
     * at least one match.
     */
    public boolean isPositive() {
        return this.positive;
    }

    /**
     * Indicates if there is a rule associated with this condition.
     * Only existential and universal conditions can have associated rules.
     * Convenience method for {@code getRule() != null}.
     * @return {@code true} if there is a rule associated with this condition
     * @see #getRule()
     */
    final public boolean hasRule() {
        return getRule() != null;
    }

    /**
     * Returns the rule associated with this condition, if any.
     * Only existential and universal conditions can have associated rules.
     * @return The rule associated with this condition, or {@code null}
     * if there is no associated rule.
     */
    public Rule getRule() {
        return null;
    }

    /**
     * The name of this condition. May be <code>code</code> null.
     */
    private RuleName name;

    /** The collection of sub-conditions of this condition. */
    private final Collection<Condition> subConditions;

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
    private final RuleGraph rootGraph;

    /** Subset of the root nodes that are bound to be bound before the condition is matched. */
    private Set<RuleNode> inputNodes;

    /** The pattern graph of this morphism. */
    private final RuleGraph pattern;

    /**
     * Factory instance for creating the correct simulation.
     */
    private final SystemProperties systemProperties;
    /** Subtyping relation, derived from the SystemProperties. */
    private LabelStore labelStore;

    /** Node capturing the match count of this condition. */
    private VariableNode countNode;

    /**
     * Flag indicating whether the condition is positive, i.e., cannot be
     * vacuously true.
     */
    private boolean positive;

    /** 
     * The mode of this condition.
     * This corresponds to a First Order Logic operator.
     */
    public static enum Mode {
        /** Universally quantified pattern. */
        FORALL("Universal"),
        /** Existentially quantified pattern. */
        EXISTS("Existential"),
        /** Negated condition. */
        NOT("Negated"),
        /** Conjunction of subconditions. */
        AND("Conjunctive"),
        /** Disjunction of subconditions. */
        OR("Disjunctive");

        private Mode(String name) {
            this.name = name;
        }

        /** Returns the name of this condition mode. */
        public final String getName() {
            return this.name;
        }

        @Override
        public String toString() {
            return getName();
        }

        private final String name;
    }
}