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

import groove.graph.AbstractBinaryEdge;
import groove.graph.Label;
import groove.graph.Morphism;
import groove.graph.Node;
import groove.lts.GraphTransitionStub;
import groove.trans.RuleEvent;
import groove.trans.RuleMatch;

/**
 * @author Iovka Boneva
 * @version $Revision $
 */
public class AbstrGraphTransitionImpl extends
        AbstractBinaryEdge<AbstrGraphState,Label,AbstrGraphState> implements
        AbstrGraphTransition {

    public RuleMatch getMatch() {
        // This is needed for displaying matches
        return getEvent().getMatch(source().getGraph());
    }

    /**
     * Not implemented This does not make sense for abstract transformation.
     */
    public Morphism getMorphism() {
        throw new UnsupportedOperationException();
    }

    public boolean isSymmetry() {
        throw new UnsupportedOperationException();
    }

    public GraphTransitionStub toStub() {
        throw new UnsupportedOperationException();
    }

    // ------------------------------------------------------------
    // FIELDS, CONSTRUCTORS, STANDARD METHODS
    // ------------------------------------------------------------
    private final RuleEvent event;

    /**
     * @param source
     * @param event
     * @param target
     */
    public AbstrGraphTransitionImpl(AbstrGraphState source, RuleEvent event,
            AbstrGraphState target) {
        super(source, event.getLabel(), target);
        this.event = event;
    }

    public final RuleEvent getEvent() {
        return this.event;
    }

    public boolean isEquivalent(AbstrGraphTransition other) {
        return source() == other.source() && getEvent() == other.getEvent();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AbstrGraphTransitionImpl)) {
            return false;
        }
        AbstrGraphTransitionImpl t = (AbstrGraphTransitionImpl) o;
        boolean result =
            source().equals(t.source()) && target().equals(t.target())
                && getEvent().equals(t.getEvent());
        assert (!result || t.hashCode() == hashCode()) : "The equals method does not comply with the hash code method !!!";
        return result;
    }

    // The hashCode() method is implemented as final and only depends on the
    // hash code of the end points

    /**
     * No implementation.
     */
    public Node[] getAddedNodes() {
        throw new UnsupportedOperationException();
    }
}
