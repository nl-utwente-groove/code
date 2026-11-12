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
package nl.utwente.groove.test.annotation;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.Test;

import nl.utwente.groove.annotation.BoolSeq;
import nl.utwente.groove.annotation.IntSeq;
import nl.utwente.groove.annotation.RealSeq;
import nl.utwente.groove.annotation.Seq;
import nl.utwente.groove.annotation.StringSeq;
import nl.utwente.groove.util.AIGenerated;

/**
 * Tests the sequence classes that carry the sequence-valued components of user types.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
@AIGenerated("Claude Fable 5.2, 2026-11")
public class SeqTest {
    /** Equality and hash code are determined by the class and the elements in order. */
    @Test
    public void testEquality() {
        assertEqual(IntSeq.of(1, 2, 3), IntSeq.of(1, 2, 3));
        assertEqual(RealSeq.of(1.5, -2), RealSeq.of(1.5, -2));
        assertEqual(BoolSeq.of(true, false), BoolSeq.of(true, false));
        // the second string is built at run time, so equality is not identity
        assertEqual(StringSeq.of("a", "bc"), StringSeq.of("a", "b".concat("c")));

        assertNotEquals(IntSeq.of(1, 2, 3), IntSeq.of(3, 2, 1));
        assertNotEquals(IntSeq.of(1, 2), IntSeq.of(1, 2, 0));
        assertNotEquals(BoolSeq.of(true), BoolSeq.of(false));
        assertNotEquals(StringSeq.of("a", "b"), StringSeq.of("ab"));
        assertNotEquals(IntSeq.of(1), RealSeq.of(1));
        assertNotEquals(IntSeq.of(1), null);
        assertNotEquals(IntSeq.of(1), "[1]");
    }

    /** Empty sequences are equal within a class, and different across classes. */
    @Test
    public void testEmpty() {
        assertEqual(IntSeq.of(), IntSeq.of(new int[0]));
        assertEquals(0, StringSeq.of().size());
        Seq[] empties = {IntSeq.of(), RealSeq.of(), BoolSeq.of(), StringSeq.of()};
        for (int i = 0; i < empties.length; i++) {
            for (int j = 0; j < empties.length; j++) {
                assertEquals(i == j, empties[i].equals(empties[j]));
            }
        }
    }

    /** Reals are compared as {@link Double#equals} does. */
    @Test
    public void testRealEquality() {
        assertEqual(RealSeq.of(Double.NaN), RealSeq.of(Double.NaN));
        assertNotEquals(RealSeq.of(0.0), RealSeq.of(-0.0));
    }

    /**
     * The hash code is stable, also if the content hashes to the value
     * that stands for "not yet computed".
     */
    @Test
    public void testHashCode() {
        // Arrays.hashCode of a singleton array is 31 plus the element hash
        var zeroHash = IntSeq.of(-31);
        int hash = zeroHash.hashCode();
        assertNotEquals(0, hash);
        assertEquals(hash, zeroHash.hashCode());
        assertEquals(hash, IntSeq.of(-31).hashCode());
        // a hash code must not depend on the run: this is the value of Arrays.hashCode
        assertEquals(30817, IntSeq.of(1, 2, 3).hashCode());
        assertEquals(1262, BoolSeq.of(true).hashCode());
        assertEquals(31 + "a".hashCode(), StringSeq.of("a").hashCode());
        assertEquals(31 + Double.hashCode(1.5), RealSeq.of(1.5).hashCode());
    }

    /** The elements are reported in order, unboxed and boxed. */
    @Test
    public void testAccess() {
        var ints = IntSeq.of(4, 5, 6);
        assertEquals(3, ints.size());
        assertEquals(5, ints.get(1));
        assertEquals(Integer.valueOf(6), boxed(ints, 2));
        assertEquals("[4, 5, 6]", ints.toString());

        var reals = RealSeq.of(0.5, 2);
        assertEquals(2, reals.size());
        assertEquals(2.0, reals.get(1));
        assertEquals(Double.valueOf(0.5), boxed(reals, 0));

        var bools = BoolSeq.of(false, true);
        assertEquals(2, bools.size());
        assertEquals(true, bools.get(1));
        assertEquals(Boolean.FALSE, boxed(bools, 0));

        var strings = StringSeq.of("x", "");
        assertEquals(2, strings.size());
        assertEquals("x", strings.get(0));
        assertEquals("", boxed(strings, 1));
        assertEquals("[x, ]", strings.toString());
    }

    /** A position outside the sequence is reported by an exception. */
    @Test
    public void testOutOfRange() {
        var ints = IntSeq.of(4, 5);
        assertThrows(IndexOutOfBoundsException.class, () -> ints.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> ints.get(2));
        assertThrows(IndexOutOfBoundsException.class, () -> ints.value(2));
        assertThrows(IndexOutOfBoundsException.class, () -> ints.with(2, 0));
        assertThrows(IndexOutOfBoundsException.class, () -> IntSeq.of().get(0));
        assertThrows(IndexOutOfBoundsException.class, () -> RealSeq.of(1).get(1));
        assertThrows(IndexOutOfBoundsException.class, () -> RealSeq.of(1).with(-1, 0));
        assertThrows(IndexOutOfBoundsException.class, () -> BoolSeq.of(true).get(1));
        assertThrows(IndexOutOfBoundsException.class, () -> BoolSeq.of(true).with(1, true));
        assertThrows(IndexOutOfBoundsException.class, () -> StringSeq.of("a").value(1));
        assertThrows(IndexOutOfBoundsException.class, () -> StringSeq.of("a").with(1, "b"));
    }

    /** Arrays passed in and handed out are copies. */
    @Test
    public void testCopies() {
        int[] intArray = {1, 2};
        var ints = IntSeq.of(intArray);
        intArray[0] = 9;
        ints.toArray()[1] = 9;
        assertArrayEquals(new int[] {1, 2}, ints.toArray());

        double[] realArray = {1, 2};
        var reals = RealSeq.of(realArray);
        realArray[0] = 9;
        reals.toArray()[1] = 9;
        assertArrayEquals(new double[] {1, 2}, reals.toArray());

        boolean[] boolArray = {true, true};
        var bools = BoolSeq.of(boolArray);
        boolArray[0] = false;
        bools.toArray()[1] = false;
        assertArrayEquals(new boolean[] {true, true}, bools.toArray());

        String[] stringArray = {"a", "b"};
        var strings = StringSeq.of(stringArray);
        stringArray[0] = "z";
        strings.toArray()[1] = "z";
        assertArrayEquals(new String[] {"a", "b"}, strings.toArray());
    }

    /** The modifying operations return a new sequence and leave the receiver as it was. */
    @Test
    public void testModification() {
        var ints = IntSeq.of(1, 2);
        int intHash = ints.hashCode();
        assertEqual(IntSeq.of(1, 7), ints.with(1, 7));
        assertEqual(IntSeq.of(1, 2, 3), ints.append(3));
        assertEqual(IntSeq.of(5), IntSeq.of().append(5));
        assertEqual(IntSeq.of(1, 2), ints);
        assertEquals(intHash, ints.hashCode());

        var reals = RealSeq.of(1, 2);
        assertEqual(RealSeq.of(0.5, 2), reals.with(0, 0.5));
        assertEqual(RealSeq.of(1, 2, 3), reals.append(3));
        assertEqual(RealSeq.of(1, 2), reals);

        var bools = BoolSeq.of(true, true);
        assertEqual(BoolSeq.of(true, false), bools.with(1, false));
        assertEqual(BoolSeq.of(true, true, false), bools.append(false));
        assertEqual(BoolSeq.of(true, true), bools);

        var strings = StringSeq.of("a", "b");
        assertEqual(StringSeq.of("c", "b"), strings.with(0, "c"));
        assertEqual(StringSeq.of("a", "b", "c"), strings.append("c"));
        assertEqual(StringSeq.of("a", "b"), strings);
    }

    /** A string sequence does not accept {@code null} as an element. */
    @Test
    public void testNullElement() {
        // an array element is how unchecked user code would get a null past the analysis
        String[] none = new String[1];
        assertThrows(NullPointerException.class, () -> StringSeq.of("a", none[0]));
        assertThrows(NullPointerException.class, () -> StringSeq.of("a").with(0, none[0]));
        assertThrows(NullPointerException.class, () -> StringSeq.of("a").append(none[0]));
    }

    /** Returns an element of a sequence through the common interface. */
    private Object boxed(Seq seq, int index) {
        return seq.value(index);
    }

    /** Asserts that two sequences are equal in both directions and agree on the hash code. */
    private void assertEqual(Seq expected, Seq actual) {
        assertEquals(expected, actual);
        assertEquals(actual, expected);
        assertEquals(expected.hashCode(), actual.hashCode());
    }
}
