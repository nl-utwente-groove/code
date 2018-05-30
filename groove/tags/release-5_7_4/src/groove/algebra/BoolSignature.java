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

import static groove.util.parse.OpKind.AND;
import static groove.util.parse.OpKind.EQUAL;
import static groove.util.parse.OpKind.NOT;
import static groove.util.parse.OpKind.OR;

import java.util.List;

import groove.annotation.InfixSymbol;
import groove.annotation.PrefixSymbol;
import groove.annotation.Syntax;
import groove.annotation.ToolTipBody;
import groove.annotation.ToolTipHeader;

/**
 * Signature for boolean algebras.
 * <Bool> Representation type for boolean values
 * @author Arend Rensink
 * @version $Revision$
 */
public abstract class BoolSignature<Bool> implements Signature {
    /** Negation. */
    @ToolTipHeader("Inversion")
    @Syntax("Q%s.LPAR.b1.RPAR")
    @ToolTipBody("Yields TRUE if boolean %s is FALSE")
    @PrefixSymbol(symbol = "!", kind = NOT)
    public abstract Bool not(Bool arg);

    /** Conjunction. */
    @ToolTipHeader("Conjunction")
    @Syntax("Q%s.LPAR.b1.COMMA.b2.RPAR")
    @ToolTipBody("Yields TRUE if booleans %s and %s are both TRUE")
    @InfixSymbol(symbol = "&", kind = AND)
    public abstract Bool and(Bool arg0, Bool arg1);

    /** Conjunction of a set of booleans. */
    @Syntax("Q%s.LPAR.i1.RPAR")
    @ToolTipHeader("Collective boolean conjunction")
    @ToolTipBody("Returns the conjunction of all quantified values")
    public abstract Bool bigand(List<Bool> arg);

    /** Disjunction of a set of booleans. */
    @Syntax("Q%s.LPAR.i1.RPAR")
    @ToolTipHeader("Collective boolean disjunction")
    @ToolTipBody("Returns the disjunction of all quantified values")
    public abstract Bool bigor(List<Bool> arg);

    /** Disjunction. */
    @ToolTipHeader("Disjunction")
    @Syntax("Q%s.LPAR.b1.COMMA.b2.RPAR")
    @ToolTipBody("Yields TRUE if at least one of booleans %s and %s is TRUE")
    @InfixSymbol(symbol = "|", kind = OR)
    public abstract Bool or(Bool arg0, Bool arg1);

    /** Equality test. */
    @ToolTipHeader("Boolean equality test")
    @Syntax("Q%s.LPAR.b1.COMMA.b2.RPAR")
    @ToolTipBody("Yields TRUE if boolean %s equals boolean %s")
    @InfixSymbol(symbol = "==", kind = EQUAL)
    public abstract Bool eq(Bool arg0, Bool arg1);

    /** Inequality test. */
    @ToolTipHeader("Boolean inequality test")
    @Syntax("Q%s.LPAR.b1.COMMA.b2.RPAR")
    @ToolTipBody("Yields TRUE if boolean %s is not equal to boolean %s")
    @InfixSymbol(symbol = "!=", kind = EQUAL)
    public abstract Bool neq(Bool arg0, Bool arg1);

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
