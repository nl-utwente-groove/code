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
 * $Id: AbstrGraphTransitionImpl.java,v 1.6 2008-02-29 11:02:17 fladder Exp $
 */
package groove.abstraction.neigh.lts;

import groove.lts.DefaultGraphTransition;
import groove.trans.RuleEvent;

/**
 * Implements a transition in the Shape Transition System.
 * 
 * @author Eduardo Zambon
 */
public final class ShapeTransition extends DefaultGraphTransition {

    private int index;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** Default constructor, delegates to super class. */
    public ShapeTransition(ShapeState source, RuleEvent event, ShapeState target) {
        super(event, source, target);
    }

    /** Sets the index of the transition in the state transition set. */
    public void setIndex(int index) {
        this.index = index;
    }

    /** Gets the index of the transition in the state transition set. */
    public int getIndex() {
        return this.index;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ShapeTransition
            && this.index == ((ShapeTransition) obj).index;
    }

}