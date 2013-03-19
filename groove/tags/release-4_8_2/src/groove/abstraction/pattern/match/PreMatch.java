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

import groove.abstraction.Multiplicity;
import groove.abstraction.Multiplicity.MultKind;
import groove.abstraction.pattern.shape.PatternEdge;
import groove.abstraction.pattern.shape.PatternNode;
import groove.abstraction.pattern.shape.PatternShape;
import groove.abstraction.pattern.trans.PatternRule;
import groove.abstraction.pattern.trans.PatternRuleGraph;
import groove.abstraction.pattern.trans.RuleEdge;
import groove.abstraction.pattern.trans.RuleNode;

import java.util.Map.Entry;
import java.util.Set;

/**
 * Pre-match of a pattern rule into a pattern shape.
 *  
 * @author Eduardo Zambon
 */
public final class PreMatch extends Match {

    /** Default constructor. */
    public PreMatch(PatternRule pRule, PatternShape pShape) {
        super(pRule, pShape);
    }

    /** Returns the pattern shape matched. */
    @Override
    public PatternShape getGraph() {
        return (PatternShape) super.getGraph();
    }

    /** Checks if this pre-match respects multiplicities. */
    @Override
    public boolean isValid() {
        PatternShape pShape = getGraph();
        PatternRuleGraph lhs = getRule().lhs();

        // Check node multiplicities.
        for (Entry<PatternNode,Set<RuleNode>> entry : getInverseNodeMap().entrySet()) {
            PatternNode pNode = entry.getKey();
            Multiplicity nMult = pShape.getMult(pNode);
            Set<RuleNode> rNodes = entry.getValue();
            if (!Multiplicity.getNodeSetMult(rNodes).le(nMult)) {
                // Violation of node multiplicity.
                return false;
            }
        }

        // Check edge multiplicities.
        for (Entry<PatternEdge,Set<RuleEdge>> entry : getInverseEdgeMap().entrySet()) {
            // Edge multiplicity per source node.
            Multiplicity eMult = pShape.getMult(entry.getKey());
            // Multiplicity for rule edges.
            for (RuleNode rNode : lhs.nodeSet()) {
                int rEdgeCount = 0;
                for (RuleEdge rEdge : entry.getValue()) {
                    if (lhs.outEdgeSet(rNode).contains(rEdge)) {
                        rEdgeCount++;
                    }
                }
                Multiplicity rMult =
                    Multiplicity.approx(rEdgeCount, rEdgeCount,
                        MultKind.EDGE_MULT);
                if (!rMult.le(eMult)) {
                    // Violation of edge multiplicity.
                    return false;
                }
            }
        }

        return true;
    }

}
