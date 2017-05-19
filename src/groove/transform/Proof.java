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
import java.util.Collection;

import groove.grammar.Condition;
import groove.grammar.Rule;
import groove.grammar.host.HostEdgeSet;
import groove.grammar.host.HostNodeSet;
import groove.grammar.rule.RuleToHostMap;
import groove.transform.RuleEvent.Reuse;

/**
 * Proof of a {@link Condition}.
 * A proof may contain the following elements:
 * <ul>
 * <li> A match of the condition pattern, if the condition is a quantifier
 * <li> One or more proofs for one or more subconditions. In particular, for a
 * universal subcondition there will in general be one proof for
 * each match of the condition pattern; and if the condition of this proof is
 * conjunctive, there will be proofs for all subconditions.
 * </ul>
 * @author Arend Rensink
 * @version $Revision $
 */
public class Proof {
    /** Constructs a proof for a given {@link Condition}. */
    public Proof(Condition condition, RuleToHostMap patternMap) {
        this.condition = condition;
        this.patternMap = patternMap;
        assert condition.getOp()
            .hasPattern()
            || condition.getOp()
                .isConjunctive() && patternMap == null;
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

    /** Returns the set of proofs of sub-conditions. */
    public Collection<Proof> getSubProofs() {
        return this.subProofs;
    }

    /** Returns the (host graph) edges used as images in the proof. */
    public HostEdgeSet getEdgeValues() {
        HostEdgeSet result = new HostEdgeSet();
        for (Proof subMatch : getSubProofs()) {
            result.addAll(subMatch.getEdgeValues());
        }
        if (this.patternMap != null) {
            result.addAll(this.patternMap.edgeMap()
                .values());
        }
        return result;
    }

    /** Returns the (host graph) nodes used as images in the proof. */
    public HostNodeSet getNodeValues() {
        HostNodeSet result = new HostNodeSet();
        for (Proof subMatch : getSubProofs()) {
            result.addAll(subMatch.getNodeValues());
        }
        if (this.patternMap != null) {
            result.addAll(this.patternMap.nodeMap()
                .values());
        }
        return result;
    }

    /**
     * Creates an event on the basis of this proof.
     * This is only allowed if the proved condition has an associated rule.
     * An optional event factory can be used for event reuse
     * @param record factory for fresh nodes; may be <code>null</code>, in which case
     * events are not reused among transitions
     */
    public RuleEvent newEvent(Record record) {
        assert hasRule();
        Collection<BasicEvent> eventSet = new ArrayList<>();
        collectEvents(eventSet, record);
        assert !eventSet.isEmpty();
        if (eventSet.size() == 1 && !getRule().hasSubRules()) {
            return eventSet.iterator()
                .next();
        } else {
            return createCompositeEvent(record, eventSet);
        }
    }

    /**
     * Recursively collects the events of this proof and all sub-proofs into a
     * given collection.
     * @param events the resulting set of events
     * @param record factory for events; may be <code>null</code>, in which case
     * events are not reused among transitions
     */
    private void collectEvents(Collection<BasicEvent> events, Record record) {
        if (hasRule()) {
            BasicEvent myEvent = createSimpleEvent(record);
            events.add(myEvent);
        }
        for (Proof subMatch : getSubProofs()) {
            subMatch.collectEvents(events, record);
        }
    }

    /**
     * Callback factory method to create a simple event. Delegates to
     * {@link Record#createSimpleEvent(Rule, RuleToHostMap)} if
     * <code>record</code> is not <code>null</code>.
     */
    private BasicEvent createSimpleEvent(Record record) {
        assert hasRule();
        if (record == null) {
            return new BasicEvent(getRule(), getPatternMap(), Reuse.NONE);
        } else {
            return record.createSimpleEvent(getRule(), getPatternMap());
        }
    }

    /**
     * Callback factory method to create a composite event. Delegates to
     * {@link Record#createSimpleEvent(Rule, RuleToHostMap)} if
     * <code>nodeFactory</code> is not <code>null</code>.
     */
    private RuleEvent createCompositeEvent(Record record, Collection<BasicEvent> eventSet) {
        if (record == null) {
            return new CompositeEvent(record, getRule(), eventSet, Reuse.NONE);
        } else {
            return record.createCompositeEvent(getRule(), eventSet);
        }
    }

    /** Equality is determined by rule and element map. */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Proof)) {
            return false;
        }
        Proof other = (Proof) obj;
        if (!other.getCondition()
            .equals(getCondition())) {
            return false;
        }
        if (getPatternMap() == null) {
            if (other.getPatternMap() != null) {
                return false;
            }
        } else if (!getPatternMap().equals(other.getPatternMap())) {
            return false;
        }
        if (getSubProofs() == null) {
            return other.getSubProofs() == null;
        }
        return getSubProofs().equals(other.getSubProofs());
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
        result = prime * result + getSubProofs().hashCode();
        if (getPatternMap() != null) {
            result = prime * result + getPatternMap().hashCode();
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        if (getPatternMap() == null) {
            result.append(String.format("Combined match of %s", getCondition().getName()));
        } else {
            result.append(String.format("Match of %s: Nodes %s, edges %s",
                getCondition().getName(),
                getPatternMap().nodeMap(),
                getPatternMap().edgeMap()));
        }
        if (!getSubProofs().isEmpty()) {
            result.append(String.format("%n--- Submatches of %s ---%n", getCondition().getName()));
            for (Proof match : getSubProofs()) {
                result.append(match.toString());
                result.append("\n");
            }
            result.append(String.format("--- End of %s ---", getCondition().getName()));
        }
        return result.toString();
    }

    /**
     * The condition of which this is a proof.
     */
    private final Condition condition;
    /**
     * The pattern map of the match.
     * May be {@code null} if this is a composite proof:
     * in that case the proof is only a conjunction of its sub-proofs.
     */
    private final RuleToHostMap patternMap;

    /** The proofs of the sub-conditions. */
    private final Collection<Proof> subProofs = new java.util.LinkedHashSet<>();
    /** The (pre-computed) hash code of this match. */
    private int hashCode;

    /** Proof of {@link Condition#True}. */
    public static final Proof TrueProof = new Proof(Condition.True, null);
}
