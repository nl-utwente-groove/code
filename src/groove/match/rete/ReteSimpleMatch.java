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

import groove.rel.LabelVar;
import groove.rel.Valuation;
import groove.trans.HostEdge;
import groove.trans.HostElement;
import groove.trans.HostFactory;
import groove.trans.HostNode;
import groove.trans.RuleEdge;
import groove.trans.RuleElement;
import groove.trans.RuleNode;
import groove.trans.RuleToHostMap;
import groove.util.TreeHashSet;

import java.util.Set;

/**
 * @author Arash Jalali
 * @version $Revision $
 */
public class ReteSimpleMatch extends AbstractReteMatch {

    /** Host graph elements. */
    private HostElement[] units;
    /**
     * This is the set of nodes (host nodes) in this match.
     * It is only of use in injective matching so it will be
     * filled lazily if needed by the <code>getNodes</code> method.
     */
    private Set<HostNode> nodes = null;

    /**
     * Creates a new match object from a given sub-match copying all the units of the submatch.
     * @param origin The n-node this match is associated with.
     * @param injective Determines if this match is used in an engine with injective matching  
     * @param subMatch The sub-match to be used.
     */
    public ReteSimpleMatch(ReteNetworkNode origin, boolean injective,
            AbstractReteMatch subMatch) {
        this(origin, injective);
        this.specialPrefix = subMatch.specialPrefix;
        subMatch.getSuperMatches().add(this);
        assert origin.getPattern().length == subMatch.getOrigin().getPattern().length;
        this.units = subMatch.getAllUnits();
        this.valuation = subMatch.valuation;
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
    public ReteSimpleMatch(ReteNetworkNode origin, boolean injective,
            AbstractReteMatch subMatch, HostElement[] unitsToAppend) {
        this(origin, injective);
        HostElement[] subMatchUnits = subMatch.getAllUnits();
        assert unitsToAppend.length + subMatchUnits.length == origin.getPattern().length;
        this.specialPrefix = subMatch.specialPrefix;
        this.valuation = subMatch.valuation;
        subMatch.getSuperMatches().add(this);
        this.units =
            new HostElement[subMatchUnits.length + unitsToAppend.length];
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
        this.units = new HostElement[origin.getPattern().length];
    }

    /**
     * Creates a singleton match consisting of one Edge match
     * @param origin The n-node to which this match belongs/is found by.
     * @param match The matched edge.
     * @param injective Determines if this is an injectively found match.
     */
    public ReteSimpleMatch(ReteNetworkNode origin, HostEdge match,
            boolean injective) {
        this(origin, injective);
        this.units[0] = match;
        this.hashCode = match.hashCode();
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
    public ReteSimpleMatch(ReteNetworkNode origin, HostEdge match,
            LabelVar variable, boolean injective) {
        this(origin, injective);
        this.units[0] = match;
        this.hashCode = match.hashCode();
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
    public ReteSimpleMatch(ReteNetworkNode origin, HostNode match,
            boolean injective) {
        this(origin, injective);
        this.units[0] = match;
        this.hashCode = match.hashCode();
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
    public HostElement[] getAllUnits() {
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
        int[] index = this.getOrigin().getPatternLookupTable().getNode(n);
        HostNode result = lookupNode(index);
        return result;
    }

    private HostNode lookupNode(int[] index) {
        HostNode result = null;
        if ((index != null) && (index[0] >= 0)) {
            result =
                (index[1] != -1) ? ((index[1] == 0)
                        ? ((HostEdge) this.units[index[0]]).source()
                        : ((HostEdge) this.units[index[0]]).target())
                        : (HostNode) this.units[index[0]];
        }
        return result;
    }

    @Override
    public Set<HostNode> getNodes() {
        if (this.nodes == null) {
            this.nodes = new TreeHashSet<HostNode>();
            for (int i = 0; i < this.units.length; i++) {
                if (this.units[i] instanceof HostEdge) {
                    HostEdge e = (HostEdge) this.units[i];
                    this.nodes.add(e.source());
                    if (!e.source().equals(e.target())) {
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
        int index = this.getOrigin().getPatternLookupTable().getEdge(e);
        return (index != -1) ? (HostEdge) this.units[index] : null;
    }

    /**
     * Compares this match to an allegedly-comparable match. It does not really
     * check for comparability, i.e. to see that the two have the same actually
     * originate from the same pattern.
     * 
     * To check comparability the {@link #equals(ReteSimpleMatch)} method should be called.
     * 
     * @param m The match to which the current match object should be compared.
     * @return positive integer if this match is greater than <code>m</code>,
     * zero if the two have the exact same match-units in the exact same order, and
     * a negative integer if the this match is less than <code>m</code>.
     */
    public int compareTo(AbstractReteMatch m) {
        HostElement[] thisList = this.getAllUnits();
        HostElement[] mList = m.getAllUnits();

        int result = this.hashCode() - m.hashCode();
        if (result == 0) {
            result = this.size() - m.size();
            if (result == 0) {
                int thisSize = this.size();
                for (int i = 0; (i < thisSize) && (result == 0); i++) {
                    result = thisList[i].compareTo(mList[i]);
                }
            }
        }
        return result;
    }

    /**
     * @param m A given match
     * @return <code>true</code> if this object is equal to <code>m</code>, i.e.
     * if they both refer to the same object or if they have the same origin,
     * and the exact array of elements in their array of {@link #units} with the exact
     * same order. Otherwise, <code>m</code> is considered unequal to the current object
     * and the return value will be <code>null</code>. 
     */
    public boolean equals(ReteSimpleMatch m) {
        boolean result;
        if ((m != null) && (this.getOrigin() == m.getOrigin())
            && (this.hashCode() == m.hashCode())) {
            result = (m == this) || (this.compareToForEquality(m));
        } else {
            result = false;
        }
        assert (this.hashCode() == m.hashCode())
            || !this.compareToForEquality(m);
        return result;
    }

    private boolean compareToForEquality(AbstractReteMatch m) {
        HostElement[] thisList = this.getAllUnits();
        HostElement[] mList = m.getAllUnits();
        boolean result = true;

        int thisSize = this.size();
        for (int i = 0; i < thisSize; i++) {
            if (thisList[i] != mList[i]) {
                assert !thisList[i].equals(mList[i]);
                result = false;
                break;
            }
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof ReteSimpleMatch)
            && this.equals((ReteSimpleMatch) o);
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
        HostElement[] units = this.getAllUnits();
        for (int i = 0; i < mSize; i++) {
            if (!this.units[i + index].equals(units[i])) {
                result = false;
                break;
            }
        }
        return result;
    }

    /**
     * Merges two sub-matches into a bigger one. For more details see 
     * the documentation for {@link #merge(ReteNetworkNode, ReteSimpleMatch, AbstractReteMatch, boolean, boolean)}
     *
     * @param origin The n-node that is to be set as the origin of the resulting merge.  
     * @param m1 The left match, the units of which will be at the beginning of the
     *           units of the merged match.
     * @param m2 The right match, the units of which will be at the end of the
     *           units of the merged match.
     * @param injective Specifies if this is an injectively found match. 
     * @return The resulting merged match.
     */
    public static ReteSimpleMatch merge(ReteNetworkNode origin,
            ReteSimpleMatch m1, AbstractReteMatch m2, boolean injective) {
        return ReteSimpleMatch.merge(origin, m1, m2, injective, false);
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
    public static ReteSimpleMatch merge(ReteNetworkNode origin,
            AbstractReteMatch[] subMatches, boolean injective) {
        ReteSimpleMatch result = new ReteSimpleMatch(origin, injective);
        TreeHashSet<HostNode> nodes =
            (injective) ? new TreeHashSet<HostNode>() : null;
        Valuation valuation = AbstractReteMatch.mergeValuations(subMatches);
        if (valuation != null) {
            int k = 0;
            for (int i = 0; i < subMatches.length; i++) {
                HostElement[] subMatchUnits = subMatches[i].getAllUnits();
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
                subMatches[i].getSuperMatches().add(result);
            }
            assert k == origin.getPattern().length;

            assert subMatches[0].hashCode() != 0;
            result.refreshHashCode(subMatches[0].hashCode(),
                subMatches[0].getAllUnits().length);
            result.valuation = (valuation == emptyMap) ? null : valuation;
        } else {
            result = null;
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
    @Override
    public RuleToHostMap toRuleToHostMap(HostFactory factory) {
        if (this.equivalentMap == null) {
            this.equivalentMap = factory.createRuleToHostMap();

            RuleElement[] pattern = this.getOrigin().getPattern();
            for (int i = 0; i < this.units.length; i++) {
                HostElement e = this.units[i];
                if (e instanceof HostNode) {
                    this.equivalentMap.putNode((RuleNode) pattern[i],
                        (HostNode) e);
                } else {
                    RuleEdge e1 = (RuleEdge) pattern[i];
                    HostEdge e2 = (HostEdge) e;
                    this.equivalentMap.putEdge(e1, e2);
                    this.equivalentMap.putNode(e1.source(), e2.source());
                    this.equivalentMap.putNode(e1.target(), e2.target());
                }
            }
            if (this.getValuation() != null) {
                this.equivalentMap.getValuation().putAll(this.getValuation());
            }
        }
        return this.equivalentMap;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[ " + this.getOrigin().getPattern().toString() + ": ");
        for (int i = 0; i < this.units.length; i++) {
            sb.append("[ " + this.units[i].toString() + "] ");
        }
        if ((this.valuation != null)) {
            sb.append(" |> " + this.valuation.toString());
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public AbstractReteMatch merge(ReteNetworkNode origin, AbstractReteMatch m,
            boolean copyLeftPrefix) {
        return ReteSimpleMatch.merge(origin, this, m, this.isInjective(),
            copyLeftPrefix);
    }

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
    public static ReteSimpleMatch merge(ReteNetworkNode origin,
            ReteSimpleMatch m1, AbstractReteMatch m2, boolean injective,
            boolean copyPrefix) {

        ReteSimpleMatch result = null;
        Valuation valuation = m1.mergeValuationsWith(m2);
        if (valuation != null) {
            result = new ReteSimpleMatch(origin, injective);
            HostElement[] units2 = m2.getAllUnits();
            if (copyPrefix) {
                result.specialPrefix =
                    (m1.specialPrefix != null) ? m1.specialPrefix : m1;
            }
            assert result.units.length == m1.units.length + units2.length;
            int i = 0;
            for (; i < m1.units.length; i++) {
                result.units[i] = m1.units[i];
            }

            for (; i < result.units.length; i++) {
                result.units[i] = units2[i - m1.units.length];
            }

            assert m1.hashCode != 0;
            result.refreshHashCode(m1.hashCode, m1.units.length);
            m1.getSuperMatches().add(result);
            m2.getSuperMatches().add(result);
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
    public static ReteSimpleMatch merge(ReteNetworkNode origin,
            AbstractReteMatch m1, AbstractReteMatch m2, boolean injective,
            boolean copyPrefix) {

        ReteSimpleMatch result = null;
        Valuation valuation = m1.mergeValuationsWith(m2);
        if (valuation != null) {
            HostElement[] m1Units = m1.getAllUnits();
            result = new ReteSimpleMatch(origin, injective);
            HostElement[] units2 = m2.getAllUnits();
            if (copyPrefix) {
                result.specialPrefix =
                    (m1.specialPrefix != null) ? m1.specialPrefix : m1;
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
            m1.getSuperMatches().add(result);
            m2.getSuperMatches().add(result);
            result.valuation = (valuation != emptyMap) ? valuation : null;
        }
        return result;
    }

    /**
     * Makes another <code>ReteSimpleMatch</code> object that contains the same
     * units as the units in this object.
     * 
     * The origin is set to the same as the origin of this object as reported
     * by {@link #getOrigin()}.
     * 
     * 
     * @param shallow if {@literal true} then the unit array of the source is reused
     *               otherwise a new array of the same size is created and the contents
     *               are copied.
     * @return A new {@link ReteSimpleMatch} object the content 
     *         of which is copied from the match object given in the 
     *         <code>source</code> parameter.
     */
    @Override
    protected AbstractReteMatch clone(boolean shallow) {
        ReteSimpleMatch result =
            new ReteSimpleMatch(this.getOrigin(), this.isInjective());
        if (shallow) {
            result.units = this.units;
        } else {
            result.units = new HostElement[this.units.length];
            for (int i = 0; i < result.units.length; i++) {
                result.units[i] = this.units[i];
            }
        }
        result.valuation =
            (shallow) ? this.valuation : (this.valuation != null)
                    ? new Valuation(this.valuation) : null;
        result.hashCode = this.hashCode;
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
    public static ReteSimpleMatch forge(ReteNetworkNode origin,
            boolean injective, AbstractReteMatch source) {
        ReteSimpleMatch result = new ReteSimpleMatch(origin, injective);
        result.specialPrefix = source.specialPrefix;
        assert (source.specialPrefix == null)
            || (origin.getPattern().length == source.specialPrefix.getOrigin().getPattern().length);
        result.units = source.getAllUnits();
        result.valuation = source.valuation;
        return result;
    }
}
