/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2010 University of Twente
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

import groove.grammar.Condition;
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
            public void matchRemoved(AbstractReteMatch match) {
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
    public CompositeConditionChecker(ReteNetwork network, Condition relatedNac,
            ConditionChecker parentConditionChecker,
            ReteStaticMapping antecedent) {
        super(network, relatedNac, parentConditionChecker, antecedent);
    }

    @Override
    public void receive(AbstractReteMatch match) {
        match.addDominoListener(this.conflictSetMatchDominoAdapter);
        ReteSimpleMatch actualPrefixMatchWithCorrectOwner =
            this.getParent().isEmpty()
                    ? this.getParent().getEmptyMatch()
                    : ReteSimpleMatch.forge(this.parent,
                        this.getOwner().isInjective(), match.getSpecialPrefix());

        this.getParent().receiveInhibitorMatch(
            actualPrefixMatchWithCorrectOwner, Action.ADD);
    }

    /**
     * This method is the call-back method called by the composite 
     * matches when they are domino-removed.
     * 
     * @param m The composite match that has been removed.
     */
    public void matchDeminoRemovedFromConflictSet(AbstractReteMatch m) {
        ReteSimpleMatch actualPrefixMatchWithCorrectOwner =
            this.getParent().isEmpty() ? this.getParent().getEmptyMatch()
                    : ReteSimpleMatch.forge(this.parent,
                        this.getOwner().isInjective(), m.getSpecialPrefix());
        this.getParent().receiveInhibitorMatch(
            actualPrefixMatchWithCorrectOwner, Action.REMOVE);
    }

    /**
     * Determines if the negative parts of this composite condition
     * are up to date.
     */
    public boolean isNegativePartUpToDate() {
        //If the positive component 
        //(which is the parent) is
        //up to date then this whole composite condition is up to date too. 
        return this.isUpToDate() || !this.parent.isUpToDate();
    }

    @Override
    public boolean invalidate() {
        boolean result = super.invalidate();
        if (!this.isNegativePartUpToDate()) {
            this.getParent().invalidate();
        }
        return result;
    }

}
