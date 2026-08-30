/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2026 University of Twente
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
package nl.utwente.groove.test.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.Test;

import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.parse.FormatError;
import nl.utwente.groove.util.parse.Severity;

/**
 * Tests the generic context mechanism of {@link FormatError}: the collection,
 * flattening and typed retrieval of the opaque context parameters, and their
 * part in equality. The severity mechanism is tested separately in
 * {@link FormatErrorSeverityTest}.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5, 2026-08")
@NonNullByDefault
public class FormatErrorContextTest {
    /** Tests the collection, flattening and typed retrieval of context parameters. */
    @Test
    public void testContextCollection() {
        Object context = new Object();
        var error = new FormatError("error in %s", "object", context, 1, 2, null);
        // numbers are collected separately, nulls are dropped;
        // all other parameters (including strings) form the context
        assertEquals(List.of("object", context), List.copyOf(error.getContext()));
        assertEquals(List.of(1, 2), error.getNumbers());
        // arrays and collections are flattened
        var flattened = new FormatError("flat", new Object[] {context, List.of("nested")});
        assertEquals(List.of(context, "nested"), List.copyOf(flattened.getContext()));
        // duplicates are collapsed
        var duplicated = new FormatError("dup", context, context);
        assertEquals(List.of(context), List.copyOf(duplicated.getContext()));
        // typed retrieval filters the context
        assertEquals(List.of("nested"), flattened.getContext(String.class));
    }

    /** Tests the flattening of nested errors into context and numbers.
     * (The severity takeover of nested errors is tested in
     * {@link FormatErrorSeverityTest#testWrapping()}.) */
    @Test
    public void testNestedError() {
        Object context = new Object();
        var inner = new FormatError("inner", context, 3, Severity.WARNING);
        var outer = new FormatError("outer: %s", inner);
        assertEquals(List.of(context), List.copyOf(outer.getContext()));
        assertEquals(List.of(3), outer.getNumbers());
    }

    /** Tests that the context enters the equality test and the hash code,
     * by value rather than by the identity of the unmodifiable view. */
    @Test
    public void testEquality() {
        Object context = new Object();
        var error = new FormatError("msg", context);
        var equal = new FormatError("msg", context);
        assertEquals(error, equal);
        assertEquals(error.hashCode(), equal.hashCode());
        assertNotEquals(error, new FormatError("msg"));
    }
}
