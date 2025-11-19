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
package nl.utwente.groove.verify;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Stream;

import nl.utwente.groove.graph.Edge;
import nl.utwente.groove.graph.EdgeRole;
import nl.utwente.groove.graph.Node;
import nl.utwente.groove.util.Exceptions;

/**
 * Implementation of the CTL model checking algorithm.
 * @author Arend Rensink
 * @version $Revision$
 */
public class CTLMarker {
    /**
     * Constructs a marker for a given (top-level) formula over a given
     * graph, where certain special LTS-related properties may be indicated
     * by special labels.
     */
    public CTLMarker(Formula formula, CTLModelChecker.ModelFacade model) {
        assert model != null;
        this.formula = formula;
        this.model = model;
        init();
    }

    /** The (top-level) formula to check. */
    private final Formula formula;
    /** The GTS on which to check the formula. */
    private final CTLModelChecker.ModelFacade model;

    /**
     * Creates and initialises the internal data structures for marking.
     * To be invoked immediately after construction.
     */
    private void init() {
        // initialise the formula numbering
        registerPropositions(this.formula);
        int nodeCount = this.nodeCount = this.model.nodeCount();
        // initialise the forward count and backward structure
        // & initialise the outgoing transition count
        // as well as the satisfaction of the atoms
        @SuppressWarnings("unchecked")
        List<Integer>[] backward = new List[nodeCount];
        this.outCount = new int[nodeCount];
        // build the backward reachability matrix and mark the atomic propositions
        for (Node node : this.model.nodeSet()) {
            // EZ says: change for SF bug #442.
            // int nodeNr = node.getNumber();
            int nodeIx = this.model.toIndex(node);
            this.model.toProps(node).forEach(p -> testProposition(p, nodeIx));
            int outCount = 0;
            for (Edge outEdge : this.model.outEdges(node)) {
                if (outEdge.getRole() == EdgeRole.BINARY) {
                    Node target = outEdge.target();
                    // EZ says: change for SF bug #442.
                    // int targetNr = target.getNumber();
                    int targetNr = this.model.toIndex(target);
                    if (backward[targetNr] == null) {
                        backward[targetNr] = new ArrayList<>();
                    }
                    backward[targetNr].add(nodeIx);
                    outCount++;
                }
                testProposition(this.model.toProp(outEdge), nodeIx);
            }
            // subtract the special atoms from the outgoing edge count,
            // if the model is not a GTS
            this.outCount[nodeIx] = outCount;
        }
        // Calculate the backward structure
        this.backward = new int[nodeCount][];
        for (int i = 0; i < nodeCount; i++) {
            int backCount = backward[i] == null
                ? 0
                : backward[i].size();
            int[] backEntry = new int[backCount];
            for (int j = 0; j < backCount; j++) {
                backEntry[j] = backward[i].get(j);
            }
            this.backward[i] = backEntry;
        }
    }

    /** Backward reachability matrix. */
    private int[][] backward;
    /** Number of outgoing non-special-label edges. */
    private int[] outCount;
    /** State count of the transition system. */
    private int nodeCount;
    /** Mapping from subformulas to satisfaction vectors. */
    private final Map<Formula,BitSet> marking = new HashMap<>();

    /**
     * Registers all propositions in the {@link #marking} map,
     * so they can be precomputed.
     */
    private void registerPropositions(Formula formula) {
        if (formula.isProp()) {
            var propVector = new PropSatVector(formula.getProp(), createVector());
            this.propVectors.add(propVector);
            this.marking.put(formula, propVector.vector());
        } else {
            formula.getArgs().forEach(this::registerPropositions);
        }
    }

    /** Tests a given proposition against all stored propositions
     */
    private void testProposition(Proposition prop, int nodeIx) {
        this.propVectors
            .stream()
            .filter(pv -> pv.matches(prop))
            .map(PropSatVector::vector)
            .forEach(b -> b.set(nodeIx));
    }

    private final List<PropSatVector> propVectors = new LinkedList<>();

    /**
     * Returns the satisfaction vector for a given formula.
     */
    private BitSet mark(Formula formula) {
        // use the existing result, if any
        BitSet result = this.marking.get(formula);
        if (result == null) {
            result = switch (formula.getOp()) {
            case FORALL -> markForall(formula.getArg1());
            case EXISTS -> markExists(formula.getArg1());
            default -> markLocal(formula);
            };
            this.marking.put(formula, result);
            if (DEBUG) {
                System.out.printf("Formula %s holds in %s%n", formula, result);
            }
        }
        return result;
    }

    /**
     * Returns the satisfaction vector for a given local formula.
     */
    private BitSet markLocal(Formula formula) {
        BitSet result;
        LogicOp token = formula.getOp();
        var illegalOp = Exceptions
            .illegalArg("Top level operator '%s' in formula %s not allowed", token, formula);
        result = switch (token.getArity()) {
        case 0 -> {
            yield switch (token) {
            case TRUE -> computeTrue();
            case FALSE -> computeFalse();
            // the only other nullary operator is PROP, which is pre-computed
            default -> throw illegalOp;
            };
        }
        case 1 -> {
            BitSet arg1 = mark(formula.getArg1());
            yield switch (token) {
            case NOT -> computeNeg(arg1);
            // other unary operators do not exist
            default -> throw illegalOp;
            };
        }
        case 2 -> {
            BitSet arg1 = mark(formula.getArg1());
            BitSet arg2 = mark(formula.getArg2());
            yield switch (token) {
            case OR -> computeOr(arg1, arg2);
            case AND -> computeAnd(arg1, arg2);
            case IMPLIES -> computeImplies(arg1, arg2);
            case FOLLOWS -> computeImplies(arg2, arg1);
            case EQUIV -> computeEquiv(arg1, arg2);
            // other binary operators do not exist
            default -> throw illegalOp;
            };
        }
        // other arities do not exist
        default -> throw Exceptions.UNREACHABLE;
        };
        if (DEBUG) {
            System.out.printf("Formula %s holds in %s%n", formula, result);
        }
        return result;
    }

    private BitSet markExists(Formula property) {
        var op = property.getOp();
        var arg1 = property.getArg1();
        switch (op.getArity()) {
        case 1:
            return switch (op) {
            case NEXT -> computeEX(mark(arg1));
            // EF a -> E(false U a)
            case EVENTUALLY -> mark(Formula.ff().EU(arg1));
            // EG a -> !AF !a
            case ALWAYS -> mark(arg1.neg().AF().neg());
            default -> throw Exceptions.UNREACHABLE;
            };
        case 2:
            var arg2 = property.getArg2();
            return switch (op) {
            case UNTIL -> computeEU(mark(arg1), mark(arg2));
            // E(a W b) = !A(!b U !a)
            case W_UNTIL -> mark(arg2.neg().AU(arg1.neg()).neg());
            // E(a R b) = E(b W a&b)
            case RELEASE -> mark(arg2.EW(arg1.and(arg2)));
            // E(a M b) = E(b U a&b)
            case S_RELEASE -> mark(arg2.EU(arg1.and(arg2)));
            default -> throw Exceptions.UNREACHABLE;
            };
        default:
            throw Exceptions.UNREACHABLE;
        }
    }

    private BitSet markForall(Formula property) {
        var op = property.getOp();
        var arg1 = property.getArg1();
        switch (op.getArity()) {
        case 1:
            return switch (op) {
            case NEXT -> computeAX(mark(arg1));
            // AF a -> A(false U a)
            case EVENTUALLY -> mark(Formula.ff().AU(arg1));
            // AG a -> !EF !a
            case ALWAYS -> mark(arg1.neg().EF().neg());
            default -> throw Exceptions.UNREACHABLE;
            };
        case 2:
            var arg2 = property.getArg2();
            return switch (op) {
            case UNTIL -> computeAU(mark(arg1), mark(arg2));
            // A(a W b) = !E(!b U !a)
            case W_UNTIL -> mark(arg2.neg().EU(arg1.neg()).neg());
            // A(a R b) = A(b W a&b)
            case RELEASE -> mark(arg2.AW(arg1.and(arg2)));
            // A(a M b) = A(b U a&b)
            case S_RELEASE -> mark(arg2.AU(arg1.and(arg2)));
            default -> throw Exceptions.UNREACHABLE;
            };
        default:
            throw Exceptions.UNREACHABLE;
        }
    }

    /** Returns the (bit) set of all states. */
    private BitSet computeTrue() {
        BitSet result = createVector();
        for (int i = 0; i < this.nodeCount; i++) {
            result.set(i);
        }
        return result;
    }

    /** Returns the empty (bit) set. */
    private BitSet computeFalse() {
        return createVector();
    }

    /** Returns the negation of a (bit) set. */
    private BitSet computeNeg(BitSet arg) {
        BitSet result = (BitSet) arg.clone();
        result.flip(0, this.nodeCount);
        return result;
    }

    /** Returns the disjunction of two bit sets. */
    private BitSet computeOr(BitSet arg1, BitSet arg2) {
        BitSet result = (BitSet) arg1.clone();
        result.or(arg2);
        return result;
    }

    /** Returns the conjunction of two bit sets */
    private BitSet computeAnd(BitSet arg1, BitSet arg2) {
        BitSet result = (BitSet) arg1.clone();
        result.and(arg2);
        return result;
    }

    /** Returns the implication of two bit sets */
    private BitSet computeImplies(BitSet arg1, BitSet arg2) {
        BitSet result = (BitSet) arg2.clone();
        for (int i = 0; i < this.nodeCount; i++) {
            if (!result.get(i)) {
                result.set(i, !arg1.get(i));
            }
        }
        return result;
    }

    /** Returns the implication of two bit sets */
    private BitSet computeEquiv(BitSet arg1, BitSet arg2) {
        BitSet result = createVector();
        for (int i = 0; i < this.nodeCount; i++) {
            result.set(i, arg1.get(i) == arg1.get(i));
        }
        return result;
    }

    /**
     * Returns the bit set for the EX operator.
     */
    private BitSet computeEX(BitSet arg) {
        BitSet result = createVector();
        for (int i = 0; i < this.nodeCount; i++) {
            if (arg.get(i)) {
                int[] preds = this.backward[i];
                for (int p = 0; p < preds.length; p++) {
                    result.set(preds[p]);
                }
            }
        }
        return result;
    }

    /**
     * Returns the bit set for the AX operator.
     */
    private BitSet computeAX(BitSet arg) {
        BitSet result = createVector();
        int[] nextCounts = new int[this.nodeCount];
        for (int i = 0; i < this.nodeCount; i++) {
            if (arg.get(i)) {
                int[] preds = this.backward[i];
                for (int p = 0; p < preds.length; p++) {
                    int pred = preds[p];
                    nextCounts[pred]++;
                    if (this.outCount[pred] == nextCounts[pred]) {
                        result.set(pred);
                    }
                }
            }
            // the property vacuously holds for deadlocked states
            if (this.outCount[i] == 0) {
                result.set(i);
            }
        }
        return result;
    }

    /**
     * Constructs the bit set for the EU operator.
     */
    private BitSet computeEU(BitSet arg1, BitSet arg2) {
        BitSet result = createVector();
        BitSet arg1Marking = arg1;
        BitSet arg2Marking = arg2;
        // mark the states that satisfy the second operand
        Queue<Integer> newStates = new LinkedList<>();
        for (int i = 0; i < this.nodeCount; i++) {
            if (arg2Marking.get(i)) {
                result.set(i);
                newStates.add(i);
            }
        }
        // recurse to the predecessors of newly marked states
        while (!newStates.isEmpty()) {
            int newState = newStates.poll();
            int[] preds = this.backward[newState];
            for (int b = 0; b < preds.length; b++) {
                int pred = preds[b];
                // mark the predecessor, if it satisfies the first operand
                // and it is not yet marked
                if (arg1Marking.get(pred) && !result.get(pred)) {
                    result.set(pred);
                    newStates.add(pred);
                }
            }
        }
        return result;
    }

    /**
     * Constructs the bit set for the AU operator.
     */
    private BitSet computeAU(BitSet arg1, BitSet arg2) {
        BitSet result = createVector();
        int[] markedNextCount = new int[this.nodeCount];
        // mark the states that satisfy the second operand
        Queue<Integer> newStates = new LinkedList<>();
        for (int i = 0; i < this.nodeCount; i++) {
            if (arg2.get(i)) {
                result.set(i);
                newStates.add(i);
            }
        }
        // recurse to the predecessors of newly marked states
        while (!newStates.isEmpty()) {
            int newState = newStates.poll();
            int[] preds = this.backward[newState];
            for (int b = 0; b < preds.length; b++) {
                int pred = preds[b];
                // mark the predecessor, if all successors have now been
                // marked, it satisfies the first operand and has not yet
                // been marked
                if (arg1.get(pred) && !result.get(pred)) {
                    markedNextCount[pred]++;
                    int nextTotal = this.outCount[pred];
                    if (markedNextCount[pred] == nextTotal) {
                        result.set(pred);
                        newStates.add(pred);
                    }
                }
            }
        }
        return result;
    }

    /** Callback method to create a satisfaction vector. */
    private BitSet createVector() {
        return new BitSet(this.nodeCount);
    }

    /**
     * Tests if the top-level formula holds for the initial state.
     */
    public boolean hasValue() {
        // EZ says: change for SF bug #442.
        int rootIx = this.model.toIndex(this.model.getRoot());
        return mark(this.formula).get(rootIx);
    }

    /** Reports the number of states that satisfy the top-level formula. */
    public int getCount() {
        return mark(this.formula).cardinality();
    }

    /** Returns a stream over the nodes that satisfy the top-level formula. */
    public Stream<Node> stateStream() {
        return mark(this.formula).stream().mapToObj(this.model::toNode);
    }

    /** Debug flag */
    static private boolean DEBUG = false;

    /** Satisfaction vector for a given proposition. */
    static private record PropSatVector(Proposition prop, BitSet vector) {
        /** Tests whether the proposition of this vector matches another.
         * @see Proposition#matches(Proposition)
         */
        boolean matches(Proposition other) {
            return this.prop.matches(other);
        }
    }
}
