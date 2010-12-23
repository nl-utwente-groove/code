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
package groove.gui.jgraphx;

import groove.trans.RuleName;
import groove.view.RuleView;
import groove.view.StoredGrammarView;
import groove.view.aspect.AspectGraph;

import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;

import com.mxgraph.swing.mxGraphComponent;

/**
 * Class to check JGraphX capabilities.
 * @author Eduardo Zambon
 */
public class JGraphXTest {

    /** Test method. */
    public static void main(String[] args) {
        test0();
    }

    private static void test0() {
        final String DIRECTORY = "junit/samples/abs-test.gps/";
        File file = new File(DIRECTORY);
        try {
            StoredGrammarView view = StoredGrammarView.newInstance(file, false);
            RuleView ruleView = view.getRuleView(new RuleName("del"));
            AspectGraph rule = ruleView.getView();
            showRule(rule);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void showRule(AspectGraph ruleGraph) {
        RuleJGraph ruleJGraph = new RuleJGraph(ruleGraph);
        mxGraphComponent graphComp = new mxGraphComponent(ruleJGraph);
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(graphComp);
        frame.pack();
        frame.setVisible(true);
    }

}
