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
 * $Id: GraphCondition.java,v 1.3 2007-04-19 06:39:23 rensink Exp $
 */
package groove.trans;

import groove.graph.Edge;
import groove.graph.Morphism;
import groove.graph.Node;
import groove.rel.VarGraph;
import groove.rel.VarMorphism;
import groove.util.FormatException;

/**
 * Interface for conditions over graphs.
 * Conditions are parts of predicates, effectively constituting disjuncts.
 * @author Arend Rensink
 * @version $Revision: 1.3 $
 */
public interface GraphCondition extends GraphTest {
    /**
     * Returns the pattern morphism that this condition itself tests for.
     * Together with the negative predicate this determines the complete condition.
     * @return a graph that must be present for this condition to hold
     * @see #getNegConjunct()
     * @see #hasMatching(VarMorphism)
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
     * @see #hasMatching(VarMorphism)
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
     * Specialises the return type.
     */
    public GraphConditionOutcome getOutcome(VarMorphism subject);
//
//    /**
//     * @param ruleFactory the rule-factory to be set
//     */
//    public void setRuleFactory(RuleFactory ruleFactory);
//
//    /**
//     * Returns the <code>ruleFactory</code>.
//     * @return the <code>ruleFactory</code>
//     */
//    public RuleFactory getRuleFactory();
}