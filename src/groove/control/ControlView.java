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

import groove.control.parse.AutomatonBuilder;
import groove.control.parse.Counter;
import groove.control.parse.GCLBuilder;
import groove.control.parse.GCLChecker;
import groove.control.parse.GCLLexer;
import groove.control.parse.GCLParser;
import groove.trans.GraphGrammar;
import groove.view.DefaultGrammarView;
import groove.view.FormatException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTreeNodeStream;

/**
 * 
 * The Control part of the GrammarView. For loading, saving, getting an actual
 * representation, etc.
 * 
 * @author Staijen Loads a control program into a given ControlAutomaton
 */
public class ControlView {
    /** File where the control program is stored. * */
    private final File controlFile;
    /** Grammar view to which this control view belongs. */
    private final DefaultGrammarView grammarView;
    /**
     * The control program, loaded from {@link #controlFile} at construction
     * time.
     */
    private final String controlProgram;

    private ControlAutomaton automaton;

    /**
     * Constructor, needs a grammar view and a filename to a control program.
     * 
     * Afterwards, programShape and automaton should have a value.
     * 
     * @param result
     * @param controlProgramFile
     */
    public ControlView(DefaultGrammarView result, File controlProgramFile) {
        this.grammarView = result;
        this.controlFile = controlProgramFile;
        this.controlProgram = loadProgram(controlProgramFile);
    }

    //	
    // /**
    // * Initialises the Control given the rulenames in the grammar
    // * Can only be called once and must be called before any other method is
    // used.
    // * @param grammar
    // */
    // public void initNamespace(DefaultGrammarView grammar)
    // {
    //		
    // //System.out.println("Initializing Control NameSpace");
    //		
    // // try
    // // {
    // // AspectualRuleView lambdaRV = new
    // AspectualRuleView(ControlView.LAMBDA_RULE);
    // // AspectualRuleView elseRV = new
    // AspectualRuleView(ControlView.ELSE_RULE);
    // // grammar.addRule(lambdaRV);
    // // grammar.addRule(elseRV);
    // //
    // // } catch(FormatException e) {
    // // // will not happen
    // // }
    //		
    // this.builder = new AutomatonBuilder();
    // this.builder.setRuleNames(grammar);
    // }

    /** returns the control automaton */
    public ControlAutomaton getAutomaton() {
        return this.automaton;
    }

    /** returns the textual control program */
    public String getProgram() {
        return this.controlProgram;
    }

    /** loads the program from a File * */
    private String loadProgram(File controlFile) {
        StringBuilder contents = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(controlFile));
            String line;
            while (((line = br.readLine()) != null)) {
                contents.append(line + "\r\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contents.toString();
    }

    /** returns the File containing the current control program */
    public File getFile() {
        return this.controlFile;
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
        builder.setRuleNames(this.grammarView);

        try {
            GCLLexer lexer = new GCLLexer(new ANTLRStringStream(getProgram()));
            GCLParser parser = new GCLParser(new CommonTokenStream(lexer));
            GCLParser.program_return r = parser.program();
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
            // fetch checker tree (since it was edited)
            nodes = new CommonTreeNodeStream(c_r.getTree());

            GCLBuilder gclb = new GCLBuilder(nodes);
            gclb.setBuilder(builder);
            // reset the counter for unique controlstate numbers to 0
            Counter.reset();
            ControlShape programShape = gclb.program();
            builder.optimize();
            builder.finalize(grammar);
            return new ControlAutomaton(programShape);
        } catch (RecognitionException re) {
            throw new FormatException(re);
        }
    }

    /**
     * Saves the program to the given file.
     * 
     * @param controlProgram
     * @param file
     * @throws IOException
     */
    public static void saveFile(String controlProgram, File file)
        throws IOException {
        PrintWriter pw = new PrintWriter(file);
        pw.write(controlProgram);
        pw.close();
    }
}
