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

import groove.algebra.Sort;
import groove.sts.Gate;
import groove.sts.InteractionVariable;

import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * Tests for gates in an STS.
 * @author Vincent de Bruijn
 * @version $Revision $
 */
@SuppressWarnings("all")
public class GateTest extends TestCase {

    private Gate g1;

    /**
     * Constructor.
     * @param name The name of the test
     */
    public GateTest(String name) {
        super(name);
    }

    @Override
    protected void setUp() {
        Set<InteractionVariable> s = new HashSet();
        s.add(new InteractionVariable("label", Sort.INT));
        s.add(new InteractionVariable("lebal", Sort.BOOL));
        this.g1 = new Gate("?ding", s);
    }

    /**
     * Tests getLabel.
     */
    public void testGetLabel() {
        Assert.assertEquals(this.g1.getLabel(), "?ding");
    }

    /**
     * Tests equals.
     */
    public void testEquals() {
        Gate g2 = new Gate("!ble_fg", new HashSet());
        Gate g3 = new Gate("?ding", new HashSet());
        Assert.assertTrue(this.g1.equals(this.g1));
        Assert.assertFalse(this.g1.equals(g2));
        Assert.assertTrue(this.g1.equals(g3));
        Assert.assertFalse(this.g1.equals(""));
    }

    /**
     * Tests hashcode.
     */
    public void testHashcode() {
        this.g1.hashCode();
    }

    /**
     * Tests toJson.
     */
    public void testToJson() {
        String json = this.g1.toJSON();
        // TODO: check if json is well-formatted.
    }

    /**
     * Tests getStrippedLabel.
     */
    public void testGetStrippedLabel() {
        Gate g2 = new Gate("!ble_fg", new HashSet());
        Assert.assertEquals(this.g1.getStrippedLabel(), "ding");
        Assert.assertEquals(g2.getStrippedLabel(), "ble_fg");
    }
}
