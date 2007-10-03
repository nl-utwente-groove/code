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
 * $Id: DefaultGraphCondition.java,v 1.30 2007-10-03 07:23:28 rensink Exp $
 */
package groove.trans;

import groove.graph.DefaultMorphism;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Label;
import groove.graph.Morphism;
import groove.graph.Node;
import groove.graph.NodeEdgeMap;
import groove.graph.algebra.ValueNode;
import groove.match.ConditionSearchPlanFactory;
import groove.match.MatchStrategy;
import groove.rel.VarNodeEdgeHashMap;
import groove.rel.VarNodeEdgeMap;
import groove.rel.VarSupport;
import groove.util.FilterIterator;
import groove.util.Reporter;
import groove.util.TransformIterator;
import groove.view.FormatException;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author Arend Rensink
 * @version $Revision: 1.30 $
 */
public class DefaultGraphCondition extends DefaultMorphism implements GraphCondition {
    /**
     * Constructs a (named) graph condition based on a given pattern morphism.
     * The name may be <code>null</code>.
     */
    protected DefaultGraphCondition(Morphism pattern, NameLabel name, SystemProperties properties) {
        super(pattern);
		this.properties = properties;
        this.name = name;
    }
    
    /**
     * Constructs a (named) ground graph condition based on a given pattern target.
     * and initially empty nested predicate.
     * The name may be <code>null</code>.
     */
    protected DefaultGraphCondition(Graph target, NameLabel name, SystemProperties properties) {
        super(target.newGraph(), target);
		this.properties = properties;
        this.name = name;
    }
//
//    /**
//     * Constructs a closed graph condition with given target pattern and name.
//     * The negative conjunct is initially empty.
//     */
//    protected DefaultGraphCondition(VarGraph target, NameLabel name, SystemProperties matchProperties) {
//        this((VarGraph) target.newGraph(), target, name, matchProperties);
//    }
//    
//    /**
//     * Constructs a graph condition with given context, pattern target and name,
//     * and initially empty negated predicate.
//     */
//    protected DefaultGraphCondition(VarGraph context, VarGraph target, SystemProperties properties) {
//        this(context, target, null, properties);
//    }

    /**
     * Constructs an anonymous graph condition with given initial pattern morphism.
     */
    protected DefaultGraphCondition(Morphism pattern, SystemProperties properties) {
        this(pattern, null, properties);
    }
//
//    /**
//     * Constructs an anonymous closed graph condition with given target pattern,
//     * and initially empty negated conjunct.
//     */
//    protected DefaultGraphCondition(VarGraph context, SystemProperties matchProperties) {
//        this(context, (NameLabel) null, matchProperties);
//    }

    public SystemProperties getProperties() {
		return properties;
	}
//
//	/**
//     * Returns the rule factory of this graph condition. 
//	 */
//	protected RuleFactory getRuleFactory() {
//		return getProperties().getFactory();
//	}

	/**
     * Calls <code>getNegPredicate().setOr(test)</code>,
     * and for all the conditions in <code>test</code> calls
     * {@link #addAndNot(GraphCondition)}.
     * @throws IllegalStateException if the condition is fixed at the time of invocation
     * @see #isFixed()
     */
    public void setAndNot(GraphTest test) throws IllegalStateException {
    	testFixed(false);
        getNegConjunct().setOr(test);
        if (test instanceof GraphCondition) {
            addAndNot((GraphCondition) test);
        } else {
        	for (GraphCondition condition: ((GraphPredicate) test).getConditions()) {
                addAndNot(condition);
            }
        }
    }

    /**
     * If the condition is an edge embargo, calls
     * {@link #addNegation(Edge)} with the embargo edge, and if it
     * is a merge embargo, calls {@link #addInjection(Set)} with
     * the injectively matchable nodes. If it is neither, adds the
     * condition to the complex negated conjunct.
     * @see #addInjection(Set)
     * @see #addNegation(Edge)
     * @see #addComplexNegCondition(GraphCondition)
     */
    protected void addAndNot(GraphCondition condition) {
        if (condition instanceof EdgeEmbargo) {
        	addNegation(((EdgeEmbargo) condition).getEmbargoEdge());
        } else if (condition instanceof MergeEmbargo) {
            Set<? extends Node> injection = condition.getPattern().elementMap().nodeMap().keySet();
            addInjection(injection);
        } else {
            addComplexNegCondition(condition);
        }
    }
    
    /** Adds a graph condition to the negated conjunct of this condition. */
    protected void addComplexNegCondition(GraphCondition condition) {
        if (complexNegConjunct == null) {
            complexNegConjunct = createGraphPredicate();
        }
        complexNegConjunct.setOr(condition);        
    }
    
    /** Adds a negative edge, i.e., an edge embargo, to this condition. */
    protected void addNegation(Edge negativeEdge) {
        if (negations == null) {
            negations = new HashSet<Edge>();
        }
        negations.add(negativeEdge);
    }

    /** Adds an injection constraint, i.e., a merge embargo, to this condition. */
    protected void addInjection(Set<? extends Node> injection) {
    	assert injection.size() == 2 : String.format("Injection %s should have size 2", injection);
        if (injections == null) {
            injections = new HashSet<Set<? extends Node>>();
        }
        injections.add(injection);
    }

    /** Fixes the sub-predicate and this morphism. */
    @Override
    public void setFixed() {
    	if (!isFixed()) {
			getNegConjunct().setFixed();
			super.setFixed();
		}
    }

    public DefaultGraphPredicate getNegConjunct() {
    	if (negConjunct == null) {
    		negConjunct = createGraphPredicate();
    	}
        return negConjunct;
    }

    /**
     * This implementation returns <code>this</code>.
     */
    public Morphism getPattern() {
        return this;
    }

    /**
     * This implementation returns <code>cod()</code>.
     */
    public Graph getTarget() {
        return cod();
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
        return getContext().isEmpty();
    }

    /**
     * Returns <code>getPattern().dom()</code>.
     */
    public Graph getContext() {
        return dom();
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
		Iterator<DefaultGraphCondition> subConditionIter = getNegConjunct().getConditions().iterator();
		while (!result && subConditionIter.hasNext()) {
			result = subConditionIter.next().hasAttributes();
		}
		return result;
	}

	/**
	 * Returns <code>true</code> if the target graph of the condition
	 * contains fresh nodes without incident edges, or one of the sub-conditions does.
	 */
	protected boolean hasIsolatedNodes() {
		boolean result = false;
		// first test if the pattern target has isolated nodes
		Set<Node> freshTargetNodes = new HashSet<Node>(getTarget().nodeSet());
		freshTargetNodes.removeAll(getPattern().nodeMap().values());
		Iterator<Node> nodeIter = freshTargetNodes.iterator();
		while (!result && nodeIter.hasNext()) {
			result = getTarget().edgeSet(nodeIter.next()).isEmpty();
		}
		// now recursively test the sub-conditions
		Iterator<DefaultGraphCondition> subConditionIter = getNegConjunct().getConditions().iterator();
		while (!result && subConditionIter.hasNext()) {
			result = subConditionIter.next().hasIsolatedNodes();
		}
		return result;
	}

	/**
	 * Returns <code>true</code> if {@link #matches(Graph)} returns a non-<code>null</code>
	 * result.
	 */
    public boolean matches(Graph graph) {
    	return getMapIter(graph, null).hasNext();
	}

    /**
	 * Returns <code>true</code> if {@link #getMapIter(Graph, NodeEdgeMap)}
	 * returns a non-empty iterator.
	 * @deprecated Use {@link #matches(Graph,NodeEdgeMap)} instead
	 */
    @Deprecated
	public boolean matches(groove.rel.VarMorphism subject) {
		return matches(subject.cod(), subject.elementMap());
	}

	/**
	 * Returns <code>true</code> if {@link #getMapIter(Graph, NodeEdgeMap)}
	 * returns a non-empty iterator.
	 */
    public boolean matches(Graph host, NodeEdgeMap matchMap) {
    	return getMapIter(host, matchMap).hasNext();
    }

    public Iterable<? extends Match> getMatches(final Graph host, final NodeEdgeMap patternMap) {
        Iterable<? extends Match> result;
        reporter.start(GET_MATCHING);
        testFixed(true);
        // list the pattern match to a pre-match of this condition's target
        final VarNodeEdgeMap anchorMap = liftPatternMap(patternMap);
        result = new Iterable<Match>() {
            public Iterator<Match> iterator() {
            	return new TransformIterator<VarNodeEdgeMap, Match>(createMapIter(host, anchorMap)) {
	                @Override
	                protected Match toOuter(VarNodeEdgeMap from) {
	                	return createMatch(from);
	                }
            	};
            }
        };
        reporter.stop();
        return result;
    }
    
    /** 
     * Returns an iterator over the mappings of this condition
     * into a given host graph, given a mapping of the pattern graph.
     * @param host the host graph we are matching into
     * @param patternMap a matching of the pattern of this condition; may
     * be <code>null</code> if the condition is ground.
     * @return an iterator over the matches of this graph condition into <code>host</code>
     * that are consistent with <code>patternMatch</code>
     * @throws IllegalArgumentException if <code>patternMatch</code> is <code>null</code>
     * and the condition is not ground, or if <code>patternMatch</code> is not compatible
     * with the pattern graph
     */
    public Iterator<VarNodeEdgeMap> getMapIter(Graph host, NodeEdgeMap patternMap) {
    	Iterator<VarNodeEdgeMap> result;
        reporter.start(GET_MATCHING);
        testFixed(true);
        VarNodeEdgeMap preMatch = liftPatternMap(patternMap);
		result = createMapIter(host, preMatch);
		reporter.stop();
		return result;
    }
    
    /** 
     * Factors given matching of {@link #getPattern()} through this condition's
     * morphism to obtain a matching of {@link #getTarget()}.
     * @return a mapping that, concatenated after this condition's morphism,
     * returns <code>patternMatch</code>; or <code>null</code> if there is
     * no such mapping.
     */
    final protected VarNodeEdgeMap liftPatternMap(NodeEdgeMap patternMap) {
    	VarNodeEdgeMap result;
    	if (patternMap == null) {
    		testGround();
    		result = null;
    	} else try {
    		result = new VarNodeEdgeHashMap();
    		constructInvertConcat(elementMap(), patternMap, result);
    		if (patternMap instanceof VarNodeEdgeMap) {
    			Map<String, Label> valuation = ((VarNodeEdgeMap) patternMap).getValuation();
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
    						patternMap,
    						getPattern()));
    	}
		return result;
    }
    
    /**
	 * Returns an iterator over the mappings of this condition into a given host
	 * graph, given a pre-matching of the pattern image.
	 * 
	 * @param host
	 *            the host graph we are mapping into
	 * @param anchorMap
	 *            a mapping of the image of {@link #getPattern()} under this
	 *            condition's morphism, into <code>host</code>; may be 
	 *            <code>null</code> if the condition is ground.
	 * @return an iterator over the matches of this graph condition into
	 *         <code>host</code> that are consistent with
	 *         <code>preMatch</code>
	 */
    final protected Iterator<VarNodeEdgeMap> createMapIter(final Graph host, NodeEdgeMap anchorMap) {
        Iterator<VarNodeEdgeMap> result = getMatchStrategy().getMatchIter(host, anchorMap);
        return filterMapIter(result, host);
    }

    /** 
     * Filters the results of an existing (raw) iterator so that
     * the resulting objects all satisfy the additional constraints of this condition.
     * @param matchMapIter the iterator to be filtered
     * @param host the host graph we are mapping into
     * @return an iterator of which all results satisfy {@link #satisfiesConstraints(Graph, VarNodeEdgeMap)}
     */
    final protected Iterator<VarNodeEdgeMap> filterMapIter(Iterator<VarNodeEdgeMap> matchMapIter, final Graph host) {
        Iterator<VarNodeEdgeMap> result = matchMapIter;
        if (hasConstraints()) {
            result = new FilterIterator<VarNodeEdgeMap>(result) {
                @Override
                protected boolean approves(Object obj) {
                    return satisfiesConstraints(host, (VarNodeEdgeMap) obj);
                }
            };
        }
        return result;
    }

    /**
     * Indicates that this graph condition imposes further constraints on
     * the validity of a match, besides embedding the condition's target graph.
     * @return <code>true</code> if {@link #hasComplexNegConjunct()} holds
     */
    protected boolean hasConstraints() {
    	return hasComplexNegConjunct();
    }
    
	/**
	 * Tests if a given candidate match satisfies the additional constraints
	 * of this graph condition.
	 * This includes the negative and universal application conditions, if any.
	 * @param host the graph into which a match is sought
	 * @param matchMap a candidate mapping from {@link #getTarget()} to <code>host</code>
	 * @return <code>true</code> if <code>matchMap</code> satisfies the additional constraints
	 */
	protected boolean satisfiesConstraints(final Graph host, VarNodeEdgeMap matchMap) {
		for (DefaultGraphCondition subCondition : getComplexNegConjunct()) {
			if (subCondition.getMapIter(host, matchMap).hasNext()) {
				return false;
			}
		}
        return true;
	}

    /** 
     * Callback factory method to create a match on the basis of
     * a mapping of this condition's target.
     * @param map the mapping, presumably of the elements of {@link #getTarget()}
     * into some host graph
     * @return a match constructed on the basis of <code>map</code>
     */
    protected Match createMatch(VarNodeEdgeMap map) {
    	return new DefaultConditionMatch(map);
    }
    
    /**
	 * If the condition is ground, returns a total matching from a given graph
	 * to this condition's target pattern, if one exists. Otherwise, throws an
	 * exception as per contract.
	 * @see #isGround()
	 */
    @Deprecated
    public Matching getMatching(Graph graph) {
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
	public Matching getMatching(groove.rel.VarMorphism subject) {
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
    public Collection<? extends Matching> getMatchingSet(Graph graph) {
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
    public Collection<? extends Matching> getMatchingSet(groove.rel.VarMorphism subject) {
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
    public Iterator<? extends Matching> getMatchingIter(Graph graph) {
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
    public Iterator<? extends Matching> getMatchingIter(groove.rel.VarMorphism subject) {
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
    	Iterator<? extends Match> matchIter = getMatches(host, patternMap).iterator();
    	Map<Match,GraphPredicateOutcome> matchMap = new HashMap<Match,GraphPredicateOutcome>();
    	while (matchIter.hasNext()) {
    		Match match = matchIter.next();
    		GraphPredicateOutcome negResultSet = getNegConjunct().getOutcome(host, ((DefaultConditionMatch) match).getElementMap());
    		matchMap.put(match, negResultSet);
    	}
        return createOutcome(host, patternMap, matchMap);
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
     * Callback method for creating the negated and complex negated conjuncts.
     * @see #DefaultGraphCondition(Graph, NameLabel, SystemProperties)
     * @see #addComplexNegCondition(GraphCondition)
     */
    protected DefaultGraphPredicate createGraphPredicate() {
        return new DefaultGraphPredicate(getTarget());
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
    protected Matching newMatcher(groove.rel.VarMorphism subject) {
        try {
            Matching result = newMatcher(subject.cod());
            constructInvertConcat(this, subject, result);
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
    	return new DefaultMatching(this, graph);
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
    protected GraphConditionOutcome createOutcome(Graph host, NodeEdgeMap elementMap, final Map<Match, GraphPredicateOutcome> matchingMap) {
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
     * Callback method to create a matching order.
     * Typically invoked once, at the first invocation of {@link #getMatchStrategy()}.
     * This implementation retrieves its value from the matching order factory.
     * @see #getMatchStrategy()
     * For a (non-closed) graph condition, it is more efficient to start the matching at the edges
     * connected to the context.
     */
    @Override
    protected MatchStrategy<VarNodeEdgeMap> createMatchStrategy() {
        setFixed();
        return getMatcherFactory().createMatcher(this);
    }

    /** Returns a matcher factory, tuned to the injectivity of this rule system. */
    protected ConditionSearchPlanFactory getMatcherFactory() {
        return groove.match.ConditionSearchPlanFactory.getInstance(getProperties().isInjective());
    }
    
	/**
     * Returns the fragment of the negated conjunct without the edge or merge embargoes. Since we
     * are checking for those already in the simulation, the search for matchings only has to regard
     * the complex negated conjunct. May be <code>null</code>, if only simple negative conditions
     * were added.
     * @see #getNegConjunct()
     * @see #getInjections()
     * @see #getNegations()
     */
    protected DefaultGraphPredicate getComplexNegConjunct() {
        return complexNegConjunct;
    }
    
    /**
     * Indicates if there are any negative conditions more complex than 
     * injections and negtions.
     * Convenience method for <code>getComplexNegConjunct() != null</code>. 
     */
    protected boolean hasComplexNegConjunct() {
        return complexNegConjunct != null;
    }
    
    /**
     * Returns the map from nodes to sets of injectively matchable nodes.
     */
    public Set<Set<? extends Node>> getInjections() {
        return injections;
    }

    /**
     * Returns the map from lhs nodes to incident negative edges.
     */
    public Set<Edge> getNegations() {
        return negations;
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
     * The negated conjunct of this graph condition.
     */
    private DefaultGraphPredicate negConjunct;
    /** 
     * The fragment of the negated conjunct without edge and merge embargoes.
     */
    private DefaultGraphPredicate complexNegConjunct;

    /**
     * Mapping from codomain nodes to sets of other codomain nodes with which
     * they must be matched injectively.
     */
    private Set<Set<? extends Node>> injections;
    
    /**
     * Mapping from codomain nodes to single or sets of incident codomain edges
     * that must be absent in the matching.
     */
    private Set<Edge> negations;
    /**
     * The fixed matching strategy for this graph condition.
     * Initially <code>null</code>; set by {@link #getMatchStrategy()} upon its
     * first invocation.
     */
    private MatchStrategy<VarNodeEdgeMap> matchStrategy;
    /** The variables occurring in edges of the target (i.e., the codomain). */
    private Set<String> targetVars;
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