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

import java.util.Arrays;

import org.junit.Test;

import groove.algebra.Algebra;
import groove.algebra.AlgebraFamily;
import groove.algebra.BoolAlgebra;
import groove.algebra.IntAlgebra;
import groove.algebra.RealAlgebra;
import groove.algebra.Sort;
import groove.algebra.StringAlgebra;
import groove.algebra.syntax.Expression;

/** Abstract class to test the functionality of an algebra family. */
public abstract class AlgebraTest<B,I,R,S> {
    @SuppressWarnings("unchecked")
    AlgebraTest(AlgebraFamily family) {
        this.family = family;
        this.intAlgebra = (IntAlgebra<I,R,B,S>) family.getAlgebra(Sort.INT);
        this.boolAlgebra = (BoolAlgebra<B>) family.getAlgebra(Sort.BOOL);
        this.realAlgebra = (RealAlgebra<I,R,B,S>) family.getAlgebra(Sort.REAL);
        this.stringAlgebra = (StringAlgebra<S,B,I>) family.getAlgebra(Sort.STRING);
        this.TRUE = this.boolAlgebra.toValueFromJava(true);
        this.FALSE = this.boolAlgebra.toValueFromJava(false);
    }

    /** Tests consistency of the definitions. */
    @Test
    public void testConsistency() {
        assertEquals(this.family, this.boolAlgebra.getFamily());
        assertEquals(this.family, this.intAlgebra.getFamily());
        assertEquals(this.family, this.realAlgebra.getFamily());
        assertEquals(this.family, this.stringAlgebra.getFamily());
    }

    /** Tests creation of constants. */
    @Test
    public void testCreate() {
        createInt(0);
        createInt(25);
        createInt(-123);
        createBool(true);
        createBool(false);
        createReal(0.0);
        createReal(0.1e10);
        createReal(-1000.1e-1);
        createString("");
        createString("a");
        createString("a \" complex%");
    }

    /** Tests conversion to and from integers. */
    @Test
    public void testCast() {
        testCast(0);
        testCast(10);
        testCast(-5124);

        testCut(1.0, 1);
        testCut(3.1415, 3);
        testCut(-11.11, -11);
    }

    /** Casts an integer back and forth to real, and tests for equality. */
    protected void testCast(int value) {
        I converted = createInt(value);
        if (!(converted instanceof Expression)) {
            R real = iToReal(converted);
            I back = rToInt(real);
            assertEquals(converted, back);
        }
    }

    /** Coerces a real to an integer, and tests for equality. */
    protected void testCut(double realValue, int intValue) {
        R converted = createReal(realValue);
        if (!(converted instanceof Expression)) {
            I i = rToInt(converted);
            assertEquals(createInt(intValue), i);
        }
    }

    /** Factory method to create an integer representation from a Java integer value. */
    protected I createInt(int value) {
        I result = this.intAlgebra.toValueFromJava(value);
        testConversion(this.intAlgebra, result);
        return result;
    }

    /** Factory method to create a boolean representation from a Java boolean value. */
    protected B createBool(boolean value) {
        B result = this.boolAlgebra.toValueFromJava(value);
        testConversion(this.boolAlgebra, result);
        return result;
    }

    /** Factory method to create a real-number representation from a Java double value. */
    protected R createReal(double value) {
        R result = this.realAlgebra.toValueFromJava(value);
        testConversion(this.realAlgebra, result);
        return result;
    }

    /** Factory method to create a string representation from a Java String value. */
    protected S createString(String value) {
        S result = this.stringAlgebra.toValueFromJava(value);
        testConversion(this.stringAlgebra, result);
        return result;
    }

    /** Tests conversion from algebra value to term and back. */
    private void testConversion(Algebra<?> algebra, Object value) {
        assertEquals(value, algebra.toValue(algebra.toTerm(value)));
    }

    /** Returns the representation of the Boolean value {@code true} */
    protected B bTrue() {
        return this.TRUE;
    }

    /** Returns the representation of the Boolean value {@code false} */
    protected B bFalse() {
        return this.FALSE;
    }

    /** Delegates to {@link BoolAlgebra#not} */
    protected B bNot(B arg) {
        return this.boolAlgebra.not(arg);
    }

    /** Delegates to {@link BoolAlgebra#and} */
    protected B bAnd(B arg0, B arg1) {
        return this.boolAlgebra.and(arg0, arg1);
    }

    /** Delegates to {@link BoolAlgebra#or} */
    protected B bOr(B arg0, B arg1) {
        return this.boolAlgebra.or(arg0, arg1);
    }

    /** Delegates to {@link BoolAlgebra#eq} */
    protected B bEq(B arg0, B arg1) {
        return this.boolAlgebra.eq(arg0, arg1);
    }

    /** Delegates to {@link BoolAlgebra#neq} */
    protected B bNeq(B arg0, B arg1) {
        return this.boolAlgebra.neq(arg0, arg1);
    }

    /** Delegates to {@link IntAlgebra#abs} */
    protected I iAbs(I arg) {
        return this.intAlgebra.abs(arg);
    }

    /** Delegates to {@link IntAlgebra#add} */
    protected I iAdd(I arg0, I arg1) {
        return this.intAlgebra.add(arg0, arg1);
    }

    /** Delegates to {@link IntAlgebra#div} */
    protected I iDiv(I arg0, I arg1) {
        return this.intAlgebra.div(arg0, arg1);
    }

    /** Delegates to {@link IntAlgebra#ge} */
    protected B iGe(I arg0, I arg1) {
        return this.intAlgebra.ge(arg0, arg1);
    }

    /** Delegates to {@link IntAlgebra#gt} */
    protected B iGt(I arg0, I arg1) {
        return this.intAlgebra.gt(arg0, arg1);
    }

    /** Delegates to {@link IntAlgebra#le} */
    protected B iLe(I arg0, I arg1) {
        return this.intAlgebra.le(arg0, arg1);
    }

    /** Delegates to {@link IntAlgebra#lt} */
    protected B iLt(I arg0, I arg1) {
        return this.intAlgebra.lt(arg0, arg1);
    }

    /** Delegates to {@link IntAlgebra#max} */
    protected I iMax(I arg0, I arg1) {
        return this.intAlgebra.max(arg0, arg1);
    }

    /** Delegates to {@link IntAlgebra#min} */
    protected I iMin(I arg0, I arg1) {
        return this.intAlgebra.min(arg0, arg1);
    }

    /** Delegates to {@link IntAlgebra#mod} */
    protected I iMod(I arg0, I arg1) {
        return this.intAlgebra.mod(arg0, arg1);
    }

    /** Delegates to {@link IntAlgebra#mul} */
    protected I iMul(I arg0, I arg1) {
        return this.intAlgebra.mul(arg0, arg1);
    }

    /** Delegates to {@link IntAlgebra#neg} */
    protected I iNeg(I arg) {
        return this.intAlgebra.neg(arg);
    }

    /** Delegates to {@link IntAlgebra#sub} */
    protected I iSub(I arg0, I arg1) {
        return this.intAlgebra.sub(arg0, arg1);
    }

    /** Delegates to {@link IntAlgebra#sum} */
    @SafeVarargs
    final protected I iSum(I... arg) {
        return this.intAlgebra.sum(Arrays.asList(arg));
    }

    /** Delegates to {@link IntAlgebra#toReal} */
    protected R iToReal(I arg) {
        return this.intAlgebra.toReal(arg);
    }

    /** Delegates to {@link IntAlgebra#toString} */
    protected S iToString(I arg) {
        return this.intAlgebra.toString(arg);
    }

    /** Delegates to {@link IntAlgebra#eq} */
    protected B iEq(I arg0, I arg1) {
        return this.intAlgebra.eq(arg0, arg1);
    }

    /** Delegates to {@link IntAlgebra#neq} */
    protected B iNeq(I arg0, I arg1) {
        return this.intAlgebra.neq(arg0, arg1);
    }

    /** Delegates to {@link RealAlgebra#abs} */
    protected R rAbs(R arg) {
        return this.realAlgebra.abs(arg);
    }

    /** Delegates to {@link RealAlgebra#add} */
    protected R rAdd(R arg0, R arg1) {
        return this.realAlgebra.add(arg0, arg1);
    }

    /** Delegates to {@link RealAlgebra#sub} */
    protected R rSub(R arg0, R arg1) {
        return this.realAlgebra.sub(arg0, arg1);
    }

    /** Delegates to {@link RealAlgebra#mul} */
    protected R rMul(R arg0, R arg1) {
        return this.realAlgebra.mul(arg0, arg1);
    }

    /** Delegates to {@link RealAlgebra#div} */
    protected R rDiv(R arg0, R arg1) {
        return this.realAlgebra.div(arg0, arg1);
    }

    /** Delegates to {@link RealAlgebra#min} */
    protected R rMin(R arg0, R arg1) {
        return this.realAlgebra.min(arg0, arg1);
    }

    /** Delegates to {@link RealAlgebra#max} */
    protected R rMax(R arg0, R arg1) {
        return this.realAlgebra.max(arg0, arg1);
    }

    /** Delegates to {@link RealAlgebra#lt} */
    protected B rLt(R arg0, R arg1) {
        return this.realAlgebra.lt(arg0, arg1);
    }

    /** Delegates to {@link RealAlgebra#le} */
    protected B rLe(R arg0, R arg1) {
        return this.realAlgebra.le(arg0, arg1);
    }

    /** Delegates to {@link RealAlgebra#gt} */
    protected B rGt(R arg0, R arg1) {
        return this.realAlgebra.gt(arg0, arg1);
    }

    /** Delegates to {@link RealAlgebra#ge} */
    protected B rGe(R arg0, R arg1) {
        return this.realAlgebra.ge(arg0, arg1);
    }

    /** Delegates to {@link RealAlgebra#neg} */
    protected R rNeg(R arg) {
        return this.realAlgebra.neg(arg);
    }

    /** Delegates to {@link IntAlgebra#toReal} */
    protected I rToInt(R arg) {
        return this.realAlgebra.toInt(arg);
    }

    /** Delegates to {@link RealAlgebra#toString} */
    protected S rToString(R arg) {
        return this.realAlgebra.toString(arg);
    }

    /** Delegates to {@link RealAlgebra#eq} */
    protected B rEq(R arg0, R arg1) {
        return this.realAlgebra.eq(arg0, arg1);
    }

    /** Delegates to {@link RealAlgebra#neq} */
    protected B rNeq(R arg0, R arg1) {
        return this.realAlgebra.neq(arg0, arg1);
    }

    /** Delegates to {@link StringAlgebra#concat} */
    protected S sConcat(S arg0, S arg1) {
        return this.stringAlgebra.concat(arg0, arg1);
    }

    /** Delegates to {@link StringAlgebra#lt} */
    protected B sLt(S arg0, S arg1) {
        return this.stringAlgebra.lt(arg0, arg1);
    }

    /** Delegates to {@link StringAlgebra#le} */
    protected B sLe(S arg0, S arg1) {
        return this.stringAlgebra.le(arg0, arg1);
    }

    /** Delegates to {@link StringAlgebra#gt} */
    protected B sGt(S arg0, S arg1) {
        return this.stringAlgebra.gt(arg0, arg1);
    }

    /** Delegates to {@link StringAlgebra#ge} */
    protected B sGe(S arg0, S arg1) {
        return this.stringAlgebra.ge(arg0, arg1);
    }

    /** Delegates to {@link StringAlgebra#length} */
    protected I sLength(S arg) {
        return this.stringAlgebra.length(arg);
    }

    /** Delegates to {@link StringAlgebra#eq} */
    protected B sEq(S arg0, S arg1) {
        return this.stringAlgebra.eq(arg0, arg1);
    }

    /** Delegates to {@link StringAlgebra#neq} */
    protected B sNeq(S arg0, S arg1) {
        return this.stringAlgebra.neq(arg0, arg1);
    }

    private final AlgebraFamily family;
    final IntAlgebra<I,R,B,S> intAlgebra;
    final BoolAlgebra<B> boolAlgebra;
    final RealAlgebra<I,R,B,S> realAlgebra;
    final StringAlgebra<S,B,I> stringAlgebra;
    private final B TRUE;
    private final B FALSE;
}
