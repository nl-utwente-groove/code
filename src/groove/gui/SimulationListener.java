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
 * $Id: SimulationListener.java,v 1.3 2007-04-29 09:22:28 rensink Exp $
 */
package groove.gui;

import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.trans.NameLabel;
import groove.view.RuleViewGrammar;

/**
 * Observer (= viewer) interface for production rule simulation.
 * @see Simulator
 */
public interface SimulationListener {
    /**
     * Reports the update of the graph grammar.
     * The grammar is not yet active.
     * If the new grammar is <code>null</code>, the entire simulator is reset
     * @param grammar the new graph grammar; may be <code>null</code>
     */
    void setGrammarUpdate(RuleViewGrammar grammar);

    /**
     * Reports the activation of the simulation by setting a graph transition system.
     * The GTS is not <code>null</code>; deactivation is signalled
     * through {@link #setGrammarUpdate(RuleViewGrammar)}.
     * The GTS' grammar may differ from the previously set grammar.
     * The start state should also be set as part of this action; no separate call of
     * {@link #setStateUpdate(GraphState)} is guanteed for the start state.
     * @param gts the active graph transition system; non-<code>null</code>
     */
    void activateGrammarUpdate(GTS gts);

    /**
     * Reports the update of the currently selected state.
     * The new state, and all its outgoing transitions, are required to be in
     * the current grammar's LTS.
     * The currently selected transition (if any) is implicitly reset to null,
     * but the currently selected rule is unaffected.
     * @param state the new current state; non-<code>null</code>
     */
    void setStateUpdate(GraphState state);

    /**
     * Reports the update of the currently selected derivation rule, by name.
     * The name refers to one of the rules in the current grammar's rule system.
     * The currently selected transition (if any) is implicitly reset to null.
     * @param name the name of the new selected derivation rule
     * @require name != null
     */
    void setRuleUpdate(NameLabel name);

    /**
     * Reports the change of the currently selected transition.
     * The new transition is required to be in the current grammar's LTS.
     * @param transition the new selected transition; non-<code>null</code>
     */
    void setTransitionUpdate(GraphTransition transition);

    /**
     * Reports the application of a given transition.
     * The target state is required to be in the current GTS.
     * The currently selected transition (if any) is implicitly reset to null,
     * but the currently selected rule is unaffected.
     * The effect should be much the same as for <tt>setSateUpdate(edge.target())</tt>.
     * @param transition the applied transition
     * @see #setStateUpdate(GraphState)
     */
    void applyTransitionUpdate(GraphTransition transition);
}

