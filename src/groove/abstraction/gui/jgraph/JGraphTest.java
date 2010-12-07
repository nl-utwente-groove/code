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
        ShapeJModel model = new ShapeJModel(shape, null);
        ShapeJGraph jGraph = new ShapeJGraph();
        jGraph.setJModel(model);
        jGraph.runLayout();
        return jGraph;
    }

}
