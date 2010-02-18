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

import java.util.HashSet;
import java.util.Set;

/**
 * @author Olaf Keijsers
 * @version $Revision $
 * 
 * Class Scope, keeps track of variables declared and initialized in a given scope
 */
public class Scope {
    /**
     * Creates a new Scope with empty declared and initialized sets
     */
    public Scope() {
        this.declared = new HashSet<String>();
        this.initialized = new HashSet<String>();
    }

    /**
     * Declares a variable in this Scope
     * @param var the variable to declare
     */
    public void declare(String var) {
        this.declared.add(var);
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
        return this.declared.contains(var);
    }

    /**
     * Checks whether a given variable is initialized in this Scope
     * @param var the variable to check
     * @return true if the variable is initialized, false otherwise
     */
    public boolean isInitialized(String var) {
        return this.initialized.contains(var);
    }

    private Set<String> declared, initialized;
}
