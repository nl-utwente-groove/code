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

import static org.junit.Assert.assertTrue;
import groove.algebra.Algebra;
import groove.algebra.AlgebraFamily;
import groove.algebra.Sort;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

/** Tests the functionality of the java algebra family. */
public class AlgebrasTest {
    /** Tests consistency of the algebras. */
    @Test
    public void testConsistency() {
        Set<String> names = new HashSet<>();
        for (AlgebraFamily family : AlgebraFamily.values()) {
            for (Sort kind : Sort.values()) {
                Algebra<?> algebra = family.getAlgebra(kind);
                boolean freshName = names.add(algebra.getName());
                assertTrue(freshName);
            }
        }
    }
}
