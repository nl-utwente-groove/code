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
 * $Id: RuleGraph.java,v 1.8 2007-04-04 07:04:23 rensink Exp $
 */

package groove.trans.view;

import groove.graph.AbstractGraph;
import groove.graph.DefaultLabel;
import groove.graph.DefaultNode;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.GraphFactory;
import groove.graph.GraphInfo;
import groove.graph.GraphShape;
import groove.graph.Label;
import groove.graph.Morphism;
import groove.graph.Node;
import groove.graph.NodeEdgeHashMap;
import groove.graph.NodeEdgeMap;
import groove.graph.NodeSetEdgeSetGraph;
import groove.graph.iso.DefaultIsoChecker;
import groove.graph.iso.IsoChecker;
import groove.rel.RegExpr;
import groove.rel.RegExprGraph;
import groove.rel.RegExprLabel;
import groove.rel.VarGraph;
import groove.trans.DefaultNAC;
import groove.trans.EdgeEmbargo;
import groove.trans.GraphCondition;
import groove.trans.MergeEmbargo;
import groove.trans.NAC;
import groove.trans.NameLabel;
import groove.trans.Rule;
import groove.trans.RuleFactory;
import groove.trans.RuleProperties;
import groove.util.FormatException;
import groove.util.Groove;
import groove.util.Pair;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Provides a graph view upon a production rule.
 * The nodes and edges are divided into embargoes, erasers, readers and creators, 
 * with the following intuition: <ul>
 * <li> Maximal connected embargo subgraphs correspond to negative application conditions.
 * <li> Erasers correspond to LHS elements that are not RHS.
 * <li> Readers (the default) are elements that are both LHS and RHS.
 * <li> Creators are RHS elements that are not LHS.</ul>
 * @author Arend Rensink
 * @version $Revision: 1.8 $
 * @deprecated replaced by AspectRuleView
 */
@Deprecated
public class RuleGraph extends NodeSetEdgeSetGraph implements RuleView {
    /** Number of node/edge roles */
    static public final int NR_OF_ROLES = 4;
    /** Code for embargo nodes and edges. */
    static public final int EMBARGO = 0;
    /** Code for eraser nodes and edges. */
    static public final int ERASER = 1;
    /** Code for reader nodes and edges. */
    static public final int READER = 2;
    /** Code for creator nodes and edges. */
    static public final int CREATOR = 3;
    /** Default role code. */
    static public final int DEFAULT_ROLE = READER;
    /** Code. for no (valid) role */
    static public final int NO_ROLE = -1;

    /** Label text for merges (merger edges and merge embargoes) */
    static public final String MERGE_LABEL_TEXT = Groove.getXMLProperty("label.merge");
    /** Label for merges (merger edges and merge embargoes) */
    static public final Label MERGE_LABEL = DefaultLabel.createLabel(MERGE_LABEL_TEXT);
    /** Label for injection constraints */
    static public final Label NEGATIVE_MERGE_LABEL = new RegExprLabel(RegExpr.atom(MERGE_LABEL_TEXT).neg());

    /** Seperator string for role prefixes. */
    static public final String SEPARATOR = Groove.getXMLProperty("label.aspect.separator");
    /** Prefix for the label text of embargo labels. */
    static public final String EMBARGO_PREFIX = Groove.getXMLProperty("label.embargo.prefix") + SEPARATOR;
    /** Prefix for the label text of eraser labels. */
    static public final String ERASER_PREFIX = Groove.getXMLProperty("label.eraser.prefix") + SEPARATOR;
    /** Prefix for the label text of reader labels. */
    static public final String READER_PREFIX = Groove.getXMLProperty("label.reader.prefix") + SEPARATOR;
    /** Prefix for the label text of creator labels. */
    static public final String CREATOR_PREFIX = Groove.getXMLProperty("label.creator.prefix") + SEPARATOR;

    /** 
     * Array of role prefixes.
     * <b>Important:</b> the indices within this array correspond to the role code.
     */
    static public final String[] ROLE_PREFIX = { EMBARGO_PREFIX, ERASER_PREFIX, READER_PREFIX, CREATOR_PREFIX };

    /** Isomorphism checker (used for testing purposes). */
    static private final IsoChecker isoChecker = new DefaultIsoChecker();
    /** Graph factory used for building a graph view of this rule graph.*/
    static protected GraphFactory graphFactory = GraphFactory.getInstance();

    /**
     * Tests whether a given role is legal, i.e., one of
     * <tt>EMBARGO</tt>, <tt>ERASER</tt>, <tt>READER</tt> or <tt>CREATOR</tt>.
     * @param role the role to be tested
     * @return <tt>true</tt> iff <tt>role<tt> is one of
     * <tt>EMBARGO</tt>, <tt>ERASER</tt>, <tt>READER</tt> or <tt>CREATOR</tt>
     * @see #EMBARGO
     * @see #ERASER
     * @see #READER
     * @see #CREATOR
     */
    static public boolean isValidRole(int role) {
        switch (role) {
            case EMBARGO :
            case ERASER :
            case READER :
            case CREATOR :
                return true;
            default :
                return false;
        }
    }

    /**
     * Tests whether a role indicator is in the LHS of the rule, i.e., 
     * equals <tt>ERASER</tt> or <tt>READER</tt>.
     * @param role the role indicator to be tested
     * @return <tt>true</tt> iff <tt>role == ERASER || role == READER</tt>
     */
    static public boolean inLHS(int role) {
        return role == ERASER || role == READER;
    }

    /**
      * Tests whether a role indicator is in the RHS of the rule, i.e., 
      * equals <tt>CREATOR</tt> or <tt>READER</tt>.
      * @param role the role indicator to be tested
      * @return <tt>true</tt> iff <tt>role == CREATOR || role == READER</tt>
      */
    static public boolean inRHS(int role) {
        return role == READER || role == CREATOR;
    }

    /**
     * Returns the role of a string, if it were to be used as label text.
     * @param text the string whose role is to be investigated
     * @return the role as indicated by <tt>text</tt>
     * @ensure <tt>isValidRole(result) || result == NO_ROLE</tt>
     */
    static public int labelRole(String text) {
        for (int i = 0; i < ROLE_PREFIX.length; i++) {
            if (CONSTRUCTOR_DEBUG) {
                Groove.message("Is \"" + ROLE_PREFIX[i] + "\" a prefix of " + text + "?");
            }
            if (text.startsWith(ROLE_PREFIX[i]))
                return i;
        }
        // no role prefix recognised: take default
        return NO_ROLE;
    }

    /**
     * Returns the role of a label as indicated by the prefix of its text.
     * @param label the label whose role is to be investigated
     * @return the role as indicated by <tt>label</tt>
     * @ensure <tt>isValidRole(result) || result == NO_ROLE</tt>
     */
    static public int labelRole(Label label) {
        return labelRole(label.text());
    }

    /**
     * Returns the role as indicated by an ordinary edge.
     * An edge indicates a role if it is a flag labelled only with the role prefix.
     * @param edge the label whose role indication is to be investigated
     * @return the role as indicated by <tt>edge</tt>
     * @ensure <tt>isValidRole(result) || result == NO_ROLE</tt>
     */
    static public int selfEdgeRole(Edge edge) {
        if (edge.endCount() > 1 && !edge.source().equals(edge.end(Edge.TARGET_INDEX))) {
            return NO_ROLE;
        } else {
            int role = labelRole(edge.label());
            if (role != NO_ROLE && !edge.label().text().equals(ROLE_PREFIX[role]))
                role = NO_ROLE;
            return role;
        }
    }

    /**
     * Returns the text of a label as minus its role prefix.
     * @param label the label whose text prefix is to be cut off
     * @return label text without its role prefix 
     */
    static public String labelText(Label label) {
        int role = labelRole(label);
        if (role == NO_ROLE)
            return label.text();
        else
            return label.text().substring(ROLE_PREFIX[role].length());
    }

    /**
     * This main is provided for testing purposes only.
     * @param args names of XML files to be used as test input
     */
    static public void main(String[] args) {
        System.out.println("Test of RuleGraph");
        System.out.println("=================");
        groove.io.UntypedGxl gxl = new groove.io.UntypedGxl();
        for (int i = 0; i < args.length; i++) {
            System.out.println("\nTesting: " + args[i]);
            NameLabel ruleName = new NameLabel(args[i]);
            try {
                // Create file
                System.out.print("    Creating file: ");
                java.io.File file = new java.io.File(args[i]);
                System.out.println("OK");
                // Unmarshal graph
                System.out.print("    Unmarshalling graph: ");
                Graph graph = gxl.unmarshalGraph(file);
                System.out.println("OK");
                // construct rule graph
                System.out.print("    Constructing rule graph from graph: ");
                RuleGraph ruleGraph = new RuleGraph(graph, ruleName);
                System.out.println("OK");
                // convert rule graph back into graph and test for isomorphism
                System.out.print("    Reconstructing graph from rule graph: ");
                GraphShape newGraph = ruleGraph.toGraph();
                System.out.println("OK");
                System.out.print("    Testing for isomorphism of original and reconstructed graph: ");
                if (isoChecker.areIsomorphic(new RuleGraph(newGraph, ruleName),ruleGraph))
                    System.out.println("OK");
                else {
                    System.out.println("ERROR");
                    System.out.println("Original graph");
                    System.out.println("--------------");
                    System.out.println(graph);
                    System.out.println("Reconstructed graph");
                    System.out.println("-------------------");
                    System.out.println(newGraph);
                }
                // convert rule graph into rule
                System.out.print("    Constructing rule from rule graph: ");
                Rule rule = ruleGraph.toRule();
                System.out.println("OK");
                System.out.println("Resulting rule:");
                System.out.println("--------------");
                System.out.println(rule);
                // convert rule back into rule graph and test for isomorphism
                System.out.print("    Reconstructing rule graph from rule: ");
                RuleGraph newRuleGraph = new RuleGraph(rule);
                System.out.println("OK");
                System.out.print("    Testing for isomorphism of original and reconstructed rule graph: ");
                if (isoChecker.areIsomorphic(newRuleGraph,ruleGraph))
                    System.out.println("OK");
                else {
                    System.out.println("ERROR");
                    System.out.println("Original rule graph");
                    System.out.println("-----------------");
                    System.out.println(ruleGraph);
                    System.out.println("Reconstructed rule graph");
                    System.out.println("------------------------");
                    System.out.println(newRuleGraph);
                }
            } catch (Exception exc) {
                exc.printStackTrace();
            }
        }
    }

    /**
     * Constructs a new rule graph on the basis of a given production rule.
     * @param rule the production rule for which a rule graph is to be constructed
     * @require <tt>rule != null</tt>
     * @throws FormatException if <code>rule</code> cannot be displayed as a {@link RuleGraph},
     * for instance because its NACs are nested too deep or not connected
     */
    public RuleGraph(Rule rule) throws FormatException {
    	this.name = rule.getName();
        this.priority = rule.getPriority();
        this.properties = rule.getProperties();
        this.rule = rule;
        try {
            // start with lhs
            Map<Node,RuleNode> lhsNodeMap = new HashMap<Node,RuleNode>();
            // add lhs nodes
            for (Node lhsNode: rule.lhs().nodeSet()) {
                int nodeRole = rule.getMorphism().containsKey(lhsNode) ? READER : ERASER;
                lhsNodeMap.put(lhsNode, addRuleNode(nodeRole));
            }
            // add lhs edges
            for (Edge lhsEdge: rule.lhs().edgeSet()) {
                int edgeRole = rule.getMorphism().containsKey(lhsEdge) ? READER : ERASER;
                addRuleEdge(images(lhsNodeMap, lhsEdge.ends()), 
                        lhsEdge.label(),
                        edgeRole);
            }
            // now add the rhs
            Map<Node,RuleNode> rhsNodeMap = new HashMap<Node,RuleNode>();
            // add rhs nodes and mergers to rule graph
            // first find out which rhs nodes correspond to readers
            for (Node lhsNode: rule.lhs().nodeSet()) {
                Node rhsNode = rule.getMorphism().getNode(lhsNode);
                if (rhsNode != null) {
                    // we have a rhs reader node
                    // check if we had it before (in which case we have a merger)
                    if (rhsNodeMap.containsKey(rhsNode)) {
                        // yes, it's a merger
                        addRuleEdge(new RuleNode[] {lhsNodeMap.get(lhsNode), rhsNodeMap.get(rhsNode)},
                                MERGE_LABEL,
                                CREATOR);
                    } else {
                        // no, it's a "fresh" reader node
                        rhsNodeMap.put(rhsNode, lhsNodeMap.get(lhsNode));
                    }
                }
            }
            // the rhs nodes not yet dealt with must be creators
            // iterate over the rhs nodes
            for (Node rhsNode: rule.rhs().nodeSet()) {
                if (!rhsNodeMap.containsKey(rhsNode)) {
                    rhsNodeMap.put(rhsNode, addRuleNode(CREATOR));
                }
            }
            // add rhs edges
            for (Edge rhsEdge: rule.rhs().edgeSet()) {
                if (!rule.getMorphism().containsValue(rhsEdge)) {
                    addRuleEdge(images(rhsNodeMap, rhsEdge.ends()), rhsEdge.label(), CREATOR);
                }
            }
            // now add the NACs
            for (GraphCondition nac: rule.getNegConjunct().getConditions()) {
                Morphism nacMorphism = nac.getPattern();
                if (nac instanceof MergeEmbargo) {
                    addInjectionEdge((MergeEmbargo) nac, lhsNodeMap, READER);                    
                } else if (nac instanceof EdgeEmbargo) {
                    addNegationEdge((EdgeEmbargo) nac, lhsNodeMap, READER);
                } else {
                    // NOTE: we're assuming the NAC is injective and connected,
                    // otherwise no rule graph can be given
                    testInjective(nacMorphism);
                    testConnected(nacMorphism.cod());
                    // store the mapping from the NAC target nodes to the rule graph
                    Map<Node,RuleNode> nacNodeMap = new HashMap<Node,RuleNode>();
                    // first register the lhs nodes
                    for (Node key: nacMorphism.dom().nodeSet()) {
                        Node image = nacMorphism.getNode(key);
                        if (image != null) {
                        	nacNodeMap.put(image, lhsNodeMap.get(key));
                        }
                    }
                    // add this nac's nodes
                    for (Node node: nacMorphism.cod().nodeSet()) {
                        if (!nacNodeMap.containsKey(node)) {
                        	nacNodeMap.put(node, addRuleNode(EMBARGO));
                        }
                    }
                    Set<Edge> newEdgeSet = new HashSet<Edge>(nacMorphism.cod().edgeSet());
                    newEdgeSet.removeAll(nacMorphism.elementMap().edgeMap().values());
                    // add this nac's edges
                    for (Edge edge: newEdgeSet) {
                            addRuleEdge(images(nacNodeMap, edge.ends()), edge.label(), EMBARGO);
                    }
                    for (GraphCondition subNac: nac.getNegConjunct().getConditions()) {
                        if (subNac instanceof MergeEmbargo) {
                            addInjectionEdge((MergeEmbargo) subNac, nacNodeMap, EMBARGO);
                        } else if (subNac instanceof EdgeEmbargo) {
                            addNegationEdge((EdgeEmbargo) subNac, nacNodeMap, EMBARGO);
                        } else {
                            throw new IllegalArgumentException("Level 2 NACs must be merge or edge embargoes");
                        }
                    }
                }
            }
        } catch (FormatException exc) {
            throw new FormatException(exc);
        }
        setFixed();
    }

    /**
     * Creates and adds an injection edge based on a given merge embargo to this rule graph.
     * The embargo is interpreted under a certain node mapping.
     * A role parameter controls whether it is a level 1 injection (READER) or level 2 (EMBARGO)
     * @throws FormatException if the role parameter is invalid
     */
    private void addInjectionEdge(MergeEmbargo embargo, Map<Node,? extends Node> nodeMap, int role) throws FormatException {
        addRuleEdge(images(nodeMap, embargo.getNodes()), NEGATIVE_MERGE_LABEL, role);
    }

    /**
     * Creates and adds a negation edge based on a given edge embargo to this rule graph.
     * The embargo is interpreted under a certain node mapping.
     * A role parameter controls whether it is a level 1 negation (READER) or level 2 (EMBARGO)
     * @throws FormatException if the role parameter is invalid
     */
    private void addNegationEdge(EdgeEmbargo embargo, Map<Node,? extends Node> nodeMap, int role) throws FormatException {
        Edge embargoEdge = embargo.getEmbargoEdge();
        Label label = embargoEdge.label();
        // we have to add a negation to the label, which may mean we first have
        // to turn it into a regular expression
        RegExpr labelExpr = label instanceof RegExprLabel ? ((RegExprLabel) label).getRegExpr() : RegExpr.atom(label.text());
        addRuleEdge(images(nodeMap, embargoEdge.ends()), new RegExprLabel(labelExpr.neg()), role);
    }
    
    /**
     * Constructs a rule graph with a given name from an (ordinary) graph.
     * Empty labels (after the role prefix) are interpreted as merge labels.
     * Priority is set to the default rule priority, and the rule factory
     * is not set.
     * @param graph the graph to be converted
     * @param name the name of the rule
     * @require <tt>graph != null</tt>
     * @throws FormatException if <tt>graph</tt> does not have
     * the required meta-format
     */
    public RuleGraph(GraphShape graph, NameLabel name) throws FormatException {
        this(graph, name, Rule.DEFAULT_PRIORITY, RuleProperties.DEFAULT_PROPERTIES);
    }

    /**
     * Constructs a rule graph with a given name from an (ordinary) graph.
     * Empty labels (after the role prefix) are interpreted as merge labels.
     * @param graph the graph to be converted
     * @param name the name of the rule
     * @param priority the priority ot the rule
     * @require <tt>graph != null</tt>
     * @throws FormatException if <tt>graph</tt> does not have
     * the required meta-format
     */
    public RuleGraph(GraphShape graph, NameLabel name, int priority, RuleProperties properties) throws FormatException {
        this.name = name;
        this.priority = priority;
        this.properties = properties;
        boolean emptyLabelIsMerge = true;
        NodeEdgeMap graphToRuleGraphMap = new NodeEdgeHashMap();
//        this.ruleFactory = ruleFactory;
        // first insert the nodes into the rule graph, while looking up their role
        for (Node node: graph.nodeSet()) {
            if (CONSTRUCTOR_DEBUG) {
                Groove.message("Adding node " + node + " to rule graph");
            }
            // ok, now look up the role: it is indicated by a self-edge with empty label
            Iterator<? extends Edge> selfEdgeIter = graph.outEdgeSet(node).iterator();
            int nodeRole = NO_ROLE;
            while (nodeRole == NO_ROLE && selfEdgeIter.hasNext()) {
                Edge edge = selfEdgeIter.next();
                if (CONSTRUCTOR_DEBUG) {
                    Groove.message("Testing " + edge + " for role indication");
                }
                nodeRole = selfEdgeRole(edge);
            }
            RuleNode ruleNode = addRuleNode(nodeRole);
            // in special cases the returned rule-node may be null
            if (ruleNode != null) {
                // make a rule node and register it
                graphToRuleGraphMap.putNode(node, ruleNode);
            }
        }
        // now insert the edges into the rule graph
        for (Edge edge: graph.edgeSet()) {
            if (selfEdgeRole(edge) == NO_ROLE) {
                // this is a proper edge; insert it into the rule graph
                int edgeRole = labelRole(edge.label());
                // find out the label text; convert empty label into merge label if required 
                String labelText = labelText(edge.label());
                if (labelText.length() == 0) {
                    if (emptyLabelIsMerge) {
                        labelText = MERGE_LABEL_TEXT;
                    } else {
                        throw new FormatException("Empty label in rule graph");
                    }
                }
                Label label = createLabel(labelText);
                if (isLabelSupported(label, edgeRole) && isGraphStructureCorrect(edge, graph)) {
                    RuleEdge ruleEdge = addRuleEdge(images(graphToRuleGraphMap.nodeMap(), edge.ends()),
                        label,
                        edgeRole);
                    // in special cases the returned edge may be null
                    if (ruleEdge != null) {
                    	graphToRuleGraphMap.putEdge(edge, ruleEdge);
                    }
                }
            } else if (selfEdgeRole(edge) != ((RuleNode) graphToRuleGraphMap.getNode(edge.source())).role())
                throw new FormatException("Node role in rule graph not uniquely identified");
        }
        Set<String> boundVars = getVars(READER, true);
        boundVars.addAll(getVars(ERASER, true));
        Set<String> rhsOnlyVars = getVars(CREATOR, false);
        if (!boundVars.containsAll(rhsOnlyVars)) {
            rhsOnlyVars.removeAll(boundVars);
            throw new FormatException("Right hand side variables "+rhsOnlyVars+" not bound on left hand side");
        }
        Set<String> embargoVars = getVars(EMBARGO, false);
        if (!boundVars.containsAll(embargoVars)) {
        	embargoVars.removeAll(boundVars);
            throw new FormatException("NAC variables "+embargoVars+" not bound on left hand side");
        }
        GraphInfo.transfer(graph, this, graphToRuleGraphMap);
        setFixed();
        rule = computeRule();
    }

    /**
     * Returns the rule factory.
     * @return the rule factory.
     */
    public RuleProperties getRuleProperties() {
    	return properties;
    }

    /**
     * Returns the rule factory.
     * @return the rule factory.
     */
    public RuleFactory getRuleFactory() {
    	return properties.getFactory();
    }

    /**
     * Returns the production rule corresponding to this rule graph.
     */
    public Rule toRule() {
    	return rule;
    }
    
    /**
     * Callback method to compute the rule for this rule graph.
     */
    protected Rule computeRule() throws FormatException {
        if (TO_RULE_DEBUG) {
            System.out.println("");
        }
        // create the new lhs
        VarGraph lhs = createRegExprGraph();
        // also create a graph for all left elements, i.e., LHS and NAC
        VarGraph left = createRegExprGraph();
        // mapping from rule graph elements to left (lhs and nac) elements
        NodeEdgeMap toLeft = new NodeEdgeHashMap();
        // create the new rhs
        VarGraph rhs = createRegExprGraph();
        // mapping from rule graph elements to RHS elements
        NodeEdgeMap toRight = new NodeEdgeHashMap();
        // we create a single graph containing all NAC nodes and edges
        // as a supergraph of the lhs graph
        // this will be partitioned later
        // for the partitioning, we separately keep a set of NAC-only elements
        Set<Node> nacNodeSet = new HashSet<Node>();
        Set<Edge> nacEdgeSet = new HashSet<Edge>();
        // rule morphism for the resulting production rule
        Morphism ruleMorph = createMorphism(lhs, rhs);
        // first add nodes to lhs, rhs, morphism and NAC graph
        Iterator<? extends Node> nodeIter = nodeSet().iterator();
        while (nodeIter.hasNext()) {
            RuleNode node = (RuleNode) nodeIter.next();
            if (inLHS(node.role())) {
            	Node lhsNodeImage = addNode(left,node);
                toLeft.putNode(node, lhsNodeImage);
                lhs.addNode(lhsNodeImage);
            }
            if (inRHS(node.role())) {
            	Node rhsNodeImage = addNode(rhs, node);
                toRight.putNode(node, rhsNodeImage);
                if (inLHS(node.role())) {
                    ruleMorph.putNode(toLeft.getNode(node), rhsNodeImage);
                }
            } else if (node.role() == EMBARGO) {
            	Node nodeImage = addNode(left, node);
                toLeft.putNode(node, nodeImage);
                nacNodeSet.add(nodeImage);
            }
        }
        Set<GraphCondition> embargoes = new HashSet<GraphCondition>();
        // now add edges to lhs, rhs and morphism
        // as well as to injections and negations
        Iterator<? extends Edge> edgeIter = edgeSet().iterator();
        while (edgeIter.hasNext()) {
            RuleEdge edge = (RuleEdge) edgeIter.next();
            // an embargo edge looks like a READER but should actually be treated as LHS-only
            boolean isEmbargo = false;
            if (inLHS(edge.role())) {
                Edge lhsEdgeImage = addEdge(left, edge, toLeft);
            	if (lhsEdgeImage == null)
            		continue;
                NAC embargo = constructEmbargo(lhs, lhsEdgeImage);
                if (embargo == null) {
                    lhs.addEdge(lhsEdgeImage);
                    toLeft.putEdge(edge, lhsEdgeImage);
                } else {
                    isEmbargo = true;
                    embargoes.add(embargo);
                }
            }
            if (inRHS(edge.role())) {
                Node[] endImages = images(toRight.nodeMap(), edge.ends());
//                Edge rhsEdgeImage = (Edge) edge.imageFor(toRight);
                if (edge.role() == CREATOR && edge.label().equals(MERGE_LABEL)) {
                    // it's a merger; it's bound to be binary
                    assert endImages.length == 2 : "Merger edge "+edge+" should be binary";
                    Node mergeFrom = endImages[Edge.SOURCE_INDEX]; 
                    Node mergeTo = endImages[Edge.TARGET_INDEX]; 
                    // existing edges will automatically be redirected
                    rhs.mergeNodes(mergeFrom, mergeTo);
                    // make that sure edges to be added later also get the right end nodes
                    toRight.putNode(edge.source(), mergeTo);
                } else if (!isEmbargo) {
                    Edge rhsEdgeImage = addEdge(rhs, edge, toRight);
                    toRight.putEdge(edge, rhsEdgeImage);
                    if (inLHS(edge.role())) {
                        ruleMorph.putEdge(toLeft.getEdge(edge), rhsEdgeImage);
                    }
                }
            }
            if (edge.role() == EMBARGO) {
            	Edge lhsEdgeImage = addEdge(left, edge, toLeft);
                nacEdgeSet.add(lhsEdgeImage);
            }
        }
        assert lhs.boundVarSet().containsAll(rhs.allVarSet()) : "Right hand side variables "+rhs.allVarSet()+" not all bound on left hand side";
        // the resulting rule
        Rule result = createRule(ruleMorph, name, priority);
        // add the nacs to the rule
        for (Pair<Set<Node>,Set<Edge>> nacSet: getConnectedSets(nacNodeSet, nacEdgeSet)) {
            result.setAndNot(constructNac(result.lhs(), nacSet.first(), nacSet.second()));
        }
        // add the embargoes
        for (GraphCondition embargo: embargoes) {
            result.setAndNot(embargo);
        }
        result.setFixed();
        if (TO_RULE_DEBUG) {
            System.out.println("Constructed rule: "+result);
        }
        return result;
    }

    /**
     * Adds a node to a graph corresponding to a certain rule node.
     * The number of the new node is the same as that of the rule node.
     * Also adds the mapping from rule node to the new node to a node map.
     * @param graph the graph to which the new node is to be added
     * @param node the rule node for which an image is to be created
     * @return the newly added node
     */
    protected Node addNode(Graph graph, RuleNode node) {
    	// we want the same node numbers in lhs and rule graph
    	// so make sure the node number is copied
	   	Node nodeImage = createLhsRhsNode(node.getNumber());
    	graph.addNode(nodeImage);
    	return nodeImage;
    }

    /**
     * Adds an edge to a graph corresponding to a certain rule edge.
     * A node map is given to determine the end points of the new edge.
     * @param graph the graph to which the new edge is to be added
     * @param edge the rule edge for which an image is to be added
     * @param elementMap the mapping of the end nodes
     * @return the newly added edge, if any
     */
    protected Edge addEdge(Graph graph, Edge edge, NodeEdgeMap elementMap) {
    	Node[] ends = images(elementMap.nodeMap(), edge.ends());
    	for (int i = 0; i < ends.length; i++) {
    		if (ends[i] == null)
    			return null;
    	}
    	return graph.addEdge(ends, edge.label());
    }

    /**
     * Constructs a negative application condition based on a LHS graph
     * and a set of graph elements that should make up the NAC target.
     * The connection between LHS and NAC target is given by identity, i.e., those 
     * elements in the NAC set that are in the LHS graph are indeed LHS elements.
     * @param lhs the LHS graph
     * @param nacNodeSet set of graph elements that should be turned into a NAC target
     */
    protected NAC constructNac(VarGraph lhs, Set<Node> nacNodeSet, Set<Edge> nacEdgeSet) {
        // distinguish merge end edge embargoes
        // they are characterised by the fact that there is precisely 1 element
        // in the nacElemSet, which is an edge
    	NAC edgeMergeEmbargo = createEdgeMergeEmbargo(lhs, nacNodeSet, nacEdgeSet);
    	if (edgeMergeEmbargo != null)
    		return edgeMergeEmbargo;

    	// if we're here it means we couldn't make an edge embargo
        if (CREATE_EMBARGO_DEBUG)
            Groove.message("Constructing general structure embargo from " + nacNodeSet);

        NAC result = new DefaultNAC(lhs, getRuleFactory());
        Morphism nacMorphism = result.getPattern();
        VarGraph nacTarget = result.getTarget();
        // add all nodes to nacTarget, and insert mappings for LHS elements
		nodesToEmbargo(nacNodeSet, nacTarget);
        // add edges and embargoes to nacTarget
        edgesToEmbargo(nacEdgeSet, result, nacMorphism, nacTarget);
        return result;
    }

	/**
	 * Adds the edges to the NAC.
	 * @param nacEdgeSet the set containing the NAC-edges
	 * @param result the resulting graph transformation rule
	 * @param nacMorphism the NAC-morphism
	 * @param nacTarget the NAC-target
	 */
	protected void edgesToEmbargo(Set<Edge> nacEdgeSet, NAC result, Morphism nacMorphism, VarGraph nacTarget) {
		for (Edge edge : nacEdgeSet) {
			// add the endpoints that were not in the nac element set; it means
			// they are lhs nodes, so add them to the nacMorphism as well
			for (int i = 0; i < edge.endCount(); i++) {
				Node end = edge.end(i);
				if (nacTarget.addNode(end)) {
					// the node identity in the lhs is the same
					nacMorphism.putNode(end, end);
				}
			}
			// now check if this edge itself represents a negative condition
			GraphCondition embargo = constructEmbargo(nacTarget, edge);
			if (embargo == null) {
				nacTarget.addEdge(edge);
			} else {
				result.setAndNot(embargo);
			}
			if (CREATE_EMBARGO_DEBUG)
				Groove.message("Added edge " + edge + " to NAC target");
		}
	}

	/**
	 * Adds the nodes to the NAC.
	 * 
	 * @param nacNodeSet
	 *            the set containing the NAC-elements
	 * @param nacTarget
	 *            the NAC-target
	 */
	protected void nodesToEmbargo(Set<Node> nacNodeSet, VarGraph nacTarget) {
		nacTarget.addNodeSet(nacNodeSet);
	}

	/**
	 * Creates an edge or merge embargo if the nacElemSet contains only one
	 * element.
	 * 
	 * @param lhs
	 *            the left-hand-side of the rule for which to create NACs
	 * @param nacNodeSet
	 *            the elements of the NAC to be created
	 * @return an edge or merge embargo if <code>nacElemSet</code> contains
	 *         exactly one element, <code>null</code> otherwise
	 */
	protected NAC createEdgeMergeEmbargo(VarGraph lhs, Set<Node> nacNodeSet, Set<Edge> nacEdgeSet) {
		NAC result = null;
		if (nacNodeSet.size() == 0 && nacEdgeSet.size() == 1) {
			Edge embargoEdge = nacEdgeSet.iterator().next();
			if (RuleGraph.MERGE_LABEL.equals(embargoEdge.label())) {
				// this is supposed to be a merge embargo
				if (CREATE_EMBARGO_DEBUG)
					Groove.message("Constructing merge embargo from "
							+ nacNodeSet);
				result = createMergeEmbargo(lhs, embargoEdge.ends());
			} else {
				// this is supposed to be an edge embargo
				if (CREATE_EMBARGO_DEBUG)
					Groove.message("Constructing edge embargo from "
							+ nacNodeSet);
				result = createEdgeEmbargo(lhs, embargoEdge);
			}
		}
		return result;
	}

	/**
	 * Callback method to construct a merge or edge embargo from a given edge.
	 * If the edge has a negated empty regular expression, it gives rise to a
	 * merge embargo, if it has any other regular label it gives rise to an edge
	 * embargo. If neither is the case, the method returns <code>null</code>.
	 * @param graph the context for the embargo, if one is constructed
	 * @param edge the edge from which the embargo is constructed
	 * @return the embargo, or <code>null</code> if <code>edge</code> does
	 * not have a top-level {@link RegExpr.Neg} operator.
	 */
    public NAC constructEmbargo(VarGraph graph, Edge edge) {
        Label edgeLabel = edge.label();
        RegExpr negOperand = RegExprLabel.getNegOperand(edgeLabel);
        if (negOperand == null) {
            // the label is not a negation: no embargo
            return null;
        } else if (negOperand instanceof RegExpr.Empty) {
            return createMergeEmbargo(graph, edge.ends());
        } else {
            // it is an edge embargo. we prefer DefaultLabels.
            Label embargoLabel;
            if (negOperand instanceof RegExpr.Atom) {
                embargoLabel = DefaultLabel.createLabel(((RegExpr.Atom) negOperand).text());
            } else {
                embargoLabel = new RegExprLabel(negOperand);
            }
            return createEdgeEmbargo(graph, createEdge(edge.ends(), embargoLabel));
        }
    }
    
    /**
     * Creates and returns the ordinary graph view corresponding to this rule graph.
     * Delegates the work to {@link #toGraph(NodeEdgeMap)}.
     */
    public Graph toGraph() {
        return toGraph(new NodeEdgeHashMap());
    }

    /**
     * Creates and returns the ordinary graph view corresponding to this rule graph.
     * Also keeps track of the connection between rule graph and resulting graph.
     * @param toGraphMap a mapping from the rule graph elements to the corresponding
     * elements of the result graph
     * @require <code>toGraphMap.isEmpty()</code>
     * @ensure <tt>result != null</tt>
     */
    public Graph toGraph(NodeEdgeMap toGraphMap) {
        Graph result = createGraph();
        // we can simply take all the nodes from this graph
        // but the node role must still be turned into a special edgeSet().
        Iterator<? extends Node> nodeIter = nodeSet().iterator();
        while (nodeIter.hasNext()) {
            RuleNode node = (RuleNode) nodeIter.next();
            result.addNode(node);
            toGraphMap.putNode(node, node);
            // only add a role edge for non-default role
            if (node.role() != DEFAULT_ROLE) {
                String rolePrefix = ROLE_PREFIX[node.role()];
                result.addEdge(node, DefaultLabel.createLabel(rolePrefix), node);
            }
        }
        // now add the edges
        Iterator<? extends Edge> edgeIter = edgeSet().iterator();
        while (edgeIter.hasNext()) {
            RuleEdge edge = (RuleEdge) edgeIter.next();
            // only add a role prefix for non-default role
            Label roleLabel = DefaultLabel.createLabel(edge.textWithRole());
            toGraphMap.putEdge(edge, result.addEdge(edge.ends(), roleLabel));
        }
        result.setFixed();
        return result;
    }

    /** Returns the name of the rule represented by this rule graph, set at construction time. */
    public NameLabel getName() {
        return name;
    }

    /** Returns the priority of the rule represented by this rule graph, set at construction time. */
    public int getPriority() {
        return priority;
    }
    
    /** Invokes {@link #RuleGraph(Rule)} to construct a rule graph. */
    public RuleView newInstance(Rule rule) throws FormatException {
        return new RuleGraph(rule);
    }
//
//    public RuleView newInstance(GraphShape graph, NameLabel name, int priority) throws GraphFormatException {
//    	return new RuleGraph(graph, name, priority, getRuleFactory());
//    }

    /**
     * Factory method to create a label from a string.
     * This implementation returns a merge label (see <tt>{@link #MERGE_LABEL}</tt>)
     * if the string equals <tt>{@link #MERGE_LABEL_TEXT}</tt>;
     * otherwise, it creates a default label if the string looks like a single token,
     * and a regular expression label if the string can be parsed as a regular expression.
     * Any other case, including an empty label, raises an exception.
     */
    protected Label createLabel(String text) throws FormatException {
        if (text.equals(MERGE_LABEL_TEXT)) {
            return MERGE_LABEL;
        } else {
            try {
				RegExpr textAsRegExpr = RegExpr.parse(text);
				if (textAsRegExpr instanceof RegExpr.Atom) {
					// to maintain existing quotes, just take the original text
					return DefaultLabel.createLabel(text);
				} else {
					return new RegExprLabel(textAsRegExpr);
				}
			} catch (FormatException exc) {
                throw new FormatException(exc);
            }
        }
    }

    /**
	 * Factory method for rule nodes.
     * @param role the role of the node to be created
     * @return the fresh rule node
     * @throws FormatException (outdated?)
     * //HARMEN: is this exception still thrown?
	 */
    protected RuleNode createRuleNode(int role) throws FormatException {
        return new RuleNode(role);
    }
    
    /**
     * Factory method for rule edges.
     * @param ends the end-point for the fresh rule-edge
     * @param label the label of the fresh rule-edge
     * @param role the role of the fresh rule-edge
     * @return the fresh rule-edge
     * @throws FormatException (outdated?)
     * //HARMEN: is this exception still thrown?
     */
    protected RuleEdge createRuleEdge(Node[] ends, Label label, int role) throws FormatException {
        return new RuleEdge(ends, label, role);
    }
    
    /**
     * Factory method for rules.
     * This implementation delegates to {@link #getRuleFactory()}.
     * @param ruleMorphism morphism of the new rule to be created
     * @param name name of the new rule to be created
     * @param priority the priority of the new rule.
     * @return the fresh rule created by the factory
     */
    protected Rule createRule(Morphism ruleMorphism, NameLabel name, int priority) throws FormatException {
        return getRuleFactory().createRule(ruleMorphism, name, priority, RuleProperties.DEFAULT_PROPERTIES);
    }

    /**
	 * Factory method for numbered nodes, to be inserted in LHS and RHS.
	 * Used to ensure node number consistency between rule graph and rule.
     * @param nr the node-number for the node
     * @return the fresh node
	 */
    protected DefaultNode createLhsRhsNode(int nr) {
        return new DefaultNode(nr);
    }
    
    /**
     * Callback method to create a graph.
     * @return the fresh graph created by the graph-factory
     * @see #toGraph()
     */
    protected Graph createGraph() {
        return graphFactory.newGraph();
    }

    /**
     * Callback method to create a graph that can serve as LHS or RHS of a rule.
     * @return a fresh instance of {@link groove.rel.RegExprGraph}
     * @see #toGraph()
     */
    protected VarGraph createRegExprGraph() {
        return new RegExprGraph();
    }

    /**
     * Callback method to create an ordinary graph morphism.
     * @see #toRule()
     */
    @Override
    protected Morphism createMorphism(Graph dom, Graph cod) {
        return graphFactory.newMorphism(dom, cod);
    }
    
    /**
     * Callback method to create a merge embargo.
     * @param context the context-graph
     * @param embargoNodes the nodes involved in this merge-embargoe
     * @return the new {@link groove.trans.MergeEmbargo}
     * @see #toRule()
     */
    public MergeEmbargo createMergeEmbargo(VarGraph context, Node[] embargoNodes) {
        return new MergeEmbargo(context, embargoNodes, getRuleFactory());
    }
    
    /**
     * Callback method to create an edge embargo.
     * @param context the context-graph
     * @param embargoEdge the edge to be turned into an embargoe
     * @return the new {@link groove.trans.EdgeEmbargo}
     * @see #toRule()
     */
    public EdgeEmbargo createEdgeEmbargo(VarGraph context, Edge embargoEdge) {
        return new EdgeEmbargo(context, embargoEdge, getRuleFactory());
    }
    
    /**
     * Callback method to create a general NAC on a given {@link VarGraph}.
     * @param context the context-graph
     * @return the new {@link groove.trans.NAC}
     * @see #toRule()
     */
    protected NAC createNAC(VarGraph context) {
        return new DefaultNAC(context, getRuleFactory());
    }
    
    /**
     * Collects the variables from the regular expressions in edges with 
     * a given role. A flag indicates if it is just the bound variables
     * we are interested in.
     */
    protected Set<String> getVars(int role, boolean bound) {
        Set<String> result = new HashSet<String>();
        Iterator<? extends Edge> edgeIter = edgeSet().iterator();
        while (edgeIter.hasNext()) {
            RuleEdge edge = (RuleEdge) edgeIter.next();
            if (edge.role() == role) {
                if (edge.label() instanceof RegExprLabel) {
                    RegExpr expr = ((RegExprLabel) edge.label()).getRegExpr();
                    result.addAll(bound ? expr.boundVarSet() : expr.allVarSet());
                }
            }
        }
        return result;
    }

    /**
     * Creates, adds and returns a rule edge based on the given information.
     * Relies on {@link #createRuleEdge(RuleNode[], Label, int)} for the creation.
     * @param ends the intended edge ends
     * @param label the intended edge label
     * @param role the intended edge role within the rule graph
     * @return the newly created and added edge
     * @throws FormatException if the edge cannot be created
     */
    protected RuleEdge addRuleEdge(Node[] ends, Label label, int role) throws FormatException {
        RuleEdge result = createRuleEdge(ends, label, role);
        addEdge(result);
        return result;
    }
//
//    /**
//     * Returns the rule node corresponding to the given node and the graph
//     * the node is in. If this node is not placed correctly in the graph,
//     * a {@link groove.graph.GraphFormatException} is thrown.
//     * @param role the role of the resulting rule-node
//     * @param node the node for which to get a corresponding rule-node
//     * @param graph the graph the node is in, providing the necessary information
//     * to decide whether this node is placed correctly
//     * @return a corresponding rule-node
//     * @throws GraphFormatException if the node is not placed correctly in the
//     * graph
//     */
//    protected RuleNode addRuleNode(int role, Node node, GraphShape graph) throws GraphFormatException {
//    	return addRuleNode(role);
//    }

    /**
     * Creates, adds and returns a rule node with a given role.
     * @param role the role of the new node
     * @return the newly created and added node
     * @throws FormatException is the node cannot be created.
     */
    protected RuleNode addRuleNode(int role) throws FormatException {
        RuleNode result = createRuleNode(role);
        addNode(result);
        return result;
    }

    /**
     * Callback method that tests if a given label is supported by the current
     * rule graph format.
     * Throws a {@link FormatException} with a specific error message if
     * the label is not supported.
     * @param label the label to be tested
     * @return <code>true</code> if the label is not supported. Always throws an exception
     * in preference to returning <code>false</code>.
     * @throws FormatException if <code>label</code> is not a supported label
     */
    protected boolean isLabelSupported(Label label, int role) throws FormatException {
        if (label instanceof DefaultLabel || RegExprLabel.getWildcardId(label) != null) {
            return true;
        } else if (label instanceof RegExprLabel) {
            if (role == CREATOR) {
                throw new FormatException("Regular expression label "+label+" not allowed on creator edges");                
            } else if (role == ERASER) {
                throw new FormatException("Regular expression label "+label+" not allowed on eraser edges");                
            }
            RegExpr expr = ((RegExprLabel) label).getRegExpr();
            if (expr.containsOperator(RegExpr.NEG_OPERATOR)) {
                throw new FormatException("Negation only allowed on top level for "+label);
            }
            return true;
        } else {
            throw new FormatException("Label of "+label.getClass()+" not supported for "+label);
        }
    }

    /**
     * Checks whether the graph structure local to the given edge conforms the
     * requirements (if any). By default, there are no stuctural requirements
     * on the graph.
     * @param edge the edge for which we check its context for structure requirements
     * @param graph the graph providing that context
     * @return <tt>true</tt> if the graph structure local to the given edge
     * conforms the requirements (if any), <tt>false</tt> otherwise
     * @throws FormatException if the graph structure local to the given
     * edge does not conform the requirements
     */
    protected boolean isGraphStructureCorrect(Edge edge, GraphShape graph) throws FormatException {
    	return true;
    }

    /**
     * Tests if a given morphism is injective; throws a {@link IllegalArgumentException} if it is not.
     * @param morphism the morphisms to be check for injectivity
     * @throws IllegalArgumentException if <code>morphism</code> is not injective
     */
    protected void testInjective(Morphism morphism) {
        if (! morphism.isInjective()) {
            throw new IllegalArgumentException("Morpism "+morphism+" should be injective");
        }
    }
    
    /**
     * Tests if a given graph is connected; throws a {@link IllegalArgumentException} if it is not.
     * @param graph the graph to be tested for connectiveness
     * @throws IllegalArgumentException if <code>graph</code> is not connected
     * @see AbstractGraph#isConnected()
     */
    protected void testConnected(Graph graph) {
        if (! ((AbstractGraph) graph).isConnected()) {
            throw new IllegalArgumentException("Graph "+graph+" should be connected");
        }
    }
    
    /**
     * Convenience method to map an array of nodes to an array of rule nodes,
     * given a mapping from individual nodes to rule nodes.
     * @param map the map in which to look for images
     * @param sources the nodes for which to get the images
     * @return the array containing the images for the given nodes
     */
    protected Node[] images(Map<Node,? extends Node> map, Node[] sources) {
        Node[] result = new Node[sources.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = map.get(sources[i]);
        }
        return result;
    }
//
//    /**
//     * @return the <code>graphToRuleGraphMap</code> field
//     */
//    public NodeEdgeMap getGraphToRuleGraphMap() {
//    	return graphToRuleGraphMap;
//    }

    /**
     * The name of the rule represented by this rule graph.
     */
    protected final NameLabel name;
    /**
     * The priority of the rule represented by this rule graph.
     */
    protected final int priority;
//    
//    /** 
//     * Mapping from the elements of the graph from which this rule graph was constructed to the elements
//     * of the rule graph, if the rule graph was indeed constructed from a graph.
//     * <tt>null</tt> if the rule graph was constructed from a rule.
//     */
//    protected NodeEdgeMap graphToRuleGraphMap = new NodeEdgeHashMap();

    /** The rule derived from this graph, once it is computed. */
    private final Rule rule;
    /** Rule properties (non-<code>null</code>). */
    private final RuleProperties properties;

    /** Debug flag for creating embargoes. */
    static private final boolean CREATE_EMBARGO_DEBUG = false;
    /** Debug flag for the constructor. */
    static private final boolean CONSTRUCTOR_DEBUG = false;
    /** Debug flag for creating rules. */
    static private final boolean TO_RULE_DEBUG = false;
}