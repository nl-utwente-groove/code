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
package groove.explore.result;

import groove.graph.Edge;
import groove.graph.GraphShape;
import groove.lts.GraphTransition;
import groove.trans.Rule;
import groove.view.StoredGrammarView;

/**
 * Accepts states that violate an invariant condition on states.
 * The invariant is defined by the application of a given rule.
 * @author Eduardo Zambon
 */
public class RuleApplicationAcceptor extends ConditionalAcceptor<Rule> {
    /**
     * Creates an instance with a default {@link Result}.
     */
    public RuleApplicationAcceptor() {
        this(null, new Result());
    }
    
    /**
     * Constructs a new instance with a given condition and a default
     * {@link Result}.
     * @param condition the condition to be used; may be <code>null</code>.
     */
    public RuleApplicationAcceptor(ExploreCondition<Rule> condition) {
        this(condition, new Result());
    }
    
    /**
     * Constructs a new instance with a given Result.
     */
    public RuleApplicationAcceptor(Result result) {
        this(null, result);
    }

    /**
     * Constructs a new instance with a given condition and Result.
     * @param condition the condition to be used; may be <code>null</code>.
     */
    public RuleApplicationAcceptor(ExploreCondition<Rule> condition,
            Result result) {
        super(condition, result);
    }

    /**
     * This implementation adds the state to the result if it violates the
     * invariant condition.
     */
    @Override
    public void addUpdate(GraphShape graph, Edge edge) {
        GraphTransition transition = (GraphTransition) edge;
        IsRuleApplicableCondition condition =
                            (IsRuleApplicableCondition) this.getCondition();
        if (condition.isSatisfied(transition)) {
            this.getResult().add(transition.target());
        }
    }
    
    /**
     * Updates the acceptor when the grammar changes.
     * Passes the change on to the IsRuleApplicableCondition. 
     * @param grammar - the new grammar
     * @return true - the acceptor is still valid after the grammar update
     *         false - the acceptor is no longer valid after the update
     */
    @Override
    public boolean respondToGrammarUpdate(StoredGrammarView grammar) {
        return ((IsRuleApplicableCondition) this.getCondition()).respondToGrammarUpdate(grammar);
    }
    
    /** This implementation returns an {@link RuleApplicationAcceptor}. */
    @Override
    public Acceptor newInstance() {
        return new RuleApplicationAcceptor(getCondition(),
            getResult().newInstance());
    }
}
