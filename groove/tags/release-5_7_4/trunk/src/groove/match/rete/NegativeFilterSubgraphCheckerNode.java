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

import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import groove.grammar.rule.RuleElement;
import groove.match.rete.ReteNetwork.ReteStaticMapping;
import groove.util.collect.TreeHashSet;

/**
 * Performs a negative filtering on the incoming matches from the left, such
 * that all matches coming from left that do not join/overlap with the matches
 * coming from the right antecedent will be passed through, while others will be
 * stopped.
 *
 * If a subsequent right match arrives at a later time the already sent-down
 * matches on the left will be domino deleted down-wards.
 *
 * @author Arash Jalali
 * @version $Revision $
 */
public class NegativeFilterSubgraphCheckerNode<LeftMatchType extends AbstractReteMatch,RightMatchType extends AbstractReteMatch>
    extends SubgraphCheckerNode<LeftMatchType,RightMatchType> {

    private NegativeFilterSubgraphCheckerNode<LeftMatchType,RightMatchType>.BidirectionalInhibitionMap inhibitionMap;

    /**
     * Creates a negative filter
     *
     * @param network The RETE network to which this node is to belong
     * @param left  The antecedent that sends down positive matches
     * @param right The antecedent that sends down negative/inhibiter matches
     */
    public NegativeFilterSubgraphCheckerNode(ReteNetwork network, ReteStaticMapping left,
        ReteStaticMapping right, boolean keepPrefix) {
        super(network, left, right, keepPrefix);
        this.inhibitionMap = this.new BidirectionalInhibitionMap();
    }

    @Override
    protected void copyPatternsFromAntecedents() {
        RuleElement[] leftAntecedentPattern = this.getAntecedents()
            .get(0)
            .getPattern();
        this.pattern = new RuleElement[leftAntecedentPattern.length];

        for (int i = 0; i < leftAntecedentPattern.length; i++) {
            this.pattern[i] = leftAntecedentPattern[i];
        }

    }

    @Override
    @SuppressWarnings("unchecked")
    protected void selectJoinStrategy(ReteStaticMapping left, ReteStaticMapping right) {
        if (!(left.getNNode() instanceof AbstractPathChecker)
            && (right.getNNode() instanceof AbstractPathChecker)) {
            this.joinStrategy =
                (JoinStrategy<LeftMatchType,RightMatchType>) new AbstractJoinWithPathStrategy<ReteSimpleMatch>(
                    this) {

                    @Override
                    public AbstractReteMatch construct(ReteSimpleMatch left, RetePathMatch right) {
                        assert right == null;
                        return new ReteSimpleMatch(this.subgraphChecker,
                            this.subgraphChecker.getOwner()
                                .isInjective(),
                            left);
                    }

                };
        } else if ((left.getNNode() instanceof AbstractPathChecker)
            && (right.getNNode() instanceof AbstractPathChecker)) {
            this.joinStrategy =
                (JoinStrategy<LeftMatchType,RightMatchType>) new AbstractJoinWithPathStrategy<RetePathMatch>(
                    this) {

                    @Override
                    public AbstractReteMatch construct(RetePathMatch left, RetePathMatch right) {
                        assert right == null;
                        return new ReteSimpleMatch(this.subgraphChecker,
                            this.subgraphChecker.getOwner()
                                .isInjective(),
                            left);
                    }

                };
        } else {
            throw new UnsupportedOperationException();
        }
    }

    //TODO ARASH: remove this once on-demand is properly implemented for this
    @Override
    public void receive(ReteNetworkNode source, int repeatIndex, AbstractReteMatch subgraph) {
        receiveAndProcess(source, repeatIndex == 0, subgraph);
    }

    //TODO ARASH: remove this once on-demand is properly implemented for this
    @Override
    public boolean demandUpdate() {
        boolean result = false;
        if (!this.isUpToDate()) {
            for (ReteNetworkNode nnode : this.getAntecedents()) {
                result = result || nnode.demandUpdate();
            }
            setUpToDate(true);
        }
        return result;
    }

    @Override
    protected boolean unbufferMatch(ReteNetworkNode source, boolean first,
        AbstractReteMatch subgraph) {
        return false;
    }

    //TODO ARASH: remove this once on-demand is properly implemented for this
    @Override
    public int demandOneMatch() {
        demandUpdate();
        return 0;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected int receiveAndProcess(ReteNetworkNode source, boolean first,
        AbstractReteMatch subgraph) {
        int result = 0;
        if (isLeftAntecedent(source, first)) {
            result = receivePositiveMatch((LeftMatchType) subgraph);
        } else {
            result = receiveNegativeMatch(source, (RightMatchType) subgraph);
        }
        return result;
    }

    /**
     * @return The number of positive matches that had to be
     * retracted because of this new negative match.
     */
    private int receiveNegativeMatch(ReteNetworkNode source, RightMatchType subgraph) {
        int result = 0;
        this.rightMemory.add(subgraph);
        subgraph.addContainerCollection(this.rightMemory);
        subgraph.addDominoListener(new DominoEventListener() {

            @SuppressWarnings("unchecked")
            @Override
            public void matchRemoved(AbstractReteMatch match) {
                negativeMatchRemoved((RightMatchType) match);
            }

        });
        for (LeftMatchType positiveMatch : this.leftMemory) {
            if (this.joinStrategy.test(positiveMatch, subgraph)) {
                if (this.inhibitionMap.getInhibitorsOf(positiveMatch)
                    .size() == 0) {
                    positiveMatch.dominoDeleteAfter();
                }
                this.inhibitionMap.add(subgraph, positiveMatch);
                result++;
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private void negativeMatchRemoved(RightMatchType negativeMatch) {
        Set<AbstractReteMatch> positiveMatches = this.inhibitionMap.removeInhibitor(negativeMatch);
        if (positiveMatches != null) {
            for (AbstractReteMatch positiveMatch : positiveMatches) {
                if (this.inhibitionMap.getInhibitorsOf(positiveMatch)
                    .size() == 0) {
                    assert this.leftMemory.contains(positiveMatch);
                    AbstractReteMatch combined =
                        this.joinStrategy.construct((LeftMatchType) positiveMatch, null);
                    if (combined != null) {
                        passDownMatchToSuccessors(combined);
                    }
                }
            }
        }
    }

    private void positiveMatchRemoved(RightMatchType match) {
        this.inhibitionMap.removePositiveMatch(match);
    }

    /**
     * Receives a positive match. Saves it in the left memory and if it is not
     * inhibited by any of the negative matches on the right memeory, it will be
     * passed down.
     *
     * @param subgraph the newly received positive match
     * @return 0 if the received match was not sent down due to inhibition,
     * 1 otherwise.
     */
    private int receivePositiveMatch(LeftMatchType subgraph) {
        int result = 0;
        this.leftMemory.add(subgraph);
        subgraph.addContainerCollection(this.leftMemory);
        subgraph.addDominoListener(new DominoEventListener() {

            @SuppressWarnings("unchecked")
            @Override
            public void matchRemoved(AbstractReteMatch match) {
                positiveMatchRemoved((RightMatchType) match);
            }
        });

        boolean canBePassedDown = true;
        for (RightMatchType negativeMatch : this.rightMemory) {
            if (this.joinStrategy.test(subgraph, negativeMatch)) {
                canBePassedDown = false;
                this.inhibitionMap.add(negativeMatch, subgraph);
            }
        }
        if (canBePassedDown) {
            result = 1;
            AbstractReteMatch combined = this.joinStrategy.construct(subgraph, null);
            passDownMatchToSuccessors(combined);
        }
        return result;
    }

    /**
     * A bidirectional internal record of inhibition of positive matches by
     * negative matches used by the negative filter subgraph-checker.
     *
     * @author Arash Jalali
     * @version $Revision $
     */
    protected class BidirectionalInhibitionMap {
        private HashMap<AbstractReteMatch,Set<AbstractReteMatch>> positiveToNegative =
            new HashMap<>();
        private HashMap<AbstractReteMatch,Set<AbstractReteMatch>> negativeToPositive =
            new HashMap<>();

        /**
         * Records an inhibiter-inhibited relationship between two matches.
         *
         * @param inhibitor The negative match
         * @param inhibited The positive match.
         */
        public void add(AbstractReteMatch inhibitor, AbstractReteMatch inhibited) {
            Set<AbstractReteMatch> inhibitorSet = this.positiveToNegative.get(inhibited);
            if (inhibitorSet == null) {
                inhibitorSet = new TreeHashSet<>();
                this.positiveToNegative.put(inhibited, inhibitorSet);
            }
            inhibitorSet.add(inhibitor);

            Set<AbstractReteMatch> inhibitedSet = this.negativeToPositive.get(inhibitor);
            if (inhibitedSet == null) {
                inhibitedSet = new TreeHashSet<>();
                this.negativeToPositive.put(inhibitor, inhibitedSet);
            }
            inhibitedSet.add(inhibited);
        }

        /**
         * The set of positive matches inhibited by a given negative (inhibitor)
         * match
         * @param inhibitor The negative match. Is assumed not to be <code>null</code>.
         * @return A set of {@link AbstractReteMatch} objects inhibited by the
         * parameter <code>inhibitor</code>, or {@link Collections#emptySet()}
         * if the given inhibiter does not inhibit any positive matches.
         */
        public Set<AbstractReteMatch> getInhibitedBy(AbstractReteMatch inhibitor) {
            assert inhibitor != null;
            Set<AbstractReteMatch> result = this.negativeToPositive.get(inhibitor);
            if (result == null) {
                result = Collections.emptySet();
            }
            return result;
        }

        /**
         * The set of negative matches inhibiting a given positive (inhibited)
         * match
         * @param positive The positive match. Is assumed not to be <code>null</code>.
         * @return A set of {@link AbstractReteMatch} objects inhibiting the
         * parameter <code>positive</code>, or {@link Collections#emptySet()}
         * if the given positive match is not inhibited by any negative matches.
         */
        public Set<AbstractReteMatch> getInhibitorsOf(AbstractReteMatch positive) {
            assert positive != null;
            Set<AbstractReteMatch> result = this.positiveToNegative.get(positive);
            if (result == null) {
                result = Collections.emptySet();
            }
            return result;
        }

        /**
         * Determines if the given positive match is currently inhibited by one
         * or more negative matches.
         * @param positiveMatch The given positive match
         */
        public boolean isInhibited(AbstractReteMatch positiveMatch) {
            return this.positiveToNegative.containsKey(positiveMatch);
        }

        /**
         * Removes a negative match from the inhibition map.
         * @param inhibitor The negative match
         * @return The set of positive matches inhibited by this negative match
         * prior to its removal.
         */
        public Set<AbstractReteMatch> removeInhibitor(AbstractReteMatch inhibitor) {
            Set<AbstractReteMatch> inhibitedPositiveMatches =
                this.negativeToPositive.get(inhibitor);
            if (inhibitedPositiveMatches != null) {
                for (AbstractReteMatch positiveMatch : inhibitedPositiveMatches) {
                    Set<AbstractReteMatch> s = this.positiveToNegative.get(positiveMatch);
                    boolean b = s.remove(inhibitor);
                    assert b;
                    if (s.size() == 0) {
                        this.positiveToNegative.remove(positiveMatch);
                    }
                }
                this.negativeToPositive.remove(inhibitor);
            }
            return inhibitedPositiveMatches;
        }

        /**
         * Removes a given positive match from the map.
         *
         * All the negative matches that have only been inhibiting this
         * given positive match will also be removed from this inhibition map
         * as a result.
         *
         * @param positive The positive match to be removed.
         */
        public void removePositiveMatch(AbstractReteMatch positive) {
            Set<AbstractReteMatch> inhibitors = this.positiveToNegative.get(positive);
            if (inhibitors != null) {
                this.positiveToNegative.remove(positive);
                for (AbstractReteMatch negative : inhibitors) {
                    Set<AbstractReteMatch> otherPositives = this.negativeToPositive.get(negative);
                    assert otherPositives != null;
                    otherPositives.remove(positive);
                    if (otherPositives.size() == 0) {
                        this.negativeToPositive.remove(negative);
                    }

                }
            }
        }
    }

    @Override
    public String toString() {
        return super.toString().replaceAll("Subgraph Checker", "Negative Filter");
    }

}
