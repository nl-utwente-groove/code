/*
 * GROOVE: GRaphs for Object Oriented VErification
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
 * $Id: CTLMatchingMarker.java,v 1.1.1.2 2007-03-20 10:43:00 kastenberg Exp $
 */
package groove.verify;

import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.trans.GraphCondition;
import groove.util.Reporter;
import groove.verify.CTLStarFormula.Neg;
import groove.verify.CTLStarFormula.Next;
import groove.verify.CTLStarFormula.Or;
import groove.verify.CTLStarFormula.Until;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Visitor-implementation of {@link groove.verify.CTLFormulaMarker} using the matching-strategy on
 * the Atom-level.
 * @author Harmen Kastenberg
 * @version $Revision: 1.1.1.2 $ $Date: 2007-03-20 10:43:00 $
 */
public class CTLMatchingMarker implements CTLFormulaMarker {

    /**
     * Constructor.
     */
    public CTLMatchingMarker() {
    	// empty constructor
    }

    /**
     * @param verifier
     */
    public CTLMatchingMarker(CTLModelChecker verifier) {
        try {
            verifier.getProperty().linkPredicates(verifier);
        } catch (GraphPredicateNotFoundException gpnfe) {
            gpnfe.printStackTrace();
        }
    }
    /* (non-Javadoc)
     * @see groove.verify.CTLExprMarker#markTrue(groove.verify.Marking, groove.verify.CTLExpr, groove.lts.GTS)
     */
    public void markTrue(Marking marking, TemporalFormula property, GTS gts) {
        reporter.start(MARK_TRUE);
        Iterator<? extends GraphState> stateIter = gts.nodeSet().iterator();
        while(stateIter.hasNext()) {
            GraphState nextState = stateIter.next();
            marking.set(nextState, property, true);
        }
        reporter.stop();
    }

    /* (non-Javadoc)
     * @see groove.verify.CTLExprMarker#markFalse(groove.verify.Marking, groove.verify.CTLExpr, groove.lts.GTS)
     */
    public void markFalse(Marking marking, TemporalFormula property, GTS gts) {
        reporter.start(MARK_FALSE);
        Iterator<? extends GraphState> stateIter = gts.nodeSet().iterator();
        while(stateIter.hasNext()) {
            GraphState nextState = stateIter.next();
            marking.set(nextState, property, false);
            property.getCounterExamples().add(nextState);
        }
        reporter.stop();
    }

    /* (non-Javadoc)
     * @see groove.verify.CTLExprMarker#markAtom(groove.verify.Marking, groove.verify.CTLExpr, groove.lts.GTS)
     */
    public void markAtom(Marking marking, TemporalFormula property, GTS gts) {
        reporter.start(MARK_ATOM);
        boolean specialAtom = markSpecialAtom(marking, property, gts);
        if (!specialAtom) {
	        GraphCondition condition = ((CTLStarFormula.Atom) property).graphCondition();
	        Iterator<? extends GraphState> stateIter = gts.nodeSet().iterator();
	        while(stateIter.hasNext()) {
	            GraphState nextState = stateIter.next();
	            if (condition.hasMatching(nextState.getGraph())) {
	                marking.set(nextState, property, true);
	            }
	            else {
	                marking.set(nextState, property, false);
	                property.getCounterExamples().add(nextState);
	            }
	        }
        }
        reporter.stop();
    }

    /* (non-Javadoc)
     * @see groove.verify.CTLExprMarker#markNeg(groove.verify.Marking, groove.verify.CTLExpr, groove.lts.GTS)
     */
    public void markNeg(Marking marking, TemporalFormula property, GTS gts) {
        reporter.start(MARK_NEG);
        TemporalFormula operand = (TemporalFormula) property.getOperands().get(0);
        // first mark all states for its operand
        operand.mark(this, marking, gts);

        Iterator<? extends GraphState> stateIter = gts.nodeSet().iterator();
        while(stateIter.hasNext()) {
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
        reporter.stop();
    }

    /* (non-Javadoc)
     * @see groove.verify.CTLExprMarker#markOr(groove.verify.Marking, groove.verify.CTLExpr, groove.lts.GTS)
     */
    public void markOr(Marking marking, TemporalFormula property, GTS gts) {
        reporter.start(MARK_OR);
        List<TemporalFormula> operands = property.getOperands();

        // perform the marking for each operand
        for (int i = 0; i < operands.size(); i++) {
            TemporalFormula nextOperand = operands.get(i);
            nextOperand.mark(this, marking, gts);
        }

        Iterator<? extends GraphState> stateIter = gts.nodeSet().iterator();
        while (stateIter.hasNext()) {
            GraphState nextState = stateIter.next();
            boolean satisfies = false;
            for (int i = 0; (satisfies == false) && (i < operands.size()); i++) {
                TemporalFormula nextOperand = (TemporalFormula) operands.get(i);
                satisfies |= marking.satisfies(nextState, nextOperand);
            }

            // if the state satisfies one of the predicates
            if (satisfies) {
                marking.set(nextState, property, true);
            }
            else {
                marking.set(nextState, property, false);
                property.getCounterExamples().add(nextState);
            }
        }
        reporter.stop();
    }

    /**
     * Mark each state of the given state-space according the and-operator.
     * 
     * @param marking the current marking.
     * @param property the CTL-expression in question.
     * @param gts the state space as a graph transition system.
     */
    public void markAnd(Marking marking, TemporalFormula property, GTS gts) {
    	// (phi & ... & psi) <==> !(!phi | ... | !psi)
        List<TemporalFormula> operands = property.getOperands();
        List<TemporalFormula> negOperands = new ArrayList<TemporalFormula>();
        for (TemporalFormula nextOperand : operands) {
			negOperands.add(new Neg(nextOperand));
		}

    	TemporalFormula newExpr = new Neg(new Or(negOperands));
    	newExpr.mark(this, marking, gts);
    }

    /* (non-Javadoc)
     * @see groove.verify.CTLExprMarker#markExists(groove.verify.Marking, groove.verify.CTLExpr, groove.lts.GTS)
     */
    public void markExists(Marking marking, TemporalFormula property, GTS gts) {
    	List<TemporalFormula> operandList = property.getOperands();
    	TemporalFormula firstOperand = operandList.get(0);
    	if (firstOperand instanceof CTLStarFormula.Next) {
            markExistsNext(marking, property, gts);
    	} else if (firstOperand instanceof CTLStarFormula.Until) {
    		markExistsUntil(marking, property, gts);
    	} else if (firstOperand instanceof CTLStarFormula.Finally) {
    		markExistsFinally(marking, property, gts);
    	} else if (firstOperand instanceof CTLStarFormula.Globally) {
    		markExistsGlobally(marking, property, gts);
    	}
    }

    /* (non-Javadoc)
     * @see groove.verify.CTLExprMarker#markAll(groove.verify.Marking, groove.verify.CTLExpr, groove.lts.GTS)
     */
    public void markAll(Marking marking, TemporalFormula property, GTS gts) {
    	List<TemporalFormula> operandList = property.getOperands();
    	TemporalFormula firstOperand = operandList.get(0);
    	if (firstOperand instanceof CTLStarFormula.Next) {
            markAllNext(marking, property, gts);
    	} else if (firstOperand instanceof CTLStarFormula.Until) {
    		markAllUntil(marking, property, gts);
    	} else if (firstOperand instanceof CTLStarFormula.Finally) {
    		markAllFinally(marking, property, gts);
    	} else if (firstOperand instanceof CTLStarFormula.Globally) {
    		markAllGlobally(marking, property, gts);
    	}
    }

    /* (non-Javadoc)
     * @see groove.verify.CTLFormulaMarker#markNext(groove.verify.Marking, groove.verify.TemporalFormula, groove.lts.GTS)
     */
    public void markNext(Marking marking, TemporalFormula property, GTS gts) {
    	throw new UnsupportedOperationException("In CTL, the Next-operator should be bounded by a path quantifier.");
    }

    /* (non-Javadoc)
     * @see groove.verify.CTLFormulaMarker#markUntil(groove.verify.Marking, groove.verify.TemporalFormula, groove.lts.GTS)
     */
    public void markUntil(Marking marking, TemporalFormula property, GTS gts) {
    	throw new UnsupportedOperationException("In CTL, the Until-operator should be bounded by a path quantifier.");
    }

    /* (non-Javadoc)
     * @see groove.verify.CTLFormulaMarker#markFinally(groove.verify.Marking, groove.verify.TemporalFormula, groove.lts.GTS)
     */
    public void markFinally(Marking marking, TemporalFormula property, GTS gts) {
    	throw new UnsupportedOperationException("In CTL, the Finally-operator should be bounded by a path quantifier.");
    }

    /* (non-Javadoc)
     * @see groove.verify.CTLFormulaMarker#markGlobally(groove.verify.Marking, groove.verify.TemporalFormula, groove.lts.GTS)
     */
    public void markGlobally(Marking marking, TemporalFormula property, GTS gts) {
    	throw new UnsupportedOperationException("In CTL, the Globally-operator should be bounded by a path quantifier.");
    }

    /**
     * Mark each state of the given state-space according the exists next operator.
     * 
     * @param marking the current marking.
     * @param property the CTL-expression in question.
     * @param gts the state space as a graph transition system.
     */
    public void markExistsNext(Marking marking, TemporalFormula property, GTS gts) {
        reporter.start(MARK_EXISTS_NEXT);
        TemporalFormula next = property.getOperands().get(0);
        assert (next instanceof Next) : "This method can only be called if the Exists-operator has an Next-formula as its operand." ;
        TemporalFormula operand = next.getOperands().get(0);
        operand.mark(this, marking, gts);
        for (GraphState nextState: gts.nodeSet()) {
            boolean stateSet = false;
            // if there exists at least one satisfying successor-state
            // this state satisfies the given property
            for (GraphTransition outTransition: nextState.getTransitionSet()) {
            	if (outTransition.getRule().isModifying()) {
            		GraphState successorState = outTransition.target();
            		if (marking.satisfies(successorState, operand)) {
            			marking.set(nextState, property, true);
            			stateSet = true;
            			break;
            		}
            	} else {
            		// if the rule does not change the graph, we must not interpret
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
        reporter.stop();
    }

    /**
     * Mark each state of the given state-space according the exists until-operator.
     * 
     * @param marking the current marking.
     * @param property the CTL-expression in question.
     * @param gts the state space as a graph transition system.
     */
    public void markExistsUntil(Marking marking, TemporalFormula property, GTS gts) {
        reporter.start(MARK_EXISTS_UNTIL);
        TemporalFormula until = property.getOperands().get(0);
        assert (until instanceof Until) : "This method can only be called if the Exists-operator has an Until-formula as its operand." ;
        TemporalFormula firstOperand = until.getOperands().get(0);
        TemporalFormula secondOperand = until.getOperands().get(1);
        Set<GraphState> seenBefore = new HashSet<GraphState>();
        List<GraphState> todo = new ArrayList<GraphState>();

        // perform the marking for the operands
        firstOperand.mark(this, marking, gts);
        secondOperand.mark(this, marking, gts);

        Iterator<? extends GraphState> stateIter = gts.nodeSet().iterator();
        while (stateIter.hasNext()) {
            GraphState nextState = stateIter.next();
            if (marking.satisfies(nextState, secondOperand)) {
                todo.add(nextState);
            }
        }

        // as long as there are states that are not yet processed
        while (!todo.isEmpty()) {
            GraphState currentState = todo.get(0);
            todo.remove(currentState);
            marking.set(currentState, property, true);
            // get all predecessors of the current state
            Collection<GraphState> predecessors = modelChecker.getPredecessorMap().get(currentState);
            Iterator<GraphState> predecessorIter = predecessors.iterator();
            while (predecessorIter.hasNext()) {
                GraphState predecessorState = predecessorIter.next();
                if (!seenBefore.contains(predecessorState)) {
                    seenBefore.add(predecessorState);
                    if (marking.satisfies(predecessorState, firstOperand)) {
                        todo.add(predecessorState);
                    }
                }
            }
        }

        Iterator<? extends GraphState> counterExampleIter = gts.nodeSet().iterator();
        while(counterExampleIter.hasNext()) {
            GraphState nextState = counterExampleIter.next();
            if (!marking.satisfies(nextState, property))
                property.getCounterExamples().add(nextState);
        }

        reporter.stop();
    }

    /**
     * Mark each state of the given state-space according the exists finally-operator.
     * 
     * @param marking the current marking.
     * @param property the CTL-expression in question.
     * @param gts the state space as a graph transition system.
     */
    public void markExistsFinally(Marking marking, TemporalFormula property, GTS gts) {
    	// EF(phi) <==> E(true U phi)
    	throw new UnsupportedOperationException("This method should never be called. The EF(phi) construction should have been rewritten to a E(true U phi) construction.");
//    	List<TemporalFormula> operandList = new ArrayList<TemporalFormula>();
//    	operandList.add(new True());
//    	operandList.add(property.getOperands().get(0).getOperands().get(0));
//    	TemporalFormula newExpr = new Exists(new Until(operandList));
//    	newExpr.mark(this, marking, gts);
    }

    /**
     * Mark each state of the given state-space according the exists globally-operator.
     * 
     * @param marking the current marking.
     * @param property the CTL-expression in question.
     * @param gts the state space as a graph transition system.
     */
    public void markExistsGlobally(Marking marking, TemporalFormula property, GTS gts) {
    	// EG(phi) <==> !(AF(!phi))
    	throw new UnsupportedOperationException("This method should never be called. The EG(phi) construction should have been rewritten to a !(AF(!phi)) construction.");
//    	TemporalFormula operand = property.getOperands().get(0).getOperands().get(0);
//    	TemporalFormula newExpr = new Neg(new All(new Finally(new Neg(operand))));
//    	newExpr.mark(this, marking, gts);
    }

    /**
     * Mark each state of the given state-space according the all next-operator.
     * 
     * @param marking the current marking.
     * @param property the CTL-expression in question.
     * @param gts the state space as a graph transition system.
     */
    public void markAllNext(Marking marking, TemporalFormula property, GTS gts) {
        // AX(phi) <==> !EX(!phi)
    	throw new UnsupportedOperationException("This method should never be called. The AX(phi) construction should have been rewritten to a !(EX(!phi)) construction.");
//    	TemporalFormula operand = property.getOperands().get(0).getOperands().get(0);
//    	TemporalFormula newExpr = new Neg(new Exists(new Next(new Neg(operand))));
//    	newExpr.mark(this, marking, gts);
    }

    /**
     * Mark each state of the given state-space according the all until-operator.
     * 
     * @param marking the current marking.
     * @param property the CTL-expression in question.
     * @param gts the state space as a graph transition system.
     */
    public void markAllUntil(Marking marking, TemporalFormula property, GTS gts) {
        reporter.start(MARK_ALL_UNTIL);
        TemporalFormula until = property.getOperands().get(0);
        assert (until instanceof Until) : "This method can only be called if the All-operator has an Until-formula as its operand." ;
        TemporalFormula firstOperand = until.getOperands().get(0);
        TemporalFormula secondOperand = until.getOperands().get(1);
        List<GraphState> todo = new ArrayList<GraphState>();

        firstOperand.mark(this, marking, gts);
        secondOperand.mark(this, marking, gts);

        Map<GraphState, Integer> nb = new HashMap<GraphState, Integer>();
        Iterator<? extends GraphState> stateIter = gts.nodeSet().iterator();
        while (stateIter.hasNext()) {
            GraphState nextState = stateIter.next();
            nb.put(nextState, new Integer(gts.outEdgeSet(nextState).size()));
            if (marking.satisfies(nextState, secondOperand)) {
                todo.add(nextState);
            }
        }

        while (!todo.isEmpty()) {
            GraphState currentState = (GraphState) todo.get(0);
            todo.remove(currentState);
            marking.set(currentState, property, true);

            Collection<GraphState> predecessors = modelChecker.getPredecessorMap().get(currentState);
            Iterator<GraphState> predecessorIter = predecessors.iterator();
            while (predecessorIter.hasNext()) {
                GraphState predecessor = predecessorIter.next();
                int newNb = ((Integer) nb.get(predecessor)).intValue() - 1;
                nb.put(predecessor, new Integer(newNb));
                if (newNb == 0 &&
                        marking.satisfies(predecessor, firstOperand) &&
                        !marking.satisfies(predecessor, property)) {
                    todo.add(predecessor);
                }
            }
        }

        Iterator<? extends GraphState> counterExampleIter = gts.nodeSet().iterator();
        while(counterExampleIter.hasNext()) {
            GraphState nextState = counterExampleIter.next();
            if (!marking.satisfies(nextState, property))
                property.getCounterExamples().add(nextState);
        }

        reporter.stop();
    }

    /**
     * Mark each state of the given state-space according the all finally-operator.
     * 
     * @param marking the current marking.
     * @param property the CTL-expression in question.
     * @param gts the state space as a graph transition system.
     */
    public void markAllFinally(Marking marking, TemporalFormula property, GTS gts) {
    	// AF(phi) <==> A(true U phi)
    	throw new UnsupportedOperationException("This method should never be called. The AF(phi) construction should have been rewritten to a A(true U phi) construction.");
//    	List<TemporalFormula> operandList = new ArrayList<TemporalFormula>();
//    	operandList.add(new True());
//    	operandList.add(property.getOperands().get(0).getOperands().get(0));
//    	TemporalFormula newExpr = new All(new Until(operandList));
//    	newExpr.mark(this, marking, gts);
    }

    /**
     * Mark each state of the given state-space according the all globally-operator.
     * 
     * @param marking the current marking.
     * @param property the CTL-expression in question.
     * @param gts the state space as a graph transition system.
     */
    public void markAllGlobally(Marking marking, TemporalFormula property, GTS gts) {
    	throw new UnsupportedOperationException("This method should never be called. The AG(phi) construction should have been rewritten to a !(EF(!phi)) construction.");
//    	// AG(phi) <==> !(EF(!phi))
//    	TemporalFormula operand = property.getOperands().get(0).getOperands().get(0);
//    	TemporalFormula newExpr = new Neg(new Exists(new Finally(new Neg(operand))));
//    	newExpr.mark(this, marking, gts);
    }

    /**
     * Delegates the marking process to the given CTL-expression.
     * @param marking the marking of states so far
     * @param property the CTL-expression to which the marking is delegated
     * @param gts the graph transition system providing the states and transitions
     */
    public void mark(Marking marking, TemporalFormula property, GTS gts) {
        reporter.start(MARK);
        try {
        	property.mark(this, marking, gts);
        } catch(UnsupportedOperationException uoe) {
        	System.err.println(uoe.getMessage());
        }
        reporter.stop();
    }

    /* (non-Javadoc)
     * @see groove.verify.TemporalFormulaMarker#mark(groove.verify.Marking, groove.verify.CTLExpr, groove.lts.GTS, groove.verify.CTLModelChecker)
     */
    public void mark(Marking marking, TemporalFormula property, GTS gts, CTLModelChecker modelChecker) {
        this.modelChecker = modelChecker;
        mark(marking, property, gts);
    }

    /**
     * Checks whether the given CTL-expression is one of the two special
     * atomic propositions, namely 'final state' or 'open state' and performs
     * the actual marking.
     * @param marking the marking of states so far
     * @param property the CTL-expression to mark states with
     * @param gts the graph transition system providing the states and transitions
     * @return <tt>true</tt> if the CTL-expression is a special atomic proposition,
     * <tt>false</tt> otherwise
     */
    protected boolean markSpecialAtom(Marking marking, TemporalFormula property, GTS gts) {
    	boolean specialAtom = false;
        String name = ((CTLStarFormula.Atom) property).predicateName();
        if (name.startsWith(CTLModelChecker.SPECIAL_STATE_PREFIX)) {
        	specialAtom = true;
        	Iterator<? extends GraphState> stateIter = gts.nodeSet().iterator();
        	while (stateIter.hasNext()) {
        		GraphState nextState = stateIter.next();
            	// we distinguish two types of special states:
            	// - open states (i.e. unexplored states)
            	if(name.substring(1).endsWith(GTS.OPEN_LABEL_TEXT)) {
            		if (!nextState.isClosed()) {
    	                marking.set(nextState, property, true);
            		}
            	}
                // - final states (i.e. states with no outgoing transitions)
            	else if (name.substring(1).endsWith(GTS.FINAL_LABEL_TEXT)) {
            		if (! nextState.getOutTransitionIter().hasNext())
    	                marking.set(nextState, property, true);
            	}
        	}
        }
        else {
        	specialAtom = false;
        }
    	
    	return specialAtom;
    }

    /**
     * 
     */
    protected CTLModelChecker modelChecker;
    /**
     * Reporter.
     */
    static public final Reporter reporter = Reporter.register(CTLMatchingMarker.class);
    /**
     * Registration id for mark-method.
     */
    static public final int MARK = reporter.newMethod("mark(Marking, TemporalFormula, GTS) - matching");
    /**
     * Registration id for markTrue-method.
     */
    static public final int MARK_TRUE = reporter.newMethod("markTrue(Marking, TemporalFormula, GTS) - matching");
    /**
     * Registration id for markFalse-method.
     */
    static public final int MARK_FALSE = reporter.newMethod("markFalse(Marking, TemporalFormula, GTS) - matching");
    /**
     * Registration id for markAtom-method.
     */
    static public final int MARK_ATOM = reporter.newMethod("markAtom(Marking, TemporalFormula, GTS) - matching");
    /**
     * Registration id for markNeg-method.
     */
    static public final int MARK_NEG = reporter.newMethod("markNeg(Marking, TemporalFormula, GTS) - matching");
    /**
     * Registration id for markOr-method.
     */
    static public final int MARK_OR = reporter.newMethod("markOr(Marking, TemporalFormula, GTS)");
    /**
     * Registration id for markExistsNext-method.
     */
    static public final int MARK_EXISTS_NEXT = reporter.newMethod("markExistsNext(Marking, TemporalFormula, GTS) - matching");
    /**
     * Registration id for markExistsUntil-method.
     */
    static public final int MARK_EXISTS_UNTIL = reporter.newMethod("markExistsUntil(Marking, TemporalFormula, GTS) - matching");
    /**
     * Registration id for markAllUntil-method.
     */
    static public final int MARK_ALL_UNTIL = reporter.newMethod("markAllUntil(Marking, TemporalFormula, GTS) - matching");
}