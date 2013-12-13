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
import groove.explore.util.LTSLabels;
import groove.explore.util.LTSLabels.Flag;
import groove.grammar.model.FormatException;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Node;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.verify.FormulaParser.Token;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.EnumMap;
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
public class CTLMarker {
    /**
     * Constructs a marker for a given (top-level) formula over a given
     * graph, where certain special LTS-related properties may be indicated
     * by special labels.
     * @throws FormatException if the model has special state markers that occur
     * on edge labels 
     */
    public CTLMarker(Formula formula, Graph model, LTSLabels ltsLabels)
        throws FormatException {
        assert model != null;
        this.formula = formula;
        this.model = model;
        this.ltsLabels = ltsLabels;
        testFormat();
        init();
    }

    /**
     * Constructs a marker for a given (top-level) formula over a given
     * GTS.
     */
    public CTLMarker(Formula formula, GTS model) {
        assert model != null;
        this.formula = formula;
        this.model = model;
        this.ltsLabels = null;
        init();
    }

    /** Tests if the model is consistent with the special state markers. 
     * @throws FormatException if the model has special state markers that occur
     * on edge labels 
     */
    private void testFormat() throws FormatException {
        for (Node node : this.model.nodeSet()) {
            Set<? extends Edge> outEdges = this.model.outEdgeSet(node);
            for (Edge outEdge : outEdges) {
                if (getFlag(outEdge.label().text()) != null
                    && !outEdge.isLoop()) {
                    throw new FormatException(
                        "Special state marker '%s' occurs as edge label in model",
                        outEdge.label());
                }
            }
        }
    }

    /** Creates and initialises the internal data structures for marking. 
     */
    private void init() {
        // initialise the formula numbering
        registerFormula(this.formula);
        registerFormula(START_ATOM);
        // initialise the markings array
        int nodeCount = this.nodeCount = this.model.nodeCount();
        this.marking = new BitSet[this.formulaNr.size()];
        for (int i : this.atoms.values()) {
            this.marking[i] = new BitSet(nodeCount);
        }
        // initialise the forward count and backward structure
        // & initialise the outgoing transition count
        // as well as the satisfaction of the atoms
        this.states = new Node[nodeCount];
        @SuppressWarnings("unchecked")
        List<Integer>[] backward = new List[nodeCount];
        this.outCount = new int[nodeCount];
        // collect the special flag labels used in the formula
        Map<Flag,Integer> flagNrs = new EnumMap<Flag,Integer>(Flag.class);
        for (Flag flag : Flag.values()) {
            Integer flagIx = this.atoms.get(flagText.get(flag));
            if (flagIx != null) {
                flagNrs.put(flag, flagIx);
            }
        }
        for (Node node : this.model.nodeSet()) {
            Set<? extends Edge> outEdges = this.model.outEdgeSet(node);
            int nodeNr = node.getNumber();
            this.states[nodeNr] = node;
            int specialEdgeCount = 0;
            for (Edge outEdge : outEdges) {
                String label = outEdge.label().text();
                Flag flag = getFlag(label);
                if (flag == null) {
                    Node target = outEdge.target();
                    int targetNr = target.getNumber();
                    if (backward[targetNr] == null) {
                        backward[targetNr] = new ArrayList<Integer>();
                    }
                    backward[targetNr].add(nodeNr);
                    setAtom(nodeNr, label);
                } else {
                    assert outEdge.isLoop() : String.format(
                        "Special state marker '%s' occurs as edge label in model",
                        outEdge.label());
                    setAtom(nodeNr, flagText.get(flag));
                    specialEdgeCount++;
                }
            }
            // subtract the special atoms from the outgoing edge count,
            // if the model is not a GTS
            this.outCount[nodeNr] = outEdges.size() - specialEdgeCount;
            // Test the state markers in case we are in a GTS
            for (Map.Entry<Flag,Integer> flagEntry : flagNrs.entrySet()) {
                if (isSpecial(node, flagEntry.getKey())) {
                    this.marking[flagEntry.getValue()].set(nodeNr);
                }
            }
        }
        // Calculate the backward structure
        this.backward = new int[nodeCount][];
        for (int i = 0; i < nodeCount; i++) {
            int backCount = backward[i] == null ? 0 : backward[i].size();
            int[] backEntry = new int[backCount];
            for (int j = 0; j < backCount; j++) {
                backEntry[j] = backward[i].get(j);
            }
            this.backward[i] = backEntry;
        }
    }

    /** Returns the special-label-flag corresponding to a given edge label, if any. */
    private Flag getFlag(String label) {
        Flag result = null;
        if (this.ltsLabels != null) {
            return this.ltsLabels.getFlag(label);
        }
        return result;
    }

    /**
     * Marks a given node as satisfying an atomic proposition,
     * if the proposition occurs in the formula.
     * @param nodeNr the node to be marked
     * @param atom the proposition text
     */
    private void setAtom(int nodeNr, String atom) {
        Integer atomIx = this.atoms.get(atom);
        if (atomIx != null) {
            this.marking[atomIx].set(nodeNr);
        }
    }

    /** 
     * Registers a formula and all its subformulas
     * into the {@link #formulaNr} and {@link #atoms} maps.
     */
    private void registerFormula(Formula formula) {
        if (!this.formulaNr.containsKey(formula)) {
            Integer index = this.formulaNr.size();
            this.formulaNr.put(formula, index);
            switch (formula.getToken().getArity()) {
            case 0:
                if (formula.getToken() == TRUE || formula.getToken() == FALSE) {
                    break;
                }
                String prop = formula.getProp();
                assert prop != null;
                this.atoms.put(prop, index);
                break;
            case 1:
                registerFormula(formula.getArg1());
                break;
            case 2:
                registerFormula(formula.getArg1());
                registerFormula(formula.getArg2());
                break;
            default:
                throw new IllegalStateException();
            }
        }
    }

    /** 
     * Tests if a given node satisfies one of the special ({@link Flag}) atoms.
     */
    private boolean isSpecial(Node node, Flag flag) {
        boolean result = false;
        if (this.model instanceof GTS) {
            switch (flag) {
            case FINAL:
                result = ((GTS) this.model).isFinal((GraphState) node);
                break;
            case OPEN:
                result = !((GraphState) node).isClosed();
                break;
            case RESULT:
                result = ((GTS) this.model).isResult((GraphState) node);
                break;
            case START:
                result = node == ((GTS) this.model).startState();
            }
        }
        return result;
    }

    /**
     * Verifies the top-level property.
     */
    private void verify() {
        mark(this.formula);
        setVerified();
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
        BitSet result = new BitSet(this.nodeCount);
        for (int i = 0; i < this.nodeCount; i++) {
            result.set(i);
        }
        return result;
    }

    /** Returns the empty (bit) set. */
    private BitSet computeFalse() {
        return new BitSet(this.nodeCount);
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
                result.set(i, arg1.get(i));
            }
        }
        return result;
    }

    /** Returns the implication of two bit sets */
    private BitSet computeEquiv(BitSet arg1, BitSet arg2) {
        BitSet result = new BitSet(this.nodeCount);
        for (int i = 0; i < this.nodeCount; i++) {
            result.set(i, arg1.get(i) == arg1.get(i));
        }
        return result;
    }

    /**
     * Returns the bit set for the EX operator.
     */
    private BitSet computeEX(BitSet arg) {
        BitSet result = new BitSet(this.nodeCount);
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
        BitSet result = new BitSet(this.nodeCount);
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
        BitSet result = new BitSet(this.nodeCount);
        BitSet arg1Marking = arg1;
        BitSet arg2Marking = arg2;
        // mark the states that satisfy the second operand
        Queue<Integer> newStates = new LinkedList<Integer>();
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
        BitSet result = new BitSet(this.nodeCount);
        int[] markedNextCount = new int[this.nodeCount];
        // mark the states that satisfy the second operand
        Queue<Integer> newStates = new LinkedList<Integer>();
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

    /** 
     * Tests if the top-level formula has a given boolean value for the initial state.
     * @param value the value for which the top-level formula is tested
     */
    public boolean hasValue(boolean value) {
        return hasValue(this.formula, value);
    }

    /** Tests if the top-level formula has a given boolean value for a given state.
     * @param state the state that is being tested
     * @param value the value for which the top-level formula is tested
     */
    public boolean hasValue(Node state, boolean value) {
        return hasValue(this.formula, state, value);
    }

    /** Tests the satisfaction of a given subformula in the initial state. */
    private boolean hasValue(Formula formula, boolean value) {
        if (!hasRoot()) {
            throw new IllegalArgumentException(
                "The model being checked does not have an unabiguous root node");
        }
        assert this.formulaNr.containsKey(formula);
        return hasValue(formula, getRoot(), value);
    }

    /** Tests the satisfaction of a given subformula in a given state. */
    private boolean hasValue(Formula formula, Node state, boolean value) {
        assert this.formulaNr.containsKey(formula);
        if (!isVerified()) {
            verify();
        }
        return this.marking[this.formulaNr.get(formula)].get(state.getNumber()) == value;
    }

    /** Indicates if the model has an unambiguous root. */
    private boolean hasRoot() {
        return this.marking[this.formulaNr.get(START_ATOM)].cardinality() == 1;
    }

    /** Returns the (unambiguous) root node of the model, if there is any.
     */
    private Node getRoot() {
        Node result = null;
        if (this.model instanceof GTS) {
            result = ((GTS) this.model).startState();
        } else {
            BitSet startNodes = this.marking[this.formulaNr.get(START_ATOM)];
            if (startNodes.cardinality() == 1) {
                result = this.states[startNodes.nextSetBit(0)];
            }
        }
        return result;
    }

    /** Reports the number of states that satisfy or fail to satisfy the top-level formula. */
    public int getCount(boolean value) {
        return getCount(this.formula, value);
    }

    /** Reports the number of states that satisfy or fail to satisfy a given subformula. */
    private int getCount(Formula formula, boolean value) {
        assert this.formulaNr.containsKey(formula);
        if (!isVerified()) {
            verify();
        }
        int result = 0;
        BitSet sat = this.marking[this.formulaNr.get(formula)];
        for (int i = 0; i < this.nodeCount; i++) {
            if (sat.get(i) == value) {
                result++;
            }
        }
        return result;
    }

    /** Returns an iterable over the states that satisfy or fail to satisfy the top-level formula. */
    public Iterable<Node> getStates(boolean value) {
        return getStates(this.formula, value);
    }

    /** Returns an iterable over the states that satisfy or fail to satisfy a given subformula. */
    public Iterable<Node> getStates(Formula formula, final boolean value) {
        assert this.formulaNr.containsKey(formula);
        if (!isVerified()) {
            verify();
        }
        final BitSet sat = this.marking[this.formulaNr.get(formula)];
        return new Iterable<Node>() {
            @Override
            public Iterator<Node> iterator() {
                return new Iterator<Node>() {
                    @Override
                    public boolean hasNext() {
                        return this.stateIx >= 0
                            && this.stateIx < CTLMarker.this.nodeCount;
                    }

                    @Override
                    public Node next() {
                        if (!hasNext()) {
                            throw new NoSuchElementException();
                        }
                        Node result = CTLMarker.this.states[this.stateIx];
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
    private final Graph model;
    private final LTSLabels ltsLabels;
    /**
     * Mapping from subformulas to (consecutive) numbers
     */
    private final Map<Formula,Integer> formulaNr =
        new HashMap<Formula,Integer>();
    /** Mapping from atomic propositions (as literal strings) to formula numbers. */
    private final Map<String,Integer> atoms = new HashMap<String,Integer>();
    /** Marking matrix: 1st dimension = state, 2nd dimension = formula. */
    private BitSet[] marking;
    /** Backward reachability matrix. */
    private int[][] backward;
    /** Backward reachability matrix. */
    private int[] outCount;
    /** State number-indexed array of states in the GTS. */
    private Node[] states;
    /** State count of the transition system. */
    private int nodeCount;

    /** Indicates if the {@link #verify()} method has been invoked. */
    private boolean isVerified() {
        return this.verified;
    }

    /** Sets the {@link #isVerified()} property tp {@code true}. */
    private void setVerified() {
        this.verified = true;
    }

    private boolean verified;
    /** Mapping from flags to the text of the corresponding atomic proposition. */
    static final Map<Flag,String> flagText =
        new EnumMap<LTSLabels.Flag,String>(Flag.class);
    /** Mapping from flags to the corresponding atomic formula. */
    /** Mapping from special atomic formulae to the corresponding flags. */
    static final Map<Formula,Flag> formulaFlag = new HashMap<Formula,Flag>();
    static {
        for (Flag flag : Flag.values()) {
            String text = "$" + flag.getDefault();
            flagText.put(flag, text);
            Formula atom = Formula.Atom(text);
            formulaFlag.put(atom, flag);
        }
    }
    /** Proposition text expressing that a node is the start state of the GTS. */
    static public final Formula START_ATOM =
        Formula.Atom(flagText.get(Flag.START));
}
