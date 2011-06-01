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
 * View for prolog programs (which are just strings).
 * @author Arend Rensink
 */
public class PrologView {
    /**
     * Constructs a prolog view from a given prolog program.
     * @param name the name of the prolog program; non-{@code null}
     * @param program the prolog program; non-null
     */
    public PrologView(String name, String program) {
        this.name = name;
        this.program = program;
    }

    /**
     * Returns the name of the prolog program.
     */
    public String getName() {
        return this.name;
    }

    /** Returns the textual prolog program. */
    public String getProgram() {
        return this.program;
    }

    /** The prolog program loaded at construction time. */
    private final String program;
    /** The name of the prolog program, set at construction time. */
    private final String name;

    /**
     * Saves the program to the given OutputStream.
     * @param out the output stream to write the prolog program to.
     */
    public static void store(String program, OutputStream out) {
        PrintWriter pw = new PrintWriter(out);
        pw.write(program);
        pw.close();
    }
}
