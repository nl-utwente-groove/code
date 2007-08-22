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
 * $Id: NestedRule.java,v 1.1 2007-08-22 09:19:47 kastenberg Exp $
 */
package groove.nesting.rule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import groove.graph.DefaultMorphism;
import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.Graph;
import groove.graph.Morphism;
import groove.graph.Node;
import groove.graph.match.SearchItem;
import groove.nesting.VarNodeEdgeMultiMap;
import groove.rel.RegExprGraph;
import groove.rel.RegExprLabel;
import groove.rel.RegExprMorphism;
import groove.rel.VarGraph;
import groove.rel.VarMorphism;
import groove.rel.VarNodeEdgeMap;
import groove.trans.DefaultConditionOutcome;
import groove.trans.DefaultMatching;
import groove.trans.DefaultPredicateOutcome;
import groove.trans.GraphCondition;
import groove.trans.GraphConditionOutcome;
import groove.trans.GraphPredicate;
import groove.trans.GraphPredicateOutcome;
import groove.trans.GraphTest;
import groove.trans.GraphTestOutcome;
import groove.trans.Matching;
import groove.trans.Rule;
import groove.trans.RuleEvent;
import groove.trans.RuleNameLabel;
import groove.trans.SPORule;
import groove.trans.SystemProperties;
import groove.trans.SystemRecord;
import groove.util.Pair;
import groove.view.FormatException;

/**
 * Alleen regels die geen forest zijn
 * @author kramor
 * @version 0.1 $Revision: 1.1 $ $Date: 2007-08-22 09:19:47 $
 */
public class NestedRule extends SPORule {

	/* (non-Javadoc)
	 * @see groove.trans.DefaultGraphCondition#getOutcome(groove.graph.Graph)
	 */
	@Override
	public GraphConditionOutcome getOutcome(Graph subject) {
		// Fix branches on existential levels
		return fixOutcome(super.getOutcome(subject));
	}

	/* (non-Javadoc)
	 * @see groove.trans.DefaultGraphCondition#getOutcome(groove.rel.VarMorphism)
	 */
	@Override
	public GraphConditionOutcome getOutcome(VarMorphism subject) {
		// Fix branches on existential levels
		return fixOutcome(super.getOutcome(subject));
	}
	
	protected GraphConditionOutcome fixOutcome(GraphConditionOutcome subject) {
		return subject;
	}

	/** Collection of all subrules in this nested rule, only maintained by top rule */
	Map<String, NestedRule> subrules;
	
	/** Rule morphism connecting this rule's parent to it */
	SPORuleMorphism morphismFromParent;
	
	/** String representation of the level of this NestedRule */
	String level;
	
	static {
		// Turn off the use of dependency checks
		groove.lts.AliasRuleApplier.setUseDependencies(false);
		/* Deze optimalisatie aanzetten levert normale resultaten bij gossiping girls,
		 * maar petrinet wil niet meer binnen het geheugen blijven...
		 */
	}
	
	private boolean hasCreators;
	
	/**
	 * Constructs a new NestedRule
	 * @param topMorphism
	 * @param name
	 * @param priority
	 * @param properties
	 * @throws FormatException
	 */
	public NestedRule(String level, Morphism topMorphism, RuleNameLabel name, int priority, SystemProperties properties) throws FormatException {
		super(topMorphism, name, priority, properties);
		this.level = level;
	}
	
	public NestedRule(SPORule other, SystemProperties properties) throws FormatException {
		super(other.getMorphism(), other.getName(), other.getPriority(), properties);
		this.level = "1";
		for( GraphCondition cond : other.getNegConjunct().getConditions() ) {
			setAndNot(cond);
		}
		Map<String, NestedRule> subs = new HashMap<String, NestedRule> ();
		subs.put(this.level, this);
		this.fixate(subs);
	}
	
	/**
	 * Fixates the NestedRule. It collects all the rules connected to it at toplevel
	 * and calls {@link DefaultGraphCondition#setFixed} to fixate the predicate structure.
	 */
	public void fixate(Map<String, NestedRule> subrules) {
		setFixed();
		if( this.subrules == null ) {
			this.subrules = new HashMap<String, NestedRule> (subrules);
			boolean isModifying = super.isModifying();
			hasCreators = super.hasCreators();
			for( NestedRule tmp : subrules.values() ) {
				tmp.setFixed();
				isModifying = tmp.isModifying() || isModifying;
				hasCreators = tmp.hasCreators() || hasCreators;
			}
			setModifying(isModifying);
		}
	}
	
	/**
	 * Indicates if the rule creates any nodes or edges.
	 */
	// JHK: public
	@Override
	public boolean hasCreators() {
		if( subrules == null ) {
			// This is not the top rule
			return super.hasCreators();
		} else {
			return hasCreators;
		}
	}

	/**
	 * Returns the set of rules combined in this NestedRule
	 * @return an immutable set of NestedRules
	 */
	public Collection<NestedRule> getSubrules() {
		return Collections.unmodifiableCollection(subrules.values());
	}
	
	/**
	 * 
	 * @return
	 */
	public Map<String, NestedRule> getSubruleMap() {
		return Collections.unmodifiableMap(subrules);
	}

	/**
	 * Sets the SPORuleMorphism connecting this rule's parent to it. Can only be done
	 * while not fixed.
	 * @param morphism the SPORuleMorphism connecting a parent to this rule
	 */
	public void setRuleMorphism(SPORuleMorphism morphism) {
		if(!isFixed()) {
			morphismFromParent = morphism;
		}
	}
	
	/**
	 * @return the SPORuleMorphism connecting this rule's parent to it, if any
	 */
	public SPORuleMorphism getRuleMorphism() {
		return morphismFromParent;
	}

	/* (non-Javadoc)
	 * @see groove.trans.SPORule#newEvent(groove.rel.VarNodeEdgeMap, groove.trans.SystemRecord)
	 */
	@Override
	public RuleEvent newEvent(VarNodeEdgeMap anchorMap, SystemRecord record) {
		throw new RuntimeException("Find this call and change it!");
	}
	
	/**
	 * Computes the eraser (i.e., lhs-only) nodes.
	 */
	@Override
	protected Node[] computeEraserNodes() {
		if( subrules == null ) {
			// not a toprule
			return super.computeEraserNodes();
		} else {
			// construct lhsOnlyNodes
			Set<Node> result = new HashSet<Node> ();
			result.addAll(Arrays.asList(super.computeEraserNodes()));
			for( NestedRule rule : subrules.values() ) {
				if( rule != this ) {
					Set<Node> eraserNodeSet = new HashSet<Node>(rule.lhs().nodeSet());
					eraserNodeSet.removeAll(rule.getMorphism().nodeMap().keySet());
					result.addAll(eraserNodeSet);
				}
			}
		    return result.toArray(new Node[0]);
		}
	}
	
	/**
	 * Computes the set of variable-binding edges occurring in the lhs.
	 */
	@Override
	protected Edge[] computeVarEdges() {
		if( subrules == null ) {
			// no toprule
			return super.computeVarEdges();
		} else {
			Set<Edge> result = new HashSet<Edge> ();
			result.addAll(Arrays.asList(super.computeVarEdges()));
			for( NestedRule rule : subrules.values() ) {
				if( rule != this ) {
					result.addAll(rule.lhs().varEdgeSet());
				}
			}
			return result.toArray(new Edge[0]);
		}
	}
	
	/**
	 * Computes the variables occurrind in RHS edges.
	 */
	@Override
	protected String[] computeCreatorVars() {
		if( subrules == null ) {
			// no toprule
			return super.computeCreatorVars();
		} else {
			Set<String> creatorVarSet = new HashSet<String>();
			creatorVarSet.addAll(Arrays.asList(super.computeCreatorVars()));
			for( NestedRule rule : subrules.values() ) {
				if( rule != this ) {
				    for (int i = 0; i < rule.getCreatorEdges().length; i++) {
				        Edge creatorEdge = rule.getCreatorEdges()[i];
				        String creatorVar = RegExprLabel.getWildcardId(creatorEdge.label());
				        if (creatorVar != null) {
				            creatorVarSet.add(creatorVar);
				        }
				    }
			    }
			}
		    return creatorVarSet.toArray(new String[0]);
		}
	}
	
	/** Computes the array of nodes isolated in the left hand side. */
	@Override
	protected Node[] computeIsolatedNodes() {
		if( subrules == null ) {
			// no toprule
			return super.computeIsolatedNodes();
		} else {
			Set<Node> result = new HashSet<Node>();
			result.addAll(Arrays.asList(super.computeIsolatedNodes()));
			for( NestedRule rule : subrules.values() ) {
				if( rule != this ) {
					for (Node node: rule.lhs().nodeSet()) {
						if (rule.lhs().edgeSet(node).isEmpty()) {
							result.add(node);
						}
					}
				}
			}
			return result.toArray(new Node[0]);
		}
	}
	
    /**
	 * Computes the eraser (i.e., LHS-only) edges.
	 */
	@Override
	protected Edge[] computeEraserEdges() {
		if( subrules == null ) {
			// no toprule
			return super.computeEraserEdges();
		} else {
			Set<Edge> result = new HashSet<Edge> ();
			result.addAll(Arrays.asList(super.computeEraserEdges()));
			for( NestedRule rule : subrules.values() ) {
				if( rule != this ) {
				    Set<Edge> eraserEdgeSet = new HashSet<Edge>(rule.lhs().edgeSet());
				    eraserEdgeSet.removeAll(rule.getMorphism().edgeMap().keySet());
				    // also remove the incident edges of the lhs-only nodes
				    for (Node eraserNode: getEraserNodes()) {
				        eraserEdgeSet.removeAll(rule.lhs().edgeSet(eraserNode));
				    }
				}
			}
		    return result.toArray(new Edge[0]);
		}
	}
	
	/**
	 * Computes the array of creator edges that are not themselves anchors.
	 */
	@Override
	protected Edge[] computeEraserNonAnchorEdges() {
		if( subrules == null ) {
			// no toprule
			return super.computeEraserNonAnchorEdges();
		} else {
			Set<Edge> result = new HashSet<Edge> ();
			result.addAll(Arrays.asList(super.computeEraserNonAnchorEdges()));
			for( NestedRule rule : subrules.values() ) {
				if( rule != this ) {
					Set<Edge> eraserNonAnchorEdgeSet = new HashSet<Edge>(Arrays.asList(rule.getEraserEdges()));
					eraserNonAnchorEdgeSet.removeAll(Arrays.asList(rule.anchor()));
				}
			}
			return result.toArray(new Edge[0]);			
		}
	}
	
	/**
	 * Computes the creator (i.e., RHS-only) nodes.
	 */
	@Override
	protected Node[] computeCreatorNodes() {
		if( subrules == null ) {
			// This is not a top-rule
			return super.computeCreatorNodes();
			
		} else {
			Set<Node> result = new HashSet<Node> ();
			result.addAll(Arrays.asList(super.computeCreatorNodes()));
			for( NestedRule rule : subrules.values() ) {
				if( rule != this ) {
					Set<Node> tmp = new HashSet<Node>(rule.rhs().nodeSet());
					tmp.removeAll(rule.getMorphism().nodeMap().values());
					result.addAll(tmp);
				}
			}
			return result.toArray(new Node[0]);
		}
	}
	
    /**
     * Computes the creator (i.e., RHS-only) edges.
     */
	@Override
    protected Edge[] computeCreatorEdges() {
		if( subrules == null ) {
			//This is not a top-rule
			return super.computeCreatorEdges();
		} else {
			Set<Edge> result = new HashSet<Edge> ();
			result.addAll(Arrays.asList(super.computeCreatorEdges()));
			for( NestedRule rule : subrules.values() ) {
				if( rule != this ) {
					Set<Edge> tmp = new HashSet<Edge>(rule.rhs().edgeSet());
					tmp.removeAll(rule.getMorphism().edgeMap().values());
					result.addAll(tmp);
				}
			}
	        return result.toArray(new Edge[0]);
		}
    }
	
	
	/**
	 * Computes a value for the creator map.
	 * The creator map maps the endpoints of creator edges
	 * that are not themselves creator nodes to one of their pre-images.
	 */
	@Override
	protected Map<Node, Node> computeCreatorMap() {
		// construct rhsOnlyMap
		if( subrules == null ) {
			return super.computeCreatorMap();
		} else {
		    Map<Node, Node> result = new HashMap<Node, Node>();
		    result.putAll(super.computeCreatorMap());
		    for( NestedRule rule : subrules.values() ) {
		    	if( rule != this ) {
			    	Set<? extends Node> creatorNodes = rule.getCreatorGraph().nodeSet();
			    	for (Map.Entry<Node,Node> nodeEntry: rule.getMorphism().elementMap().nodeMap().entrySet()) {
			    		if (creatorNodes.contains(nodeEntry.getValue())) {
			    			result.put(nodeEntry.getValue(), nodeEntry.getKey());
			    		}
			    	}
		    	}
		    }
		    return result;
		}
	}

	/**
	 * Computes the merge map, which maps each LHS node 
	 * that is merged with others
	 * to the LHS node it is merged with.
	 */
	@Override
	protected Map<Node, Node> computeMergeMap() {
		Map<Node,Node> result = new HashMap<Node,Node>();
		Map<Node,Node> rhsToLhsMap = new HashMap<Node,Node>();
		for (Map.Entry<Node,Node> nodeEntry: getMorphism().elementMap().nodeMap().entrySet()) {
			Node mergeTarget = rhsToLhsMap.get(nodeEntry.getValue());
			if (mergeTarget == null) {
				mergeTarget = nodeEntry.getKey();
				rhsToLhsMap.put(nodeEntry.getValue(), mergeTarget);
			} else {
				result.put(nodeEntry.getKey(), mergeTarget);
				// the merge target is also merged
				// maybe we do this more than once, but that's negligable
				result.put(mergeTarget, mergeTarget);
			}
		}
		return result;
	}

	public RuleEvent newEvent(VarNodeEdgeMultiMap elementMap, Matching matching, GraphTestOutcome<GraphCondition, Matching> outcome, SystemRecord record) {
		return new NestedEvent(this, elementMap, matching, outcome, record);
	}
	
	public String getLevel() {
		return this.level;
	}
	
}
