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
 * $Id: AspectualRuleView.java,v 1.7 2007-05-14 18:52:03 rensink Exp $
 */

package groove.view;

import static groove.view.aspect.RuleAspect.CREATOR;
import static groove.view.aspect.RuleAspect.EMBARGO;
import static groove.view.aspect.RuleAspect.ERASER;
import static groove.view.aspect.RuleAspect.READER;
import groove.graph.AbstractGraph;
import groove.graph.DefaultEdge;
import groove.graph.DefaultNode;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.GraphFactory;
import groove.graph.GraphProperties;
import groove.graph.Label;
import groove.graph.Morphism;
import groove.graph.Node;
import groove.graph.NodeEdgeHashMap;
import groove.graph.NodeEdgeMap;
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
import groove.trans.Rule;
import groove.trans.RuleNameLabel;
import groove.trans.SPORule;
import groove.trans.SystemProperties;
import groove.util.Groove;
import groove.util.Pair;
import groove.view.aspect.AspectEdge;
import groove.view.aspect.AspectGraph;
import groove.view.aspect.AspectNode;
import groove.view.aspect.AspectValue;
import groove.view.aspect.AttributeAspect;
import groove.view.aspect.RuleAspect;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Provides a graph view upon a production rule.
 * The nodes and edges are divided into embargoes, erasers, readers and creators, 
 * with the following intuition: <ul>
 * <li> Maximal connected embargo subgraphs correspond to negative application conditions.
 * <li> Erasers correspond to LHS elements that are not RHS.
 * <li> Readers (the default) are elements that are both LHS and RHS.
 * <li> Creators are RHS elements that are not LHS.</ul>
 * @author Arend Rensink
 * @version $Revision: 1.7 $
 */
public class AspectualRuleView extends AspectualView<Rule> implements RuleView {
	/** Label for merges (merger edges and merge embargoes) */
    static public final Label MERGE_LABEL = RegExpr.empty().toLabel();
//    /** Label for injection constraints */
//    static public final Label NEGATIVE_MERGE_LABEL = RegExpr.empty().neg().toLabel();

    /** Isomorphism checker (used for testing purposes). */
    static private final IsoChecker isoChecker = new DefaultIsoChecker();
    /** Graph factory used for building a graph view of this rule graph.*/
    static protected GraphFactory graphFactory = GraphFactory.getInstance();

    /**
     * This main is provided for testing purposes only.
     * @param args names of XML files to be used as test input
     */
    static public void main(String[] args) {
        System.out.printf("Test of %s%n", AspectualRuleView.class);
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
	private static void testFile(File file) {
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
		} catch (FormatException exc) {
			// do nothing (skip)
		} catch (IOException exc) {
			// do nothing (skip)
		}
	}
	
	/** Tests the translation from an aspect graph to a rule and back. */
	private static void testTranslation(String name, AspectGraph graph) throws FormatException, FormatException {
        RuleNameLabel ruleName = new RuleNameLabel(name);
        // construct rule graph
        AspectualRuleView ruleGraph = new AspectualRuleView(graph, ruleName);
        // convert rule graph into rule
        System.out.print("    Constructing rule from rule graph: ");
        Rule rule = ruleGraph.toRule();
        System.out.println("OK");
        // convert rule back into rule graph and test for isomorphism
        System.out.print("    Reconstructing rule graph from rule: ");
        AspectualRuleView newRuleGraph = new AspectualRuleView(rule);
        System.out.println("OK");
        System.out.print("    Testing for isomorphism of original and reconstructed rule graph: ");
        if (isoChecker.areIsomorphic(newRuleGraph.getAspectGraph(),ruleGraph.getAspectGraph())) {
            System.out.println("OK");
        } else {
            System.out.println("ERROR");
            System.out.println("Resulting rule:");
            System.out.println("--------------");
            System.out.println(rule);
            System.out.println("Original rule graph");
            System.out.println("-----------------");
            System.out.println(ruleGraph.getAspectGraph());
            System.out.println("Reconstructed rule graph");
            System.out.println("------------------------");
            System.out.println(newRuleGraph.getAspectGraph());
        }
    }
    
    /**
     * Constructs a new rule graph on the basis of a given production rule.
     * @param rule the production rule for which a rule graph is to be constructed
     * @require <tt>rule != null</tt>
     * @throws FormatException if <code>rule</code> cannot be displayed as a {@link AspectualRuleView},
     * for instance because its NACs are nested too deep or not connected
     */
    public AspectualRuleView(Rule rule) throws FormatException {
    	this.name = rule.getName();
        this.priority = rule.getPriority();
        this.enabled = true;
        this.rule = rule;
        this.properties = rule.getProperties();
        this.viewToRuleMap = new NodeEdgeHashMap();
        this.graph = computeAspectGraph(rule, viewToRuleMap);
    }

    /**
     * Constructs a rule graph with a given name from an (ordinary) graph.
     * Empty labels (after the role prefix) are interpreted as merge labels.
     * Priority is set to the default rule priority, and the rule factory
     * is not set.
     * @param graph the graph to be converted
     * @param name the name of the rule
     * @require <tt>graph != null</tt>
     */
    public AspectualRuleView(AspectGraph graph, RuleNameLabel name) {
        this(graph, name, null);
    }

    /**
     * Constructs a rule graph with a given name from an (ordinary) graph.
     * Empty labels (after the role prefix) are interpreted as merge labels.
     * @param graph the graph to be converted
     * @param name the name of the rule
     * @require <tt>graph != null</tt>
     */
    public AspectualRuleView(AspectGraph graph, RuleNameLabel name, SystemProperties properties) {
        this.name = name;
        this.priority = GraphProperties.getPriority(graph);
        this.enabled = GraphProperties.isEnabled(graph);
        this.properties = properties;
        this.graph = graph;
        this.viewToRuleMap = new NodeEdgeHashMap();
    }
    
    /**
     * Checks if the variables bound by the left hand side of an aspect graph
     * cover all variables used in the right hand side and the NACs.
     * @param graph the graph to be checked
     * @throws FormatException if there is a free variable in the rhs or NAC
     */
    protected void testVariableBinding(AspectGraph graph) throws FormatException {
        Set<String> boundVars = getVars(graph, READER, true);
        boundVars.addAll(getVars(graph, ERASER, true));
        Set<String> rhsOnlyVars = getVars(graph, CREATOR, false);
        if (!boundVars.containsAll(rhsOnlyVars)) {
            rhsOnlyVars.removeAll(boundVars);
            throw new FormatException("Right hand side variables %s not bound on left hand side", rhsOnlyVars);
        }
        Set<String> embargoVars = getVars(graph, EMBARGO, false);
        if (!boundVars.containsAll(embargoVars)) {
        	embargoVars.removeAll(boundVars);
            throw new FormatException("NAC variables %s not bound on left hand side", embargoVars);
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
	
    /** Returns the name of the rule represented by this rule graph, set at construction time. */
	public RuleNameLabel getNameLabel() {
	    return name;
	}

	/** Convenience method for <code>getNameLabel().name()</code>. */
	public String getName() {
		return getNameLabel().name();
	}

	/** Returns the priority of the rule represented by this rule graph, set at construction time. */
	public int getPriority() {
	    return priority;
	}
	
	public boolean isEnabled() {
		return enabled;
	}

	public int compareTo(RuleView o) {
		int result = getPriority() - o.getPriority();
		if (result == 0) {
			result = getNameLabel().compareTo(o.getNameLabel());
		}
		return result;
	}

	/** Invokes {@link #AspectualRuleView(Rule)} to construct a rule graph. */
	public RuleView newInstance(Rule rule) throws FormatException {
	    return new AspectualRuleView(rule);
	}

	/**
     * Creates and returns the production rule corresponding to this rule graph.
     */
    public Rule toModel() throws FormatException {
    	return toRule();
    }

	/**
     * Creates and returns the production rule corresponding to this rule graph.
     */
    public Rule toRule() throws FormatException {
    	if (rule == null) {
            rule = computeRule(graph, viewToRuleMap);
    	}
    	return rule;
    }
    
	public List<String> getErrors() {
		if (errors == null) {
			try {
				toRule();
				errors = Collections.emptyList();
			} catch (FormatException exc) {
				errors = exc.getErrors();
			}
		}
		return errors;
	}

	@Override
	public AspectGraph getAspectGraph() {
		return graph;
	}
	
	@Override
	public NodeEdgeMap getMap() {
		return viewToRuleMap;
	}

	/**
     * Callback method to compute a rule from an aspect graph.
     * @param graph the aspect graph to compute the rule from
     */
    protected Rule computeRule(AspectGraph graph, NodeEdgeMap viewToRuleMap) throws FormatException {
    	Set<String> errors = new TreeSet<String>(graph.getErrors());
        if (TO_RULE_DEBUG) {
            System.out.println("");
        }
        // create the new lhs
        VarGraph lhs = createVarGraph();
        // also create a graph for all left elements, i.e., LHS and NAC
        VarGraph left = createVarGraph();
        // create the new rhs
        VarGraph rhs = createVarGraph();
        // mapping from aspect nodes to RHS nodes
        Map<AspectNode,Node> toRight = new HashMap<AspectNode,Node>();
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
        	try {
        	if (RuleAspect.inRule(node)) {
				Node nodeImage = computeNodeImage(node, graph);
				viewToRuleMap.putNode(node, nodeImage);
				if (RuleAspect.inLHS(node)) {
					left.addNode(nodeImage);
					lhs.addNode(nodeImage);
				}
				if (RuleAspect.inRHS(node)) {
					rhs.addNode(nodeImage);
					toRight.put(node, nodeImage);
					if (RuleAspect.inLHS(node)) {
						ruleMorph.putNode(nodeImage, nodeImage);
					}
				} else if (RuleAspect.inNAC(node)) {
					left.addNode(nodeImage);
					nacNodeSet.add(nodeImage);
				}
			}
        	} catch (FormatException exc) {
        		errors.addAll(exc.getErrors());
        	}
        }
        // add merger edges
        for (AspectEdge edge: graph.edgeSet()) {
        	if (RuleAspect.isCreator(edge) && RegExprLabel.isEmpty(edge.label())) {
                // it's a merger; it's bound to be binary
                assert edge.endCount() == 2 : "Merger edge "+edge+" should be binary";
                // existing edges will automatically be redirected
                rhs.mergeNodes(toRight.get(edge.source()), toRight.get(edge.opposite()));
                // make that sure edges to be added later also get the correct end nodes
                toRight.put(edge.source(), toRight.get(edge.opposite()));
            }
        }
        // now add edges to lhs, rhs and morphism
        Set<GraphCondition> embargoes = new HashSet<GraphCondition>();
        for (AspectEdge edge: graph.edgeSet()) {
        	try {
        	if (RuleAspect.inRule(edge)) {
				Edge edgeImage = computeEdgeImage(edge, graph, viewToRuleMap.nodeMap());
				viewToRuleMap.putEdge(edge, edgeImage);
				if (RuleAspect.inLHS(edge)) {
					left.addEdge(edgeImage);
					lhs.addEdge(edgeImage);
				}
				if (RuleAspect.inRHS(edge)
						&& !(RuleAspect.isCreator(edge) && RegExprLabel.isEmpty(edge.label()))) {
					// use the toRight map because we may have merged nodes
					Edge rhsEdgeImage = computeEdgeImage(edge, graph, toRight);
					rhs.addEdge(rhsEdgeImage);
					if (RuleAspect.inLHS(edge)) {
						ruleMorph.putEdge(edgeImage, rhsEdgeImage);
					}
				}
				if (RuleAspect.inNAC(edge)) {
					left.addEdge(edgeImage);
					nacEdgeSet.add(edgeImage);
				}
			}
        	} catch (FormatException exc) {
        		errors.addAll(exc.getErrors());
        	}
        }
        try {
			// the resulting rule
			Rule result = createRule(ruleMorph, name, priority);
			// add the nacs to the rule
			for (Pair<Set<Node>, Set<Edge>> nacPair : AbstractGraph.getConnectedSets(nacNodeSet,
					nacEdgeSet)) {
				result.setAndNot(computeNac(result.lhs(), nacPair.first(), nacPair.second()));
			}
			// add the embargoes
			for (GraphCondition embargo : embargoes) {
				result.setAndNot(embargo);
			}
			testVariableBinding(graph);
			result.setFixed();
			if (TO_RULE_DEBUG) {
				System.out.println("Constructed rule: " + result);
			}
			if (errors.isEmpty()) {
				return result;
			}
		} catch (FormatException e) {
			errors.addAll(e.getErrors());
		}
		throw new FormatException(new ArrayList<String>(errors));
    }

    /**
	 * Creates an image for a given aspect node.
	 * Node numbers are copied.
	 * @param node the node to be copied
     * @param context the graph in which the original node occurs;
     * may be necessary to determine the type of the image.
	 * @return the fresh node
     * @throws FormatException if <code>node</code> does not
     * occur in a correct way in <code>context</code>
	 */
	protected Node computeNodeImage(AspectNode node, AspectGraph context) throws FormatException {
		if (node.getValue(AttributeAspect.getInstance()) == null) {
			return new DefaultNode(node.getNumber());
		} else {
			return AttributeAspect.createAttributeNode(node, context);
		}
	}

	/**
     * Creates a an edge by copying a given edge under a given node mapping.
     * The mapping is assumed to have images for all end nodes.
     * @param edge the edge for which an image is to be created
	 * @param context the graph in which the original edge occurs;
	 * may be necessary to determine the type of the image.
	 * @param elementMap the mapping of the end nodes
     * @return the newly added edge, if any
	 * @throws FormatException if <code>edge</code> does not
     * occur in a correct way in <code>context</code>
     */
    protected Edge computeEdgeImage(AspectEdge edge, AspectGraph context, Map<? extends Node, Node> elementMap) throws FormatException {
    	Node[] ends = new Node[edge.endCount()];
    	for (int i = 0; i < ends.length; i++) {
    		Node endImage = elementMap.get(edge.end(i));
    		if (endImage == null) {
        		throw new FormatException("Cannot compute image of '%s'-edge: %s node does not have image", edge.label(), i == Edge.SOURCE_INDEX ? "source": "target");
    		}
    		ends[i] = endImage;
    	}
    	if (edge.getValue(AttributeAspect.getInstance()) == null) {
    		return createEdge(ends, edge.label());
    	} else {
    		return AttributeAspect.createAttributeEdge(edge, context, ends);
    	}
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
			result = createNAC(lhs);
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
//				NAC subEmbargo = computeEmbargoFromNegation(nacTarget, edge);
//				if (subEmbargo == null) {
					nacTarget.addEdge(edge);
//				} else {
//					result.setAndNot(subEmbargo);
//				}
			}
		}
        return result;
    }
//
//	/**
//     * Callback method to construct a merge or edge embargo from a given edge,
//     * in case the edge label is a negated regular expression. If the inner
//     * regular expression is an {@link RegExpr.Empty}, the method yields a 
//     * merge embargo, for any other it yields an edge embargo. If the label is
//     * not a negation, the method returns <code>null</code>.
//	 * @param graph the context for the embargo, if one is constructed
//	 * @param edge the edge from which the embargo is constructed
//	 * @return the embargo, or <code>null</code> if <code>edge</code> does
//	 * not have a top-level {@link RegExpr.Neg} operator.
//     */
//    protected NAC computeEmbargoFromNegation(VarGraph graph, Edge edge) {
//        RegExpr negOperand = RegExprLabel.getNegOperand(edge.label());
//        if (negOperand == null) {
//            // the label is not a negation: no embargo
//            return null;
//        } else if (negOperand instanceof RegExpr.Empty) {
//            return createMergeEmbargo(graph, edge.ends());
//        } else {
//            // it is an edge embargo. we prefer DefaultLabels.
//            Label embargoLabel;
//            if (negOperand instanceof RegExpr.Atom) {
//                embargoLabel = DefaultLabel.createLabel(((RegExpr.Atom) negOperand).text());
//            } else {
//                embargoLabel = new RegExprLabel(negOperand);
//            }
//            return createEdgeEmbargo(graph, createEdge(edge.ends(), embargoLabel));
//        }
//    }
    
    /**
	 * Callback method to create a merge embargo.
	 * @param context the context-graph
	 * @param embargoNodes the nodes involved in this merge-embargoe
	 * @return the new {@link groove.trans.MergeEmbargo}
	 * @see #toRule()
	 */
	protected MergeEmbargo createMergeEmbargo(VarGraph context, Node[] embargoNodes) {
	    return new MergeEmbargo(context, embargoNodes, properties);
	}

	/**
	 * Callback method to create an edge embargo.
	 * @param context the context-graph
	 * @param embargoEdge the edge to be turned into an embargoe
	 * @return the new {@link groove.trans.EdgeEmbargo}
	 * @see #toRule()
	 */
	protected EdgeEmbargo createEdgeEmbargo(VarGraph context, Edge embargoEdge) {
	    return new EdgeEmbargo(context, embargoEdge, properties);
	}

	/**
	 * Callback method to create a general NAC on a given {@link VarGraph}.
	 * @param context the context-graph
	 * @return the new {@link groove.trans.NAC}
	 * @see #toRule()
	 */
	protected NAC createNAC(VarGraph context) {
	    return new DefaultNAC(context, properties);
	}

	/**
	 * Factory method for rules.
	 * @param ruleMorphism morphism of the new rule to be created
	 * @param name name of the new rule to be created
	 * @param priority the priority of the new rule.
	 * @return the fresh rule created by the factory
	 */
	protected Rule createRule(Morphism ruleMorphism, RuleNameLabel name, int priority) throws FormatException {
	    return new SPORule(ruleMorphism, name, priority, properties);
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
    	Node source = ends[Edge.SOURCE_INDEX];
    	Node target = ends[Edge.TARGET_INDEX];
    	return DefaultEdge.createEdge(source, label, target);
    }

    /**
	 * Callback method to create a graph that can serve as LHS or RHS of a rule.
	 * @return a fresh instance of {@link groove.rel.RegExprGraph}
	 * @see #getAspectGraph()
	 */
	protected VarGraph createVarGraph() {
	    return new RegExprGraph();
	}

	/**
     * Computes an aspect graph representation of the rule
     * stored in this rule view.
     */
    protected AspectGraph computeAspectGraph(Rule rule, NodeEdgeMap viewToRuleMap) throws FormatException {
    	AspectGraph result = createAspectGraph();
		// start with lhs
		Map<Node, AspectNode> lhsNodeMap = new HashMap<Node, AspectNode>();
		// add lhs nodes
		for (Node lhsNode : rule.lhs().nodeSet()) {
			AspectValue nodeRole = rule.getMorphism().containsKey(lhsNode) ? READER
					: ERASER;
			AspectNode nodeImage = computeAspectNode(result, nodeRole, lhsNode);
			result.addNode(nodeImage);
			lhsNodeMap.put(lhsNode, nodeImage);
			viewToRuleMap.putNode(nodeImage, lhsNode);
		}
		// add lhs edges
		for (Edge lhsEdge : rule.lhs().edgeSet()) {
			AspectValue edgeRole = rule.getMorphism().containsKey(lhsEdge) ? READER
					: ERASER;
			AspectEdge edgeImage = computeAspectEdge(images(lhsNodeMap,
					lhsEdge.ends()), lhsEdge.label(), edgeRole, lhsEdge);
			result.addEdge(edgeImage);
			viewToRuleMap.putEdge(edgeImage, lhsEdge);
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
					result.addEdge(computeAspectEdge(ends, MERGE_LABEL, CREATOR, null));
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
				AspectNode nodeImage = computeAspectNode(result, CREATOR, rhsNode);
				result.addNode(nodeImage);
				rhsNodeMap.put(rhsNode, nodeImage);
				viewToRuleMap.putNode(nodeImage, rhsNode);
			}
		}
		// add rhs edges
		for (Edge rhsEdge : rule.rhs().edgeSet()) {
			if (!rule.getMorphism().containsValue(rhsEdge)) {
				List<AspectNode> endImages = images(rhsNodeMap, rhsEdge.ends());
				Edge edgeImage = (computeAspectEdge(endImages,
						rhsEdge.label(),
						CREATOR, rhsEdge));
				result.addEdge(edgeImage);
				viewToRuleMap.putEdge(edgeImage, rhsEdge);
			}
		}
		// now add the NACs
		for (GraphCondition nac : rule.getNegConjunct().getConditions()) {
			Morphism nacMorphism = nac.getPattern();
			if (nac instanceof MergeEmbargo) {
				result.addEdge(computeAspectEdge(images(lhsNodeMap,
						((MergeEmbargo) nac).getNodes()), MERGE_LABEL, EMBARGO, null));
			} else {
				// NOTE: we're assuming the NAC is injective and connected,
				// otherwise no rule graph can be given
				testInjective(nacMorphism);
				// also store the nac into a graph, to test for connectedness
				AspectGraph nacGraph = createAspectGraph();
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
						AspectNode nacNodeImage = computeAspectNode(result, EMBARGO, nacNode);
						nacNodeMap.put(nacNode, nacNodeImage);
						viewToRuleMap.putNode(nacNodeImage, nacNode);
						result.addNode(nacNodeImage);
						nacGraph.addNode(nacNodeImage);
					}
				}
				Set<Edge> nacEdgeSet = new HashSet<Edge>(nacMorphism.cod().edgeSet());
				nacEdgeSet.removeAll(nacMorphism.elementMap().edgeMap().values());
				// add this nac's edges
				for (Edge nacEdge : nacEdgeSet) {
					List<AspectNode> endImages = images(nacNodeMap, nacEdge.ends());
					AspectEdge nacEdgeImage = computeAspectEdge(endImages,
							nacEdge.label(),
							EMBARGO, nacEdge);
					result.addEdge(nacEdgeImage);
					viewToRuleMap.putEdge(nacEdgeImage, nacEdge);
					nacGraph.addEdge(nacEdgeImage);
				}
				testConnected(nacGraph);
			}
		}
		result.setFixed();
        return result;
    }
    
    /** Callback factory method to create an empty aspect graph. */
    protected AspectGraph createAspectGraph() {
    	return new AspectGraph();
    }

    /**
	 * Factory method for aspect nodes.
	 * @param graph the graph in which the node is to be inserted
     * @param role
	 *            the role of the node to be created
     * @param original the node for which we want a copy; used to 
     * determine the attribute aspect value of the resulting node
	 * 
	 * @return the fresh rule node
	 */
    protected AspectNode computeAspectNode(AspectGraph graph, AspectValue role, Node original) {
    	AspectNode result = graph.createNode();
		if (role != null) {
			try {
				result.setDeclaredValue(role);
			} catch (FormatException exc) {
				assert false : String.format("Fresh node %s cannot have two rule aspect values",
						result);
			}
		}
		AspectValue attributeValue = AttributeAspect.getAttributeValue(original);
		if (attributeValue != null) {
			try {
				result.setDeclaredValue(attributeValue);
			} catch (FormatException exc) {
				assert false : String.format("Fresh node %s cannot have two attribute aspect values",
						result);
			}
		}
		return result;
    }
    
    /**
	 * Factory method for aspect edges.
	 * @param ends
	 *            the end-point for the fresh rule-edge
     * @param label
	 *            the label of the fresh rule-edge
     * @param role
	 *            the role of the fresh rule-edge
     * @param edge original edge for which the newly created aspect edge is an image. 
     * Used to determine the attribute aspect value of the result; may be <code>null</code>
	 * @return the fresh rule-edge
	 */
    protected AspectEdge computeAspectEdge(List<AspectNode> ends, Label label, AspectValue role, Edge edge) {
    	AspectValue attributeValue = edge == null ? null : AttributeAspect.getAttributeValue(edge); 
    	try {
    		if (attributeValue == null) {
    			return new AspectEdge(ends, label, role);
    		} else {
        		return new AspectEdge(ends, label, role, attributeValue);
    		}
    	} catch (FormatException exc) {
    		assert false : String.format("Fresh '%s'-edge cannot have two values for the same aspect", label);
    		return null;
    	}
    }
//    
//    /**
//	 * Creates an injection edge based on a given merge embargo.
//	 * The embargo is interpreted under a certain node mapping.
//	 * A role parameter controls whether it is a level 1 injection (READER) or level 2 (EMBARGO)
//	 */
//	private AspectEdge createInjectionEdge(MergeEmbargo embargo, Map<Node,AspectNode> nodeMap, AspectValue role) {
//	    return computeAspectEdge(images(nodeMap, embargo.getNodes()), NEGATIVE_MERGE_LABEL, role, null);
//	}
//
//	/**
//	 * Creates a negation edge based on a given edge embargo.
//	 * The embargo is interpreted under a certain node mapping.
//	 * A role parameter controls whether it is a level 1 negation (READER) or level 2 (EMBARGO)
//	 */
//	private AspectEdge createNegationEdge(EdgeEmbargo embargo, Map<Node,AspectNode> nodeMap, AspectValue role) {
//	    Edge embargoEdge = embargo.getEmbargoEdge();
//	    Label label = embargoEdge.label();
//	    // we have to add a negation to the label, which may mean we first have
//	    // to turn it into a regular expression
//	    RegExpr labelExpr = label instanceof RegExprLabel ? ((RegExprLabel) label).getRegExpr() : RegExpr.atom(label.text());
//	    List<AspectNode> endImages = images(nodeMap, embargoEdge.ends());
//	    return computeAspectEdge(endImages, labelExpr.neg().toLabel(), role, null);
//	}

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
    protected final RuleNameLabel name;
    /**
     * The priority of the rule represented by this rule graph.
     */
    protected final int priority;

    /**
     * The enabledness of the rule view.
     */
    protected final boolean enabled;
    
    /** The aspect graph representation of the rule. */
    private final AspectGraph graph;
    /** Errors found while converting the view to a rule. */
    private List<String> errors;
    /** The rule derived from this graph, once it is computed. */
    private Rule rule;
    /** 
     * Mapping from the elements of the aspect graph representation
     * to the corresponding elements of the rule.
     */
    private final NodeEdgeMap viewToRuleMap;
    /** Rule factory set for this rule. */
    private final SystemProperties properties;

    /** Debug flag for creating rules. */
    static private final boolean TO_RULE_DEBUG = false;
}