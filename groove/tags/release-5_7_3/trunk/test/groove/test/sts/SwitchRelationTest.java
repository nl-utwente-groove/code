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
package groove.test.sts;

import groove.sts.Gate;
import groove.sts.InteractionVariable;
import groove.sts.Location;
import groove.sts.SwitchRelation;

import java.util.HashSet;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * Tests a switch relation in an sts.
 * @author Vincent de Bruijn
 * @version $Revision $
 */
@SuppressWarnings("all")
public class SwitchRelationTest extends TestCase {

    private SwitchRelation sr;
    private Gate gate;
    private String guard;
    private String update;

    /**
     * Constructor.
     * @param name The name of the test.
     */
    public SwitchRelationTest(String name) {
        super(name);
    }

    @Override
    protected void setUp() {
        this.gate = new Gate("gateLabel", new HashSet<InteractionVariable>());
        this.guard = "i < 5";
        this.update = "i = 5;";
        this.sr = new SwitchRelation(this.gate, this.guard, this.update);
    }

    /**
     * Tests getGate.
     */
    public void testgetGate() {
        Assert.assertEquals(this.gate, this.sr.getGate());
    }

    /**
     * Tests getGuard.
     */
    public void testgetGuard() {
        Assert.assertEquals(this.guard, this.sr.getGuard());
    }

    /**
     * Tests getUpdate.
     */
    public void testGetUpdate() {
        Assert.assertEquals(this.update, this.sr.getUpdate());
    }

    /**
     * Tests equals.
     */
    public void testEquals() {
        SwitchRelation sr2 = new SwitchRelation(this.gate, this.guard, "i = 8");
        Assert.assertTrue(this.sr.equals(this.sr));
        Assert.assertFalse(this.sr.equals(""));
        Assert.assertFalse(this.sr.equals(sr2));
    }

    /**
     * Tests hashCode.
     */
    public void testHashCode() {
        this.sr.hashCode();
    }

    /**
     * Tests toJson.
     */
    public void testToJSON() {
        Location source = new Location("source");
        Location target = new Location("target");
        String json = this.sr.toJSON(source, target);
    }

}
