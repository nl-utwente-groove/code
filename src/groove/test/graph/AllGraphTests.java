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
 * $Id: AllGraphTests.java,v 1.2 2007-03-20 18:22:06 rensink Exp $
 */
package groove.test.graph;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * 
 * @author Arend Rensink
 * @version $Revision: 1.2 $
 */
public class AllGraphTests {

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for groove.graph");
        //$JUnit-BEGIN$

        // all kinds of graphs
        suite.addTest(new TestSuite(DeltaGraphTest.class));
        suite.addTest(new TestSuite(DefaultGraphTest.class));
        suite.addTest(new TestSuite(AdjacencyMapGraphTest.class));
        suite.addTest(new TestSuite(NodeEdgeSetGraphTest.class));
        suite.addTest(new TestSuite(NodeSetEdgeMapGraphTest.class));
        suite.addTest(new TestSuite(NodeSetEdgeSetGraphTest.class));

        // all kinds of graph elements
        suite.addTest(new TestSuite(OperationNodeTest.class));
        //$JUnit-END$
        return suite;
    }
}
