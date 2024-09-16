/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2023
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
package nl.utwente.groove.algebra;

import java.util.List;

/**
 * The signature for real number algebras.
 * @param <INT> The representation type of the int algebra
 * @param <REAL> The representation type of the real algebra
 * @param <BOOL> The representation type of the boolean algebra
 * @param <STRING> The representation type of the string algebra
 * @author Arend Rensink
 * @version $Revision$
 */
public sealed abstract class RealSignature<INT,REAL,BOOL,STRING>
    implements GSignature<REAL,INT,REAL,BOOL,STRING> permits RealAlgebra {
    @Override
    public abstract REAL abs(REAL arg);

    @Override
    public abstract REAL add(REAL arg0, REAL arg1);

    @Override
    public abstract REAL bigmax(List<REAL> arg);

    @Override
    public abstract REAL bigmin(List<REAL> arg);

    @Override
    public abstract REAL sub(REAL arg0, REAL arg1);

    @Override
    public abstract REAL mul(REAL arg0, REAL arg1);

    @Override
    public abstract REAL div(REAL arg0, REAL arg1);

    @Override
    public abstract REAL min(REAL arg0, REAL arg1);

    @Override
    public abstract REAL max(REAL arg0, REAL arg1);

    @Override
    public abstract REAL prod(List<REAL> arg);

    @Override
    public abstract REAL sum(List<REAL> arg);

    @Override
    public abstract BOOL lt(REAL arg0, REAL arg1);

    @Override
    public abstract BOOL le(REAL arg0, REAL arg1);

    @Override
    public abstract REAL ite(BOOL arg0, REAL arg1, REAL arg2);

    @Override
    public abstract BOOL gt(REAL arg0, REAL arg1);

    @Override
    public abstract BOOL ge(REAL arg0, REAL arg1);

    @Override
    public abstract BOOL eq(REAL arg0, REAL arg1);

    @Override
    public abstract BOOL neq(REAL arg0, REAL arg1);

    @Override
    public abstract REAL neg(REAL arg);

    @Override
    public abstract STRING toString(REAL arg);

    @Override
    public abstract INT toInt(REAL arg);

    @Override
    public Sort getSort() {
        return Sort.REAL;
    }

    /** Real constant for the value zero. */
    public static final Constant ZERO = Constant.instance(0.0);

    /** Enumeration of all operators defined in this signature. */
    public enum Op implements Signature.OpValue {
        /** Value for {@link RealSignature#abs(Object)}. */
        ABS,
        /** Value for {@link RealSignature#add(Object, Object)}. */
        ADD,
        /** Value for {@link RealSignature#bigmax(List)}. */
        BIGMAX,
        /** Value for {@link RealSignature#bigmin(List)}. */
        BIGMIN,
        /** Value for {@link RealSignature#div(Object, Object)}. */
        DIV,
        /** Value for {@link RealSignature#eq(Object, Object)}. */
        EQ,
        /** Value for {@link RealSignature#ge(Object, Object)}. */
        GE,
        /** Value for {@link RealSignature#gt(Object, Object)}. */
        GT,
        /** Value for {@link RealSignature#ite(Object, Object, Object)}. */
        ITE,
        /** Value for {@link RealSignature#le(Object, Object)}. */
        LE,
        /** Value for {@link RealSignature#lt(Object, Object)}. */
        LT,
        /** Value for {@link RealSignature#max(Object, Object)}. */
        MAX,
        /** Value for {@link RealSignature#min(Object, Object)}. */
        MIN,
        /** Value for {@link RealSignature#mul(Object, Object)}. */
        MUL,
        /** Value for {@link RealSignature#neq(Object, Object)}. */
        NEQ,
        /** Value for {@link RealSignature#neq(Object, Object)}. */
        NEG,
        /** Value for {@link RealSignature#prod(List)}. */
        PROD(true),
        /** Value for {@link RealSignature#sub(Object, Object)}. */
        SUB,
        /** Value for {@link RealSignature#sum(List)}. */
        SUM(true),
        /** Value for {@link RealSignature#toInt(Object)}. */
        TO_INT,
        /** Value for {@link RealSignature#toString(Object)}. */
        TO_STRING,;

        /**
         * Constructs an operator that does not support zero arguments.
         */
        private Op() {
            this(false);
        }

        /**
         * Constructs an operator that may or may not support zero arguments.
         */
        private Op(boolean supportsZero) {
            this.supportsZero = supportsZero;
        }

        @Override
        public Operator getOperator() {
            if (this.operator == null) {
                this.operator = Operator.newInstance(Sort.REAL, this);
            }
            return this.operator;
        }

        /** Corresponding operator object. */
        private Operator operator;

        @Override
        public boolean isZeroArgs() {
            return this.supportsZero;
        }

        private final boolean supportsZero;
    }
}
