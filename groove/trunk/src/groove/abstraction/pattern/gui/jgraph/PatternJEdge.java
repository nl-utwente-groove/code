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
package groove.abstraction.pattern.gui.jgraph;

import groove.abstraction.pattern.shape.AbstractPatternGraph;
import groove.gui.jgraph.JEdge;
import groove.gui.jgraph.JModel;
import groove.gui.look.Look;

/**
 * Class that connects to the JGraph library for displaying pattern edges.
 * 
 * @author Eduardo Zambon
 */
public class PatternJEdge extends JEdge<AbstractPatternGraph<?,?>> {

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    // Private constructor. Use the prototype.
    private PatternJEdge() {
        // empty
    }

    // ------------------------------------------------------------------------
    // Overridden methods
    // ------------------------------------------------------------------------

    @Override
    public PatternJGraph getJGraph() {
        return (PatternJGraph) super.getJGraph();
    }

    @Override
    public String toString() {
        return String.format("PatternJEdge with labels %s", getKeys());
    }

    // ------------------------------------------------------------------------
    // Static methods and fields
    // ------------------------------------------------------------------------

    @Override
    protected Look getStructuralLook() {
        return Look.PATTERN;
    }

    /** 
     * Returns a fresh, uninitialised instance.
     * Call {@link #setJModel(JModel)} to initialise. 
     */
    @SuppressWarnings("unchecked")
    public static PatternJEdge newInstance() {
        return new PatternJEdge();
    }
}
