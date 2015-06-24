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
import groove.grammar.model.GrammarModel;
import groove.transform.criticalpair.ConfluenceResult;
import groove.transform.criticalpair.ConfluenceStatus;
import groove.transform.criticalpair.CriticalPair;
import groove.util.parse.FormatException;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.junit.Test;

/**
 * @author Ruud Welling
 */
@SuppressWarnings("javadoc")
public class TestConfluence {
    @Test
    public void testConfluence() {
        String grammarStr = "junit/criticalpair/basic.gps/";
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
        for (CriticalPair pair : pairs) {
            assertTrue(pair.getStrictlyConfluent(grammar) == ConfluenceStatus.STRICTLY_CONFLUENT);
        }

    }

    @Test
    public void testConfluentGrammar() {
        String grammarStr = "junit/criticalpair/phil-getBoth.gps/";
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
        ConfluenceStatus expected = ConfluenceStatus.STRICTLY_CONFLUENT;
        ConfluenceResult result = ConfluenceResult.checkStrictlyConfluent(grammar, false);
        assertTrue(result.getStatus() == expected);

        //test using efficient method
        result = ConfluenceResult.checkStrictlyConfluent(grammar, true);
        assertTrue(result.getStatus() == expected);
    }

    @Test
    public void testNonConfluentGrammar() {
        String grammarStr = "junit/criticalpair/phil.gps/";
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
        ConfluenceStatus expected = ConfluenceStatus.NOT_STICTLY_CONFLUENT;
        ConfluenceResult result = ConfluenceResult.checkStrictlyConfluent(grammar, false);
        result.analyzeAll();
        assertTrue(result.getStatus() == expected);

        //test using efficient method
        result = ConfluenceResult.checkStrictlyConfluent(grammar, true);
        result.analyzeAll();
        assertTrue(result.getStatus() == expected);
    }

    /**
     * The test below fails, this is because the theory for efficient confluence analysis is not applicable
     * The problem is that some transformations are not pushouts
     * TODO (AR) use multi-sorted graphs!!!!
     */
    //@Test
    public void testPhilAlternateMethod() {
        String grammarStr = "junit/criticalpair/phil-invalidTransformation.gps/";
        File grammarFile = new File(grammarStr);
        GrammarModel view = null;
        Grammar grammar = null;
        try {
            view = GrammarModel.newInstance(grammarFile, false);
            //            GrammarProperties props = view.getProperties();
            //            props.setInjective(true);
            //            view.setProperties(props);
            grammar = view.toGrammar();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        ConfluenceStatus expected = ConfluenceStatus.NOT_STICTLY_CONFLUENT;
        ConfluenceResult result =
            ConfluenceResult.checkStrictlyConfluent(grammar, ConfluenceStatus.UNTESTED, true);
        result.analyzeAll();
        assertTrue(result.getStatus() == expected);
    }
}
