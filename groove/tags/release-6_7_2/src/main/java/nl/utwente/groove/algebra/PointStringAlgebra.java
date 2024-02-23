/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.algebra;

import nl.utwente.groove.algebra.syntax.Expression;

/**
 * Implementation of strings consisting of a singleton value.
 * To be used in conjunction with {@link PointBoolAlgebra}.
 * @author Arend Rensink
 * @version $Revision$
 */
public final class PointStringAlgebra extends StringAlgebra<Integer,Double,Boolean,String>
    implements PointAlgebra<String> {
    /** Private constructor for the singleton instance. */
    private PointStringAlgebra() {
        // empty
    }

    @Override
    public String concat(String arg0, String arg1) {
        return singleString;
    }

    @Override
    public Boolean isBool(String arg0) {
        return singleBool;
    }

    @Override
    public Boolean isInt(String arg0) {
        return singleBool;
    }

    @Override
    public Boolean isReal(String arg0) {
        return singleBool;
    }

    @Override
    public Boolean toBool(String arg0) {
        return singleBool;
    }

    @Override
    public Integer toInt(String arg0) {
        return singleInt;
    }

    @Override
    public Double toReal(String arg0) {
        return singleReal;
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
    public String ite(Boolean arg0, String arg1, String arg2) {
        return singleString;
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
    public String substring(String arg0, Integer arg1, Integer arg2) {
        return singleString;
    }

    @Override
    public String suffix(String arg0, Integer arg1) {
        return singleString;
    }

    @Override
    public Integer lookup(String arg0, String arg1) {
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
    public boolean isValidValue(Object value) {
        return value == singleString;
    }

    @Override
    public Expression toValidTerm(Object value) {
        return Constant.instance(singleString);
    }

    @Override
    public Object toJavaValue(Object value) {
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
    /** Point value of the real algebra. */
    public static final Double singleReal = PointRealAlgebra.singleReal;
    /** Point value of the integer algebra. */
    public static final Integer singleInt = PointIntAlgebra.singleInt;
    /** Point value of the string algebra. */
    public static final String singleString = "";
    /** Singleton instance of this algebra. */
    public static final PointStringAlgebra instance = new PointStringAlgebra();
}
