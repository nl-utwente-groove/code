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

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.util.Exceptions;
import nl.utwente.groove.util.parse.StringParser;

/**
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public class Acceptor extends Setting {
    /** Creates an acceptor with null condition and property. */
    private Acceptor(Key key) {
        this(key, Null.instance());
    }

    private Acceptor(Key key, Object content) {
        super(key, content);
    }

    /** Key for an {@link Acceptor} setting. */
    static public enum Key implements Setting.Key {
        /** Final states. */
        FINAL("final", "", "Final states are results", ContentType.NULL),
        /** States satisfying a graph condition. */
        CONDITION("condition", "Property name", "Any state satisfying a given property",
            ContentType.STRING),
        /** States satisfying a propositional formula. */
        FORMULA("formula", "Property formula", "Any state satisfying a propositional formula",
            ContentType.STRING),
        /** All states. */
        ANY("any", "", "All states are results", ContentType.NULL),
        /** No states. */
        NONE("none", "", "No state is considered a result", ContentType.NULL),;

        private Key(String name, String description, String explanation, ContentType contentType) {
            this.name = name;
            this.description = description;
            this.explanation = explanation;
            this.contentType = contentType;
        }

        @Override
        public String getName() {
            return this.name;
        }

        private final String name;

        @Override
        public String description() {
            return this.description;
        }

        private final String description;

        @Override
        public @NonNull ContentType contentType() {
            return this.contentType;
        }

        private final ContentType contentType;

        @Override
        public Acceptor createSetting(Object content) throws UnsupportedOperationException {
            return new Acceptor(this, content);
        }

        @Override
        public String getExplanation() {
            return this.explanation;
        }

        private final String explanation;

        @Override
        public ContentParser parser() {
            var result = this.parser;
            if (result == null) {
                var inner = switch (contentType()) {
                case NULL -> Null.Parser.instance();
                case STRING -> StringParser.identity();
                default -> throw Exceptions.UNREACHABLE;
                };
                this.parser = result = new ContentParser(this, inner);
            }
            return result;
        }

        private @Nullable ContentParser parser;
    }
}
