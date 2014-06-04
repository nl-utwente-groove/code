/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
package groove.abstraction.pattern.lts;

import groove.abstraction.pattern.shape.PatternGraph;
import groove.graph.EdgeRole;

/**
 * Combines a {@link PatternGraphState} and a {@link PatternGraphTransition}.
 *
 * @author Eduardo Zambon
 */
public class PatternGraphNextState extends PatternGraphState implements PatternNextState {

    // ------------------------------------------------------------------------
    // Object Fields
    // ------------------------------------------------------------------------

    /** First transition that lead to this state. */
    private final PatternGraphTransition transition;

    /** Default constructor. */
    public PatternGraphNextState(PatternGraph graph, PatternGraphState source, int number,
            PGTS pgts, MatchResult match) {
        super(graph, match.getStep().onFinish(), number, pgts);
        this.transition = new PatternGraphTransition(source, match, this);
    }

    @Override
    public PatternState source() {
        return this.transition.source();
    }

    @Override
    public PatternState target() {
        return this;
    }

    @Override
    public boolean isLoop() {
        return source() == target();
    }

    @Override
    public PatternTransitionLabel label() {
        return this.transition.label();
    }

    @Override
    public EdgeRole getRole() {
        return EdgeRole.BINARY;
    }

}
