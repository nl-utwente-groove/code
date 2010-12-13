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
import groove.trans.Condition;
import groove.trans.RuleToHostMap;
import groove.util.FilterIterator;
import groove.util.HashBag;
import groove.util.TreeHashSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author Arash Jalali
 * @version $Revision $
 */
public class ConditionChecker extends ReteNetworkNode implements
        StateSubscriber {

    class SearchTree {
        //This is the hierarchical order in which the conflict set of
        //a subcondition is stored. 
        protected Element[] rootSearchOrder;

        HashMap<Element,Object> root = new HashMap<Element,Object>();

        SearchTree(List<? extends Element> searchOrder) {
            this.rootSearchOrder = new Element[searchOrder.size()];
            this.rootSearchOrder = searchOrder.toArray(this.rootSearchOrder);
        }

        @SuppressWarnings("unchecked")
        Set<ReteMatch> getStorageFor(ReteMatch m) {
            Set<ReteMatch> result = null;
            HashMap<Element,Object> leaf = this.root;
            for (int i = 0; i < this.rootSearchOrder.length - 1; i++) {
                Element ei;
                if (this.rootSearchOrder[i] instanceof Node) {
                    ei = m.getNode((Node) this.rootSearchOrder[i]);
                } else {
                    ei = m.getEdge((Edge) this.rootSearchOrder[i]);
                }
                HashMap<Element,Object> treeNode =
                    (HashMap<Element,Object>) leaf.get(ei);
                if (treeNode == null) {
                    treeNode = new HashMap<Element,Object>();
                    leaf.put(ei, treeNode);
                }
                leaf = treeNode;
            }
            Element ei =
                (this.rootSearchOrder[this.rootSearchOrder.length - 1] instanceof Node)
                        ? m.getNode((Node) this.rootSearchOrder[this.rootSearchOrder.length - 1])
                        : m.getEdge((Edge) this.rootSearchOrder[this.rootSearchOrder.length - 1]);
            Object o = leaf.get(ei);
            if (o == null) {
                o = new TreeHashSet<ReteMatch>();
                leaf.put(ei, o);
            }
            result = (Set<ReteMatch>) o;
            return result;
        }

        @SuppressWarnings("unchecked")
        Set<ReteMatch> getStorageFor(RuleToHostMap anchorMap) {
            Set<ReteMatch> result = null;
            HashMap<Element,Object> leaf = this.root;
            for (int i = 0; i < this.rootSearchOrder.length - 1; i++) {
                Element ei;
                if (this.rootSearchOrder[i] instanceof Node) {
                    ei = anchorMap.nodeMap().get(this.rootSearchOrder[i]);
                } else {
                    ei = anchorMap.edgeMap().get(this.rootSearchOrder[i]);
                }
                HashMap<Element,Object> treeNode =
                    (HashMap<Element,Object>) leaf.get(ei);
                if (treeNode == null) {
                    treeNode = new HashMap<Element,Object>();
                    leaf.put(ei, treeNode);
                }
                leaf = treeNode;
            }
            Element ei =
                (this.rootSearchOrder[this.rootSearchOrder.length - 1] instanceof Node)
                        ? anchorMap.nodeMap().get(
                            this.rootSearchOrder[this.rootSearchOrder.length - 1])
                        : anchorMap.edgeMap().get(
                            this.rootSearchOrder[this.rootSearchOrder.length - 1]);
            Object o = leaf.get(ei);
            if (o == null) {
                o = new TreeHashSet<ReteMatch>();
                leaf.put(ei, o);
            }
            result = (Set<ReteMatch>) o;
            return result;
        }
    }

    /**
     * This is the pattern of edges (and isolated nodes)
     * of the source (LHS) of the associated <code>condition</code>.
     * The array of elements in match records for this condition checker 
     * follow the same order as this pattern array. 
     */
    protected Element[] pattern;

    protected SearchTree conflictSetSearchTree = null;

    protected Set<ReteMatch> conflictSet = new TreeHashSet<ReteMatch>();
    protected HashBag<ReteMatch> inhibitionMap = new HashBag<ReteMatch>();
    protected Condition condition;
    protected ConditionChecker parent;
    //protected InferentialNodeEdgeHashMap mapToLHS;
    protected List<ConditionChecker> subConditionCheckers;
    private Set<ReteMatch> oneEmptyMatch;

    /**
     * @param network
     * @param c
     */
    public ConditionChecker(ReteNetwork network, Condition c,
            ConditionChecker parentConditionChecker,
            ReteStaticMapping antecedent) {
        super(network);
        this.condition = c;
        this.getOwner().getState().subscribe(this);
        //this.mapToLHS = new InferentialNodeEdgeHashMap(false);
        this.parent = parentConditionChecker;
        makeRootSearchOrder(c);
        this.subConditionCheckers = new ArrayList<ConditionChecker>();
        if (this.parent != null) {
            this.parent.addSubConditionChecker(this);
        }
        connectToAntecedent(antecedent);
        this.oneEmptyMatch = Collections.singleton(new ReteMatch(this, false));
    }

    private void makeRootSearchOrder(Condition c) {
        if ((c.getRootMap() != null) && (!c.getRootMap().isEmpty())) {
            ArrayList<Node> nodes = new ArrayList<Node>();
            nodes.addAll(c.getRootMap().nodeMap().keySet());
            Collections.sort(nodes);
            this.conflictSetSearchTree = new SearchTree(nodes);
        }
    }

    /**
     * Establishes the link between this condition checker and it only antecedent
     * (which might be a {@link SubgraphCheckerNode}, a {@link DisconnectedSubgraphChecker},
     * a {@link NodeCheckerNode}, or an {@link EdgeCheckerNode}.
     * It adds itself to the antecedent's list of successors and adding it to
     * this condition-checker's list of antecedents. It also adjusts the 
     * patterns list of this condition-checker.
     * @param antecedent
     */
    private void connectToAntecedent(ReteStaticMapping antecedent) {
        if (antecedent != null) {
            this.addAntecedent(antecedent.getNNode());
            antecedent.getNNode().addSuccessor(this);
            this.pattern =
                Arrays.copyOf(antecedent.getElements(),
                    antecedent.getElements().length);
        } else {
            this.pattern = new Element[0];
        }
    }

    public void addSubConditionChecker(ConditionChecker cc) {
        if (!this.subConditionCheckers.contains(cc)) {
            this.subConditionCheckers.add(cc);
        }
    }

    public List<ConditionChecker> getSubConditionCheckers() {
        return this.subConditionCheckers;
    }

    public boolean equals(ConditionChecker node) {
        return (this == node)
            || ((node != null) && (node instanceof ConditionChecker) && this.condition.equals(node.condition));
    }

    public Condition getCondition() {
        return this.condition;
    }

    public ConditionChecker getParent() {
        return this.parent;
    }

    /**
     * For production node the value of size is equal to the size of 
     * its antecedent sub-graphchecker node
     * 
     * This is a construction-time method only.  
     */
    @Override
    public int size() {
        assert this.getAntecedents().size() == 1;
        return this.getAntecedents().iterator().next().size();
    }

    /**
     * @return The set of the current matches of the 
     * target of the this condition with the host graph filtered 
     * through the NAC subconditions. 
     */
    public Set<ReteMatch> getConflictSet() {
        assert this.conflictSetSearchTree == null;
        Set<ReteMatch> cs =
            this.isEmpty() ? this.oneEmptyMatch : this.conflictSet;
        Set<ReteMatch> result = cs;

        if (!this.inhibitionMap.isEmpty() && (cs.size() > 0)) {
            result = new TreeHashSet<ReteMatch>();
            for (ReteMatch m : cs) {
                if (!this.isInhibited(m)) {
                    result.add(m);
                }
            }
        }
        return result;
    }

    protected boolean isInhibited(ReteMatch m) {
        return this.inhibitionMap.contains(m);
    }

    /**
     * This is for debugging.
     * @return
     */
    public Set<ReteMatch> getMatchMemory() {
        /*
                ArrayList<ReteMatch> reteMatchArray =
                    new ArrayList<ReteMatch>(this.conflictSet);
                if (this.conflictSet.size() > 100) {
                    Collections.sort(reteMatchArray);
                    for (ReteMatch m : reteMatchArray) {
                        System.out.println(m.hashCode());
                    }
                    System.out.println("-----------------------------------------------------------");
                }
        */
        return this.conflictSet;
    }

    /**
     * @return an iterator through the eligible matches of this condition checker,
     * that is, those positive matches that are not inhibitted by any Nac conditions. 
     */
    public Iterator<ReteMatch> getConflictSetIterator() {
        Iterator<ReteMatch> result;

        if (this.isEmpty()) {
            result = this.oneEmptyMatch.iterator();
        } else if (!this.inhibitionMap.isEmpty()
            && (this.conflictSet.size() > 0)) {
            result =
                new FilterIterator<ReteMatch>(this.conflictSet.iterator()) {
                    @Override
                    protected boolean approves(Object obj) {
                        ReteMatch m = (ReteMatch) obj;
                        return !ConditionChecker.this.isInhibited(m);

                    }

                };
        } else {
            result = this.conflictSet.iterator();
        }

        return result;

    }

    /**
     * @param anchorMap
     * @return An iterator that returns only those matches that conform with
     * the given anchor map and are not inhibited by any NAC subconditions.
     */
    public Iterator<ReteMatch> getConflictSetIterator(
            final RuleToHostMap anchorMap) {
        Iterator<ReteMatch> result;

        if (this.isEmpty()) {
            result = this.oneEmptyMatch.iterator();
        } else if (!this.inhibitionMap.isEmpty()) {
            if (this.conflictSetSearchTree != null) {
                result =
                    new FilterIterator<ReteMatch>((anchorMap != null)
                            ? this.conflictSetSearchTree.getStorageFor(
                                anchorMap).iterator()
                            : this.getConflictSet().iterator()) {

                        @Override
                        protected boolean approves(Object obj) {
                            ReteMatch m = (ReteMatch) obj;
                            return !ConditionChecker.this.isInhibited((ReteMatch) obj);

                        }

                    };

            } else {

                result =
                    new FilterIterator<ReteMatch>(
                        this.getConflictSet().iterator()) {

                        RuleToHostMap anchor = anchorMap;

                        @Override
                        protected boolean approves(Object obj) {
                            ReteMatch m = (ReteMatch) obj;
                            return !ConditionChecker.this.isInhibited((ReteMatch) obj)
                                && m.conformsWith(this.anchor);

                        }

                    };
            }
        } else {
            if (this.conflictSetSearchTree != null) {
                result =
                    this.conflictSetSearchTree.getStorageFor(anchorMap).iterator();
            } else {
                result =
                    new FilterIterator<ReteMatch>(
                        this.getConflictSet().iterator()) {

                        RuleToHostMap anchor = anchorMap;

                        @Override
                        protected boolean approves(Object obj) {
                            ReteMatch m = (ReteMatch) obj;
                            return m.conformsWith(this.anchor);
                        }

                    };

            }
        }

        return result;
    }

    /**
     * This method is called by an edge checker node when the associated production
     * rule of this n-node has an lhs consisting of only one edge.
     * @param antecedent
     * @param repeatIndex This parameter is basically a counter over repeating antecedents.
     *        For condition-checker nodes that do not have a disconnected LHS, the value
     *        of this argument should always be 0. If a conditio-checker node has
     *        a disconnected LHS and one of the disconnected component patterns, represented
     *        by a subgraph-checker, repeats more than once in the LHS, then
     *        the subgraph-checker will have to call this method several times, and
     *        the value of this parameter will have to increase, starting from 0. 
     * @param mu
     * @param action
     */
    public void receive(EdgeCheckerNode antecedent, int repeatIndex, Edge mu,
            Action action) {
        ReteMatch m = new ReteMatch(this, mu, this.getOwner().isInjective());
        updateConflictSet(antecedent, repeatIndex, m, action);
    }

    /**
     * Receives a match of the subgraph representing the lhs of this n-node's
     * associated production rule and turns it into a LHS-to-HOST match and
     * saves/removes it into/from the conflict set.
     *  
     * @param antecedent
     * @param repeatIndex This parameter is basically a counter over repeating antecedents.
     *        For condition-checker nodes that do not have a disconnected LHS, the value
     *        of this argument should always be 0. If a conditio-checker node has
     *        a disconnected LHS and one of the disconnected component patterns, represented
     *        by a subgraph-checker, repeats more than once in the LHS, then
     *        the subgraph-checker will have to call this method several times, and
     *        the value of this parameter will have to increase, starting from 0.  
     * @param match
     * @param action
     */
    public void receive(SubgraphCheckerNode antecedent, int repeatIndex,
            ReteMatch match, Action action) {
        ReteMatch m = new ReteMatch(this, this.getOwner().isInjective(), match);
        updateConflictSet(antecedent, repeatIndex, m, action);
    }

    public void receive(DisconnectedSubgraphChecker antecedent,
            ReteMatch match, Action action) {
        ReteMatch m = new ReteMatch(this, this.getOwner().isInjective(), match);
        updateConflictSet(antecedent, 0, m, action);
    }

    /**
     * This method is called by a {@link NodeCheckerNode} when the production rule's
     * lhs consists of only one node or has isolated nodes in it.
     * 
     * @param antecedent
     * @param repeatIndex This parameter is basically a counter over repeating antecedents.
     *        For condition-checker nodes that do not have a disconnected LHS, the value
     *        of this argument should always be 0. If a conditio-checker node has
     *        a disconnected LHS and one of the disconnected component patterns, represented
     *        by a subgraph-checker, repeats more than once in the LHS, then
     *        the subgraph-checker will have to call this method several times, and
     *        the value of this parameter will have to increase, starting from 0.  
     * @param nodeMatch
     * @param action
     */
    public void receive(NodeCheckerNode antecedent, int repeatIndex, Node node,
            Action action) {
        ReteMatch m = new ReteMatch(this, node, this.getOwner().isInjective());
        this.updateConflictSet(antecedent, repeatIndex, m, action);
    }

    /**
     * This method is called by the NAC subconditions of this condition checker 
     * to notify it of the formation of an inhibitor match.
     *  
     * @param m
     * @param action
     */
    public void receiveInhibitorMatch(ReteMatch m, Action action) {

        if (action == Action.ADD) {
            this.inhibitionMap.add(m);
        } else {
            this.inhibitionMap.remove(m);
        }
    }

    protected void updateConflictSet(ReteNetworkNode antecedent,
            int repeatIndex, ReteMatch m, Action action) {
        if (action == Action.ADD) {
            addMatchToConflictSet(m);
        } else {
            removeMatchFromConflictSet(m);
        }
    }

    protected void addMatchToConflictSet(ReteMatch m) {
        Collection<ReteMatch> c;
        if (this.conflictSetSearchTree == null) {
            assert !this.conflictSet.contains(m);
            c = this.conflictSet;
        } else {
            c = this.conflictSetSearchTree.getStorageFor(m);
        }
        c.add(m);
        m.addContainerCollection(c);

    }

    protected void removeMatchFromConflictSet(ReteMatch m) {
        assert m != null;
        this.conflictSet.remove(m);
        assert !this.conflictSet.contains(m);
    }

    /**
      * Determines if the target of this condition is an empty graph.
      * Such conditions have an isolated condition checker that has no
      * antecedent because no host graph edge or node needs to be propagated
      * through them during run-time.
      *  
      * @return {@literal true} if this node has no antecedent,
      * {@literal false} otherwise. 
      */
    public boolean isEmpty() {
        return this.getAntecedents().size() == 0;
    }

    @Override
    public boolean equals(ReteNetworkNode node) {
        return (node instanceof ConditionChecker)
            && this.equals((ConditionChecker) node);
    }

    @Override
    public int hashCode() {
        return this.condition.hashCode();
    }

    @Override
    public String toString() {
        StringBuilder res =
            new StringBuilder(String.format("Name %s: ",
                this.condition.getName() != null
                        ? this.condition.getName().toString() : "null"));
        res.append(String.format("The conflict set size: %s",
            getConflictSet().size()));
        int i = 0;
        for (ReteMatch rm : getConflictSet()) {
            res.append(String.format("Match(%d): %s", ++i, rm));
        }
        return res.toString();
    }

    @Override
    public void clear() {
        this.inhibitionMap.clear();
        this.conflictSet.clear();

    }

    @Override
    public List<? extends Object> initialize() {
        return null;
    }

    @Override
    public Element[] getPattern() {
        return this.pattern;
    }

    public boolean isIndexed() {
        return this.conflictSetSearchTree != null;
    }

}
