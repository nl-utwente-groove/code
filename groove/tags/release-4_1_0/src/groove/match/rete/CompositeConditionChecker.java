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
package groove.match.rete;

import groove.graph.Edge;
import groove.graph.Node;
import groove.match.rete.ReteNetwork.ReteStaticMapping;

/**
 * A checker node which is the result of superimposing the
 * positive and negative components of a condition.
 * 
 * @author Arash Jalali
 * @version $Revision $
 */
public class CompositeConditionChecker extends ConditionChecker {

    private DominoEventListener conflictSetMatchDominoAdapter =
        new DominoEventListener() {
            @Override
            public void matchRemoved(ReteMatch match) {
                matchDeminoRemovedFromConflictSet(match);
            }
        };

    /**
     * Creates a composite condition checker
     * 
     * @param network The RETE network this n-node belongs to.
     * @param parentConditionChecker  The condition checker for the positive part.
     * @param antecedent The antecedent subgraph-checker.
     */
    public CompositeConditionChecker(ReteNetwork network,
            ConditionChecker parentConditionChecker,
            ReteStaticMapping antecedent) {
        super(network, parentConditionChecker.getCondition(),
            parentConditionChecker, antecedent);
    }

    @Override
    public void receive(ReteMatch match, Action action) {
        match.addDominoListener(this.conflictSetMatchDominoAdapter);
        ReteMatch actualPrefixMatchWithCorrectOwner =
            ReteMatch.copyContents(this.parent, match.getSpecialPrefix(), true);
        this.getParent().receiveInhibitorMatch(
            actualPrefixMatchWithCorrectOwner, Action.ADD);
    }

    @Override
    public void receive(DisconnectedSubgraphChecker antecedent,
            ReteMatch match, Action action) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void receive(Edge mu, Action action) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void receive(Node node, Action action) {
        throw new UnsupportedOperationException();
    }

    /**
     * This method is the call-back method called by the composite 
     * matches when they are domino-removed.
     * 
     * @param m The composite match that has been removed.
     */
    public void matchDeminoRemovedFromConflictSet(ReteMatch m) {
        ReteMatch actualPrefixMatchWithCorrectOwner =
            ReteMatch.copyContents(this.parent, m.getSpecialPrefix(), true);

        this.getParent().receiveInhibitorMatch(
            actualPrefixMatchWithCorrectOwner, Action.REMOVE);
    }
}