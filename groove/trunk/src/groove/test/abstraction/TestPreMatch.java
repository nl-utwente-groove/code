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
package groove.test.abstraction;

import groove.abstraction.Multiplicity;
import groove.abstraction.Parameters;
import groove.abstraction.PreMatch;
import groove.abstraction.Shape;
import groove.graph.Graph;
import groove.trans.GraphGrammar;
import groove.trans.Rule;
import groove.trans.RuleMatch;
import groove.view.FormatException;
import groove.view.StoredGrammarView;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import junit.framework.TestCase;

/**
 * @author Eduardo Zambon
 * @version $Revision $
 */
@SuppressWarnings("all")
public class TestPreMatch extends TestCase {

    static private final String DIRECTORY = "junit/samples/abs-test.gps/";

    protected void setUp() throws Exception {
        super.setUp();
        Parameters.setNodeMultBound(1);
        Parameters.setEdgeMultBound(1);
        Multiplicity.initMultStore();
    }

    public void testPreMatch0() {
        Parameters.setNodeMultBound(3);
        Parameters.setEdgeMultBound(1);
        Multiplicity.initMultStore();
        File file = new File(DIRECTORY);
        try {
            StoredGrammarView view = StoredGrammarView.newInstance(file, false);
            Graph graph = view.getGraphView("shape-build-test-2").toModel();
            Shape shape = new Shape(graph);
            GraphGrammar grammar = view.toGrammar();
            Rule rule = grammar.getRule("test-match-0");
            Set<RuleMatch> preMatches = PreMatch.getPreMatches(shape, rule);
            assertTrue(preMatches.isEmpty());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
    }

    public void testPreMatch1() {
        Parameters.setNodeMultBound(1);
        Parameters.setEdgeMultBound(1);
        Multiplicity.initMultStore();
        File file = new File(DIRECTORY);
        try {
            StoredGrammarView view = StoredGrammarView.newInstance(file, false);
            Graph graph = view.getGraphView("shape-build-test-8").toModel();
            Shape shape = new Shape(graph);
            GraphGrammar grammar = view.toGrammar();
            Rule rule = grammar.getRule("test-match-1");
            Set<RuleMatch> preMatches = PreMatch.getPreMatches(shape, rule);
            assertTrue(preMatches.size() == 3);
            rule = grammar.getRule("test-match-2");
            preMatches = PreMatch.getPreMatches(shape, rule);
            assertTrue(preMatches.size() == 4);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
    }

    public void testPreMatch2() {
        File file = new File(DIRECTORY);
        try {
            StoredGrammarView view = StoredGrammarView.newInstance(file, false);
            Graph graph = view.getGraphView("shape-build-test-9").toModel();
            Shape shape = new Shape(graph);
            GraphGrammar grammar = view.toGrammar();
            Rule rule = grammar.getRule("test-match-3");
            Set<RuleMatch> preMatches = PreMatch.getPreMatches(shape, rule);
            assertTrue(preMatches.size() == 16);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
    }

}
