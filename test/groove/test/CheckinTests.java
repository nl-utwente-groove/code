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
 * $Id$
 */
package groove.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import groove.test.algebra.AllAlgebraTests;
import groove.test.control.AllControlTests;
import groove.test.criticalpair.AllCriticalPairTests;
import groove.test.grammar.AllGrammarTests;
import groove.test.graph.AllGraphTests;
import groove.test.prolog.AllPrologTests;
import groove.test.rel.AllRelTests;
import groove.test.rule.AllRuleTests;
import groove.test.sts.AllSTSTests;
import groove.test.type.AllTypeTests;
import groove.test.util.AllUtilTests;
import groove.test.verify.AllVerifyTests;

/**
 * Test suite to be run upon checkin.
 * Also run by the Jenkins build server.
 * @author Arend Rensink
 * @version $Revision$
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({AllAlgebraTests.class, AllControlTests.class, AllCriticalPairTests.class,
    AllGraphTests.class, AllGrammarTests.class, AllPrologTests.class, AllRuleTests.class,
    AllTypeTests.class, AllRelTests.class, AllVerifyTests.class, BinaryEdgeTest.class,
    ExplorationTest.class, IOTest.class, AllUtilTests.class, AllSTSTests.class})
public class CheckinTests {
    // Empty by design.
}
