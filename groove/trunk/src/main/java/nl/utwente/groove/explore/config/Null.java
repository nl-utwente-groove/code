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
package nl.utwente.groove.explore.config;

import nl.utwente.groove.util.parse.FormatException;

/**
 * Exploration content that can only have a {@code null} instance.
 */
public class Null {
    /**
     * Private constructor that is never invoked
     */
    private Null() {
        // empty by design
    }

    /** Returns the singleton instance of this class. */
    public static Null instance() {
        return instance;
    }

    /** The singleton instance of this class. */

    private static final Null instance = new Null();

    /** Parser the {@link Null} type.
     * Only accepts {@code null} and the empty string.
     */
    static public class Parser extends nl.utwente.groove.util.parse.Parser.AParser<Null> {
        /** Instantiates this parser for a given class. */
        private Parser() {
            super("The empty string", Null.instance());
        }

        @Override
        public Null parse(String input) throws FormatException {
            if (!input.isEmpty()) {
                throw new FormatException("Expected empty string rather than %s", input);
            }
            return Null.instance();
        }

        @Override
        public <V extends Null> String unparse(V value) {
            return "";
        }

        /** Returns the singleton instance of this parser. */
        public static Parser instance() {
            return instance;
        }

        static final private Parser instance = new Parser();
    }
}
