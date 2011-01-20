/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2010 University of Twente
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
package groove.gui.jgraphx;

import groove.view.aspect.AspectEdge;
import groove.view.aspect.AspectGraph;
import groove.view.aspect.AspectNode;

import java.util.HashMap;
import java.util.Map;

import com.mxgraph.view.mxGraph;

/**
 * @author Eduardo Zambon
 */
@SuppressWarnings("all")
public class RuleJGraph extends mxGraph {

    private final AspectGraph ruleGraph;
    private final Map<AspectNode,RuleJNode> nodeMap;
    private final Map<AspectEdge,RuleJEdge> edgeMap;

    public RuleJGraph(AspectGraph ruleGraph) {
        this.ruleGraph = ruleGraph;
        this.nodeMap = new HashMap<AspectNode,RuleJNode>();
        this.edgeMap = new HashMap<AspectEdge,RuleJEdge>();

        this.setStylesheet(JGraphXStyles.ruleStylesheet);

        this.setAllowDanglingEdges(false);
        this.setAutoSizeCells(true);
        this.setCellsDisconnectable(false);
        this.setCellsResizable(false);
        this.setDisconnectOnMove(false);
        this.setDropEnabled(false);

        this.buildGraph();
    }

    private void buildGraph() {
        this.getModel().beginUpdate();
        try {
            for (AspectNode ruleNode : this.ruleGraph.nodeSet()) {
                RuleJNode ruleJNode = new RuleJNode(this, ruleNode);
                this.nodeMap.put(ruleNode, ruleJNode);
                this.addCell(ruleJNode);
            }
            for (AspectEdge ruleEdge : this.ruleGraph.edgeSet()) {
                RuleJEdge ruleJEdge = new RuleJEdge(this, ruleEdge);
                this.edgeMap.put(ruleEdge, ruleJEdge);
                RuleJNode srcJNode = this.getRuleJNode(ruleEdge.source());
                RuleJNode tgtJNode = this.getRuleJNode(ruleEdge.target());
                this.addEdge(ruleJEdge, this.defaultParent, srcJNode, tgtJNode,
                    null);
                srcJNode.addOutEdge(ruleJEdge);
            }
            for (RuleJNode ruleJNode : this.nodeMap.values()) {
                ruleJNode.setInscription();
            }
        } finally {
            // Updates the display
            this.getModel().endUpdate();
        }

    }

    public AspectGraph getRuleGraph() {
        return this.ruleGraph;
    }

    public RuleJNode getRuleJNode(AspectNode node) {
        return this.nodeMap.get(node);
    }
}
