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
 * @author Arend Rensink
 * @version $Revision $
 */
@NonNullByDefault
public final class Traversal extends Setting {
    /**
     * Setting determining the order in which states are explored.
     * @param kind the kind of traversal
     */
    public Traversal(Kind kind) {
        super(kind, Null.instance());
    }

    /**
     * Kind of traversal strategies.
     * @author Arend Rensink
     * @version $Revision $
     */
    public enum Kind implements Setting.Key {
        /** Depth-first search. */
        DEPTH_FIRST("DFS", "Depth-first search"),
        /** Breadth-first search. */
        BREADTH_FIRST("BFS", "Breadth-first search"),
        /** Linear search. */
        LINEAR("Linear", "Linear search: never backtracks"),
        /** Best-first search, driven by some heuristic. */
        BEST_FIRST("Heuristic", "Heuristic search according to a given function"),

        /** LTL model checking, driven by some property to be checked. */
        //LTL("LTL", "LTL model checking of a given formula"),
        ;

        private Kind(String name, String explanation) {
            this.name = name;
            this.explanation = explanation;
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
        public Traversal createSetting(Object content) throws IllegalArgumentException {
            if (content != Null.instance()) {
                throw Exceptions.illegalArg("No setting exists for algebra '%s' and content '%s'",
                                            this, content);
            }
            return new Traversal(this);
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
