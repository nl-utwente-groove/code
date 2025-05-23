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
package nl.utwente.groove.grammar.groovy;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.HashMap;

import nl.utwente.groove.grammar.QualName;
import nl.utwente.groove.grammar.aspect.AspectEdge;
import nl.utwente.groove.grammar.aspect.AspectElement;
import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.aspect.AspectLabel;
import nl.utwente.groove.grammar.aspect.AspectNode;
import nl.utwente.groove.grammar.aspect.AspectParser;
import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.graph.GraphInfo;
import nl.utwente.groove.graph.GraphRole;
import nl.utwente.groove.gui.SimulatorModel;
import nl.utwente.groove.gui.layout.JVertexLayout;
import nl.utwente.groove.gui.layout.LayoutMap;

/** Auxiliary class for the GROOVY plugin. */
public class GraphManager {
    private final SimulatorModel simulatorModel;
    private final HashMap<AspectGraph,LayoutMap> layouts;

    /**
     * Create GraphManager useful for handling graphs in the simulator
     *
     * @param simulatorModel SimulatorModel to use for managing graphs
     */
    public GraphManager(SimulatorModel simulatorModel) {
        this.simulatorModel = simulatorModel;
        this.layouts = new HashMap<>();
    }

    /**
     * Create a new graph with specified name and role. Will not be inserted into the model until doneGraph is called on the result.
     *
     * @param name Name of then ew graph
     * @param role GraphRole of the graph (HOST, RULE or TYPE only)
     * @return The created graph.
     */
    public AspectGraph createGraph(String name, GraphRole role) {
        switch (role) {
        case HOST:
        case RULE:
        case TYPE:
            // all ok
            break;
        default:
            // rest is not permitted
            return null;
        }
        AspectGraph newGraph = new AspectGraph(name, role,
            !this.simulatorModel.getGrammar().getProperties().isHasParallelEdges());

        // Do not insert layout yet, rather postpone until graph is done,
        // otherwise half finished layouts may be created
        this.layouts.put(newGraph, new LayoutMap());
        return newGraph;
    }

    /**
     * Load an (unfixed) copy of the graph with specified name and role.
     *
     * @param name Name of graph to load
     * @param role Role of the graph
     * @return A copy of the graph that can be modified, will overwrite existing graph upon calling doneGraph.
     */
    public AspectGraph loadGraph(String name, GraphRole role) {
        AspectGraph result;
        switch (role) {
        case HOST:
        case RULE:
        case TYPE:
            result = this.simulatorModel
                .getGrammar()
                .getModelGraph(ResourceKind.toResource(role), QualName.parse(name));
            break;
        default:
            result = null;
        }
        return result == null
            ? null
            : result.clone();
    }

    /**
     * Finalise a graph from either createGraph or loadGraph. Do not attempt to call on other graphs.
     *
     * @param graph Graph to finalise and insert into the model.
     * @return true on success, false otherwise
     */
    public boolean doneGraph(AspectGraph graph) {
        if (graph == null) {
            return false;
        }

        // Insert the precreated layout into the model
        LayoutMap layoutMap = this.layouts.remove(graph);
        if (layoutMap != null) {
            GraphInfo.setLayoutMap(graph, layoutMap);
        }

        graph.setFixed();
        try {
            switch (graph.getRole()) {
            case HOST:
            case RULE:
            case TYPE:
                this.simulatorModel
                    .doAddGraph(ResourceKind.toResource(graph.getRole()), graph, false);
                return true;
            default:
                return false;
            }
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Create new node inside graph, without position.
     *
     * @param graph Graph to add node to
     * @param label Label of node. Separate multiple labels with a single newline
     * @return The new node.
     */
    public AspectNode createNode(AspectGraph graph, String label) {
        if (graph == null) {
            return null;
        }

        String[] labels = label.split("\n");
        AspectNode newNode = new AspectNode(graph.nodeCount(), graph);

        for (String sublabel : labels) {
            AspectLabel alabel = AspectParser.getInstance().parse(sublabel, graph.getRole());
            // add self edge
            if (alabel.isEdgeOnly()) {
                AspectEdge newEdge = graph.getFactory().createEdge(newNode, alabel, newNode);
                graph.addEdgeContext(newEdge);
            } else {
                newNode.addLabel(alabel);
            }

        }
        graph.addNode(newNode);
        return newNode;
    }

    /**
     * Create new node inside graph, with position.
     *
     * @param graph Graph to add node to
     * @param label Label of node. Separate multiple labels with a single newline
     * @param location The location to put the new node.
     * @return The new node.
     */
    public AspectNode createNode(AspectGraph graph, String label, Point2D location) {
        if (graph == null) {
            return null;
        }

        AspectNode newNode = createNode(graph, label);

        JVertexLayout layout = new JVertexLayout(
            new java.awt.Rectangle((int) location.getX(), (int) location.getY(), 60, 20));
        LayoutMap layoutMap = this.layouts.get(graph);
        if (layoutMap == null) {
            layoutMap = GraphInfo.getLayoutMap(graph);
        }
        if (layoutMap != null) {
            layoutMap.putNode(newNode, layout);
        }
        return newNode;
    }

    /**
     * Create a new edge between two nodes in the given graph.
     *
     * @param graph Graph to add edge to
     * @param nodeSource Source node of edge
     * @param nodeTarget Target node of edge
     * @param label Label of edge. Separate multiple labels with a single newline, this will create multiple edges.
     * @return The created edge. If multiple edges are created, only the first created edge is returned.
     */
    public AspectElement createEdge(AspectGraph graph, AspectNode nodeSource, AspectNode nodeTarget,
                                    String label) {
        if (graph == null) {
            return null;
        }

        AspectElement resultEdge = null;

        String[] labels = label.split("\n");
        for (String sublabel : labels) {
            AspectLabel alabel = AspectParser.getInstance().parse(sublabel, graph.getRole());
            if (alabel.isEdgeOnly()) {
                AspectEdge newEdge = graph.getFactory().createEdge(nodeSource, alabel, nodeTarget);
                graph.addEdgeContext(newEdge);
                if (resultEdge == null) {
                    resultEdge = newEdge;
                }
            } else {
                // error
            }
        }
        // just return the first edge
        return resultEdge;
    }

    /**
     * Return the node with the specified internal id.
     *
     * @param graph Graph to find node in
     * @param id Id of node to find
     * @return The Node with the given id, null if it could not be found
     */
    public AspectNode getNode(AspectGraph graph, int id) {
        if (graph == null) {
            return null;
        }

        for (AspectNode node : graph.nodeSet()) {
            if (node.getNumber() == id) {
                return node;
            }
        }
        return null;
    }

}
