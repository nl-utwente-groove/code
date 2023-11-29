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

/**
 * Parser for strings; either passes through the string unchanged, or trims whitespace.
 * @author Arend Rensink
 * @version $Revision$
 */
public class StringParser extends Parser.AParser<String> {
    private StringParser(boolean trim) {
        super("Any string", "");
        this.trim = trim;
    }

    private final boolean trim;

    @Override
    public boolean accepts(String text) {
        return true;
    }

    @Override
    public String parse(String input) {
        return this.trim
            ? input.trim()
            : input;
    }

    @Override
    public String unparse(String value) {
        return value;
    }

    @Override
    public boolean isValue(Object value) {
        return value instanceof String;
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
