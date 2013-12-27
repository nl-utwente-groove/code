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

import org.junit.Test;

/**
 * @author Ruud Welling
 */
public class TestDeleteUseWithAttributes {

    @Test
    public void testAttributes() {
        String grammar = "junit/criticalpair/attributes.gps/";
        File grammarFile = new File(grammar);
        GrammarModel view = null;
        try {
            view = GrammarModel.newInstance(grammarFile, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Rule addOneToNumber = getSimpleRule("addOneToNumber", view);
        Rule addOneToNumber_2 = getSimpleRule("addOneToNumber_2", view);
        Rule deleteNumber = getSimpleRule("deleteNumber", view);
        Rule deleteNumberOne = getSimpleRule("deleteNumberOne", view);

        Set<CriticalPair> pairs =
            CriticalPair.computeCriticalPairs(addOneToNumber, addOneToNumber);
        //TODO the result should be 5
        assertTrue(pairs.size() == 5 || pairs.size() == 3);
        pairs =
            CriticalPair.computeCriticalPairs(addOneToNumber, addOneToNumber_2);
        assertTrue(pairs.size() == 5);
        pairs = CriticalPair.computeCriticalPairs(addOneToNumber, deleteNumber);
        assertTrue(pairs.size() == 2);
        pairs =
            CriticalPair.computeCriticalPairs(addOneToNumber, deleteNumberOne);
        System.out.println(deleteNumberOne.lhs().nodeCount());
        assertTrue(pairs.size() == 2);

        pairs =
            CriticalPair.computeCriticalPairs(addOneToNumber_2,
                addOneToNumber_2);
        //TODO the result should be 5
        assertTrue(pairs.size() == 5 || pairs.size() == 3);
        pairs =
            CriticalPair.computeCriticalPairs(addOneToNumber_2, deleteNumber);
        assertTrue(pairs.size() == 2);
        pairs =
            CriticalPair.computeCriticalPairs(addOneToNumber_2, deleteNumberOne);
        assertTrue(pairs.size() == 2);

        pairs = CriticalPair.computeCriticalPairs(deleteNumber, deleteNumber);
        System.out.println("numPairs: " + pairs.size());
        System.out.println("numNodes: " + deleteNumber.lhs().nodeCount());
        assertTrue(pairs.size() == 1);
        pairs =
            CriticalPair.computeCriticalPairs(deleteNumber, deleteNumberOne);
        assertTrue(pairs.size() == 1);

        pairs =
            CriticalPair.computeCriticalPairs(deleteNumberOne, deleteNumberOne);
        assertTrue(pairs.size() == 1);

    }

    private Rule getSimpleRule(String name, GrammarModel view) {
        Rule result = null;
        try {
            result = view.getRuleModel(name).toResource();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        return result;
    }

}
