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
 * $Id: GraphShapeListener.java,v 1.3 2008-01-30 09:32:58 iovka Exp $
 */
package groove.graph;

/**
 * Interface for classes that are to be notified of changes in a graph shape.
 * @author Arend Rensink
 * @version $Revision: 1.3 $ $Date: 2008-01-30 09:32:58 $
 */
public interface GraphShapeListener {
    /**
     * Signals that a node has been added to a given graph. 
     * @param graph the graph that has been updated
     * @param node the node that has been added
     * @require <tt>graph.containsElement(elem)</tt>
     */
    void addUpdate(GraphShape graph, Node node);

    /**
     * Signals that an edge has been added to a given graph.
     * @param graph the graph that has been updated
     * @param edge the edge that has been added
     * @require <tt>graph.containsElement(elem)</tt>
     */
    void addUpdate(GraphShape graph, Edge edge);

    /**
     * Signals that a Node or Edge has been removed from a given Graph. Only Nodes without incident
     * Edges may be removed.
     * @param graph the Graph that has been updated
     * @param node the Node or Edge that has been removed
     * @require <tt>! graph.containsElement(elem)</tt>
     */
    void removeUpdate(GraphShape graph, Node node);

    /**
     * Signals that an edge has been removed from a given graph.
     * @param graph the Graph that has been updated
     * @param elem the edge that has been removed
     * @require <tt>! graph.containsElement(elem)</tt>
     */
    void removeUpdate(GraphShape graph, Edge elem);
}
