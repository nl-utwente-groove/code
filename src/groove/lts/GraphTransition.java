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

import groove.graph.Edge;
import groove.trans.Action;
import groove.trans.HostGraphMorphism;

/**
 * Models a transition in a GTS.
 * @author Arend Rensink
 * @version $Revision$
 */
public interface GraphTransition extends Edge {
    /** Overrides the method to specialise the result type. */
    GraphState source();

    /** Overrides the method to specialise the result type. */
    GraphState target();

    /** Overrides the method to specialise the result type. */
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

    /** Indicates if this transition is part of a recipe transition. */
    public boolean isPartial();

    /** 
     * Returns an iterator over the steps comprising this transition.
     */
    public Iterable<RuleTransition> getSteps();

    /**
     * Returns the (partial) morphism from the source to the target graph.
     */
    public HostGraphMorphism getMorphism();
}