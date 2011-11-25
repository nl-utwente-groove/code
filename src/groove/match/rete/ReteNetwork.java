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

import groove.algebra.Constant;
import groove.graph.DefaultEdge;
import groove.graph.DefaultGraph;
import groove.graph.DefaultNode;
import groove.graph.Graph;
import groove.graph.GraphRole;
import groove.graph.TypeNode;
import groove.graph.algebra.ArgumentEdge;
import groove.graph.algebra.OperatorEdge;
import groove.graph.algebra.VariableNode;
import groove.io.FileType;
import groove.io.xml.DefaultGxl;
import groove.match.rete.ReteNetwork.ReteState.ReteUpdateMode;
import groove.match.rete.ReteNetworkNode.Action;
import groove.rel.RegExpr;
import groove.trans.Condition;
import groove.trans.DefaultRuleNode;
import groove.trans.GraphGrammar;
import groove.trans.HostEdge;
import groove.trans.HostFactory;
import groove.trans.HostGraph;
import groove.trans.HostNode;
import groove.trans.Rule;
import groove.trans.RuleEdge;
import groove.trans.RuleElement;
import groove.trans.RuleFactory;
import groove.trans.RuleGraph;
import groove.trans.RuleGraphMorphism;
import groove.trans.RuleNode;
import groove.trans.Condition.Op;
import groove.util.TreeHashSet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Arash Jalali
 * @version $Revision $
 */
public class ReteNetwork {
    private final String grammarName;
    private RootNode root;

    //Due to typing, RETE now can have many node checkers, one for each
    //type of node occurring as an isolated node
    private HashMap<TypeNode,DefaultNodeChecker> defaultNodeCheckers =
        new HashMap<TypeNode,DefaultNodeChecker>();

    private HashMap<Rule,ProductionNode> productionNodes =
        new HashMap<Rule,ProductionNode>();

    private HashMap<Condition,ConditionChecker> conditionCheckerNodes =
        new HashMap<Condition,ConditionChecker>();

    private ArrayList<CompositeConditionChecker> compositeConditionCheckerNodes =
        new ArrayList<CompositeConditionChecker>();

    private HashMap<Constant,ValueNodeChecker> valueNodeCheckerNodes =
        new HashMap<Constant,ValueNodeChecker>();

    private HashMap<RuleNode,QuantifierCountChecker> quantifierCountCheckerNodes =
        new HashMap<RuleNode,QuantifierCountChecker>();

    private PathCheckerFactory pathCheckerFactory =
        new PathCheckerFactory(this);

    private boolean injective = false;

    private ReteState state;

    private HostFactory hostFactory = null;

    /**
     * Flag that determines if the RETE network is in the process
     * of receiving updates.
     */
    private boolean updating = false;

    private ReteSearchEngine ownerEngine = null;

    /**
     * Creates a RETE network and initializes its state by processing the
     * given grammar's start graph.
     * 
     * @param g The grammar from which a RETE network should be built.
     * @param enableInjectivity determines if this RETE network should perform 
     *        injective matching.
     */
    public ReteNetwork(ReteSearchEngine engine, GraphGrammar g,
            boolean enableInjectivity) {
        this.grammarName = g.getName();
        this.injective = enableInjectivity;
        this.root = new RootNode(this);
        this.state = new ReteState(this);
        this.build(g.getRules());
        this.ownerEngine = engine;
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
            addConditionToNetwork(p.getCondition(), null);
        }
    }

    /**
     * Adds one {@link Condition} to the structure of
     * the RETE network. If the condition is complex it recursively
     * adds the sub-conditions as well.
     *  
     * @param condition The condition to processed and added to the RETE network.
     */
    @SuppressWarnings("unchecked")
    private void addConditionToNetwork(Condition condition,
            ConditionChecker parent) {
        ConditionChecker result = null;

        /**
         * This is a list of n-nodes used during the construction
         * of the RETE network only.
         */
        StaticMap openList = new StaticMap();

        Set<RuleEdge> emptyAndNegativePathEdges = new TreeHashSet<RuleEdge>();
        Set<OperatorEdge> operatorEdges = new TreeHashSet<OperatorEdge>();
        mapQuantifierCountNodes(openList, condition);
        mapEdgesAndNodes(openList, condition.getPattern(),
            emptyAndNegativePathEdges, operatorEdges);

        if (openList.size() > 0) {
            //generate subgraph-checkers
            boolean changes;
            StaticMap toBeDeleted = new StaticMap();

            //This flag is true whenever a new n-node
            //has replaced some other n-nodes in the open list.
            //When this happens the algorithm tries to
            //re-merge the existing n-nodes using their
            //already existing subgraph-checkers
            changes = false;

            //isolated components are checker nodes 
            //in the open list (disconnected islands in the lhs of this rule)
            //that can no longer be merged with other connected 
            //checkers in the open list
            HashSet<ReteStaticMapping> isolatedComponents =
                new HashSet<ReteStaticMapping>();
            while (((openList.size() > 1) && (isolatedComponents.size() < openList.size()))
                || !operatorEdges.isEmpty()) {

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
                //until a new subgraph-checker is created or
                //until it turns out that no more merge is possible.
                while (!changes
                    && ((isolatedComponents.size() < openList.size()) || !(operatorEdges.isEmpty()))) {
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
                    } else if (!operatorEdges.isEmpty()) {
                        List<ReteStaticMapping> argumentSources =
                            new ArrayList<ReteStaticMapping>();
                        OperatorEdge opEdge =
                            pickOneOperatorEdge(openList, operatorEdges,
                                argumentSources);
                        ReteStaticMapping inputAntecedent = null;
                        assert argumentSources.size() > 0;
                        if (argumentSources.size() == 1) {
                            inputAntecedent = argumentSources.get(0);
                        } else {
                            inputAntecedent =
                                createDisjointJoin(argumentSources);
                        }
                        toBeDeleted.addAll(argumentSources);
                        DataOperatorChecker operatorNode =
                            new DataOperatorChecker(this, inputAntecedent,
                                opEdge);
                        ReteStaticMapping opCheckerMapping =
                            ReteStaticMapping.mapDataOperatorNode(operatorNode,
                                opEdge, inputAntecedent);
                        openList.add(opCheckerMapping);
                        assert opEdge != null;
                        operatorEdges.remove(opEdge);
                        changes = true;
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
                    ReteStaticMapping disjointMerge =
                        createDisjointJoin(openList);
                    openList.clear();
                    openList.add(disjointMerge);
                }
                if (emptyAndNegativePathEdges.size() > 0) {
                    addEmptyWordAcceptingAndNegativePathCheckers(openList,
                        emptyAndNegativePathEdges, false);
                }
                if (parent == null) {
                    result =
                        new ProductionNode(this, condition.getRule(),
                            openList.get(0));
                    this.productionNodes.put(condition.getRule(),
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
            //an empty match set. They do not have any antecedents.            
            if (parent == null) {
                result = new ProductionNode(this, condition.getRule(), null);
                this.productionNodes.put(condition.getRule(),
                    (ProductionNode) result);
                this.conditionCheckerNodes.put(condition, result);
            } else {
                result = new ConditionChecker(this, condition, parent, null);
                this.conditionCheckerNodes.put(condition, result);
            }
        }
        if (condition.getCountNode() != null) {
            QuantifierCountChecker qcc =
                this.getQuantifierCountCheckerFor(condition);
            assert qcc != null;
            qcc.setUniversalQuantifierChecker(result);
            result.setCountCheckerNode(qcc);
        }
        if (condition.getSubConditions().size() > 0) {
            Set<Condition> nacs = new HashSet<Condition>();
            Set<Condition> positiveSubConditions = new HashSet<Condition>();
            for (Condition c : condition.getSubConditions()) {
                if (c.getOp() == Op.NOT) {
                    nacs.add(c);
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
     * Finds the count nodes of the immediately lower quantifiers 
     * and creates/maps appropriate {@link QuantifierCountChecker} n-nodes
     * for them.
     * @param openList Where the mapping(s), if any, would be put
     * @param condition The condition for sub-conditions of which the count nodes
     *                  should be found
     */
    private void mapQuantifierCountNodes(StaticMap openList, Condition condition) {
        for (Condition c : condition.getSubConditions()) {
            if ((c.getOp() == Op.FORALL) && (c.getCountNode() != null)) {
                QuantifierCountChecker qcc =
                    new QuantifierCountChecker(this, c);
                this.quantifierCountCheckerNodes.put(c.getCountNode(), qcc);
                ReteStaticMapping sm =
                    new ReteStaticMapping(qcc,
                        new RuleElement[] {c.getCountNode()});
                openList.add(sm);
            }
        }
    }

    /**
     * Receives a series of disjoint components of a rule's LHS mapping
     * and joins them using a proper n-node.
     * 
     * @param antecedents The list of disjoint components
     * @return The static mapping of the n-node created to join the
     *         given antecedents. 
     */
    @SuppressWarnings("unchecked")
    private ReteStaticMapping createDisjointJoin(
            List<ReteStaticMapping> antecedents) {
        ReteStaticMapping disjointMerge = null;

        //Make a copy so that we could sort the list
        List<ReteStaticMapping> scratchList =
            new ArrayList<ReteStaticMapping>(antecedents);

        //we sort the antecedents in descending order of size
        //so that during the merging of the matches
        //the hash code calculation of the composite
        //match would be faster.
        Collections.sort(scratchList, new Comparator<ReteStaticMapping>() {
            public int compare(ReteStaticMapping m1, ReteStaticMapping m2) {
                return m2.getNNode().size() - m1.getNNode().size();
            }
        });

        //If there are only two disjoint components
        //merge them with an ordinary subgraph-checker
        if (scratchList.size() == 2) {
            SubgraphCheckerNode sgc =
                new SubgraphCheckerNode(this, scratchList.get(0),
                    scratchList.get(1));
            disjointMerge =
                ReteStaticMapping.combine(scratchList.get(0),
                    scratchList.get(1), sgc);
        } else {
            //if there are more then combine them with special
            //subgraph-checker that is capable of merging several disjoint
            //subgraphs.

            DisconnectedSubgraphChecker dsc =
                new DisconnectedSubgraphChecker(this, scratchList);

            disjointMerge = ReteStaticMapping.combine(scratchList, dsc);
        }

        return disjointMerge;

    }

    /**
     * Tries to pick "the best" operator edge in the given list
     * of operator edges that could connect or make use of the 
     * disconnected components on the openList. 
     * 
     * "The best" is a heuristic criterion that is now chosen to be 
     * the edge operator whose arguments lie on the greatest number
     * of components on the open list. If more than one is found
     * then one is taken that would build a collectively larger
     * new component. 
     * 
     * This routine assumes that at least one of the operator
     * edges in the given list already has it all the argument 
     * nodes already in the components on the open list    
     *   
     * @param openList  The list of "seemingly" disconnected components of a rule
     * @param operatorEdges The list of candidate operator edges.
     * @param argumentSources Output parameter. The list of components 
     *                        containing the argument nodes of the operator reside.
     *                        No component is repeated in the list.  
     * @return The operator edge picked. Will return <code>null</code> if
     * the parameter operatorEdges is empty, or none of the operator edges
     * have their arguments on the open list 
     * otherwise it will definitely return some operator edge.
     */
    private OperatorEdge pickOneOperatorEdge(StaticMap openList,
            Set<OperatorEdge> operatorEdges,
            List<ReteStaticMapping> argumentSources) {
        OperatorEdge result = null;

        final HashMap<OperatorEdge,List<ReteStaticMapping>> candidates =
            new HashMap<OperatorEdge,List<ReteStaticMapping>>();
        for (OperatorEdge edge : operatorEdges) {
            boolean allArgumentsFound = true;
            List<ReteStaticMapping> argumentComponents =
                new ArrayList<ReteStaticMapping>();
            for (VariableNode vn : edge.source().getArguments()) {
                boolean found = false;
                for (ReteStaticMapping component : openList) {
                    if (component.getLhsNodes().contains(vn)) {
                        found = true;
                        if (!argumentComponents.contains(component)) {
                            argumentComponents.add(component);
                        }
                        break;
                    }
                }
                if (!found) {
                    allArgumentsFound = false;
                    break;
                }
            }
            if (allArgumentsFound) {
                candidates.put(edge, argumentComponents);
            }
        }
        OperatorEdge[] resultCandidates =
            new OperatorEdge[candidates.keySet().size()];
        candidates.keySet().toArray(resultCandidates);
        Arrays.sort(resultCandidates, new Comparator<OperatorEdge>() {

            @Override
            public int compare(OperatorEdge arg0, OperatorEdge arg1) {
                int result =
                    candidates.get(arg0).size() - candidates.get(arg1).size();
                if (result == 0) {
                    result =
                        getTotalSize(candidates.get(arg0))
                            - getTotalSize(candidates.get(arg1));
                }
                return 0;
            }

            private int getTotalSize(List<ReteStaticMapping> argumentComps) {
                int result = 0;
                for (int i = 0; i < argumentComps.size(); i++) {
                    result += argumentComps.get(i).getElements().length;
                }
                return result;
            }

        });
        result = resultCandidates[0];
        argumentSources.clear();
        argumentSources.addAll(candidates.get(result));
        return result;
    }

    private void addEmptyWordAcceptingAndNegativePathCheckers(
            StaticMap openList, Set<RuleEdge> emptyPathEdges, boolean keepPrefix) {
        assert openList.size() == 1;
        for (RuleEdge e : emptyPathEdges) {
            RegExpr exp = e.label().getMatchExpr();
            ReteStaticMapping m1 = openList.get(0);
            AbstractPathChecker pc =
                this.pathCheckerFactory.getPathCheckerFor((exp.isNeg())
                        ? exp.getNegOperand() : exp);
            ReteStaticMapping m2 =
                new ReteStaticMapping(pc, new RuleElement[] {e.source(),
                    e.target()});
            if (exp.isNeg()) {
                NegativeFilterSubgraphCheckerNode<ReteSimpleMatch,RetePathMatch> sg =
                    new NegativeFilterSubgraphCheckerNode<ReteSimpleMatch,RetePathMatch>(
                        this, m1, m2, keepPrefix);
                m1 = ReteStaticMapping.combine(m1, m2, sg);
            } else {
                SubgraphCheckerNode<ReteSimpleMatch,RetePathMatch> sg =
                    new SubgraphCheckerNode<ReteSimpleMatch,RetePathMatch>(
                        this, m1, m2, keepPrefix);
                m1 = ReteStaticMapping.combine(m1, m2, sg);
            }
            openList.set(0, m1);
        }
        assert openList.size() == 1;
    }

    /**
     * Returns the collection of edges in the given graph's {@link Graph#edgeSet()}
     * in the order that is deemed suitable for making RETE. 
     * 
     * @param c The condition from target of which the edges have to be listed. 
     * @return A collection of edges of the given condition. 
     */
    protected Collection<RuleEdge> getEdgeCollection(Condition c) {

        Collection<RuleEdge> result = c.getPattern().edgeSet();

        result = new ArrayList<RuleEdge>(c.getPattern().edgeSet());
        Collections.sort((List<RuleEdge>) result, new Comparator<RuleEdge>() {
            @Override
            public int compare(RuleEdge e1, RuleEdge e2) {
                return e2.compareTo(e1);
            }
        });

        return result;
    }

    private RuleNode translate(RuleFactory factory,
            RuleGraphMorphism translationMap, RuleNode node) {
        RuleNode result = node;
        if (translationMap != null) {
            result = translationMap.getNode(node);
            if (result == null) {
                result = node;
            }
        }
        return result;
    }

    private RuleEdge translate(RuleFactory factory,
            RuleGraphMorphism translationMap, RuleEdge edge) {
        RuleEdge result = edge;
        if (translationMap != null) {
            RuleNode n1 = translate(factory, translationMap, edge.source());
            RuleNode n2 = translate(factory, translationMap, edge.target());
            if (!edge.source().equals(n1) || !edge.target().equals(n2)) {
                result = factory.createEdge(n1, edge.label(), n2);
            }
        }
        return result;
    }

    /**
     * Goes through the given edge-set and node-set and creates the proper
     * static mappings and puts them on the given open list.
     * 
     * @param openList The static mappings between the current rule
     *                 and the n-nodes in the RETE network. This list will be
     *                 filled by this method with mappings of normal nodes and
     *                 and edges.
     * @param ruleGraph  The rule graph whose nodes and edges should be processed
     * @param emptyAndNegativePathEdges  This is an output parameter. This method
     *                                   fills up this collection with the edges
     *                                   that are either negative path match (labelled
     *                                   with a regular expression beginning with !)
     *                                   or edges that accept empty paths. These
     *                                   are not mapped and are not put on the open 
     *                                   list so that they could be processed after 
     *                                   everything else is processed in building 
     *                                   the RETE network. 
     * @param operatorEdges This is an output parameter. This routine will just
     *                      collects the data operator edges in this set without
     *                      statically mapping them and putting them on the open-list.                      
     */
    private void mapEdgesAndNodes(StaticMap openList, RuleGraph ruleGraph,
            Set<RuleEdge> emptyAndNegativePathEdges,
            Set<OperatorEdge> operatorEdges) {

        Collection<RuleNode> mappedLHSNodes = new HashSet<RuleNode>();
        Collection<RuleEdge> edgeSet = ruleGraph.edgeSet();
        Collection<RuleNode> nodeSet = ruleGraph.nodeSet();

        //Adding the required edge-checkers if needed.
        for (RuleEdge e : edgeSet) {
            ReteStaticMapping mapping = null;
            if (e instanceof OperatorEdge) {
                operatorEdges.add((OperatorEdge) e);
                // We don't need n-node-checkers for those
                mappedLHSNodes.add(e.target());
                mappedLHSNodes.add(e.source());
            } else if (e instanceof ArgumentEdge) {
                continue;
            } else if (e.label().isAtom() || e.label().isWildcard()) {
                EdgeCheckerNode edgeChecker = findEdgeCheckerForEdge(e);
                if (edgeChecker == null) {
                    edgeChecker = new EdgeCheckerNode(this, e);
                    this.root.addSuccessor(edgeChecker);
                }
                mapping =
                    new ReteStaticMapping(edgeChecker, new RuleElement[] {e});
            } else if (!e.label().getMatchExpr().isAcceptsEmptyWord()
                && !e.label().getMatchExpr().isNeg()) {
                AbstractPathChecker pathChecker =
                    this.pathCheckerFactory.getPathCheckerFor(e.label().getMatchExpr());
                mapping =
                    new ReteStaticMapping(pathChecker, new RuleElement[] {
                        e.source(), e.target()});
            } else {
                emptyAndNegativePathEdges.add(e);
            }

            if (mapping != null) {
                openList.add(mapping);
                mappedLHSNodes.add(e.source());
                mappedLHSNodes.add(e.target());
            }
        }
        //Now we see if there are any unmatched nodes on the lhs
        //These are isolated nodes. We will use one node checker but each
        //will be represented by a separate static mapping in the open list.
        //This part is a deviation from the standard algorithm spec.        
        for (RuleNode n : nodeSet) {
            if (!mappedLHSNodes.contains(n)
                && !this.quantifierCountCheckerNodes.containsKey(n)) {
                NodeChecker nc = findNodeCheckerForNode(n);
                ReteStaticMapping mapping =
                    new ReteStaticMapping(nc, new RuleElement[] {n});
                openList.add(mapping);
            }
        }
    }

    /**
     * Prepares an bijective mapping between a the nodes of given rule graph and an 
     * isomorphic copy of it which has new node numbers.
     * 
     * @param source The graph to be replicated with new node numbers 
     * @return The mapping from the nodes of the <code>source</code>
     *         to the newly made/numbered nodes. 
     */
    private RuleGraphMorphism createRuleMorphismForCloning(RuleGraph source) {
        RuleGraphMorphism result = source.getFactory().createMorphism();
        for (RuleNode n : source.nodeSet()) {
            if (n instanceof VariableNode) {
                VariableNode vn = (VariableNode) n;
                if (vn.getConstant() != null) {
                    result.nodeMap().put(
                        n,
                        source.getFactory().createVariableNode(
                            source.getFactory().getMaxNodeNr() + 1,
                            vn.getConstant()));

                } else {
                    result.nodeMap().put(
                        n,
                        source.getFactory().createVariableNode(
                            source.getFactory().getMaxNodeNr() + 1,
                            vn.getSignature()));
                }
            } else {

                result.nodeMap().put(
                    n,
                    source.getFactory().createNode(
                        source.getFactory().getMaxNodeNr() + 1,
                        n.getType().label(), n.isSharp()));
            }

        }
        return result;
    }

    /**
     * Generates an isomorphic copy of a given rule graph based on a bijective
     * node map. 
     * 
     * @param source The rule graph to be copied
     * @param nodeMapping A bijection between nodes of <code>source</code>
     * and the nodes of the expected result of this method.
     *  
     * @return A graph isomorphic to <code>source</code> whose node set
     * is equal to the range of <code>nodeMapping</code>.
     * 
     */
    private RuleGraph copyAndRenumberNodes(RuleGraph source,
            RuleGraphMorphism nodeMapping) {
        RuleGraph result = source.newGraph(source.getName());
        for (RuleNode n : source.nodeSet()) {
            result.addNode(nodeMapping.getNode(n));
        }
        for (RuleEdge e : source.edgeSet()) {
            result.addEdge(translate(source.getFactory(), nodeMapping, e));
        }
        return result;
    }

    /**
     * Makes a copy of a Nac condition's rootMap given a node renumbering 
     * for the nac's target.  
     * 
     * @param sourceRootNodes the root nodes of the NAC condition
     * @param nodeMapping The node renumbering map  
     */
    private RuleGraphMorphism copyRootMap(Set<RuleNode> sourceRootNodes,
            RuleGraphMorphism nodeMapping) {
        RuleGraphMorphism result = new RuleGraphMorphism();
        for (RuleNode sourceEntry : sourceRootNodes) {
            result.nodeMap().put(sourceEntry, nodeMapping.getNode(sourceEntry));
        }
        return result;
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

    @SuppressWarnings("unchecked")
    private void processNacs(ReteStaticMapping lastSubgraphMapping,
            Set<Condition> nacs, ConditionChecker positiveConditionChecker) {

        assert (lastSubgraphMapping == null)
            || (positiveConditionChecker.getAntecedents().get(0).equals(lastSubgraphMapping.getNNode()));

        StaticMap openList = new StaticMap();

        List<ReteStaticMapping> byPassList = new ArrayList<ReteStaticMapping>();
        for (Condition nac : nacs) {
            byPassList.clear();
            openList.clear();

            //we need to renumber the nodes in the nac's target
            //to avoid in any mix-up in the join-equalities
            //of the entailing composite subgraph checkers.
            RuleGraphMorphism nodeRenumberingMapping =
                createRuleMorphismForCloning(nac.getPattern());
            RuleGraph newNacGraph =
                copyAndRenumberNodes(nac.getPattern(), nodeRenumberingMapping);
            RuleGraphMorphism newRootMap =
                copyRootMap(nac.getRoot().nodeSet(), nodeRenumberingMapping);

            ReteStaticMapping m1 =
                duplicateAndTranslateMapping(nac.getPattern().getFactory(),
                    lastSubgraphMapping, newRootMap);
            if (m1 != null) {
                byPassList.add(m1);
                openList.add(m1);
            }

            Set<RuleEdge> emptyAcceptingAndNegativeEdges =
                new TreeHashSet<RuleEdge>();
            Set<OperatorEdge> operatorEdge = new TreeHashSet<OperatorEdge>();
            mapEdgesAndNodes(openList, newNacGraph,
                emptyAcceptingAndNegativeEdges, operatorEdge);
            if (m1 == null) {
                m1 = openList.get(0);
                byPassList.add(m1);
            }

            while (openList.size() > 1) {
                ReteStaticMapping m2 = pickCheckerNodeConnectedTo(openList, m1);
                if (m2 == null) {
                    m2 = pickTheNextLargestCheckerNode(openList, byPassList);
                }
                SubgraphCheckerNode sg =
                    SubgraphCheckerNode.create(this, m1, m2, true);
                m1 = ReteStaticMapping.combine(m1, m2, sg);
                openList.set(0, m1);
                if (!byPassList.isEmpty()) {
                    byPassList.set(0, m1);
                } else {
                    byPassList.add(m1);
                }
                openList.remove(m2);
            }
            if (emptyAcceptingAndNegativeEdges.size() > 0) {
                addEmptyWordAcceptingAndNegativePathCheckers(openList,
                    emptyAcceptingAndNegativeEdges, true);
            }

            CompositeConditionChecker result =
                new CompositeConditionChecker(this, nac,
                    positiveConditionChecker, openList.get(0));
            this.compositeConditionCheckerNodes.add(result);
        }
    }

    private ReteStaticMapping duplicateAndTranslateMapping(RuleFactory factory,
            ReteStaticMapping source, RuleGraphMorphism translationMap) {
        ReteStaticMapping result = null;
        if (source != null) {
            RuleElement[] oldElements = source.getElements();
            RuleElement[] newElements = new RuleElement[oldElements.length];
            for (int i = 0; i < newElements.length; i++) {
                if (oldElements[i] instanceof RuleEdge) {
                    newElements[i] =
                        translate(factory, translationMap,
                            (RuleEdge) oldElements[i]);
                } else {
                    newElements[i] =
                        translate(factory, translationMap,
                            (RuleNode) oldElements[i]);
                }
            }
            result = new ReteStaticMapping(source.getNNode(), newElements);
        }
        return result;
    }

    private NodeChecker findNodeCheckerForNode(RuleNode n) {

        NodeChecker result = null;
        if (n instanceof DefaultRuleNode) {
            DefaultNodeChecker dnc = this.defaultNodeCheckers.get(n.getType());
            if (dnc == null) {
                dnc = new DefaultNodeChecker(this, n);
                this.defaultNodeCheckers.put(n.getType(), dnc);
                this.root.addSuccessor(dnc);
            }
            result = dnc;
        } else if (n instanceof VariableNode) {
            VariableNode vn = (VariableNode) n;
            assert vn.getConstant() != null;
            if (this.valueNodeCheckerNodes.get(vn.getConstant()) != null) {
                result = this.valueNodeCheckerNodes.get(vn.getConstant());
            } else {
                ValueNodeChecker vnc = new ValueNodeChecker(this, vn);
                this.root.addSuccessor(vnc);
                this.valueNodeCheckerNodes.put(vn.getConstant(), vnc);
                result = vnc;
            }
        }
        return result;
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
        assert this.isUpdating();
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
        assert this.isUpdating();
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
     * Retrieves the quantifier count checker n-node for the given condition.
     * 
     * @param c The condition. It has to be universal and it must have a count node 
     *          associated with it in the rule.
     */
    public QuantifierCountChecker getQuantifierCountCheckerFor(Condition c) {
        assert (c.getOp() == Op.FORALL) && (c.getCountNode() != null);
        return this.quantifierCountCheckerNodes.get(c.getCountNode());
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
     * Returns a map of default node checkers based on their type.
     */
    public HashMap<TypeNode,DefaultNodeChecker> getDefaultNodeCheckers() {
        return this.defaultNodeCheckers;
    }

    /**
     * Returns a collection of default node checkers.
     */
    public DefaultNodeChecker getDefaultNodeCheckerForType(TypeNode type) {
        return this.defaultNodeCheckers.get(type);
    }

    /**
     * Initialises the RETE network by feeding all nodes and edges of a given
     * host graph to it.
     *  
     * @param g The given host graph.
     */
    public void processGraph(HostGraph g) {
        this.hostFactory = g.getFactory();
        this.getState().clearSubscribers();
        this.getState().initializeSubscribers();
        ReteUpdateMode oldUpdateMode = this.getState().getUpdateMode();
        this.setUpdating(true);
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
        this.setUpdating(false);
    }

    /** Creates and returns a graph showing the structure of this RETE network. */
    public DefaultGraph toPlainGraph() {
        DefaultGraph graph = new DefaultGraph(this.grammarName + "-rete");
        graph.setRole(GraphRole.RETE);
        Map<ReteNetworkNode,DefaultNode> map =
            new HashMap<ReteNetworkNode,DefaultNode>();
        DefaultNode rootNode = graph.addNode();
        map.put(this.getRoot(), rootNode);
        graph.addEdge(rootNode, "ROOT", rootNode);
        addChildren(graph, map, this.getRoot());
        addEmptyConditions(graph, map);
        addQuantifierCountCheckers(graph, map);
        addSubConditionEdges(graph, map);
        return graph;
    }

    private void addQuantifierCountCheckers(DefaultGraph graph,
            Map<ReteNetworkNode,DefaultNode> map) {
        for (ConditionChecker cc : this.getConditonCheckerNodes()) {
            QuantifierCountChecker qcc = cc.getCountCheckerNode();
            if (qcc != null) {
                DefaultNode qccNode = graph.addNode();
                map.put(qcc, qccNode);
                DefaultEdge[] flags = makeNNodeLabels(qcc, qccNode);
                for (DefaultEdge f : flags) {
                    graph.addEdge(f);
                }
                String l = "count";
                graph.addEdge(map.get(cc), l, map.get(qcc));
                addChildren(graph, map, qcc);
            }
        }
    }

    private void addEmptyConditions(DefaultGraph graph,
            Map<ReteNetworkNode,DefaultNode> map) {
        for (ConditionChecker cc : this.getConditonCheckerNodes()) {
            if (cc.isEmpty()) {
                DefaultNode conditionCheckerNode = graph.addNode();
                map.put(cc, conditionCheckerNode);
                DefaultEdge[] flags = makeNNodeLabels(cc, conditionCheckerNode);
                for (DefaultEdge f : flags) {
                    graph.addEdge(f);
                }
            }
        }
    }

    private void addSubConditionEdges(DefaultGraph graph,
            Map<ReteNetworkNode,DefaultNode> map) {
        for (ConditionChecker cc : this.getConditonCheckerNodes()) {
            ConditionChecker parent = cc.getParent();
            if (parent != null) {
                String l = "subcondition";
                graph.addEdge(map.get(cc), l, map.get(parent));
            }
        }

        for (CompositeConditionChecker cc : this.getCompositeConditonCheckerNodes()) {
            ConditionChecker parent = cc.getParent();
            if (parent != null) {
                String l = "NAC";
                graph.addEdge(map.get(cc), l, map.get(parent));
            }
        }
    }

    private void addChildren(DefaultGraph graph,
            Map<ReteNetworkNode,DefaultNode> map, ReteNetworkNode nnode) {
        DefaultNode jNode = map.get(nnode);
        boolean navigate;
        if (jNode != null) {
            ReteNetworkNode previous = null;
            int repeatCounter = 0;
            for (ReteNetworkNode childNNode : nnode.getSuccessors()) {
                repeatCounter =
                    (previous == childNNode) ? repeatCounter + 1 : 0;
                navigate = false;
                DefaultNode childJNode = map.get(childNNode);
                if (childJNode == null) {
                    childJNode = graph.addNode();
                    DefaultEdge[] flags =
                        makeNNodeLabels(childNNode, childJNode);
                    for (DefaultEdge f : flags) {
                        graph.addEdge(f);
                    }
                    map.put(childNNode, childJNode);
                    navigate = true;
                }

                if (childNNode instanceof SubgraphCheckerNode) {
                    if (childNNode.getAntecedents().get(0) != childNNode.getAntecedents().get(
                        1)) {
                        if (nnode == childNNode.getAntecedents().get(0)) {
                            graph.addEdge(jNode, "left", childJNode);
                        } else {
                            graph.addEdge(jNode, "right", childJNode);
                        }
                    } else {
                        graph.addEdge(jNode, "left", childJNode);
                        graph.addEdge(jNode, "right", childJNode);
                    }
                } else if ((childNNode instanceof ConditionChecker)
                    && (repeatCounter > 0)) {
                    graph.addEdge(jNode, "receive_" + repeatCounter, childJNode);
                } else {
                    graph.addEdge(jNode, "receive", childJNode);
                }

                if (navigate) {
                    addChildren(graph, map, childNNode);
                }
                previous = childNNode;
            }
        }
    }

    private DefaultEdge[] makeNNodeLabels(ReteNetworkNode nnode,
            DefaultNode source) {
        ArrayList<DefaultEdge> result = new ArrayList<DefaultEdge>();
        if (nnode instanceof RootNode) {
            result.add(DefaultEdge.createEdge(source, "ROOT", source));
        } else if (nnode instanceof DefaultNodeChecker) {
            result.add(DefaultEdge.createEdge(source, "Node Checker", source));
            result.add(DefaultEdge.createEdge(source,
                ((DefaultNodeChecker) nnode).getNode().toString(), source));
        } else if (nnode instanceof ValueNodeChecker) {
            result.add(DefaultEdge.createEdge(
                source,
                String.format(
                    "Value Node Checker - %s ",
                    ((VariableNode) ((ValueNodeChecker) nnode).getNode()).getConstant().getSymbol()),
                source));
            result.add(DefaultEdge.createEdge(source, ":"
                + ((ValueNodeChecker) nnode).getNode().toString(), source));
        } else if (nnode instanceof QuantifierCountChecker) {
            result.add(DefaultEdge.createEdge(source,
                String.format("Quantifier Count Checker "), source));

        } else if (nnode instanceof EdgeCheckerNode) {
            result.add(DefaultEdge.createEdge(source, "Edge Checker", source));
            result.add(DefaultEdge.createEdge(source, ":"
                + ((EdgeCheckerNode) nnode).getEdge().toString(), source));
        } else if (nnode instanceof SubgraphCheckerNode) {
            String[] lines = nnode.toString().split("\n");
            for (String s : lines) {
                result.add(DefaultEdge.createEdge(source, s, source));
            }
        } else if (nnode instanceof DisconnectedSubgraphChecker) {
            result.add(DefaultEdge.createEdge(source,
                "DisconnectedSubgraphChecker", source));
        } else if (nnode instanceof ProductionNode) {
            result.add(DefaultEdge.createEdge(source, "- Production Node "
                + (((ConditionChecker) nnode).isIndexed() ? "(idx)" : "()"),
                source));
            result.add(DefaultEdge.createEdge(source, "-"
                + ((ProductionNode) nnode).getCondition().getName(), source));
            for (int i = 0; i < ((ProductionNode) nnode).getPattern().length; i++) {
                RuleElement e = ((ProductionNode) nnode).getPattern()[i];
                result.add(DefaultEdge.createEdge(source, ":" + "--" + i + " "
                    + e.toString(), source));
            }
        } else if (nnode instanceof ConditionChecker) {
            result.add(DefaultEdge.createEdge(source, "- Condition Checker "
                + (((ConditionChecker) nnode).isIndexed() ? "(idx)" : "()"),
                source));
            for (int i = 0; i < ((ConditionChecker) nnode).getPattern().length; i++) {
                RuleElement e = ((ConditionChecker) nnode).getPattern()[i];
                result.add(DefaultEdge.createEdge(source, ":" + "--" + i + " "
                    + e.toString(), source));
            }
        } else {
            String[] lines = nnode.toString().split("\n");
            for (String s : lines) {
                result.add(DefaultEdge.createEdge(source, s, source));
            }
        }
        DefaultEdge[] res = new DefaultEdge[result.size()];
        return result.toArray(res);
    }

    /**
     * Saves the RETE network's shape into a GST file.
     * 
     * @param filePath the name of the saved file. If no extension is given,
     * a <tt>.gxl</tt> extension is added.
     * @param name the name of the network
     */
    public void save(String filePath, String name) {
        DefaultGraph graph = toPlainGraph();
        graph.setName(name);
        File file = new File(FileType.GXL_FILTER.addExtension(filePath));
        try {
            DefaultGxl graphLoader = DefaultGxl.getInstance();
            graphLoader.marshalGraph(graph, file);
        } catch (IOException exc) {
            throw new RuntimeException(String.format(
                "Error while saving graph to '%s'", file), exc);
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
     * @return The host factory for the host graph being processed. 
     */
    public HostFactory getHostFactory() {
        return this.hostFactory;
    }

    /**
     * Returns a map of constants to ValueCheckerNode for those
     * rule nodes of type {@link VariableNode} that explicitly
     * represent a constant in some rule. 
     */
    public HashMap<Constant,ValueNodeChecker> getValueNodeCheckerNodes() {
        return this.valueNodeCheckerNodes;
    }

    /**
     * Puts the network in the update reception mode.  
     */
    public void setUpdating(boolean updating) {
        if (updating && !this.updating) {
            this.updating = updating;
            getState().notifyUpdateBegin();
        } else if (!updating && this.updating) {
            this.updating = updating;
            getState().notifyUpdateEnd();
        } else {
            this.updating = updating;
        }
    }

    /**
     * @return <code>true</code> if the network is in the process of receiving
     * updates, <code>false</code> otherwise. 
     */
    public boolean isUpdating() {
        return this.updating;
    }

    /**
     * @return The {@link ReteSearchEngine} to which this network belongs
     */
    public ReteSearchEngine getOwnerEngine() {
        return this.ownerEngine;
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
        /** These are the (isolated) nodes and edges of some rule's LHS. */
        private RuleElement[] elements;

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
        public ReteStaticMapping(ReteNetworkNode reteNode,
                RuleElement[] mappedTo) {
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

        public static ReteStaticMapping mapDataOperatorNode(
                DataOperatorChecker doc, OperatorEdge opEdge,
                ReteStaticMapping antecedentMapping) {
            assert antecedentMapping.getNNode().equals(
                doc.getAntecedents().get(0));

            RuleElement[] mapto = new RuleElement[doc.getPattern().length];
            for (int i = 0; i < antecedentMapping.getElements().length; i++) {
                mapto[i] = antecedentMapping.getElements()[i];
            }
            mapto[mapto.length - 1] = opEdge.target();
            return new ReteStaticMapping(doc, mapto);

        }

        @SuppressWarnings("unchecked")
        public static ReteStaticMapping combine(ReteStaticMapping oneMap,
                ReteStaticMapping otherMap, SubgraphCheckerNode suc) {
            assert oneMap.getNNode().getSuccessors().contains(suc)
                && otherMap.getNNode().getSuccessors().contains(suc);
            ReteStaticMapping left =
                suc.getAntecedents().get(0).equals(oneMap.getNNode()) ? oneMap
                        : otherMap;
            ReteStaticMapping right = (left == oneMap) ? otherMap : oneMap;
            RuleElement[] combinedElements =
                new RuleElement[left.getElements().length
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

            List<RuleElement> tempElementsList = new ArrayList<RuleElement>();
            for (int i = 0; i < maps.size(); i++) {
                RuleElement[] elems = maps.get(i).getElements();
                for (int j = 0; j < elems.length; j++) {
                    tempElementsList.add(elems[j]);
                }
            }

            RuleElement[] combinedElements =
                new RuleElement[tempElementsList.size()];
            combinedElements = tempElementsList.toArray(combinedElements);

            ReteStaticMapping result =
                new ReteStaticMapping(suc, combinedElements);
            return result;
        }

        @SuppressWarnings("unchecked")
        public static ReteStaticMapping combine(ReteStaticMapping oneMap,
                ReteStaticMapping otherMap,
                NegativeFilterSubgraphCheckerNode suc) {

            ReteStaticMapping left =
                suc.getAntecedents().get(0).equals(oneMap.getNNode()) ? oneMap
                        : otherMap;
            RuleElement[] combinedElements =
                new RuleElement[left.getElements().length];
            int i = 0;
            for (; i < left.getElements().length; i++) {
                combinedElements[i] = left.getElements()[i];
            }

            ReteStaticMapping result =
                new ReteStaticMapping(suc, combinedElements);
            return result;
        }

        public ReteNetworkNode getNNode() {
            return this.nNode;
        }

        public RuleElement[] getElements() {
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
        private Set<ReteStateSubscriber> subscribers =
            new HashSet<ReteStateSubscriber>();
        private Set<ReteStateSubscriber> updateSubscribers =
            new HashSet<ReteStateSubscriber>();
        private ReteUpdateMode updateMode = ReteUpdateMode.NORMAL;

        protected ReteState(ReteNetwork owner) {
            this.owner = owner;
        }

        public ReteNetwork getOwner() {
            return this.owner;
        }

        public synchronized void subscribe(ReteStateSubscriber sb) {
            this.subscribe(sb, false);
        }

        public synchronized void subscribe(ReteStateSubscriber sb,
                boolean receiveUpdateNotifications) {
            this.subscribers.add(sb);
            if (receiveUpdateNotifications) {
                this.updateSubscribers.add(sb);
            }
        }

        public void unsubscribe(ReteStateSubscriber sb) {
            sb.clear();
            this.subscribers.remove(sb);
            this.updateSubscribers.remove(sb);
        }

        public void clearSubscribers() {
            for (ReteStateSubscriber sb : this.subscribers) {
                sb.clear();
            }
        }

        public synchronized void initializeSubscribers() {
            for (ReteStateSubscriber sb : this.subscribers) {
                sb.initialize();
            }
        }

        public synchronized void notifyUpdateBegin() {
            for (ReteStateSubscriber sb : this.updateSubscribers) {
                sb.updateBegin();
            }
        }

        public synchronized void notifyUpdateEnd() {
            for (ReteStateSubscriber sb : this.updateSubscribers) {
                sb.updateEnd();
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
