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
 * $Id: CTLTransitionMarker.java,v 1.7 2008-02-29 11:18:00 fladder Exp $
 */
package groove.verify;

import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.trans.RuleName;

import java.util.Iterator;

/**
 * Visitor-implementation of {@link CTLFormulaMarker} using the
 * transition-strategy. This means that every transition-label can be used as a
 * predicate. Basically, a transition in a graph transition systems represents
 * the application of a graph transformation rule (GTR). A GTR is only
 * applicable if there exists a matching from its left-hand-side (LHS) to the
 * current state (graph structure inside the state). So, actually the LHS of the
 * GTR is the property to check on.
 * 
 * @author Harmen Kastenberg
 * @version $Revision$ $Date: 2008-02-29 11:18:00 $
 */
public class CTLTransitionMarker extends CTLMatchingMarker {
    @Override
    public void markAtom(Marking marking, TemporalFormula property, GTS gts) {
        boolean specialAtom = markSpecialAtom(marking, property, gts);
        if (!specialAtom) {
            String name = ((CTLStarFormula.Atom) property).predicateName();
            for (GraphState nextState : gts.nodeSet()) {
                // this state satisfies the CTL-expression if it has an
                // outgoing-transition labelled with the
                // name of the property
                // if no such outgoing transition exists, this state does not
                // satisfy the CTL-expression
                boolean satisfies = false;
                Iterator<GraphTransition> transitionIter =
                    nextState.getTransitionIter();
                while (!satisfies && transitionIter.hasNext()) {
                    RuleName ruleName =
                        transitionIter.next().getEvent().getRule().getName();
                    if (ruleName.toString().equals(name)) {
                        satisfies = true;
                        property.getCounterExamples().add(nextState);
                    }
                }
                marking.set(nextState, property, satisfies);
            }
        }
    }
}
