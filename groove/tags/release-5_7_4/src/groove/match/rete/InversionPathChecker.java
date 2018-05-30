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

import groove.automaton.RegExpr.Inv;

/**
 * A Path checker that implements the semantics of the 
 * inversion operator.
 * 
 * @author Arash Jalali
 * @version $Revision $
 */
public class InversionPathChecker extends AbstractPathChecker {

    /**
     * Creates an inversion path checker node based on the given expression
     * @param network The RETE network to which this n-node is to belong
     * @param expression The regular expression that has inversion
     * as its top-most operator.
     */
    public InversionPathChecker(ReteNetwork network, Inv expression,
            boolean isLoop) {
        super(network, expression, isLoop);
        assert expression.getInvOperand() != null;
    }

    @Override
    public void receive(ReteNetworkNode source, int repeatedIndex,
            RetePathMatch newMatch) {
        if (this.loop && !newMatch.isEmpty()
            && !newMatch.start().equals(newMatch.end())) {
            return;
        }

        RetePathMatch m = newMatch.inverse(this);
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
}
