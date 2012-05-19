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
import groove.control.CtrlLoader.Result;
import groove.trans.Recipe;
import groove.trans.ResourceKind;

import java.util.HashSet;
import java.util.Set;

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

    @Override
    public boolean isEnabled() {
        return getName().equals(getGrammar().getControlName());
    }

    /**
     * Returns the control automaton for a given grammar. 
     */
    public CtrlAut toCtrlAut() throws FormatException {
        return toResource();
    }

    @Override
    public CtrlAut compute() throws FormatException {
        Result result =
            parser.runString(getProgram(), getGrammar().getProperties(),
                getGrammar().getRules());
        this.recipes = new HashSet<Recipe>(result.two());
        return result.one();
    }

    /** Returns the set of recipes defined in the control program. */
    public Set<Recipe> getRecipes() {
        synchronise();
        return this.recipes;
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

    /** The control parser. */
    private static final CtrlLoader parser = CtrlLoader.getInstance();
    /** The set of recipes found in the last computation of the control automaton. */
    private Set<Recipe> recipes;
}
