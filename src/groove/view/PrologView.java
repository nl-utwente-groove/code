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
package groove.view;

import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * Bridge between control programs (which are just strings) and control
 * automata.
 * @author Staijen
 */
public class PrologView {
    /**
     * Constructs a control view from a given control program.
     * @param program the control program; non-null
     * @param name the name of the control program
     */
    public PrologView(String program, String name) {
        this.name = name;
        this.prolog = program;
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
    public String getControl() {
        return this.prolog;
    }

    /** The control program loaded at construction time. */
    private final String prolog;
    /** The name of the control program, set at construction time. */
    private final String name;

    /**
     * Saves the program to the given OutputStream.
     * @param out the output stream to write the control program to.
     */
    public static void store(String program, OutputStream out) {
        PrintWriter pw = new PrintWriter(out);
        pw.write(program);
        pw.close();
    }
}
