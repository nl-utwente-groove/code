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

import nl.utwente.groove.algebra.AlgebraFamily;
import nl.utwente.groove.util.Exceptions;

/**
 * @author Arend Rensink
 * @version $Revision $
 */
@NonNullByDefault
public final class Algebra extends Setting {
    /**
     * Setting determining the order in which states are explored.
     * @param kind the kind of traversal
     */
    public Algebra(Kind kind) {
        super(kind, Null.instance());
    }

    /**
     * Kind of traversal strategies.
     * @author Arend Rensink
     * @version $Revision $
     */
    public enum Kind implements Setting.Key {
        /** Depth-first search. */
        DEFAULT(AlgebraFamily.DEFAULT),
        /** Breadth-first search. */
        BIG(AlgebraFamily.BIG),
        /** Linear search. */
        POINT(AlgebraFamily.POINT),
        /** Best-first search, driven by some heuristic. */
        TERM(AlgebraFamily.TERM),;

        private Kind(AlgebraFamily family) {
            this.family = family;
        }

        /** Returns the algebra family. */
        public AlgebraFamily getFamily() {
            return this.family;
        }

        private final AlgebraFamily family;

        @Override
        public String getName() {
            return getFamily().getName();
        }

        @Override
        public String description() {
            return "";
        }

        @Override
        public Algebra createSetting(Object content) throws IllegalArgumentException {
            if (content != Null.instance()) {
                throw Exceptions.illegalArg("No setting exists for algebra '%s' and content '%s'",
                                            this, content);
            }
            return new Algebra(this);
        }

        @Override
        public String getExplanation() {
            return getFamily().getExplanation();
        }

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
