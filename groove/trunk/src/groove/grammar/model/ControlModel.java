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
package groove.grammar.model;

import groove.control.CtrlLoader;
import groove.control.template.Program;
import groove.control.template.Template;

import java.util.Collection;
import java.util.Collections;

/**
 * Bridge between control programs (which are just strings) and control
 * automata.
 * @author Arend Rensink
 */
public class ControlModel extends TextBasedModel<Collection<Template>> {
    /**
     * Constructs a control view from a given control program.
     * @param grammar the grammar view to which this control view belongs.
     * Must be non-{@code null} in order to compute the control automation
     * @param name the name of the control program
     * @param program the control program; non-null
     */
    public ControlModel(GrammarModel grammar, String name, String program) {
        super(grammar, ResourceKind.CONTROL, name, program);
    }

    @Override
    public Collection<Template> compute() throws FormatException {
        Program program;
        if (isEnabled()) {
            CompositeControlModel model = getGrammar().getControlModel();
            if (model.hasErrors()) {
                model.getPartErrors(getFullName()).throwException();
                // there were errors in the composite model but not in this particular part
                throw new FormatException("The composite control model cannot be built");
            } else {
                program = model.getProgram();
            }
        } else {
            getLoader().parse(getFullName(), getProgram()).check();
            program = getLoader().buildProgram(Collections.singleton(getFullName()));
        }
        return program.getTemplates(getFullName());
    }

    /** Returns the control loader used in this control model. */
    public CtrlLoader getLoader() {
        if (this.loader == null) {
            this.loader = new CtrlLoader(getGrammar().getProperties(), getRules(), false);
        }
        return this.loader;
    }

    @Override
    void notifyWillRebuild() {
        this.loader = null;
        super.notifyWillRebuild();
    }

    /** The control parser. */
    private CtrlLoader loader;
}
