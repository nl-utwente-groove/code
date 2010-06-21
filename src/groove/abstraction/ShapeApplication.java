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
package groove.abstraction;

import groove.graph.Graph;
import groove.trans.DefaultApplication;
import groove.trans.GraphGrammar;
import groove.trans.Rule;
import groove.trans.RuleEvent;
import groove.trans.RuleMatch;
import groove.view.FormatException;
import groove.view.StoredGrammarView;

import java.io.File;
import java.io.IOException;
import java.util.Set;

/**
 * EDUARDO
 * @author Eduardo Zambon
 * @version $Revision $
 */
public class ShapeApplication extends DefaultApplication {

    /** EDUARDO */
    public ShapeApplication(RuleEvent event, Graph source) {
        super(event, source);
    }

    /** EDUARDO */
    public Shape applyMatch() {
        return null;

        /*// We need to clone, otherwise the source shape is modified.
        Shape clone = this.shape.clone();
        appl.applyDelta(clone);
        Shape result = (Shape) appl.getTarget();

        // Update the maps in the result.
        for (Edge edge : transfEvent.getSimpleErasedEdges()) {
            result.removeEdge(edge);
        }
        for (Node node : transfEvent.getErasedNodes()) {
            result.removeNode(node);
        }
        Multiplicity oneMult = Multiplicity.getMultOf(1);
        for (Node node : transfEvent.getCreatedNodes(this.shape.nodeSet())) {
            ShapeNode nodeS = (ShapeNode) node;
            // Fill the shape node multiplicity.
            result.setNodeMult(nodeS, oneMult);
        }*/
    }

    // ------------------------------------------------------------------------
    // Test methods
    // ------------------------------------------------------------------------

    private static void testMaterialisation0() {
        final String DIRECTORY = "junit/samples/abs-test.gps/";

        File file = new File(DIRECTORY);
        try {
            StoredGrammarView view = StoredGrammarView.newInstance(file, false);
            Graph graph = view.getGraphView("materialisation-test-0").toModel();
            Shape shape = new Shape(graph);
            GraphGrammar grammar = view.toGrammar();
            Rule rule = grammar.getRule("test-mat-0");
            Set<RuleMatch> preMatches = PreMatch.getPreMatches(shape, rule);
            for (RuleMatch preMatch : preMatches) {
                Set<Materialisation> mats =
                    Materialisation.getMaterialisations(shape, preMatch);
                for (Materialisation mat : mats) {
                    System.out.println(mat);
                    //mat.applyMatch();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
    }

    /** Unit testing. */
    public static void main(String args[]) {
        Multiplicity.initMultStore();
        testMaterialisation0();
    }

}
