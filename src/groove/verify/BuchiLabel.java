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
import groove.graph.TextLabel;

/**
 * @author Harmen Kastenberg
 * @version $Revision $
 */
public class BuchiLabel extends TextLabel {
    private final String action;

    private final Guard<Proposition> guard;

    /**
     * Constructor.
     *
     * @param action
     *          the action for the label
     * @param guard
     *          the guard for the label
     */
    public BuchiLabel(String action, Guard<Proposition> guard) {
        super(computeText(action, guard));
        this.action = action;
        this.guard = guard;
    }

    /**
     * Returns the <code>action</code> of this label.
     *
     * @return the <code>action</code> of this label.
     */
    public String action() {
        return this.action;
    }

    /**
     * Returns the <code>guard</code> of this label.
     *
     * @return the <code>guard</code> of this label
     */
    public Guard<Proposition> guard() {
        return this.guard;
    }

    private static String computeText(String action, Guard<Proposition> guard) {
        StringBuilder result = new StringBuilder();
        result.append(action);
        result.append("/[");
        boolean first = true;
        for (Literal<Proposition> literal : guard) {
            if (!first) {
                result.append(",");
                first = false;
            }
            if (literal.isNegated()) {
                result.append("!");
            }
            result.append(literal.getAtom());
        }
        result.append("]");
        return result.toString();
    }
}
