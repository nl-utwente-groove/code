/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.test.criticalpair;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.junit.Test;

import nl.utwente.groove.grammar.Grammar;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.io.store.SystemStore;
import nl.utwente.groove.transform.criticalpair.ConfluenceResult;
import nl.utwente.groove.transform.criticalpair.ConfluenceStatus;
import nl.utwente.groove.transform.criticalpair.CriticalPair;
import nl.utwente.groove.util.parse.FormatException;

/**
 * @author Ruud Welling
 */
@SuppressWarnings("javadoc")
public class TestConfluenceWithAttributes {
    @Test
    public void testConfluenceWithAttributes_OneTwo() {
        String grammarStr = "junit/criticalpair/attributes/delete-one-two.gps/";
        File grammarFile = new File(grammarStr);
        GrammarModel view = null;
        Grammar grammar = null;
        try {
            view = SystemStore.newGrammar(grammarFile);
            grammar = view.toGrammar();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        Set<CriticalPair> pairs = CriticalPair.computeCriticalPairs(grammar);
        assertTrue(pairs.size() == 0);
        assertTrue(ConfluenceResult
            .checkStrictlyConfluent(grammar)
            .getStatus() == ConfluenceStatus.STRICTLY_CONFLUENT);

    }
}
