/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2023
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
package nl.utwente.groove.util;

import nl.utwente.groove.util.parse.FormatException;

/**
 * Interface for objects that know a <i>fixing phase</i> before they can be
 * used. The fixing phase consists of method calls to build the object; after
 * this has finished, the object is fixed, meaning it should not be modified any
 * more.
 * @author Arend Rensink
 * @version $Revision$
 */
public interface Fixable {
    /**
     * Sets the object to fixed.
     * @return {@code true} if the fixed state was changed as a result of this call
     * @throws FormatException if the object is found to be inconsistent in some
     *         way.
     */
    boolean setFixed() throws FormatException;

    /**
     * Indicates if the object is fixed, i.e., {@link #setFixed()} has been
     * called.
     */
    boolean isFixed();

    /**
     * Tests that this object is still modifiable, i.e., that {@link #setFixed()}
     * has not been called yet.
     * <p>
     * This is the guard to put at the start of every mutator. Modifying an
     * object after it has been fixed is a <i>caller</i> error against a
     * published contract, and the resulting corruption tends to surface far
     * from its cause, so this throws rather than asserting and hence also
     * holds when assertions are disabled.
     * <p>
     * The read-side counterpart is deliberately <em>not</em> a method:
     * reading a value before it has been established is an implementation
     * error, for which {@code assert isFixed()} (or an asserting accessor)
     * is the better instrument. It costs nothing at runtime, and unlike a
     * method call it lets the null analysis conclude that a late-initialised
     * field is non-{@code null}.
     * @throws IllegalStateException if this object is already fixed
     */
    default void testMutable() {
        if (isFixed()) {
            throw Exceptions.illegalState("Operation not allowed: object is already fixed");
        }
    }

    /**
     * Test the fixedness of this object. Throws an exception if the fixedness
     * does not correspond to a given value.
     * @param fixed if <code>true</code>, the object is expected to be fixed;
     *        otherwise, it is expected to be unfixed.
     * @throws IllegalStateException if the fixedness of the object does not
     *         equal <code>fixed</code>, i.e., if
     *         <code>isFixed() != fixed</code>.
     * @deprecated The boolean argument is a compile-time constant at every
     *             call site, and the two cases are unrelated: use
     *             {@link #testMutable()} to guard a mutator, and
     *             {@code assert isFixed()} to guard a read.
     */
    @Deprecated
    default void testFixed(boolean fixed) {
        if (fixed != isFixed()) {
            throw Exceptions.illegalState("Expected fixed = %b", fixed);
        }
    }
}
