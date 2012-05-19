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

import groove.trans.Action;
import groove.trans.Rule;
import groove.verify.ModelChecking;
import groove.verify.ProductTransition;

import java.util.HashSet;
import java.util.Set;

/**
 * Implementation of interface {@link Boundary} that bases the boundary on a set
 * of rules for which application are said to cross the boundary.
 * 
 * @author Harmen Kastenberg
 * @version $Revision$
 */
public class RuleSetBoundary extends Boundary {

    /**
     * {@link RuleSetBoundary} constructor.
     * @param ruleSetBoundary the set of rules that constitute the boundary
     */
    public RuleSetBoundary(Set<Rule> ruleSetBoundary) {
        this.ruleSetBoundary.addAll(ruleSetBoundary);
    }

    /**
     * Add a rule to the set of boundary rules.
     * @param rule the rule to be added
     * @return see {@link java.util.Set#add(Object)}
     */
    public boolean addRule(Rule rule) {
        return this.ruleSetBoundary.add(rule);
    }

    @Override
    public boolean crossingBoundary(ProductTransition transition,
            boolean traverse) {
        boolean result = false;
        // if the underlying transition is null, this transition
        // represents a final transition and does therefore
        // not cross any boundary
        if (transition.graphTransition() != null
            && containsAction(transition.rule())) {
            // this is a forbidden rule
            // the current depth now determines whether we may
            // traverse this transition, or not
            result = currentDepth() >= ModelChecking.CURRENT_ITERATION - 2;
            if (!result && traverse) {
                increaseDepth();
            }
        }
        return result;
    }

    @Override
    public void increase() {
        // do nothing
    }

    /** Returns whether this boundary contains the given rule. */
    public boolean containsAction(Action action) {
        return this.ruleSetBoundary.contains(action);
    }

    @Override
    public void backtrackTransition(ProductTransition transition) {
        if (transition.rule() != null && containsAction(transition.rule())) {
            decreaseDepth();
        }
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (Action rule : this.ruleSetBoundary) {
            if (result.length() > 0) {
                result.append(",");
            }
            result.append(rule.getFullName());
        }
        return result.toString();
    }

    /** the set of rules that are initially forbidden to apply */
    private final Set<Rule> ruleSetBoundary = new HashSet<Rule>();
}
