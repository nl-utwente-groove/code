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
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.match.plan.Hint;
import nl.utwente.groove.util.Exceptions;

/**
 * @author Arend Rensink
 * @version $Revision $
 */
@NonNullByDefault
public class Matcher extends Setting {
    private Matcher(Key key, Object content) {
        super(key, content);
    }

    /** The matching strategy. */
    public enum Key implements Setting.Key {
        /** Search plan-based matching. */
        PLAN("plan", "Match hint", "Search plan-based matching", ContentType.HINT),
        /** RETE-based incremental matching. */
        RETE("rete", "", "Incremental (rete-based) matching", ContentType.NULL),;

        private Key(String name, String contentName, String explanation, ContentType contentType) {
            this.name = name;
            this.description = contentName;
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
        public String getExplanation() {
            return this.explanation;
        }

        private final String explanation;

        @Override
        public ContentType contentType() {
            return this.contentType;
        }

        private final ContentType contentType;

        @Override
        public ContentParser parser() {
            var result = this.parser;
            if (result == null) {
                var inner = switch (this) {
                case PLAN -> Hint.Parser.instance();
                case RETE -> Null.Parser.instance();
                };
                this.parser = result = new ContentParser(this, inner);
            }
            return result;
        }

        private @Nullable ContentParser parser;

        @Override
        public Matcher createSetting(Object content) throws IllegalArgumentException {
            if (!isValue(content)) {
                throw Exceptions.illegalArg("'%s' is not a valid value for '%s'", content, this);
            }
            return new Matcher(this, content);
        }
    }
}
