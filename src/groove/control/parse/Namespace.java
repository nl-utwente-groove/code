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
import groove.control.Callable;
import groove.control.CtrlAut;
import groove.control.CtrlPar;
import groove.control.Function;
import groove.control.Procedure;
import groove.grammar.QualName;
import groove.grammar.Recipe;
import groove.grammar.Rule;
import groove.grammar.model.FormatError;
import groove.grammar.model.FormatErrorSet;
import groove.util.antlr.ParseInfo;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
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
        boolean result = !hasCallable(name);
        if (result) {
            this.callableMap.put(name, new Function(name, priority, sig,
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
        boolean result = !hasCallable(name);
        if (result) {
            this.topNames.add(name);
            this.callableMap.put(name, new Recipe(name, priority, sig, controlName,
                startLine));
        }
        return result;
    }

    /**
     * Adds a rule to the name space.
     */
    public boolean addRule(Rule rule) {
        String ruleName = rule.getFullName();
        boolean result = !hasCallable(ruleName);
        if (result) {
            this.callableMap.put(ruleName, rule);
            this.topNames.add(ruleName);
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
        Callable unit = getCallable(name);
        assert unit != null : String.format("Unknown name %s", name);
        assert unit instanceof Procedure : String.format(
            "Non-procedure name %s", name);
        if (unit instanceof Recipe) {
            Recipe recipe = (Recipe) unit;
            body = body.clone(recipe);
            // the rules in a transaction body are no longer available
            for (Rule rule : body.getRules()) {
                this.topNames.remove(rule.getFullName());
            }
        } else {
            body = body.clone(name);
        }
        ((Procedure) unit).setBody(body);
    }

    /** Checks if a callable unit with a given name has been declared. */
    public boolean hasCallable(String name) {
        return this.callableMap.containsKey(name);
    }

    /** Returns the callable unit with a given name. */
    public Callable getCallable(String name) {
        return this.callableMap.get(name);
    }

    /** Returns the collection of all callable units known in this name space. */
    public Collection<Callable> getCallables() {
        return this.callableMap.values();
    }

    /** Mapping from declared callable unit names to the units. */
    private final Map<String,Callable> callableMap = new TreeMap<String,Callable>();

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

    /** Full name of the program file being parsed. */
    private String fullName;
    /** Parent name of {@link #fullName}. */
    private String parentName;

    /**
     * Returns the set of all top-level rule and recipe names.
     * Rules and recipes invoked from (other) recipes are 
     * excluded from this set.
     */
    public Set<String> getTopNames() {
        return this.topNames;
    }

    /** Set of top-level rule and recipe names. */
    private final Set<String> topNames = new TreeSet<String>();

    /** 
     * Signals that a given name should be added to the used names.
     * The name should be a known rule or recipe name. 
     */
    public void addUsedName(String name) {
        this.usedNames.add(name);
    }

    /** Returns the set of all used rules,
     * i.e., all rules for which {@link Namespace#addUsedName(String)}
     * has been invoked.
     */
    public Set<String> getUsedNames() {
        return this.usedNames;
    }

    /** Set of used rule names. */
    private final Set<String> usedNames = new HashSet<String>();

    @Override
    public String toString() {
        return String.format("Namespace for %s, defining %s", getFullName(),
            this.topNames);
    }

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
