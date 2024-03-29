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
package nl.utwente.groove.explore.strategy;

import nl.utwente.groove.explore.result.Acceptor;
import nl.utwente.groove.lts.DefaultGraphNextState;
import nl.utwente.groove.lts.GTS;
import nl.utwente.groove.lts.GraphState;
import nl.utwente.groove.match.MatcherFactory;
import nl.utwente.groove.match.SearchEngine;
import nl.utwente.groove.match.rete.ReteSearchEngine;
import nl.utwente.groove.transform.DeltaStore;

/**
 * Explores a single path until reaching a final state or a loop. In case of
 * abstract simulation, this implementation will prefer going along a path then
 * stopping exploration when a loop is met.
 * @author Amir Hossein Ghamarian
 *
 */
public class ReteLinearStrategy extends LinearStrategy {
    @Override
    protected void prepare(GTS gts, GraphState state, Acceptor acceptor) {
        super.prepare(gts, state, acceptor);
        // initialise the RETE network
        this.rete = new ReteSearchEngine(getGTS().getGrammar());
        this.oldEngine = MatcherFactory.instance(gts.hasSimpleGraphs())
            .getEngine();
        MatcherFactory.instance(gts.hasSimpleGraphs())
            .setEngine(this.rete);
    }

    @Override
    protected GraphState computeNextState() {
        GraphState result = super.computeNextState();
        DeltaStore d = new DeltaStore();
        if (result != null) {
            ((DefaultGraphNextState) result).getDelta()
                .applyDelta(d);
            this.rete.transitionOccurred(result.getGraph(), d);
        }
        return result;
    }

    /**
     * Does some clean-up for when the full exploration is finished.
     */
    @Override
    public void finish() {
        MatcherFactory.instance(getGTS().hasSimpleGraphs())
            .setEngine(this.oldEngine);
        super.finish();
    }

    private SearchEngine oldEngine;
    private ReteSearchEngine rete;
}
