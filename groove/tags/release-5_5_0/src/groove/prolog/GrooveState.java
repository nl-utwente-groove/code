/*
 * Groove Prolog Interface
 * Copyright (C) 2009 Michiel Hendriks, University of Twente
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package groove.prolog;

import groove.grammar.Grammar;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.transform.RuleEvent;

/**
 * The current state in groove.
 * @author Lesley Wevers
 */
public class GrooveState {
    /**
     * The GrammarView
     */
    private Grammar graphGrammar;

    /**
     * The currently selected GraphState
     */
    private GraphState state;

    /**
     * The GTS
     */
    private GTS gts;

    /**
     * The currently selected RuleEvent
     */
    private RuleEvent event;

    /**
     * Constructs a groove state from a graph, a GTS and the current state
     * @param graphGrammar      A grammar view
     * @param gts               A GTS
     * @param state             The currently selected state in the GTS
     * @param event   The currently selected rule event in the GTS
     */
    public GrooveState(Grammar graphGrammar, GTS gts, GraphState state,
            RuleEvent event) {
        if (graphGrammar == null) {
            throw new NullPointerException();
        }

        this.graphGrammar = graphGrammar;
        this.gts = gts;
        this.state = state;
        this.event = event;
    }

    /**
     * Gets the graph grammar
     * @return The graph grammar
     */
    public Grammar getGraphGrammar() {
        return this.graphGrammar;
    }

    /**
     * Gets the GTS
     * @return The GTS, or null if there is no GTS
     */
    public GTS getGts() {
        return this.gts;
    }

    /**
     * Gets the currently selected state in the GTS
     * @return The currently selected state in the GTS, or null if there is no selected state
     */
    public GraphState getState() {
        return this.state;
    }

    /**
     * Gets he rule event of the currently selected transition in the GTS
     * @return  The rule event of the currently selected transition in the GTS, or null if there is no selected transition
     */
    public RuleEvent getActiveEvent() {
        return this.event;
    }
}
