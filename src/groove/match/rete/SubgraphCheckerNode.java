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
public class SubgraphCheckerNode extends ReteNetworkNode implements
        StateSubscriber {

    private TreeHashSet<ReteMatch> leftOnDemandBuffer =
        new TreeHashSet<ReteMatch>();
    private TreeHashSet<ReteMatch> leftMemory = new TreeHashSet<ReteMatch>();

    private TreeHashSet<ReteMatch> rightOnDemandBuffer =
        new TreeHashSet<ReteMatch>();
    private TreeHashSet<ReteMatch> rightMemory = new TreeHashSet<ReteMatch>();

    /**
     * This is fast lookup table for equality checking of left and
     * right match during runtime. This is basically the join condition
     * that this subgraph represents.
     */
    private int[][] fastEqualityLookupTable;

    private RuleElement[] pattern;

    //This flag indicates if the special prefix link of matches coming
    //the left antecedent should be copied for the combined matches
    //that are passed down the network.
    private boolean shouldPreservePrefix = false;

    /**
     * Creates a subgraph checker from two statically-matched antecedents.
     * @param network the RETE network this subgraph-checker belongs to.
     * @param left the left antecedent along with its matching to the LHS of some rule
     * @param right the left antecedent along with its matching to the LHS of some rule
     * @param keepPrefix Indicates if indicates if the special prefix link of matches 
     *        coming the left antecedent should be copied for the combined matches that 
     *        are passed down the network.
     */
    public SubgraphCheckerNode(ReteNetwork network, ReteStaticMapping left,
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

    private void copyPatternsFromAntecedents() {
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
    public boolean addSuccessor(ReteNetworkNode nnode) {
        boolean result =
            (nnode instanceof SubgraphCheckerNode)
                || (nnode instanceof ConditionChecker);

        if (result) {
            result = super.addSuccessor(nnode);
        }
        return result;
    }

    /**
     * Receives a matched edge/node during runtime from an EdgeChecker/NodeChecker antecedent
     * and passes along down the RETE network based on the rules of the algorithm.
     * 
     * This method uses the {@link #receive(ReteNetworkNode, int, ReteMatch)} variant
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
    public void receive(ReteNetworkNode source, int repeatIndex,
            HostElement mu, Action action) {
        ReteMatch sg =
            (mu instanceof HostEdge) ? new ReteMatch(source, (HostEdge) mu,
                this.getOwner().isInjective()) : new ReteMatch(source,
                (HostNode) mu, this.getOwner().isInjective());
        if (action == Action.ADD) {
            this.receive(source, repeatIndex, sg);
        } else if (!this.getOwner().isInOnDemandMode()
            || !unbufferMatch(source, repeatIndex, sg)) {
            TreeHashSet<ReteMatch> memory;
            if (getAntecedents().get(0) != getAntecedents().get(1)) {
                memory =
                    (getAntecedents().get(0) == source) ? this.leftMemory
                            : this.rightMemory;
            } else {
                memory =
                    (repeatIndex == 0) ? this.leftMemory : this.rightMemory;
            }
            if (memory.contains(sg)) {
                ReteMatch m = sg;
                sg = memory.put(sg);
                memory.remove(m);
                sg.dominoDelete(null);
            }
        }
    }

    private boolean unbufferMatch(ReteNetworkNode source, int repeatIndex,
            ReteMatch subgraph) {
        TreeHashSet<ReteMatch> c;
        assert !subgraph.isDeleted();
        if (getAntecedents().get(0) != getAntecedents().get(1)) {
            c =
                (getAntecedents().get(0) == source) ? this.leftOnDemandBuffer
                        : this.rightOnDemandBuffer;
        } else {
            c =
                (repeatIndex == 0) ? this.leftOnDemandBuffer
                        : this.rightOnDemandBuffer;
        }

        boolean result = c.remove(subgraph);
        if (result) {
            subgraph.removeContainerCollection(c);
        }
        return result;
    }

    private void bufferMatch(ReteNetworkNode source, int repeatIndex,
            ReteMatch subgraph) {
        TreeHashSet<ReteMatch> c;

        if (getAntecedents().get(0) != getAntecedents().get(1)) {
            c =
                (getAntecedents().get(0) == source) ? this.leftOnDemandBuffer
                        : this.rightOnDemandBuffer;
        } else {
            c =
                (repeatIndex == 0) ? this.leftOnDemandBuffer
                        : this.rightOnDemandBuffer;
        }
        c.add(subgraph);
        subgraph.addContainerCollection(c);
    }

    /**
     * Receives a new subgraph match (resulting from an ADD operation)
     * of type {@link ReteMatch} from an antecedent. Whether it is immediately
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
            ReteMatch subgraph) {
        if (this.getOwner().isInOnDemandMode()) {
            bufferMatch(source, repeatIndex, subgraph);
        } else {
            receiveAndProcess(source, repeatIndex, subgraph);
        }
    }

    /**
     * Receives a new subgraph match (resulting from an ADD operation)
     * of type {@link ReteMatch} from an antecedent and immediately
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
     */
    protected void receiveAndProcess(ReteNetworkNode source, int repeatIndex,
            ReteMatch subgraph) {

        TreeHashSet<ReteMatch> memory;
        TreeHashSet<ReteMatch> otherMemory;

        if (this.getAntecedents().get(0) != this.getAntecedents().get(1)) {
            memory =
                (this.getAntecedents().get(0) == source) ? this.leftMemory
                        : this.rightMemory;

        } else {
            memory = (repeatIndex == 0) ? this.leftMemory : this.rightMemory;
        }
        otherMemory =
            (memory == this.leftMemory) ? this.rightMemory : this.leftMemory;

        memory.add(subgraph);
        subgraph.addContainerCollection(memory);
        for (ReteMatch gOther : otherMemory) {
            ReteMatch left = (memory == this.leftMemory) ? subgraph : gOther;
            ReteMatch right = (left == subgraph) ? gOther : subgraph;

            if (this.test(left, right)) {
                ReteMatch combined = this.construct(left, right);
                ReteNetworkNode previous = null;
                int repeatedSuccessorIndex = 0;
                for (ReteNetworkNode n : this.getSuccessors()) {
                    repeatedSuccessorIndex =
                        (n != previous) ? 0 : (repeatedSuccessorIndex + 1);
                    if (n instanceof SubgraphCheckerNode) {
                        ((SubgraphCheckerNode) n).receive(this,
                            repeatedSuccessorIndex, combined);
                    } else if (n instanceof ConditionChecker) {
                        ((ConditionChecker) n).receive(combined);
                    } else if (n instanceof DisconnectedSubgraphChecker) {
                        ((DisconnectedSubgraphChecker) n).receive(this,
                            repeatedSuccessorIndex, combined);
                    }
                    previous = n;
                }
            }
        }
    }

    private ReteMatch construct(ReteMatch subgraph, ReteMatch other) {
        ReteMatch result =
            ReteMatch.merge(this, subgraph, other, false,
                this.shouldPreservePrefix);
        return result;
    }

    private boolean test(ReteMatch left, ReteMatch right) {
        boolean allEqualitiesSatisfied = true;
        boolean injective = this.getOwner().isInjective();

        HostElement[] leftUnits = left.getAllUnits();
        HostElement[] rightUnits = right.getAllUnits();
        Set<HostNode> nodesLeft = (injective) ? left.getNodes() : null;
        Set<HostNode> nodesRight = (injective) ? right.getNodes() : null;

        int i = 0;
        for (; i < this.fastEqualityLookupTable.length; i++) {
            int[] equality = this.fastEqualityLookupTable[i];
            //The equalities are guaranteed to work on edges because
            //isolated nodes do not occur in connected components
            HostNode n1 =
                (equality[1] == 0)
                        ? ((HostEdge) leftUnits[equality[0]]).source()
                        : ((HostEdge) leftUnits[equality[0]]).target();
            HostNode n2 =
                (equality[3] == 0)
                        ? ((HostEdge) rightUnits[equality[2]]).source()
                        : ((HostEdge) rightUnits[equality[2]]).target();

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
                ReteMatch.checkInjectiveOverlap(nodesLeft, nodesRight);
        }

        return allEqualitiesSatisfied;
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
        if (this.getOwner().isInOnDemandMode()) {
            for (ReteNetworkNode nnode : this.getAntecedents()) {
                nnode.demandUpdate();
            }
            result =
                (this.leftOnDemandBuffer.size() + this.rightOnDemandBuffer.size()) > 0;

            if (result) {
                for (ReteMatch m : this.leftOnDemandBuffer) {
                    assert !m.isDeleted();
                    m.removeContainerCollection(this.leftOnDemandBuffer);
                    this.receiveAndProcess(m.getOrigin(), 0, m);
                }
                this.leftOnDemandBuffer.clear();
                int repeatIndex =
                    (this.getAntecedents().get(0) != this.getAntecedents().get(
                        1)) ? 0 : 1;
                for (ReteMatch m : this.rightOnDemandBuffer) {
                    assert !m.isDeleted();
                    m.removeContainerCollection(this.rightOnDemandBuffer);
                    this.receiveAndProcess(m.getOrigin(), repeatIndex, m);
                }
                this.rightOnDemandBuffer.clear();
            }
        }
        return result;
    }
}
