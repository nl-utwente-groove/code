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

import groove.verify.BuchiLocation;
import groove.verify.DefaultBuchiTransition;

import java.util.Set;

import junit.framework.Assert;

/**
 * @author Harmen Kastenberg
 * @version $Revision $
 */
public class NASABuchiTransition extends DefaultBuchiTransition {
    /** symbol used for unguarded enabledness */
    private static final String TRUE_SYMBOL = "-";

    /** symbol used for negation */
    private static final String NEGATION_SYMBOL = "!";

    /**
     * Constructor for creating a new Buchi
     */
    public NASABuchiTransition(BuchiLocation source, NASABuchiLabel label,
            BuchiLocation target) {
        super(source, label, target);
    }

    @Override
    public boolean isEnabled(Set<String> applicableRules) {
        String guard = ((NASABuchiLabel) label()).guard();
        if (guard.equals(TRUE_SYMBOL)) {
            return true;
        } else {
            // extract all components of the conjunction
            String[] args = guard.split("&");
            boolean result = true;
            for (String arg : args) {
                if (isNegated(arg)) {
                    String nArg = arg.substring(1);
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
                    if (applicableRules.contains(arg)) {
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

    private boolean isNegated(String argument) {
        if (argument.startsWith(NEGATION_SYMBOL)) {
            return true;
        }

        return false;
    }

    @Override
    public String toString() {
        return source().toString() + this.label().toString()
            + target().toString();
    }
}
