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
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

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
        scopes = new ArrayDeque<Scope>();
    }
    
    /**
     * Opens a scope, which can hold symbols which will be kept until the scope is closed
     */
    public void openScope() {
        Scope s = new Scope();
        if (!scopes.isEmpty()) {
            scopes.peek().addSubScope(s);
            debug("adding subscope");
        }
        scopes.push(s);
        debug("opening scope");
    }
    
    /**
     * Removes the current scope
     */
    public void closeScope() {
        Scope s = scopes.pop();
        debug("closing scope with vars: "+s.getInitialized().toString());
        if (!scopes.isEmpty()) {
            scopes.peek().closeSubScope(s);
            debug("closing subscope");
        }
    }
    
    /**
     * Declares a symbol in the current scope
     * @param symbolName the name of the symbol to be declared
     * @require this.scopes.peek().get(symbolName) == null
     * @return true if the declaration succeeded, false if not
     */
    public boolean declareSymbol(String symbolName) {
        if (!scopes.peek().isDeclared(symbolName)) { 
            scopes.peek().declare(symbolName);
            debug("declaring variable: "+symbolName);
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
        for (Scope s : scopes) {
            if (s.isDeclared(symbolName)) {
                found = true;
            }
        }
        if (found) {
            scopes.peek().initialize(symbolName);
        }
        debug("initializing variable: "+symbolName);
    }
    
    /**
     * Checks if the given symbol is initialized in this scope or a higher one
     * @param symbolName the symbol to be checked
     * @return true if the symbol is initialized, false if not
     */
    public boolean isInitialized(String symbolName) {
        debug("checking if initialized: "+symbolName);
        for (Scope s : scopes) {
            if (s.isInitialized(symbolName)) {
                debug("   yes");
                return true;
            }
        }
        debug("   no");
        return false;
    }
    
    public boolean canInitialize(String symbolName) {
        debug("checking if we can initialize: "+symbolName);
        for (Scope s : scopes) {
            if (s.isInitializedHere(symbolName)) {
                debug("    no");
                return false;
            }
        }
        debug("    yes");
        return true;
    }
    
    /**
     * Checks if the given symbol is declared in this scope or a higher one
     * @param symbolName the symbol to be checked
     * @return true if the symbol is declared, false if not
     */
    public boolean isDeclared(String symbolName) {
        debug("checking if declared: "+symbolName);
        for (Scope s : scopes) {
            if (s.isDeclared(symbolName)) {
                debug("   true");
                return true;
            }
        }
        debug("   false");
        return false;
    }
    
    private void debug(String msg) {
        //System.out.println("== ST DEBUG: "+msg);
    }
    
    private ArrayDeque<Scope> scopes;
}
