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
 * $Id$
 */
package groove.explore;

import groove.explore.strategy.BFSStrategy;
import groove.explore.strategy.BranchingStrategy;
import groove.explore.strategy.ExploreRuleDFStrategy;
import groove.explore.strategy.LinearConfluentRules;
import groove.explore.strategy.LinearStrategy;
import groove.explore.strategy.RandomLinearStrategy;
import groove.explore.strategy.Strategy;

/**
 * An enumeration of Documented<Strategy>.
 * Stores all the exploration strategies that can be executed within Groove.
 *
 * @author Maarten de Mol
 * @version $Revision $
 * 
 */
public class StrategyEnumerator extends Enumerator<Strategy> {
    /**
     * Extended constructor. Enumerates the available strategies one by one.
     */
    public StrategyEnumerator() {
        super();
        
        addObject(new Documented<Strategy>(new BranchingStrategy(),
            "Branching",
            "Full Exploration (branching, aliasing)",
            "This strategy first generates all possible transitions from each open state, " +
            "and then continues in a breadth-first fashion.<BR>" +
            "<I>This strategy does not use local cache and is optimized for memory consumption.</I>"));

        addObject(new Documented<Strategy>(new BFSStrategy(),
            "Breadth-First",
            "Full Exploration (breadth-first, aliasing)",
            "This strategy first generates all possible transitions from each open state, " +
            "and then continues in a breadth-first fashion.<BR>" +
            "<I>This strategy uses a local cache of transitions.</U>"));
       
        addObject(new Documented<Strategy>(new ExploreRuleDFStrategy(),
            "Depth-First",
            "Full Exploration (depth-first, no aliasing)",
            "This strategy first generates all possible transitions from each open state, " +
            "and then continues in a depth-first fashion."));

        addObject(new Documented<Strategy>(new LinearConfluentRules(),
            "LinearConfluent",
            "Full Exploration (linear confluent rules)",
            "This strategy generates all possible transitions from each open state, " +
            "but only takes one transition of each pair of transitions that have been marked as confluent."));

        addObject(new Documented<Strategy>(new LinearStrategy(),
            "Linear",
            "Linear Exploration",
            "This strategy chooses one transition from each open state. " +
            "The transition of choice will be the same within one incarnation of Groove."));
       
        addObject(new Documented<Strategy>(new RandomLinearStrategy(),
            "RandomLinear",
            "Random Linear Exploration",
            "This strategy chooses one transition from each open state. " +
            "The transition is chosen randomly."));
    }
}