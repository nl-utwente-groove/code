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
package groove.abs.lts;

import groove.graph.Morphism;
import groove.graph.Node;
import groove.lts.DefaultGraphTransition;
import groove.lts.GraphTransitionStub;
import groove.trans.RuleEvent;

/**
 * @author Eduardo Zambon
 * @version $Revision $
 */
@Deprecated
public class ShapeGraphTransition extends DefaultGraphTransition {

    // ------------------------------------------------------------------------
    // NOT IMPLEMENTED. This does not make sense for abstract transformation.
    // ------------------------------------------------------------------------

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

    // ------------------------------------------------------------
    // FIELDS, CONSTRUCTORS, STANDARD METHODS
    // ------------------------------------------------------------

    /**
     * Constructor used for shape graphs.
     * @param source the source state
     * @param event the rule event of the transition
     * @param target the target state
     */
    public ShapeGraphTransition(ShapeGraphState source, RuleEvent event,
            ShapeGraphState target) {
        super(source, event.getLabel(), target);
        this.event = event;
    }

    @Override
    public final RuleEvent getEvent() {
        return this.event;
    }

    /**
     * @param other the other transition to be compared.
     * @return true if both transitions are equivalent.
     */
    public boolean isEquivalent(ShapeGraphTransition other) {
        return source() == other.source() && getEvent() == other.getEvent();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ShapeGraphTransition)) {
            return false;
        }
        ShapeGraphTransition t = (ShapeGraphTransition) o;
        boolean result =
            source().equals(t.source()) && target().equals(t.target())
                && getEvent().equals(t.getEvent());
        assert (!result || t.hashCode() == hashCode()) : "The equals method does not comply with the hash code method !!!";
        return result;
    }

}
