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
package nl.utwente.groove.util;

import java.util.Optional;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

/**
 * Collection of convenience methods to avoid {@link NoNonNull}-related warnings
 * of standard Java library methods.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public class NoNonNull {
    /** Convenience method for {@link Object#toString()}.
     * Avoids {@link NoNonNull}-warnings.
     */
    public static String toString(Object s) {
        String result = s.toString();
        assert result != null;
        return result;
    }

    /** Mimics the behaviour of {@link Optional#ofNullable(Object)},
     * but circumvents the spurious {@code null} check of the argument
     */
    static public <T> Optional<T> ofNullable(@Nullable T value) {
        return value == null
            ? Optional.empty()
            : Optional.of(value);
    }

    /** Mimics the behaviour of {@link Optional#orElse(Object)},
     * but circumvents the spurious {@code null} check of the argument
     */
    static public <T> @Nullable T orElse(Optional<T> value, @Nullable T alt) {
        return value.orElse(alt);
    }

    /** Returns a given value if it is {@code null}, or an alternative (non-{@code null}) value otherwise. */
    static public <T> @NonNull T orElse(@Nullable T value, @NonNull T alt) {
        return value == null
            ? alt
            : value;
    }
}
