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
 * $Id: RuleSetBoundary.java,v 1.6 2008/03/21 12:36:04 kastenberg Exp $
 */
package groove.explore.strategy;

import groove.lts.ProductTransition;
import groove.trans.Rule;
import groove.verify.ModelChecking;

import java.util.Set;

/**
 * @author Harmen Kastenberg
 * @version $Revision$
 */
public class RuleSetStartBoundary extends RuleSetBoundary {

    /**
     * {@link RuleSetStartBoundary} constructor.
     * @param ruleSetBoundary the set of rules that constitute the boundary
     */
    public RuleSetStartBoundary(Set<Rule> ruleSetBoundary) {
        super(ruleSetBoundary);
    }

    @Override
    public boolean crossingBoundary(ProductTransition transition,
            boolean traverse) {
        boolean crossing = super.crossingBoundary(transition, false);

        if (!crossing) {
            return false;
        } else {
            // this is a forbidden rule
            // the current depth now determines whether we may
            // traverse this transition, or not
            if (currentDepth() < ModelChecking.CURRENT_ITERATION - 2) {
                if (traverse) {
                    increaseDepth();
                }
                return false;
            } else {
                return true;
            }
        }
    }

    @Override
    public void backtrackTransition(ProductTransition transition) {
        if (transition.rule() == null) {
            System.out.println("backtracking final transition");
        } else if (containsRule(transition.rule())) {
            decreaseDepth();
        }
    }

    @Override
    public void increase() {
        // do nothing
    }
}
