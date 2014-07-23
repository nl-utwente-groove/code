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
import groove.sts.InteractionVariable;
import groove.sts.LocationVariable;
import groove.sts.Variable;
import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * Tests for variables in an sts.
 * @author Vincent de Bruijn
 * @version $Revision $
 */
@SuppressWarnings("all")
public class VariableTest extends TestCase {

    InteractionVariable iVar;
    LocationVariable lVar;

    /**
     * Constructor.
     * @param name Name of the test.
     */
    public VariableTest(String name) {
        super(name);
    }

    @Override
    protected void setUp() {
        this.iVar = new InteractionVariable("iLabel", Sort.INT);
        this.lVar = new LocationVariable("lLabel", Sort.REAL, 1.2);
    }

    /**
     * Tests getLabel.
     */
    public void testGetLabel() {
        Assert.assertEquals(this.iVar.getLabel(), "iLabel");
        Assert.assertEquals(this.lVar.getLabel(), "lLabel");
    }

    /**
     * Tests getType.
     */
    public void testGetType() {
        Assert.assertEquals(this.iVar.getType(), Sort.INT);
        Assert.assertEquals(this.lVar.getType(), Sort.REAL);
    }

    /**
     * Tests equals.
     */
    public void testEquals() {
        Assert.assertEquals(this.iVar, this.iVar);
        Assert.assertFalse(this.iVar.equals(this.lVar));
        Assert.assertFalse(this.lVar.equals(""));
    }

    /**
     * Tests hashCode.
     */
    public void testHashCode() {
        this.iVar.hashCode();
        this.lVar.hashCode();
    }

    /**
     * Test getInitialValue.
     */
    public void testGetInitialValue() {
        Assert.assertEquals(this.lVar.getInitialValue(), 1.2);
    }

    /**
     * Tests toJson in LocationVariable.
     */
    public void testToJsonInLocationVariable() {
        this.lVar.toJSON();
    }

    /**
     * Tests toJson in InteractionVariable.
     */
    public void testToJsonInInteractionVariable() {
        this.iVar.toJSON();
    }

    /** 
     * Test getDefaultValue in Variable
     */
    public void testGetDefaultValue() {
        Assert.assertEquals(
            Variable.getDefaultValue(Sort.INT).getClass(),
            Integer.class);
        Assert.assertEquals(
            Variable.getDefaultValue(Sort.REAL).getClass(),
            Double.class);
        Assert.assertEquals(
            Variable.getDefaultValue(Sort.BOOL).getClass(),
            Boolean.class);
        Assert.assertEquals(
            Variable.getDefaultValue(Sort.STRING).getClass(),
            String.class);
    }
}
