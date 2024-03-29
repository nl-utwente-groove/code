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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import nl.utwente.groove.algebra.AlgebraFamily;
import nl.utwente.groove.algebra.PointAlgebra;
import nl.utwente.groove.algebra.PointBoolAlgebra;
import nl.utwente.groove.algebra.PointIntAlgebra;
import nl.utwente.groove.algebra.PointRealAlgebra;
import nl.utwente.groove.algebra.PointStringAlgebra;
import nl.utwente.groove.algebra.Sort;

/** Tests the functionality of the java algebra family. */
public class PointAlgebraTest extends NonFinalAlgebraTest<Object,Object,Object,Object> {
    /** Creates a test. */
    public PointAlgebraTest() {
        super(AlgebraFamily.POINT);
    }

    @Override
    public void testConsistency() {
        super.testConsistency();
        Set<Object> points = new HashSet<>();
        for (Sort sort : Sort.values()) {
            if (sort == Sort.USER) {
                continue;
            }
            PointAlgebra<?> algebra = (PointAlgebra<?>) AlgebraFamily.POINT.getAlgebra(sort);
            Object point = algebra.getPointValue();
            assertNotNull(point);
            boolean freshValue = points.add(point);
            assertTrue(freshValue);
        }
    }

    @Override
    public void testInt() {
        super.testInt();
        assertEquals(createInt(-1), createInt(1));
        assertEquals(PointIntAlgebra.singleInt, createInt(1));
    }

    @Override
    public void testReal() {
        super.testReal();
        assertEquals(createReal(-0.1), createReal(10.5));
        assertEquals(PointRealAlgebra.singleReal, createReal(11.23));
    }

    @Override
    public void testBoolean() {
        super.testBoolean();
        assertEquals(bTrue(), bFalse());
        assertEquals(PointBoolAlgebra.singleBool, bTrue());
    }

    @Override
    public void testString() {
        super.testString();
        assertEquals(createString("a"), createString("b"));
        assertEquals(PointStringAlgebra.singleString, createString("a"));
    }

    @Override
    public void testIntError() {
        // empty as these errors do not occur in the point algebra
    }

    @Override
    public void testRealError() {
        // empty as these errors do not occur in the point algebra
    }
}
