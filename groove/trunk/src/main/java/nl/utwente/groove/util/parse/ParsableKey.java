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

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * Interface for keys with values that can be parsed from strings.
 * @author Arend Rensink
 */
@NonNullByDefault
public interface ParsableKey<V> extends Parser<V> {
    /** Key name, in camel case (starting with lowercase). */
    public String getName();

    /** Returns an explanation of this key. */
    public String getExplanation();

    /** Returns the internal parser for values of this key.
     * All parse methods are delegated to this parser.
     */
    public Parser<V> parser();

    @Override
    default @NonNull String getDescription() {
        return parser().getDescription();
    }

    @Override
    default V parse(String input) throws FormatException {
        return parser().parse(input);
    }

    @Override
    default <T extends V> String unparse(@NonNull T value) throws IllegalArgumentException {
        return parser().unparse(value);
    }

    @Override
    default @NonNull Class<? extends V> getValueType() {
        return parser().getValueType();
    }

    @Override
    default V getDefaultValue() throws UnsupportedOperationException {
        return parser().getDefaultValue();
    }
}
