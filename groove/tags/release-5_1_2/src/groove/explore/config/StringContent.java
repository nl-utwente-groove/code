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
 * @author Arend Rensink
 * @version $Revision $
 */
public class StringContent implements SettingContent {
    /**
     * Constructs a new content value.
     */
    public StringContent(String value) {
        assert value != null;
        this.value = value;
    }

    /** Returns the value wrapped in this content object. */
    public String value() {
        return this.value;
    }

    private final String value;

    @Override
    public String toString() {
        return "String[" + this.value + "]";
    }

    @Override
    public int hashCode() {
        return this.value.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        StringContent other = (StringContent) obj;
        return this.value.equals(other.value);
    }

    /** Returns a parser wrapping strings into {@link StringContent} objects. */
    public final static Parser<StringContent> PARSER =
        new Parser.AbstractStringParser<StringContent>(StringContent.class, "", true) {
            @Override
            public boolean isValue(Object value) {
                return value instanceof StringContent;
            }

            @Override
            protected StringContent createContent(String value) {
                return new StringContent(value);
            }

            @Override
            protected String extractValue(StringContent content) {
                return content.value();
            }
        };
}
