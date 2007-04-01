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
 * $Id: ProductEdgeSearchItem.java,v 1.1 2007-04-01 12:50:11 rensink Exp $
 */
package groove.graph.match;

import java.util.Arrays;
import java.util.List;

import groove.algebra.Constant;
import groove.algebra.Operation;
import groove.graph.Edge;
import groove.graph.Node;
import groove.graph.algebra.AlgebraGraph;
import groove.graph.algebra.ProductEdge;
import groove.graph.algebra.ProductNode;
import groove.graph.algebra.ValueNode;

/**
 * A search item for a product edge.
 * The source node (a {@link ProductNode}) will typically have no images;
 * instead, its operands are guaranteed to have images. 
 * @author Arend Rensink
 * @version $Revision $
 */
public class ProductEdgeSearchItem implements SearchItem {
	/**
	 * Record of an edge seach item, storing an iterator over the
	 * candidate images.
	 * @author Arend Rensink
	 * @version $Revision $
	 */
	private class ProductEdgeRecord implements Record {
		/**
		 * Creates a record based on a given underlying matcher.
		 */
		protected ProductEdgeRecord(Matcher matcher) {
			this.matcher = matcher;
		}
		
		/**
		 * The first call delegates to {@link #select()};
		 * the next call returns <code>false</code>.
		 */
		public boolean find() {
			if (atEnd) {
				// if we already returned false, as per contract
				// we restart
				reset();
			}
			if (called) {
				// if the test was called before, it should return false now
				undo();
				atEnd = true;
				return false;
			} else {
				called = true;
				boolean result = select();
				atEnd = !result;
				return result;
			}
		}

		/**
		 * Computes the result of the product edge's operation
		 */
		public boolean select() {
			Constant outcome = calculateResult();
			if (targetPreMatched) {
				ValueNode currentTargetImage = (ValueNode) matcher.getSingularMap().getNode(target);
				return currentTargetImage.getConstant().equals(outcome);
			} else {
				ValueNode targetImage = AlgebraGraph.getInstance().getValueNode(outcome);
				matcher.getSingularMap().putNode(target, targetImage);
				return true;
			}
		}

		/**
		 * Removes the edge added during the last {@link #find()}, if any.
		 */
		public void undo() {
			if (!targetPreMatched) {
				matcher.getSingularMap().removeNode(target);
			}
		}
		
		public void reset() {
			called = false;
			atEnd = false;
		}

		/**
		 * Calculates the result of the operation in {@link #getEdge()},
		 * based on the currently installed images of the arguments.
		 */
		private Constant calculateResult() {
			Constant[] arguments = new Constant[operands.size()];
			for (int i = 0; i < operands.size(); i++) {
				Node operandImage = matcher.getSingularMap().getNode(operands.get(i));
				arguments[i] = ((ValueNode) operandImage).getConstant();
			}
			Constant outcome = operation.apply(Arrays.asList(arguments));
			return outcome;
		}
		
		@Override
		public String toString() {
			return ProductEdgeSearchItem.this.toString();
		}

		/** The underlying matcher of the search record. */
		private final Matcher matcher;
		/** Flag to indicate that {@link #find()} has been called. */
		private boolean called;
		/** Flag to indicate that {@link #find()} has returned <code>false</code>. */
		private boolean atEnd;
	}

	/**
	 * Creates a search item for a given edge, for which it is know
	 * which edge ends have already been matched (in the search plan) before this one.
	 * @param edge the edge to be matched
	 * @param preMatched array of booleans indicating if the corresponding edge
	 * end has been pre-matched according to the search plan; or <code>null</code>
	 * if all ends have been pre-matched.
	 */
	public ProductEdgeSearchItem(ProductEdge edge, boolean[] preMatched) {
		this.edge = edge;
		this.operation = edge.getOperation();
		this.operands = edge.source().getArguments();
		this.target = edge.target();
		this.targetPreMatched = preMatched == null || preMatched[Edge.TARGET_INDEX];
	}
	
	public Record get(Matcher matcher) {
		return new ProductEdgeRecord(matcher);
	}

	@Override
	public String toString() {
		return String.format("Find %s", edge); 
	}

	/** The product edge for which we seek an image. */
	private final ProductEdge edge;
	/** The operation of the product edge. */
	private final Operation operation;
	/** List of operands of the product edge's source node. */
	private final List<ValueNode> operands;
	/** The target node of the product edge. */
	private final ValueNode target;
	/** Flag indicating whether the target of the edge is prematched. */
	private final boolean targetPreMatched;
}
