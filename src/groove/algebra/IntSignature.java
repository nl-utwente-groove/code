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
 * $Id: IntSignature.java,v 1.3 2007-08-26 07:24:19 rensink Exp $
 */
package groove.algebra;

import static groove.algebra.Precedence.ADD;
import static groove.algebra.Precedence.COMPARE;
import static groove.algebra.Precedence.EQUAL;
import static groove.algebra.Precedence.MULT;
import groove.annotation.InfixSymbol;
import groove.annotation.PrefixSymbol;

import java.math.BigInteger;

/**
 * Interface for integer algebras.
 * @author Arend Rensink
 * @version $Revision$
 */
@SuppressWarnings("hiding")
public abstract class IntSignature<Int,Bool,String> implements Signature {
    /** Addition of two integers. */
    @InfixSymbol(symbol = "+", precedence = ADD)
    public abstract Int add(Int arg0, Int arg1);

    /** Subtraction of two integers. */
    @InfixSymbol(symbol = "-", precedence = ADD)
    public abstract Int sub(Int arg0, Int arg1);

    /** Multiplication of two integers. */
    @InfixSymbol(symbol = "*", precedence = MULT)
    public abstract Int mul(Int arg0, Int arg1);

    /** Division of two integers. */
    @InfixSymbol(symbol = "/", precedence = MULT)
    public abstract Int div(Int arg0, Int arg1);

    /** Modulo of two integers. */
    @InfixSymbol(symbol = "%", precedence = MULT)
    public abstract Int mod(Int arg0, Int arg1);

    /** Minimum of two integers. */
    public abstract Int min(Int arg0, Int arg1);

    /** Maximum of two integers. */
    public abstract Int max(Int arg0, Int arg1);

    /** Lesser-than comparison. */
    @InfixSymbol(symbol = "<", precedence = COMPARE)
    public abstract Bool lt(Int arg0, Int arg1);

    /** Lesser-or-equal comparison. */
    @InfixSymbol(symbol = "<=", precedence = COMPARE)
    public abstract Bool le(Int arg0, Int arg1);

    /** Greater-than comparison. */
    @InfixSymbol(symbol = ">", precedence = COMPARE)
    public abstract Bool gt(Int arg0, Int arg1);

    /** Greater-or-equal comparison. */
    @InfixSymbol(symbol = ">=", precedence = COMPARE)
    public abstract Bool ge(Int arg0, Int arg1);

    /** Equality test. */
    @InfixSymbol(symbol = "==", precedence = EQUAL)
    public abstract Bool eq(Int arg0, Int arg1);

    /** Inversion. */
    @PrefixSymbol(symbol = "-")
    public abstract Int neg(Int arg);

    /** String representation. */
    public abstract String toString(Int arg);

    /**
     * Tests if the number can be parsed as a {@link BigInteger}. This means
     * that a number of any length is accepted.
     */
    final public boolean isValue(java.lang.String value) {
        try {
            new BigInteger(value);
            return true;
        } catch (NumberFormatException exc) {
            return false;
        }
    }

    /**
     * Conversion of native Java representation of integer constants to
     * the corresponding algebra values.
     * @throws IllegalArgumentException if the parameter is not of type {@link Integer}
     */
    final public Int getValueFromJava(Object constant) {
        if (!(constant instanceof Integer)) {
            throw new IllegalArgumentException(java.lang.String.format(
                "Native int type is %s, not %s", Integer.class.getSimpleName(),
                constant.getClass().getSimpleName()));
        }
        return toValue((Integer) constant);
    }

    /** 
     * Callback method to convert from the native ({@link Integer})
     * representation to the algebra representation.
     */
    protected abstract Int toValue(Integer constant);
}
