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

import groove.graph.EdgeRole;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.trans.Condition;
import groove.verify.FormulaParser.Token;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Visitor-implementation of {@link groove.verify.CTLFormulaMarker} using the
 * matching-strategy on the Atom-level.
 * @author Harmen Kastenberg
 * @version $Revision$ $Date: 2008-03-05 16:52:10 $
 */
public class CTLMatchingMarker implements CTLFormulaMarker {

    /**
     * Constructor.
     */
    public CTLMatchingMarker() {
        // empty constructor
    }

    /**
     * Default constructor.
     */
    public CTLMatchingMarker(CTLModelChecker verifier) {
        try {
            verifier.getProperty().linkPredicates(verifier);
        } catch (GraphPredicateNotFoundException gpnfe) {
            gpnfe.printStackTrace();
        }
    }

    public void markTrue(Marking marking, Formula property, GTS gts) {
        Iterator<? extends GraphState> stateIter = gts.nodeSet().iterator();
        while (stateIter.hasNext()) {
            GraphState nextState = stateIter.next();
            marking.set(nextState, property, true);
        }
    }

    public void markFalse(Marking marking, Formula property, GTS gts) {
        Iterator<? extends GraphState> stateIter = gts.nodeSet().iterator();
        while (stateIter.hasNext()) {
            GraphState nextState = stateIter.next();
            marking.set(nextState, property, false);
            property.getCounterExamples().add(nextState);
        }
    }

    public void markAtom(Marking marking, Formula property, GTS gts) {
        boolean specialAtom = markSpecialAtom(marking, property, gts);
        if (!specialAtom) {
            Condition condition = property.getProp();
            Iterator<? extends GraphState> stateIter = gts.nodeSet().iterator();
            while (stateIter.hasNext()) {
                GraphState nextState = stateIter.next();
                if (condition.getMatchIter(nextState.getGraph(), null).hasNext()) {
                    marking.set(nextState, property, true);
                    //                    System.out.println("nextState: " + property);
                } else {
                    marking.set(nextState, property, false);
                    property.getCounterExamples().add(nextState);
                    //                    System.out.println("nextState: !" + property);
                }
            }
        }
    }

    public void markNeg(Marking marking, Formula property, GTS gts) {
        Formula operand = property.getArg1();
        // first mark all states for its operand
        operand.mark(this, marking, gts);

        Iterator<? extends GraphState> stateIter = gts.nodeSet().iterator();
        while (stateIter.hasNext()) {
            GraphState nextState = stateIter.next();
            // all states that DO satisfy the operand CTL-expression
            // DO NOT satisfy this CTL-expression
            if (marking.satisfies(nextState, operand)) {
                marking.set(nextState, property, false);
                property.getCounterExamples().add(nextState);
            }
            // and vice versa
            else {
                marking.set(nextState, property, true);
            }
        }
    }

    public void markOr(Marking marking, Formula property, GTS gts) {
        // perform the marking for each operand
        property.getArg1().mark(this, marking, gts);
        property.getArg2().mark(this, marking, gts);

        Iterator<? extends GraphState> stateIter = gts.nodeSet().iterator();
        while (stateIter.hasNext()) {
            GraphState nextState = stateIter.next();
            boolean satisfies =
                marking.satisfies(nextState, property.getArg1());
            if (!satisfies) {
                satisfies = marking.satisfies(nextState, property.getArg2());
            }
            // if the state satisfies one of the predicates
            if (satisfies) {
                marking.set(nextState, property, true);
            } else {
                marking.set(nextState, property, false);
                property.getCounterExamples().add(nextState);
            }
        }
    }

    /**
     * Mark each state of the given state-space according the and-operator.
     * 
     * @param marking the current marking.
     * @param property the CTL-expression in question.
     * @param gts the state space as a graph transition system.
     */
    public void markAnd(Marking marking, Formula property, GTS gts) {
        property.getArg1().mark(this, marking, gts);
        property.getArg2().mark(this, marking, gts);

        Iterator<? extends GraphState> stateIter = gts.nodeSet().iterator();
        while (stateIter.hasNext()) {
            GraphState nextState = stateIter.next();
            boolean satisfies =
                marking.satisfies(nextState, property.getArg1());
            if (satisfies) {
                satisfies = marking.satisfies(nextState, property.getArg2());
            }
            // if the state satisfies all the predicates
            if (satisfies) {
                marking.set(nextState, property, true);
            } else {
                marking.set(nextState, property, false);
                property.getCounterExamples().add(nextState);
            }
        }
    }

    public void markExists(Marking marking, Formula property, GTS gts) {
        switch (property.getToken()) {
        case NEXT:
            markExistsNext(marking, property, gts);
            break;
        case UNTIL:
            markExistsUntil(marking, property, gts);
            break;
        case EVENTUALLY:
            throw new UnsupportedOperationException(
                "The EF(phi) construction should have been rewritten to a E(true U phi) construction.");
        case ALWAYS:
            throw new UnsupportedOperationException(
                "The EG(phi) construction should have been rewritten to a !(AF(!phi)) construction.");
        }
    }

    public void markForall(Marking marking, Formula property, GTS gts) {
        switch (property.getToken()) {
        case NEXT:
            throw new UnsupportedOperationException(
                "The AX(phi) construction should have been rewritten to a !(EX(!phi)) construction.");
        case UNTIL:
            markAllUntil(marking, property, gts);
            break;
        case EVENTUALLY:
            throw new UnsupportedOperationException(
                "The AF(phi) construction should have been rewritten to a A(true U phi) construction.");
        case ALWAYS:
            throw new UnsupportedOperationException(
                "The AG(phi) construction should have been rewritten to a !(EF(!phi)) construction.");
        }
    }

    public void markNext(Marking marking, Formula property, GTS gts) {
        throw new UnsupportedOperationException(
            "In CTL, the Next-operator should be bounded by a path quantifier.");
    }

    public void markUntil(Marking marking, Formula property, GTS gts) {
        throw new UnsupportedOperationException(
            "In CTL, the Until-operator should be bounded by a path quantifier.");
    }

    public void markFinally(Marking marking, Formula property, GTS gts) {
        throw new UnsupportedOperationException(
            "In CTL, the Finally-operator should be bounded by a path quantifier.");
    }

    public void markGlobally(Marking marking, Formula property, GTS gts) {
        throw new UnsupportedOperationException(
            "In CTL, the Globally-operator should be bounded by a path quantifier.");
    }

    /**
     * Mark each state of the given state-space according the exists next
     * operator.
     * 
     * @param marking the current marking.
     * @param property the CTL-expression in question.
     * @param gts the state space as a graph transition system.
     */
    public void markExistsNext(Marking marking, Formula property, GTS gts) {
        Formula next = property.getArg1();
        assert (next.getToken() == Token.NEXT) : "This method can only be called if the Exists-operator has an Next-formula as its operand.";
        Formula operand = next.getArg1();
        operand.mark(this, marking, gts);
        for (GraphState nextState : gts.nodeSet()) {
            boolean stateSet = false;
            // if there exists at least one satisfying successor-state
            // this state satisfies the given property
            for (GraphTransition outTransition : nextState.getTransitionSet()) {
                if (outTransition.getRole() == EdgeRole.BINARY) {
                    GraphState successorState = outTransition.target();
                    if (marking.satisfies(successorState, operand)) {
                        marking.set(nextState, property, true);
                        stateSet = true;
                        break;
                    }
                } else {
                    // if the rule does not change the graph, we must not
                    // interpret
                    // the target state of this transition as a successor state
                }
            }
            // if their exists no satisfying successor-state
            // this state does also not satisfy
            if (!stateSet) {
                marking.set(nextState, property, false);
                property.getCounterExamples().add(nextState);
            }
        }
    }

    /**
     * Mark each state of the given state-space according the exists
     * until-operator.
     * 
     * @param marking the current marking.
     * @param property the CTL-expression in question.
     * @param gts the state space as a graph transition system.
     */
    public void markExistsUntil(Marking marking, Formula property, GTS gts) {
        Formula until = property.getArg1();
        assert (until.getToken() == Token.UNTIL) : "This method can only be called if the Exists-operator has an Until-formula as its operand.";
        Formula arg1 = until.getArg1();
        Formula arg2 = until.getArg2();
        Set<GraphState> seenBefore = new HashSet<GraphState>();
        List<GraphState> todo = new ArrayList<GraphState>();

        // perform the marking for the operands
        arg1.mark(this, marking, gts);
        arg2.mark(this, marking, gts);

        Iterator<? extends GraphState> stateIter = gts.nodeSet().iterator();
        while (stateIter.hasNext()) {
            GraphState nextState = stateIter.next();
            if (marking.satisfies(nextState, arg2)) {
                todo.add(nextState);
            }
        }

        // as long as there are states that are not yet processed
        while (!todo.isEmpty()) {
            GraphState currentState = todo.get(0);
            todo.remove(currentState);
            marking.set(currentState, property, true);
            // get all predecessors of the current state
            Collection<GraphState> predecessors =
                this.modelChecker.getPredecessorMap().get(currentState);
            Iterator<GraphState> predecessorIter = predecessors.iterator();
            while (predecessorIter.hasNext()) {
                GraphState predecessorState = predecessorIter.next();
                if (!seenBefore.contains(predecessorState)) {
                    seenBefore.add(predecessorState);
                    if (marking.satisfies(predecessorState, arg1)) {
                        todo.add(predecessorState);
                    }
                }
            }
        }

        Iterator<? extends GraphState> counterExampleIter =
            gts.nodeSet().iterator();
        while (counterExampleIter.hasNext()) {
            GraphState nextState = counterExampleIter.next();
            if (!marking.satisfies(nextState, property)) {
                property.getCounterExamples().add(nextState);
            }
        }
    }

    /**
     * Mark each state of the given state-space according the all
     * until-operator.
     * 
     * @param marking the current marking.
     * @param property the CTL-expression in question.
     * @param gts the state space as a graph transition system.
     */
    public void markAllUntil(Marking marking, Formula property, GTS gts) {
        Formula until = property.getArg1();
        assert (until.getToken() == Token.UNTIL) : "This method can only be called if the All-operator has an Until-formula as its operand.";
        Formula firstOperand = until.getArg1();
        Formula secondOperand = until.getArg2();
        List<GraphState> todo = new ArrayList<GraphState>();

        firstOperand.mark(this, marking, gts);
        secondOperand.mark(this, marking, gts);

        Map<GraphState,Integer> nb = new HashMap<GraphState,Integer>();
        Iterator<? extends GraphState> stateIter = gts.nodeSet().iterator();
        while (stateIter.hasNext()) {
            GraphState nextState = stateIter.next();
            nb.put(nextState, Integer.valueOf(gts.outEdgeSet(nextState).size()));
            if (marking.satisfies(nextState, secondOperand)) {
                todo.add(nextState);
            }
        }

        while (!todo.isEmpty()) {
            GraphState currentState = todo.get(0);
            todo.remove(currentState);
            marking.set(currentState, property, true);

            Collection<GraphState> predecessors =
                this.modelChecker.getPredecessorMap().get(currentState);
            Iterator<GraphState> predecessorIter = predecessors.iterator();
            while (predecessorIter.hasNext()) {
                GraphState predecessor = predecessorIter.next();
                int newNb = (nb.get(predecessor)).intValue() - 1;
                nb.put(predecessor, Integer.valueOf(newNb));
                if (newNb == 0 && marking.satisfies(predecessor, firstOperand)
                    && !marking.satisfies(predecessor, property)) {
                    todo.add(predecessor);
                }
            }
        }

        Iterator<? extends GraphState> counterExampleIter =
            gts.nodeSet().iterator();
        while (counterExampleIter.hasNext()) {
            GraphState nextState = counterExampleIter.next();
            if (!marking.satisfies(nextState, property)) {
                property.getCounterExamples().add(nextState);
            }
        }
    }

    /**
     * Delegates the marking process to the given CTL-expression.
     * @param marking the marking of states so far
     * @param property the CTL-expression to which the marking is delegated
     * @param gts the graph transition system providing the states and
     *        transitions
     */
    public void mark(Marking marking, Formula property, GTS gts) {
        switch (property.getToken()) {
        case TRUE:
            markTrue(marking, property, gts);
            break;
        case FALSE:
            markFalse(marking, property, gts);
            break;
        case ATOM:
            markAtom(marking, property, gts);
            break;
        case NOT:
            markNeg(marking, property, gts);
            break;
        case AND:
            markAnd(marking, property, gts);
            break;
        case OR:
            markOr(marking, property, gts);
            break;
        case FORALL:
            markForall(marking, property, gts);
            break;
        case EXISTS:
            markExists(marking, property, gts);
        }
    }

    public void mark(Marking marking, Formula property, GTS gts,
            CTLModelChecker modelChecker) {
        this.modelChecker = modelChecker;
        mark(marking, property, gts);
    }

    /**
     * Checks whether the given CTL-expression is one of the two special atomic
     * propositions, namely 'final state' or 'open state' and performs the
     * actual marking.
     * @param marking the marking of states so far
     * @param property the CTL-expression to mark states with
     * @param gts the graph transition system providing the states and
     *        transitions
     * @return <tt>true</tt> if the CTL-expression is a special atomic
     *         proposition, <tt>false</tt> otherwise
     */
    protected boolean markSpecialAtom(Marking marking, Formula property, GTS gts) {
        boolean specialAtom = false;
        String name = property.getProp();
        if (name.startsWith(CTLModelChecker.SPECIAL_STATE_PREFIX)) {
            specialAtom = true;
            Iterator<? extends GraphState> stateIter = gts.nodeSet().iterator();
            while (stateIter.hasNext()) {
                GraphState nextState = stateIter.next();
                // we distinguish two types of special states:
                // - open states (i.e. unexplored states)
                if (name.substring(1).endsWith(GTS.OPEN_LABEL_TEXT)) {
                    if (!nextState.isClosed()) {
                        marking.set(nextState, property, true);
                    }
                }
                // - final states (i.e. states with no outgoing transitions)
                else if (name.substring(1).endsWith(GTS.FINAL_LABEL_TEXT)) {
                    if (!gts.isFinal(nextState)) {
                        marking.set(nextState, property, true);
                    }
                }
            }
        } else {
            specialAtom = false;
        }

        return specialAtom;
    }

    /**
     * 
     */
    protected CTLModelChecker modelChecker;
}