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
package groove.abstraction.pattern.trans;

import groove.abstraction.pattern.shape.AbstractPatternGraph;
import groove.graph.Node;

import java.util.Set;

/**
 * Pattern graphs that compose rules.
 * 
 * @author Eduardo Zambon
 */
public final class PatternRuleGraph extends
        AbstractPatternGraph<RuleNode,RuleEdge> {

    /** Default constructor. */
    public PatternRuleGraph(String name) {
        super(name);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<RuleNode> nodeSet() {
        return (Set<RuleNode>) super.nodeSet();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<RuleEdge> edgeSet() {
        return (Set<RuleEdge>) super.edgeSet();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<RuleEdge> inEdgeSet(Node node) {
        return (Set<RuleEdge>) super.inEdgeSet(node);
    }

}