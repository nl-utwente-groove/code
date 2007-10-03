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
 * $Id: DefaultGraphCondition.java,v 1.33 2007-10-03 23:10:53 rensink Exp $
 */
package groove.trans;

import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Morphism;
import groove.graph.Node;
import groove.graph.NodeEdgeMap;
import groove.rel.VarNodeEdgeMap;
import groove.util.Reporter;
import groove.util.TransformIterator;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Arend Rensink
 * @version $Revision: 1.33 $
 */
@Deprecated
public class DefaultGraphCondition extends AbstractCondition<ExistsMatch> {
    /**
     * Constructs a (named) graph condition based on a given pattern morphism.
     * The name may be <code>null</code>.
     */
    protected DefaultGraphCondition(Morphism pattern, NameLabel name, SystemProperties properties) {
        super(pattern.cod(), pattern.elementMap(), name, properties);
    }
    
    /**
     * Constructs a (named) ground graph condition based on a given pattern target.
     * and initially empty nested predicate.
     * The name may be <code>null</code>.
     */
    protected DefaultGraphCondition(Graph target, NameLabel name, SystemProperties properties) {
        super(target, name, properties);
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
    public void addAndNot(GraphCondition condition) {
        addSubCondition(condition);
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
    @Override
    public void addSubCondition(GraphCondition condition) {
        super.addSubCondition(condition);
        if (condition instanceof EdgeEmbargo) {
            addNegation(((EdgeEmbargo) condition).getEmbargoEdge());
        } else if (condition instanceof MergeEmbargo) {
            Set<? extends Node> injection = condition.getPatternMap().nodeMap().keySet();
            addInjection(injection);
        } else {
            addComplexNegCondition(condition);
        }
    }

    /** Adds a graph condition to the negated conjunct of this condition. */
    protected void addComplexNegCondition(GraphCondition condition) {
        getComplexConjunct().setOr(condition);        
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
            for (GraphCondition condition: getSubConditions()) {
                if (condition instanceof DefaultGraphCondition) {
                    ((DefaultGraphCondition) condition).setFixed();
                }
            }
			super.setFixed();
		}
    }

	@Override
    public Iterator<ExistsMatch> getMatchIter(final Graph host, final NodeEdgeMap contextMap) {
        Iterator<ExistsMatch> result;
        reporter.start(GET_MATCHING);
        testFixed(true);
        // list the pattern match to a pre-match of this condition's target
        final VarNodeEdgeMap anchorMap = getAnchorMap(contextMap);
        result = new TransformIterator<VarNodeEdgeMap, ExistsMatch>(createMapIter(host, anchorMap)) {
	                @Override
	                protected ExistsMatch toOuter(VarNodeEdgeMap from) {
	                	return getMatch(host, from);
	                }
            	};
        reporter.stop();
        return result;
    }

    /** 
     * Callback factory method to create a match on the basis of
     * a mapping of this condition's target.
     * @param matchMap the mapping, presumably of the elements of {@link #getTarget()}
     * into some host graph
     * @return a match constructed on the basis of <code>map</code>
     */
    protected ExistsMatch getMatch(Graph host, VarNodeEdgeMap matchMap) {
        ExistsMatch result = createMatch(matchMap);
        for (AbstractCondition<?> condition: getComplexConjunct()) {
            Iterable<? extends Match> subMatch = condition.getMatches(host, matchMap);
            if (subMatch.iterator().hasNext()) {
                return null;
//                result.addMatch(subMatch.iterator().next());
            } else {
//                return null;
            }
        }
        return result;
    }

    /** 
     * Returns an iterator over the mappings of this condition
     * into a given host graph, given a mapping of the pattern graph.
     * @param host the host graph we are matching into
     * @param contextMap a matching of the pattern of this condition; may
     * be <code>null</code> if the condition is ground.
     * @return an iterator over the matches of this graph condition into <code>host</code>
     * that are consistent with <code>patternMatch</code>
     * @throws IllegalArgumentException if <code>patternMatch</code> is <code>null</code>
     * and the condition is not ground, or if <code>patternMatch</code> is not compatible
     * with the pattern graph
     */
    public Iterator<VarNodeEdgeMap> getMapIter(Graph host, NodeEdgeMap contextMap) {
    	Iterator<VarNodeEdgeMap> result;
        reporter.start(GET_MATCHING);
        testFixed(true);
        VarNodeEdgeMap preMatch = getAnchorMap(contextMap);
		result = createMapIter(host, preMatch);
		reporter.stop();
		return result;
    }
    
    /**
	 * Returns an iterator over the mappings of this condition into a given host
	 * graph, given a pre-matching of the pattern image.
	 * 
	 * @param host
	 *            the host graph we are mapping into
	 * @param anchorMap
	 *            a mapping of the images of {@link #getPatternMap()} into <code>host</code>; 
	 *            may be <code>null</code> if the condition is ground.
	 * @return an iterator over the matches of this graph condition into
	 *         <code>host</code> that are consistent with
	 *         <code>preMatch</code>
	 */
    final protected Iterator<VarNodeEdgeMap> createMapIter(final Graph host, NodeEdgeMap anchorMap) {
        return getMatchStrategy().getMatchIter(host, anchorMap);
    }
//    
//    /** 
//     * Filters the results of an existing (raw) iterator so that
//     * the resulting objects all satisfy the additional constraints of this condition.
//     * @param matchMapIter the iterator to be filtered
//     * @param host the host graph we are mapping into
//     * @return an iterator of which all results satisfy {@link #satisfiesConstraints(Graph, VarNodeEdgeMap)}
//     */
//    final protected Iterator<VarNodeEdgeMap> filterMapIter(Iterator<VarNodeEdgeMap> matchMapIter, final Graph host) {
//        Iterator<VarNodeEdgeMap> result = matchMapIter;
//        if (hasConstraints()) {
//            result = new FilterIterator<VarNodeEdgeMap>(result) {
//                @Override
//                protected boolean approves(Object obj) {
//                    return satisfiesConstraints(host, (VarNodeEdgeMap) obj);
//                }
//            };
//        }
//        return result;
//    }
//
//
//    /** 
//     * Filters the results of an existing (raw) iterator so that
//     * the resulting objects all satisfy the additional constraints of this condition.
//     * @param matchMapIter the iterator to be filtered
//     * @param host the host graph we are mapping into
//     * @return an iterator of which all results satisfy {@link #satisfiesConstraints(Graph, VarNodeEdgeMap)}
//     */
//    final protected Iterator<VarNodeEdgeMap> filterMapIter(Iterator<VarNodeEdgeMap> matchMapIter, final Graph host) {
//        Iterator<VarNodeEdgeMap> result = matchMapIter;
//        if (hasConstraints()) {
//            result = new FilterIterator<VarNodeEdgeMap>(result) {
//                @Override
//                protected boolean approves(Object obj) {
//                    return satisfiesConstraints(host, (VarNodeEdgeMap) obj);
//                }
//            };
//        }
//        return result;
//    }

    /** 
     * Callback factory method to create a match on the basis of
     * a mapping of this condition's target.
     * @param matchMap the mapping, presumably of the elements of {@link #getTarget()}
     * into some host graph
     * @return a match constructed on the basis of <code>map</code>
     */
    protected ExistsMatch createMatch(VarNodeEdgeMap matchMap) {
    	return new ExistsMatch(matchMap);
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
     * Returns the fragment of the negated conjunct without the edge or merge embargoes. Since we
     * are checking for those already in the simulation, the search for matchings only has to regard
     * the complex negated conjunct. May be <code>null</code>, if only simple negative conditions
     * were added.
     * @see #getInjections()
     * @see #getNegations()
     */
    protected DefaultGraphPredicate getComplexConjunct() {
        if (complexConjunct == null) {
            complexConjunct = createGraphPredicate();
        }
        return complexConjunct;
    }
    
    /**
     * Indicates if there are any negative conditions more complex than 
     * injections and negations.
     * Convenience method for <code>getComplexNegConjunct() != null</code>. 
     */
    protected boolean hasComplexConjunct() {
        return complexConjunct != null && !complexConjunct.isEmpty();
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
     * The fragment of the negated conjunct without edge and merge embargoes.
     */
    private DefaultGraphPredicate complexConjunct;

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
    
    /** Reporter instance for profiling this class. */
    static public final Reporter reporter = Reporter.register(GraphTest.class);
    /** Handle for profiling {@link #getMatches(Graph,NodeEdgeMap)} and related methods. */
    static public final int GET_MATCHING = reporter.newMethod("getMatching...");
    /** Handle for profiling {@link #matches(Graph)} and related methods. */
    static public final int HAS_MATCHING = reporter.newMethod("hasMatching...");
}