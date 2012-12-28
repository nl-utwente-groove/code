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
 * $Id: ModelChecking.java,v 1.5 2008/03/20 13:29:51 kastenberg Exp $
 */
package groove.verify;

/**
 * This class contains a number of constants to be used for model checking.
 * @author Harmen Kastenberg
 * @version $Revision$
 */
public class ModelChecking {

    /** constant for non-toggled state colour white */
    public static final int NO_COLOUR = 0;
    /** constant for non-toggled state colour white */
    public static final int WHITE = 2;
    /** constant for non-toggled state colour cyan */
    public static final int CYAN = 3;
    /** constant for non-toggled state colour blue */
    public static final int BLUE = 4;
    /** constant for non-toggled state colour red */
    public static final int RED = 5;

    /** constant for toggled state colour white */
    public static final int WHITE_TOGGLE = -2;
    /** constant for toggled state colour cyan */
    public static final int CYAN_TOGGLE = -3;
    /** constant for toggled state colour blue */
    public static final int BLUE_TOGGLE = -4;
    /** constant for toggled state colour red */
    public static final int RED_TOGGLE = -5;

    /** the value for the colour white in the current colour-scheme. */
    private static int CURRENT_WHITE = WHITE;
    /** the value for the colour cyan in the current colour-scheme. */
    private static int CURRENT_CYAN = CYAN;
    /** the value for the colour blue in the current colour-scheme. */
    private static int CURRENT_BLUE = BLUE;
    /** the value for the colour red in the current colour-scheme. */
    private static int CURRENT_RED = RED;

    /** the value for marking pocket states */
    public static int POCKET = 1;
    /** the value for marking non-pocket states */
    public static int NO_POCKET = -1;

    /** constant for notifying the system is OK */
    public static final int OK = 1;
    /** constant for notifying that a counter-example has been identified */
    public static final int COUNTER_EXAMPLE = 2;

    /** constant for the true label in the Buchi automaton */
    public static final String SIGMA = "<SIGMA>";
    /** constant for the conjunction symbol in labels in the Buchi automaton */
    public static final String CONJUNCTION_SYMBOL = "&";

    private static boolean TOGGLE = false;

    /** constant specifying whether to mark pocket-states */
    public static boolean MARK_POCKET_STATES;
    /** constant specifying the maximal number of iterations to be performed */
    private static int CURRENT_ITERATION = 0;
    /** constant specifying the maximal amount of time to spend */
    public static long MAX_TIME = 0;
    /** constant specifying the maximal amount of time to spend */
    public static boolean START_FROM_BORDER_STATES = false;

    /**
     * Return the current constant for the colour white.
     * @return the constant {@link ModelChecking#CURRENT_WHITE}
     */
    public static int white() {
        return CURRENT_WHITE;
    }

    /**
     * Return the current constant for the colour cyan.
     * @return the constant {@link ModelChecking#CURRENT_CYAN}
     */
    public static int cyan() {
        return CURRENT_CYAN;
    }

    /**
     * Return the current constant for the colour blue.
     * @return the constant {@link ModelChecking#CURRENT_BLUE}
     */
    public static int blue() {
        return CURRENT_BLUE;
    }

    /**
     * Return the current constant for the colour red.
     * @return the constant {@link ModelChecking#CURRENT_RED}
     */
    public static int red() {
        return CURRENT_RED;
    }

    /**
     * Toggle the colour-scheme used.
     */
    public static void toggle() {
        TOGGLE = !TOGGLE;
        if (TOGGLE) {
            CURRENT_WHITE = WHITE_TOGGLE;
            CURRENT_CYAN = CYAN_TOGGLE;
            CURRENT_BLUE = BLUE_TOGGLE;
            CURRENT_RED = RED_TOGGLE;
        } else {
            CURRENT_WHITE = WHITE;
            CURRENT_CYAN = CYAN;
            CURRENT_BLUE = BLUE;
            CURRENT_RED = RED;
        }
    }

    /**
     * Reset the iteration counter.
     */
    public static void resetIteration() {
        CURRENT_ITERATION = 0;
    }

    /**
     * Increase the iteration count.
     */
    public static void nextIteration() {
        CURRENT_ITERATION++;
    }

    /**
     * Increase the iteration count.
     */
    public static int getIteration() {
        return CURRENT_ITERATION;
    }

    private static enum Color {
        NONE, WHITE, CYAN, BLUE, RED, ALT_WHITE, ALT_CYAN, ALT_BLUE, ALT_RED;
    }

    /** Record of a model checking run. */
    public static class Record {
        private Record(boolean altColour, boolean pocket) {
            this.white = altColour ? Color.ALT_WHITE : Color.WHITE;
            this.cyan = altColour ? Color.ALT_CYAN : Color.CYAN;
            this.blue = altColour ? Color.ALT_BLUE : Color.BLUE;
            this.red = altColour ? Color.ALT_RED : Color.RED;
            this.pocket = pocket;
        }

        private void setToggle(Record toggled) {
            this.toggled = toggled;
        }

        /** Returns the white value of this record. */
        public Color white() {
            return this.white;
        }

        /** Returns the cyan value of this record. */
        public Color cyan() {
            return this.cyan;
        }

        /** Returns the red value of this record. */
        public Color red() {
            return this.red;
        }

        /** Returns the blue value of this record. */
        public Color blue() {
            return this.blue;
        }

        /** Indicates if the model checking run is a pocket strategy. */
        public boolean isPocket() {
            return this.pocket;
        }

        /** Returns the record where the used colours are toggled. */
        public Record toggle() {
            return this.toggled;
        }

        private final Color white;
        private final Color cyan;
        private final Color red;
        private final Color blue;
        private final boolean pocket;
        private Record toggled;
    }

    /** Returns a record for the current model checking run. */
    public static Record getRecord(boolean pocket) {
        return getRecord(false, pocket);
    }

    private static Record getRecord(boolean altColour, boolean pocket) {
        if (altColour) {
            return pocket ? altPocketRecord : altNonPocketRecord;
        } else {
            return pocket ? normPocketRecord : normNonPocketRecord;
        }
    }

    private final static Record normNonPocketRecord = new Record(false, false);
    private final static Record normPocketRecord = new Record(false, true);
    private final static Record altNonPocketRecord = new Record(true, false);
    private final static Record altPocketRecord = new Record(true, true);
    static {
        normPocketRecord.setToggle(altPocketRecord);
        altPocketRecord.setToggle(normPocketRecord);
        normNonPocketRecord.setToggle(altNonPocketRecord);
        altNonPocketRecord.setToggle(normNonPocketRecord);
    }
}
