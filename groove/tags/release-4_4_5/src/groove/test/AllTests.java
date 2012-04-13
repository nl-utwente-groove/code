/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * $Id: AllTests.java,v 1.7 2008-03-17 17:42:09 iovka Exp $
 */
package groove.test;

import groove.test.control.AllControlTests;
import groove.test.graph.AllGraphTests;
import groove.test.prolog.AllPrologTests;
import groove.test.rule.AllRuleTests;
import groove.test.verify.AllVerifyTests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author Arend Rensink
 * @version $Revision$
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({//AllAbstractionTests.class, 
AllControlTests.class, AllGraphTests.class, AllPrologTests.class,
    AllRuleTests.class, AllVerifyTests.class, AutomatonTest.class,
    BinaryEdgeTest.class, ExplorationTest.class, HashBagTest.class,
    IOTest.class, LabelStoreTest.class, TreeHashSetTest.class})
public class AllTests {
    // Empty by design.
}