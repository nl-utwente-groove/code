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

import java.util.ArrayDeque;

/**
 * @author Olaf Keijsers
 * @version $Revision $
 * 
 * Class SymbolTable
 * Keeps track of symbols used in the control language, including scopes
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
    public boolean declareSymbol(String symbolName, String symbolType) {
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
    public String getType(String symbolName) {
        String type = null;
        for (Scope s : this.scopes) {
            if (s.isDeclared(symbolName)) {
                type = s.getType(symbolName);
            }
        }
        return type;
    }

    private ArrayDeque<Scope> scopes;
}
