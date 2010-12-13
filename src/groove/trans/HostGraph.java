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
package groove.trans;

import groove.graph.Graph;
import groove.graph.Label;
import groove.graph.Node;

import java.util.Set;

/**
 * Graph type used for graphs under transformation.
 * Host graphs consist of {@link HostNode}s and {@link HostEdge}s.
 * @author Arend Rensink
 * @version $Revision $
 */
public interface HostGraph extends Graph {
    @Override
    HostGraph newGraph();

    @Override
    HostGraph clone();

    @Override
    HostNode addNode();

    @Override
    HostNode addNode(int nr);

    @Override
    HostEdge addEdge(Node source, Label label, Node target);

    @Override
    Set<? extends HostNode> nodeSet();

    @Override
    Set<? extends HostEdge> edgeSet();

    @Override
    Set<? extends HostEdge> edgeSet(Node node);

    @Override
    Set<? extends HostEdge> inEdgeSet(Node node);

    @Override
    Set<? extends HostEdge> outEdgeSet(Node node);

    @Override
    Set<? extends HostEdge> labelEdgeSet(Label label);

    @Override
    HostFactory getFactory();
}
