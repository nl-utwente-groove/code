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

import groove.trans.HostEdge;
import groove.trans.HostElement;
import groove.trans.HostFactory;
import groove.trans.HostNode;
import groove.trans.RuleEdge;
import groove.trans.RuleElement;
import groove.trans.RuleNode;
import groove.trans.RuleToHostMap;
import groove.util.TreeHashSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @author Arash Jalali
 * @version $Revision $
 */
public class ReteMatch implements Comparable<ReteMatch> {
    /** Host graph elements. */
    private HostElement[] units;
    /**
     * This is the set of nodes (host nodes) in this match.
     * It is only of use in injective matching so it will be
     * filled lazily if needed by the <code>getNodes</code> method.
     */
    private Set<HostNode> nodes = null;
    /**
     * The origin determines the pattern (and the associated lookup table)
     * that this match is an instance of.
     */
    private ReteNetworkNode origin = null;

    /**
     * A special prefix match is one that whose units are 
     * identical to the initial units of this match (up to a certain point)
     * and we are interested in keeping this this association
     * for certain reasons, including NAC inhibition tracking.     
     */
    private ReteMatch specialPrefix;

    private Collection<ReteMatch> superMatches = new ArrayList<ReteMatch>();
    private Collection<Collection<ReteMatch>> containerCollections =
        new ArrayList<Collection<ReteMatch>>();
    private List<DominoEventListener> dominoListeners =
        new ArrayList<DominoEventListener>();
    private boolean deleted = false;

    private boolean injective = false;

    /**
     * Creates a new match object from a given sub-match copying all the units of the submatch.
     * @param origin The n-node this match is associated with.
     * @param injective Determines if this match is used in an engine with injective matching  
     * @param subMatch The sub-match to be used.
     */
    public ReteMatch(ReteNetworkNode origin, boolean injective,
            ReteMatch subMatch) {
        this(origin, injective);
        this.specialPrefix = subMatch.specialPrefix;
        subMatch.superMatches.add(this);
        assert origin.getPattern().length == subMatch.getOrigin().getPattern().length;
        this.units = subMatch.units;
    }

    /**
     * Creates an empty match
     * 
     * @param origin The n-node that this match belongs to.
     * @param injective  determines if the match is being used in an injective engine instance.
     */
    public ReteMatch(ReteNetworkNode origin, boolean injective) {
        this.injective = injective;
        this.origin = origin;
        this.units = new HostElement[origin.getPattern().length];
    }

    /**
     * Creates a singleton match consisting of one Edge match
     * @param origin The n-node to which this match belongs/is found by.
     * @param match The matched edge.
     * @param injective Determines if this is an injectively found match.
     */
    public ReteMatch(ReteNetworkNode origin, HostEdge match, boolean injective) {
        this(origin, injective);
        this.units[0] = match;
        this.hashCode = match.hashCode();
    }

    /**
     * Creates a singleton match consisting of one Node match
     * @param origin The n-node by which this match has been found.
     * @param match The graph node that has been found as a match
     * @param injective Determines if this is a match found by an 
     *        injective matcher.
     */
    public ReteMatch(ReteNetworkNode origin, HostNode match, boolean injective) {
        this(origin, injective);
        this.units[0] = match;
        this.hashCode = match.hashCode();
    }

    /**
     * @return The n-node that this match originates from/is found by.
     */
    public ReteNetworkNode getOrigin() {
        return this.origin;
    }

    /**
     * @return The reference to the prefix positive match that this match is a composite
     * (positive + negative) extension of. The return value will be <code>null</code>
     * if this match is not the left prefix of a composite (positive+negative) match. 
     */
    public ReteMatch getSpecialPrefix() {
        return this.specialPrefix;
    }

    /**
     * @return The array of all the match elements, i.e. elements of 
     * the host graph that are part of this match.
     */
    public HostElement[] getAllUnits() {
        return this.units;
    }

    /**
     * @return The number of units in this match.
     */
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

    /**
     * @return The set of host-nodes of the match, i.e. nodes in the host graph
     * that this match covers.
     */
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
     * @param e An edge in the pattern associated with the {@link #origin} of this
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
     * To check comparability the {@link #equals(ReteMatch)} method should be called.
     * 
     * @param m The match to which the current match object should be compared.
     * @return positive integer if this match is greater than <code>m</code>,
     * zero if the two have the exact same match-units in the exact same order, and
     * a negative integer if the this match is less than <code>m</code>.
     */
    public int compareTo(ReteMatch m) {
        HostElement[] thisList = this.getAllUnits();
        HostElement[] mList = m.getAllUnits();

        int result = this.hashCode - m.hashCode;
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

    private int hashCode = 0;

    @Override
    public synchronized int hashCode() {
        if (this.hashCode == 0) {
            refreshHashCode();
        }
        return this.hashCode;
    }

    private void refreshHashCode(int initialHash, int initialIndex) {
        this.hashCode = initialHash;
        for (int i = initialIndex; i < this.units.length; i++) {
            if (i > 0) {
                boolean neg = this.hashCode < 0;
                this.hashCode <<= 1;
                if (neg) {
                    this.hashCode |= 1;
                }
            }
            this.hashCode += this.units[i].hashCode();
        }
    }

    private void refreshHashCode() {
        refreshHashCode(0, 0);
    }

    /**
     * @param m A given match
     * @return <code>true</code> if this object is equal to <code>m</code>, i.e.
     * if they both refer to the same object or if they have the same origin,
     * and the exact array of elements in their array of {@link #units} with the exact
     * same order. Otherwise, <code>m</code> is considered unequal to the current object
     * and the return value will be <code>null</code>. 
     */
    public boolean equals(ReteMatch m) {
        boolean result;
        if ((m != null) && (this.origin == m.origin)
            && (this.hashCode() == m.hashCode())) {
            result = (m == this) || (this.compareToForEquality(m));
        } else {
            result = false;
        }
        return result;
    }

    private boolean compareToForEquality(ReteMatch m) {
        HostElement[] thisList = this.getAllUnits();
        HostElement[] mList = m.getAllUnits();
        boolean result = true;

        int thisSize = this.size();
        for (int i = 0; i < thisSize; i++) {
            if (thisList[i] != mList[i]) {
                result = false;
                break;
            }
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof ReteMatch) && this.equals((ReteMatch) o);
    }

    /**
     * Determines if a sub-match <code>m</code>'s units exist in the units
     * of this match beginning at a given index.
     *  
     * @param index The index at which the units of <code>m</code> should begin to correspond.
     * @param m The alleged sub-match at the given index.
     * @return <code>true</code> if it is contained, <code>false</code> otherwise.
     */
    public boolean isContainedAt(int index, ReteMatch m) {
        boolean result = true;
        int mSize = m.size();
        for (int i = 0; i < mSize; i++) {
            if (!this.units[i + index].equals(m.units[i])) {
                result = false;
                break;
            }
        }
        return result;
    }

    /**
     * Decides if this match is an extension of a given partial match.
     *  
     * @param anchorMap The partial match
     * @return <code>true</code> if the units in this match do 
     *         not contradict the given partial match in <code>anchorMap</code>
     */
    public boolean conformsWith(RuleToHostMap anchorMap) {
        LookupTable lookup = this.origin.getPatternLookupTable();
        boolean result = true;
        for (Entry<RuleEdge,? extends HostEdge> m : anchorMap.edgeMap().entrySet()) {
            int i = lookup.getEdge(m.getKey());
            if ((i == -1) || (!this.units[i].equals(m.getValue()))) {
                result = false;
                break;
            }
        }
        if (result) {
            for (RuleNode n : anchorMap.nodeMap().keySet()) {
                int[] idx = lookup.getNode(n);
                if (idx != null) {
                    HostElement e = this.units[idx[0]];
                    if (e instanceof HostNode) {
                        if (!e.equals(anchorMap.getNode(n))) {
                            result = false;
                            break;
                        }
                    } else {
                        HostNode n1 =
                            (idx[1] == 0) ? ((HostEdge) e).source()
                                    : ((HostEdge) e).target();
                        if (!n1.equals(anchorMap.getNode(n))) {
                            result = false;
                            break;
                        }
                    }
                } else {
                    result = false;
                    break;
                }

            }
        }
        return result;
    }

    /**
     * Checks if the intersection of two sets of nodes is empty.
     * 
     * @param s1 One set of nodes
     * @param s2 Another set of nodes.
     * @return <code>true</code> if the intersection of s1 and s2 is empty,<code>false</code>
     * otherwise.
     */
    public static boolean checkInjectiveOverlap(Set<HostNode> s1,
            Set<HostNode> s2) {
        boolean result = true;
        Set<HostNode> largerNodes = s1.size() > s2.size() ? s1 : s2;
        Set<HostNode> smallerNodes = (largerNodes == s1) ? s2 : s1;
        for (HostNode n : smallerNodes) {
            if (largerNodes.contains(n)) {
                result = false;
                break;
            }
        }
        return result;
    }

    /**
     * Combines two matches into one match. 
     * 
     * No conflict checking is performed. In other words, this method assumes
     * that merging the given sub-matches will result in a consistent bigger match.
     *   
     * @param origin The n-node that is to be set as the origin of the resulting merge.  
     * @param m1 The left match, the units of which will be at the beginning of the
     *           units of the merged match.
     * @param m2 The right match, the units of which will be at the end of the
     *           units of the merged match.
     * @param injective Specifies if this is an injectively found match. 
     * @param copyPrefix if {@literal true} then the special prefix link of m1 
     *        (or m1 if it's prefix is null) will be copied to that of the result.
     * @return A newly created match object containing the merge of m1 and m2
     * if m1 and m2 do not conflict, {@literal null} otherwise. 
     */
    public static ReteMatch merge(ReteNetworkNode origin, ReteMatch m1,
            ReteMatch m2, boolean injective, boolean copyPrefix) {
        ReteMatch result = new ReteMatch(origin, injective);
        if (copyPrefix) {
            result.specialPrefix =
                (m1.specialPrefix != null) ? m1.specialPrefix : m1;
        }
        assert result.units.length == m1.units.length + m2.units.length;
        int i = 0;
        for (; i < m1.units.length; i++) {
            result.units[i] = m1.units[i];
        }

        for (; i < result.units.length; i++) {
            result.units[i] = m2.units[i - m1.units.length];
        }

        assert m1.hashCode != 0;
        result.refreshHashCode(m1.hashCode, m1.units.length);
        m1.superMatches.add(result);
        m2.superMatches.add(result);
        return result;
    }

    /**
     * Merges two sub-matches into a bigger one. For more details see 
     * the documentation for {@link #merge(ReteNetworkNode, ReteMatch, ReteMatch, boolean, boolean)}
     *
     * @param origin The n-node that is to be set as the origin of the resulting merge.  
     * @param m1 The left match, the units of which will be at the beginning of the
     *           units of the merged match.
     * @param m2 The right match, the units of which will be at the end of the
     *           units of the merged match.
     * @param injective Specifies if this is an injectively found match. 
     * @return The resulting merged match.
     */
    public static ReteMatch merge(ReteNetworkNode origin, ReteMatch m1,
            ReteMatch m2, boolean injective) {
        return ReteMatch.merge(origin, m1, m2, injective, false);
    }

    /**
     * Merges an array matches into one match in the order appearing in the array. 
     * 
     * If the matches conflict, this method will fail. A conflict constitutes
     * violation of injectivity if the resulting merge is meant to be an
     * injective match.
     *   
     * @param origin The n-node that is to be set as the origin of the resulting merge.  
     * @param subMatches the array of sub-matches
     * @param injective Specifies if this is an injectively found match.      
     * @return A newly created match object containing the merge of all the subMatches
     * if they do not conflict, {@literal null} otherwise. 
     */
    public static ReteMatch merge(ReteNetworkNode origin,
            ReteMatch[] subMatches, boolean injective) {
        ReteMatch result = new ReteMatch(origin, injective);
        TreeHashSet<HostNode> nodes =
            (injective) ? new TreeHashSet<HostNode>() : null;

        int k = 0;
        for (int i = 0; i < subMatches.length; i++) {
            if (injective) {
                for (HostNode n : subMatches[i].getNodes()) {
                    if (nodes.put(n) != null) {
                        return null;
                    }
                }
            }
            for (int j = 0; j < subMatches[i].units.length; j++) {
                result.units[k++] = subMatches[i].units[j];
            }
            subMatches[i].superMatches.add(result);
        }
        assert k == origin.getPattern().length;

        assert subMatches[0].hashCode != 0;
        result.refreshHashCode(subMatches[0].hashCode,
            subMatches[0].units.length);
        return result;
    }

    /**
     * Makes another <code>ReteMatch</code> object that contains the same
     * units as the one indicated by the parameter <code>source</code> but whose
     * origin is set to a new value.
     * 
     * @param newOrigin The {@link ReteNetworkNode} object that is to be used as the
     *                  origin of the resulting match object.
     * @param source The match object from which the units are to be copied
     * @param naive  if {@literal true} then the unit array of the source is reused
     *               otherwise a new array of the same size is created and the contents
     *               are copied.
     * @return A new {@link ReteMatch} object the content (the match units)
     *         of which is copied from the match object given in the 
     *         <code>source</code> parameter.
     */
    public static ReteMatch copyContents(ReteNetworkNode newOrigin,
            ReteMatch source, boolean naive) {
        ReteMatch result = new ReteMatch(newOrigin, source.injective);
        if (naive) {
            result.units = source.units;
        } else {
            result.units = new HostElement[source.units.length];
            for (int i = 0; i < result.units.length; i++) {
                result.units[i] = source.units[i];
            }
        }
        result.hashCode = source.hashCode;
        return result;
    }

    /**
     * Determines if this match object is already marked as deleted through a 
     * domino process. 
     * 
     * This is necessary because the domino-deletion moves only 
     * forward and so if a match M is the result of the merge of two match M1 and M2,
     * the domino deletion of M1 will mark M as deleted, however since M2 is still
     * holding a reference to M as its super-match, then it is important for M2
     * to know upon M2's deletion (possibly at some later time) that M is already 
     * deleted, so that it won't have to follow the domino thread
     * originating from M twice.
     * 
     * @return <code>true</code> if this match object is already domino-deleted,
     * <code>false</code> otherwise.
     */
    public boolean isDeleted() {
        return this.deleted;
    }

    /**
     * Adds a collection to the list of container collections of this match
     * so that in case of deletion it would have them remove itself from them.
     * @param c The collection that is alleged to contain is match as well.
     */
    public void addContainerCollection(Collection<ReteMatch> c) {
        this.containerCollections.add(c);
    }

    /**
     * Removes a given collection from the ones this match resides in.
     * @param c The given collection.
     */
    public void removeContainerCollection(Collection<ReteMatch> c) {
        this.containerCollections.remove(c);
    }

    /**
     * Adds a listener to the list of {@link DominoEventListener} objects
     * that will be notified when this match object is deleted through 
     * a domino-deletion process.
     * @param listener The object to be added to the list of listeners.
     */
    public void addDominoListener(DominoEventListener listener) {
        this.dominoListeners.add(listener);
    }

    /**
     * This method is called whenever the match object is deleted through the domino
     * deletion process. This will cause the deletion to cascade through its associated
     * super-matches, i.e. matches that are partially made of this match object.
     *  
     * @param callerSubMatch The sub-match that has called this method.
     */
    public synchronized void dominoDelete(ReteMatch callerSubMatch) {
        if (!this.deleted) {
            this.deleted = true;
            for (ReteMatch m : this.superMatches) {
                if (!m.isDeleted()) {
                    m.dominoDelete(this);
                }
            }
            this.superMatches = null;

            for (Collection<ReteMatch> c : this.containerCollections) {
                c.remove(this);
            }
            this.containerCollections.clear();

            for (DominoEventListener l : this.dominoListeners) {
                l.matchRemoved(this);
            }
            this.dominoListeners.clear();
        }
    }

    private RuleToHostMap equivalentMap = null;

    /**
     * Translates this match object, which is only used inside the RETE network,
     * to an instance of {@link RuleToHostMap} that is the standard representation 
     * of any matching between a rule's nodes and edges to those of a host graph 
     * in GROOVE.
     * 
     * @return A translation of this match object to the {@link RuleToHostMap} representation  
     */
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
        }
        return this.equivalentMap;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[ " + this.origin.getPattern().toString() + ": ");
        for (int i = 0; i < this.units.length; i++) {
            sb.append("[ " + this.units[i].toString() + "] ");
        }
        sb.append("]");
        return sb.toString();
    }
}
