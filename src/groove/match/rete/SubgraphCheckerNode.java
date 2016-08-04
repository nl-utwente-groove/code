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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import groove.grammar.host.HostNode;
import groove.grammar.host.HostNodeSet;
import groove.grammar.rule.RuleEdge;
import groove.grammar.rule.RuleElement;
import groove.grammar.rule.RuleNode;
import groove.graph.Node;
import groove.match.rete.LookupEntry.Role;
import groove.match.rete.ReteNetwork.ReteStaticMapping;
import groove.match.rete.RetePathMatch.EmptyPathMatch;

/**
 *
 * @author  Arash Jalali
 * @version $Revision$
 */
public class SubgraphCheckerNode<LeftMatchType extends AbstractReteMatch,RightMatchType extends AbstractReteMatch>
    extends ReteNetworkNode implements ReteStateSubscriber {

    /**
     * left on-demand buffer
     */
    protected HashSet<LeftMatchType> leftOnDemandBuffer = new HashSet<>();

    /**
     * memory containing the matches received from the left antecedent
     */
    protected HashSet<LeftMatchType> leftMemory = new HashSet<>();

    /**
     * left on-demand buffer
     */
    protected HashSet<RightMatchType> rightOnDemandBuffer = new HashSet<>();

    /**
     * memory containing the matches received from the right antecedent
     */
    protected HashSet<RightMatchType> rightMemory = new HashSet<>();

    /**
     * This is a fast lookup table for equality checking of left and
     * right match during runtime. This is basically the join condition
     * that this subgraph represents.
     */
    private LookupEntry[] leftLookupTable;
    /**
     * This is a fast lookup table for equality checking of left and
     * right match during runtime. This is basically the join condition
     * that this subgraph represents.
     */
    private LookupEntry[] rightLookupTable;

    /**
     * The static subgraph pattern represented by this checker
     */
    protected RuleElement[] pattern;

    /** This flag indicates if the special prefix link of matches coming
     * the left antecedent should be copied for the combined matches
     * that are passed down the network. */
    protected boolean shouldPreservePrefix = false;

    /**
     * The strategy object that performs the join and test operations
     * based on the given left and right match types.
     */
    protected JoinStrategy<LeftMatchType,RightMatchType> joinStrategy;

    /**
     * Creates a subgraph checker from two statically-matched antecedents.
     * @param network the RETE network this subgraph-checker belongs to.
     * @param left the left antecedent along with its matching to the LHS of some rule
     * @param right the left antecedent along with its matching to the LHS of some rule
     * @param keepPrefix Indicates if indicates if the special prefix link of matches
     *        coming the left antecedent should be copied for the combined matches that
     *        are passed down the network.
     */
    protected SubgraphCheckerNode(ReteNetwork network, ReteStaticMapping left,
        ReteStaticMapping right, boolean keepPrefix) {
        super(network);
        this.shouldPreservePrefix = keepPrefix;
        this.getOwner()
            .getState()
            .subscribe(this);
        left.getNNode()
            .addSuccessor(this);
        this.addAntecedent(left.getNNode());
        right.getNNode()
            .addSuccessor(this);
        this.addAntecedent(right.getNNode());
        copyPatternsFromAntecedents();
        staticJoin(left, right);
        selectJoinStrategy(left, right);
    }

    /**
     * Picks the right join strategy given the left and right
     * antecedents.
     *
     * @param left The static (build-time) mapping of the left antecedent
     * @param right The static (build-time) mapping of the right antecedent
     */
    @SuppressWarnings("unchecked")
    protected void selectJoinStrategy(ReteStaticMapping left, ReteStaticMapping right) {
        if ((!(left.getNNode() instanceof AbstractPathChecker)
            && !(right.getNNode() instanceof AbstractPathChecker))) {

            this.joinStrategy =
                (JoinStrategy<LeftMatchType,RightMatchType>) new AbstractSimpleTestJoinStrategy<ReteSimpleMatch,ReteSimpleMatch>(
                    this) {

                    @Override
                    public AbstractReteMatch construct(ReteSimpleMatch left,
                        ReteSimpleMatch right) {

                        return ReteSimpleMatch.merge(this.subgraphChecker,
                            left,
                            right,
                            this.subgraphChecker.getOwner()
                                .isInjective(),
                            this.subgraphChecker.shouldPreservePrefix);
                    }

                };

        } else if (right.getNNode() instanceof AbstractPathChecker) {
            this.joinStrategy =
                (JoinStrategy<LeftMatchType,RightMatchType>) new AbstractJoinWithPathStrategy<AbstractReteMatch>(
                    this) {

                    @Override
                    public AbstractReteMatch construct(AbstractReteMatch left,
                        RetePathMatch right) {
                        return (right == null || right.isEmpty())
                            ? this.mergeWithEmptyPath(left, right) : ReteSimpleMatch.merge(
                                this.subgraphChecker, left, right, this.subgraphChecker.getOwner()
                                    .isInjective(),
                                this.subgraphChecker.shouldPreservePrefix);
                    }

                };

        } else if (left.getNNode() instanceof AbstractPathChecker) {
            this.joinStrategy =
                (JoinStrategy<LeftMatchType,RightMatchType>) new AbstractSimpleTestJoinStrategy<RetePathMatch,AbstractReteMatch>(
                    this) {

                    @Override
                    public AbstractReteMatch construct(RetePathMatch left,
                        AbstractReteMatch right) {

                        return ReteSimpleMatch.merge(this.subgraphChecker,
                            left,
                            right,
                            this.subgraphChecker.getOwner()
                                .isInjective(),
                            this.subgraphChecker.shouldPreservePrefix);
                    }

                };

        } else {

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
     * Creates a new subgraph checker n-node from two left and right antecedent mappings.
     *
     * @param network The RETE network this n-node is to belong to.
     * @param left The left antecedent.
     * @param right The right antecedent.
     */
    public SubgraphCheckerNode(ReteNetwork network, ReteStaticMapping left,
        ReteStaticMapping right) {
        this(network, left, right, false);
    }

    /**
     * Builds the static pattern of this subgraph based
     * on that of the antecedents'.
     */
    protected void copyPatternsFromAntecedents() {
        assert this.getAntecedents()
            .size() == 2;
        RuleElement[] leftAntecedentPattern = this.getAntecedents()
            .get(0)
            .getPattern();
        RuleElement[] rightAntecedentPattern = this.getAntecedents()
            .get(1)
            .getPattern();
        this.pattern =
            new RuleElement[leftAntecedentPattern.length + rightAntecedentPattern.length];
        int i = 0;
        for (; i < leftAntecedentPattern.length; i++) {
            this.pattern[i] = leftAntecedentPattern[i];
        }
        for (; i < this.pattern.length; i++) {
            this.pattern[i] = rightAntecedentPattern[i - leftAntecedentPattern.length];
        }
    }

    private void staticJoin(ReteStaticMapping leftMap, ReteStaticMapping rightMap) {
        Set<RuleNode> s1 = leftMap.getLhsNodes();
        Set<RuleNode> s2 = rightMap.getLhsNodes();
        HashSet<RuleNode> intersection = new HashSet<>();
        for (RuleNode n1 : s1) {
            if (s2.contains(n1)) {
                intersection.add(n1);
            }
        }
        this.leftLookupTable = new LookupEntry[intersection.size()];
        this.rightLookupTable = new LookupEntry[intersection.size()];
        int i = 0;
        for (RuleNode n : intersection) {
            this.leftLookupTable[i] = leftMap.locateNode(n);
            this.rightLookupTable[i] = rightMap.locateNode(n);
            i++;
        }
    }

    @Override
    public void addSuccessor(ReteNetworkNode nnode) {
        boolean isValid = (nnode instanceof SubgraphCheckerNode)
            || (nnode instanceof ConditionChecker) || (nnode instanceof DataOperatorChecker)
            || (nnode instanceof DisconnectedSubgraphChecker);
        assert isValid;

        if (isValid) {
            super.addSuccessor(nnode);
        }
    }

    /**
     * Determines if a given antecedent n-node is the left antecedent or the
     * right one. This method is a utility function
     * just for enhancing readability and ease of use.
     *
     *
     * @param antecedent The antecedent in question
     * @param first Indicates if this is the leftmost occurrence of the antecedent
     * @return <code>true</code> if <code>antecedent</code> is the left antecedent,
     * <code>false</code> otherwise.
     */
    protected boolean isLeftAntecedent(ReteNetworkNode antecedent, boolean first) {
        if (getAntecedents().get(0) == getAntecedents().get(1)) {
            return first;
        } else {
            return getAntecedents().get(0) == antecedent;
        }
    }

    /**
     * Takes a match from the buffer associated with the given nodes.
     * @return <code>true</code> if something was unbuffered, <code>false</code>
     * otherwise.
     */
    @SuppressWarnings("unchecked")
    protected boolean unbufferMatch(ReteNetworkNode source, boolean first,
        AbstractReteMatch subgraph) {
        assert!subgraph.isDeleted();
        return isLeftAntecedent(source, first)
            ? this.unbufferMatch(source, this.leftOnDemandBuffer, (LeftMatchType) subgraph)
            : this.unbufferMatch(source, this.rightOnDemandBuffer, (RightMatchType) subgraph);
    }

    private <E extends AbstractReteMatch> boolean unbufferMatch(ReteNetworkNode source,
        HashSet<E> memory, E match) {
        assert!match.isDeleted();
        boolean result = memory.remove(match);
        if (result) {
            match.removeContainerCollection(memory);
        }
        return result;
    }

    /**
     * Buffers the the given match in the proper on-demand buffer
     */
    @SuppressWarnings("unchecked")
    protected void bufferMatch(ReteNetworkNode source, boolean first, AbstractReteMatch subgraph) {

        if (isLeftAntecedent(source, first)) {
            bufferMatch(source, this.leftOnDemandBuffer, (LeftMatchType) subgraph);
        } else {
            bufferMatch(source, this.rightOnDemandBuffer, (RightMatchType) subgraph);
        }
    }

    /**
     * Buffers the given match in the given ondemand buffer
     */
    protected <E extends AbstractReteMatch> void bufferMatch(ReteNetworkNode source,
        HashSet<E> memory, E match) {
        memory.add(match);
        match.addContainerCollection(memory);
        this.invalidate();
    }

    /** Returns the left hand lookup table. */
    public LookupEntry[] getLeftLookupTable() {
        return this.leftLookupTable;
    }

    /** Returns the right hand lookup table. */
    public LookupEntry[] getRightLookupTable() {
        return this.rightLookupTable;
    }

    /**
     * Receives a new subgraph match (resulting from an ADD operation)
     * of type {@link AbstractReteMatch} from an antecedent. Whether it is immediately
     * processed or buffered depends on the RETE network's update propagation mode.
     *
     * @param source The n-node that is calling this method.
     * @param repeatIndex This parameter is basically a counter over repeating antecedents.
     *        If <code>source</code> checks against more than one sub-component of this subgraph
     *        , it will repeat in the list of antecedents. In such a case this
     *        parameter specifies which of those components is calling this method, which
     *        could be any value from 0 to k-1, which k is the number of
     *        times <code>source</code> occurs in the list of antecedents.
     *
     * @param subgraph The subgraph match found by <code>source</code>.
     */
    @Override
    public void receive(ReteNetworkNode source, int repeatIndex, AbstractReteMatch subgraph) {
        if (this.getOwner()
            .isInOnDemandMode()) {
            bufferMatch(source, repeatIndex == 0, subgraph);
        } else {
            receiveAndProcess(source, repeatIndex == 0, subgraph);
        }
    }

    /**
     * Receives a new subgraph match (resulting from an ADD operation)
     * of type {@link AbstractReteMatch} from an antecedent and immediately
     * processes the match for possible merge with already existing matches
     * from the opposite side.
     *
     * @param source The n-node that is calling this method.
     * @param first if {@code true}, this is the first occurrence of {@code source}
     *
     * @param subgraph The subgraph match found by <code>source</code>.
     * @return The number of new combined matches generated
     */
    @SuppressWarnings("unchecked")
    protected int receiveAndProcess(ReteNetworkNode source, boolean first,
        AbstractReteMatch subgraph) {
        int result = 0;
        HashSet<AbstractReteMatch> memory;
        HashSet<AbstractReteMatch> otherMemory;
        boolean sourceIsLeft = isLeftAntecedent(source, first);

        memory = (HashSet<AbstractReteMatch>) (sourceIsLeft ? this.leftMemory : this.rightMemory);

        otherMemory = (HashSet<AbstractReteMatch>) ((memory == this.leftMemory) ? this.rightMemory
            : this.leftMemory);

        memory.add(subgraph);
        subgraph.addContainerCollection(memory);
        for (AbstractReteMatch gOther : otherMemory) {
            LeftMatchType left = (LeftMatchType) (sourceIsLeft ? subgraph : gOther);
            RightMatchType right = (RightMatchType) (sourceIsLeft ? gOther : subgraph);

            if (this.joinStrategy.test(left, right)) {
                result++;
                AbstractReteMatch combined = this.joinStrategy.construct(left, right);
                if (combined != null) {
                    passDownMatchToSuccessors(combined);
                }
            }
        }
        return result;
    }

    @Override
    public boolean equals(ReteNetworkNode node) {
        return node == this;
    }

    /**
     * This is an auxiliary utility method to quickly get the
     * antecedent of a subgraph-checker other than the one specified
     * by the argument oneAntecedent
     * @param oneAntecedent The antecedent whose opposite we need.
     * @return the antecedent that is not the one passed in the oneAntecedent parameter.
     */
    public ReteNetworkNode getOtherAntecedent(ReteNetworkNode oneAntecedent) {
        ReteNetworkNode result = null;
        List<ReteNetworkNode> aSet = this.getAntecedents();
        assert aSet.size() > 1;
        for (ReteNetworkNode n : aSet) {
            if (n != oneAntecedent) {
                result = n;
                break;
            }
        }
        return result;
    }

    /**
     * This method checks if the the antecedents of this
     * subgraph checker can be combined into this, assuming that
     * those antecedents are already checking valid subgraphs of some
     * graph rule individually.
     * This is a construction-time method only.
     * @return {@literal true} if the combination is possible, {@literal false} otherwise.
     */
    public boolean checksValidSubgraph(ReteStaticMapping oneMapping,
        ReteStaticMapping otherMapping) {
        assert this.getAntecedents()
            .contains(oneMapping.getNNode())
            && this.getAntecedents()
                .contains(otherMapping.getNNode());

        //the two mappings <code>oneMapping</code> and <code>otherMapping
        //could be taken as the mapping for the left antecedent and right successor
        //respectively, or vice versa. In case of the two antecedents being
        //the same n-node both combinations should be checked.
        ReteStaticMapping[][] combinationChoices;

        if (oneMapping.getNNode() != otherMapping.getNNode()) {
            ReteStaticMapping lm = (this.getAntecedents()
                .get(0) == oneMapping.getNNode()) ? oneMapping : otherMapping;
            ReteStaticMapping rm = (lm == oneMapping) ? otherMapping : oneMapping;

            combinationChoices = new ReteStaticMapping[][] {{lm, rm}};
        } else {
            combinationChoices =
                new ReteStaticMapping[][] {{oneMapping, otherMapping}, {otherMapping, oneMapping}};
        }
        boolean result = true;

        Set<RuleEdge> s1 = new HashSet<>();
        for (RuleElement e : oneMapping.getElements()) {
            if (e instanceof RuleEdge) {
                s1.add((RuleEdge) e);
            }
        }

        //No shared edges
        for (RuleElement e : otherMapping.getElements()) {
            if (e instanceof RuleEdge) {
                if (s1.contains(e)) {
                    result = false;
                    break;
                }
            }
        }

        Set<RuleNode> nodes1 = oneMapping.getLhsNodes();
        Set<RuleNode> sharedNodes = new HashSet<>();
        for (RuleNode n : otherMapping.getLhsNodes()) {
            if (nodes1.contains(n)) {
                sharedNodes.add(n);
            }
        }

        for (int i = 0; result && (i < combinationChoices.length); i++) {
            ReteStaticMapping leftMapping = combinationChoices[i][0];
            ReteStaticMapping rightMapping = combinationChoices[i][1];

            Set<RuleNode> tempSharedNodes = sharedNodes;

            for (int j = 0; j < this.leftLookupTable.length; j++) {
                LookupEntry leftEntry = this.leftLookupTable[j];
                LookupEntry rightEntry = this.rightLookupTable[j];

                Node leftMappedValue = leftEntry.lookup(leftMapping.getElements());
                Node rightMappedValue = rightEntry.lookup(rightMapping.getElements());

                result = leftMappedValue.equals(rightMappedValue);
                if (!result) {
                    break;
                } else {
                    tempSharedNodes.remove(leftMappedValue);
                }
            }

            //if tempSharedNodes is not empty is means the two bindings
            //have more nodes in common that is going to be checked using the equalities
            result = result && tempSharedNodes.isEmpty();
        }
        return result;
    }

    /**
     * For subgraph-checkers the value of size is defined to be the number
     * of the edges in the associated subgraph.
     *
     * This is a construction-time method only.
     */
    @Override
    public int size() {
        return this.pattern.length;
    }

    /**
     * Determines if this subgraph-checker merges two completely disjoint
     * subgraphs. This happens when a rule has a disconnected LHS.
     * @return <code>true</code> if this subgraph checker is joining to disjoint
     *         components (i.e. they have no overlapping nodes).
     */
    public boolean isDisjointMerger() {
        return this.leftLookupTable.length == 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("- Subgraph Checker\n");
        sb.append("--  Edge Set-\n");

        for (int i = 0; i < this.pattern.length; i++) {
            sb.append("-- " + i + " -" + this.pattern[i].toString()
                .replace(':', '-') + "\n");
        }
        sb.append("--- Equalities-\n");
        for (int i = 0; i < this.leftLookupTable.length; i++) {
            sb.append(
                String.format("--- left[%d]%s = right[%d]%s \n",
                    this.leftLookupTable[i].getPos(),
                    (this.leftLookupTable[i].getRole() == Role.NODE) ? ""
                        : ((this.leftLookupTable[i].getRole() == Role.SOURCE) ? ".source"
                            : ".target"),
                    this.rightLookupTable[i].getPos(),
                    (this.rightLookupTable[i].getRole() == Role.NODE) ? ""
                        : ((this.rightLookupTable[i].getRole() == Role.SOURCE) ? ".source"
                            : ".target")));
        }
        return sb.toString();
    }

    @Override
    public void clear() {
        this.leftOnDemandBuffer.clear();
        this.leftMemory.clear();
        this.rightOnDemandBuffer.clear();
        this.rightMemory.clear();
    }

    @Override
    public List<? extends Object> initialize() {
        return null;
    }

    @Override
    public RuleElement[] getPattern() {
        return this.pattern;
    }

    @Override
    public boolean demandUpdate() {
        boolean result = false;
        if (!this.isUpToDate()) {
            if (this.getOwner()
                .isInOnDemandMode()) {

                for (ReteNetworkNode nnode : this.getAntecedents()) {
                    nnode.demandUpdate();
                }
                result = (this.leftOnDemandBuffer.size() + this.rightOnDemandBuffer.size()) > 0;

                if (result) {
                    int newMatchCounter = 0;
                    result = false;
                    for (AbstractReteMatch m : this.leftOnDemandBuffer) {
                        assert!m.isDeleted();
                        m.removeContainerCollection(this.leftOnDemandBuffer);
                        newMatchCounter += this.receiveAndProcess(m.getOrigin(), true, m);
                    }
                    this.leftOnDemandBuffer.clear();
                    boolean first = (this.getAntecedents()
                        .get(0) != this.getAntecedents()
                            .get(1));
                    for (AbstractReteMatch m : this.rightOnDemandBuffer) {
                        assert!m.isDeleted();
                        m.removeContainerCollection(this.rightOnDemandBuffer);
                        newMatchCounter += this.receiveAndProcess(m.getOrigin(), first, m);
                    }
                    this.rightOnDemandBuffer.clear();
                    result = newMatchCounter > 0;
                }
            }
            setUpToDate(true);
        }
        return result;
    }

    @Override
    public int demandOneMatch() {
        int result = 0;
        if (!this.isUpToDate()) {
            if (this.getOwner()
                .isInOnDemandMode()) {
                HashSet<? extends AbstractReteMatch> theBuffer;
                ReteNetworkNode theAntecedent;
                theBuffer = this.rightOnDemandBuffer;
                theAntecedent = this.getAntecedents()
                    .get(1);
                boolean first = (this.getAntecedents()
                    .get(0) != this.getAntecedents()
                        .get(1));
                do {
                    do {
                        if (theBuffer.size() == 0) {
                            if (theAntecedent.demandOneMatch() == 0) {
                                break;
                            }
                        }
                        AbstractReteMatch m = theBuffer.iterator()
                            .next();
                        theBuffer.remove(m);
                        m.removeContainerCollection(theBuffer);
                        result += this.receiveAndProcess(m.getOrigin(), first, m);
                    } while (result == 0);

                    if ((result == 0) && (theBuffer == this.rightOnDemandBuffer)) {
                        theBuffer = this.leftOnDemandBuffer;
                        theAntecedent = this.getAntecedents()
                            .get(0);
                        first = true;
                    } else {
                        break;
                    }
                } while (result == 0);
            }
        }
        return result;
    }

    /**
     * Performs ordinary overlap tests of nodes based on the
     * node-equality set of a given subgraph-checker.
     *
     * @author Arash Jalali
     * @version $Revision $
     */
    protected static abstract class AbstractSimpleTestJoinStrategy<LT extends AbstractReteMatch,RT extends AbstractReteMatch>
        implements JoinStrategy<LT,RT> {

        /**
         * The subgraph-checker node to which this join strategy belongs
         */

        protected SubgraphCheckerNode<?,?> subgraphChecker;

        /**
         * @param sgChecker The subgraph-checker node to which this strategy belongs
         */
        public AbstractSimpleTestJoinStrategy(SubgraphCheckerNode<?,?> sgChecker) {
            this.subgraphChecker = sgChecker;
        }

        @Override
        public boolean test(LT left, RT right) {
            boolean allEqualitiesSatisfied = true;
            boolean injective = this.subgraphChecker.getOwner()
                .isInjective();

            Object[] leftUnits = left.getAllUnits();
            Object[] rightUnits = right.getAllUnits();
            HostNodeSet nodesLeft = (injective) ? left.getNodes() : null;
            HostNodeSet nodesRight = (injective) ? right.getNodes() : null;

            int i = 0;
            for (; i < this.subgraphChecker.leftLookupTable.length; i++) {
                LookupEntry leftEquality = this.subgraphChecker.leftLookupTable[i];
                LookupEntry rightEquality = this.subgraphChecker.rightLookupTable[i];
                Node n1 = leftEquality.lookup(leftUnits);
                Node n2 = rightEquality.lookup(rightUnits);
                allEqualitiesSatisfied = n1.equals(n2);

                if (!allEqualitiesSatisfied) {
                    break;
                } else if (injective) {
                    assert nodesLeft != null && nodesRight != null; // implied by injective
                    nodesLeft.remove(n1);
                    nodesRight.remove(n1);
                }
            }

            //Final injective Check
            if (allEqualitiesSatisfied && injective) {
                //if any of the nodes that do not participate in the equalities
                //of this subgraph checker map to the same host nodes
                //then injectivity is violated
                allEqualitiesSatisfied =
                    AbstractReteMatch.checkInjectiveOverlap(nodesLeft, nodesRight);
            }

            return allEqualitiesSatisfied;
        }

    }

    /**
     * A joint strategy for joining with path matches that takes into account
     * the possibility of the path match being an {@link EmptyPathMatch}.
     *
     * @author Arash Jalali
     * @version $Revision $
     */
    protected static abstract class AbstractJoinWithPathStrategy<LT extends AbstractReteMatch>
        extends AbstractSimpleTestJoinStrategy<LT,RetePathMatch> {

        /**
         * The index of the start node of this path in
         * the left-match's units. The index is a pair of integers
         * in an array, the 0-index element point to the row in the
         * units array, and 1-index determining if it is the source/target
         * of the edge, or -1 if it is a node.
         *
         * It will be <code>null</code> if the start index of the path-edge
         * does not join with the left in this
         * strategy's subgraph.
         */
        protected LookupEntry leftPathStartEntry = null;

        /**
         * The index of the end node of this path in
         * the left-match's units.The index is a pair of integers
         * in an array, the 0-index element point to the row in the
         * units array, and 1-index determining if it is the source/target
         * of the edge, or -1 if it is a node.
         *
         * It will be <code>null</code> if the end index of the path-edge
         * does not join with the left in this
         * strategy's subgraph.
         */
        protected LookupEntry leftPathEndEntry = null;

        /**
         * @param sgChecker The subgraph-checker node to which this strategy belongs
         */
        public AbstractJoinWithPathStrategy(SubgraphCheckerNode<?,?> sgChecker) {
            super(sgChecker);
            for (int i = 0; i < sgChecker.leftLookupTable.length; i++) {
                LookupEntry leftEquality = sgChecker.leftLookupTable[i];
                LookupEntry rightEquality = sgChecker.rightLookupTable[i];
                //This equality is about the start point of the path edge
                if (rightEquality.getRole() == Role.SOURCE) {
                    this.leftPathStartEntry = leftEquality;
                    if (sgChecker.leftLookupTable.length == 1) {
                        this.leftPathEndEntry = this.leftPathStartEntry;
                    }
                }
                //This equality is about the start point of the path edge
                else if (rightEquality.getRole() == Role.TARGET) {
                    this.leftPathEndEntry = leftEquality;
                    //If the path checker antecedent is a loop edge
                    if (sgChecker.leftLookupTable.length == 1) {
                        this.leftPathStartEntry = this.leftPathEndEntry;
                    }
                }
            }
        }

        @Override
        public boolean test(LT left, RetePathMatch right) {
            if (right.isEmpty()) {
                return testJointPointNodesEquality(left);
            } else {
                return super.test(left, right);
            }
        }

        private boolean testJointPointNodesEquality(LT left) {
            assert this.subgraphChecker.leftLookupTable.length <= 2;
            if (this.subgraphChecker.leftLookupTable.length == 2) {
                Object[] leftUnits = left.getAllUnits();
                LookupEntry leftEntry = this.subgraphChecker.leftLookupTable[0];
                Node node1 = leftEntry.lookup(leftUnits);
                leftEntry = this.subgraphChecker.leftLookupTable[1];
                Node node2 = leftEntry.lookup(leftUnits);
                return node1.equals(node2);
            } else {
                return true;
            }
        }

        /**
         * Merges the given left match with an empty match
         * padding the match units with the proper overlap nodes.
         *
         * @param left the given left match
         */
        public ReteSimpleMatch mergeWithEmptyPath(LT left, RetePathMatch emptyMatch) {
            assert emptyMatch instanceof EmptyPathMatch;
            Object[] leftUnits = left.getAllUnits();
            HostNode node1 = (HostNode) this.leftPathStartEntry.lookup(leftUnits);
            assert node1 == this.leftPathEndEntry.lookup(leftUnits);

            return new ReteSimpleMatch(this.subgraphChecker, this.subgraphChecker.getOwner()
                .isInjective(), left,
                new Object[] {new RetePathMatch.EmptyPathMatch(emptyMatch.getOrigin(), node1)});
        }
    }

    /**
     * Factory method to create the properly typed subgraph-checker based on
     * the given left and right mappings.
     * @param network The owner RETE network
     * @param left The static mapping of the left antecedent
     * @param right the static mapping of the right antecedent
     * @param keepPrefix Indicates if indicates if the special prefix link of matches
     *        coming the left antecedent should be copied for the combined matches that
     *        are passed down the network.
     */
    public static SubgraphCheckerNode<?,?> create(ReteNetwork network, ReteStaticMapping left,
        ReteStaticMapping right, boolean keepPrefix) {
        if ((left.getNNode() instanceof AbstractPathChecker)
            && (right.getNNode() instanceof AbstractPathChecker)) {
            return new SubgraphCheckerNode<RetePathMatch,RetePathMatch>(network, left, right,
                keepPrefix);
        } else if (!(left.getNNode() instanceof AbstractPathChecker)
            && (right.getNNode() instanceof AbstractPathChecker)) {
            return new SubgraphCheckerNode<ReteSimpleMatch,RetePathMatch>(network, left, right,
                keepPrefix);
        } else if ((left.getNNode() instanceof AbstractPathChecker)
            && !(right.getNNode() instanceof AbstractPathChecker)) {
            return new SubgraphCheckerNode<RetePathMatch,ReteSimpleMatch>(network, left, right,
                keepPrefix);
        } else if (!(left.getNNode() instanceof AbstractPathChecker)
            && !(right.getNNode() instanceof AbstractPathChecker)) {
            return new SubgraphCheckerNode<ReteSimpleMatch,ReteSimpleMatch>(network, left, right,
                keepPrefix);
        } else {
            throw new UnsupportedOperationException("Antecent types are not supported.");
        }
    }

    @Override
    public void updateBegin() {
        //Do nothing
    }

    @Override
    public void updateEnd() {
        //Do nothing
    }

}
