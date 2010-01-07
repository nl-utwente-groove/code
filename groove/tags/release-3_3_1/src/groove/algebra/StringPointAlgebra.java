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
 * Implementation of strings consisting of a singleton value.
 * To be used in conjunction with {@link BoolPointAlgebra}.
 * @author Arend Rensink
 * @version $Revision $
 */
public class StringPointAlgebra extends StringSignature<Object,Object>
        implements Algebra<Object> {

    @Override
    public Object concat(Object arg0, Object arg1) {
        return singleString;
    }

    @Override
    public Object eq(Object arg0, Object arg1) {
        return singleBool;
    }

    @Override
    public Object ge(Object arg0, Object arg1) {
        return singleBool;
    }

    @Override
    public Object gt(Object arg0, Object arg1) {
        return singleBool;
    }

    @Override
    public Object le(Object arg0, Object arg1) {
        return singleBool;
    }

    @Override
    public Object lt(Object arg0, Object arg1) {
        return singleBool;
    }

    public String getName() {
        return NAME;
    }

    public String getSymbol(Object value) {
        return value.toString();
    }

    public Object getValue(String constant) {
        return singleString;
    }

    /** Name of this algebra. */
    public static final String NAME = "pstring";
    
    /** 
     * Representation of the point value of the boolean algebra;
     * redefined literally to avoid class loading dependencies.
     * @see BoolPointAlgebra#singleBool
     */
    public static final String singleBool = "B";
    /** Point value of the string algebra. */
    public static final String singleString = "S";
}
