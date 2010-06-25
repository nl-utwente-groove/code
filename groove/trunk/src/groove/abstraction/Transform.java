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
package groove.abstraction;

import groove.graph.GraphShape;
import groove.trans.RuleEvent;

import java.util.HashSet;
import java.util.Set;

/**
 * EDUARDO
 * @author Eduardo Zambon 
 * @version $Revision $
 */
public class Transform {

    // ------------------------------------------------------------------------
    // Static methods
    // ------------------------------------------------------------------------

    /** EDUARDO */
    public static Set<Shape> transform(GraphShape host, RuleEvent event) {
        assert host instanceof Shape : "Cannot use abstract methods on non-abstract graphs.";
        Shape shape = (Shape) host;
        Set<Shape> result = new HashSet<Shape>();

        if (shape.isValidPreMatch(event)) {
            // Find all materialisations.
            Set<Materialisation> mats =
                Materialisation.getMaterialisations(shape,
                    event.getMatch(shape));
            // For all materialisations.
            for (Materialisation mat : mats) {
                // Transform the shape.
                Shape transformedShape = mat.applyMatch();
                Shape normalisedShape = transformedShape.normalise();
                result.add(normalisedShape);
            }
        }
        return result;
    }

}
