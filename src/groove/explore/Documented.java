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

/**
 * Wrapper class that stores a combination of:
 * - an object
 * - name for the object on the command line (=keyword)
 * - name for the object in the GUI 
 * - explanation for the object 
 * 
 * Implements the following public methods:
 * - getObjectForUI          - get the object, if necessary query user for additional arguments
 * - getObjectForCommandline - get the object, if necessary parse command line for additional arguments
 * - getKeyword              - get the keyword
 * - getName                 - get the name
 * - getExplanation          - get the explanation
 * - needsArguments          - true if the object is null (but will be evaluated later, see below)
 * - parseCommandline        - parse a String to an object - must be overridden when the object needs additional
 *                                                           arguments that will be supplied by the command line
 * - queryUser               - open a dialog to obtain an object - must be overridden when the object needs additional
 *                                                                 arguments that will be supplied by the user
 * 
 * Implements the following private methods:
 * 
 * @author Maarten de Mol
 * @version $Revision $
 * 
 */
public class Documented<A> {
    private A object;
    private String keyword;
    private String name;
    private String explanation;
        
    /**
     * Default constructor. Only task is to initialize the local data.
     * 
     * @param object - the object, may be null (see   
     * @param keyword - name for the command line
     * @param name - name for the GUI
     * @param explanation - explanation
     */
    public Documented(A object, String keyword, String name, String explanation) {
        this.object = object;
        this.keyword = keyword;
        this.name = name;
        this.explanation = explanation;
    }
    
    /**
     * @return the internal object - always fully evaluated (use queryUser if null)
     */
    public A getObjectForUI() {
        if (this.object == null)
           this.object = queryUser();
            
        return this.object;
    }

    /**
     * @return the internal object - always fully evaluated (use parseCommandline if null)
     */
    public A getObjectForCommandline(String commandLineArgument) {
        if (this.object == null)
           this.object = parseCommandLine(commandLineArgument);

        return this.object;
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
    public Boolean needsArguments() {
        return (this.object == null);
    }
    
    /**
     * Must be overridden by the user, if an additional argument is to be parsed from the command line.
     * Will be called with the command line argument given in the constructor.
     * 
     * @return null when parsing fails, a strategy otherwise 
     */
    public A parseCommandLine(String commandLineArgument) {
        return null;
    }

    /**
     * Must be overridden by the user, if an additional argument is to be obtained from the user.
     * Should open a custom dialog which asks for this argument.
     * 
     * @return a strategy when the user supplies the argument, null otherwise 
     */
    public A queryUser() {
        return null;
    }
}
