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

import java.util.List;

import groove.automaton.RegExpr;
import groove.automaton.RegExpr.Atom;
import groove.automaton.RegExpr.Wildcard;
import groove.grammar.host.HostEdge;
import groove.util.collect.TreeHashSet;

/**
 * The abstract class for all path checkers that match against one host edge.
 * The criterion for matching depends on the concrete implementation.
 *
 * @author Arash Jalali
 * @version $Revision $
 */
public abstract class SingleEdgePathChecker extends AbstractPathChecker
    implements ReteStateSubscriber {

    /**
     * edge path-checkers have memories to store their single-edge
     * path matches, so that during deletion, these matches can be
     * picked up and deleted in a domino way without having to pass down
     * the edges themselves.
     */
    private TreeHashSet<RetePathMatch> memory = new TreeHashSet<>();

    /**
     *
     * @param network The RETE network to which this will belong
     * @param expression The regular expression that should be either
     * an atom or a wild-card.
     */
    public SingleEdgePathChecker(ReteNetwork network, RegExpr expression, boolean isLoop) {
        super(network, expression, isLoop);
        this.getOwner()
            .getState()
            .subscribe(this);
        assert(expression instanceof Atom) || (expression instanceof Wildcard);
    }

    /**
     * @param source The n-node that has sent down an edge for processing
     * @param gEdge the edge to be processed
     * @param action indicates whether the edge has been added or removed.
     */
    public void receive(ReteNetworkNode source, HostEdge gEdge, Action action) {

        if (this.loop && gEdge.source() != gEdge.target()) {
            return;
        }

        RetePathMatch m = makeMatch(gEdge);

        if (action == Action.ADD) {

            assert!this.memory.contains(m);
            this.memory.add(m);
            passDownMatchToSuccessors(m);

        } else { // action == Action.REMOVE

            if (this.memory.contains(m)) {
                RetePathMatch m1 = m;
                m = this.memory.put(m);
                this.memory.remove(m1);
                if (m != null) {
                    m.dominoDelete(null);
                }
            }
        }

    }

    /**
     * Makes a proper path match with the given host edge
     * @param gEdge The given host edge
     */
    protected abstract RetePathMatch makeMatch(HostEdge gEdge);

    @Override
    public int demandOneMatch() {
        //TODO ARASH: make demand-based
        return 0;
    }

    @Override
    public boolean demandUpdate() {
        //TODO ARASH: make demand-based
        return false;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public void receive(ReteNetworkNode source, int repeatedIndex, RetePathMatch newMatch) {
        //This method will not be called for this type of n-node
        throw new UnsupportedOperationException();
    }

    @Override
    public List<? extends Object> initialize() {
        super.initialize();
        return null;
    }

    @Override
    public void clear() {
        super.clear();
        this.memory.clear();
    }

}