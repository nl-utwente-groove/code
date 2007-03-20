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
 * $Id: ConditionalExploreStrategy.java,v 1.1.1.1 2007-03-20 10:05:24 kastenberg Exp $
 */
package groove.lts;

import groove.trans.GraphTest;

/**
 * Extends the exploration strategy with the notion of condition
 * that controls when to stop exploration.
 * @author Arend Rensink
 * @version $Revision: 1.1.1.1 $
 */
public interface ConditionalExploreStrategy extends ExploreStrategy {
    /**
     * Returns the controlling condition for this strategy.
     */
    public GraphTest getCondition();
    
    /**
     * Sets the controlling condition for this strategy.
     */
    public void setCondition(GraphTest rule);
    
    
   /**
    * Sets whether the controlling condition should be negated before being applied.
    * @param negated if <tt>true</tt>, the controlling condition should be negated
    */
   public void setNegated(boolean negated);
   
   /**
    * Signals if the controling condition is negated before being applied.
    */
   public boolean isNegated();
}
