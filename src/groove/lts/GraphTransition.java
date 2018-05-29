// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente

// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// http://www.apache.org/licenses/LICENSE-2.0

// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
// either express or implied. See the License for the specific
// language governing permissions and limitations under the License.
/**
 *
 */
package groove.lts;

import groove.control.template.Switch;
import groove.grammar.Action;
import groove.grammar.host.HostGraphMorphism;
import groove.graph.GEdge;
import groove.transform.Event;
import groove.util.parse.FormatException;

/**
 * Models a transition in a GTS.
 * @author Arend Rensink
 * @version $Revision$
 */
public interface GraphTransition extends GEdge<GraphState> {
    /** Overrides the method to specialise the result type. */
    @Override
    ActionLabel label();

    /**
     * Returns the transition label text as shown in the transition
     * system, taking into account whether anchors should be shown.
     * @param anchored if {@code true}, anchors should be shown in
     * the transition label
     * @return the text to be displayed in the transition system
     */
    String text(boolean anchored);

    /** Returns the action for which this is a transition. */
    public Action getAction();

    /** Returns the action instance on which this transition is based. */
    public Event getEvent();

    /** Returns the GTS in which this transition occurs. */
    public GTS getGTS();

    /** Indicates if this transition is part of an atomic block or recipe. */
    public boolean isPartial();

    /**
     * Indicates if this transition is a step in a recipe transition.
     * If this is the case, then either the step is partial or it represents
     * an atomic recipe execution.
     * @see #isPartial()
     */
    public boolean isInternalStep();

    /**'
     * Indicates if this transition is a real part of the GTS.
     * This is the case if it is not an internal recipe step, and its source and
     * target states are real.
     * @see #isInternalStep()
     * @see GraphState#isRealState()
     */
    public boolean isRealStep();

    /** Returns the corresponding switch from the control template.
     * For rule transitions, this is the top switch in the call stack of the step;
     * for recipe transitions, it is the recipe call switch in the main template.
     * */
    public Switch getSwitch();

    /**
     * Returns the initial rule transition of this graph transition.
     * If the graph transition is itself a rule transition, this returns
     * the object itself; otherwise, it returns the initial outgoing
     * rule transition in the recipe transition.
     */
    public RuleTransition getInitial();

    /**
     * Returns an iterator over the steps comprising this transition.
     * The steps are returned in arbitrary order.
     */
    public Iterable<RuleTransition> getSteps();

    /**
     * Returns a string to be sent to the standard output
     * on adding a transition with this event to a GTS.
     * @return a standard output string, or {@code null} if
     * there is no standard output for the rule of this event.
     * @throws FormatException if the format string of the rule
     * does not correspond to the actual rule parameters
     */
    public String getOutputString() throws FormatException;

    /** Extracts the key ingredients from this graph transition. */
    public GraphTransitionKey getKey();

    /**
     * Converts this transition to a more memory-efficient representation, from
     * which the original transition can be retrieved by
     * {@link GraphTransitionStub#toTransition(GraphState)}.
     */
    public GraphTransitionStub toStub();

    /**
     * Returns the (partial) morphism from the source to the target graph.
     */
    public HostGraphMorphism getMorphism();

    /** Classes of graph transitions. */
    public enum Claz {
        /** Combination of {@link Claz#RULE} and {@link Claz#COMPLETE}. */
        ANY {
            @Override
            public boolean admits(GraphTransition trans) {
                return true;
            }
        },
        /** Only rule transitions, be they internal or complete. */
        RULE {
            @Override
            public boolean admits(GraphTransition trans) {
                return trans instanceof RuleTransition;
            }
        },
        /**
         * Only complete (i.e., non-internal) transitions, be they rule- or recipe-triggered.
         * This includes transitions between (non-internal) absent states.
         * @see GraphTransition#isInternalStep()
         */
        COMPLETE {
            @Override
            public boolean admits(GraphTransition trans) {
                return !trans.isInternalStep();
            }
        },
        /**
         * Only real transitions.
         * @see GraphTransition#isRealStep()
         */
        REAL {
            @Override
            public boolean admits(GraphTransition trans) {
                return trans.isRealStep();
            }
        },
        /**
         * All transitions between non-absent states, including internal transitions.
         * @see GraphState#isAbsent()
         */
        PRESENT {
            @Override
            public boolean admits(GraphTransition trans) {
                return !trans.source()
                    .isAbsent()
                    && !trans.target()
                        .isAbsent();
            }
        },;

        /** Indicates if a given graph transition belongs to this class. */
        abstract public boolean admits(GraphTransition trans);

        /** Returns one of four classes of transitions, depending
         * on whether internal and absent transitions are to be included or not.
         * @param includeInternal if {@code true}, include internal transitions
         * @param includeAbsent if {@code true}, include absent transitions
         */
        public static Claz getClass(boolean includeInternal, boolean includeAbsent) {
            if (includeInternal) {
                return includeAbsent ? ANY : PRESENT;
            } else {
                return includeAbsent ? COMPLETE : REAL;
            }
        }
    }
}