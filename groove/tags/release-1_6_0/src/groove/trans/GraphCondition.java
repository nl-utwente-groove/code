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
 * $Id: GraphCondition.java,v 1.5 2007-04-29 09:22:23 rensink Exp $
 */
package groove.trans;

import java.util.Collection;
import java.util.Iterator;

import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Morphism;
import groove.graph.Node;
import groove.rel.VarGraph;
import groove.rel.VarMorphism;
import groove.view.FormatException;

/**
 * Interface for conditions over graphs.
 * Conditions are parts of predicates, effectively constituting disjuncts.
 * @author Arend Rensink
 * @version $Revision: 1.5 $
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
    public VarGraph getTarget();
    
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
     * @see #setAndDistinct(Node, Node)
     * @see #setAndNot(Edge)
     */
    public void setAndNot(GraphTest test);

    /**
     * Adds a negative condition that is an injectivity constraint for two
     * given nodes.
     * This kind of condition is called a <i>merge embargo</i>
     * The method creates and returns a {@link MergeEmbargo}, and adds
     * it to the negative predicate using {@link #setAndNot(GraphTest)}.
     * @param node1 first node of the injectivity constraint
     * @param node2 second node of the injectivity constraint
     * @see #setAndNot(GraphTest)
     * @deprecated no longer used (since new search plan implementation)
     */
    @Deprecated
    public GraphCondition setAndDistinct(Node node1, Node node2);

    /**
     * Adds a negative condition that is an injectivity constraint for the
     * nodes in a given node array. The array is required to be binary.
     * Convenience method for <code>addMergeEmbargo(nodes[0], nodes[1])</code>.
     * @see #setAndDistinct(Node, Node)
     * @deprecated no longer used (since new search plan implementation)
     */
    @Deprecated
    public GraphCondition setAndDistinct(Node[] nodes);
    
    /**
     * Adds a negative condition that forbids the existence of a certain edge.
     * This kind of condition is called an <i>edge embargo</i>
     * The method creates and returns a {@link EdgeEmbargo}, and adds
     * it to the negative predicate using {@link #setAndNot(GraphTest)}.
     * @param embargoEdge the edge that is forbidden
     * @see #setAndNot(GraphTest)
     * @deprecated no longer used (since new search plan implementation)
     */
    @Deprecated
    public GraphCondition setAndNot(Edge embargoEdge);
    
    /** 
     * Factory method to create an initially empty matcher for this condition
     * and a given graph.
     * @param graph the graph in which we want to match this condition
     * @return an initially empty matching, to be refined to get a real matching.
     */
    public Matching newMatcher(Graph graph);
    
    /**
     * Specialises the return type.
     */
    public GraphConditionOutcome getOutcome(VarMorphism subject);
//
//    /**
//     * Indicates if a given graph satisfies this predicate.
//     * Only applicable if this predicate is closed; throws an
//     * {@link IllegalArgumentException} otherwise.
//     * @param graph the graph to be tested
//     * @return <code>true</code> if this predicate is closed and <code>graph</code> satisfies it
//     * @throws IllegalArgumentException if <code>! isClosed()</code>
//     * @see #matches(VarMorphism)
//     * @see #getMatching(Graph)
//     */
//    public boolean matches(Graph graph);
//    
//    /**
//     * Indicates if a given graph morphism satisfies this predicate.
//     * It is required that the morphism domain equals this condition's context
//     * and that the morphism is total;
//     * an <code>IllegalArgumentException</code> may be thrown otherwise.
//     * @param subject the morphism to be tested
//     * @return <code>true</code> if <code>morph</code> satisfies this predicate
//     * @see #getMatching(VarMorphism)
//     * @throws IllegalArgumentException if <code>! subject.isTotal()</code> or <code>subject.dom() != getContext()</code>
//     */
//    public boolean matches(VarMorphism subject);
    
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