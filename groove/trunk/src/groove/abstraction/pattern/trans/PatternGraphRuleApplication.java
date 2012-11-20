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
import groove.abstraction.pattern.shape.PatternGraph;
import groove.abstraction.pattern.shape.PatternNode;
import groove.abstraction.pattern.shape.TypeEdge;
import groove.util.Duo;
import groove.util.Pair;

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
        RuleNode rNode = this.pRule.getCreatorNodes()[0];
        createPattern(this.pGraph, rNode, true);
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
        // and the edges will be gone as well. The same holds for creation.
        erasePatterns(host);
        createPatterns(host);
        // This is a normal rule application. Close the transformed graph.
        close(host);
        return host;
    }

    private void erasePatterns(PatternGraph host) {
        for (RuleNode rNode : this.pRule.getEraserNodes()) {
            host.deletePattern(this.match.getNode(rNode));
        }
    }

    private void createPatterns(PatternGraph host) {
        // First add layer 0 patterns.
        for (RuleNode rNode : this.pRule.getCreatorNodes()) {
            if (!rNode.isNodePattern()) {
                continue;
            }
            PatternNode newNode = host.addNodePattern(rNode.getType());
            this.match.putNode(rNode, newNode);
        }

        // Then add layer 1 patterns.
        for (RuleNode rNode : this.pRule.getCreatorNodes()) {
            if (!rNode.isEdgePattern()) {
                continue;
            }
            createPattern(host, rNode, false);
        }
    }

    private void createPattern(PatternGraph host, RuleNode rNode,
            boolean closure) {
        Duo<RuleEdge> inEdges = this.pRule.rhs().getIncomingEdges(rNode);

        RuleEdge r1 = inEdges.one();
        RuleEdge r2 = inEdges.two();
        TypeEdge m1 = r1.getType();
        TypeEdge m2 = r2.getType();
        PatternNode p1 = this.match.getNode(r1.source());
        PatternNode p2 = this.match.getNode(r2.source());

        Pair<PatternNode,Duo<PatternEdge>> pair;
        if (closure) {
            pair = host.closePattern(m1, m2, p1, p2);
        } else {
            pair = host.addEdgePattern(m1, m2, p1, p2);
        }

        PatternNode newNode = pair.one();
        PatternEdge d1 = pair.two().one();
        PatternEdge d2 = pair.two().two();
        this.match.putNode(rNode, newNode);
        this.match.putEdge(r1, d1);
        this.match.putEdge(r2, d2);
    }

    private void close(PatternGraph host) {
        this.pRule.getTypeGraph().close(host);
    }

}
