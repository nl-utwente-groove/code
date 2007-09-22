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
 * $Id: DefaultMatching.java,v 1.13 2007-09-22 09:10:44 rensink Exp $
 */
package groove.trans;

import groove.calc.Property;
import groove.graph.Graph;
import groove.graph.Morphism;
import groove.graph.NodeEdgeMap;
import groove.match.MatchStrategy;
import groove.rel.RegExprMorphism;
import groove.rel.VarMorphism;
import groove.rel.VarNodeEdgeMap;
import groove.util.FilterIterator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Default implementation of the {@link Matching} interface, based on
 * {@link groove.graph.DefaultMorphism}.
 * Especially redefines the notion of a <i>total extension</i> to those that
 * also fail to satisfy the negated conjunct of this graph condition.
 * @author Arend Rensink
 * @version $Revision: 1.13 $
 */
public class DefaultMatching extends RegExprMorphism implements Matching {
    /**
     * Constructs an initially empty matching for a given graph condition and graph to be matched.
     * The pattern morphism of the graph condition is disregarded.
     * @param condition the graph condition for which this is a matching
     * @param graph the graph to be matched
     */
    public DefaultMatching(DefaultGraphCondition condition, Graph graph) {
        super(condition.getTarget(), graph);
        this.condition = condition;
    }

    public DefaultGraphCondition getCondition() {
        return condition;
    }

    /**
     * Convenience method for <code>getTotalExtension() != null</code>.
     */
    @Override
    public boolean hasTotalExtensions() {
        if (hasAC()) {
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
    @Override
    public Morphism getTotalExtension() {
        if (hasAC()) {
            Iterator<? extends Matching> matchIter = (Iterator<? extends Matching>) super.getTotalExtensionsIter();
            while (matchIter.hasNext()) {
                Matching candidate = matchIter.next();
                if (satisfiesAC(candidate)) {
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
    @Override
    public Collection<? extends Matching> getTotalExtensions() {
        Collection<? extends Matching> result = (Collection<? extends Matching>) super.getTotalExtensions();
        if (hasAC()) {
            // throw away those results that match the negated conjunct
            Iterator<? extends Matching> matchIter = result.iterator();
            while (matchIter.hasNext()) {
                Matching candidate = matchIter.next();
                if (!satisfiesAC(candidate)) {
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
    @Override
    public Iterator<? extends Matching> getTotalExtensionsIter() {
        Iterator<? extends Matching> result = (Iterator<? extends Matching>) super.getTotalExtensionsIter();
        if (hasAC()) {
            return new FilterIterator<Matching>(result) {
                @Override
                protected boolean approves(Object obj) {
                    return satisfiesAC((Matching) obj);
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
     * This implementation delegates to {@link DefaultGraphCondition#getMatchStrategy()}.
     */
    @Override
    protected MatchStrategy createMatchStrategy() {
        return getCondition().getMatchStrategy();
    }

    /**
     * This implementation returns a {@link groove.trans.match.MatchingMatcher} based on this matching.
     */
    @Override
    @Deprecated
    protected groove.graph.match.Matcher createMatcher() {
        return new groove.trans.match.MatchingMatcher(this, condition.getProperties().isInjective());
    }
//
//    /**
//     * This implementation defers the creation to the rule factory.
//     * It is assumed that the simulation <code>sim</code> is a {@link MatchingSimulation}.
//     * @see RuleFactory#createMatching(MatchingSimulation)
//     */
//    @Override
//    protected DefaultMatching createMorphism(final Simulation sim) {
//    	return (DefaultMatching) getRuleFactory().createMatching(sim);
//    }

    @Override
    protected DefaultMatching createMorphism(final NodeEdgeMap sim) {
    	DefaultMatching result = new DefaultMatching(getCondition(), cod()) {
            @Override
            protected VarNodeEdgeMap createElementMap() {
                return (VarNodeEdgeMap) sim;
            }
        };
        return result;
    }

    /** 
     * Sets a (further) application condition,
     * i.e., a property that should be satisfied for 
     * a morphism to be a valid matching.
     * @param ac
     */
    public void setAC(Property<VarMorphism> ac) {
    	this.ac = ac;
    }
    
    /**
     * Callback method that checks if the underlying graph condition has
     * more complex application conditions than merge and edge embargoes, which
     * therefore have to be checked separately.
     * @see #getTotalExtension()
     * @see #getTotalExtensions()
     * @see #getTotalExtensionsIter()
     */
    protected boolean hasAC() {
        return condition.hasComplexNegConjunct() || ac != null;
    }

    /**
     * Callback method that checks if a candidate matching satisfies the (additional)
     * application conditions of the underlying graph condition.
     * This will always return <tt>true</tt> if {@link #hasAC()}
     * does not hold.
     * @see #getTotalExtension()
     * @see #getTotalExtensions()
     * @see #getTotalExtensionsIter()
     */
    protected boolean satisfiesAC(VarMorphism candidate) {
        DefaultGraphPredicate complexNegConjunct = condition.getComplexNegConjunct();
        boolean result = complexNegConjunct == null || !complexNegConjunct.matches(candidate);
        if (result) {
        	result = ac == null || ac.isSatisfied(candidate);
        }
        return result;
    }
    
    /**
     * The graph condition for which this is a matching.
     */
    private final DefaultGraphCondition condition;
    /** 
     * Filter to be applied to all matchings returned by
     * any of the extension methods.
     */
    private Property<VarMorphism> ac;
//    /** Flag indicating that matching should be injective. */
//    private final boolean injective;
//
//    /**
//     * Factory instance for creating the correct simulation.
//     */
//    private final RuleFactory ruleFactory;
}