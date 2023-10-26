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
package nl.utwente.groove.test.sts;

import java.util.HashSet;

import org.junit.Assert;

import junit.framework.TestCase;
import nl.utwente.groove.sts.Gate;
import nl.utwente.groove.sts.Location;
import nl.utwente.groove.sts.SwitchRelation;

/**
 * Tests a location in an sts.
 * @author Vincent de Bruijn
 * @version $Revision $
 */
public class LocationTest extends TestCase {

    private Location l;

    /**
     * Constructor.
     * @param name The name of the test.
     */
    public LocationTest(String name) {
        super(name);
    }

    @Override
    protected void setUp() {
        this.l = new Location("label");
    }

    /**
     * Tests add/getSwitchRelations and getRelationTarget.
     */
    public void testAddGetSwitchRelations() {
        Location l2 = new Location("l2");
        SwitchRelation sr = new SwitchRelation(new Gate("lbl", new HashSet<>()), "", "");
        this.l.addSwitchRelation(sr, l2);
        Assert.assertNotNull(this.l.getSwitchRelations());
        Assert.assertTrue(this.l.getSwitchRelations().size() == 1);
        Assert.assertEquals(this.l.getSwitchRelations().iterator().next(), sr);
        Assert.assertEquals(this.l.getRelationTargets(sr).iterator().next(), l2);
    }

    /**
     * Tests getLabel.
     */
    public void testGetLabel() {
        Assert.assertEquals(this.l.getLabel(), "label");
    }

    /**
     * Tests equals.
     */
    public void testEquals() {
        Location l2 = new Location("labe");
        Assert.assertTrue(this.l.equals(this.l));
        Assert.assertFalse(this.l.equals(l2));
    }

    /**
     * Tests hashcode.
     */
    public void testHashCode() {
        this.l.hashCode();
    }

    /**
     * Tests toJson.
     */
    public void testToJSON() {
        this.l.toJSON();
    }

}
