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
 * $Id$
 */
package groove.explore.strategy;

import java.util.HashSet;
import java.util.Set;

import groove.grammar.Action;
import groove.grammar.Rule;
import groove.lts.GraphTransition;
import groove.verify.ModelChecking.Record;
import groove.verify.ProductTransition;

/**
 * Implementation of interface {@link Boundary} that bases the boundary on a set
 * of rules for which application are said to cross the boundary.
 *
 * @author Harmen Kastenberg
 * @version $Revision$
 */
public class RuleSetBoundary extends Boundary {
    /**
     * Constructs a prototype boundary object.
     * To use, invoke {@link #instantiate(Record)}.
     * @param rules the set of rules that constitute the boundary
     */
    public RuleSetBoundary(Set<Rule> rules) {
        this(rules, null);
    }

    /**
     * {@link RuleSetBoundary} constructor.
     * @param rules the set of rules that constitute the boundary
     */
    private RuleSetBoundary(Set<Rule> rules, Record record) {
        super(record);
        this.rules = new HashSet<>(rules);
    }

    @Override
    public Boundary instantiate(Record record) {
        return new RuleSetBoundary(this.rules, record);
    }

    @Override
    public boolean crossingBoundary(ProductTransition transition, boolean traverse) {
        boolean result = false;
        // if the underlying transition is null, this transition
        // represents a final transition and does therefore
        // not cross any boundary
        if (transition.graphTransition() != null && containsAction(transition.rule())) {
            // this is a forbidden rule
            // the current depth now determines whether we may
            // traverse this transition, or not
            result = currentDepth() >= getRecord().getIteration() - 2;
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
    private boolean containsAction(Action action) {
        return this.rules.contains(action);
    }

    @Override
    public void backtrackTransition(GraphTransition transition) {
        if (transition.getAction() != null && containsAction(transition.getAction())) {
            decreaseDepth();
        }
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (Action rule : this.rules) {
            if (result.length() > 0) {
                result.append(",");
            }
            result.append(rule.getQualName());
        }
        return result.toString();
    }

    /** the set of rules that are initially forbidden to apply */
    private final Set<Rule> rules;
}
