// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente

// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// http://www.apache.org/licenses/LICENSE-2.0

// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
// either express or implied. See the License for the specific
// language governing permissions and limitations under the License.
/*
 * $Id$
 */
package groove.transform;

import groove.grammar.Rule;
import groove.grammar.host.AnchorValue;
import groove.grammar.host.HostGraph;
import groove.grammar.rule.RuleToHostMap;

/**
 * Interface to encode a rule instantiation that provides images to the rule
 * anchors. Together with the host graph, the event uniquely defines a
 * transformation. The event does not store information specific to the host
 * graph. To apply it to a given host graph, it has to be further instantiated
 * to a rule application.
 * @author Arend Rensink
 * @version $Revision$ $Date: 2008-03-03 21:27:40 $
 */
public interface RuleEvent extends Comparable<RuleEvent>, Event {
    /**
     * Returns the rule for which this is an application.
     */
    public Rule getRule();

    /**
     * Returns a string representation of the anchor image.
     */
    public String getAnchorImageString();

    /**
     * Returns the anchor map of the event.
     * The anchor map maps the rule anchor nodes and edges to
     * host elements.
     * This always refers to the top level existential event.
     */
    public RuleToHostMap getAnchorMap();

    /**
     * Returns the anchor image at a given position.
     * This always refers to the anchor of the top level existential event.
     */
    public AnchorValue getAnchorImage(int i);

    /**
     * Returns a proof of this event's rule condition in a given host graph,
     * based on the anchor map in this event.
     * @param source the host graph in which a proof should be found
     * @return a proof based on this event, of {@code null} if there is
     * no such proof in {@code source}
     */
    public Proof getMatch(HostGraph source);

    /**
     * Records the application of this event, by storing the relevant
     * information into the record object passed in as a parameter.
     * @throws InterruptedException if an oracle input was cancelled
     */
    void recordEffect(RuleEffect record) throws InterruptedException;

    /**
     * Tests if this event conflicts with another, in the sense that if the
     * events occur in either order it is not guaranteed that the result is the
     * same. This is the case if one event creates a simple edge (i.e., not
     * between creator nodes) that the other erases.
     */
    public boolean conflicts(RuleEvent other);

    /**
     * Factory method to create an event from a proof, using this
     * event's system record if there is one.
     */
    public RuleEvent createEvent(Proof proof);

    /** Returns the reuse policy of rule events. */
    public Reuse getReuse();

    /**
     * Event reuse mode.
     * The values are ordered in increasing event reuse.
     */
    enum Reuse {
        /** No event or node reuse. */
        NONE,
        /** Normal event and node reuse. */
        EVENT;
    }
}