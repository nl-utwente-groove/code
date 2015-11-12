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
 * $Id$
 */
package groove.graph.multi;

import groove.graph.EdgeMapGraph;
import groove.graph.GraphRole;

/**
 * Plain graph with parallel (equi-labelled) edges.
 * @author Arend Rensink
 * @version $Revision$ $Date: 2008-01-30 09:32:51 $
 */
public class MultiGraph extends EdgeMapGraph<MultiNode,MultiEdge> implements Cloneable {
    /**
     * Constructs a new, empty graph.
     * @param name the (non-{@code null}) name of the graph.
     * @param role the (non-{@code null}) graph role of the new graph.
     */
    public MultiGraph(String name, GraphRole role) {
        super(name, role);
    }

    /**
     * Constructs a clone of a given {@link MultiGraph}.
     * @param graph the (non-{@code null}) graph to be cloned
     */
    protected MultiGraph(MultiGraph graph) {
        super(graph);
    }

    @Override
    public MultiGraph clone() {
        return new MultiGraph(this);
    }

    @Override
    public MultiGraph newGraph(String name) {
        return new MultiGraph(name, getRole());
    }

    @Override
    public MultiFactory getFactory() {
        return MultiFactory.instance();
    }
}