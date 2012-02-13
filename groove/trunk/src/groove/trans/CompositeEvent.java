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
 * $Id: CompositeEvent.java,v 1.10 2008-03-03 21:27:40 rensink Exp $
 */
package groove.trans;

import groove.util.CacheReference;
import groove.util.Property;
import groove.util.Visitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Rule event consisting of a set of events.
 * @author Arend Rensink
 * @version $Revision $
 */
public class CompositeEvent extends
        AbstractEvent<Rule,CompositeEvent.CompositeEventCache> {
    /**
     * Creates a composite event on the basis of a given (nonempty) constituent event set.
     * @param rule the rule for which this is an event
     * @param eventSet ordered non-empty collection of constituent events. The
     *        order is assumed to be the prefix traversal order of the
     *        dependency tree of the events, meaning the the first element is
     *        the event corresponding to the top level of <code>rule</code>.
     */
    public CompositeEvent(Rule rule, Collection<BasicEvent> eventSet,
            boolean reuse) {
        super(reference, rule, reuse);
        assert !eventSet.isEmpty();
        this.eventArray = new BasicEvent[eventSet.size()];
        eventSet.toArray(this.eventArray);
    }

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

    public String getAnchorImageString() {
        List<String> eventLabels = new ArrayList<String>();
        for (BasicEvent event : this.eventArray) {
            eventLabels.add(event.getRule().getFullName()
                + event.getAnchorImageString());
        }
        return Arrays.toString(eventLabels.toArray());
    }

    public Proof getMatch(HostGraph source) {
        Property<Proof> isMyMatch = new Property<Proof>() {
            @Override
            public boolean isSatisfied(Proof value) {
                return value.newEvent(null).equals(CompositeEvent.this);
            }
        };
        Proof result =
            getRule().traverseMatches(source, getAnchorMap(),
                Visitor.newFinder(isMyMatch));
        if (result != null) {
            return result;
        }
        // if we're here, we failed to reconstruct this event from
        // any of the matches.
        throw new IllegalArgumentException(String.format(
            "Can't find match for event %s", this));
    }

    @Override
    public void recordEffect(RuleEffect record) {
        BasicEvent[] events = this.eventArray;
        int eventCount = events.length;
        for (int i = 0; i < eventCount; i++) {
            events[i].recordEffect(record);
        }
    }

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
        final int prime = 31;
        for (int i = 0; i < this.eventArray.length; i++) {
            result = prime * result + this.eventArray[i].hashCode();
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
        return Arrays.equals(this.eventArray, ((CompositeEvent) obj).eventArray);
    }

    @Override
    public String toString() {
        return this.eventArray[0].toString();
    }

    @Override
    HostNode[] getArguments(HostNode[] addedNodes) {
        return this.eventArray[0].getArguments(addedNodes);
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

    /** The (non-empty) array of sub-events constituting this event. */
    final BasicEvent[] eventArray;
    /** Cache reference instance for initialisation. */
    static private final CacheReference<CompositeEventCache> reference =
        CacheReference.<CompositeEventCache>newInstance(false);

    class CompositeEventCache extends
            AbstractEvent<Rule,CompositeEventCache>.AbstractEventCache {
        /**
         * Reconstructs a set of events from the array stored in the composite
         * event.
         */
        SortedSet<BasicEvent> getEventSet() {
            if (this.eventSet == null) {
                this.eventSet =
                    new TreeSet<BasicEvent>(
                        Arrays.asList(CompositeEvent.this.eventArray));
            }
            return this.eventSet;
        }

        private SortedSet<BasicEvent> eventSet;
    }
}
