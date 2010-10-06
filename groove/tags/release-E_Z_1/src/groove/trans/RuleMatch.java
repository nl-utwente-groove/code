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

import groove.rel.VarNodeEdgeMap;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Match of an {@link SPORule}.
 * @author Arend Rensink
 * @version $Revision $
 */
public class RuleMatch extends CompositeMatch {
    /** Constructs a match for a given {@link SPORule}. */
    public RuleMatch(SPORule rule, VarNodeEdgeMap elementMap) {
        super(elementMap);
        this.rule = rule;
    }

    /** Returns the rule of which this is a match. */
    public SPORule getRule() {
        return this.rule;
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
        Collection<SPOEvent> eventSet = new ArrayList<SPOEvent>();
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
    private void collectEvents(Collection<SPOEvent> events,
            SystemRecord nodeFactory) {
        SPOEvent myEvent = createSimpleEvent(nodeFactory);
        events.add(myEvent);
        for (Match subMatch : getSubMatches()) {
            if (subMatch instanceof RuleMatch) {
                ((RuleMatch) subMatch).collectEvents(events, nodeFactory);
            }
        }
    }

    /**
     * Callback factory method to create a simple event. Delegates to
     * {@link SystemRecord#createSimpleEvent(SPORule, VarNodeEdgeMap)} if
     * <code>nodeFactory</code> is not <code>null</code>.
     */
    private SPOEvent createSimpleEvent(SystemRecord nodeFactory) {
        if (nodeFactory == null) {
            return new SPOEvent(getRule(), getElementMap(), null, false);
        } else {
            return nodeFactory.createSimpleEvent(getRule(), getElementMap());
        }
    }

    /**
     * Callback factory method to create a composite event. Delegates to
     * {@link SystemRecord#createSimpleEvent(SPORule, VarNodeEdgeMap)} if
     * <code>nodeFactory</code> is not <code>null</code>.
     */
    private RuleEvent createCompositeEvent(SystemRecord nodeFactory,
            Collection<SPOEvent> eventSet) {
        if (nodeFactory == null) {
            return new CompositeEvent(getRule(), eventSet, false);
        } else {
            return nodeFactory.createCompositeEvent(getRule(), eventSet);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<RuleMatch> addSubMatchChoice(
            Iterable<? extends Match> choices) {
        return (Collection<RuleMatch>) super.addSubMatchChoice(choices);
    }

    @Override
    public RuleMatch createMatch() {
        return new RuleMatch(getRule(), getElementMap());
    }

    /** Equality is determined by rule and element map. */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof RuleMatch
            && ((RuleMatch) obj).getRule().equals(getRule())
            && super.equals(obj);
    }

    /** This implementation takes the rule into account. */
    @Override
    protected int computeHashCode() {
        return getRule().hashCode() + super.computeHashCode();
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
            for (Match match : getSubMatches()) {
                result.append(match.toString());
                result.append("\n");
            }
            result.append(String.format("--- End of %s ---",
                getRule().getName()));
        }
        return result.toString();
    }

    /** The fixed rule of which this is a match. */
    private final SPORule rule;
}
