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
import nl.utwente.groove.explore.result.Acceptor;
import nl.utwente.groove.grammar.Grammar;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Base class for the exploration types of the legacy strategies that the
 * exploration feature model deliberately does not cover (single-state,
 * remote and minimax exploration). The strategy and acceptor are
 * instantiated directly from the state of the type.
 * @author Arend Rensink
 * @version $Revision$
 */
public abstract class DirectExploreType extends ExploreType {
    /**
     * Constructs a direct exploration type.
     * @param acceptor the acceptor specification
     * @param count number of results after which exploration halts;
     * {@code 0} means unbounded
     */
    protected DirectExploreType(LegacySyntaxParser.AcceptorSpec acceptor, int count) {
        super(count);
        this.acceptor = acceptor;
    }

    /** Returns the acceptor specification of this exploration type. */
    protected LegacySyntaxParser.AcceptorSpec getAcceptorSpec() {
        return this.acceptor;
    }

    private final LegacySyntaxParser.AcceptorSpec acceptor;

    @Override
    public Acceptor getParsedAcceptor(Grammar grammar) throws FormatException {
        return getAcceptorSpec().instantiate(grammar);
    }

    /** Returns the legacy descriptor of the strategy of this type,
     * used in the identifier. */
    abstract protected String getStrategyIdentifier();

    @Override
    public String getIdentifier() {
        return getStrategyIdentifier() + " / " + getAcceptorSpec().getIdentifier() + " / "
            + (getBound() == 0
                ? "infinite"
                : getBound());
    }
}
