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
 * $Id: GraphTest.java,v 1.1.1.2 2007-03-20 10:42:56 kastenberg Exp $
 */
package groove.trans;

import groove.graph.Graph;
import groove.rel.VarGraph;
import groove.rel.VarMorphism;

import java.util.Collection;
import java.util.Iterator;

/**
 * Super-interface for tests over graphs.
 * Contains the common functionality of {@link GraphPredicate} and {@link GraphCondition}.
 * @author Arend Rensink
 * @version $Revision: 1.1.1.2 $
 */
public interface GraphTest {    
    /**
     * Called to indicate that this predicate is fixed from now on.
     * This means no more graph conditions may be added to it.
     * The conditions themselves are also set to fixed.
     * @see #isFixed()
     */
    public void setFixed();
    
    /**
     * Indicates whether the predicate has been fixed.
     * @return <code>true</code> if the predicate has been fixed
     * @see #setFixed()
     */
    public boolean isFixed();
    
    /** 
     * Returns the name of this predicate.
     * A return value of <code>null</code> indicates that the predicate is unnamed.
     */
    public NameLabel getName();
    
    /**
     * Indicates if this graph predicate is closed, which is to say that
     * it has an empty context.
     * Convenience method for <code>getContext().isEmpty()</code>.
     * @return <code>true</code> if this predicate has an empty context.
     */
    public boolean isClosed();
    
    /**
     * Returns the context of this predicate.
     * The context is a subgraph that has to be matched already before
     * this predicate can become relevant.
     * If the context is empty, we call the predicate <i>closed</i>.
     * @see #isClosed()
     */
    public VarGraph getContext();
    
    /**
     * Indicates if a given graph satisfies this predicate.
     * Only applicable if this predicate is closed; throws an
     * {@link IllegalArgumentException} otherwise.
     * @param graph the graph to be tested
     * @return <code>true</code> if this predicate is closed and <code>graph</code> satisfies it
     * @throws IllegalArgumentException if <code>! isClosed()</code>
     * @see #hasMatching(VarMorphism)
     * @see #getMatching(Graph)
     */
    public boolean hasMatching(Graph graph);
    
    /**
     * Indicates if a given graph morphism satisfies this predicate.
     * It is required that the morphism domain equals this graph's context
     * and that the morphism is total;
     * an <code>IllegalArgumentException</code> may be thrown otherwise.
     * @param subject the morphism to be tested
     * @return <code>true</code> if <code>morph</code> satisfies this predicate
     * @see #getMatching(VarMorphism)
     * @throws IllegalArgumentException if <code>! morph.isTotal()</code> or <code>morph.dom() != getContext()</code>
     */
    public boolean hasMatching(VarMorphism subject);
    
    /**
     * Returns a matching for a given graph.
     * The matching condition is either this condition (if this test is a
     * {@link GraphCondition}) or among the conditions of this test
     * Refines the result of {@link #hasMatching(Graph)} by indicating the <i>reason</i>
     * why the predicate is or is not satisfied.
     * Only applicable if this predicate is closed; throws an
     * {@link IllegalArgumentException} otherwise.
     * @param graph the graph to be tested
     * @require <code>!isClosed()</code>
     * @see GraphTest#getMatching(Graph)
     * @see #hasMatching(Graph)
     * @throws IllegalArgumentException if <code>! isClosed()</code>
     */
    public Matching getMatching(Graph graph);
    

    /**
     * Returns a matching for the codomain of a given morphism.
     * The matching condition is either this condition (if this test is a
     * {@link GraphCondition}) or among the conditions of this test
     * (if this test is a {@link GraphPredicate}).
     * Refines the result of {@link #hasMatching(VarMorphism)} by indicating the <i>reason</i>
     * why the test is or is not satisfied.
     * @param subject the morphism to be tested
     * @require <code>getContext() == morph.dom()</code>
     * @ensure <code>result.cod() == morph.cod()</code>
     * @see #hasMatching(VarMorphism)
     * @throws IllegalArgumentException if <code>! morph.isTotal()</code> or <code>morph.dom() != getContext()</code>
     */
    public Matching getMatching(VarMorphism subject);

    /**
     * Returns the set of all matchings for a given graph.
     * The matching conditions are either this condition (if this test is a
     * {@link GraphCondition}) or among the conditions of this test
     * Only applicable if this predicate is closed; throws an
     * {@link IllegalArgumentException} otherwise.
     * @param graph the graph to be tested
     * @require <code>!isClosed()</code>
     * @see #getMatching(Graph)
     * @see #hasMatching(Graph)
     * @throws IllegalArgumentException if <code>! isClosed()</code>
     */
    public Collection<? extends Matching> getMatchingSet(Graph graph);

    /**
     * Returns the set of all matchings for a given morphism.
     * The matching conditions are either this condition (if this test is a
     * {@link GraphCondition}) or among the conditions of this test
     * @param subject the morphism to be tested
     * @require <code>getContext() == morph.dom()</code>
     * @see #hasMatching(VarMorphism)
     * @see #getMatching(VarMorphism)
     * @throws IllegalArgumentException if <code>! morph.isTotal()</code> or <code>morph.dom() != getContext()</code>
     */
    public Collection<? extends Matching> getMatchingSet(VarMorphism subject);

    /**
     * Returns an iterator over the set of all matchings for a given graph.
     * The iterator may be computed in a lazy fashion.
     * The matching conditions are either this condition (if this test is a
     * {@link GraphCondition}) or among the conditions of this test
     * Only applicable if this predicate is closed; throws an
     * {@link IllegalArgumentException} otherwise.
     * @param graph the graph to be tested
     * @require <code>!isClosed()</code>
     * @see #hasMatching(Graph)
     * @see #getMatching(Graph)
     * @see #getMatchingSet(Graph)
     * @throws IllegalArgumentException if <code>! isClosed()</code>
     */
    public Iterator<? extends Matching> getMatchingIter(Graph graph);

    /**
     * Returns an iterator over the set of all matchings for a given morphism.
     * The iterator may be computed in a lazy fashion.
     * The matching conditions are either this condition (if this test is a
     * {@link GraphCondition}) or among the conditions of this test
     * Only applicable if this predicate is closed; throws an
     * {@link IllegalArgumentException} otherwise.
     * @param subject the morphism to be tested
     * @require <code>getContext() == morph.dom()</code>
     * @see #hasMatching(VarMorphism)
     * @see #getMatching(VarMorphism)
     * @see #getMatchingSet(VarMorphism)
     * @throws IllegalArgumentException if <code>! morph.isTotal()</code> or <code>morph.dom() != getContext()</code>
     */
    public Iterator<? extends Matching> getMatchingIter(VarMorphism subject);

    /**
     * Returns the outcome of this test for a given subject morphism.
     * @param subject the morphism to be tested
     * @ensure <code>result.test() == this && result.subject() == morph</code>
     * @see #getMatchingSet(VarMorphism)
     * @throws IllegalArgumentException if <code>! morph.isTotal()</code> or <code>morph.dom() != getContext()</code>
     */
    public GraphTestOutcome<?,?> getOutcome(VarMorphism subject);
}