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
import groove.graph.Graph;
import groove.graph.GraphInfo;
import groove.gui.jgraph.ReteJModel;
import groove.io.AspectGxl;
import groove.io.LayedOutXml;
import groove.io.Xml;
import groove.match.rete.ReteNetwork.ReteState.ReteUpdateMode;
import groove.match.rete.ReteNetworkNode.Action;
import groove.trans.AbstractCondition;
import groove.trans.Condition;
import groove.trans.GraphGrammar;
import groove.trans.HostEdge;
import groove.trans.HostGraph;
import groove.trans.HostNode;
import groove.trans.NotCondition;
import groove.trans.Rule;
import groove.trans.RuleEdge;
import groove.trans.RuleGraph;
import groove.trans.RuleGraphMorphism;
import groove.trans.RuleName;
import groove.trans.RuleNode;
import groove.view.FormatException;
import groove.view.StoredGrammarView;
import groove.view.aspect.AspectGraph;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Arash Jalali
 * @version $Revision $
 */
public class ReteNetwork {

    private RootNode root;

    //Every RETE network in GROOVE will have at most one node-checker.
    //This <code>nodeChecker</code> attribute is not null if and only if
    //the LHS of some rule/sub-condition in the grammar has at least 
    //one isolated node.
    private NodeCheckerNode nodeChecker = null;

    private HashMap<Rule,ProductionNode> productionNodes =
        new HashMap<Rule,ProductionNode>();

    private HashMap<Condition,ConditionChecker> conditionCheckerNodes =
        new HashMap<Condition,ConditionChecker>();

    private ArrayList<CompositeConditionChecker> compositeConditionCheckerNodes =
        new ArrayList<CompositeConditionChecker>();

    private boolean injective = false;

    private ReteState state;

    /**
    * Creates a new RETE network from scratch out of 
    * the rules of a {@link StoredGrammarView}.
    *  
    * @param grammarView is the stored grammar view whose rules' lhs will
    * be fed into the construction algorithm one by one.
    * @param enableInjectivity specifies if matching should be injectively
    */
    public ReteNetwork(StoredGrammarView grammarView, boolean enableInjectivity) {
        this.injective = enableInjectivity;
        this.root = new RootNode(this);
        this.state = new ReteState(this);
        ArrayList<Rule> rules = new ArrayList<Rule>();
        for (RuleName rn : grammarView.getRuleNames()) {
            try {
                rules.add(grammarView.getRuleView(rn).toModel());
            } catch (FormatException ex) {
                throw new RuntimeException(
                    String.format("Failed to add rule %s to the RETE network.",
                        rn.toString()));
            }
        }
        this.build(rules);
    }

    /**
     * Creates a RETE network and initializes its state by processing the
     * given grammar's start graph.
     * 
     * @param g The grammar from which a RETE network should be built.
     * @param enableInjectivity determines if this RETE network should perform 
     *        injective matching.
     */
    public ReteNetwork(GraphGrammar g, boolean enableInjectivity) {
        this.injective = enableInjectivity;
        this.root = new RootNode(this);
        this.state = new ReteState(this);
        this.build(g.getRules());
    }

    /**
     * implements the static construction of the RETE network
     * @param rules The list of rules that are to be processes and added to the RETE
     * network.
     */
    public void build(Collection<Rule> rules) {
        Collection<Rule> shuffledRules = rules;
        //Collections.shuffle(shuffledRules);
        for (Rule p : shuffledRules) {
            addConditionToNetwork(p, null);
        }
    }

    /**
     * Adds one {@link AbstractCondition} to the structure of
     * the RETE network. If the condition is complex it recursively
     * adds the sub-conditions as well.
     *  
     * @param condition The condition to processed and added to the RETE network.
     */
    private void addConditionToNetwork(Condition condition,
            ConditionChecker parent) {
        ConditionChecker result = null;

        /**
         * This is a list of n-nodes used during the construction
         * of the RETE network only.
         */
        StaticMap openList = new StaticMap();
        RuleGraph g = condition.getTarget();

        Collection<RuleEdge> edgeList = getEdgeCollection(condition);

        mapEdgesAndNodes(openList, edgeList, g.nodeSet());

        if (openList.size() > 0) {
            //generate subgraph-checkers
            boolean changes;
            StaticMap toBeDeleted = new StaticMap();

            //This flag is true whenever a new n-node
            //has replaced some other n-nodes in the open list.
            //When this happens the algorithm tries to
            //remerge the existing n-nodes using their
            //already existing subgraph-checkers
            changes = false;

            //isolated components are checker nodes 
            //in the open list (disconnected islands in the lhs of this rule)
            //that can no longer be merged with other connected 
            //checkers in the open list
            HashSet<ReteStaticMapping> isolatedComponents =
                new HashSet<ReteStaticMapping>();
            while ((openList.size() > 1)
                && (isolatedComponents.size() < openList.size())) {

                toBeDeleted.clear();
                //Try to merge the n-nodes using their existing
                //successor subgraph-checkers as far as possible 
                for (int i = 0; i < openList.size(); i++) {
                    ReteStaticMapping m = openList.get(i);
                    if (!toBeDeleted.contains(m)) {
                        for (ReteNetworkNode suc : m.getNNode().getSuccessors()) {
                            if (suc instanceof SubgraphCheckerNode) {
                                ReteNetworkNode other =
                                    ((SubgraphCheckerNode) suc).getOtherAntecedent(m.getNNode());
                                ReteStaticMapping otherM =
                                    openList.getFirstMappingFor(other, m);
                                if ((otherM != null)
                                    && !toBeDeleted.containsNNode(other)
                                    && ((SubgraphCheckerNode) suc).checksValidSubgraph(
                                        m, otherM)) {
                                    toBeDeleted.add(m);
                                    toBeDeleted.add(otherM);
                                    ReteStaticMapping sucMapping =
                                        ReteStaticMapping.combine(m, otherM,
                                            (SubgraphCheckerNode) suc);

                                    openList.add(sucMapping);
                                    changes = true;
                                    break;
                                }
                            }
                        }

                    }
                }
                for (ReteStaticMapping n : toBeDeleted) {
                    openList.remove(n);
                }
                toBeDeleted.clear();

                //If no new nodes have been added to the open list
                //it means it is time to combine them using newly
                //created subgraph-checkers. This loop goes on
                //until a new subgrpah-checker is created or
                //until it turns out that no more merge is possible.
                while (!changes
                    && (isolatedComponents.size() < openList.size())) {
                    /**
                     * then remove from open-list the references to
                     * - the subgraph-checker of the largest subgraph g of the actual
                     *   production's left-hand side (7)
                     * - some n-node g' that checks an edge or a subgraph
                     *   connected to the subgraph g; 
                     * generate a new n-node as successor of the two n-nodes, i.e.
                     * a subgraph-checker checking for the combination of g and g';
                     * put a reference to the new n-node on open-list;
                     */
                    ReteStaticMapping m1 =
                        pickTheNextLargestCheckerNode(openList,
                            isolatedComponents);

                    ReteStaticMapping m2 =
                        (m1 != null) ? pickCheckerNodeConnectedTo(openList, m1)
                                : null;

                    if (m2 != null) {
                        SubgraphCheckerNode sgc =
                            new SubgraphCheckerNode(this, m1, m2);

                        ReteStaticMapping newCombinedMapping =
                            ReteStaticMapping.combine(m1, m2, sgc);

                        toBeDeleted.add(m1);
                        toBeDeleted.add(m2);
                        openList.add(newCombinedMapping);
                        changes = true;
                    } else if (m1 != null) {
                        isolatedComponents.add(m1);
                    } else {
                        //everything else in the openList is just a bunch of 
                        //disconnected components of one rule's LHS
                        break;
                    }
                }
                changes = false;
                for (ReteStaticMapping mappingToDelete : toBeDeleted) {
                    openList.remove(mappingToDelete);
                }
            }
            /** what is left on the list could be a reference to one 
             *  subgraph equal to the left-hand side of the actual 
             * production/condition or there are more elements in the open list,
             * which means this rule's/condition's LHS is a disconnected graph
             */
            if (openList.size() >= 1) {
                if (openList.size() > 1) {
                    //we have a disconnected LHS
                    Collections.sort(openList,
                        new Comparator<ReteStaticMapping>() {
                            @Override
                            public int compare(ReteStaticMapping m1,
                                    ReteStaticMapping m2) {
                                return m1.getNNode().size()
                                    - m2.getNNode().size();
                            }
                        });
                    ReteStaticMapping disjointMerge = null;

                    //we sort the antecedents in descending order of size
                    //so that during the merging of the matches
                    //the hash code calculation of the composite
                    //match would be faster.
                    Collections.sort(openList,
                        new Comparator<ReteStaticMapping>() {
                            public int compare(ReteStaticMapping m1,
                                    ReteStaticMapping m2) {
                                return m2.getNNode().size()
                                    - m1.getNNode().size();
                            }
                        });

                    //If there are only two disjoint components
                    //merge them with an ordinary subgraph-checker
                    if (openList.size() == 2) {
                        SubgraphCheckerNode sgc =
                            new SubgraphCheckerNode(this, openList.get(0),
                                openList.get(1));
                        disjointMerge =
                            ReteStaticMapping.combine(openList.get(0),
                                openList.get(1), sgc);
                    } else {
                        //if there are more then combine them with special
                        //subgraph-checker that is capable of merging several disjoint
                        //subgraphs.

                        DisconnectedSubgraphChecker dsc =
                            new DisconnectedSubgraphChecker(this, openList);

                        disjointMerge =
                            ReteStaticMapping.combine(openList, dsc);
                    }
                    openList.clear();
                    openList.add(disjointMerge);
                }
                if (parent == null) {
                    result =
                        new ProductionNode(this, (Rule) condition,
                            openList.get(0));
                    this.productionNodes.put((Rule) condition,
                        (ProductionNode) result);
                    this.conditionCheckerNodes.put(condition, result);
                } else {
                    result =
                        new ConditionChecker(this, condition, parent,
                            openList.get(0));
                    this.conditionCheckerNodes.put(condition, result);
                }
            }
        }
        if (result == null) {
            //this is a rule/condition with empty LHS/target. 
            //Such-special nodes will always return 
            //and empty match set. They do not have any antecedents.            
            if (parent == null) {
                result = new ProductionNode(this, (Rule) condition, null);
                this.productionNodes.put((Rule) condition,
                    (ProductionNode) result);
                this.conditionCheckerNodes.put(condition, result);
            } else {
                result = new ConditionChecker(this, condition, parent, null);
                this.conditionCheckerNodes.put(condition, result);
            }
        }
        if (condition.getSubConditions().size() > 0) {
            Set<NotCondition> nacs = new HashSet<NotCondition>();
            Set<Condition> positiveSubConditions = new HashSet<Condition>();
            for (Condition c : condition.getSubConditions()) {
                if (c instanceof NotCondition) {
                    nacs.add((NotCondition) c);
                } else {
                    positiveSubConditions.add(c);
                }
            }
            processNacs(openList.size() > 0 ? openList.get(0) : null, nacs,
                result);
            for (Condition c : positiveSubConditions) {
                addConditionToNetwork(c, result);
            }
        }
    }

    /**
     * Returns the collection of edges in the given graph's {@link Graph#edgeSet()}
     * in the order that is deemed suitable for making RETE. 
     * 
     * @param c The condition from target of which the edges have to be listed. 
     * @return A collection of edges of the given condition. 
     */
    protected Collection<RuleEdge> getEdgeCollection(Condition c) {

        Collection<RuleEdge> result = c.getTarget().edgeSet();

        result = new ArrayList<RuleEdge>(c.getTarget().edgeSet());
        Collections.sort((List<RuleEdge>) result, new Comparator<RuleEdge>() {
            @Override
            public int compare(RuleEdge e1, RuleEdge e2) {
                return e2.compareTo(e1);
            }
        });

        /*
        GraphSearchPlanFactory f = GraphSearchPlanFactory.getInstance();
        GraphSearchPlanFactory.PlanData pd =
            f.new PlanData(c.getTarget(), c.getLabelStore());

        List<AbstractSearchItem> plan =
            pd.getPlan(c.getTarget().nodeSet(), c.getTarget().edgeSet());

        result = new ArrayList<Edge>();
        for (SearchItem it : plan) {
            result.addAll(it.bindsEdges());
        }
        */
        return result;
    }

    private RuleNode translate(RuleGraphMorphism translationMap, RuleNode node) {
        RuleNode result = node;
        if (translationMap != null) {
            result = translationMap.getNode(node);
            if (result == null) {
                result = node;
            }
        }
        return result;
    }

    private RuleEdge translate(RuleGraphMorphism translationMap, RuleEdge edge) {
        RuleEdge result = edge;
        if (translationMap != null) {
            RuleNode n1 = translate(translationMap, edge.source());
            RuleNode n2 = translate(translationMap, edge.target());
            if (!edge.source().equals(n1) || !edge.target().equals(n2)) {
                result = new RuleEdge(n1, edge.label(), n2);
            }
        }
        return result;
    }

    private void mapEdgesAndNodes(StaticMap openList,
            Collection<RuleEdge> edgeSet, Collection<RuleNode> nodeSet) {

        Collection<RuleNode> mappedLHSNodes = new HashSet<RuleNode>();
        //Adding the required edge-checkers if needed.
        for (RuleEdge e : edgeSet) {
            //TODO this call should be removed after all features of groove are implemented into RETE
            checkEdgeForSupport(e);

            EdgeCheckerNode edgeChecker = findEdgeCheckerForEdge(e);
            if (edgeChecker == null) {
                edgeChecker = new EdgeCheckerNode(this, e);
                this.root.addSuccessor(edgeChecker);
            }
            ReteStaticMapping mapping =
                new ReteStaticMapping(edgeChecker, new Element[] {e});
            openList.add(mapping);
            mappedLHSNodes.add(e.source());
            mappedLHSNodes.add(e.target());
        }
        //Now we see if there are any unmatched nodes on the lhs
        //These are isolated nodes. We will use one node checker but each
        //will be represented by a separate static mapping in the open list.
        //This part is a deviation from the standard algorithm spec.        
        for (RuleNode n : nodeSet) {
            if (!mappedLHSNodes.contains(n)) {
                NodeCheckerNode nc = findNodeCheckerForNode(n);
                ReteStaticMapping mapping =
                    new ReteStaticMapping(nc, new Element[] {n});
                openList.add(mapping);
            }
        }
    }

    /**
     * Creates composite subgraph-checkers for each NAC sub-condition
     * all the way down to a CompositeConditionChecker corresponding to
     * each NAC sub-condition of the condition represented by
     * <code>positiveConditionChecker</code>.   
     * @param lastSubgraphMapping This is the mapping of the antecedent subgraph checker
     *        of the positive condition that corresponds with 
     *        <code>positiveConditionChecker</code>. 
     * @param nacs The list of NAC sub-conditions of the condition represented by
     *        the parameter <code>positiveConditionChecker</code>
     * @param positiveConditionChecker This is the condition-checker for the positive
     *        condition that has negative sub-conditions.
     */
    private void processNacs(ReteStaticMapping lastSubgraphMapping,
            Set<NotCondition> nacs, ConditionChecker positiveConditionChecker) {

        assert (lastSubgraphMapping == null)
            || (positiveConditionChecker.getAntecedents().get(0).equals(lastSubgraphMapping.getNNode()));

        StaticMap openList = new StaticMap();

        List<ReteStaticMapping> byPassList = new ArrayList<ReteStaticMapping>();
        for (NotCondition nac : nacs) {
            byPassList.clear();
            openList.clear();
            ReteStaticMapping m1 =
                duplicateAndTranslateMapping(lastSubgraphMapping,
                    nac.getRootMap());
            if (m1 != null) {
                byPassList.add(m1);
                openList.add(m1);
            }

            mapEdgesAndNodes(openList, nac.getTarget().edgeSet(),
                nac.getTarget().nodeSet());
            if (m1 == null) {
                m1 = openList.get(0);
            }

            while (openList.size() > 1) {
                ReteStaticMapping m2 = pickCheckerNodeConnectedTo(openList, m1);
                if (m2 == null) {
                    m2 = pickTheNextLargestCheckerNode(openList, byPassList);
                }
                SubgraphCheckerNode sg =
                    new SubgraphCheckerNode(this, m1, m2, true);
                m1 = ReteStaticMapping.combine(m1, m2, sg);
                openList.set(0, m1);
                byPassList.set(0, m1);
                openList.remove(m2);
            }
            CompositeConditionChecker result =
                new CompositeConditionChecker(this, nac,
                    positiveConditionChecker, openList.get(0));
            this.compositeConditionCheckerNodes.add(result);

        }

    }

    private ReteStaticMapping duplicateAndTranslateMapping(
            ReteStaticMapping source, RuleGraphMorphism translationMap) {
        ReteStaticMapping result = null;
        if (source != null) {
            Element[] oldElements = source.getElements();
            Element[] newElements = new Element[oldElements.length];
            for (int i = 0; i < newElements.length; i++) {
                if (oldElements[i] instanceof RuleEdge) {
                    newElements[i] =
                        translate(translationMap, (RuleEdge) oldElements[i]);
                } else {
                    newElements[i] =
                        translate(translationMap, (RuleNode) oldElements[i]);
                }
            }
            result = new ReteStaticMapping(source.getNNode(), newElements);
        }
        return result;
    }

    private NodeCheckerNode findNodeCheckerForNode(RuleNode n) {
        if (this.nodeChecker == null) {
            this.nodeChecker = new NodeCheckerNode(this);
            this.root.addSuccessor(this.nodeChecker);
        }
        return this.nodeChecker;
    }

    //TODO ARASH: This method will have to be removed eventually once 
    //we implement all features into RETE
    /**
     * Throws a {@link RuntimeException} if the given edge in <code>e</code>
     * is not of the currently supported type.
     * @param e The edge to be checked.
     */
    private void checkEdgeForSupport(RuleEdge e) {
        if (!(e.label().isAtom())) {
            throw new RuntimeException(
                "The current RETE implementation does not support rules with edge labels of type "
                    + e.label().getClass().toString());
            //        } else if (!(e instanceof RuleEdge)) {
            //            throw new RuntimeException(
            //                "The current RETE implementation does not support rules with edges of type "
            //                    + e.getClass().toString());
        }
    }

    private ReteStaticMapping pickTheNextLargestCheckerNode(StaticMap openList,
            Collection<ReteStaticMapping> bypassThese) {
        assert openList.size() > 0;
        ReteStaticMapping result = null;
        for (int i = 0; i < openList.size(); i++) {
            if ((result == null)
                || (result.getNNode().size() < openList.get(i).getNNode().size())) {
                if (!bypassThese.contains(openList.get(i))) {
                    result = openList.get(i);
                }
            }
        }
        return result;
    }

    private ReteStaticMapping pickCheckerNodeConnectedTo(StaticMap openList,
            ReteStaticMapping g1) {
        ReteStaticMapping result = null;
        Set<RuleNode> nodes1 = g1.getLhsNodes();
        for (ReteStaticMapping m : openList) {
            if (m != g1) {
                Set<RuleNode> nodes2 = m.getLhsNodes();
                for (RuleNode n : nodes1) {
                    if (nodes2.contains(n)) {
                        result = m;
                        break;
                    }
                }
                if (result != null) {
                    break;
                }
            }
        }
        return result;
    }

    private EdgeCheckerNode findEdgeCheckerForEdge(RuleEdge e) {
        EdgeCheckerNode result = null;
        for (ReteNetworkNode n : this.getRoot().getSuccessors()) {
            if (n instanceof EdgeCheckerNode) {
                //if it can match this edge "e"
                if (((EdgeCheckerNode) n).canBeStaticallyMappedToEdge(e)) {
                    result = (EdgeCheckerNode) n;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Reports if this network performs injective matching
     * 
     * @return {@literal true} if it is injective, {@literal false} otherwise
     */
    public boolean isInjective() {
        return this.injective;
    }

    /**
     * updates the RETE state by receiving a node that is added or
     * removed.
     * 
     * @param e The node that has been added/removed to/from the 
     *          host graph.
     * @param action Determines if the given element has been added or removed.
     */
    public void update(HostNode e, Action action) {
        this.getRoot().receiveNode(e, action);
    }

    /**
     * updates the RETE state by receiving an edge that is added or
     * removed.
     * 
     * @param e The edge that has been added/removed to/from the 
     *          host graph.
     * @param action Determines if the given element has been added or removed.
     */
    public void update(HostEdge e, Action action) {
        this.getRoot().receiveEdge(e, action);
    }

    /**
     * Returns the root of the RETE network
     * 
     * @return root of the RETE network.
     */
    public RootNode getRoot() {
        return this.root;
    }

    /**
     * 
     * @return The object containing some global runtime information about the RETE network. 
     */
    public ReteState getState() {
        return this.state;
    }

    /** 
     * @return the collection of production nodes. That is, the nodes which correspond
     * with upper-most level grammar rule. 
     */
    public Collection<ProductionNode> getProductionNodes() {
        return this.productionNodes.values();
    }

    /** 
     * @return the collection of all condition-checker nodes including production nodes. 
     */
    public Collection<ConditionChecker> getConditonCheckerNodes() {
        return this.conditionCheckerNodes.values();
    }

    /** 
     * @return the collection of all composite condition-checker nodes  
     */
    public Collection<CompositeConditionChecker> getCompositeConditonCheckerNodes() {
        return this.compositeConditionCheckerNodes;
    }

    /**
     * @param r The given rule
     * @return Returns the production checker node in the RETE network that finds matches
     * for the given rule <code>r</code>
     */
    public ProductionNode getProductionNodeFor(Rule r) {
        ProductionNode result = this.productionNodes.get(r);
        return result;
    }

    /**
     * 
     * @param c The given condition
     * @return Returns the condition checker node in the RETE network that finds 
     * top-level matches for the given condition <code>c</code>
     */
    public ConditionChecker getConditionCheckerNodeFor(Condition c) {
        ConditionChecker result = this.conditionCheckerNodes.get(c);
        return result;
    }

    /**
     * Initialises the RETE network by feeding all nodes and edges of a given
     * host graph to it.
     *  
     * @param g The given host graph.
     */
    public void processGraph(HostGraph g) {
        this.getState().clearSubscribers();
        ReteUpdateMode oldUpdateMode = this.getState().getUpdateMode();
        this.getState().updateMode = ReteUpdateMode.NORMAL;
        for (HostNode n : g.nodeSet()) {
            this.getRoot().receiveNode(n, Action.ADD);
        }
        for (HostEdge e : g.edgeSet()) {
            this.getRoot().receiveEdge(e, Action.ADD);
        }
        if (oldUpdateMode == ReteUpdateMode.ONDEMAND) {
            this.getState().setUpdateMode(oldUpdateMode);
        }
        this.getState().setHostGraph(g);
    }

    /**
     * Saves the RETE network's shape into a GST file.
     * 
     * @param filePath the path of the directory that would contain the saved file
     * @param fileName the file name to use for saving, without the .gst extension 
     */
    public void save(String filePath, String fileName) {
        AspectGraph graph =
            AspectGraph.newInstance((new ReteJModel(this)).getGraph());
        String name = fileName + ".gst";
        GraphInfo.setName(graph, name);
        doSaveGraph(graph, new File(filePath + "\\" + name));
    }

    void doSaveGraph(AspectGraph graph, File selectedFile) {
        try {
            Xml<AspectGraph> graphLoader = new AspectGxl(new LayedOutXml());
            graphLoader.marshalGraph(graph, selectedFile);
        } catch (IOException exc) {
            throw new RuntimeException(String.format(
                "Error while saving graph to '%s'", selectedFile), exc);
        }
    }

    /**     
     * @return <code>true</code> if this network is currently in the
     * on-demand mode of update propagation.
     */
    public boolean isInOnDemandMode() {
        return this.getState().getUpdateMode() == ReteUpdateMode.ONDEMAND;
    }

    /**
     * The class that represents the mapping of some RETE node 
     * to (parts of ) a rule's LHS during the static build time.
     * 
     * This class is only used during the static build of the RETE 
     * network
     * @author Arash Jalali
     * @version $Revision $
     */
    static class ReteStaticMapping {
        private ReteNetworkNode nNode;
        //These are the (isolated) nodes and edges of some rule's LHS 
        private Element[] elements;

        //This is a quick look up map that says where each LHS-node 
        //is in the <code>elements</code> array. Each value
        // is an array of two integers. The one at index 0 is the index
        // inside the <code>elements</code> array and the integer at index  1
        // is -1 for node element, 0 for the source of edge elements and 1
        // for the target of edge elements.
        private HashMap<RuleNode,int[]> nodeLookupMap =
            new HashMap<RuleNode,int[]>();

        /**
         * 
         * @param reteNode The RETE n-node that is to be mapped to some rule's LHS element 
         * @param mappedTo the LHS elements the <code>reteNode</code> parameter is to be mapped to.
         */
        public ReteStaticMapping(ReteNetworkNode reteNode, Element[] mappedTo) {
            this.nNode = reteNode;
            this.elements = mappedTo;
            for (int i = 0; i < this.elements.length; i++) {
                if (this.elements[i] instanceof RuleEdge) {
                    RuleNode n1 = ((RuleEdge) this.elements[i]).source();
                    RuleNode n2 = ((RuleEdge) this.elements[i]).target();
                    this.nodeLookupMap.put(n1, new int[] {i, 0});
                    this.nodeLookupMap.put(n2, new int[] {i, 1});
                } else {
                    assert (this.elements[i] instanceof RuleNode);
                    this.nodeLookupMap.put((RuleNode) this.elements[i],
                        new int[] {i, -1});
                }
            }
            assert reteNode.getPattern().length == mappedTo.length;
        }

        public static ReteStaticMapping combine(ReteStaticMapping oneMap,
                ReteStaticMapping otherMap, SubgraphCheckerNode suc) {
            assert oneMap.getNNode().getSuccessors().contains(suc)
                && otherMap.getNNode().getSuccessors().contains(suc);
            ReteStaticMapping left =
                suc.getAntecedents().get(0).equals(oneMap.getNNode()) ? oneMap
                        : otherMap;
            ReteStaticMapping right = (left == oneMap) ? otherMap : oneMap;
            Element[] combinedElements =
                new Element[left.getElements().length
                    + right.getElements().length];
            int i = 0;
            for (; i < left.getElements().length; i++) {
                combinedElements[i] = left.getElements()[i];
            }
            for (; i < combinedElements.length; i++) {
                combinedElements[i] =
                    right.getElements()[i - left.getElements().length];
            }
            ReteStaticMapping result =
                new ReteStaticMapping(suc, combinedElements);
            return result;
        }

        public static ReteStaticMapping combine(List<ReteStaticMapping> maps,
                DisconnectedSubgraphChecker suc) {

            List<Element> tempElementsList = new ArrayList<Element>();
            for (int i = 0; i < maps.size(); i++) {
                Element[] elems = maps.get(i).getElements();
                for (int j = 0; j < elems.length; j++) {
                    tempElementsList.add(elems[j]);
                }
            }

            Element[] combinedElements = new Element[tempElementsList.size()];
            combinedElements = tempElementsList.toArray(combinedElements);

            ReteStaticMapping result =
                new ReteStaticMapping(suc, combinedElements);
            return result;
        }

        public ReteNetworkNode getNNode() {
            return this.nNode;
        }

        public Element[] getElements() {
            return this.elements;
        }

        /** 
         * @return The set of LHS nodes in this mapping.
         */
        public Set<RuleNode> getLhsNodes() {
            return this.nodeLookupMap.keySet();
        }

        /**
         * @param n the LHS node the location of which in the <code>elements</code>
         * (as returned by {@link #getElements()}) is to be reported.
         * @return An array of two integers. The element at index 0 is the 
         * index inside the <code>elements</code> array and the integer at index 1
         * is -1 for node elements, and 0 if <code>n</code> is the source of the 
         * edge, and 1 if <code>n</code> is the target of the edge. This method
         * returns {@literal null} if <code>n</code> does not occur in the list of
         * elements of this mapping.
         */
        public int[] locateNode(RuleNode n) {
            return this.nodeLookupMap.get(n);
        }

        @Override
        public String toString() {
            StringBuilder res =
                new StringBuilder(String.format("%s \n lhs-elements:\n",
                    this.getNNode().toString()));
            for (int i = 0; i < this.getElements().length; i++) {
                res.append(String.format("%d %s \n", i,
                    this.getElements()[i].toString()));
            }
            res.append("------------\n");
            return res.toString();
        }
    }

    /**
     * Special collection of {#link {@link ReteNetwork.ReteStaticMapping} objects
     * 
     * This class is used during the static build of the RETE network
     * @author Arash Jalali
     * @version $Revision $
     */
    static class StaticMap extends ArrayList<ReteStaticMapping> {

        public boolean containsNNode(ReteNetworkNode nnode) {
            boolean result = false;
            for (ReteStaticMapping m : this) {
                if (m.getNNode().equals(nnode)) {
                    result = true;
                    break;
                }
            }
            return result;
        }

        public ReteStaticMapping getFirstMappingFor(ReteNetworkNode nnode,
                ReteStaticMapping exceptThis) {
            ReteStaticMapping result = null;
            for (ReteStaticMapping m : this) {
                if ((m != exceptThis) && (m.getNNode().equals(nnode))) {
                    result = m;
                    break;
                }
            }
            return result;
        }
    }

    /**
     * Encapsulates a RETE global runtime state.
     *   
     * @author Arash Jalali
     * @version $Revision $
     */
    static class ReteState {
        /**
         *  The modes of update propagation in the RETE network.
         */
        public enum ReteUpdateMode {
            /**
             * In this mode all updates are immediately propagrated 
             * down to the final condition-checker and production checker
             * nodes.
             */
            NORMAL,
            /**
             * In this mode, update propagations are avoided as far as
             * possible and are performed on an on-demand basis. 
             */
            ONDEMAND
        }

        private ReteNetwork owner;
        private HostGraph hostGraph;
        private Set<StateSubscriber> subscribers =
            new HashSet<StateSubscriber>();
        private ReteUpdateMode updateMode = ReteUpdateMode.ONDEMAND;

        protected ReteState(ReteNetwork owner) {
            this.owner = owner;
        }

        public ReteNetwork getOwner() {
            return this.owner;
        }

        public synchronized void subscribe(StateSubscriber sb) {
            this.subscribers.add(sb);
        }

        public void unsubscribe(StateSubscriber sb) {
            sb.clear();
            this.subscribers.remove(sb);
        }

        public void clearSubscribers() {
            for (StateSubscriber sb : this.subscribers) {
                sb.clear();
            }
        }

        public synchronized void initializeSubscribers() {
            for (StateSubscriber sb : this.subscribers) {
                sb.initialize();
            }

        }

        synchronized void setHostGraph(HostGraph hgraph) {
            this.hostGraph = hgraph;
        }

        public HostGraph getHostGraph() {
            return this.hostGraph;
        }

        public void setUpdateMode(ReteUpdateMode newMode) {
            if (newMode != this.updateMode) {
                if (this.updateMode == ReteUpdateMode.ONDEMAND) {
                    this.updateMode = newMode;
                    this.owner.getRoot().forceFlush();
                } else {
                    this.updateMode = newMode;
                }
            }
        }

        public ReteUpdateMode getUpdateMode() {
            return this.updateMode;
        }
    }

}
