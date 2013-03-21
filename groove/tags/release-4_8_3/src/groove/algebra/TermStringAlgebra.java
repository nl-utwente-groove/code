/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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
package groove.algebra;

import groove.util.ExprParser;

/**
 * Term algebra of strings.
 * @author Arend Rensink
 * @version $Revision $
 */
public class TermStringAlgebra extends StringAlgebra<Term,Term,Term> {
    /** Private constructor for the singleton instance. */
    private TermStringAlgebra() {
        // empty
    }

    @Override
    public Term concat(Term arg0, Term arg1) {
        // for now, only constants are supported
        throw new UnsupportedOperationException();
    }

    @Override
    public Term eq(Term arg0, Term arg1) {
        // for now, only constants are supported
        throw new UnsupportedOperationException();
    }

    @Override
    public Term neq(Term arg0, Term arg1) {
        // for now, only constants are supported
        throw new UnsupportedOperationException();
    }

    @Override
    public Term ge(Term arg0, Term arg1) {
        // for now, only constants are supported
        throw new UnsupportedOperationException();
    }

    @Override
    public Term gt(Term arg0, Term arg1) {
        // for now, only constants are supported
        throw new UnsupportedOperationException();
    }

    @Override
    public Term le(Term arg0, Term arg1) {
        // for now, only constants are supported
        throw new UnsupportedOperationException();
    }

    @Override
    public Term lt(Term arg0, Term arg1) {
        // for now, only constants are supported
        throw new UnsupportedOperationException();
    }

    @Override
    public Term length(Term arg) {
        // for now, only constants are supported
        throw new UnsupportedOperationException();
    }

    public String getName() {
        return NAME;
    }

    @Override
    public AlgebraFamily getFamily() {
        return AlgebraFamily.TERM;
    }

    public String getSymbol(Object value) {
        return ((Constant) value).getSymbol();
    }

    public Constant getValueFromSymbol(String symbol) {
        return Algebras.getConstant(SignatureKind.STRING, symbol);
    }

    @Override
    public Constant toValue(String value) {
        String symbol =
            ExprParser.toQuoted(value, ExprParser.DOUBLE_QUOTE_CHAR);
        return Algebras.getConstant(SignatureKind.STRING, symbol);
    }

    /** Name of this algebra. */
    public static final String NAME = "tstring";
    /** Singleton instance of this algebra. */
    public static final TermStringAlgebra instance = new TermStringAlgebra();
}
