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
package nl.utwente.groove.io.external.format.ecore;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.Exceptions;

/**
 * The named fields of a composite entry value of the Ecore mapping vocabulary:
 * space-separated {@code field=value} pairs, order-free, every field occurring
 * at most once. The composite shape is what keeps the vocabulary small: a
 * field can be added without touching any existing file, and retired by
 * accepting and ignoring it, whereas a choice key per field would cost a key
 * form and a line apiece.
 * @param map the fields of the value, in order of occurrence
 * @author Arend Rensink
 */
@AIGenerated("Claude Opus 5, 2026-09")
@NonNullByDefault
public record EcoreFields(Map<String,@Nullable String> map) {
    /** Returns the value of a given field, or {@code null} if the field is unset. */
    public @Nullable String get(String field) {
        return this.map.get(field);
    }

    /**
     * Splits a composite value into its named fields.
     * @param value the value to be split; may be empty, in which case no field is set
     * @param allowed the admissible field names
     * @return the parsed fields, or an error message completing the sentence
     * "Value 'v' of 'key' ..."
     */
    public static Parsed<EcoreFields> parse(String value, List<String> allowed) {
        Map<String,@Nullable String> result = new LinkedHashMap<>();
        for (String pair : value.trim().split("\\s+")) {
            if (pair.isEmpty()) {
                // the split of an empty value yields a single empty element
                continue;
            }
            int equals = pair.indexOf(ASSIGN);
            if (equals <= 0 || equals == pair.length() - 1) {
                return Parsed
                    .error("has malformed field '%s'; expected 'name%cvalue'", pair, ASSIGN);
            }
            String field = pair.substring(0, equals);
            if (!allowed.contains(field)) {
                return Parsed.error("has unknown field '%s'; expected one of %s", field, allowed);
            }
            if (result.containsKey(field)) {
                return Parsed.error("repeats field '%s'", field);
            }
            result.put(field, pair.substring(equals + 1));
        }
        return Parsed.of(new EcoreFields(result));
    }

    /** Appends a {@code field=value} pair to a composite value under
     * construction, unless the value is {@code null}. */
    public static void append(StringBuilder result, String field, @Nullable Object value) {
        if (value == null) {
            return;
        }
        if (result.length() > 0) {
            result.append(' ');
        }
        result.append(field).append(ASSIGN).append(value);
    }

    /** Separator between a field name and its value. */
    private static final char ASSIGN = '=';

    /**
     * Outcome of parsing a composite entry value: either the parsed value or
     * an error message. Exactly one of the two components is non-{@code null}.
     * @param <T> the type of the parsed value
     * @param value the parsed value, or {@code null} if parsing failed
     * @param error the error message, or {@code null} if parsing succeeded
     */
    public static record Parsed<T>(@Nullable T value, @Nullable String error) {
        /** Returns the parsed value, which must be present. */
        public T getValue() {
            var result = this.value;
            if (result == null) {
                throw Exceptions.illegalState("Unparsed value: %s", this.error);
            }
            return result;
        }

        /** Returns this failed outcome, retyped for another value type. */
        public <U> Parsed<U> retype() {
            var error = this.error;
            if (error == null) {
                throw Exceptions.illegalState("Successful outcome cannot be retyped");
            }
            return new Parsed<>(null, error);
        }

        /** Creates a successful outcome. */
        public static <T> Parsed<T> of(T value) {
            return new Parsed<>(value, null);
        }

        /** Creates a failed outcome with a formatted error message. */
        public static <T> Parsed<T> error(String message, Object... args) {
            return new Parsed<>(null, String.format(message, args));
        }
    }
}
