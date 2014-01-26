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

import groove.abstraction.MyHashSet;
import groove.abstraction.pattern.shape.PatternGraph;
import groove.control.CtrlFrame;
import groove.control.CtrlState;
import groove.lts.AbstractGraphState;

import java.util.Set;

/**
 * Combination of graph and node functionality, used to store the state of a
 * pattern graph transition system.
 * 
 * See {@link AbstractGraphState}. 
 */
public abstract class AbstractPatternState implements PatternState {

    /** The number of this state */
    private final int nr;
    /** The underlying control state, if any. */
    private CtrlFrame frame;
    /** Flag to indicated if the state has been closed. */
    private boolean closed;
    /** Set of outgoing transitions from this state. */
    private Set<PatternTransition> transitions;

    /**
     * Constructs a an abstract graph state.
     * @param number the number of the state; required to be non-negative
     */
    public AbstractPatternState(int number) {
        assert number >= 0;
        this.nr = number;
        this.transitions = new MyHashSet<PatternTransition>();
    }

    @Override
    public int getNumber() {
        return this.nr;
    }

    /**
     * Returns a name for this state, rather than a full description.
     */
    @Override
    public String toString() {
        return "s" + this.nr;
    }

    @Override
    abstract public PGTS getPGTS();

    @Override
    abstract public PatternGraph getGraph();

    @Override
    public final CtrlFrame getFrame() {
        return this.frame.getPrime();
    }

    @Override
    public final void setFrame(CtrlFrame frame) {
        if (frame instanceof CtrlState) {
            this.frame = ((CtrlState) frame).getSchedule();
        } else {
            this.frame = frame;
        }
    }

    @Override
    public final CtrlFrame getCurrentFrame() {
        return this.frame;
    }

    @Override
    public final boolean setClosed(boolean finished) {
        this.closed = finished;
        if (finished) {
            getPGTS().notifyClosure(this);
        }
        return finished;
    }

    @Override
    public final boolean isClosed() {
        return this.closed;
    }

    @Override
    public boolean addTransition(PatternTransition transition) {
        this.transitions.add(transition);
        return true;
    }

    @Override
    public final Set<PatternTransition> getTransitionSet() {
        return this.transitions;
    }

}
