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
 * $Id$
 */
package groove.explore;

import groove.explore.result.Result;
import groove.explore.strategy.Strategy;
import groove.lts.GTS;
import groove.lts.GraphState;

/**
 * A scenario for exploring a (part of) a graph transition system yielding a
 * result. A scenario is a combination of a
 * {@link groove.explore.strategy.Strategy}, a
 * {@link groove.explore.result.Acceptor} and a
 * {@link groove.explore.result.Result}. Playing a scenario consists in
 * repeating the {@link groove.explore.strategy.Strategy#next()} method as long
 * as it returns <code>true</code> and the result is not
 * {@link groove.explore.result.Result#done()}. A scenario works on a
 * {@link groove.lts.GTS} and starts exploration in a pre-defined state.
 * 
 * @author Iovka Boneva
 * @author Tom Staijen
 */
public interface Scenario {
    /** Returns then name of this scenario. */
    public String getName();

    /** Returns a one-line description of this scenario. */
    public String getDescription();

    /** Returns the strategy this scenario uses. */
    public Strategy getStrategy();

    /**
     * Plays the scenario on a given GTS, yielding a result. Convenience method
     * for {@link #prepare(GTS, GraphState)} with the GTS' initial state as
     * start state for the scenario.
     * @see #prepare(GTS, GraphState)
     */
    public void prepare(GTS gts);

    /**
     * Plays the scenario on a given GTS and state, yielding a result. The
     * method returns when there are no more states to explore, or when the
     * result is done (according to {@link Result#done()}), or when the thread
     * is interrupted.
     * @param gts the GTS to play the scenario on
     * @param state the start state for the scenario; must be in
     *        <code>gts</code>
     * @see Result#done()
     * @see #isInterrupted()
     */
    public void prepare(GTS gts, GraphState state);

    /**
     * Plays the scenario on the GTS and state for which it was prepared with
     * the last call to {@link #prepare(GTS)} or
     * {@link #prepare(GTS, GraphState)}. The method returns when there are no
     * more states to explore, or when the result is done (according to
     * {@link Result#done()}), or when the thread is interrupted.
     * @return the result of the scenario. This may be partial if the thread was
     *         interrupted.
     * @see Result#done()
     * @see #isInterrupted()
     */
    public Result play();

    /**
     * Returns the result of this scenario. The result is retrieved from the
     * acceptor; it is an error to call this method if no acceptor is set.
     */
    public Result getResult();

    /**
     * Indicates whether the last invocation of {@link #prepare(GTS)} was
     * interrupted.
     */
    public boolean isInterrupted();
}
