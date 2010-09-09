/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
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
 * $Id: PatternFamily.java,v 1.4 2008-02-05 13:28:21 rensink Exp $
 */
package groove.abs;

import groove.abs.MyHashSet.Hasher;
import groove.graph.DefaultEdge;
import groove.graph.DefaultGraph;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Node;
import groove.graph.NodeEdgeHashMap;
import groove.graph.NodeEdgeMap;
import groove.graph.iso.DefaultIsoChecker;
import groove.rel.VarNodeEdgeMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Allows to create and reference a set of graph patterns with a guarantee that
 * every pattern is represented only once. Additionally, if two graph patterns
 * gp1 and gp2 are obtained as result of the method gp1 =
 * family.getAddPattern(g1,c1) and gp2 = family.getAddPattern(g2,c2) of the same
 * object family, then gp1 is equivalent (isomorphic) to gp2 iff gp1 == gp2. All
 * patterns given by this factory have the same radius and the same maximal
 * incidence.
 * 
 * @author Iovka Boneva
 * @version $Revision $
 */
@Deprecated
public class PatternFamily implements Iterable<GraphPattern> {

    /** The radius around the central node */
    final int RADIUS;
    /** The maximal allowed incidence for nodes in the patterns */
    private final int MAX_INCIDENCE;
    /** Used to contain the set of patterns already in this family */
    private final MyHashSet<GraphPattern> thePatterns;

    /** Used in case of symmetry reduction. */

    /**
     * Creates a family of patterns.
     * @param radius the radius of patterns in this family
     * @param max_incidence the maximal allowed incidence of nodes
     * @require radius should be positive (>= 1)
     * @require max_incidence should be positive (>=1)
     */
    public PatternFamily(final int radius, final int max_incidence) {
        assert radius > 0 && max_incidence > 0 : "A radius and max_incidence should be positive.";
        this.RADIUS = radius;
        this.MAX_INCIDENCE = max_incidence;
        this.thePatterns =
            new MyHashSet<GraphPattern>(new DefaultGraphPatternHasher());
    }

    /**
     * The maximal allowed incidence. This is the maximal number of incident
     * edges that a pattern in this family may have.
     * @return the maximal allowed incidence
     */
    public int getMaxIncidence() {
        return this.MAX_INCIDENCE;
    }

    /**
     * The radius of the patterns in this family.
     * @return the radius of patterns in this family
     */
    public int getRadius() {
        return this.RADIUS;
    }

    /**
     * Iterator over the patterns of the family.
     * @return Iterator over the patterns of the family.
     */
    public Iterator<GraphPattern> iterator() {
        return this.thePatterns.iterator();
    }

    /**
     * Computes the pattern in the graph <code>g</code> defined by central
     * node <code>n</code>. If this pattern is already in the family, then
     * returns the pattern in the family.
     * @param graph The graph from which a pattern is to be extracted.
     * @param cnode The central node of the pattern
     * @return the pattern of graph defined by cnode
     * @throws ExceptionIncompatibleWithMaxIncidence if the pattern violates the
     *         maximum incidence constraint
     * @see #computeAddPattern(Graph, Node)
     */
    public GraphPattern getPattern(Graph graph, Node cnode)
        throws ExceptionIncompatibleWithMaxIncidence {
        GraphPattern result = (GraphPattern) getNeighInGraph(graph, cnode);
        result.setFixed();
        GraphPattern alreadyThere = this.thePatterns.get(result);
        return alreadyThere == null ? result : alreadyThere;
    }

    /**
     * Computes and returns the pattern in the graph <code>g</code> defined by
     * central node <code>n</code>. Does not test whether the pattern is in
     * the family.
     * @param graph
     * @param node
     * @return The pattern defined by node.
     * @throws ExceptionIncompatibleWithMaxIncidence
     */
    public GraphPattern computePattern(Graph graph, Node node)
        throws ExceptionIncompatibleWithMaxIncidence {
        GraphPattern result = (GraphPattern) getNeighInGraph(graph, node);
        return result;
    }

    /**
     * Computes and adds to this family the pattern defined by the central node
     * <code>cnode</code> in the graph <code>graph</code>. If the pattern
     * (or an equivalent one) is already in the family, then this method does
     * not have effect on the family. This method guarantees that only correct
     * patterns can be added to the family.
     * @param graph the graph from which the pattern is extracted
     * @param cnode the central node
     * @return the pattern of graph defined by cnode and contained in this
     *         family
     * @throws ExceptionIncompatibleWithMaxIncidence
     * @see #getPattern(Graph, Node)
     */
    public GraphPattern computeAddPattern(Graph graph, Node cnode)
        throws ExceptionIncompatibleWithMaxIncidence {
        GraphPattern result =
            new DefaultGraphPattern(getPatternEdgesInGraph(graph, cnode),
                cnode, true);
        result.setFixed();
        GraphPattern alreadyThere = this.thePatterns.getAndAdd(result);
        return alreadyThere == null ? result : alreadyThere;
    }

    /**
     * Extracts the neighbourhood of <code>cnode</code> in
     * <code>graph</code> as a subgraph of <code>graph</code>. The
     * neighbourhood is of the same radius as the radius of this family.
     * @param graph
     * @param cnode
     * @return the subgraph of <code>graph</code> that defines the pattern. Is
     *         a modifiable graph.
     * @throws ExceptionIncompatibleWithMaxIncidence
     */
    public Graph getNeighInGraph(Graph graph, Node cnode)
        throws ExceptionIncompatibleWithMaxIncidence {
        return new DefaultGraphPattern(getPatternEdgesInGraph(graph, cnode),
            cnode, false);
    }

    /**
     * Computes the set of edges that are included in the pattern defined by
     * center node <code>cnode</code> in graph <code>graph</code>.
     * @param graph
     * @param cnode
     * @return the set of edges belonging to the pattern
     * @throws ExceptionIncompatibleWithMaxIncidence
     * @require cnode is a node of graph
     */
    @SuppressWarnings("unchecked")
    private Collection<? extends Edge> getPatternEdgesInGraph(Graph graph,
            Node cnode) throws ExceptionIncompatibleWithMaxIncidence {
        assert graph.containsElement(cnode) : "Incorrect usage: " + cnode
            + " is not a node of " + graph + ".";

        // The set of edges to be constructed
        Set<Edge> res = new HashSet<Edge>();

        // exploredNodes[i] will contain the sets of nodes of the source graph
        // at distance i from the center node
        Set<Node>[] exploredNodes = new HashSet[this.RADIUS + 1];
        for (int i = 0; i <= this.RADIUS; i++) {
            exploredNodes[i] = new HashSet<Node>();
        }

        exploredNodes[0].add(cnode);

        // At ith iteration, the nodes at distance i are added to
        // exploredNodes[i]
        // and the explored edges are added to res
        for (int r = 1; r <= this.RADIUS; r++) {
            // For all nodes currNode at distance i-1
            for (Node currNode : exploredNodes[r - 1]) {
                Set<? extends Edge> incidentEdges = graph.edgeSet(currNode);
                // Check that the max_incidence constraint is not violated
                if (incidentEdges.size() > this.MAX_INCIDENCE) {
                    throw new ExceptionIncompatibleWithMaxIncidence();
                }
                // For all edges e incident to currNode
                // ensure that the edge is binary
                // add the edge to res
                // and add the end of the edge not yet known to exploredNodes[r]
                for (Edge ee : incidentEdges) {
                    DefaultEdge e = (DefaultEdge) ee; // Not defined for non
                    // binary edges
                    res.add(e);
                    for (Node newNode : e.ends()) {
                        if (newNode == currNode) {
                            continue;
                        }
                        // check whether the node is indeed new, and add it in
                        // this case
                        boolean isNew = true;
                        for (int i = 0; i <= r; i++) {
                            if (exploredNodes[i].contains(newNode)) {
                                isNew = false;
                                break;
                            }
                        }
                        if (isNew) {
                            exploredNodes[r].add(newNode);
                        }
                    }
                }
            }
        }

        // The edges between nodes of the last level are still to be added

        // For all node currNode at distance radius
        for (Node currNode : exploredNodes[this.RADIUS]) {
            Set<? extends Edge> incidentEdges = graph.edgeSet(currNode);
            // Check that the max_incidence constraint is not violated
            if (incidentEdges.size() > this.MAX_INCIDENCE) {
                throw new ExceptionIncompatibleWithMaxIncidence();
            }
            // For all edge e incident to currNode
            // ensure that the edge is binary
            // add the edge if the other end point is also at distance radius
            for (Edge e : incidentEdges) {
                assert e.endCount() <= 2 : "Undefined for graphs with hyperedges of arity greater than 2.";
                if (e.endCount() == 1) {
                    continue;
                }
                if (exploredNodes[this.RADIUS].contains(e.end(0))
                    && exploredNodes[this.RADIUS].contains(e.end(1))) {
                    res.add(e);
                }
            }
        }
        return res;
    }

    /**
     * Checks whether a GraphPattern has been issued by this PatternFamily. That
     * is, tests whether this particular object is contained in the family, and
     * not any equivalent one.
     * @param p
     * @return true if p is issued by this PatternFamily
     */
    public boolean issued(GraphPattern p) {
        Iterator<GraphPattern> it = this.thePatterns.iterator();
        while (it.hasNext()) {
            if (it.next() == p) {
                return true;
            }
        }
        return false;
    }

    /** Debugging method. */
    public Collection<NodeEdgeMap> getSelfIsomorphisms(GraphPattern p) {
        return ((DefaultGraphPattern) p).getSelfIsomorphisms();
    }

    // ----------------------------------------------------------------------------------------------
    // INNER CLASSES
    // ----------------------------------------------------------------------------------------------
    /**
     * Default implementation for a graph pattern
     * @author Iovka Boneva
     */
    private class DefaultGraphPattern extends DefaultGraph implements
            GraphPattern {

        private Map<Node,Integer> distanceMap;

        /** The central node of the pattern */
        private final Node central;

        /** */
        private ArrayList<NodeEdgeMap> selfIsomorphisms;

        /**
         * Creates a non fixed GraphPattern from its set of edges and its
         * central node
         * @param edges
         * @param central
         * @param computeDistance indicates whether the distance map should be
         *        computed
         */
        DefaultGraphPattern(Collection<? extends Edge> edges, Node central,
                boolean computeDistance) {
            super();
            super.addNode(central);
            super.addEdgeSet(edges);
            this.central = central;
            if (computeDistance) {
                computeDistanceMap();
            }
        }

        public Node central() {
            return this.central;
        }

        public Graph graph() {
            return this;
        }

        /** Lazily computes self isomorphisms. */
        private ArrayList<NodeEdgeMap> getSelfIsomorphisms() {
            if (this.selfIsomorphisms == null) {
                computeSelfIsomorphisms();
            }
            return this.selfIsomorphisms;
        }

        /** Initialises and computes the distance map of this pattern. */
        private void computeDistanceMap() {
            this.distanceMap = new HashMap<Node,Integer>();
            for (Node n : nodeSet()) {
                this.distanceMap.put(n, 100);
            }
            this.distanceMap.put(central(), 0);

            List<Node> current = new ArrayList<Node>();
            current.add(central());
            for (int i = 0; i < PatternFamily.this.RADIUS; i++) {
                for (Node n : current) {
                    List<Node> nextCurrent = new ArrayList<Node>();
                    int distN = this.distanceMap.get(n);
                    for (Edge ee : graph().edgeSet(n)) {
                        // Does not work for non binary edges
                        DefaultEdge e = (DefaultEdge) ee;
                        if (e.source() != e.target()) {
                            Node nn = e.end(1 - e.endIndex(n));
                            int distNN = this.distanceMap.get(nn);
                            if (distNN > distN + 1) {
                                this.distanceMap.put(nn, distN + 1);
                                nextCurrent.add(nn);
                            }
                        }
                    }
                    current = nextCurrent;
                }
            }
        }

        /** Computes all non-trivial self-isomorphisms for this graph. */
        private void computeSelfIsomorphisms() {
            this.selfIsomorphisms = new ArrayList<NodeEdgeMap>();
            for (VarNodeEdgeMap map : Util.getInjMatchesIter(this, this,
                new NodeEdgeHashMap())) {
                // check whether this is the identity
                boolean identity = true;
                for (Map.Entry<Node,Node> entry : map.nodeMap().entrySet()) {
                    if (entry.getKey() != entry.getValue()) {
                        identity = false;
                        break;
                    }
                }

                if (!identity) {
                    this.selfIsomorphisms.add(map);
                }
            }
        }

        public Collection<VarNodeEdgeMap> possibleTypings(Graph g, Node center,
                boolean symmetryReduction) {
            NodeEdgeMap centerMap = new NodeEdgeHashMap();
            centerMap.putNode(center, central());
            return possibleTypings(g, centerMap, symmetryReduction);
        }

        public Collection<VarNodeEdgeMap> possibleTypings(Graph g,
                NodeEdgeMap preMatched, boolean symmetryReduction) {
            if (!symmetryReduction) {
                return Util.getInjMatchSet(g, this, preMatched);
            }

            ArrayList<VarNodeEdgeMap> result = new ArrayList<VarNodeEdgeMap>();
            for (VarNodeEdgeMap newMap : Util.getInjMatchesIter(g, this,
                preMatched)) {
                // check whether newMap is symmetric to one of the previously
                // computed morphisms
                boolean symmetric = false;
                for (NodeEdgeMap iso : getSelfIsomorphisms()) {
                    for (int i = 0; i < result.size(); i++) {
                        if (equalsComposition(newMap, iso, result.get(i))) {
                            symmetric = true;
                            break;
                        }
                    }
                    if (symmetric) {
                        break;
                    }
                }
                if (!symmetric) {
                    result.add(newMap);
                }
            }
            return result;
        }

        /**
         * Tests whether the morphism m1 is equivalent to m2 \circ m3. When
         * using it, m1 will be a map from a g to this, m2 a map from this to
         * this, m3 a map from g to this
         */
        private boolean equalsComposition(NodeEdgeMap m1, NodeEdgeMap m2,
                NodeEdgeMap m3) {
            for (Map.Entry<Node,Node> entry : m1.nodeMap().entrySet()) {
                if (entry.getValue() != m2.getNode(m3.getNode(entry.getKey()))) {
                    return false;
                }
            }
            return true;
        }

        @Override
        /** In this implementation equality is the same as pointer equality. */
        public boolean equals(Object o) {
            return this == o;
        }

        @Override
        public String toString() {
            String result = super.toString();
            String distances = new String();
            if (this.distanceMap != null) {
                distances += "Distances:";
                for (Node n : super.nodeSet()) {
                    distances += " " + n + "(" + this.distanceMap.get(n) + ")";
                }
            }
            return "Center: " + this.central + "; " + result + "; " + distances;
        }

    }

    /**
     * Implements a naive hasher
     * @author Iovka Boneva
     */
    class DefaultGraphPatternHasher implements Hasher<GraphPattern> {

        public int getHashCode(GraphPattern p) {
            return p.graph().getCertifier(true).getGraphCertificate().hashCode();
        }

        public boolean areEqual(GraphPattern p1, GraphPattern p2) {
            NodeEdgeMap isomorphism =
                DefaultIsoChecker.getInstance(true).getIsomorphism(p1.graph(),
                    p2.graph());
            if (isomorphism == null) {
                return false;
            } else if (isomorphism.getNode(p1.central()).equals(p2.central())) {
                return true;
            }
            // there is an isomorphism, but not the one we want
            // compute all isomorphisms
            NodeEdgeMap centerMap = new NodeEdgeHashMap();
            centerMap.putNode(p1.central(), p2.central());

            // if a morphism exists, it is necessary an isomorphism,
            // as the graphs are isomorphic, thus same number nodes and edges,
            // and morph is injective and total, thus surjective
            return !groove.abs.Util.getInjMatchSet(p1.graph(), p2.graph(),
                centerMap).isEmpty();
        }
    }
}
