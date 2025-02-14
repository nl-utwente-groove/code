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
package nl.utwente.groove.lts;

import nl.utwente.groove.util.Exceptions;

/**
 * Set of graph state status flags.
 * @author Arend Rensink
 * @version $Revision$
 */
public class Status {
    /** Sets the absence level in a given status value, and returns the result. */
    static public int setAbsence(int status, int absence) {
        if (absence > Status.MAX_ABSENCE) {
            throw Exceptions
                .illegalArg("Absence level %d too large: max. %s", absence, Status.MAX_ABSENCE);
        }
        return (status & ABSENCE_MASK) | (absence << Status.ABSENCE_SHIFT);
    }

    /** Retrieves the absence level from a given status value. */
    static public int getAbsence(int status) {
        return status >>> Status.ABSENCE_SHIFT;
    }

    /** Checks whether a given status value implies absence.
     * This is the case if the status is {@link Flag#FULL} and the absence level is positive.
     * @param status status value to be checked
     * @see #getAbsence(int)
     */
    static public boolean isAbsent(int status) {
        return Flag.REMOVED.test(status) || Flag.FULL.test(status) && getAbsence(status) > 0;
    }

    /** Number of bits by which a status value has be right-shifted to get
     * the absence value.
     */
    private final static int ABSENCE_SHIFT = Flag.values().length;

    /** Maximal absence value. */
    public final static int MAX_ABSENCE = (1 << (32 - ABSENCE_SHIFT)) - 1;

    /** Mask value with 0 on all absence positions and 1 elsewhere. */
    static private final int ABSENCE_MASK = ~(MAX_ABSENCE << ABSENCE_SHIFT);

    /** Initial status value. */
    static public final int INIT_STATUS = setAbsence(0, MAX_ABSENCE);

    /** Checks if a given integer status representation stands for a public state,
     * i.e., one that is neither inner nor absent.
     */
    public static boolean isPublic(int status) {
        return !Flag.INNER.test(status) && !isAbsent(status);
    }

    /** Changeable status flags of a graph state. */
    public enum Flag {
        /**
         * Indicates that a state is closed.
         * This is the case if and only if no more outgoing transitions will be added.
         */
        CLOSED(false, true),
        /**
         * Indicates that a state is final. This is the case if
         * the underlying (actual) control frame is final.
         */
        FINAL(false, false),
        /**
         * Indicates that exploration of a graph state is complete.
         * This is the case if and only if it is closed, and if it is transient,
         * then all outgoing transitions lead to full or steady states.
         */
        FULL(false, true),
        /** Indicates that a state is inner, i.e., a recipe state. */
        INNER(false, false),
        /** Indicates that a state has been explicitly removed because of some policy, and hence is absent regardless of its absence level. */
        REMOVED(false, false),
        /** Flag indicating that the state is transient, i.e., inside an atomic block. */
        TRANSIENT(false, false),
        /** Flag indicating that the state has an error. */
        ERROR(false, true),
        /** Helper flag used during state space exploration. */
        KNOWN(true, false),
        /** Indicates a result state in the current exploration. */
        RESULT(false, true);

        private Flag(boolean strategy, boolean change) {
            this.mask = 1 << ordinal();
            this.strategy = strategy;
            this.change = change;
        }

        /** Returns the mask corresponding to this flag. */
        public int mask() {
            return this.mask;
        }

        private final int mask;

        /** Sets this flag in a given integer value. */
        public int set(int status) {
            return status | this.mask;
        }

        /** Resets this flag in a given integer value. */
        public int reset(int status) {
            return status & ~this.mask;
        }

        /** Tests if this flag is set in a given integer value. */
        public boolean test(int status) {
            return (status & this.mask) != 0;
        }

        /** Indicates if this flag can be used to indicate a change in
         * status. If {@code false}, the flag is (un)set as a consequence
         * of some other change.
         * @return {@code true} if this flag is explicitly set; {@code false}
         * if it is derived
         */
        public boolean isChange() {
            return this.change;
        }

        private final boolean change;

        /** Indicates if this flag is exploration strategy-related. */
        public boolean isStrategy() {
            return this.strategy;
        }

        /** Indicates if this flag is exploration-related. */
        private final boolean strategy;
    }
}
