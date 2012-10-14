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
 * Implementation of booleans consisting of a singleton value.
 * @author Arend Rensink
 * @version $Revision $
 */
public class TermBoolAlgebra extends BoolSignature<Term> implements
        Algebra<Term> {
    /** Private constructor for the singleton instance. */
    private TermBoolAlgebra() {
        // empty
    }

    @Override
    public Term and(Term arg0, Term arg1) {
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
    public Term not(Term arg) {
        // for now, only constants are supported
        throw new UnsupportedOperationException();
    }

    @Override
    public Term or(Term arg0, Term arg1) {
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

    public Constant getValueFromString(String constant) {
        return Algebras.getConstant(SignatureKind.BOOL, constant);
    }

    @Override
    protected Constant toValue(Boolean constant) {
        return Algebras.getConstant(SignatureKind.BOOL, constant.toString());
    }

    /** Name of this algebra. */
    public static final String NAME = "tbool";
    /** Singleton instance of this algebra. */
    public static final TermBoolAlgebra instance = new TermBoolAlgebra();
}
