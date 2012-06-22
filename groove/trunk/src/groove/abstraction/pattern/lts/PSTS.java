/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
 * $Id$
 */
package groove.abstraction.pattern.lts;

import groove.abstraction.pattern.explore.util.PatternRuleEventApplier;
import groove.abstraction.pattern.explore.util.PatternShapeMatchApplier;
import groove.abstraction.pattern.explore.util.PatternShapeMatchSetCollector;
import groove.abstraction.pattern.shape.PatternGraph;
import groove.abstraction.pattern.shape.PatternShape;
import groove.abstraction.pattern.shape.iso.PatternShapeIsoChecker;
import groove.abstraction.pattern.trans.PatternGraphGrammar;

/**
 * Pattern Shape Transition System.  
 * @author Eduardo Zambon
 */
public final class PSTS extends PGTS {

    /** Number of states marked as subsumed. */
    private int subsumedStatesCount;
    /** Number of transitions marked as subsumed. */
    private int subsumedTransitionsCount;

    /** Constructs a PGTS for the given grammar. */
    public PSTS(PatternGraphGrammar grammar) {
        super(grammar);
        this.subsumedStatesCount = 0;
        this.subsumedTransitionsCount = 0;
    }

    /** Callback factory method for the match applier. */
    @Override
    public PatternRuleEventApplier createMatchApplier() {
        return new PatternShapeMatchApplier(this);
    }

    /** Returns a fresh match collector for the given state. */
    @Override
    public PatternShapeMatchSetCollector createMatchCollector(PatternState state) {
        return new PatternShapeMatchSetCollector(state);
    }

    /** 
     * Returns a copy of the given graph with a fresh element factory.
     * The resulting graph will be used as start graph state.
     */
    @Override
    protected PatternGraph createStartGraph(PatternGraph startGraph) {
        PatternGraph result = new PatternShape(startGraph);
        return result;
    }

    /** Callback factory method for a state set. */
    @Override
    protected StateSet createStateSet() {
        return new ShapeStateSet();
    }

    /**
     * Adds the given state to the abstract GTS. 
     * The given state must be of type ShapeState or ShapeNextState.
     * While trying to add the new state, subsumption is computed in both
     * directions. If the state is fresh, this method goes over the subsumed
     * states already stored and tries to update the subsumption relation.
     */
    @Override
    public PatternState addState(PatternState newState) {
        assert newState.hasPatternShape();
        PatternState result = super.addState(newState);
        if (result == null) {
            // There is no state in the transition system that subsumes the
            // new state. Maybe the new state subsumes some states that are
            // already in the GTS.
            this.subsumedStatesCount += newState.markSubsumedStates();
        } else if (newState.isSubsumed()) {
            // The state will produce only a transition.
            this.subsumedTransitionsCount++;
        }
        return result;
    }

    /** Returns the number of states marked as subsumed. */
    public int getSubsumedStatesCount() {
        return this.subsumedStatesCount;
    }

    /** Returns the number of transitions marked as subsumed. */
    public int getSubsumedTransitionsCount() {
        return this.subsumedTransitionsCount;
    }

    // ------------------------------------------------------------------------
    // Inner classes
    // ------------------------------------------------------------------------

    /** Class to store the states of the PSTS. */
    private static final class ShapeStateSet extends PGTS.StateSet {

        /** Default constructor, delegates to super class. */
        ShapeStateSet() {
            super(PatternShapeIsoChecker.getInstance());
        }

        /**
         * Compares the given states both for (in)equality and subsumption.
         * Bear in mind that this method has side-effects. 
         */
        @Override
        protected boolean areEqual(PatternState myState, PatternState otherState) {
            assert myState.hasPatternShape() && otherState.hasPatternShape();

            if (myState.getCtrlState() != otherState.getCtrlState()) {
                return false;
            }

            if (otherState.isSubsumed()) {
                // We are not in reachability mode and the other state is
                // subsumed. This means we can leave the comparison to the
                // the subsumptor state.
                return false;
            }

            // Now let's check for iso...
            PatternShapeIsoChecker checker =
                (PatternShapeIsoChecker) this.checker;
            int comparison =
                checker.compareShapes(myState.getShape(), otherState.getShape()).one();
            if (checker.isDomStrictlyLargerThanCod(comparison)) {
                // New state subsumes old one.
                myState.addSubsumedState(otherState);
            } else if (checker.isCodSubsumesDom(comparison)) {
                // Old state subsumes new state.
                myState.setSubsumptor(otherState);
            }

            return checker.areEqual(comparison);
        }

    }

}
