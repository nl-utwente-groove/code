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
 * Setting content wrapping an integer number.
 * @author Arend Rensink
 * @version $Revision $
 */
public class IntContent implements SettingContent {
    /**
     * Creates a wrapper for a given value.
     */
    public IntContent(int value) {
        this.value = value;
    }

    /** Returns the value wrapped into this content. */
    public int getValue() {
        return this.value;
    }

    private final int value;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.value;
        return result;
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
        IntContent other = (IntContent) obj;
        if (this.value != other.value) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "" + this.value;
    }

    /** Parser for non-negative integer content. */
    public static final Parser<IntContent> NAT_PARSER = new IntParser(Parser.natural);
    /** Parser for arbitrary integer content. */
    public static final Parser<IntContent> INT_PARSER = new IntParser(Parser.integer);

    private static class IntParser implements Parser<IntContent> {
        IntParser(groove.util.Parser.IntParser inner) {
            this.inner = inner;
            this.defaultValue = new IntContent(inner.getDefaultValue());
        }

        private final groove.util.Parser.IntParser inner;

        @Override
        public String getDescription(boolean uppercase) {
            return this.inner.getDescription(uppercase);
        }

        @Override
        public boolean accepts(String text) {
            return this.inner.accepts(text);
        }

        @Override
        public IntContent parse(String text) {
            return new IntContent(this.inner.parse(text));
        }

        @Override
        public String toParsableString(Object value) {
            return this.inner.toParsableString(((IntContent) value).getValue());
        }

        @Override
        public boolean isValue(Object value) {
            return value instanceof IntContent
                && this.inner.isValue(((IntContent) value).getValue());
        }

        @Override
        public IntContent getDefaultValue() {
            return this.defaultValue;
        }

        private final IntContent defaultValue;

        @Override
        public String getDefaultString() {
            return this.inner.getDefaultString();
        }

        @Override
        public boolean isDefault(Object value) {
            return value instanceof IntContent
                && this.inner.isDefault(((IntContent) value).getValue());
        }
    }
}
