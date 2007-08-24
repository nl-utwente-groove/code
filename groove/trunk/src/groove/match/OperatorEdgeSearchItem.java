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
 * $Id: OperatorEdgeSearchItem.java,v 1.1 2007-08-24 17:34:57 rensink Exp $
 */
package groove.match;

import java.util.Arrays;
import java.util.List;

import groove.algebra.Operation;
import groove.graph.Edge;
import groove.graph.Node;
import groove.graph.algebra.AlgebraGraph;
import groove.graph.algebra.ProductEdge;
import groove.graph.algebra.ProductNode;
import groove.graph.algebra.ValueNode;

import static groove.match.SearchPlanStrategy.Search;

/**
 * A search item for a product edge.
 * The source node (a {@link ProductNode}) will typically have no images;
 * instead, its operands are guaranteed to have images. 
 * @author Arend Rensink
 * @version $Revision $
 */
public class OperatorEdgeSearchItem extends AbstractSearchItem {
	/**
	 * Creates a search item for a given edge, for which it is know
	 * which edge ends have already been matched (in the search plan) before this one.
	 * @param edge the edge to be matched
	 * @param preMatched array of booleans indicating if the corresponding edge
	 * end has been pre-matched according to the search plan; or <code>null</code>
	 * if all ends have been pre-matched.
	 */
	public OperatorEdgeSearchItem(ProductEdge edge, boolean[] preMatched) {
		this.edge = edge;
		this.operation = edge.getOperation();
		this.arguments = edge.source().getArguments();
		this.target = edge.target();
		this.targetPreMatched = preMatched == null || preMatched[Edge.TARGET_INDEX];
	}
	
    @Override
	public OperatorEdgeRecord getRecord(Search matcher) {
		return new OperatorEdgeRecord(matcher);
	}

	@Override
	public String toString() {
		return String.format("Compute %s", edge); 
	}
	
	/** Returns the product edge being calculated by this search item. */
	public ProductEdge getEdge() {
		return edge;
	}

	/** The product edge for which we seek an image. */
	private final ProductEdge edge;
	/** The operation of the product edge. */
	private final Operation operation;
	/** List of operands of the product edge's source node. */
	private final List<ValueNode> arguments;
	/** The target node of the product edge. */
	private final ValueNode target;
	/** Flag indicating whether the target of the edge is prematched. */
	private final boolean targetPreMatched;
    
    /**
     * Record of an edge seach item, storing an iterator over the
     * candidate images.
     * @author Arend Rensink
     * @version $Revision $
     */
    private class OperatorEdgeRecord extends AbstractRecord {
        /**
         * Creates a record based on a given underlying matcher.
         */
        protected OperatorEdgeRecord(Search search) {
            super(search);
        }
        
        @Override
        void exit() {
            nextCalled = false;
        }

        @Override
        void init() {
            // empty
        }

        @Override
        boolean next() {
            boolean result = !nextCalled;
            if (result) {
                Object outcome = calculateResult();
                if (outcome == null) {
                    result = false;
                } else if (targetPreMatched) {
                    ValueNode currentTargetImage;
                    if (target.hasValue()) {
                        currentTargetImage = target;
                    } else {
                        currentTargetImage = (ValueNode) getResult().getNode(target);
                    }
                    assert currentTargetImage != null: String.format("Target image of %s null in %s", edge, getResult());
                    result = currentTargetImage.getValue().equals(outcome);
                } else {
                    ValueNode targetImage = AlgebraGraph.getInstance().getValueNode(operation.getResultType(), outcome);
                    result = isAvailable(targetImage);
                    if (result) {
                        getResult().putNode(target, targetImage);
                    }
                }
            }
            nextCalled = true;
            return result;
        }

//        
//        /**
//         * The first call delegates to {@link #select()};
//         * the next call returns <code>false</code>.
//         */
//        public boolean find() {
//            if (atEnd) {
//                // if we already returned false, as per contract
//                // we restart
//                reset();
//            }
//            if (called) {
//                // if the test was called before, it should return false now
//                undo();
//                atEnd = true;
//                return false;
//            } else {
//                called = true;
//                boolean result = select();
//                atEnd = !result;
//                return result;
//            }
//        }
//
//        /**
//         * Computes the result of the product edge's operation
//         */
//        boolean select() {
//            Object outcome = calculateResult();
//            if (outcome == null) {
//                return false;
//            } else if (targetPreMatched) {
//                ValueNode currentTargetImage;
//                if (target.hasValue()) {
//                    currentTargetImage = target;
//                } else {
//                    currentTargetImage = (ValueNode) getResult().getNode(target);
//                }
//                assert currentTargetImage != null: String.format("Target image of %s null in %s", edge, getResult());
//                return currentTargetImage.getValue().equals(outcome);
//            } else {
//                ValueNode targetImage = AlgebraGraph.getInstance().getValueNode(operation.getResultType(), outcome);
//                if (isAvailable(targetImage)) {
//                    getResult().putNode(target, targetImage);
//                    return true;
//                } else {
//                    return false;
//                }
//            }
//        }

        /**
         * Removes the edge added during the last {@link #find()}, if any.
         */
        @Override
        void undo() {
            if (!targetPreMatched) {
                getResult().removeNode(target);
            }
        }
//        
//        /** Resets the record to its original state (after construction), so that the search can start anew. */
//        public void reset() {
//            called = false;
//            atEnd = false;
//        }

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
                Node operandImage = getResult().getNode(arguments.get(i));
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
                return operation.apply(Arrays.asList(operands));
            } catch (IllegalArgumentException exc) {
                return null;
            }
        }
        
        @Override
        public String toString() {
            return String.format("%s = %s", OperatorEdgeSearchItem.this.toString(), getResult().getNode(target));
        }
        
        /** Flag indicating that {@link #next()} has been invoked since {@link #init()}. */
        private boolean nextCalled;
//
//        /** The underlying matcher of the search record. */
//        private final SearchPlanStrategy.Search search;
//        /** Flag to indicate that {@link #find()} has been called. */
//        private boolean called;
//        /** Flag to indicate that {@link #find()} has returned <code>false</code>. */
//        private boolean atEnd;
    }
}
