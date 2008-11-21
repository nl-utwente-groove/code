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

import groove.graph.Edge;
import groove.trans.Rule;

import java.util.Collection;

import rwth.i2.ltl2ba4j.model.IGraphProposition;

/**
 * This class contains a number of constants to be used for model checking.
 * @author Harmen Kastenberg
 * @version $Revision$
 */
public class ModelChecking {

    /** constant for non-toggled state colour white */
    public static final int NO_COLOUR = 0;
    /** constant for state colour black (so-called pocket state) */
    // public static final int BLACK = 1;
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
    public static int CURRENT_WHITE = WHITE;
    /** the value for the colour cyan in the current colour-scheme. */
    public static int CURRENT_CYAN = CYAN;
    /** the value for the colour blue in the current colour-scheme. */
    public static int CURRENT_BLUE = BLUE;
    /** the value for the colour red in the current colour-scheme. */
    public static int CURRENT_RED = RED;

    /** constant to keep track of dynamic colour-schemes */
    private static int NEXT_FREE_COLOUR = 6;

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
    public static int MAX_ITERATIONS = -1;
    /** constant specifying the maximal number of iterations to be performed */
    public static int CURRENT_ITERATION = 0;
    /** constant specifying the maximal amount of time to spend */
    public static long MINUTES = -1;
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
     * Return the constant for the colour black. This colour is shared by all
     * colour-schemes.
     * @return the constant {@link ModelChecking#BLACK}
     */
    // public static int black() {
    // return BLACK;
    // }
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
        // updateColourScheme();
    }

    /** Updates the color scheme */
    public static void updateColourScheme() {
        if (ModelChecking.START_FROM_BORDER_STATES) {
            nextColourScheme();
        }
    }

    /**
     * Instantiate a fresh colour-scheme.
     */
    public static void nextColourScheme() {
        assert (NEXT_FREE_COLOUR % 4 == 2) : "Faulty colour-scheme in use: constant for WHITE should be have value n*4+1";
        CURRENT_WHITE = nextFreeColour();
        CURRENT_CYAN = nextFreeColour();
        CURRENT_BLUE = nextFreeColour();
        CURRENT_RED = nextFreeColour();
    }

    /**
     * Returns the next integer value that is available for colouring.
     * @return the next integer value that is available for colouring.
     */
    private static int nextFreeColour() {
        return NEXT_FREE_COLOUR++;
    }

    /**
     * Checks whether a Buchi-transition is enabled, given a set of rule-names
     * of applicable rules.
     * @return <tt>true</tt> if the given transition is enabled,
     *         <tt>false</tt> otherwise
     */
    public static boolean isPropertyTransitionEnabled(
            BuchiTransition transition, Collection<Rule> rules) {
        boolean result = true;
        for (IGraphProposition gp : transition.getLabels()) {
            if (gp.getFullLabel().equals(SIGMA)) {
                continue;
            }
            boolean applicable = false;
            // only take the label of the proposition - negation will be checked
            // afterwards
            String prop = gp.getLabel();
            for (Rule rule : rules) {
                if (prop.equals(rule.getName().name())) {
                    applicable = true;
                }
            }
            boolean match = (gp.isNegated() ^ applicable);
            result = result && match;
        }
        return result;
    }

    /**
     * Checks whether the given edge is labelled with
     * {@link ModelChecking#SIGMA}.
     * @param edge the edge to be checked
     * @return <tt>true</tt> if the edge is indeed labelled with
     *         {@link ModelChecking#SIGMA}, <tt>false</tt> otherwise
     */
    public static boolean hasSigmaLabel(Edge edge) {
        return edge.label().text().equals(SIGMA);
    }

    /** Reporter for profiling information. */
    // static public final Reporter reporter =
    // Reporter.register(ModelChecking.class);
    // static public final int POCKET_STATE_REPORTER =
    // reporter.newMethod("blackPainting()");
    // public static int NEXT = reporter.newMethod("ModelChecking.next");
    // public static int UPDATE =
    // reporter.newMethod("ModelChecking.updateNext");
    // public static int BACKTRACK =
    // reporter.newMethod("ModelChecking.backtrack");
}
