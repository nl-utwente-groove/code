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

import groove.match.rete.ReteNetwork.ReteStaticMapping;
import groove.match.rete.RetePathMatch.EmptyPathMatch;
import groove.rel.LabelVar;
import groove.trans.HostEdge;
import groove.trans.HostElement;
import groove.trans.HostNode;
import groove.trans.RuleEdge;
import groove.trans.RuleElement;
import groove.trans.RuleNode;
import groove.util.TreeHashSet;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 
 * @author  Arash Jalali
 * @version $Revision$
 */
public class SubgraphCheckerNode<LeftMatchType extends AbstractReteMatch,RightMatchType extends AbstractReteMatch>
        extends ReteNetworkNode implements StateSubscriber {

    /**
     * left on-demand buffer
     */
    protected TreeHashSet<LeftMatchType> leftOnDemandBuffer =
        new TreeHashSet<LeftMatchType>();

    /**
     * memory containing the matches received from the left antecedent
     */
    protected TreeHashSet<LeftMatchType> leftMemory =
        new TreeHashSet<LeftMatchType>();

    /**
     * left on-demand buffer
     */
    protected TreeHashSet<RightMatchType> rightOnDemandBuffer =
        new TreeHashSet<RightMatchType>();

    /**
     * memory containing the matches received from the right antecedent
     */
    protected TreeHashSet<RightMatchType> rightMemory =
        new TreeHashSet<RightMatchType>();

    /**
     * This is a fast lookup table for equality checking of left and
     * right match during runtime. This is basically the join condition
     * that this subgraph represents.
     */
    private int[][] fastEqualityLookupTable;

    /**
     * The static subgraph pattern represented by this checker
     */
    protected RuleElement[] pattern;

    //This flag indicates if the special prefix link of matches coming
    //the left antecedent should be copied for the combined matches
    //that are passed down the network.
    private boolean shouldPreservePrefix = false;

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
        this.getOwner().getState().subscribe(this);
        left.getNNode().addSuccessor(this);
        this.addAntecedent(left.getNNode());
        right.getNNode().addSuccessor(this);
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
    protected void selectJoinStrategy(ReteStaticMapping left,
            ReteStaticMapping right) {
        if (!(left.getNNode() instanceof AbstractPathChecker)
            && !(right.getNNode() instanceof AbstractPathChecker)) {
            this.joinStrategy =
                (JoinStrategy<LeftMatchType,RightMatchType>) new AbstractSimpleTestJoinStrategy<ReteSimpleMatch,ReteSimpleMatch>(
                    this) {

                    @Override
                    public AbstractReteMatch construct(ReteSimpleMatch left,
                            ReteSimpleMatch right) {

                        return left.merge(this.subgraphChecker, right,
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

                        return (right.isEmpty())
                                ? this.mergeWithEmptyPath(left)
                                : ReteSimpleMatch.merge(
                                    this.subgraphChecker,
                                    left,
                                    right,
                                    this.subgraphChecker.getOwner().isInjective(),
                                    this.subgraphChecker.shouldPreservePrefix);
                    }

                };

        } else {
            throw new UnsupportedOperationException(String.format(
                "Left is of type %s and right is of type %s",
                left.getNNode().getClass().toString(),
                right.getNNode().getClass().toString()));
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
        assert this.getAntecedents().size() == 2;
        RuleElement[] leftAntecedentPattern =
            this.getAntecedents().get(0).getPattern();
        RuleElement[] rightAntecedentPattern =
            this.getAntecedents().get(1).getPattern();
        this.pattern =
            new RuleElement[leftAntecedentPattern.length
                + rightAntecedentPattern.length];
        int i = 0;
        for (; i < leftAntecedentPattern.length; i++) {
            this.pattern[i] = leftAntecedentPattern[i];
        }
        for (; i < this.pattern.length; i++) {
            this.pattern[i] =
                rightAntecedentPattern[i - leftAntecedentPattern.length];
        }
    }

    private void staticJoin(ReteStaticMapping leftMap,
            ReteStaticMapping rightMap) {
        Set<RuleNode> s1 = leftMap.getLhsNodes();
        Set<RuleNode> s2 = rightMap.getLhsNodes();
        HashSet<RuleNode> intersection = new HashSet<RuleNode>();
        for (RuleNode n1 : s1) {
            if (s2.contains(n1)) {
                intersection.add(n1);
            }
        }
        this.fastEqualityLookupTable = new int[intersection.size()][4];
        int i = 0;
        for (RuleNode n : intersection) {
            int[] a = leftMap.locateNode(n);
            this.fastEqualityLookupTable[i][0] = a[0];
            this.fastEqualityLookupTable[i][1] = a[1];
            a = rightMap.locateNode(n);
            this.fastEqualityLookupTable[i][2] = a[0];
            this.fastEqualityLookupTable[i++][3] = a[1];
        }
    }

    @Override
    public void addSuccessor(ReteNetworkNode nnode) {
        boolean isValid =
            (nnode instanceof SubgraphCheckerNode)
                || (nnode instanceof ConditionChecker);
        assert isValid;

        if (isValid) {
            super.addSuccessor(nnode);
        }
    }

    /**
     * Receives a matched edge/node during runtime from an EdgeChecker/NodeChecker antecedent
     * and passes along down the RETE network based on the rules of the algorithm.
     * 
     * This method uses the {@link #receive(ReteNetworkNode, int, AbstractReteMatch)} variant
     * to do the actual job.
     * 
     * @param source The n-node that is calling this method.
     * @param repeatIndex This parameter is basically a counter over repeating antecedents.
     *        If <code>source</code> checks against more than one sub-component of this subgraph
     *        , it will repeat in the list of antecedents. In such a case this
     *        parameter specifies which of those components is calling this method, which
     *        could be any value from 0 to k-1, which k is the number of 
     *        times <code>source</code> occurs in the list of antecedents. 
     *         
     * @param mu The graph element found by <code>source</code>.
     * @param action Determines if the match is added or removed.     */
    @SuppressWarnings("unchecked")
    public void receive(ReteNetworkNode source, int repeatIndex,
            HostElement mu, Action action) {

        AbstractReteMatch sg =
            (mu instanceof HostEdge) ? new ReteSimpleMatch(source,
                (HostEdge) mu, this.getOwner().isInjective())
                    : new ReteSimpleMatch(source, (HostNode) mu,
                        this.getOwner().isInjective());
        if (action == Action.ADD) {
            this.receive(source, repeatIndex, sg);
        } else if (!this.getOwner().isInOnDemandMode()
            || !unbufferMatch(source, repeatIndex, sg)) {

            if (isLeftAntecedent(source, repeatIndex)) {
                startDominoDeletion(this.leftMemory, (LeftMatchType) sg);
            } else {
                startDominoDeletion(this.rightMemory, (RightMatchType) sg);
            }
        }
    }

    /**
     * Receives a matched edge bound to a variable during runtime from an EdgeChecker antecedent
     * and passes along down the RETE network based on the rules of the algorithm.
     * 
     * This method uses the {@link #receive(ReteNetworkNode, int, AbstractReteMatch)} variant
     * to do the actual job.
     * 
     * @param source The n-node that is calling this method.
     * @param repeatIndex This parameter is basically a counter over repeating antecedents.
     *        If <code>source</code> checks against more than one sub-component of this subgraph
     *        , it will repeat in the list of antecedents. In such a case this
     *        parameter specifies which of those components is calling this method, which
     *        could be any value from 0 to k-1, which k is the number of 
     *        times <code>source</code> occurs in the list of antecedents. 
     *         
     * @param mu The edge found by <code>source</code>.
     * @param variable The variable to which this <code>mu</code>'s label is bound.
     * @param action Determines if the match is added or removed.     */
    @SuppressWarnings("unchecked")
    public void receiveBoundEdge(ReteNetworkNode source, int repeatIndex,
            HostEdge mu, LabelVar variable, Action action) {

        AbstractReteMatch sg =
            new ReteSimpleMatch(source, mu, variable,
                this.getOwner().isInjective());
        if (action == Action.ADD) {
            this.receive(source, repeatIndex, sg);
        } else if (!this.getOwner().isInOnDemandMode()
            || !unbufferMatch(source, repeatIndex, sg)) {
            if (isLeftAntecedent(source, repeatIndex)) {
                startDominoDeletion(this.leftMemory, (LeftMatchType) sg);
            } else {
                startDominoDeletion(this.rightMemory, (RightMatchType) sg);
            }
        }
    }

    /**
     * Determines if a given antecedent n-node is the left antecedent or the
     * right one. This method is a utility function
     * just for enhancing readability and ease of use.
     * 
     * 
     * @param antecedent The antecedent in question
     * @param repeatIndex The repeat index in case the antecedent is both
     * @return <code>true</code> if <code>antecedent</code> is the left antecedent,
     * <code>false</code> otherwise.
     */
    protected boolean isLeftAntecedent(ReteNetworkNode antecedent,
            int repeatIndex) {
        return ((getAntecedents().get(0) != getAntecedents().get(1)) && (getAntecedents().get(
            0) == antecedent))
            || ((getAntecedents().get(0) == getAntecedents().get(1)) && (repeatIndex == 0));
    }

    private <E extends AbstractReteMatch> void startDominoDeletion(
            TreeHashSet<E> memory, E match) {
        if (memory.contains(match)) {
            AbstractReteMatch m = match;
            match = memory.put(match);
            memory.remove(m);
            match.dominoDelete(null);
        }
    }

    /**
     * Takes a match from the buffer associated with the given nodes.
     * @return <code>true</code> if something was unbuffered, <code>false</code>
     * otherwise.
     */
    @SuppressWarnings("unchecked")
    protected boolean unbufferMatch(ReteNetworkNode source, int repeatIndex,
            AbstractReteMatch subgraph) {
        assert !subgraph.isDeleted();
        return isLeftAntecedent(source, repeatIndex) ? this.unbufferMatch(
            source, this.leftOnDemandBuffer, (LeftMatchType) subgraph)
                : this.unbufferMatch(source, this.rightOnDemandBuffer,
                    (RightMatchType) subgraph);
    }

    private <E extends AbstractReteMatch> boolean unbufferMatch(
            ReteNetworkNode source, TreeHashSet<E> memory, E match) {
        assert !match.isDeleted();
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
    protected void bufferMatch(ReteNetworkNode source, int repeatIndex,
            AbstractReteMatch subgraph) {

        if (isLeftAntecedent(source, repeatIndex)) {
            bufferMatch(source, this.leftOnDemandBuffer,
                (LeftMatchType) subgraph);
        } else {
            bufferMatch(source, this.rightOnDemandBuffer,
                (RightMatchType) subgraph);
        }
    }

    /**
     * Buffers the given match in the given ondemand buffer
     */
    protected <E extends AbstractReteMatch> void bufferMatch(
            ReteNetworkNode source, TreeHashSet<E> memory, E match) {
        memory.add(match);
        match.addContainerCollection(memory);
        this.invalidate();
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
    public void receive(ReteNetworkNode source, int repeatIndex,
            AbstractReteMatch subgraph) {
        if (this.getOwner().isInOnDemandMode()) {
            bufferMatch(source, repeatIndex, subgraph);
        } else {
            receiveAndProcess(source, repeatIndex, subgraph);
        }
    }

    /**
     * Receives a new subgraph match (resulting from an ADD operation)
     * of type {@link AbstractReteMatch} from an antecedent and immediately
     * processes the match for possible merge with already existing matches
     * from the opposite side.
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
     * @return The number of new combined matches generated     
     */
    @SuppressWarnings("unchecked")
    protected int receiveAndProcess(ReteNetworkNode source, int repeatIndex,
            AbstractReteMatch subgraph) {
        int result = 0;
        TreeHashSet<AbstractReteMatch> memory;
        TreeHashSet<AbstractReteMatch> otherMemory;
        boolean sourceIsLeft = isLeftAntecedent(source, repeatIndex);

        memory =
            (TreeHashSet<AbstractReteMatch>) (sourceIsLeft ? this.leftMemory
                    : this.rightMemory);

        otherMemory =
            (TreeHashSet<AbstractReteMatch>) ((memory == this.leftMemory)
                    ? this.rightMemory : this.leftMemory);

        memory.add(subgraph);
        subgraph.addContainerCollection(memory);
        for (AbstractReteMatch gOther : otherMemory) {
            LeftMatchType left =
                (LeftMatchType) (sourceIsLeft ? subgraph : gOther);
            RightMatchType right =
                (RightMatchType) (sourceIsLeft ? gOther : subgraph);

            if (this.joinStrategy.test(left, right)) {
                result++;
                AbstractReteMatch combined =
                    this.joinStrategy.construct(left, right);
                if (combined != null) {
                    passDownMatchToSuccessors(combined);
                }
            }
        }
        return result;
    }

    @Override
    @SuppressWarnings("rawtypes")
    protected void passDownMatchToSuccessors(AbstractReteMatch m) {
        ReteNetworkNode previous = null;
        int repeatedSuccessorIndex = 0;
        for (ReteNetworkNode n : this.getSuccessors()) {
            repeatedSuccessorIndex =
                (n != previous) ? 0 : (repeatedSuccessorIndex + 1);
            if (n instanceof SubgraphCheckerNode) {
                ((SubgraphCheckerNode) n).receive(this, repeatedSuccessorIndex,
                    m);
            } else if (n instanceof ConditionChecker) {
                ((ConditionChecker) n).receive(m);
            } else if (n instanceof DisconnectedSubgraphChecker) {
                ((DisconnectedSubgraphChecker) n).receive(this,
                    repeatedSuccessorIndex, m);
            }
            previous = n;
        }
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
        assert this.getAntecedents().contains(oneMapping.getNNode())
            && this.getAntecedents().contains(otherMapping.getNNode());

        //the two mappings <code>oneMapping</code> and <code>otherMapping
        //could be taken as the mapping for the left antecedent and right successor
        //respectively, or vice versa. In case of the two antecedents being 
        //the same n-node both combinations should be checked.
        ReteStaticMapping[][] combinationChoices;

        if (oneMapping.getNNode() != otherMapping.getNNode()) {
            ReteStaticMapping lm =
                (this.getAntecedents().get(0) == oneMapping.getNNode())
                        ? oneMapping : otherMapping;
            ReteStaticMapping rm =
                (lm == oneMapping) ? otherMapping : oneMapping;

            combinationChoices = new ReteStaticMapping[][] {{lm, rm}};
        } else {
            combinationChoices =
                new ReteStaticMapping[][] { {oneMapping, otherMapping},
                    {otherMapping, oneMapping}};
        }
        boolean result = true;

        Set<RuleEdge> s1 = new HashSet<RuleEdge>();
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
        Set<RuleNode> sharedNodes = new HashSet<RuleNode>();
        for (RuleNode n : otherMapping.getLhsNodes()) {
            if (nodes1.contains(n)) {
                sharedNodes.add(n);
            }
        }

        for (int i = 0; result && (i < combinationChoices.length); i++) {
            ReteStaticMapping leftMapping = combinationChoices[i][0];
            ReteStaticMapping rightMapping = combinationChoices[i][1];

            RuleNode leftMappedValue;
            RuleNode rightMappedValue;

            Set<RuleNode> tempSharedNodes = sharedNodes;

            for (int j = 0; j < this.fastEqualityLookupTable.length; j++) {
                int[] leftIndices =
                    new int[] {this.fastEqualityLookupTable[j][0],
                        this.fastEqualityLookupTable[j][1]};
                int[] rightIndices =
                    new int[] {this.fastEqualityLookupTable[j][2],
                        this.fastEqualityLookupTable[j][3]};

                if (leftIndices[1] != -1) {
                    leftMappedValue =
                        (leftIndices[1] == 0)
                                ? ((RuleEdge) leftMapping.getElements()[leftIndices[0]]).source()
                                : ((RuleEdge) leftMapping.getElements()[leftIndices[0]]).target();
                } else {
                    leftMappedValue =
                        (RuleNode) leftMapping.getElements()[leftIndices[0]];
                }

                if (rightIndices[1] != -1) {
                    rightMappedValue =
                        (rightIndices[1] == 0)
                                ? ((RuleEdge) rightMapping.getElements()[rightIndices[0]]).source()
                                : ((RuleEdge) rightMapping.getElements()[rightIndices[0]]).target();
                } else {
                    rightMappedValue =
                        (RuleNode) rightMapping.getElements()[rightIndices[0]];
                }

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
        return this.fastEqualityLookupTable.length == 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("- Subgraph Checker\n");
        sb.append("--  Edge Set-\n");

        for (int i = 0; i < this.pattern.length; i++) {
            sb.append("-- " + i + " -" + this.pattern[i].toString() + "\n");
        }
        sb.append("--- Equalities-\n");
        for (int i = 0; i < this.fastEqualityLookupTable.length; i++) {
            sb.append(String.format("--- left[%d]%s = right[%d]%s \n",
                this.fastEqualityLookupTable[i][0],
                (this.fastEqualityLookupTable[i][1] < 0) ? ""
                        : ((this.fastEqualityLookupTable[i][1] == 0)
                                ? ".source" : ".target"),
                this.fastEqualityLookupTable[i][2],
                (this.fastEqualityLookupTable[i][3] < 0) ? ""
                        : ((this.fastEqualityLookupTable[i][3] == 0)
                                ? ".source" : ".target")));
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
            if (this.getOwner().isInOnDemandMode()) {

                for (ReteNetworkNode nnode : this.getAntecedents()) {
                    nnode.demandUpdate();
                }
                result =
                    (this.leftOnDemandBuffer.size() + this.rightOnDemandBuffer.size()) > 0;

                if (result) {
                    int newMatchCounter = 0;
                    result = false;
                    for (AbstractReteMatch m : this.leftOnDemandBuffer) {
                        assert !m.isDeleted();
                        m.removeContainerCollection(this.leftOnDemandBuffer);
                        newMatchCounter +=
                            this.receiveAndProcess(m.getOrigin(), 0, m);
                    }
                    this.leftOnDemandBuffer.clear();
                    int repeatIndex =
                        (this.getAntecedents().get(0) != this.getAntecedents().get(
                            1)) ? 0 : 1;
                    for (AbstractReteMatch m : this.rightOnDemandBuffer) {
                        assert !m.isDeleted();
                        m.removeContainerCollection(this.rightOnDemandBuffer);
                        newMatchCounter +=
                            this.receiveAndProcess(m.getOrigin(), repeatIndex,
                                m);
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
            if (this.getOwner().isInOnDemandMode()) {
                TreeHashSet<? extends AbstractReteMatch> theBuffer;
                ReteNetworkNode theAntecedent;
                theBuffer = this.rightOnDemandBuffer;
                theAntecedent = this.getAntecedents().get(1);
                int repeatIndex =
                    (this.getAntecedents().get(0) != this.getAntecedents().get(
                        1)) ? 0 : 1;
                do {
                    do {
                        if (theBuffer.size() == 0) {
                            if (theAntecedent.demandOneMatch() == 0) {
                                break;
                            }
                        }
                        AbstractReteMatch m = theBuffer.iterator().next();
                        theBuffer.remove(m);
                        m.removeContainerCollection(theBuffer);
                        result +=
                            this.receiveAndProcess(m.getOrigin(), repeatIndex,
                                m);
                    } while (result == 0);

                    if ((result == 0)
                        && (theBuffer == this.rightOnDemandBuffer)) {
                        theBuffer = this.leftOnDemandBuffer;
                        theAntecedent = this.getAntecedents().get(0);
                        repeatIndex = 0;
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
        @SuppressWarnings("rawtypes")
        protected SubgraphCheckerNode subgraphChecker;

        /**
         * @param sgChecker The subgraph-checker node to which this strategy belongs
         */
        @SuppressWarnings("rawtypes")
        public AbstractSimpleTestJoinStrategy(SubgraphCheckerNode sgChecker) {
            this.subgraphChecker = sgChecker;
        }

        @Override
        public boolean test(LT left, RT right) {
            boolean allEqualitiesSatisfied = true;
            boolean injective = this.subgraphChecker.getOwner().isInjective();

            HostElement[] leftUnits = left.getAllUnits();
            HostElement[] rightUnits = right.getAllUnits();
            Set<HostNode> nodesLeft = (injective) ? left.getNodes() : null;
            Set<HostNode> nodesRight = (injective) ? right.getNodes() : null;

            int i = 0;
            for (; i < this.subgraphChecker.fastEqualityLookupTable.length; i++) {
                int[] equality =
                    this.subgraphChecker.fastEqualityLookupTable[i];
                HostNode n1 =
                    (equality[1] >= 0) ? ((equality[1] == 0)
                            ? ((HostEdge) leftUnits[equality[0]]).source()
                            : ((HostEdge) leftUnits[equality[0]]).target())
                            : (HostNode) leftUnits[equality[0]];
                HostNode n2 =
                    (equality[3] >= 0) ? ((equality[3] == 0)
                            ? ((HostEdge) rightUnits[equality[2]]).source()
                            : ((HostEdge) rightUnits[equality[2]]).target())
                            : (HostNode) rightUnits[equality[2]];

                allEqualitiesSatisfied = n1.equals(n2);

                if (!allEqualitiesSatisfied) {
                    break;
                } else if (injective) {
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
                    AbstractReteMatch.checkInjectiveOverlap(nodesLeft,
                        nodesRight);
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
        protected int[] pathStartIndexInLeft = null;

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
        protected int[] pathEndIndexInLeft = null;

        /**
         * @param sgChecker The subgraph-checker node to which this strategy belongs
         */
        @SuppressWarnings("rawtypes")
        public AbstractJoinWithPathStrategy(SubgraphCheckerNode sgChecker) {
            super(sgChecker);
            for (int i = 0; i < sgChecker.fastEqualityLookupTable.length; i++) {
                int[] equality = sgChecker.fastEqualityLookupTable[i];
                //This equality is about the start point of the path edge
                if (equality[2] == 0) {
                    this.pathStartIndexInLeft =
                        new int[] {equality[0], equality[1]};
                }
                //This equality is about the start point of the path edge
                else if (equality[2] == 1) {
                    this.pathEndIndexInLeft =
                        new int[] {equality[0], equality[1]};
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
            assert this.subgraphChecker.fastEqualityLookupTable.length == 2;
            HostElement[] leftUnits = left.getAllUnits();
            int[] equality = this.subgraphChecker.fastEqualityLookupTable[0];

            HostNode node1 =
                (equality[1] >= 0) ? ((equality[1] == 0)
                        ? ((HostEdge) leftUnits[equality[0]]).source()
                        : ((HostEdge) leftUnits[equality[0]]).target())
                        : (HostNode) leftUnits[equality[0]];

            equality = this.subgraphChecker.fastEqualityLookupTable[1];
            HostNode node2 =
                (equality[1] >= 0) ? ((equality[1] == 0)
                        ? ((HostEdge) leftUnits[equality[0]]).source()
                        : ((HostEdge) leftUnits[equality[0]]).target())
                        : (HostNode) leftUnits[equality[0]];

            return node1.equals(node2);
        }

        /**
         * Merges the given left match with an empty match
         * padding the match units with the proper overlap nodes.
         * 
         * @param left the given left match
         */
        public ReteSimpleMatch mergeWithEmptyPath(LT left) {
            int[] equality = this.pathStartIndexInLeft;
            HostElement[] leftUnits = left.getAllUnits();
            HostNode node1 =
                (equality[1] >= 0) ? ((equality[1] == 0)
                        ? ((HostEdge) leftUnits[equality[0]]).source()
                        : ((HostEdge) leftUnits[equality[0]]).target())
                        : (HostNode) leftUnits[equality[0]];

            equality = this.pathEndIndexInLeft;
            HostNode node2 =
                (equality[1] >= 0) ? ((equality[1] == 0)
                        ? ((HostEdge) leftUnits[equality[0]]).source()
                        : ((HostEdge) leftUnits[equality[0]]).target())
                        : (HostNode) leftUnits[equality[0]];

            return new ReteSimpleMatch(this.subgraphChecker,
                this.subgraphChecker.getOwner().isInjective(), left,
                new HostElement[] {node1, node2});

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
    @SuppressWarnings("rawtypes")
    public static SubgraphCheckerNode create(ReteNetwork network,
            ReteStaticMapping left, ReteStaticMapping right, boolean keepPrefix) {
        if ((left.getNNode() instanceof AbstractPathChecker)
            && (right.getNNode() instanceof AbstractPathChecker)) {
            return new SubgraphCheckerNode<RetePathMatch,RetePathMatch>(
                network, left, right, keepPrefix);
        } else if (!(left.getNNode() instanceof AbstractPathChecker)
            && (right.getNNode() instanceof AbstractPathChecker)) {
            return new SubgraphCheckerNode<ReteSimpleMatch,RetePathMatch>(
                network, left, right, keepPrefix);
        } else if ((left.getNNode() instanceof AbstractPathChecker)
            && !(right.getNNode() instanceof AbstractPathChecker)) {
            return new SubgraphCheckerNode<RetePathMatch,ReteSimpleMatch>(
                network, left, right, keepPrefix);
        } else if (!(left.getNNode() instanceof AbstractPathChecker)
            && !(right.getNNode() instanceof AbstractPathChecker)) {
            return new SubgraphCheckerNode<ReteSimpleMatch,ReteSimpleMatch>(
                network, left, right, keepPrefix);
        } else {
            throw new UnsupportedOperationException(
                "Antecent types are not supported.");
        }
    }

}
