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

import groove.graph.DefaultGraph;
import groove.graph.Edge;
import groove.graph.ElementFactory;
import groove.graph.Label;
import groove.graph.Node;

import java.util.Set;

/**
 * Special class of graphs that can appear (only) in rules.
 * Rule graphs may only have {@link RuleEdge}s.
 * @author Arend Rensink
 * @version $Revision $
 */
public class RuleGraph extends DefaultGraph {
    /**
     * Constructs a new, empty rule graph.
     */
    public RuleGraph() {
        // empty
    }

    /**
     * Clones a given rule graph.
     */
    public RuleGraph(RuleGraph graph) {
        super(graph);
    }

    @Override
    public RuleGraph clone() {
        return new RuleGraph(this);
    }

    @Override
    public RuleGraph newGraph() {
        return new RuleGraph();
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

    @SuppressWarnings("unchecked")
    @Override
    public Set<RuleEdge> edgeSet(Node node) {
        return (Set<RuleEdge>) super.edgeSet(node);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<RuleEdge> outEdgeSet(Node node) {
        return (Set<RuleEdge>) super.outEdgeSet(node);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<RuleEdge> inEdgeSet(Node node) {
        return (Set<RuleEdge>) super.inEdgeSet(node);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<RuleEdge> labelEdgeSet(Label label) {
        return (Set<RuleEdge>) super.labelEdgeSet(label);
    }

    @Override
    public RuleNode addNode() {
        return (RuleNode) super.addNode();
    }

    @Override
    protected boolean isTypeCorrect(Node edge) {
        return edge instanceof RuleNode;
    }

    @Override
    protected boolean isTypeCorrect(Edge edge) {
        return edge instanceof RuleEdge;
    }

    @Override
    public ElementFactory<?,?,?> getFactory() {
        return RuleFactory.INSTANCE;
    }
}
