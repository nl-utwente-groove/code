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
import groove.util.Status;

import java.util.ArrayList;
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
        super(grammar, ResourceKind.CONTROL, name, program);
    }

    @Override
    public boolean isEnabled() {
        return getName().equals(getGrammar().getControlName());
    }

    @Override
    public CtrlAut toResource() throws FormatException {
        return toCtrlAut();
    }

    /**
     * Returns the control automaton for a given grammar. 
     */
    public CtrlAut toCtrlAut() throws FormatException {
        initialise();
        if (this.status == Status.ERROR) {
            throw new FormatException(this.errors);
        } else {
            return this.automaton;
        }
    }

    /**
     * Returns the syntax errors in this control program, if any.
     */
    @Override
    public List<FormatError> getErrors() {
        initialise();
        return this.errors;
    }

    /**
     * Initialises the control automaton and error fields,
     * or reinitialises them if the grammar has changed.
     */
    private void initialise() {
        if (isGrammarModified()) {
            this.status = Status.START;
        }
        if (this.status == Status.START) {
            this.errors.clear();
            try {
                this.automaton = compute();
                this.status = Status.DONE;
            } catch (FormatException e) {
                this.errors.addAll(e.getErrors());
                this.status = Status.ERROR;
            }
        }
    }

    @Override
    protected CtrlAut compute() throws FormatException {
        return parser.runString(getProgram(), getGrammar().getProperties(),
            getGrammar().getRules());
    }

    /** Status of the construction. */
    private Status status = Status.START;
    /** The most recently computed control automaton. */
    private CtrlAut automaton;
    /** Errors encountered in the automaton. */
    private final List<FormatError> errors = new ArrayList<FormatError>();
    /** The control parser. */
    private static final CtrlLoader parser = CtrlLoader.getInstance();
}
