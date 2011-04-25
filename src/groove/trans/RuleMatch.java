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
 * $Id: RuleMatch.java,v 1.9 2008-03-03 21:27:40 rensink Exp $
 */
package groove.trans;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Match of an {@link Rule}.
 * @author Arend Rensink
 * @version $Revision $
 */
public class RuleMatch {
    /** Constructs a match for a given {@link Rule}. */
    public RuleMatch(Rule rule, RuleToHostMap elementMap) {
        this.rule = rule;
        this.elementMap = elementMap;
    }

    /** Returns the rule of which this is a match. */
    public Rule getRule() {
        return this.rule;
    }

    /** Returns the element map constituting the match. */
    public RuleToHostMap getElementMap() {
        return this.elementMap;
    }

    /** Returns the set of matches of sub-rules. */
    public Collection<RuleMatch> getSubMatches() {
        return this.subMatches;
    }

    /** Returns the (host graph) edges used as images in the match. */
    public Collection<HostEdge> getEdgeValues() {
        Set<HostEdge> result = new HashSet<HostEdge>();
        for (RuleMatch subMatch : getSubMatches()) {
            result.addAll(subMatch.getEdgeValues());
        }
        result.addAll(this.elementMap.edgeMap().values());
        return result;
    }

    /** Returns the (host graph) nodes used as images in the match. */
    public Collection<HostNode> getNodeValues() {
        Set<HostNode> result = new HashSet<HostNode>();
        for (RuleMatch subMatch : getSubMatches()) {
            result.addAll(subMatch.getNodeValues());
        }
        result.addAll(this.elementMap.nodeMap().values());
        return result;
    }

    /**
     * Creates an event on the basis of this match.
     * @param nodeFactory factory for fresh nodes; may be <code>null</code>
     */
    public RuleEvent newEvent(SystemRecord nodeFactory) {
        // the event set used to be a sorted set, but I think this is wrong
        // because the sorting will not respect the desired event hierarchy.
        // and in fact events may actually occur more than once.
        // SortedSet<SPOEvent> eventSet = new TreeSet<SPOEvent>();
        Collection<BasicEvent> eventSet = new ArrayList<BasicEvent>();
        collectEvents(eventSet, nodeFactory);
        assert !eventSet.isEmpty();
        if (eventSet.size() == 1 && !getRule().hasSubRules()) {
            return eventSet.iterator().next();
        } else {
            return createCompositeEvent(nodeFactory, eventSet);
        }
    }

    /**
     * Recursively collects the events of this match and all sub-matches into a
     * given collection.
     * @param events the resulting set of events
     * @param nodeFactory factory for fresh nodes; may be <code>null</code>
     */
    private void collectEvents(Collection<BasicEvent> events,
            SystemRecord nodeFactory) {
        BasicEvent myEvent = createSimpleEvent(nodeFactory);
        events.add(myEvent);
        for (RuleMatch subMatch : getSubMatches()) {
            subMatch.collectEvents(events, nodeFactory);
        }
    }

    /**
     * Callback factory method to create a simple event. Delegates to
     * {@link SystemRecord#createSimpleEvent(Rule, RuleToHostMap)} if
     * <code>nodeFactory</code> is not <code>null</code>.
     */
    private BasicEvent createSimpleEvent(SystemRecord record) {
        if (record == null) {
            return new BasicEvent(getRule(), getElementMap(), false);
        } else {
            return record.createSimpleEvent(getRule(), getElementMap());
        }
    }

    /**
     * Callback factory method to create a composite event. Delegates to
     * {@link SystemRecord#createSimpleEvent(Rule, RuleToHostMap)} if
     * <code>nodeFactory</code> is not <code>null</code>.
     */
    private RuleEvent createCompositeEvent(SystemRecord nodeFactory,
            Collection<BasicEvent> eventSet) {
        if (nodeFactory == null) {
            return new CompositeEvent(getRule(), eventSet, false);
        } else {
            return nodeFactory.createCompositeEvent(getRule(), eventSet);
        }
    }

    /** Equality is determined by rule and element map. */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof RuleMatch)) {
            return false;
        }
        RuleMatch other = (RuleMatch) obj;
        return other.getRule().equals(getRule())
            && other.getElementMap().equals(getElementMap())
            && other.getSubMatches().equals(getSubMatches());
    }

    @Override
    public int hashCode() {
        // pre-compute the value, if not yet done
        if (this.hashCode == 0) {
            this.hashCode = computeHashCode();
            if (this.hashCode == 0) {
                this.hashCode = 1;
            }
        }
        return this.hashCode;
    }

    /** Computes a value for the hash code. */
    protected int computeHashCode() {
        return getRule().hashCode() + getSubMatches().hashCode()
            ^ getElementMap().hashCode();
    }

    @Override
    public String toString() {
        StringBuilder result =
            new StringBuilder(String.format("Match of %s: Nodes %s, edges %s",
                getRule().getName(), getElementMap().nodeMap(),
                getElementMap().edgeMap()));
        if (!getSubMatches().isEmpty()) {
            result.append(String.format("%n--- Submatches of %s ---%n",
                getRule().getName()));
            for (RuleMatch match : getSubMatches()) {
                result.append(match.toString());
                result.append("\n");
            }
            result.append(String.format("--- End of %s ---",
                getRule().getName()));
        }
        return result.toString();
    }

    /** The fixed rule of which this is a match. */
    private final Rule rule;
    /** The map constituting the match. */
    private final RuleToHostMap elementMap;

    /** The map constituting the match. */
    private final Collection<RuleMatch> subMatches =
        new java.util.LinkedHashSet<RuleMatch>();
    /** The (pre-computed) hash code of this match. */
    private int hashCode;
}
