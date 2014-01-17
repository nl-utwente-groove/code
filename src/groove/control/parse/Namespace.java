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
import groove.control.symbolic.Term;
import groove.grammar.QualName;
import groove.grammar.Recipe;
import groove.grammar.Rule;
import groove.grammar.model.FormatError;
import groove.grammar.model.FormatErrorSet;
import groove.util.antlr.ParseInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
    /** Constructs a new name space, on the basis of a given algebra family. 
     * @param checkDependencies flag to determine whether the name space
     * should check for circular dependencies and forward references.
     */
    public Namespace(AlgebraFamily family, boolean checkDependencies) {
        this.family = family;
        this.checkDependencies = checkDependencies;
    }

    /** Returns the algebra family of this name space. */
    public AlgebraFamily getAlgebraFamily() {
        return this.family;
    }

    private final AlgebraFamily family;

    /** Indicates if the name space should check for circular
     * dependencies ({@link #addDependency}) and
     * call resolution ({@link #isResolved} and
     * {@link #resolveFunctions}.
     */
    public boolean isCheckDependencies() {
        return this.checkDependencies;
    }

    private final boolean checkDependencies;

    /**
     * Adds a function or recipe name to the set of declared names.
     * @return {@code true} if the name is new.
     */
    public boolean addFunction(String fullName, int priority,
            List<CtrlPar.Var> sig, String controlName, int startLine) {
        boolean result = !hasCallable(fullName);
        if (result) {
            addProcedure(new Function(fullName, priority, sig, controlName,
                startLine));
        }
        return result;
    }

    /**
     * Adds a function or recipe name to the set of declared names.
     * @return {@code true} if the name is new.
     */
    public boolean addRecipe(String fullName, int priority,
            List<CtrlPar.Var> sig, String controlName, int startLine) {
        boolean result = !hasCallable(fullName);
        if (result) {
            this.topNames.add(fullName);
            addProcedure(new Recipe(fullName, priority, sig, controlName,
                startLine));
        }
        return result;
    }

    private void addProcedure(Procedure proc) {
        String fullName = proc.getFullName();
        this.callableMap.put(fullName, proc);
        Set<String> callers = new HashSet<String>();
        this.callerMap.put(fullName, callers);
        callers.add(fullName);
        Set<String> callees = new HashSet<String>();
        callees.add(fullName);
        this.calleeMap.put(fullName, callees);
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
    private final Map<String,Callable> callableMap =
        new TreeMap<String,Callable>();

    /** Sets the full name of the control program currently being explored. */
    public void setControlName(String controlName) {
        this.controlName = controlName;
        this.parentName = QualName.getParent(controlName);
    }

    /** Returns the full name of the control program being parsed. */
    public String getControlName() {
        return this.controlName;
    }

    /** Returns the parent name space of the control program being parsed. */
    public String getParentName() {
        return this.parentName;
    }

    /** Full name of the program file being parsed. */
    private String controlName;
    /** Parent name of {@link #controlName}. */
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

    /** 
     * Indicates if a certain procedure name has been resolved. 
     */
    public boolean isResolved(String name) {
        return !isCheckDependencies() || this.resolved.contains(name);
    }

    private Set<String> resolved = new HashSet<String>();

    /** Tries to add a dependency from a caller to a callee.
     * @return {@code true} if the dependency was added; {@code false} if this
     * was prevented by a circularity
     */
    public boolean addDependency(String caller, String callee) {
        boolean result = true;
        if (isCheckDependencies()) {
            Set<String> parentCallers = this.callerMap.get(caller);
            Set<String> callees = this.calleeMap.get(caller);
            result = !parentCallers.contains(callee);
            if (result && callees.add(callee)) {
                Set<String> childCallees = this.calleeMap.get(callee);
                for (String parentCaller : parentCallers) {
                    this.calleeMap.get(parentCaller).addAll(childCallees);
                }
                for (String childCallee : childCallees) {
                    this.callerMap.get(childCallee).addAll(parentCallers);
                }
            }
        }
        return result;
    }

    /**
     * Returns a set of function names in an order in which there are no
     * forward dependencies.
     * Functions that already have bodies do not count as dependencies.
     * @return an ordered list consisting of the elements of {@code functions};
     * or {@code null} if no order exists
     */
    public List<String> resolveFunctions(Collection<String> functions) {
        List<String> result = new ArrayList<String>();
        if (isCheckDependencies()) {
            // collect the remaining functions to be resolved
            Set<String> resolved = this.resolved;
            functions = new HashSet<String>(functions);
            while (!functions.isEmpty()) {
                Iterator<String> remainingIter = functions.iterator();
                String candidate = null;
                while (remainingIter.hasNext()) {
                    candidate = remainingIter.next();
                    resolved.add(candidate);
                    if (resolved.containsAll(this.calleeMap.get(candidate))) {
                        remainingIter.remove();
                        break;
                    } else {
                        resolved.remove(candidate);
                        candidate = null;
                    }
                }
                if (candidate == null) {
                    result = null;
                    break;
                } else {
                    result.add(candidate);
                }
            }
        } else {
            result.addAll(functions);
        }
        return result;
    }

    /** Mapping from function names to other functions being invoked from it. */
    private final Map<String,Set<String>> calleeMap =
        new HashMap<String,Set<String>>();

    /** Mapping from function names to other functions invoking it. */
    private final Map<String,Set<String>> callerMap =
        new HashMap<String,Set<String>>();

    @Override
    public String toString() {
        return String.format("Namespace for %s, defining %s", getControlName(),
            this.callableMap.keySet());
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

    /** Returns a prototype term, to be shared by all programs compiled
     * against this name space.
     */
    public Term getPrototype() {
        if (this.prototype == null) {
            this.prototype = Term.prototype();
        }
        return this.prototype;
    }

    private Term prototype;
}
