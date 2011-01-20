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

import groove.trans.GraphGrammar;
import groove.view.FormatException;

import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * Bridge between control programs (which are just strings) and control
 * automata.
 * @author Staijen
 */
public class ControlView {
    /**
     * Constructs a control view from a given control program.
     * @param program the control program; non-null
     * @param name the name of the control program
     */
    public ControlView(String program, String name) {
        this.name = name;
        this.program = program;
    }

    /**
     * Returns the control automaton for a given grammar. 
     */
    public CtrlAut toCtrlAut(GraphGrammar grammar) throws FormatException {
        if (this.program == null) {
            throw new FormatException("Error in control: no program available");
        }
        // use the stored result if that was for the same grammar
        if (grammar != this.lastGrammar) {
            this.lastAut = this.parser.runString(this.program, grammar);
            this.lastGrammar = grammar;
        }
        return this.lastAut;
    }

    /**
     * Returns a unique identifier for the location, set by the
     * LocationAutomatonBuilder
     * @return name
     */
    public String getName() {
        return this.name;
    }

    /** Returns the textual control program. */
    public String getProgram() {
        return this.program;
    }

    /** The control program loaded at construction time. */
    private final String program;
    /** The name of the control program, set at construction time. */
    private final String name;
    /** The grammar of the most recently computed control automaton. */
    private GraphGrammar lastGrammar;
    /** The most recently computed control automaton. */
    private CtrlAut lastAut;
    /** The control parser. */
    private final CtrlLoader parser = CtrlLoader.getInstance();

    /**
     * Saves the program to the given OutputStream.
     * @param out the output stream to write the control program to.
     */
    public static void store(String controlProgram, OutputStream out) {
        PrintWriter pw = new PrintWriter(out);
        pw.write(controlProgram);
        pw.close();
    }
}
