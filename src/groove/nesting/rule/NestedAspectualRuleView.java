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
 * $Id: NestedAspectualRuleView.java,v 1.1 2007-08-22 09:19:48 kastenberg Exp $
 */
package groove.nesting.rule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import groove.graph.DefaultMorphism;
import groove.graph.Edge;
import groove.graph.GraphInfo;
import groove.graph.Morphism;
import groove.graph.Node;
import groove.graph.NodeEdgeHashMap;
import groove.graph.NodeEdgeMap;
import groove.gui.Editor;
import groove.nesting.NestingAspect;
import groove.nesting.NestingAspectValue;
import groove.nesting.VarNodeEdgeMultiHashMap;
import groove.nesting.VarNodeEdgeMultiMap;
import groove.rel.RegExprLabel;
import groove.rel.VarGraph;
import groove.trans.DefaultGraphCondition;
import groove.trans.DefaultGraphPredicate;
import groove.trans.DefaultNAC;
import groove.trans.GraphCondition;
import groove.trans.GraphPredicate;
import groove.trans.NAC;
import groove.trans.NameLabel;
import groove.trans.Rule;
import groove.trans.RuleNameLabel;
import groove.trans.SPORule;
import groove.trans.SystemProperties;
import groove.util.Counter;
import groove.util.Pair;
import groove.view.AspectualRuleView;
import groove.view.FormatException;
import groove.view.aspect.AspectEdge;
import groove.view.aspect.AspectElement;
import groove.view.aspect.AspectGraph;
import groove.view.aspect.AspectNode;
import groove.view.aspect.AspectValue;
import groove.view.aspect.RuleAspect;

/**
 *
 * @author kramor
 * @version 0.1 $Revision: 1.1 $ $Date: 2007-08-22 09:19:48 $
 */
public class NestedAspectualRuleView extends AspectualRuleView {

	/**
	 * Constructs a new NestedAspectualRuleView from a give Rule
	 * @param rule
	 * @throws FormatException
	 */
	public NestedAspectualRuleView(Rule rule) throws FormatException {
		super(rule);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Constructs a new NestedAspectualRuleView from a rulegraph
	 * @param graph
	 * @param name
	 * @throws FormatException
	 */
	public NestedAspectualRuleView(AspectGraph graph, RuleNameLabel name) {
		super(graph, name);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Constructs a new NestedAspectualRuleView
	 * @param graph
	 * @param name
	 * @param properties
	 */
	public NestedAspectualRuleView(AspectGraph graph, RuleNameLabel name,
			SystemProperties properties) {
		super(graph, name, properties);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see groove.view.AspectualRuleView#computeRule(groove.view.aspect.AspectGraph, java.util.Map)
	 */
	@Override
	protected Pair<Rule, NodeEdgeMap> computeRule(AspectGraph graph) throws FormatException {
		NodeEdgeMap graphToRuleMap = new NodeEdgeHashMap();
		Set<String> errors = new TreeSet<String>(graph.getErrors());
		// create the ruletree
		Set<AspectNode> metaNodes = new java.util.HashSet<AspectNode> (); //new java.util.TreeSet<AspectNode> (NestingAspect.comparator);
		Set<AspectNode> topLevelNodes = new java.util.HashSet<AspectNode> ();
		Map<String, Set<AspectEdge>> namedItemsMap = new HashMap<String, Set<AspectEdge>> ();
		
		// Sort meta nodes by level of appearance
		for( AspectNode an : graph.nodeSet() ) {
			if( NestingAspect.isMetaElement(an) ) {
				metaNodes.add(an);
			} else if( isPureEdgeSet(graph.outEdgeSet(an)) ) {
				// The node has no outgoing level:: edge and is therefor an implicit toplevel node
				topLevelNodes.add(an);
			}
		}
		//System.out.println("topLevelNodes: " + topLevelNodes);
		
		// find edges that may refer to named levels
		for( AspectEdge edge : graph.edgeSet() ) {
			NestingAspectValue value = (NestingAspectValue) edge.getValue(NestingAspect.getInstance());
			if( value != null && !NestingAspect.isMetaElement(edge) ) {
				String levelName = value.getContent();
				if( ! namedItemsMap.containsKey(levelName) ) {
					namedItemsMap.put(levelName, new HashSet<AspectEdge> ());
				}
				namedItemsMap.get(levelName).add(edge);
			}
		}
		
		SortedSet<AspectNode> levels = calculateLevels(graph, metaNodes);
		
		if( levels.isEmpty() ) {
			// Ordinary rule
			Pair<Rule, NodeEdgeMap> tmpRule = super.computeRule(graph);
			NestedRule newRule = new NestedRule((SPORule)tmpRule.first(), properties);
			//Editor.previewRule(newRule, this.name.name());
			return new Pair<Rule, NodeEdgeMap> (newRule, tmpRule.second());
		}
		// so far, so good
				
		Map<String, NestedRule> nestedRules = new HashMap<String, NestedRule> ();
		Map<String, Pair<UniversalLevel, Pair<Set<AspectNode>, Set<AspectEdge>>>> universalLevels = new HashMap<String, Pair<UniversalLevel, Pair<Set<AspectNode>, Set<AspectEdge>>>> ();
		Map<String, Map<AspectNode, Node>> toRights = new HashMap<String, Map<AspectNode, Node>> ();
		Map<String, NAC> nacLevels = new HashMap<String, NAC> ();
		// Back patching is required if an element on a universal level is removed or added
		Map<AspectElement, String> backPatches = new HashMap<AspectElement, String> ();
		Set<AspectElement> nacPatches = new HashSet<AspectElement> ();
		// Create implicit topnode

		{
			String level = "1";
			String name = null;
			// Create local lhs
			VarGraph lhs = createVarGraph();
			// Create local rhs
			VarGraph rhs = createVarGraph();
			// Create local rule morphism
			Morphism ruleMorph = DefaultMorphism.prototype.createMorphism(lhs, rhs);
		
	        // mapping from aspect nodes to RHS nodes
	        Map<AspectNode,Node> toRight = new HashMap<AspectNode,Node>();
	        toRights.put(level, toRight);
			for( AspectNode node : topLevelNodes ) {
				if( RuleAspect.inRule(node) ) {
					Node nodeImage = computeNodeImage(node, graph);
					graphToRuleMap.putNode(node, nodeImage);
					if( RuleAspect.inLHS(node) ) {
						lhs.addNode(nodeImage);
					}
					if( RuleAspect.inRHS(node) ) {
						rhs.addNode(nodeImage);
						toRight.put(node, nodeImage);
						if( RuleAspect.inLHS(node) ) {
							ruleMorph.putNode(nodeImage, nodeImage);
						}
					}
					if( RuleAspect.inNAC(node) ) {
						nacPatches.add(node);
					}
				}
			}
			
			// Add merger edges
			for( AspectNode node : topLevelNodes ) {
				for( Edge edgeit : graph.edgeSet(node) ) {
					AspectEdge edge = (AspectEdge)edgeit;
					if( ! NestingAspect.isMetaElement(edge) ) {
						if( RuleAspect.isCreator(edge) && RegExprLabel.isEmpty(edge.label()) && isEdgeOfThisLevel(graph, edge, level, name) ) {
							assert edge.endCount() == 2 : "Merger edge "+edge+ " should be binary";
							rhs.mergeNodes(toRight.get(edge.source()), toRight.get(edge.opposite()));
							toRight.put(edge.source(), toRight.get(edge.opposite()));
						}
					}
				}
			}
			
			// add edges to lhs, rhs and morphism
			//Set<GraphCondition> embargoes = new HashSet<GraphCondition> ();
			for( AspectNode node : topLevelNodes ) {
				for( AspectEdge edge : graph.edgeSet(node)) {
					try {
						if( RuleAspect.inRule(edge) && isEdgeOfThisLevel(graph, edge, level, name) ) {
							Edge edgeImage = computeEdgeImage(edge, graph, graphToRuleMap.nodeMap());
							graphToRuleMap.putEdge(edge, edgeImage);
							if( RuleAspect.inLHS(edge) ) {
								lhs.addEdge(edgeImage);
							}
							if( RuleAspect.inRHS(edge)
									&& !(RuleAspect.isCreator(edge) && RegExprLabel.isEmpty(edge.label())) ) {
								Edge rhsEdgeImage = computeEdgeImage(edge, graph, toRight);
								
								rhs.addEdge(rhsEdgeImage);
								if( RuleAspect.inLHS(edge) ) {
									ruleMorph.putEdge(edgeImage, rhsEdgeImage);
								}
							}
							if( RuleAspect.inNAC(edge) ) {
								nacPatches.add(edge);
							}
						}
					} catch( FormatException exc ) {
						errors.addAll(exc.getErrors());
					}
				}
			}
			
			// Try to add named edges
			if( namedItemsMap.containsKey(name) ) {
				Set<AspectEdge> moreEdges = namedItemsMap.remove(name);
				for( AspectEdge edge : moreEdges ) {
					Edge edgeImage = computeEdgeImage(edge, graph, graphToRuleMap.nodeMap());
					graphToRuleMap.putEdge(edge, edgeImage);
					if( RuleAspect.inLHS(edge) ) {
						lhs.addEdge(edgeImage);
					}
					if( RuleAspect.inRHS(edge)
							&& !(RuleAspect.isCreator(edge) && RegExprLabel.isEmpty(edge.label())) ) {
						Edge rhsEdgeImage = computeEdgeImage(edge, graph, toRight);
						
						rhs.addEdge(rhsEdgeImage);
						if( RuleAspect.inLHS(edge) ) {
							ruleMorph.putEdge(edgeImage, rhsEdgeImage);
						}
					}
					if( RuleAspect.inNAC(edge) ) {
						nacPatches.add(edge);
					}
				}
			}

			// Create the local rule
			String ruleName = (level.indexOf('.') > 0) ?
					this.name.name()+'.'+level.replaceAll("\\.", "-")
					: this.name.name();
			NestedRule localRule = new NestedRule(level, ruleMorph, new RuleNameLabel(ruleName), priority, properties);
			
			if( !nacPatches.isEmpty() ) {
				NAC newCondition = new DefaultNAC(lhs, properties);
				VarGraph nac = newCondition.getTarget();
				for( AspectElement elem : nacPatches ) {
					if( elem instanceof AspectNode ) {
						Node nodeImage = computeNodeImage((AspectNode)elem, graph);
						graphToRuleMap.putNode((Node)elem, nodeImage);
						nac.addNode(nodeImage);
					}
				}
				for( AspectElement elem : nacPatches ) {
					if( elem instanceof AspectEdge ) {
						Edge edgeImage = computeEdgeImage((AspectEdge)elem, graph, graphToRuleMap.nodeMap());
						nac.addEdge(edgeImage);
					}
				}
				localRule.setAndNot(newCondition);
				nacPatches.clear();
			}
						
			if( localRule == null ) System.out.println("Hrm, null as Existential");
			//Editor.previewRule(localRule, "1");
			nestedRules.put(level, localRule);
		}

		
		
		// Create tree
		for( AspectNode an : levels ) {
			String level = NestingAspect.getLevel(an);
			String name = NestingAspect.getLevelName(an);
			if( NestingAspect.isExistentialLevel(level) && an.getValue(NestingAspect.getInstance()).equals(NestingAspect.EXISTS) ) {
				//System.out.println("Existential level, making SPORule");
				// Get the level of the above universal level (if any)
				String parentLevel = NestingAspect.getParentLevel(level);
				// Get the level of the rule above the universal level (if any)
				String parentRuleLevel = NestingAspect.getParentLevel(parentLevel);
				UniversalLevel parentCondition = parentLevel == null ?
						null
						: universalLevels.get(parentLevel).first();
				Pair<Set<AspectNode>, Set<AspectEdge>> newElements = parentLevel == null ?
						null
						: universalLevels.get(parentLevel).second();
				NestedRule parentRule = parentRuleLevel == null ?
						null
						: nestedRules.get(parentRuleLevel);
				
				// Create local lhs
				VarGraph lhs = parentRule != null ? 
						(VarGraph) parentCondition.cod().clone() 
						: createVarGraph();
				// Create local rhs
				VarGraph rhs = parentRule != null ?
						(VarGraph) parentRule.rhs().clone()
						: createVarGraph();
				// Create local rule morphism
				Morphism ruleMorph = DefaultMorphism.prototype.createMorphism(lhs, rhs);
				
				// Create local predicate morphism
				Morphism predicateMorph = parentRule != null ?
						DefaultMorphism.prototype.createMorphism(parentCondition.cod(), lhs)
						: null;

		        // mapping from aspect nodes to RHS nodes
		        Map<AspectNode,Node> toRight = new HashMap<AspectNode,Node>();
		        toRights.put(level, toRight);

				// Create left subrule morphism if necessary
				Morphism leftRuleMorph = parentRule == null ?
						null
						: DefaultMorphism.prototype.createMorphism(parentRule.lhs(), lhs);
				// Create right subrule morphism if necessary
				Morphism rightRuleMorph = parentRule == null ?
						null
						: DefaultMorphism.prototype.createMorphism(parentRule.rhs(), rhs);
				
				if( parentRule != null ) {
					toRight.putAll(toRights.get(parentRuleLevel));
					// Copy universally matched elements to new rhs
					for( AspectNode node : newElements.first() ) {
						Node nodeImage = null;
						if( RuleAspect.inLHS(node) ) {
							// The node itself was already copied in the clone, don't add 
							nodeImage = graphToRuleMap.getNode(node);
						} else {
							nodeImage = computeNodeImage(node, graph);
							graphToRuleMap.putNode(node, nodeImage);
						}
						if( RuleAspect.inRHS(node) ) {
							rhs.addNode(nodeImage);
							toRight.put(node, nodeImage);
							if( RuleAspect.inLHS(node) ) {
								ruleMorph.putNode(nodeImage, nodeImage);
							}
						}
					}
					for( AspectEdge edge : newElements.second() ) {
						if( RuleAspect.inRHS(edge)
								&& !(RuleAspect.isCreator(edge) && RegExprLabel.isEmpty(edge.label())) ) {
							Edge rhsEdgeImage = computeEdgeImage(edge, graph, toRight);
							
							rhs.addEdge(rhsEdgeImage);
							if( RuleAspect.inLHS(edge) ) {
								ruleMorph.putEdge(graphToRuleMap.getEdge(edge), rhsEdgeImage);
							} else {
								graphToRuleMap.putEdge(edge, null);
							}
						}
					}
					
					// Populate predicate morphism
					for( Node n : parentCondition.cod().nodeSet() ) {
						predicateMorph.putNode(n, n);
					}
					for( Edge e : parentCondition.cod().edgeSet() ) {
						predicateMorph.putEdge(e, e);
					}
					
					// Populate the subrule morphisms
					for( Node n : parentRule.lhs().nodeSet() ) {
						leftRuleMorph.putNode(n, n);
					}
					for( Node n : parentRule.rhs().nodeSet() ) {
						rightRuleMorph.putNode(n, n);
					}
					for( Map.Entry<Node, Node> entry : parentRule.getMorphism().nodeMap().entrySet() ) {
						ruleMorph.putNode(leftRuleMorph.getNode(entry.getKey()),
								rightRuleMorph.getNode(entry.getValue()));
					}
					for( Edge e : parentRule.lhs().edgeSet() ) {
						leftRuleMorph.putEdge(e, e);
					}
					for( Edge e : parentRule.rhs().edgeSet() ) {
						rightRuleMorph.putEdge(e, e);
					}
					for( Map.Entry<Edge, Edge> entry : parentRule.getMorphism().edgeMap().entrySet() ) {
						ruleMorph.putEdge(leftRuleMorph.getEdge(entry.getKey()),
								rightRuleMorph.getEdge(entry.getValue()));
					}
					//for( Map.Entry<AspectNode, Node> entry : toRights.get(parentLevel).entrySet() ) {
						//toRight.put(entry.getKey(), rightRuleMorph.getNode(entry.getValue()));
					//}
					//groove.gui.Editor.previewRule(parentRule, "Parent of: " + level);
				}
				
				// Collect edges pointig to this meta node
				Set<AspectNode> localNodes = gatherLocalNodes(graph, an);
				
				for( AspectNode node : localNodes ) {
					if( RuleAspect.inRule(node) ) {
						Node nodeImage = computeNodeImage(node, graph);
						graphToRuleMap.putNode(node, nodeImage);
						if( RuleAspect.inLHS(node) ) {
							lhs.addNode(nodeImage);
						}
						if( RuleAspect.inRHS(node) ) {
							rhs.addNode(nodeImage);
							toRight.put(node, nodeImage);
							if( RuleAspect.inLHS(node) ) {
								ruleMorph.putNode(nodeImage, nodeImage);
							}
						}
						if( RuleAspect.inNAC(node) ) {
							nacPatches.add(node);
						}
					}
				}
				
				// Add merger edges
				for( AspectNode node : localNodes ) {
					for( Edge edgeit : graph.edgeSet(node) ) {
						AspectEdge edge = (AspectEdge)edgeit;
						if( ! NestingAspect.isMetaElement(edge) ) {
							if( RuleAspect.isCreator(edge) && RegExprLabel.isEmpty(edge.label()) && isEdgeOfThisLevel(graph, edge, level, name) ) {
								assert edge.endCount() == 2 : "Merger edge "+edge+ " should be binary";
								rhs.mergeNodes(toRight.get(edge.source()), toRight.get(edge.opposite()));
								toRight.put(edge.source(), toRight.get(edge.opposite()));
							}
						}
					}
				}
				
				// add edges to lhs, rhs and morphism
				//Set<GraphCondition> embargoes = new HashSet<GraphCondition> ();
				for( AspectNode node : localNodes ) {
					for( AspectEdge edge : graph.edgeSet(node)) {
						try {
							if( RuleAspect.inRule(edge) && isEdgeOfThisLevel(graph, edge, level, name) ) {
								Edge edgeImage = computeEdgeImage(edge, graph, graphToRuleMap.nodeMap());
								graphToRuleMap.putEdge(edge, edgeImage);
								if( RuleAspect.inLHS(edge) ) {
									lhs.addEdge(edgeImage);
								}
								if( RuleAspect.inRHS(edge)
										&& !(RuleAspect.isCreator(edge) && RegExprLabel.isEmpty(edge.label())) ) {
									Edge rhsEdgeImage = computeEdgeImage(edge, graph, toRight);
									
									rhs.addEdge(rhsEdgeImage);
									if( RuleAspect.inLHS(edge) ) {
										ruleMorph.putEdge(edgeImage, rhsEdgeImage);
									}
								}
								if( RuleAspect.inNAC(edge) ) {
									nacPatches.add(edge);
								}
							}
						} catch( FormatException exc ) {
							errors.addAll(exc.getErrors());
						}
					}
				}
				
				// Try to add named edges
				if( namedItemsMap.containsKey(name) ) {
					Set<AspectEdge> moreEdges = namedItemsMap.remove(name);
					for( AspectEdge edge : moreEdges ) {
						Edge edgeImage = computeEdgeImage(edge, graph, graphToRuleMap.nodeMap());
						graphToRuleMap.putEdge(edge, edgeImage);
						if( RuleAspect.inLHS(edge) ) {
							lhs.addEdge(edgeImage);
						}
						if( RuleAspect.inRHS(edge)
								&& !(RuleAspect.isCreator(edge) && RegExprLabel.isEmpty(edge.label())) ) {
							Edge rhsEdgeImage = computeEdgeImage(edge, graph, toRight);
							
							rhs.addEdge(rhsEdgeImage);
							if( RuleAspect.inLHS(edge) ) {
								ruleMorph.putEdge(edgeImage, rhsEdgeImage);
							}
						}
						if( RuleAspect.inNAC(edge) ) {
							nacPatches.add(edge);
						}
					}
				}

				// Create the local rule
				String ruleName = (level.indexOf('.') > 0) ?
						this.name.name()+'.'+level.replaceAll("\\.", "-")
						: this.name.name();
				NestedRule localRule = new NestedRule(level, ruleMorph, new RuleNameLabel(ruleName), priority, properties);
				
				if( !nacPatches.isEmpty() ) {
					NAC newCondition = new DefaultNAC(lhs, properties);
					VarGraph nac = newCondition.getTarget();
					for( AspectElement elem : nacPatches ) {
						if( elem instanceof AspectNode ) {
							Node nodeImage = computeNodeImage((AspectNode)elem, graph);
							graphToRuleMap.putNode((Node)elem, nodeImage);
							nac.addNode(nodeImage);
						}
					}
					for( AspectElement elem : nacPatches ) {
						if( elem instanceof AspectEdge ) {
							Edge edgeImage = computeEdgeImage((AspectEdge)elem, graph, graphToRuleMap.nodeMap());
							nac.addEdge(edgeImage);
						}
					}
					localRule.setAndNot(newCondition);
					nacPatches.clear();
				}
				
				// If necessary, create subrule morphism
				SPORuleMorphism subruleMorph = parentLevel == null ?
						null
						: SPORuleMorphism.prototype.createMorphism(parentRule, localRule, leftRuleMorph, rightRuleMorph);
				
				// Add level to parent RuleTree
				if( parentLevel != null ) {
					parentCondition.setAndNot(new ExistentialLevel(localRule, predicateMorph, properties));
					//System.out.println("Existential predicate morphism");
					//System.out.println(predicateMorph);
					localRule.setRuleMorphism(subruleMorph);
					//Editor.previewRule(localRule, this.name.name() + ":" + level);
				}
				if( localRule == null ) System.out.println("Hrm, null as Existential");
				//Editor.previewRule(localRule, this.name.name() + ":" + level);
				nestedRules.put(level, localRule);
			} else if( NestingAspect.isUniversalLevel(level) ) {
				if( an.getValue(NestingAspect.getInstance()).equals(NestingAspect.FORALL) ) {
					//System.out.println("Universal level, making VarGraph");
					// Get the level of the above existential level (if any)
					String parentLevel = NestingAspect.getParentLevel(level);
					NestedRule parentRule = nestedRules.get(parentLevel);
					
					// Create level and connecting morphism
					VarGraph target = (VarGraph)parentRule.lhs().clone();
					GraphInfo.setName(target, "UL:"+level);
					Morphism levelMorphism = DefaultMorphism.prototype.createMorphism(parentRule.lhs(), target);
					
					// Populate morphism with identity
					for( Node node : parentRule.lhs().nodeSet() ) {
						levelMorphism.putNode(node, node);
					}
					for( Edge edge : parentRule.lhs().edgeSet() ) {
						levelMorphism.putEdge(edge, edge);
					}
					
					// Add new elements
					// Collect edges pointig to this meta node
					Set<AspectNode> localNodes = gatherLocalNodes(graph, an);
					Set<AspectNode> addedNodes = new HashSet<AspectNode> ();
					Set<AspectEdge> addedEdges = new HashSet<AspectEdge> ();
					for( AspectNode node : localNodes ) {
						if( RuleAspect.inRule(node) ) {
							if( RuleAspect.inLHS(node) ) {
								Node nodeImage = computeNodeImage(node, graph);
								target.addNode(nodeImage);
								graphToRuleMap.putNode(node, nodeImage);
								addedNodes.add(node);
							} else if( ! RuleAspect.inLHS(node) || ! RuleAspect.inRHS(node) ) {
								// This kind of action should be at an existential level, patch...
								addedNodes.add(node);
								// FIXME: moet dit niet in de patch?
							}
						}
					}
					
					// Now add the edges
					for( AspectNode node : localNodes ) {
						for( AspectEdge edge : graph.edgeSet(node) ) {
							if( RuleAspect.inRule(edge) && isEdgeOfThisLevel(graph, edge, level, name) ) {
								if( RuleAspect.inLHS(edge) ) {
									Edge edgeImage = computeEdgeImage(edge, graph, graphToRuleMap.nodeMap());
									graphToRuleMap.putEdge(edge, edgeImage);
									target.addEdge(edgeImage);
									addedEdges.add(edge);
								} else if( ! RuleAspect.inLHS(edge) || ! RuleAspect.inRHS(edge) ) {
									// This kind of action should be at an existential level, patch...
									addedEdges.add(edge);
									// FIXME: moet dit niet in de patch?
								}
							}
						}
					}
					
					// Create the UniversalLevel
					//System.out.println("Universal predicate morphism");
					//System.out.println(levelMorphism);
					UniversalLevel newCondition = new UniversalLevel(levelMorphism, new NameLabel(level), properties);
					// Add the condition to the parent rule
					parentRule.setAndNot(newCondition);
					//System.out.println("NegConjunct");
					//System.out.println(parentRule.getNegConjunct());
					Pair<Set<AspectNode>, Set<AspectEdge>> addedStuff = Pair.createPair(addedNodes, addedEdges);
					Pair<UniversalLevel, Pair<Set<AspectNode>, Set<AspectEdge>>> storagePair = Pair.createPair(newCondition, addedStuff);
					// TODO: if no explicit child node defined, make one on the fly
					// Store the level
					//Editor.previewGraph(target, "UL:"+level);
					universalLevels.put(level, storagePair);
				} else if( an.getValue(NestingAspect.getInstance()).equals(NestingAspect.NAC) ) {
					// Get the level of the above existential level (if any)
					// FIXME: NACs alleen onder exists
					String parentLevel = NestingAspect.getParentLevel(level);
					NestedRule parentRule = nestedRules.get(parentLevel);
					
					// Create level and connecting morphism
					VarGraph target = (VarGraph)parentRule.lhs().clone();
					GraphInfo.setName(target, "NAC:"+level);
					Morphism levelMorphism = DefaultMorphism.prototype.createMorphism(parentRule.lhs(), target);
					
					// Populate morphism with identity
					for( Node node : parentRule.lhs().nodeSet() ) {
						levelMorphism.putNode(node, node);
					}
					for( Edge edge : parentRule.lhs().edgeSet() ) {
						levelMorphism.putEdge(edge, edge);
					}
					
					// Add new elements
					// Collect edges pointig to this meta node
					Set<AspectNode> localNodes = gatherLocalNodes(graph, an);
					for( AspectNode node : localNodes ) {
						Node nodeImage = computeNodeImage(node, graph);
						target.addNode(nodeImage);
						graphToRuleMap.putNode(node, nodeImage);
					}
					
					// Now add the edges
					for( AspectNode node : localNodes ) {
						for( AspectEdge edge : graph.edgeSet(node) ) {
							if( ! NestingAspect.isMetaElement(edge) ) {
								Edge edgeImage = computeEdgeImage(edge, graph, graphToRuleMap.nodeMap());
								graphToRuleMap.putEdge(edge, edgeImage);
								target.addEdge(edgeImage);
							}
						}
					}
					
					if( namedItemsMap.containsKey(name) ) {
						Set<AspectEdge> extra = namedItemsMap.remove(name);
						for( AspectEdge edge : extra ) {
							Edge edgeImage = computeEdgeImage(edge, graph, graphToRuleMap.nodeMap());
							graphToRuleMap.putEdge(edge, edgeImage);
							target.addEdge(edge);
						}
					}
					
					//Editor.previewGraph(target, "NAC " + level);
					// Create the NAC
					NAC newCondition = new DefaultNAC(levelMorphism, properties);
					// Add the condition to the parent rule
					parentRule.setAndNot(newCondition);
				} else {
					throw new FormatException("One or more levels are incorrectly nested. Tops should be forall!");
				}
			} else {
				throw new FormatException("One or more levels are incorrectly nested. Tops should be forall!");
			}
			
		}
		
		if( ! namedItemsMap.isEmpty() ) {
			throw new FormatException("One or more named items were not added to the rule! (Check level names)");
		}
		
        Pair<Rule, NodeEdgeMap> result = null;
		if( ! nestedRules.isEmpty() ) {
			// Fixate the top NestedRule
			// The top level is now ALWAYS 1, since there is an implicit top node.
			NestedRule topRule = nestedRules.get("1");
			// Notice: any other top-level elements are discarded. This is an implementation choice.
			//Editor.previewRule(topRule, "Toprule");
			topRule.fixate(nestedRules);
			
	        result = new Pair<Rule, NodeEdgeMap> (topRule, graphToRuleMap);
		} else {
			result = new Pair<Rule, NodeEdgeMap> (null, null);
		}
		metaNodes.clear(); metaNodes = null;
		namedItemsMap = null;
		levels.clear(); levels = null;
		nestedRules.clear(); nestedRules = null;
		for( Map.Entry<String, Pair<UniversalLevel, Pair<Set<AspectNode>, Set<AspectEdge>>>> ul : universalLevels.entrySet() ) {
			ul.getValue().second().first().clear();
			ul.getValue().second().second().clear();
		}
		universalLevels.clear(); universalLevels = null;
		for( Map.Entry<String, Map<AspectNode, Node>> entry : toRights.entrySet() ) {
			entry.getValue().clear();
		}
		toRights.clear(); toRights = null;
		nacLevels.clear(); nacLevels = null;
		backPatches.clear();backPatches = null;
		
		return result;
	}
	
	private Set<AspectNode>	gatherLocalNodes(AspectGraph graph, AspectNode metaNode) throws FormatException {
		Set<AspectEdge> localEdges = graph.edgeSet(metaNode, Edge.TARGET_INDEX);
		Set<AspectNode> localNodes = new HashSet<AspectNode> ();
		for( AspectEdge edge : localEdges ) {
			if( NestingAspect.isParentEdge(edge) )
				continue; // This is meta-edge towards a higher level
			if( NestingAspect.isLevelEdge(edge) ) {
				localNodes.add(edge.source());
			} else {
				throw new FormatException("Meta nodes may only have meta edges! (ie level and parent)");
			}
		}
		return localNodes;
	}
	
	/**
	 * Calculates the levels of a set of metanodes.
	 * @param graph
	 * @param metaNodes
	 * @return
	 */
	private SortedSet<AspectNode> calculateLevels(AspectGraph graph, Set<AspectNode> metaNodes) throws FormatException {
		SortedSet<AspectNode> sortedLevels = new java.util.TreeSet<AspectNode> (NestingAspect.comparator);
		
		Queue<Pair<AspectNode, Counter>> nextLevel = new LinkedList<Pair<AspectNode, Counter>> ();
		Counter topLevelCounter = new Counter(0);
		for( AspectNode an : metaNodes ) {
			if( ((NestingAspectValue)(an.getValue(NestingAspect.getInstance()))).getLevel() != null ) {
				// Already has a level, add and skip (prolly rule renamed)
				sortedLevels.add(an);
				continue;
			}
			AspectNode top = an;
			while( ! graph.outEdgeSet(top).isEmpty() ) {
				// Move one up in the hierarchy
				top = graph.outEdgeSet(top).iterator().next().target();
			}
			
			// top is now the top of a tree
			String level = "1.";
			((NestingAspectValue)(top.getValue(NestingAspect.getInstance()))).setLevel(level);
			nextLevel.add(Pair.createPair(top, topLevelCounter));
			while( ! nextLevel.isEmpty() ) {
				Pair<AspectNode, Counter> work = nextLevel.poll();
				// Set correct level for this element
				level = ((NestingAspectValue)(work.first().getValue(NestingAspect.getInstance()))).getLevel();
				level = level + work.second().increment();
				((NestingAspectValue)(work.first().getValue(NestingAspect.getInstance()))).setLevel(level);
				// Create level for children:
				level = level + ".";
				
				// Add any children to the work queue
				Counter localCounter = new Counter(0);
				for( Edge edge : graph.edgeSet(work.first(), Edge.TARGET_INDEX) ) {
					if( edge.source() != work.first() && NestingAspect.isParentEdge((AspectEdge)edge) ) {
						AspectNode nextWork = (AspectNode)edge.source();
						try {
							((NestingAspectValue)(nextWork.getValue(NestingAspect.getInstance()))).setLevel(level);
						} catch( NullPointerException e ) {
							throw new FormatException("Check level nesting!");
						}
						nextLevel.add(Pair.createPair(nextWork, localCounter));
					}
				}
				
				// Add current element to the resultset
				sortedLevels.add(work.first());
			}
		}
		
		return sortedLevels;
	}
	
	protected NestedRule createRule(String level, Morphism ruleMorphism, RuleNameLabel name, int priority) throws FormatException {
		return new NestedRule(level, ruleMorphism, name, priority, properties);
	}
	
	/**
	 * Determine whether an edge belongs to a certain level. This
	 * method assigns the level of the deepest endpoint to the edge.
	 * @param context the aspect graph that is currently being processed
	 * @param edge the edge to inquire about
	 * @param level the requested level
	 * @param name the name of the current (requested) level (a name on an edge overrides its normal level)
	 * @return true if edge belongs the request level, false if not
	 */
	protected boolean isEdgeOfThisLevel(AspectGraph context, AspectEdge edge, String level, String name) {
		NestingAspectValue value = (NestingAspectValue)edge.getValue(NestingAspect.getInstance());
		if( value != null && value.getContent() != null ) {
			return name != null && value.getContent().equals(name);
		}
		String srcLevel = NestingAspect.getLevelOfNode(context, edge.source());
		String tgtLevel = NestingAspect.getLevelOfNode(context, edge.target());
		
		if( srcLevel == null || tgtLevel == null ) return false;
		
		int cmpSrc = srcLevel.compareTo(level);
		int cmpTgt = tgtLevel.compareTo(level);
		
		boolean result = (cmpSrc == 0 && cmpTgt <= 0) || (cmpTgt == 0 && cmpSrc <= 0);
		//System.out.println(result);
		return result;
	}
	
	/**
	 * Determine whether an edge set is pure. A pure edge set contains
	 * no level meta-edges.
	 * @param edges the edge set to test for purity
	 * @return true if there is no level meta-edge in the set, false
	 *   otherwise 
	 */
	protected boolean isPureEdgeSet(Set<AspectEdge> edges) {
		boolean isPure = true;
		for( AspectEdge edge : edges ) {
			isPure = isPure && ! NestingAspect.isLevelEdge(edge);
		}
		return isPure;
	}

}
