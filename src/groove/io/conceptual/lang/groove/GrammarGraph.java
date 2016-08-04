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
package groove.io.conceptual.lang.groove;

import java.util.HashMap;
import java.util.Map;

import groove.grammar.QualName;
import groove.graph.GraphRole;
import groove.io.conceptual.Acceptor;
import groove.io.conceptual.graph.AbsGraph;
import groove.io.conceptual.graph.AbsNode;

@SuppressWarnings("javadoc")
public class GrammarGraph {
    private final AbsGraph m_graph;
    private final QualName m_graphName;
    private final GraphRole m_graphRole;

    public Map<Acceptor,AbsNode> m_nodes = new HashMap<>();
    /** Node array map. Used by instance models for container values. */
    public Map<Acceptor,AbsNode[]> m_multiNodes = new HashMap<>();

    public GrammarGraph(QualName graphName, GraphRole graphRole) {
        this.m_graph = new AbsGraph(graphName, graphRole);
        this.m_graphName = graphName;
        this.m_graphRole = graphRole;
    }

    public AbsGraph getGraph() {
        // Reset the graph and rebuild it from the nodes that were added to the node map
        this.m_graph.clear();
        for (AbsNode node : this.m_nodes.values()) {
            this.m_graph.addNode(node);
        }
        // Also for node arrays
        for (AbsNode[] nodes : this.m_multiNodes.values()) {
            for (AbsNode node : nodes) {
                this.m_graph.addNode(node);
            }
        }
        return this.m_graph;
    }

    public QualName getQualName() {
        return this.m_graphName;
    }

    public GraphRole getGraphRole() {
        return this.m_graphRole;
    }
}
