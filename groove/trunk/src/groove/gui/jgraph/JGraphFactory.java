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
package groove.gui.jgraph;

import groove.graph.Edge;
import groove.graph.Node;
import groove.gui.look.VisualKey;
import groove.gui.look.VisualValue;

/**
 * Factory for JGraph vertices and edges,
 * as well as for visual value refreshers.
 * @author Arend Rensink
 * @version $Revision $
 */
public interface JGraphFactory {
    /** JGraph instance for which this factory was created. */
    GraphJGraph getJGraph();

    /** 
     * Creates a fresh, uninitialised instance of a JVertex.
     * The JVertex is initialised with {@link GraphJVertex#setNode(Node)}.
     * The result needs to be provided a JModel before it can be used.
     * @param node a (non-{@code null}) node, 
     * used to determine the type of JVertex needed
     */
    GraphJVertex newJVertex(Node node);

    /** 
     * Creates a fresh, initialised instance of a JEdge.
     * The result needs to provided a JModel before it can be used.
     * @param edge a (possibly {@code null}) edge, 
     * used to determine the type of JEdge needed
     */
    GraphJEdge newJEdge(Edge edge);

    /** Constructs a new JModel suitable for the JGraph of this factory. */
    GraphJModel<?,?> newModel();

    /** Creates a visual value refresher for a given key. */
    VisualValue<?> newVisualValue(VisualKey key);
}
