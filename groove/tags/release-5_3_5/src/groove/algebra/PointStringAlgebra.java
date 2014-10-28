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

import groove.algebra.syntax.Expression;

/**
 * Implementation of strings consisting of a singleton value.
 * To be used in conjunction with {@link PointBoolAlgebra}.
 * @author Arend Rensink
 * @version $Revision $
 */
public class PointStringAlgebra extends StringAlgebra<String,Boolean,Integer> implements
    PointAlgebra<String> {
    /** Private constructor for the singleton instance. */
    private PointStringAlgebra() {
        // empty
    }

    @Override
    public String concat(String arg0, String arg1) {
        return singleString;
    }

    @Override
    public Boolean eq(String arg0, String arg1) {
        return singleBool;
    }

    @Override
    public Boolean neq(String arg0, String arg1) {
        return singleBool;
    }

    @Override
    public Boolean ge(String arg0, String arg1) {
        return singleBool;
    }

    @Override
    public Boolean gt(String arg0, String arg1) {
        return singleBool;
    }

    @Override
    public Boolean le(String arg0, String arg1) {
        return singleBool;
    }

    @Override
    public Boolean lt(String arg0, String arg1) {
        return singleBool;
    }

    @Override
    public Integer length(String arg) {
        return singleInt;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public AlgebraFamily getFamily() {
        return AlgebraFamily.POINT;
    }

    @Override
    public boolean isValue(Object value) {
        return value == singleString;
    }

    @Override
    public String getSymbol(Object value) {
        return singleString;
    }

    @Override
    public Expression toTerm(Object value) {
        return Constant.instance(singleString);
    }

    @Override
    public String toJavaValue(Object value) {
        return singleString;
    }

    @Override
    public String toValueFromConstant(Constant constant) {
        return singleString;
    }

    @Override
    public String getPointValue() {
        return singleString;
    }

    @Override
    public String toValueFromJavaString(String value) {
        return singleString;
    }

    /** Name of this algebra. */
    public static final String NAME = "pstring";

    /** 
     * Representation of the point value of the boolean algebra;
     * redefined literally to avoid class loading dependencies.
     * @see PointBoolAlgebra#singleBool
     */
    public static final Boolean singleBool = PointBoolAlgebra.singleBool;
    /** Point value of the string algebra. */
    public static final Integer singleInt = PointIntAlgebra.singleInt;
    /** Point value of the string algebra. */
    public static final String singleString = "";
    /** Singleton instance of this algebra. */
    public static final PointStringAlgebra instance = new PointStringAlgebra();
}
