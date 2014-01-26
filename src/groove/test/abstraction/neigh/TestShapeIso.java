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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import groove.abstraction.neigh.NeighAbstraction;
import groove.abstraction.neigh.match.PreMatch;
import groove.abstraction.neigh.shape.Shape;
import groove.abstraction.neigh.shape.iso.ShapeIsoChecker;
import groove.abstraction.neigh.trans.Materialisation;
import groove.grammar.Grammar;
import groove.grammar.Rule;
import groove.grammar.host.HostGraph;
import groove.grammar.model.FormatException;
import groove.grammar.model.GrammarModel;
import groove.transform.Proof;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Eduardo Zambon
 */
@SuppressWarnings("all")
public class TestShapeIso {

    static private final String DIRECTORY = "junit/abstraction/basic-tests.gps/";
    static private GrammarModel view;
    static private Grammar grammar;

    @BeforeClass
    public static void setUp() {
        NeighAbstraction.initialise();
        File file = new File(DIRECTORY);
        try {
            view = GrammarModel.newInstance(file, false);
            grammar = view.toGrammar();
        } catch (IOException e) {
            Assert.fail(e.toString());
        } catch (FormatException e) {
            Assert.fail(e.toString());
        }
    }

    @Test
    public void testShapeIso0() {
        HostGraph graph0 = null;
        HostGraph graph1 = null;
        HostGraph graph2 = null;
        try {
            graph0 = view.getHostModel("materialisation-test-0a").toResource();
            graph1 = view.getHostModel("materialisation-test-1a").toResource();
            graph2 = view.getHostModel("rule-app-test-0").toResource();
        } catch (FormatException e) {
            Assert.fail(e.toString());
        }
        Rule rule2 = grammar.getRule("add");

        Shape shape0 = Shape.createShape(graph0);
        Shape shape1 = Shape.createShape(graph1);
        Shape shape2 = Shape.createShape(graph2);

        ShapeIsoChecker checker = ShapeIsoChecker.getInstance(true);

        // Basic tests.
        // A shape must be isomorphic to itself.
        assertTrue(checker.areIsomorphic(shape0, shape0));
        // Compare to a clone.
        assertTrue(checker.areIsomorphic(shape0, shape0.clone()));
        // Two completely different shapes.
        assertFalse(checker.areIsomorphic(shape0, shape2));
        // Shapes with same graph structure but different multiplicities.
        assertFalse(checker.areIsomorphic(shape0, shape1));

        // More elaborated tests.
        Set<Proof> preMatches = PreMatch.getPreMatches(shape2, rule2);
        for (Proof preMatch : preMatches) {
            Set<Materialisation> mats = Materialisation.getMaterialisations(shape2, preMatch);
            for (Materialisation mat : mats) {
                Shape result = mat.applyMatch(null).one();
                // The shape after rule application is different.
                assertFalse(checker.areIsomorphic(shape2, result));
                Shape normalisedShape = result.normalise();
                // The shape after normalisation is isomorphic to the
                // original one.
                assertTrue(checker.areIsomorphic(shape2, normalisedShape));
            }
        }
    }

}
