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
 * $Id: Namespace.java,v 1.4 2008-02-05 13:27:53 rensink Exp $
 */
package groove.control.parse;

import groove.control.Callable;
import groove.control.CtrlEdge.Kind;
import groove.control.CtrlPar;
import groove.control.CtrlPar.Var;
import groove.control.Function;
import groove.control.Template;
import groove.grammar.Action;
import groove.grammar.QualName;
import groove.grammar.Recipe;
import groove.grammar.Rule;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Name space for building a control automaton.
 * The name space holds the function, transaction and rule names.
 * @author Arend Rensink
 * @version $Revision $
 */
public class CtrlNameSpace {
    /**
     * Adds a function or recipe name to the set of declared names.
     * @return {@code true} if the name is new.
     */
    public boolean addFunction(String name, int priority,
            List<CtrlPar.Var> sig, String controlName, int startLine) {
        return addCallable(new Function(name, priority, sig, controlName,
            startLine));
    }

    /**
     * Adds a function or recipe name to the set of declared names.
     * @return {@code true} if the name is new.
     */
    public boolean addRecipe(String name, int priority, List<CtrlPar.Var> sig,
            String controlName, int startLine) {
        return addCallable(new Recipe(name, priority, sig, controlName,
            startLine));
    }

    /**
     * Adds a rule to the name space.
     */
    public boolean addRule(Rule rule) {
        return addCallable(rule);
    }

    private boolean addCallable(Callable unit) {
        String name = unit.getFullName();
        boolean result = !this.unitMap.containsKey(name);
        if (result) {
            this.unitMap.put(name, unit);
            if (unit.getKind().isAction()) {
                this.topNames.add(name);
            }
        }
        return result;
    }

    /**
     * Adds a function or recipe body to the name space.
     * The function or recipe should have been added already.
     * If this concerns a recipe, the body is cloned and the rules appearing
     * in the recipe are removed from the set of known rules.
     */
    public void addBody(String name, Template body) {
        assert hasName(name) && getKind(name).hasBody() : String.format(
            "Unknown or inappropriate name %s", name);
        Callable unit = getUnit(name);
        switch (unit.getKind()) {
        case RECIPE:
            Recipe recipe = (Recipe) unit;
            body = body.clone(recipe);
            // the callables in a recipe body are no longer top level
            for (Action action : body.getActions()) {
                this.topNames.remove(action.getFullName());
            }
            recipe.setTemplate(body);
            break;
        case FUNCTION:
            Function function = (Function) unit;
            function.setTemplate(body);
        }
    }

    /** Returns the set of all names known to this namespace. */
    public Set<String> getNames() {
        return this.unitMap.keySet();
    }

    /** Checks if a given name has been declared. */
    public boolean hasName(String name) {
        return getUnit(name) != null;
    }

    /**
     * Returns the kind of object a given name has been declared as.
     * @param name the name to be checked
     * @return the kind of object {@code name} has been declared as,
     * or {@code null} if {@code name} is unknown
     */
    public Kind getKind(String name) {
        Callable unit = getUnit(name);
        return unit == null ? null : unit.getKind();
    }

    /**
     * Returns the callable unit stored for a given name.
     * @param name the name to be checked
     * @return the callable unit with name {@code name},
     * or {@code null} if {@code name} is unknown
     */
    public Callable getUnit(String name) {
        return this.unitMap.get(name);
    }

    /**
     * Returns the set of all top-level rule and recipe names.
     * Rules and recipes invoked from (other) recipes are 
     * excluded from this set.
     */
    public Set<String> getTopNames() {
        return this.topNames;
    }

    /** 
     * Signals that a given name should be added to the used names.
     * The name should denote a known callable unit. 
     */
    public void useName(String name) {
        assert hasName(name);
        this.usedNames.add(name);
    }

    /** Returns the set of all used rules,
     * i.e., all rules for which {@link CtrlNameSpace#useName(String)}
     * has been invoked.
     */
    public Set<String> getUsedNames() {
        return this.usedNames;
    }

    /**
     * Returns the signature associated with a given rule name
     */
    public List<Var> getSig(String name) {
        return getUnit(name).getSignature();
    }

    /** Sets the full name of the control program currently being explored. */
    public void setFullName(String fullName) {
        this.fullName = fullName;
        this.parentName = QualName.getParent(fullName);
    }

    /** Returns the full name of the control program being parsed. */
    public String getFullName() {
        return this.fullName;
    }

    /** Returns the parent name space of the control program being parsed. */
    public String getParentName() {
        return this.parentName;
    }

    /** Returns the set of (top-level) actions in this namespace. */
    public Collection<Action> getActions() {
        Set<Action> result = new LinkedHashSet<Action>();
        for (String name : getTopNames()) {
            result.add((Action) getUnit(name));
        }
        return result;
    }

    @Override
    public String toString() {
        return String.format("Namespace for %s, defining %s", getFullName(),
            this.topNames);
    }

    /** Full name of the program file being parsed. */
    private String fullName;
    /** Parent name of {@link #fullName}. */
    private String parentName;
    /** Mapping from declared names to the corresponding unit. */
    private final Map<String,Callable> unitMap =
        new LinkedHashMap<String,Callable>();
    /** Set of top-level rule and recipe names. */
    private final Set<String> topNames = new LinkedHashSet<String>();
    /** Set of used rule names. */
    private final Set<String> usedNames = new HashSet<String>();
}
