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

import org.eclipse.jdt.annotation.NonNullByDefault;

import nl.utwente.groove.util.Exceptions;
import nl.utwente.groove.util.parse.ParsableKey;
import nl.utwente.groove.util.parse.Parser;
import nl.utwente.groove.util.parse.StringParser;

/**
 * Value assigned to an {@link ExploreKey}: a kind (one of the enumerated
 * feature values of that key) plus optional content (e.g., a bound or a name).
 * The content is never {@code null}; kinds without content carry the singleton
 * {@link Null} instance.
 * @param kind the feature value; determines the required content type
 * @param content the content; must be an instance of the kind's content type
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public record Setting(Kind kind, Object content) {
    /** Checks that the content is admissible for the kind. */
    public Setting {
        if (!kind.contentType().type().isInstance(content)) {
            throw Exceptions.illegalArg("Kind '%s' does not admit content '%s' of type %s", kind,
                                        content, content.getClass());
        }
    }

    /**
     * Supertype of the per-key enumerations of feature values.
     * Implementations are enums such as {@link NextState} or {@link Goal}.
     * @author Arend Rensink
     * @version $Revision$
     */
    public interface Kind extends ParsableKey<Setting> {
        /** Returns the type of content that settings of this kind carry. */
        public ContentType contentType();

        /**
         * Creates a setting of this kind with given content.
         * @throws IllegalArgumentException if {@code content} is not admissible for this kind
         */
        default public Setting createSetting(Object content) throws IllegalArgumentException {
            return new Setting(this, content);
        }

        /** Creates a setting of this kind with the default content for its content type. */
        default public Setting createSetting() {
            return createSetting(contentType().parser().getDefaultValue());
        }

        @Override
        default public ContentParser parser() {
            return new ContentParser(this, contentType().parser());
        }
    }

    /** Exhaustive enumeration of the content types occurring in {@link Kind} implementations. */
    public enum ContentType {
        /** No content (encoded by the singleton {@link Null} instance). */
        NULL(Null.class, Null.Parser.instance()),
        /** Natural number. */
        INTEGER(Integer.class, Parser.natural),
        /** String value (a name or formula, resolved against the grammar when used). */
        STRING(String.class, StringParser.identity()),
        /** Exploration limit: a maximum with an optional increment. */
        LIMIT(Bound.Limit.class, Bound.Limit.Parser.instance());

        private ContentType(Class<?> type, Parser<?> parser) {
            this.type = type;
            this.parser = parser;
        }

        /** Returns the (Java) type corresponding to this content type. */
        public Class<?> type() {
            return this.type;
        }

        private final Class<?> type;

        /** Returns the parser for content values of this type. */
        public Parser<?> parser() {
            return this.parser;
        }

        private final Parser<?> parser;
    }

    /** Parser from content strings to settings of a fixed kind. */
    public static class ContentParser extends Parser.Wrap<Setting> {
        /** Constructs a parser wrapping a given kind's content parser. */
        @SuppressWarnings("unchecked")
        public <C> ContentParser(Kind kind, Parser<C> inner) {
            super(inner, Setting.class, kind::createSetting, s -> (C) s.content());
            this.kind = kind;
        }

        /** Returns the setting kind of this parser. */
        public Kind kind() {
            return this.kind;
        }

        private final Kind kind;
    }
}
