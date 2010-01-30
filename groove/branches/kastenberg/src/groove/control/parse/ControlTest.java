/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * $Id$
 */
package groove.control.parse;

import groove.control.ControlAutomaton;
import groove.gui.jgraph.ControlJGraph;
import groove.gui.jgraph.ControlJModel;
import groove.io.DefaultFileSystemStore;
import groove.view.FormatException;
import groove.view.StoredGrammarView;

import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

import javax.swing.JDialog;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.tree.CommonTreeNodeStream;

/**
 * @author Olaf Keijsers
 * @version $Revision $
 * 
 *          Tests the creation of a control automaton from a given control file
 *          and grammar (args[0] and args[1] from main() respectively).
 */
public class ControlTest {
    /**
     * Creates and a ControlTest object and displays the generated control
     * automaton
     * @param ctlFileName the path of the control file
     * @param grammarFileName the path of the grammar file (directory)
     * @param optimize whether or not the control automaton should be optimized
     *        (lambda transitions collapsed)
     */
    public ControlTest(String ctlFileName, String grammarFileName,
            boolean optimize) {
        try {
            File ctlFile = new File(ctlFileName);
            File grammarFile = new File(grammarFileName);

            System.out.println(("=== " + ctlFile.getName() + ":"));

            // load the program
            String program = loadProgram(ctlFile);

            // load the grammar
            DefaultFileSystemStore dfss =
                new DefaultFileSystemStore(grammarFile, false);
            dfss.reload();
            StoredGrammarView sgv = dfss.toGrammarView();

            AutomatonBuilder builder = new AutomatonBuilder();
            builder.setRuleNames(sgv.getRuleNames());
            builder.setRules(sgv.toGrammar().getRules());
            builder.finalize(sgv.toModel());

            GCLLexer lexer = new GCLLexer(new ANTLRStringStream(program));
            GCLParser parser = new GCLParser(new CommonTokenStream(lexer));
            GCLParser.program_return r = parser.program();

            boolean DEBUG = true;
            if (DEBUG) {
                ASTFrame frame =
                    new ASTFrame("parser result",
                        (org.antlr.runtime.tree.CommonTree) r.getTree());
                frame.setSize(500, 1000);
                frame.setVisible(true);
            }

            List<String> errors = parser.getErrors();
            if (errors.size() != 0) {
                errors.add(0, "Encountered parse errors in control program");
                throw new FormatException(errors);
            }
            // fetch the resulting tree
            CommonTreeNodeStream nodes = new CommonTreeNodeStream(r.getTree());
            // checker will store and remove functions
            GCLChecker checker = new GCLChecker(nodes);
            checker.setNamespace(builder);

            GCLChecker.program_return c_r = checker.program();

            errors = checker.getErrors();
            if (errors.size() != 0) {
                errors.add(0, "Encountered checker errors in control program");
                throw new FormatException(errors);
            }

            if (DEBUG) {
                ASTFrame frame =
                    new ASTFrame("checker result",
                        (org.antlr.runtime.tree.CommonTree) c_r.getTree());
                frame.setSize(500, 1000);
                frame.setVisible(true);
            }

            // fetch checker tree (since it was edited)
            nodes = new CommonTreeNodeStream(c_r.getTree());

            GCLDeterminismChecker determinismChecker =
                new GCLDeterminismChecker(nodes);
            determinismChecker.setNamespace(builder);
            GCLDeterminismChecker.program_return dc_r =
                determinismChecker.program();

            errors = determinismChecker.getErrors();
            if (errors.size() != 0) {
                errors.add(0,
                    "Encountered determinism checker errors in control program");
                throw new FormatException(errors);
            }

            if (DEBUG) {
                ASTFrame frame =
                    new ASTFrame("determinism checker result",
                        (org.antlr.runtime.tree.CommonTree) dc_r.getTree());
                frame.setSize(500, 1000);
                frame.setVisible(true);
            }

            nodes = new CommonTreeNodeStream(dc_r.getTree());
            GCLBuilder gclb = new GCLBuilder(nodes);
            gclb.setBuilder(builder);
            // reset the counter for unique controlstate numbers to 0
            Counter.reset();
            ControlAutomaton aut = gclb.program();

            // optimize if requested
            if (optimize) {
                builder.optimize();
                //builder.finalize(grammar);
            }

            groove.gui.Simulator sim = new groove.gui.Simulator();
            ControlJGraph cjg =
                new ControlJGraph(new ControlJModel(aut, sim.getOptions()),
                    null);
            cjg.setEnabled(true);
            groove.gui.JGraphPanel<ControlJGraph> autPanel =
                new groove.gui.JGraphPanel<ControlJGraph>(cjg, true, false,
                    sim.getOptions());

            JDialog jf =
                new JDialog(sim.getFrame(), "grammar: " + grammarFile.getName()
                    + ", ctl: " + ctlFile.getName());

            jf.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    ControlTest.closeWindow();
                }
            });

            jf.add(autPanel);
            jf.setSize(600, 700);
            Point p = sim.getFrame().getLocation();
            jf.setLocation(new Point(p.x + 50, p.y + 50));
            System.err.println("showing panel");
            jf.setVisible(true);

            cjg.getLayouter().start(true);

            ControlTest.openWindows++;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Called after closing a window. Reduces the open window count and shuts
     * down if no more windows are open.
     */
    public static void closeWindow() {
        openWindows--;
        if (openWindows == 0) {
            System.exit(0);
        }
    }

    /**
     * Runs the test, displaying a control automaton
     * @param args args[0] should be the path of a control file, args[1] should
     *        be the path of a grammar file
     */
    @SuppressWarnings("unused")
    public static void main(String[] args) {
        // someone testing this will probably want to change this method ;)
        final String TEST_DIRECTORY = "D:\\Studie\\Afstuderen\\Groove\\test";
        ControlTest ct;
        //ct = new ControlTest(TEST_DIRECTORY + "\\test1.ctl", 
        //TEST_DIRECTORY + "\\varTest.gps", true);
        //ct = new ControlTest(TEST_DIRECTORY+"\\test2.ctl",
        //TEST_DIRECTORY+"\\varTest.gps", true);
        //ct = new ControlTest(TEST_DIRECTORY+"\\test3.ctl",
        //TEST_DIRECTORY+"\\varTest.gps", true);
        ct =
            new ControlTest(TEST_DIRECTORY + "\\test4.ctl", TEST_DIRECTORY
                + "\\varTest.gps", true);
        ct =
            new ControlTest(
                "D:\\Studie\\Afstuderen\\antworld.gps\\control.gcp",
                "D:\\Studie\\Afstuderen\\antworld.gps", true);
    }

    /**
     * Loads a control program from a file
     * @param file the file to be read
     * @return the contents of the file as a string
     */
    private String loadProgram(File file) {
        StringBuilder contents = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while (((line = br.readLine()) != null)) {
                contents.append(line + "\r\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contents.toString();
    }

    private static int openWindows = 0;
}
