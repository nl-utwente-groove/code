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
 * Signature for boolean algebras.
 * <Bool> Representation type for boolean values
 * @author Arend Rensink
 * @version $Revision$
 */
public sealed abstract class BoolSignature<BOOL> implements GSignature<BOOL,Void,Void,BOOL,Void>
    permits BoolAlgebra {
    @Override
    public abstract BOOL not(BOOL arg);

    @Override
    public abstract BOOL and(BOOL arg0, BOOL arg1);

    @Override
    public abstract BOOL bigand(List<BOOL> arg);

    @Override
    public abstract BOOL bigor(List<BOOL> arg);

    @Override
    public abstract BOOL or(BOOL arg0, BOOL arg1);

    @Override
    public abstract BOOL eq(BOOL arg0, BOOL arg1);

    @Override
    public abstract BOOL neq(BOOL arg0, BOOL arg1);

    @Override
    public Sort getSort() {
        return Sort.BOOL;
    }

    /** The constant for the true value. */
    public static final Constant TRUE = Constant.instance(true);
    /** The constant for the false value. */
    public static final Constant FALSE = Constant.instance(false);

    /** Enumeration of all operators defined in this signature. */

    public enum Op implements Signature.OpValue {
        /** Value for {@link BoolSignature#and(Object,Object)}. */
        AND,
        /** Value for {@link BoolSignature#bigand(List)}. */
        BIGAND,
        /** Value for {@link BoolSignature#bigor(List)}. */
        BIGOR,
        /** Value for {@link BoolSignature#or(Object, Object)}. */
        OR,
        /** Value for {@link BoolSignature#not(Object)}. */
        NOT,
        /** Value for {@link BoolSignature#eq(Object, Object)}. */
        EQ,
        /** Value for {@link BoolSignature#neq(Object, Object)}. */
        NEQ,;

        @Override
        public Operator getOperator() {
            if (this.operator == null) {
                this.operator = Operator.newInstance(Sort.BOOL, this);
            }
            return this.operator;
        }

        /** Corresponding operator object. */
        private Operator operator;
    }
}
