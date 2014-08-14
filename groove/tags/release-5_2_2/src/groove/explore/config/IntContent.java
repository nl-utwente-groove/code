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

import groove.util.parse.Parser;

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
    public int value() {
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
    public static final Parser<IntContent> NAT_PARSER = new IntParser(false);
    /** Parser for arbitrary integer content. */
    public static final Parser<IntContent> INT_PARSER = new IntParser(true);

    private static class IntParser extends Parser.AbstractIntParser<IntContent> {
        IntParser(boolean neg) {
            super(IntContent.class, 0, neg);
        }

        @Override
        protected IntContent createContent(int value) {
            return new IntContent(value);
        }

        @Override
        protected int extractValue(IntContent content) {
            return content.value();
        }

        @Override
        public boolean isValue(Object value) {
            return value instanceof IntContent
                && (allowsNeg() || ((IntContent) value).value() >= 0);
        }

    }
}
