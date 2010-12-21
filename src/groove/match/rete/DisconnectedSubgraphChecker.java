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

import groove.graph.Element;
import groove.match.rete.ReteNetwork.ReteStaticMapping;
import groove.trans.HostEdge;
import groove.trans.HostNode;
import groove.util.TreeHashSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * A special check node that collects the matches of connected components of rules/conditions
 * with a disconnected LHS. Each ConditionChecker with a disconnected LHS has exactly one 
 * antecedent of type <code>DisconnectedSubgraphChecker</code>
 *  
 * @author Arash Jalali
 * @version $Revision $
 */
public class DisconnectedSubgraphChecker extends ReteNetworkNode implements
        StateSubscriber {

    private Element[] pattern;

    /**
     * Collection of partial matches separately kept based on the antecedent
     * that they belong to.
     */
    protected HashMap<ReteNetworkNode,TreeHashSet<ReteMatch>> partialMatches =
        new HashMap<ReteNetworkNode,TreeHashSet<ReteMatch>>();

    /**
     * Creates a subgraph-checker from a list of antecedents, each of which
     * check a disconnected component.
     * 
     * @param network The RETE network that is to own this node.
     * @param antecedents The list of the antecedents.
     */
    public DisconnectedSubgraphChecker(ReteNetwork network,
            List<ReteStaticMapping> antecedents) {
        super(network);
        assert antecedents.size() > 1;
        this.getOwner().getState().subscribe(this);
        connectToAntecedents(antecedents);
    }

    private void connectToAntecedents(List<ReteStaticMapping> antecedents) {
        List<Element> tempPatternList = new ArrayList<Element>();
        //We sort the mappings based on the associated n-nodes 
        //so that those with the same n-node would be next to 
        //one another and so identically repeating antecedents
        //would be adjacent to one another in the antecedents 
        //list of this condition checker.
        Collections.sort(antecedents, new Comparator<ReteStaticMapping>() {
            @Override
            public int compare(ReteStaticMapping o1, ReteStaticMapping o2) {
                return o1.getNNode().hashCode() - o2.getNNode().hashCode();
            }
        });
        for (ReteStaticMapping ant : antecedents) {
            Element[] pat = ant.getNNode().getPattern();
            for (int j = 0; j < pat.length; j++) {
                tempPatternList.add(pat[j]);
            }
            this.addAntecedent(ant.getNNode());
            ant.getNNode().addSuccessor(this);
        }
        this.pattern = new Element[tempPatternList.size()];
        tempPatternList.toArray(this.pattern);
    }

    /**
     * Receives a matched edge/node during runtime from an EdgeChecker/NodeChecker 
     * antecedent and takes appropriate action according to the <code>action<code>
     * parameter.
     * 
     * @param source The n-node that is calling this method
     * @param repeatIndex This parameter is basically a counter over repeating antecedents.
     *        If <code>source</code> checks against more than one disjoint component, it will
     *        repeat in the list of the current n-nodes antecedents. In such a case this
     *        parameter specifies which of those components is calling this method, which
     *        could be any value from 0 to k-1, which k is the number of 
     *        times <code>source</code> occurs in the list of antecedents. 
     *         
     * @param mu The match object found by <code>source</code>.
     * @param action Determines if the match is added or removed.
     */
    public void receive(ReteNetworkNode source, int repeatIndex, Element mu,
            Action action) {
        ReteMatch sg =
            (mu instanceof HostEdge) ? new ReteMatch(source, (HostEdge) mu,
                this.getOwner().isInjective()) : new ReteMatch(source,
                (HostNode) mu, this.getOwner().isInjective());

        if (action == Action.ADD) {
            this.receive(source, repeatIndex, sg);
        } else {
            TreeHashSet<ReteMatch> memory = getPartialMatchesFor(source);
            if (memory.contains(sg)) {
                ReteMatch m = sg;
                sg = memory.put(sg);
                memory.remove(m);
                sg.dominoDelete(null);
            }
        }
    }

    /**
     * Receives a match of a connected subgraph component of an otherwise
     * disconnected LHS represented by this object. 
     *  
     * @param source The n-node that is calling this method
     * @param repeatIndex This parameter is basically a counter over repeating antecedents.
     *        If <code>source</code> checks against more than one disjoint component, it will
     *        repeat in the list of the current n-nodes antecedents. In such a case this
     *        parameter specifies which of those components is calling this method, which
     *        could be any value from 0 to k-1, which k is the number of 
     *        times <code>source</code> occurs in the list of antecedents. 
     * @param match The match object found by <code>source</code>.
     */
    public void receive(ReteNetworkNode source, int repeatIndex, ReteMatch match) {
        produceAndSendDownNewMatches(source, repeatIndex, match);
    }

    /**
     * Takes a newly received partial match and then produces and forwards down the
     * RETE network the combination of this partial match with the already found 
     * partial matches from other disjoint components.
     * 
     * @param antecedent The antecedent that has produced the new partial match
     * @param repeatIndex This parameter is basically a counter over repeating antecedents.
     *        If <code>antecedent</code> checks against more than one disjoint component, it will
     *        repeat in the list of the current n-nodes antecedents. In such a case this
     *        parameter specifies which of those components is calling this method, which
     *        could be any value from 0 to k-1, which k is the number of 
     *        times <code>antecedent</code> occurs in the list of antecedents. 
     * @param m The newly received partial match
     */
    protected void produceAndSendDownNewMatches(ReteNetworkNode antecedent,
            int repeatIndex, ReteMatch m) {

        TreeHashSet<ReteMatch> c = this.getPartialMatchesFor(antecedent);
        if (c.isEmpty() || (repeatIndex == 0)) {
            c.add(m);
            m.addContainerCollection(c);
        }

        List<ReteMatch> completeMatches =
            this.makeWholeMatchesIfPossible(antecedent, repeatIndex, m);

        if (completeMatches != null) {
            for (ReteMatch completeMatch : completeMatches) {
                ReteNetworkNode previous = null;
                int repeatedSuccessorIndex = 0;
                for (ReteNetworkNode n : this.getSuccessors()) {
                    repeatedSuccessorIndex =
                        (n != previous) ? 0 : (repeatedSuccessorIndex + 1);
                    if (n instanceof ConditionChecker) {
                        ((ConditionChecker) n).receive(this, completeMatch);
                    } else if (n instanceof SubgraphCheckerNode) {
                        ((SubgraphCheckerNode) n).receive(this,
                            repeatedSuccessorIndex, completeMatch);
                    }
                    previous = n;
                }
            }
        }
    }

    /**
     * Generates all the matches resulting from combining a new partial match
     * with the already found partial matches of other antecedents.
     * 
     * @param antecedent The antecedent that has produced the new partial match
     * @param repeatIndex This parameter is basically a counter over repeating antecedents.
     *        If <code>antecedent</code> checks against more than one disjoint component, it will
     *        repeat in the list of the current n-nodes antecedents. In such a case this
     *        parameter specifies which of those components is calling this method, which
     *        could be any value from 0 to k-1, which k is the number of 
     *        times <code>antecedent</code> occurs in the list of antecedents. 
     * @param newMatch The newly received partial match
     * @return The list of complete matches generated by combining <code>newMatch</code>
     *          with other existing partial matches of other disjoint components.
     */
    @SuppressWarnings("unchecked")
    protected List<ReteMatch> makeWholeMatchesIfPossible(
            ReteNetworkNode antecedent, int repeatIndex, ReteMatch newMatch) {
        List<ReteMatch> result = null;
        //This is the index of the antecedent that we need to jump over in
        //combining matches because we only need on match out of it and that's the
        //one represented by the variable newMatch
        int jumpIndex = this.getAntecedents().indexOf(antecedent) + repeatIndex;

        //it is possible to combine m with other partial matches into a whole match
        //if and only if other antecedents have found at least one partial match
        boolean isPossible = true;

        for (ReteNetworkNode nnode : this.getAntecedents()) {
            if (antecedent != nnode) {
                isPossible = getPartialMatchesFor(nnode).size() > 0;
                if (!isPossible) {
                    break;
                }
            }
        }

        if (isPossible) {
            result = new ArrayList<ReteMatch>();
            Iterator<ReteMatch>[] partialMatchIterators =
                new Iterator[this.getAntecedents().size()];
            for (int i = 0; i < partialMatchIterators.length; i++) {
                if (i != jumpIndex) {
                    partialMatchIterators[i] =
                        getPartialMatchesFor(this.getAntecedents().get(i)).iterator();
                }
            }
            ReteMatch[] subMatches =
                new ReteMatch[partialMatchIterators.length];
            subMatches[jumpIndex] = newMatch;
            int j = 0;
            boolean injective = this.getOwner().isInjective();
            while (j >= 0) {
                while (j < partialMatchIterators.length) {
                    if (partialMatchIterators[j] != null) {
                        if (partialMatchIterators[j].hasNext()) {
                            if (j != jumpIndex) {
                                subMatches[j] = partialMatchIterators[j].next();
                            }
                        } else {
                            break;
                        }
                    }
                    j++;
                }
                if (j == partialMatchIterators.length) {
                    ReteMatch m = ReteMatch.merge(this, subMatches, injective);
                    if (m != null) {
                        result.add(m);
                    }
                    j--;
                    while (j >= 0) {
                        if (partialMatchIterators[j] != null) {
                            if (!partialMatchIterators[j].hasNext()) {
                                partialMatchIterators[j] =
                                    getPartialMatchesFor(
                                        this.getAntecedents().get(j)).iterator();
                                j--;
                            } else {
                                break;
                            }
                        } else {
                            j--;
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * @param antecedent The antecedent the partial matches of which is needed. 
     * @return The list of already received partial matches for a given antecedent.
     */
    protected TreeHashSet<ReteMatch> getPartialMatchesFor(
            ReteNetworkNode antecedent) {
        TreeHashSet<ReteMatch> result = this.partialMatches.get(antecedent);
        if (result == null) {
            result = new TreeHashSet<ReteMatch>();
            this.partialMatches.put(antecedent, result);
        }
        return result;
    }

    @Override
    public boolean equals(ReteNetworkNode node) {
        return node == this;
    }

    @Override
    public Element[] getPattern() {
        return this.pattern;
    }

    @Override
    public int size() {
        return this.pattern.length;
    }

    @Override
    public void clear() {
        this.partialMatches.clear();
    }

    @Override
    public List<? extends Object> initialize() {
        // TODO Auto-generated method stub
        return null;
    }

}
