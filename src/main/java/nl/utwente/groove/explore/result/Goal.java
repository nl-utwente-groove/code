/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.explore.result;

import java.util.function.Predicate;

import org.eclipse.jdt.annotation.NonNullByDefault;

import nl.utwente.groove.lts.GraphState;
import nl.utwente.groove.lts.GraphTransition;
import nl.utwente.groove.lts.Status.Flag;
import nl.utwente.groove.util.AIGenerated;

/**
 * Run-time realisation of the goal feature of an exploration: decides which
 * states an {@link Acceptor} collects into the result. A goal is offered the
 * events of the exploration — states as they are added, states as their
 * status changes, transitions as they are added — and tests whether the
 * event realises the goal. All tests are {@code false} by default; a
 * concrete goal overrides the tests through which it can be realised.
 * @see Acceptor
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
@AIGenerated("Claude Fable 5, 2026-08")
public abstract class Goal {
    /** Tests whether a state just added to the GTS realises this goal. */
    public boolean testAdded(GraphState state) {
        return false;
    }

    /** Tests whether a state whose status just changed realises this goal.
     * @param state the state whose status changed
     * @param change the vector of status changes (see {@link Flag#test(int)})
     */
    public boolean testStatus(GraphState state, int change) {
        return false;
    }

    /** Tests whether a transition just added to the GTS realises this goal.
     * If so, the source state of the transition is collected.
     */
    public boolean testAdded(GraphTransition trans) {
        return false;
    }

    /** Returns the empty goal, which is never realised. */
    public static Goal none() {
        return NONE;
    }

    private static final Goal NONE = new Goal() {
        // no tests overridden: the goal is never realised
    };

    /** Returns the goal realised by every public state, at the moment the
     * state is added or becomes complete.
     * @see GraphState#isPublic()
     */
    public static Goal anyState() {
        return ANY_STATE;
    }

    private static final Goal ANY_STATE = new Goal() {
        @Override
        public boolean testAdded(GraphState state) {
            return state.isPublic();
        }

        @Override
        public boolean testStatus(GraphState state, int change) {
            return Flag.FULL.test(change) && state.isPublic();
        }
    };

    /** Returns the goal realised by every final state, at the moment the
     * state is added or becomes final.
     */
    public static Goal finalState() {
        return FINAL_STATE;
    }

    private static final Goal FINAL_STATE = new Goal() {
        @Override
        public boolean testAdded(GraphState state) {
            return state.isFinal();
        }

        @Override
        public boolean testStatus(GraphState state, int change) {
            return Flag.FINAL.test(change);
        }
    };

    /** Returns the goal realised by every non-inner state that satisfies a
     * given test at the moment the state is added.
     */
    public static Goal state(Predicate<GraphState> test) {
        return new Goal() {
            @Override
            public boolean testAdded(GraphState state) {
                return !state.isInner() && test.test(state);
            }
        };
    }

    /** Returns the goal realised by the source state of every non-inner
     * transition that satisfies a given test at the moment the transition
     * is added.
     */
    public static Goal transition(Predicate<GraphTransition> test) {
        return new Goal() {
            @Override
            public boolean testAdded(GraphTransition trans) {
                return !trans.isInnerStep() && test.test(trans);
            }
        };
    }
}
