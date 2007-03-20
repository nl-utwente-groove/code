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
 * $Id: AttributedGraphTest.java,v 1.1.1.2 2007-03-20 10:42:55 kastenberg Exp $
 */
package groove.test.graph;

import groove.graph.Graph;
import groove.graph.algebra.AttributedGraph;
import groove.io.UntypedGxl;
import groove.io.Xml;

import java.io.File;
import java.util.Set;

import junit.framework.TestCase;

/**
 * Test class to test <tt>DefaultGraph</tt>
 * @author Arend Rensink
 * @version $Revision: 1.1.1.2 $
 */
public class AttributedGraphTest extends TestCase {

    static public final String ATTR_GRAPH_NAME = "attr-";
    static public final String GRAPH_TEST_DIR = "junit/graphs";

    AttributedGraph attrG1, attrG2;

    public AttributedGraphTest(String name) {
        super(name);
        this.xml = new UntypedGxl();
    }

	public void setUp() throws Exception {
//		GraphFactory factory = DefaultAttributedGraph.graphFactory;
//		attrG1 = (AttributedGraph) factory.newGraph(loadGraph(testFile(ATTR_GRAPH_NAME + 1)));
//		attrG2 = (AttributedGraph) factory.newGraph(loadGraph(testFile(ATTR_GRAPH_NAME + 2)));
	}

    protected Graph loadGraph(File file) throws Exception {
        return xml.unmarshal(file);
    }
    
	final public void testNodeSet() {
		Set<?> nodeSet1 = attrG1.nodeSet();
		Set<?> nodeSet2 = attrG2.nodeSet();
		assertEquals(3, nodeSet1.size());
		assertEquals(6, nodeSet2.size());
	}

	final public void testNodeCount() {
		assertEquals(3, attrG1.nodeCount());
		assertEquals(6, attrG2.nodeCount());
	}

	final public void testEdgeCount() {
		assertEquals(3, attrG1.edgeCount());
		assertEquals(8, attrG2.edgeCount());
	}

	private final Xml xml;
}