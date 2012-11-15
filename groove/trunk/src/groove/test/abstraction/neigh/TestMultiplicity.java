/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
package groove.test.abstraction.neigh;

import static groove.abstraction.Multiplicity.OMEGA;
import static groove.abstraction.Multiplicity.add;
import static groove.abstraction.Multiplicity.approx;
import static groove.abstraction.Multiplicity.getMultiplicity;
import static groove.abstraction.Multiplicity.scale;
import static groove.abstraction.Multiplicity.sub;
import static groove.abstraction.Multiplicity.times;
import static groove.abstraction.Multiplicity.MultKind.EDGE_MULT;
import static groove.abstraction.Multiplicity.MultKind.EQSYS_MULT;
import static groove.abstraction.Multiplicity.MultKind.NODE_MULT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import groove.abstraction.Multiplicity;
import groove.abstraction.neigh.NeighAbsParam;

import org.junit.Test;

@SuppressWarnings("all")
public class TestMultiplicity {

    @Test
    public void testNatOmegaAdd() {
        assertEquals(0, add(0, 0));
        assertEquals(1, add(0, 1));
        assertEquals(1, add(1, 0));
        assertEquals(2, add(1, 1));
        assertEquals(OMEGA, add(1, OMEGA));
        assertEquals(OMEGA, add(OMEGA, 0));
        assertEquals(OMEGA, add(OMEGA, OMEGA));
    }

    @Test
    public void testNatOmegaSub() {
        assertEquals(0, sub(0, 0));
        assertEquals(0, sub(0, 1));
        assertEquals(0, sub(2, 2));
        assertEquals(1, sub(2, 1));
        assertEquals(OMEGA, sub(OMEGA, 0));
        assertEquals(OMEGA, sub(OMEGA, 1));
    }

    @Test
    public void testNatOmegaTimes() {
        assertEquals(0, times(0, 0));
        assertEquals(0, times(0, 1));
        assertEquals(0, times(1, 0));
        assertEquals(1, times(1, 1));
        assertEquals(6, times(2, 3));
        assertEquals(OMEGA, times(1, OMEGA));
        assertEquals(0, times(OMEGA, 0));
        assertEquals(OMEGA, times(OMEGA, OMEGA));
    }

    @Test
    public void testApprox() {
        NeighAbsParam.getInstance().setNodeMultBound(3);
        Multiplicity twoThree = getMultiplicity(2, 3, NODE_MULT);
        Multiplicity twoPlus = getMultiplicity(2, OMEGA, NODE_MULT);
        Multiplicity fourPlus = getMultiplicity(4, OMEGA, NODE_MULT);
        assertEquals(twoThree, approx(2, 3, NODE_MULT));
        assertEquals(twoPlus, approx(2, 4, NODE_MULT));
        assertEquals(fourPlus, approx(4, 4, NODE_MULT));
    }

    @Test
    public void testScale() {
        NeighAbsParam.getInstance().setNodeMultBound(2);
        Multiplicity zero = getMultiplicity(0, 0, NODE_MULT);
        Multiplicity one = getMultiplicity(1, 1, NODE_MULT);
        Multiplicity two = getMultiplicity(2, 2, NODE_MULT);
        Multiplicity threePlus = getMultiplicity(3, OMEGA, NODE_MULT);
        assertEquals(zero, scale(zero, 2));
        assertEquals(zero, scale(one, 0));
        assertEquals(one, scale(one, 1));
        assertEquals(two, scale(one, 2));
        assertEquals(threePlus, scale(two, 2));
    }

    @Test
    public void testEquals() {
        NeighAbsParam.getInstance().setNodeMultBound(2);
        Multiplicity zeroNode = getMultiplicity(0, 0, NODE_MULT);
        Multiplicity oneNode = getMultiplicity(1, 1, NODE_MULT);
        Multiplicity zeroEdge = getMultiplicity(0, 0, EDGE_MULT);
        assertFalse(zeroNode.equals(zeroEdge));
        assertFalse(zeroNode.equals(oneNode));
    }

    @Test
    public void testAdd() {
        NeighAbsParam.getInstance().setNodeMultBound(1);
        Multiplicity zero = getMultiplicity(0, 0, NODE_MULT);
        Multiplicity one = getMultiplicity(1, 1, NODE_MULT);
        Multiplicity twoPlus = getMultiplicity(2, OMEGA, NODE_MULT);
        assertEquals(one, zero.add(one));
        assertEquals(twoPlus, one.add(one));
        assertEquals(twoPlus, twoPlus.add(one));
    }

    @Test
    public void testSub() {
        NeighAbsParam.getInstance().setNodeMultBound(2);
        Multiplicity zero = getMultiplicity(0, 0, NODE_MULT);
        Multiplicity one = getMultiplicity(1, 1, NODE_MULT);
        Multiplicity zeroOne = getMultiplicity(0, 1, NODE_MULT);
        Multiplicity oneTwo = getMultiplicity(1, 2, NODE_MULT);
        Multiplicity onePlus = getMultiplicity(1, OMEGA, NODE_MULT);
        Multiplicity twoPlus = getMultiplicity(2, OMEGA, NODE_MULT);
        Multiplicity threePlus = getMultiplicity(3, OMEGA, NODE_MULT);
        assertEquals(zero, zero.sub(0));
        assertEquals(zero, zero.sub(1));
        assertEquals(one, one.sub(0));
        assertEquals(zeroOne, oneTwo.sub(1));
        assertEquals(twoPlus, threePlus.sub(1));
    }

    @Test
    public void testTimes() {
        NeighAbsParam.getInstance().setNodeMultBound(1);
        Multiplicity zeroN = getMultiplicity(0, 0, NODE_MULT);
        Multiplicity oneN = getMultiplicity(1, 1, NODE_MULT);
        Multiplicity twoPlusN = getMultiplicity(2, OMEGA, NODE_MULT);
        Multiplicity oneE = getMultiplicity(1, 1, EDGE_MULT);
        Multiplicity twoPlusE = getMultiplicity(2, OMEGA, EDGE_MULT);
        Multiplicity zeroS = getMultiplicity(0, 0, EQSYS_MULT);
        Multiplicity oneS = getMultiplicity(1, 1, EQSYS_MULT);
        Multiplicity twoPlusS = getMultiplicity(2, OMEGA, EQSYS_MULT);
        Multiplicity fourPlusS = getMultiplicity(4, OMEGA, EQSYS_MULT);
        assertEquals(zeroS, zeroN.times(oneE));
        assertEquals(zeroS, zeroN.times(twoPlusE));
        assertEquals(oneS, oneN.times(oneE));
        assertEquals(twoPlusS, oneN.times(twoPlusE));
        assertEquals(twoPlusS, twoPlusN.times(oneE));
        assertEquals(fourPlusS, twoPlusN.times(twoPlusE));
    }

    @Test
    public void testLe() {
        NeighAbsParam.getInstance().setNodeMultBound(2);
        Multiplicity one = getMultiplicity(1, 1, NODE_MULT);
        Multiplicity zeroOne = getMultiplicity(0, 1, NODE_MULT);
        Multiplicity onePlus = getMultiplicity(1, OMEGA, NODE_MULT);
        Multiplicity threePlus = getMultiplicity(3, OMEGA, NODE_MULT);
        assertTrue(zeroOne.le(one));
        assertTrue(zeroOne.le(onePlus));
        assertTrue(onePlus.le(threePlus));
        assertTrue(threePlus.le(threePlus));
    }

    @Test
    public void testSubsumes() {
        NeighAbsParam.getInstance().setNodeMultBound(2);
        Multiplicity one = getMultiplicity(1, 1, NODE_MULT);
        Multiplicity zeroOne = getMultiplicity(0, 1, NODE_MULT);
        Multiplicity onePlus = getMultiplicity(1, OMEGA, NODE_MULT);
        Multiplicity threePlus = getMultiplicity(3, OMEGA, NODE_MULT);
        assertFalse(one.subsumes(zeroOne));
        assertTrue(zeroOne.subsumes(one));
        assertFalse(zeroOne.subsumes(onePlus));
        assertTrue(onePlus.subsumes(threePlus));
        assertTrue(threePlus.subsumes(threePlus));
        assertFalse(threePlus.subsumes(onePlus));
    }
}
