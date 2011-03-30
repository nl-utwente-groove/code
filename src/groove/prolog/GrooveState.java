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

import groove.lts.GTS;
import groove.lts.GraphState;
import groove.trans.RuleEvent;
import groove.view.GrammarView;

import java.util.Collections;
import java.util.Set;

/**
 * The current state in groove.
 * 
 * @author Michiel Hendriks
 */
public class GrooveState {
    /**
     * TODO
     */
    private GrammarView grammarView;

    /**
     * TODO
     */
    private GraphState state;

    /**
     * TODO
     */
    private GTS gts;

    /**
     * TODO
     */
    private Set<RuleEvent> ruleEvents;

    /**
     * Constructs a groove state from a graph, a GTS and the current state
     * @param grammarView       A grammar view
     * @param gts               A GTS
     * @param state             The current state in the GTS
     */
    public GrooveState(GrammarView grammarView, GTS gts, GraphState state) {
        if (grammarView == null) {
            throw new NullPointerException();
        }

        this.grammarView = grammarView;
        this.gts = gts;
        this.state = state;
    }

    /**
     * @return The grammar view
     */
    public GrammarView getGrammarView() {
        return this.grammarView;
    }

    /**
     * @return The GTS
     */
    public GTS getGts() {
        return this.gts;
    }

    /**
     * @return The currently selected state in the GTS
     */
    public GraphState getState() {
        return this.state;
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
