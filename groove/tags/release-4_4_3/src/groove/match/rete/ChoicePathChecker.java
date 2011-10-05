/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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

import groove.rel.RegExpr.Choice;

/**
 * A Path checker that implements the semantics of the 
 * choice operator.
 * @author Arash Jalali
 * @version $Revision $
 */
public class ChoicePathChecker extends AbstractPathChecker {

    /**
     * Creates a choice path checker node for given path expression
     * of type
     */
    public ChoicePathChecker(ReteNetwork network, Choice expression) {
        super(network, expression);
        assert expression.getChoiceOperands() != null;
    }

    @Override
    public void receive(ReteNetworkNode source, int repeatedIndex,
            RetePathMatch newMatch) {
        assert repeatedIndex < 2;
        RetePathMatch m = newMatch.reoriginate(this);
        passDownMatchToSuccessors(m);
    }

    @Override
    public int demandOneMatch() {
        // TODO ARASH:implement on-demand
        return 0;
    }

    @Override
    public boolean demandUpdate() {
        // TODO ARASH:implement on-demand
        return false;
    }

    @Override
    protected void passDownMatchToSuccessors(AbstractReteMatch m) {
        ReteNetworkNode previous = null;
        int repeatedSuccessorIndex = 0;
        for (ReteNetworkNode n : this.getSuccessors()) {
            repeatedSuccessorIndex =
                (n != previous) ? 0 : (repeatedSuccessorIndex + 1);
            giveNewMatchToSuccessor((RetePathMatch) m, n,
                repeatedSuccessorIndex);
            previous = n;
        }
    }

}
