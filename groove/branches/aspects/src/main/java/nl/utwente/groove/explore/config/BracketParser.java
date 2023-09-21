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
package nl.utwente.groove.explore.config;

import nl.utwente.groove.util.parse.FormatException;
import nl.utwente.groove.util.parse.Parser;

/**
 * Derived parser that wraps another parser inside brackets of some kind
 * @author Arend Rensink
 * @version $Revision $
 */
public class BracketParser<V> extends Parser.AParser<V> {
    /**
     * Creates a parser with '(' and ')' as brackets.
     * @param inner the parser for the content between the brackets
     */
    public BracketParser(Parser<V> inner) {
        this(inner, '(', ')');
    }

    /**
     * Creates a parser with a given start and end symbol.
     * @param inner the parser for the content between the brackets
     * @param start the opening bracket
     * @param end the closing bracket
     */
    public BracketParser(Parser<V> inner, char start, char end) {
        super(String.format("%s between '%s' and '%s'", inner.getDescription(), start, end),
              inner.getDefaultValue());
        this.inner = inner;
        this.start = start;
        this.end = end;
    }

    private final Parser<V> inner;
    private final char start;
    private final char end;

    @Override
    public V parse(String input) throws FormatException {
        if (input.length() == 0) {
            return getDefaultValue();
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
    public <U extends V> String unparse(U value) {
        String result = this.inner.unparse(value);
        return result.length() == 0
            ? result
            : "" + this.start + result + this.end;
    }

    @Override
    public boolean isValue(Object value) {
        return this.inner.isValue(value);
    }
}
