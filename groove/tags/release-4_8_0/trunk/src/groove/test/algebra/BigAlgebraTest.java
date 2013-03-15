/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
package groove.test.algebra;

import static org.junit.Assert.assertEquals;
import groove.algebra.AlgebraFamily;

import java.math.BigDecimal;
import java.math.BigInteger;

/** Tests the functionality of the java algebra family. */
public class BigAlgebraTest extends
        NonFinalAlgebraTest<Boolean,BigInteger,BigDecimal,String> {
    /** Creates a test. */
    public BigAlgebraTest() {
        super(AlgebraFamily.BIG);
    }

    @Override
    public void testInt() {
        super.testInt();
        BigInteger large = new BigInteger("1000000000000");
        BigInteger veryLarge = new BigInteger("1000000000000000000000000");
        assertEquals(veryLarge, iMul(large, large));
    }

    @Override
    public void testReal() {
        super.testReal();
        BigDecimal large = new BigDecimal("1000000000000.000000000000001");
        BigDecimal veryLarge = large.multiply(large);
        assertEquals(veryLarge, rMul(large, large));
    }
}
