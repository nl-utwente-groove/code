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

import groove.graph.GraphRole;
import groove.io.conceptual.graph.AbsGraph;
import groove.io.conceptual.graph.AbsNode;
import groove.io.conceptual.graph.AbsNodeIter;

import java.util.LinkedList;
import java.util.List;

/** Extension of an {@link AbsGraph} with graph name and role. */
public class GrammarGraph {
    /** Constructs an initially empty graph with a given name and role. */
    public GrammarGraph(String graphName, GraphRole graphRole) {
        this.m_graphName = graphName;
        this.m_graphRole = graphRole;
    }

    /** Converts this pre-graph into an {@link AbsGraph}. */
    public AbsGraph getGraph() {
        AbsGraph result = new AbsGraph();
        for (AbsNodeIter iter : this.m_nodes) {
            for (AbsNode node : iter) {
                result.addNode(node);
            }
        }
        return result;
    }

    /** Adds a set of nodes to this pre-graph. */
    public void addNodes(AbsNodeIter iter) {
        this.m_nodes.add(iter);
    }

    private final List<AbsNodeIter> m_nodes = new LinkedList<>();

    /** Returns the name of this pre-graph. */
    public String getGraphName() {
        return this.m_graphName;
    }

    private final String m_graphName;

    /** Returns the role of this pre-graph. */
    public GraphRole getGraphRole() {
        return this.m_graphRole;
    }

    private final GraphRole m_graphRole;
}
