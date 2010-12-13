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
package groove.gui.jgraph;

import groove.graph.DefaultEdge;
import groove.graph.DefaultGraph;
import groove.graph.DefaultNode;
import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.GraphShape;
import groove.graph.Node;
import groove.match.rete.CompositeConditionChecker;
import groove.match.rete.ConditionChecker;
import groove.match.rete.DisconnectedSubgraphChecker;
import groove.match.rete.EdgeCheckerNode;
import groove.match.rete.NodeCheckerNode;
import groove.match.rete.ProductionNode;
import groove.match.rete.ReteNetwork;
import groove.match.rete.ReteNetworkNode;
import groove.match.rete.RootNode;
import groove.match.rete.SubgraphCheckerNode;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Arash Jalali
 * @version $Revision $
 */
public class ReteJModel extends GraphJModel {
    private ReteNetwork network;
    private DefaultGraph graph;
    private HashMap<ReteNetworkNode,Node> map =
        new HashMap<ReteNetworkNode,Node>();

    public ReteJModel(ReteNetwork network) {
        super();
        this.network = network;
        makeGraphFromNetwork();
    }

    private void makeGraphFromNetwork() {
        this.graph = new DefaultGraph();
        Node rootNode = DefaultNode.createNode();
        this.map.put(this.network.getRoot(), rootNode);
        Edge rootFlag = DefaultEdge.createEdge(rootNode, "ROOT", rootNode);
        this.graph.addNode(rootNode);
        this.graph.addEdge(rootFlag);
        addChildren(this.network.getRoot());
        addSubConditionEdges();
    }

    private void addSubConditionEdges() {
        for (ConditionChecker cc : this.network.getConditonCheckerNodes()) {
            ConditionChecker parent = cc.getParent();
            if (parent != null) {
                String l = "subcondition";
                this.graph.addEdge(this.map.get(cc), l, this.map.get(parent));
            }
        }

        for (CompositeConditionChecker cc : this.network.getCompositeConditonCheckerNodes()) {
            ConditionChecker parent = cc.getParent();
            if (parent != null) {
                String l = "NAC";
                this.graph.addEdge(this.map.get(cc), l, this.map.get(parent));
            }
        }

    }

    private void addChildren(ReteNetworkNode nnode) {
        Node jNode = this.map.get(nnode);
        boolean navigate;
        if (jNode != null) {
            ReteNetworkNode previous = null;
            int repeatCounter = 0;
            for (ReteNetworkNode childNNode : nnode.getSuccessors()) {
                repeatCounter =
                    (previous == childNNode) ? repeatCounter + 1 : 0;
                navigate = false;
                Node childJNode = this.map.get(childNNode);
                if (childJNode == null) {
                    childJNode = DefaultNode.createNode();
                    Edge[] flags = makeNNodeLabels(childNNode, childJNode);
                    this.graph.addNode(childJNode);
                    for (Edge f : flags) {
                        this.graph.addEdge(f);
                    }
                    this.map.put(childNNode, childJNode);
                    navigate = true;
                }

                if (childNNode instanceof SubgraphCheckerNode) {
                    if (childNNode.getAntecedents().get(0) != childNNode.getAntecedents().get(
                        1)) {
                        if (nnode == childNNode.getAntecedents().get(0)) {
                            this.graph.addEdge(jNode, "left", childJNode);
                        } else {
                            this.graph.addEdge(jNode, "right", childJNode);
                        }
                    } else {
                        this.graph.addEdge(jNode, "left", childJNode);
                        this.graph.addEdge(jNode, "right", childJNode);
                    }
                } else if ((childNNode instanceof ConditionChecker)
                    && (repeatCounter > 0)) {
                    this.graph.addEdge(jNode, "receive_" + repeatCounter,
                        childJNode);
                } else {
                    this.graph.addEdge(jNode, "receive", childJNode);
                }

                if (navigate) {
                    addChildren(childNNode);
                }
                previous = childNNode;
            }
        }
    }

    private Edge[] makeNNodeLabels(ReteNetworkNode nnode, Node source) {
        ArrayList<Edge> result = new ArrayList<Edge>();
        if (nnode instanceof RootNode) {
            result.add(DefaultEdge.createEdge(source, "ROOT", source));
        } else if (nnode instanceof NodeCheckerNode) {
            result.add(DefaultEdge.createEdge(source, "Node Checker", source));
            result.add(DefaultEdge.createEdge(source,
                ((NodeCheckerNode) nnode).getNode().toString(), source));
        } else if (nnode instanceof EdgeCheckerNode) {
            result.add(DefaultEdge.createEdge(source, "Edge Checker", source));
            result.add(DefaultEdge.createEdge(source,
                ((EdgeCheckerNode) nnode).getEdge().toString(), source));
        } else if (nnode instanceof SubgraphCheckerNode) {
            String[] lines = nnode.toString().split("\n");
            //result.add(new DefaultFlag(source, "- Subgraph Checker"));
            for (String s : lines) {
                result.add(DefaultEdge.createEdge(source, s, source));
            }
        } else if (nnode instanceof DisconnectedSubgraphChecker) {
            result.add(DefaultEdge.createEdge(source,
                "DisconnectedSubgraphChecker", source));
        } else if (nnode instanceof ProductionNode) {
            result.add(DefaultEdge.createEdge(source, "- Production Node "
                + (((ConditionChecker) nnode).isIndexed() ? "(idx)" : "()"),
                source));
            result.add(DefaultEdge.createEdge(source, "-"
                + ((ProductionNode) nnode).getCondition().getName().toString(),
                source));
            for (int i = 0; i < ((ProductionNode) nnode).getPattern().length; i++) {
                Element e = ((ProductionNode) nnode).getPattern()[i];
                result.add(DefaultEdge.createEdge(source,
                    "--" + i + " " + e.toString(), source));
            }
        } else if (nnode instanceof ConditionChecker) {
            result.add(DefaultEdge.createEdge(source, "- Condition Checker "
                + (((ConditionChecker) nnode).isIndexed() ? "(idx)" : "()"),
                source));
            for (int i = 0; i < ((ConditionChecker) nnode).getPattern().length; i++) {
                Element e = ((ConditionChecker) nnode).getPattern()[i];
                result.add(DefaultEdge.createEdge(source,
                    "--" + i + " " + e.toString(), source));
            }
        }
        DefaultEdge[] res = new DefaultEdge[result.size()];
        return result.toArray(res);
    }

    @Override
    public boolean hasError(JCell cell) {
        return false;
    }

    @Override
    public GraphShape getGraph() {
        return this.graph;
    }
}
