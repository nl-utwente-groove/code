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
 * $Id: AbstractCondition.java,v 1.12 2007-11-26 21:17:27 rensink Exp $
 */
package groove.trans;

import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Label;
import groove.graph.Node;
import groove.graph.NodeEdgeHashMap;
import groove.graph.NodeEdgeMap;
import groove.graph.algebra.AlgebraEdge;
import groove.graph.algebra.ProductEdge;
import groove.graph.algebra.ProductNode;
import groove.graph.algebra.ValueNode;
import groove.match.ConditionSearchPlanFactory;
import groove.match.MatchStrategy;
import groove.rel.VarNodeEdgeHashMap;
import groove.rel.VarNodeEdgeMap;
import groove.rel.VarSupport;
import groove.util.Reporter;
import groove.view.FormatException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author Arend Rensink
 * @version $Revision: 1.12 $
 */
abstract public class AbstractCondition<M extends Match> implements Condition {
	/**
	 * Constructs a (named) graph condition based on a given graph to be matched
	 * and root map.
	 * 
	 * @param target
	 *            the graph to be matched
	 * @param rootMap
	 *            element map from the context to the anchor elements of
	 *            <code>target</code>; may be <code>null</code> if the
	 *            condition is ground
	 * @param name
	 *            the name of the condition; may be <code>null</code>
	 * @param properties
	 *            properties for matching the condition
	 */
	protected AbstractCondition(Graph target, NodeEdgeMap rootMap,
			NameLabel name, SystemProperties properties) {
		this.rootMap = rootMap;
		this.target = target;
		this.properties = properties;
		this.name = name;
	}

	/**
	 * Constructs a (named) ground graph condition based on a given target
	 * graph. The name may be <code>null</code>.
	 */
	protected AbstractCondition(Graph target, NameLabel name,
			SystemProperties properties) {
		this(target, null, name, properties);
	}

	/**
	 * Returns the properties set at construction time.
	 */
	public SystemProperties getProperties() {
		return properties;
	}
//
//	/** Sets the root map of this condition. */
//	void setRootMap(NodeEdgeMap rootMap) {
//		testFixed(false);
//		assert rootMap != null : String.format("Root map already set to %s",
//				rootMap);
//		this.rootMap = rootMap;
//	}

	public NodeEdgeMap getRootMap() {
		if (rootMap == null) {
			testFixed(true);
			rootMap = new NodeEdgeHashMap();
		}
		return rootMap;
	}

	public Set<String> getRootVars() {
		if (rootVars == null) {
			rootVars = new HashSet<String>();
			for (Edge rootEdge : getRootMap().edgeMap().keySet()) {
				rootVars.addAll(VarSupport.getAllVars(rootEdge));
			}
		}
		return rootVars;
	}

	/**
	 * Returns the target set at construction time.
	 */
	public Graph getTarget() {
		return target;
	}

	/**
	 * Returns the name set at construction time.
	 */
	public NameLabel getName() {
		return name;
	}

	/**
	 * Sets the name of this condition, if the condition is not fixed. The name
	 * is assumed to be as yet unset.
	 */
	public void setName(NameLabel name) {
		testFixed(false);
		assert this.name == null : String.format("Condition name already set to %s",
				name);
		this.name = name;
	}

	/**
	 * Delegates to <code>getRootMap().isEmpty()</code> as per contract.
	 */
	public boolean isGround() {
		return getRootMap().isEmpty();
	}

	/**
	 * This implementation tests for the use of attributes and the presence of
	 * isolated nodes.
	 * 
	 * @see #hasAttributes()
	 * @see SystemProperties#isAttributed()
	 */
	public void testConsistent() throws FormatException {
		String attributeKey = SystemProperties.ATTRIBUTES_KEY;
		String attributeProperty = getProperties().getProperty(attributeKey);
		if (getProperties().isAttributed()) {
			if (hasIsolatedNodes()) {
				throw new FormatException(
						"Condition tests isolated nodes, conflicting with \"%s=%s\"",
						attributeKey, attributeProperty);
			}
		} else if (hasAttributes()) {
			if (attributeProperty == null) {
				throw new FormatException(
						"Condition uses attributes, but \"%s\" not declared",
						attributeKey);
			} else {
				throw new FormatException(
						"Condition uses attributes, violating \"%s=%s\"",
						attributeKey, attributeProperty);
			}
		}
	}

	/**
	 * Returns <code>true</code> if the target graph of the condition contains
	 * {@link ValueNode}s, or the negative conjunct is attributed.
	 */
	private boolean hasAttributes() {
		boolean result = ValueNode.hasValueNodes(getTarget());
		if (result) {
			Iterator<AbstractCondition<?>> subConditionIter = getSubConditions().iterator();
			while (!result && subConditionIter.hasNext()) {
				result = subConditionIter.next().hasAttributes();
			}
		}
		return result;
	}

	/**
	 * Tests if the target graph of the condition contains nodes without
	 * incident edges.
	 */
	private boolean hasIsolatedNodes() {
		boolean result = false;
		// first test if the pattern target has isolated nodes
		Set<Node> freshTargetNodes = new HashSet<Node>(getTarget().nodeSet());
		freshTargetNodes.removeAll(getRootMap().nodeMap().values());
		Iterator<Node> nodeIter = freshTargetNodes.iterator();
		while (!result && nodeIter.hasNext()) {
			result = getTarget().edgeSet(nodeIter.next()).isEmpty();
		}
		if (!result) {
			// now recursively test the sub-conditions
			Iterator<AbstractCondition<?>> subConditionIter = getSubConditions().iterator();
			while (!result && subConditionIter.hasNext()) {
				result = subConditionIter.next().hasIsolatedNodes();
			}
		}
		return result;
	}

	public Collection<AbstractCondition<?>> getSubConditions() {
		if (subConditions == null) {
			subConditions = new ArrayList<AbstractCondition<?>>();
		}
		return subConditions;
	}

	public void addSubCondition(Condition condition) {
		testFixed(false);
		assert condition instanceof AbstractCondition : String.format("Condition %s should be an AbstractCondition",
				condition);
		getSubConditions().add((AbstractCondition<?>) condition);
	}

	/** Fixes the sub-predicate and this morphism. */
	public void setFixed() throws FormatException {
		if (!isFixed()) {
            fixed = true;
			getTarget().setFixed();
			for (AbstractCondition<?> subCondition : getSubConditions()) {
				subCondition.setFixed();
			}
		}
	}

	public boolean isFixed() {
		return fixed;
	}

	final public boolean hasMatch(Graph host) {
		return isGround() && getMatchIter(host, null).hasNext();
	}

	/**
	 * Returns an iterable wrapping a call to
	 * {@link #getMatchIter(Graph, NodeEdgeMap)}.
	 */
	public Iterable<M> getMatches(final Graph host, final NodeEdgeMap contextMap) {
		return new Iterable<M>() {
			public Iterator<M> iterator() {
				return getMatchIter(host, contextMap);
			}
		};
	}

	final public Iterator<M> getMatchIter(Graph host, NodeEdgeMap contextMap) {
		Iterator<M> result = null;
		reporter.start(GET_MATCHING);
		testFixed(true);
		// lift the pattern match to a pre-match of this condition's target
		final VarNodeEdgeMap anchorMap;
		if (contextMap == null) {
			testGround();
			anchorMap = EMPTY_ANCHOR_MAP;
		} else {
			anchorMap = createAnchorMap(contextMap);
		}
		if (anchorMap == null) {
			// the context map could not be lifted to this condition
			result = Collections.<M>emptySet().iterator();
		} else {
			result = computeMatchIter(host, getMatcher().getMatchIter(host, anchorMap));
		}
		reporter.stop();
		return result;
	}

	/**
	 * Returns an iterator over the matches for a given graph, based on a series
	 * of match maps for this condition.
	 */
	abstract Iterator<M> computeMatchIter(Graph host, Iterator<VarNodeEdgeMap> matchMaps);

	/**
	 * Factors given matching of the condition context through this condition's
	 * root map, to obtain a matching of {@link #getTarget()}.
	 * 
	 * @return a mapping that, concatenated after this condition's root map, is
	 *         a sub-map of <code>contextMap</code>; or <code>null</code>
	 *         if there is no such mapping.
	 */
	final VarNodeEdgeMap createAnchorMap(NodeEdgeMap contextMap) {
		VarNodeEdgeMap result = new VarNodeEdgeHashMap();
		for (Map.Entry<Node, Node> entry : getRootMap().nodeMap().entrySet()) {
		    if (!isAnchorable(entry.getKey())) {
		        continue;
		    }
			Node image = contextMap.getNode(entry.getKey());
			assert image != null : String.format("Context map %s does not contain image for root %s",
					contextMap,
					entry.getKey());
			if (image == null) {
				return null;
			} else {
				Node key = entry.getValue();
				// result already contains an image for nodeKey
				// if it is not the same as the one we want to insert now,
				// stop the whole thing; otherwise we're fine
				Node oldImage = result.putNode(key, image);
				if (oldImage != null && !oldImage.equals(image)) {
					return null;
				}
			}
		}
		for (Map.Entry<Edge, Edge> entry : getRootMap().edgeMap().entrySet()) {
		    if (!isAnchorable(entry.getKey())) {
                continue;
            }
			Edge image = contextMap.mapEdge(entry.getKey());
			assert image != null : String.format("Context map %s does not contain image for root %s",
					contextMap,
					entry.getKey());
			if (image == null) {
				return null;
			} else {
				Edge key = entry.getValue();
				// result already contains an image for nodeKey
				// if it is not the same as the one we want to insert now,
				// stop the whole thing; otherwise we're fine
				Edge oldImage = result.putEdge(key, image);
				if (oldImage != null && !oldImage.equals(image)) {
					return null;
				}
			}
		}
		if (contextMap instanceof VarNodeEdgeMap) {
			for (String var : getRootVars()) {
				Label image = ((VarNodeEdgeMap) contextMap).getVar(var);
				if (image == null) {
					return null;
				} else {
					result.putVar(var, image);
				}
			}
		} else if (!getRootVars().isEmpty()) {
			return null;
		}
		return result;
	}
    
	/** 
	 * Tests is a give node can serve proper anchor, in the sense that it is matched to an actual host graph node.
	 * This fails to hold for {@link ProductNode}s that are not {@link ValueNode}s.
	 */
    boolean isAnchorable(Node node) {
        return !(node instanceof ProductNode) || node instanceof ValueNode;
    }
    
    /** 
     * Tests is a give edge is a proper anchor, in the sense that it is matched to an actual host graph edge.
     * This fails to hold for {@link AlgebraEdge}s and {@link ProductEdge}s.
     */
    boolean isAnchorable(Edge edge) {
        return !(edge instanceof AlgebraEdge || edge instanceof ProductEdge);
    }

	/**
	 * Returns the precomputed matching order for the elements of the target
	 * pattern. First creates the order using {@link #createMatcher()} if that
	 * has not been done.
	 * 
	 * @see #createMatcher()
	 */
	final public MatchStrategy<VarNodeEdgeMap> getMatcher() {
		if (matchStrategy == null) {
			matchStrategy = createMatcher();
		}
		return matchStrategy;
	}

	/**
	 * Callback method to create a matching factory. Typically invoked once, at
	 * the first invocation of {@link #getMatcher()}. This implementation
	 * retrieves its value from {@link #getMatcherFactory()}.
	 */
	MatchStrategy<VarNodeEdgeMap> createMatcher() {
		testFixed(true);
		return getMatcherFactory().createMatcher(this);
	}

	/** Returns a matcher factory, tuned to the injectivity of this condition. */
	ConditionSearchPlanFactory getMatcherFactory() {
		return groove.match.ConditionSearchPlanFactory.getInstance(getProperties().isInjective());
	}

	/**
	 * Tests if the condition is fixed or not. Throws an exception if the
	 * fixedness does not coincide with the given value.
	 * 
	 * @param value
	 *            the expected fixedness state
	 * @throws IllegalStateException
	 *             if {@link #isFixed()} does not yield <code>value</code>
	 */
	public void testFixed(boolean value) throws IllegalStateException {
		if (isFixed() != value) {
			String message;
			if (value) {
				message = "Graph condition should be fixed in this state";
			} else {
				message = "Graph condition should not be fixed in this state";
			}
			throw new IllegalStateException(message);
		}
	}

	/**
	 * Tests if the condition can be used to tests on graphs rather than
	 * morphisms. This is the case if and only if the condition is ground (i.e.,
	 * the context graph is empty), as determined by {@link #isGround()}.
	 * 
	 * @throws IllegalStateException
	 *             if this condition is not ground.
	 * @see #isGround()
	 */
	void testGround() throws IllegalStateException {
		if (!isGround()) {
			throw new IllegalStateException(
					"Method only allowed on ground condition");
		}
	}

	@Override
	public String toString() {
		StringBuilder res = new StringBuilder(String.format("Condition %s: ",
				getName()));
		res.append(String.format("Target: %s", getTarget()));
		if (!getRootMap().isEmpty()) {
			res.append(String.format("%nRoot map: %s", getRootMap()));
		}
		if (!getSubConditions().isEmpty()) {
			res.append(String.format("%nSubconditions:"));
			for (Condition subCondition : getSubConditions()) {
				res.append(String.format("%n    %s", subCondition));
			}
		}
		return res.toString();
	}

	/**
	 * The name of this condition. May be <code>code</code> null.
	 */
	private NameLabel name;

	/**
	 * The fixed matching strategy for this graph condition. Initially
	 * <code>null</code>; set by {@link #getMatcher()} upon its first
	 * invocation.
	 */
	private MatchStrategy<VarNodeEdgeMap> matchStrategy;

	/** The collection of sub-conditions of this condition. */
	private Collection<AbstractCondition<?>> subConditions;

	/** Flag indicating if this condition is now fixed, i.e., unchangeable. */
	private boolean fixed;

	/**
	 * The pattern map of this condition, i.e., the element map from the context
	 * graph to the target graph.
	 */
	private NodeEdgeMap rootMap;

	/** Set of all variables occurring in root elements. */
	private Set<String> rootVars;

	/** The target graph of this morphism. */
	private final Graph target;

	/**
	 * Factory instance for creating the correct simulation.
	 */
	private final SystemProperties properties;

	/** Reporter instance for profiling this class. */
	static public final Reporter reporter = Reporter.register(Condition.class);

	/**
	 * Handle for profiling {@link #getMatches(Graph,NodeEdgeMap)} and related
	 * methods.
	 */
	static public final int GET_MATCHING = reporter.newMethod("getMatching...");
	/** Constant empty anchor map. */
	static final VarNodeEdgeMap EMPTY_ANCHOR_MAP = new VarNodeEdgeHashMap();
}