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

import static groove.graph.GraphRole.RULE;
import groove.graph.GraphRole;
import groove.graph.NodeSetEdgeSetGraph;

import java.util.Set;

/**
 * Special class of graphs that can appear (only) in rules.
 * Rule graphs may only have {@link RuleEdge}s.
 * @author Arend Rensink
 * @version $Revision $
 */
public class RuleGraph extends NodeSetEdgeSetGraph<RuleNode,RuleEdge> {
    /**
     * Constructs a new, empty rule graph.
     * @param name the name of the new rule graph
     */
    public RuleGraph(String name) {
        super(name);
    }

    /**
     * Clones a given rule graph.
     */
    public RuleGraph(RuleGraph graph) {
        super(graph);
    }

    @Override
    public GraphRole getRole() {
        return RULE;
    }

    @Override
    public RuleGraph clone() {
        return new RuleGraph(this);
    }

    @Override
    public RuleGraph newGraph(String name) {
        return new RuleGraph(getName());
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<RuleEdge> edgeSet() {
        return (Set<RuleEdge>) super.edgeSet();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<RuleNode> nodeSet() {
        return (Set<RuleNode>) super.nodeSet();
    }

    @Override
    public RuleFactory getFactory() {
        return RuleFactory.instance();
    }
}
