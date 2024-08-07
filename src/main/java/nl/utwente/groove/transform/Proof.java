/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2023
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
package nl.utwente.groove.transform;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.grammar.Condition;
import nl.utwente.groove.grammar.Rule;
import nl.utwente.groove.grammar.host.HostEdgeSet;
import nl.utwente.groove.grammar.host.HostNodeSet;
import nl.utwente.groove.grammar.rule.RuleToHostMap;
import nl.utwente.groove.transform.RuleEvent.Reuse;

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
 * @version $Revision$
 */
@NonNullByDefault
public class Proof {
    /** Constructs a proof for a given {@link Condition}. */
    public Proof(Condition condition, @Nullable RuleToHostMap patternMap) {
        this.condition = condition;
        this.patternMap = patternMap;
        assert condition.getOp().hasPattern()
            || condition.getOp().isConjunctive() && patternMap == null;
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
     * The condition of which this is a proof.
     */
    private final Condition condition;

    /**
     * Indicates if this is a composite proof.
     * A composite proof consists of conjunctively interpreted subproofs,
     * but has no pattern map of its own.
     */
    public boolean isComposite() {
        return this.patternMap == null;
    }

    /** Returns the pattern map of this proof, if the condition is a quantifier. */
    public @Nullable RuleToHostMap getPatternMap() {
        return this.patternMap;
    }

    /**
     * The pattern map of the match.
     * May be {@code null} if this is a composite proof:
     * in that case the proof is only a conjunction of its sub-proofs.
     */
    private final @Nullable RuleToHostMap patternMap;

    /** Returns the set of proofs of sub-conditions. */
    public Collection<Proof> getSubProofs() {
        return this.subProofs;
    }

    /** The proofs of the sub-conditions. */
    private final Collection<Proof> subProofs = new java.util.LinkedHashSet<>();

    /** Returns the (host graph) edges used as images in the proof. */
    public HostEdgeSet getEdgeValues() {
        HostEdgeSet result = new HostEdgeSet();
        for (Proof subMatch : getSubProofs()) {
            result.addAll(subMatch.getEdgeValues());
        }
        if (this.patternMap != null) {
            result.addAll(this.patternMap.edgeMap().values());
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
            result.addAll(this.patternMap.nodeMap().values());
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
    public RuleEvent newEvent(@Nullable Record record) {
        assert hasRule();
        Collection<BasicEvent> eventSet = new ArrayList<>();
        collectEvents(eventSet, record);
        assert !eventSet.isEmpty();
        if (!getRule().hasSubRules()) {
            assert eventSet.size() == 1;
            return eventSet.iterator().next();
        } else {
            return createCompositeEvent(record, eventSet);
        }
    }

    /**
     * Recursively collects the events of this proof and all sub-proofs into a
     * given collection.
     * Events of locally non-modifying rules are skipped except for the top-level rule.
     * @param events the resulting set of events
     * @param record factory for events; may be <code>null</code>, in which case
     * events are not reused among transitions
     */
    private void collectEvents(Collection<BasicEvent> events, @Nullable Record record) {
        if (hasRule() && (getRule().isTop() || getRule().isLocallyModifying())) {
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
    private BasicEvent createSimpleEvent(@Nullable Record record) {
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
    private RuleEvent createCompositeEvent(@Nullable Record record,
                                           Collection<BasicEvent> eventSet) {
        if (record == null) {
            return new CompositeEvent(record, getRule(), eventSet, Reuse.NONE);
        } else {
            return record.createCompositeEvent(getRule(), eventSet);
        }
    }

    /** Equality is determined by rule and element map. */
    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Proof other)) {
            return false;
        }
        if (!other.getCondition().equals(getCondition())) {
            return false;
        }
        if (isComposite() != other.isComposite()) {
            return false;
        }
        var patternMap = getPatternMap();
        assert patternMap != null;
        if (!patternMap.equals(other.getPatternMap())) {
            return false;
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
        var patternMap = getPatternMap();
        if (patternMap != null) {
            result = prime * result + patternMap.hashCode();
        }
        return result;
    }

    /** The (pre-computed) hash code of this match. */
    private int hashCode;

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        var patternMap = getPatternMap();
        if (patternMap == null) {
            result.append(String.format("Combined match of %s", getCondition().getName()));
        } else {
            result
                .append(String
                    .format("Match of %s: Nodes %s, edges %s", getCondition().getName(),
                            patternMap.nodeMap(), patternMap.edgeMap()));
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

    /** Proof of {@link Condition#True}. */
    public static final Proof TrueProof = new Proof(Condition.True, null);
}
