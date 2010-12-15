/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * $Id: GraphCalculator.java,v 1.5 2008-01-30 09:33:18 iovka Exp $
 */
package groove.calc;

import groove.lts.GTS;
import groove.lts.GraphState;
import groove.trans.Condition;
import groove.trans.GraphGrammar;
import groove.trans.HostGraph;

import java.util.Collection;

/**
 * Interface for an object that uses graph transformation as a calculation tool.
 * A graph calculator is loaded with a grammar, i.e., a rule system and a basis
 * graph. Essentially it gives an easier interface to the underlying GTS.
 * @author Arend Rensink
 * @version $Revision$
 */
public interface GraphCalculator {
    /**
     * Returns the (presumably only) "final" graph, i.e., that cannot evolve
     * further. The assumption that there is a unique such state allows a linear
     * exploration strategy. The method will fail to terminate if there is no
     * graph meeting the requirements.
     * @return A result wrapping a graph whose only outgoing transitions are to
     *         itself, or <code>null</code> if there is no such graph.
     * @throws IllegalStateException if the basis has not been initialised
     */
    public GraphState getFinal();

    /**
     * Returns the set of all maximal graphs, i.e., that cannot evolve further.
     * The method will only terminate if the state space is infinite.
     * @return The set of all graphs that cannot evolve further.
     * @throws IllegalStateException if the basis has not been initialised
     */
    public Collection<GraphState> getAllFinal();

    /**
     * Returns the first graph satisfying a named condition. The name refers to
     * a rule in the underlying grammar; the effect is thus the same as
     * <code>getFirst(getGrammar().getRule(conditionName))</code>. The method
     * may fail to terminate or return <code>null</code> if there is no graph
     * satisfying the condition.
     * @param conditionName the graph condition that should be satisfied
     * @return A result wrapping a graph that satisfies the condition
     *         <code>conditionName</code>, or <code>null</code> if there is
     *         no such graph.
     * @throws IllegalStateException if the basis has not been initialised
     * @throws IllegalArgumentException if <code>conditionName</code> is not
     *         the name of a rule in the current grammar
     */
    public GraphState getFirst(String conditionName);

    /**
     * Returns the first graph satisfying a certain condition. The method may
     * fail to terminate or return <code>null</code> if there is no graph
     * satisfying the condition.
     * @param condition the graph condition that should be satisfied
     * @return A result wrapping a graph such that
     *         <code>condition.hasMatching(result)</code>, or
     *         <code>null</code> if there is no such graph.
     * @throws IllegalStateException if the basis has not been initialised
     */
    public GraphState getFirst(Condition condition);

    /**
     * Returns the set of all graphs satisfying a certain condition. The name
     * refers to a rule in the underlying grammar; the effect is thus the same
     * as <code>getAll(getGrammar().getRule(conditionName))</code>. The
     * method will fail to terminate the state space is infinite.
     * @param conditionName the graph condition that should be satisfied
     * @return A set of {@link GraphState}s that satisfy the condition
     *         <code>conditionName</code> for each <code>result</code> in
     *         the set.
     * @throws IllegalStateException if the basis has not been initialised
     * @throws IllegalArgumentException if <code>conditionName</code> is not
     *         the name of a rule in the current grammar
     */
    public Collection<GraphState> getAll(String conditionName);

    /**
     * Returns the set of all graphs satisfying a certain condition. The method
     * will fail to terminate the state space is infinite.
     * @param condition the graph condition that should be satisfied
     * @return A set of {@link GraphState}s such that
     *         <code>condition.hasMatching(result)</code> for each
     *         <code>result</code> in the set.
     * @throws IllegalStateException if the basis has not been initialised
     */
    public Collection<GraphState> getAll(Condition condition);

    /**
     * The original graph, i.e., the one on which the calculator is initialised.
     * May return <code>null</code> if this object is only intended to be used
     * as a prototype.
     */
    public HostGraph getBasis();

    /**
     * Creates and returns a new instance of the calculator, based on another
     * start graph.
     * @param basis the basis for the new calculator.
     * @return A new calculator, such that
     *         <code>result.getBasis().equals(basis)</code>
     * @throws IllegalArgumentException if the new graph is not consistent with
     *         the grammar properties
     * @see GraphGrammar#testConsistent()
     */
    public GraphCalculator newInstance(HostGraph basis)
        throws IllegalArgumentException;

    /**
     * Returns the GTS built up in this calculator.
     */
    public GTS getGTS();

    /**
     * Returns the underlying graph grammar of the GTS.
     */
    public GraphGrammar getGrammar();
}