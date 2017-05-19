/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * $Id$
 */
package groove.algebra;

import java.util.List;

import groove.algebra.syntax.Expression;

/**
 * Double algebra based on the java type {@link Double}.
 * @author Arend Rensink
 * @version $Revision$
 */
public class JavaRealAlgebra extends RealAlgebra<Integer,Double,Boolean,String> {
    /** Private constructor for the singleton instance. */
    private JavaRealAlgebra() {
        // empty
    }

    @Override
    public Double abs(Double arg) {
        return Math.abs(arg);
    }

    @Override
    public Double add(Double arg0, Double arg1) {
        return arg0 + arg1;
    }

    @Override
    public Double bigmax(List<Double> arg) {
        return arg.stream()
            .max(Double::compareTo)
            .get();
    }

    @Override
    public Double bigmin(List<Double> arg) {
        return arg.stream()
            .min(Double::compareTo)
            .get();
    }

    @Override
    public Double div(Double arg0, Double arg1) {
        return arg0 / arg1;
    }

    @Override
    public Boolean eq(Double arg0, Double arg1) {
        return approximatelyEquals(arg0, arg1);
    }

    @Override
    public Boolean neq(Double arg0, Double arg1) {
        return !approximatelyEquals(arg0, arg1);
    }

    @Override
    public Boolean ge(Double arg0, Double arg1) {
        return arg0 >= arg1 || approximatelyEquals(arg0, arg1);
    }

    @Override
    public Boolean gt(Double arg0, Double arg1) {
        return arg0 > arg1 && !approximatelyEquals(arg0, arg1);
    }

    @Override
    public Boolean le(Double arg0, Double arg1) {
        return arg0 <= arg1 || approximatelyEquals(arg0, arg1);
    }

    @Override
    public Boolean lt(Double arg0, Double arg1) {
        return arg0 < arg1 && !approximatelyEquals(arg0, arg1);
    }

    @Override
    public Double max(Double arg0, Double arg1) {
        return Math.max(arg0, arg1);
    }

    @Override
    public Double min(Double arg0, Double arg1) {
        return Math.min(arg0, arg1);
    }

    @Override
    public Double mul(Double arg0, Double arg1) {
        return arg0 * arg1;
    }

    @Override
    public Double neg(Double arg) {
        return -arg;
    }

    @Override
    public Double prod(List<Double> arg) {
        return arg.stream()
            .reduce(1., (i, j) -> i * j);
    }

    @Override
    public Double sub(Double arg0, Double arg1) {
        return arg0 - arg1;
    }

    @Override
    public Double sum(List<Double> arg) {
        return arg.stream()
            .reduce(0., (i, j) -> i + j);
    }

    @Override
    public Integer toInt(Double arg) {
        return arg.intValue();
    }

    @Override
    public String toString(Double arg) {
        return arg.toString();
    }

    @Override
    public boolean isValue(Object value) {
        return value instanceof Double;
    }

    @Override
    public Expression toTerm(Object value) {
        return Constant.instance((Double) value);
    }

    @Override
    public Double toValueFromConstant(Constant constant) {
        return constant.getRealRepr()
            .doubleValue();
    }

    /* The value is already of the right type. */
    @Override
    public Double toJavaValue(Object value) {
        return (Double) value;
    }

    @Override
    protected Double toValueFromJavaDouble(Double value) {
        return value;
    }

    @Override
    public String getSymbol(Object value) {
        return value.toString();
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public AlgebraFamily getFamily() {
        return AlgebraFamily.DEFAULT;
    }

    /** Tests if two numbers are equal up to {@link #TOLERANCE}. */
    public static boolean approximatelyEquals(double d1, double d2) {
        return Math.abs(d1 - d2) <= (Math.abs(d1) + Math.abs(d2) + TOLERANCE) * TOLERANCE;
    }

    /**
     * Used to compare real numbers: Two doubles are equal if the absolute value
     * of their difference is smaller than this number. See
     * {@link #approximatelyEquals(double, double)}.
     */
    public static final double TOLERANCE = 0.0000001;

    /** Name of the algebra. */
    public static final String NAME = "jdouble";
    /** Singleton instance of this algebra. */
    public static final JavaRealAlgebra instance = new JavaRealAlgebra();
}
