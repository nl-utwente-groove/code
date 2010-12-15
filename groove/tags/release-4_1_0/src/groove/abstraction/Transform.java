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
 * This class is only a collection of static methods for transforming shapes
 * and therefore should not be instantiated.
 * Stupid packaging system of Java... >:(
 * 
 * @author Eduardo Zambon 
 */
public final class Transform {

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    private Transform() {
        // We make the constructor private to prevent the creation of objects
        // of this class.
    }

    // ------------------------------------------------------------------------
    // Static methods
    // ------------------------------------------------------------------------

    /**
     * Returns a set of transformed shapes produced by a rule match into a
     * shape. The transformation produces a set of shapes because the
     * materialisation of a shape is a non-deterministic step.
     * 
     * @param host - the host of the transformation, must be a Shape object.
     * @param event - the rule event that defines a pre-match on the host.
     * @return a set of shapes produced by the transformation.
     *         The return set is empty if the pre-match is not valid or if the
     *         host does not admit a valid materialisation w.r.t. the rule.
     */
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
