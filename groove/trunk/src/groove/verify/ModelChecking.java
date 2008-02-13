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
 * $Id: ModelChecking.java,v 1.2 2008-02-13 14:16:11 kastenberg Exp $
 */

package groove.verify;

import groove.graph.Edge;
import groove.trans.Rule;

import java.util.Collection;
import java.util.Set;

import rwth.i2.ltl2ba4j.model.IGraphProposition;

/**
 * This class contains a number of constants to be used for model checking.
 * @author Harmen Kastenberg
 * @version $Revision: 1.2 $ $Date: 2008-02-13 14:16:11 $
 */
public class ModelChecking {

	/** state colouring */
	public static final int WHITE = 1;
	public static final int CYAN = 2;
	public static final int BLUE = 3;
	public static final int RED = 4;
	public static final int GRAY = 5;
	public static final int BLACK = 6;

	public static final int OK = 1;
	public static final int COUNTER_EXAMPLE = 2;

    public static final String SIGMA = "<SIGMA>";
    public static final String CONJUNCTION_SYMBOL = "&";

    /**
	 * @return
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
