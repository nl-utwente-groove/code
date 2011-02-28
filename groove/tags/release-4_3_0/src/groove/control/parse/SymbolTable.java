/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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
package groove.control.parse;

import groove.control.CtrlType;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Keeps track of symbols used in the control language, including scopes.
 * @author Olaf Keijsers
 * @version $Revision $
 */
public class SymbolTable {
    /**
     * Creates a new SymbolTable
     */
    public SymbolTable() {
        this.scopes = new ArrayDeque<Scope>();
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
    }

    /**
     * Declares a symbol in the current scope
     * @param symbolName the name of the symbol to be declared
     * @require this.scopes.peek().get(symbolName) == null
     * @return true if the declaration succeeded, false if not
     */
    public boolean declareSymbol(String symbolName, CtrlType symbolType) {
        if (!this.scopes.peek().isDeclared(symbolName)) {
            this.scopes.peek().declare(symbolName, symbolType);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Initialized a symbol in the current scope
     * @param symbolName the symbol to be initialized
     * @require this.isDeclared(symbolName) == true
     */
    public void initializeSymbol(String symbolName) {
        boolean found = false;
        for (Scope s : this.scopes) {
            if (s.isDeclared(symbolName)) {
                found = true;
            }
        }
        if (found) {
            this.scopes.peek().initialize(symbolName);
        }
    }

    /**
     * Checks if the given symbol is initialized in this scope or a higher one
     * @param symbolName the symbol to be checked
     * @return true if the symbol is initialized, false if not
     */
    public boolean isInitialized(String symbolName) {
        for (Scope s : this.scopes) {
            if (s.isInitialized(symbolName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether we can initialize a given variable. Variables may only be
     * initialized once in a given program.
     * @param symbolName the variable to check
     * @return true if the variable can be initialized, false otherwise
     */
    public boolean canInitialize(String symbolName) {
        for (Scope s : this.scopes) {
            if (s.isInitialized(symbolName)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the given symbol is declared in this scope or a higher one
     * @param symbolName the symbol to be checked
     * @return true if the symbol is declared, false if not
     */
    public boolean isDeclared(String symbolName) {
        for (Scope s : this.scopes) {
            if (s.isDeclared(symbolName)) {
                return true;
            }
        }
        return false;
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

    private ArrayDeque<Scope> scopes;

    /**
     * Keeps track of variables declared and initialised in a given scope.
     * The type parameter is generic.
     * @author Olaf Keijsers
     * @version $Revision $
     */
    private class Scope {
        /**
         * Creates a new Scope with empty declared and initialized sets
         */
        public Scope() {
            this.declared = new HashMap<String,CtrlType>();
            this.initialized = new HashSet<String>();
        }

        /**
         * Declares a variable in this Scope
         * @param var the variable to declare
         */
        public void declare(String var, CtrlType type) {
            this.declared.put(var, type);
        }

        /**
         * Initializes a variable in this Scope
         * @param var the variable to initialize
         */
        public void initialize(String var) {
            this.initialized.add(var);
        }

        /**
         * Checks whether a given variable is declared in this Scope
         * @param var the variable to check
         * @return true if the variable is declared, false otherwise
         */
        public boolean isDeclared(String var) {
            return this.declared.containsKey(var);
        }

        /**
         * Checks whether a given variable is initialized in this Scope
         * @param var the variable to check
         * @return true if the variable is initialized, false otherwise
         */
        public boolean isInitialized(String var) {
            return this.initialized.contains(var);
        }

        /**
         * Returns the type of a declared variable
         * @param var the name of the variable to look up
         * @return the type of var
         */
        public CtrlType getType(String var) {
            return this.declared.get(var);
        }

        private Map<String,CtrlType> declared;
        private Set<String> initialized;
    }
}
