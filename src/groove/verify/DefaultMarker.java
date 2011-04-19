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
 * $Id: CTLMatchingMarker.java,v 1.8 2008-03-05 16:52:10 rensink Exp $
 */
package groove.verify;

import static groove.verify.FormulaParser.Token.FALSE;
import static groove.verify.FormulaParser.Token.NOT;
import static groove.verify.FormulaParser.Token.TRUE;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.verify.FormulaParser.Token;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Set;

/**
 * Implementation of the CTL model checking algorithm.
 * @author Arend Rensink
 * @version $Revision $
 */
public class DefaultMarker {
    /**
     * Constructs a marker for a given (top-level) formula over a given
     * GTS.
     */
    public DefaultMarker(Formula formula, GTS gts) {
        this.formula = formula;
        this.gts = gts;
        init();
    }

    /** Creates and initialises the internal data structures for marking. */
    private void init() {
        // initialise the formula numbering
        initFormula(this.formula);
        int formulaCount = this.formulaNr.size();
        // initialise the markings array
        int stateCount = this.stateCount = this.gts.nodeCount();
        this.marking = new BitSet[formulaCount];
        for (int i : this.ruleAtoms.values()) {
            this.marking[i] = new BitSet(stateCount);
        }
        for (int i : this.stringAtoms.values()) {
            this.marking[i] = new BitSet(stateCount);
        }
        // initialise the forward count and backward structure
        // as well as the satisfaction of the atoms
        this.states = new GraphState[stateCount];
        @SuppressWarnings("unchecked")
        List<Integer>[] backward = new List[stateCount];
        Integer openAtomIndex = this.stringAtoms.get(Formula.OPEN_ATOM);
        Integer finalAtomIndex = this.stringAtoms.get(Formula.FINAL_ATOM);
        for (GraphState state : this.gts.nodeSet()) {
            Set<GraphTransition> transitions = state.getTransitionSet();
            int stateNr = state.getNumber();
            this.states[stateNr] = state;
            for (GraphTransition transition : transitions) {
                GraphState target = transition.target();
                int targetNr = target.getNumber();
                if (backward[targetNr] == null) {
                    backward[targetNr] = new ArrayList<Integer>();
                }
                backward[targetNr].add(stateNr);
                // check whether this transition corresponds to an atomic
                // proposition of the source state
                Integer atomIndex =
                    this.ruleAtoms.get(transition.getEvent().getRule().getName().toString());
                if (atomIndex != null) {
                    this.marking[atomIndex].set(stateNr);
                }
                atomIndex = this.stringAtoms.get(transition.label().text());
                if (atomIndex != null) {
                    this.marking[atomIndex].set(stateNr);
                }
            }
            if (openAtomIndex != null && !state.isClosed()) {
                this.marking[openAtomIndex].set(stateNr);
            }
            if (finalAtomIndex != null && this.gts.isFinal(state)) {
                this.marking[finalAtomIndex].set(stateNr);
            }
        }
        // copy the backward structure to the instance variable
        this.backward = new int[stateCount][];
        for (int i = 0; i < stateCount; i++) {
            int backCount = backward[i] == null ? 0 : backward[i].size();
            int[] backEntry = new int[backCount];
            for (int j = 0; j < backCount; j++) {
                backEntry[j] = backward[i].get(j);
            }
            this.backward[i] = backEntry;
        }
    }

    /** 
     * Initialises the {@link #formulaNr}, {@link #stringAtoms} and 
     * {@link #ruleAtoms} mappings.
     */
    private void initFormula(Formula formula) {
        Integer result = this.formulaNr.get(formula);
        if (result == null) {
            result = this.formulaNr.size();
            this.formulaNr.put(formula, result);
            switch (formula.getToken().getArity()) {
            case 0:
                if (formula.getToken() == TRUE || formula.getToken() == FALSE) {
                    break;
                }
                String prop = formula.getProp();
                assert prop != null;
                if (isRuleName(prop)) {
                    this.ruleAtoms.put(prop, result);
                } else {
                    this.stringAtoms.put(prop, result);
                }
                break;
            case 1:
                initFormula(formula.getArg1());
                break;
            case 2:
                initFormula(formula.getArg1());
                initFormula(formula.getArg2());
                break;
            default:
                throw new IllegalStateException();
            }
        }
    }

    /** Tests is a given string is the name of a rule in the GTS' rule system. */
    private boolean isRuleName(String text) {
        return this.gts.getGrammar().getRule(text) != null;
    }

    /**
     * Verifies the top-level property.
     */
    public void verify() {
        mark(this.formula);
    }

    /**
     * Delegates the marking process to the given CTL-expression.
     * @param property the CTL-expression to which the marking is delegated
     */
    private BitSet mark(Formula property) {
        int nr = this.formulaNr.get(property);
        // use the existing result, if any
        BitSet result = this.marking[nr];
        if (result != null) {
            return result;
        }
        Token token = property.getToken();
        // compute the arguments, if any
        BitSet arg1 = null;
        BitSet arg2 = null;
        switch (token.getArity()) {
        case 1:
            if (token == NOT) {
                arg1 = mark(property.getArg1());
            }
            break;
        case 2:
            arg1 = mark(property.getArg1());
            arg2 = mark(property.getArg2());
        }
        // compose the arguments according to the top level operator
        switch (token) {
        case TRUE:
            result = computeTrue();
            break;
        case FALSE:
            result = computeFalse();
            break;
        case NOT:
            result = computeNeg(arg1);
            break;
        case OR:
            result = computeOr(arg1, arg2);
            break;
        case AND:
            result = computeAnd(arg1, arg2);
            break;
        case IMPLIES:
            result = computeImplies(arg1, arg2);
            break;
        case FOLLOWS:
            result = computeImplies(arg2, arg1);
            break;
        case EQUIV:
            result = computeEquiv(arg1, arg2);
            break;
        case FORALL:
            result = markForall(property.getArg1());
            break;
        case EXISTS:
            result = markExists(property.getArg1());
            break;
        default:
            throw new IllegalArgumentException();
        }
        this.marking[nr] = result;
        return result;
    }

    private BitSet markExists(Formula property) {
        switch (property.getToken()) {
        case NEXT:
            return computeEX(mark(property.getArg1()));
        case UNTIL:
            return computeEU(mark(property.getArg1()), mark(property.getArg2()));
        case EVENTUALLY:
            throw new UnsupportedOperationException(
                "The EF(phi) construction should have been rewritten to a E(true U phi) construction.");
        case ALWAYS:
            throw new UnsupportedOperationException(
                "The EG(phi) construction should have been rewritten to a !(AF(!phi)) construction.");
        default:
            throw new IllegalArgumentException();
        }
    }

    private BitSet markForall(Formula property) {
        switch (property.getToken()) {
        case NEXT:
            return computeAX(mark(property.getArg1()));
        case UNTIL:
            return computeAU(mark(property.getArg1()), mark(property.getArg2()));
        case EVENTUALLY:
            throw new UnsupportedOperationException(
                "The AF(phi) construction should have been rewritten to a A(true U phi) construction.");
        case ALWAYS:
            throw new UnsupportedOperationException(
                "The AG(phi) construction should have been rewritten to a !(EF(!phi)) construction.");
        default:
            throw new IllegalArgumentException();
        }
    }

    /** Returns the (bit) set of all states. */
    private BitSet computeTrue() {
        BitSet result = new BitSet(this.stateCount);
        for (int i = 0; i < this.stateCount; i++) {
            result.set(i);
        }
        return result;
    }

    /** Returns the empty (bit) set. */
    private BitSet computeFalse() {
        return new BitSet(this.stateCount);
    }

    /** Returns the negation of a (bit) set. */
    private BitSet computeNeg(BitSet arg) {
        BitSet result = (BitSet) arg.clone();
        result.flip(0, this.stateCount);
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
        for (int i = 0; i < this.stateCount; i++) {
            if (!result.get(i)) {
                result.set(i, arg1.get(i));
            }
        }
        return result;
    }

    /** Returns the implication of two bit sets */
    private BitSet computeEquiv(BitSet arg1, BitSet arg2) {
        BitSet result = new BitSet(this.stateCount);
        for (int i = 0; i < this.stateCount; i++) {
            result.set(i, arg1.get(i) == arg1.get(i));
        }
        return result;
    }

    /**
     * Returns the bit set for the EX operator.
     */
    private BitSet computeEX(BitSet arg) {
        BitSet result = new BitSet(this.stateCount);
        for (int i = 0; i < this.stateCount; i++) {
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
        BitSet result = new BitSet(this.stateCount);
        int[] nextCounts = new int[this.stateCount];
        for (int i = 0; i < this.stateCount; i++) {
            if (arg.get(i)) {
                int[] preds = this.backward[i];
                for (int p = 0; p < preds.length; p++) {
                    int pred = preds[p];
                    nextCounts[pred]++;
                    if (this.states[pred].getTransitionCount() == nextCounts[pred]) {
                        result.set(pred);
                    }
                }
            }
            // the property vacuously holds for deadlocked states
            if (this.states[i].getTransitionCount() == 0) {
                result.set(i);
            }
        }
        return result;
    }

    /**
     * Constructs the bit set for the EU operator.
     */
    private BitSet computeEU(BitSet arg1, BitSet arg2) {
        BitSet result = new BitSet(this.stateCount);
        BitSet arg1Marking = arg1;
        BitSet arg2Marking = arg2;
        // mark the states that satisfy the second operand
        Queue<Integer> newStates = new LinkedList<Integer>();
        for (int i = 0; i < this.stateCount; i++) {
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
        BitSet result = new BitSet(this.stateCount);
        int[] markedNextCount = new int[this.stateCount];
        // mark the states that satisfy the second operand
        Queue<Integer> newStates = new LinkedList<Integer>();
        for (int i = 0; i < this.stateCount; i++) {
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
                    int nextTotal = this.states[pred].getTransitionCount();
                    if (markedNextCount[pred] == nextTotal) {
                        result.set(pred);
                        newStates.add(pred);
                    }
                }
            }
        }
        return result;
    }

    /** Tests the satisfaction of the top-level formula for the initial state. */
    public boolean hasValue(boolean value) {
        return hasValue(this.formula, value);
    }

    /** Tests the satisfaction of the top-level formula for a given state. */
    public boolean hasValue(GraphState state, boolean value) {
        return hasValue(this.formula, state, value);
    }

    /** Tests the satisfaction of a given subformula in the initial state. */
    public boolean hasValue(Formula formula, boolean value) {
        assert this.formulaNr.containsKey(formula);
        return hasValue(formula, this.gts.startState(), value);
    }

    /** Tests the satisfaction of a given subformula in a given state. */
    public boolean hasValue(Formula formula, GraphState state, boolean value) {
        assert this.formulaNr.containsKey(formula);
        return this.marking[this.formulaNr.get(formula)].get(state.getNumber()) == value;
    }

    /** Reports the number of states that satisfy or fail to satisfy the top-level formula. */
    public int getCount(boolean value) {
        return getCount(this.formula, value);
    }

    /** Reports the number of states that satisfy or fail to satisfy a given subformula. */
    public int getCount(Formula formula, boolean value) {
        assert this.formulaNr.containsKey(formula);
        int result = 0;
        BitSet sat = this.marking[this.formulaNr.get(formula)];
        for (int i = 0; i < this.stateCount; i++) {
            if (sat.get(i) == value) {
                result++;
            }
        }
        return result;
    }

    /** Returns an iterable over the states that satisfy or fail to satisfy the top-level formula. */
    public Iterable<GraphState> getStates(boolean value) {
        return getStates(this.formula, value);
    }

    /** Returns an iterable over the states that satisfy or fail to satisfy a given subformula. */
    public Iterable<GraphState> getStates(Formula formula, final boolean value) {
        assert this.formulaNr.containsKey(formula);
        final BitSet sat = this.marking[this.formulaNr.get(formula)];
        return new Iterable<GraphState>() {
            @Override
            public Iterator<GraphState> iterator() {
                return new Iterator<GraphState>() {
                    @Override
                    public boolean hasNext() {
                        return this.stateIx >= 0
                            && this.stateIx < DefaultMarker.this.stateCount;
                    }

                    @Override
                    public GraphState next() {
                        if (!hasNext()) {
                            throw new NoSuchElementException();
                        }
                        GraphState result =
                            DefaultMarker.this.states[this.stateIx];
                        this.stateIx =
                            value ? sat.nextSetBit(this.stateIx + 1)
                                    : sat.nextClearBit(this.stateIx + 1);
                        return result;
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }

                    int stateIx = value ? sat.nextSetBit(0)
                            : sat.nextClearBit(0);
                };
            }
        };
    }

    /** The (top-level) formula to check. */
    private final Formula formula;
    /** The GTS on which to check the formula. */
    private final GTS gts;
    /**
     * Mapping from subformulas to (consecutive) numbers
     */
    private final Map<Formula,Integer> formulaNr =
        new HashMap<Formula,Integer>();
    /** Mapping from atomic propositions (as literal strings) to formula numbers. */
    private final Map<String,Integer> stringAtoms =
        new HashMap<String,Integer>();
    /** Mapping from atomic propositions (as rule names) to formula numbers. */
    private final Map<String,Integer> ruleAtoms = new HashMap<String,Integer>();
    /** Marking matrix: 1st dimension = state, 2nd dimension = formula. */
    private BitSet[] marking;
    /** Backward reachability matrix. */
    private int[][] backward;
    /** State number-indexed array of states in the GTS. */
    private GraphState[] states;
    /** State count of the transition system. */
    private int stateCount;
}
