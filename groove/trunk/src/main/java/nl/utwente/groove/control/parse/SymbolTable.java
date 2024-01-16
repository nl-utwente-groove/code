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
package nl.utwente.groove.control.parse;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import nl.utwente.groove.algebra.syntax.SortMap;
import nl.utwente.groove.control.CtrlType;
import nl.utwente.groove.util.LazyFactory;

/**
 * Keeps track of symbols used in the control language, including scopes.
 * @author Olaf Keijsers
 * @version $Revision$
 */
public class SymbolTable {
    /**
     * Creates a new SymbolTable
     */
    public SymbolTable() {
        this.scopes = new ArrayDeque<>();
    }

    /**
     * Opens a scope, which can hold symbols which will be kept until the scope is closed
     */
    public void openScope() {
        this.scopes.push(new Scope());
    }

    /**
     * Removes the current scope
     */
    public void closeScope() {
        this.scopes.pop();
        this.sortMap.reset();
    }

    /**
     * Declares a symbol in the current scope.
     * @param symbolName the name of the symbol to be declared
     * @require this.scopes.peek().get(symbolName) == null
     * @return true if the declaration succeeded, false if not
     */
    public boolean declareSymbol(String symbolName, CtrlType symbolType) {
        if (!this.scopes.peek().isDeclared(symbolName)) {
            this.scopes.peek().declare(symbolName, symbolType, false);
            this.sortMap.reset();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Declares a symbol in the current scope, optionally
     * declaring it to be an output parameter.
     * @param symbolName the name of the symbol to be declared
     * @require this.scopes.peek().get(symbolName) == null
     * @return true if the declaration succeeded, false if not
     */
    public boolean declareSymbol(String symbolName, CtrlType symbolType, boolean out) {
        if (!this.scopes.peek().isDeclared(symbolName)) {
            this.scopes.peek().declare(symbolName, symbolType, out);
            this.sortMap.reset();
            return true;
        } else {
            return false;
        }
    }

    /** Returns the output parameters in the current scope. */
    public Set<String> getOutPars() {
        return this.scopes.peek().getOutPars();
    }

    /**
     * Checks if the given symbol is declared in this scope or a higher one
     * @param symbolName the symbol to be checked
     * @return true if the symbol is declared, false if not
     */
    public boolean isDeclared(String symbolName) {
        return !this.scopes.stream().noneMatch(s -> s.isDeclared(symbolName));
    }

    /** Returns the set of all symbols currently declared in this table. */
    public Set<String> getDeclared() {
        Set<String> result = new HashSet<>();
        this.scopes.stream().flatMap(s -> s.getDeclared().stream()).forEach(result::add);
        return result;
    }

    /**
     * Gets the type of a given symbol
     * @param symbolName the name of the symbol to look up
     * @require isDeclared(symbolName) == true
     * @return the type of symbolName
     */
    public CtrlType getType(String symbolName) {
        CtrlType type = null;
        for (Scope s : this.scopes) {
            if (s.isDeclared(symbolName)) {
                type = s.getType(symbolName);
            }
        }
        return type;
    }

    private final ArrayDeque<Scope> scopes;

    /** Returns the sort map for the data variables that are currently in scope. */
    public SortMap getSortMap() {
        return this.sortMap.get();
    }

    private final LazyFactory<SortMap> sortMap = LazyFactory.instance(this::computeSortMap);

    /** Computes the value for {@link #sortMap}. */
    private SortMap computeSortMap() {
        var result = new SortMap();
        for (var symbol : getDeclared()) {
            var sort = getType(symbol).getSort();
            if (sort != null) {
                result.add(symbol, sort);
            }
        }
        return result;
    }

    /**
     * Keeps track of variables declared and initialised in a given scope.
     * The type parameter is generic.
     * @author Olaf Keijsers
     * @version $Revision$
     */
    private static class Scope {
        /**
         * Creates a new Scope with empty declared and initialised sets
         */
        public Scope() {
            this.declared = new HashMap<>();
            this.outPars = new LinkedHashSet<>();
        }

        /**
         * Declares a variable in this Scope
         * @param var the variable to declare
         */
        public void declare(String var, CtrlType type, boolean out) {
            this.declared.put(var, type);
            if (out) {
                this.outPars.add(var);
            }
        }

        /**
         * Checks whether a given variable is declared in this Scope
         * @param var the variable to check
         * @return true if the variable is declared, false otherwise
         */
        public boolean isDeclared(String var) {
            return this.declared.containsKey(var);
        }

        /** Returns the set of symbols declared in this scope. */
        public Set<String> getDeclared() {
            return this.declared.keySet();
        }

        /**
         * Returns the type of a declared variable
         * @param var the name of the variable to look up
         * @return the type of var
         */
        public CtrlType getType(String var) {
            return this.declared.get(var);
        }

        /** Returns the output parameters in the top scope. */
        public Set<String> getOutPars() {
            return this.outPars;
        }

        private final Map<String,CtrlType> declared;
        private final Set<String> outPars;
    }
}
