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
 * Implementation of reals consisting of a singleton value.
 * To be used in conjunction with {@link PointBoolAlgebra} and {@link PointStringAlgebra}.
 * @author Arend Rensink
 * @version $Revision $
 */
public class PointRealAlgebra extends RealAlgebra<Integer,Double,Boolean,String>
    implements PointAlgebra<Double> {
    /** Private constructor for the singleton instance. */
    private PointRealAlgebra() {
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
        return value == singleReal;
    }

    @Override
    public String getSymbol(Object value) {
        return value.toString();
    }

    @Override
    public Double getPointValue() {
        return singleReal;
    }

    @Override
    public Expression toTerm(Object value) {
        return singlRealConstant;
    }

    @Override
    public Double toJavaValue(Object value) {
        return singleReal;
    }

    @Override
    public Double toValueFromConstant(Constant constant) {
        return singleReal;
    }

    @Override
    protected Double toValueFromJavaDouble(Double value) {
        return singleReal;
    }

    @Override
    public Double abs(Double arg) {
        return singleReal;
    }

    @Override
    public Double add(Double arg0, Double arg1) {
        return singleReal;
    }

    @Override
    public Double bigmax(List<Double> arg) {
        return singleReal;
    }

    @Override
    public Double bigmin(List<Double> arg) {
        return singleReal;
    }

    @Override
    public Double div(Double arg0, Double arg1) {
        return singleReal;
    }

    @Override
    public Boolean eq(Double arg0, Double arg1) {
        return singleBool;
    }

    @Override
    public Boolean neq(Double arg0, Double arg1) {
        return singleBool;
    }

    @Override
    public Boolean ge(Double arg0, Double arg1) {
        return singleBool;
    }

    @Override
    public Boolean gt(Double arg0, Double arg1) {
        return singleBool;
    }

    @Override
    public Boolean le(Double arg0, Double arg1) {
        return singleBool;
    }

    @Override
    public Double ite(Boolean arg0, Double arg1, Double arg2) {
        return singleReal;
    }

    @Override
    public Boolean lt(Double arg0, Double arg1) {
        return singleBool;
    }

    @Override
    public Double max(Double arg0, Double arg1) {
        return singleReal;
    }

    @Override
    public Double min(Double arg0, Double arg1) {
        return singleReal;
    }

    @Override
    public Double mul(Double arg0, Double arg1) {
        return singleReal;
    }

    @Override
    public Double neg(Double arg) {
        return singleReal;
    }

    @Override
    public Double prod(List<Double> arg) {
        return singleReal;
    }

    @Override
    public Double sub(Double arg0, Double arg1) {
        return singleReal;
    }

    @Override
    public Double sum(List<Double> arg) {
        return singleReal;
    }

    @Override
    public Integer toInt(Double arg) {
        return singleInt;
    }

    @Override
    public String toString(Double arg) {
        return singleString;
    }

    /** Name of this algebra. */
    public static final String NAME = "preal";
    /**
     * Representation of the point value of the string algebra;
     * redefined literally to avoid class loading dependencies.
     * @see PointStringAlgebra#singleString
     */
    public static final String singleString = PointStringAlgebra.singleString;
    /**
     * Representation of the point value of the boolean algebra;
     * redefined literally to avoid class loading dependencies.
     * @see PointBoolAlgebra#singleBool
     */
    public static final Boolean singleBool = PointBoolAlgebra.singleBool;
    /** Point value of the int algebra. */
    public static final Integer singleInt = 0;
    /** Point value of the real algebra. */
    public static final Double singleReal = 0.0;
    /** Point value of the real algebra, represented as a {@link Double}. */
    public static final Constant singlRealConstant = Constant.instance(singleReal);
    /** Singleton instance of this algebra. */
    public static final PointRealAlgebra instance = new PointRealAlgebra();
}
