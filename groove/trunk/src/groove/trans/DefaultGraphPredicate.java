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
 * $Id: DefaultGraphPredicate.java,v 1.9 2007-08-31 10:23:07 rensink Exp $
 */
package groove.trans;

import groove.graph.DefaultMorphism;
import groove.graph.Graph;
import groove.graph.GraphFactory;
import groove.graph.Morphism;
import groove.rel.RegExprMorphism;
import groove.rel.VarMorphism;
import groove.util.NestedIterator;
import groove.util.TransformIterator;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author Arend Rensink
 * @version $Revision: 1.9 $
 */
public class DefaultGraphPredicate extends HashSet<DefaultGraphCondition> implements GraphPredicate {
    /**
     * Constructs a graph condition with given context and empty name,
     * and initially empty pattern and target graph.
     */
    protected DefaultGraphPredicate(Graph context, NameLabel name) {
        this.context = context;
        this.name = name;
    }
    
    /**
     * Constructs a graph condition with given context and name,
     * and initially empty pattern and target graph.
     */
    protected DefaultGraphPredicate(Graph context) {
        this(context, null);
    }

    public void setOr(GraphTest test) {
        if (isFixed()) {
            throw new IllegalStateException("No conditions may be added to fixed predicate");
        }
        if (test instanceof DefaultGraphCondition) {
            add((DefaultGraphCondition) test);
        } else {
            addAll(((DefaultGraphPredicate) test).getConditions());
        }
    }

    public Set<DefaultGraphCondition> getConditions() {
        return Collections.unmodifiableSet(this);
    }

    public boolean isFixed() {
        return fixed;
    }

    public void setFixed() {
        getContext().setFixed();
        for (GraphCondition condition: this) {
            condition.setFixed();
        }
        fixed = true;
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
        return context;
    }
	/**
     * If {@link #isGround()} holds, delegates to {@link #matches(VarMorphism)} 
     * using an initial morphism (created using {@link #createInitialMorphism(Graph)}).
     * Otherwise, throws an exception as per contract.
     */
    public boolean matches(Graph graph) {
        testClosed();
        return matches(createInitialMorphism(graph));
    }

    /**
     * Creates an initial match using <code>this</code> followed by the inverse of
     * <code>morph</code>, and tests whether that can be extended to a total morphism.
     * @see Morphism#hasTotalExtensions()
     */
    public boolean matches(VarMorphism subject) {
    	for (GraphCondition condition: this) {
            if (condition.matches(subject)) {
                return true;
            }
        }
        return false;
    }

    /**
     * If {@link #isGround()} holds, delegates to {@link #getMatching(VarMorphism)} 
     * using an initial morphism (created using {@link #createInitialMorphism(Graph)}).
     * Otherwise, throws an exception as per contract.
     */
    public Matching getMatching(Graph graph) {
        testClosed();
        return getMatching(createInitialMorphism(graph));
    }

    /**
     * Iterates over the conditions in this predicate. Returns upon the first
     * condition that can be fulfilled. If none can be fulfilled, returns <code>null</code>.
     */
    public Matching getMatching(VarMorphism subject) {
    	for (GraphCondition condition: this) {
            Matching result = condition.getMatching(subject);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    /**
     * If {@link #isGround()} holds, delegates to {@link #getMatching(VarMorphism)} 
     * using an initial morphism (created using {@link #createInitialMorphism(Graph)}).
     * Otherwise, throws an exception as per contract.
     */
    public Collection<? extends Matching> getMatchingSet(Graph graph) {
        testClosed();
        return getMatchingSet(createInitialMorphism(graph));
    }

    /**
     * Collects the matches of the conditions in this predicate.
     */
    public Collection<? extends Matching> getMatchingSet(VarMorphism subject) {
        Set<Matching> result = new HashSet<Matching>();
        for (GraphCondition condition: this) {
            result.addAll(condition.getMatchingSet(subject));
        }
        return result;
    }

    /**
     * If {@link #isGround()} holds, delegates to {@link #getMatching(VarMorphism)} 
     * using an initial morphism (created using {@link #createInitialMorphism(Graph)}).
     * Otherwise, throws an exception as per contract.
     */
    public Iterator<? extends Matching> getMatchingIter(Graph graph) {
        testClosed();
        return getMatchingIter(createInitialMorphism(graph));
    }

    /**
     * Iterates over the conditions, and for each condition, over the
     * positive results for that condition.
     */
    public Iterator<? extends Matching> getMatchingIter(final VarMorphism subject) {
        return new NestedIterator<Matching>(new TransformIterator<GraphCondition,Iterator<? extends Matching>>(iterator()) {
        	@Override
        	protected Iterator<? extends Matching> toOuter(GraphCondition from) {
                return from.getMatchingIter(subject);
            }
        });
    }

    /**
     * Iterates over the conditions in this predicate. Returns upon the first
     * condition that can be fulfilled. If none can be fulfilled, creates a
     * failure with (if the result is not shallow) a map of the conditions' 
     * failures.
     */
    public GraphPredicateOutcome getOutcome(VarMorphism subject) {
        Map<GraphCondition,GraphConditionOutcome> conditionMap = new HashMap<GraphCondition,GraphConditionOutcome>();
        for (GraphCondition condition: this) {
            conditionMap.put(condition, condition.getOutcome(subject));
        }
        return createOutcome(subject, conditionMap);
    }

    /**
     * Factory method for a morphism from the empty graph to a given graph.
     * This implementation returns a {@link RegExprMorphism}; the empty graph
     * is obtained by creating one from <code>graph.newGraph()</code>.
     */
    protected VarMorphism createInitialMorphism(Graph graph) {
        return new RegExprMorphism(EMPTY_GRAPH, graph);
    }

    /**
     * Factory method for a graph predicate success object from a 
     * graph condition success object.
     */
    protected GraphPredicateOutcome createOutcome(final VarMorphism subject, final Map<GraphCondition, GraphConditionOutcome> failureMap) {
        return new DefaultPredicateOutcome(this, subject, failureMap);
    }
    
    /**
     * Throws an {@link IllegalArgumentException} if this predicate is not closed.
     */
    protected void testClosed() {
        if (!isGround()) {
            throw new IllegalArgumentException("Method only allowed on closed predicate");
        }
    }

    /**
     * The name of this condition. May be <code>null</code>.
     */
    private final NameLabel name;
    /**
     * The context of this predicate.
     */
    private final Graph context;
    /**
     * Flag to indicate that the predicate is fixed.
     * This means all the graphs and morphisms are fixed,
     * and no more conditions may be added.
     */
    private boolean fixed;
    
    /** Empty graph, to be used in the standard construction of an initial morphism. */
    static private final Graph EMPTY_GRAPH = GraphFactory.getInstance().newGraph();
}