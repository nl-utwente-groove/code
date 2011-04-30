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
 * Proof of a {@link Condition}.
 * A proof may contain the following elements:
 * <ul>
 * <li> A match of the condition pattern, if the condition is a quantifier
 * <li> One or more proofs for one or more subconditions. In particular, for a
 * universal subcondition there will in general one proof for 
 * each match of the condition pattern; and if the condition of this proof is
 * conjunctive, there will be proofs for all subconditions.
 * </ul>
 * @author Arend Rensink
 * @version $Revision $
 */
public class RuleMatch {
    /** Constructs a match for a given {@link Rule}. */
    public RuleMatch(Condition condition, RuleToHostMap patternMap) {
        this.condition = condition;
        this.patternMap = patternMap;
        assert condition.getOp().isQuantifier() || patternMap == null;
    }

    /** 
     * Indicates whether the proved condition is a rule.
     * Convenience method for {@code getCondition().hasRule()}.
     */
    public boolean hasRule() {
        return this.condition.hasRule();
    }

    /**
     * Returns the rule of the proved condition, if any. 
     * Convenience method for {@code getCondition().getRule()}.
     */
    public Rule getRule() {
        return this.condition.getRule();
    }

    /** Returns the condition of which this is a proof. */
    public Condition getCondition() {
        return this.condition;
    }

    /** 
     * Indicates if this is a composite proof.
     * A composite proof consists of conjunctively interpreted subproofs,
     * but has no pattern map of its own.
     */
    public boolean isComposite() {
        return this.patternMap == null;
    }

    /** Returns the pattern map of this proof, if the condition is a quantifier. */
    public RuleToHostMap getPatternMap() {
        return this.patternMap;
    }

    /** Returns the set of matches of sub-rules. */
    public Collection<RuleMatch> getSubMatches() {
        return this.subProofs;
    }

    /** Returns the (host graph) edges used as images in the match. */
    public Collection<HostEdge> getEdgeValues() {
        Set<HostEdge> result = new HashSet<HostEdge>();
        for (RuleMatch subMatch : getSubMatches()) {
            result.addAll(subMatch.getEdgeValues());
        }
        result.addAll(this.patternMap.edgeMap().values());
        return result;
    }

    /** Returns the (host graph) nodes used as images in the match. */
    public Collection<HostNode> getNodeValues() {
        Set<HostNode> result = new HashSet<HostNode>();
        for (RuleMatch subMatch : getSubMatches()) {
            result.addAll(subMatch.getNodeValues());
        }
        result.addAll(this.patternMap.nodeMap().values());
        return result;
    }

    /**
     * Creates an event on the basis of this proof.
     * This is only allowed if the proved condition has an associated rule.
     * @param nodeFactory factory for fresh nodes; may be <code>null</code>
     */
    public RuleEvent newEvent(SystemRecord nodeFactory) {
        assert hasRule();
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
     * Recursively collects the events of this proof and all sub-proofs into a
     * given collection.
     * @param events the resulting set of events
     * @param nodeFactory factory for fresh nodes; may be <code>null</code>
     */
    private void collectEvents(Collection<BasicEvent> events,
            SystemRecord nodeFactory) {
        if (hasRule()) {
            BasicEvent myEvent = createSimpleEvent(nodeFactory);
            events.add(myEvent);
        }
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
        assert hasRule();
        if (record == null) {
            return new BasicEvent(getRule(), getPatternMap(), false);
        } else {
            return record.createSimpleEvent(getRule(), getPatternMap());
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
        if (!other.getCondition().equals(getCondition())) {
            return false;
        }
        if (!other.getPatternMap().equals(getPatternMap())) {
            return false;
        }
        if (getSubMatches() == null) {
            return other.getSubMatches() == null;
        }
        return getSubMatches().equals(other.getSubMatches());
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
        final int prime = 31;
        int result = getCondition().hashCode();
        result = prime * result + getSubMatches().hashCode();
        if (getPatternMap() != null) {
            result = prime * result + getPatternMap().hashCode();
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder result =
            new StringBuilder(String.format("Match of %s: Nodes %s, edges %s",
                getCondition().getName(), getPatternMap().nodeMap(),
                getPatternMap().edgeMap()));
        if (!getSubMatches().isEmpty()) {
            result.append(String.format("%n--- Submatches of %s ---%n",
                getCondition().getName()));
            for (RuleMatch match : getSubMatches()) {
                result.append(match.toString());
                result.append("\n");
            }
            result.append(String.format("--- End of %s ---",
                getCondition().getName()));
        }
        return result.toString();
    }

    /**
     * The condition of which this is a proof.
     */
    private final Condition condition;
    /** 
     * The pattern map of the match.
     * May be {@code null} if this is a conjunctive proof. 
     */
    private final RuleToHostMap patternMap;

    /** The proofs of the sub-conditions. */
    private final Collection<RuleMatch> subProofs =
        new java.util.LinkedHashSet<RuleMatch>();
    /** The (pre-computed) hash code of this match. */
    private int hashCode;
}
