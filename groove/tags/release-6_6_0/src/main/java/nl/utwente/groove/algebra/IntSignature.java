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
 * The signature for integer algebras.
 * @param <INT> The representation type of the integer algebra
 * @param <REAL> The representation type of the real algebra
 * @param <BOOL> The representation type of the boolean algebra
 * @param <STRING> The representation type of the string algebra

 * @author Arend Rensink
 * @version $Revision$
 */
@SuppressWarnings("rawtypes")
public sealed abstract class IntSignature<INT,REAL,BOOL,STRING>
    implements GSignature<INT,INT,REAL,BOOL,STRING> permits IntAlgebra {
    @Override
    public abstract INT abs(INT arg);

    @Override
    public abstract INT add(INT arg0, INT arg1);

    @Override
    public abstract INT bigmax(List<INT> arg);

    @Override
    public abstract INT bigmin(List<INT> arg);

    @Override
    public abstract INT div(INT arg0, INT arg1);

    @Override
    public abstract BOOL eq(INT arg0, INT arg1);

    @Override
    public abstract BOOL ge(INT arg0, INT arg1);

    @Override
    public abstract BOOL gt(INT arg0, INT arg1);

    @Override
    public abstract INT ite(BOOL arg0, INT arg1, INT arg2);

    @Override
    public abstract BOOL le(INT arg0, INT arg1);

    @Override
    public abstract BOOL lt(INT arg0, INT arg1);

    @Override
    public abstract INT max(INT arg0, INT arg1);

    @Override
    public abstract INT min(INT arg0, INT arg1);

    @Override
    public abstract INT mod(INT arg0, INT arg1);

    @Override
    public abstract INT mul(INT arg0, INT arg1);

    @Override
    public abstract BOOL neq(INT arg0, INT arg1);

    @Override
    public abstract INT neg(INT arg);

    @Override
    public abstract INT prod(List<INT> arg);

    @Override
    public abstract INT sub(INT arg0, INT arg1);

    @Override
    public abstract INT sum(List<INT> arg);

    @Override
    public abstract STRING toString(INT arg);

    @Override
    public abstract REAL toReal(INT arg);

    @Override
    public Sort getSort() {
        return Sort.INT;
    }

    /** Integer constant for the value zero. */
    public static final Constant ZERO = Constant.instance(0);

    /** Enumeration of all operators defined in this signature. */
    public enum Op implements Signature.OpValue {
        /** Value for {@link IntSignature#abs(Object)}. */
        ABS,
        /** Value for {@link IntSignature#add(Object, Object)}. */
        ADD,
        /** Value for {@link IntSignature#bigmax(List)}. */
        BIGMAX,
        /** Value for {@link IntSignature#bigmin(List)}. */
        BIGMIN,
        /** Value for {@link IntSignature#div(Object, Object)}. */
        DIV,
        /** Value for {@link IntSignature#eq(Object, Object)}. */
        EQ,
        /** Value for {@link IntSignature#ge(Object, Object)}. */
        GE,
        /** Value for {@link IntSignature#gt(Object, Object)}. */
        GT,
        /** Value for {@link IntSignature#ite(Object, Object, Object)}. */
        ITE,
        /** Value for {@link IntSignature#le(Object, Object)}. */
        LE,
        /** Value for {@link IntSignature#lt(Object, Object)}. */
        LT,
        /** Value for {@link IntSignature#max(Object, Object)}. */
        MAX,
        /** Value for {@link IntSignature#min(Object, Object)}. */
        MIN,
        /** Value for {@link IntSignature#mod(Object, Object)}. */
        MOD,
        /** Value for {@link IntSignature#mul(Object, Object)}. */
        MUL,
        /** Value for {@link IntSignature#neq(Object, Object)}. */
        NEQ,
        /** Value for {@link IntSignature#neq(Object, Object)}. */
        NEG,
        /** Value for {@link IntSignature#prod(List)}. */
        PROD(true),
        /** Value for {@link IntSignature#sub(Object, Object)}. */
        SUB,
        /** Value for {@link IntSignature#sum(List)}. */
        SUM(true),
        /** Value for {@link IntSignature#toReal(Object)}. */
        TO_REAL,
        /** Value for {@link IntSignature#toString(Object)}. */
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
                this.operator = Operator.newInstance(Sort.INT, this);
            }
            return this.operator;
        }

        /** Corresponding operator object. */
        private Operator operator;

        @Override
        public boolean isSupportsZero() {
            return this.supportsZero;
        }

        private final boolean supportsZero;
    }
}
