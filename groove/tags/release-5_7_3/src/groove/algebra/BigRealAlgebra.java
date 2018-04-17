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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import groove.algebra.syntax.Expression;

/**
 * Double algebra based on reals of arbitrary precision.
 * Implemented by the Java type {@link BigDecimal}.
 * @author Arend Rensink
 * @version $Revision$
 */
public class BigRealAlgebra extends RealAlgebra<BigInteger,BigDecimal,Boolean,String> {
    /** Private constructor for the singleton instance. */
    private BigRealAlgebra() {
        // empty
    }

    @Override
    public BigDecimal abs(BigDecimal arg) {
        return arg.abs();
    }

    @Override
    public BigDecimal add(BigDecimal arg0, BigDecimal arg1) {
        return arg0.add(arg1);
    }

    @Override
    public BigDecimal bigmax(List<BigDecimal> arg) {
        return arg.stream()
            .max(BigDecimal::compareTo)
            .get();
    }

    @Override
    public BigDecimal bigmin(List<BigDecimal> arg) {
        return arg.stream()
            .min(BigDecimal::compareTo)
            .get();
    }

    @Override
    public BigDecimal div(BigDecimal arg0, BigDecimal arg1) {
        return arg0.divide(arg1);
    }

    @Override
    public Boolean eq(BigDecimal arg0, BigDecimal arg1) {
        return approximatelyEquals(arg0, arg1);
    }

    @Override
    public Boolean neq(BigDecimal arg0, BigDecimal arg1) {
        return !approximatelyEquals(arg0, arg1);
    }

    @Override
    public Boolean ge(BigDecimal arg0, BigDecimal arg1) {
        return arg0.subtract(arg1)
            .signum() >= 0 || approximatelyEquals(arg0, arg1);
    }

    @Override
    public Boolean gt(BigDecimal arg0, BigDecimal arg1) {
        return arg0.subtract(arg1)
            .signum() > 0 && !approximatelyEquals(arg0, arg1);
    }

    @Override
    public Boolean le(BigDecimal arg0, BigDecimal arg1) {
        return arg0.subtract(arg1)
            .signum() <= 0 || approximatelyEquals(arg0, arg1);
    }

    @Override
    public Boolean lt(BigDecimal arg0, BigDecimal arg1) {
        return arg0.subtract(arg1)
            .signum() < 0 && !approximatelyEquals(arg0, arg1);
    }

    @Override
    public BigDecimal max(BigDecimal arg0, BigDecimal arg1) {
        return arg0.max(arg1);
    }

    @Override
    public BigDecimal min(BigDecimal arg0, BigDecimal arg1) {
        return arg0.min(arg1);
    }

    @Override
    public BigDecimal mul(BigDecimal arg0, BigDecimal arg1) {
        return arg0.multiply(arg1);
    }

    @Override
    public BigDecimal neg(BigDecimal arg) {
        return arg.negate();
    }

    @Override
    public BigDecimal prod(List<BigDecimal> arg) {
        return arg.stream()
            .reduce(BigDecimal.ONE, (i, j) -> i.multiply(j));
    }

    @Override
    public BigDecimal sub(BigDecimal arg0, BigDecimal arg1) {
        return arg0.subtract(arg1);
    }

    @Override
    public BigDecimal sum(List<BigDecimal> arg) {
        return arg.stream()
            .reduce(BigDecimal.ZERO, (i, j) -> i.add(j));
    }

    @Override
    public BigInteger toInt(BigDecimal arg) {
        return arg.toBigInteger();
    }

    @Override
    public String toString(BigDecimal arg) {
        return arg.toString();
    }

    @Override
    public boolean isValue(Object value) {
        return value instanceof BigDecimal;
    }

    @Override
    public Expression toTerm(Object value) {
        return Constant.instance((BigDecimal) value);
    }

    @Override
    public BigDecimal toValueFromConstant(Constant constant) {
        return constant.getRealRepr();
    }

    @Override
    public Double toJavaValue(Object value) {
        return ((BigDecimal) value).doubleValue();
    }

    @Override
    protected BigDecimal toValueFromJavaDouble(Double value) {
        return BigDecimal.valueOf(value);
    }

    /**
     * Delegates to {@link Double#toString()}.
     */
    @Override
    public String getSymbol(Object value) {
        return value.toString();
    }

    /** Returns {@link #NAME}. */
    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public AlgebraFamily getFamily() {
        return AlgebraFamily.BIG;
    }

    /** Tests if two numbers are equal up to {@link #TOLERANCE}. */
    public static boolean approximatelyEquals(BigDecimal d1, BigDecimal d2) {
        return d1.subtract(d2)
            .abs()
            .doubleValue() < (d1.abs()
                .doubleValue()
                + d2.abs()
                    .doubleValue()
                + TOLERANCE) * TOLERANCE;
    }

    /**
     * Used to compare real numbers: Two doubles are equal if the absolute value
     * of their difference is smaller than this number. See
     * {@link #approximatelyEquals(BigDecimal, BigDecimal)}.
     */
    public static final double TOLERANCE = 1e-30;

    /** Name of the algebra. */
    public static final String NAME = "bdouble";
    /** Singleton instance of this algebra. */
    public static final BigRealAlgebra instance = new BigRealAlgebra();
}
