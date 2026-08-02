/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * $Id$
 */
package nl.utwente.groove.explore;

import nl.utwente.groove.explore.config.parse.LegacySyntaxParser;
import nl.utwente.groove.explore.strategy.ExploreStateStrategy;
import nl.utwente.groove.explore.strategy.Strategy;
import nl.utwente.groove.grammar.Grammar;

/**
 * Exploration type for single-state exploration: the start state is fully
 * explored, and no further states are.
 * @author Arend Rensink
 * @version $Revision$
 */
public class StateExploreType extends DirectExploreType {
    /**
     * Constructs a single-state exploration type.
     * @param acceptor the acceptor specification
     * @param count number of results after which exploration halts;
     * {@code 0} means unbounded
     */
    public StateExploreType(LegacySyntaxParser.AcceptorSpec acceptor, int count) {
        super(acceptor, count);
    }

    @Override
    protected String getStrategyIdentifier() {
        return "state";
    }

    @Override
    public Strategy getParsedStrategy(Grammar grammar) {
        return new ExploreStateStrategy();
    }

    @Override
    public ExploreType withResultCount(int count) {
        return new StateExploreType(getAcceptorSpec(), count);
    }
}
