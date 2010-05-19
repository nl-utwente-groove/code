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

import groove.test.verify.ModelCheckingTest;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * 
 * @author Arend Rensink
 * @version $Revision$
 */
@SuppressWarnings("all")
public class AllTests {

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for groove.test");
        // $JUnit-BEGIN$
        suite.addTest(new TestSuite(BinaryEdgeTest.class));
        suite.addTest(new TestSuite(GraphCreationTest.class));
        suite.addTest(new TestSuite(MorphismTest.class));
        suite.addTest(new TestSuite(NACTest.class));
        suite.addTest(new TestSuite(HashBagTest.class));
        suite.addTest(new TestSuite(ExplorationTest.class));
        // suite.addTest(new TestSuite(TemporalFormulaTest.class));
        suite.addTest(new TestSuite(ModelCheckingTest.class));
        suite.addTest(new TestSuite(TreeSetTest.class));
        suite.addTest(new TestSuite(StrategiesTest.class));
        suite.addTest(new TestSuite(IOTest.class));
        suite.addTest(new TestSuite(ControlVariablesTest.class));
        suite.addTest(new TestSuite(ControlAttributeParametersTest.class));
        suite.addTest(new TestSuite(LabelStoreTest.class));
        // $JUnit-END$
        return suite;
    }
}
