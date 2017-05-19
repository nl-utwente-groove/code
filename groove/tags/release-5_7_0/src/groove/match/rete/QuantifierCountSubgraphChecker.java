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

import java.util.HashMap;
import java.util.Set;

import groove.match.rete.ReteNetwork.ReteStaticMapping;
import groove.match.rete.ReteSimpleMatch.ReteCountMatch;
import groove.util.collect.TreeHashSet;

/**
 * A special subgraph-checker that combines matches from a given subgraph
 * of any kind (one the left) with count matches coming from a quantifier-count
 * checker (on the right).
 *
 *
 * The reason why this special subgraph checker has been devised is that
 * the merge algorithm is different in two ways:
 * 1- When a match comes from the left, it should only be matched against
 *    at most one count match on the right (so there's no need to go through
 *    all the count matches) because each count match corresponds with a unique
 *    anchor.
 *
 * 2- This subgraph checker guarantees that every match coming from the left
 *    matches against some count match, which at the very worst case would be
 *    the default ZERO count match.
 *
 * One important thing to note about this particular subgraph checker
 * is that the left and right memories should not be interpreted
 * as a typical subgraph-checker's memories are. The left memory
 * contains only those matches that have arrived but have not, for some reason,
 * not yet bound to any count matches. The right memory is basically not used
 * here.
 *
 *
 * @author Arash Jalali
 * @version $Revision $
 */
public class QuantifierCountSubgraphChecker
    extends SubgraphCheckerNode<AbstractReteMatch,ReteCountMatch>implements DominoEventListener {

    /**
     * This is the special dummy (count=0) match
     * that this subgraph-checker falls back on
     * when no proper count match is found for an
     * incoming match from the left.
     *
     */
    protected ReteCountMatch dummyCountMatch = null;

    /** The static mapping of a quantifier counter checker node. */
    protected QuantifierCountChecker countCheckerNode = null;

    /**
     * A map that shows which left-matches have been already bound
     * to count matches. This is to make the removal of count-bindings
     * faster.
     */
    protected HashMap<ReteCountMatch,Set<AbstractReteMatch>> countBindings =
        new HashMap<>();

    /**
     * @param network The RETE network to which this n-node is to belong to.
     * @param left  The static mapping of a some checker node.
     * @param right The static mapping of a quantifier counter
     *              checker node.
     */
    public QuantifierCountSubgraphChecker(ReteNetwork network, ReteStaticMapping left,
        ReteStaticMapping right) {
        super(network, left, right);
        assert right.getNNode() instanceof QuantifierCountChecker;
        this.countCheckerNode = (QuantifierCountChecker) right.getNNode();
    }

    @Override
    protected int receiveAndProcess(ReteNetworkNode source, boolean first,
        AbstractReteMatch match) {
        if (isLeftAntecedent(source, first)) {
            return receiveLeftMatch(source, first, match);
        } else {
            return receiveRightMatch((ReteCountMatch) match);
        }
    }

    private int receiveRightMatch(ReteCountMatch countMatch) {
        assert!this.countBindings.containsKey(countMatch);
        int result = 0;
        countMatch.addDominoListener(this);
        Set<AbstractReteMatch> toBeDeletedFromLeft = new TreeHashSet<>();
        if (!countMatch.isDummy()) {
            this.rightMemory.add(countMatch);
            countMatch.addContainerCollection(this.rightMemory);
        }

        for (AbstractReteMatch left : this.leftMemory) {
            if (this.joinStrategy.test(left, countMatch)) {
                AbstractReteMatch combined = this.joinStrategy.construct(left, countMatch);
                if (combined != null) {
                    result++;
                    toBeDeletedFromLeft.add(left);
                    mapToCountMatch(left, countMatch);
                    passDownMatchToSuccessors(combined);
                }
            }
        }
        for (AbstractReteMatch m : toBeDeletedFromLeft) {
            m.removeContainerCollection(this.leftMemory);
            this.leftMemory.remove(m);
        }
        toBeDeletedFromLeft.clear();
        if (countMatch.isDummy()) {
            this.dummyCountMatch = countMatch;
            //After the dummy match is received and processed,
            //there should be no more matches left in the left memory
            //as it should always contain the as-yet not processed
            //left matches.
            assert this.leftMemory.size() == 0;
        }
        return result;
    }

    private int receiveLeftMatch(ReteNetworkNode source, boolean first, AbstractReteMatch left) {
        int result = 0;
        if (isCountBindingPossible()) {
            for (ReteCountMatch right : this.rightMemory) {
                if (this.joinStrategy.test(left, right)) {
                    AbstractReteMatch combined = this.joinStrategy.construct(left, right);
                    if (combined != null) {
                        result = 1;
                        mapToCountMatch(left, right);
                        passDownMatchToSuccessors(combined);
                        break;
                    }
                }
            }
            if ((result == 0) && (this.dummyCountMatch != null)) {
                AbstractReteMatch combined =
                    this.joinStrategy.construct(left, this.dummyCountMatch);
                assert combined != null;
                result = 1;
                mapToCountMatch(left, this.dummyCountMatch);
                passDownMatchToSuccessors(combined);
            }

        } else {
            this.leftMemory.add(left);
            left.addContainerCollection(this.leftMemory);
        }
        return result;
    }

    private void mapToCountMatch(AbstractReteMatch left, ReteCountMatch countMatch) {
        Set<AbstractReteMatch> boundMatches = this.countBindings.get(countMatch);
        if (boundMatches == null) {
            boundMatches = new TreeHashSet<>();
            this.countBindings.put(countMatch, boundMatches);
        }
        boundMatches.add(left);
        left.addContainerCollection(boundMatches);
    }

    @Override
    protected void selectJoinStrategy(ReteStaticMapping left, ReteStaticMapping right) {
        if (!(left.getNNode() instanceof QuantifierCountChecker)
            && (right.getNNode() instanceof QuantifierCountChecker)) {
            this.joinStrategy = new JoinWithCountStrategy<>(this);
        } else if ((left.getNNode() instanceof QuantifierCountChecker)
            && !(right.getNNode() instanceof QuantifierCountChecker)) {
            throw new UnsupportedOperationException(
                String.format("Left is of type %s and right is of type %s",
                    left.getNNode()
                        .getClass()
                        .toString(),
                    right.getNNode()
                        .getClass()
                        .toString()));
        }
    }

    /**
     * Determines
     * whether we have all the count matches or not to perform
     * binding left matches with counts.
     * If it is <code>true</code> then it means
     * we have all the count matches that could possibly
     * exist. Otherwise, it means that we do not have
     * the complete list of count matches and that
     * any incoming left matches should be held
     * for further processing at a later time.
     */
    protected boolean isCountBindingPossible() {
        return (this.countCheckerNode.isConstant()) || (this.dummyCountMatch != null);
    }

    /**
     * This one is called when a count match is
     * removed.
     */
    @Override
    public void matchRemoved(AbstractReteMatch match) {
        ReteCountMatch countMatch = (ReteCountMatch) match;
        Set<AbstractReteMatch> boundMatches = this.countBindings.get(countMatch);
        if (boundMatches != null) {
            for (AbstractReteMatch left : boundMatches) {
                this.leftMemory.add(left);
                left.addContainerCollection(this.leftMemory);
            }
            boundMatches.clear();
            this.countBindings.remove(countMatch);
        }
        if (countMatch.isDummy()) {
            this.dummyCountMatch = null;
        }
    }

    @Override
    public void clear() {
        super.clear();
        this.dummyCountMatch = null;
        this.countBindings.clear();
    }

    /**
     * The join strategy specifically capable of joining any match from the left
     * with a count match from the right, properly taking care of
     * joining with dummy matches if needed.
     *
     * @author Arash Jalali
     * @version $Revision $
     */
    protected static class JoinWithCountStrategy<LT extends AbstractReteMatch>
        extends AbstractSimpleTestJoinStrategy<LT,ReteCountMatch> {

        /**
         * A n by 2 matrix (n is the number of anchors nodes)
         * the i-th row of which says where in the left match
         * the i-th element of the right count match can be found.
         * This is to make it easy to copy anchor node matches
         * when mergin a normal match with a dummy count match,
         * because a dummy count match does not actually contain
         * the node images of any particular anchor.
         */
        final LookupEntry[] leftAnchorLookup;

        /**
         * Creates a join-strategy for a particular subgraph-checker
         * whose right antecedent is a quantifier counter node
         */
        public JoinWithCountStrategy(SubgraphCheckerNode<?,?> sgChecker) {
            super(sgChecker);
            assert!(sgChecker.getAntecedents()
                .get(0) instanceof QuantifierCountChecker)
                && (sgChecker.getAntecedents()
                    .get(1) instanceof QuantifierCountChecker);
            QuantifierCountChecker qcc = (QuantifierCountChecker) sgChecker.getAntecedents()
                .get(1);
            this.leftAnchorLookup = new LookupEntry[qcc.getPattern().length - 1];
            LookupEntry[] leftTable = this.subgraphChecker.getLeftLookupTable();
            LookupEntry[] rightTable = this.subgraphChecker.getRightLookupTable();
            for (int i = 0; i < leftTable.length; i++) {
                LookupEntry leftEntry = leftTable[i];
                LookupEntry rightEntry = rightTable[i];
                this.leftAnchorLookup[rightEntry.getPos()] = leftEntry;
            }
        }

        @Override
        public boolean test(LT left, ReteCountMatch right) {
            if (right.isDummy()) {
                return true;
            } else {
                return super.test(left, right);
            }
        }

        @Override
        public AbstractReteMatch construct(LT left, ReteCountMatch right) {
            if (right != null && right.isDummy()) {
                return right.dummyMerge(this.subgraphChecker,
                    left,
                    this.subgraphChecker.shouldPreservePrefix,
                    this.leftAnchorLookup);
            } else {
                return ReteSimpleMatch.merge(this.subgraphChecker,
                    left,
                    right,
                    this.subgraphChecker.shouldPreservePrefix,
                    false);
            }
        }

    }

}
