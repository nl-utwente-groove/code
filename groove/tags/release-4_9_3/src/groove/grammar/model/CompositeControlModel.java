/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on an 
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific 
 * language governing permissions and limitations under the License.
 *
 * $Id$
 */
package groove.grammar.model;

import static groove.grammar.model.ResourceKind.CONTROL;
import groove.control.CtrlAut;
import groove.control.CtrlLoader;
import groove.control.instance.Automaton;
import groove.control.template.Program;
import groove.grammar.Recipe;
import groove.grammar.Rule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Model combining all enabled control programs.
 * @author Arend Rensink
 * @version $Revision $
 */
public class CompositeControlModel extends ResourceModel<CtrlAut> {
    /** Constructs an instance for a given grammar model. */
    CompositeControlModel(GrammarModel grammar) {
        super(grammar, ResourceKind.CONTROL, "control");
    }

    @Override
    public Object getSource() {
        return null;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    CtrlAut compute() throws FormatException {
        FormatErrorSet errors = createErrors();
        Collection<String> controlNames = getGrammar().getActiveNames(CONTROL);
        for (String controlName : controlNames) {
            ControlModel controlModel = getGrammar().getControlModel(controlName);
            if (controlModel == null) {
                addPartError(controlModel, new FormatError("Control program cannot be found"));
            } else {
                try {
                    getLoader().parse(controlName, controlModel.getProgram()).check();
                } catch (FormatException exc) {
                    for (FormatError error : exc.getErrors()) {
                        addPartError(controlModel, error);
                    }
                }
            }
        }
        getAllPartErrors().throwException();
        CtrlAut result = null;
        List<String> programNames = new ArrayList<String>();
        for (String controlName : controlNames) {
            try {
                CtrlAut aut = getLoader().buildAutomaton(controlName);
                if (aut != null) {
                    result = aut;
                    programNames.add(controlName);
                }
            } catch (FormatException exc) {
                ControlModel controlModel = getGrammar().getControlModel(controlName);
                for (FormatError error : exc.getErrors()) {
                    addPartError(controlModel, error);
                }
            }
        }
        getAllPartErrors().throwException();
        if (programNames.size() > 1) {
            errors.add("Duplicate control programs %s", programNames);
        }
        try {
            if (result == null) {
                result = getLoader().buildDefaultAutomaton();
            } else {
                result = result.normalise();
            }
        } catch (FormatException e) {
            for (FormatError error : e.getErrors()) {
                errors.add("Error in composite control program: %s", error);
            }
        }
        errors.throwException();
        result.setFixed();
        this.automaton = computeAutomaton();
        return result;
    }

    /** Returns the control automaton constructed at the last call to {@link #compute()}.
     * TODO this is a stopgap solution: as soon as the new control is fully integrated,
     * {@link #compute()} should be rewritten so as to generate only the {@link Automaton}.
     */
    public Automaton getAutomaton() {
        assert this.automaton != null;
        return this.automaton;
    }

    private Automaton automaton;

    private Automaton computeAutomaton() throws FormatException {
        // look for the control program with the main loop
        Collection<String> controlNames = getGrammar().getActiveNames(CONTROL);
        Program program = getLoader().buildProgram(controlNames);
        return new Automaton(program.getTemplate());
    }

    /** Returns the set of all top-level actions of the enabled control programs. */
    public Collection<Recipe> getRecipes() {
        synchronise();
        return getLoader().getRecipes();
    }

    /** Returns the control loader used in this composite control model. */
    public CtrlLoader getLoader() {
        if (this.loader == null) {
            this.loader =
                new CtrlLoader(getGrammar().getProperties().getAlgebraFamily(), getRules(), true);
        }
        return this.loader;
    }

    private CtrlLoader loader;

    /** 
     * Returns the (non-{@code null}) set of recipe names calling a given rule.
     */
    public Set<String> getRecipes(String rule) {
        synchronise();
        Set<String> result = getRuleRecipeMap().get(rule);
        if (result == null) {
            result = Collections.emptySet();
        }
        return result;
    }

    private Map<String,Set<String>> getRuleRecipeMap() {
        Map<String,Set<String>> result = this.ruleRecipeMap;
        if (result == null) {
            result = this.ruleRecipeMap = new HashMap<String,Set<String>>();
            for (Recipe recipe : getRecipes()) {
                Set<Rule> subrules = recipe.getRules();
                if (subrules != null) {
                    for (Rule subrule : subrules) {
                        String subruleName = subrule.getFullName();
                        Set<String> recipes = this.ruleRecipeMap.get(subruleName);
                        if (recipes == null) {
                            this.ruleRecipeMap.put(subruleName, recipes = new HashSet<String>());
                        }
                        recipes.add(recipe.getFullName());
                    }
                }
            }
        }
        return result;
    }

    private Map<String,Set<String>> ruleRecipeMap;

    @Override
    void notifyWillRebuild() {
        this.ruleRecipeMap = null;
        this.loader = null;
        this.partErrorsMap = null;
        super.notifyWillRebuild();
    }

    /** Adds an error for a particular control program. */
    private void addPartError(ControlModel part, FormatError error) {
        getPartErrorsMap().get(part).add(error);
    }

    /** Collects and returns all errors found in the partial control models. */
    private FormatErrorSet getAllPartErrors() {
        FormatErrorSet result = createErrors();
        for (Map.Entry<ControlModel,FormatErrorSet> entry : getPartErrorsMap().entrySet()) {
            for (FormatError error : entry.getValue()) {
                result.add("Error in control program '%s': %s", entry.getKey().getFullName(),
                    error, entry.getKey());
            }
        }
        return result;
    }

    /** Returns the errors found in a given partial control model. */
    FormatErrorSet getPartErrors(ControlModel part) {
        return getPartErrorsMap().get(part);
    }

    private Map<ControlModel,FormatErrorSet> getPartErrorsMap() {
        if (this.partErrorsMap == null) {
            this.partErrorsMap = new HashMap<ControlModel,FormatErrorSet>();
            for (String name : getGrammar().getActiveNames(CONTROL)) {
                this.partErrorsMap.put(getGrammar().getControlModel(name), createErrors());
            }
        }
        return this.partErrorsMap;
    }

    private Map<ControlModel,FormatErrorSet> partErrorsMap;
}
