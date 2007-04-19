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
 * $Id: GraphCalculator.java,v 1.2 2007-04-19 06:39:12 rensink Exp $
 */
package groove.calc;

import groove.graph.Graph;
import groove.graph.GraphListener;
import groove.lts.GTS;
import groove.lts.StateGenerator;
import groove.trans.GraphGrammar;
import groove.trans.GraphTest;

import java.util.Collection;

/**
 * Interface for an object that uses graph transformation as a calculation tool.
 * A graph calculator is loaded with a grammar, i.e., a rule system and a basis graph.
 * Essentially it gives an easier interface to the underlying GTS.
 * @author Arend Rensink
 * @version $Revision: 1.2 $
 */
public interface GraphCalculator {
    /**
     * Returns the (presumably only) "maximal" graph, i.e., that cannot evolve further.
     * The assumption that there is a unique such state allows a linear exploration strategy.
     * The method will fail to terminate if there is no graph meeting the requirements.
     * @return A result wrapping a graph whose only outgoing transitions are to itself, or 
     * <code>null</code> if there is no such graph.
     * @throws IllegalStateException if the basis has not been initialised
     */
    GraphResult getMax();
//    
//    /**
//     * Returns the first "maximal" graph, i.e., that cannot evolve further, 
//     * where <i>first</i> means that it has the shortest path from the original graph
//     * (as given by {@link #getBasis()}).
//     * The method will fail to terminate if there is no graph meeting the requirements.
//     * @return A result wrapping a state whose only outgoing transitions are to itself, or 
//     * <code>null</code> if there is no such state. The resulting state is guaranteed to
//     * be the one closest to the original graph.
//     * @see #getMax()
//     * @see #getAllMax()
//     * @see #getFirst(GraphCondition)
//     * @throws IllegalStateException if the basis has not been initialised
//     */
//    GraphResult getFirstMax();
    
    /**
     * Returns the set of all maximal graphs, i.e., that cannot evolve further.
     * The method will only terminate if the state space is infinite.
     * @return The set of all graphs that cannot evolve further.
     * @throws IllegalStateException if the basis has not been initialised
     */
    Collection<GraphResult> getAllMax();
    
    /**
     * Returns the first graph satisfying a named condition.
     * The name refers to a rule in the underlying grammar; the effect is thus the
     * same as <code>getFirst(getGrammar().getRule(conditionName))</code>.
     * The method may fail to terminate or return <code>null</code> if there is no graph satisfying the condition.
     * @param conditionName the graph condition that should be satisfied
     * @return A result wrapping a graph that satisfies the condition <code>conditionName</code>, or 
     * <code>null</code> if there is no such graph.
     * @throws IllegalStateException if the basis has not been initialised
     * @throws IllegalArgumentException if <code>conditionName</code> is not the name of a rule in the 
     * current grammar
     */
    GraphResult getFirst(String conditionName);
    
    /**
     * Returns the first graph satisfying a certain condition.
     * The method may fail to terminate or return <code>null</code> if there is no graph satisfying the condition.
     * @param condition the graph condition that should be satisfied
     * @return A result wrapping a graph such that <code>condition.hasMatching(result)</code>, or 
     * <code>null</code> if there is no such graph.
     * @throws IllegalStateException if the basis has not been initialised
     */
    GraphResult getFirst(GraphTest condition);
    
    /**
     * Returns the set of all graphs satisfying a certain condition.
     * The name refers to a rule in the underlying grammar; the effect is thus the
     * same as <code>getAll(getGrammar().getRule(conditionName))</code>.
     * The method will fail to terminate the state space is infinite.
     * @param conditionName the graph condition that should be satisfied
     * @return A set of {@link GraphResult}s that satisfy the condition <code>conditionName</code>
     * for each <code>result</code> in the set.
     * @throws IllegalStateException if the basis has not been initialised
     * @throws IllegalArgumentException if <code>conditionName</code> is not the name of a rule in the 
     * current grammar
     */
    Collection<GraphResult> getAll(String conditionName);
    
    /**
     * Returns the set of all graphs satisfying a certain condition.
     * The method will fail to terminate the state space is infinite.
     * @param condition the graph condition that should be satisfied
     * @return A set of {@link GraphResult}s such that <code>condition.hasMatching(result)</code>
     * for each <code>result</code> in the set.
     * @throws IllegalStateException if the basis has not been initialised
     */
    Collection<GraphResult> getAll(GraphTest condition);
    
    /** 
     * The original graph, i.e., the one on which the calculator is initialised.
     * May return <code>null</code> if this object is only intended to be used as a prototype. 
     */
    Graph getBasis();
    
    /**
     * Creates and returns a new instance of the calculator, based on another start graph.
     * @param basis the basis for the new calculator.
     * @return A new calculator, such that <code>result.getBasis().equals(basis)</code>
     * @throws IllegalArgumentException if the new graph is not consistent with the grammar properties
     * @see GraphGrammar#testConsistent()
     */
    GraphCalculator newInstance(Graph basis) throws IllegalArgumentException;
    
    
    /**
     * Adds a graph listener to the GTS built up in this calculator.
     */
    public void addGTSListener(GraphListener listener);

    /**
     * Returns the GTS built up in this calculator. 
     */
    public GTS getGTS();

    /**
	 * Returns the state generator used in this calculator.
	 */
    public StateGenerator getGenerator();
    
    /**
     * Returns the underlying graph grammar of the GTS.
     */
    public GraphGrammar getGrammar();
}