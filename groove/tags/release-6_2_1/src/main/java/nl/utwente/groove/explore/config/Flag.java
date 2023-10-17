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

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.util.Exceptions;

/**
 * Setting consisting of a boolean value.
 * @author Arend Rensink
 * @version $Revision $
 */
@NonNullByDefault
public final class Flag extends Setting {
    /**
     * Setting determining the order in which states are explored.
     * @param kind the kind of traversal
     */
    public Flag(Kind kind) {
        super(kind, Null.instance());
    }

    /**
     * Available values.
     * @author Arend Rensink
     * @version $Revision $
     */
    public enum Kind implements Setting.Key {
        /** Depth-first search. */
        TRUE("Flag is set"),
        /** Breadth-first search. */
        FALSE("Flag is not set"),;

        private Kind(String explanation) {
            this.explanation = explanation;
        }

        /** Returns the name of this search order. */
        @Override
        public String getName() {
            return name();
        }

        @Override
        public String description() {
            return "";
        }

        @Override
        public Flag createSetting(Object content) throws IllegalArgumentException {
            if (content != Null.instance()) {
                throw Exceptions.illegalArg("No setting exists for algebra '%s' and content '%s'",
                                            this, content);
            }
            return new Flag(this);
        }

        @Override
        public String getExplanation() {
            return this.explanation;
        }

        private final String explanation;

        @Override
        public @NonNull ContentType contentType() {
            return ContentType.NULL;
        }

        @Override
        public ContentParser parser() {
            var result = this.parser;
            if (result == null) {
                this.parser = result = new ContentParser(this, Null.Parser.instance());
            }
            return result;
        }

        private @Nullable ContentParser parser;
    }
}
