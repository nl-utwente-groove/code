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
 * $Id: BoundedStrategy.java,v 1.6 2007-10-05 08:31:50 rensink Exp $
 */
package groove.lts.explore;

import groove.graph.GraphAdapter;
import groove.graph.GraphShape;
import groove.graph.GraphShapeListener;
import groove.graph.Node;
import groove.lts.ConditionalExploreStrategy;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.State;
import groove.trans.Condition;
import groove.trans.Rule;

import java.util.Collection;

/**
 * Continues exploration, in a breadth-firsth manner, except where a given condition
 * (the bounding condition) is violated; from such states no further exploration takes place.
 * Currently, the bounding condition is expressed by a graph transformation rule.
 * @author Arend Rensink
 * @version $Revision: 1.6 $
 */
public class BoundedStrategy extends BranchingStrategy implements ConditionalExploreStrategy {
	/** Name of this exploration strategy. */
    static public final String NAME = "Bounded";
	/** Short description of this exploration strategy. */
    static public final String DESCRIPTION = "Explores all states where the (negated) bounding condition holds";

    /**
     * Constructs a bounded strategy, without setting a rule.
     * The rule should be set using <tt>{@link #setCondition}</tt>
     */
    public BoundedStrategy() {
        this(false);
    }

    /**
     * Constructs a bounded strategy with given negation, without setting a rule.
     * The rule should be set using {@link #setCondition}
     * @param negated <tt>true</tt> if the bounding condition should be negated
     */
    public BoundedStrategy(boolean negated) {
        this.negated = negated;
    }

    /**
     * Constructs a bounded strategy from a given rule.
     * The rule will serve as the bounding condition for the exploration:
     * states in which it is not applicable will not be explored further.
     * @param rule the new bounding condition
     */
    public BoundedStrategy(Rule rule) {
        this.rule = rule;
    }

    /**
     * Initializes the set of open states, then calls the super method.
     */
    @Override
    public void setGTS(GTS gts) {
        if (getGTS() != null) {
            getGTS().removeGraphListener(graphListener);
        }
        gts.addGraphListener(graphListener);
        openStateSet = createStateSet();
        openStateSet.addAll(gts.getOpenStates());
        super.setGTS(gts);
    }

    @Override
    public String getShortDescription() {
        return DESCRIPTION;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String toString() {
        String result = getName();
        result += " by " + (negated ? "!" : "") + (rule == null ? "..." : rule.getName().toString());
        return result;
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

    /**
     * This strategy explores all currently open states, and disregards
     * {@link #getAtState()}.
     */
    @Override
    public Collection<? extends State> explore() throws InterruptedException {
        // clones the open states to avoid concurrent modifications while exploring
        Collection<GraphState> openStateSet = this.openStateSet;
        this.openStateSet = createStateSet();
        explore(openStateSet);
        return getGTS().getFinalStates();
    }

    /**
     * Overwrites the method so that only states to which the bounding rule is
     * applicable are explorable.
     */
    @Override
    protected boolean isExplorable(GraphState state) {
        return rule.hasMatch(state.getGraph()) != negated;
    }

    /**
     * The bounding rule of this strategy.
     */
    private Condition rule;
    /**
     * Flag that signals whether the condition should be negated before being applied.
     */
    private boolean negated;

    /**
     * The current set of open states.
     */
    private Collection<GraphState> openStateSet;
    /** The graph lisener permanently associated with this exploration strategy. */
    private final GraphShapeListener graphListener = new GraphAdapter() {
        /** This method adds the element to the open states, if it is a state. */
        @Override
        public void addUpdate(GraphShape graph, Node node) {
        	openStateSet.add((GraphState) node);
        }
    };
}