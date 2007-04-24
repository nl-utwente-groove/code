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
 * $Id: ExploreStrategy.java,v 1.3 2007-04-24 10:06:43 rensink Exp $
 */
package groove.lts;


import java.util.Collection;

/**
 * A strategy interface for state space exploration.
 * Intended for implementation as depth-first, breadth-first, etc.
 * @author Arend Rensink
 * @version $Revision: 1.3 $
 */
public interface ExploreStrategy {
    /**
     * Sets the start state at which the exploration should take place.
     * @param atState the state from which exploration should start
     * @require <tt>getLTS().contains(atState)</tt>
     * @ensure <tt>getAtState() == atState</tt>
     */
    void setAtState(State atState);
    
    /**
     * Returns the start state at which the exploration should take place.
     * @return the state from which exploration should start
     * @ensure <tt>getLTS() == null || getLTS().contains(result)</tt>
     */
    State getAtState();
    
    /**
     * Sets the (maximum) exploration depth.
     * If zero, there is no maximum depth.
     * @param toDepth the (maximum) exploration depth; <tt>0</tt> for unbounded exploration
     * @require <tt>toDepth >= 0</tt>
     * @ensure <tt>getToDepth() == toDepth</tt>
     * @deprecated the depth is ignored
     */
    @Deprecated
    void setToDepth(int toDepth);
    
    /**
     * Returns the current (maximum) exploration depth.
     * If zero, there is no maximum depth.
     * @return the current (maximum) exploration depth
     * @ensure <tt>result >= 0</tt>
     * @deprecated the depth is ignored
     */
    @Deprecated
    int getToDepth();
    
    /**
     * Sets the generator to be used for exploration.
     * The GTS to be explored is derived from the generator.
     * @param generator the generator to be used in exploration
     */
    public void setGenerator(StateGenerator generator);
    
    /**
     * Sets the GTS to be explored.
     * A new state generator is created for the exploration.
     * @param gts the GTS to be explored
     */
    public void setLTS(GTS gts);
    
    /**
     * Returns the GTS currently being explored.
     * @return the LTS currently being explored; <tt>null</tt> if no GTS is set
     */
    public LTS getLTS();

    /**
     * Returns a one-line description of the exploration strategy.
     */
    public String getShortDescription();

    /**
     * Returns a one-word denominator of the exploration strategy.
     */
    public String getName();

    /**
     * Explores the currently set LTS, at the state returned by {@link #getAtState}, and
     * returns the set of result states.
     * What is a result state is determined by the strategy itself.
     * @require <tt>getLTS() != null</tt>
     */
    public Collection<? extends State> explore() throws InterruptedException;
}