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
 * Integer algebra based on the java type {@link Integer}.
 * @author Arend Rensink
 * @version $Revision$
 */
public class BigIntAlgebra extends IntAlgebra<BigInteger,BigDecimal,Boolean,String> {
    /** Private constructor for the singleton instance. */
    private BigIntAlgebra() {
        // empty
    }

    @Override
    public BigInteger abs(BigInteger arg) {
        return arg.abs();
    }

    @Override
    public BigInteger add(BigInteger arg0, BigInteger arg1) {
        return arg0.add(arg1);
    }

    @Override
    public BigInteger bigmax(List<BigInteger> arg) {
        return arg.stream()
            .max(BigInteger::compareTo)
            .get();
    }

    @Override
    public BigInteger bigmin(List<BigInteger> arg) {
        return arg.stream()
            .min(BigInteger::compareTo)
            .get();
    }

    @Override
    public BigInteger div(BigInteger arg0, BigInteger arg1) {
        return arg0.divide(arg1);
    }

    @Override
    public Boolean eq(BigInteger arg0, BigInteger arg1) {
        return arg0.equals(arg1);
    }

    @Override
    public Boolean neq(BigInteger arg0, BigInteger arg1) {
        return !arg0.equals(arg1);
    }

    @Override
    public Boolean ge(BigInteger arg0, BigInteger arg1) {
        return arg0.subtract(arg1)
            .signum() >= 0;
    }

    @Override
    public Boolean gt(BigInteger arg0, BigInteger arg1) {
        return arg0.subtract(arg1)
            .signum() > 0;
    }

    @Override
    public Boolean le(BigInteger arg0, BigInteger arg1) {
        return arg0.subtract(arg1)
            .signum() <= 0;
    }

    @Override
    public Boolean lt(BigInteger arg0, BigInteger arg1) {
        return arg0.subtract(arg1)
            .signum() < 0;
    }

    @Override
    public BigInteger max(BigInteger arg0, BigInteger arg1) {
        return arg0.max(arg1);
    }

    @Override
    public BigInteger min(BigInteger arg0, BigInteger arg1) {
        return arg0.min(arg1);
    }

    @Override
    public BigInteger mod(BigInteger arg0, BigInteger arg1) {
        return arg0.remainder(arg1);
    }

    @Override
    public BigInteger mul(BigInteger arg0, BigInteger arg1) {
        return arg0.multiply(arg1);
    }

    @Override
    public BigInteger neg(BigInteger arg) {
        return arg.negate();
    }

    @Override
    public BigInteger prod(List<BigInteger> arg) {
        return arg.stream()
            .reduce(BigInteger.ONE, (i, j) -> i.multiply(j));
    }

    @Override
    public BigInteger sub(BigInteger arg0, BigInteger arg1) {
        return arg0.subtract(arg1);
    }

    @Override
    public BigInteger sum(List<BigInteger> arg) {
        return arg.stream()
            .reduce(BigInteger.ZERO, (i, j) -> i.add(j));
    }

    @Override
    public String toString(BigInteger arg) {
        return arg.toString();
    }

    @Override
    public BigDecimal toReal(BigInteger arg) {
        return new BigDecimal(arg);
    }

    @Override
    public boolean isValue(Object value) {
        return value instanceof BigInteger;
    }

    @Override
    public Expression toTerm(Object value) {
        return Constant.instance((BigInteger) value);
    }

    @Override
    public BigInteger toValueFromConstant(Constant constant) {
        return constant.getIntRepr();
    }

    @Override
    public Integer toJavaValue(Object value) {
        return ((BigInteger) value).intValue();
    }

    @Override
    protected BigInteger toValue(Integer constant) {
        return BigInteger.valueOf(constant);
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
        return AlgebraFamily.BIG;
    }

    /** Name of the algebra. */
    public static final String NAME = "bint";
    /** Singleton instance of this algebra. */
    public static final BigIntAlgebra instance = new BigIntAlgebra();

}
