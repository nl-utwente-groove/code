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
package groove.verify.ltl2ba;

import gov.nasa.ltl.graph.Guard;
import gov.nasa.ltl.graph.Literal;
import groove.verify.BuchiLocation;
import groove.verify.DefaultBuchiTransition;

import java.util.Set;

import junit.framework.Assert;

/**
 * @author Harmen Kastenberg
 * @version $Revision $
 */
public class NASABuchiTransition extends DefaultBuchiTransition {
    /**
     * Constructor for creating a new Buchi transition
     */
    public NASABuchiTransition(BuchiLocation source, NASABuchiLabel label,
            BuchiLocation target) {
        super(source, label, target);
    }

    @Override
    public boolean isEnabled(Set<String> applicableRules) {
        Guard<String> guard = ((NASABuchiLabel) label()).guard();
        if (guard.isEmpty()) {
            return true;
        } else {
            boolean result = true;
            for (Literal<String> arg : guard) {
                if (arg.isNegated()) {
                    String nArg = arg.getAtom();
                    // if applicable and negated, the transition is not enabled
                    // we can return value false
                    if (applicableRules.contains(nArg)) {
                        return false;
                    }
                    // if not applicable and negated, the transition can still be enabled
                    // the below else-statement could be left out
                    else {
                        result &= true;
                    }
                } else {
                    // if applicable and not negated, transition can still be enabled
                    // the below else-statement could be left out
                    if (applicableRules.contains(arg.getAtom())) {
                        result &= true;
                    } else {
                        return false;
                    }
                }
            }
            // when reaching this part, the variable 'result' should be true
            Assert.assertTrue(
                "I have not been able to think of a situation in which we should return 'false' here. Double check this.",
                result);
            return result;
        }
    }

    @Override
    public String toString() {
        return source().toString() + this.label().toString()
            + target().toString();
    }
}
