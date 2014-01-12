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

import groove.algebra.AlgebraFamily;
import groove.control.CtrlAut;
import groove.control.Switch.Kind;
import groove.control.CtrlPar;
import groove.control.CtrlPar.Var;
import groove.control.Function;
import groove.grammar.Action;
import groove.grammar.QualName;
import groove.grammar.Recipe;
import groove.grammar.Rule;
import groove.grammar.model.FormatError;
import groove.grammar.model.FormatErrorSet;
import groove.util.antlr.ParseInfo;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Namespace for building a control automaton.
 * The namespace holds the function, transaction and rule names.
 * @author Arend Rensink
 * @version $Revision $
 */
public class Namespace implements ParseInfo {
    /** Constructs a new name space, on the basis of a given algebra family. */
    public Namespace(AlgebraFamily family) {
        this.family = family;
    }

    /** Returns the algebra family of this name space. */
    public AlgebraFamily getAlgebraFamily() {
        return this.family;
    }

    private final AlgebraFamily family;

    /**
     * Adds a function or recipe name to the set of declared names.
     * @return {@code true} if the name is new.
     */
    public boolean addFunction(String name, int priority,
            List<CtrlPar.Var> sig, String controlName, int startLine) {
        boolean result = !this.kindMap.containsKey(name);
        if (result) {
            this.kindMap.put(name, Kind.FUNCTION);
            this.sigMap.put(name, sig);
            this.functionMap.put(name, new Function(name, priority, sig,
                controlName, startLine));
        }
        return result;
    }

    /**
     * Adds a function or recipe name to the set of declared names.
     * @return {@code true} if the name is new.
     */
    public boolean addRecipe(String name, int priority, List<CtrlPar.Var> sig,
            String controlName, int startLine) {
        boolean result = !this.kindMap.containsKey(name);
        if (result) {
            this.kindMap.put(name, Kind.RECIPE);
            this.sigMap.put(name, sig);
            this.topNames.add(name);
            this.recipeMap.put(name, new Recipe(name, priority, sig,
                controlName, startLine));
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
            this.kindMap.put(ruleName, Kind.RULE);
            this.ruleMap.put(ruleName, rule);
            this.topNames.add(ruleName);
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
        assert hasName(name) && getKind(name).isProcedure() : String.format(
            "Unknown or inappropriate name %s", name);
        // the rules in a transaction body are no longer available
        if (getKind(name) == Kind.RECIPE) {
            body = body.clone(getRecipe(name));
            for (Rule rule : body.getRules()) {
                this.topNames.remove(rule.getFullName());
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
    public Kind getKind(String name) {
        return this.kindMap.get(name);
    }

    /**
     * Returns the automaton for a procedure.
     */
    public CtrlAut getBody(String name) {
        assert hasName(name) : String.format(
            "Unknown or inappropriate name %s", name);
        assert getKind(name).isProcedure() : String.format(
            "%s does not denote a procedure", name);
        CtrlAut result = this.bodyMap.get(name);
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
     * Rules and recipes invoked from (other) recipes are 
     * excluded from this set.
     */
    public Set<String> getTopNames() {
        return this.topNames;
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

    /** Returns the recipe associated with a given recipe name. */
    public Recipe getRecipe(String name) {
        return this.recipeMap.get(name);
    }

    /** Returns the declared function with a given name. */
    public Function getFunction(String name) {
        return this.functionMap.get(name);
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
        Set<Action> result = new TreeSet<Action>();
        result.addAll(this.ruleMap.values());
        for (Recipe recipe : this.recipeMap.values()) {
            result.add(recipe);
            result.removeAll(recipe.getRules());
        }
        return result;
    }

    /** Returns the set of recipes in this namespace. */
    public Collection<Recipe> getRecipes() {
        return this.recipeMap.values();
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
    /** Mapping from declared names to their signatures. */
    private final Map<String,List<Var>> sigMap =
        new HashMap<String,List<Var>>();
    /** Mapping from declared names to their kinds. */
    private final Map<String,Kind> kindMap = new HashMap<String,Kind>();
    /** Mapping from declared rules names to the rules. */
    private final Map<String,Rule> ruleMap = new HashMap<String,Rule>();
    /** Mapping from names to their declared bodies. */
    private final Map<String,CtrlAut> bodyMap = new HashMap<String,CtrlAut>();
    /** Mapping from declared function names to the corresponding functions. */
    private final Map<String,Function> functionMap =
        new HashMap<String,Function>();
    /** Mapping from declared recipe names to the corresponding recipes. */
    private final Map<String,Recipe> recipeMap = new HashMap<String,Recipe>();
    /** Set of top-level rule and recipe names. */
    private final Set<String> topNames = new HashSet<String>();
    /** Set of used rule names. */
    private final Set<String> usedNames = new HashSet<String>();

    /** Adds an error to the errors contained in this name space. */
    public void addError(String message, Object... args) {
        this.errors.add(message, args);
    }

    /** Adds an error to the errors contained in this name space. */
    public void addError(FormatError error) {
        this.errors.add(error);
    }

    /** Returns the errors collected in this name space. */
    public FormatErrorSet getErrors() {
        return this.errors;
    }

    private final FormatErrorSet errors = new FormatErrorSet();
}
