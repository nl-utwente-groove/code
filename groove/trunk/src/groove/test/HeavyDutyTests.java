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

import groove.test.abstraction.ExtraShapeGeneratorTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Suite of tests that are time consuming and therefore are not included in
 * the AllTests suite.
 * 
 * @author Eduardo Zambon
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ExtraShapeGeneratorTest.class})
public class HeavyDutyTests {
    // Empty by design.
}
