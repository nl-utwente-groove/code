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
    public DocumentedStrategy[] documentedStrategies;
    private int enumeratorIndex;
    private int nrElements;
    
    /**
     * Constructor of the Enumeration. Responsible for adding DocumentedStrategies to the
     * internal administration one by one. The order in which the strategies are added is
     * also the order in which they will be displayed on the ExplorationDialog.
     */
    public StrategyEnumeration() {
        // Prepare the internal administration.
        this.nrElements = 0;
        this.enumeratorIndex = 0;
        this.documentedStrategies = new DocumentedStrategy[100];
        
        // Create the strategies, one by one.
        addDocumentedStrategy(new DocumentedStrategy(new BranchingStrategy(),
            "Branching",
            "Full Exploration (branching, aliasing)",
            "TBA"));

        addDocumentedStrategy(new DocumentedStrategy(new BFSStrategy(),
            "Breadth-First",
            "Full Exploration (breadth-first, aliasing)",
            "TBA"));
       
        addDocumentedStrategy(new DocumentedStrategy(new ExploreRuleDFStrategy(),
            "Depth-First",
            "Full Exploration (depth-first, no aliasing)",
            "TBA"));

        addDocumentedStrategy(new DocumentedStrategy(new LinearConfluentRules(),
            "LinearConfluent",
            "Full Exploration (linear confluent rules)",
            "TBA"));

        addDocumentedStrategy(new DocumentedStrategy(new LinearStrategy(),
            "Linear",
            "Linear Exploration",
            "TBA"));
       
        addDocumentedStrategy(new DocumentedStrategy(new RandomLinearStrategy(),
            "RandomLinear",
            "Random Linear Exploration",
            "TBA"));
    }
    
    /**
     * Inserts a strategy into the internal administration.
     * For internal use in the class constructor only.
     * 
     * @param documentedStrategy - the documented strategy
     */
    private void addDocumentedStrategy(DocumentedStrategy documentedStrategy) {
        this.nrElements++;
        this.documentedStrategies[this.nrElements-1] = documentedStrategy;
    }
    
    /**
     * Looks up a documentedStrategy using its keyword.
     * Use this method to obtain stored strategies independent of an enumerated
     * walk-through.
     * 
     * @param keyword - the keyword of the strategy to lookup
     * @return the documentedStrategy associated with the keyword;
     *         if keyword is unused, then returns null
     */
    public DocumentedStrategy lookupDocumentedStrategyByKeyword(String keyword) {
        int i;
        
        for (i = 0; i < this.nrElements; i++) {
            if (this.documentedStrategies[i].getKeyword() == keyword)
                return this.documentedStrategies[i];
        }
        return null;
    }
  
    /**
     * Looks up a documentedStrategy using its short name.
     * Use this method to obtain stored strategies independent of an enumerated
     * walk-through.
     * 
     * @param name - the short name of the strategy to lookup
     * @return the documentedStrategy associated with the name;
     *         if keyword is unused, then returns null
     */
    public DocumentedStrategy lookupDocumentedStrategyByName(String name) {
        int i;
        
        for (i = 0; i < this.nrElements; i++) {
            if (this.documentedStrategies[i].getName() == name)
                return this.documentedStrategies[i];
        }
        return null;
    }
    
    @Override
    public boolean hasMoreElements() {
        return (this.enumeratorIndex < this.nrElements);
    }

    @Override
    public DocumentedStrategy nextElement() {
        if (this.enumeratorIndex < this.nrElements) {
            this.enumeratorIndex++;
            return (this.documentedStrategies[this.enumeratorIndex-1]);
        } else
            return null;
    }
}
