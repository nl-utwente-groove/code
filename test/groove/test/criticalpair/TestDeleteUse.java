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
package groove.test.criticalpair;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import groove.grammar.QualName;
import groove.grammar.Rule;
import groove.grammar.host.DefaultHostGraph;
import groove.grammar.host.HostEdge;
import groove.grammar.host.HostFactory;
import groove.grammar.host.HostGraphMorphism;
import groove.grammar.host.HostNode;
import groove.grammar.model.GrammarModel;
import groove.grammar.rule.RuleEdge;
import groove.grammar.rule.RuleGraph;
import groove.grammar.rule.RuleNode;
import groove.grammar.rule.RuleToHostMap;
import groove.grammar.type.TypeLabel;
import groove.transform.BasicEvent;
import groove.transform.RuleApplication;
import groove.transform.RuleEvent.Reuse;
import groove.transform.criticalpair.CriticalPair;
import groove.util.parse.FormatException;

/**
 * @author Ruud Welling
 */
@SuppressWarnings("javadoc")
public class TestDeleteUse {
    @Test
    public void testBasic() {
        String grammar = "junit/criticalpair/basic.gps/";
        File grammarFile = new File(grammar);
        GrammarModel view = null;
        try {
            view = GrammarModel.newInstance(grammarFile, false);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Rule addNodeAndEdge = getSimpleRule("addNodeAndEdge", view);
        Rule constantNode = getSimpleRule("constantNode", view);
        Rule constant_3_clique = getSimpleRule("constant_3_clique", view);
        Rule constantSelfEdge = getSimpleRule("constantSelfEdge", view);
        Rule deleteEdge = getSimpleRule("deleteEdge", view);
        Rule deleteNode = getSimpleRule("deleteNode", view);
        Rule deleteSelfEdge = getSimpleRule("deleteSelfEdge", view);

        Set<CriticalPair> pairs = CriticalPair.computeCriticalPairs(addNodeAndEdge, addNodeAndEdge);
        assertTrue(pairs.size() == 0);
        pairs = CriticalPair.computeCriticalPairs(addNodeAndEdge, constantNode);
        assertTrue(pairs.size() == 0);
        pairs = CriticalPair.computeCriticalPairs(addNodeAndEdge, constant_3_clique);
        assertTrue(pairs.size() == 0);
        pairs = CriticalPair.computeCriticalPairs(addNodeAndEdge, constantSelfEdge);
        assertTrue(pairs.size() == 0);
        pairs = CriticalPair.computeCriticalPairs(addNodeAndEdge, deleteEdge);
        assertTrue(pairs.size() == 0);

        pairs = CriticalPair.computeCriticalPairs(addNodeAndEdge, addNodeAndEdge);
        assertTrue(pairs.size() == 0);
        pairs = CriticalPair.computeCriticalPairs(addNodeAndEdge, deleteNode);
        assertTrue(pairs.size() == 1);
        pairs = CriticalPair.computeCriticalPairs(addNodeAndEdge, deleteSelfEdge);
        assertTrue(pairs.size() == 0);
        pairs = CriticalPair.computeCriticalPairs(constantNode, constant_3_clique);
        assertTrue(pairs.size() == 0);
        pairs = CriticalPair.computeCriticalPairs(constantNode, constantSelfEdge);
        assertTrue(pairs.size() == 0);
        pairs = CriticalPair.computeCriticalPairs(constantNode, deleteEdge);
        assertTrue(pairs.size() == 0);
        pairs = CriticalPair.computeCriticalPairs(constantNode, deleteNode);
        assertTrue(pairs.size() == 1);
        pairs = CriticalPair.computeCriticalPairs(constantNode, deleteSelfEdge);
        assertTrue(pairs.size() == 0);

        pairs = CriticalPair.computeCriticalPairs(constant_3_clique, constant_3_clique);
        assertTrue(pairs.size() == 0);
        pairs = CriticalPair.computeCriticalPairs(constant_3_clique, constantSelfEdge);
        assertTrue(pairs.size() == 0);
        pairs = CriticalPair.computeCriticalPairs(constant_3_clique, deleteEdge);
        assertTrue(pairs.size() == 16);

        pairs = CriticalPair.computeCriticalPairs(constant_3_clique, deleteNode);
        assertTrue(pairs.size() == 10);
        pairs = CriticalPair.computeCriticalPairs(constant_3_clique, deleteSelfEdge);
        assertTrue(pairs.size() == 4);

        pairs = CriticalPair.computeCriticalPairs(constantSelfEdge, deleteEdge);
        assertTrue(pairs.size() == 1);
        pairs = CriticalPair.computeCriticalPairs(constantSelfEdge, deleteNode);
        assertTrue(pairs.size() == 1);
        pairs = CriticalPair.computeCriticalPairs(constantSelfEdge, deleteSelfEdge);
        assertTrue(pairs.size() == 1);

        pairs = CriticalPair.computeCriticalPairs(deleteEdge, deleteNode);
        assertTrue(pairs.size() == 3);
        pairs = CriticalPair.computeCriticalPairs(deleteEdge, deleteSelfEdge);
        assertTrue(pairs.size() == 1);

        pairs = CriticalPair.computeCriticalPairs(deleteNode, deleteSelfEdge);
        assertTrue(pairs.size() == 1);

        /* For the following rules, the number of critical pairs must be zero,
         * because same rule and same match is always strictly confluent
         * and therefore not a critical pair
         */
        pairs = CriticalPair.computeCriticalPairs(deleteEdge, deleteEdge);
        assertTrue(pairs.size() == 0);
        pairs = CriticalPair.computeCriticalPairs(deleteNode, deleteNode);
        assertTrue(pairs.size() == 0);
        pairs = CriticalPair.computeCriticalPairs(deleteSelfEdge, deleteSelfEdge);
        assertTrue(pairs.size() == 0);

    }

    @Test
    public void testFlag() {
        String grammar = "junit/criticalpair/flags.gps/";
        File grammarFile = new File(grammar);
        GrammarModel view = null;
        try {
            view = GrammarModel.newInstance(grammarFile, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Rule deleteFlagA = getSimpleRule("deleteFlagA", view);
        Rule nodeWithEdgeA = getSimpleRule("nodeWithEdgeA", view);
        Rule nodewithFlagA = getSimpleRule("nodeWithFlagA", view);
        Rule threeNodesOneFlag = getSimpleRule("threeNodesOneFlag", view);
        Rule threeNodesThreeFlags = getSimpleRule("threeNodesThreeFlags", view);

        Set<CriticalPair> pairs = CriticalPair.computeCriticalPairs(deleteFlagA, nodeWithEdgeA);
        assertTrue(pairs.size() == 0);
        pairs = CriticalPair.computeCriticalPairs(deleteFlagA, nodeWithEdgeA);
        assertTrue(pairs.size() == 0);
        pairs = CriticalPair.computeCriticalPairs(deleteFlagA, nodewithFlagA);
        assertTrue(pairs.size() == 1);
        pairs = CriticalPair.computeCriticalPairs(deleteFlagA, threeNodesOneFlag);
        assertTrue(pairs.size() == 5);
        pairs = CriticalPair.computeCriticalPairs(deleteFlagA, threeNodesThreeFlags);
        assertTrue(pairs.size() == 10);

    }

    @Test
    /**
     * Tests whether the transformation morphism works as expected
     * When an edge is both deleted and added, it should not be included in the
     * transformation morphism
     */
    public void testTransformationMorphism() {
        String grammar = "junit/criticalpair/morphism.gps/";
        File grammarFile = new File(grammar);
        GrammarModel view = null;
        try {
            view = GrammarModel.newInstance(grammarFile, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //first create a match for the rule
        Rule deleteAndAddEdge = getSimpleRule("deleteAndAddEdge", view);
        RuleGraph lhs = deleteAndAddEdge.lhs();
        DefaultHostGraph host =
            new DefaultHostGraph("target", HostFactory.newInstance(deleteAndAddEdge.getTypeGraph()
                .getFactory(), true));
        RuleToHostMap match = new RuleToHostMap(host.getFactory());

        HostNode hostSource = host.addNode();
        HostNode hostTarget = host.addNode();
        RuleNode ruleSource = null;
        RuleNode ruleTarget = null;
        for (RuleEdge re : lhs.edgeSet()) {
            ruleSource = re.source();
            ruleTarget = re.target();
            match.putNode(re.source(), hostSource);
            match.putNode(re.target(), hostTarget);
            TypeLabel label = re.label()
                .getTypeLabel();
            HostEdge hostEdge = host.addEdge(hostSource, label, hostTarget);
            match.putEdge(re, hostEdge);
        }

        Set<RuleNode> nodeSet = new HashSet<>(lhs.nodeSet());
        nodeSet.remove(ruleSource);
        nodeSet.remove(ruleTarget);

        for (RuleNode rn : nodeSet) {
            match.putNode(rn, hostTarget);
        }

        //now that we have a match, apply the transformation
        BasicEvent ruleEvent = new BasicEvent(deleteAndAddEdge, match, Reuse.NONE);
        RuleApplication app = new RuleApplication(ruleEvent, host);
        HostGraphMorphism transformationMorphism = app.getMorphism();
        assertTrue(transformationMorphism.edgeMap()
            .isEmpty());

    }

    private Rule getSimpleRule(String name, GrammarModel view) {
        Rule result = null;
        try {
            result = view.getRuleModel(QualName.name(name))
                .toResource();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        return result;
    }

}
