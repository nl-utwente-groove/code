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

/**
 * Implementation of reals consisting of a singleton value.
 * To be used in conjunction with {@link PointBoolAlgebra} and {@link PointStringAlgebra}.
 * @author Arend Rensink
 * @version $Revision $
 */
public class TermRealAlgebra extends RealSignature<Term,Term,Term> implements
        Algebra<Term> {
    /** Private constructor for the singleton instance. */
    private TermRealAlgebra() {
        // empty
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

    public Constant getValueFromString(String constant) {
        return Algebras.getConstant(SignatureKind.REAL, constant);
    }

    @Override
    protected Constant toValue(Double constant) {
        return Algebras.getConstant(SignatureKind.REAL, constant.toString());
    }

    @Override
    public Term abs(Term arg) {
        // for now, only constants are supported
        throw new UnsupportedOperationException();
    }

    @Override
    public Term add(Term arg0, Term arg1) {
        // for now, only constants are supported
        throw new UnsupportedOperationException();
    }

    @Override
    public Term div(Term arg0, Term arg1) {
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
    public Term max(Term arg0, Term arg1) {
        // for now, only constants are supported
        throw new UnsupportedOperationException();
    }

    @Override
    public Term min(Term arg0, Term arg1) {
        // for now, only constants are supported
        throw new UnsupportedOperationException();
    }

    @Override
    public Term mul(Term arg0, Term arg1) {
        // for now, only constants are supported
        throw new UnsupportedOperationException();
    }

    @Override
    public Term neg(Term arg) {
        // for now, only constants are supported
        throw new UnsupportedOperationException();
    }

    @Override
    public Term sub(Term arg0, Term arg1) {
        // for now, only constants are supported
        throw new UnsupportedOperationException();
    }

    @Override
    public Term toString(Term arg) {
        // for now, only constants are supported
        throw new UnsupportedOperationException();
    }

    /** Name of this algebra. */
    public static final String NAME = "treal";
    /** Singleton instance of this algebra. */
    public static final TermRealAlgebra instance = new TermRealAlgebra();
}
