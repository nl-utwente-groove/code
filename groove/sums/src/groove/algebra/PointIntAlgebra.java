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

import java.util.List;

import groove.algebra.syntax.Expression;

/**
 * Implementation of integers consisting of a singleton value.
 * To be used in conjunction with {@link PointBoolAlgebra} and {@link PointStringAlgebra}.
 * @author Arend Rensink
 * @version $Revision $
 */
public class PointIntAlgebra extends IntAlgebra<Integer,Double,Boolean,String>
    implements PointAlgebra<Integer> {
    /** Private constructor for the singleton instance. */
    private PointIntAlgebra() {
        // empty
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
        return value == singleInt;
    }

    @Override
    public String getSymbol(Object value) {
        return value.toString();
    }

    @Override
    public Integer getPointValue() {
        return singleInt;
    }

    @Override
    public Expression toTerm(Object value) {
        return singleIntConstant;
    }

    @Override
    public Integer toJavaValue(Object value) {
        return singleInt;
    }

    @Override
    public Integer toValueFromConstant(Constant constant) {
        return singleInt;
    }

    @Override
    protected Integer toValue(Integer constant) {
        return singleInt;
    }

    @Override
    public Integer abs(Integer arg) {
        return singleInt;
    }

    @Override
    public Integer add(Integer arg0, Integer arg1) {
        return singleInt;
    }

    @Override
    public Integer bigmax(List<Integer> arg) {
        return singleInt;
    }

    @Override
    public Integer bigmin(List<Integer> arg) {
        return singleInt;
    }

    @Override
    public Integer div(Integer arg0, Integer arg1) {
        return singleInt;
    }

    @Override
    public Boolean eq(Integer arg0, Integer arg1) {
        return singleBool;
    }

    @Override
    public Boolean neq(Integer arg0, Integer arg1) {
        return singleBool;
    }

    @Override
    public Boolean ge(Integer arg0, Integer arg1) {
        return singleBool;
    }

    @Override
    public Boolean gt(Integer arg0, Integer arg1) {
        return singleBool;
    }

    @Override
    public Boolean le(Integer arg0, Integer arg1) {
        return singleBool;
    }

    @Override
    public Boolean lt(Integer arg0, Integer arg1) {
        return singleBool;
    }

    @Override
    public Integer max(Integer arg0, Integer arg1) {
        return singleInt;
    }

    @Override
    public Integer min(Integer arg0, Integer arg1) {
        return singleInt;
    }

    @Override
    public Integer mod(Integer arg0, Integer arg1) {
        return singleInt;
    }

    @Override
    public Integer mul(Integer arg0, Integer arg1) {
        return singleInt;
    }

    @Override
    public Integer neg(Integer arg) {
        return singleInt;
    }

    @Override
    public Integer prod(List<Integer> arg) {
        return singleInt;
    }

    @Override
    public Integer sub(Integer arg0, Integer arg1) {
        return singleInt;
    }

    @Override
    public Integer sum(List<Integer> arg) {
        return singleInt;
    }

    @Override
    public String toString(Integer arg) {
        return singleString;
    }

    @Override
    public Double toReal(Integer arg) {
        return singleReal;
    }

    /** Name of this algebra. */
    public static final String NAME = "pint";
    /**
     * Representation of the point value of the string algebra.
     * Redefined literally to avoid circular class loading dependencies.
     * @see PointStringAlgebra#singleString
     */
    public static final String singleString = "";
    /**
     * Representation of the point value of the boolean algebra.
     * Redefined literally to avoid circular class loading dependencies.
     * @see PointBoolAlgebra#singleBool
     */
    public static final Boolean singleBool = Boolean.FALSE;
    /** Point value of the int algebra. */
    public static final Integer singleInt = 0;
    /** Constant representing the point value of the int algebra. */
    public static final Constant singleIntConstant = Constant.instance(singleInt);
    /** Point value of the real algebra. */
    public static final Double singleReal = 0.0;
    /** Singleton instance of this algebra. */
    public static final PointIntAlgebra instance = new PointIntAlgebra();
}
