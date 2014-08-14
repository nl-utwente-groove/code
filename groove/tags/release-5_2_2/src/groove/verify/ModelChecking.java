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
package groove.verify;

/**
 * This class contains a number of constants to be used for model checking.
 * @author Harmen Kastenberg
 * @version $Revision$
 */
public class ModelChecking {
    /** Static value indicating the absence of a colour. */
    public static final int NO_COLOUR = Color.NONE.ordinal();

    private static enum Color {
        NONE, WHITE, CYAN, BLUE, RED, ALT_WHITE, ALT_CYAN, ALT_BLUE, ALT_RED;
    }

    /** Possible outcomes for a model checking run. */
    public static enum Outcome {
        /** No errors found. */
        OK,
        /** Counterexample found. */
        ERROR;
    }

    /** Record of a model checking run. */
    public static class Record {
        /** Constructs a fresh record. */
        public Record() {
            this.iteration = 0;
            setColours(false);
        }

        private void setColours(boolean altColour) {
            this.altColour = altColour;
            this.white = (altColour ? Color.ALT_WHITE : Color.WHITE).ordinal();
            this.cyan = (altColour ? Color.ALT_CYAN : Color.CYAN).ordinal();
            this.blue = (altColour ? Color.ALT_BLUE : Color.BLUE).ordinal();
            this.red = (altColour ? Color.ALT_RED : Color.RED).ordinal();
        }

        /** Returns the white value of this record. */
        public int white() {
            return this.white;
        }

        /** Returns the cyan value of this record. */
        public int cyan() {
            return this.cyan;
        }

        /** Returns the red value of this record. */
        public int red() {
            return this.red;
        }

        /** Returns the blue value of this record. */
        public int blue() {
            return this.blue;
        }

        /** Returns the current iteration of the record. */
        public int getIteration() {
            return this.iteration;
        }

        /** Returns the record where the used colours are toggled. */
        public void increase() {
            this.iteration++;
            setColours(!this.altColour);
        }

        private int white;
        private int cyan;
        private int red;
        private int blue;
        private boolean altColour;
        private int iteration;
    }
}
