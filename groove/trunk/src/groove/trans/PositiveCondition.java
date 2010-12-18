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
 * $Id: PositiveCondition.java,v 1.5 2008-01-04 17:07:35 rensink Exp $
 */
package groove.trans;

import groove.graph.algebra.ArgumentEdge;
import groove.graph.algebra.OperatorEdge;
import groove.graph.algebra.ProductNode;
import groove.graph.algebra.ValueNode;
import groove.graph.algebra.VariableNode;
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
 * Abstract superclass of conditions that test for the existence of a (sub)graph
 * structure.
 * @author Arend Rensink
 * @version $Revision$
 */
abstract public class PositiveCondition<M extends Match> extends
        AbstractCondition<M> {
    /**
     * Constructs a (named) graph condition based on a given target graph and
     * root morphism.
     * @param name the name of the condition; may be <code>null</code>
     * @param target the graph to be matched
     * @param rootMap element map from the context to the anchor elements of
     *        <code>target</code>; may be <code>null</code> if the condition is
     *        ground
     * @param properties properties for matching the condition; may be
     *        <code>null</code>
     */
    PositiveCondition(RuleName name, RuleGraph target, RuleGraphMorphism rootMap,
            SystemProperties properties) {
        super(name, target, rootMap, properties);
    }

    @Override
    public void setFixed() throws FormatException {
        if (!isFixed()) {
            testAlgebra();
            super.setFixed();
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
     * Calculates whether any VariableNode or ProductNode is unresolved and places
     * these in the appropriate Set or Map.
     */
    protected void computeUnresolvedNodes() {
        this.unresolvedVariableNodes = new HashSet<VariableNode>();
        this.unresolvedProductNodes = new HashMap<ProductNode,BitSet>();
        // test if product nodes have the required arguments
        for (RuleNode node : getTarget().nodeSet()) {
            if (node instanceof VariableNode && !(node instanceof ValueNode)) {
                boolean hasIncomingNonAttributeEdge = false;
                for (RuleEdge edge : getTarget().inEdgeSet(node)) {
                    if (edge.getClass().equals(RuleEdge.class)) {
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
     */
    private void stabilizeUnresolvedNodes() {
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
                for (RuleEdge edge : getTarget().outEdgeSet(product)) {
                    if (edge instanceof ArgumentEdge
                        && !this.unresolvedVariableNodes.contains(edge.target())) {
                        int argumentNumber = ((ArgumentEdge) edge).getNumber();
                        arguments.set(argumentNumber);
                    }
                }
                if (arguments.cardinality() == product.arity()) {
                    // the product node is resolved, so resolve the targets of
                    // the outgoing operations
                    for (RuleEdge edge : getTarget().outEdgeSet(product)) {
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

    /**
     * Apart from calling the super method, also maintains the subset of complex
     * sub-conditions.
     * @see #addComplexSubCondition(AbstractCondition)
     */
    @Override
    public void addSubCondition(Condition condition) {
        super.addSubCondition(condition);
        if (!(condition instanceof NotCondition)) {
            addComplexSubCondition((AbstractCondition<?>) condition);
        }
    }

    /**
     * Adds a graph condition to the complex sub-conditions, which are those
     * that are not edge or merge embargoes.
     */
    void addComplexSubCondition(AbstractCondition<?> condition) {
        getComplexSubConditions().add(condition);
    }

    /**
     * Returns the set of sub-conditions that are <i>not</i>
     * {@link NotCondition}s.
     */
    Collection<AbstractCondition<?>> getComplexSubConditions() {
        if (this.complexSubConditions == null) {
            this.complexSubConditions = new ArrayList<AbstractCondition<?>>();
        }
        return this.complexSubConditions;
    }

    /**
      * Callback factory method to create a match on the basis of a mapping of
      * this condition's target.
      * @param matchMap the mapping, presumably from the elements of
      *        {@link #getTarget()} into some host graph
      * @return a match constructed on the basis of <code>map</code>
      */
    abstract M createMatch(RuleToHostMap matchMap);

    /**
     * The sub-conditions that are not edge or merge embargoes.
     */
    private Collection<AbstractCondition<?>> complexSubConditions;

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