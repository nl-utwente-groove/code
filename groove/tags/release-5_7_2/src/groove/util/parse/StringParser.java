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

/**
 * Parser for strings; either passes through the string unchanged, or trims whitespace.
 * @author Arend Rensink
 * @version $Revision $
 */
public class StringParser extends Parser.AbstractStringParser<String> {
    private StringParser(boolean trim) {
        super(String.class, trim);
    }

    @Override
    protected String createContent(String value) {
        return value;
    }

    @Override
    protected String extractValue(String content) {
        return content;
    }

    @Override
    public boolean isValue(Object value) {
        return value == null || value instanceof String;
    }

    /** Returns the singleton trimming string parser. */
    public static StringParser trim() {
        if (TRIM == null) {
            TRIM = new StringParser(true);
        }
        return TRIM;
    }

    /** Trimming string parser. */
    private static StringParser TRIM;

    /** Returns the singleton identity string parser. */
    public static StringParser identity() {
        if (IDENTITY == null) {
            IDENTITY = new StringParser(false);
        }
        return IDENTITY;
    }

    /** Identity string parser. */
    private static StringParser IDENTITY;
}
