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
package groove.algebra;

/**
 * Interface for integer algebras.
 * @author Arend Rensink
 * @version $Revision: 1577 $
 */
public abstract class BoolSignature<Bool> implements Signature {
    /** Negation. */
    public abstract Bool not(Bool arg);
    
    /** Conjunction. */
    public abstract Bool and(Bool arg0, Bool arg1);
    
    /** Disjunction. */
    public abstract Bool or(Bool arg0, Bool arg1);
    
    /** Equality test. */
    public abstract Bool eq(Bool arg0, Bool arg1);

    /** Only <code>true</code> and <code>false</code> are legal values. */
    final public boolean isValue(String value) {
        return value.equals("true") || value.equals("false");
    }
}
