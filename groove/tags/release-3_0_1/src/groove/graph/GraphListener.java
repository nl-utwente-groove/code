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
 * $Id: GraphListener.java,v 1.3 2008-01-30 09:32:53 iovka Exp $
 */
package groove.graph;

/**
 * Interface for classes that are to be notified of changes in a graph.
 * @author Arend Rensink
 * @version $Revision: 1.3 $ $Date: 2008-01-30 09:32:53 $
 */
public interface GraphListener extends GraphShapeListener {
    /**
     * Signals that an element has been replaced by another in a given graph. Both elements exist in
     * the graph (at the moment of invocation of this method) and are distinct. A replace
     * notification for a node is always followed immediately by replace notifications for its
     * incident edges, and vice versa, a replace notification for an edge is preceded by replace
     * notifications for its (replaced) end points. Separate add and remove notifications are given
     * (before and after this notification) for the actual changes to the graph.
     * @param graph the Graph that has been updated
     * @param from the element that is replaced
     * @param to its replacement
     * @require <tt>graph.containsElement(elem1) && graph.containsElement(elem2)</tt>
     */
    void replaceUpdate(GraphShape graph, Node from, Node to);

    /**
     * Signals that an edge has been replaced by another in a given graph. Both elements exist in
     * the graph (at the moment of invocation of this method) and are distinct. A replace
     * notification for an edge is preceded by replace notifications for its (replaced) end points. 
     * Separate add and remove notifications are given
     * (before and after this notification) for the actual changes to the graph.
     * @param graph the Graph that has been updated
     * @param elem1 the edge that is replaced
     * @param elem2 its replacement
     * @require <tt>graph.containsElement(elem1) && graph.containsElement(elem2)</tt>
     */
    void replaceUpdate(GraphShape graph, Edge elem1, Edge elem2);
}
