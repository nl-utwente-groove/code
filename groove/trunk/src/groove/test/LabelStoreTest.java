/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * $Id$
 */
package groove.test;

import groove.graph.DefaultLabel;
import groove.graph.Label;
import groove.graph.LabelStore;
import groove.view.FormatException;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

/**
 * @author Arend
 * @version $Revision $
 */
public class LabelStoreTest extends TestCase {
    @Override
    protected void setUp() throws Exception {
        this.store1 = new LabelStore();
        this.store1.addSubtype(this.typeA, this.typeB);
        this.store1.addSubtype(this.typeB, this.typeC);
        this.store1.addSubtype(this.typeA, this.typeD);
        this.store2 = new LabelStore();
    }

    /**
     * Test method for
     * {@link groove.graph.LabelStore#add(groove.graph.LabelStore)}.
     */
    public void testAdd() {
        Set<Label> labels1 = new HashSet<Label>(this.store1.getLabels());
        Set<Label> subtypesC =
            new HashSet<Label>(this.store1.getSubtypes(this.typeC));
        this.store2.addLabel(this.a);
        this.store2.addSubtype(this.typeC, this.typeD);
        this.store1.add(this.store2);
        labels1.add(this.a);
        subtypesC.add(this.typeD);
        assertEquals(this.store1.getLabels(), labels1);
        assertEquals(this.store1.getSubtypes(this.typeC), subtypesC);
    }

    /**
     * Test method for
     * {@link groove.graph.LabelStore#addLabel(groove.graph.Label)}.
     */
    public void testAddLabel() {
        assertFalse(this.store1.getLabels().contains(this.a));
        assertNull(this.store1.getDirectSubtypes(this.a));
        // add an edge type
        this.store1.addLabel(this.a);
        assertTrue(this.store1.getLabels().contains(this.a));
        this.store1.addLabel(this.a);
        assertTrue(this.store1.getLabels().contains(this.a));
        assertTrue(this.store1.getDirectSubtypes(this.a).isEmpty());
        Set<Label> subtypes = new HashSet<Label>();
        subtypes.add(this.a);
        assertEquals(this.store1.getSubtypes(this.a), subtypes);
        // add a node type
        assertTrue(this.store1.getLabels().contains(this.typeA));
        this.store1.addLabel(this.typeA);
        assertTrue(this.store1.getLabels().contains(this.typeA));
    }

    /**
     * Test method for {@link groove.graph.LabelStore#addLabels(java.util.Set)}.
     */
    public void testAddLabels() {
        Set<Label> labelSet = new HashSet<Label>();
        labelSet.add(this.a);
        labelSet.add(this.typeA);
        labelSet.add(this.b);
        this.store1.addLabels(labelSet);
        assertTrue(this.store1.getLabels().contains(this.a));
        assertTrue(this.store1.getLabels().contains(this.b));
        assertFalse(this.store1.getLabels().contains(this.c));
    }

    /**
     * Test method for
     * {@link groove.graph.LabelStore#addSubtype(groove.graph.Label, groove.graph.Label)}
     * .
     */
    public void testAddSubtype() {
        this.store2.addSubtype(this.typeA, this.typeB);
        assertTrue(this.store2.getLabels().contains(this.typeA));
        assertTrue(this.store2.getLabels().contains(this.typeB));
        Set<Label> directSubtypes = new HashSet<Label>();
        directSubtypes.add(this.typeB);
        assertEquals(this.store2.getDirectSubtypes(this.typeA), directSubtypes);
        Set<Label> subtypes = new HashSet<Label>();
        subtypes.add(this.typeA);
        subtypes.add(this.typeB);
        assertEquals(this.store2.getSubtypes(this.typeA), subtypes);
        this.store2.addSubtype(this.typeC, this.typeD);
        this.store2.addSubtype(this.typeB, this.typeC);
        assertEquals(this.store2.getDirectSubtypes(this.typeA), directSubtypes);
        subtypes.add(this.typeC);
        subtypes.add(this.typeD);
        assertEquals(this.store2.getSubtypes(this.typeA), subtypes);
    }

    /**
     * Test method for
     * {@link groove.graph.LabelStore#removeSubtype(groove.graph.Label, groove.graph.Label)}
     * .
     */
    public void testRemoveSubtype() {
        this.store2.addSubtype(this.typeA, this.typeB);
        this.store2.addSubtype(this.typeB, this.typeC);
        this.store2.addSubtype(this.typeC, this.typeD);
        assertTrue(this.store2.getSubtypes(this.typeA).contains(this.typeC));
        assertTrue(this.store2.getSubtypes(this.typeA).contains(this.typeD));
        assertTrue(this.store2.getSubtypes(this.typeB).contains(this.typeD));
        this.store2.removeSubtype(this.typeB, this.typeC);
        assertFalse(this.store2.getSubtypes(this.typeA).contains(this.typeC));
        assertFalse(this.store2.getSubtypes(this.typeA).contains(this.typeD));
        assertFalse(this.store2.getSubtypes(this.typeB).contains(this.typeD));
    }

    /**
     * Test method for
     * {@link groove.graph.LabelStore#getSupertypes(groove.graph.Label)}.
     */
    public void testGetSupertypes() {
        Set<Label> supertypesA = new HashSet<Label>();
        supertypesA.add(this.typeA);
        assertEquals(this.store1.getSupertypes(this.typeA), supertypesA);
        Set<Label> supertypesB = new HashSet<Label>();
        supertypesB.add(this.typeB);
        supertypesB.add(this.typeA);
        assertEquals(this.store1.getSupertypes(this.typeB), supertypesB);
        Set<Label> supertypesC = new HashSet<Label>();
        supertypesC.add(this.typeC);
        supertypesC.add(this.typeB);
        supertypesC.add(this.typeA);
        assertEquals(this.store1.getSupertypes(this.typeC), supertypesC);
        Set<Label> supertypesD = new HashSet<Label>();
        supertypesD.add(this.typeD);
        supertypesD.add(this.typeA);
        assertEquals(this.store1.getSupertypes(this.typeD), supertypesD);
    }

    /**
     * Test method for
     * {@link groove.graph.LabelStore#getDirectSupertypes(groove.graph.Label)}.
     */
    public void testGetDirectSupertypes() {
        Set<Label> supertypesA = new HashSet<Label>();
        assertEquals(this.store1.getDirectSupertypes(this.typeA), supertypesA);
        Set<Label> supertypesB = new HashSet<Label>();
        supertypesB.add(this.typeA);
        assertEquals(this.store1.getDirectSupertypes(this.typeB), supertypesB);
        Set<Label> supertypesC = new HashSet<Label>();
        supertypesC.add(this.typeB);
        assertEquals(this.store1.getDirectSupertypes(this.typeC), supertypesC);
        Set<Label> supertypesD = new HashSet<Label>();
        supertypesD.add(this.typeA);
        assertEquals(this.store1.getDirectSupertypes(this.typeD), supertypesD);
    }

    /**
     * Test method for {@link groove.graph.LabelStore#equals(java.lang.Object)}.
     */
    public void testEqualsObject() {
        this.store2 = this.store1.clone();
        assertEquals(this.store1, this.store2);
        this.store2.addLabel(this.a);
        assertFalse(this.store1.equals(this.store2));
        this.store1.addLabel(this.a);
        assertEquals(this.store1, this.store2);
        this.store2.addSubtype(this.typeA, this.typeB);
        assertEquals(this.store1, this.store2);
        this.store2.addSubtype(this.typeA, this.typeC);
        assertFalse(this.store1.equals(this.store2));
        this.store1.addSubtype(this.typeA, this.typeC);
        assertEquals(this.store1, this.store2);
    }

    /**
     * Test method for {@link groove.graph.LabelStore#toDirectSubtypeString()}.
     */
    public void testToDirectSubtypeString() {
        assertEquals(this.store1.toDirectSubtypeString(), "A > B, D; B > C");
        assertEquals(this.store2.toDirectSubtypeString(), "");
    }

    /**
     * Test method for
     * {@link groove.graph.LabelStore#addDirectSubtypes(java.lang.String)}.
     */
    public void testAddDirectSubtypes() {
        LabelStore store1Clone = this.store1.clone();
        try {
            store1Clone.addDirectSubtypes("");
        } catch (FormatException exc) {
            fail();
        }
        assertEquals(this.store1, store1Clone);
        try {
            this.store2.addDirectSubtypes("A > B, D; B > C");
        } catch (FormatException exc) {
            fail();
        }
        assertEquals(this.store1, this.store2);
        try {
            this.store2.addDirectSubtypes("A > D");
        } catch (FormatException exc) {
            fail();
        }
        assertEquals(this.store1, this.store2);
        try {
            this.store2.addDirectSubtypes("A > C");
        } catch (FormatException exc) {
            fail();
        }
        this.store1.addSubtype(this.typeA, this.typeC);
        assertEquals(this.store1, this.store2);
    }

    /**
     * Test method for {@link groove.graph.LabelStore#clone()}.
     */
    public void testClone() {
        assertEquals(this.store2.clone().getLabels(), Collections.emptySet());
    }

    /**
     * Test method for
     * {@link groove.graph.LabelStore#parseDirectSubtypeString(java.lang.String)}
     * .
     */
    public void testParseDirectSubtypeString() {
        correctDirectSubtypeString("A > B; A>C;B> C,D;  B >D,E");
        correctDirectSubtypeString("A > C;");
        correctDirectSubtypeString("");
        incorrectDirectSubtypeString("A > C D");
        incorrectDirectSubtypeString("A, C > D");
        incorrectDirectSubtypeString("; C > D");
        incorrectDirectSubtypeString("C > ,D");
    }

    private void correctDirectSubtypeString(String string) {
        try {
            LabelStore.parseDirectSubtypeString(string);
        } catch (FormatException exc) {
            fail(String.format("Wrong format error in '%s': %s", string,
                exc.getMessage()));
        }
    }

    private void incorrectDirectSubtypeString(String string) {
        try {
            LabelStore.parseDirectSubtypeString(string);
            fail(String.format("Format error in '%s' not caught", string));
        } catch (FormatException exc) {
            // ignore
        }
    }

    private LabelStore store1;
    private LabelStore store2;
    private final Label typeA = DefaultLabel.createLabel("A", Label.NODE_TYPE);
    private final Label typeB = DefaultLabel.createLabel("B", Label.NODE_TYPE);
    private final Label typeC = DefaultLabel.createLabel("C", Label.NODE_TYPE);
    private final Label typeD = DefaultLabel.createLabel("D", Label.NODE_TYPE);
    private final Label a = DefaultLabel.createLabel("a");
    private final Label b = DefaultLabel.createLabel("b");
    private final Label c = DefaultLabel.createLabel("c");
}
