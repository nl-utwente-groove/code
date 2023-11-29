/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2023
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
 * $Id$
 */
package nl.utwente.groove.control.parse;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.control.Procedure;
import nl.utwente.groove.control.term.Term;
import nl.utwente.groove.grammar.Action;
import nl.utwente.groove.grammar.Callable;
import nl.utwente.groove.grammar.GrammarProperties;
import nl.utwente.groove.grammar.ModuleName;
import nl.utwente.groove.grammar.QualName;
import nl.utwente.groove.grammar.Rule;
import nl.utwente.groove.util.antlr.ParseInfo;
import nl.utwente.groove.util.parse.Fallible;
import nl.utwente.groove.util.parse.FormatError;
import nl.utwente.groove.util.parse.FormatErrorSet;

/**
 * Namespace for building a control automaton.
 * The namespace holds the function, transaction and rule names.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public class Namespace implements ParseInfo, Fallible {
    /** Constructs a new name space, on the basis of a given algebra family.
     */
    public Namespace(GrammarProperties grammarProperties) {
        this.grammarProperties = grammarProperties;
    }

    /** Returns the algebra family of this name space. */
    public GrammarProperties getGrammarProperties() {
        return this.grammarProperties;
    }

    private final GrammarProperties grammarProperties;

    /**
     * Adds a function or recipe to the set of declared procedures.
     * @return {@code true} if the name is new.
     */
    public boolean addProcedure(Procedure proc) {
        QualName fullName = proc.getQualName();
        boolean result = !hasCallable(fullName);
        if (result) {
            this.callableMap.put(fullName, proc);
            this.declaringNameMap.put(fullName, getControlName());
        }
        return result;
    }

    /** Returns the control program name in which a procedure with a given name has been declared. */
    public @Nullable QualName getDeclaringName(QualName procName) {
        return this.declaringNameMap.get(procName);
    }

    /** Mapping from declared procedures to the declaring control program. */
    private final Map<QualName,@Nullable QualName> declaringNameMap = new HashMap<>();

    /**
     * Adds a rule to the name space.
     */
    public boolean addRule(Rule rule) {
        QualName ruleName = rule.getQualName();
        boolean result = !hasCallable(ruleName);
        if (result) {
            this.callableMap.put(ruleName, rule);
        }
        return result;
    }

    /** Checks if a callable unit with a given name has been declared. */
    public boolean hasCallable(QualName name) {
        return this.callableMap.containsKey(name);
    }

    /** Returns the callable unit with a given name. */
    public @Nullable Callable getCallable(QualName name) {
        return this.callableMap.get(name);
    }

    /** Returns the collection of all callable units known in this name space. */
    public Collection<Callable> getCallables() {
        return this.callableMap.values();
    }

    /** Tries to add a dependency from a caller to a callee.
     */
    public void addCall(@Nullable QualName caller, QualName callee) {
        if (caller != null) {
            Set<QualName> parentCallers = getFromMap(this.callerMap, caller);
            Set<QualName> callees = getFromMap(this.calleeMap, caller);
            if (callees.add(callee)) {
                Set<QualName> childCallees = getFromMap(this.calleeMap, callee);
                for (QualName parentCaller : parentCallers) {
                    getFromMap(this.calleeMap, parentCaller).addAll(childCallees);
                }
                for (QualName childCallee : childCallees) {
                    getFromMap(this.callerMap, childCallee).addAll(parentCallers);
                }
            }
        }
        this.usedNames.add(callee);
    }

    private Set<QualName> getFromMap(Map<QualName,@Nullable Set<QualName>> map, QualName key) {
        var result = map.get(key);
        if (result == null) {
            map.put(key, result = new HashSet<>());
            result.add(key);
        }
        return result;
    }

    /** Mapping from declared callable unit names to the units. */
    private final Map<QualName,Callable> callableMap = new TreeMap<>();

    /** Mapping from function names to other functions being invoked from it. */
    private final Map<QualName,@Nullable Set<QualName>> calleeMap = new HashMap<>();
    /** Mapping from function names to other functions invoking it. */
    private final Map<QualName,@Nullable Set<QualName>> callerMap = new HashMap<>();

    /** Sets the full name of the control program currently being explored. */
    public void setControlName(QualName controlName) {
        assert !controlName.hasErrors() : String
            .format("Errors in control: %s", controlName.getErrors());
        if (!this.importedMap.containsKey(controlName)) {
            this.importedMap.put(controlName, new HashSet<>());
            this.importMap.put(controlName, new HashMap<>());
        }
        this.controlName = controlName;
    }

    /** Returns the full name of the control program being parsed.
     * This should only be called if the a control program has been loaded,
     * and hence the control name is non-{@code null}.
     */
    public QualName getControlName() {
        var result = this.controlName;
        assert result != null;
        return result;
    }

    /** Returns the module name of this name space,
     * being the parent of the control name.
     * This should only be called if the a control program has been loaded,
     * and hence the control name is non-{@code null}.
     */
    public ModuleName getModuleName() {
        return getControlName().parent();
    }

    /** Full name of the program file being parsed. */
    private @Nullable QualName controlName;

    /** Adds an import to the map of the current control program. */
    public void addImport(QualName fullName) {
        this.importedMap.get(this.controlName).add(fullName);
        this.importMap.get(this.controlName).put(fullName.last(), fullName);
    }

    /** Tests if a given qualified name is imported. */
    public boolean hasImport(QualName fullName) {
        return this.importedMap.get(this.controlName).contains(fullName);
    }

    /** Returns a mapping from last names to full names for all imported names. */
    public Map<String,QualName> getImportMap() {
        return this.importMap.get(this.controlName);
    }

    /** Map from control names to sets of imported action names. */
    private final Map<QualName,Set<QualName>> importedMap = new HashMap<>();
    /** Map from control names to maps from imported last names to full names. */
    private final Map<QualName,Map<String,QualName>> importMap = new HashMap<>();

    /**
     * Returns the set of all top-level actions (rules and recipes).
     * Rules and recipes directly or indirectly invoked from (other) procedures are
     * excluded from this set.
     */
    public Set<Action> getActions() {
        Set<Action> result = this.actions;
        if (result == null) {
            Set<QualName> calledNames = new HashSet<>();
            for (Callable callable : this.callableMap.values()) {
                if (callable instanceof Action) {
                    if (callable.getKind().isProcedure()) {
                        QualName name = callable.getQualName();
                        Set<QualName> newCalledNames = new HashSet<>();
                        Set<QualName> calleeMapValue = this.calleeMap.get(name);
                        if (calleeMapValue != null) {
                            newCalledNames.addAll(calleeMapValue);
                        }
                        newCalledNames.remove(name);
                        calledNames.addAll(newCalledNames);
                    }
                }
            }
            result = this.actions = new TreeSet<>();
            for (Callable unit : this.callableMap.values()) {
                if (!(unit instanceof Action action)) {
                    continue;
                }
                if (action.isProperty() || !calledNames.contains(action.getQualName())) {
                    result.add(action);
                }
            }
        }
        return result;
    }

    /** Set of top-level rule and recipe names. */
    private @Nullable Set<Action> actions;

    /**
     * Returns the set of all property actions in the grammar.
     */
    public Set<Action> getProperties() {
        Set<Action> result = this.properties;
        if (result == null) {
            result = this.properties = new TreeSet<>();
            for (Action action : getActions()) {
                if (action.isProperty()) {
                    result.add(action);
                }
            }
        }
        return result;
    }

    /** Set of property actions. */
    private @Nullable Set<Action> properties;

    /**
     * Returns the set of all top-level transformer actions (rules and recipes).
     * Rules and recipes directly or indirectly invoked from (other) procedures are
     * excluded from this set.
     */
    public Set<Action> getTransformers() {
        Set<Action> result = this.transformers;
        if (result == null) {
            result = this.transformers = new TreeSet<>(getActions());
            result.removeAll(getProperties());
        }
        return result;
    }

    /** Set of transformer actions. */
    private @Nullable Set<Action> transformers;

    /** Returns the set of all used names,
     * i.e., all rules for which {@link #addCall(QualName, QualName)}
     * has been invoked.
     */
    public Set<QualName> getUsedNames() {
        return this.usedNames;
    }

    /** Set of callable names appearing explicitly in the control program. */
    private final Set<QualName> usedNames = new HashSet<>();

    @Override
    public String toString() {
        return String
            .format("Namespace for %s, defining %s", getControlName(), this.callableMap.keySet());
    }

    /** Adds an error to the errors contained in this name space. */
    @Override
    public void addError(String message, Object... args) {
        this.errors.add(message, args);
    }

    /** Adds an error to the errors contained in this name space. */
    @Override
    public void addError(FormatError error) {
        this.errors.add(error);
    }

    /** Returns the errors collected in this name space. */
    @Override
    public FormatErrorSet getErrors() {
        return this.errors;
    }

    private final FormatErrorSet errors = new FormatErrorSet();

    /** Returns a prototype term, to be shared by all programs compiled
     * against this name space.
     */
    public Term getPrototype() {
        var result = this.prototype;
        if (result == null) {
            result = this.prototype = Term.prototype();
        }
        return result;
    }

    private @Nullable Term prototype;
}
