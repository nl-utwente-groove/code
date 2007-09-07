// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente
 
// Licensed under the Apache License, Version 2.0 (the "License"); 
// you may not use this file except in compliance with the License. 
// You may obtain a copy of the License at 
// http://www.apache.org/licenses/LICENSE-2.0 
 
// Unless required by applicable law or agreed to in writing, 
// software distributed under the License is distributed on an 
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
// either express or implied. See the License for the specific 
// language governing permissions and limitations under the License.
/*
 * $Id: TypeDiscoverer.java,v 1.13 2007-09-07 19:13:40 rensink Exp $
 */
package groove.util;

import groove.calc.DefaultGraphCalculator;
import groove.calc.GraphCalculator;
import groove.calc.GraphResult;
import groove.graph.DefaultLabel;
import groove.graph.DefaultMorphism;
import groove.graph.DefaultNode;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.GraphFactory;
import groove.graph.Label;
import groove.graph.Morphism;
import groove.graph.Node;
import groove.graph.NodeSetEdgeSetGraph;
import groove.trans.DefaultNAC;
import groove.trans.GraphGrammar;
import groove.trans.Rule;
import groove.trans.RuleNameLabel;
import groove.trans.RuleSystem;
import groove.trans.SPORule;
import groove.trans.SystemProperties;
import groove.view.FormatException;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Algorithm to generate a typ graph from a graph grammar.
 * @author Arend Rensink
 * @version $Revision: 1.13 $ $Date: 2007-09-07 19:13:40 $
 */
public class TypeDiscoverer {
    /**
     * Creates and returns a type graph for a given graph grammar.
     */
    public Graph inferType(GraphGrammar grammar) throws FormatException {
        RuleSystem introduceSystem = new RuleSystem();
        RuleSystem deleteSystem = new RuleSystem();
        RuleSystem mergeSystem = new RuleSystem();
        for (Rule rule: grammar.getRules()) {
            // Integer priority = grammar.getPriority(rule.getName());
            // first do the introduction rule
            // create the rule handle, which is the structure used to 
            // identify the rule in the intermediate stages of constructing the type graph
            Graph ruleHandle = createGraph();
            Node ruleIdNode = ruleHandle.addNode();
            ruleHandle.addEdge(ruleIdNode, DefaultLabel.createLabel(rule.getName().text()), ruleIdNode);
            Edge ruleIdEdge = ruleHandle.addEdge(ruleIdNode, createFreshLabel(), ruleIdNode);
            Map<Node,Node> lhsToHandleNodeMap = new HashMap<Node,Node>();
            Map<Node,Node> rhsToHandleNodeMap = new HashMap<Node,Node>();
            Map<Node,Node> handleToLhsNodeMap = new HashMap<Node,Node>();
            Map<Node,Node> handleToRhsNodeMap = new HashMap<Node,Node>();
            for (Node lhsNode: rule.lhs().nodeSet()) {
                Node lhsNodeImage = ruleHandle.addNode();
                lhsToHandleNodeMap.put(lhsNode, lhsNodeImage);
                handleToLhsNodeMap.put(lhsNodeImage, lhsNode);
                ruleHandle.addEdge(ruleIdNode, createFreshLabel(), lhsNodeImage);
            }
            for (Node creatorNode: getCreatorNodes(rule)) {
                Node creatorImage = ruleHandle.addNode();
                rhsToHandleNodeMap.put(creatorNode, creatorImage);
                handleToRhsNodeMap.put(creatorImage, creatorNode);
                ruleHandle.addEdge(ruleIdNode, createFreshLabel(), creatorImage);
            }
            Morphism introduceMorph = new DefaultMorphism(createGraph(), ruleHandle);
            Rule introduce = createRule(introduceMorph, rule.getName(), introduceSystem);
            introduce.setAndNot(new DefaultNAC(introduceMorph, SystemProperties.getInstance(true)));
            introduceSystem.add(introduce);
            // now the deletion rule
            Graph deleteLhs = createGraph();
            deleteLhs.addNode(ruleIdNode);
            deleteLhs.addEdge(ruleIdEdge);
            Morphism deleteMorph = new DefaultMorphism(deleteLhs, createGraph());
            deleteSystem.add(createRule(deleteMorph, rule.getName(), deleteSystem));
            // now the merging rule
            Graph mergeLhs = rule.lhs().clone();
            Graph mergeRhs = rule.rhs().clone();
            Morphism mergeMorph = new DefaultMorphism(mergeLhs, mergeRhs);
            // process the rule's LHS
            for (Node lhsNode: mergeLhs.nodeSet()) {
                Node rhsNode = rule.getMorphism().getNode(lhsNode);
                // if the node was an eraser, add it to the type RHS
                if (rhsNode == null) {
                    rhsNode = mergeRhs.addNode();
                }
                mergeMorph.putNode(lhsNode, rhsNode); 
            }
            for (Edge lhsEdge: mergeLhs.edgeSet()) {
                Edge rhsEdge = lhsEdge.imageFor(mergeMorph.elementMap());
                // add the edge image to the rhs (necessary for eraser edges)
                mergeRhs.addEdge(rhsEdge);
                mergeMorph.putEdge(lhsEdge, rhsEdge);
            }
            // Now process the rule handle nodes
            for (Node handleNode: ruleHandle.nodeSet()) {
                if (handleNode.equals(ruleIdNode)) {
                    mergeLhs.addNode(handleNode);
                    mergeRhs.addNode(handleNode);
                    mergeMorph.putNode(handleNode, handleNode);
                } else {
                    // get handle node image in RHS
                    Node rhsHandleNodeImage = handleToRhsNodeMap.get(handleNode);
                    if (rhsHandleNodeImage == null) {
                        Node lhsHandleNodeImage = handleToLhsNodeMap.get(handleNode);
                        rhsHandleNodeImage = mergeMorph.getNode(lhsHandleNodeImage);
                    }
                    mergeMorph.putNode(handleNode, rhsHandleNodeImage);
                }
            }
            for (Edge handleEdge: ruleHandle.edgeSet()) {
                Edge handleEdgeImage = handleEdge.imageFor(mergeMorph.elementMap());
                mergeLhs.addEdge(handleEdge);
                mergeRhs.addEdge(handleEdgeImage);
                mergeMorph.putEdge(handleEdge, handleEdgeImage);
            }
            Rule merge = createRule(mergeMorph, rule.getName(), mergeSystem);
            // probably adding the NAC only slows things down
            // merge.addNAC(new DefaultNAC(mergeMorph));
            mergeSystem.add(merge);
        }
//        try {
//            Groove.saveRuleSystem(introduceSystem, grammar.getName()+"-I");
//            Groove.saveRuleSystem(deleteSystem, grammar.getName()+"-D");
//            Groove.saveRuleSystem(mergeSystem, grammar.getName()+"-M");
//        } catch (IOException exc) {
//            exc.printStackTrace();
//        }
        GraphResult deleted;
		Graph typeStartGraph = new NodeSetEdgeSetGraph(grammar.getStartGraph());
		GraphGrammar newGrammar = new GraphGrammar(introduceSystem, typeStartGraph);
		newGrammar.setFixed();
		GraphCalculator calculator = new DefaultGraphCalculator(newGrammar);
		GraphResult introduced = calculator.getMax();
		GraphResult merged = introduced.getMax(mergeSystem);
		deleted = merged.getMax(deleteSystem);
		return deleted.getGraph();
	}

    /**
     * Factory method for a graph.
     */
    protected Graph createGraph() {
        return GraphFactory.getInstance().newGraph();
    }
//
//    /**
//     * Factory method for a {@link VarGraph} copy of a given graph.
//     */
//    protected Graph createVarGraph(Graph graph) {
//        return graph.newGraph();
//    }
    
    /**
     * Factory method for a fresh label.
     */
    protected Label createFreshLabel() {
        return DefaultLabel.createFreshLabel();
    }
//    
//    /**
//     * Factory method to create a graph to be used as the LHS or RHS of an {@link SPORule}.
//     */
//    protected Graph createGraph() {
//        return new RegExprGraph();
//    }
    
    /** Callback factory method to create a rule. */
    protected Rule createRule(Morphism ruleMorphism, RuleNameLabel name, RuleSystem ruleSystem) throws FormatException {
        return new SPORule(ruleMorphism, name, Rule.DEFAULT_PRIORITY, ruleSystem.getProperties());
    }
    
    /** Callback method to retrieve the creator nodes from a rule. */
    protected Set<Node> getCreatorNodes(Rule rule) {
        Set<Node> result = new HashSet<Node>(rule.rhs().nodeSet());
        result.removeAll(rule.getMorphism().elementMap().nodeMap().keySet());
        return result;
    }
    
    /**
     * Applies the type discoverer to a given graph grammar.
     */
    public static void main(String[] args) {
        TypeDiscoverer discoverer = getInstance();
        GraphGrammar grammar;
        try {
            switch (args.length) {
            case 1 :
                grammar = Groove.loadGrammar(getGrammarDirname(args)).toGrammar();
                break;
            case 2 :
            case 3 :
                grammar = Groove.loadGrammar(getGrammarDirname(args), getStartFilename(args)).toGrammar();
                break;
            default : printUsage();
            return;
            }
        } catch (FormatException exc) {
            System.err.println("Error loading graph grammar: "+exc.getMessage());
            return;
        } catch (IOException exc) {
            System.err.println("Error loading graph grammar: "+exc.getMessage());
            return;
        }
        try {
            Graph type = discoverer.inferType(grammar);
            String resultFilename = getTypeFilename(args);
            Groove.saveGraph(type, resultFilename);
        } catch (FormatException exc) {
            System.err.println("Error in rule format: "+exc.getMessage());
            return;
        } catch (IOException exc) {
            System.err.println("Error saving type graph: "+exc.getMessage());
            return;
        }
    }

    /**
     * Retrieves the grammar directory name from main's arguments.
     */
    private static String getGrammarDirname(String[] args) {
        return args[0];
    }
    
    /**
     * Retrieves the start state file name from main's arguments, under the assumeption
     * that the name is actually given as a parameter.
     */
    private static String getStartFilename(String[] args) {
        return args[1];
    }
    
    /**
     * Retrieves the resulting type file name from main's arguments.
     * Either the name is explicitly given, or it equals the grammar name minus extension.
     */
    private static String getTypeFilename(String[] args) {
        if (args.length == 3) {
            return args[2];
        } else {
            String ruleSystemName = Groove.createRuleSystemFilter().stripExtension(getGrammarDirname(args));
            return ruleSystemName+TYPE_EXTENSION;
        }
    }

    /**
     * Returns the unique instance of this (singleton) class.
     */
    public static TypeDiscoverer getInstance() {
        return instance;
    }
    
    /**
     * Prints a usage message to the standard error output.
     */
    private static void printUsage() {
        System.err.println("Usage: TypeDiscoverer <grammar> [<start state>] [<type graph>]");
    }
    /** Extension of files containing type information. */
    public static final String TYPE_EXTENSION = ".type";
    
    /**
     * Static variable holding the unique instance of this class.
     */
    private static final TypeDiscoverer instance = new TypeDiscoverer();
}