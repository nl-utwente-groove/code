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
import groove.control.CtrlState;

/** Top class for states of a PGTS. */
public class PatternGraphState extends AbstractPatternState {

    /** The pattern graph associated with this state. */
    private final PatternGraph graph;
    /** The transition system this state belongs to. */
    private final PGTS pgts;

    /** Default constructor. */
    public PatternGraphState(PatternGraph graph, CtrlState ctrlState,
            int number, PGTS pgts) {
        super(number);
        this.graph = graph;
        this.graph.setFixed();
        this.pgts = pgts;
        setCtrlState(ctrlState);
    }

    @Override
    public PGTS getPGTS() {
        return this.pgts;
    }

    @Override
    public PatternGraph getGraph() {
        return this.graph;
    }

}
