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

import groove.match.rete.ClosurePathChecker.ClosureInfo;
import groove.rel.Valuation;
import groove.trans.HostEdge;
import groove.trans.HostNode;
import groove.util.TreeHashSet;

import java.util.Set;

/**
 * @author Arash Jalali
 * @version $Revision $
 */
public class RetePathMatch extends AbstractReteMatch {
    /** Start node of the path. */
    private final HostNode start;
    /** End node of the path. */
    private final HostNode end;
    /**
     * Determines the length of the path (number of edges)
     * represented by this match.
     */
    protected int pathLength = 0;

    /**
     * Lazily evaluated set of nodes returned by the method
     * {@link #getNodes()}.
     * 
     * Warning: The lazy evaluation is not thread-safe.
     */
    protected Set<HostNode> nodes = null;
    /** Additional information in case this match is for a closure. */
    protected ClosureInfo auxiliaryData = null;

    /**
     * For single-edge path matches this variable holds 
     * a reference to the associated host edge for equality checking. 
     */
    protected HostEdge associatedEdge = null;

    private RetePathMatch(ReteNetworkNode origin, HostNode start, HostNode end) {
        super(origin, false);
        this.start = start;
        this.end = end;
        this.valuation = new Valuation();
    }

    /**
     * @param origin The regular-expression path checker node generating this  
     */
    public RetePathMatch(ReteNetworkNode origin, HostEdge edge) {
        this(origin, edge.source(), edge.target());
        this.hashCode = edge.hashCode();
        this.pathLength = 1;
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
        this(origin, subMatch.start, subMatch.end);
        this.pathLength = subMatch.pathLength;
        this.valuation = subMatch.valuation;
        this.associatedEdge = subMatch.associatedEdge;
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
        if (this.nodes == null) {
            this.nodes = new TreeHashSet<HostNode>();
            this.nodes.add(this.start);
            this.nodes.add(this.end);
        }
        return this.nodes;
    }

    /**
     * @return The additional information in case this match is for a closure
     */
    public ClosureInfo getClosureInfo() {
        return this.auxiliaryData;
    }

    /**
     * Initialises the information object for the case this matches a closure
     */
    public void setClosureInfo(ClosureInfo value) {
        this.auxiliaryData = value;
    }

    @Override
    public int hashCode() {
        if (this.hashCode == 0) {
            this.hashCode = this.start.hashCode();
            boolean neg = this.hashCode < 0;
            this.hashCode <<= 1;
            if (neg) {
                this.hashCode |= 1;
            }
            this.hashCode += this.end.hashCode();
        }
        return this.hashCode;
    }

    @Override
    public int size() {
        return 2;
    }

    /**
     * Compares this instance with an instance of the {@link RetePathMatch} class.
     */
    public int compareTo(RetePathMatch m) {
        int result = this.hashCode() - m.hashCode();
        if (result == 0) {
            result = this.start.compareTo(m.start);
            if (result == 0) {
                result = this.end.compareTo(m.end);
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
        if (this.end.equals(m.start)) {
            Valuation valuation = getMergedValuation(m);
            if (valuation != null) {
                result = new RetePathMatch(origin, this.start, m.end);
                if (copyPrefix) {
                    result.specialPrefix =
                        (m.specialPrefix != null) ? m.specialPrefix : m;
                }
                result.pathLength = this.pathLength + (m).pathLength;
                result.valuation = (valuation != emptyMap) ? valuation : null;
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
        RetePathMatch result = new RetePathMatch(origin, this.end, this.start);
        result.pathLength = this.pathLength;
        result.valuation = this.valuation;
        result.hashCode(); //refresh hash code
        this.getSuperMatches().add(result);
        return result;
    }

    /**
     * @return The start node of the path associated with this match object
     */
    public HostNode start() {
        return this.start;
    }

    /**
     * @return The end node of the path associated with this match object
     */
    public HostNode end() {
        return this.end;
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
     * @param m match to be duplicated
     * @return A path match that is the replica of <code>m</code>
     * except for the domino history. Its domino history is empty
     */
    public static RetePathMatch duplicate(RetePathMatch m) {
        RetePathMatch result = new RetePathMatch(m.getOrigin(), m.start, m.end);
        result.associatedEdge = m.associatedEdge;
        result.nodes = m.nodes;
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
         * Creates a generic empty match for a given n-node as origin.
         * 
         * @param origin The n-node that produces/has produced this match.
         */
        public EmptyPathMatch(ReteNetworkNode origin) {
            super(origin, null, null);
        }

        /**
         * Creates a concrete empty match. This is usually used
         * for merging an abstract empty match with an 
         * ordinary match of type {@link ReteSimpleMatch}.
         * 
         * @param origin n-node of which this is a match
         * @param n node for which this represents an empty path
         */
        public EmptyPathMatch(ReteNetworkNode origin, HostNode n) {
            super(origin, n, n);
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
