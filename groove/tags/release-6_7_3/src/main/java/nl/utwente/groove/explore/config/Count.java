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
import nl.utwente.groove.util.parse.Parser;

/**
 * Setting that determines whether exploration stops after having found a number of result states.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public final class Count extends Setting {
    /**
     * Creates a count on the basis of a given integer value.
     */
    public Count(Key kind, Integer count) {
        super(kind, count);
    }

    /**
     * Creates a count on the basis of a given integer value.
     */
    public Count(Key kind) {
        super(kind, Null.instance());
    }

    /**
     * Kind of result count.
     * @author Arend Rensink
     * @version $Revision$
     */
    public enum Key implements Setting.Key {
        /** Continue regardless of results found. */
        ALL("All", "Continue regardless of the number of results", ContentType.NULL),
        /** Halt after the first result. */
        ONE("One", "Halt after the first result", ContentType.NULL),
        /** User-defined count; 0 means unbounded. */
        COUNT("Value", "User-defined; 0 means unbounded", ContentType.INTEGER),;

        private Key(String name, String explanation, ContentType contentType) {
            this.name = name;
            this.explanation = explanation;
            this.contentType = contentType;
        }

        /** Returns the name of this search order. */
        @Override
        public String getName() {
            return this.name;
        }

        private final String name;

        @Override
        public String description() {
            return "";
        }

        @Override
        public Count createSetting(Object content) throws IllegalArgumentException {
            return switch (contentType()) {
            case NULL -> new Count(this);
            case INTEGER -> new Count(this, (Integer) content);
            default -> throw Exceptions.UNREACHABLE;
            };
        }

        @Override
        public String getExplanation() {
            return this.explanation;
        }

        private final String explanation;

        @Override
        public @NonNull ContentType contentType() {
            return this.contentType;
        }

        private final ContentType contentType;

        @Override
        public ContentParser parser() {
            var result = this.parser;
            if (result == null) {
                var innerParser = switch (this) {
                case ALL, ONE -> Null.Parser.instance();
                case COUNT -> Parser.natural;
                };
                this.parser = result = new ContentParser(this, innerParser);
            }
            return result;
        }

        private @Nullable ContentParser parser;
    }
}
