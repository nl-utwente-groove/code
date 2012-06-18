// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente

// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// http://www.apache.org/licenses/LICENSE-2.0

// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
// either express or implied. See the License for the specific
// language governing permissions and limitations under the License.
/*
 * $Id: AutomatonTest.java,v 1.10 2008-02-05 13:28:27 rensink Exp $
 */
package groove.test.rel;

import groove.rel.RegAut;
import groove.rel.SimpleNFA;

/**
 * Tests the available {@link RegAut} interface.
 * @author Arend Rensink
 * @version $Revision: 4155 $
 */
@SuppressWarnings("all")
public class SimpleAutomatonTest extends AbstractAutomatonTest {
    @Override
    protected RegAut getPrototype() {
        return SimpleNFA.PROTOTYPE;
    }
}
