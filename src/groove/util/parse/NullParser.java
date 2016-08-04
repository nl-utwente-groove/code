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
package groove.util.parse;

import org.eclipse.jdt.annotation.Nullable;

/** Parser for an arbitrary type, only recognising the {@code null} value. */
public class NullParser<@Nullable O> implements Parser<@Nullable O> {
    /** Instantiates this parser for a given class. */
    private NullParser(Class<? extends O> valueType) {
        this.valueType = valueType;
    }

    @Override
    public String getDescription() {
        return "The empty string";
    }

    @Override
    public O parse(String input) throws FormatException {
        if (input != null && !input.isEmpty()) {
            throw new FormatException("Expected null value rather than %s", input);
        }
        return null;
    }

    @Override
    public String toParsableString(Object value) {
        return "";
    }

    @Override
    public Class<? extends O> getValueType() {
        return this.valueType;
    }

    private final Class<? extends O> valueType;

    @Override
    public boolean isValue(Object value) {
        return value == null;
    }

    @Override
    public O getDefaultValue() {
        return null;
    }

    /** Returns a {@link groove.util.parse.NullParser} instance for a given type. */
    public static <O> NullParser<@Nullable O> instance(Class<? extends O> type) {
        return new NullParser<>(type);
    }
}