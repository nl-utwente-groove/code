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
package groove.abstraction.lts;

import groove.graph.Morphism;
import groove.graph.Node;
import groove.lts.DefaultGraphTransition;
import groove.lts.GraphTransitionStub;
import groove.trans.RuleEvent;

/**
 * Implements a transition in the abstract GTS.
 * 
 * @author Eduardo Zambon
 */
public final class ShapeTransition extends DefaultGraphTransition {

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** Default constructor, delegates to super class. */
    public ShapeTransition(ShapeState source, RuleEvent event, ShapeState target) {
        super(event, source, target);
    }

    // ------------------------------------------------------------------------
    // Overridden methods
    // ------------------------------------------------------------------------

    /**
     * Two transitions are equal if they have the same source and target, and
     * the same event.
     */
    @Override
    public boolean equals(Object o) {
        boolean result;
        if (this == o) {
            result = true;
        } else if (!(o instanceof ShapeTransition)) {
            return false;
        }
        ShapeTransition t = (ShapeTransition) o;
        result =
            source().equals(t.source()) && target().equals(t.target())
                && getEvent().equals(t.getEvent());
        // Check for consistency between equals and hashCode.
        assert (!result || this.hashCode() == o.hashCode());
        return result;
    }

    // Unimplemented methods.

    @Override
    public Morphism getMorphism() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isSymmetry() {
        throw new UnsupportedOperationException();
    }

    @Override
    public GraphTransitionStub toStub() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Node[] getAddedNodes() {
        throw new UnsupportedOperationException();
    }

}
