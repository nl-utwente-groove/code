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
import groove.grammar.Action;
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
        this.loader.init(getGrammar().getProperties().getAlgebraFamily(),
            getRules());
        Collection<String> controlNames = getGrammar().getActiveNames(CONTROL);
        for (String controlName : controlNames) {
            ControlModel controlModel =
                getGrammar().getControlModel(controlName);
            if (controlModel == null) {
                errors.add("Control program '%s' cannot be found", controlName);
            } else {
                try {
                    this.loader.parse(controlName, controlModel.getProgram());
                } catch (FormatException exc) {
                    for (FormatError error : exc.getErrors()) {
                        errors.add("Error in control program '%s': %s",
                            controlName, error, controlModel);
                    }
                }
            }
        }
        errors.throwException();
        CtrlAut result = null;
        List<String> programNames = new ArrayList<String>();
        for (String controlName : controlNames) {
            try {
                CtrlAut aut = this.loader.buildAutomaton(controlName);
                if (aut != null) {
                    result = aut;
                    programNames.add(controlName);
                }
            } catch (FormatException exc) {
                ControlModel controlModel =
                    getGrammar().getControlModel(controlName);
                for (FormatError error : exc.getErrors()) {
                    errors.add("Error in control program '%s': %s",
                        controlName, error, controlModel);
                }
            }
        }
        if (programNames.size() > 1) {
            errors.add("Duplicate control programs %s", programNames);
        }
        try {
            if (result == null) {
                result = this.loader.buildDefaultAutomaton();
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
        return result;
    }

    /** Returns the set of all top-level actions of the enabled control programs. */
    public Collection<Action> getActions() {
        synchronise();
        return this.loader.getActions();
    }

    /** Returns the set of all top-level actions of the enabled control programs. */
    public Collection<Recipe> getRecipes() {
        synchronise();
        return this.loader.getRecipes();
    }

    /** Returns the control loader used in this composite control model. */
    public CtrlLoader getLoader() {
        return this.loader;
    }

    /** 
     * Returns the (non-{@code null}) set of recipe names calling a given rule.
     */
    public Set<String> getRecipes(String rule) {
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
                        Set<String> recipes =
                            this.ruleRecipeMap.get(subruleName);
                        if (recipes == null) {
                            this.ruleRecipeMap.put(subruleName, recipes =
                                new HashSet<String>());
                        }
                        recipes.add(recipe.getFullName());
                    }
                }
            }
        }
        return result;
    }

    @Override
    void notifyWillRebuild() {
        this.ruleRecipeMap = null;
        super.notifyWillRebuild();
    }

    private final CtrlLoader loader = new CtrlLoader();
    private Map<String,Set<String>> ruleRecipeMap;
}
