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
 * $Id: AllGraphTests.java,v 1.4 2008-01-30 09:32:47 iovka Exp $
 */
package groove.test.graph;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author Arend Rensink
 * @version $Revision$
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({DefaultGraphTest.class, GraphCreationTest.class,
    NodeSetEdgeSetGraphTest.class})
public class AllGraphTests {
    // Empty by design.
}