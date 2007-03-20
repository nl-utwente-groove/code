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
 * $Id: DefaultGraphCondition.java,v 1.1.1.1 2007-03-20 10:05:19 kastenberg Exp $
 */
package groove.trans;

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
import groove.graph.GraphFormatException;
import groove.graph.Label;
import groove.graph.Morphism;
import groove.graph.Node;
import groove.rel.VarEdge;
import groove.rel.VarGraph;
import groove.rel.VarMorphism;
import groove.util.Reporter;

/**
 * @author Arend Rensink
 * @version $Revision: 1.1.1.1 $
 */
public class DefaultGraphCondition extends DefaultMorphism implements GraphCondition {
    /**
     * Constructs a graph condition with given context, pattern target and name,
     * and initially empty negated predicate.
     */
    protected DefaultGraphCondition(VarGraph context, VarGraph target, NameLabel name, RuleFactory ruleFactory) {
        super(context, target);
		this.ruleFactory = ruleFactory;
        this.name = name;
        this.negConjunct = createGraphPredicate();
    }

    /**
     * Constructs a graph condition with given initial pattern morphism and name.
     */
    protected DefaultGraphCondition(Morphism pattern, NameLabel name, RuleFactory ruleFactory) {
        this((VarGraph) pattern.dom(), (VarGraph) pattern.cod(), name, ruleFactory);
        elementMap.putAll(pattern.elementMap());
    }

    /**
     * Constructs a closed graph condition with given target pattern and name.
     * The negative conjunct is initially empty.
     */
    protected DefaultGraphCondition(VarGraph target, NameLabel name, RuleFactory ruleFactory) {
        this((VarGraph) target.newGraph(), target, name, ruleFactory);
    }
    
    /**
     * Constructs a graph condition with given context, pattern target and name,
     * and initially empty negated predicate.
     */
    protected DefaultGraphCondition(VarGraph context, VarGraph target, RuleFactory ruleFactory) {
        this(context, target, null, ruleFactory);
    }

    /**
     * Constructs an anonymous graph condition with given initial pattern morphism.
     */
    protected DefaultGraphCondition(Morphism pattern, RuleFactory ruleFactory) {
        this(pattern, null, ruleFactory);
    }

    /**
     * Constructs an anonymous closed graph condition with given target pattern,
     * and initially empty negated conjunct.
     */
    protected DefaultGraphCondition(VarGraph context, RuleFactory ruleFactory) {
        this(context, (NameLabel) null, ruleFactory);
    }

    
    /**
     * Returns the rule factory of this graph condition. 
	 */
	protected RuleFactory getRuleFactory() {
		return ruleFactory;
	}

	/**
     * Calls <code>getNegPredicate().setOr(test)</code>,
     * and for all the conditions in <code>test</code> calls
     * {@link #addAndNot(GraphCondition)}.
     */
    public void setAndNot(GraphTest test) {
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
     * {@link #addNegation(Edge)} with the embardo edge, and if it
     * is a merge embargo, calle {@link #addInjection(Collection)} with
     * the injectively matchable nodes. If it is neither, adds the
     * condition to the complex negated conjunct.
     * @see #addInjection(Collection)
     * @see #addNegation(Edge)
     * @see #addComplexNegCondition(GraphCondition)
     */
    protected void addAndNot(GraphCondition condition) {
        if (condition instanceof EdgeEmbargo) {
            Edge edge = ((EdgeEmbargo) condition).getEmbargoEdge();
            // early detection of edge embargoes not implemented for variable edges
            if (! (edge instanceof VarEdge)) {
                addNegation(edge);
            } else {
                addComplexNegCondition(condition);
            }
        } else if (condition instanceof MergeEmbargo) {
            Set<Node> injection = new HashSet<Node>(Arrays.asList(((MergeEmbargo) condition).getNodes()));
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
    
    /** Adds a negative edge, i.e., an edge empargo, to this condition. */
    protected void addNegation(Edge negativeEdge) {
        if (negationMap == null) {
            negationMap = new HashMap<Node,Collection<Edge>>();
        }
        assert !(negativeEdge instanceof VarEdge) : "Variables not allowed in negative edge "+negativeEdge;
        // first add the negative edge to the negation map
        int arity = negativeEdge.endCount();
        for (int i = 0; i < arity; i++) {
            Node embargoEnd = negativeEdge.end(i);
            Collection<Edge> embargo = negationMap.get(embargoEnd);
            if (embargo == null) {
                embargo = new ArrayList<Edge>();
                negationMap.put(embargoEnd, embargo);
            }
            embargo.add(negativeEdge);
        }
    }

    /** Adds an injection constraint, i.e., a merge empargo, to this condition. */
    protected void addInjection(Collection<Node> injection) {
        if (injectionMap == null) {
            injectionMap = new HashMap<Node,Set<Node>>();
        }
        // first add the injection to the injection map
        for (Node injectionKey: injection) {
            Set<Node> injectiveSet = injectionMap.get(injectionKey);
            if (injectiveSet == null) {
                injectionMap.put(injectionKey, injectiveSet = new HashSet<Node>());
            }
            injectiveSet.addAll(injection);
            injectiveSet.remove(injectionKey);
        }
    }

    public GraphCondition setAndNot(Edge embargoEdge) {
        GraphCondition result = createEdgeEmbargo(embargoEdge); 
        setAndNot(result);
        return result;
    }
    
    public GraphCondition setAndDistinct(Node node1, Node node2) {
        GraphCondition result = createMergeEmbargo(node1, node2);
        setAndNot(result);
        return result;
    }

    public GraphCondition setAndDistinct(Node[] nodes) {
        if (nodes.length != 2) {
            throw new IllegalArgumentException("Merge embargo must be binary");
        }
        return setAndDistinct(nodes[0], nodes[1]);
    }

    /** Fixes the sub-predicate and this morphism. */
    public void setFixed() {
        getNegConjunct().setFixed();
        super.setFixed();
    }

    public GraphPredicate getNegConjunct() {
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
    public VarGraph getTarget() {
        return (VarGraph) cod();
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
    public boolean isClosed() {
        return getContext().isEmpty();
    }

    /**
     * Returns <code>getPattern().dom()</code>.
     */
    public VarGraph getContext() {
        return (VarGraph) dom();
    }

    /**
     * Returns <code>true</code> if {@link #hasMatching(Graph)} returns a non-<code>null</code>
     * result.
     */
    public boolean hasMatching(Graph graph) {
        testClosed();
		reporter.start(HAS_MATCHING);
		try {
			return createMatching(graph).hasTotalExtensions();
		} finally {
			reporter.stop();
		}
	}

    /**
	 * Returns <code>true</code> if {@link #hasMatching(VarMorphism)} returns
	 * a non-<code>null</code> result.
	 */
    public boolean hasMatching(VarMorphism subject) {
        reporter.start(HAS_MATCHING);
		try {
			Matching partialMatch = createMatching(subject);
			return partialMatch == null ? false : partialMatch
					.hasTotalExtensions();
		} finally {
			reporter.stop();
		}
    }

    /**
	 * If {@link #isClosed()} holds, returns a total matching from a given graph
	 * to this condition's target pattern, if one exists. Otherwise, throws an
	 * exception as per contract.
	 */
    public Matching getMatching(Graph graph) {
        testClosed();
		reporter.start(GET_MATCHING);
		try {
			return (Matching) createMatching(graph).getTotalExtension();
		} finally {
			reporter.stop();
		}
	}

    /**
	 * Creates an initial match using the inverse of <code>this</code>
	 * followed by <code>morph</code>, and attempts to extend that to a total
	 * morphism. The method reports success if there is no total extension that
	 * satisfies the negated conjunct.
	 * 
	 * @see Matching#getTotalExtension()
	 */
	public Matching getMatching(VarMorphism subject) {
		reporter.start(GET_MATCHING);
		try {
			Matching partialMatch = createMatching(subject);
			return partialMatch == null ? null : (Matching) partialMatch.getTotalExtension();
		} finally {
			reporter.stop();
		}
	}

    /**
	 * If {@link #isClosed()} holds, returns the set of total extensions of an
	 * initially empty matching to the given graph. Otherwise, throws an
	 * exception as per contract.
	 * 
	 * @see Matching#getTotalExtensions()
	 */
    public Collection<? extends Matching> getMatchingSet(Graph graph) {
        reporter.start(GET_MATCHING);
        try {
            testClosed();
        	return createMatching(graph).getTotalExtensions();
        } finally {
        	reporter.stop();
        }
    }

    /**
     * Creates an initial match using the inverse of <code>this</code> followed by
     * <code>morph</code>, and constructs the set of total extensions.
     * Returns the set of those total extensions that do not satisfies the sub-predicate.
     * @see Morphism#getTotalExtensions()
     */
    public Collection<? extends Matching> getMatchingSet(VarMorphism subject) {
        reporter.start(GET_MATCHING);
        try {
        	Matching partialMatch = createMatching(subject);
        	return partialMatch == null ? Collections.<Matching>emptySet() : partialMatch.getTotalExtensions();
        } finally {
        	reporter.stop();
        }
    }

    /**
     * If {@link #isClosed()} holds, returns an iterator over the total extensions
     * of an initially empty matching to the given graph.
     * Otherwise, throws an exception as per contract.
     */
    public Iterator<? extends Matching> getMatchingIter(Graph graph) {
        reporter.start(GET_MATCHING);
		try {
			testClosed();
			return createMatching(graph).getTotalExtensionsIter();
		} finally {
			reporter.stop();
		}
	}

    /**
	 * Creates an initial match using the inverse of <code>this</code>
	 * followed by <code>morph</code>, and constructs an iterator over the
	 * total extensions. Filters out thos matchings that satisfy the
	 * sub-predicate.
	 * 
	 * @see Morphism#getTotalExtensionsIter()
	 */
    public Iterator<? extends Matching> getMatchingIter(VarMorphism subject) {
		reporter.start(GET_MATCHING);
		try {
			Matching partialMatch = createMatching(subject);
			return partialMatch == null ? Collections.<Matching>emptySet().iterator()
					: partialMatch.getTotalExtensionsIter();
		} finally {
			reporter.stop();
		}
	}

    /**
	 * Creates an initial (partial) match using the inverse of <code>this</code>
	 * followed by <code>morph</code>, and attempts to extend that to a total
	 * morphism.
	 * 
	 * @see Morphism#getTotalExtension()
	 */
    public GraphConditionOutcome getOutcome(VarMorphism subject) {
        Matching partialMatch = createMatching(subject);
        Map<Matching,GraphPredicateOutcome> matchMap = partialMatch == null ? Collections.<Matching,GraphPredicateOutcome>emptyMap() : partialMatch.getTotalExtensionMap();
        return createOutcome(subject, matchMap);
    }
    
    /**
     * Two conditions are equivalent if they have the same structure up to isomorphism.
     * Since this is obviously too expensive to test here, we go the other way
     * and call two conditions equal only if they are the same object.
     */
    public boolean equals(Object obj) {
        return this == obj;
    }
    
    /**
     * In line with that choice for {@link #equals(Object)}, we defer
     * to {@link System#identityHashCode(java.lang.Object)}.
     */
    public int hashCode() {
        return System.identityHashCode(this);
    }
    
    /**
     * Callback method for creating the negated and complex negated conjuncts.
     * @see #DefaultGraphCondition(VarGraph, VarGraph, NameLabel, RuleFactory)
     * @see #addComplexNegCondition(GraphCondition)
     */
    protected GraphPredicate createGraphPredicate() {
        return new DefaultGraphPredicate(getTarget());
    }

    /**
     * Callback method to create an initial (partial) matching for a given subject
     * morphism. The matching goes from the target pattern of this condition to 
     * the subject's codomain.
     * This implementation builds the result upon an empty matching obtained from
     * {@link #createMatching(Graph)} by inverting the underlying morphism of 
     * this condition and concatenating the subject morphism.
     * Returns <code>null</code> if this fails due to inconsistent injectivity constraints
     * of <code>this</code> and <code>subject</code>.
     */
    protected Matching createMatching(VarMorphism subject) {
        try {
            Matching result = createMatching(subject.cod());
            constructInvertConcat(this, subject, result);
            for (Map.Entry<String,Label> varEntry: subject.getValuation().entrySet()) {
                String var = varEntry.getKey();
                if (getTarget().hasVar(var)) {
                    result.putVar(var, varEntry.getValue());
                }
            }
            return result;
        } catch (GraphFormatException exc) {
            return null;
        }
    }

    /**
     * Callback method to create an initial (empty) matching from this condition's
     * target pattern to a given graph.
     * This implementation returns a {@link DefaultMatching}.
     * @see #createMatching(VarMorphism)
     */
    protected Matching createMatching(Graph graph) {
    	return getRuleFactory().createMatching(this, graph);
//        return new DefaultMatching(this, graph, getRuleFactory());
    }
    
    /**
     * Creates and returns a failure result for a given subject morphism,
     * based on a given mapping from matchings of this condition's pattern
     * to successes of the sub-predicate.
     */
    protected GraphConditionOutcome createOutcome(final VarMorphism subject, final Map<Matching, GraphPredicateOutcome> matchingMap) {
        return new DefaultConditionOutcome(this, subject, matchingMap);
    }
    
    /**
     * Callback method to create a merge embargo.
     * @see #setAndDistinct(Node, Node)
     * @see #setAndDistinct(Node[])
     */
    protected MergeEmbargo createMergeEmbargo(Node node1, Node node2) {
        return new MergeEmbargo((VarGraph) cod(), node1, node2, getRuleFactory());
    }
    
    /**
     * Callback method to create a merge embargo.
     * @see #setAndNot(Edge)
     */
    protected EdgeEmbargo createEdgeEmbargo(Edge embargoEdge) {
        return new EdgeEmbargo((VarGraph) cod(), embargoEdge, getRuleFactory());
    }

    /**
     * Callback method to create a matching order.
     * Typically invoked once, at the first invocation of {@link #getMatchingSchedule()}.
     * This implementation retrieves its value from the matching order factory.
     * @see #getMatchingSchedule()
     * @see #getMatchingScheduleFactory()
     * For a (non-closed) graph condition, it is more efficient to start the matching at the edges
     * connected to the context.
     */
    protected List<Element> computeMatchingSchedule() {
        setFixed();
        return getMatchingScheduleFactory().newMatchingOrder(this);
    }

    /**
     * Returns the precomputed matching order for the elements of the target pattern. First creates
     * the order using {@link #computeMatchingSchedule()} if that has not been done.
     * @see #computeMatchingSchedule()
     */
    public List<Element> getMatchingSchedule() {
        if (matchingSchedule == null) {
            matchingSchedule = computeMatchingSchedule();
        }
        return matchingSchedule;
    }
    
    /**
     * Returns the fragment of the negated conjunct without the edge or merge embargoes. Since we
     * are checking for those already in the simulation, the search for matchings only has to regard
     * the complex negated conjunct. May be <code>null</code>, if only simple negative conditions
     * were added.
     * @see #getNegConjunct()
     * @see #getInjectionMap()
     * @see #getNegationMap()
     */
    protected GraphPredicate getComplexNegConjunct() {
        return complexNegConjunct;
    }
    
    /**
     * Indicates if there are any negative conditions more complex than 
     * injections and negtions.
     * Convenience method for <code>getComplexNegConjunct() != null</code>. 
     */
    protected boolean hasComplexNegConditions() {
        return complexNegConjunct != null;
    }
    
    /**
     * Returns the map from nodes to sets of injectively matchable nodes.
     */
    public Map<Node, Set<Node>> getInjectionMap() {
        return injectionMap;
    }

    /**
     * Returns the map from lhs nodes to incident negative edges.
     */
    public Map<Node, Collection<Edge>> getNegationMap() {
        return negationMap;
    }

    /**
     * Throws an {@link IllegalStateException} if the graph condition is not fixed.
     *
     */
    protected void testFixed() {
        if (! isFixed()) {
            throw new IllegalStateException("Graph condition should be fixed before invoking this method");
        }
    }

    /**
     * Throws an {@link IllegalArgumentException} if this predicate is not closed.
     */
    protected void testClosed() {
        if (! isClosed()) {
            throw new IllegalArgumentException("Method only allowed on closed predicate");
        }
    }
    
    /**
     * Sets the matching schedule factory.
     * Affects only the matching schedules created <i>after</i> this
     * method invocation, either in this condition or in conditions cloned from it.
     * @see #computeMatchingSchedule()
     */
    public void setMatchingScheduleFactory(MatchingScheduleFactory matchingScheduleFactory) {
        this.matchingScheduleFactory = matchingScheduleFactory;
    }
    
    /**
     * Returns the current matching schedule factory.
     * The factory should have been set previously by a call to {@link #setMatchingScheduleFactory(MatchingScheduleFactory)}.
     * If no matching schedule factory yet exists, one is created using
     * {@link #computeMatchingScheduleFactory()}.
     * @see #setMatchingScheduleFactory(MatchingScheduleFactory)
     * @see #computeMatchingScheduleFactory()
     * @see #computeMatchingSchedule()
     */
    protected MatchingScheduleFactory getMatchingScheduleFactory() {
    	if (matchingScheduleFactory == null) {
    		matchingScheduleFactory = computeMatchingScheduleFactory();
    	}
        return matchingScheduleFactory;
    }
    
    /**
     * Callback initialisation method for the matching schedule factory.
     * This implementation merely invokes {@link #createMatchingScheduleFactory()}.
     */
    protected MatchingScheduleFactory computeMatchingScheduleFactory() {
    	return createMatchingScheduleFactory();
    }
    
    /**
     * Callback factory method to create a matching schedule factory.
     * This implementation returns a {@linkplain IndegreeScheduleFactory}.
     */
    protected MatchingScheduleFactory createMatchingScheduleFactory() {
    	return new IndegreeScheduleFactory();
    }
    
    /**
     * The name of this condition. May be <code>code</code> null.
     */
    protected NameLabel name;
    /** 
     * The negated cunjunct of this graph condition.
     */
    private final GraphPredicate negConjunct;
    /** 
     * The fragment of the negated conjunct without edge and merge embargoes.
     */
    private GraphPredicate complexNegConjunct;

    /**
     * Mapping from codomain nodes to sets of other codomain nodes with which
     * they must be matched injectively.
     */
    private Map<Node,Set<Node>> injectionMap;
    
    /**
     * Mapping from codomain nodes to single or sets of incident codomain edges
     * that must be absent in the matching.
     */
    private Map<Node,Collection<Edge>> negationMap;
    /**
     * The fixed matching order for this graph condition.
     * Initially <code>null</code>; set by {@link #getMatchingSchedule()} upon its
     * first invocation.
     */
    private List<Element> matchingSchedule;
    /**
     * The strategy for constructing the matching order.
     */
    private MatchingScheduleFactory matchingScheduleFactory;

    /**
     * Factory instance for creating the correct simulation.
     */
    private final RuleFactory ruleFactory;

    /** Reporter instance for profiling this class. */
    static public final Reporter reporter = Reporter.register(GraphTest.class);
    /** Handle for profiling {@link #getMatching(Graph)} and related methods. */
    static public final int GET_MATCHING = reporter.newMethod("getMatching...");
    /** Handle for profiling {@link #hasMatching(Graph)} and related methods. */
    static public final int HAS_MATCHING = reporter.newMethod("hasMatching...");
}