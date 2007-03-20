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
 * $Id: AspectRuleView.java,v 1.3 2007-03-20 23:04:46 rensink Exp $
 */

package groove.trans.view;

import static groove.graph.aspect.RuleAspect.*;

import groove.graph.AbstractGraph;
import groove.graph.DefaultEdge;
import groove.graph.DefaultLabel;
import groove.graph.DefaultNode;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.GraphFactory;
import groove.graph.GraphFormatException;
import groove.graph.GraphShape;
import groove.graph.Label;
import groove.graph.Morphism;
import groove.graph.Node;
import groove.graph.NodeEdgeHashMap;
import groove.graph.NodeEdgeMap;
import groove.graph.aspect.AspectEdge;
import groove.graph.aspect.AspectGraph;
import groove.graph.aspect.AspectNode;
import groove.graph.aspect.AspectValue;
import groove.graph.aspect.RuleAspect;
import groove.graph.iso.DefaultIsoChecker;
import groove.graph.iso.IsoChecker;
import groove.rel.RegExpr;
import groove.rel.RegExprGraph;
import groove.rel.RegExprLabel;
import groove.rel.VarGraph;
import groove.trans.DefaultNAC;
import groove.trans.DefaultRuleFactory;
import groove.trans.EdgeEmbargo;
import groove.trans.GraphCondition;
import groove.trans.MergeEmbargo;
import groove.trans.NAC;
import groove.trans.NameLabel;
import groove.trans.Rule;
import groove.trans.RuleFactory;
import groove.util.Groove;
import groove.util.Pair;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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
 * @version $Revision: 1.3 $
 */
public class AspectRuleView implements RuleView {
//    /** Label text for merges (merger edges and merge embargoes) */
//    static public final String MERGE_LABEL_TEXT = Groove.getXMLProperty("label.merge");
	/** Regular expression for merges and injections. */
	static private final RegExpr mergeExpr = RegExpr.empty();
	/** Label for merges (merger edges and merge embargoes) */
    static public final Label MERGE_LABEL = new RegExprLabel(mergeExpr);
    /** Label for injection constraints */
    static public final Label NEGATIVE_MERGE_LABEL = new RegExprLabel(mergeExpr.neg());

    /** Isomorphism checker (used for testing purposes). */
    static private final IsoChecker isoChecker = new DefaultIsoChecker();
    /** Graph factory used for building a graph view of this rule graph.*/
    static protected GraphFactory graphFactory = GraphFactory.newInstance();

    /**
     * This main is provided for testing purposes only.
     * @param args names of XML files to be used as test input
     */
    static public void main(String[] args) {
        System.out.printf("Test of %s%n", AspectRuleView.class);
        System.out.println("=================");
        for (int i = 0; i < args.length; i++) {
        	try {
        		testFile(new File(args[i]));
            } catch (Exception exc) {
                exc.printStackTrace();
            }
        }
    }

	/**
	 * Loads a graph from a file and tests its conversion from aspect graph to rule
	 * and back, using {@link #testTranslation(String,AspectGraph)}. 
	 * Recursively descends into directories.
	 */
	private static void testFile(File file) throws GraphFormatException, ViewFormatException {
        AspectGraph factory = AspectGraph.getFactory();
		if (file.isDirectory()) {
			for (File nestedFile: file.listFiles()) {
				testFile(nestedFile);
			}
		} else try {
			Graph plainGraph = Groove.loadGraph(file);
			if (plainGraph != null) {
				System.out.printf("Testing %s%n", file);
				testTranslation(file.getName(), factory.fromPlainGraph(plainGraph));
				System.out.println(" - OK");
			}
		} catch (IOException exc) {
			// do nothing (skip)
		}
	}
	
	/** Tests the translation from an aspect graph to a rule and back. */
	private static void testTranslation(String name, AspectGraph graph) throws ViewFormatException, GraphFormatException {
        NameLabel ruleName = new NameLabel(name);
        // construct rule graph
        AspectRuleView ruleGraph = new AspectRuleView(graph, ruleName);
        // convert rule graph into rule
        System.out.print("    Constructing rule from rule graph: ");
        Rule rule = ruleGraph.toRule();
        System.out.println("OK");
        // convert rule back into rule graph and test for isomorphism
        System.out.print("    Reconstructing rule graph from rule: ");
        AspectRuleView newRuleGraph = new AspectRuleView(rule);
        System.out.println("OK");
        System.out.print("    Testing for isomorphism of original and reconstructed rule graph: ");
        if (isoChecker.areIsomorphic(newRuleGraph.toGraph(),ruleGraph.toGraph()))
            System.out.println("OK");
        else {
            System.out.println("ERROR");
            System.out.println("Resulting rule:");
            System.out.println("--------------");
            System.out.println(rule);
            System.out.println("Original rule graph");
            System.out.println("-----------------");
            System.out.println(ruleGraph.toGraph());
            System.out.println("Reconstructed rule graph");
            System.out.println("------------------------");
            System.out.println(newRuleGraph.toGraph());
        }
    }
    
    /**
     * Constructs a new rule graph on the basis of a given production rule.
     * @param rule the production rule for which a rule graph is to be constructed
     * @require <tt>rule != null</tt>
     * @throws ViewFormatException if <code>rule</code> cannot be displayed as a {@link AspectRuleView},
     * for instance because its NACs are nested too deep or not connected
     */
    public AspectRuleView(Rule rule) throws ViewFormatException {
    	this.name = rule.getName();
        this.priority = rule.getPriority();
        this.rule = rule;
        this.graph = computeGraph(rule);
    }

    /**
     * Constructs a rule graph with a given name from an (ordinary) graph.
     * Empty labels (after the role prefix) are interpreted as merge labels.
     * Priority is set to the default rule priority, and the rule factory
     * is not set.
     * @param graph the graph to be converted
     * @param name the name of the rule
     * @require <tt>graph != null</tt>
     * @throws GraphFormatException if <tt>graph</tt> does not have
     * the required meta-format
     */
    public AspectRuleView(AspectGraph graph, NameLabel name) throws GraphFormatException {
        this(graph, name, Rule.DEFAULT_PRIORITY, null);
    }

    /**
     * Constructs a rule graph with a given name from an (ordinary) graph.
     * Empty labels (after the role prefix) are interpreted as merge labels.
     * @param graph the graph to be converted
     * @param name the name of the rule
     * @param priority the priority ot the rule
     * @require <tt>graph != null</tt>
     * @throws GraphFormatException if <tt>graph</tt> does not have
     * the required meta-format
     */
    public AspectRuleView(AspectGraph graph, NameLabel name, int priority, RuleFactory ruleFactory) throws GraphFormatException {
        this.name = name;
        this.priority = priority;
        this.ruleFactory = ruleFactory;
        this.graph = graph;
        this.rule = computeRule(graph);
    }
    
    /**
     * Checks if the variables bound by the left hand side of an aspect graph
     * cover all variables used in the right hand side and the NACs.
     * @param graph the graph to be checked
     * @throws GraphFormatException if there is a free variable in the rhs or NAC
     */
    protected void testVariableBinding(AspectGraph graph) throws GraphFormatException {
        Set<String> boundVars = getVars(graph, READER, true);
        boundVars.addAll(getVars(graph, ERASER, true));
        Set<String> rhsOnlyVars = getVars(graph, CREATOR, false);
        if (!boundVars.containsAll(rhsOnlyVars)) {
            rhsOnlyVars.removeAll(boundVars);
            throw new GraphFormatException("Right hand side variables %s not bound on left hand side", rhsOnlyVars);
        }
        Set<String> embargoVars = getVars(graph, EMBARGO, false);
        if (!boundVars.containsAll(embargoVars)) {
        	embargoVars.removeAll(boundVars);
            throw new GraphFormatException("NAC variables %s not bound on left hand side", embargoVars);
        }
    }
    
    /**
	 * Collects the variables from the regular expressions in edges with 
	 * a given role from a given graph. A flag indicates if it is just the bound variables
	 * we are interested in.
	 * @param graph the graph to be checked
	 * @param role the role to look for
	 * @param bound if <code>true</code>, collect bound variables only
	 * @return the requested set of variables
	 */
	protected Set<String> getVars(AspectGraph graph, AspectValue role, boolean bound) {
	    Set<String> result = new HashSet<String>();
	    Iterator<? extends Edge> edgeIter = graph.edgeSet().iterator();
	    while (edgeIter.hasNext()) {
	        AspectEdge edge = (AspectEdge) edgeIter.next();
	        if (edge.getValue(RuleAspect.getInstance()) == role) {
	            if (edge.label() instanceof RegExprLabel) {
	                RegExpr expr = ((RegExprLabel) edge.label()).getRegExpr();
	                result.addAll(bound ? expr.boundVarSet() : expr.allVarSet());
	            }
	        }
	    }
	    return result;
	}

	/**
     * Returns the rule factory.
     * @return the rule factory.
     */
    public RuleFactory getRuleFactory() {
    	if (ruleFactory == null) {
    		ruleFactory = DefaultRuleFactory.getInstance();
    	}
    	return ruleFactory;
    }

    /** Returns the name of the rule represented by this rule graph, set at construction time. */
	public NameLabel getName() {
	    return name;
	}

	/** Returns the priority of the rule represented by this rule graph, set at construction time. */
	public int getPriority() {
	    return priority;
	}

	/** Invokes {@link #AspectRuleView(Rule)} to construct a rule graph. */
	public RuleView newInstance(Rule rule) throws ViewFormatException {
	    return new AspectRuleView(rule);
	}

	/**
     * Creates and returns the production rule corresponding to this rule graph.
     * @ensure <tt>result != null</tt>
     */
    public Rule toRule() {
    	return rule;
    }
    
    /**
     * Callback method to compute a rule from an aspect graph.
     * @param graph the aspect graph to compute the rule from
     */
    protected Rule computeRule(AspectGraph graph) throws GraphFormatException {
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
        for (AspectNode node: graph.nodeSet()) {
            if (RuleAspect.inLHS(node)) {
            	Node lhsNodeImage = computeNodeImage(node);
            	left.addNode(lhsNodeImage);
                toLeft.putNode(node, lhsNodeImage);
                lhs.addNode(lhsNodeImage);
            }
            if (RuleAspect.inRHS(node)) {
            	Node rhsNodeImage = computeNodeImage(node);
            	rhs.addNode(rhsNodeImage);
                toRight.putNode(node, rhsNodeImage);
                if (RuleAspect.inLHS(node)) {
                    ruleMorph.putNode(toLeft.getNode(node), rhsNodeImage);
                }
            } else if (RuleAspect.inNAC(node)) {
            	Node nodeImage = computeNodeImage(node);
            	left.addNode(nodeImage);
                toLeft.putNode(node, nodeImage);
                nacNodeSet.add(nodeImage);
            }
        }
        Set<GraphCondition> embargoes = new HashSet<GraphCondition>();
        // now add edges to lhs, rhs and morphism
        for (AspectEdge edge: graph.edgeSet()) {
        	boolean isEmbargo = false;
            if (RuleAspect.inLHS(edge)) {
                Edge lhsEdgeImage = computeEdgeImage(edge, toLeft);
            	assert lhsEdgeImage != null : String.format("End nodes of %s do not have image in %s", edge, toLeft);
                NAC embargo = computeEmbargoFromNegation(lhs, lhsEdgeImage);
                isEmbargo = embargo != null;
                if (isEmbargo) {
                	embargoes.add(embargo);
                } else {
                	left.addEdge(lhsEdgeImage);
                	lhs.addEdge(lhsEdgeImage);
                	toLeft.putEdge(edge, lhsEdgeImage);
                }
            }
            if (!isEmbargo && RuleAspect.inRHS(edge)) {
                if (RuleAspect.isCreator(edge) && RegExprLabel.isEmpty(edge.label())) {
                    List<Node> endImages = images(toRight.nodeMap(), edge.ends());
                    // it's a merger; it's bound to be binary
                    assert endImages.size() == 2 : "Merger edge "+edge+" should be binary";
                    Node mergeFrom = endImages.get(Edge.SOURCE_INDEX); 
                    Node mergeTo = endImages.get(Edge.TARGET_INDEX); 
                    // existing edges will automatically be redirected
                    rhs.mergeNodes(mergeFrom, mergeTo);
                    // make that sure edges to be added later also get the right end nodes
                    toRight.putNode(edge.source(), mergeTo);
                } else {
                    Edge rhsEdgeImage = computeEdgeImage(edge, toRight);
                    assert rhsEdgeImage != null : String.format("Image of edge %s under map %s should not be null", edge, toRight);
                    rhs.addEdge(rhsEdgeImage);
                    toRight.putEdge(edge, rhsEdgeImage);
                    if (RuleAspect.inLHS(edge)) {
                        ruleMorph.putEdge(toLeft.getEdge(edge), rhsEdgeImage);
                    }
                }
            }
            if (RuleAspect.inNAC(edge)) {
            	Edge lhsEdgeImage = computeEdgeImage(edge, toLeft);
            	left.addEdge(lhsEdgeImage);
                nacEdgeSet.add(lhsEdgeImage);
            }
        }
        // the resulting rule
        Rule result = getRuleFactory().createRule(ruleMorph, name, priority);
        // add the nacs to the rule
        for (Pair<Set<Node>,Set<Edge>> nacPair: AbstractGraph.getConnectedSets(nacNodeSet, nacEdgeSet)) {
            result.setAndNot(computeNac(result.lhs(), nacPair.first(), nacPair.second()));
        }
        // add the embargoes
        for (GraphCondition embargo: embargoes) {
            result.setAndNot(embargo);
        }
        testVariableBinding(graph);
        result.setFixed();
        if (TO_RULE_DEBUG) {
            System.out.println("Constructed rule: "+result);
        }
        return result;
    }

    /**
	 * Creates an image for a given aspect node.
	 * Node numbers are copied.
	 * @param node the node to be copied
	 * @return the fresh node
	 */
	protected Node computeNodeImage(AspectNode node) {
	    return new DefaultNode(node.getNumber());
	}

	/**
     * Creates a an edge by copying a given edge under a given node mapping.
     * Returns <code>null</code> if the node map fails to have an image for one of the edge ends.
     * @param edge the edge for which an image is to be created
     * @param elementMap the mapping of the end nodes
     * @return the newly added edge, if any
     */
    protected Edge computeEdgeImage(AspectEdge edge, NodeEdgeMap elementMap) {
    	Node[] ends = new Node[edge.endCount()];
    	for (int i = 0; i < ends.length; i++) {
    		Node endImage = elementMap.getNode(edge.end(i));
    		if (endImage == null) {
    			return null;
    		} else {
    			ends[i] = endImage;
    		}
    	}
    	return createEdge(ends, edge.label());
    }

    /**
     * Constructs a negative application condition based on a LHS graph
     * and a set of graph elements that should make up the NAC target.
     * The connection between LHS and NAC target is given by identity, i.e., those 
     * elements in the NAC set that are in the LHS graph are indeed LHS elements.
     * @param lhs the LHS graph
     * @param nacNodeSet set of graph elements that should be turned into a NAC target
     */
    protected NAC computeNac(VarGraph lhs, Set<Node> nacNodeSet, Set<Edge> nacEdgeSet) {
    	NAC result = null;
        // first check for merge end edge embargoes
        // they are characterised by the fact that there is precisely 1 element
        // in the nacElemSet, which is an edge
		if (nacNodeSet.size() == 0 && nacEdgeSet.size() == 1) {
			Edge embargoEdge = nacEdgeSet.iterator().next();
			if (RegExprLabel.isEmpty(embargoEdge.label())) {
				// this is supposed to be a merge embargo
				result = createMergeEmbargo(lhs, embargoEdge.ends());
			} else {
				// this is supposed to be an edge embargo
				result = createEdgeEmbargo(lhs, embargoEdge);
			}
		} else {
			// if we're here it means we couldn't make an embargo
			result = new DefaultNAC(lhs, getRuleFactory());
			VarGraph nacTarget = result.getTarget();
			Morphism nacMorphism = result.getPattern();
			// add all nodes to nacTarget
			nacTarget.addNodeSet(nacNodeSet);
			// add edges and embargoes to nacTarget
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
				NAC subEmbargo = computeEmbargoFromNegation(nacTarget, edge);
				if (subEmbargo == null) {
					nacTarget.addEdge(edge);
				} else {
					result.setAndNot(subEmbargo);
				}
			}
		}
        return result;
    }

	/**
     * Callback method to construct a merge or edge embargo from a given edge,
     * in case the edge label is a negated regular expression. If the inner
     * regular expression is an {@link RegExpr.Empty}, the method yields a 
     * merge embargo, for any other it yields an edge embargo. If the label is
     * not a negation, the method returns <code>null</code>.
	 * @param graph the context for the embargo, if one is constructed
	 * @param edge the edge from which the embargo is constructed
	 * @return the embargo, or <code>null</code> if <code>edge</code> does
	 * not have a top-level {@link RegExpr.Neg} operator.
     */
    protected NAC computeEmbargoFromNegation(VarGraph graph, Edge edge) {
        RegExpr negOperand = RegExprLabel.getNegOperand(edge.label());
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
	 * Callback method to create a general NAC on a given {@link VarGraph}.
	 * @param context the context-graph
	 * @return the new {@link groove.trans.NAC}
	 * @see #toRule()
	 */
	protected NAC createNAC(VarGraph context) {
	    return new DefaultNAC(context, getRuleFactory());
	}

	/**
	 * Callback method to create a merge embargo.
	 * @param context the context-graph
	 * @param embargoNodes the nodes involved in this merge-embargoe
	 * @return the new {@link groove.trans.MergeEmbargo}
	 * @see #toRule()
	 */
	protected MergeEmbargo createMergeEmbargo(VarGraph context, Node[] embargoNodes) {
	    return new MergeEmbargo(context, embargoNodes, getRuleFactory());
	}

	/**
	 * Callback method to create an edge embargo.
	 * @param context the context-graph
	 * @param embargoEdge the edge to be turned into an embargoe
	 * @return the new {@link groove.trans.EdgeEmbargo}
	 * @see #toRule()
	 */
	protected EdgeEmbargo createEdgeEmbargo(VarGraph context, Edge embargoEdge) {
	    return new EdgeEmbargo(context, embargoEdge, getRuleFactory());
	}

	/**
	 * Factory method for rules.
	 * This implementation delegates to {@link #getRuleFactory()}.
	 * @param ruleMorphism morphism of the new rule to be created
	 * @param name name of the new rule to be created
	 * @param priority the priority of the new rule.
	 * @return the fresh rule created by the factory
	 */
	protected Rule createRule(Morphism ruleMorphism, NameLabel name, int priority) {
	    return getRuleFactory().createRule(ruleMorphism, name, priority);
	}

	/**
	 * Callback method to create an ordinary graph morphism.
	 * @see #toRule()
	 */
	protected Morphism createMorphism(Graph dom, Graph cod) {
	    return graphFactory.newMorphism(dom, cod);
	}
	
	/**
     * Callback factory method for a binary edge.
     * @param ends the end nodes for the new edge; should contain exactly two element
     * @param label the label for the new edge
     * @return a DefaultEdge with the given end nodes and label
     */
    protected Edge createEdge(Node[] ends, Label label) {
    	assert ends.length == 2 : String.format("Cannot create edge with end nodes %s", Arrays.toString(ends));
    	return DefaultEdge.createEdge(ends[DefaultEdge.SOURCE_INDEX], label, ends[DefaultEdge.TARGET_INDEX]);
    }

    /**
     * Returns the aspect graph representation of this rule view.
     */
    public AspectGraph toGraph() {
    	return graph;
    }

    /**
     * Computes an aspect graph representation of the rule
     * stored in this rule view.
     */
    protected AspectGraph computeGraph(Rule rule) throws ViewFormatException {
    	AspectGraph result = createGraph();
		// start with lhs
		Map<Node, AspectNode> lhsNodeMap = new HashMap<Node, AspectNode>();
		// add lhs nodes
		for (Node lhsNode : rule.lhs().nodeSet()) {
			AspectValue nodeRole = rule.getMorphism().containsKey(lhsNode) ? READER
					: ERASER;
			AspectNode nodeImage = createAspectNode(result, nodeRole);
			result.addNode(nodeImage);
			lhsNodeMap.put(lhsNode, nodeImage);
		}
		// add lhs edges
		for (Edge lhsEdge : rule.lhs().edgeSet()) {
			AspectValue edgeRole = rule.getMorphism().containsKey(lhsEdge) ? READER
					: ERASER;
			AspectEdge edgeImage = createAspectEdge(images(lhsNodeMap,
					lhsEdge.ends()), lhsEdge.label(), edgeRole);
			result.addEdge(edgeImage);
		}
		// now add the rhs
		Map<Node, AspectNode> rhsNodeMap = new HashMap<Node, AspectNode>();
		// add rhs nodes and mergers to rule graph
		// first find out which rhs nodes correspond to readers
		for (Node lhsNode : rule.lhs().nodeSet()) {
			Node rhsNode = rule.getMorphism().getNode(lhsNode);
			if (rhsNode != null) {
				// we have a rhs reader node
				// check if we had it before (in which case we have a merger)
				if (rhsNodeMap.containsKey(rhsNode)) {
					// yes, it's a merger
					List<AspectNode> ends = Arrays.asList(new AspectNode[] {
							lhsNodeMap.get(lhsNode), rhsNodeMap.get(rhsNode) });
					result.addEdge(createAspectEdge(ends, MERGE_LABEL, CREATOR));
				} else {
					// no, it's a "fresh" reader node
					rhsNodeMap.put(rhsNode, lhsNodeMap.get(lhsNode));
				}
			}
		}
		// the rhs nodes not yet dealt with must be creators
		// iterate over the rhs nodes
		for (Node rhsNode : rule.rhs().nodeSet()) {
			if (!rhsNodeMap.containsKey(rhsNode)) {
				AspectNode nodeImage = createAspectNode(result, CREATOR);
				result.addNode(nodeImage);
				rhsNodeMap.put(rhsNode, nodeImage);
			}
		}
		// add rhs edges
		for (Edge rhsEdge : rule.rhs().edgeSet()) {
			if (!rule.getMorphism().containsValue(rhsEdge)) {
				List<AspectNode> endImages = images(rhsNodeMap, rhsEdge.ends());
				result.addEdge(createAspectEdge(endImages,
						rhsEdge.label(),
						CREATOR));
			}
		}
		// now add the NACs
		for (GraphCondition nac : rule.getNegConjunct().getConditions()) {
			Morphism nacMorphism = nac.getPattern();
			if (nac instanceof MergeEmbargo) {
				result.addEdge(createAspectEdge(images(lhsNodeMap,
						((MergeEmbargo) nac).getNodes()), MERGE_LABEL, EMBARGO));
				// result.addEdge(createInjectionEdge((MergeEmbargo) nac,
				// lhsNodeMap, READER));
				// } else if (nac instanceof EdgeEmbargo) {
				// result.addEdge(createAspectEdge(images(lhsNodeMap((EdgeEmbargo)
				// nac, lhsNodeMap, READER));
				// // result.addEdge(createAspectEdge(images(lhsNodeMap,
				// ((MergeEmbargo) nac).getNodes()), MERGE_LABEL, EMBARGO));
			} else {
				// NOTE: we're assuming the NAC is injective and connected,
				// otherwise no rule graph can be given
				testInjective(nacMorphism);
				// also store the nac into a graph, to test for connectedness
				AspectGraph nacGraph = createGraph();
				// store the mapping from the NAC target nodes to the rule graph
				Map<Node, AspectNode> nacNodeMap = new HashMap<Node, AspectNode>();
				// first register the lhs nodes
				for (Node key : nacMorphism.dom().nodeSet()) {
					Node nacNode = nacMorphism.getNode(key);
					if (nacNode != null) {
						AspectNode nacNodeImage = lhsNodeMap.get(key);
						nacNodeMap.put(nacNode, nacNodeImage);
						nacGraph.addNode(nacNodeImage);
					}
				}
				// add this nac's nodes
				for (Node nacNode : nacMorphism.cod().nodeSet()) {
					if (!nacNodeMap.containsKey(nacNode)) {
						AspectNode nacNodeImage = createAspectNode(result, EMBARGO);
						nacNodeMap.put(nacNode, nacNodeImage);
						result.addNode(nacNodeImage);
						nacGraph.addNode(nacNodeImage);
					}
				}
				Set<Edge> nacEdgeSet = new HashSet<Edge>(
						nacMorphism.cod().edgeSet());
				nacEdgeSet.removeAll(nacMorphism.elementMap().edgeMap().values());
				// add this nac's edges
				for (Edge nacEdge : nacEdgeSet) {
					List<AspectNode> endImages = images(nacNodeMap, nacEdge.ends());
					AspectEdge nacEdgeImage = createAspectEdge(endImages,
							nacEdge.label(),
							EMBARGO);
					result.addEdge(nacEdgeImage);
					nacGraph.addEdge(nacEdgeImage);
				}
				for (GraphCondition subNac : nac.getNegConjunct().getConditions()) {
					AspectEdge subNacEdge;
					if (subNac instanceof MergeEmbargo) {
						subNacEdge = createInjectionEdge((MergeEmbargo) subNac,
								nacNodeMap,
								EMBARGO);
					} else if (subNac instanceof EdgeEmbargo) {
						subNacEdge = createNegationEdge((EdgeEmbargo) subNac,
								nacNodeMap,
								EMBARGO);
					} else {
						throw new ViewFormatException(
								"Level 2 NACs must be merge or edge embargoes");
					}
					result.addEdge(subNacEdge);
					nacGraph.addEdge(subNacEdge);
				}
				testConnected(nacGraph);
			}
		}
		result.setFixed();
        return result;
    }

    /** Callback factory method to create an empty aspect graph. */
    protected AspectGraph createGraph() {
    	return new AspectGraph();
    }

    /**
	 * Factory method for rule nodes.
	 * 
	 * @param role
	 *            the role of the node to be created
	 * @return the fresh rule node
	 */
    protected AspectNode createAspectNode(AspectGraph graph, AspectValue role) {
    	AspectNode result = graph.createNode();
		if (role != null) {
			try {
				result.setDeclaredValue(role);
			} catch (GraphFormatException exc) {
				assert false : String.format("Fresh node %s cannot have two values for ",
						result,
						RuleAspect.class);
			}
		}
		return result;
    }
    
    /**
	 * Factory method for rule edges.
	 * 
	 * @param ends
	 *            the end-point for the fresh rule-edge
	 * @param label
	 *            the label of the fresh rule-edge
	 * @param role
	 *            the role of the fresh rule-edge
	 * @return the fresh rule-edge
	 */
    protected AspectEdge createAspectEdge(List<AspectNode> ends, Label label, AspectValue role) {
    	try {
    		return new AspectEdge(ends, label, role);
    	} catch (GraphFormatException exc) {
    		assert false : String.format("Fresh edge cannot have two values for ", RuleAspect.class);
    		return null;
    	}
    }
    
    /**
	 * Creates an injection edge based on a given merge embargo.
	 * The embargo is interpreted under a certain node mapping.
	 * A role parameter controls whether it is a level 1 injection (READER) or level 2 (EMBARGO)
	 */
	private AspectEdge createInjectionEdge(MergeEmbargo embargo, Map<Node,AspectNode> nodeMap, AspectValue role) {
	    return createAspectEdge(images(nodeMap, embargo.getNodes()), NEGATIVE_MERGE_LABEL, role);
	}

	/**
	 * Creates a negation edge based on a given edge embargo.
	 * The embargo is interpreted under a certain node mapping.
	 * A role parameter controls whether it is a level 1 negation (READER) or level 2 (EMBARGO)
	 */
	private AspectEdge createNegationEdge(EdgeEmbargo embargo, Map<Node,AspectNode> nodeMap, AspectValue role) {
	    Edge embargoEdge = embargo.getEmbargoEdge();
	    Label label = embargoEdge.label();
	    // we have to add a negation to the label, which may mean we first have
	    // to turn it into a regular expression
	    RegExpr labelExpr = label instanceof RegExprLabel ? ((RegExprLabel) label).getRegExpr() : RegExpr.atom(label.text());
	    List<AspectNode> endImages = images(nodeMap, embargoEdge.ends());
	    return createAspectEdge(endImages, new RegExprLabel(labelExpr.neg()), role);
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
     * Checks whether the graph structure local to the given edge conforms the
     * requirements (if any). By default, there are no stuctural requirements
     * on the graph.
     * @param edge the edge for which we check its context for structure requirements
     * @param graph the graph providing that context
     * @return <tt>true</tt> if the graph structure local to the given edge
     * conforms the requirements (if any), <tt>false</tt> otherwise
     * @throws GraphFormatException if the graph structure local to the given
     * edge does not conform the requirements
     */
    protected boolean isGraphStructureCorrect(Edge edge, GraphShape graph) throws GraphFormatException {
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
     * @return the array containing the images for the given nodes,
     * or <code>null</code> if one of the nodes does not have an image in <code>map</code>
     */
    protected <N extends Node> List<N> images(Map<Node,N> map, Node[] sources) {
    	List<N> result = new ArrayList<N>();
        for (int i = 0; i < sources.length; i++) {
        	result.add(map.get(sources[i]));
        }
        return result;
    }

    /**
     * The name of the rule represented by this rule graph.
     */
    protected final NameLabel name;
    /**
     * The priority of the rule represented by this rule graph.
     */
    protected final int priority;
    
    /** The aspect graph representation of the rule. */
    private final AspectGraph graph;
    /** The rule derived from this graph, once it is computed. */
    private final Rule rule;
    /** Rule factory set for this rule. */
    private RuleFactory ruleFactory;

    /** Debug flag for creating rules. */
    static private final boolean TO_RULE_DEBUG = false;
}