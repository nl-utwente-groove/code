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
package groove.explore.config;

import groove.util.Parser;

/**
 * Exploration content that can only have a {@code null} instance.
 */
public class NullContent implements SettingContent {
    /**
     * Private constructor that is never invoked
     */
    private NullContent() {
        assert false;
    }

    /** Parser for {@link NullContent}. */
    public static Parser<NullContent> PARSER = new Parser<NullContent>() {
        @Override
        public String getDescription(boolean uppercase) {
            return uppercase ? "The empty string" : "the empty string";
        }

        @Override
        public boolean accepts(String text) {
            return text == null || text.length() == 0;
        }

        @Override
        public NullContent parse(String text) {
            return null;
        }

        @Override
        public String toParsableString(Object value) {
            return "";
        }

        @Override
        public boolean isValue(Object value) {
            return value == null;
        }

        @Override
        public NullContent getDefaultValue() {
            return null;
        }

        @Override
        public String getDefaultString() {
            return "";
        }

        @Override
        public boolean isDefault(Object value) {
            return value == null;
        }
    };
}
