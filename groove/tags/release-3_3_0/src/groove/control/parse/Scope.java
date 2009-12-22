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
@SuppressWarnings("all")
public class Scope {
    public Scope() {
        declared = new HashSet<String>();
        initialized = new HashSet<String>();
        initializedBySubScopes = new HashSet<String>();
        subScopes = new HashSet<Scope>();
    }
    
    public void declare(String var) {
        declared.add(var);
    }
    
    public void initialize(String var) {
        initialized.add(var);
    }
    
    public boolean isDeclared(String var) {
        return declared.contains(var);
    }
    
    public boolean isInitialized(String var) {
        return initialized.contains(var) || initializedBySubScopes.contains(var);
    }
    
    public boolean isInitializedHere(String var) {
        return initialized.contains(var);
    }
    
    public void addSubScope(Scope sub) {
        subScopes.add(sub);
    }
    
    public void closeSubScope(Scope sub) {
        // gather all variables initialized in subscopes
        Set<String> initializedVars = new HashSet<String>();
        Set<String> initializedVars2 = new HashSet<String>();
        for (Scope s : subScopes) {
            initializedVars.addAll(s.getInitialized());
        }
        initializedVars2.addAll(initializedVars);
        
        // now check which of these are initialized in all subscopes
        for (String str : initializedVars2) {
            for (Scope s : subScopes) {
                if (!s.getInitialized().contains(str)) {
                    initializedVars.remove(str);
                }
            }
        }
        
        initializedBySubScopes = initializedVars;
        debug("now initialized by subscopes: "+initializedBySubScopes.toString());
    }
    
    public Set<String> getInitialized() {
        Set<String> ret = new HashSet<String>();
        ret.addAll(initialized);
        ret.addAll(initializedBySubScopes);
        return ret;
    }
    
    private void debug(String msg) {
        //System.out.println("Variable debug (Scope): "+msg);
    }
    
    private Set<String> declared, initialized, initializedBySubScopes;
    private Set<Scope> subScopes;
}
