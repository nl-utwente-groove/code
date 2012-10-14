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

import groove.control.CtrlAut;
import groove.control.CtrlCall;
import groove.control.CtrlCall.Kind;
import groove.control.CtrlPar;
import groove.control.CtrlPar.Var;
import groove.trans.Action;
import groove.trans.QualName;
import groove.trans.Recipe;
import groove.trans.Rule;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Namespace for building a control automaton.
 * The namespace holds the function, transaction and rule names.
 * @author Arend Rensink
 * @version $Revision $
 */
public class Namespace {
    /**
     * Adds a function or recipe name to the set of declared names.
     * @return {@code true} if the name is new.
     */
    public boolean addFunction(String name, List<CtrlPar.Var> sig) {
        boolean result = !this.kindMap.containsKey(name);
        if (result) {
            this.kindMap.put(name, Kind.FUNCTION);
            this.sigMap.put(name, sig);
        }
        return result;
    }

    /**
     * Adds a function or recipe name to the set of declared names.
     * @return {@code true} if the name is new.
     */
    public boolean addRecipe(String name, int priority, List<CtrlPar.Var> sig,
            String text) {
        boolean result = !this.kindMap.containsKey(name);
        if (result) {
            this.kindMap.put(name, Kind.RECIPE);
            this.sigMap.put(name, sig);
            this.allNames.add(name);
            this.recipeMap.put(name, new Recipe(name, priority, sig, text));
        }
        return result;
    }

    /**
     * Adds a rule to the name space.
     */
    public boolean addRule(Rule rule) {
        String ruleName = rule.getFullName();
        boolean result = !this.kindMap.containsKey(ruleName);
        if (result) {
            this.kindMap.put(ruleName, CtrlCall.Kind.RULE);
            this.ruleMap.put(ruleName, rule);
            this.allNames.add(ruleName);
            this.sigMap.put(ruleName, rule.getSignature());
        }
        return result;
    }

    /**
     * Adds a function or recipe body to the namespace.
     * The function or transaction name should have been defined already.
     * If this concerns a recipe, the body is cloned and the rules appearing
     * in the recipe are removed from the set of known rules.
     */
    public void addBody(String name, CtrlAut body) {
        assert hasName(name) && getKind(name).hasBody() : String.format(
            "Unknown or inappropriate name %s", name);
        // the rules in a transaction body are no longer available
        if (getKind(name) == CtrlCall.Kind.RECIPE) {
            body = body.clone(getRecipe(name));
            for (Rule rule : body.getRules()) {
                this.allNames.remove(rule.getFullName());
            }
            getRecipe(name).setBody(body);
        }
        this.bodyMap.put(name, body);
    }

    /** Returns the set of all names known to this namespace. */
    public Set<String> getNames() {
        return this.kindMap.keySet();
    }

    /** Checks if a given name has been declared. */
    public boolean hasName(String name) {
        return getKind(name) != null;
    }

    /**
     * Checks the kind of object a given name has been declared as.
     * @param name the name to be checked
     * @return the kind of object {@code name} has been declared as,
     * or {@code null} if {@code name} is unknown
     */
    public CtrlCall.Kind getKind(String name) {
        return this.kindMap.get(name);
    }

    /**
     * Returns the automaton for a named function.
     */
    public CtrlAut getBody(String name) {
        assert hasName(name) && getKind(name).hasBody() : String.format(
            "Unknown or inappropriate name %s", name);
        CtrlAut result = this.bodyMap.get(name);
        assert result != null : String.format("Unknown function %s", name);
        return result;
    }

    /**
     * Returns the rule with a given name.
     */
    public Rule getRule(String name) {
        return this.ruleMap.get(name);
    }

    /**
     * Returns the set of all top-level rule and recipe names.
     */
    public Set<String> getTopNames() {
        return this.allNames;
    }

    /** 
     * Signals that a given name should be added to the used names.
     * The name should be a known rule or recipe name. 
     */
    public void useName(String name) {
        assert getKind(name) != Kind.OMEGA;
        this.usedNames.add(name);
    }

    /** Returns the set of all used rules,
     * i.e., all rules for which {@link Namespace#useName(String)}
     * has been invoked.
     */
    public Set<String> getUsedNames() {
        return this.usedNames;
    }

    /**
     * Returns the signature associated with a given rule name
     */
    public List<Var> getSig(String name) {
        return this.sigMap.get(name);
    }

    /** Returns the recipe text associated with a given recipe name. */
    public Recipe getRecipe(String name) {
        return this.recipeMap.get(name);
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
        Set<Action> result = new HashSet<Action>();
        result.addAll(this.ruleMap.values());
        for (Recipe recipe : this.recipeMap.values()) {
            result.add(recipe);
            result.removeAll(recipe.getBody().getRules());
        }
        return result;
    }

    @Override
    public String toString() {
        return String.format("Namespace for %s, defining %s", getFullName(),
            this.allNames);
    }

    /** Full name of the program file being parsed. */
    private String fullName;
    /** Parent name of {@link #fullName}. */
    private String parentName;
    /** Mapping from declared names to their signatures. */
    private final Map<String,List<Var>> sigMap =
        new HashMap<String,List<Var>>();
    /** Mapping from declared names to their kinds. */
    private final Map<String,CtrlCall.Kind> kindMap =
        new HashMap<String,CtrlCall.Kind>();
    /** Mapping from declared rules names to the rules. */
    private final Map<String,Rule> ruleMap = new HashMap<String,Rule>();
    /** Mapping from names to their declared bodies. */
    private final Map<String,CtrlAut> bodyMap = new HashMap<String,CtrlAut>();
    /** Mapping from declared recipe names to their body text. */
    private final Map<String,Recipe> recipeMap = new HashMap<String,Recipe>();
    /** Set of all rule names. */
    private final Set<String> allNames = new HashSet<String>();
    /** Set of used rule names. */
    private final Set<String> usedNames = new HashSet<String>();
}
