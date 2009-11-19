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
 * $Id: ControlView.java,v 1.10 2008-03-18 12:17:29 fladder Exp $
 */
package groove.control;

import groove.control.parse.ASTFrame;
import groove.control.parse.AutomatonBuilder;
import groove.control.parse.Counter;
import groove.control.parse.GCLBuilder;
import groove.control.parse.GCLChecker;
import groove.control.parse.GCLLexer;
import groove.control.parse.GCLParser;
import groove.trans.GraphGrammar;
import groove.view.FormatException;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTreeNodeStream;

/**
 * Bridge between control programs (which are just strings) and control
 * automata.
 * @author Staijen
 */
public class ControlView {
    /**
     * Constructs a control view from a given control program.
     * @param control the control program; non-null
     * @param controlName the name of the control program
     */
    public ControlView(String control, String controlName) {
        this.controlName = controlName;
        this.controlProgram = control;
    }

    /** Returns the control automaton. */
    public ControlAutomaton getAutomaton() {
        return this.automaton;
    }

    /** Returns the textual control program. */
    public String getProgram() {
        return this.controlProgram;
    }

    /**
     * This method should only be called from DefaultGrammarView.computeGrammar
     * Create the automaton once, then, use getAutomaton() to get the automaton.
     */
    public ControlAutomaton toAutomaton(GraphGrammar grammar)
        throws FormatException {
        if (this.automaton == null) {
            this.automaton = computeAutomaton(grammar);
        }

        return this.automaton;
    }

    /**
     * Resets the pre-computed control automaton. Called when the underlying
     * grammar changes, so that the automaton has to be computed anew.
     */
    public void invalidateAutomaton() {
        this.automaton = null;
    }

    /**
     * load the program currently in controlProgram
     */
    private ControlAutomaton computeAutomaton(GraphGrammar grammar)
        throws FormatException {
        if (this.controlProgram == null) {
            throw new FormatException("Error in control:no program available ");
        }
        AutomatonBuilder builder = new AutomatonBuilder();
        builder.setRuleNames(grammar.getRuleNames());
        builder.setRules(grammar.getRules());

        try {
            ANTLRStringStream stream = new ANTLRStringStream(getProgram());
            stream.name = getName();
            GCLLexer lexer = new GCLLexer(stream);
            GCLParser parser = new GCLParser(new CommonTokenStream(lexer));
            GCLParser.program_return r = parser.program();

            boolean DEBUG = false;

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

            GCLBuilder gclb = new GCLBuilder(nodes);
            gclb.setBuilder(builder);
            gclb.setName(getName());
            // reset the counter for unique controlstate numbers to 0
            Counter.reset();
            ControlAutomaton aut = gclb.program();
            builder.optimize();
            builder.finalize(grammar);
            //
            // groove.gui.Simulator sim = new groove.gui.Simulator();
            // ControlJGraph cjg = new ControlJGraph(new ControlJModel(aut,
            // sim.getOptions()));
            // groove.gui.JGraphPanel autPanel = new groove.gui.JGraphPanel(cjg,
            // true, sim.getOptions());
            //
            // JDialog jf = new JDialog(sim.getFrame(), "Control Automaton");
            // jf.add(autPanel);
            // jf.setSize(600, 700);
            // Point p = sim.getFrame().getLocation();
            // jf.setLocation(new Point(p.x + 50, p.y + 50));
            // System.err.println("showing panel");
            // jf.setVisible(true);
            //            
            // cjg.getLayouter().start(true);

            return aut;
        } catch (RecognitionException re) {
            throw new FormatException(re);
        }
    }

    /**
     * Returns a unique identifier for the location, set by the
     * LocationAutomatonBuilder
     * @return name
     */
    public String getName() {
        return this.controlName;
    }

    /** The control program loaded at construction time. */
    private final String controlProgram;
    /** The control automaton constructed from the program. */
    private ControlAutomaton automaton;
    /** The name of the control program, set at construction time. */
    private final String controlName;

    /**
     * Saves the program to the given OutputStream.
     * @param controlProgram
     * @param out the output stream to write the control program to.
     */
    public static void store(String controlProgram, OutputStream out) {
        PrintWriter pw = new PrintWriter(out);
        pw.write(controlProgram);
        pw.close();
    }
}
