/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2023
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
package nl.utwente.groove.graph.plain;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.NonNullByDefault;

import nl.utwente.groove.graph.AGraphMap;
import nl.utwente.groove.graph.Edge;
import nl.utwente.groove.graph.EdgeMapGraph;
import nl.utwente.groove.graph.ElementFactory;
import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.graph.GraphInfo;
import nl.utwente.groove.graph.GraphRole;
import nl.utwente.groove.graph.Node;

/**
 * Simple graph with plain nodes and edges.
 * @author Arend Rensink
 * @version $Revision$ $Date: 2008-01-30 09:32:51 $
 */
@NonNullByDefault
public class PlainGraph extends EdgeMapGraph<PlainNode,PlainEdge> implements Cloneable {
    /**
     * Constructs a new, empty Graph with a given graph role.
     * @param name the (non-{@code null}) name of the graph.
     * @param role the (non-{@code null}) role of the graph.
     */
    public PlainGraph(String name, GraphRole role) {
        super(name, role, true);
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

    /**
     * Creates a {@link PlainGraph} from an arbitrary graph,
     * using {@link PlainLabel}s based on the string values of the edge labels.
     */
    static public PlainGraph instance(Graph source) {
        var result = new PlainGraph(source.getName(), source.getRole());
        var map = new PlainGraphMap(result.getFactory());
        source.nodeSet().forEach(n -> map.putNode(n, result.addNode(n.getNumber())));
        source.edgeSet().forEach(e -> result.addEdge(map.mapEdge(e)));
        GraphInfo.transferAll(source, result, map);
        return result;
    }

    static private class PlainGraphMap extends AGraphMap<Node,Edge,PlainNode,PlainEdge> {
        public PlainGraphMap(ElementFactory<PlainNode,PlainEdge> factory) {
            super(factory);
        }

        @Override
        public @NonNull PlainEdge mapEdge(Edge key) {
            var sourceImage = getNode(key.source());
            var targetImage = getNode(key.target());
            // we only use this in a context where all incident nodes have been mapped
            assert sourceImage != null && targetImage != null;
            var result = getFactory().createEdge(sourceImage, key.label().toString(), targetImage);
            putEdge(key, result);
            return result;
        }
    }
}