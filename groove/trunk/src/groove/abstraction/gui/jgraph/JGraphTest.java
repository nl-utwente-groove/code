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
package groove.abstraction.gui.jgraph;

import groove.abstraction.Multiplicity;
import groove.abstraction.Shape;
import groove.graph.Graph;
import groove.view.FormatException;
import groove.view.StoredGrammarView;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import org.jgraph.JGraph;

/**
 * Class to check JGraph capabilities.
 * @author Eduardo Zambon
 */
public class JGraphTest {

    /** Test method. */
    public static void main(String[] args) {
        Multiplicity.initMultStore();
        test0();
    }

    private static void test0() {
        final String DIRECTORY = "junit/samples/abs-test.gps/";
        File file = new File(DIRECTORY);
        try {
            StoredGrammarView view = StoredGrammarView.newInstance(file, false);
            Graph graph = view.getGraphView("rule-app-test-0").toModel();
            Shape shape = new Shape(graph);
            showShape(shape);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
    }

    private static void showShape(Shape shape) {
        JGraph jgraph = getJGraphFrom(shape);
        jgraph.setPreferredSize(new Dimension(600, 600));
        jgraph.setEnabled(true);
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new JScrollPane(jgraph));
        frame.pack();
        frame.setVisible(true);
    }

    private static JGraph getJGraphFrom(Shape shape) {
        ShapeJModel model = new ShapeJModel(shape);
        ShapeJGraph jGraph = new ShapeJGraph(model);
        jGraph.runLayout();
        return jGraph;
    }

    /*private static void createEdgeMults(JGraph jgraph, Shape shape) {
        String labels[] = new String[2];
        Point2D[] labelPositions =
            {new Point2D.Double(GraphConstants.PERMILLE * 9 / 10, -10),
                new Point2D.Double(GraphConstants.PERMILLE / 10, -10)};

        HashSet<EdgeSignature> usedInEs = new HashSet<EdgeSignature>();
        HashSet<EdgeSignature> usedOutEs = new HashSet<EdgeSignature>();

        for (EdgeSignature outEs : outEsMap.keySet()) {
            ShapeEdge edge = shape.getEdgesFrom(outEs, true).iterator().next();
            String outMult = shape.getEdgeOutMult(edge).toString();
            String inMult = shape.getEdgeInMult(edge).toString();
            EdgeSignature inEs = shape.getEdgeInSignature(edge);
            if (usedOutEs.contains(outEs)) {
                continue;
            } else {
                labels[0] = outMult;
                usedOutEs.add(outEs);
            }
            if (usedInEs.contains(inEs)) {
                continue;
            } else {
                labels[1] = inMult;
                usedInEs.add(inEs);
            }
            DefaultEdge jedge = edgeMap.get(edge);
            AttributeMap attrMap = jedge.getAttributes();
            GraphConstants.setExtraLabelPositions(attrMap, labelPositions);
            GraphConstants.setExtraLabels(attrMap, labels);
        }

        for (EdgeSignature inEs : inEsMap.keySet()) {
            ShapeEdge edge = shape.getEdgesFrom(inEs, false).iterator().next();
            String outMult = shape.getEdgeOutMult(edge).toString();
            String inMult = shape.getEdgeInMult(edge).toString();
            EdgeSignature outEs = shape.getEdgeOutSignature(edge);
            if (usedOutEs.contains(outEs)) {
                continue;
            } else {
                labels[0] = outMult;
                usedOutEs.add(outEs);
            }
            if (usedInEs.contains(inEs)) {
                continue;
            } else {
                labels[1] = inMult;
                usedInEs.add(inEs);
            }
            DefaultEdge jedge = edgeMap.get(edge);
            AttributeMap attrMap = jedge.getAttributes();
            GraphConstants.setExtraLabelPositions(attrMap, labelPositions);
            GraphConstants.setExtraLabels(attrMap, labels);
        }

        assert usedInEs.containsAll(inEsMap.keySet())
            && usedOutEs.containsAll(outEsMap.keySet());
    }*/

}
