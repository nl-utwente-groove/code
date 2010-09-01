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
 * $Id$
 */
package groove.test.abstraction;

import groove.abstraction.Multiplicity;
import groove.abstraction.Parameters;
import groove.graph.DefaultEdge;
import groove.graph.DefaultLabel;
import groove.graph.DefaultNode;
import groove.graph.Edge;
import groove.graph.Node;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

/**
 * @author Eduardo Zambon
 * @version $Revision $
 */
@SuppressWarnings("all")
public class TestMultiplicity extends TestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Multiplicity.initMultStore();
    }

    public void testEqualsObject() {
        assertTrue(Multiplicity.OMEGA.equals(Multiplicity.OMEGA));
        assertFalse(Multiplicity.OMEGA.equals(Multiplicity.getMultOf(0)));
        assertFalse(Multiplicity.OMEGA.equals(Multiplicity.getMultOf(1)));
        assertTrue(Multiplicity.getMultOf(1).equals(Multiplicity.getMultOf(1)));
    }

    public void testGetNodeSetMult() {
        Parameters.setNodeMultBound(2);
        Multiplicity.initMultStore();
        Set<Node> nodes = new HashSet<Node>(3);
        assertTrue(Multiplicity.getMultOf(0).equals(
            Multiplicity.getNodeSetMult(nodes)));
        nodes.add(DefaultNode.createNode());
        assertTrue(Multiplicity.getMultOf(1).equals(
            Multiplicity.getNodeSetMult(nodes)));
        nodes.add(DefaultNode.createNode());
        assertTrue(Multiplicity.getMultOf(2).equals(
            Multiplicity.getNodeSetMult(nodes)));
        nodes.add(DefaultNode.createNode());
        assertTrue(Multiplicity.OMEGA.equals(Multiplicity.getNodeSetMult(nodes)));
    }

    public void testGetEdgeSetMult() {
        Parameters.setEdgeMultBound(2);
        Multiplicity.initMultStore();
        Set<Edge> edges = new HashSet<Edge>(3);
        assertTrue(Multiplicity.getMultOf(0).equals(
            Multiplicity.getEdgeSetMult(edges)));
        edges.add(DefaultEdge.createEdge(DefaultNode.createNode(),
            DefaultLabel.createFreshLabel(), DefaultNode.createNode()));
        assertTrue(Multiplicity.getMultOf(1).equals(
            Multiplicity.getEdgeSetMult(edges)));
        edges.add(DefaultEdge.createEdge(DefaultNode.createNode(),
            DefaultLabel.createFreshLabel(), DefaultNode.createNode()));
        assertTrue(Multiplicity.getMultOf(2).equals(
            Multiplicity.getEdgeSetMult(edges)));
        edges.add(DefaultEdge.createEdge(DefaultNode.createNode(),
            DefaultLabel.createFreshLabel(), DefaultNode.createNode()));
        assertTrue(Multiplicity.OMEGA.equals(Multiplicity.getEdgeSetMult(edges)));
    }

    public void testCompare() {
        Parameters.setNodeMultBound(2);
        Multiplicity.initMultStore();
        Multiplicity zero = Multiplicity.getMultOf(0);
        Multiplicity one = Multiplicity.getMultOf(1);
        Multiplicity two = Multiplicity.getMultOf(2);
        Multiplicity omega = Multiplicity.OMEGA;
        assertTrue(zero.compare(zero) == 0);
        assertTrue(one.compare(one) == 0);
        assertTrue(two.compare(two) == 0);
        assertTrue(omega.compare(omega) == 0);
        assertTrue(zero.compare(one) == -1 && one.compare(zero) == 1);
        assertTrue(zero.compare(two) == -1 && two.compare(zero) == 1);
        assertTrue(zero.compare(omega) == -1 && omega.compare(zero) == 1);
        assertTrue(one.compare(two) == -1 && two.compare(one) == 1);
        assertTrue(one.compare(omega) == -1 && omega.compare(one) == 1);
        assertTrue(two.compare(omega) == -1 && omega.compare(two) == 1);
    }

    public void testIsPositive() {
        Parameters.setNodeMultBound(1);
        Multiplicity.initMultStore();
        Multiplicity zero = Multiplicity.getMultOf(0);
        Multiplicity one = Multiplicity.getMultOf(1);
        Multiplicity omega = Multiplicity.OMEGA;
        assertFalse(zero.isPositive());
        assertTrue(one.isPositive());
        assertTrue(omega.isPositive());
    }

    public void testIsAtMost() {
        Parameters.setNodeMultBound(2);
        Multiplicity.initMultStore();
        Multiplicity zero = Multiplicity.getMultOf(0);
        Multiplicity one = Multiplicity.getMultOf(1);
        Multiplicity two = Multiplicity.getMultOf(2);
        Multiplicity omega = Multiplicity.OMEGA;
        assertTrue(zero.isAtMost(zero));
        assertTrue(zero.isAtMost(one));
        assertTrue(zero.isAtMost(two));
        assertTrue(zero.isAtMost(omega));
        assertFalse(one.isAtMost(zero));
        assertTrue(one.isAtMost(one));
        assertTrue(one.isAtMost(two));
        assertTrue(one.isAtMost(omega));
        assertFalse(two.isAtMost(zero));
        assertFalse(two.isAtMost(one));
        assertTrue(two.isAtMost(two));
        assertTrue(two.isAtMost(omega));
        assertFalse(omega.isAtMost(zero));
        assertFalse(omega.isAtMost(one));
        assertFalse(omega.isAtMost(two));
        assertTrue(omega.isAtMost(omega));
    }

    public void testAdd() {
        Parameters.setNodeMultBound(2);
        Multiplicity.initMultStore();
        Multiplicity zero = Multiplicity.getMultOf(0);
        Multiplicity one = Multiplicity.getMultOf(1);
        Multiplicity two = Multiplicity.getMultOf(2);
        Multiplicity omega = Multiplicity.OMEGA;
        assertTrue(zero.add(zero, 1).equals(zero));
        assertTrue(zero.add(one, 1).equals(one));
        assertFalse(zero.add(two, 1).equals(two));
        assertTrue(zero.add(two, 1).equals(omega));
        assertTrue(zero.add(two, 2).equals(two));
        assertTrue(zero.add(omega, 1).equals(omega));
        assertTrue(zero.add(omega, 2).equals(omega));
        assertFalse(one.add(one, 1).equals(two));
        assertTrue(one.add(one, 1).equals(omega));
        assertTrue(one.add(one, 2).equals(two));
        assertTrue(two.add(two, 2).equals(omega));
        assertTrue(omega.add(one, 2).equals(omega));
    }

    public void testSub() {
        Parameters.setNodeMultBound(3);
        Multiplicity.initMultStore();
        Multiplicity zero = Multiplicity.getMultOf(0);
        Multiplicity one = Multiplicity.getMultOf(1);
        Multiplicity two = Multiplicity.getMultOf(2);
        Multiplicity three = Multiplicity.getMultOf(3);
        Multiplicity omega = Multiplicity.OMEGA;
        Set<Multiplicity> result = three.sub(zero, 3);
        assertTrue(result.size() == 1 && result.contains(three));
        result = three.sub(one, 3);
        assertTrue(result.size() == 1 && result.contains(two));
        result = three.sub(two, 3);
        assertTrue(result.size() == 1 && result.contains(one));
        result = three.sub(three, 3);
        assertTrue(result.size() == 1 && result.contains(zero));
        result = omega.sub(zero, 3);
        assertTrue(result.size() == 1 && result.contains(omega));
        result = omega.sub(one, 3);
        assertTrue(result.size() == 2 && result.contains(omega)
            && result.contains(three));
        result = omega.sub(two, 3);
        assertTrue(result.size() == 3 && result.contains(omega)
            && result.contains(three) && result.contains(two));
        result = omega.sub(three, 3);
        assertTrue(result.size() == 4 && result.contains(omega)
            && result.contains(three) && result.contains(two)
            && result.contains(one));
        result = omega.sub(omega, 3);
        assertTrue(result.size() == 1 && result.contains(zero));
    }

    public void testMultiply() {
        Parameters.setEdgeMultBound(2);
        Multiplicity.initMultStore();
        Multiplicity zero = Multiplicity.getMultOf(0);
        Multiplicity one = Multiplicity.getMultOf(1);
        Multiplicity two = Multiplicity.getMultOf(2);
        Multiplicity omega = Multiplicity.OMEGA;

        assertTrue(zero.multiply(zero).equals(zero));
        assertTrue(zero.multiply(one).equals(zero));
        assertTrue(zero.multiply(two).equals(zero));
        assertTrue(zero.multiply(omega).equals(zero));
        assertTrue(one.multiply(one).equals(one));
        assertTrue(one.multiply(two).equals(two));
        assertTrue(one.multiply(omega).equals(omega));
        assertTrue(two.multiply(two).equals(omega));
        assertTrue(two.multiply(omega).equals(omega));
        assertTrue(omega.multiply(omega).equals(omega));
    }

}
