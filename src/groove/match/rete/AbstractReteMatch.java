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

import groove.graph.TypeLabel;
import groove.rel.LabelVar;
import groove.rel.VarMap;
import groove.trans.HostEdge;
import groove.trans.HostElement;
import groove.trans.HostFactory;
import groove.trans.HostNode;
import groove.trans.RuleEdge;
import groove.trans.RuleNode;
import groove.trans.RuleToHostMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * @author Arash Jalali
 * @version $Revision $
 */
public abstract class AbstractReteMatch implements
        Comparable<AbstractReteMatch>, VarMap {

    private boolean injective = false;

    /**
     * The origin determines the pattern (and the associated lookup table)
     * that this match is an instance of.
     */
    private ReteNetworkNode origin = null;

    /**
     * A special prefix match is one that whose units are 
     * identical to the initial units of this match (up to a certain point)
     * and we are interested in keeping this association
     * for certain reasons, including NAC inhibition tracking.     
     */
    protected AbstractReteMatch specialPrefix;

    private boolean deleted = false;

    private Collection<AbstractReteMatch> superMatches =
        new ArrayList<AbstractReteMatch>();
    private Collection<Collection<? extends AbstractReteMatch>> containerCollections =
        new ArrayList<Collection<? extends AbstractReteMatch>>();
    private List<DominoEventListener> dominoListeners =
        new ArrayList<DominoEventListener>();

    /**
     * Calculated hashCode. 0 means it is not yet calculated
     * due to lazy evaluation based on the constituting units
     */
    protected int hashCode = 0;

    /**
     * Basic constructor to be used by subclasses as basic initializer of
     * shared attributes.
     * 
     * @param origin The n-node that this match belongs to.
     * @param injective  determines if the match is being used in an injective engine instance.
     */
    public AbstractReteMatch(ReteNetworkNode origin, boolean injective) {
        this.injective = injective;
        this.origin = origin;
    }

    /**
     * @return <code>true</code> if this match is to be used in an injective
     * RETE engine instance, <code>false</code> otherwise.
     */
    public boolean isInjective() {
        return this.injective;
    }

    /**
     * @return The array of all the match elements, i.e. elements of 
     * the host graph that are part of this match.
     */
    public abstract HostElement[] getAllUnits();

    /**
     * @return The number of units in this match.
     */
    public abstract int size();

    /**
     * @return The n-node that this match originates from/is found by.
     */
    public ReteNetworkNode getOrigin() {
        return this.origin;
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
        HostElement[] units = this.getAllUnits();
        boolean result = true;
        for (Entry<RuleEdge,? extends HostEdge> m : anchorMap.edgeMap().entrySet()) {
            int i = lookup.getEdge(m.getKey());
            assert i != -1;
            if (!units[i].equals(m.getValue())) {
                result = false;
                break;
            }
        }
        if (result) {
            for (RuleNode n : anchorMap.nodeMap().keySet()) {
                int[] idx = lookup.getNode(n);
                assert idx != null;
                HostElement e = units[idx[0]];
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

            }
        }
        return result;
    }

    /**
     * Adds a collection to the list of container collections of this match
     * so that in case of deletion it would have them remove itself from them.
     * @param c The collection that is alleged to contain is match as well.
     */
    public void addContainerCollection(Collection<? extends AbstractReteMatch> c) {
        this.containerCollections.add(c);
    }

    /**
     * Removes a given collection from the ones this match resides in.
     * @param c The given collection.
     */
    public void removeContainerCollection(
            Collection<? extends AbstractReteMatch> c) {
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
     * @return The set of host-nodes of the match, i.e. nodes in the host graph
     * that this match covers.
     */
    public abstract Set<HostNode> getNodes();

    /**
     * 
     * @return The collection of matches made of combining this match 
     * and something else.
     */
    protected Collection<AbstractReteMatch> getSuperMatches() {
        return this.superMatches;
    }

    /**
     * This method is called whenever the match object is deleted through the domino
     * deletion process. This will cause the deletion to cascade through its associated
     * super-matches, i.e. matches that are partially made of this match object.
     *  
     * @param callerSubMatch The sub-match that has called this method.
     */
    public synchronized void dominoDelete(AbstractReteMatch callerSubMatch) {
        if (!this.isDeleted()) {
            this.markDeleted();
            for (AbstractReteMatch m : this.superMatches) {
                if (!m.isDeleted()) {
                    m.dominoDelete(this);
                }
            }
            this.superMatches = null;

            for (Collection<? extends AbstractReteMatch> c : this.containerCollections) {
                c.remove(this);
            }
            this.containerCollections.clear();

            for (DominoEventListener l : this.dominoListeners) {
                l.matchRemoved(this);
            }
            this.dominoListeners.clear();
        }
    }

    /**
     * Domino-deletes the super-matches of this match, leaving the current
     * match intact. 
     */
    public synchronized void dominoDeleteAfter() {
        for (AbstractReteMatch superMatch : this.superMatches) {
            superMatch.dominoDelete(this);
        }
        this.superMatches.clear();
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
     * Should be called when this object is to be categorically
     * reported as deleted by the {@link #isDeleted()} method 
     * from this point on.
     */
    protected void markDeleted() {
        this.deleted = true;
    }

    /**
     * Implementations of this method should make sure a proper
     * GROOVE compatible map object is created that corresponds 
     * with this RETE-specific match object. 
     * 
     * @param factory The factory that can create the right map type 
     * @return A translation of this match object to the {@link RuleToHostMap} representation  
     */
    public abstract RuleToHostMap toRuleToHostMap(HostFactory factory);

    /**
     * Creates a new match object that is the result of merging this match 
     * with a given match (the <code>m</code> parameter).
     * The semantics of the merge depends on the concrete implementation but
     * it is assumed that <code>m</code> will be merged from <i>right</i>,
     * whatever "right" means in the context of the concrete implementation.
     * 
     * Concrete implementations should specify under what circumstances
     * this method will fail to merge (and will return <code>null</code>.
     *  
     * @param origin The n-node the resulting match will be associated with
     * @param m The match object that is to be combined with this object
     * from "right".
     * @param copyLeftPrefix if <code>true</code> then the special prefix link of <code>this</code>
     *        (or <code>this</code> if it's prefix is null) will be copied to that of the result.
     * 
     * @return A new match object is the result of combining this and <code>m</code>,
     * in which <code>m</code> is added from the "right". The injectivity flag
     * of the returned object should be equal to that of the current object. 
     * If the method returns <code>null</code>, then it means
     * that merging has not been possible due to some sort of conflict.
    
     */
    public abstract AbstractReteMatch merge(ReteNetworkNode origin,
            AbstractReteMatch m, boolean copyLeftPrefix);

    /**
     * An empty valuation map.
     */
    protected static Map<LabelVar,TypeLabel> emptyMap =
        new HashMap<LabelVar,TypeLabel>();

    /**
     * Merges the variable valuation map of this match with a given match,
     * if they do not conflict with one-another.
     * 
     * If neither this nor the given match have valuation maps, the result 
     * will be an empty map.
     * @param m The given match
     * @return A new valuation map that is the result of consistent union of both,
     * <code>null</code> if there is a conflict.
     */
    protected Map<LabelVar,TypeLabel> mergeValuationsWith(AbstractReteMatch m) {
        Map<LabelVar,TypeLabel> result = null;
        Map<LabelVar,TypeLabel> v1 = this.getValuation();
        Map<LabelVar,TypeLabel> v2 = m.getValuation();
        if ((v1 != null) && (v2 != null)) {
            Map<LabelVar,TypeLabel> vSmall = (v1.size() < v2.size()) ? v1 : v2;
            Map<LabelVar,TypeLabel> vBig = (vSmall == v1) ? v2 : v1;
            result = new HashMap<LabelVar,TypeLabel>(v1.size() + v2.size());
            for (Entry<LabelVar,TypeLabel> e : vSmall.entrySet()) {
                if (!vBig.get(e.getKey()).equals(e.getValue())) {
                    result = null;
                    break;
                } else {
                    result.put(e.getKey(), e.getValue());
                }
            }
            if (result != null) {
                result.putAll(vBig);
            }
        } else if ((v1 != null) || (v2 != null)) {
            Map<LabelVar,TypeLabel> v = (v1 != null) ? v1 : v2;
            result = new HashMap<LabelVar,TypeLabel>(v);
        } else {
            result = emptyMap;
        }
        return result;
    }

    /**
     * Combines the variable bindings(valuation maps)
     * of a number of match objects into a new valuation map, provided
     * that the bindings do not contradict eachother. 
     *
     * 
     * @param matches An array
     * @return A new valuation map object or <code>null</code> if
     * there is a binding conflict, i.e. more than one value is bound
     * to the same variable. 
     */
    protected static Map<LabelVar,TypeLabel> mergeValuations(
            AbstractReteMatch[] matches) {
        Map<LabelVar,TypeLabel> result = emptyMap;
        for (int i = 0; (i < matches.length) && (result != null); i++) {
            Map<LabelVar,TypeLabel> v = matches[i].getValuation();
            if (v != null) {
                if (result == emptyMap) {
                    result = new HashMap<LabelVar,TypeLabel>();
                }
                for (Entry<LabelVar,TypeLabel> e : v.entrySet()) {
                    if (!result.get(e.getKey()).equals(e.getValue())) {
                        result = null;
                        break;
                    } else {
                        result.put(e.getKey(), e.getValue());
                    }
                }
            }
        }
        return result;
    }

    /**
     * Returns the special prefix match of this match object.
     * 
     * A special prefix match is one that the units of which are 
     * identical to the initial units of this match (up to a certain point)
     * and we are interested in keeping this association
     * for certain reasons, including NAC inhibition tracking.     
     */
    public AbstractReteMatch getSpecialPrefix() {
        return this.specialPrefix;
    }

    /**
     * Concrete implementations should create a clone copy of this current
     * object. 
     * The interpretation as to what a "shallow" copy means is left to
     * concrete implementations, but it should properly documented.
     * 
     * @param shallow determines if all reference data is properly cloned 
     * as well or some object references are just copied. 
     */
    protected abstract AbstractReteMatch clone(boolean shallow);

    @Override
    public synchronized int hashCode() {
        if (this.hashCode == 0) {
            refreshHashCode();
        }
        return this.hashCode;
    }

    /**
     * Recalculates the hash code given an initial pre-calculated value for a
     * given number of prefix match units.
     * 
     * @param initialHash The hash-code pre-calculated up to <code>initialIndex</code>-th
     *        match in the units.
     * @param initialIndex The number of initial match units that can be skipped
     */
    protected void refreshHashCode(int initialHash, int initialIndex) {
        this.hashCode = initialHash;
        int l = this.getAllUnits().length;
        HostElement[] theUnits = this.getAllUnits();
        for (int i = initialIndex; i < l; i++) {
            if (i > 0) {
                boolean neg = this.hashCode < 0;
                this.hashCode <<= 1;
                if (neg) {
                    this.hashCode |= 1;
                }
            }
            this.hashCode += theUnits[i].hashCode();
        }
    }

    /**
     * Completely recalculates the hash code from scratch.
     */
    protected void refreshHashCode() {
        refreshHashCode(0, 0);
    }

}
