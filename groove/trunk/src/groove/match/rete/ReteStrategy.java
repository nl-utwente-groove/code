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

import groove.match.MatchStrategy;
import groove.match.TreeMatch;
import groove.trans.Condition;
import groove.trans.Condition.Op;
import groove.trans.EdgeEmbargo;
import groove.trans.HostEdge;
import groove.trans.HostGraph;
import groove.trans.HostNode;
import groove.trans.RuleToHostMap;
import groove.util.Visitor;
import groove.util.Visitor.Collector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * @author Arash Jalali
 * @version $Revision $
 */
public class ReteStrategy extends MatchStrategy<TreeMatch> {
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
    public <T> T traverse(final HostGraph host, RuleToHostMap seedMap,
            Visitor<TreeMatch,T> visitor) {
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
                Iterator<ReteSimpleMatch> iter;
                if ((seedMap != null) && (!seedMap.isEmpty())) {
                    iter = cc.getConflictSetIterator(seedMap);
                } else {
                    iter = cc.getConflictSetIterator();
                }
                boolean cont = true;
                while (cont && iter.hasNext()) {
                    cont = visitor.visit(createTreeMatch(iter.next(), host));
                }
            }
        }
        return visitor.getResult();
    }

    /**
     * Constructs a tree match from a top level pattern match.
     * @param host the host graph into which the condition is matched
     * @param matchMap matching of the condition pattern
     * @return a tree match constructed by extending {@code patternMap} with
     * matchings of all subconditions 
     */
    private TreeMatch createTreeMatch(ReteSimpleMatch matchMap, HostGraph host) {
        RuleToHostMap patternMap = matchMap.toRuleToHostMap(host.getFactory());
        final TreeMatch result = new TreeMatch(this.condition, patternMap);
        ReteStrategy[] subMatchers = getSubMatchers();
        if (subMatchers.length != 0) {
            for (int i = 0; i < subMatchers.length; i++) {
                Condition subCondition = subMatchers[i].condition;
                Condition.Op op;
                switch (subCondition.getOp()) {
                case AND:
                case OR:
                    op = subCondition.getOp();
                    break;
                case EXISTS:
                    op = Op.OR;
                    break;
                case FORALL:
                    op = Op.AND;
                    break;
                case NOT:
                    continue;
                default:
                    assert false;
                    op = null;
                }
                final TreeMatch subResult =
                    new TreeMatch(op, subCondition, null);
                // add matches for the subconditions
                Collector<TreeMatch,?> collector =
                    Visitor.newCollector(subResult.getSubMatches());
                subMatchers[i].traverse(host, patternMap, collector);
                collector.dispose();
                result.addSubMatch(subResult);
            }
        }
        return result;
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

    /** 
     * Lazily constructs and returns an array of match strategies for all 
     * non-trivial subconditions.
     */
    private ReteStrategy[] getSubMatchers() {
        if (this.subMatchers == null) {
            List<ReteStrategy> result =
                new ArrayList<ReteStrategy>(
                    this.condition.getSubConditions().size());
            for (Condition subCondition : this.condition.getSubConditions()) {
                if (!(subCondition instanceof EdgeEmbargo)) {
                    result.add(new ReteStrategy(this.owner, subCondition));
                }
            }
            this.subMatchers = result.toArray(new ReteStrategy[result.size()]);
        }
        return this.subMatchers;
    }

    private final ReteSearchEngine owner;
    private final Condition condition;
    private ReteStrategy[] subMatchers;
}
