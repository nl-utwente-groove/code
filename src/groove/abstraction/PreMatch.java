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
package groove.abstraction;

import groove.graph.GraphShape;
import groove.graph.Node;
import groove.graph.NodeEdgeMap;
import groove.trans.Rule;
import groove.trans.RuleMatch;

import java.util.HashSet;
import java.util.Set;

/**
 * EDUARDO
 * @author Eduardo Zambon
 * @version $Revision $
 */
public class PreMatch {

    // ------------------------------------------------------------------------
    // Static methods
    // ------------------------------------------------------------------------

    /** EDUARDO */
    public static Set<RuleMatch> getPreMatches(GraphShape host, Rule rule) {
        assert host instanceof Shape : "Cannot use abstract methods to non-abstract graphs.";
        Shape shape = (Shape) host;
        Set<RuleMatch> preMatches = new HashSet<RuleMatch>();
        // We use the normal matching algorithms for finding matches.
        for (RuleMatch match : rule.getMatches(shape, null)) {
            // Since we have non-injective matching of the LHS of the rule
            // we need to check if the multiplicities are respected. 

            NodeEdgeMap map = match.getElementMap();

            // Check node multiplicities.
            boolean complyToNodeMult = true;
            for (ShapeNode nodeS : shape.nodeSet()) {
                Set<Node> nodesG = Util.getReverseNodeMap(map, nodeS);
                Multiplicity nSMult = shape.getNodeMult(nodeS);
                if (!Multiplicity.getNodeSetMult(nodesG).isAtMost(nSMult)) {
                    // Violation of node multiplicity.
                    complyToNodeMult = false;
                    break;
                }
            }

            // Check edge multiplicities.
            boolean complyToEdgeMult = true;
            // EZ says to himself: this check was commented out because it
            // should only be performed after materialisation.
            /*if (complyToNodeMult) {
                for (EdgeSignature es : shape.getEdgeSigSet()) {
                    // Outgoing multiplicities.
                    Set<Edge> edgesG = Util.getReverseOutEdgeMap(map, es);
                    Multiplicity esOutMult = shape.getEdgeSigOutMult(es);
                    if (!Multiplicity.getEdgeSetMult(edgesG).isAtMost(esOutMult)) {
                        // Violation of edge out multiplicity.
                        complyToEdgeMult = false;
                        break;
                    }

                    // Incoming multiplicities.
                    edgesG = Util.getReverseInEdgeMap(map, es);
                    Multiplicity esInMult = shape.getEdgeSigInMult(es);
                    if (!Multiplicity.getEdgeSetMult(edgesG).isAtMost(esInMult)) {
                        // Violation of edge in multiplicity.
                        complyToEdgeMult = false;
                        break;
                    }
                }
            }*/

            if (complyToNodeMult && complyToEdgeMult) {
                // We have a pre-match.
                preMatches.add(match);
            }
        }
        return preMatches;
    }
}
