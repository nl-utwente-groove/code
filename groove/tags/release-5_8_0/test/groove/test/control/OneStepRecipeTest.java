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
package groove.test.control;

import org.junit.Test;

/**
 * Tests the behaviour of recipes with a single role body,
 * and with an output parameter that is called inside a function.
 * @author Arend Rensink
 * @version $Revision $
 */
@SuppressWarnings("all")
public class OneStepRecipeTest extends AControlTest {
    @Override
    protected String getDirectory() {
        return "junit/control/recipes.gps";
    }

    @Test
    public void testR() {
        explore("r", 2, 1, 2, 2);
    }

    @Test
    public void testF() {
        explore("f", 2, 1, 2, 2);
    }
}
