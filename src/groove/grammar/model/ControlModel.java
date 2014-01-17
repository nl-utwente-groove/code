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
package groove.grammar.model;

import groove.control.CtrlAut;
import groove.control.CtrlLoader;
import groove.graph.GraphInfo;

/**
 * Bridge between control programs (which are just strings) and control
 * automata.
 * @author Arend Rensink
 */
public class ControlModel extends TextBasedModel<CtrlAut> {
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

    /**
     * Returns the control automaton for a given grammar. 
     */
    public CtrlAut toCtrlAut() throws FormatException {
        return toResource();
    }

    @Override
    public CtrlAut compute() throws FormatException {
        CtrlAut result;
        if (isEnabled()) {
            CompositeControlModel model = getGrammar().getControlModel();
            if (model.hasErrors()) {
                model.getPartErrors(this).throwException();
                // there were errors in the composite model but not in this particular part
                throw new FormatException(
                    "The composite control model cannot be built");
            } else {
                result = getGrammar().getControlModel().toResource();
            }
        } else {
            getLoader().parse(getFullName(), getProgram());
            result = getLoader().buildAutomaton(getFullName());
            if (result == null) {
                result = getLoader().buildDefaultAutomaton();
            } else {
                GraphInfo.throwException(result);
                result = result.normalise();
            }
            result.setFixed();
        }
        return result;
    }

    /** 
     * Indicates if the control automaton is the default automaton,
     * i.e., without explicit control.
     */
    public boolean isDefault() {
        boolean result = false;
        try {
            CtrlAut aut = toResource();
            result = aut == null || aut.isDefault();
        } catch (FormatException e) {
            // do nothing
        }
        return result;
    }

    /** Returns the control loader used in this control model. */
    public CtrlLoader getLoader() {
        if (this.loader == null) {
            this.loader =
                new CtrlLoader(getGrammar().getProperties().getAlgebraFamily(),
                    getRules(), true);
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
