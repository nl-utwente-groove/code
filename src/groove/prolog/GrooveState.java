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

import groove.graph.Graph;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.trans.RuleEvent;

import java.util.Collections;
import java.util.Set;

/**
 * The current state in groove.
 * 
 * @author Michiel Hendriks
 */
public class GrooveState {
    // TODO: Lesley, Should be specialized
    /**
     * TODO
     */
    protected Graph<?,?> graph;

    /**
     * TODO
     */
    protected GraphState state;

    /**
     * TODO
     */
    protected GTS gts;

    /**
     * TODO
     */
    protected Set<RuleEvent> ruleEvents;

    /**
     * TODO
     */
    public GrooveState(Graph<?,?> forGraph) {
        this.graph = forGraph;
    }

    /**
     * TODO
     */
    public GrooveState(GraphState forState) {
        this(forState.getGraph());
        this.state = forState;
    }

    /**
     * TODO
     */
    public GrooveState(GTS forGTS) {
        this.graph = this.gts = forGTS;
    }

    /**
     * TODO
     */
    public GrooveState(GTS forGTS, GraphState currentState) {
        this(forGTS);
        this.state = currentState;
    }

    /**
     * TODO
     */
    public GrooveState(GTS forGTS, GraphState currentState,
            Set<RuleEvent> matches) {
        this(forGTS);
        this.state = currentState;
        this.ruleEvents = matches;
    }

    /**
     * @return the graph
     */
    public Graph<?,?> getGraph() {
        return this.graph;
    }

    /**
     * @return the state
     */
    public GraphState getState() {
        return this.state;
    }

    /**
     * @return the gts
     */
    public GTS getGts() {
        return this.gts;
    }

    /**
     * Rule events, used by the RuleEvent filtering exploration system
     * 
     * TODO
     */
    public Set<RuleEvent> getRuleEvents() {
        if (this.ruleEvents == null) {
            return Collections.emptySet();
        } else {
            return Collections.unmodifiableSet(this.ruleEvents);
        }
    }
}
