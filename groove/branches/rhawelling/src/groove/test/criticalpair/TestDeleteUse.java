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
import groove.abstraction.pattern.PatternAbstraction;
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

    static private final String GRAMMAR = "junit/criticalpair/basic1.gps/";
    static private GrammarModel view;

    @BeforeClass
    public static void setUp() {
        PatternAbstraction.initialise();
        File grammarFile = new File(GRAMMAR);
        try {
            view = GrammarModel.newInstance(grammarFile, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testBasic1() {
        Rule r1 = getSimpleRule("r1");
        Rule r2 = getSimpleRule("r2");
        Set<CriticalPair> pairs = CriticalPair.computeCriticalPairs(r1, r2);
        assertTrue(pairs.size() == 1);
        pairs = CriticalPair.computeCriticalPairs(r2, r1);
        assertTrue(pairs.size() == 1);
        pairs = CriticalPair.computeCriticalPairs(r2, r1);
        assertTrue(pairs.size() == 1);
        pairs = CriticalPair.computeCriticalPairs(r2, r2);
        assertTrue(pairs.size() == 0);
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
