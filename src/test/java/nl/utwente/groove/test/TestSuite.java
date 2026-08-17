package nl.utwente.groove.test;

import org.junit.platform.suite.api.ExcludeEngines;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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

/**
 * Test suite for all GROOVE tests.
 * This is the single-class entry point for runners that do not discover test
 * classes themselves; the "GROOVE - all JUnit tests" launch configuration runs
 * exactly this class. Surefire, which does discover the classes directly,
 * excludes this file in the pom — running both would execute every test twice.
 * <p>
 * Note that the launch configuration lists an {@code --add-opens} VM argument
 * per test package (the JUnit engines live in the unnamed module and access
 * test classes reflectively); a new test package needs a line added there.
 * @author Arend Rensink
 * @version $Revision$
 */
@Suite
@SelectPackages("nl.utwente.groove.test")
@ExcludeEngines("junit-platform-suite")
public class TestSuite {
    // empty by design
}
