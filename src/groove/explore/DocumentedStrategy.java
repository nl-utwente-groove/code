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

import groove.explore.strategy.Strategy;

/**
 * Wrapper class that stores a combination of:
 * - a strategy
 * - a short String that identifies it on the command line
 * - a long String that explains its behavior 
 * 
 * @author Maarten de Mol
 * @version $Revision $
 */
public class DocumentedStrategy {
    private Strategy strategy;
    private String keyword;
    private String name;
    private String explanation;
        
    /**
     * All-purpose constructor.
     *  
     * @param strategy - if this is null, then additional parameters are needed;
     *                   otherwise, the strategy is fixed  
     * @param keyword - identification of the strategy on the command line
     * @param name - identification of the strategy in the user interface
     * @param explanation - explanation of the strategy in the user interface
     */
    public DocumentedStrategy(Strategy strategy, String keyword, String name, String explanation) {
        this.strategy = strategy;
        this.keyword = keyword;
        this.name = name;
        this.explanation = explanation;
    }
    
    /**
     * Get the internally stored strategy. If additional arguments are required,
     * they are obtained by means of a call to queryUser.
     * 
     * @return the strategy, which is either fixed or must still be constructed
     */
    public Strategy getStrategyForUI() {
        if (this.strategy == null)
           this.strategy = queryUser();
            
        return this.strategy;
    }

    /**
     * Get the internally stored strategy. If additional arguments are required,
     * they are obtained by means of a call to parseCommandLine.
     * 
     * @return the strategy, which is either fixed or must still be constructed
     */
    public Strategy getStrategyForCommandline(String commandLineArgument) {
        if (this.strategy == null)
           this.strategy = parseCommandLine(commandLineArgument);

        return this.strategy;
    }
    
    
    /**
     * @return the internally stored keyword
     */
    public String getKeyword() {
        return this.keyword;
    }

    /**
     * @return the internally stored name
     */
    public String getName() {
        return this.name;
    }
    
    /**
     * @return the internally stored explanation
     */
    public String getExplanation() {
        return this.explanation;
    }
    
    /**
     * @return true when the strategy needs additional arguments to be computed,
     *         false otherwise
     */
    public Boolean needsAdditionalArguments() {
        return (this.strategy == null);
    }
    
    /**
     * Must be overridden by the user, if an additional argument is to be parsed from the command line.
     * Will be called with the command line argument given in the constructor.
     * 
     * @return null when parsing fails, a strategy otherwise 
     */
    public Strategy parseCommandLine(String commandLineArgument) {
        return null;
    }

    /**
     * Must be overridden by the user, if an additional argument is to be obtained from the user.
     * Should open a custom dialog which asks for this argument.
     * 
     * @return a strategy when the user supplies the argument, null otherwise 
     */
    public Strategy queryUser() {
        return null;
    }
}
