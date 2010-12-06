/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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

import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.Node;
import groove.rel.VarNodeEdgeLinkedHashMap;
import groove.rel.RuleToStateMap;
import groove.trans.RuleEdge;
import groove.trans.RuleNode;
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

    private Element[] units;
    /**
     * This is the set of nodes (host nodes) in this match.
     * It is only of use in injective matching so it will be
     * filled lazily if needed by the <code>getNodes</code> method.
     */
    private Set<Node> nodes = null;
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

    public ReteMatch(ReteNetworkNode origin, boolean injective,
            ReteMatch subMatch) {
        this(origin, injective);
        this.specialPrefix = subMatch.specialPrefix;
        subMatch.superMatches.add(this);
        assert origin.getPattern().length == subMatch.getOrigin().getPattern().length;
        Element[] smUnits = subMatch.getAllUnits();
        this.units = subMatch.units;
    }

    public ReteMatch(ReteNetworkNode origin, boolean injective) {
        this.injective = injective;
        this.origin = origin;
        this.units = new Element[origin.getPattern().length];
    }

    /**
     * Creates a singleton match consisting of one Edge match
     * @param origin
     * @param match
     * @param injective
     */
    public ReteMatch(ReteNetworkNode origin, Edge match, boolean injective) {
        this(origin, injective);
        this.units[0] = match;
        this.hashCode = match.hashCode();
    }

    /**
     * Creates a singleton match consisting of one Node match
     * @param origin
     * @param match
     * @param injective
     */
    public ReteMatch(ReteNetworkNode origin, Node match, boolean injective) {
        this(origin, injective);
        this.units[0] = match;
        this.hashCode = match.hashCode();
    }

    public ReteNetworkNode getOrigin() {
        return this.origin;
    }

    public ReteMatch getSpecialPrefix() {
        return this.specialPrefix;
    }

    public Element[] getAllUnits() {
        return this.units;
    }

    /**
     * @return The number of units in this match.
     */
    public int size() {
        return this.units.length;
    }

    /**
     * @param n
     * @return
     */
    public Node getNode(Node n) {
        int[] index = this.getOrigin().getPatternLookupTable().getNode(n);
        Node result = lookupNode(index);
        return result;
    }

    private Node lookupNode(int[] index) {
        Node result = null;
        if ((index != null) && (index[0] >= 0)) {
            result =
                (index[1] != -1) ? ((index[1] == 0)
                        ? ((Edge) this.units[index[0]]).source()
                        : ((Edge) this.units[index[0]]).target())
                        : (Node) this.units[index[0]];
        }
        return result;
    }

    public Set<Node> getNodes() {
        if (this.nodes == null) {
            this.nodes = new TreeHashSet<Node>();
            for (int i = 0; i < this.units.length; i++) {
                if (this.units[i] instanceof Edge) {
                    Edge e = (Edge) this.units[i];
                    this.nodes.add(e.source());
                    if (!e.source().equals(e.target())) {
                        this.nodes.add(e.target());
                    }
                } else {
                    this.nodes.add((Node) this.units[i]);
                }
            }
        }
        return this.nodes;
    }

    /**
     * @param e
     * @return the Edge to which <code>e</code> is mapped, <code>null</code>
     * otherwise.
     */
    public Edge getEdge(Edge e) {
        int index = this.getOrigin().getPatternLookupTable().getEdge(e);
        return (index != -1) ? (Edge) this.units[index] : null;
    }

    /**
     * Compares this match to an allegedly-comparable match. It does not really
     * check for comparability, i.e. to see that the two have the same actually
     * originate from the same pattern.
     * 
     * To check comparability the {@link #equals(ReteMatch)} method should be called.
     * 
     * @param m
     * @return positive integer if this match is greater than <code>m</code>,
     * zero if the two have the exact same match-units in the exact same order, and
     * a negative integer if the this match is less than <code>m</code>.
     */
    public int compareTo(ReteMatch m) {
        Element[] thisList = this.getAllUnits();
        Element[] mList = m.getAllUnits();

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

    public boolean equals(ReteMatch m) {
        boolean result;
        if ((this.origin == m.origin) && (this.hashCode() == m.hashCode())) {
            result =
                (m == this) || ((m != null) && (this.compareToForEquality(m)));
        } else {
            result = false;
        }
        return result;
    }

    public boolean compareToForEquality(ReteMatch m) {
        Element[] thisList = this.getAllUnits();
        Element[] mList = m.getAllUnits();
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
     * Determines if a submatch <code>m</code>'s units exist in this units
     * of this match beginning at a given index.
     *  
     * @param index
     * @param m
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

    public boolean conformsWith(RuleToStateMap anchorMap) {
        LookupTable lookup = this.origin.getPatternLookupTable();
        boolean result = true;
        for (Entry<RuleEdge,Edge> m : anchorMap.edgeMap().entrySet()) {
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
                    Element e = this.units[idx[0]];
                    if (e instanceof Node) {
                        if (!e.equals(anchorMap.getNode(n))) {
                            result = false;
                            break;
                        }
                    } else {
                        Node n1 =
                            (idx[1] == 0) ? ((Edge) e).source()
                                    : ((Edge) e).target();
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
     * @param s1
     * @param s2
     * @return <code>true</code> if the intersection of s1 and s2 is empty,<code>false</code>
     * otherwise.
     */
    public static boolean checkInjectiveOverlap(Set<Node> s1, Set<Node> s2) {
        boolean result = true;
        Set<Node> largerNodes = s1.size() > s2.size() ? s1 : s2;
        Set<Node> smallerNodes = (largerNodes == s1) ? s2 : s1;
        for (Node n : smallerNodes) {
            if (largerNodes.contains(n)) {
                result = false;
                break;
            }
        }
        return result;
    }

    /**
     * Merges two matches into one match. 
     * 
     * If the matches conflict, this method will fail. A conflict constitutes
     * violation of injectivity if the resulting merge is meant to be an
     * injective match.
     *   
     * @param origin  
     * @param m1
     * @param m2
     * @param injective 
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
     * @param origin  
     * @param subMatches
     * @param injective 
     * @return A newly created match object containing the merge of all the subMatches
     * if they do not conflict, {@literal null} otherwise. 
     */
    public static ReteMatch merge(ReteNetworkNode origin,
            ReteMatch[] subMatches, boolean injective) {
        ReteMatch result = new ReteMatch(origin, injective);
        TreeHashSet<Node> nodes = (injective) ? new TreeHashSet<Node>() : null;

        int k = 0;
        for (int i = 0; i < subMatches.length; i++) {
            if (injective) {
                for (Node n : subMatches[i].getNodes()) {
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
     * units as the one indicated by the parameter <code>source</code>.
     * 
     * @param newOrigin
     * @param source
     * @param naive  if {@literal true} then the unit array of the source is reused
     *               otherwise a new array of the same size is created and the contents
     *               are copied.
     * @return
     */
    public static ReteMatch copyContents(ReteNetworkNode newOrigin,
            ReteMatch source, boolean naive) {
        ReteMatch result = new ReteMatch(newOrigin, source.injective);
        if (naive) {
            result.units = source.units;
        } else {
            result.units = new Element[source.units.length];
            for (int i = 0; i < result.units.length; i++) {
                result.units[i] = source.units[i];
            }
        }
        result.hashCode = source.hashCode;
        return result;
    }

    public boolean isDeleted() {
        return this.deleted;
    }

    /**
     * Adds a collection to the list of container collections of this match
     * so that in case of deletion it would have them remove itself from them.
     * @param c
     */
    public void addContainerCollection(Collection<ReteMatch> c) {
        this.containerCollections.add(c);
    }

    public void addDominoListener(DominoEventListener listener) {
        this.dominoListeners.add(listener);
    }

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

    private void removeSuperMatch(ReteMatch m) {
        this.superMatches.remove(m);
    }

    private RuleToStateMap equivalentMap = null;

    public RuleToStateMap toVarNodeEdgeMap() {
        if (this.equivalentMap == null) {
            this.equivalentMap = new VarNodeEdgeLinkedHashMap();

            Element[] pattern = this.getOrigin().getPattern();
            for (int i = 0; i < this.units.length; i++) {
                Element e = this.units[i];
                if (e instanceof Node) {
                    this.equivalentMap.putNode((RuleNode) pattern[i], (Node) e);
                } else if (e instanceof Edge) {
                    RuleEdge e1 = (RuleEdge) pattern[i];
                    Edge e2 = (Edge) e;
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
