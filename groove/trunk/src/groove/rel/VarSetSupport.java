// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente
 
// Licensed under the Apache License, Version 2.0 (the "License"); 
// you may not use this file except in compliance with the License. 
// You may obtain a copy of the License at 
// http://www.apache.org/licenses/LICENSE-2.0 
 
// Unless required by applicable law or agreed to in writing, 
// software distributed under the License is distributed on an 
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
// either express or implied. See the License for the specific 
// language governing permissions and limitations under the License.
/*
 * $Id: VarSetSupport.java,v 1.1.1.1 2007-03-20 10:05:24 kastenberg Exp $
 */
package groove.rel;

import java.util.Set;

/**
 * Specifies support for classes that may maintain sets of variables.
 * A distinction is made between the variables occurring in an object and 
 * the variables bound by that object.
 * @author Arend Rensink
 * @version $Revision: 1.1.1.1 $
 */
public interface VarSetSupport {
    /**
     * Returns the set of variables occurring in this object.
     * @see RegExpr#allVarSet()
     */
    Set<String> allVarSet();
    /**
     * Returns the set of variables bound by this object.
     * This is a subset of all variables, which are guaranteed
     * to receive a value when the object is applied.
     * @see #allVarSet()
     */
    Set<String> boundVarSet();
    /**
     * Indicates if a given variable is used by this graph.
     * Convenience method for <code>allVarSet().contains(var)</code>.
     */
    boolean hasVar(String var);
    /**
     * Indicates if a given variable is bound by this graph.
     * Convenience method for <code>boundVarSet().contains(var)</code>.
     */
    boolean bindsVar(String var);
    /**
     * Indicates if this object has any variables at all.
     * Convenienct method for <code>!varSet.isEmpty()</code>.
     */
    boolean hasVars();
}
