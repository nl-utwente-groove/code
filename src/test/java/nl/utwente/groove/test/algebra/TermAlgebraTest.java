/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.test.algebra;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import nl.utwente.groove.algebra.AlgebraFamily;
import nl.utwente.groove.algebra.IntSignature;
import nl.utwente.groove.algebra.RealSignature;
import nl.utwente.groove.algebra.Signature;
import nl.utwente.groove.algebra.syntax.CallExpr;
import nl.utwente.groove.algebra.syntax.Expression;
import nl.utwente.groove.util.AIGenerated;

/** Tests the functionality of the java algebra family. */
public class TermAlgebraTest extends AlgebraTest<Expression,Expression,Expression,Expression> {
    /** Creates a test. */
    public TermAlgebraTest() {
        super(AlgebraFamily.TERM);
    }

    /** Tests that the collection operators build terms with their own operator (gh #932). */
    @AIGenerated("Claude Opus 5.5, 2026-11")
    @Test
    public void testCollectionOperators() {
        var ints = List.of(this.intAlgebra.toValueFromJava(1), this.intAlgebra.toValueFromJava(2),
                           this.intAlgebra.toValueFromJava(3));
        assertOperator(IntSignature.Op.BIGMAX, this.intAlgebra.bigmax(ints));
        assertOperator(IntSignature.Op.BIGMIN, this.intAlgebra.bigmin(ints));
        assertOperator(IntSignature.Op.PROD, this.intAlgebra.prod(ints));
        assertOperator(IntSignature.Op.SUM, this.intAlgebra.sum(ints));
        var reals = List.of(this.realAlgebra.toValueFromJava(1.0),
                            this.realAlgebra.toValueFromJava(2.0),
                            this.realAlgebra.toValueFromJava(3.0));
        assertOperator(RealSignature.Op.BIGMAX, this.realAlgebra.bigmax(reals));
        assertOperator(RealSignature.Op.BIGMIN, this.realAlgebra.bigmin(reals));
        assertOperator(RealSignature.Op.PROD, this.realAlgebra.prod(reals));
        assertOperator(RealSignature.Op.SUM, this.realAlgebra.sum(reals));
    }

    @AIGenerated("Claude Opus 5.5, 2026-11")
    private void assertOperator(Signature.OpValue op, Expression term) {
        assertTrue(term instanceof CallExpr);
        assertEquals(op.getOperator(), ((CallExpr) term).getOperator());
        assertEquals(3, ((CallExpr) term).getArgs().size());
    }
}
