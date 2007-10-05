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
 * $Id: OperatorEdgeSearchItem.java,v 1.13 2007-10-05 11:44:39 rensink Exp $
 */
package groove.match;

import groove.algebra.Operation;
import groove.graph.Node;
import groove.graph.algebra.AlgebraGraph;
import groove.graph.algebra.ProductEdge;
import groove.graph.algebra.ProductNode;
import groove.graph.algebra.ValueNode;
import groove.match.SearchPlanStrategy.Search;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * A search item for a product edge.
 * The source node (a {@link ProductNode}) will typically have no images;
 * instead, its operands are guaranteed to have images. 
 * @author Arend Rensink
 * @version $Revision $
 */
class OperatorEdgeSearchItem extends AbstractSearchItem {
	/**
	 * Creates a search item for a given edge, for which it is know
	 * which edge ends have already been matched (in the search plan) before this one.
	 * @param edge the edge to be matched
	 */
	public OperatorEdgeSearchItem(ProductEdge edge) {
		this.edge = edge;
		this.operation = edge.getOperation();
		this.arguments = edge.source().getArguments();
		this.target = edge.target();
        this.neededNodes = new HashSet<Node>(arguments);
        if (target.hasValue()) {
            this.boundNodes = Collections.<Node>emptySet();
            this.neededNodes.add(target);
        } else {
            this.boundNodes = Collections.<Node>singleton(target);
        }
	}
	
	public OperatorEdgeRecord getRecord(Search matcher) {
		return new OperatorEdgeRecord(matcher);
	}

	/**
     * Returns a singleton set consisting of the target node of the operator edge.
     */
    @Override
    public Collection<? extends Node> bindsNodes() {
        return boundNodes;
    }

    /**
     * Returns the set of argument nodes of the source (product) node.
     */
    @Override
    public Collection<Node> needsNodes() {
        return neededNodes;
    }

    @Override
	public String toString() {
		return String.format("Compute %s%s-->%s", operation.toString(), arguments, target); 
	}
	
	/**
     * If the other item is also a {@link OperatorEdgeSearchItem}, compares on the
     * basis of the label and then the arguments; otherwise, delegates to <code>super</code>.
     */
    @Override
    public int compareTo(SearchItem other) {
        int result = 0;
        if (other instanceof OperatorEdgeSearchItem) {
            ProductEdge otherEdge = ((OperatorEdgeSearchItem) other).getEdge();
            List<ValueNode> otherArguments = otherEdge.source().getArguments();
            result = edge.label().compareTo(otherEdge.label());
            for (int i = 0; result == 0 && i < arguments.size(); i++) {
                result = arguments.get(i).compareTo(otherArguments.get(i));
            }
            if (result == 0) {
                result = target.compareTo(otherEdge.target());
            }
        }
        if (result == 0) {
            return super.compareTo(other);
        } else {
            return result;
        }
    }

    /**
     * This implementation returns the product edge's hash code.
     */
    @Override
    int getRating() {
        return edge.hashCode();
    }

    /** Returns the product edge being calculated by this search item. */
	public ProductEdge getEdge() {
		return edge;
	}

	public void activate(SearchPlanStrategy strategy) {
		targetFound = strategy.isNodeFound(target);
        targetIx = strategy.getNodeIx(target);
        argumentIxs = new int[arguments.size()];
        for (int i = 0; i < arguments.size(); i++) {
            argumentIxs[i] = strategy.getNodeIx(arguments.get(i));
        }
    }

    /** The product edge for which we seek an image. */
	private final ProductEdge edge;
	/** The operation of the product edge. */
	private final Operation operation;
	/** List of operands of the product edge's source node. */
	private final List<ValueNode> arguments;
	/** The target node of the product edge. */
	private final ValueNode target;
    /** Singleton set consisting of <code>target</code>. */
    private final Collection<Node> boundNodes;
    /** Set of the nodes in <code>arguments</code>. */
    private final Collection<Node> neededNodes;
    /** Indices of the argument nodes in the result. */
    private int[] argumentIxs;
    /** Flag indicating if the target node has been found at search time. */
    private boolean targetFound;
    /** Index of {@link #target} in the result. */
    private int targetIx;
    
    /**
     * Record of an edge search item, storing an iterator over the
     * candidate images.
     * @author Arend Rensink
     * @version $Revision $
     */
    private class OperatorEdgeRecord extends SingularRecord {
        /**
         * Creates a record based on a given underlying matcher.
         */
        OperatorEdgeRecord(Search search) {
            super(search);
            targetPreMatch = search.getNodeAnchor(targetIx);
        }
        
        @Override
        public String toString() {
            return String.format("%s = %s", OperatorEdgeSearchItem.this.toString(), search.getNode(targetIx));
        }

        @Override
        boolean set() {
            boolean result;
            Object outcome = calculateResult();
            if (outcome == null || target.hasValue() && !target.getValue().equals(outcome)) {
                result = false;
            } else if (targetFound || targetPreMatch != null) {
            	Node targetFind = targetPreMatch;
            	if (targetFind == null) {
            		targetFind = search.getNode(targetIx);
            	}
                result = ((ValueNode) targetFind).getValue().equals(outcome);
            } else {
                ValueNode targetImage = AlgebraGraph.getInstance().getValueNode(operation.getResultType(), outcome);
                result = search.putNode(targetIx, targetImage);
            }
            return result;
        }

        /**
         * Removes the edge added during the last {@link #find()}, if any.
         */
        @Override
        public void reset() {
        	super.reset();
            if (targetPreMatch == null && !targetFound) {
                search.putNode(targetIx, null);
            }
        }
        
        /**
         * Calculates the result of the operation in {@link #getEdge()},
         * based on the currently installed images of the arguments.
         * @return the result of the operation, or <code>null</code> if it cannot
         * be calculated due to the fact that one of the arguments was bound to
         * a non-value.
         */
        private Object calculateResult() throws IllegalArgumentException {
            Object[] operands = new Object[arguments.size()];
            for (int i = 0; i < arguments.size(); i++) {
                Node operandImage = search.getNode(argumentIxs[i]);
                if (! (operandImage instanceof ValueNode)) {
                    // one of the arguments was not bound to a value
                    // (probably due to some typing error in another rule)
                    // and so we cannot match the edge
                    return null;
                } 
                assert ((ValueNode) operandImage).hasValue() : String.format("Graph node %s has no value", operandImage);
                operands[i] = ((ValueNode) operandImage).getValue();
            }
            try {
                Object result = operation.apply(Arrays.asList(operands));
                if (PRINT) {
                    System.out.printf("Applying %s to %s yields %s%n", operation, Arrays.asList(operands), result);
                }
                return result;
            } catch (IllegalArgumentException exc) {
                return null;
            }
        }
        
        /** The pre-matched target node, if any. */
        private final Node targetPreMatch;
        
        /** Flag to control debug printing. */
        static private final boolean PRINT = false; 
    }
}
