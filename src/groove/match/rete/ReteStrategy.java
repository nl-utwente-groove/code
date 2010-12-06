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
package groove.match.rete;

import groove.graph.Edge;
import groove.graph.GraphShape;
import groove.graph.Node;
import groove.match.AbstractMatchStrategy;
import groove.rel.RuleToStateMap;
import groove.trans.Condition;
import groove.util.TransformIterator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

/**
 * @author Arash Jalali
 * @version $Revision $
 */
public class ReteStrategy extends AbstractMatchStrategy<RuleToStateMap> {

    private ReteSearchEngine owner;
    private Condition condition = null;

    public ReteStrategy(ReteSearchEngine owner, Condition condition) {
        this.owner = owner;
        this.condition = condition;
    }

    public ReteStrategy(ReteSearchEngine owner) {
        this(owner, null);
    }

    @Override
    public synchronized Iterator<RuleToStateMap> getMatchIter(GraphShape host,
            RuleToStateMap anchorMap) {
        Iterator<RuleToStateMap> result =
            (new ArrayList<RuleToStateMap>()).iterator();
        assert this.owner.getNetwork() != null;

        if (host != this.owner.getNetwork().getState().getHostGraph()) {
            this.owner.getNetwork().processGraph(host);
        }

        assert graphShapesEqual(host,
            this.owner.getNetwork().getState().getHostGraph());

        if ((this.owner.getNetwork() != null) && (this.condition != null)) {
            //iterate through the conflict set of the production node
            //associated with this condition
            ConditionChecker cc =
                this.owner.getNetwork().getConditionCheckerNodeFor(
                    this.condition);
            if (cc != null) {
                if ((anchorMap != null) && (!anchorMap.isEmpty())) {
                    result =
                        new TransformIterator<ReteMatch,RuleToStateMap>(
                            cc.getConflictSetIterator(anchorMap)) {
                            @Override
                            public RuleToStateMap toOuter(ReteMatch matchMap) {
                                return matchMap.toVarNodeEdgeMap();
                            }
                        };
                } else {
                    result =
                        new TransformIterator<ReteMatch,RuleToStateMap>(
                            cc.getConflictSetIterator()) {
                            @Override
                            public RuleToStateMap toOuter(ReteMatch matchMap) {
                                return matchMap.toVarNodeEdgeMap();
                            }
                        };
                }

            }

        }
        return result;
    }

    private synchronized boolean graphShapesEqual(GraphShape g1, GraphShape g2) {
        boolean result = true;

        HashSet<Node> nodes = new HashSet<Node>(g1.nodeSet());

        for (Node n : nodes) {
            result = g2.nodeSet().contains(n);
            if (!result) {
                System.out.println("------------------------ReteStrategy.graph comparison failed.--------------------------");
                System.out.println(String.format(
                    "Node %s in RETE-state does not exist in given host graph.",
                    n.toString()));
                break;
            }
        }

        if (result) {
            nodes = new HashSet<Node>(g2.nodeSet());
            for (Node n : nodes) {
                result = g1.nodeSet().contains(n);
                if (!result) {
                    System.out.println("------------------------ReteStrategy.graph comparison failed.--------------------------");
                    System.out.println(String.format(
                        "Node %s in given host graph does not exist in RETE-state graph.",
                        n.toString()));
                    break;
                }
            }
        }
        if (result) {
            HashSet<Edge> edges = new HashSet<Edge>(g1.edgeSet());
            for (Edge e : edges) {
                result = g2.edgeSet().contains(e);
                if (!result) {
                    System.out.println("------------------------ReteStrategy.graph comparison failed.--------------------------");
                    System.out.println(String.format(
                        "Edge %s in given RETE-state graph does not exist in given host graph.",
                        e.toString()));
                    break;
                }
            }
        }

        if (result) {
            HashSet<Edge> edges = new HashSet<Edge>(g2.edgeSet());
            for (Edge e : edges) {
                result = g1.edgeSet().contains(e);
                if (!result) {
                    System.out.println("------------------------ReteStrategy.graph comparison failed.--------------------------");
                    System.out.println(String.format(
                        "Edge %s in given host graph does not exist in RETE-state graph.",
                        e.toString()));

                    break;
                }
            }
        }
        if (!result) {
            System.out.println("RETE host graph:");
            System.out.println(g1.toString());
            System.out.println("given host graph:");
            System.out.println(g2.toString());
        }
        return result;
    }
}
