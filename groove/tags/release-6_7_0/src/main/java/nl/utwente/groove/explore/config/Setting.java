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

import java.util.Objects;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.match.plan.Hint;
import nl.utwente.groove.util.Exceptions;
import nl.utwente.groove.util.parse.ParsableKey;
import nl.utwente.groove.util.parse.Parser;
import nl.utwente.groove.verify.Formula;

/**
 * Supertype for all values that can be assigned to {@link ExploreKey}s.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public abstract class Setting {
    /** Constructs a setting with non-{@code null}  key and content. */
    protected Setting(Setting.Key key, Object content) {
        this.key = key;
        this.content = content;
        checkInvariant();
    }

    /** Returns the key of this setting. */
    public Key key() {
        return this.key;
    }

    private final Key key;

    /** Returns the value of this setting. */
    public Object content() {
        return this.content;
    }

    private final Object content;

    /** Helper method for subclasses to assert the required invariant for this entry.
     * @throws IllegalArgumentException if the entry's key and value type are incompatible.
     */
    protected void checkInvariant() throws IllegalArgumentException {
        assert key() != null : "Key should not be null";
        assert content() != null : String.format("Value for '%s' should not be null", key());
        if (!key().contentType().type().isInstance(content())) {
            throw Exceptions.illegalArg("Content type '%s' does not admit value '%s' of type %s",
                                        key(), content(), content().getClass());
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.content, this.key);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Setting other = (Setting) obj;
        return Objects.equals(this.content, other.content) && Objects.equals(this.key, other.key);
    }

    @Override
    public String toString() {
        return "Setting [key=" + this.key + ", content=" + this.content + "]";
    }

    /**
     * Supertype for {@link Setting} keys
     * @author Arend Rensink
     * @version $Revision$
     */
    static public interface Key extends ParsableKey<Setting> {
        /** Returns a description of the content expected for this setting key.
         * The description is a capitalised statement of a single expected value.
         */
        public String description();

        /** Returns the type of content that this key expects. */
        public ContentType contentType();

        /**
         * Creates an exploration value of this kind, and a given content.
         * @param content exploration content; should be compatible with this kind
         * @throws IllegalArgumentException if {@code content} does not satisfy {@link Parser#isValue}
         */
        public Setting createSetting(Object content) throws IllegalArgumentException;

        @Override
        abstract public ContentParser parser();
    }

    /** Exhaustive enumeration of all value types occurring for {@link Key} implementations. */
    public enum ContentType {
        /** No content (encoded by the singular {@link Null} instance). */
        NULL(Null.class),
        /** Integer value. */
        INTEGER(Integer.class),
        /** String value. */
        STRING(String.class),
        /** Logic formula. */
        FORMULA(Formula.class),
        /** Match hint. */
        HINT(Hint.class);

        private ContentType(Class<?> type) {
            this.type = type;
        }

        /** Returns the (java) type corresponding to this content type. */
        public Class<?> type() {
            return this.type;
        }

        private final Class<?> type;
    }

    /** Superclass for parsers of concrete {@link Key} subtypes. */
    static public class ContentParser extends Parser.Wrap<Setting> {
        /** Constructs a wrapped parser from a given inner parser, target type, and wrapping function. */
        @SuppressWarnings("unchecked")
        protected <C> ContentParser(Key key, Parser<C> inner) {
            super(inner, Setting.class, key::createSetting, e -> (C) e.content());
            this.key = key;
        }

        /** Returns the key of this parser. */
        public Key key() {
            return this.key;
        }

        private final Key key;
    }
}
