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
import groove.abstraction.neigh.Multiplicity;
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

    @BeforeClass
    public static void setUp() {
        Multiplicity.initMultStore();
    }

    @Test
    public void testMaterialisation0() {
        File file = new File(DIRECTORY);
        try {
            GrammarModel view = GrammarModel.newInstance(file, false);
            HostGraph graph =
                view.getHostModel("materialisation-test-0").toResource();
            Shape shape = Shape.createShape(graph);
            GraphGrammar grammar = view.toGrammar();
            Rule rule = grammar.getRule("test-mat-0");
            Set<Proof> preMatches = PreMatch.getPreMatches(shape, rule);
            assertEquals(1, preMatches.size());
            for (Proof preMatch : preMatches) {
                Set<Materialisation> mats =
                    Materialisation.getMaterialisations(shape, preMatch);
                /*assertEquals(6, mats.size());
                for (Materialisation mat : mats) {
                    Shape matShape = mat.getShape();
                    int binaryEdgeCount = getBinaryEdges(matShape).size();
                    assertTrue((matShape.nodeSet().size() == 5 && binaryEdgeCount == 4)
                        || (matShape.nodeSet().size() == 6 && binaryEdgeCount == 7));
                }*/
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
    }

}
