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
package groove.test.rel;

import static groove.graph.EdgeRole.BINARY;
import static groove.graph.EdgeRole.FLAG;
import static groove.graph.EdgeRole.NODE_TYPE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import groove.grammar.AnchorKind;
import groove.grammar.rule.LabelVar;

import org.junit.Test;

/** Tests the class {@link LabelVar}. */
public class LabelVarTest {
    /** Tests the construction of named variables. */
    @Test
    public void testNamedVar() {
        LabelVar var = new LabelVar("a", BINARY);
        // equals tests
        assertEquals(var, var);
        assertEquals(new LabelVar("a", BINARY), var);
        assertEquals(new LabelVar("a", BINARY).hashCode(), var.hashCode());
        assertFalse(var.equals(null));
        assertFalse(var.equals("a"));
        assertFalse(var.equals(new LabelVar("a", FLAG)));
        assertFalse(var.equals(new LabelVar("a", NODE_TYPE)));
        // compareTo test
        assertEquals(0, var.compareTo(var));
        assertEquals(0, var.compareTo(new LabelVar("a", BINARY)));
        assertTrue(var.compareTo(new LabelVar("a", FLAG)) != 0);
        assertTrue(var.compareTo(new LabelVar("a", NODE_TYPE)) != 0);
        LabelVar otherVar = new LabelVar(BINARY);
        assertEquals(var.compareTo(otherVar) < 0, otherVar.compareTo(var) > 0);
        // field tests
        assertEquals(BINARY, var.getKind());
        assertEquals("a", var.getName());
        assertTrue(var.hasName());
        assertEquals(AnchorKind.LABEL, var.getAnchorKind());
        assertNotNull(var.getKey());
        assertNotNull(var.toString());
    }

    /** Tests the construction of unnamed variables. */
    @Test
    public void testUnnamedVar() {
        LabelVar var = new LabelVar(BINARY);
        // equals tests
        assertEquals(var, var);
        assertFalse(var.equals(null));
        assertFalse(var.equals("a"));
        assertFalse(var.equals(new LabelVar(BINARY)));
        assertFalse(var.equals(new LabelVar("a", BINARY)));
        // field tests
        assertEquals("", var.getName());
        assertFalse(var.hasName());
        assertEquals(BINARY, var.getKind());
        assertNotNull(var.getKey());
        assertNotNull(var.toString());
    }
}
