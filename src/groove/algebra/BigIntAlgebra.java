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

import java.math.BigInteger;


/**
 * Integer algebra based on the java type {@link Integer}.
 * @author Arend Rensink
 * @version $Revision: 1580 $
 */
public class BigIntAlgebra extends IntSignature<BigInteger,Boolean,String> implements Algebra<BigInteger> {
    @Override
    public BigInteger add(BigInteger arg0, BigInteger arg1) {
        return arg0.add(arg1);
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
    public Boolean ge(BigInteger arg0, BigInteger arg1) {
        return arg0.subtract(arg1).signum() >= 0;
    }

    @Override
    public Boolean gt(BigInteger arg0, BigInteger arg1) {
        return arg0.subtract(arg1).signum() > 0;
    }

    @Override
    public Boolean le(BigInteger arg0, BigInteger arg1) {
        return arg0.subtract(arg1).signum() <= 0;
    }

    @Override
    public Boolean lt(BigInteger arg0, BigInteger arg1) {
        return arg0.subtract(arg1).signum() < 0;
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
    public BigInteger sub(BigInteger arg0, BigInteger arg1) {
        return arg0.subtract(arg1);
    }

    @Override
    public String toString(BigInteger arg) {
        return arg.toString();
    }

    /**
     * Delegates to {@link BigInteger#intValue()}.
     */
    public BigInteger getValue(String symbol) {
        return new BigInteger(symbol);
    }
    
    /**
     * Delegates to {@link Integer#toString()}.
     */
    public String getSymbol(Object value) {
        return value.toString();
    }

    /** Returns {@link #NAME}. */
    public String getName() {
        return NAME;
    }
    
    /** Name of the algebra. */
    public static final String NAME = "jint";
}
