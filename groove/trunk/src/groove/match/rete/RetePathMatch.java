/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
import groove.trans.HostEdge;
import groove.trans.HostElement;
import groove.trans.HostFactory;
import groove.trans.HostNode;
import groove.trans.RuleElement;
import groove.trans.RuleNode;
import groove.trans.RuleToHostMap;
import groove.util.TreeHashSet;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Arash Jalali
 * @version $Revision $
 */
public class RetePathMatch extends AbstractReteMatch {

    /**
     * Determines the length of the path (number of edges)
     * represented by this match.
     */
    private int pathLength = 0;

    /*
     * Array consisting of the start
     * and end nodes of the path at indices 0, and 1
     * respectively.
     *
     */
    private HostNode[] units = null;

    /**
     * Lazily evaluated set of nodes returned by the method
     * {@link #getNodes()}.
     * 
     * Warning: The lazy evaluation is not thread-safe.
     */
    private Set<HostNode> nodes = null;

    /**
     * For single-edge path matches this variable holds 
     * a reference to the associated host edge for equality checking. 
     */
    private HostEdge associatedEdge = null;

    private Map<LabelVar,TypeLabel> variableMappings = null;

    private RetePathMatch(ReteNetworkNode origin) {
        super(origin, false);
    }

    /**
     * @param origin The regular-expression path checker node generating this  
     */
    public RetePathMatch(ReteNetworkNode origin, HostEdge edge) {
        super(origin, false);
        this.hashCode = edge.hashCode();
        this.pathLength = 1;
        this.units = new HostNode[] {edge.source(), edge.target()};
        this.associatedEdge = edge;
    }

    /**
     * Creates a new path match object from a given path match object
     * replacing the origin. 
     * 
     * This constructor is particularly useful
     * when creating a new match through the choice operator of regular expressions.
     * 
     * @param origin The new origin
     * @param subMatch The given path match based on which a new one is to be created.
     */
    public RetePathMatch(ReteNetworkNode origin, RetePathMatch subMatch) {
        super(origin, false);
        this.units = subMatch.units;
        this.pathLength = subMatch.pathLength;
        this.variableMappings = subMatch.variableMappings;
        this.associatedEdge = subMatch.associatedEdge;
        subMatch.getSuperMatches().add(this);
    }

    @Override
    public HostElement[] getAllUnits() {
        return this.units;
    }

    @Override
    public Set<HostNode> getNodes() {
        assert (this.units != null) && (this.units.length == 2);
        if (this.nodes == null) {
            this.nodes = new TreeHashSet<HostNode>();
            this.nodes.add(this.units[0]);
            this.nodes.add(this.units[1]);
        }
        return null;
    }

    @Override
    public int hashCode() {
        assert this.units != null;
        if (this.hashCode == 0) {
            this.hashCode = this.units[0].hashCode();
            boolean neg = this.hashCode < 0;
            this.hashCode <<= 1;
            if (neg) {
                this.hashCode |= 1;
            }
            this.hashCode += this.units[1].hashCode();
        }
        return this.hashCode;
    }

    @Override
    public int size() {
        return 2;
    }

    private RuleToHostMap equivalentMap = null;

    @Override
    public RuleToHostMap toRuleToHostMap(HostFactory factory) {
        if (this.equivalentMap == null) {
            this.equivalentMap = factory.createRuleToHostMap();

            RuleElement[] pattern = this.getOrigin().getPattern();
            for (int i = 0; i < this.units.length; i++) {
                HostNode n = this.units[i];
                this.equivalentMap.putNode((RuleNode) pattern[i], n);
            }
        }
        return this.equivalentMap;
    }

    @Override
    public int compareTo(AbstractReteMatch o) {

        return (o instanceof RetePathMatch) ? compareTo(o) : -1;
    }

    /**
     * Compares this instance with an instance of the {@link RetePathMatch} class.
     */
    public int compareTo(RetePathMatch m) {
        HostNode[] thisList = (HostNode[]) this.getAllUnits();
        HostNode[] mList = (HostNode[]) m.getAllUnits();

        int result = this.hashCode() - m.hashCode();
        if (result == 0) {
            int thisSize = this.size();
            for (int i = 0; (i < thisSize) && (result == 0); i++) {
                result = thisList[i].compareTo(mList[i]);
            }
            result = thisList[0].compareTo(mList[0]);
            if (result == 0) {
                result = thisList[1].compareTo(mList[1]);
            }
        }
        return result;
    }

    /**
     * 
     * @return The length of the path (in number of edges) 
     * represented by this match.
     */
    public int getPathLength() {
        return this.pathLength;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof RetePathMatch) && this.equals((RetePathMatch) o);
    }

    /**
     * Two RetePathMatch objects are equal if they are the same object or
     * they are single-path matches generated by the same n-node and representing the same edge 
     */
    public boolean equals(RetePathMatch m) {
        return (this == m)
            || ((this.pathLength * m.pathLength == 1) && (this.associatedEdge.equals(m.associatedEdge) && this.getOrigin().equals(
                m.getOrigin())));
    }

    @Override
    public Map<LabelVar,TypeLabel> getValuation() {
        return this.variableMappings;
    }

    @Override
    public TypeLabel getVar(LabelVar var) {
        return this.variableMappings.get(var);
    }

    @Override
    public void putAllVar(Map<LabelVar,TypeLabel> valuation) {
        this.variableMappings.putAll(valuation);
    }

    @Override
    public TypeLabel putVar(LabelVar var, TypeLabel value) {
        return this.variableMappings.put(var, value);
    }

    /**
     * Concatenates this match object with another path match object.
     * 
     * This will yield a result if the destination of this match
     * object is equal to the source of the given path match and if
     * valuation-maps do not conflict with another.
     * 
     * @param m The given match object which is expected to be a {@link RetePathMatch}
     * object.
     *  
     * @return The concatenation of <code>this</code> and <code>m</code> if
     * there is no conflict, <code>null</code> if there is a conflict or if
     * the end points do not overlap or if m is not an instance of 
     * <code>RetePathMatch</code>.
     */
    @Override
    public AbstractReteMatch merge(ReteNetworkNode origin, AbstractReteMatch m,
            boolean copyPrefix) {
        assert m instanceof RetePathMatch;
        RetePathMatch result = null;
        HostElement[] mUnits = m.getAllUnits();
        if (this.units[1].equals(mUnits[0])) {
            Map<LabelVar,TypeLabel> valuation = this.mergeValuationsWith(m);
            if (valuation != null) {
                result = new RetePathMatch(origin);
                if (copyPrefix) {
                    result.specialPrefix =
                        (m.specialPrefix != null) ? m.specialPrefix : m;
                }
                result.units =
                    new HostNode[] {this.units[0], (HostNode) mUnits[1]};
                result.pathLength =
                    this.pathLength + ((RetePathMatch) m).pathLength;
                result.variableMappings =
                    (valuation != emptyMap) ? valuation : null;
                hashCode();
                this.getSuperMatches().add(result);
                m.getSuperMatches().add(result);
            }
        }
        return result;
    }

    /**
     * Creates a new path match that is the inverse of a given match object, that is,
     * a path match in which the start and end points are the reversed of the given object.
     * 
     * @param origin The RETE n-node with which the result should be associated.
     * @param m The path match object to be inverted.   
     * @return The inverted path match. 
     */
    public static RetePathMatch inverse(ReteNetworkNode origin, RetePathMatch m) {
        RetePathMatch result = new RetePathMatch(origin);
        result.units = new HostNode[] {m.units[1], m.units[0]};
        result.pathLength = m.pathLength;
        result.variableMappings = m.variableMappings;
        result.hashCode(); //refresh hash code
        m.getSuperMatches().add(result);
        return result;
    }

    @Override
    protected AbstractReteMatch clone(boolean shallow) {
        RetePathMatch result = new RetePathMatch(this.getOrigin());
        result.units =
            (shallow) ? this.units : new HostNode[] {this.units[0],
                this.units[1]};
        result.associatedEdge = this.associatedEdge;
        result.pathLength = this.pathLength;
        result.hashCode = this.hashCode;

        result.variableMappings =
            (shallow) ? this.variableMappings : (this.variableMappings != null)
                    ? new HashMap<LabelVar,TypeLabel>(this.variableMappings)
                    : null;
        return result;
    }

    /**
     * @return The start node of the path associated with this match object
     */
    public HostNode start() {
        assert (this.units != null) && (this.units.length >= 1);
        return this.units[0];
    }

    /**
     * @return The end node of the path associated with this match object
     */
    public HostNode end() {
        assert (this.units != null) && (this.units.length == 2);
        return this.units[1];
    }
}
