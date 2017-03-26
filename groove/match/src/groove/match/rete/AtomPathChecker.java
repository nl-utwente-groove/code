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

import groove.automaton.RegExpr.Atom;
import groove.grammar.host.HostEdge;

/**
 * @author Arash Jalali
 * @version $Revision $
 */
public class AtomPathChecker extends SingleEdgePathChecker {

    /**
     * @param network the owner RETE network
     * @param theAtom the atom component of the regular expression 
     *         that is to be checked by this n-node 
     */
    public AtomPathChecker(ReteNetwork network, Atom theAtom, boolean isLoop) {
        super(network, theAtom, isLoop);
    }

    /**
     * @return The text label of the associated atom which this checker
     * n-node finds matches for
     */
    public String getText() {
        return this.getExpression().getAtomText();
    }

    @Override
    public boolean equals(ReteNetworkNode node) {
        return (this == node)
            || ((node instanceof AtomPathChecker)
                && this.getOwner().equals(node.getOwner()) && this.getText().equals(
                ((AtomPathChecker) node).getText()));
    }

    @Override
    protected RetePathMatch makeMatch(HostEdge edge) {
        return new RetePathMatch(this, edge);
    }

    @Override
    public void updateBegin() {
        // Do nothing        
    }

    @Override
    public void updateEnd() {
        // Do nothing        
    }
}
