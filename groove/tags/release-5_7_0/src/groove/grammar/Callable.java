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
package groove.grammar;

import groove.control.Procedure;

/**
 * Unit of functionality that can be called from a control program.
 * @author Arend Rensink
 * @version $Revision $
 */
public interface Callable {
    /**
     * Returns the kind of callable.
     */
    Kind getKind();

    /**
     * Returns the full (qualified) name of this unit.
     */
    QualName getQualName();

    /** Returns the last part of the full name of this unit.
     * @see #getQualName()
     */
    default String getLastName() {
        return getQualName().last();
    }

    /** Returns the signature of the unit. */
    public Signature<?> getSignature();

    /** Callable unit kind. */
    public static enum Kind {
        /** Transformation rule. */
        RULE("rule"),
        /** Function. */
        FUNCTION("function"),
        /** Recipe. */
        RECIPE("recipe"),;

        private Kind(String name) {
            this.name = name;
        }

        /**
         * Indicates if this kind represents a {@link Procedure}.
         */
        public boolean isProcedure() {
            return this == FUNCTION || this == RECIPE;
        }

        /** Indicates if this kind represents an {@link Action}. */
        public boolean isAction() {
            return this == RECIPE || this == RULE;
        }

        /**
         * Returns the description of this kind,
         * with the initial letter optionally capitalised.
         */
        public String getName(boolean upper) {
            StringBuilder result = new StringBuilder(this.name);
            if (upper) {
                result.replace(0, 1, "" + Character.toUpperCase(this.name.charAt(0)));
            }
            return result.toString();
        }

        private final String name;
    }
}
