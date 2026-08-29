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
package nl.utwente.groove.util.parse;

import org.eclipse.jdt.annotation.NonNullByDefault;

import nl.utwente.groove.util.AIGenerated;

/**
 * Severity level of a {@link FormatError}.
 * The constants are declared in increasing order of severity, so that the
 * natural ordering determines the most severe of a set of levels.
 * Only {@link #ERROR} is blocking: it keeps the object the message is about
 * from being used, and makes {@link FormatErrorSet#throwException()} throw.
 * Messages of the other levels are diagnostics travelling with an object
 * that is perfectly usable.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
@AIGenerated("Claude Opus 5, 2026-08")
public enum Severity {
    /** Purely informative message; nothing is wrong. */
    INFO("info"),
    /** Message signalling a suspicious but legal situation. */
    WARNING("warning"),
    /** Message signalling a defect that keeps the object from being used. */
    ERROR("error");

    private Severity(String text) {
        this.text = text;
        this.capText = Character.toUpperCase(text.charAt(0)) + text.substring(1);
    }

    /** Returns a lower-case one-word description of this severity level. */
    public String getText() {
        return this.text;
    }

    /** Lower-case one-word description of this severity level. */
    private final String text;

    /** Returns a capitalised one-word description of this severity level. */
    public String getCapText() {
        return this.capText;
    }

    /** Capitalised one-word description of this severity level. */
    private final String capText;

    /** Indicates if messages of this severity level keep the object they
     * are about from being used. Only holds for {@link #ERROR}.
     */
    public boolean isBlocking() {
        return this == ERROR;
    }

    /** Returns the more severe of two severity levels. */
    public static Severity max(Severity one, Severity two) {
        return one.compareTo(two) >= 0
            ? one
            : two;
    }
}
