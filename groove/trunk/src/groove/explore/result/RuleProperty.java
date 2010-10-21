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
package groove.explore.result;

import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.trans.Rule;
import groove.util.Property;

import java.util.Iterator;

/** Property that tests whether a {@link GraphState} has an outgoing transition
 * for a given rule.
 * @author Arend Rensink
 * @version $Revision $
 */
@Deprecated
public class RuleProperty extends Property<GraphState> {
    /** Constructs a property that tests if a given rule has been applied. */
    public RuleProperty(Rule rule) {
        this.rule = rule;
    }

    @Override
    public boolean isSatisfied(GraphState value) {
        Iterator<GraphTransition> transIter = value.getTransitionIter();
        while (transIter.hasNext()) {
            if (transIter.next().getEvent().getRule().equals(this.rule)) {
                return true;
            }
        }
        return false;
    }

    /** The rule for which this property tests. */
    private final Rule rule;
}
