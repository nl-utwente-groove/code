/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2010 University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * $Id$
 */
package groove.match.rete;

import java.util.Arrays;

import groove.grammar.host.HostEdge;
import groove.grammar.host.HostFactory;
import groove.grammar.host.HostNode;
import groove.grammar.host.HostNodeSet;
import groove.grammar.host.ValueNode;
import groove.grammar.rule.LabelVar;
import groove.grammar.rule.RuleEdge;
import groove.grammar.rule.RuleElement;
import groove.grammar.rule.RuleNode;
import groove.grammar.rule.RuleToHostMap;
import groove.grammar.rule.Valuation;

/**
 * @author Arash Jalali
 * @version $Revision $
 */
public class ReteSimpleMatch extends AbstractReteMatch {

    /** Host graph elements. */
    private Object[] units;
    /**
     * This is the set of nodes (host nodes) in this match.
     * It is only of use in injective matching so it will be
     * filled lazily if needed by the <code>getNodes</code> method.
     */
    protected HostNodeSet nodes = null;

    /**
     * Creates a new match object from a given sub-match copying all the units of the submatch.
     * @param origin The n-node this match is associated with.
     * @param injective Determines if this match is used in an engine with injective matching
     * @param subMatch The sub-match to be used.
     */
    public ReteSimpleMatch(ReteNetworkNode origin, boolean injective, AbstractReteMatch subMatch) {
        this(origin, injective);
        this.specialPrefix = subMatch.specialPrefix;
        assert origin.getPattern().length == subMatch.getOrigin()
            .getPattern().length;
        this.units = subMatch.getAllUnits();
        this.valuation = subMatch.valuation;
        subMatch.addSuperMatch(this);
    }

    /**
     * Creates a new match object from a given sub-match copying all the units of the submatch
     * and appending the given units
     *
     * @param origin The n-node this match is associated with.
     * @param injective Determines if this match is used in an engine with injective matching
     * @param subMatch The sub-match to be used.
     * @param unitsToAppend The units to append to the match units of the create match
     * object. It is assumed that <code>unitsToAppend.length + subMatch.getAllUnits().length == origin.getPattern().length</code>
     */
    public ReteSimpleMatch(ReteNetworkNode origin, boolean injective, AbstractReteMatch subMatch,
        Object[] unitsToAppend) {
        this(origin, injective);
        Object[] subMatchUnits = subMatch.getAllUnits();
        assert unitsToAppend.length + subMatchUnits.length == origin.getPattern().length;
        this.specialPrefix = subMatch.specialPrefix;
        this.valuation = subMatch.valuation;
        subMatch.addSuperMatch(this);
        this.units = new Object[subMatchUnits.length + unitsToAppend.length];
        for (int i = 0; i < subMatchUnits.length; i++) {
            this.units[i] = subMatchUnits[i];
        }
        for (int i = 0; i < unitsToAppend.length; i++) {
            this.units[i + subMatchUnits.length] = unitsToAppend[i];
        }
    }

    /**
     * Creates an empty match, a match without any units stored in its list
     * of units.
     *
     * A proper size is however provisioned for where the units are saved
     * based on the size of the origin's pattern (see {@link ReteNetworkNode#getPattern()}).
     *
     * @param origin The n-node that this match belongs to.
     * @param injective  determines if the match is being used in an injective engine instance.
     */
    public ReteSimpleMatch(ReteNetworkNode origin, boolean injective) {
        super(origin, injective);
        this.units = new Object[origin.getPattern().length];
    }

    /**
     * Creates a singleton match consisting of one Edge match
     * @param origin The n-node to which this match belongs/is found by.
     * @param match The matched edge.
     * @param injective Determines if this is an injectively found match.
     */
    public ReteSimpleMatch(ReteNetworkNode origin, HostEdge match, boolean injective) {
        this(origin, injective);
        this.units[0] = match;
    }

    /**
     * Creates a singleton match consisting of one Edge match with the edge
     * label assigned to the given variable.
     *
     * @param origin The n-node to which this match belongs/is found by.
     * @param match The matched edge.
     * @param variable The variable that has to be bound to
     *                     the label of the given <code>match</code>
     * @param injective Determines if this is an injectively found match.
     */
    public ReteSimpleMatch(ReteNetworkNode origin, HostEdge match, LabelVar variable,
        boolean injective) {
        this(origin, injective);
        this.units[0] = match;
        this.valuation = new Valuation();
        this.valuation.put(variable, match.getType());
    }

    /**
     * Creates a singleton match consisting of one Node match
     * @param origin The n-node by which this match has been found.
     * @param match The graph node that has been found as a match
     * @param injective Determines if this is a match found by an
     *        injective matcher.
     */
    public ReteSimpleMatch(ReteNetworkNode origin, HostNode match, boolean injective) {
        this(origin, injective);
        this.units[0] = match;
    }

    /**
     * @return The reference to the prefix positive match that this match is a composite
     * (positive + negative) extension of. The return value will be <code>null</code>
     * if this match is not the left prefix of a composite (positive+negative) match.
     */
    @Override
    public AbstractReteMatch getSpecialPrefix() {
        return this.specialPrefix;
    }

    /**
     * @return The array of all the match elements, i.e. elements of
     * the host graph that are part of this match.
     */
    @Override
    public Object[] getAllUnits() {
        return this.units;
    }

    @Override
    public int size() {
        return this.units.length;
    }

    /**
     * @param n A RETE or LHS node.
     * @return The {@link HostNode} object in the host graph to which <code>n</code> is mapped.
     */
    public HostNode getNode(RuleNode n) {
        LookupEntry entry = getOrigin().getPatternLookupTable()
            .getNode(n);
        return (HostNode) entry.lookup(this.units);
    }

    @Override
    public HostNodeSet getNodes() {
        if (this.nodes == null) {
            this.nodes = new HostNodeSet();
            for (int i = 0; i < this.units.length; i++) {
                if (this.units[i] instanceof HostEdge) {
                    HostEdge e = (HostEdge) this.units[i];
                    this.nodes.add(e.source());
                    if (!e.source()
                        .equals(e.target())) {
                        this.nodes.add(e.target());
                    }
                } else {
                    this.nodes.add((HostNode) this.units[i]);
                }
            }
        }
        return this.nodes;
    }

    /**
     * @param e An edge in the pattern associated with the {@link #getOrigin()} of this
     *          match.
     * @return the host-Edge to which <code>e</code> is mapped, <code>null</code>
     * otherwise.
     */
    public HostEdge getEdge(RuleEdge e) {
        int index = this.getOrigin()
            .getPatternLookupTable()
            .getEdge(e);
        return (index != -1) ? (HostEdge) this.units[index] : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReteSimpleMatch)) {
            return false;
        }
        ReteSimpleMatch m = (ReteSimpleMatch) o;
        if (hashCode() != m.hashCode()) {
            return false;
        }
        if (getOrigin() != m.getOrigin()) {
            return false;
        }
        Object[] thisList = this.getAllUnits();
        Object[] mList = m.getAllUnits();
        boolean result = mList.length == thisList.length;
        if (result) {
            int thisSize = this.size();
            for (int i = 0; i < thisSize; i++) {
                if (thisList[i] != mList[i]) {
                    assert!thisList[i].equals(mList[i]);
                    result = false;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Determines if a sub-match <code>m</code>'s units exist in the units
     * of this match beginning at a given index.
     *
     * @param index The index at which the units of <code>m</code> should begin to correspond.
     * @param m The alleged sub-match at the given index.
     * @return <code>true</code> if it is contained, <code>false</code> otherwise.
     */
    public boolean isContainedAt(int index, ReteSimpleMatch m) {
        boolean result = true;
        int mSize = m.size();
        Object[] units = this.getAllUnits();
        for (int i = 0; i < mSize; i++) {
            if (!this.units[i + index].equals(units[i])) {
                result = false;
                break;
            }
        }
        return result;
    }

    private RuleToHostMap equivalentMap = null;

    /**
     * Translates this match object, which is only used inside the RETE network,
     * to an instance of {@link RuleToHostMap} that is the standard representation
     * of any matching between a rule's nodes and edges to those of a host graph
     * in GROOVE.
     *
     * @param factory The factory that can create the right map type
     * @return A translation of this match object to the {@link RuleToHostMap} representation
     */
    public RuleToHostMap toRuleToHostMap(HostFactory factory) {
        if (this.equivalentMap == null) {
            this.equivalentMap = factory.createRuleToHostMap();

            RuleElement[] pattern = this.getOrigin()
                .getPattern();
            for (int i = 0; i < this.units.length; i++) {
                Object e = this.units[i];
                if (e instanceof HostNode) {
                    this.equivalentMap.putNode((RuleNode) pattern[i], (HostNode) e);
                } else if (e instanceof HostEdge) {
                    RuleEdge e1 = (RuleEdge) pattern[i];
                    HostEdge e2 = (HostEdge) e;
                    this.equivalentMap.putEdge(e1, e2);
                    this.equivalentMap.putNode(e1.source(), e2.source());
                    this.equivalentMap.putNode(e1.target(), e2.target());
                } else { //e instance of RetePathMatch
                    RuleEdge e1 = (RuleEdge) pattern[i];
                    RetePathMatch m = (RetePathMatch) e;
                    this.equivalentMap.putNode(e1.source(), m.start());
                    this.equivalentMap.putNode(e1.target(), m.end());
                }
            }
            if (this.getValuation() != null) {
                this.equivalentMap.getValuation()
                    .putAll(this.getValuation());
            }
        }
        return this.equivalentMap;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[ " + Arrays.toString(this.getOrigin()
            .getPattern()) + ": ");
        for (int i = 0; i < this.units.length; i++) {
            sb.append("[ " + this.units[i].toString() + "] ");
        }
        if ((this.valuation != null)) {
            sb.append(" |> " + this.valuation.toString());
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Merges an array matches into one match in the order appearing in the array.
     *
     * If the matches conflict, this method will fail. A conflict constitutes
     * a variable-binding conflict among the sub-matches, or violation of injectivity
     * if the resulting merge is meant to be an injective match.
     *
     * @param origin The n-node that is to be set as the origin of the resulting merge.
     * @param subMatches the array of sub-matches
     * @param injective Specifies if this is an injectively found match.
     * @return A newly created match object containing the merge of all the subMatches
     * if they do not conflict, {@literal null} otherwise.
     */

    public static ReteSimpleMatch merge(ReteNetworkNode origin, AbstractReteMatch[] subMatches,
        boolean injective) {
        ReteSimpleMatch result = new ReteSimpleMatch(origin, injective);
        Valuation valuation = AbstractReteMatch.getMergedValuation(subMatches);
        if (valuation != null) {
            HostNodeSet nodes = new HostNodeSet();
            int k = 0;
            for (int i = 0; i < subMatches.length; i++) {
                Object[] subMatchUnits = subMatches[i].getAllUnits();
                if (injective) {
                    for (HostNode n : subMatches[i].getNodes()) {
                        if (nodes.put(n) != null) {
                            return null;
                        }
                    }
                }
                for (int j = 0; j < subMatchUnits.length; j++) {
                    result.units[k++] = subMatchUnits[j];
                }
                subMatches[i].addSuperMatch(result);
            }
            assert k == origin.getPattern().length;

            result.valuation = (valuation == emptyMap) ? null : valuation;
        } else {
            result = null;
        }
        return result;
    }

    /*
    @Override
    public AbstractReteMatch merge(ReteNetworkNode origin, AbstractReteMatch m,
            boolean copyLeftPrefix) {
        assert m instanceof ReteSimpleMatch;
        return ReteSimpleMatch.merge(origin, this, m, this.isInjective(),
            copyLeftPrefix);
    }
    */
    /**
     * Combines a simple match with any other type of matche into one match, preserving
     * the prelim match's hash code.
     *
     * No injective conflict checking is performed. In other words, this method assumes
     * that merging the given sub-matches will result in a consistent bigger match.
     *
     * @param origin The n-node that is to be set as the origin of the resulting merge.
     * @param m1 The left match, the units of which will be in the beginning of the
     *           units of the merged match.
     * @param m2 The right match, the units of which will be at the end of the
     *           units of the merged match.
     * @param injective Specifies if this is an injectively found match.
     * @param copyPrefix if {@literal true} then the special prefix link of m1
     *        (or m1 if it's prefix is null) will be copied to that of the result.
     * @return A newly created match object containing the merge of m1 and m2
     * if m1 and m2 do not conflict, {@literal null} otherwise.
     */
    public static ReteSimpleMatch merge(ReteNetworkNode origin, AbstractReteMatch m1,
        AbstractReteMatch m2, boolean injective, boolean copyPrefix) {

        ReteSimpleMatch result = null;
        Valuation valuation = m1.getMergedValuation(m2);
        if (valuation != null) {
            result = new ReteSimpleMatch(origin, injective);
            Object[] units1 = m1.getAllUnits();
            Object[] units2 = m2.getAllUnits();
            if (copyPrefix) {
                result.specialPrefix = (m1.specialPrefix != null) ? m1.specialPrefix : m1;
            }
            assert result.units.length == units1.length + units2.length;
            int i = 0;
            for (; i < units1.length; i++) {
                result.units[i] = units1[i];
            }

            for (; i < result.units.length; i++) {
                result.units[i] = units2[i - units1.length];
            }

            m1.addSuperMatch(result);
            m2.addSuperMatch(result);
            result.valuation = (valuation != emptyMap) ? valuation : null;
        }
        return result;
    }

    /**
     * Combines two matches into one simple match.
     *
     * No injective conflict checking is performed. In other words, this method assumes
     * that merging the given sub-matches will result in a consistent bigger match.
     *
     * @param origin The n-node that is to be set as the origin of the resulting merge.
     * @param m1 The left match, the units of which will be in the beginning of the
     *           units of the merged match.
     * @param m2 The right match, the units of which will be at the end of the
     *           units of the merged match.
     * @param injective Specifies if this is an injectively found match.
     * @param copyPrefix if {@literal true} then the special prefix link of m1
     *        (or m1 if it's prefix is null) will be copied to that of the result.
     * @return A newly created match object containing the merge of m1 and m2
     * if m1 and m2 do not conflict, {@literal null} otherwise.
     */
    public static ReteSimpleMatch merge(ReteNetworkNode origin, ReteSimpleMatch m1,
        ReteSimpleMatch m2, boolean injective, boolean copyPrefix) {

        ReteSimpleMatch result = null;
        Valuation valuation = m1.getMergedValuation(m2);
        if (valuation != null) {
            Object[] m1Units = m1.getAllUnits();
            result = new ReteSimpleMatch(origin, injective);
            Object[] units2 = m2.getAllUnits();
            if (copyPrefix) {
                result.specialPrefix = (m1.specialPrefix != null) ? m1.specialPrefix : m1;
            }
            assert result.units.length == m1Units.length + units2.length;
            int i = 0;
            for (; i < m1Units.length; i++) {
                result.units[i] = m1Units[i];
            }

            for (; i < result.units.length; i++) {
                result.units[i] = units2[i - m1Units.length];
            }

            result.hashCode();
            m1.addSuperMatch(result);
            m2.addSuperMatch(result);
            result.valuation = (valuation != emptyMap) ? valuation : null;
        }
        return result;
    }

    /**
     * Creates a simple match object of any given match object of any kind
     * by simply naively copying the units and the special prefix.
     *
     * @param origin The owner to be set for the created match object
     * @param injective if the match should be marked as injectively-found
     * @param source The object which the result should be made from
     */
    public static ReteSimpleMatch forge(ReteNetworkNode origin, boolean injective,
        AbstractReteMatch source) {
        ReteSimpleMatch result = new ReteSimpleMatch(origin, injective);
        result.specialPrefix = source.specialPrefix;
        assert(source.specialPrefix == null)
            || (origin.getPattern().length == source.specialPrefix.getOrigin()
                .getPattern().length);
        result.units = source.getAllUnits();
        result.valuation = source.valuation;
        return result;
    }

    /**
     * Represents a match for a quantifier's count node as well
     * as the quantifier's root anchor nodes.
     *
     * @author Arash Jalali
     * @version $Revision $
     */
    public static class ReteCountMatch extends ReteSimpleMatch {
        private final boolean dummy;

        /**
         * Creates a non-wildcard count match.
         *
         * @param owner The {@link QuantifierCountChecker} node to which this match belongs
         * @param anchors The pre-matched root nodes of the quantifier
         * @param value The count value
         */
        public ReteCountMatch(ReteNetworkNode owner, HostNode[] anchors, ValueNode value) {
            super(owner, owner.getOwner()
                .isInjective());
            Object[] myUnits = getAllUnits();
            assert(owner instanceof QuantifierCountChecker)
                && (anchors.length + 1 == owner.getPattern().length);
            this.dummy = false;
            for (int i = 0; i < anchors.length; i++) {
                myUnits[i] = anchors[i];
            }
            myUnits[size() - 1] = value;
        }

        /**
         * Creates a dummy count match.
         *
         * See {@link #isDummy()} on what a dummy count match is.
         * @param owner The {@link QuantifierCountChecker} n-node this match
         *              belongs to.
         * @param value The zero value node for this dummy match.
         * @throws IllegalArgumentException is raised if
         *            the <code>value</code> parameter does not
         *            represent a zero.
         */
        public ReteCountMatch(ReteNetworkNode owner, ValueNode value) {
            super(owner, owner.getOwner()
                .isInjective());
            this.dummy = true;
            Object[] myUnits = getAllUnits();
            for (int i = 0; i < size() - 1; i++) {
                myUnits[i] = value;
            }
            if (!value.getValue()
                .equals(0)) {
                throw new IllegalArgumentException(String.format(
                    "The given value for the wildcard match must be zero. It is now %s",
                    value.getValue()
                        .toString()));
            } else {
                myUnits[size() - 1] = value;
            }
        }

        /**
         * Determines if this match is a dummy count match.
         *
         * A dummy count match is one that represents
         * the zero-count for all the possible anchors
         * that do not explicitly occur in matches produced by
         * a {@link QuantifierCountChecker}.
         *
         * A dummy match's units (see {@link #getAllUnits()})
         * consist of zero value nodes for all anchor values
         * and zero (0) for the value itself.
         */
        public boolean isDummy() {
            return this.dummy;
        }

        /**
         * Returns the value node associated with the actual count-value
         * this match represents
         */
        public ValueNode getValue() {
            return (ValueNode) getAllUnits()[size() - 1];
        }

        @Override
        public boolean equals(Object o) {
            if (o != null) {
                return (o instanceof ReteCountMatch)
                    && this.getOrigin() == ((ReteCountMatch) o).getOrigin()
                    && this.dummy == ((ReteCountMatch) o).dummy
                    && this.getValue() == ((ReteCountMatch) o).getValue();
            } else {
                return false;
            }
        }

        @Override
        public String toString() {
            return "Count match for "
                + ((QuantifierCountChecker) getOrigin()).getUniversalQuantifierChecker()
                    .getCondition()
                    .getName()
                + ". Value = " + this.getValue()
                    .toString();
        }

        /**
         * Merges this dummy count match with a given match.
         *
         * @param origin The RETE network n-node to which this match is to belong to
         * @param leftMatch the actual match that is to be merged with this dummy count match
         * @param copyPrefix if {@literal true} then the special prefix link of leftMatch
        *        (or leftMatch if its prefix is null) will be copied to that of the result.
         * @param mergeLookupTable A table that determines where the anchor points (to be copied from)
         *                         reside in the leftMatch.
         */
        public ReteSimpleMatch dummyMerge(ReteNetworkNode origin, AbstractReteMatch leftMatch,
            boolean copyPrefix, LookupEntry[] mergeLookupTable) {
            assert this.dummy;
            ReteSimpleMatch result = new ReteSimpleMatch(origin, origin.getOwner()
                .isInjective());
            Object[] resultUnits = result.getAllUnits();
            Object[] leftUnits = leftMatch.getAllUnits();
            Object[] myUnits = getAllUnits();
            int i = 0;
            for (; i < leftUnits.length; i++) {
                resultUnits[i] = leftUnits[i];
            }
            for (; i < leftUnits.length + size() - 1; i++) {
                LookupEntry pos = mergeLookupTable[i - leftUnits.length];
                resultUnits[i] = pos.lookup(leftUnits);
            }
            resultUnits[result.size() - 1] = myUnits[size() - 1];
            if (copyPrefix) {
                result.specialPrefix =
                    (leftMatch.specialPrefix != null) ? leftMatch.specialPrefix : leftMatch;
            }

            result.hashCode();
            this.addSuperMatch(result);
            leftMatch.addSuperMatch(result);
            result.valuation = leftMatch.valuation;
            return result;
        }
    }
}
