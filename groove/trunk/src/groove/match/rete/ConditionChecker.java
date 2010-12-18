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

    /**
     * This is the pattern of edges (and isolated nodes)
     * of the source (LHS) of the associated <code>condition</code>.
     * The array of elements in match records for this condition checker 
     * follow the same order as this pattern array. 
     */
    protected Element[] pattern;

    /**
     * The tree-like multilevel index that might be made for faster retrieval of
     * matches in the conflict set.   
     */
    protected SearchTree conflictSetSearchTree = null;

    /**
     * The complete set of matches of the current condition. This set is not
     * used if the matches are stored in the tree-liked index structure of
     * the {@link #conflictSetSearchTree}.
     */
    protected Set<ReteMatch> conflictSet = new TreeHashSet<ReteMatch>();

    /**
     * A bag structure that keeps the record of number of times (one or more)
     * a given match is inhibited by an associated embargo match. 
     */
    protected HashBag<ReteMatch> inhibitionMap = new HashBag<ReteMatch>();

    /**
     * The associated {@link Condition} for this checker node.
     */
    protected Condition condition;

    /**
     * A reference to the checker node of the parent (upper level) condition.
     * Its value is null of the upper-most level conditions in a rule.  
     */
    protected ConditionChecker parent;

    /**
     * List of references to the condition checker nodes of the sub-conditions 
     * of {@link #condition}.
     */
    protected List<ConditionChecker> subConditionCheckers;
    private Set<ReteMatch> oneEmptyMatch;

    /**
     * @param network The owner network of this checker node. 
     * @param c The condition object for which this checker is to be created. 
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
     * Establishes the link between this condition checker and its only antecedent
     * (which might be a {@link SubgraphCheckerNode}, a {@link DisconnectedSubgraphChecker},
     * a {@link NodeCheckerNode}, or an {@link EdgeCheckerNode}.
     * It adds itself to the antecedent's list of successors and adding it to
     * this condition-checker's list of antecedents. It also adjusts the 
     * patterns list of this condition-checker.
     * @param antecedent The static mapping for the antecedent of this checker node.
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

    private void addSubConditionChecker(ConditionChecker cc) {
        if (!this.subConditionCheckers.contains(cc)) {
            this.subConditionCheckers.add(cc);
        }
    }

    /**
     * @return The list of sub-condition-checkers.
     */
    public List<ConditionChecker> getSubConditionCheckers() {
        return this.subConditionCheckers;
    }

    @Override
    public boolean equals(Object node) {
        return (node != null)
            && (node instanceof ConditionChecker)
            && ((this == (ConditionChecker) node) || (this.condition.equals(((ConditionChecker) node).condition)));
    }

    /**
     * @return The {@link Condition} object associated with this checker node.
     */
    public Condition getCondition() {
        return this.condition;
    }

    /**
     * @return The condition checker for the parent of this checker, or <code>null</code>
     * if this checker is for the upper-most level condition in a rule.
     */
    public ConditionChecker getParent() {
        return this.parent;
    }

    /**
     * For production node the value of size is equal to the size of 
     * its antecedent subgraph-checker node
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

    /**
     * @param m The match we want to know if it is inhibited by some embargo match.
     * @return <code>true</code> if <code>m</code> is inhibited by some embargo match,
     * <code>false</code> otherwise.
     */
    protected boolean isInhibited(ReteMatch m) {
        return this.inhibitionMap.contains(m);
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
     * @param anchorMap The partial map that is to be extended by the returned matches.
     * 
     * @return An iterator that returns only those matches that conform with
     * the given anchor map and are not inhibited by any NAC sub-conditions.
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
     * This method is called by an edge checker node when the associated condition
     * of this n-node has an LHS/Target consisting of only one edge.

     * @param mu The edge in the host graph that needs to be added/removed to/from the conflict set.
     * @param action Indicates if the given edge is going to be added or removed from the network.
     */
    public void receive(Edge mu, Action action) {
        ReteMatch m = new ReteMatch(this, mu, this.getOwner().isInjective());
        updateConflictSet(m, action);
    }

    /**
     * Receives a match of the subgraph representing the lhs of this n-node's
     * associated production rule and turns it into a LHS-to-HOST match and
     * saves it into the conflict set.
     *  
     * @param match The match object that is to added/removed to/from the conflict set.
     */
    public void receive(ReteMatch match) {
        ReteMatch m = new ReteMatch(this, this.getOwner().isInjective(), match);
        updateConflictSet(m, Action.ADD);
    }

    /**
     * Receives a match of the subgraph representing the disconnected LHS/target
     * of this n-node's {@link #condition} and turns it into a LHS-to-HOST match and
     * saves it into the conflict set.
     *  
     * This method is only called if the target of this node's {@link #condition}
     * is not a connected graph.
     * 
     * @param antecedent The antecedent that is calling this method. 
     * @param match The match that is to be added/removed to/from the conflict set.
     */
    public void receive(DisconnectedSubgraphChecker antecedent, ReteMatch match) {
        ReteMatch m = new ReteMatch(this, this.getOwner().isInjective(), match);
        updateConflictSet(m, Action.ADD);
    }

    /**
     * This method is called by a {@link NodeCheckerNode} when the
     * LHS/target of this n-node's {@link #condition}
     * consists of only one node.
     * 
     * @param node The matched {@link Node} that is to be added/removed to/from 
     *        the conflict set.
     * @param action Determines if the match is to be added or removed.
     */
    public void receive(Node node, Action action) {
        ReteMatch m = new ReteMatch(this, node, this.getOwner().isInjective());
        this.updateConflictSet(m, action);
    }

    /**
     * This method is called by the composite sub-condition checker
     * of this condition-checker to notify it that a given match in 
     * this condition's conflict set is inhibited or that one it's
     * inhibitors has been removed. 
     *  
     * @param m The positive match for this condition that is inhibited or uninhibited.
     * @param action If this parameter has a value of {@link ReteNetworkNode.Action#ADD} then the given
     *               match <code>m</code> is once again inhibited, otherwise it means
     *               one of its (possibly many) inhibitors has been removed.
     */
    public void receiveInhibitorMatch(ReteMatch m, Action action) {

        if (action == Action.ADD) {
            this.inhibitionMap.add(m);
        } else {
            this.inhibitionMap.remove(m);
        }
    }

    /**
     * Updates the conflict set by adding/removing a given match to/from the conflict set.
     * @param m The given match
     * @param action Determines if the match is to be removed or added.
     */
    protected void updateConflictSet(ReteMatch m, Action action) {
        if (action == Action.ADD) {
            addMatchToConflictSet(m);
        } else {
            removeMatchFromConflictSet(m);
        }
    }

    /**
     * Adds the given match to the conflict set, either the plain list 
     * or the tree-like indexed conflict set, whichever is relevant.
     * 
     * See the documentation for {@link #conflictSetSearchTree} for more info.
     * @param m The match to be added.
     */
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

    /**
     * Removes a given match from the conflict set. 
     * @param m The given match.
     */
    protected void removeMatchFromConflictSet(ReteMatch m) {
        assert m != null;
        Collection<ReteMatch> c;
        if (this.conflictSetSearchTree == null) {
            assert !this.conflictSet.contains(m);
            c = this.conflictSet;
        } else {
            c = this.conflictSetSearchTree.getStorageFor(m);
        }

        c.remove(m);
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
        return (node instanceof ConditionChecker) && this.equals(node);
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

    /**
     * @return <code>true</code> if the conflict set for this condition-checker
     * is indexed or whether it is maintained in one plain collection, <code>false</code>
     * otherwise.     
     */
    public boolean isIndexed() {
        return this.conflictSetSearchTree != null;
    }

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

}
