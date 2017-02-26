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
package groove.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

/**
 * Collection of convenience methods to avoid {@link NoNonNull}-related warnings
 * of standard Java library methods.
 * @author Arend Rensink
 * @version $Revision $
 */
@NonNullByDefault
public class NoNonNull {
    /** Convenience method for {@link Collections#singleton(Object)}.
     * Avoids {@link NoNonNull}-warnings.
     */
    @SuppressWarnings("null")
    public static <T> Set<T> singleton(T o) {
        return Collections.singleton(o);
    }

    /** Convenience method for {@link Object#toString()}.
     * Avoids {@link NoNonNull}-warnings.
     */
    public static String toString(Object s) {
        String result = s.toString();
        assert result != null;
        return result;
    }

    /** Convenience method for {@link Collection#toArray(Object[])}.
     * Avoids {@link NoNonNull}-warnings.
     */
    @SuppressWarnings("null")
    public static <T> T[] toArray(Collection<T> source, T[] a) {
        return source.toArray(a);
    }

    /** Convenience method for {@link Collections#unmodifiableList(List)}.
     * Avoids {@link NoNonNull}-warnings.
     */
    @SuppressWarnings("null")
    public static <T> List<T> unmodifiableList(List<T> source) {
        return Collections.unmodifiableList(source);
    }

    /** Convenience method for {@link Iterator#next()}.
     * Avoids {@link NoNonNull}-warnings.
     */
    @SuppressWarnings("null")
    public static <T> T next(@Nullable Iterator<T> source) {
        return source.next();
    }

    /** Convenience method for {@link String#substring(int, int)}.
     * Avoids {@link NoNonNull}-warnings.
     */
    @SuppressWarnings("null")
    public static String substring(String source, int start, int end) {
        return source.substring(start, end);
    }

    /** Convenience method for {@link String#substring(int)}.
     * Avoids {@link NoNonNull}-warnings.
     */
    @SuppressWarnings("null")
    public static String substring(String source, int start) {
        return source.substring(start);
    }

    /** Convenience method for {@link String#trim()}.
     * Avoids {@link NoNonNull}-warnings.
     */
    @SuppressWarnings("null")
    public static final String trim(String string) {
        return (@NonNull String) string.trim();
    }

}
