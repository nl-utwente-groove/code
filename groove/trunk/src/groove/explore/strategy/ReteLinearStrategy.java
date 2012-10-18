/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2010
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
 * $Id$
 */
package groove.explore.strategy;

import groove.lts.DefaultGraphNextState;
import groove.lts.GraphState;
import groove.match.MatcherFactory;
import groove.match.SearchEngine;
import groove.match.rete.ReteSearchEngine;
import groove.trans.DeltaStore;

/**
 * Explores a single path until reaching a final state or a loop. In case of
 * abstract simulation, this implementation will prefer going along a path then
 * stopping exploration when a loop is met.
 * @author Amir Hossein Ghamarian
 * 
 */
public class ReteLinearStrategy extends LinearStrategy {
    /**
     * Constructs a default instance of the strategy, in which states are only
     * closed if they have been fully explored
     */
    public ReteLinearStrategy() {
        this(false);
    }

    /**
     * Constructs an instance of the strategy with control over the closing of
     * states.
     * @param closeFast if <code>true</code>, close states immediately after a
     *        single outgoing transition has been computed.
     */
    public ReteLinearStrategy(boolean closeFast) {
        super(closeFast);
    }

    @Override
    protected GraphState getNextState() {
        GraphState result = super.getNextState();
        DeltaStore d = new DeltaStore();
        if (result != null) {
            ((DefaultGraphNextState) result).getDelta().applyDelta(d);
            this.rete.transitionOccurred(result.getGraph(), d);
        }
        return result;
    }

    @Override
    protected void prepare() {
        super.prepare();
        // initialise the RETE network
        this.rete = new ReteSearchEngine(getGTS().getGrammar());
        this.oldEngine = MatcherFactory.instance().getEngine();
        MatcherFactory.instance().setEngine(this.rete);
    }

    /**
     * Does some clean-up for when the full exploration is finished.
     */
    @Override
    protected void finish() {
        MatcherFactory.instance().setEngine(this.oldEngine);
        super.finish();
    }

    private SearchEngine oldEngine;
    private ReteSearchEngine rete;
}
