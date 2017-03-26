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
package groove.explore.config;

import groove.util.parse.FormatException;
import groove.util.parse.Parser;

/**
 * Derived parser that wraps another parser inside brackets of some kind
 * @author Arend Rensink
 * @version $Revision $
 */
public class BracketParser<V> implements Parser<V> {
    /**
     * Creates a parser with '(' and ')' as brackets.
     * @param inner the parser for the content between the brackets
     * @param allowsEmpty if {@code true}, the empty string is allowed;
     * it will be passed on to the inner parser to get the default value
     */
    public BracketParser(Parser<? extends V> inner, boolean allowsEmpty) {
        this(inner, '(', ')', allowsEmpty);
    }

    /**
     * Creates a parser with a given start and end symbol.
     * @param inner the parser for the content between the brackets
     * @param start the opening bracket
     * @param end the closing bracket
     * @param allowsEmpty if {@code true}, the empty string is allowed;
     * it will be passed on to the inner parser to get the default value
     */
    public BracketParser(Parser<? extends V> inner, char start, char end, boolean allowsEmpty) {
        this.inner = inner;
        this.start = start;
        this.end = end;
        this.allowsEmpty = allowsEmpty;
    }

    private final Parser<? extends V> inner;
    private final char start;
    private final char end;
    private final boolean allowsEmpty;

    @Override
    public String getDescription() {
        return this.inner.getDescription() + " between " + this.start + " and " + this.end;
    }

    @Override
    public V parse(String input) throws FormatException {
        if (input == null || input.length() == 0) {
            if (this.allowsEmpty) {
                return this.inner.parse(input);
            } else {
                throw new FormatException("Empty string not allowed");
            }
        }
        if (input.charAt(0) != this.start) {
            throw new FormatException("Expected input '%s' to start with '%s'", input, this.start);
        }
        int last = input.length() - 1;
        if (input.charAt(last) != this.end) {
            throw new FormatException("Expected input '%s' to end with '%s'", input, this.end);
        }
        return this.inner.parse(input.substring(1, last));
    }

    @Override
    public String toParsableString(Object value) {
        String result = this.inner.toParsableString(value);
        return result.length() == 0 ? result : "" + this.start + result + this.end;
    }

    @Override
    public Class<? extends V> getValueType() {
        return this.inner.getValueType();
    }

    @Override
    public boolean isValue(Object value) {
        return this.inner.isValue(value);
    }

    @Override
    public V getDefaultValue() {
        return this.inner.getDefaultValue();
    }

    @Override
    public String getDefaultString() {
        String result = this.inner.getDefaultString();
        return result.length() == 0 ? result : "" + this.start + result + this.end;
    }

    @Override
    public boolean isDefault(Object value) {
        return this.inner.isDefault(value);
    }
}
