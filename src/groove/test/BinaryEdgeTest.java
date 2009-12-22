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
 * @version $Revision$ $Date: 2008-01-30 09:33:03 $
 */
@SuppressWarnings("all")
public class BinaryEdgeTest extends TestCase {
    protected Node n1, n2;
    protected BinaryEdge e;

    public BinaryEdgeTest(String name) {
        super(name);
    }

    @Override
    protected void setUp() {
        this.n1 = DefaultNode.createNode();
        this.n2 = DefaultNode.createNode();
        this.e = DefaultEdge.createEdge(this.n1, "a", this.n2);
    }

    public void testQueries() {
        assertEquals(this.n1, this.e.source());
        assertEquals(DefaultLabel.createLabel(new String("a")), this.e.label());
        assertEquals(this.n2, this.e.target());
    }

    public void testCreateAndCompare() {
        BinaryEdge e2 = DefaultEdge.createEdge(this.n1, "a", this.n2);

        assertEquals(this.e, e2);
        assertEquals(e2, this.e);

        assertTrue(!this.e.equals(DefaultEdge.createEdge(this.n1, "a", this.n1)));
        assertTrue(!this.e.equals(DefaultEdge.createEdge(this.n1, "b", this.n2)));
        assertTrue(!this.e.equals(DefaultEdge.createEdge(this.n2, "a", this.n2)));
    }

    public void testCopyAndCompare() {
        BinaryEdge e2 =
            DefaultEdge.createEdge(this.e.source(), this.e.label(),
                this.e.target());

        assertEquals(this.e, e2);
        assertEquals(e2, this.e);
    }

    public void testHashSetEquals() {
        BinaryEdge e2 = DefaultEdge.createEdge(this.n1, "a", this.n2);

        Set<BinaryEdge> s1 = new HashSet<BinaryEdge>();
        s1.add(this.e);
        assertTrue(s1.contains(e2));

        Set<BinaryEdge> s2 = new HashSet<BinaryEdge>();
        s2.add(e2);
        assertEquals(s1, s2);
    }
}
