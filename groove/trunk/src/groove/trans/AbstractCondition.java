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
 * $Id: AbstractCondition.java,v 1.2 2007-10-03 23:10:54 rensink Exp $
 */
package groove.trans;

import groove.graph.AbstractMorphism;
import groove.graph.DefaultMorphism;
import groove.graph.Graph;
import groove.graph.Label;
import groove.graph.Morphism;
import groove.graph.Node;
import groove.graph.NodeEdgeHashMap;
import groove.graph.NodeEdgeMap;
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
 * @version $Revision: 1.2 $
 */
abstract public class AbstractCondition<M extends Match> implements GraphCondition {
    /**
     * Constructs a (named) graph condition based on a given pattern morphism.
     * The name may be <code>null</code>.
     */
    protected AbstractCondition(Graph target, NodeEdgeMap patternMap, NameLabel name, SystemProperties properties) {
        this.patternMap = patternMap;
        this.target = target;
		this.properties = properties;
        this.name = name;
    }
    
    /**
     * Constructs a (named) ground graph condition based on a given target graph.
     * The name may be <code>null</code>.
     */
    protected AbstractCondition(Graph target, NameLabel name, SystemProperties properties) {
    	this(target, new NodeEdgeHashMap(), name, properties);
    }

    public SystemProperties getProperties() {
		return properties;
	}

    /**
     * This implementation returns <code>this</code>.
     * @deprecated use {@link #getPatternMap()} instead
     */
    @Deprecated
    public Morphism getPattern() {
        return new DefaultMorphism(getContext(), getTarget(), getPatternMap());
    }

    /**
     * This implementation returns <code>this</code>.
     */
    public NodeEdgeMap getPatternMap() {
        return patternMap;
    }

    /**
     * This implementation returns <code>cod()</code>.
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
     * Delegates to <code>getContext().isEmpty()</code> as per contract.
     */
    public boolean isGround() {
        return getPatternMap().isEmpty();
    }

    /**
     * Returns <code>getPattern().dom()</code>.
     */
    @Deprecated
    public Graph getContext() {
        return null;
    }

    /** 
     * This implementation tests for the use of attributes and the presence of isolated nodes.
     * @see #hasAttributes()
     * @see SystemProperties#isAttributed()
     */
	public void testConsistent() throws FormatException {
		String attributeKey = SystemProperties.ATTRIBUTES_KEY;
		String attributeProperty = getProperties().getProperty(attributeKey);
		if (getProperties().isAttributed()) {
			if (hasIsolatedNodes()) {
				throw new FormatException("Condition tests isolated nodes, conflicting with \"%s=%s\"", attributeKey, attributeProperty);
			}
		} else if (hasAttributes()) {
			if (attributeProperty == null) {
				throw new FormatException("Condition uses attributes, but \"%s\" not declared", attributeKey);
			} else {
				throw new FormatException("Condition uses attributes, violating \"%s=%s\"", attributeKey, attributeProperty);
			}
		}
	}

	/**
	 * Returns <code>true</code> if the target graph of the condition
	 * contains {@link ValueNode}s, or the negative conjunct is attributed.
	 */
	protected boolean hasAttributes() {
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
	 * Tests if the target graph of the condition
	 * contains nodes without incident edges.
	 */
	protected boolean hasIsolatedNodes() {
		boolean result = false;
		// first test if the pattern target has isolated nodes
		Set<Node> freshTargetNodes = new HashSet<Node>(getTarget().nodeSet());
		freshTargetNodes.removeAll(getPatternMap().nodeMap().values());
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

    public void addSubCondition(GraphCondition condition) {
        assert condition instanceof AbstractCondition : String.format("Condition %s should be an AbstractCondition", condition);
        getSubConditions().add((AbstractCondition<?>) condition);
    }

    /** Fixes the sub-predicate and this morphism. */
    public void setFixed() {
        if (!isFixed()) {
        	getTarget().setFixed();
            for (AbstractCondition<?> subCondition: getSubConditions()) {
                subCondition.setFixed();
            }
            fixed = true;
        }
    }

    public boolean isFixed() {
		return fixed;
	}

	/**
     * Calls <code>getNegPredicate().setOr(test)</code>,
     * and for all the conditions in <code>test</code> calls
     * {@link #addSubCondition(GraphCondition)}.
     * @throws IllegalStateException if the condition is fixed at the time of invocation
     * @see #isFixed()
     */
    @Deprecated
    public void setAndNot(GraphTest test) throws IllegalStateException {
        testFixed(false);
        if (test instanceof GraphCondition) {
            addSubCondition((GraphCondition) test);
        } else {
            for (GraphCondition condition: ((GraphPredicate) test).getConditions()) {
                addSubCondition(condition);
            }
        }
    }

    @Deprecated
    public GraphPredicate getNegConjunct() {
        throw new IllegalStateException();
    }
    
    /**
     * Delegates to {@link #matches(Graph, NodeEdgeMap)} with <code>null</code> as second parameter.
     */
    final public boolean matches(Graph graph) {
    	return matches(graph, null);
	}

    /**
	 * @deprecated Use {@link #matches(Graph,NodeEdgeMap)} instead
	 */
    @Deprecated
	final public boolean matches(groove.rel.VarMorphism subject) {
		return matches(subject.cod(), subject.elementMap());
	}

	/**
	 * Returns <code>true</code> if {@link #getMatches(Graph, NodeEdgeMap)}
	 * reports a match.
	 */
    final public boolean matches(Graph host, NodeEdgeMap matchMap) {
    	return getMatches(host, matchMap).iterator().hasNext();
    }

    public Iterable<M> getMatches(final Graph host, final NodeEdgeMap contextMap) {
		return new Iterable<M>() {
			public Iterator<M> iterator() {
				return getMatchIter(host, contextMap);
			}
		};
	}

    abstract public Iterator<M> getMatchIter(Graph host, NodeEdgeMap contextMap);
    
    /** 
     * Factors given matching of the condition context through this condition's
     * pattern map, to obtain a matching of {@link #getTarget()}.
     * @return a mapping that, concatenated after this condition's morphism,
     * returns <code>patternMatch</code>; or <code>null</code> if there is
     * no such mapping.
     */
    final protected VarNodeEdgeMap getAnchorMap(NodeEdgeMap contextMap) {
    	VarNodeEdgeMap result;
    	if (contextMap == null) {
    		testGround();
    		result = null;
    	} else try {
    		result = new VarNodeEdgeHashMap();
    		AbstractMorphism.constructInvertConcat(getPatternMap(), contextMap, result);
    		if (contextMap instanceof VarNodeEdgeMap) {
    			Map<String, Label> valuation = ((VarNodeEdgeMap) contextMap).getValuation();
    			for (Map.Entry<String, Label> varEntry : valuation.entrySet()) {
    				String var = varEntry.getKey();
    				if (getTargetVars().contains(var)) {
    					result.putVar(var, varEntry.getValue());
    				}
    			}
    		}
    	} catch (FormatException exc) {
    		throw new IllegalArgumentException(
    				String.format("Pattern match %s incompatible with pattern %s",
    						contextMap,
    						getPatternMap()));
    	}
		return result;
    }
//    
//    /**
//	 * Returns an iterator over the mappings of this condition into a given host
//	 * graph, given a pre-matching of the pattern image.
//	 * 
//	 * @param host
//	 *            the host graph we are mapping into
//	 * @param anchorMap
//	 *            a mapping of the image of {@link #getPattern()} under this
//	 *            condition's morphism, into <code>host</code>; may be 
//	 *            <code>null</code> if the condition is ground.
//	 * @return an iterator over the matches of this graph condition into
//	 *         <code>host</code> that are consistent with
//	 *         <code>preMatch</code>
//	 */
//    final protected Iterator<VarNodeEdgeMap> createMatchMapIter(Graph host, NodeEdgeMap anchorMap) {
//        Iterator<VarNodeEdgeMap> result = getMatchStrategy().getMatchIter(host, anchorMap);
//        return filterMapIter(result, host);
//    }
//
//
//    /**
//     * Indicates whether this graph condition imposes further constraints on
//     * the validity of a match map, besides embedding the condition's target graph.
//     */
//    protected boolean hasConstraints() {
//    	return false;
//    }
    
	/**
	 * Tests if a given candidate match satisfies the additional constraints
	 * of this graph condition.
	 * This does <i>not</i> include the sub-conditions!
	 * @param host the graph into which a match is sought
	 * @param matchMap a candidate mapping from {@link #getTarget()} to <code>host</code>
	 * @return <code>true</code> if <code>matchMap</code> satisfies the additional constraints
	 */
	protected boolean satisfiesConstraints(Graph host, VarNodeEdgeMap matchMap) {
        return true;
	}

    /**
	 * If the condition is ground, returns a total matching from a given graph
	 * to this condition's target pattern, if one exists. Otherwise, throws an
	 * exception as per contract.
	 * @see #isGround()
	 */
    @Deprecated
    final public Matching getMatching(Graph graph) {
		Matching result;
		reporter.start(GET_MATCHING);
        testGround();
        testFixed(true);
        result = (Matching) newMatcher(graph).getTotalExtension();
        reporter.stop();
		return result;
	}

    /**
	 * Creates an initial match using the inverse of <code>this</code>
	 * followed by <code>morph</code>, and attempts to extend that to a total
	 * morphism. The method reports success if there is no total extension that
	 * satisfies the negated conjunct.
	 * 
	 * @see Matching#getTotalExtension()
	 */
    @Deprecated
	final public Matching getMatching(groove.rel.VarMorphism subject) {
		Matching result;
		reporter.start(GET_MATCHING);
        testFixed(true);
		Matching partialMatch = newMatcher(subject);
		result = partialMatch == null ? null
				: (Matching) partialMatch.getTotalExtension();
		reporter.stop();
		return result;
	}

    /**
	 * If the condition is ground, returns the set of total extensions of an
	 * initially empty matching to the given graph. Otherwise, throws an
	 * exception as per contract.
	 * @see #isGround()
	 * @see Matching#getTotalExtensions()
	 */
    @Deprecated
    final public Collection<? extends Matching> getMatchingSet(Graph graph) {
    	Collection <? extends Matching> result;
        reporter.start(GET_MATCHING);
		testGround();
		testFixed(true);
		result = newMatcher(graph).getTotalExtensions();
		reporter.stop();
		return result;
    }

    /**
	 * Creates an initial match using the inverse of <code>this</code>
	 * followed by <code>morph</code>, and constructs the set of total
	 * extensions. Returns the set of those total extensions that do not
	 * satisfies the sub-predicate.
	 * 
	 * @see Morphism#getTotalExtensions()
	 */
    @Deprecated
    final public Collection<? extends Matching> getMatchingSet(groove.rel.VarMorphism subject) {
    	Collection <? extends Matching> result;
        reporter.start(GET_MATCHING);
		testFixed(true);
		Matching partialMatch = newMatcher(subject);
		result = partialMatch == null ? Collections.<Matching> emptySet()
				: partialMatch.getTotalExtensions();
		reporter.stop();
		return result;
    }

    /**
	 * If the condition is ground, returns an iterator over the total extensions
	 * of an initially empty matching to the given graph. Otherwise, throws an
	 * exception as per contract.
	 * 
	 * @see #isGround()
	 */
    @Deprecated
    final public Iterator<? extends Matching> getMatchingIter(Graph graph) {
    	Iterator<? extends Matching> result;
        reporter.start(GET_MATCHING);
		testFixed(true);
		testGround();
		result = newMatcher(graph).getTotalExtensionsIter();
		reporter.stop();
		return result;
	}

    /**
	 * Creates an initial match using the inverse of <code>this</code>
	 * followed by <code>morph</code>, and constructs an iterator over the
	 * total extensions. Filters out thos matchings that satisfy the
	 * sub-predicate.
	 * 
	 * @see Morphism#getTotalExtensionsIter()
	 */
    @Deprecated
    final public Iterator<? extends Matching> getMatchingIter(groove.rel.VarMorphism subject) {
    	Iterator<? extends Matching> result;
		reporter.start(GET_MATCHING);
		testFixed(true);
		Matching partialMatch = newMatcher(subject);
		result = partialMatch == null ? Collections.<Matching>emptySet().iterator()
				: partialMatch.getTotalExtensionsIter();
		reporter.stop();
		return result;
	}

    /**
	 * Creates an outcome of this graph condition for a given host graph,
	 * based on a given initial matching of the pattern graph.
	 */
    public GraphConditionOutcome getOutcome(Graph host, NodeEdgeMap patternMap) {
    	throw new UnsupportedOperationException();
//    	Iterator<? extends Match> matchIter = getMatches(host, patternMap).iterator();
//    	Map<Match,GraphPredicateOutcome> matchMap = new HashMap<Match,GraphPredicateOutcome>();
//    	while (matchIter.hasNext()) {
//    		Match match = matchIter.next();
//    		GraphPredicateOutcome negResultSet = getNegConjunct().getOutcome(host, ((ExistentialMatch) match).matchMap());
//    		matchMap.put(match, negResultSet);
//    	}
//        return createOutcome(host, patternMap, matchMap);
    }

    /**
     * Two conditions are equivalent if they have the same structure up to isomorphism.
     * Since this is obviously too expensive to test here, we go the other way
     * and call two conditions equal only if they are the same object.
     */
    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }
    
    /**
     * In line with that choice for {@link #equals(Object)}, we defer
     * to {@link System#identityHashCode(java.lang.Object)}.
     */
    @Override
    public int hashCode() {
    	if (! identityHashCodeSet) {
    		identityHashCode = System.identityHashCode(this);
    		identityHashCodeSet = true;
    	}
    	return identityHashCode;
    }
    
    /**
     * Callback method to create an initial (partial) matching for a given subject
     * morphism. The matching goes from the target pattern of this condition to 
     * the subject's codomain.
     * This implementation builds the result upon a given subject matching of the context,
     * by inverting the underlying morphism of this condition and concatenating the subject morphism.
     * Returns <code>null</code> if this fails due to inconsistent injectivity constraints
     * of <code>this</code> and <code>subject</code>.
     */
    @Deprecated
    final protected Matching newMatcher(groove.rel.VarMorphism subject) {
        try {
            Matching result = newMatcher(subject.cod());
            AbstractMorphism.constructInvertConcat(getPatternMap(), subject, result);
            for (Map.Entry<String,Label> varEntry: subject.getValuation().entrySet()) {
                String var = varEntry.getKey();
                if (getTargetVars().contains(var)) {
                    result.putVar(var, varEntry.getValue());
                }
            }
            return result;
        } catch (FormatException exc) {
            return null;
        }
    }

    /**
     * Callback method to create an initial (empty) matching from this condition's
     * target pattern to a given graph.
     * This implementation returns a {@link DefaultMatching}.
     * @see #newMatcher(groove.rel.VarMorphism)
     * @deprecated use {@link #getMatchStrategy()}
     */
    @Deprecated
    protected DefaultMatching newMatcher(Graph graph) {
        throw new IllegalStateException();
    }
    
    /** Returns the set of variables in the target graph. */
    private Set<String> getTargetVars() {
        if (targetVars == null) {
            targetVars = VarSupport.getAllVars(getTarget());
        }
        return targetVars;
    }
    /**
     * Creates and returns a failure result for a given subject morphism,
     * based on a given mapping from matchings of this condition's pattern
     * to successes of the sub-predicate.
     */
    protected GraphConditionOutcome createOutcome(Graph host, NodeEdgeMap elementMap, Map<Match, GraphPredicateOutcome> matchingMap) {
        return new DefaultConditionOutcome(this, host, elementMap, matchingMap);
    }

    /**
     * Returns the precomputed matching order for the elements of the target pattern. First creates
     * the order using {@link #createMatchStrategy()} if that has not been done.
     * @see #createMatchStrategy()
     */
    final public MatchStrategy<VarNodeEdgeMap> getMatchStrategy() {
        if (matchStrategy == null) {
            matchStrategy = createMatchStrategy();
        }
        return matchStrategy;
    }

    /**
     * Callback method to create a matching factory.
     * Typically invoked once, at the first invocation of {@link #getMatchStrategy()}.
     * This implementation retrieves its value from {@link #getMatcherFactory()}.
     */
    protected MatchStrategy<VarNodeEdgeMap> createMatchStrategy() {
        setFixed();
        return getMatcherFactory().createMatcher(this);
    }

    /** Returns a matcher factory, tuned to the injectivity of this condition. */
    protected ConditionSearchPlanFactory getMatcherFactory() {
        return groove.match.ConditionSearchPlanFactory.getInstance(getProperties().isInjective());
    }

    /**
     * Tests if the condition is fixed or not.
     * Throws an exception if the fixedness does not coincide with the given value.
     * @param value the expected fixedness state
     * @throws IllegalStateException if {@link #isFixed()} does not yield <code>value</code>
     */
    protected void testFixed(boolean value) throws IllegalStateException {
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
     * Tests if the condition can be used to tests on graphs rather than morphisms.
     * This is the case if and only if the condition is ground (i.e., the
     * context graph is empty), as determined by {@link #isGround()}.
     * @throws IllegalStateException if this condition is not ground.
     * @see #isGround()
     */
    private void testGround() throws IllegalStateException {
        if (! isGround()) {
            throw new IllegalStateException("Method only allowed on ground condition");
        }
    }
    
    /**
     * The name of this condition. May be <code>code</code> null.
     */
    protected NameLabel name;
    /**
     * The fixed matching strategy for this graph condition.
     * Initially <code>null</code>; set by {@link #getMatchStrategy()} upon its
     * first invocation.
     */
    private MatchStrategy<VarNodeEdgeMap> matchStrategy;
    /** The variables occurring in edges of the target (i.e., the codomain). */
    private Set<String> targetVars;
    /** The collection of sub-conditions of this condition. */
    private Collection<AbstractCondition<?>> subConditions;
	/**
	 * Flag indicating that {@link #identityHashCode} has been computed
	 * and assigned.
	 */
	private boolean identityHashCodeSet;
	/**
	 * Hash code based on the identity, rather than the content, of
	 * the event.
	 */
    private int identityHashCode;
    /** Flag indicating if this condition is now fixed, i.e., unchangeable. */
    boolean fixed;
    /** 
     * The pattern map of this condition, i.e., the element
     * map from the context graph to the target graph.
     */
    private final NodeEdgeMap patternMap;
    /** The target graph of this morphism. */
    private final Graph target;
    /**
     * Factory instance for creating the correct simulation.
     */
    private final SystemProperties properties;
    
    /** Reporter instance for profiling this class. */
    static public final Reporter reporter = Reporter.register(GraphTest.class);
    /** Handle for profiling {@link #getMatches(Graph,NodeEdgeMap)} and related methods. */
    static public final int GET_MATCHING = reporter.newMethod("getMatching...");
    /** Handle for profiling {@link #matches(Graph)} and related methods. */
    static public final int HAS_MATCHING = reporter.newMethod("hasMatching...");
}