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
package groove.test.abstraction.neigh;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import groove.abstraction.neigh.Multiplicity;
import groove.abstraction.neigh.Parameters;
import groove.abstraction.neigh.match.PreMatch;
import groove.abstraction.neigh.shape.Shape;
import groove.trans.GraphGrammar;
import groove.trans.HostGraph;
import groove.trans.Proof;
import groove.trans.Rule;
import groove.view.FormatException;
import groove.view.GrammarModel;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Eduardo Zambon
 */
@SuppressWarnings("all")
public class TestPreMatch {

    static private final String DIRECTORY = "junit/samples/abs-test.gps/";
    static private GrammarModel view;
    static private GraphGrammar grammar;

    @BeforeClass
    public static void setUp() {
        File file = new File(DIRECTORY);
        try {
            view = GrammarModel.newInstance(file, false);
            grammar = view.toGrammar();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testPreMatch0() {
        Parameters.setNodeMultBound(3);
        Parameters.setEdgeMultBound(1);
        Multiplicity.initMultStore();
        HostGraph graph = null;
        try {
            graph = view.getHostModel("shape-build-test-2").toResource();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        Rule rule = grammar.getRule("test-match-0");
        Shape shape = Shape.createShape(graph);
        Set<Proof> preMatches = PreMatch.getPreMatches(shape, rule);
        assertTrue(preMatches.isEmpty());
    }

    @Test
    public void testPreMatch1() {
        Parameters.setNodeMultBound(3);
        Parameters.setEdgeMultBound(1);
        Multiplicity.initMultStore();
        HostGraph graph = null;
        try {
            graph = view.getHostModel("shape-build-test-2").toResource();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        Rule rule = grammar.getRule("test-match-4");
        Shape shape = Shape.createShape(graph);
        Set<Proof> preMatches = PreMatch.getPreMatches(shape, rule);
        assertTrue(preMatches.isEmpty());
    }

    @Test
    public void testPreMatch2() {
        Parameters.setNodeMultBound(1);
        Parameters.setEdgeMultBound(1);
        Multiplicity.initMultStore();
        HostGraph graph = null;
        try {
            graph = view.getHostModel("shape-build-test-8").toResource();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        Shape shape = Shape.createShape(graph);
        Rule rule = grammar.getRule("test-match-1");
        Set<Proof> preMatches = PreMatch.getPreMatches(shape, rule);
        assertEquals(3, preMatches.size());
        rule = grammar.getRule("test-match-2");
        preMatches = PreMatch.getPreMatches(shape, rule);
        assertEquals(4, preMatches.size());
    }

    @Test
    public void testPreMatch3() {
        HostGraph graph = null;
        try {
            graph = view.getHostModel("shape-build-test-9").toResource();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        Shape shape = Shape.createShape(graph);
        Rule rule = grammar.getRule("test-match-3");
        Set<Proof> preMatches = PreMatch.getPreMatches(shape, rule);
        assertEquals(16, preMatches.size());
    }

}