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

import groove.abstraction.pattern.match.Match;
import groove.abstraction.pattern.shape.PatternEdge;
import groove.abstraction.pattern.shape.PatternFactory;
import groove.abstraction.pattern.shape.PatternGraph;
import groove.abstraction.pattern.shape.PatternNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Application of a matched pattern graph transformation rule.
 * 
 * @author Eduardo Zambon
 */
public final class PatternGraphRuleApplication {

    private final PatternGraph pGraph;
    private final PatternRule pRule;
    private final Match match;

    /** Default constructor. */
    public PatternGraphRuleApplication(PatternGraph pGraph, Match match) {
        this.pGraph = pGraph;
        this.match = match;
        this.pRule = match.getRule();
    }

    /** Special method for computing the closure. */
    public void transformWithClosureRule() {
        assert this.pRule.isClosure();
        createNodes(this.pGraph);
        createEdges(this.pGraph);
    }

    /** Executes the rule application and returns the result. */
    public PatternGraph transform(boolean inPlace) {
        if (!this.pRule.isModifying()) {
            return this.pGraph;
        } // else rule is modifying.
        if (inPlace) {
            return transform(this.pGraph);
        } else {
            return transform(this.pGraph.clone());
        }
    }

    /** Transforms and returns the given pattern graph.*/
    private PatternGraph transform(PatternGraph host) {
        assert !this.pRule.isClosure();
        // Pattern graphs are a particular graph in a sense that no pattern
        // edges can be erased without removing also the source or target
        // pattern nodes. This means that we need only to delete pattern nodes
        // and the edges will be gone as well. Element creation goes are usual. 
        eraseNodes(host);
        createNodes(host);
        createEdges(host);
        // This is a normal rule application. Close the transformed graph.
        this.pRule.getTypeGraph().close(host);
        return host;
    }

    private void eraseNodes(PatternGraph host) {
        for (PatternNode delNode : computeErasedNodes(host)) {
            host.removeNode(delNode);
        }
    }

    private Set<PatternNode> computeErasedNodes(PatternGraph host) {
        List<PatternNode> toTraverse = new ArrayList<PatternNode>();
        // The initial list of nodes to be deleted comes from the match.
        for (RuleNode rNode : this.pRule.getEraserNodes()) {
            toTraverse.add(this.match.getNode(rNode));
        }
        return host.getDownwardTraversal(toTraverse);
    }

    private void createNodes(PatternGraph host) {
        for (RuleNode rNode : this.pRule.getCreatorNodes()) {
            PatternNode newNode = host.createNode(rNode.getType());
            host.addNode(newNode);
            this.match.putNode(rNode, newNode);
        }
    }

    private void createEdges(PatternGraph host) {
        PatternFactory factory = host.getFactory();
        for (RuleEdge rEdge : this.pRule.getCreatorEdges()) {
            PatternNode source = this.match.getNode(rEdge.source());
            PatternNode target = this.match.getNode(rEdge.target());
            assert source != null && target != null;
            PatternEdge newEdge =
                factory.createEdge(source, rEdge.getType(), target);
            host.addEdge(newEdge);
        }
    }
}
