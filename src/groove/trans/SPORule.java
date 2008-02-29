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
 * $Id: SPORule.java,v 1.51 2008-02-29 11:02:20 fladder Exp $
 */
package groove.trans;

import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.Graph;
import groove.graph.GraphShape;
import groove.graph.Morphism;
import groove.graph.Node;
import groove.graph.NodeEdgeHashMap;
import groove.graph.NodeEdgeMap;
import groove.graph.NodeFactory;
import groove.graph.algebra.ValueNode;
import groove.match.MatchStrategy;
import groove.match.SearchPlanStrategy;
import groove.rel.RegExprLabel;
import groove.rel.VarNodeEdgeMap;
import groove.rel.VarSupport;
import groove.util.Groove;
import groove.util.NestedIterator;
import groove.util.TransformIterator;
import groove.view.FormatException;
import groove.view.aspect.AspectValue;

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
import java.util.TreeSet;

/**
 * Default model of a graph production rule.
 * This implementation assumes simple graphs, and yields 
 * <tt>DefaultTransformation</tt>s.
 * @author Arend Rensink
 * @version $Revision: 1.51 $
 */
public class SPORule extends PositiveCondition<RuleMatch> implements Rule {
    /**
     * @param morph the morphism on which this production is to be based
     * @param name the name of the new rule
     * @param priority the priority of this rule; should be non-negative
     * @param properties the factory this rule used to instantiate related classes
     * @throws FormatException if the rule system properties do not concur with the rule itself
     */
    public SPORule(Morphism morph, RuleNameLabel name, int priority, SystemProperties properties) throws FormatException {
        super(morph.dom(), name, properties);
        this.morphism = morph;
    	this.priority = priority;
    }
    
    /**
     * Constructs a rule that is a sub-condition of another rule.
     * The information should be completed lated by a call to {@link #setParent(SPORule, int[])}.
     * @param morph the morphism on which this production is to be based
     * @param rootMap pattern map leading into the LHS
     * @param name the name of the new rule
     * @param properties the factory this rule used to instantiate related classes
     * @throws FormatException if the rule system properties do not concur with the rule itself
     */
    public SPORule(Morphism morph, NodeEdgeMap rootMap, NodeEdgeMap coRootMap, RuleNameLabel name, SystemProperties properties) throws FormatException {
        super(morph.dom(), rootMap, name, properties);
        this.coRootMap = coRootMap;
        this.morphism = morph;
    	this.priority = DEFAULT_PRIORITY;
    	assert this.rhs().nodeSet().containsAll(coRootMap.nodeMap().values()) : String.format("RHS nodes %s do not contain all co-root values %s", rhs().nodeSet(), coRootMap.nodeMap().values()); 
    }
    
    /** Sets the priority of this rule. */
    public void setPriority(int priority) {
    	testFixed(false);
    	this.priority = priority;
    }
    
    /**
     * Sets the parent rule of this rule, together with the nesting level
     * and the co-root map.
     * @param parent the parent rule for this rule; not <code>null</code>
     * @param level nesting level of this rule within the condition tree
     */
    public void setParent(SPORule parent, int[] level) {
    	testFixed(false);
    	assert coRootMap != null : String.format("Sub-rule at level %s must have a non-trivial co-root map", Arrays.toString(level));
    	assert parent.rhs().nodeSet().containsAll(coRootMap.nodeMap().keySet()) : String.format("Parent nodes %s do not contain all co-roots %s", parent.rhs().nodeSet(), coRootMap.nodeMap().keySet()); 
    	this.parent = parent;
    	this.level = level;
    }
    
    /** 
     * Returns the parent rule of this rule.
     * The parent may be this rule itself. 
     */
    public SPORule getParent() {
    	if (parent == null) {
        	testFixed(true);
        	parent = this;
    	}
    	return parent;
    }
    
    /** Indicates if this is a top-level rule. */
    public boolean isTop() {
        return getParent() == this;
    }
    
    /** Returns the top rule of the hierarchy in which this rule is nested. */
    public SPORule getTop() {
        if (isTop()) {
            return this;
        } else {
            return getParent().getTop();
        }
    }
    
    /** 
     * Returns the nesting position of this rule in the rule hierarchy.
     * Each array element indicates a next level of the tree; the value 
     * is the order index within the level.
     * Thus, an empty array indicates this is a top-level rule.
     * Parent rule and level uniquely identify a rule.
     */
    public int[] getLevel() {
    	if (level == null) {
    		testFixed(true);
    		level = new int[0];
    	}
    	return level;
    }
    
    /** 
     * Returns the direct sub-rules of this rule, or the entire rule hierarchy.
     * @param recursive if <code>true</code>, returns the entire rule hierarch (including this rule);
     * otherwise, only returns the direct sub-rules.
     */
    public Collection<SPORule> getSubRules(boolean recursive) {
    	Collection<SPORule> result = new TreeSet<SPORule>();
    	if (recursive) {
    		result.add(this);
    	}
    	for (SPORule subRule: getDirectSubRules()) {
    		result.add(subRule);
    		if (recursive) {
    			result.addAll(subRule.getSubRules(true));
    		}
    	}
    	return result;
    }
    
    /**
     * Indicates if this rule has sub-rules.
     */
    public boolean hasSubRules() {
    	return !getDirectSubRules().isEmpty();
    }
    
    /** 
     * Returns the direct sub-rules of this rule,
     * i.e., the sub-rules that have this rule as their parent.
     */
    private Collection<SPORule> getDirectSubRules() {
    	if (directSubRules == null) {
    		directSubRules = new TreeSet<SPORule>();
        	for (AbstractCondition<?> condition: getSubConditions()) {
        		for (AbstractCondition<?> subCondition: condition.getSubConditions()) {
        			if (subCondition instanceof SPORule) {
        				directSubRules.add((SPORule) subCondition);
        			} 
        		}
        	}
    	}
    	return directSubRules;
    }
    
    /**
     * Apart from the super method, also tests the RHS of the rule for 
     * attributes.
     */
    @Override
    public void testConsistent() throws FormatException {
    	super.testConsistent();
    	if (!getProperties().isAttributed() && ValueNode.hasValueNodes(rhs())) {
    		String attributeKey = SystemProperties.ATTRIBUTES_KEY;
    		String attributeProperty = getProperties().getProperty(attributeKey);
    		if (attributeProperty == null) {
    			throw new FormatException("Rule uses attributes, but \"%s\" not declared", attributeKey);
    		} else {
        		throw new FormatException("Rule uses attributes, violating \"%s=%s\"", attributeKey, attributeProperty);
    		}
    	}
    }
    
    @Deprecated
    public RuleEvent newEvent(VarNodeEdgeMap anchorMap, NodeFactory nodeFactory, boolean reuse) {
        return new SPOEvent(this, anchorMap, nodeFactory, reuse);
    }
    
    /** Creates the search plan using the rule's search plan factory. */
    public MatchStrategy<VarNodeEdgeMap> getEventMatcher() {
        if (eventMatcher == null) {
            eventMatcher = getMatcherFactory().createMatcher(this, getAnchorGraph().nodeSet(), getAnchorGraph().edgeSet());
        }
        return eventMatcher;
    }

	@Override
    public Iterator<RuleMatch> computeMatchIter(final GraphShape host, Iterator<VarNodeEdgeMap> matchMapIter) {
        Iterator<RuleMatch> result = null;
        result = new NestedIterator<RuleMatch>(new TransformIterator<VarNodeEdgeMap,Iterator<RuleMatch>>(matchMapIter) {
        	@Override
        	public Iterator<RuleMatch> toOuter(VarNodeEdgeMap matchMap) {
        		if (isValidMatchMap(host, matchMap)) {
        			return addSubMatches(host, createMatch(matchMap)).iterator();
        		} else {
        			return null;
        		}
        	}
        });
        return result;
    }

	/** 
	 * Returns a collection of matches extending a given match with
	 * matches for the sub-conditions.
	 */
    Collection<RuleMatch> addSubMatches(GraphShape host, RuleMatch simpleMatch) {
    	Collection<RuleMatch> result = Collections.singleton(simpleMatch);
		VarNodeEdgeMap matchMap = simpleMatch.getElementMap();
		for (AbstractCondition<?> condition : getComplexSubConditions()) {
			Iterable<? extends Match> subMatches = condition.getMatches(host, matchMap);
			Collection<RuleMatch> oldResult = result;
			result = new ArrayList<RuleMatch>();
			for (RuleMatch oldMatch : oldResult) {
				result.addAll(oldMatch.addSubMatchChoice(subMatches));
			}
		}
		return result;
    }

    /**
	 * Callback factory method to create a match on the basis of a mapping of
	 * this condition's target.
	 * 
	 * @param matchMap
	 *            the mapping, presumably of the elements of
	 *            {@link #getTarget()} into some host graph
	 * @return a match constructed on the basis of <code>map</code>
	 */
	@Override
    protected RuleMatch createMatch(VarNodeEdgeMap matchMap) {
        return new RuleMatch(this, matchMap);
    }
    
	/**
	 * Tests whether a given match map satisfies the additional constraints imposed by this
	 * rule.
	 * @param host the graph to be matched
	 * @param matchMap the proposed map from {@link #getTarget()} to <code>host</code>
	 * @return <code>true</code> if <code>matchMap</code> satisfies the constraints imposed
	 * by the rule (if any).
	 */
	boolean isValidMatchMap(GraphShape host, VarNodeEdgeMap matchMap) {
		boolean result = true;
		if (SystemProperties.isCheckDangling(getProperties())) {
			result = satisfiesDangling(host, matchMap);
		}
		return result;
	}

	/** Tests if a given (proposed) match into a host graph leaves dangling edges. */ 
	private boolean satisfiesDangling(GraphShape host, VarNodeEdgeMap match) {
		boolean result = true;
		for (Node eraserNode : getEraserNodes()) {
			Node erasedNode = match.getNode(eraserNode);
			Set<Edge> danglingEdges = new HashSet<Edge>(host.edgeSet(erasedNode));
			for (Edge eraserEdge: lhs().edgeSet(eraserNode)) {
				danglingEdges.remove(match.getEdge(eraserEdge));
			}
			if (!danglingEdges.isEmpty()) {
				result = false;
				break;
			}
		}
		return result;
	}

    public Graph lhs() {
    	if (lhs == null) {
    		lhs = getMorphism().dom();
    	}
        return lhs;
    }

    public Graph rhs() {
    	if (rhs == null) {
    		rhs = getMorphism().cod();
    	}
        return rhs;
    }

    public Morphism getMorphism() {
        return morphism;
    }

    /** 
     * Returns the array of elements that should be matched to
     * have an unambiguous rule event.
     * This includes the eraser nodes (or incident edges thereof),
     * the eraser edges (or end nodes thereof) and the end nodes of 
     * creator edges (insofar they are not creator nodes), as well as
     * root node images.
     */
	public Element[] anchor() {
        if (anchor == null) {
            getTop().setAnchor(null);
        }
        return anchor;
    }

	/** 
	 * Sets the anchor of this rule recursively.
	 * Anchors of sub-rules that have roots in this rule are added.
	 * @param parentAnchor the collection of anchors from the parent rule; may be <code>null</code>
	 * if this rule is the top rule.
	 */
	private void setAnchor(Collection<Element> parentAnchor) {
		Collection<Element> myAnchor = new TreeSet<Element>(Arrays.asList(computeNestedAnchor()));
		for (SPORule subRule: getSubRules(false)) {
			subRule.setAnchor(myAnchor);
		}
		if (parentAnchor != null) {
			for (Map.Entry<Node, Node> rootNodeEntry : getRootMap().nodeMap().entrySet()) {
				// TODO the following selects a node in the universal condition, not the parent rule!
				// This goes right because node identities are actually the same, but...
				Node myNode = rootNodeEntry.getValue();
				Node parentNode = rootNodeEntry.getKey();
				if (myAnchor.contains(myNode) && getParent().lhs().containsElement(parentNode) && isAnchorable(myNode)) {
					parentAnchor.add(parentNode);
				}
			}
			for (Map.Entry<Edge, Edge> rootEdgeEntry : getRootMap().edgeMap().entrySet()) {
				Edge myEdge = rootEdgeEntry.getValue();
				Edge parentEdge = rootEdgeEntry.getKey();
				if (myAnchor.contains(myEdge) && getParent().lhs().containsElement(parentEdge) && isAnchorable(myEdge)) {
					parentAnchor.add(parentEdge);
				}
			}
		}
		this.anchor = myAnchor.toArray(new Element[0]);
	}
	
	/**
	 * Callback method creating the anchors of this rule.
	 * Called from the constructor.
	 * This implementation delegates to {@link #getAnchorFactory()}.
	 */
	private Element[] computeNestedAnchor() {
	    return anchorFactory.newAnchors(this);
	}

    // -------------------- OBJECT OVERRIDES -----------------------------

	/**
     * @see Object#toString()
     */
	@Override
    public String toString() {
        StringBuilder res = new StringBuilder(String.format("Rule %s, level %s, anchor %s%n", getName(), Groove.toString(Groove.toArray(getLevel())), Groove.toString(anchor())));
        res.append(String.format("LHS: %s%nRHS: %s%nMorphism: %s", lhs(), rhs(), getMorphism().elementMap()));
        if (!getRootMap().isEmpty()) {
        	res.append(String.format("%nRoot map: %s", getRootMap()));
        } else if (!getCoRootMap().isEmpty()) {
        	res.append(String.format("%nCo-root map: %s", getCoRootMap()));
        }
        if (!getSubConditions().isEmpty()) {
            res.append(String.format("%n----Subconditions of %s:", getName()));
            for (Condition subCondition: getSubConditions()) {
                res.append(String.format("%n%s", subCondition));
            }
            res.append(String.format("%n----End of %s", getName()));
        }
        return res.toString();
    }
    
    /** 
     * Compares two rules on the basis of their nesting level,
     * or failing that, their names. 
     */
    public int compareTo(Rule other) {
    	int result = 0;
    	if (!(other instanceof SPORule)) {
        	// SPO rules come before others
    		result = -1;
    	} else if (! equals(other)) {
        	// compare parent rules
			Rule otherParent = ((SPORule) other).getParent();
			if (equals(getParent())) {
				other = otherParent;
			} else {
				result = getParent().compareTo(otherParent);
			}
			if (result == 0) {
				// compare levels
				int[] level = getLevel();
				int[] otherLevel = ((SPORule) other).getLevel();
				for (int depth = 0; result == 0 && depth < level.length; depth++) {
					if (depth == otherLevel.length) {
						result = +1;
					} else {
						result = level[depth] - otherLevel[depth];
					}
				}
			}
			if (result == 0) {
				// we have to rely on names, so they'd better be non-null
				result = getName().compareTo(other.getName());
			}
		}
		return result;
    }

    // ------------------- commands --------------------------

    /**
     * Specialises the return type.
     */
    @Override
    public RuleNameLabel getName() {
    	return (RuleNameLabel) super.getName();
    }
    
    public int getPriority() {
		return priority;
	}

    /** 
	 * In addition to calling the super method,
	 * adds implicit NACs as dictated by {@link SystemProperties#isCheckCreatorEdges()}
	 * and {@link SystemProperties#isRhsAsNac()}.
	 */
	@Override
	public void setFixed() throws FormatException {
		if (!isFixed()) {
			if (getProperties() != null) {
				if (getProperties().isCheckCreatorEdges()) {
					for (Edge edge : getSimpleCreatorEdges()) {
						addSubCondition(new EdgeEmbargo(lhs(),
								getCreatorMap().mapEdge(edge), getProperties()));
					}
				}
				if (getProperties().isRhsAsNac() && hasCreators()) {
					Condition rhsNac = new NotCondition(rhs(), getMorphism().elementMap(), getProperties());
					rhsNac.setFixed();
					addSubCondition(rhsNac);
				}
			}
			super.setFixed();
		}
        if (PRINT && isTop()) {
            System.out.println(toString());
        }
	}

	/** Returns an array of nodes isolated in the left hand side. */
	final public Node[] getIsolatedNodes() {
		if (isolatedNodes == null) {
			isolatedNodes = computeIsolatedNodes();
		}
		return isolatedNodes;
	}

	/** Computes the array of nodes isolated in the left hand side. */
	private Node[] computeIsolatedNodes() {
		testFixed(true);
		Set<Node> result = new HashSet<Node>();
		for (Node node: lhs().nodeSet()) {
			if (lhs().edgeSet(node).isEmpty()) {
				result.add(node);
			}
		}
		result.removeAll(getRootMap().nodeMap().values());
		return result.toArray(new Node[0]);
	}

	/**
	 * Indicates if this rule has mergers.
	 * @invariant <tt>result == ! getMergeMap().isEmpty()</tt>
	 */
	final public boolean hasMergers() {
		if (! hasMergersSet) {
			hasMergers = computeHasMergers();
			hasMergersSet = true;
		}
		return hasMergers;
	}
	
	/**
	 * Computes if the rule has mergers or not.
	 */
	private boolean computeHasMergers() {
		boolean result = ! getMergeMap().isEmpty();
		if (!result) {
			result = hasMergingSubRules(this);
		}
		return result;
	}
	
	/**
	 * Computes if a given condition has merging rules as sub-conditions.
	 */
	private boolean hasMergingSubRules(Condition condition) {
		boolean result = false;		
		for (Condition subCondition : condition.getSubConditions()) {
			if (subCondition instanceof Rule) {
				result = ((Rule) subCondition).hasMergers();
			} else {
				result = hasMergingSubRules(subCondition);
			}
			if (result) {
				break;
			}
		}
		return result;
	}
	
	public boolean isModifying() {
		if (! modifyingSet) {
			modifying = computeIsModifying();
			modifyingSet = true;
		}
	    return modifying;
	}

	/**
	 * Computes if the rule is modifying or not.
	 */
	private boolean computeIsModifying() {
		boolean result = this.getEraserEdges().length > 0 || this.getEraserNodes().length > 0 || hasMergers() || hasCreators();
		if (!result) {
			result = hasModifyingSubRules(this);
		}
		return result;
	}

	/**
	 * Computes if a given condition has modifying rules as sub-conditions.
	 */
	private boolean hasModifyingSubRules(Condition condition) {
		boolean result = false;
		for (Condition subCondition : condition.getSubConditions()) {
			if (subCondition instanceof Rule) {
				result = ((Rule) subCondition).isModifying();
			} else {
				result = hasModifyingSubRules(subCondition);
			}
			if (result) {
				break;
			}
		}
		return result;
	}

    /**
	 * Indicates if the rule creates any nodes or edges.
	 */
	public boolean hasCreators() {
		if (! hasCreatorsSet) {
			hasCreators = computeHasCreators();
			hasCreatorsSet = true;
		}
		return hasCreators;
	}

	private boolean computeHasCreators() {
		boolean result = getCreatorNodes().length + getCreatorEdges().length > 0;
		if (!result) {
			result = hasCreatingSubRules(this);
		}
		return result;
	}

	/**
	 * Computes if a given condition has creating rules as sub-conditions.
	 */
	private boolean hasCreatingSubRules(Condition condition) {
		boolean result = false;
		for (Condition subCondition: condition.getSubConditions()) {
			if (subCondition instanceof Rule) {
				result = ((Rule) subCondition).hasCreators();
			} else {
				result = hasCreatingSubRules(subCondition);
			}
			if (result) {
				break;
			}
		}
		return result;
	}

	/** Returns the eraser (i.e., LHS-only) edges. */
    final Edge[] getEraserEdges() {
    	if (eraserEdges == null) {
    		eraserEdges = computeEraserEdges();
    	}
		return eraserEdges;
	}

    /**
	 * Computes the eraser (i.e., LHS-only) edges.
	 */
	private Edge[] computeEraserEdges() {
		testFixed(true);
	    Set<Edge> eraserEdgeSet = new HashSet<Edge>(lhs().edgeSet());
	    eraserEdgeSet.removeAll(getMorphism().edgeMap().keySet());
	    // also remove the incident edges of the lhs-only nodes
	    for (Node eraserNode: getEraserNodes()) {
	        eraserEdgeSet.removeAll(lhs().edgeSet(eraserNode));
	    }
	    return eraserEdgeSet.toArray(new Edge[0]);
	}

	/** Returns the eraser edges that are not themselves anchors. */
	final Edge[] getEraserNonAnchorEdges() {
		if (eraserNonAnchorEdges == null) {
			eraserNonAnchorEdges = computeEraserNonAnchorEdges();
		}
		return eraserNonAnchorEdges;
	}

	/**
	 * Computes the array of creator edges that are not themselves anchors.
	 */
	private Edge[] computeEraserNonAnchorEdges() {
		Set<Edge> eraserNonAnchorEdgeSet = new HashSet<Edge>(Arrays.asList(getEraserEdges()));
		eraserNonAnchorEdgeSet.removeAll(Arrays.asList(anchor()));
		return eraserNonAnchorEdgeSet.toArray(new Edge[0]);
	}

	/**
	 * Returns the LHS nodes that are not mapped to the RHS.
	 */
	final Node[] getEraserNodes() {
		if (eraserNodes == null) {
			eraserNodes = computeEraserNodes();
		}
		return eraserNodes;
	}

	/**
	 * Computes the eraser (i.e., lhs-only) nodes.
	 */
	private Node[] computeEraserNodes() {
		testFixed(true);
	    Set<Node> eraserNodeSet = new HashSet<Node>(lhs().nodeSet());
	    eraserNodeSet.removeAll(getMorphism().nodeMap().keySet());
//	    eraserNodeSet.removeAll(getCoRootMap().nodeMap().values());
	    return eraserNodeSet.toArray(new Node[0]);
	}

	NodeEdgeMap getCoRootMap() {
		if (coRootMap == null) {
			testFixed(true);
			coRootMap = new NodeEdgeHashMap();
		}
		return coRootMap;
	}
    /**
     * Returns the creator edges between reader nodes.
     */
    final Edge[] getSimpleCreatorEdges() {
        if (simpleCreatorEdges == null) {
            simpleCreatorEdges = computeSimpleCreatorEdges();
        }
        return simpleCreatorEdges;
    }

    /**
     * Computes the creator edges between reader nodes.
     */
    private Edge[] computeSimpleCreatorEdges() {
        List<Edge> result = new ArrayList<Edge>();
        Set<Node> nonCreatorNodes = getCreatorMap().nodeMap().keySet();
        // iterate over all creator edges
        for (Edge edge: getCreatorEdges()) {
            // determine if this edge is simple
            if (nonCreatorNodes.containsAll(Arrays.asList(edge.ends()))) {
                result.add(edge);
            }
        }
        return result.toArray(new Edge[0]);
    }

    /**
     * Returns the creator edges that have at least one creator end.
     */
    public final Set<Edge> getComplexCreatorEdges() {
        if (complexCreatorEdges == null) {
            complexCreatorEdges = computeComplexCreatorEdges();
        }
        return complexCreatorEdges;
    }

    /**
     * Computes the creator edges that have at least one creator end.
     */
    private Set<Edge> computeComplexCreatorEdges() {
        Set<Edge> result = new HashSet<Edge>(Arrays.asList(getCreatorEdges()));
        result.removeAll(Arrays.asList(getSimpleCreatorEdges()));
        return result;
    }

    /**
     * Returns the RHS edges that are not images of an LHS edge.
     */
    final Edge[] getCreatorEdges() {
        if (creatorEdges == null) {
            creatorEdges = computeCreatorEdges();
        }
        return creatorEdges;
    }

    /**
     * Computes the creator (i.e., RHS-only) edges.
     */
    private Edge[] computeCreatorEdges() {
        Set<Edge> result = new HashSet<Edge>(rhs().edgeSet());
        result.removeAll(getMorphism().edgeMap().values());   
        result.removeAll(getCoRootMap().edgeMap().values());
        return result.toArray(new Edge[0]);
    }

	/**
	 * Returns the RHS nodes that are not images of an LHS node.
	 */
	final public Node[] getCreatorNodes() {
		if (creatorNodes == null) {
			creatorNodes = computeCreatorNodes();
		}
		return creatorNodes;
	}

	/**
	 * Computes the creator (i.e., RHS-only) nodes.
	 */
	private Node[] computeCreatorNodes() {
	    Set<Node> result = new HashSet<Node>(rhs().nodeSet());
	    result.removeAll(getMorphism().nodeMap().values());   
        result.removeAll(getCoRootMap().nodeMap().values());
		return result.toArray(new Node[0]);
	}

	/**
	 * Returns the variables that occur in creator edges.
	 * @see #getCreatorEdges()
	 */
	final String[] getCreatorVars() {
		if (creatorVars == null) {
			creatorVars = computeCreatorVars();
		}
		return creatorVars;
	}

	/**
	 * Computes the variables occurring in RHS edges.
	 */
	private String[] computeCreatorVars() {
		Set<String> creatorVarSet = new HashSet<String>();
	    for (int i = 0; i < getCreatorEdges().length; i++) {
	        Edge creatorEdge = getCreatorEdges()[i];
	        String creatorVar = RegExprLabel.getWildcardId(creatorEdge.label());
	        if (creatorVar != null) {
	            creatorVarSet.add(creatorVar);
	        }
	    }
	    return creatorVarSet.toArray(new String[0]);
	}

	/** 
	 * Returns a sub-graph of the RHS consisting of the creator nodes and
	 * the creator edges with their endpoints.
	 */
	final Graph getCreatorGraph() {
		if (creatorGraph == null) {
			creatorGraph = computeCreatorGraph();
		}
		return creatorGraph;
	}

	/**
	 * Computes a creator graph, consisting of the 
	 * creator nodes together with the creator edges and their endpoints. 
	 */
	private Graph computeCreatorGraph() {
		Graph result = rhs().newGraph();
		result.addNodeSet(Arrays.asList(this.getCreatorNodes()));
		result.addEdgeSet(Arrays.asList(this.getCreatorEdges()));
	    return result;
	}

	/** 
	 * Returns a partial map from the nodes of the creator graph (see {@link #getCreatorGraph()})
	 * that are not themselves creator nodes but are the ends of creator edges, to the
	 * corresponding nodes of the LHS.
	 */
	final NodeEdgeMap getCreatorMap() {
		if (creatorMap == null) {
			creatorMap = computeCreatorMap();
		}
		return creatorMap;
	}

	/**
	 * Computes a value for the creator map.
	 * The creator map maps the endpoints of creator edges
	 * that are not themselves creator nodes to one of their pre-images.
	 */
	private NodeEdgeMap computeCreatorMap() {
		// construct rhsOnlyMap
	    NodeEdgeMap result = new NodeEdgeHashMap();
	    Set<? extends Node> creatorNodes = getCreatorGraph().nodeSet();
	    for (Map.Entry<Node,Node> nodeEntry: getMorphism().nodeMap().entrySet()) {
	    	if (creatorNodes.contains(nodeEntry.getValue())) {
	    		result.putNode(nodeEntry.getValue(), nodeEntry.getKey());
	    	}
	    }
	    return result;
	}

	/**
	 * Returns a map from LHS nodes that are merged to those LHS nodes
	 * they are merged with.
	 */
	final Map<Node, Node> getMergeMap() {
		if (mergeMap == null) {
			mergeMap = computeMergeMap();
		}
		return mergeMap;
	}

	/**
	 * Computes the merge map, which maps each LHS node 
	 * that is merged with others
	 * to the LHS node it is merged with.
	 */
	private Map<Node, Node> computeMergeMap() {
		testFixed(true);
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

	/**
	 * Array of LHS edges that bind variables.
	 */
	final Edge[] getVarEdges() {
		if (varEdges == null) {
			varEdges = computeVarEdges();
		}
		return varEdges;
	}

	/**
	 * Computes the set of variable-binding edges occurring in the lhs.
	 */
	private Edge[] computeVarEdges() {
		return VarSupport.getVarEdges(lhs()).toArray(new Edge[0]);
	}
	
	/**
	 * Lazily creates and returns the anchor graph of this rule.
	 * The anchor graph is the smallest subgraph of the LHS that is
	 * necessary to apply the rule. This means it contains all eraser
	 * edges and all variables and nodes necessary for creation.
	 */
	Graph getAnchorGraph() {
		if (anchorGraph == null) {
			anchorGraph = computeAnchorGraph();
		}
		return anchorGraph;
	}
	
	/**
	 * Computes the anchor graph of this rule.
	 * @see #getAnchorGraph()
	 */
	private Graph computeAnchorGraph() {
		Graph result = lhs().newGraph();
		for (Element elem: anchor()) {
			if (elem instanceof Node) {
				result.addNode((Node) elem);
			} else {
				result.addEdge((Edge) elem);
			}
		}
//		for (Node rootImage: getRootMap().nodeMap().values()) {
//			result.addNode(rootImage);
//		}
//		for (Edge rootImage: getRootMap().edgeMap().values()) {
//			result.addEdge(rootImage);
//		}
		result.addEdgeSet(Arrays.asList(getEraserEdges()));
		return result;
	}

	/** 
	 * The parent rule of this rule; may be <code>null</code>, if this
	 * is a top-level rule.
	 */
	private SPORule parent;
	/** 
	 * The collection of direct sub-rules of this rules.
	 * Lazily created by {@link #getDirectSubRules()}.
	 */
	private Collection<SPORule> directSubRules;
	/** The nesting level of this rule. */
	private int[] level;
	/**
     * Indicates if this rule has node mergers.
     */
    private boolean hasMergers;
    /** Flag indicating if the {@link #hasMergers} has been computed. */
    private boolean hasMergersSet;
	/**
     * Indicates if this rule has creator edges or nodes.
     * @invariant <tt>hasCreators == ! ruleMorph.isSurjective()</tt>
     */
    private boolean hasCreators;
    /** Flag indicating if the {@link #hasCreators} has been computed. */
    private boolean hasCreatorsSet;
    /**
     * Indicates if this rule makes changes to a graph at all.
     */
    private boolean modifying;
    /**
     * Indicates if the {@link #modifying} variable has been computed
     */
    private boolean modifyingSet;
    /** 
     * The underlying production morphism.
     * @invariant ruleMorph : lhs --> rhs
     */
    private final Morphism morphism;
    /** 
     * This production rule's left hand side.
     * @invariant lhs != null
     */
    private Graph lhs;
    /** 
     * This production rule's right hand side.
     * @invariant rhs != null
     */
    private Graph rhs;
    /** Mapping from the context of this rule to the RHS. */
    private NodeEdgeMap coRootMap;
    /** 
     * Smallest subgraph of the left hand side that is necessary to
     * apply the rule.
     */
    private Graph anchorGraph;
    /**
     * A sub-graph of the production rule's right hand side,
     * consisting only of the fresh nodes and edges.
     */
    private Graph creatorGraph;
    /**
     * A map from the nodes of <tt>rhsOnlyGraph</tt> to <tt>lhs</tt>,
     * which is the restriction of the inverse of <tt>ruleMorph</tt> to <tt>rhsOnlyGraph</tt>.
     */
    private NodeEdgeMap creatorMap;
    /** 
     * The lhs nodes that are not ruleMorph keys
     * @invariant lhsOnlyNodes \subseteq lhs.nodeSet()
     */
    private Node[] eraserNodes;
    /** 
     * The lhs edges that are not ruleMorph keys
     * @invariant lhsOnlyEdges \subseteq lhs.edgeSet()
     */
    private Edge[] eraserEdges;
    /**
	 * The set of anchors of this rule.
	 */
	private Element[] anchor;
	/** 
     * The lhs edges that are not ruleMorph keys and are not anchors
     * @invariant lhsOnlyEdgeSet \subseteq lhs.edgeSet()
     */
    private Edge[] eraserNonAnchorEdges;
    /** 
     * The lhs edges containing bound variables.
     * @invariant lhsOnlyNonAnchorEdges = lhsOnlyEdgeSet \setminus anchors
     */
    private Edge[] varEdges;
    /**
     * The LHS nodes that do not have any incident edges in the LHS.
     */
    private Node[] isolatedNodes;
    /** 
     * The rhs nodes that are not ruleMorph images
     * @invariant rhsOnlyNodeSet \subseteq rhs.nodeSet()
     */
    private Node[] creatorNodes;
    
    /** 
     * The rhs edges that are not ruleMorph images
     */
    private Edge[] creatorEdges;
    /** 
     * The rhs edges that are not ruleMorph images but with all ends morphism images
     */
    private Edge[] simpleCreatorEdges;
    /** 
     * The rhs edges with at least one end not a morphism image
     */
    private Set<Edge> complexCreatorEdges;
    /**
     * Variables occurring in the rhsOnlyEdges
     */
    private String[] creatorVars;
    /**
     * A partial mapping from LHS nodes to RHS nodes, indicating
     * which nodes are merged and which nodes are deleted.
     */
    private Map<Node,Node> mergeMap;
    /**
     * The priority of this rule.
     */
    private int priority;
    /** The matcher for events of this rule. */
    private MatchStrategy<VarNodeEdgeMap> eventMatcher;
    
    /** Returns the current anchor factory for all rules. */
    public static AnchorFactory getAnchorFactory() {
        return anchorFactory;
    }

    /**
     * Sets the anchor factory for all rules.
     * Only affects rules created from this moment on.
     */
    public static void setAnchorFactory(AnchorFactory anchorFactory) {
        SPORule.anchorFactory = anchorFactory;
    }

//    /**
//     * Returns the total time doing transformation-related computations.
//     */
//    static public long getTransformingTime() {
//        return DefaultScenario.reporter.getTotalTime(DefaultScenario.GET_DERIVATIONS) - getMatchingTime();
//    }
    
    /**
     * Returns the total time doing matching-related computations.
     * This includes time spent in certificate calculation.
     */
    static public long getMatchingTime() {
        return SearchPlanStrategy.reporter.getTotalTime(SearchPlanStrategy.SEARCH_FIND);
    }

    /**
     *  Implementation of ParameterAspect stuff
     */
    private Map<Integer, Node> parameterNodeMap;
    	
    /** set map containing param_ids and the nodes **/
    public void setParameterMap(Map<Integer, Node> map) {
    	parameterNodeMap = map;
    }
    
    /** Let RuleEvent deal with this map being <code>null</code> **/
    public Map<Integer, Node> getParameterNodeMap() {
    	return parameterNodeMap;
    }
    
    
    /**
     * The factory used for creating rule anchors.
     */
    private static AnchorFactory anchorFactory = MinimalAnchorFactory.getInstance(); 
    /** Debug flag for the constructor. */
    private static final boolean PRINT = false;
    /** Handle for profiling {@link #newEvent(VarNodeEdgeMap, NodeFactory, boolean)} and related methods. */
    static public final int GET_EVENT = reporter.newMethod("getEvent");
}
