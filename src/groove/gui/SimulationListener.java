/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * $Id: SimulationListener.java,v 1.8 2008-01-30 09:33:35 iovka Exp $
 */
package groove.gui;

import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.trans.NameLabel;
import groove.trans.RuleMatch;
import groove.view.DefaultGrammarView;

/**
 * Observer (= viewer) interface for production rule simulation.
 * @see Simulator
 */
public interface SimulationListener {
    /**
     * Reports the update of the graph grammar. If the new grammar is
     * <code>null</code>, the entire simulator is reset Note that the content
     * of the grammar view object may change even though its identity does not;
     * thus, comparing the parameter value with a previous one is misleading.
     * @param grammar the current graph grammar view; may be <code>null</code>
     */
    void setGrammarUpdate(DefaultGrammarView grammar);

    /**
     * Reports the start of the simulation by setting a graph transition system.
     * The GTS is not <code>null</code>; deactivation is signalled through
     * {@link #setGrammarUpdate(DefaultGrammarView)}. This update is always
     * preceded by a {@link #setGrammarUpdate(DefaultGrammarView)} with the
     * relevant grammar view; that is, the GTS' grammar is derived from the view
     * as last passed through a grammar update. The start state should also be
     * set as part of this action; no separate call of
     * {@link #setStateUpdate(GraphState)} is guanteed for the start state.
     * @param gts the active graph transition system; non-<code>null</code>
     */
    void startSimulationUpdate(GTS gts);

    /**
     * Reports the update of the currently selected state. The new state, and
     * all its outgoing transitions, are required to be in the current grammar's
     * LTS. The currently selected transition (if any) is implicitly reset to
     * null, but the currently selected rule is unaffected.
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
     * Reports the change of the currently selected transition. The new
     * transition is required to be in the current grammar's LTS.
     * @param transition the new selected transition; non-<code>null</code>
     */
    void setTransitionUpdate(GraphTransition transition);

    /**
     * Reports the change of the currently selected match. The new match is
     * located at the current state
     * @param match the new selected match; non-<code>null</code>
     */
    void setMatchUpdate(RuleMatch match);

    /**
     * Reports the application of a given transition. The target state is
     * required to be in the current GTS. The currently selected transition (if
     * any) is implicitly reset to null, but the currently selected rule is
     * unaffected. The effect should be much the same as for
     * <tt>setSateUpdate(edge.target())</tt>.
     * @param transition the applied transition
     * @see #setStateUpdate(GraphState)
     */
    void applyTransitionUpdate(GraphTransition transition);
}
