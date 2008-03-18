/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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
 * $Id: PositiveCondition.java,v 1.5 2008-01-04 17:07:35 rensink Exp $
 */
package groove.trans;

import groove.graph.DefaultEdge;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Node;
import groove.graph.NodeEdgeMap;
import groove.graph.algebra.AlgebraEdge;
import groove.graph.algebra.ProductEdge;
import groove.graph.algebra.ProductNode;
import groove.graph.algebra.ValueNode;
import groove.rel.VarNodeEdgeMap;
import groove.view.FormatException;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Abstract superclass of conditions that test for the existence of a (sub)graph structure.
 * @author Arend Rensink
 * @version $Revision: 1.5 $
 */
abstract public class PositiveCondition<M extends Match> extends AbstractCondition<M> {
    /**
     * Constructs a (named) graph condition based on a given target graph and root morphism.
     * @param target the graph to be matched
     * @param rootMap element map from the context to the anchor elements of <code>target</code>;
     * may be <code>null</code> if the condition is ground
     * @param name the name of the condition; may be <code>null</code>
     * @param properties properties for matching the condition; may be <code>null</code>
     */
    PositiveCondition(Graph target, NodeEdgeMap rootMap, NameLabel name, SystemProperties properties) {
        super(target, rootMap, name, properties);
    }
    
    /**
     * Constructs a (named) ground graph condition based on a given pattern target.
     * and initially empty nested predicate.
     * The name may be <code>null</code>.
     */
    PositiveCondition(Graph target, NameLabel name, SystemProperties properties) {
        super(target, name, properties);
    }

    @Override
    public void setFixed() throws FormatException {
        if (!isFixed()) {
            testAlgebra();
            super.setFixed();
        }
    }
    
    /** 
     * Tests if the algebra part of the target graph can be matched.
     * This requires that there are no variable nodes that cannot be resolved, no
     * typing conflicts, and no missing arguments.
     * This is checked at fixing time of the condition.
     * @throws FormatException if the algebra part cannot be matched
     */
    private void testAlgebra() throws FormatException {
        // collect value and product nodes
        Set<ValueNode> unresolvedValueNodes = new HashSet<ValueNode>();
        Map<ProductNode,BitSet> unresolvedProductNodes = new HashMap<ProductNode,BitSet>();
        // test if product nodes have the required arguments
        for (Node node : getTarget().nodeSet()) {
            if (node instanceof ValueNode) {
                if (!((ValueNode) node).hasValue()) {
                    boolean hasIncomingNonAttributeEdge = false;
                    for (Edge edge: getTarget().edgeSet(node, Edge.TARGET_INDEX)) {
                        if (edge instanceof DefaultEdge) {
                            hasIncomingNonAttributeEdge = true;
                        }
                    }
                    if (! hasIncomingNonAttributeEdge) {
                        unresolvedValueNodes.add((ValueNode) node);
                    }
                }
            } else if (node instanceof ProductNode) {
                ProductNode product = (ProductNode) node;
                unresolvedProductNodes.put(product, new BitSet(product.arity()));
            }
        }
        unresolvedValueNodes.removeAll(getRootMap().nodeMap().values());
        // now resolve nodes until stable
        boolean stable = false;
        while (! stable) {
            stable = true;
            java.util.Iterator<Map.Entry<ProductNode,BitSet>> productIter = unresolvedProductNodes.entrySet().iterator();
            while (productIter.hasNext()) {
                Map.Entry<ProductNode,BitSet> productEntry = productIter.next();
                ProductNode product = productEntry.getKey();
                BitSet arguments = productEntry.getValue();
                for (Edge edge : getTarget().outEdgeSet(product)) {
                    if (edge instanceof AlgebraEdge && !unresolvedValueNodes.contains(edge.opposite())) {
                        int argumentNumber = ((AlgebraEdge) edge).getNumber();
                        arguments.set(argumentNumber);
                    }
                }
                if (arguments.cardinality() == product.arity()) {
                    // the product node is resolved, so resolve the targets of the outgoing operations
                    for (Edge edge : getTarget().outEdgeSet(product)) {
                        if (edge instanceof ProductEdge) {
                            if (unresolvedValueNodes.remove(((ProductEdge) edge).target())) {
                                stable = false;
                            }
                        }
                    }
                    productIter.remove();
                }
            }
        }
        if (! unresolvedValueNodes.isEmpty()) {
            throw new FormatException("Cannot resolve attribute value nodes %s", unresolvedValueNodes);
        }
        if (! unresolvedProductNodes.isEmpty()) {
            Map.Entry<ProductNode,BitSet> productEntry = unresolvedProductNodes.entrySet().iterator().next();
            ProductNode product = productEntry.getKey();
            BitSet arguments = productEntry.getValue();
            if (arguments.cardinality() != product.arity()) {
                arguments.flip(0, product.arity());
                throw new FormatException(
                    "Arguments edges %s of product node %s missing in sub-condition",
                    arguments, product);
            }
        }
    }

    /**
     * Apart from calling the super method, also maintains the subset of complex sub-conditions.
     * @see #addComplexSubCondition(AbstractCondition)
     */
    @Override
    public void addSubCondition(Condition condition) {
        super.addSubCondition(condition);
        if (!(condition instanceof NotCondition)) {
            addComplexSubCondition((AbstractCondition<?>) condition);
        }
    }
//    
//    /** Adds a negative edge, i.e., an edge embargo, to this condition. */
//    protected void addNegation(Edge negativeEdge) {
//        if (negations == null) {
//            negations = new HashSet<Edge>();
//        }
//        negations.add(negativeEdge);
//    }
//
//    /** Adds an injection constraint, i.e., a merge embargo, to this condition. */
//    protected void addInjection(Set<? extends Node> injection) {
//    	assert injection.size() == 2 : String.format("Injection %s should have size 2", injection);
//        if (injections == null) {
//            injections = new HashSet<Set<? extends Node>>();
//        }
//        injections.add(injection);
//    }

    /** 
	 * Adds a graph condition to the complex sub-conditions, which are
	 * those that are not edge or merge embargoes. 
	 */
	void addComplexSubCondition(AbstractCondition<?> condition) {
	    getComplexSubConditions().add(condition);        
	}

	/**
	 * Returns the set of sub-conditions that are <i>not</i> {@link NotCondition}s.
	 */
	Collection<AbstractCondition<?>> getComplexSubConditions() {
	    if (complexSubConditions == null) {
	        complexSubConditions = new ArrayList<AbstractCondition<?>>();
	    }
	    return complexSubConditions;
	}

    /** 
     * Callback factory method to create a match on the basis of
     * a mapping of this condition's target.
     * @param matchMap the mapping, presumably from the elements of {@link #getTarget()}
     * into some host graph
     * @return a match constructed on the basis of <code>map</code>
     */
    abstract M createMatch(VarNodeEdgeMap matchMap);
//    
//	/**
//     * Returns the map from nodes to sets of injectively matchable nodes.
//     */
//    public Set<Set<? extends Node>> getInjections() {
//        return injections;
//    }
//
//    /**
//     * Returns the map from lhs nodes to incident negative edges.
//     */
//    public Set<Edge> getNegations() {
//        return negations;
//    }
    
    /** 
     * The sub-conditions that are not edge or merge embargoes.
     */
    private Collection<AbstractCondition<?>> complexSubConditions;
//
//    /**
//     * Mapping from codomain nodes to sets of other codomain nodes with which
//     * they must be matched injectively.
//     */
//    private Set<Set<? extends Node>> injections;
//    
//    /**
//     * Mapping from codomain nodes to single or sets of incident codomain edges
//     * that must be absent in the matching.
//     */
//    private Set<Edge> negations;
}