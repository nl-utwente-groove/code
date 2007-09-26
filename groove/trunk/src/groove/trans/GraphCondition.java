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
 * $Id: GraphCondition.java,v 1.10 2007-09-26 21:04:24 rensink Exp $
 */
package groove.trans;

import groove.graph.Graph;
import groove.graph.Morphism;
import groove.rel.VarMorphism;
import groove.view.FormatException;

import java.util.Collection;
import java.util.Iterator;

/**
 * Interface for conditions over graphs.
 * Conditions are parts of predicates, effectively constituting disjuncts.
 * @author Arend Rensink
 * @version $Revision: 1.10 $
 */
public interface GraphCondition extends GraphTest {
    /**
     * Returns the pattern morphism that this condition itself tests for.
     * Together with the negative predicate this determines the complete condition.
     * @return a graph that must be present for this condition to hold
     * @see #getNegConjunct()
     * @see #matches(VarMorphism)
     */
    public Morphism getPattern();
    
    /**
     * The codomain of the pattern morphism.
     * Convenience method for <code>getPattern().cod()</code>.
     */
    public Graph getTarget();
    
    /** Returns the secondary properties of this graph condition. */
    public SystemProperties getProperties();
    
    /**
     * Tests if this graph condition is internally consistent.
     * Inconsistencies may arise for instance due to incompatibility of the
     * actual condition and the secondary properties, as returned by {@link #getProperties()}.
     * The method does nothing if this graph condition is consistent, and throws an exception
     * if it is not.
     * @throws FormatException if this graph condition is inconsistent. The exception contains
     * a list of errors. 
     */
    public void testConsistent() throws FormatException;
//    
//    /**
//     * Indicates if this graph condition uses data attributes in any way.
//     */
//    public boolean hasAttributes();

    /**
     * Returns the negative predicate of this graph condition.
     * The negative predicate has this condition's pattern codomain as it's context;
     * it should <i>fail</i> to hold after the condition has been matched.
     * @ensure <code>result.getCountext() == getPattern().cod()</code>
     * @return A predicate over the pattern codomain that
     * @see #getPattern()
     * @see #matches(VarMorphism)
     */
    public GraphPredicate getNegConjunct();
    
    /**
     * Adds a negative condition.
     * This means adding the condition to the negative predicate.
     * The new condition's context should equal this condition's pattern graph.
     * A {@link IllegalStateException} is thrown if this is not the case 
     * @param test the condition to be added
     * @require <code>condition.getContext() == getPattern().cod()</code>
     * @throws IllegalStateException if the precondition is not fulfilled
     */
    public void setAndNot(GraphTest test);

    /**
     * Specialises the return type.
     */
    
    public GraphConditionOutcome getOutcome(VarMorphism subject);
    /**
     * Returns a matching for a given graph, or <code>null</code> if no matching exists.
     * The matching condition is this condition.
     * Refines the result of {@link #matches(Graph)} by indicating the <i>reason</i>
     * why the condition is satisfied.
     * Only applicable if this condition is ground; throws an
     * {@link IllegalArgumentException} otherwise.
     * @param graph the graph to be tested
     * @see #matches(Graph)
     * @throws IllegalArgumentException if {@link #isGround()} does not hold.
     */
    public Matching getMatching(Graph graph);

    /**
     * Returns a matching for the codomain of a given morphism.
     * The matching condition is this condition.
     * Refines the result of {@link #matches(VarMorphism)} by indicating the <i>reason</i>
     * why the test is or is not satisfied.
     * @param subject the morphism to be tested
     * @require <code>getContext() == subject.dom()</code>
     * @ensure <code>result.cod() == subject.cod()</code>
     * @see #matches(VarMorphism)
     * @throws IllegalArgumentException if <code>! subject.isTotal()</code> or <code>subject.dom() != getContext()</code>
     */
    public Matching getMatching(VarMorphism subject);

    /**
     * Returns the set of all matchings for a given graph.
     * The matching condition is this condition.
     * Only applicable if this condition is ground; throws an
     * {@link IllegalArgumentException} otherwise.
     * @param graph the graph to be tested
     * @require <code>!isClosed()</code>
     * @see #getMatching(Graph)
     * @see #matches(Graph)
     * @throws IllegalArgumentException if {@link #isGround()} does not hold.
     */
    public Collection<? extends Matching> getMatchingSet(Graph graph);

    /**
     * Returns the set of all matchings for a given morphism.
     * The matching condition is this condition.
     * @param subject the morphism to be tested
     * @require <code>getContext() == subject.dom()</code>
     * @see #matches(VarMorphism)
     * @see #getMatching(VarMorphism)
     * @throws IllegalArgumentException if <code>! subject.isTotal()</code> or <code>subject.dom() != getContext()</code>
     */
    public Collection<? extends Matching> getMatchingSet(VarMorphism subject);

    /**
     * Returns an iterator over the set of all matchings for a given graph.
     * The iterator may be computed in a lazy fashion.
     * The matching condition is this condition.
     * Only applicable if this condition is ground; throws an
     * {@link IllegalArgumentException} otherwise.
     * @param graph the graph to be tested
     * @see #matches(Graph)
     * @see #getMatching(Graph)
     * @see #getMatchingSet(Graph)
     * @throws IllegalArgumentException if  {@link #isGround()} does not hold.
     */
    public Iterator<? extends Matching> getMatchingIter(Graph graph);

    /**
     * Returns an iterator over the set of all matchings for a given morphism.
     * The iterator may be computed in a lazy fashion.
     * The matching condition is this condition.
     * Only applicable if this condition is ground; throws an
     * {@link IllegalArgumentException} otherwise.
     * @param subject the morphism to be tested
     * @require <code>getContext() == subject.dom()</code>
     * @see #matches(VarMorphism)
     * @see #getMatching(VarMorphism)
     * @see #getMatchingSet(VarMorphism)
     * @throws IllegalArgumentException if <code>! subject.isTotal()</code> or <code>subject.dom() != getContext()</code>
     */
    public Iterator<? extends Matching> getMatchingIter(VarMorphism subject);
}