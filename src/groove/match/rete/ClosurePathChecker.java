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

import groove.rel.RegExpr;
import groove.rel.RegExpr.Star;

import com.sun.org.apache.xpath.internal.operations.Plus;

/**
 * A Path-checker that finds a (kleene or transitive) sequence closure of
 * a certain path expression.
 * @author Arash Jalali
 * @version $Revision $
 */
public class ClosurePathChecker extends SequenceOperatorPathChecker {

    /**
     * Creates a Path-checker note that performs sequencing-closure, i.e.
     * the plus and star (kleene) operators.
     * @param network The RETE network to which this node will belong
     * @param expression The regular path expression, the operator of
     * which should be either {@link Plus} or {@link Star}.
     */
    public ClosurePathChecker(ReteNetwork network, RegExpr expression) {
        super(network, expression);

        assert (expression.getPlusOperand() != null)
            || (expression.getStarOperand() != null);
        this.addSuccessor(this);
        this.addAntecedent(this);
    }

    @Override
    protected void giveNewMatchToSuccessor(RetePathMatch newMatch,
            ReteNetworkNode n, int repeatedSuccessorIndex) {
        //if the new match is a loop, then there's no need to feed it
        //to yourself.
        if ((n != this) || newMatch.end().equals(newMatch.start())) {
            super.giveNewMatchToSuccessor(newMatch, n, repeatedSuccessorIndex);
        }
    }

}
