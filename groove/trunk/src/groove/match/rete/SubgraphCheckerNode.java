/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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

import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.Node;
import groove.match.rete.ReteNetwork.ReteStaticMapping;
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

    private TreeHashSet<ReteMatch> leftMemory = new TreeHashSet<ReteMatch>();
    private TreeHashSet<ReteMatch> rightMemory = new TreeHashSet<ReteMatch>();

    /**
     * This is fast lookup table for equality checking of left and
     * right match during runtime. This is basically the join condition
     * that this subgraph represents.
     */
    private int[][] fastEqualityLookupTable;

    private Element[] pattern;

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

    public SubgraphCheckerNode(ReteNetwork network, ReteStaticMapping left,
            ReteStaticMapping right) {
        this(network, left, right, false);
    }

    private void copyPatternsFromAntecedents() {
        assert this.getAntecedents().size() == 2;
        Element[] leftAntecedentPattern =
            this.getAntecedents().get(0).getPattern();
        Element[] rightAntecedentPattern =
            this.getAntecedents().get(1).getPattern();
        this.pattern =
            new Element[leftAntecedentPattern.length
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
        Set<Node> s1 = leftMap.getLhsNodes();
        Set<Node> s2 = rightMap.getLhsNodes();
        HashSet<Node> intersection = new HashSet<Node>();
        for (Node n1 : s1) {
            if (s2.contains(n1)) {
                intersection.add(n1);
            }
        }
        this.fastEqualityLookupTable = new int[intersection.size()][4];
        int i = 0;
        for (Node n : intersection) {
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
     * This method uses the {@link #receive(ReteNetworkNode, List, Action)} variant
     * to do the actual job.
     * 
     * @param source
     * @param mu
     * @param action
     */
    public void receive(ReteNetworkNode source, int repeatIndex, Element mu,
            Action action) {
        ReteMatch sg =
            (mu instanceof Edge) ? new ReteMatch(source, (Edge) mu,
                this.getOwner().isInjective()) : new ReteMatch(source,
                (Node) mu, this.getOwner().isInjective());
        if (action == Action.ADD) {
            this.receive(source, repeatIndex, sg, action);
        } else {
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

    /**
     * @param source
     * @param subgraph
     */
    public void receive(ReteNetworkNode source, int repeatIndex,
            ReteMatch subgraph, Action action) {

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

        if (action == Action.ADD) {
            //assert memory.indexOf(subgraph) == -1;
            memory.add(subgraph);
            subgraph.addContainerCollection(memory);
        } else {
            memory.remove(subgraph);
            //assert memory.indexOf(subgraph) == -1;
        }
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
                            repeatedSuccessorIndex, combined, action);
                    } else if (n instanceof ConditionChecker) {
                        ((ConditionChecker) n).receive(this,
                            repeatedSuccessorIndex, combined, action);
                    } else if (n instanceof DisconnectedSubgraphChecker) {
                        ((DisconnectedSubgraphChecker) n).receive(this,
                            repeatedSuccessorIndex, combined, action);
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

        //these are the nodes that allowed to be shared
        //between the left and right matches.
        //In case of injective matching, we collect these
        //shared nodes so that we could then subtract them from
        //other nodes in the left and right. What remains from
        //left and right should then have an empty intersection
        //This collection is only of use if injective matching is on
        Set<Node> allowedSharedNodes =
            (injective) ? (new TreeHashSet<Node>()) : null;

        Element[] leftUnits = left.getAllUnits();
        Element[] rightUnits = right.getAllUnits();
        Set<Node> nodesLeft = (injective) ? left.getNodes() : null;
        Set<Node> nodesRight = (injective) ? right.getNodes() : null;

        int i = 0;
        for (; i < this.fastEqualityLookupTable.length; i++) {
            int[] equality = this.fastEqualityLookupTable[i];
            Node n1 =
                (equality[1] == -1) ? (Node) leftUnits[equality[0]]
                        : (equality[1] == 0)
                                ? ((Edge) leftUnits[equality[0]]).source()
                                : ((Edge) leftUnits[equality[0]]).target();
            Node n2 =
                (equality[3] == -1) ? (Node) rightUnits[equality[2]]
                        : (equality[3] == 0)
                                ? ((Edge) rightUnits[equality[2]]).source()
                                : ((Edge) rightUnits[equality[2]]).target();

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
     * @param oneAntecedent
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

        Set<Edge> s1 = new HashSet<Edge>();
        for (Element e : oneMapping.getElements()) {
            if (e instanceof Edge) {
                s1.add((Edge) e);
            }
        }

        //No shared edges
        for (Element e : otherMapping.getElements()) {
            if (e instanceof Edge) {
                if (s1.contains(e)) {
                    result = false;
                    break;
                }
            }
        }

        Set<Node> nodes1 = oneMapping.getLhsNodes();
        Set<Node> sharedNodes = new HashSet<Node>();
        for (Node n : otherMapping.getLhsNodes()) {
            if (nodes1.contains(n)) {
                sharedNodes.add(n);
            }
        }

        for (int i = 0; result && (i < combinationChoices.length); i++) {
            ReteStaticMapping leftMapping = combinationChoices[i][0];
            ReteStaticMapping rightMapping = combinationChoices[i][1];

            Node leftMappedValue;
            Node rightMappedValue;

            Set<Node> tempSharedNodes = sharedNodes;

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
                                ? ((Edge) leftMapping.getElements()[leftIndices[0]]).source()
                                : ((Edge) leftMapping.getElements()[leftIndices[0]]).target();
                } else {
                    leftMappedValue =
                        (Node) leftMapping.getElements()[leftIndices[0]];
                }

                if (rightIndices[1] != -1) {
                    rightMappedValue =
                        (rightIndices[1] == 0)
                                ? ((Edge) rightMapping.getElements()[rightIndices[0]]).source()
                                : ((Edge) rightMapping.getElements()[rightIndices[0]]).target();
                } else {
                    rightMappedValue =
                        (Node) rightMapping.getElements()[rightIndices[0]];
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
     * @return
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
        this.leftMemory.clear();
        this.rightMemory.clear();
    }

    @Override
    public List<? extends Object> initialize() {
        return null;
    }

    @Override
    public Element[] getPattern() {
        return this.pattern;
    }

}
