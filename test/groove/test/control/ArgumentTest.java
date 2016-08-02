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
 * Tests the attribute parameter part of the control language.
 * @author Olaf Keijsers
 * @version $Revision $
 */
@SuppressWarnings("all")
public class ArgumentTest extends AControlTest {
    @Override
    protected String getDirectory() {
        return "junit/samples/control.gps";
    }

    /** Regression bug SF #3536820. */
    @Test
    public void testEmpty() {
        explore("empty", 2, 2, 22, 33);
    }
}
