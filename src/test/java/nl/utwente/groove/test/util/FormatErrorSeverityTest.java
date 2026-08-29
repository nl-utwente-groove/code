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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.Test;

import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.parse.FormatError;
import nl.utwente.groove.util.parse.FormatErrorSet;
import nl.utwente.groove.util.parse.FormatException;
import nl.utwente.groove.util.parse.Severity;

/**
 * Tests the severity level of {@link FormatError}s and its consequences
 * for {@link FormatErrorSet} (gh #885): only blocking ({@link Severity#ERROR})
 * messages make {@link FormatErrorSet#hasErrors()} true and
 * {@link FormatErrorSet#throwException()} throw.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Opus 5, 2026-08")
@NonNullByDefault
public class FormatErrorSeverityTest {
    /** The default severity is {@link Severity#ERROR}. */
    @Test
    public void testDefaultSeverity() {
        var error = new FormatError("some error");
        assertEquals(Severity.ERROR, error.getSeverity());
        assertTrue(error.isBlocking());
    }

    /** The severity can be set through the dedicated constructor
     * or as a constructor parameter. */
    @Test
    public void testExplicitSeverity() {
        var warning = new FormatError(Severity.WARNING, "some warning");
        assertEquals(Severity.WARNING, warning.getSeverity());
        assertFalse(warning.isBlocking());
        var info = new FormatError("some info", Severity.INFO);
        assertEquals(Severity.INFO, info.getSeverity());
        assertFalse(info.isBlocking());
    }

    /** The severity enters the equality test. */
    @Test
    public void testEquality() {
        var error = new FormatError("message");
        var warning = new FormatError(Severity.WARNING, "message");
        assertNotEquals(error, warning);
        assertEquals(warning, new FormatError(Severity.WARNING, "message"));
    }

    /** The severity survives cloning and extension. */
    @Test
    public void testCloneAndExtend() {
        var warning = new FormatError(Severity.WARNING, "some warning");
        assertEquals(Severity.WARNING, warning.clone().getSeverity());
        assertEquals(Severity.WARNING, warning.extend(1).getSeverity());
    }

    /** An error wrapping another error takes over the nested severity. */
    @Test
    public void testWrapping() {
        var warning = new FormatError(Severity.WARNING, "nested warning");
        var wrapped = new FormatError("In context: %s", warning);
        assertEquals(Severity.WARNING, wrapped.getSeverity());
        // an explicitly set severity wins over the nested one
        var overridden = new FormatError(Severity.ERROR, "In context: %s", warning);
        assertEquals(Severity.ERROR, overridden.getSeverity());
    }

    /** Only blocking errors make the set report errors or throw. */
    @Test
    public void testErrorSet() throws FormatException {
        var warningsOnly = new FormatErrorSet();
        warningsOnly.add(new FormatError(Severity.WARNING, "some warning"));
        assertFalse(warningsOnly.isEmpty());
        assertFalse(warningsOnly.hasErrors());
        assertEquals(Severity.WARNING, warningsOnly.getSeverity());
        warningsOnly.throwException(); // does not throw
        var mixed = new FormatErrorSet();
        mixed.add(new FormatError(Severity.WARNING, "some warning"));
        mixed.add("some error");
        assertTrue(mixed.hasErrors());
        assertEquals(Severity.ERROR, mixed.getSeverity());
        assertThrows(FormatException.class, () -> mixed.throwException());
        assertEquals(1, mixed.filter(Severity.WARNING).get().size());
        assertEquals(1, mixed.filter(Severity.ERROR).get().size());
        assertEquals(0, mixed.filter(Severity.INFO).get().size());
    }
}
