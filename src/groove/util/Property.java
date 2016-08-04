/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * $Id$
 */
package groove.util;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Interface to wrap a simple condition on a subject type.
 * @author Arend Rensink
 * @version $Revision $
 */
abstract public class Property<S> {
    /** Creates an instance with <code>null</code> comment and description. */
    public Property() {
        this(null, null);
    }

    /**
     * Creates an instance with a given description and no explanation.
     */
    public Property(String description) {
        this(description, null);
    }

    /**
     * Creates an instance with a given description and explanation.
     * @param description the description of the property
     * @param explanation the property explanation
     */
    public Property(String description, String explanation) {
        this.description = description;
        this.explanation = explanation;
    }

    /**
     * Indicates if this property is satisfied by a given object of type
     * <code>S</code>.
     */
    abstract public boolean isSatisfied(S value);

    /**
     * Removes from a given set all values that do not satisfy this property.
     * @param collection the set to be filtered; will be modified as a result of this call
     * @return the parameter {@code collection}
     */
    public <C extends Collection<? extends S>> C filter(C collection) {
        Iterator<? extends S> iter = collection.iterator();
        while (iter.hasNext()) {
            if (!isSatisfied(iter.next())) {
                iter.remove();
            }
        }
        return collection;
    }

    /**
     * HTML-formatted description of the value(s) that satisfy this property.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Provides a description of the value(s) that satisfy this property,
     * with the first character optionally capitalised.
     */
    public StringBuffer getDescription(boolean uppercase) {
        if (getDescription() == null) {
            return null;
        } else {
            StringBuffer result = new StringBuffer(getDescription());
            if (uppercase) {
                result.setCharAt(0, Character.toUpperCase(result.charAt(0)));
            } else {
                result.setCharAt(0, Character.toLowerCase(result.charAt(0)));
            }
            return result;
        }
    }

    /**
     * Provides a comment on this property. This can be a description of the
     * thing the property is testing.
     */
    public String getExplanation() {
        return this.explanation;
    }

    /** Explanation of this property. */
    private final String explanation;
    /** Description of the values of this property. */
    private final String description;

    /**
     * Creates and returns a property that returns <code>true</code> on all
     * objects of a generic type.
     */
    static public <T> Property<T> createTrue() {
        return new True<>();
    }

    /** Property subclass that always returns true. */
    static public class True<S> extends Property<S> {
        /**
         * Constructs an instance with <code>null</code> description and
         * comment.
         */
        public True() {
            this(null);
        }

        /**
         * Constructs an instance with <code>null</code> description and a
         * given comment.
         */
        public True(String comment) {
            super(null, comment);
        }

        @Override
        public boolean isSatisfied(S state) {
            return true;
        }
    }

    /**
     * Property subclass that tests if a given string represents a boolean
     * value. This is considered to be the case if the string equals
     * <code>true</code>, <code>false</code>, or optionally the empty
     * string.
     * @author Arend Rensink
     * @version $Revision $
     */
    static public class IsBoolean extends Property<String> {
        /**
         * Constructs an instance with a flag to indicate if the empty string
         * should be approved.
         * @param emptyOk if <code>true</code>, the empty string is approved.
         */
        protected IsBoolean(String description, String comment, boolean emptyOk) {
            super(description, comment);
            this.emptyOk = emptyOk;
        }

        /**
         * Constructs an instance with a flag to indicate if the empty string
         * should be approved.
         * @param emptyOk if <code>true</code>, the empty string is approved.
         */
        public IsBoolean(String comment, boolean emptyOk) {
            this(description, comment, emptyOk);
        }

        /**
         * A value is only correct if it is empty, or equals <code>true</code>
         * or <code>false</code>.
         */
        @Override
        public boolean isSatisfied(String value) {
            return (this.emptyOk && value.equals("")) || value.equals(trueString)
                || value.equals(falseString);
        }

        /** Flag indicating if the empty string is approved. */
        private final boolean emptyOk;

        /** Representation of <code>true</code>. */
        static private final String trueString = Boolean.toString(true);
        /** Representation of <code>false</code>. */
        static private final String falseString = Boolean.toString(false);
        /** The property description. */
        static private final String description =
            String.format("%s or %s", trueString, falseString);
    }

    /**
     * Properties subclass that tests whether a given value is a correct value
     * of an {@link Enum} type (passed in as a type parameter).
     */
    static public class IsEnumValue<T extends Enum<T>> extends Property<String> {
        /**
         * Constructs an instance with a flag to indicate if the empty string
         * should be approved.
         * @param enumType the enum type supported by this property
         * @param emptyOk if <code>true</code>, the empty string is approved.
         */
        public IsEnumValue(Class<T> enumType, boolean emptyOk) {
            super(getDescription(enumType), "Should be " + getDescription(enumType));
            this.emptyOk = emptyOk;
            this.enumType = enumType;
        }

        /**
         * A value is only correct if it is empty, or equals a value of the
         * wrapped enum type.
         */
        @Override
        public boolean isSatisfied(String value) {
            if (value.length() == 0) {
                return this.emptyOk;
            }
            try {
                Enum.valueOf(this.enumType, value.toUpperCase());
                return true;
            } catch (IllegalArgumentException e) {
                return false;
            }
        }

        /** Flag indicating if the empty string is approved. */
        private final boolean emptyOk;
        /** The type of enum. */
        private final Class<T> enumType;

        /** enumType has to be an enumeration type. */
        private static String getDescription(Class<?> enumType) {
            StringBuilder result = new StringBuilder();
            Field[] fields = enumType.getFields();
            if (fields.length == 0) {
                return " Error : no value possible.";
            }
            for (int i = 1; i < fields.length; i++) {
                if (i != 0) {
                    result.append(" or ");
                }
                result.append(fields[i].getName());
            }
            return result.toString();
        }
    }

    /**
     * Property that is satisfied by any of the values from a given predetermined set.
     */
    static public class Choice<S> extends Property<S> {
        /** Constructs a choice based on a given set of values, and a given description. */
        @SafeVarargs
        public Choice(String comment, S... values) {
            this.values = new HashSet<>(Arrays.asList(values));
            this.explanation = comment;
        }

        @Override
        public String getExplanation() {
            return this.explanation;
        }

        @Override
        public String getDescription() {
            return Groove.toString(this.values.toArray(), "One of ", "", ", ", " or ");
        }

        @Override
        public boolean isSatisfied(S value) {
            return this.values.contains(value);
        }

        private final String explanation;
        private final Set<S> values;
    }
}
