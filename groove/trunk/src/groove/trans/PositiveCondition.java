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
 * $Id: PositiveCondition.java,v 1.1 2007-10-03 23:10:54 rensink Exp $
 */
package groove.trans;

import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Node;
import groove.graph.NodeEdgeMap;
import groove.rel.VarNodeEdgeMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Arend Rensink
 * @version $Revision: 1.1 $
 */
abstract public class PositiveCondition<M extends ExistsMatch> extends AbstractCondition<M> {
    /**
     * Constructs a (named) graph condition based on a given pattern morphism.
     * The name may be <code>null</code>.
     */
    protected PositiveCondition(Graph target, NodeEdgeMap patternMap, NameLabel name, SystemProperties properties) {
        super(target, patternMap, name, properties);
    }
    
    /**
     * Constructs a (named) ground graph condition based on a given pattern target.
     * and initially empty nested predicate.
     * The name may be <code>null</code>.
     */
    protected PositiveCondition(Graph target, NameLabel name, SystemProperties properties) {
        super(target, name, properties);
    }

    /**
     * If the condition is an edge embargo, calls
     * {@link #addNegation(Edge)} with the embargo edge, and if it
     * is a merge embargo, calls {@link #addInjection(Set)} with
     * the injectively matchable nodes. If it is neither, adds the
     * condition to the complex negated conjunct.
     * @see #addInjection(Set)
     * @see #addNegation(Edge)
     * @see #addComplexSubCondition(AbstractCondition)
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
            addComplexSubCondition((AbstractCondition<?>) condition);
        }
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

    /** 
	 * Adds a graph condition to the complex sub-conditions, which are
	 * those that are not edge or merge embargoes. 
	 */
	protected void addComplexSubCondition(AbstractCondition<?> condition) {
	    getComplexSubConditions().add(condition);        
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
	protected Collection<AbstractCondition<?>> getComplexSubConditions() {
	    if (complexSubConditions == null) {
	        complexSubConditions = new ArrayList<AbstractCondition<?>>();
	    }
	    return complexSubConditions;
	}

	/**
	 * Indicates if there are any negative conditions more complex than 
	 * injections and negations.
	 * Convenience method for <code>getComplexNegConjunct() != null</code>. 
	 */
	protected boolean hasComplexSubConditions() {
	    return !getComplexSubConditions().isEmpty();
	}

	@Override
    public Iterator<M> getMatchIter(final Graph host, NodeEdgeMap contextMap) {
        Iterator<M> result = null;
        reporter.start(GET_MATCHING);
        testFixed(true);
        // lift the pattern match to a pre-match of this condition's target
        final VarNodeEdgeMap anchorMap = getAnchorMap(contextMap);
        Iterator<VarNodeEdgeMap> matchMapIter = getMatchStrategy().getMatchIter(host, anchorMap);
        while (result == null && matchMapIter.hasNext()) {
        	M match = getMatch(host, matchMapIter.next());
        	if (match != null) {
        		result = Collections.singleton(match).iterator();
        	}
        }
        if (result == null) {
        	result = Collections.<M>emptySet().iterator();
        }
        reporter.stop();
        return result;
    }

    /** 
     * Returns a match on the basis of a mapping of this condition's target to a given graph.
     * The mapping is checked for satisfaction {@link #satisfiesConstraints(Graph, VarNodeEdgeMap)},
     * and matches of the sub-conditions are added; if either of these steps fails,
     * the method returns <code>null</code>.
     * @param host the graph into which the mapping goes
     * @param matchMap the mapping, which should go from the elements of {@link #getTarget()}
     * into <code>host</code>
     * @return a match constructed on the basis of <code>matchMap</code>; or <code>null</code>
     * if {@link #satisfiesConstraints(Graph, VarNodeEdgeMap)} returns <code>false</code>
     * or any of the sub-conditions cannot be matched
     */
    protected M getMatch(Graph host, VarNodeEdgeMap matchMap) {
        M result = null;
		if (satisfiesConstraints(host, matchMap)) {
	        result = createMatch(matchMap);
	        for (AbstractCondition<?> condition: getComplexSubConditions()) {
	            Iterable<? extends Match> subMatch = condition.getMatches(host, matchMap);
	            if (subMatch.iterator().hasNext()) {
	                result.addMatch(subMatch.iterator().next());
	            } else {
	                result = null;
	                break;
	            }
	        }
		} 
        return result;
    }

    /** 
     * Callback factory method to create a match on the basis of
     * a mapping of this condition's target.
     * @param matchMap the mapping, presumably of the elements of {@link #getTarget()}
     * into some host graph
     * @return a match constructed on the basis of <code>map</code>
     */
    abstract protected M createMatch(VarNodeEdgeMap matchMap);
    
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
     * The sub-conditions that are not edge or merge embargoes.
     */
    private Collection<AbstractCondition<?>> complexSubConditions;

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
}