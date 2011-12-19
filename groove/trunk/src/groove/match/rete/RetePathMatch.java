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

import groove.rel.Valuation;
import groove.trans.HostEdge;
import groove.trans.HostNode;
import groove.trans.RuleToHostMap;
import groove.util.TreeHashSet;

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
    protected int pathLength = 0;

    /**
     * Array consisting of the start
     * and end nodes of the path at indices 0, and 1
     * respectively.
     */
    protected HostNode[] endpoints = null;

    /**
     * Lazily evaluated set of nodes returned by the method
     * {@link #getNodes()}.
     * 
     * Warning: The lazy evaluation is not thread-safe.
     */
    protected Set<HostNode> nodes = null;

    protected Set<HostNode> participatingNodes = new TreeHashSet<HostNode>();

    /**
     * For single-edge path matches this variable holds 
     * a reference to the associated host edge for equality checking. 
     */
    protected HostEdge associatedEdge = null;

    private RetePathMatch(ReteNetworkNode origin) {
        super(origin, false);
        this.valuation = new Valuation();
    }

    /**
     * @param origin The regular-expression path checker node generating this  
     */
    public RetePathMatch(ReteNetworkNode origin, HostEdge edge) {
        super(origin, false);
        this.hashCode = edge.hashCode();
        this.pathLength = 1;
        this.endpoints = new HostNode[] {edge.source(), edge.target()};
        this.participatingNodes.add(edge.source());
        this.participatingNodes.add(edge.target());
        this.associatedEdge = edge;
        this.valuation = new Valuation();
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
    protected RetePathMatch(ReteNetworkNode origin, RetePathMatch subMatch) {
        super(origin, false);
        this.endpoints = subMatch.endpoints;
        this.pathLength = subMatch.pathLength;
        this.valuation = subMatch.valuation;
        this.associatedEdge = subMatch.associatedEdge;
        this.participatingNodes = subMatch.participatingNodes;
        subMatch.getSuperMatches().add(this);
    }

    /**
     * Creates a new path match object from this object
     * replacing the origin. 
     * 
     * @param newOrigin The new origin
     */
    public RetePathMatch reoriginate(ReteNetworkNode newOrigin) {
        return new RetePathMatch(newOrigin, this);
    }

    Object[] unitsToReport = null;

    @Override
    public Object[] getAllUnits() {
        if (this.unitsToReport == null) {
            this.unitsToReport = new Object[] {this};
        }
        return this.unitsToReport;
    }

    @Override
    public Set<HostNode> getNodes() {
        assert (this.endpoints != null) && (this.endpoints.length == 2);
        if (this.nodes == null) {
            this.nodes = new TreeHashSet<HostNode>();
            this.nodes.add(this.endpoints[0]);
            this.nodes.add(this.endpoints[1]);
        }
        return this.nodes;
    }

    /**
     * @return The nodes participating in this path
     */
    public Set<HostNode> getParticipatingNodes() {
        return this.participatingNodes;
    }

    /**
     * @return Utility method for {@link #getParticipatingNodes().size()}
     */
    public int getNodeCount() {
        return this.participatingNodes.size();
    }

    @Override
    public int hashCode() {
        assert this.endpoints != null;
        if (this.hashCode == 0) {
            this.hashCode = this.endpoints[0].hashCode();
            boolean neg = this.hashCode < 0;
            this.hashCode <<= 1;
            if (neg) {
                this.hashCode |= 1;
            }
            this.hashCode += this.endpoints[1].hashCode();
        }
        return this.hashCode;
    }

    @Override
    public int size() {
        return 2;
    }

    private RuleToHostMap equivalentMap = null;

    /**
     * Compares this instance with an instance of the {@link RetePathMatch} class.
     */
    public int compareTo(RetePathMatch m) {
        HostNode[] thisList = this.endpoints;
        HostNode[] mList = m.endpoints;

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
            || ((this.pathLength * m.pathLength == 1)
                && (this.associatedEdge != null) && (this.associatedEdge.equals(m.associatedEdge) && this.getOrigin().equals(
                m.getOrigin())));
    }

    public boolean equivalentWith(RetePathMatch m) {
        return this.equals(m)
            || ((m != null) && this.start() == m.start()
                && this.end() == m.end() && this.valuation.equals(m.valuation));
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
    public RetePathMatch concatenate(ReteNetworkNode origin, RetePathMatch m,
            boolean copyPrefix) {
        RetePathMatch result = null;
        HostNode[] mEndpoints = m.endpoints;
        if (this.endpoints[1].equals(mEndpoints[0])) {
            Valuation valuation = this.mergeValuationsWith(m);
            if (valuation != null) {
                result = new RetePathMatch(origin);
                if (copyPrefix) {
                    result.specialPrefix =
                        (m.specialPrefix != null) ? m.specialPrefix : m;
                }
                result.endpoints =
                    new HostNode[] {this.endpoints[0], mEndpoints[1]};
                result.pathLength = this.pathLength + (m).pathLength;
                result.valuation = (valuation != emptyMap) ? valuation : null;
                result.participatingNodes.addAll(this.participatingNodes);
                result.participatingNodes.addAll(m.participatingNodes);
                hashCode();
                this.getSuperMatches().add(result);
                m.getSuperMatches().add(result);
            }
        }
        return result;
    }

    /**
     * Creates a new path match that is the inverse of a this match object, that is,
     * a path match in which the start and end points are the reversed of the given object.
     * 
     * @param origin The RETE n-node with which the result should be associated.   
     * @return The inverted path match. 
     */
    public RetePathMatch inverse(ReteNetworkNode origin) {
        RetePathMatch result = new RetePathMatch(origin);
        result.endpoints =
            new HostNode[] {this.endpoints[1], this.endpoints[0]};
        result.pathLength = this.pathLength;
        result.valuation = this.valuation;
        result.participatingNodes = this.participatingNodes;
        result.hashCode(); //refresh hash code
        this.getSuperMatches().add(result);
        return result;
    }

    /**
     * @return The start node of the path associated with this match object
     */
    public HostNode start() {
        assert (this.endpoints != null) && (this.endpoints.length >= 1);
        return this.endpoints[0];
    }

    /**
     * @return The end node of the path associated with this match object
     */
    public HostNode end() {
        assert (this.endpoints != null) && (this.endpoints.length == 2);
        return this.endpoints[1];
    }

    /**
     * @return <code>true</code> if this is an empty path match,
     * <code>false</code> otherwise.
     */
    public boolean isEmpty() {
        return false;
    }

    @Override
    public String toString() {
        return String.format(
            "Path from %s to %s (l= %d) match %s |> %s",
            this.start().toString(),
            this.end().toString(),
            this.pathLength,
            ((AbstractPathChecker) this.getOrigin()).getExpression().toString(),
            this.valuation.toString());
    }

    /**
     * Creates a duplicate of a given path match. This
     * duplicate is used by a path-checker's match cache.
     * @param m
     * @return A path match that is the replica of <code>m</code>
     * except for the domino history. Its domino history is empty
     */
    public static RetePathMatch duplicate(RetePathMatch m) {
        RetePathMatch result = new RetePathMatch(m.getOrigin());
        result.endpoints = m.endpoints;
        result.associatedEdge = m.associatedEdge;
        result.nodes = m.nodes;
        result.participatingNodes = m.participatingNodes;
        result.pathLength = m.pathLength;
        result.specialPrefix = m.specialPrefix;
        result.valuation = m.valuation;
        result.hashCode = m.hashCode;
        return result;
    }

    /**
     * Represents an empty path match, equivalent of an empty word in regular
     * expressions. 
     * @author Arash Jalali
     * @version $Revision $
     */
    public static class EmptyPathMatch extends RetePathMatch {

        /**
         * the empty units array is purposely set to the length of 2
         * so that inverse operations would be possible.
         * 
         */
        private static HostNode[] emptyUnits = new HostNode[] {null, null};

        /**
         * Creates a generic empty match for a given n-node as origin.
         * 
         * @param origin The n-node that produces/has produced this match.
         */
        public EmptyPathMatch(ReteNetworkNode origin) {
            super(origin);
            this.endpoints = emptyUnits;
        }

        /**
         * Creates a concrete empty match. This is usually used
         * for merging an abstract empty match with an 
         * ordinary match of type {@link ReteSimpleMatch}.
         * 
         * @param origin
         * @param n
         */
        public EmptyPathMatch(ReteNetworkNode origin, HostNode n) {
            super(origin);
            this.endpoints = new HostNode[] {n, n};
        }

        /**
         * Creates an empty super-match from a given empty submatch.
         * 
         * This constructor is primarily used for empty matches that are
         * passed down the RETE network which are required (like any other match
         * in the RETE network) to have the proper origin.
         * 
         * @param origin The n-node that is to be the origin of the resulting match
         * @param subMatch The empty sub-match that is to be linked to this newly
         * created one by the way of domino-deletion threads. 
         */
        private EmptyPathMatch(ReteNetworkNode origin, EmptyPathMatch subMatch) {
            super(origin, subMatch);
        }

        /**
         * Creates a new path match object from this object
         * replacing the origin. 
         * 
         * @param newOrigin The new origin
         */
        @Override
        public RetePathMatch reoriginate(ReteNetworkNode newOrigin) {
            return new EmptyPathMatch(newOrigin, this);
        }

        @Override
        public RetePathMatch inverse(ReteNetworkNode origin) {
            return this.reoriginate(origin);
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public int hashCode() {
            return this.getOrigin().hashCode();
        }

        @Override
        public boolean equals(Object o) {
            return (o instanceof EmptyPathMatch)
                && this.getOrigin() == ((EmptyPathMatch) o).getOrigin();
        }

        @Override
        public String toString() {
            return String.format(
                "Empty path matched by %s",
                ((AbstractPathChecker) this.getOrigin()).getExpression().toString());
        }

    }
}
