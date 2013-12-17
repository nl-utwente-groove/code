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
import groove.grammar.Rule;
import groove.grammar.model.FormatException;
import groove.grammar.model.GrammarModel;
import groove.transform.criticalpair.CriticalPair;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Ruud Welling
 */
public class TestDeleteUse {

    static private final String GRAMMAR = "junit/criticalpair/basic.gps/";
    static private GrammarModel view;

    @BeforeClass
    public static void setUp() {
        File grammarFile = new File(GRAMMAR);
        try {
            view = GrammarModel.newInstance(grammarFile, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testBasic() {
        Rule addNodeAndEdge = getSimpleRule("addNodeAndEdge");
        Rule constantNode = getSimpleRule("constantNode");
        Rule constant_3_clique = getSimpleRule("constant_3_clique");
        Rule constantSelfEdge = getSimpleRule("constantSelfEdge");
        Rule deleteEdge = getSimpleRule("deleteEdge");
        Rule deleteNode = getSimpleRule("deleteNode");
        Rule deleteSelfEdge = getSimpleRule("deleteSelfEdge");

        Set<CriticalPair> pairs =
            CriticalPair.computeCriticalPairs(addNodeAndEdge, constantNode);
        assertTrue(pairs.size() == 0);
        pairs =
            CriticalPair.computeCriticalPairs(addNodeAndEdge, constant_3_clique);
        assertTrue(pairs.size() == 0);
        pairs =
            CriticalPair.computeCriticalPairs(addNodeAndEdge, constantSelfEdge);
        assertTrue(pairs.size() == 0);
        pairs = CriticalPair.computeCriticalPairs(addNodeAndEdge, deleteEdge);
        assertTrue(pairs.size() == 0);

        pairs = CriticalPair.computeCriticalPairs(addNodeAndEdge, deleteNode);
        assertTrue(pairs.size() == 1);
        pairs =
            CriticalPair.computeCriticalPairs(addNodeAndEdge, deleteSelfEdge);
        assertTrue(pairs.size() == 0);
        pairs =
            CriticalPair.computeCriticalPairs(constantNode, constant_3_clique);
        assertTrue(pairs.size() == 0);
        pairs =
            CriticalPair.computeCriticalPairs(constantNode, constantSelfEdge);
        assertTrue(pairs.size() == 0);
        pairs = CriticalPair.computeCriticalPairs(constantNode, deleteEdge);
        assertTrue(pairs.size() == 0);
        pairs = CriticalPair.computeCriticalPairs(constantNode, deleteNode);
        assertTrue(pairs.size() == 1);
        pairs = CriticalPair.computeCriticalPairs(constantNode, deleteSelfEdge);
        assertTrue(pairs.size() == 0);

        pairs =
            CriticalPair.computeCriticalPairs(constant_3_clique,
                constantSelfEdge);
        assertTrue(pairs.size() == 0);
        pairs =
            CriticalPair.computeCriticalPairs(constant_3_clique, deleteEdge);
        assertTrue(pairs.size() == 16);

        pairs =
            CriticalPair.computeCriticalPairs(constant_3_clique, deleteNode);
        assertTrue(pairs.size() == 10);
        pairs =
            CriticalPair.computeCriticalPairs(constant_3_clique, deleteSelfEdge);
        assertTrue(pairs.size() == 4);

        pairs = CriticalPair.computeCriticalPairs(constantSelfEdge, deleteEdge);
        assertTrue(pairs.size() == 1);
        pairs = CriticalPair.computeCriticalPairs(constantSelfEdge, deleteNode);
        assertTrue(pairs.size() == 1);
        pairs =
            CriticalPair.computeCriticalPairs(constantSelfEdge, deleteSelfEdge);
        assertTrue(pairs.size() == 1);

        pairs = CriticalPair.computeCriticalPairs(deleteEdge, deleteNode);
        assertTrue(pairs.size() == 3);
        pairs = CriticalPair.computeCriticalPairs(deleteEdge, deleteSelfEdge);
        assertTrue(pairs.size() == 1);
    }

    private Rule getSimpleRule(String name) {
        Rule result = null;
        try {
            result = view.getRuleModel(name).toResource();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        return result;
    }

}
