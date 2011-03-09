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
 * $Id: OperatorEdgeSearchItem.java,v 1.15 2008-01-30 09:33:29 iovka Exp $
 */
package groove.match;

import groove.algebra.AlgebraFamily;
import groove.algebra.Operation;
import groove.graph.algebra.OperatorEdge;
import groove.graph.algebra.ProductNode;
import groove.graph.algebra.ValueNode;
import groove.graph.algebra.VariableNode;
import groove.match.SearchPlanStrategy.Search;
import groove.trans.HostFactory;
import groove.trans.HostNode;
import groove.trans.RuleNode;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * A search item for a product edge. The source node (a {@link ProductNode})
 * will typically have no images; instead, its operands are guaranteed to have
 * images.
 * @author Arend Rensink
 * @version $Revision $
 */
class OperatorEdgeSearchItem extends AbstractSearchItem {
    /**
     * Creates a search item for a given edge, for which it is know which edge
     * ends have already been matched (in the search plan) before this one.
     * @param edge the edge to be matched
     */
    public OperatorEdgeSearchItem(OperatorEdge edge, AlgebraFamily family) {
        this.edge = edge;
        this.operation = family.getOperation(edge.getOperator());
        this.arguments = edge.source().getArguments();
        this.target = edge.target();
        this.neededNodes = new HashSet<RuleNode>(this.arguments);
        if (this.target.getConstant() != null) {
            this.boundNodes = Collections.<RuleNode>emptySet();
            this.neededNodes.add(this.target);
            this.value =
                family.getValue(edge.getOperator().getResultType(),
                    this.target.getSymbol());
        } else {
            this.boundNodes = Collections.<RuleNode>singleton(this.target);
            this.value = null;
        }
    }

    public OperatorEdgeRecord getRecord(Search matcher) {
        return new OperatorEdgeRecord(matcher);
    }

    /**
     * Returns a singleton set consisting of the target node of the operator
     * edge.
     */
    @Override
    public Collection<RuleNode> bindsNodes() {
        return this.boundNodes;
    }

    /**
     * Returns the set of argument nodes of the source (product) node.
     */
    @Override
    public Collection<RuleNode> needsNodes() {
        return this.neededNodes;
    }

    @Override
    public String toString() {
        return String.format("Compute %s%s-->%s", this.operation.toString(),
            this.arguments, this.target);
    }

    /**
     * If the other item is also a {@link OperatorEdgeSearchItem}, compares on
     * the basis of the label and then the arguments; otherwise, delegates to
     * <code>super</code>.
     */
    @Override
    public int compareTo(SearchItem other) {
        int result = 0;
        if (other instanceof OperatorEdgeSearchItem) {
            OperatorEdge otherEdge = ((OperatorEdgeSearchItem) other).getEdge();
            List<VariableNode> otherArguments =
                otherEdge.source().getArguments();
            result = this.edge.label().compareTo(otherEdge.label());
            for (int i = 0; result == 0 && i < this.arguments.size(); i++) {
                result = this.arguments.get(i).compareTo(otherArguments.get(i));
            }
            if (result == 0) {
                result = this.target.compareTo(otherEdge.target());
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
        return this.edge.hashCode();
    }

    /** Returns the product edge being calculated by this search item. */
    public OperatorEdge getEdge() {
        return this.edge;
    }

    public void activate(SearchPlanStrategy strategy) {
        this.targetFound = strategy.isNodeFound(this.target);
        this.targetIx = strategy.getNodeIx(this.target);
        this.argumentIxs = new int[this.arguments.size()];
        for (int i = 0; i < this.arguments.size(); i++) {
            this.argumentIxs[i] = strategy.getNodeIx(this.arguments.get(i));
        }
    }

    /** The product edge for which we seek an image. */
    final OperatorEdge edge;
    /** The operation determined by the product edge. */
    final Operation operation;
    /** The factory needed to create value nodes for the calculated outcomes. */
    /** List of operands of the product edge's source node. */
    final List<VariableNode> arguments;
    /** The target node of the product edge. */
    final VariableNode target;
    /** The value of the target node, if it is a constant. */
    final Object value;
    /** Singleton set consisting of <code>target</code>. */
    final Collection<RuleNode> boundNodes;
    /** Set of the nodes in <code>arguments</code>. */
    final Collection<RuleNode> neededNodes;
    /** Indices of the argument nodes in the result. */
    int[] argumentIxs;
    /** Flag indicating if the target node has been found at search time. */
    boolean targetFound;
    /** Index of {@link #target} in the result. */
    int targetIx;

    /**
     * Record of an edge search item, storing an iterator over the candidate
     * images.
     * @author Arend Rensink
     * @version $Revision $
     */
    private class OperatorEdgeRecord extends SingularRecord {
        /**
         * Creates a record based on a given underlying matcher.
         */
        OperatorEdgeRecord(Search search) {
            super(search);
            this.targetPreMatch =
                search.getNodeAnchor(OperatorEdgeSearchItem.this.targetIx);
            this.factory = search.getHost().getFactory();
        }

        @Override
        public String toString() {
            return String.format("%s = %s",
                OperatorEdgeSearchItem.this.toString(),
                this.search.getNode(OperatorEdgeSearchItem.this.targetIx));
        }

        @Override
        boolean set() {
            boolean result;
            Object outcome = calculateResult();
            if (outcome == null) {
                result = false;
            } else if (OperatorEdgeSearchItem.this.value != null) {
                result = OperatorEdgeSearchItem.this.value.equals(outcome);
            } else if (OperatorEdgeSearchItem.this.targetFound
                || this.targetPreMatch != null) {
                HostNode targetFind = this.targetPreMatch;
                if (targetFind == null) {
                    targetFind =
                        this.search.getNode(OperatorEdgeSearchItem.this.targetIx);
                }
                result = ((ValueNode) targetFind).getValue().equals(outcome);
            } else {
                ValueNode targetImage =
                    this.factory.createNode(
                        OperatorEdgeSearchItem.this.operation.getResultAlgebra(),
                        outcome);
                result =
                    this.search.putNode(OperatorEdgeSearchItem.this.targetIx,
                        targetImage);
            }
            return result;
        }

        /**
         * Removes the edge added during the last {@link #find()}, if any.
         */
        @Override
        public void reset() {
            super.reset();
            if (this.targetPreMatch == null
                && !OperatorEdgeSearchItem.this.targetFound) {
                this.search.putNode(OperatorEdgeSearchItem.this.targetIx, null);
            }
        }

        /**
         * Calculates the result of the operation in {@link #getEdge()}, based
         * on the currently installed images of the arguments.
         * @return the result of the operation, or <code>null</code> if it
         *         cannot be calculated due to the fact that one of the
         *         arguments was bound to a non-value.
         */
        private Object calculateResult() throws IllegalArgumentException {
            Object[] operands =
                new Object[OperatorEdgeSearchItem.this.arguments.size()];
            for (int i = 0; i < OperatorEdgeSearchItem.this.arguments.size(); i++) {
                HostNode operandImage =
                    this.search.getNode(OperatorEdgeSearchItem.this.argumentIxs[i]);
                if (!(operandImage instanceof ValueNode)) {
                    // one of the arguments was not bound to a value
                    // (probably due to some typing error in another rule)
                    // and so we cannot match the edge
                    return null;
                }
                operands[i] = ((ValueNode) operandImage).getValue();
            }
            try {
                Object result =
                    OperatorEdgeSearchItem.this.operation.apply(Arrays.asList(operands));
                if (PRINT) {
                    System.out.printf("Applying %s to %s yields %s%n",
                        OperatorEdgeSearchItem.this.operation,
                        Arrays.asList(operands), result);
                }
                return result;
            } catch (IllegalArgumentException exc) {
                return null;
            }
        }

        /** The pre-matched target node, if any. */
        private final HostNode targetPreMatch;
        /** The factory for creating value nodes for the outcomes. */
        private final HostFactory factory;
        /** Flag to control debug printing. */
        static private final boolean PRINT = false;
    }
}
