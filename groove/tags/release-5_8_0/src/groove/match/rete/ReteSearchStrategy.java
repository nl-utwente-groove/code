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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import groove.grammar.Condition;
import groove.grammar.Condition.Op;
import groove.grammar.EdgeEmbargo;
import groove.grammar.host.HostEdge;
import groove.grammar.host.HostEdgeSet;
import groove.grammar.host.HostGraph;
import groove.grammar.host.HostNode;
import groove.grammar.host.HostNodeSet;
import groove.grammar.rule.RuleToHostMap;
import groove.match.SearchStrategy;
import groove.match.TreeMatch;
import groove.util.Visitor;
import groove.util.Visitor.Collector;

/**
 * @author Arash Jalali
 * @version $Revision $
 */
public class ReteSearchStrategy implements SearchStrategy {
    /**
     * Creates a matching strategy object that uses the RETE algorithm for matching.
     * @param owner The RETE search engine
     * @param condition the condition for which this strategy is to be created; non-{@code null}.
     */
    public ReteSearchStrategy(ReteSearchEngine owner, Condition condition) {
        this.engine = owner;
        this.condition = condition;
        assert condition != null;
    }

    @Override
    public ReteSearchEngine getEngine() {
        return this.engine;
    }

    @Override
    public <T> T traverse(final HostGraph host, RuleToHostMap seedMap,
        Visitor<TreeMatch,T> visitor) {
        assert host.getFactory()
            .getTypeFactory()
            .getGraph() == this.condition.getTypeGraph();
        ReteNetwork network = getEngine().getNetwork();
        assert network != null;

        if (host != network.getState()
            .getHostGraph()) {
            network.processGraph(host);
        }

        assert graphShapesEqual(host, network.getState()
            .getHostGraph());

        //iterate through the conflict set of the production node
        //associated with this condition
        ConditionChecker cc = network.getConditionCheckerNodeFor(getCondition());
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
        final TreeMatch result = new TreeMatch(getCondition(), patternMap);
        ReteSearchStrategy[] subMatchers = getSubMatchers();
        if (subMatchers.length != 0) {
            for (int i = 0; i < subMatchers.length; i++) {

                Condition subCondition = subMatchers[i].getCondition();
                Op subConditionOp = subCondition.getOp();
                if (subConditionOp != Op.NOT) {
                    List<TreeMatch> subMatches = new ArrayList<>();
                    Collector<TreeMatch,List<TreeMatch>> collector =
                        Visitor.newCollector(subMatches);
                    subMatchers[i].traverse(host, patternMap, collector);
                    collector.dispose();
                    Condition.Op op;
                    boolean noMatches = subMatches.isEmpty();
                    boolean positive = subCondition.isPositive();
                    switch (subConditionOp) {
                    case AND:
                        op = noMatches ? Op.TRUE : Op.AND;
                        break;
                    case FORALL:
                        op = noMatches ? (positive ? Op.FALSE : Op.TRUE) : Op.AND;
                        break;
                    case OR:
                        op = noMatches ? Op.FALSE : Op.OR;
                        break;
                    case EXISTS:
                        op = noMatches ? (positive ? Op.FALSE : Op.TRUE) : Op.OR;
                        break;
                    default:
                        assert false;
                        op = null;
                        throw new IllegalStateException();
                    }
                    final TreeMatch subResult = new TreeMatch(op, subCondition);
                    subResult.getSubMatches()
                        .addAll(subMatches);
                    result.addSubMatch(subResult);
                }
            }
        }
        return result;
    }

    private synchronized boolean graphShapesEqual(HostGraph g1, HostGraph g2) {
        boolean result = true;

        HostNodeSet nodes = new HostNodeSet(g1.nodeSet());

        for (HostNode n : nodes) {
            result = g2.nodeSet()
                .contains(n);
            if (!result) {
                System.out.println(
                    "------------------------ReteStrategy.graph comparison failed.--------------------------");
                System.out.println(String.format(
                    "Node %s in RETE-state does not exist in given host graph.", n.toString()));
                break;
            }
        }

        if (result) {
            nodes = new HostNodeSet(g2.nodeSet());
            for (HostNode n : nodes) {
                result = g1.nodeSet()
                    .contains(n);
                if (!result) {
                    System.out.println(
                        "------------------------ReteStrategy.graph comparison failed.--------------------------");
                    System.out.println(String.format(
                        "Node %s in given host graph does not exist in RETE-state graph.",
                        n.toString()));
                    break;
                }
            }
        }
        if (result) {
            HostEdgeSet edges = new HostEdgeSet(g1.edgeSet());
            for (HostEdge e : edges) {
                result = g2.edgeSet()
                    .contains(e);
                if (!result) {
                    System.out.println(
                        "------------------------ReteStrategy.graph comparison failed.--------------------------");
                    System.out.println(String.format(
                        "Edge %s in given RETE-state graph does not exist in given host graph.",
                        e.toString()));
                    break;
                }
            }
        }

        if (result) {
            HostEdgeSet edges = new HostEdgeSet(g2.edgeSet());
            for (HostEdge e : edges) {
                result = g1.edgeSet()
                    .contains(e);
                if (!result) {
                    System.out.println(
                        "------------------------ReteStrategy.graph comparison failed.--------------------------");
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
    private ReteSearchStrategy[] getSubMatchers() {
        if (this.subMatchers == null) {
            List<ReteSearchStrategy> result = new ArrayList<>(getCondition().getSubConditions()
                .size());
            for (Condition subCondition : getCondition().getSubConditions()) {
                if (!(subCondition instanceof EdgeEmbargo)) {
                    result.add(new ReteSearchStrategy(getEngine(), subCondition));
                }
            }
            this.subMatchers = result.toArray(new ReteSearchStrategy[result.size()]);
        }
        return this.subMatchers;
    }

    /** Returns the condition that this strategy is matching. */
    private final Condition getCondition() {
        return this.condition;
    }

    private final Condition condition;
    private final ReteSearchEngine engine;
    private ReteSearchStrategy[] subMatchers;
}
