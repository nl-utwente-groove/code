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
import groove.gui.jgraph.AJVertex;
import groove.gui.look.Look;

/**
 * Class that connects to the JGraph library for displaying pattern nodes.
 * 
 * @author Eduardo Zambon
 */
public class PatternJVertex
        extends
        AJVertex<AbstractPatternGraph<?,?>,PatternJGraph,PatternJModel,PatternJEdge>
        implements PatternJCell {

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    // Private constructor. Use the prototype.
    private PatternJVertex(boolean outer) {
        this.outer = outer;
    }

    // ------------------------------------------------------------------------
    // Static methods and fields
    // ------------------------------------------------------------------------

    @Override
    protected Look getStructuralLook() {
        return isOuter() ? Look.PATTERN : super.getStructuralLook();
    }

    @Override
    public boolean isOuter() {
        return this.outer;
    }

    private final boolean outer;

    /** Returns a new instance of this class. */
    public static PatternJVertex newInstance(boolean outer) {
        return new PatternJVertex(outer);
    }
}
