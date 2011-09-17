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
 * Implementation of integers consisting of a singleton value.
 * To be used in conjunction with {@link BoolPointAlgebra} and {@link StringPointAlgebra}.
 * @author Arend Rensink
 * @version $Revision $
 */
public class IntPointAlgebra extends IntSignature<Object,Object,Object>
        implements Algebra<Object> {
    /** Private constructor for the singleton instance. */
    private IntPointAlgebra() {
        // empty
    }

    public String getName() {
        return NAME;
    }

    public String getSymbol(Object value) {
        return value.toString();
    }

    public Object getValueFromString(String constant) {
        return singleInt;
    }

    @Override
    protected Object toValue(Integer constant) {
        return singleInt;
    }

    @Override
    public Object add(Object arg0, Object arg1) {
        return singleInt;
    }

    @Override
    public Object div(Object arg0, Object arg1) {
        return singleInt;
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

    @Override
    public Object max(Object arg0, Object arg1) {
        return singleInt;
    }

    @Override
    public Object min(Object arg0, Object arg1) {
        return singleInt;
    }

    @Override
    public Object mod(Object arg0, Object arg1) {
        return singleInt;
    }

    @Override
    public Object mul(Object arg0, Object arg1) {
        return singleInt;
    }

    @Override
    public Object neg(Object arg) {
        return singleInt;
    }

    @Override
    public Object sub(Object arg0, Object arg1) {
        return singleInt;
    }

    @Override
    public Object toString(Object arg) {
        return singleString;
    }

    /** Name of this algebra. */
    public static final String NAME = "pint";
    /** 
     * Representation of the point value of the string algebra;
     * redefined literally to avoid class loading dependencies.
     * @see StringPointAlgebra#singleString
     */
    public static final String singleString = StringPointAlgebra.singleString;
    /** 
     * Representation of the point value of the boolean algebra;
     * redefined literally to avoid class loading dependencies.
     * @see BoolPointAlgebra#singleBool
     */
    public static final String singleBool = BoolPointAlgebra.singleBool;
    /** Point value of the int algebra. */
    public static final String singleInt = "1";
    /** Singleton instance of this algebra. */
    public static final IntPointAlgebra instance = new IntPointAlgebra();
}
