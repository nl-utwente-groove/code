/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
package groove.test.algebra;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import groove.algebra.AlgebraFamily;
import groove.algebra.RealSignature;

/** Abstract class to test the functionality of a non-final algebra family. */
public abstract class NonFinalAlgebraTest<B,I,R,S> extends AlgebraTest<B,I,R,S> {
    NonFinalAlgebraTest(AlgebraFamily family) {
        super(family);
    }

    /** Tests the integer algebra. */
    @Test
    public void testInt() {
        I zero = createInt(0);
        I one = createInt(1);
        I two = createInt(2);
        I three = createInt(3);
        I four = createInt(4);
        I minusOne = createInt(-1);
        // arithmetic
        assertEquals(zero, iAdd(one, minusOne));
        assertEquals(two, iSub(one, minusOne));
        assertEquals(four, iMul(two, two));
        assertEquals(one, iDiv(four, three));
        assertEquals(one, iMod(four, three));
        assertEquals(one, iAbs(minusOne));
        assertEquals(minusOne, iNeg(one));
        assertEquals(createInt(5), iSum(one, zero, two, two));
        // min/max
        assertEquals(one, iMin(one, two));
        assertEquals(two, iMax(one, two));
        // lesser/greater than
        assertEquals(bFalse(), iLt(one, one));
        assertEquals(bTrue(), iLt(one, two));
        assertEquals(bTrue(), iLe(one, one));
        assertEquals(bTrue(), iLe(one, two));
        assertEquals(bFalse(), iGt(one, one));
        assertEquals(bFalse(), iGt(one, two));
        assertEquals(bTrue(), iGe(one, one));
        assertEquals(bFalse(), iGe(one, two));
        // (in)equality
        assertEquals(bTrue(), iEq(one, one));
        assertEquals(bFalse(), iEq(one, two));
        assertEquals(bFalse(), iNeq(one, one));
        assertEquals(bTrue(), iNeq(one, two));
        // if-then-else
        assertEquals(one, iIte(bTrue(), one, two));
        assertEquals(two, iIte(bFalse(), one, two));

    }

    /** Tests the real-number algebra. */
    @Test
    public void testReal() {
        R zero = createReal(0);
        assertEquals(bTrue(), rEq(zero, this.realAlgebra.toValue(RealSignature.ZERO)));
        R one = createReal(1.1);
        R two = createReal(2.2);
        R four = createReal(4.84);
        R minusOne = createReal(-1.1);
        R smallDiff = createReal(0.000001);
        // arithmetic
        assertEquals(zero, rAdd(one, minusOne));
        assertEquals(two, rSub(one, minusOne));
        assertEquals(bTrue(), rLt(rSub(rMul(two, two), four), smallDiff));
        assertEquals(bTrue(), rLt(rSub(rDiv(four, two), two), smallDiff));
        assertEquals(one, rAbs(minusOne));
        assertEquals(minusOne, rNeg(one));
        // min/max
        assertEquals(one, rMin(one, two));
        assertEquals(two, rMax(one, two));
        // lesser/greater than
        assertEquals(bFalse(), rLt(one, one));
        assertEquals(bTrue(), rLt(one, two));
        assertEquals(bTrue(), rLe(one, one));
        assertEquals(bTrue(), rLe(one, two));
        assertEquals(bFalse(), rGt(one, one));
        assertEquals(bFalse(), rGt(one, two));
        assertEquals(bTrue(), rGe(one, one));
        assertEquals(bFalse(), rGe(one, two));
        // (in)equality
        assertEquals(bTrue(), rEq(one, one));
        assertEquals(bFalse(), rEq(one, two));
        assertEquals(bFalse(), rNeq(one, one));
        assertEquals(bTrue(), rNeq(one, two));
        // if-then-else
        assertEquals(one, rIte(bTrue(), one, two));
        assertEquals(two, rIte(bFalse(), one, two));
    }

    /** Tests the boolean algebra. */
    @Test
    public void testBoolean() {
        B t = bTrue();
        B f = bFalse();
        assertEquals(t, bAnd(t, t));
        assertEquals(f, bAnd(t, f));
        assertEquals(t, bOr(f, t));
        assertEquals(f, bOr(f, f));
        assertEquals(f, bNot(t));
        // (in)equality
        assertEquals(t, bEq(t, t));
        assertEquals(f, bEq(t, f));
        assertEquals(f, bNeq(t, t));
        assertEquals(t, bNeq(t, f));
    }

    /** Tests the string algebra. */
    @Test
    public void testString() {
        S empty = createString("");
        S a = createString("a");
        S b = createString("b");
        S ab = createString("ab");
        I zero = createInt(0);
        I one = createInt(1);
        I two = createInt(2);
        // concatenation and length
        assertEquals(a, sConcat(empty, a));
        assertEquals(ab, sConcat(a, b));
        assertEquals(createInt(0), sLength(empty));
        assertEquals(createInt(1), sLength(a));
        // lesser/greater than
        assertEquals(bFalse(), sLt(empty, empty));
        assertEquals(bTrue(), sLt(empty, a));
        assertEquals(bTrue(), sLt(a, b));
        assertEquals(bTrue(), sLe(empty, empty));
        assertEquals(bTrue(), sLe(empty, a));
        assertEquals(bTrue(), sLe(a, b));
        assertEquals(bFalse(), sGt(empty, empty));
        assertEquals(bFalse(), sGt(empty, a));
        assertEquals(bFalse(), sGt(a, b));
        assertEquals(bTrue(), sGe(empty, empty));
        assertEquals(bFalse(), sGe(empty, a));
        assertEquals(bFalse(), sGe(a, b));
        // (in)equality
        assertEquals(bTrue(), sEq(empty, empty));
        assertEquals(bFalse(), sEq(empty, a));
        assertEquals(bFalse(), sNeq(empty, empty));
        assertEquals(bTrue(), sNeq(empty, a));
        // string-to-bool
        assertEquals(bFalse(), sIsBool(a));
        assertEquals(bTrue(), sIsBool(createString("false")));
        assertEquals(bFalse(), sToBool(a));
        assertEquals(bTrue(), sToBool(createString("true")));
        // string-to-int
        assertEquals(bFalse(), sIsInt(a));
        assertEquals(bTrue(), sIsInt(createString("0")));
        assertEquals(zero, sToInt(a));
        assertEquals(one, sToInt(createString("1")));
        // string-to-real
        assertEquals(bFalse(), sIsReal(a));
        assertEquals(bTrue(), sIsReal(createString("1.1")));
        assertEquals(createReal(0), sToReal(a));
        assertEquals(createReal(1.01), sToReal(createString("1.01")));
        // substring
        assertEquals(a, sSubstring(ab, zero, one));
        assertEquals(b, sSubstring(ab, one, two));
        assertEquals(empty, sSubstring(ab, one, zero));
        assertEquals(b, sSuffix(ab, one));
        assertEquals(empty, sSuffix(a, two));
        assertEquals(createInt(-1), sLookup(a, b));
        assertEquals(zero, sLookup(ab, a));
        assertEquals(one, sLookup(ab, b));
        // if-then-else
        assertEquals(a, sIte(bTrue(), a, b));
        assertEquals(b, sIte(bFalse(), a, b));
    }
}
