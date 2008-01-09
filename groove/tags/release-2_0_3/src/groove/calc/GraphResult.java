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
 * $Id: GraphResult.java,v 1.3 2007-10-05 08:31:43 rensink Exp $
 */
package groove.calc;

import java.util.Collection;
import java.util.List;

import groove.graph.Graph;
import groove.graph.Morphism;
import groove.lts.GraphState;
import groove.trans.Condition;
import groove.trans.RuleSystem;

/**
 * Class wrapping the outcome of a graph calculation.
 * @see groove.calc.GraphCalculator
 * @author Arend Rensink
 * @version $Revision: 1.3 $
 */
public interface GraphResult {
    /**
     * Returns the graph that is the actual outcome of the calculation.
     * @return A graph satisfying the conditions in the original calculation
     */
    Graph getGraph();
    
    /**
     * Returns the derivation trace from the start graph to the graph in 
     * this result.
     */
    List<GraphState> getTrace();
    
    /**
     * Returns the morphism from the start graph of the calculator to this graph.
     * @return A morphism such that <code>result.dom().equals(getCalculator().getStart())</code>
     * and <code>result.cod().equals(getGraph())</code>.
     */
    Morphism getMorphism();
    
    /**
     * Returns the calculator that gave rise to this result.
     */
    GraphCalculator getCalculator();
    
    /**
     * Returns an arbitrary state reachable from this one after applying a given named rule.
     * The rule should exist in the rule system of the underlying grammar.
     * Returns <code>null</code> if the rule is not applicable to the current graph.
     * @param ruleName the name of the rule to be applied
     * @return A {@link GraphResult} in the same calculator, reachable from the current result
     * after one application of the rule named <code>ruleName</code>, or <code>null</code> if there
     * is no such graph
     * @throws IllegalArgumentException if there is no rule with the given name
     */
    GraphResult getFirstAfter(String ruleName);
    
    /**
     * Returns all states reachable from this one after applying a given named rule.
     * The rule should exist in the rule system of the underlying grammar.
     * @param ruleName the name of the rule to be applied
     * @return A collection of {@link GraphResult}s in the same calculator, reachable from the current result
     * after one application of the rule named <code>ruleName</code>
     * @throws IllegalArgumentException if there is no rule with the given name
     */
    Collection<GraphResult> getAllAfter(String ruleName);
    
    /**
     * Given a set of transformation rules,
     * returns the (presumably only) "maximal" graph, i.e., that cannot evolve further.
     * The assumption that there is a unique such state allows a linear exploration strategy.
     * The method will fail to terminate if there is no graph meeting the requirements.
     * This is a convenience method for <code>newCalculator(rules).getMax()</code>.
     * @param rules the set of rules with respect to which the result is to be computed
     * @return A result wrapping a graph whose only outgoing transitions are to itself, or 
     * <code>null</code> if there is no such graph.
     * @see GraphCalculator#getMax()
     */
    GraphResult getMax(RuleSystem rules);
    
    /**
     * Given a set of transformation rules,
     * returns the set of all maximal graphs, i.e., that cannot evolve further.
     * The method will only terminate if the state space is infinite.
     * This is a convenience method for <code>newCalculator(rules).getAllMax()</code>.
     * @param rules the set of rules with respect to which the result is to be computed
     * @return The set of all graphs that cannot evolve further.
     * @see GraphCalculator#getAllMax()
     */
    Collection<GraphResult> getAllMax(RuleSystem rules);
    
    /**
     * Given a set of transformation rules,
     * returns the first graph satisfying a certain condition.
     * The method will fail to terminate if there is no graph satisfying the condition.
     * This is a convenience method for <code>newCalculator(rules).getFirst(condition)</code>.
     * @param rules the set of rules with respect to which the result is to be computed
     * @param condition the graph condition that should be satisfied
     * @return A result wrapping a graph such that <code>condition.hasMatching(result)</code>, or 
     * <code>null</code> if there is no such graph.
     * @see GraphCalculator#getFirst(Condition)
     */
    GraphResult getFirst(RuleSystem rules, Condition condition);
    
    /**
     * Given a set of transformation rules,
     * returns the set of all graphs satisfying a certain condition.
     * The method will fail to terminate the state space is infinite.
     * This is a convenience method for <code>newCalculator(rules).getAll(condition)</code>.
     * @param rules the set of rules with respect to which the result is to be computed
     * @param condition the graph condition that should be satisfied
     * @return A set of {@link GraphResult}s such that <code>condition.hasMatching(result)</code>
     * for each <code>result</code> in the set.
     * @see GraphCalculator#getAll(Condition)
     */
    Collection<GraphResult> getAll(RuleSystem rules, Condition condition);

    /**
     * Returns a new calculator, with the same set of rules, that takes the graph
     * in this result as a start graph.
     * @return A fresh calculator such that <code>result.getGraph().equals(getGraph())</code>
     */
    GraphCalculator newCalculator();
    
    /**
     * Returns a new calculator, with a different set of rules, that takes the graph
     * in this result as a start graph.
     * @return A fresh calculator such that <code>result.getGraph().equals(getGraph())</code>
     */
    GraphCalculator newCalculator(RuleSystem rules);
}
