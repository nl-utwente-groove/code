/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2023
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
 * $Id$
 */
package nl.utwente.groove.match.plan;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import nl.utwente.groove.algebra.AlgebraFamily;
import nl.utwente.groove.algebra.Operation;
import nl.utwente.groove.grammar.host.HostFactory;
import nl.utwente.groove.grammar.host.HostGraph;
import nl.utwente.groove.grammar.host.HostNode;
import nl.utwente.groove.grammar.host.ValueNode;
import nl.utwente.groove.grammar.rule.OperatorNode;
import nl.utwente.groove.grammar.rule.RuleNode;
import nl.utwente.groove.grammar.rule.VariableNode;
import nl.utwente.groove.match.TreeMatch;
import nl.utwente.groove.match.plan.PlanSearchStrategy.Search;

/**
 * A search item for an operator node.
 * @author Arend Rensink
 * @version $Revision$
 */
class OperatorNodeSearchItem extends AbstractSearchItem {
    /**
     * Creates a search item for a given operator node, all of whose arguments
     * have already been matched. More properly speaking, the search item calculates
     * the value of the target node, and either installs it or compares it with
     * the value already installed.
     * @param node the operator node to be matched
     * @param family the algebra family to take values from
     */
    public OperatorNodeSearchItem(OperatorNode node, AlgebraFamily family) {
        this.node = node;
        this.setOperator = node.getOperator().isVarArgs();
        this.operation = family.getOperation(node.getOperator());
        assert this.operation != null;
        this.arguments = node.getArguments();
        this.target = node.getTarget();
        this.boundNodes = new HashSet<>();
        this.boundNodes.add(node);
        this.neededNodes = new HashSet<>(this.arguments);
        if (this.target.hasConstant()) {
            this.neededNodes.add(this.target);
            this.value = family.toValue(this.target.getConstant());
        } else {
            this.boundNodes.add(this.target);
            this.value = null;
        }
    }

    @Override
    public OperatorNodeRecord createRecord(nl.utwente.groove.match.plan.PlanSearchStrategy.Search matcher) {
        return this.setOperator
            ? new SetOperatorNodeRecord(matcher)
            : new OperatorNodeRecord(matcher);
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
        return String
            .format("Compute %s%s-->%s", this.operation.toString(), this.arguments, this.target);
    }

    /**
     * If the other item is also a {@link OperatorNodeSearchItem}, compares on
     * the basis of the label and then the arguments; otherwise, delegates to
     * <code>super</code>.
     */
    @Override
    public int compareTo(SearchItem item) {
        int result = super.compareTo(item);
        if (result != 0) {
            return result;
        }
        OperatorNode hisNode = ((OperatorNodeSearchItem) item).getNode();
        List<VariableNode> hisArguments = hisNode.getArguments();
        result = this.operation.getName().compareTo(hisNode.getOperator().getName());
        if (result != 0) {
            return result;
        }
        for (int i = 0; i < this.arguments.size(); i++) {
            result = this.arguments.get(i).compareTo(hisArguments.get(i));
            if (result != 0) {
                return result;
            }
        }
        result = this.target.compareTo(hisNode.getTarget());
        if (result != 0) {
            return result;
        }
        result = getNode().compareTo(hisNode);
        return result;
    }

    /**
     * This implementation returns the operator node's hash code.
     */
    @Override
    int getRating() {
        return 0;
    }

    @Override
    int computeHashCode() {
        return super.computeHashCode() + 31 * getNode().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        OperatorNodeSearchItem other = (OperatorNodeSearchItem) obj;
        return getNode().equals(other.getNode());
    }

    /** Returns the operator node being calculated by this search item. */
    public OperatorNode getNode() {
        return this.node;
    }

    /** The operator node for which we seek an image. */
    final OperatorNode node;
    /** Flag signalling that the operator is a set operator. */
    final boolean setOperator;
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

    @Override
    public void activate(PlanSearchStrategy strategy) {
        this.targetFound = strategy.isNodeFound(this.target);
        this.targetIx = strategy.getNodeIx(this.target);
        if (this.setOperator) {
            this.source = this.arguments.get(0);
            ConditionSearchItem item
                = (ConditionSearchItem) strategy.getPlan().getBinder(this.source);
            this.sourceConditionIx = strategy.getCondIx(item.getCondition());
        } else {
            this.argumentIxs = new int[this.arguments.size()];
            for (int i = 0; i < this.arguments.size(); i++) {
                this.argumentIxs[i] = strategy.getNodeIx(this.arguments.get(i));
            }
        }
    }

    /** Indices of the argument nodes in the result. */
    int[] argumentIxs;
    /** Flag indicating if the target node has been found at search time. */
    boolean targetFound;
    /** Index of {@link #target} in the result. */
    int targetIx;
    /** Index (in the result) of the condition binding the set operator,
     * if the operator is a set operator. */
    int sourceConditionIx;
    /** Index of the condition in the result, if the operator is a set operator. */
    VariableNode source;

    /**
     * Record of a node search item, storing an iterator over the candidate
     * images.
     * @author Arend Rensink
     * @version $Revision$
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
            return String
                .format("%s = %s", OperatorNodeSearchItem.this.toString(),
                        this.search.getNode(OperatorNodeSearchItem.this.targetIx));
        }

        @Override
        public void initialise(HostGraph host) {
            super.initialise(host);
            this.factory = host.getFactory();
            this.targetPreMatch = this.search.getNodeSeed(OperatorNodeSearchItem.this.targetIx);
        }

        @Override
        boolean find() {
            boolean result;
            Object outcome = calculateResult();
            if (outcome == null) {
                result = false;
            } else if (OperatorNodeSearchItem.this.value != null) {
                result = OperatorNodeSearchItem.this.value.equals(outcome);
            } else if (OperatorNodeSearchItem.this.targetFound || this.targetPreMatch != null) {
                HostNode targetFind = this.targetPreMatch;
                if (targetFind != null && OperatorNodeSearchItem.this.operation.isIndeterminate()) {
                    // the value of this indeterminate operation was seeded,
                    // hence this is a reconstruction of the match, hence we're going
                    // to assume the seeded value is correct
                    result = true;
                } else {
                    if (targetFind == null) {
                        targetFind = this.search.getNode(OperatorNodeSearchItem.this.targetIx);
                    }
                    result = ((ValueNode) targetFind).getValue().equals(outcome);
                }
            } else {
                ValueNode targetImage = this.factory
                    .createNode(OperatorNodeSearchItem.this.operation.getResultAlgebra(), outcome);
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
                || this.search.putNode(OperatorNodeSearchItem.this.targetIx, this.image);
        }

        /**
         * Calculates the result of the operation in {@link #getNode()}, based
         * on the currently installed images of the arguments.
         * @return the result of the operation, or <code>null</code> if it
         *         cannot be calculated due to the fact that one of the
         *         arguments was bound to a non-value.
         */
        Object calculateResult() {
            List<Object> arguments = calculateArguments();
            Object result = OperatorNodeSearchItem.this.operation.applyStrict(arguments);
            if (PRINT) {
                System.out
                    .printf("Applying %s to %s yields %s%n", OperatorNodeSearchItem.this.operation,
                            arguments, result);
            }
            return result;
        }

        /**
         * Calculates the arguments for the operator.
         */
        List<Object> calculateArguments() throws ClassCastException, NullPointerException {
            List<Object> arguments = IntStream
                .of(OperatorNodeSearchItem.this.argumentIxs)
                .mapToObj(this.search::getNode)
                .map(n -> ((ValueNode) n).getValue())
                .collect(Collectors.toList());
            return arguments;
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

    private class SetOperatorNodeRecord extends OperatorNodeRecord {
        /**
         * Creates an instance for a given search.
         */
        SetOperatorNodeRecord(Search search) {
            super(search);
        }

        @Override
        List<Object> calculateArguments() throws ClassCastException, NullPointerException {
            TreeMatch match
                = this.search.getSubMatch(OperatorNodeSearchItem.this.sourceConditionIx);
            @SuppressWarnings("null")
            List<Object> setArguments = match
                .getSubMatches()
                .stream()
                .map(TreeMatch::getPatternMap)
                .map(m -> m.getNode(OperatorNodeSearchItem.this.source))
                .map(n -> ((ValueNode) n).getValue())
                .collect(Collectors.toList());
            return Collections.singletonList(setArguments);
        }
    }
}
