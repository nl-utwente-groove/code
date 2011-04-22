/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2010 University of Twente
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

import groove.match.AbstractMatchStrategy;
import groove.trans.Condition;
import groove.trans.HostEdge;
import groove.trans.HostGraph;
import groove.trans.HostNode;
import groove.trans.RuleToHostMap;
import groove.util.TransformIterator;
import groove.util.Visitor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

/**
 * @author Arash Jalali
 * @version $Revision $
 */
public class ReteStrategy extends AbstractMatchStrategy<RuleToHostMap> {
    private final ReteSearchEngine owner;
    private final Condition condition;

    /**
     * Creates a matching strategy object that uses the RETE algorithm for matching.  
     * @param owner The RETE search engine
     * @param condition the condition for which this strategy is to be created; non-{@code null}.
     */
    public ReteStrategy(ReteSearchEngine owner, Condition condition) {
        this.owner = owner;
        this.condition = condition;
        assert condition != null;
    }

    @Override
    public synchronized Iterator<RuleToHostMap> getMatchIter(
            final HostGraph host, RuleToHostMap anchorMap) {
        Iterator<RuleToHostMap> result =
            (new ArrayList<RuleToHostMap>()).iterator();
        assert this.owner.getNetwork() != null;

        if (host != this.owner.getNetwork().getState().getHostGraph()) {
            this.owner.getNetwork().processGraph(host);
        }

        assert graphShapesEqual(host,
            this.owner.getNetwork().getState().getHostGraph());

        if (this.owner.getNetwork() != null) {
            //iterate through the conflict set of the production node
            //associated with this condition
            ConditionChecker cc =
                this.owner.getNetwork().getConditionCheckerNodeFor(
                    this.condition);
            if (cc != null) {
                if ((anchorMap != null) && (!anchorMap.isEmpty())) {
                    result =
                        new TransformIterator<ReteMatch,RuleToHostMap>(
                            cc.getConflictSetIterator(anchorMap)) {
                            @Override
                            public RuleToHostMap toOuter(ReteMatch matchMap) {
                                return matchMap.toRuleToHostMap(host.getFactory());
                            }
                        };
                } else {
                    result =
                        new TransformIterator<ReteMatch,RuleToHostMap>(
                            cc.getConflictSetIterator()) {
                            @Override
                            public RuleToHostMap toOuter(ReteMatch matchMap) {
                                return matchMap.toRuleToHostMap(host.getFactory());
                            }
                        };
                }

            }

        }
        return result;
    }

    @Override
    public <T> T visitAll(final HostGraph host, RuleToHostMap anchorMap,
            Visitor<RuleToHostMap,T> visitor) {
        assert this.owner.getNetwork() != null;

        if (host != this.owner.getNetwork().getState().getHostGraph()) {
            this.owner.getNetwork().processGraph(host);
        }

        assert graphShapesEqual(host,
            this.owner.getNetwork().getState().getHostGraph());

        if (this.owner.getNetwork() != null) {
            //iterate through the conflict set of the production node
            //associated with this condition
            ConditionChecker cc =
                this.owner.getNetwork().getConditionCheckerNodeFor(
                    this.condition);
            if (cc != null) {
                Iterator<ReteMatch> iter;
                if ((anchorMap != null) && (!anchorMap.isEmpty())) {
                    iter = cc.getConflictSetIterator(anchorMap);
                } else {
                    iter = cc.getConflictSetIterator();
                }
                boolean cont = true;
                while (cont && iter.hasNext()) {
                    ReteMatch matchMap = iter.next();
                    cont =
                        visitor.visit(matchMap.toRuleToHostMap(host.getFactory()));
                }
            }
        }
        return visitor.getResult();
    }

    private synchronized boolean graphShapesEqual(HostGraph g1, HostGraph g2) {
        boolean result = true;

        HashSet<HostNode> nodes = new HashSet<HostNode>(g1.nodeSet());

        for (HostNode n : nodes) {
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
            nodes = new HashSet<HostNode>(g2.nodeSet());
            for (HostNode n : nodes) {
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
            HashSet<HostEdge> edges = new HashSet<HostEdge>(g1.edgeSet());
            for (HostEdge e : edges) {
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
            HashSet<HostEdge> edges = new HashSet<HostEdge>(g2.edgeSet());
            for (HostEdge e : edges) {
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
