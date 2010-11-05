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
package groove.test.abstraction;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Eduardo Zambon
 * @version $Revision $
 */
@SuppressWarnings("all")
public class AllAbstractionTests {

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for groove.test.abstraction");
        // $JUnit-BEGIN$
        suite.addTest(new TestSuite(TestMultiplicity.class));
        suite.addTest(new TestSuite(TestGraphNeighEquiv.class));
        suite.addTest(new TestSuite(TestShape.class));
        suite.addTest(new TestSuite(TestPreMatch.class));
        suite.addTest(new TestSuite(TestMaterialisation.class));
        suite.addTest(new TestSuite(TestShapeGenerator.class));
        // $JUnit-END$
        return suite;
    }
}
