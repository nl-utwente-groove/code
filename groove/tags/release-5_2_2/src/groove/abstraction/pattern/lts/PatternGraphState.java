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
import groove.abstraction.pattern.shape.PatternShape;
import groove.control.instance.Frame;

import java.util.ArrayList;
import java.util.List;

/** Top class for states of a PGTS. */
public class PatternGraphState extends AbstractPatternState {

    /** The pattern graph associated with this state. */
    private PatternGraph graph;
    /** The transition system this state belongs to. */
    private final PGTS pgts;
    /** A (possible null) reference to a state that subsumes this one. */
    private PatternState subsumptor;
    /**
     * Temporary list of possible subsumed states used when adding the state to
     * the GTS.
     */
    private List<PatternState> subsumedStates;

    /** Default constructor. */
    public PatternGraphState(PatternGraph graph, Frame frame, int number, PGTS pgts) {
        super(number);
        this.graph = graph;
        this.graph.setName(toString());
        this.graph.setFixed();
        this.pgts = pgts;
        setFrame(frame);
    }

    @Override
    public PGTS getPGTS() {
        return this.pgts;
    }

    @Override
    public PatternGraph getGraph() {
        return this.graph;
    }

    // Abstraction methods.

    @Override
    public boolean hasPatternShape() {
        return this.graph instanceof PatternShape;
    }

    @Override
    public PatternShape getShape() {
        return (PatternShape) this.graph;
    }

    @Override
    public boolean isSubsumed() {
        return this.subsumptor != null;
    }

    @Override
    public void addSubsumedState(PatternState subsumed) {
        getSubsumedStates().add(subsumed);
    }

    @Override
    public boolean setSubsumptor(PatternState subsumptor) {
        if (this.subsumptor != null) {
            return false;
        } else {
            this.subsumptor = subsumptor;
            return true;
        }
    }

    @Override
    public int markSubsumedStates() {
        int markCount = 0;
        for (PatternState subsumed : getSubsumedStates()) {
            if (subsumed.setSubsumptor(this)) {
                markCount++;
            }
        }
        this.subsumedStates = null;
        return markCount;
    }

    private List<PatternState> getSubsumedStates() {
        if (this.subsumedStates == null) {
            this.subsumedStates = new ArrayList<PatternState>();
        }
        return this.subsumedStates;
    }

}
