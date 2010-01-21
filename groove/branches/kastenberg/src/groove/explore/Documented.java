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

import groove.gui.Simulator;

import javax.swing.JPanel;

/**
 * Wrapper class for object that adds documentation (in the form of a name,
 * keyword and explanation) and customization (in the form of allowing
 * additional parameters to be read from the command line or from the GUI). 
 *
 * @author Maarten de Mol
 * @version $Revision $
 * 
 */
public class Documented<A> {
    /**
     * The internally stored object. May be changed by subclasses.
     */
    protected A object;

    private String keyword;
    private String name;
    private String explanation;

    /**
     * Default constructor. Creates a static instance that does not require
     * additional arguments.
     * 
     * @param object - the object (null if additional arguments are needed)
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
     * Returns a JPanel in which the user can select the values for additional
     * arguments. When this happens, the panel should (re-)set the object.
     * Returns null if the object does not require additional arguments.
     * 
     * This method must be overridden if additional arguments are required.
     * 
     * @return a JPanel, possibly null
     */
    public JPanel getArgumentPanel(Simulator simulator) {
        return null;
    }

    /**
     * Sets the object value, using the command line to obtain the values of
     * additional arguments. Default implementation does nothing. Must be
     * overridden if additional arguments can be parsed from the command line.
     */
    public void parseCommandLine(String commandLineArgument) {
        // Default action does nothing.
    }

    /**
     * @return the stored object
     */
    public A getObject() {
        return this.object;
    }

    /**
     * @return the stored keyword
     */
    public String getKeyword() {
        return this.keyword;
    }

    /**
     * @return the stored name
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return the stored explanation
     */
    public String getExplanation() {
        return this.explanation;
    }

    /**
     * @return a serialized representation of the argument values
     * This method must be overridden if additional arguments are required.
     */
    public String getArgumentValues() {
        return new String("");
    }
}
