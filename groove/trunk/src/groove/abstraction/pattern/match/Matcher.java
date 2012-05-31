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
package groove.abstraction.pattern.match;

import groove.abstraction.MyHashMap;
import groove.abstraction.MyHashSet;
import groove.abstraction.pattern.shape.PatternEdge;
import groove.abstraction.pattern.shape.PatternGraph;
import groove.abstraction.pattern.shape.PatternNode;
import groove.abstraction.pattern.trans.PatternRule;
import groove.abstraction.pattern.trans.PatternRuleGraph;
import groove.abstraction.pattern.trans.RuleEdge;
import groove.abstraction.pattern.trans.RuleNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Matcher for pattern graph rules. 
 * 
 * @author Eduardo Zambon
 */
public final class Matcher {

    private final PatternRule pRule;

    /** Default constructor. */
    public Matcher(PatternRule pRule) {
        this.pRule = pRule;
    }

    /** Returns a list of all matches found on the given graph. */
    public List<Match> findMatches(PatternGraph pGraph) {
        if (this.pRule.isClosure()) {
            return findMatchesForClosureRule(pGraph);
        } else {
            return findMatchesForNormalRule(pGraph);
        }
    }

    private List<Match> findMatchesForClosureRule(PatternGraph pGraph) {
        assert this.pRule.isClosure();
        List<Match> result = new ArrayList<Match>();

        PatternRuleGraph lhs = this.pRule.lhs();
        Set<RuleNode> rNodes = lhs.getLayerNodes(lhs.depth());
        Map<RuleNode,List<PatternNode>> candidateMap =
            new MyHashMap<RuleNode,List<PatternNode>>();
        for (RuleNode rNode : rNodes) {
            List<PatternNode> candidates = new LinkedList<PatternNode>();
            for (PatternNode pNode : pGraph.getLayerNodes(rNode.getLayer())) {
                // Object equality is sufficient.
                if (pNode.getType() == rNode.getType()) {
                    candidates.add(pNode);
                }
            }
            candidateMap.put(rNode, candidates);
        }

        return result;
    }

    private List<Match> findMatchesForNormalRule(PatternGraph pGraph) {
        assert !this.pRule.isClosure();
        List<Match> result = new ArrayList<Match>();

        // Since we have a normal rule there is only one pattern at the
        // bottom of the LHS. This means that we only have to search for nodes
        // of the type of this pattern and the number of such nodes is the
        // number of matches we have.
        PatternRuleGraph lhs = this.pRule.lhs();
        Set<RuleNode> rNodes = lhs.getLayerNodes(lhs.depth());
        assert rNodes.size() == 1;
        RuleNode rNode = rNodes.iterator().next();
        for (PatternNode pNode : pGraph.getLayerNodes(rNode.getLayer())) {
            // Object equality is sufficient because we only have one type graph.
            if (pNode.getType() == rNode.getType()) {
                // We found a match.
                Match match = new Match(this.pRule, pGraph);
                match.setBottomMatch(rNode, pNode);
                result.add(match);
            }
        }
        // Now that the bottom of each match is set, complete each match.
        completeMatches(result);

        return result;
    }

    private void completeMatches(List<Match> matches) {
        for (Match match : matches) {
            completeMatch(match);
        }
    }

    private void completeMatch(Match match) {
        PatternRuleGraph lhs = this.pRule.lhs();
        List<RuleNode> queue = new LinkedList<RuleNode>();
        queue.add(match.getBottom());
        while (!queue.isEmpty()) {
            RuleNode rNode = queue.get(0);
            PatternNode pNode = match.getNode(rNode);
            Set<RuleEdge> rEdges = lhs.inEdgeSet(rNode);
            Set<PatternEdge> pEdges = new MyHashSet<PatternEdge>();
            pEdges.addAll(match.getGraph().inEdgeSet(pNode));
            rEdgeLoop: for (RuleEdge rEdge : rEdges) {
                Iterator<PatternEdge> it = pEdges.iterator();
                while (it.hasNext()) {
                    PatternEdge pEdge = it.next();
                    if (pEdge.getType() == rEdge.getType()) {
                        it.remove();
                        match.putEdge(rEdge, pEdge);
                        match.putNode(rEdge.source(), pEdge.source());
                        continue rEdgeLoop;
                    }
                }
            }
        }
        assert match.isFinished();
    }
}
