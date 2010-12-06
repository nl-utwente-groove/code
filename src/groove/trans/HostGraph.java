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

import groove.graph.DefaultGraph;
import groove.graph.DefaultNode;

import java.util.Set;

/**
 * Class representing graphs under transformation.
 * @author Arend Rensink
 * @version $Revision $
 */
public class HostGraph extends DefaultGraph {
    /**
     * Constructs an empty host graph.
     */
    public HostGraph() {
        // empty
    }

    /**
     * Copies an existing host graph.
     */
    public HostGraph(HostGraph graph) {
        super(graph);
    }

    @Override
    public HostGraph clone() {
        return new HostGraph(this);
    }

    @Override
    public HostGraph newGraph() {
        return new HostGraph();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<HostNode> nodeSet() {
        return (Set<HostNode>) super.nodeSet();
    }

    @Override
    public HostNode createNode() {
        return DefaultNode.createNode();
    }
}
