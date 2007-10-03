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
 * $Id: ExistentialCondition.java,v 1.1 2007-10-03 16:08:40 rensink Exp $
 */
package groove.trans;

import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Morphism;
import groove.graph.Node;
import groove.graph.NodeEdgeMap;
import groove.rel.VarNodeEdgeMap;
import groove.util.TransformIterator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Arend Rensink
 * @version $Revision: 1.1 $
 */
public class ExistentialCondition extends AbstractCondition {
    /**
     * Constructs a (named) graph condition based on a given pattern morphism.
     * The name may be <code>null</code>.
     */
    protected ExistentialCondition(Morphism pattern, NameLabel name, SystemProperties properties) {
        super(pattern, name, properties);
    }
    
    /**
     * Constructs a (named) ground graph condition based on a given pattern target.
     * and initially empty nested predicate.
     * The name may be <code>null</code>.
     */
    protected ExistentialCondition(Graph target, NameLabel name, SystemProperties properties) {
        super(target, name, properties);
    }

    /**
     * Constructs an anonymous graph condition with given initial pattern morphism.
     */
    protected ExistentialCondition(Morphism pattern, SystemProperties properties) {
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
     * @see #addComplexSubCondition(AbstractCondition)
     */
    @Override
    public void addSubCondition(GraphCondition condition) {
        super.addSubCondition(condition);
        if (condition instanceof EdgeEmbargo) {
        	addNegation(((EdgeEmbargo) condition).getEmbargoEdge());
        } else if (condition instanceof MergeEmbargo) {
            Set<? extends Node> injection = condition.getPattern().elementMap().nodeMap().keySet();
            addInjection(injection);
        } else {
            addComplexSubCondition((AbstractCondition) condition);
        }
    }
    
    /** 
     * Adds a graph condition to the complex sub-conditions, which are
     * those that are not edge or merge embargoes. 
     */
    protected void addComplexSubCondition(AbstractCondition condition) {
        if (complexSubConditions == null) {
            complexSubConditions = new ArrayList<AbstractCondition>();
        }
        getComplexConjunct().add(condition);        
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

    public Iterable<? extends Match> getMatches(final Graph host, NodeEdgeMap patternMap) {
        Iterable<? extends Match> result;
        reporter.start(GET_MATCHING);
        testFixed(true);
        // list the pattern match to a pre-match of this condition's target
        final VarNodeEdgeMap anchorMap = getAnchorMap(patternMap);
        result = new Iterable<Match>() {
            public Iterator<Match> iterator() {
                return new TransformIterator<VarNodeEdgeMap, Match>(getMatchStrategy().getMatchIter(host, anchorMap)) {
                    @Override
                    protected Match toOuter(VarNodeEdgeMap from) {
                        if (!hasConstraints() || satisfiesConstraints(host, from)) {
                            return getMatch(host, from);
                        } else {
                            return null;
                        }
                    }
                };
            }
        };
        reporter.stop();
        return result;
    }
//
//    /**
//     * Returns <code>true</code> if there are complex sub-conditions.
//     * @see #getComplexConjunct()
//     */
//    @Override
//    protected boolean hasConstraints() {
//        return hasComplexConjunct();
//    }
//
//	/**
//	 * Tests if a given candidate match satisfies the complex sub-conditions
//	 * of this graph condition.
//	 * This includes the negative and universal application conditions, if any.
//	 * @param host the graph into which a match is sought
//	 * @param matchMap a candidate mapping from {@link #getTarget()} to <code>host</code>
//	 * @return <code>true</code> if <code>matchMap</code> satisfies the additional constraints
//	 */
//    @Override
//	protected boolean satisfiesConstraints(final Graph host, VarNodeEdgeMap matchMap) {
//		for (AbstractCondition subCondition : getComplexConjunct()) {
//			if (subCondition.matches(host, matchMap)) {
//				return false;
//			}
//		}
//        return true;
//	}

    /** 
     * Callback factory method to create a match on the basis of
     * a mapping of this condition's target.
     * @param matchMap the mapping, presumably of the elements of {@link #getTarget()}
     * into some host graph
     * @return a match constructed on the basis of <code>map</code>
     */
    protected ExistentialMatch getMatch(Graph host, VarNodeEdgeMap matchMap) {
        ExistentialMatch result = new ExistentialMatch(this, matchMap);
        for (AbstractCondition condition: getComplexConjunct()) {
            Iterable<? extends Match> subMatch = condition.getMatches(host, matchMap);
            if (subMatch.iterator().hasNext()) {
                result.addMatch(subMatch.iterator().next());
            } else {
                return null;
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
    protected ExistentialMatch createMatch(VarNodeEdgeMap matchMap) {
        return new ExistentialMatch(this, matchMap);
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
    protected Collection<AbstractCondition> getComplexConjunct() {
        return complexSubConditions;
    }
    
    /**
     * Indicates if there are any negative conditions more complex than 
     * injections and negations.
     * Convenience method for <code>getComplexNegConjunct() != null</code>. 
     */
    protected boolean hasComplexConjunct() {
        return complexSubConditions != null;
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
     * The sub-conditions that are not edge or merge embargoes.
     */
    private Collection<AbstractCondition> complexSubConditions;

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