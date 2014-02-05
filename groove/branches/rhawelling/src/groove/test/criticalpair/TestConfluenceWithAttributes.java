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
import groove.grammar.Grammar;
import groove.grammar.model.FormatException;
import groove.grammar.model.GrammarModel;
import groove.transform.criticalpair.ConfluenceResult;
import groove.transform.criticalpair.ConfluenceStatus;
import groove.transform.criticalpair.CriticalPair;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.junit.Test;

/**
 * @author Ruud Welling
 */
public class TestConfluenceWithAttributes {

    //    @Test
    //    public void testConfluenceWithAttributes() {
    //        String grammarStr = "junit/criticalpair/attributes.gps/";
    //        File grammarFile = new File(grammarStr);
    //        GrammarModel view = null;
    //        Grammar grammar = null;
    //        try {
    //            view = GrammarModel.newInstance(grammarFile, false);
    //            grammar = view.toGrammar();
    //        } catch (IOException e) {
    //            e.printStackTrace();
    //        } catch (FormatException e) {
    //            e.printStackTrace();
    //        }
    //        ConfluenceStatus expected = ConfluenceStatus.CONFLUENT;
    //        ConfluenceResult result =
    //            ConfluenceResult.checkStrictlyConfluent(grammar);
    //        result.analyzeAll();
    //        System.out.println("Not confluent:");
    //        for (CriticalPair nonConf : result.getNonConfluentPairs()) {
    //            System.out.println(nonConf);
    //            System.out.println(nonConf.getRuleApplication1().getTarget());
    //            System.out.println(nonConf.getRuleApplication2().getTarget());
    //        }
    //        System.out.println(result.getStatus());
    //        assertTrue(result.getStatus() == expected);
    //
    //    }

    @Test
    public void testConfluenceWithAttributes_OneTwo() {
        String grammarStr = "junit/criticalpair/attributes/delete-one-two.gps/";
        File grammarFile = new File(grammarStr);
        GrammarModel view = null;
        Grammar grammar = null;
        try {
            view = GrammarModel.newInstance(grammarFile, false);
            grammar = view.toGrammar();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        Set<CriticalPair> pairs = CriticalPair.computeCriticalPairs(grammar);
        assertTrue(pairs.size() == 0);
        assertTrue(ConfluenceResult.checkStrictlyConfluent(grammar).getStatus() == ConfluenceStatus.STRICTLY_CONFLUENT);

    }
}
