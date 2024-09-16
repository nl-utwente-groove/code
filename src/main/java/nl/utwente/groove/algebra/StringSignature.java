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

/**
 * Signature for string algebras.
 * @param <INT> The representation type of the integer algebra
 * @param <REAL> The representation type of the real algebra
 * @param <BOOL> The representation type of the boolean algebra
 * @param <STRING> The representation type of the string algebra
 * @author Arend Rensink
 * @version $Revision$
 */
public sealed abstract class StringSignature<INT,REAL,BOOL,STRING>
    implements GSignature<STRING,INT,REAL,BOOL,STRING> permits StringAlgebra {
    @Override
    public abstract STRING concat(STRING arg0, STRING arg1);

    @Override
    public abstract BOOL isBool(STRING arg0);

    @Override
    public abstract BOOL isInt(STRING arg0);

    @Override
    public abstract BOOL isReal(STRING arg0);

    @Override
    public abstract BOOL toBool(STRING arg0);

    @Override
    public abstract INT toInt(STRING arg0);

    @Override
    public abstract REAL toReal(STRING arg0);

    @Override
    public abstract BOOL lt(STRING arg0, STRING arg1);

    @Override
    public abstract BOOL le(STRING arg0, STRING arg1);

    @Override
    public abstract STRING ite(BOOL arg0, STRING arg1, STRING arg2);

    @Override
    public abstract BOOL gt(STRING arg0, STRING arg1);

    @Override
    public abstract BOOL ge(STRING arg0, STRING arg1);

    @Override
    public abstract BOOL eq(STRING arg0, STRING arg1);

    @Override
    public abstract BOOL neq(STRING arg0, STRING arg1);

    @Override
    public abstract INT length(STRING arg);

    @Override
    public abstract STRING substring(STRING arg0, INT arg1, INT arg2);

    @Override
    public abstract STRING suffix(STRING arg0, INT arg1);

    @Override
    public abstract INT lookup(STRING arg0, STRING arg1);

    @Override
    public Sort getSort() {
        return Sort.STRING;
    }

    /** String constant for the empty string. */
    public static final Constant EMPTY = Constant.instance("");

    /** Enumeration of all operators defined in this signature. */
    public enum Op implements Signature.OpValue {
        /** Value for {@link StringSignature#concat(Object, Object)}. */
        CONCAT,
        /** Value for {@link StringSignature#isBool(Object)}. */
        IS_BOOL,
        /** Value for {@link StringSignature#isInt(Object)}. */
        IS_INT,
        /** Value for {@link StringSignature#isReal(Object)}. */
        IS_REAL,
        /** Value for {@link StringSignature#toBool(Object)}. */
        TO_BOOL,
        /** Value for {@link StringSignature#toInt(Object)}. */
        TO_INT,
        /** Value for {@link StringSignature#toReal(Object)}. */
        TO_REAL,
        /** Value for {@link StringSignature#eq(Object, Object)}. */
        EQ,
        /** Value for {@link StringSignature#ge(Object, Object)}. */
        GE,
        /** Value for {@link StringSignature#gt(Object, Object)}. */
        GT,
        /** Value for {@link StringSignature#ite(Object, Object, Object)}. */
        ITE,
        /** Value for {@link StringSignature#le(Object, Object)}. */
        LE,
        /** Value for {@link StringSignature#lt(Object, Object)}. */
        LT,
        /** Value for {@link StringSignature#neq(Object, Object)}. */
        NEQ,
        /** Value for {@link StringSignature#length(Object)}. */
        LENGTH,
        /** Value for {@link StringSignature#substring(Object, Object, Object)}. */
        SUBSTRING,
        /** Value for {@link StringSignature#suffix(Object, Object)}. */
        SUFFIX,
        /** Value for {@link StringSignature#lookup(Object, Object)}. */
        LOOKUP,;

        @Override
        public Operator getOperator() {
            if (this.operator == null) {
                this.operator = Operator.newInstance(Sort.STRING, this);
            }
            return this.operator;
        }

        /** Corresponding operator object. */
        private Operator operator;
    }
}
