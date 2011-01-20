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
 * $Id: CTLFormulaMarker.java,v 1.1.1.2 2007-03-20 10:43:00 kastenberg Exp $
 */
package groove.verify;

import groove.lts.GTS;

/**
 * Visitor interface for CTL-formula.
 * @author Harmen Kastenberg
 * @version $Revision$ $Date: 2007-03-20 10:43:00 $
 */
public interface CTLFormulaMarker {

    /**
     * Starts the marking process.
     */
    public void mark(Marking marking, TemporalFormula expr, GTS gts,
            CTLModelChecker modelChecker);

    /**
     * Starts the marking-process.
     * 
     * @param marking the current marking.
     * @param expr the CTL-expression in question.
     * @param gts the state space as a graph transition system.
     */
    public void mark(Marking marking, TemporalFormula expr, GTS gts);

    /**
     * Mark each state of the given state-space with the given CTL-expression.
     * 
     * @param marking the current marking.
     * @param expr the CTL-expression in question.
     * @param gts the state space as a graph transition system.
     */
    public void markTrue(Marking marking, TemporalFormula expr, GTS gts);

    /**
     * Unmark the states of the given state-space with the given CTL-expression.
     * 
     * @param marking the current marking.
     * @param expr the CTL-expression in question.
     * @param gts the state space as a graph transition system.
     */
    public void markFalse(Marking marking, TemporalFormula expr, GTS gts);

    /**
     * Mark each state of the given state-space if that state does satisfies the
     * predicate represented by this CTL-expression.
     * 
     * @param marking the current marking.
     * @param expr the CTL-expression in question.
     * @param gts the state space as a graph transition system.
     */
    public void markAtom(Marking marking, TemporalFormula expr, GTS gts);

    /**
     * Mark each state of the given state-space according the negation-operator.
     * 
     * @param marking the current marking.
     * @param expr the CTL-expression in question.
     * @param gts the state space as a graph transition system.
     */
    public void markNeg(Marking marking, TemporalFormula expr, GTS gts);

    /**
     * Mark each state of the given state-space according the or-operator.
     * 
     * @param marking the current marking.
     * @param expr the CTL-expression in question.
     * @param gts the state space as a graph transition system.
     */
    public void markOr(Marking marking, TemporalFormula expr, GTS gts);

    /**
     * Mark each state of the given state-space according the and-operator.
     * 
     * @param marking the current marking.
     * @param expr the CTL-expression in question.
     * @param gts the state space as a graph transition system.
     */
    public void markAnd(Marking marking, TemporalFormula expr, GTS gts);

    /**
     * Mark each state of the given state-space according the exists-operator.
     * 
     * @param marking the current marking.
     * @param expr the CTL-expression in question.
     * @param gts the state space as a graph transition system.
     */
    public void markExists(Marking marking, TemporalFormula expr, GTS gts);

    /**
     * Mark each state of the given state-space according the all-operator.
     * 
     * @param marking the current marking.
     * @param expr the CTL-expression in question.
     * @param gts the state space as a graph transition system.
     */
    public void markAll(Marking marking, TemporalFormula expr, GTS gts);

    /**
     * Mark each state of the given state-space according the next-operator.
     * 
     * @param marking the current marking.
     * @param expr the CTL-expression in question.
     * @param gts the state space as a graph transition system.
     */
    public void markNext(Marking marking, TemporalFormula expr, GTS gts);

    /**
     * Mark each state of the given state-space according the until-operator.
     * 
     * @param marking the current marking.
     * @param expr the CTL-expression in question.
     * @param gts the state space as a graph transition system.
     */
    public void markUntil(Marking marking, TemporalFormula expr, GTS gts);

    /**
     * Mark each state of the given state-space according the finally-operator.
     * 
     * @param marking the current marking.
     * @param expr the CTL-expression in question.
     * @param gts the state space as a graph transition system.
     */
    public void markFinally(Marking marking, TemporalFormula expr, GTS gts);

    /**
     * Mark each state of the given state-space according the globally-operator.
     * 
     * @param marking the current marking.
     * @param expr the CTL-expression in question.
     * @param gts the state space as a graph transition system.
     */
    public void markGlobally(Marking marking, TemporalFormula expr, GTS gts);
}
