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

import groove.grammar.Action;
import groove.grammar.host.HostGraphMorphism;
import groove.graph.Edge;
import groove.transform.Event;

/**
 * Models a transition in a GTS.
 * @author Arend Rensink
 * @version $Revision$
 */
public interface GraphTransition extends Edge {
    /** Overrides the method to specialise the result type. */
    @Override
    GraphState source();

    /** Overrides the method to specialise the result type. */
    @Override
    GraphState target();

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

    /** Indicates if this transition is part of a recipe transition. */
    public boolean isPartial();

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
    public enum Class {
        /** Combination of {@link Class#RULE} and {@link Class#COMPLETE}. */
        ANY {
            @Override
            public boolean admits(GraphTransition trans) {
                return true;
            }
        },
        /** Only rule transitions, be they partial or complete. */
        RULE {
            @Override
            public boolean admits(GraphTransition trans) {
                return trans instanceof RuleTransition;
            }
        },
        /**
         * Only complete transitions, be they rule- or recipe-triggered. 
         * @see GraphTransition#isPartial()
         */
        COMPLETE {
            @Override
            public boolean admits(GraphTransition trans) {
                return !trans.isPartial();
            }
        };

        /** Indicates if a given graph transition belongs to this class. */
        abstract public boolean admits(GraphTransition trans);
    }
}