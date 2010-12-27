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
/*
 * $Id: LTS.java,v 1.6 2008-01-30 09:32:20 iovka Exp $
 */
package groove.lts;

import java.util.Collection;

/**
 * Interface of a labelled transition system, as a graph where the nodes are
 * <tt>State</tt>s and the edges are <tt>Transition</tt>s. The LTS is
 * assumed to have a fixed associated rule production system. Extends
 * graph.Graph with a start (i.e., initial) initial state.
 * @version $Revision$ $Date: 2008-01-30 09:32:20 $
 */
public interface LTS extends groove.graph.Graph<GraphState,GraphTransition> {
    /** The text of the self-edge label that indicates a start state. */
    public static final String START_LABEL_TEXT = "start";
    /** The text of the self-edge label that indicates an open state. */
    public static final String OPEN_LABEL_TEXT = "open";
    /** The text of the self-edge label that indicates a final state. */
    public static final String FINAL_LABEL_TEXT = "final";

    /**
     * Returns the start state of this LTS.
     * @return the start state of this LTS
     * @ensure result != null
     */
    public GraphState startState();

    /**
     * Indicates whether we have found a final state during exploration.
     * Convenience method for <tt>! getFinalStates().isEmpty()</tt>.
     */
    public boolean hasFinalStates();

    /**
     * Returns the set of final states explored so far.
     */
    Collection<? extends GraphState> getFinalStates();

    /**
     * Indicates whether a given state is final. Equivalent to
     * <tt>getFinalStates().contains(state)</tt>.
     */
    boolean isFinal(GraphState state);

    /**
     * Indicates whether a given state is open, in the sense of not (completely)
     * explored. Equivalent to <tt>!state.isClosed()</tt>.
     */
    boolean isOpen(GraphState state);

    /** Adds a listener to this LTS. */
    public void addLTSListener(LTSListener listener);

    /** Removes a listener from this LTS. */
    public void removeLTSListener(LTSListener listener);
}