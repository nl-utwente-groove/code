/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.match.rete;

import nl.utwente.groove.grammar.Condition;
import nl.utwente.groove.grammar.Grammar;
import nl.utwente.groove.grammar.host.HostEdge;
import nl.utwente.groove.grammar.host.HostGraph;
import nl.utwente.groove.grammar.host.HostNode;
import nl.utwente.groove.grammar.rule.Anchor;
import nl.utwente.groove.match.SearchEngine;
import nl.utwente.groove.match.rete.ReteNetworkNode.Action;
import nl.utwente.groove.transform.DeltaStore;
import nl.utwente.groove.util.Reporter;

/**
 * Objects of this class create {@link ReteSearchStrategy} instances
 * for the actual matching.
 * Every object of this class keeps a {@link ReteNetwork} that is shared
 * by all the search strategies.
 * @author Arash Jalali
 * @version $Revision $
 */
public class ReteSearchEngine extends SearchEngine {
    /** Creates a new engine, on the basis of a given grammar.
     * This means the rete network gets initialised by that grammar,
     * and populated by the grammar's start graph.
     */
    public ReteSearchEngine(Grammar grammar) {
        this.network = new ReteNetwork(this, grammar, grammar.getProperties()
            .isInjective());
    }

    /**
     * @return The network object used by this engine.
     */
    public ReteNetwork getNetwork() {
        return this.network;
    }

    /**
     * Tells the engine to update the RETE runtime state.
     *
     * @param destGraph The state/host graph that has resulted from the given update.
     *                  This host graph is given to the method so that it could
     *                  decide if re-initializing the RETE network is less costly
     *                  than applying the updates in the <code>deltaStore</code>.
     * @param deltaStore Represents the actual update (node/edge creations/removals)
     *                   to the host graph which could be the sum of the effects
     *                   of a series of rule applications/transitions.
     */
    public synchronized void transitionOccurred(HostGraph destGraph, DeltaStore deltaStore) {
        transitionOccurredReporter.start();

        if (deltaStore.size() > destGraph.size()) {
            graphChanged(destGraph);
        } else {
            this.network.setUpdating(true);
            this.network.getState()
                .setHostGraph(destGraph);
            for (HostNode n : deltaStore.getRemovedNodeSet()) {
                this.network.update(n, Action.REMOVE);
            }

            for (HostEdge e : deltaStore.getRemovedEdgeSet()) {
                this.network.update(e, Action.REMOVE);
            }

            for (HostNode n : deltaStore.getAddedNodeSet()) {
                this.network.update(n, Action.ADD);
            }

            for (HostEdge e : deltaStore.getAddedEdgeSet()) {
                this.network.update(e, Action.ADD);
            }

            this.network.setUpdating(false);
        }
        transitionOccurredReporter.stop();
    }

    /**
     * Tells the engine to reinitialise the RETE runtime state.
     *
     * @param newGraph The state/host graph to which the network should be set.
     */
    public void graphChanged(HostGraph newGraph) {
        this.network.processGraph(newGraph);
    }

    @Override
    public synchronized ReteSearchStrategy createMatcher(Condition condition, Anchor seed) {
        //TODO: ARASH: What about the seed nodes and edges?
        return new ReteSearchStrategy(this, condition);
    }

    private final ReteNetwork network;

    /**
     * The reporter object.
     */
    static public final Reporter reporter = Reporter.register(ReteSearchEngine.class);

    /**
     * The reporter for the transitionOccurred method
     */
    static public final Reporter transitionOccurredReporter =
        reporter.register("transitionOccurred()");

}
