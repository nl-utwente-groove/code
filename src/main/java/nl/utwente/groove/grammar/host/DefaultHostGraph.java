/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.grammar.host;

import static nl.utwente.groove.graph.GraphRole.HOST;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.algebra.Algebra;
import nl.utwente.groove.algebra.AlgebraFamily;
import nl.utwente.groove.algebra.ErrorValue;
import nl.utwente.groove.grammar.type.TypeFactory;
import nl.utwente.groove.graph.AGraphMap;
import nl.utwente.groove.graph.Edge;
import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.graph.GraphInfo;
import nl.utwente.groove.graph.GraphRole;
import nl.utwente.groove.graph.Node;
import nl.utwente.groove.graph.NodeSetEdgeSetGraph;

/**
 * Class providing a default implementation of {@link HostGraph}s.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public class DefaultHostGraph extends NodeSetEdgeSetGraph<HostNode,HostEdge> implements HostGraph {
    /**
     * Constructs an empty simple host graph.
     * @param name name of the new host graph.
     */
    public DefaultHostGraph(String name) {
        this(name, true);
    }

    /**
     * Constructs an empty simple host graph, based on a given type factory.
     * @param name name of the new host graph.
     */
    public DefaultHostGraph(String name, TypeFactory typeFactory) {
        this(name, HostFactory.newInstance(typeFactory, true));
    }

    /**
     * Constructs an empty (simple or multi-) host graph.
     * @param name name of the new host graph.
     * @param simple indicates if the new graph is to be a simple or multi-graph
     */
    public DefaultHostGraph(String name, boolean simple) {
        this(name, HostFactory.newInstance(simple));
    }

    /**
     * Constructs an empty host graph, with a given host factory.
     * @param name name of the new host graph
     */
    public DefaultHostGraph(String name, HostFactory factory) {
        super(name, factory.isSimple());
        assert factory != null;
        this.factory = factory;
    }

    /**
     * Creates a new host graph from an existing one, while optionally
     * changing the algebra being used.
     * The host factory is shared.
     * @param graph the non-{@code null} graph to be copied
     * @param family possibly {@code null} set of algebras to draw
     * data values from
     */
    public DefaultHostGraph(HostGraph graph, @Nullable AlgebraFamily family) {
        this(graph.getName(), graph.getFactory());
        HostGraphMorphism morphism = getFactory().createMorphism();
        for (HostNode sn : graph.nodeSet()) {
            HostNode tn;
            if (sn instanceof ValueNode vn && family != null) {
                tn = getFactory()
                    .createNode(family.getAlgebra(vn.getSort()), family.toValue(vn.toTerm()));
            } else {
                tn = sn;
            }
            addNode(tn);
            morphism.putNode(sn, tn);
        }
        for (HostEdge se : graph.edgeSet()) {
            var image = morphism.mapEdge(se);
            assert image != null;
            addEdgeContext(image);
        }
    }

    /**
     * Turns a given graph into a simple host graph,
     * by creating the appropriate types of nodes and edges.
     */
    public DefaultHostGraph(Graph graph) {
        this(graph.getName(), true);
        AGraphMap<Node,Edge,HostNode,HostEdge> map = new AGraphMap<>(getFactory()) {
            // empty
        };
        for (Node node : graph.nodeSet()) {
            HostNode newNode = addNode(node.getNumber());
            map.putNode(node, newNode);
        }
        for (Edge edge : graph.edgeSet()) {
            HostNode sourceImage = map.getNode(edge.source());
            assert sourceImage != null;
            HostNode targetImage = map.getNode(edge.target());
            assert targetImage != null;
            HostEdge edgeImage = addEdge(sourceImage, edge.label().text(), targetImage);
            map.putEdge(edge, edgeImage);
        }
        GraphInfo.transferAll(graph, this, map);
    }

    /**
     * Creates, adds and returns a value node created for a given
     * algebra and value.
     */
    public HostNode addNode(Algebra<?> algebra, Object value) {
        HostNode result = getFactory().createNode(algebra, value);
        addNode(result);
        return result;
    }

    @Override
    public DefaultHostGraph clone() {
        return new DefaultHostGraph(this, null);
    }

    @Override
    public GraphRole getRole() {
        return HOST;
    }

    @Override
    public DefaultHostGraph newGraph(String name) {
        return new DefaultHostGraph(getName(), getFactory());
    }

    @Override
    protected boolean isTypeCorrect(Node node) {
        return node instanceof HostNode;
    }

    @Override
    protected boolean isTypeCorrect(Edge edge) {
        return edge instanceof HostEdge;
    }

    @Override
    public HostFactory getFactory() {
        return this.factory;
    }

    /** The element factory of this host graph. */
    private final HostFactory factory;

    @Override
    public boolean addNode(HostNode node) {
        boolean result = super.addNode(node);
        if (node instanceof ValueNode val && val.getValue() instanceof ErrorValue error) {
            addError(error.getMessage(), node);
        }
        return result;
    }
}
