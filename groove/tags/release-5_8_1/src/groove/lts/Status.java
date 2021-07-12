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
package groove.lts;

import java.util.EnumSet;
import java.util.Set;

/**
 * Set of graph state status flags.
 * @author Arend Rensink
 * @version $Revision $
 */
public class Status {
    /**
     * Constructs a status object from a given integer representation.
     */
    private Status(int status) {
        this.code = status & MASK;
        this.flags = EnumSet.noneOf(Flag.class);
        for (Flag flag : Flag.values()) {
            if (flag.test(status)) {
                this.flags.add(flag);
            }
        }
    }

    /** Returns the integer representation of this status object. */
    public int getCode() {
        return this.code;
    }

    private final int code;

    /** Returns the set of flags in this status object. */
    public Set<Flag> getFlags() {
        return this.flags;
    }

    private final Set<Flag> flags;

    /** Sets the absence level in a given status value, and returns the result. */
    static public int setAbsence(int status, int absence) {
        assert getAbsence(status) == 0 : String.format("Absence level already set in %x", absence);
        if (absence > Status.MAX_ABSENCE) {
            throw new IllegalArgumentException(
                String.format("Absence level %d too large: max. %s", absence, Status.MAX_ABSENCE));
        }
        return status | (absence << Status.ABSENCE_SHIFT);
    }

    /** Retrieves the absence level from a given status value. */
    static public int getAbsence(int status) {
        return status >> Status.ABSENCE_SHIFT;
    }

    /** Number of bits by which a status value has be right-shifted to get
     * the absence value.
     */
    private final static int ABSENCE_SHIFT = 25;

    /** Maximal absence value. */
    public final static int MAX_ABSENCE = 1 << (31 - ABSENCE_SHIFT);

    /** Returns the (fixed) status object for a given integer representation. */
    public static Status instance(int status) {
        status = status & MASK;
        Status result = store[status];
        if (result == null) {
            result = store[status] = new Status(status);
        }
        return result;
    }

    /** Mask to select only the bits corresponding to {@link Flag} values. */
    private final static int MASK = (1 << Flag.values().length) - 1;
    /** Array of preconstructed {@link Status} objects. */
    private final static Status[] store = new Status[1 << Flag.values().length];

    /** Indicates if a given integer status representation stands for a real state. */
    public static boolean isReal(int status) {
        return !Flag.INTERNAL.test(status) && !Flag.ABSENT.test(status);
    }

    /** Changeable status flags of a graph state. */
    public enum Flag {
        /** Flag indicating that the state is absent, i.e., without any
         * non-transient or open successors.
         */
        ABSENT(false, false),
        /**
         * Flag indicating that the state has been closed.
         * This is the case if and only if no more outgoing transitions will be added.
         */
        CLOSED(false, true),
        /**
         * Flag indicating that exploration of the graph state is done.
         * This is the case if and only if it is closed, and all outgoing transition
         * sequences eventually lead to non-transient or absent states.
         */
        DONE(false, true),
        /** Flag indicating that the state has an error. */
        ERROR(false, true),
        /**
         * Flag indicating that the state is final. This is the case if
         * the underlying (actual) control frame is final.
         */
        FINAL(false, false),
        /** Flag indicating that the state is internal, i.e., a recipe state. */
        INTERNAL(false, false),
        /** Helper flag used during state space exploration. */
        KNOWN(true, false),
        /** Flag indicating that the state is transient, i.e., inside an atomic block. */
        TRANSIENT(false, false),
        /** Flag indicating that this is a result state in the current exploration. */
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

        private final int mask;
        /** Indicates if this flag is exploration-related. */
        private final boolean strategy;
    }
}
