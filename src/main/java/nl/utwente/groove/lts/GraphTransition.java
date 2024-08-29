// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2023 University of Twente

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
package nl.utwente.groove.lts;

import java.util.ArrayList;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.Optional;

import org.eclipse.jdt.annotation.NonNullByDefault;

import nl.utwente.groove.control.template.Switch;
import nl.utwente.groove.grammar.Action;
import nl.utwente.groove.grammar.host.HostGraphMorphism;
import nl.utwente.groove.grammar.host.HostNode;
import nl.utwente.groove.grammar.host.ValueNode;
import nl.utwente.groove.graph.GEdge;
import nl.utwente.groove.transform.Event;
import nl.utwente.groove.util.Fragile;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Models a transition in a GTS.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
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

    /**
     * Returns the concrete arguments of this transition.
     */
    public HostNode[] getArguments();

    /** Returns the GTS in which this transition occurs. */
    public default GTS getGTS() {
        return source().getGTS();
    }

    /** Indicates if this transition is part of an atomic block. */
    public boolean isPartialStep();

    /**
     * Indicates if this transition is a step in a recipe transition.
     * If this is the case, then either the step is partial or it represents
     * an atomic recipe execution.
     * @see #isPartialStep()
     */
    public boolean isInternalStep();

    /**'
     * Indicates if this transition is an exposed part of the GTS.
     * This is the case if it is not an internal recipe step, and its source and
     * target states are exposed.
     * @see #isInternalStep()
     * @see GraphState#isExposed()
     */
    public boolean isExposedStep();

    /** Returns the corresponding switch from the control template.
     * For rule transitions, this is the inner switch of the control step;
     * for recipe transitions, it is the recipe call switch.
     * */
    public Switch getSwitch();

    /**
     * Returns the initial rule transition of this graph transition.
     * For rule transitions, this is
     * the object itself; for recipe transitions, it is the initial internal
     * rule transition from the source state.
     */
    public RuleTransition getInitial();

    /**
     * Returns an iterator over the steps comprising this transition.
     * The steps are returned in arbitrary order.
     */
    public Iterable<RuleTransition> getSteps();

    /**
     * Returns a (wrapped) string to be sent to the standard output
     * on adding a transition with this event to a GTS.
     * The string is obtained from the action format string (see {@link Action#getFormatString()}
     * @return an optional output string, which may contain an error if the rule parameters do not
     * match the format expected by the action's format string.
     */
    default public Optional<Fragile<String>> getOutputString() {
        var result = getAction().getFormatString().map(s -> {
            List<Object> args = new ArrayList<>();
            for (HostNode arg : label().getArguments()) {
                if (arg instanceof ValueNode vn) {
                    args.add(vn.getValue());
                } else {
                    args.add(arg.toString());
                }
            }
            try {
                return Fragile.of(String.format(s, args.toArray()));
            } catch (IllegalFormatException e) {
                return Fragile
                    .<String>error(new FormatException("Error in rule output string: %s",
                        e.getMessage()));
            }
        });
        return result;
    }

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
         * Only exposed transitions.
         * @see GraphTransition#isExposedStep()
         */
        EXPOSED {
            @Override
            public boolean admits(GraphTransition trans) {
                return trans.isExposedStep();
            }
        },
        /**
         * All transitions between non-absent states, including internal transitions.
         * @see GraphState#isAbsent()
         */
        PRESENT {
            @Override
            public boolean admits(GraphTransition trans) {
                return !trans.source().isAbsent() && !trans.target().isAbsent();
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
                return includeAbsent
                    ? ANY
                    : PRESENT;
            } else {
                return includeAbsent
                    ? COMPLETE
                    : EXPOSED;
            }
        }
    }
}