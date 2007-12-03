// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente
 
// Licensed under the Apache License, Version 2.0 (the "License"); 
// you may not use this file except in compliance with the License. 
// You may obtain a copy of the License at 
// http://www.apache.org/licenses/LICENSE-2.0 
 
// Unless required by applicable law or agreed to in writing, 
// software distributed under the License is distributed on an 
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
// either express or implied. See the License for the specific 
// language governing permissions and limitations under the License.
/*
 * $Id: InvariantStrategy.java,v 1.5 2007-10-05 08:31:50 rensink Exp $
 */
package groove.lts.explore;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import groove.lts.ConditionalExploreStrategy;
import groove.lts.GraphState;
import groove.trans.Condition;
import groove.trans.Rule;

/**
 * Continues exploration, in a breadth-first manner, until a given condition
 * (the invariant) is found to be violated somewhere; this halts the entire explorations.
 * Currently, the condition is expressed by the applicability of a rule.
 * @author Arend Rensink
 * @version $Revision: 1.5 $
 */
public class InvariantStrategy extends BranchingStrategy implements ConditionalExploreStrategy {
	/** Name of this exploration strategy. */
    static public final String NAME = "Invariant";
	/** Short description of this exploration strategy. */
    static public final String DESCRIPTION = "Stops exploring if the (negated) invariant condition is violated";

    /**
     * Constructs an invariant strategy, without setting a rule.
     * The rule should be set using <tt>{@link #setCondition}</tt>
     */
    public InvariantStrategy() {
        // explicit empty constructor
    }

    /**
     * Constructs a halting strategy from a given rule.
     * The rule will serve as the halting condition for the exploration:
     * as soon as a violation is detected, no state will be explored further.
     * @param rule
     */
    public InvariantStrategy(Rule rule) {
        this.rule = rule;
    }

    @Override
    public String getShortDescription() {
        return DESCRIPTION;
    }

    @Override
    public String getName() {
        return NAME;
    }

    public boolean isNegated() {
        return negated;
    }

    public void setNegated(boolean negated) {
        this.negated = negated;
    }

    public Condition getCondition() {
        return rule;
    }

    public void setCondition(Condition rule) {
        this.rule = rule;
    }

    @Override
    public String toString() {
        String result = getName() + " ";
        if (negated) {
            result += "!";
        }
        result += rule == null ? "..." : rule.getName().toString();
        if (getGTS() != null && getAtState() != getGTS().startState()) {
            result += " (starting at " + getAtState() + ")";
        }
        return result;
    }

    /**
     * This implementation stops exploring altogether as soon as the condition is violated
     * (as indicated by <tt>isExplorable</tt>).
     */
    @Override
    protected Collection<GraphState> computeNextStates(Collection<GraphState> atStates) throws InterruptedException {
        Collection<GraphState> result = new LinkedList<GraphState>();
        getCollector().set(result);
        boolean halt = false;
        Iterator<GraphState> openStateIter = atStates.iterator();
        while (!halt && openStateIter.hasNext()) {
            if (Thread.currentThread().isInterrupted()) {
                throw new InterruptedException();
            }
            GraphState openState = openStateIter.next();
            if (isExplorable(openState)) {
                explore(openState);
            } else {
                halt = true;
                result.clear();
            }
        }
        getCollector().reset();
        return result;
    }

    /**
     * Overwrites the method so that no state is explorable as soon as any violation
     * of the invariant condition is discovered.
     */
    @Override
    protected boolean isExplorable(GraphState state) {
        valid = valid && rule.hasMatch(state.getGraph()) != isNegated();
        return valid;
    }

    /**
     * The rule that determines the invariant to be checked.
     */
    private Condition rule;
    /**
     * Flag that signals whether the condition should be negated before being applied.
     */
    private boolean negated;
    /**
     * Flag that indicates that no invariant violations have been found.
     */
    private boolean valid = true;
}