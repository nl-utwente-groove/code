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
package groove.graph.plain;

import groove.graph.EdgeMapGraph;
import groove.graph.GraphRole;

/**
 * Simple graph with plain nodes and edges.
 * @author Arend Rensink
 * @version $Revision$ $Date: 2008-01-30 09:32:51 $
 */
public class PlainGraph extends EdgeMapGraph<PlainNode,PlainEdge> implements Cloneable {
    /**
     * Constructs a new, empty Graph with a given graph role.
     * @param name the (non-{@code null}) name of the graph.
     * @param role the (non-{@code null}) role of the graph.
     */
    public PlainGraph(String name, GraphRole role) {
        super(name, role);
    }

    /**
     * Constructs a clone of a given {@link PlainGraph}.
     * @param graph the (non-{@code null}) graph to be cloned
     */
    protected PlainGraph(PlainGraph graph) {
        super(graph);
    }

    @Override
    public PlainGraph clone() {
        PlainGraph result = new PlainGraph(this);
        return result;
    }

    @Override
    public PlainGraph newGraph(String name) {
        return new PlainGraph(name, getRole());
    }

    @Override
    public PlainFactory getFactory() {
        return PlainFactory.instance();
    }
}