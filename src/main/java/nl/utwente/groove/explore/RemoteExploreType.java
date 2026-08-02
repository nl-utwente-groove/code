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
import nl.utwente.groove.explore.encode.Serialized;
import nl.utwente.groove.explore.strategy.RemoteStrategy;
import nl.utwente.groove.explore.strategy.Strategy;
import nl.utwente.groove.grammar.Grammar;

/**
 * Exploration type for remote exploration: the result is sent as an STS to
 * a remote server.
 * @author Arend Rensink
 * @version $Revision$
 */
public class RemoteExploreType extends DirectExploreType {
    /**
     * Constructs a remote exploration type.
     * @param host the host to send the result to
     * @param acceptor the acceptor specification
     * @param count number of results after which exploration halts;
     * {@code 0} means unbounded
     */
    public RemoteExploreType(String host, LegacySyntaxParser.AcceptorSpec acceptor, int count) {
        super(createStrategyDescriptor(host), acceptor, count);
        this.host = host;
    }

    private final String host;

    /** Computes the legacy display descriptor for a given host. */
    private static Serialized createStrategyDescriptor(String host) {
        Serialized result = new Serialized("remote");
        result.setArgument("host", host);
        return result;
    }

    @Override
    public Strategy getParsedStrategy(Grammar grammar) {
        RemoteStrategy result = new RemoteStrategy();
        result.setHost(this.host);
        return result;
    }
}
