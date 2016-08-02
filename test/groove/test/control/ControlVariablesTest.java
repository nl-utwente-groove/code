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
package groove.test.control;

import org.junit.Test;

/**
 * Tests the variables extension to the control language
 * @author Olaf Keijsers
 * @version $Revision $
 */
@SuppressWarnings("all")
public class ControlVariablesTest extends AControlTest {
    @Override
    protected String getDirectory() {
        return "junit/samples/control2.gps";
    }

    @Test
    public void testVariables() {
        // a; a; a;
        explore("variablesTest1", 4, 3, 4, 6);

        // a; a(out var1); a|e(var1); a(out var2); a|b(var1,var2);
        explore("variablesTest2", 6, 5, 11, 26);

        // a(out var1); a; a(out var2); try { b(var1, var2); } else { a(var1); }
        explore("variablesTest3", 5, 5, 10, 14);

        // addNode(out var1); deleteNode(var1);
        explore("variablesTest4", 3, 2, 3, 2);

        // addNode(out var1); deleteNode(var1); a(var1);
        // to show that the last rule will not match because var1 has been deleted
        explore("variablesTest5", 3, 2, 3, 2);

        // b(out var1, out var2); b(var2, out var3, c(var1, var2, var3);
        explore("variablesTest6", 3, 2, 14, 20);

        // addNode; merge(out par1, out par2)*; b(par1, par2);
        explore("mergeTest", 2, 1, 9, 17);
    }

    @Test
    public void testIntMatch() {
        // zero(1);
        explore("intMatchTest1", 2, 1, 1, 0);
        // zero(0);
        explore("intMatchTest2", 2, 1, 2, 1);
        // zero(_);
        explore("intMatchTest3", 2, 1, 2, 1);
    }
}
