/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.grammar.model;

import static nl.utwente.groove.grammar.model.ResourceKind.CONTROL;
import static nl.utwente.groove.grammar.model.ResourceKind.PROPERTIES;
import static nl.utwente.groove.grammar.model.ResourceKind.RULE;
import static nl.utwente.groove.grammar.model.ResourceKind.TYPE;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNull;

import nl.utwente.groove.control.CtrlLoader;
import nl.utwente.groove.control.instance.Automaton;
import nl.utwente.groove.control.parse.CtrlTree;
import nl.utwente.groove.control.template.Program;
import nl.utwente.groove.grammar.QualName;
import nl.utwente.groove.grammar.Recipe;
import nl.utwente.groove.grammar.Rule;
import nl.utwente.groove.util.parse.FormatError;
import nl.utwente.groove.util.parse.FormatErrorSet;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Model combining all enabled control programs.
 * @author Arend Rensink
 * @version $Revision$
 */
public class CompositeControlModel extends ResourceModel<Automaton> {
    /** Constructs an instance for a given grammar model. */
    CompositeControlModel(@NonNull GrammarModel grammar) {
        super(grammar, CONTROL);
        addDependencies(RULE, TYPE, PROPERTIES);
    }

    @Override
    public @NonNull GrammarModel getGrammar() {
        var result = super.getGrammar();
        assert result != null;
        return result;
    }

    @Override
    public Object getSource() {
        return null;
    }

    @Override
    public String getName() {
        return COMPOSITE_NAME;
    }

    @Override
    boolean isShouldRebuild() {
        boolean result = super.isShouldRebuild();
        if (getGrammar().getTypeModel().isImplicit()) {
            // the implicit type graph gets rebuilt when the start graph changes
            // the rules then also get rebuilt, and hence so must the control graph
            result |= isStale(ResourceKind.HOST);
        }
        return result;
    }

    @Override
    void notifyWillRebuild() {
        this.ruleRecipeMap = null;
        this.loader = null;
        this.partErrorsMap = null;
        this.program = null;
        super.notifyWillRebuild();
    }

    @Override
    Automaton compute() throws FormatException {
        Collection<QualName> controlNames = getGrammar().getActiveNames(CONTROL);
        // first build the trees, then check to avoid errors due to unresolved dependencies
        Map<ControlModel,CtrlTree> treeMap = new LinkedHashMap<>();
        for (QualName controlName : controlNames) {
            ControlModel controlModel = getGrammar().getControlModel(controlName);
            if (controlModel == null) {
                getPartErrors(controlName).add("Control program cannot be found");
            } else {
                try {
                    treeMap
                        .put(controlModel,
                             getLoader().addControl(controlName, controlModel.getProgram()));
                } catch (FormatException exc) {
                    getPartErrors(controlName).addAll(exc.getErrors());
                }
            }
        }
        getAllPartErrors().throwException();
        try {
            this.program = getLoader().buildProgram(controlNames);
        } catch (FormatException exc) {
            for (FormatError error : exc.getErrors()) {
                addPartError(error);
            }
        }
        getAllPartErrors().throwException();
        return new Automaton(this.program);
    }

    /** Returns the control program. */
    public Program getProgram() {
        synchronise();
        return this.program;
    }

    private Program program;

    /** Returns the set of all top-level recipes of the enabled control programs. */
    public Collection<Recipe> getRecipes() {
        synchronise();
        return getLoader().getRecipes();
    }

    /** Returns the control loader used in this composite control model. */
    public CtrlLoader getLoader() {
        if (this.loader == null) {
            this.loader = new CtrlLoader(getGrammar().getProperties(), getGrammar().getRules());
        }
        return this.loader;
    }

    private CtrlLoader loader;

    /**
     * Returns the (non-{@code null}) set of recipe names calling a given rule.
     */
    public Set<QualName> getRecipes(QualName rule) {
        synchronise();
        Set<QualName> result = getRuleRecipeMap().get(rule);
        if (result == null) {
            result = Collections.emptySet();
        }
        return result;
    }

    private Map<QualName,Set<QualName>> getRuleRecipeMap() {
        Map<QualName,Set<QualName>> result = this.ruleRecipeMap;
        if (result == null) {
            result = this.ruleRecipeMap = new HashMap<>();
            for (Recipe recipe : getRecipes()) {
                Set<Rule> subrules = recipe.getRules();
                if (subrules != null) {
                    for (Rule subrule : subrules) {
                        QualName subruleName = subrule.getQualName();
                        Set<QualName> recipes = result.get(subruleName);
                        if (recipes == null) {
                            result.put(subruleName, recipes = new HashSet<>());
                        }
                        recipes.add(recipe.getQualName());
                    }
                }
            }
        }
        return result;
    }

    private Map<QualName,Set<QualName>> ruleRecipeMap;

    /** Adds a control program-related error. */
    private void addPartError(FormatError error) {
        var names = error.getResourceNames();
        if (names.isEmpty()) {
            getPartErrors(null).add(error);
        } else {
            names.forEach(k -> getPartErrors(k).add(error));
        }
    }

    /** Collects and returns all errors found in the partial control models. */
    private FormatErrorSet getAllPartErrors() {
        FormatErrorSet result = createErrors();
        for (var entry : getPartErrorsMap().entrySet()) {
            for (FormatError error : entry.getValue()) {
                if (entry.getKey() == null) {
                    result.add("Error in implicit control: %s", error);
                } else {
                    result
                        .add("Error in control program '%s': %s", entry.getKey(), error,
                             FormatError.control(entry.getKey()));
                }
            }
        }
        return result;
    }

    /** Returns the errors found in a given partial control model. */
    FormatErrorSet getPartErrors(QualName controlName) {
        return getPartErrorsMap().get(controlName);
    }

    private Map<QualName,FormatErrorSet> getPartErrorsMap() {
        if (this.partErrorsMap == null) {
            this.partErrorsMap = new HashMap<>();
            for (QualName name : getGrammar().getActiveNames(CONTROL)) {
                this.partErrorsMap.put(name, createErrors());
            }
            this.partErrorsMap.put(null, createErrors());
        }
        return this.partErrorsMap;
    }

    private Map<QualName,FormatErrorSet> partErrorsMap;

    /** Default name of the composite control program. */
    static private final String COMPOSITE_NAME = "Composite control program";
}
