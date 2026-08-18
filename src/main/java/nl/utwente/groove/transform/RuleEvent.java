// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2023 University of Twente

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
package nl.utwente.groove.transform;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.grammar.Rule;
import nl.utwente.groove.grammar.host.AnchorValue;
import nl.utwente.groove.grammar.host.HostGraph;
import nl.utwente.groove.grammar.rule.RuleToHostMap;
import nl.utwente.groove.match.Proof;

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
    @Override
    public Rule getAction();

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
     * Returns the array of anchor images.
     * This always refers to the anchor of the top level existential event.
     */
    public AnchorValue[] getAnchorImages();

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
     * Creates an event on the basis of a given proof.
     * This is only allowed if the proved condition has an associated rule.
     * An optional event factory can be used for event reuse
     * @param proof the proof to create the event for
     * @param record factory for fresh nodes; may be <code>null</code>, in which case
     * events are not reused among transitions
     */
    public static RuleEvent createEvent(Proof proof, @Nullable Record record) {
        var rule = proof.getRule();
        assert rule != null;
        Collection<BasicEvent> eventSet = new ArrayList<>();
        collectEvents(proof, eventSet, record);
        assert !eventSet.isEmpty();
        if (!rule.hasSubRules()) {
            assert eventSet.size() == 1;
            return eventSet.iterator().next();
        } else {
            return createCompositeEvent(proof, record, eventSet);
        }
    }

    /**
     * Recursively collects the events of a given proof and all its sub-proofs
     * into a given collection.
     * Events of locally non-modifying rules are skipped except for the top-level rule.
     * @param proof the proof to collect the events of
     * @param events the resulting set of events
     * @param record factory for events; may be <code>null</code>, in which case
     * events are not reused among transitions
     */
    private static void collectEvents(Proof proof, Collection<BasicEvent> events,
                                      @Nullable Record record) {
        var rule = proof.getRule();
        if (rule != null && (rule.isTop() || rule.isLocallyModifying())) {
            BasicEvent myEvent = createSimpleEvent(proof, record);
            events.add(myEvent);
        }
        for (Proof subProof : proof.getSubProofs()) {
            collectEvents(subProof, events, record);
        }
    }

    /**
     * Callback factory method to create a simple event. Delegates to
     * {@link Record#createSimpleEvent(Rule, RuleToHostMap)} if
     * <code>record</code> is not <code>null</code>.
     */
    private static BasicEvent createSimpleEvent(Proof proof, @Nullable Record record) {
        assert proof.hasRule();
        if (record == null) {
            return new BasicEvent(proof.getRule(), proof.getPatternMap(), Reuse.NONE);
        } else {
            return record.createSimpleEvent(proof.getRule(), proof.getPatternMap());
        }
    }

    /**
     * Callback factory method to create a composite event. Delegates to
     * {@link Record#createSimpleEvent(Rule, RuleToHostMap)} if
     * <code>nodeFactory</code> is not <code>null</code>.
     */
    private static RuleEvent createCompositeEvent(Proof proof, @Nullable Record record,
                                                  Collection<BasicEvent> eventSet) {
        if (record == null) {
            return new CompositeEvent(record, proof.getRule(), eventSet, Reuse.NONE);
        } else {
            return record.createCompositeEvent(proof.getRule(), eventSet);
        }
    }

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