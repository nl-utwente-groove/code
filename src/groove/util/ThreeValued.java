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
package groove.util;

import java.util.EnumMap;
import java.util.Map;

/**
 * General three-valued enumerator.
 * @author rensink
 * @version $Revision $
 */
public enum ThreeValued {
    /** Lowest value. */
    FALSE,
    /** Middle value. */
    SOME,
    /** Upper value. */
    TRUE, ;

    /** Indicates if this is the {@link #FALSE} value. */
    public boolean isFalse() {
        return this == FALSE;
    }

    /** Indicates if this is the {@link #SOME} value. */
    public boolean isSome() {
        return this == SOME;
    }

    /** Indicates if this is the {@link #TRUE} value. */
    public boolean isTrue() {
        return this == TRUE;
    }

    /**
     * Returns the singleton instance of the default selector,
     * with string representations "false", "some" and "true".
     */
    public static Selector selector() {
        return INSTANCE;
    }

    private static final Selector INSTANCE = new Selector();

    /** Converter between {@link String} and {@link ThreeValued}. */
    static public class Selector extends Property<String> {
        /**
         * Constructs and editor with given string representations for the three values.
         * @param deflt default value; if {@code null}, there is no default
         */
        public Selector(String lower, String middle, String upper, ThreeValued deflt) {
            super(String.format("Three-valued choice of %s, %s or %s", lower, middle, upper));
            this.textMap.put(FALSE, lower);
            this.textMap.put(SOME, middle);
            this.textMap.put(TRUE, upper);
            this.deflt = deflt;
        }

        /** Construct a selector with representations "false", "some" and "true". */
        public Selector() {
            this("false", "some", "true", SOME);
        }

        /** Converts a string to a {@link ThreeValued} value,
         * according to the encoding of this editor.
         * @param text the text to be converted; non-{@code null}
         * @return the resulting {@link ThreeValued} value.
         * @throws IllegalArgumentException if {@code text} is not a string representation
         * of any of the three values and {@link #hasDefault()} is {@code false}
         */
        public ThreeValued toValue(String text) throws IllegalArgumentException {
            ThreeValued result = null;
            if (isFalse(text)) {
                result = FALSE;
            } else if (isTrue(text)) {
                result = TRUE;
            } else if (isSome(text) || hasDefault()) {
                result = SOME;
            } else {
                throw new IllegalArgumentException(
                    String.format("Value %s should be one of %s, %s or %s",
                                  text,
                                  getFalse(),
                                  getSome(),
                                  getTrue()));
            }
            return result;
        }

        /** Tests if a given text corresponds to the lower ("false") value. */
        public boolean isFalse(String text) {
            return getFalse().equals(text);
        }

        /** Tests if a given text corresponds to the middle value. */
        public boolean isSome(String text) {
            return getSome().equals(text);
        }

        /** Tests if a given text corresponds to the upper ("true") value. */
        public boolean isTrue(String text) {
            return getTrue().equals(text);
        }

        @Override
        public boolean isSatisfied(String value) {
            return isFalse(value) || isSome(value) || isTrue(value);
        }

        /** Returns the textual representation of a given {@link ThreeValued} value. */
        public String getText(ThreeValued value) {
            return this.textMap.get(value);
        }

        /** Indicates if there is a proper (non-{@code null}) default value. */
        public boolean hasDefault() {
            return this.deflt != null;
        }

        /** Returns the default value of this selector.
         * If non-{@code null}, this is the value that {@link #toValue(String)} returns
         * when given an unknown string as input.
         * @return the default value, or {@code null} if there is no default
         */
        public ThreeValued getDefault() {
            return this.deflt;
        }

        private final ThreeValued deflt;

        /** Returns the string representation of the lower ("false") value. */
        public String getFalse() {
            return getText(FALSE);
        }

        /** Returns the string representation of the middle value. */
        public String getSome() {
            return getText(SOME);
        }

        /** Returns the string representation of the upper ("true") value. */
        public String getTrue() {
            return getText(TRUE);
        }

        private final Map<ThreeValued,String> textMap = new EnumMap<>(
            ThreeValued.class);

        @Override
        public String toString() {
            return "Selector [deflt=" + this.deflt + ", textMap=" + this.textMap + "]";
        }
    }
}
