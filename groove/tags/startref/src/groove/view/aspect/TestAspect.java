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
 * $Id: TestAspect.java,v 1.1 2007-04-29 09:22:24 rensink Exp $
 */
package groove.view.aspect;

/**
 * Graph aspect dealing with graph tests.
 * Relevant information is: which nested subgraph an element is in.
 * @author Arend Rensink
 * @version $Revision: 1.1 $
 */
final public class TestAspect extends AbstractAspect {
    /**
     * The name of the test aspect.
     */
    public static final String TEST_ASPECT_NAME = "graph test";

    /**
     * The singleton instance of this class.
     */
    private static final TestAspect instance = new TestAspect();
    
    /**
     * Returns the singleton instance of this aspect.
     */
    public static TestAspect getInstance() {
        return instance;
    }
    
    /** Private constructor to create the singleton instance. */
    private TestAspect() {
        super(TEST_ASPECT_NAME);
    }
}