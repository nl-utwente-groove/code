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
 * $Id: SimulationAdapter.java,v 1.8 2008-01-30 09:33:36 iovka Exp $
 */
package groove.gui;

import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.trans.RuleMatch;
import groove.trans.RuleName;
import groove.view.DefaultGrammarView;

/**
 * An adapter for the simulation listener, offering empty stub methods.
 * @author Arend Rensink
 * @version $Revision$
 */
public class SimulationAdapter implements SimulationListener {
    /** Provides a default empty implementation. */
    public void setGrammarUpdate(DefaultGrammarView grammar) {
        // does nothing by design
    }

    /** Provides a default empty implementation. */
    public void startSimulationUpdate(GTS gts) {
        // does nothing by design
    }

    /** Provides a default empty implementation. */
    public void setStateUpdate(GraphState state) {
        // does nothing by design
    }

    /** Provides a default empty implementation. */
    public void setRuleUpdate(RuleName name) {
        // does nothing by design
    }

    /** Provides a default empty implementation. */
    public void setTransitionUpdate(GraphTransition transition) {
        // does nothing by design
    }

    /** Provides a default empty implementation. */
    public void setMatchUpdate(RuleMatch match) {
        // does nothing by design
    }

    /** Provides a default empty implementation. */
    public void applyTransitionUpdate(GraphTransition transition) {
        // does nothing by design
    }
}
