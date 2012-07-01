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
 * $Id$
 */
package groove.verify;

import gov.nasa.ltl.graph.Guard;
import gov.nasa.ltl.graph.Literal;
import groove.graph.AbstractEdge;

import java.util.Set;

/**
 * @author Harmen Kastenberg
 * @version $Revision $
 */
public class BuchiTransition extends
        AbstractEdge<BuchiLocation,BuchiLabel> {
    /**
     * Constructor for creating a new B�chi transition
     */
    public BuchiTransition(BuchiLocation source,
            BuchiLabel label, BuchiLocation target) {
        super(source, label, target);
    }

    /**
     * Determines whether the transition is enabled based on the given set of names of applicable rules.
     * 
     * @param applicableRules
     *          the set of names of applicable rules
     * @return <code>true</code> if the set of applicable rules enable the transition, <code>false</code> otherwise.
     */
    public boolean isEnabled(Set<String> applicableRules) {
        boolean result = true;
        Guard<String> guard = label().guard();
        if (!guard.isEmpty()) {
            for (Literal<String> arg : guard) {
                if (arg.isNegated() == applicableRules.contains(arg.getAtom())) {
                    result = false;
                    break;
                }
            }
        }
        return result;
    }
}