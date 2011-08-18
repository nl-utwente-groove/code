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
import groove.abstraction.neigh.Util;
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
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Eduardo Zambon
 */
@SuppressWarnings("all")
public class TestMaterialisation {

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
    public void testMaterialisation0a() {
        HostGraph graph = null;
        try {
            graph = view.getHostModel("materialisation-test-0a").toResource();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        Rule rule = grammar.getRule("test-mat-0a");

        Parameters.setNodeMultBound(2);
        Multiplicity.initMultStore();

        Shape shape = Shape.createShape(graph);
        Set<Proof> preMatches = PreMatch.getPreMatches(shape, rule);
        assertEquals(1, preMatches.size());
        for (Proof preMatch : preMatches) {
            Set<Materialisation> mats =
                Materialisation.getMaterialisations(shape, preMatch);
            assertEquals(1, mats.size());
            for (Materialisation mat : mats) {
                Shape matShape = mat.getShape();
                int binaryEdgeCount = Util.getBinaryEdges(matShape).size();
                assertTrue(matShape.nodeSet().size() == 3
                    && binaryEdgeCount == 2);
            }
        }

        Parameters.setNodeMultBound(1);
        Multiplicity.initMultStore();

        shape = Shape.createShape(graph);
        preMatches = PreMatch.getPreMatches(shape, rule);
        assertEquals(1, preMatches.size());
        for (Proof preMatch : preMatches) {
            Set<Materialisation> mats =
                Materialisation.getMaterialisations(shape, preMatch);
            assertEquals(1, mats.size());
            for (Materialisation mat : mats) {
                Shape matShape = mat.getShape();
                int binaryEdgeCount = Util.getBinaryEdges(matShape).size();
                assertTrue(matShape.nodeSet().size() == 4
                    && binaryEdgeCount == 3);
            }
        }
    }

    @Test
    public void testMaterialisation0b() {
        HostGraph graph = null;
        try {
            graph = view.getHostModel("materialisation-test-0b").toResource();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        Rule rule = grammar.getRule("test-mat-0b");

        Shape shape = Shape.createShape(graph);
        Set<Proof> preMatches = PreMatch.getPreMatches(shape, rule);
        assertEquals(1, preMatches.size());
        for (Proof preMatch : preMatches) {
            Set<Materialisation> mats =
                Materialisation.getMaterialisations(shape, preMatch);
            assertEquals(1, mats.size());
            for (Materialisation mat : mats) {
                Shape matShape = mat.getShape();
                int binaryEdgeCount = Util.getBinaryEdges(matShape).size();
                assertTrue(matShape.nodeSet().size() == 4
                    && binaryEdgeCount == 3);
            }
        }
    }

    @Test
    public void testMaterialisation0c() {
        HostGraph graph = null;
        try {
            graph = view.getHostModel("materialisation-test-0c").toResource();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        Rule rule = grammar.getRule("test-mat-0c");

        Shape shape = Shape.createShape(graph);
        Set<Proof> preMatches = PreMatch.getPreMatches(shape, rule);
        assertEquals(1, preMatches.size());
        for (Proof preMatch : preMatches) {
            Set<Materialisation> mats =
                Materialisation.getMaterialisations(shape, preMatch);
            assertEquals(1, mats.size());
            for (Materialisation mat : mats) {
                Shape matShape = mat.getShape();
                int binaryEdgeCount = Util.getBinaryEdges(matShape).size();
                assertTrue(matShape.nodeSet().size() == 6
                    && binaryEdgeCount == 3);
            }
        }
    }

    @Test
    public void testMaterialisation1() {
        HostGraph graph = null;
        try {
            graph = view.getHostModel("materialisation-test-1").toResource();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        Rule rule = grammar.getRule("test-mat-1");

        Shape shape = Shape.createShape(graph);
        Set<Proof> preMatches = PreMatch.getPreMatches(shape, rule);
        assertEquals(1, preMatches.size());
        for (Proof preMatch : preMatches) {
            Set<Materialisation> mats =
                Materialisation.getMaterialisations(shape, preMatch);
            assertEquals(1, mats.size());
            for (Materialisation mat : mats) {
                Shape matShape = mat.getShape();
                int binaryEdgeCount = Util.getBinaryEdges(matShape).size();
                assertTrue(matShape.nodeSet().size() == 6
                    && binaryEdgeCount == 4);
            }
        }
    }

    @Test
    public void testMaterialisation2() {
        HostGraph graph = null;
        try {
            graph = view.getHostModel("materialisation-test-2").toResource();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        Rule rule = grammar.getRule("test-mat-2");

        Shape shape = Shape.createShape(graph);
        Set<Proof> preMatches = PreMatch.getPreMatches(shape, rule);
        assertEquals(1, preMatches.size());
        for (Proof preMatch : preMatches) {
            Set<Materialisation> mats =
                Materialisation.getMaterialisations(shape, preMatch);
            assertEquals(2, mats.size());
            for (Materialisation mat : mats) {
                Shape matShape = mat.getShape();
                int nodeCount = matShape.nodeSet().size();
                int binaryEdgeCount = Util.getBinaryEdges(matShape).size();
                assertTrue((nodeCount == 5 || nodeCount == 6)
                    && (binaryEdgeCount == 4 || binaryEdgeCount == 7));
            }
        }
    }
}
