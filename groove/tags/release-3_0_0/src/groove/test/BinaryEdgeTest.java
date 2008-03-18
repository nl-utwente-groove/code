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
 * $Id: BinaryEdgeTest.java,v 1.4 2008-01-30 09:33:03 iovka Exp $
 */
package groove.test;

import groove.graph.BinaryEdge;
import groove.graph.DefaultEdge;
import groove.graph.DefaultLabel;
import groove.graph.DefaultNode;
import groove.graph.Node;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

/**
 * @version $Revision: 1.4 $ $Date: 2008-01-30 09:33:03 $
 */
public class BinaryEdgeTest extends TestCase {
    protected Node n1, n2;
    protected BinaryEdge e;

    public BinaryEdgeTest(String name) {
        super(name);
    }

    @Override
    protected void setUp() {
        n1 = DefaultNode.createNode();
        n2 = DefaultNode.createNode();
        e = DefaultEdge.createEdge(n1, "a", n2);
    }

    public void testQueries() {
        assertEquals(n1, e.source());
        assertEquals(DefaultLabel.createLabel(new String("a")), e.label());
        assertEquals(n2, e.target());
    }

    public void testCreateAndCompare() {
        BinaryEdge e2 = DefaultEdge.createEdge(n1, "a", n2);

        assertEquals(e,e2);
        assertEquals(e2,e);

        assertTrue(! e.equals(DefaultEdge.createEdge(n1, "a", n1)));
        assertTrue(! e.equals(DefaultEdge.createEdge(n1, "b", n2)));
        assertTrue(! e.equals(DefaultEdge.createEdge(n2, "a", n2)));
    }

    public void testCopyAndCompare() {
        BinaryEdge e2 = DefaultEdge.createEdge(e.source(), e.label(), e.target());

        assertEquals(e,e2);
        assertEquals(e2,e);
    }

    public void testHashSetEquals() {
        BinaryEdge e2 = DefaultEdge.createEdge(n1, "a", n2);

        Set<BinaryEdge> s1 = new HashSet<BinaryEdge>();
        s1.add(e);
        assertTrue(s1.contains(e2));

        Set<BinaryEdge> s2 = new HashSet<BinaryEdge>();
        s2.add(e2);
        assertEquals(s1,s2);
    }
}

