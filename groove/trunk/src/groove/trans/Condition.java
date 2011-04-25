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
import groove.graph.algebra.ArgumentEdge;
import groove.graph.algebra.OperatorEdge;
import groove.graph.algebra.ProductNode;
import groove.graph.algebra.VariableNode;
import groove.rel.LabelVar;
import groove.rel.VarSupport;
import groove.util.Fixable;
import groove.view.FormatError;
import groove.view.FormatException;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
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
    protected Condition(RuleName name, RuleGraph pattern,
            RuleGraph rootGraph, SystemProperties properties) {
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
     * Computes the set of root nodes of this condition. 
     * These are the nodes that it has in common with its parent
     * in the condition tree.
     */
    Set<RuleNode> computeRootNodes() {
        return new HashSet<RuleNode>(this.rootGraph.nodeSet());
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
     * Computes the set of root nodes of this condition. 
     * These are the nodes that it has in common with its parent
     * in the condition tree.
     */
    Set<RuleEdge> computeRootEdges() {
        return new HashSet<RuleEdge>(this.rootGraph.edgeSet());
    }

    /**
     * Set of variables in the pattern of this condition that also occur in root
     * elements.
     */
    final public Set<LabelVar> getRootVars() {
        if (this.rootVars == null) {
            this.rootVars = computeRootVars();
        }
        return this.rootVars;
    }

    /**
     * Computes the set of root nodes of this condition. 
     * These are the nodes that it has in common with its parent
     * in the condition tree.
     */
    Set<LabelVar> computeRootVars() {
        return new HashSet<LabelVar>(VarSupport.getAllVars(this.rootGraph));
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
     * @param condition the condition to be added
     * @see #getSubConditions()
     */
    public void addSubCondition(Condition condition) {
        testFixed(false);
        if (this.labelStore != null) {
            condition.setLabelStore(this.labelStore);
        }
        getSubConditions().add(condition);
        if (!(condition instanceof NotCondition)) {
            getComplexSubConditions().add(condition);
        }
    }

    /**
     * Returns the set of sub-conditions that are <i>not</i>
     * {@link NotCondition}s.
     * These are the conditions that are not part of the search plan,
     * and so have to be matched explicitly.
     */
    @SuppressWarnings("unchecked")
    protected <C extends Condition> Collection<C> getComplexSubConditions() {
        if (this.complexSubConditions == null) {
            this.complexSubConditions = new ArrayList<Condition>();
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
            for (Condition subCondition : getSubConditions()) {
                subCondition.setFixed();
            }
        }
    }

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
        this.unresolvedVariableNodes.removeAll(getRootNodes());
        for (RuleNode node : getRootNodes()) {
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
        if (!getRootNodes().isEmpty()) {
            res.append(String.format("%nRoot nodes: %s", getRootNodes()));
        }
        if (!getRootEdges().isEmpty()) {
            res.append(String.format(", edges: %s", getRootEdges()));
        }
        if (!getRootVars().isEmpty()) {
            res.append(String.format(", variables: %s", getRootVars()));
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

    /** The collection of sub-conditions of this condition. */
    private final Collection<Condition> subConditions;

    /**
     * The sub-conditions that are not negative application conditions.
     */
    private Collection<Condition> complexSubConditions;

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

}