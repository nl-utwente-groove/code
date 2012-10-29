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

import static groove.algebra.Precedence.AND;
import static groove.algebra.Precedence.EQUAL;
import static groove.algebra.Precedence.OR;
import groove.annotation.InfixSymbol;
import groove.annotation.PrefixSymbol;
import groove.annotation.Syntax;
import groove.annotation.ToolTipBody;
import groove.annotation.ToolTipHeader;

/**
 * Interface for boolean algebras.
 * @author Arend Rensink
 * @version $Revision: 1577 $
 */
public abstract class BoolSignature<Bool> implements Signature {
    /** Negation. */
    @ToolTipHeader("Inversion")
    @Syntax("Q%s.LPAR.b1.RPAR")
    @ToolTipBody("Yields TRUE if boolean %s is FALSE")
    @PrefixSymbol(symbol = "!")
    public abstract Bool not(Bool arg);

    /** Conjunction. */
    @ToolTipHeader("Conjunction")
    @Syntax("Q%s.LPAR.b1.COMMA.b2.RPAR")
    @ToolTipBody("Yields TRUE if booleans %s and %s are both TRUE")
    @InfixSymbol(symbol = "&", precedence = AND)
    public abstract Bool and(Bool arg0, Bool arg1);

    /** Disjunction. */
    @ToolTipHeader("Disjunction")
    @Syntax("Q%s.LPAR.b1.COMMA.b2.RPAR")
    @ToolTipBody("Yields TRUE if at least one of booleans %s and %s is TRUE")
    @InfixSymbol(symbol = "|", precedence = OR)
    public abstract Bool or(Bool arg0, Bool arg1);

    /** Equality test. */
    @ToolTipHeader("Boolean equality test")
    @Syntax("Q%s.LPAR.b1.COMMA.b2.RPAR")
    @ToolTipBody("Yields TRUE if boolean %s equals boolean %s")
    @InfixSymbol(symbol = "==", precedence = EQUAL)
    public abstract Bool eq(Bool arg0, Bool arg1);

    /** Inequality test. */
    @ToolTipHeader("Boolean inequality test")
    @Syntax("Q%s.LPAR.b1.COMMA.b2.RPAR")
    @ToolTipBody("Yields TRUE if boolean %s is not equal to boolean %s")
    @InfixSymbol(symbol = "!=", precedence = EQUAL)
    public abstract Bool neq(Bool arg0, Bool arg1);

    @Override
    public SignatureKind getKind() {
        return SignatureKind.BOOL;
    }

    /** Only <code>true</code> and <code>false</code> are legal values. */
    final public boolean isValue(String value) {
        return value.equals(TRUE) || value.equals(FALSE);
    }

    /**
     * Conversion of native Java representation of integer constants to
     * the corresponding algebra values.
     * @throws IllegalArgumentException if the parameter is not of type {@link Integer}
     */
    final public Bool getValueFromJava(Object constant) {
        if (!(constant instanceof Boolean)) {
            throw new IllegalArgumentException(java.lang.String.format(
                "Native int type is %s, not %s", Boolean.class.getSimpleName(),
                constant.getClass().getSimpleName()));
        }
        return toValue((Boolean) constant);
    }

    /** 
     * Callback method to convert from the native ({@link Integer})
     * representation to the algebra representation.
     */
    protected abstract Bool toValue(Boolean constant);

    /** The unique string representation of the true value. */
    public static final String TRUE = "true";
    /** The unique string representation of the false value. */
    public static final String FALSE = "false";
}
