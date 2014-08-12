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
package groove.util.line;

import groove.util.line.Line.ColorType;
import groove.util.line.Line.Style;

import java.awt.Color;

/**
 * String renderer for lines
 * @author rensink
 * @version $Revision $
 */
public class StringFormat extends LineFormat<StringFormat.Builder> {
    @Override
    public Builder applyColored(ColorType type, Color color, Builder subline) {
        return subline;
    }

    @Override
    public Builder applyStyled(Style style, Builder subline) {
        return subline;
    }

    @Override
    public Builder applyAtomic(String text) {
        Builder result = createResult();
        StringBuilder content = result.getResult();
        content.append(text);
        return result;
    }

    /* Does not support horizontal rules. */
    @Override
    public Builder createHRule() {
        return createResult();
    }

    @Override
    public Builder createResult() {
        return new Builder();
    }

    /** Returns the singleton instance of this renderer. */
    public static StringFormat instance() {
        return instance;
    }

    private static final StringFormat instance = new StringFormat();

    static class Builder implements LineFormat.Builder<Builder> {
        @Override
        public boolean isEmpty() {
            return this.content.length() == 0;
        }

        @Override
        public StringBuilder getResult() {
            return this.content;
        }

        @Override
        public void append(Builder other) {
            this.content.append(other.content);
        }

        @Override
        public void appendLineBreak() {
            this.content.append("\n");
        }

        @Override
        public String toString() {
            return this.content.toString();
        }

        private final StringBuilder content = new StringBuilder();
    }
}
