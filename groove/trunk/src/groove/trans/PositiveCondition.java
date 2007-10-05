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
 * $Id: PositiveCondition.java,v 1.3 2007-10-05 11:44:55 rensink Exp $
 */
package groove.trans;

import groove.graph.Graph;
import groove.graph.NodeEdgeMap;
import groove.rel.VarNodeEdgeMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

/**
 * Abstract superclass of conditions that test for the existence of a (sub)graph structure.
 * @author Arend Rensink
 * @version $Revision: 1.3 $
 */
abstract public class PositiveCondition<M extends ExistsMatch> extends AbstractCondition<M> {
    /**
     * Constructs a (named) graph condition based on a given target graph and root morphism.
     * @param target the graph to be matched
     * @param rootMap element map from the context to the anchor elements of <code>target</code>;
     * may be <code>null</code> if the condition is ground
     * @param name the name of the condition; may be <code>null</code>
     * @param properties properties for matching the condition; may be <code>null</code>
     */
    protected PositiveCondition(Graph target, NodeEdgeMap rootMap, NameLabel name, SystemProperties properties) {
        super(target, rootMap, name, properties);
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
     * Apart from calling the super method, also maintains the subset of complex sub-conditions.
     * @see #addComplexSubCondition(AbstractCondition)
     */
    @Override
    public void addSubCondition(Condition condition) {
        super.addSubCondition(condition);
        if (!(condition instanceof NotCondition)) {
            addComplexSubCondition((AbstractCondition<?>) condition);
        }
    }
//    
//    /** Adds a negative edge, i.e., an edge embargo, to this condition. */
//    protected void addNegation(Edge negativeEdge) {
//        if (negations == null) {
//            negations = new HashSet<Edge>();
//        }
//        negations.add(negativeEdge);
//    }
//
//    /** Adds an injection constraint, i.e., a merge embargo, to this condition. */
//    protected void addInjection(Set<? extends Node> injection) {
//    	assert injection.size() == 2 : String.format("Injection %s should have size 2", injection);
//        if (injections == null) {
//            injections = new HashSet<Set<? extends Node>>();
//        }
//        injections.add(injection);
//    }

    /** 
	 * Adds a graph condition to the complex sub-conditions, which are
	 * those that are not edge or merge embargoes. 
	 */
	protected void addComplexSubCondition(AbstractCondition<?> condition) {
	    getComplexSubConditions().add(condition);        
	}

	/**
	 * Returns the set of sub-conditions that are <i>not</i> {@link NotCondition}s.
	 */
	protected Collection<AbstractCondition<?>> getComplexSubConditions() {
	    if (complexSubConditions == null) {
	        complexSubConditions = new ArrayList<AbstractCondition<?>>();
	    }
	    return complexSubConditions;
	}

	@Override
    public Iterator<M> getMatchIter(final Graph host, NodeEdgeMap contextMap) {
        Iterator<M> result = null;
        reporter.start(GET_MATCHING);
        testFixed(true);
        // lift the pattern match to a pre-match of this condition's target
        final VarNodeEdgeMap anchorMap = createAnchorMap(contextMap);
        Iterator<VarNodeEdgeMap> matchMapIter = getMatcher().getMatchIter(host, anchorMap);
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
     * The mapping is checked for matches of the sub-conditions; if this fails,
     * the method returns <code>null</code>.
     * TODO this is not correct if a sub-condition has more than one match
     * @param host the graph that is being matched
     * @param matchMap the mapping, which should go from the elements of {@link #getTarget()}
     * into <code>host</code>
     * @return a match constructed on the basis of <code>matchMap</code>, or <code>null</code> if
     * no match exists
     */
    protected M getMatch(Graph host, VarNodeEdgeMap matchMap) {
        M result = createMatch(matchMap);
        for (AbstractCondition< ? > condition : getComplexSubConditions()) {
            Iterator< ? extends Match> subMatchIter = condition.getMatchIter(host, matchMap);
            if (subMatchIter.hasNext()) {
                result.addMatch(subMatchIter.next());
                // TODO remove check below as soon as method is generalised to sub-conditions with > 1 match
                assert !subMatchIter.hasNext();
            } else {
                result = null;
                break;
            }
        }
        return result;
    }

    /** 
     * Callback factory method to create a match on the basis of
     * a mapping of this condition's target.
     * @param matchMap the mapping, presumably from the elements of {@link #getTarget()}
     * into some host graph
     * @return a match constructed on the basis of <code>map</code>
     */
    abstract protected M createMatch(VarNodeEdgeMap matchMap);
//    
//	/**
//     * Returns the map from nodes to sets of injectively matchable nodes.
//     */
//    public Set<Set<? extends Node>> getInjections() {
//        return injections;
//    }
//
//    /**
//     * Returns the map from lhs nodes to incident negative edges.
//     */
//    public Set<Edge> getNegations() {
//        return negations;
//    }
    
    /** 
     * The sub-conditions that are not edge or merge embargoes.
     */
    private Collection<AbstractCondition<?>> complexSubConditions;
//
//    /**
//     * Mapping from codomain nodes to sets of other codomain nodes with which
//     * they must be matched injectively.
//     */
//    private Set<Set<? extends Node>> injections;
//    
//    /**
//     * Mapping from codomain nodes to single or sets of incident codomain edges
//     * that must be absent in the matching.
//     */
//    private Set<Edge> negations;
}