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

import groove.control.CtrlAut;
import groove.control.CtrlLoader;
import groove.trans.ResourceKind;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;

/**
 * Bridge between control programs (which are just strings) and control
 * automata.
 * @author Staijen
 */
public class ControlModel extends TextBasedModel<CtrlAut> {
    /**
     * Constructs a control view from a given control program.
     * @param grammar the grammar view to which this control view belongs.
     * @param name the name of the control program
     * @param program the control program; non-null
     */
    public ControlModel(GrammarModel grammar, String name, String program) {
        super(ResourceKind.CONTROL, name, program);
        this.grammar = grammar;
    }

    @Override
    public boolean isEnabled() {
        return getName().equals(this.grammar.getControlName());
    }

    @Override
    public CtrlAut toResource() throws FormatException {
        return toCtrlAut();
    }

    /**
     * Returns the control automaton for a given grammar. 
     */
    public CtrlAut toCtrlAut() throws FormatException {
        int modCount = this.grammar.getModificationCount();
        // use the stored result if that was for the same grammar
        if (modCount != this.lastCount) {
            this.lastAut =
                this.parser.runString(getProgram(),
                    this.grammar.getProperties(), this.grammar.getRules());
            this.lastCount = modCount;
        }
        if (!this.lastAut.getInfo().getErrors().isEmpty()) {
            throw new FormatException(this.lastAut.getInfo().getErrors());
        } else {
            return this.lastAut;
        }
    }

    /**
     * Returns the syntax errors in this control program, if any.
     */
    @Override
    public List<FormatError> getErrors() {
        List<FormatError> result;
        try {
            toCtrlAut();
            result = Collections.emptyList();
        } catch (FormatException e) {
            result = e.getErrors();
        }
        return result;
    }

    private final GrammarModel grammar;
    /** The grammar of the most recently computed control automaton. */
    private int lastCount = -1;
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
