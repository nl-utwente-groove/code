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
package groove.test.abstraction.neigh;

import groove.abstraction.neigh.Abstraction;

import org.junit.AfterClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author Eduardo Zambon
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({TestMultiplicity.class, TestGraphNeighEquiv.class,
    TestShape.class, TestPreMatch.class, TestMaterialisation.class,
    TestShapeIso.class, TestShapeGenerator.class})
public class AllNeighAbstractionTests {

    /** Reverts back to normal (non-abstract) mode. */
    @AfterClass
    public static void cleanUp() {
        Abstraction.terminate();
    }

}
