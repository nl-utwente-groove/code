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

import groove.grammar.host.HostEdge;
import groove.grammar.host.HostNode;
import groove.grammar.host.HostNodeSet;
import groove.grammar.rule.LabelVar;
import groove.grammar.rule.RuleEdge;
import groove.grammar.rule.RuleNode;
import groove.grammar.rule.RuleToHostMap;
import groove.grammar.rule.Valuation;
import groove.grammar.rule.VarMap;
import groove.grammar.type.TypeElement;
import groove.graph.Node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

/**
 * @author Arash Jalali
 * @version $Revision $
 */
public abstract class AbstractReteMatch implements VarMap {

    private final boolean injective;

    /**
     * The origin determines the pattern (and the associated lookup table)
     * that this match is an instance of.
     */
    private final ReteNetworkNode origin;

    /**
     * A special prefix match is one that whose units are 
     * identical to the initial units of this match (up to a certain point)
     * and we are interested in keeping this association
     * for certain reasons, including NAC inhibition tracking.     
     */
    protected AbstractReteMatch specialPrefix;

    private boolean deleted = false;

    private final List<AbstractReteMatch> superMatches =
        new ArrayList<>();
    /**
     * These are the matches that have participated in
     * building this one. We need these
     * to remove a domino-deleted match
     * from the list of <code>superMatches</codes>
     * of those who have not participated in a domino delete.
     */
    private final List<AbstractReteMatch> subMatches =
        new ArrayList<>();

    private final Collection<Collection<? extends AbstractReteMatch>> containerCollections =
        new ArrayList<>();
    private final List<DominoEventListener> dominoListeners =
        new ArrayList<>();

    /**
     * Variable bindings
     */
    protected Valuation valuation = null;

    /**
     * Calculated hashCode. 0 means it is not yet calculated
     * due to lazy evaluation based on the constituting units
     */
    private int hashCode = 0;

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
    public abstract Object[] getAllUnits();

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
        Object[] units = this.getAllUnits();
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
            for (Entry<RuleNode,? extends HostNode> n : anchorMap.nodeMap().entrySet()) {
                LookupEntry idx = lookup.getNode(n.getKey());
                if (idx != null) {
                    Node actual = idx.lookup(units);
                    if (!actual.equals(n.getValue())) {
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
     * Removes a listener from the list of {@link DominoEventListener} objects
     * that will be notified when this match object is deleted through 
     * a domino-deletion process.
     * @param listener The object to be removed from the list of listeners.
     */
    public void removeDominoListener(DominoEventListener listener) {
        this.dominoListeners.remove(listener);
    }

    /**
     * Checks if the intersection of two sets of nodes is empty.
     * 
     * @param s1 One set of nodes
     * @param s2 Another set of nodes.
     * @return <code>true</code> if the intersection of s1 and s2 is empty,<code>false</code>
     * otherwise.
     */
    public static boolean checkInjectiveOverlap(HostNodeSet s1, HostNodeSet s2) {
        boolean result = true;
        HostNodeSet largerNodes = s1.size() > s2.size() ? s1 : s2;
        HostNodeSet smallerNodes = (largerNodes == s1) ? s2 : s1;
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
    public abstract HostNodeSet getNodes();

    /**
     * Adds a match to the list of supermatches for this one
     * and adds itself to the list of submatches of
     * the given supermatch as well.  
     */
    public void addSuperMatch(AbstractReteMatch theSuperMatch) {
        this.superMatches.add(theSuperMatch);
        theSuperMatch.subMatches.add(this);

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
            for (AbstractReteMatch m : this.subMatches) {
                if ((!m.isDeleted()) && (m != callerSubMatch)) {
                    m.superMatches.remove(this);
                }
            }
            this.subMatches.clear();
            for (AbstractReteMatch m : this.superMatches) {
                if (!m.isDeleted()) {
                    m.dominoDelete(this);
                }
            }
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
    /*
    public abstract AbstractReteMatch merge(ReteNetworkNode origin,
            AbstractReteMatch m, boolean copyLeftPrefix);
    */
    /**
     * An empty valuation map.
     */
    protected static Valuation emptyMap = new Valuation();

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
    protected Valuation getMergedValuation(AbstractReteMatch m) {
        Valuation result;
        if (this.valuation == null) {
            result = m.valuation == null ? emptyMap : m.valuation;
        } else {
            result = this.valuation.getMerger(m.valuation);
        }
        return result;
    }

    /**
     * Combines the variable bindings(valuation maps)
     * of a number of match objects into a new valuation map, provided
     * that the bindings do not contradict each other. 
     *
     * 
     * @param matches An array
     * @return A new valuation map object or <code>null</code> if
     * there is a binding conflict, i.e. more than one value is bound
     * to the same variable. 
     */
    protected static Valuation getMergedValuation(AbstractReteMatch[] matches) {
        Valuation result = emptyMap;
        for (int i = 0; (i < matches.length) && (result != null); i++) {
            Valuation v = matches[i].getValuation();
            result = result.getMerger(v);
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

    @Override
    final public synchronized int hashCode() {
        if (this.hashCode == 0) {
            int result = computeHashCode();
            this.hashCode = result == 0 ? 1 : result;
        }
        return this.hashCode;
    }

    /**
     * Completely recalculates the hash code from scratch.
     */
    protected int computeHashCode() {
        int prime = 31;
        int result = getOrigin().hashCode();
        Object[] units = getAllUnits();
        int length = units.length;
        for (int i = 0; i < length; i++) {
            Object unit = units[i];
            if (unit != this) {
                result = prime * result + unit.hashCode();
            }
        }
        return result;
    }

    @Override
    public Valuation getValuation() {
        return this.valuation;
    }

    @Override
    public TypeElement getVar(LabelVar var) {
        return this.valuation.get(var);
    }

    @Override
    public void putAllVar(Valuation valuation) {
        this.valuation.putAll(valuation);
    }

    @Override
    public TypeElement putVar(LabelVar var, TypeElement value) {
        return this.valuation.put(var, value);
    }
}
