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
 * $Id$
 */
package groove.transform;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import groove.grammar.Rule;
import groove.grammar.host.AnchorValue;
import groove.grammar.rule.RuleToHostMap;
import groove.match.TreeMatch;
import groove.util.cache.CacheReference;

/**
 * Rule event consisting of a set of events.
 * @author Arend Rensink
 * @version $Revision $
 */
public class CompositeEvent extends AbstractRuleEvent<Rule,CompositeEvent.CompositeEventCache> {
    /**
     * Creates a composite event on the basis of a given (nonempty) constituent event set.
     * @param record the system record from which this event was created; may be
     * {@code null}.
     * @param rule the rule for which this is an event
     * @param eventSet ordered non-empty collection of constituent events. The
     *        order is assumed to be the prefix traversal order of the
     *        dependency tree of the events, meaning the the first element is
     *        the event corresponding to the top level of <code>rule</code>.
     */
    public CompositeEvent(Record record, Rule rule, Collection<BasicEvent> eventSet, Reuse reuse) {
        super(reference, rule);
        assert!eventSet.isEmpty();
        this.record = record;
        this.reuse = reuse;
        this.eventArray = new BasicEvent[eventSet.size()];
        eventSet.toArray(this.eventArray);
    }

    @Override
    public Reuse getReuse() {
        return this.reuse;
    }

    @Override
    public boolean conflicts(RuleEvent other) {
        for (RuleEvent event : this.eventArray) {
            if (event.conflicts(other)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public AnchorValue getAnchorImage(int i) {
        return this.eventArray[0].getAnchorImage(i);
    }

    @Override
    public RuleToHostMap getAnchorMap() {
        return this.eventArray[0].getAnchorMap();
    }

    @Override
    public String getAnchorImageString() {
        List<String> eventLabels = new ArrayList<>();
        for (BasicEvent event : this.eventArray) {
            eventLabels.add(event.getRule()
                .getQualName() + event.getAnchorImageString());
        }
        return Arrays.toString(eventLabels.toArray());
    }

    /** Extracts a proof corresponding to this event from a given match. */
    @Override
    protected Proof extractProof(TreeMatch match) {
        Proof result = null;
        for (Proof proof : match.toProofSet()) {
            RuleEvent proofEvent = createEvent(proof);
            if (hashCode() == proofEvent.hashCode() && equals(proofEvent)) {
                result = proof;
                break;
            }
        }
        return result;
    }

    @Override
    public RuleEvent createEvent(Proof proof) {
        return proof.newEvent(this.record);
    }

    @Override
    public void recordEffect(RuleEffect record) throws InterruptedException {
        BasicEvent[] events = this.eventArray;
        int eventCount = events.length;
        for (int i = 0; i < eventCount; i++) {
            events[i].recordEffect(record);
        }
    }

    @Override
    public int compareTo(RuleEvent other) {
        int result = getRule().compareTo(other.getRule());
        if (result == 0) {
            // the same rule, so the other is also a composite event
            BasicEvent[] myEventArray = this.eventArray;
            BasicEvent[] otherEventArray;
            if (other instanceof CompositeEvent) {
                otherEventArray = ((CompositeEvent) other).eventArray;
            } else {
                otherEventArray = new BasicEvent[] {(BasicEvent) other};
            }
            // more events = larger
            result = myEventArray.length - otherEventArray.length;
            if (result == 0) {
                // compare the individual events lexicographically
                for (int i = 0; result == 0 && i < myEventArray.length; i++) {
                    result = myEventArray[i].compareTo(otherEventArray[i]);
                }
            }
        }
        return result;
    }

    /** Returns the set of constituent events of this set event. */
    public Set<BasicEvent> getEventSet() {
        return getCache().getEventSet();
    }

    @Override
    int computeEventHashCode() {
        int result = 1;
        // since the ordering of the sub-events may differ for equal events,
        // we have to sum up their hashcodes
        for (int i = 0; i < this.eventArray.length; i++) {
            result += this.eventArray[i].hashCode();
        }
        return result;
    }

    /**
     * Two composite events are equal if they contain the same primitive events.
     */
    @Override
    boolean equalsEvent(RuleEvent obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof CompositeEvent)) {
            return false;
        }
        // because the basic events might be differently ordered in the
        // event arrays, we can't do a direct array comparison
        // moreover, if the degree of event reuse differs, we have to
        // collect basic events with the least amount of reuse to avoid
        // false negatives
        BasicEvent[] myEvents = this.eventArray;
        BasicEvent[] otherEvents = ((CompositeEvent) obj).eventArray;
        if (myEvents.length != otherEvents.length) {
            return false;
        }
        Set<BasicEvent> myEventSet = new HashSet<>(Arrays.asList(myEvents));
        for (int i = 0; i < otherEvents.length; i++) {
            if (!myEventSet.contains(otherEvents[i])) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return this.eventArray[0].toString();
    }

    @Override
    protected CompositeEventCache createCache() {
        return new CompositeEventCache();
    }

    /** Also clears the caches of the constituent events. */
    @Override
    public void clearCache() {
        super.clearCache();
        for (BasicEvent event : this.eventArray) {
            event.clearCache();
        }
    }

    /** Record from which to create new events. May be {@code null}. */
    private final Record record;
    /** Indicator of the reuse quality of this event. */
    private final Reuse reuse;
    /** The (non-empty) array of sub-events constituting this event. */
    final BasicEvent[] eventArray;
    /** Cache reference instance for initialisation. */
    static private final CacheReference<CompositeEventCache> reference =
        CacheReference.<CompositeEventCache>newInstance(false);

    class CompositeEventCache
        extends AbstractRuleEvent<Rule,CompositeEventCache>.AbstractEventCache {
        /**
         * Reconstructs a set of events from the array stored in the composite
         * event.
         */
        SortedSet<BasicEvent> getEventSet() {
            if (this.eventSet == null) {
                this.eventSet =
                    new TreeSet<>(Arrays.asList(CompositeEvent.this.eventArray));
            }
            return this.eventSet;
        }

        private SortedSet<BasicEvent> eventSet;
    }
}
