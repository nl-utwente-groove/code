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

import groove.explore.strategy.LinearStrategy;
import groove.explore.strategy.RandomLinearStrategy;

import java.util.Enumeration;

/**
 * @author Maarten de Mol
 * @version $Revision $
 */
public class StrategyEnumeration implements Enumeration<DocumentedStrategy> {
    /**
     * Internal administration of a list of strategies.
     * Will be filled in by the default constructor.
     */
    private DocumentedStrategy[]        strategies;  
    private int                         currentIndex;
    private int                         nrStrategies;
    
    /**
     * Constructor of the Enumeration. Responsible for adding DocumentedStrategies to the
     * internal administration one by one. The order in which the strategies are added is
     * also the order in which they will be displayed on the ExplorationDialog.
     */
    public StrategyEnumeration(String commandLineArgument) {
        // Prepare the internal administration.
        this.currentIndex = 0;
        this.nrStrategies = 0;
        this.strategies = new DocumentedStrategy[100];
        
        // Create the strategies, one by one.
        addStrategy(new DocumentedStrategy(new LinearStrategy(),
            "Linear",
            "Linear Exploration",
            "TBA",
            commandLineArgument));
       
        addStrategy(new DocumentedStrategy(new RandomLinearStrategy(),
            "RandomLinear",
            "Random Linear Exploration",
            "TBA",
            commandLineArgument));
    }
    
    /**
     * Inserts a strategy into the internal administration.
     * @param strategy - the documented strategy
     */
    public void addStrategy(DocumentedStrategy strategy) {
        this.nrStrategies++;
        this.strategies[this.nrStrategies-1] = strategy;
    }
  
    @Override
    public boolean hasMoreElements() {
        return (this.currentIndex < this.nrStrategies);
    }

    @Override
    public DocumentedStrategy nextElement() {
        if (this.currentIndex < this.nrStrategies) {
            this.currentIndex++;
            return (this.strategies[this.currentIndex-1]);
        } else
            return null;
    }
}
