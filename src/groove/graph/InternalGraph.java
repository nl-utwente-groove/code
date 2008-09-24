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
 * $Id: InternalGraph.java,v 1.4 2008-01-30 09:32:57 iovka Exp $
 */
package groove.graph;

import java.util.Collection;

/**
 * Extends the <tt>Graph</tt> interface with methods that
 * avoid some of the consistency checks. Only for package
 * internal uses.
 * @author Arend Rensink
 * @version $Revision$
 */
public interface InternalGraph extends Graph {
    /**
     * More efficient addition of edges; for package use only.
     * Avoids both the class cast and especially the
     * recursive addition of edge ends. Make sure you satisfy
     * the precondition, or an inconsistent graph may ensue!
     * @require <tt>edge instanceof Edge && containsAll(edge.ends())</tt>
     * @see #addEdge(Edge)
     */
    boolean addEdgeWithoutCheck(Edge edge);
    
    /**
     * More efficient addition of sets of edges; for package use only.
     * Avoids the recursive addition of parts. Make sure you satisfy
     * the precondition, or an inconsistent graph may ensue!
     * @require <tt>edge: 2^Edge && containsAll(edge.ends())</tt>
     * @see #addEdgeSet(Collection)
     */
    boolean addEdgeSetWithoutCheck(Collection<? extends Edge> edgeSet);
    
    /**
     * More efficient removal of nodes; for package use only.
     * Avoids both the class cast and especially the
     * recursive removal of incident edges. Make sure you satisfy
     * the precondition, or an inconsistent graph may ensue!
     * @require <tt>node instanceof Node</tt> and the graph contains no
     * incident edges.
     * @see #removeNode(Node)
     */
    boolean removeNodeWithoutCheck(Node node);
    
    /**
     * More efficient removal of sets of nodes; for package use only.
     * Avoids the recursive removal of incident edges.
     * Make sure you satisfy
     * the precondition, or an inconsistent graph may ensue!
     * @require <tt>nodeSet: 2^Node</tt> and the graph contains no
     * incident edges.
     * @see #removeNodeSet(Collection)
     */
    boolean removeNodeSetWithoutCheck(Collection<Node> nodeSet);
}
