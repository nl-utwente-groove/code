/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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
 * $Id: ModelChecking.java,v 1.5 2008-03-20 13:29:51 kastenberg Exp $
 */

package groove.verify;

import groove.graph.Edge;
import groove.trans.Rule;

import java.util.Collection;

import rwth.i2.ltl2ba4j.model.IGraphProposition;

/**
 * This class contains a number of constants to be used for model checking.
 * @author Harmen Kastenberg
 * @version $Revision: 1.5 $ $Date: 2008-03-20 13:29:51 $
 */
public class ModelChecking {

	/** constant for non-toggled state colour white */
	public static final int NO_COLOUR = 0;
	/** constant for non-toggled state colour white */
	public static final int WHITE = 1;
	/** constant for non-toggled state colour cyan */
	public static final int CYAN = 2;
	/** constant for non-toggled state colour blue */
	public static final int BLUE = 3;
	/** constant for non-toggled state colour red */
	public static final int RED = 4;
//	/** constant for non-toggled state colour gray (for future use) */
//	public static final int GRAY = 5;
//	/** constant for non-toggled state colour black (for future use) */
//	public static final int BLACK = 6;

	/** constant for toggled state colour white */
	public static final int WHITE_TOGGLE = -1;
	/** constant for toggled state colour cyan */
	public static final int CYAN_TOGGLE = -2;
	/** constant for toggled state colour blue */
	public static final int BLUE_TOGGLE = -3;
	/** constant for toggled state colour red */
	public static final int RED_TOGGLE = -4;
//	/** constant for toggled state colour gray (for future use) */
//	public static final int GRAY_TOGGLE = -5;
//	/** constant for toggled state colour black (for future use) */
//	public static final int BLACK_TOGGLE = -6;

	/** constant for notifying the system is OK */
	public static final int OK = 1;
	/** constant for notifying that a counter-example has been identified */
	public static final int COUNTER_EXAMPLE = 2;

	/** constant for the true label in the Buchi automaton */
    public static final String SIGMA = "<SIGMA>";
	/** constant for the conjunction symbol in labels in the Buchi automaton */
    public static final String CONJUNCTION_SYMBOL = "&";

    private static boolean TOGGLE = false;

    /**
     * Return the constant for the colour white, considering
     * the colour-scheme to use (toggled or non-toggled).
     * @return the white constant
     */
    public static int white() {
    	if (TOGGLE)
    		return WHITE_TOGGLE;
    	else
    		return WHITE;
    }

    /**
     * Return the constant for the colour cyan, considering
     * the colour-scheme to use (toggled or non-toggled).
     * @return the cyan constant
     */
    public static int cyan() {
    	if (TOGGLE)
    		return CYAN_TOGGLE;
    	else
    		return CYAN;
    }

    /**
     * Return the constant for the colour blue, considering
     * the colour-scheme to use (toggled or non-toggled).
     * @return the blue constant
     */
    public static int blue() {
    	if (TOGGLE)
    		return BLUE_TOGGLE;
    	else
    		return BLUE;
    }

    /**
     * Return the constant for the colour red, considering
     * the colour-scheme to use (toggled or non-toggled).
     * @return the red constant
     */
    public static int red() {
    	if (TOGGLE)
    		return RED_TOGGLE;
    	else
    		return RED;
    }

    /**
     * Toggle the colour-scheme used.
     */
    public static void toggle() {
    	TOGGLE = !TOGGLE;
    }

    /**
     * Checks whether a Buchi-transition is enabled, given a set
     * of rule-names of applicable rules.
	 * @return <tt>true</tt> if the given transition is enabled,
	 * <tt>false</tt> otherwise
	 */
	public static boolean isPropertyTransitionEnabled(BuchiTransition transition, Collection<Rule> rules) {
    	boolean result = true;
    	for (IGraphProposition gp: transition.getLabels()) {
    		if (gp.getFullLabel().equals(SIGMA)) {
    			continue;
    		}
    		boolean applicable = false;
    		// only take the label of the proposition - negation will be checked afterwards
    		String prop = gp.getLabel();
    		for (Rule rule: rules) {
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
     * Checks whether the given edge is labelled with {@link ModelChecking#SIGMA}.
     * @param edge the edge to be checked
     * @return <tt>true</tt> if the edge is indeed labelled with
     * {@link ModelChecking#SIGMA}, <tt>false</tt> otherwise 
     */
    public static boolean hasSigmaLabel(Edge edge) {
    	return edge.label().text().equals(SIGMA);
    }
}
