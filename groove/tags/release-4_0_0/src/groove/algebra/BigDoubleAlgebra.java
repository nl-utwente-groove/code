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
 * $Id: JavaIntAlgebra.java,v 1.3 2007-08-26 07:24:19 rensink Exp $
 */
package groove.algebra;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * Double algebra based on reals of arbitrary precision.
 * Implemented by the Java type {@link BigDecimal}.
 * @author Arend Rensink
 * @version $Revision: 1577 $
 */
public class BigDoubleAlgebra extends RealSignature<BigDecimal,Boolean,String> implements Algebra<BigDecimal> {
    @Override
    public BigDecimal add(BigDecimal arg0, BigDecimal arg1) {
        return arg0.add(arg1);
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
    public Boolean ge(BigDecimal arg0, BigDecimal arg1) {
        return arg0.subtract(arg1).signum() >= 0 || approximatelyEquals(arg0, arg1);
    }

    @Override
    public Boolean gt(BigDecimal arg0, BigDecimal arg1) {
        return arg0.subtract(arg1).signum() > 0 && !approximatelyEquals(arg0,arg1);
    }

    @Override
    public Boolean le(BigDecimal arg0, BigDecimal arg1) {
        return arg0.subtract(arg1).signum() <= 0 || approximatelyEquals(arg0, arg1);
    }

    @Override
    public Boolean lt(BigDecimal arg0, BigDecimal arg1) {
        return arg0.subtract(arg1).signum() < 0 && !approximatelyEquals(arg0,arg1);
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
    public BigDecimal sub(BigDecimal arg0, BigDecimal arg1) {
        return arg0.subtract(arg1);
    }

    @Override
    public String toString(BigDecimal arg) {
        return arg.toString();
    }

    /**
     * Creates a new {@link BigDecimal}.
     */
    public BigDecimal getValue(String symbol) {
        return new BigDecimal(symbol,MathContext.DECIMAL128);
    }
    
    /**
     * Delegates to {@link Double#toString()}.
     */
    public String getSymbol(Object value) {
        return value.toString();
    }

    /** Returns {@link #NAME}. */
    public String getName() {
        return NAME;
    }

    /** Tests if two numbers are equal up to {@link #TOLERANCE}. */
    public static boolean approximatelyEquals(BigDecimal d1, BigDecimal d2) {
        return d1.subtract(d2).abs().doubleValue() < (d1.abs().doubleValue() + d2.abs().doubleValue()) * TOLERANCE;
    }

    /**
     * Used to compare real numbers: Two doubles are equal if the absolute value
     * of their difference is smaller than this number. See
     * {@link #approximatelyEquals(BigDecimal, BigDecimal)}.
     */
    public static final double TOLERANCE = 1e-30;
    
    /** Name of the algebra. */
    public static final String NAME = "jdouble";
}
