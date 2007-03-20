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
 * $Id: DefaultMatching.java,v 1.1.1.1 2007-03-20 10:05:19 kastenberg Exp $
 */
package groove.trans;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Morphism;
import groove.graph.NodeEdgeMap;
import groove.graph.Simulation;
import groove.rel.RegExprMorphism;
import groove.rel.RegExprSimulation;
import groove.rel.VarEdge;
import groove.rel.VarMorphism;
import groove.rel.VarNodeEdgeMap;
import groove.util.FilterIterator;

/**
 * Default implementation of the {@link Matching} interface, based on
 * {@link groove.graph.DefaultMorphism}.
 * Expecially redefines the notion of a <i>total extension</i> to those that
 * also fail to satisfy the negated conjunct of this graph condition.
 * @author Arend Rensink
 * @version $Revision: 1.1.1.1 $
 */
public class DefaultMatching extends RegExprMorphism implements Matching {
    /**
     * Constructs an initially empty matching for a given graph condition and graph to be matched.
     * The pattern morphism of the graph condition is disregarded.
     * @param condition the graph condition for which this is a matching
     * @param graph the graph to be matched
     */
    public DefaultMatching(DefaultGraphCondition condition, Graph graph, RuleFactory ruleFactory) {
        super(condition.getTarget(), graph);
        this.ruleFactory = ruleFactory;
        this.condition = condition;
    }

    /**
     * In addition to invoking the <code>super</code> method,
     * registers the valuation if <code>key</code> is a {@link groove.rel.VarEdge}.
     */
    public Edge putEdge(Edge key, Edge value) {
        if (key instanceof VarEdge) {
            putVar(((VarEdge) key).var(), value.label());
        }
        return super.putEdge(key, value);
    }

    /**
     * Returns the rule factory instance of this mathing.
     */
    public RuleFactory getRuleFactory() {
    	return ruleFactory;
    }

    public DefaultGraphCondition getCondition() {
        return condition;
    }

    /**
     * Convenience method for <code>getTotalExtension() != null</code>.
     */
    public boolean hasTotalExtensions() {
        if (hasComplexNegConjunct()) {
            return getTotalExtension() != null;
        } else {
            // if there are no complex negative conditions,
            // the super implementation is fine.
            return super.hasTotalExtensions();
        }
    }

    /**
     * Extends the super implementation by a check of the negated conjunct
     * of the graph condition.
     * Iterates over the total extensions until one is found that does not
     * match the negated conjunct.
     */
    public Morphism getTotalExtension() {
        if (hasComplexNegConjunct()) {
            Iterator<? extends Matching> matchIter = (Iterator<? extends Matching>) super.getTotalExtensionsIter();
            while (matchIter.hasNext()) {
                Matching candidate = matchIter.next();
                if (!matchesComplexNegConjunct(candidate)) {
                    return candidate;
                }
            }
            return null;
        } else {
            // if there are no complex negative conditions,
            // the super implementation is fine.
            return super.getTotalExtension();
        }
    }

    /**
     * Extends the super implementation by a check of the negated conjunct
     * of the graph condition.
     * Removes from the result of the <code>super</code> call those morphisms that
     * match the negated conjunct.
     */
    public Collection<? extends Matching> getTotalExtensions() {
        Collection<? extends Matching> result = (Collection<? extends Matching>) super.getTotalExtensions();
        if (hasComplexNegConjunct()) {
            // throw away those results that match the negated conjunct
            Iterator<? extends Matching> matchIter = result.iterator();
            while (matchIter.hasNext()) {
                Matching candidate = matchIter.next();
                if (matchesComplexNegConjunct(candidate)) {
                    matchIter.remove();
                }
            }
        }
        return result;
    }

    /**
     * Extends the super implementation by a check of the negated conjunct
     * of the graph condition, if that contains any complex conditions.
     * Filters from the result of the <code>super</code> call those morphisms that
     * do not match the negated conjunct.
     */
    public Iterator<? extends Matching> getTotalExtensionsIter() {
        Iterator<? extends Matching> result = (Iterator<? extends Matching>) super.getTotalExtensionsIter();
        if (hasComplexNegConjunct()) {
            return new FilterIterator<Matching>(result) {
                protected boolean approves(Object obj) {
                    return !matchesComplexNegConjunct((Matching) obj);
                }
            };
        } else {
            return result;
        }
    }

    public Map<Matching, GraphPredicateOutcome> getTotalExtensionMap() {
        Iterator<Matching> matchIter = (Iterator<Matching>) super.getTotalExtensionsIter();
        Map<Matching,GraphPredicateOutcome> matchMap = new HashMap<Matching,GraphPredicateOutcome>();
        while (matchIter.hasNext()) {
            Matching match = matchIter.next();
            GraphPredicateOutcome negResultSet = condition.getNegConjunct().getOutcome(match);
            matchMap.put(match, negResultSet);
        }
        return matchMap;
    }

    /**
     * This implementation defers the creation to the rule factory.
     * @see RuleFactory#createSimulation(Matching)
     */
    protected Simulation createSimulation() {
    	return getRuleFactory().createSimulation(this);
    }

    /**
     * This implementation defers the creation to the rule factory.
     * It is assumed that the simulation <code>sim</code> is a {@link MatchingSimulation}.
     * @see RuleFactory#createMatching(MatchingSimulation)
     */
    protected DefaultMatching createMorphism(final Simulation sim) {
    	return (DefaultMatching) getRuleFactory().createMatching(sim);
    }

    /**
     * This implementation returns a {@link RegExprMorphism}.
     * The simulation is required to be a {@link RegExprSimulation};
     * the variable map is taken from the simulation.
     */
    protected DefaultMatching createMorphism(final NodeEdgeMap sim) {
    	return (DefaultMatching) getRuleFactory().createMatching(getCondition(), (VarNodeEdgeMap) sim, cod());
    }

    /**
     * Callback method that checks if the underlying graph condition has
     * more complex negated conditions than merge and edge embargoes, which
     * therefore have to be checked separately.
     * @see #getTotalExtension()
     * @see #getTotalExtensions()
     * @see #getTotalExtensionsIter()
     */
    protected boolean hasComplexNegConjunct() {
        return condition.hasComplexNegConditions();
    }

    /**
     * Callback method that checks if a candidate matching satisfies the
     * complex negated conjunct of the underlying graph condition.
     * This will always return <tt>true</tt> if {@link #hasComplexNegConjunct()}
     * does not hold.
     * @see #getTotalExtension()
     * @see #getTotalExtensions()
     * @see #getTotalExtensionsIter()
     */
    protected boolean matchesComplexNegConjunct(VarMorphism candidate) {
        GraphPredicate complexNegConjunct = condition.getComplexNegConjunct();
        return complexNegConjunct == null || complexNegConjunct.hasMatching(candidate);
    }
    
    /**
     * The graph condition for which this is a matching.
     */
    private final DefaultGraphCondition condition;

    /**
     * Factory instance for creating the correct simulation.
     */
    private final RuleFactory ruleFactory;
}