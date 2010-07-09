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
package groove.abstraction.gui;

import groove.abstraction.Shape;
import groove.gui.Options;
import groove.gui.jgraph.GraphJModel;
import groove.gui.jgraph.JAttr;

/**
 * Shape version of a GraphJModel

 * @author Eduardo Zambon
 */
public class ShapeJModel extends GraphJModel {

    /** Creates a model from a pattern graph and options */
    ShapeJModel(Shape shape, Options options) {
        super(shape, JAttr.DEFAULT_NODE_ATTR, JAttr.DEFAULT_EDGE_ATTR, options);
    }

    /**
     * Returns an instance of this class for the given Shape.
     */
    public static ShapeJModel getInstance(Shape shape, Options options) {
        ShapeJModel result = new ShapeJModel(shape, options);
        result.reload();
        return result;
    }

    @Override
    /** Specialises return type */
    public Shape getGraph() {
        return (Shape) super.getGraph();
    }

}
