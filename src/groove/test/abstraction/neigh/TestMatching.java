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
import groove.abstraction.neigh.Abstraction;
import groove.abstraction.neigh.Multiplicity;
import groove.abstraction.neigh.Parameters;
import groove.abstraction.neigh.io.xml.ShapeGxl;
import groove.abstraction.neigh.match.PreMatch;
import groove.abstraction.neigh.shape.Shape;
import groove.abstraction.neigh.trans.Materialisation;
import groove.trans.GraphGrammar;
import groove.trans.HostGraph;
import groove.trans.Proof;
import groove.trans.Rule;
import groove.view.FormatException;
import groove.view.GrammarModel;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Eduardo Zambon
 */
@SuppressWarnings("all")
public class TestMatching {

    static private final String DIRECTORY = "junit/abstraction/match-test.gps/";
    static private GrammarModel view;
    static private GraphGrammar grammar;

    @BeforeClass
    public static void setUp() {
        Abstraction.initialise();
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
            graph = view.getHostModel("shape-0").toResource();
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
            graph = view.getHostModel("shape-0").toResource();
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
            graph = view.getHostModel("shape-1").toResource();
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
            graph = view.getHostModel("shape-2").toResource();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        Shape shape = Shape.createShape(graph);
        Rule rule = grammar.getRule("test-match-3");
        Set<Proof> preMatches = PreMatch.getPreMatches(shape, rule);
        assertEquals(16, preMatches.size());
    }

    @Test
    public void testNAC0() {
        HostGraph graph0a = null;
        HostGraph graph0b = null;
        HostGraph graph0c = null;
        HostGraph graph0d = null;
        try {
            graph0a = view.getHostModel("test-nac-0a").toResource();
            graph0b = view.getHostModel("test-nac-0b").toResource();
            graph0c = view.getHostModel("test-nac-0c").toResource();
            graph0d = view.getHostModel("test-nac-0d").toResource();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        Shape shape0a = Shape.createShape(graph0a);
        Shape shape0b = Shape.createShape(graph0b);
        Shape shape0c = Shape.createShape(graph0c);
        Shape shape0d = Shape.createShape(graph0d);
        Rule rule = grammar.getRule("test-nac-0");

        Set<Proof> preMatches = PreMatch.getPreMatches(shape0a, rule);
        assertTrue(preMatches.isEmpty());
        preMatches = PreMatch.getPreMatches(shape0b, rule);
        assertTrue(preMatches.isEmpty());
        preMatches = PreMatch.getPreMatches(shape0c, rule);
        assertEquals(1, preMatches.size());
        preMatches = PreMatch.getPreMatches(shape0d, rule);
        assertEquals(4, preMatches.size());
    }

    @Test
    public void testNAC1() {
        HostGraph graph1a = null;
        HostGraph graph1b = null;
        try {
            graph1a = view.getHostModel("test-nac-1a").toResource();
            graph1b = view.getHostModel("test-nac-1b").toResource();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        Shape shape1a = Shape.createShape(graph1a);
        Shape shape1b = Shape.createShape(graph1b);
        Rule rule = grammar.getRule("test-nac-1");

        Set<Proof> preMatches = PreMatch.getPreMatches(shape1a, rule);
        assertTrue(preMatches.isEmpty());
        preMatches = PreMatch.getPreMatches(shape1b, rule);
        assertEquals(1, preMatches.size());
    }

    @Test
    public void testNAC2() {
        HostGraph graph2a = null;
        HostGraph graph2b = null;
        try {
            graph2a = view.getHostModel("test-nac-2a").toResource();
            graph2b = view.getHostModel("test-nac-2b").toResource();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        Shape shape2a = Shape.createShape(graph2a);
        Shape shape2b = Shape.createShape(graph2b);
        Rule rule = grammar.getRule("test-nac-2");

        Set<Proof> preMatches = PreMatch.getPreMatches(shape2a, rule);
        assertEquals(1, preMatches.size());
        preMatches = PreMatch.getPreMatches(shape2b, rule);
        assertEquals(2, preMatches.size());
    }

    @Test
    public void testNAC3() {
        HostGraph graph3 = null;
        try {
            graph3 = view.getHostModel("test-nac-3").toResource();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        Shape shape3 = Shape.createShape(graph3);
        Rule rule = grammar.getRule("test-nac-3");
        Set<Proof> preMatches = PreMatch.getPreMatches(shape3, rule);
        assertEquals(1, preMatches.size());
        Proof preMatch = preMatches.iterator().next();
        Set<Materialisation> mats =
            Materialisation.getMaterialisations(shape3, preMatch);
        assertEquals(2, mats.size());
    }

    @Test
    public void testNAC4() {
        HostGraph graph4 = null;
        try {
            graph4 = view.getHostModel("test-nac-4").toResource();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        Shape shape4 = Shape.createShape(graph4);
        Rule rule = grammar.getRule("test-nac-4");
        Set<Proof> preMatches = PreMatch.getPreMatches(shape4, rule);
        assertEquals(1, preMatches.size());
        Proof preMatch = preMatches.iterator().next();
        Set<Materialisation> mats =
            Materialisation.getMaterialisations(shape4, preMatch);
        assertEquals(1, mats.size());
    }

    @Test
    public void testNAC5() {
        HostGraph graph4 = null;
        try {
            graph4 = view.getHostModel("test-nac-4").toResource();
        } catch (FormatException e) {
            e.printStackTrace();
        }

        Parameters.setEdgeMultBound(2);
        Multiplicity.initMultStore();

        Shape shape4 = Shape.createShape(graph4);
        Rule rule = grammar.getRule("test-nac-4");
        Set<Proof> preMatches = PreMatch.getPreMatches(shape4, rule);
        assertEquals(1, preMatches.size());
        Proof preMatch = preMatches.iterator().next();
        Set<Materialisation> mats =
            Materialisation.getMaterialisations(shape4, preMatch);
        assertEquals(0, mats.size());
    }

    @Test
    public void testNAC6() {
        File file = new File(DIRECTORY + "test-nac-5a.gxl");
        Shape shape = null;
        try {
            shape =
                ShapeGxl.getInstance(view.getTypeGraph()).unmarshalShape(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Parameters.setNodeMultBound(1);
        Parameters.setEdgeMultBound(1);
        Multiplicity.initMultStore();

        Rule rule = grammar.getRule("test-nac-5");
        Set<Proof> preMatches = PreMatch.getPreMatches(shape, rule);
        assertEquals(1, preMatches.size());
        Proof preMatch = preMatches.iterator().next();
        Set<Materialisation> mats =
            Materialisation.getMaterialisations(shape, preMatch);
        assertEquals(0, mats.size());
    }

    @Test
    public void testNAC7() {
        File file = new File(DIRECTORY + "test-nac-5b.gxl");
        Shape shape = null;
        try {
            shape =
                ShapeGxl.getInstance(view.getTypeGraph()).unmarshalShape(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Rule rule = grammar.getRule("test-nac-5");
        Set<Proof> preMatches = PreMatch.getPreMatches(shape, rule);
        assertEquals(2, preMatches.size());
        Iterator<Proof> it = preMatches.iterator();
        Proof preMatch = it.next();
        Set<Materialisation> mats =
            Materialisation.getMaterialisations(shape, preMatch);
        assertEquals(2, mats.size());
        preMatch = it.next();
        mats = Materialisation.getMaterialisations(shape, preMatch);
        assertEquals(0, mats.size());
    }

    @AfterClass
    public static void cleanUp() {
        Parameters.setNodeMultBound(1);
        Parameters.setEdgeMultBound(1);
        Multiplicity.initMultStore();
    }
}
