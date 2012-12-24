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
package groove.match.plan;

import groove.algebra.AlgebraFamily;
import groove.algebra.Operation;
import groove.graph.algebra.OperatorNode;
import groove.graph.algebra.ValueNode;
import groove.graph.algebra.VariableNode;
import groove.match.plan.PlanSearchStrategy.Search;
import groove.trans.HostFactory;
import groove.trans.HostGraph;
import groove.trans.HostNode;
import groove.trans.RuleNode;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * A search item for an operator node.
 * @author Arend Rensink
 * @version $Revision $
 */
class OperatorNodeSearchItem extends AbstractSearchItem {
    /**
     * Creates a search item for a given edge, for which it is know which edge
     * ends have already been matched (in the search plan) before this one.
     * @param node the edge to be matched
     */
    public OperatorNodeSearchItem(OperatorNode node, AlgebraFamily family) {
        this.node = node;
        this.operation = family.getOperation(node.getOperator());
        assert this.operation != null;
        this.arguments = node.getArguments();
        this.target = node.getTarget();
        this.boundNodes = new HashSet<RuleNode>();
        this.boundNodes.add(node);
        this.neededNodes = new HashSet<RuleNode>(this.arguments);
        if (this.target.hasConstant()) {
            this.neededNodes.add(this.target);
            this.value =
                family.getValue(node.getOperator().getResultType(),
                    this.target.getConstant().getSymbol());
        } else {
            this.boundNodes.add(this.target);
            this.value = null;
        }
    }

    public OperatorNodeRecord createRecord(
            groove.match.plan.PlanSearchStrategy.Search matcher) {
        return new OperatorNodeRecord(matcher);
    }

    /**
     * Returns a singleton set consisting of the operator
     * node.
     */
    @Override
    public Collection<? extends RuleNode> bindsNodes() {
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
     * If the other item is also a {@link OperatorNodeSearchItem}, compares on
     * the basis of the label and then the arguments; otherwise, delegates to
     * <code>super</code>.
     */
    @Override
    public int compareTo(SearchItem other) {
        int result = 0;
        if (other instanceof OperatorNodeSearchItem) {
            OperatorNode hisNode = ((OperatorNodeSearchItem) other).getNode();
            List<VariableNode> hisArguments = hisNode.getArguments();
            result =
                this.operation.getName().compareTo(
                    hisNode.getOperator().getName());
            for (int i = 0; result == 0 && i < this.arguments.size(); i++) {
                result = this.arguments.get(i).compareTo(hisArguments.get(i));
            }
            if (result == 0) {
                result = this.target.compareTo(hisNode.getTarget());
            }
        }
        if (result == 0) {
            return super.compareTo(other);
        } else {
            return result;
        }
    }

    /**
     * This implementation returns the operator node's hash code.
     */
    @Override
    int getRating() {
        return this.node.hashCode();
    }

    /** Returns the operator node being calculated by this search item. */
    public OperatorNode getNode() {
        return this.node;
    }

    public void activate(PlanSearchStrategy strategy) {
        this.targetFound = strategy.isNodeFound(this.target);
        this.targetIx = strategy.getNodeIx(this.target);
        this.argumentIxs = new int[this.arguments.size()];
        for (int i = 0; i < this.arguments.size(); i++) {
            this.argumentIxs[i] = strategy.getNodeIx(this.arguments.get(i));
        }
    }

    /** The operator node for which we seek an image. */
    final OperatorNode node;
    /** The operation determined by the product edge. */
    final Operation operation;
    /** The factory needed to create value nodes for the calculated outcomes. */
    /** List of operands of the operator node. */
    final List<VariableNode> arguments;
    /** The target node of the operation. */
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
     * Record of a node search item, storing an iterator over the candidate
     * images.
     * @author Arend Rensink
     * @version $Revision $
     */
    private class OperatorNodeRecord extends SingularRecord {
        /**
         * Creates a record based on a given underlying matcher.
         */
        OperatorNodeRecord(Search search) {
            super(search);
        }

        @Override
        public String toString() {
            return String.format("%s = %s",
                OperatorNodeSearchItem.this.toString(),
                this.search.getNode(OperatorNodeSearchItem.this.targetIx));
        }

        @Override
        public void initialise(HostGraph host) {
            super.initialise(host);
            this.factory = host.getFactory();
            this.targetPreMatch =
                this.search.getNodeSeed(OperatorNodeSearchItem.this.targetIx);
        }

        @Override
        boolean find() {
            boolean result;
            Object outcome = calculateResult();
            if (outcome == null) {
                result = false;
            } else if (OperatorNodeSearchItem.this.value != null) {
                result = OperatorNodeSearchItem.this.value.equals(outcome);
            } else if (OperatorNodeSearchItem.this.targetFound
                || this.targetPreMatch != null) {
                HostNode targetFind = this.targetPreMatch;
                if (targetFind == null) {
                    targetFind =
                        this.search.getNode(OperatorNodeSearchItem.this.targetIx);
                }
                result = ((ValueNode) targetFind).getValue().equals(outcome);
            } else {
                ValueNode targetImage =
                    this.factory.createValueNode(
                        OperatorNodeSearchItem.this.operation.getResultAlgebra(),
                        outcome);
                this.image = targetImage;
                result = write();
            }
            return result;
        }

        @Override
        void erase() {
            if (this.image != null) {
                this.search.putNode(OperatorNodeSearchItem.this.targetIx, null);
            }
        }

        @Override
        boolean write() {
            return this.image == null
                || this.search.putNode(OperatorNodeSearchItem.this.targetIx,
                    this.image);
        }

        /**
         * Calculates the result of the operation in {@link #getNode()}, based
         * on the currently installed images of the arguments.
         * @return the result of the operation, or <code>null</code> if it
         *         cannot be calculated due to the fact that one of the
         *         arguments was bound to a non-value.
         */
        private Object calculateResult() throws IllegalArgumentException {
            Object[] operands =
                new Object[OperatorNodeSearchItem.this.arguments.size()];
            for (int i = 0; i < OperatorNodeSearchItem.this.arguments.size(); i++) {
                HostNode operandImage =
                    this.search.getNode(OperatorNodeSearchItem.this.argumentIxs[i]);
                if (!(operandImage instanceof ValueNode)) {
                    // one of the arguments was not bound to a value
                    // (probably due to some typing error in another rule)
                    // and so we cannot match the node
                    return null;
                }
                operands[i] = ((ValueNode) operandImage).getValue();
            }
            try {
                Object result =
                    OperatorNodeSearchItem.this.operation.apply(Arrays.asList(operands));
                if (PRINT) {
                    System.out.printf("Applying %s to %s yields %s%n",
                        OperatorNodeSearchItem.this.operation,
                        Arrays.asList(operands), result);
                }
                return result;
            } catch (IllegalArgumentException exc) {
                return null;
            }
        }

        /** The pre-matched target node, if any. */
        private HostNode targetPreMatch;
        /** The factory for creating value nodes for the outcomes. */
        private HostFactory factory;
        /**
         * The value node found as the outcome of the operation,
         * if this was not predetermined.
         */
        private ValueNode image;
        /** Flag to control debug printing. */
        static private final boolean PRINT = false;
    }
}
