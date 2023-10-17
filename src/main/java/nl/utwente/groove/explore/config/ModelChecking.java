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

import java.util.Optional;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.util.Exceptions;
import nl.utwente.groove.util.parse.Parser;
import nl.utwente.groove.verify.FormulaParser;
import nl.utwente.groove.verify.Logic;

/**
 * @author Arend Rensink
 * @version $Revision $
 */
@NonNullByDefault
public final class ModelChecking extends Setting {
    /**
     * Setting determining the order in which states are explored.
     * @param kind the kind of traversal
     */
    public ModelChecking(Kind kind, Object content) {
        super(kind, content);
    }

    /**
     * Kind of model checking strategies.
     * @author Arend Rensink
     * @version $Revision $
     */
    public enum Kind implements Setting.Key {
        /** No model checking. */
        NONE("None", "No model checking"),
        /** LTL model checking. */
        LTL_CHECK("LTL", "Linear Temporal Logic checking", Logic.LTL),
        /** CTL model checking. */
        CTL_CHECK("CTL", "Computation Tree Logic checking", Logic.CTL),;

        private Kind(String name, String explanation) {
            this(name, explanation, Optional.empty());
        }

        private Kind(String name, String explanation, Logic logic) {
            this(name, explanation, Optional.of(logic));
        }

        private Kind(String name, String explanation, Optional<Logic> logic) {
            this.name = name;
            this.explanation = explanation;
            this.logic = logic;
            this.contentType = logic.isPresent()
                ? ContentType.FORMULA
                : ContentType.NULL;
        }

        /** Returns the name of this search order. */
        @Override
        public String getName() {
            return this.name;
        }

        private final String name;

        @Override
        public @NonNull String description() {
            return getName() + " formula";
        }

        @Override
        public ContentType contentType() {
            return this.contentType;
        }

        private final ContentType contentType;

        /** Returns the logic corresponding to this checking kind, if any.
         */
        public Optional<Logic> getLogic() {
            return this.logic;
        }

        private final Optional<Logic> logic;

        @Override
        public ModelChecking createSetting(Object content) {
            return new ModelChecking(this, content);
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
                Parser<?> inner = switch (contentType()) {
                case FORMULA -> FormulaParser.instance();
                case NULL -> Null.Parser.instance();
                default -> throw Exceptions.UNREACHABLE;
                };
                this.parser = result = new ContentParser(this, inner);
            }
            return result;
        }

        private @Nullable ContentParser parser;
    }
}
