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

import groove.match.rete.RetePathMatch.EmptyPathMatch;
import groove.rel.RegExpr.Empty;

import java.util.HashMap;
import java.util.List;

/**
 * @author Arash Jalali
 * @version $Revision $
 */
public class EmptyPathChecker extends AbstractPathChecker implements
        StateSubscriber {

    private EmptyPathMatch emptyMatch = new EmptyPathMatch(this);

    /**
     * maintains singleton instances per network.
     */
    protected static HashMap<ReteNetwork,EmptyPathChecker> instances =
        new HashMap<ReteNetwork,EmptyPathChecker>();

    /**
     * Used internally for creating the singleton instance 
     * specific to a given RETE network.
     * @param network The network to which is empty path-checker should belong.
     */
    private EmptyPathChecker(ReteNetwork network) {
        super(network, new Empty());
        this.getOwner().getState().subscribe(this);
    }

    /**
     * Returns a singleton instance of the empty path-checker
     * specific to a given RETE network.
     * 
     * @param network The given network.
     */
    public static EmptyPathChecker getInstance(ReteNetwork network) {
        EmptyPathChecker result = instances.get(network);
        if (result == null) {
            result = new EmptyPathChecker(network);
            instances.put(network, result);
        }
        return result;
    }

    @Override
    public void receive(ReteNetworkNode source, int repeatedIndex,
            RetePathMatch newMatch) {
        //This is not supposed to be called for empty path checkers
        //because empty path checkers are not given any matches to 
        //pass on.
        throw new UnsupportedOperationException();
    }

    @Override
    public int demandOneMatch() {
        return 0;
    }

    @Override
    public boolean demandUpdate() {
        return false;
    }

    @Override
    public boolean equals(ReteNetworkNode node) {
        return (this == node)
            || ((node instanceof EmptyPathChecker) && this.getOwner().equals(
                node.getOwner()));
    }

    @Override
    protected void passDownMatchToSuccessors(AbstractReteMatch m) {
        assert m != null;
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

    @Override
    public void clear() {
        //Left empty. Nothing to do.
    }

    @Override
    public List<? extends Object> initialize() {
        passDownMatchToSuccessors(this.emptyMatch);
        return null;
    }
}