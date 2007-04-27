// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente
 
// Licensed under the Apache License, Version 2.0 (the "License"); 
// you may not use this file except in compliance with the License. 
// You may obtain a copy of the License at 
// http://www.apache.org/licenses/LICENSE-2.0 
 
// Unless required by applicable law or agreed to in writing, 
// software distributed under the License is distributed on an 
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
// either express or implied. See the License for the specific 
// language governing permissions and limitations under the License.
/* 
 * $Id: Graph.java,v 1.5 2007-04-27 22:07:04 rensink Exp $
 */
package groove.graph;

import groove.graph.iso.CertificateStrategy;
import groove.graph.iso.IsoChecker;
import groove.graph.iso.IsoMatcher;
import groove.util.FormatException;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Provides a model of a graph whose nodes and edges are unstructured,
 * in the sense that they are immutable and edges are completely determined by 
 * source and target nodes and edge label.
 * The interface extends <tt>GraphShape</tt> with factory methods for
 * nodes and edges and methods for generating morphisms.
 * @version $Revision: 1.5 $ $Date: 2007-04-27 22:07:04 $
 */
public interface Graph extends GraphShape, DeltaTarget {
    /**
     * Returns the set of all matches (i.e., colourings) from this graph to another.
     * @param to the graph to which this one is to be matched
     * @return the set of all total morphisms from this graph to <tt>to</tt>
     * @require <tt>to != null</tt>
     * @ensure <tt>result = { (m: this --> to) \in Morphism | m.isTotal() }</tt>
     */
    Collection<? extends Morphism> getMatchesTo(Graph to);

    /**
     * Returns an iterator over the matches (i.e., colourings) from this graph to another.
     * The matches may be computed in a lazy fashion.
     * @param to the graph to which this one is to be matched
     * @return the set of all total morphisms from this graph to <tt>to</tt>
     * @require <tt>to != null</tt>
     * @ensure <tt>result = { (m: this --> to) \in Morphism | m.isTotal() }</tt>
     */
    Iterator<? extends Morphism> getMatchesToIter(Graph to);

    /**
     * Indicates if there exists a match to a given graph.
     * @param to the graph to which this one is to be matched
     * @return <tt>true</tt> if there is a match from this graph to <tt>to</tt>
     * @require <tt>to != null</tt>
     * @ensure <tt>result == !getMatchesTo(to).isEmpty()</tt>
     * @deprecated not used in practice; scrapped to reduced interface
     */
    @Deprecated
    boolean hasMatchesTo(Graph to);

    /**
     * Returns the set of all injective matches from this graph to another.
     * @param to the Graph to which this one is to be injectively matched
     * @return the set of all total InjectiveMorphisms from this Graph to to
     * @require <tt>to != null</tt>
     * @ensure result = { (m: this --> to) \in InjectiveMorphism | m is total }
     * @deprecated Implementation now very inefficient
     */
    @Deprecated
    Collection<? extends Morphism> getInjectiveMatchesTo(Graph to);

    /**
     * Tests whether this graph can be injectively embedded in another.
     * @param other the graph into which this one is to be embedded
     * @return <tt>true</tt> iff <tt>! getInjectiveMatchesTo(other).isEmpty()</tt>
     * @require <tt>other != null</tt>
     * @deprecated not used in practice; scrapped to reduce interface
     */
    @Deprecated
    boolean hasInjectiveMatchesTo(Graph other);

    /**
     * Returns an isomorphism from this graph to another, if one exists.
     * @param to the graph to which this one is to be isomorphically matched
     * @return a total and surjective <tt>InjectiveMorphism</tt> from this graph to <tt>to</tt>;
     *          <tt>null</tt> if none exists
     */
    Morphism getIsomorphismTo(Graph to);

    /**
     * Tests whether this Graph is isomorphic to another.
     * @param other the Graph that is tested for isomorphism
     * @return <tt>true</tt> iff <tt>! getIsomorphismTo(other).isEmpty()</tt>
     * @require <tt>other != null</tt>
     * @see #getCertificate()
     * @deprecated misleading because not a fast check for isomorphism;
     * use {@link IsoChecker} instead.
     */
    @Deprecated
    boolean hasIsomorphismTo(Graph other);
    
    /**
     * Returns the certificate strategy object used for this graph.
     * The certificate strategy is used to decide isomorphism between graphs.
     * @see #getCertificate()
     */
    public CertificateStrategy getCertifier();

    /**
     * Returns an isomorphism certificate for this graph, i.e., an object
     * optimistically predicting whether or not this graph could be isomorphic
     * to another. Two graphs are potentially isomorphic if their graph certificates
     * coincide according to {@link Object#equals(java.lang.Object)}). 
     * Yields the same result as <code>getCertificateStrategy().getCertificate()</code>.
     * @return an isomorphism certificate for this graph
     * @see #getCertifier()
     * @deprecated use {@link #getCertifier()} and {@link CertificateStrategy#getGraphCertificate()}
     */
    @Deprecated
    Object getCertificate();
    
    /**
     * Returns an unmodifiable view on the map from the nodes of this graph
     * to the set of its incident edges.
     */
    Map<Node, ? extends Set<? extends Edge>> nodeEdgeMap();
    
    // --------------------------------- Object overrides ----------------------------
    
    /**
     * Makes a copy of this Graph with cloned (not aliased) node and edge sets
     * but aliased nodes and edges.
     * @ensure <tt>resultnodeSet().equals(this.nodeSet()) && result.edgeSet().equals(this.edgeSet()</tt>
     */
    Graph clone();
    
    /**
     * Returns a clone of this Graph as a Graph.
     * @ensure <tt>resultnodeSet().equals(this.nodeSet()) && result.edgeSet().equals(this.edgeSet()</tt>
     * @see #clone()
     * @deprecated as of Java 5, use {@link #clone()} instead
     */
    @Deprecated
    Graph cloneGraph();

    /**
     * Yields a morphism from an isomorphic copy to this graph.
     * Note that the result is not an <tt>InjectiveMorphism</tt>, so as
     * to allow later node mergings.
     * @ensure <tt>result: clone --> this</tt> such that
     * <tt>result.isInjective()</tt> && <tt>result.isSurjective()</tt>
     * @deprecated not used in practice; removed to clean up interface
     */
    @Deprecated
    Morphism cloneTo();

    /**
     * Yields an injective morphism from an isomorphic copy to this graph.
     * @ensure <tt>result: clone --> this</tt> such that
     * <tt>result.isSurjective()</tt>
     * @deprecated not used in practice; removed to clean up interface
     */
    @Deprecated
    InjectiveMorphism injectiveCloneTo();

    /**
     * Yields a morphism from this graph to an isomorphic copy of it.
     * Note that the result is not an <tt>InjectiveMorphism</tt>, so as
     * to allow later node mergings.
     * @ensure <tt>result: this --> clone</tt> such that
     * <tt>result.isInjective()</tt> && <tt>result.isSurjective()</tt>
     * @deprecated not used in practice; removed to clean up interface
     */
    @Deprecated
    Morphism cloneFrom();

    /**
     * Yields a morphism from this graph to an isomorphic copy of it.
     * @ensure <tt>result: this --> clone</tt> such that
     * <tt>result.isSurjective()</tt>
     * @deprecated not used in practice; removed to clean up interface
     */
    @Deprecated
    InjectiveMorphism injectiveCloneFrom();

    /**
     * Factory method: returns a fresh, empty graph.
     */
    Graph newGraph();

    /**
     * Factory method: return a fresh graph from the given graph. Throws a {@link FormatException} if there is a compatibility problem.
     * @param graph the current graph
     * @return the new graph
     * @throws FormatException
     */
    Graph newGraph(Graph graph) throws FormatException;

    // ------------------------- Commands: Factory methods ---------------------------
    
    /**
     * Generates a fresh node and adds it to this graph.
     * @return the new node
     * @see Graph#addNode(Node)
     */
    Node addNode();

    /**
     * Adds a binary edge to the graph, between given nodes and with a given label,
     * and returns the edge. Also adds the end nodes if these are not yet in the graph.
     * If an edge with these properties already exists, the method returns the
     * existing edge.
     * @param source the source node of the new edge
     * @param label the label of the new edge
     * @param target the target node of the new edge
     * @return a binary edge between <tt>source</tt> and <tt>target</tt>, 
     * labelled <tt>label</tt>
     * @see Graph#addEdge(Edge) 
     */
    BinaryEdge addEdge(Node source, Label label, Node target);
    
    /**
     * Adds an edge to the graph, between given nodes and with a given label,
     * and returns the edge. Also adds the end nodes if these are not yet in the graph.
     * If an edge with these properties already exists, the method returns the
     * existing edge.
     * @param ends the ends of the new edge
     * @param label the label of the new edge
     * @return an edge between <code>ends</code> 
     * labelled <tt>label</tt>
     * @see Graph#addEdge(Edge) 
     * @throws IllegalArgumentException if the graph implementation does not support edges
     * with the required arity <code>ends.length</code>.
     */
    Edge addEdge(Node[] ends, Label label);
    
    /**
     * Adds a graph element to the graph.
     * Convenience method, typically delegating to {@link #addNode(Node)} or {@link #addEdge(Edge)}.
     */
    @Deprecated
    void addElement(Element elem);

    /**
     * Adds a node to this graph.
     * This is allowed only if the graph is not fixed.
     * If the node is already in the graph then the method has no effect.
     * All {@link GraphListener}s are notified if the node is actually added.
     * <i>Note:</i> It is <i>not</i> guaranteed that <tt>addNode(Node)</tt> is called
     * for the addition of all nodes, so overwriting it may not have 
     * the expected effect. Use a {@link GraphListener} to ensure notification
     * of all changes to the graph.
     * @param node the node to be added.
     * @return <tt>true</tt> if the node was indeed added (and not yet present)
     * @require <tt>!isFixed()</tt>
     * @ensure <tt>nodeSet() = old.nodeSet() \cup node</tt>
     * @see #addEdge(Edge)
     * @see #isFixed()
     */
    boolean addNode(Node node);

    /**
     * Adds an edge and its end nodes to this graph.
     * This is allowed only if the graph is not fixed.
     * If the edge is already in the graph then the method has no effect.
     * All {@link GraphListener}s are notified if the edge is actually added.
     * <i>Note:</i> It is <i>not</i> guaranteed that this method is called
     * for the addition of all edges, so overwriting it may not have 
     * the expected effect. Use a {@link GraphListener} to ensure notification
     * of all changes to the graph.
     * @param edge the edge to be added.
     * @return <tt>true</tt> if the edge was indeed added (and not yet present)
     * @require <tt>!isFixed()</tt>
     * @ensure <tt>edgeSet() = old.edgeSet() \cup edge</tt> and
     *         <tt>nodeSet() = old.nodeSet() \cup edge.parts()</tt>
     * @see #addNode(Node)
     * @see #isFixed()
     */
    boolean addEdge(Edge edge);
    
    /**
     * Adds a set of nodes to this graph.
     * This is allowed only if the graph is modifiable (and not fixed).
     * If all the nodes are already in the graph then the method has no effect.
     * All GraphListeners are notified for evey node that is actually added.
     * @param nodeSet the collection of nodes to be added.
     * @return <tt>true</tt> if any node was indeed added
     * @require <tt>!isFixed()</tt>
     * @ensure <tt>nodeSet() = old.nodeSet() \cup nodeSet</tt>
     * @see #addNode(Node)
     * @see #addEdgeSet(Collection)
     * @see #isFixed()
     */
    boolean addNodeSet(Collection<? extends Node> nodeSet);
    
    /**
     * Adds a set of edges and their end nodes to this graph.
     * This is allowed only if the graph is modifiable (and not fixed).
     * If all the edges are already in the graph then the method has no effect.
     * All GraphListeners are notified for every edge that is actually added.
     * @param edgeSet the collection of edges to be added.
     * @return <tt>true</tt> if any edge was indeed added
     * @require <tt>isFixed()</tt>
     * @ensure <tt>edgeSet() = old.edgeSet() \cup edgeSet</tt> and
     *         <tt>nodeSet() = old.nodeSet() \cup edgeSet.parts()</tt>
     * @see #addEdge(Edge)
     * @see #addNodeSet(Collection)
     * @see #isFixed()
     */
    boolean addEdgeSet(Collection<? extends Edge> edgeSet);

    /**
     * Removes a graph element from the graph.
     * Convenience method, typically delegating to {@link #removeNode(Node)} or {@link #removeEdge(Edge)}.
     */
    @Deprecated
    void removeElement(Element elem);

    /**
     * Removes a given node from this graph, if it was in the graph to start with.
     * All incident edges are also removed.
     * This method is allowed only if the graph is modifiable.
     * The method has no effect if the node is not in this graph.
     * All GraphListeners are notified if the node is indeed removed.
     * <i>Note:</i> It is <i>not</i> guaranteed that <tt>removeNode(Node)</tt> is called
     * for the removal of all nodes, so overwriting it may not have 
     * the expected effect. Use <tt>GraphListener</tt> to ensure notification
     * of all changes to the graph.
     * @param node the node to be removed from the set. If <tt>other.hasPart(node)</tt>
     * for some other element such that <tt>contains(other)</tt>, then <tt>other</tt> is 
     * also removed.
     * @return <tt>true</tt> if <tt>node</tt> was there in the first place
     * @require <tt>isFixed()</tt>
     * @ensure <tt>nodeSet() = old.nodeSet() \setminus node</tt> and
     * <tt>edgeSet() = old.edgeSet() \seminus { edge | node\in edge.parts() }</tt>
     * @see #isFixed()
     * @see #removeEdge(Edge)
     */
    boolean removeNode(Node node);
    
    /**
     * Removes a given edge from this graph, if it was in the graph to start with.
     * This method is allowed only if the graph is modifiable.
     * The method has no effect if the edge is not in this graph.
     * All GraphListeners are notified if the edge is indeed removed.
     * <i>Note:</i> It is <i>not</i> guaranteed that <tt>removeEdge(Edge)</tt> is called
     * for the removal of all edges, so overwriting it may not have 
     * the expected effect. Use <tt>GraphListener</tt> to ensure notification
     * of all changes to the graph.
     * @param edge the edge to be removed from the graph.
     * @return <tt>true</tt> if <tt>edge</tt> was there in the first place
     * @require <tt>isFixed()</tt>
     * @ensure <tt>edgeSet() = edgeSet() \setminus edge</tt>
     * @see #isFixed()
     * @see #removeNode(Node)
     */
    boolean removeEdge(Edge edge);
    
    /**
     * Removes a set of nodes from this graph, if they were in the graph to start with.
     * All incident edges are also removed.
     * This method is allowed only if the graph is modifiable.
     * The method has no effect if none of the nodes are in this graph.
     * All GraphListeners are notified if the node is indeed removed.
     * @param nodeSet the collection of nodes to be removed from the set. If 
     * <tt>nodeSet.removeAll(other.parts())</tt> for some other element such that 
     * <tt>contains(other)</tt>, then <tt>other</tt> is also removed.
     * @return <tt>true</tt> if <tt>old.nodeSet() \cap nodeSet</tt> was nonempty
     * @require <tt>isFixed()</tt>
     * @ensure <tt>this.nodeSet() = old.nodeSet() \setminus nodeSet</tt> and
     * <tt>edgeSet() = old.edgeSet() \setminus { other | other.parts() \cap nodeSet }</tt>
     * @see #isFixed()
     * @see #removeNode(Node)
     * @see #removeEdgeSet(Collection)
     */
    boolean removeNodeSet(Collection<Node> nodeSet);
    
    /**
     * Removes a set of edges from this graph, if they were in the graph to start with.
     * This method is allowed only if the graph is modifiable.
     * The method has no effect if none of the edges are in this graph.
     * All GraphListeners are notified if the edge is indeed removed.
     * @param edgeSet the collection of edges to be removed from the graph.
     * @return <tt>true</tt> if <tt>old.edgeSet() \cap edgeSet</tt> was nonempty
     * @require <tt>isFixed()</tt>
     * @ensure <tt>this.edgeSet() = old.edgeSet() \setminus edgeSet</tt>
     * @see #isFixed()
     * @see #removeEdge(Edge)
     * @see #removeNodeSet(Collection)
     */
    boolean removeEdgeSet(Collection<Edge> edgeSet);

    /**
     * Merges two nodes in this graph, by adding all edges to and from
     * the first node to the second, and subsequently removing the first.
     * Before the remove notifications, all graph listeners receive a call of
     * {@link GraphListener#replaceUpdate(GraphShape, Node, Node)}.
     * @param from node to be deleted
     * @param to node to receive copies of the edges to and from the other
     * @return <tt>true</tt> if <code>first</code> is distinct from <code>second</code>, so a
     * merge actually took place
     * @require <tt>containsElement(from) && containsElement(to)</tt>
     * @ensure <tt>! containsElement(from)</tt> and
     * <tt>containsElement(to,l,n)</tt> if <tt>old.containsElement(from,l,n)</tt> and
     * <tt>containsElement(n,l,to)</tt> if <tt>old.containsElement(n,l,from)</tt> and
     */
    boolean mergeNodes(Node from, Node to);
}
