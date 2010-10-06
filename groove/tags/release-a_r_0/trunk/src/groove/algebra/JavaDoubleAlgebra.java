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

/**
 * Double algebra based on the java type {@link Double}.
 * @author Arend Rensink
 * @version $Revision: 1577 $
 */
public class JavaDoubleAlgebra extends RealSignature<Double,Boolean,String>
        implements Algebra<Double> {
    /** Private constructor for the singleton instance. */
    private JavaDoubleAlgebra() {
        // empty
    }

    @Override
    public Double add(Double arg0, Double arg1) {
        return arg0 + arg1;
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
    public Double sub(Double arg0, Double arg1) {
        return arg0 - arg1;
    }

    @Override
    public String toString(Double arg) {
        return arg.toString();
    }

    /**
     * Delegates to {@link BigDecimal#doubleValue()}.
     */
    public Double getValue(String symbol) {
        return new BigDecimal(symbol).doubleValue();
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
    public static boolean approximatelyEquals(double d1, double d2) {
        return Math.abs(d1 - d2) < (Math.abs(d1) + Math.abs(d2)) * TOLERANCE;
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
    public static final JavaDoubleAlgebra instance = new JavaDoubleAlgebra();
}
