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

import static groove.abstraction.Multiplicity.MAX_BOUND;

/**
 * Class that stores some parameters of abstraction.
 * 
 * @author Eduardo Zambon
 */
public abstract class AbsParameters {

    // ------------------------------------------------------------------------
    // Object fields
    // ------------------------------------------------------------------------

    /** Node multiplicity bound (\nu). Is a natural number. Defaults to 1. */
    private int nodeMultBound = 1;
    /** Edge multiplicity bound (\mu). Is a natural number. Defaults to 1. */
    private int edgeMultBound = 1;

    /**
     * Flag to indicate if only three values should be used for multiplicities.
     * This limit the multiplicity values to either 0, 1 or 0+. Setting this
     * flag to true decreases the precision of the abstraction but it can be
     * useful to limit the size of the abstract state space.
     * Defaults to false.
     */
    private boolean useThreeValues = false;

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    /** Basic getter method. */
    public int getNodeMultBound() {
        return this.nodeMultBound;
    }

    /** Basic getter method. */
    public int getEdgeMultBound() {
        return this.edgeMultBound;
    }

    /** Basic inspection method. */
    public boolean isUseThreeValues() {
        return this.useThreeValues;
    }

    /** Basic setter method. The bound given must be positive. */
    public void setNodeMultBound(int nodeMultBound) {
        assert nodeMultBound > 0 : "Invalid node multiplicity bound.";
        if (nodeMultBound > MAX_BOUND) {
            throw new IllegalArgumentException(String.format(
                "Node bound %d exceeds maximum %s", nodeMultBound, MAX_BOUND));
        }
        this.nodeMultBound = nodeMultBound;
    }

    /** Basic setter method. The bound given must be positive. */
    public void setEdgeMultBound(int edgeMultBound) {
        assert edgeMultBound > 0 : "Invalid edge multiplicity bound.";
        if (edgeMultBound > MAX_BOUND) {
            throw new IllegalArgumentException(String.format(
                "Edge bound %d exceeds maximum %s", edgeMultBound, MAX_BOUND));
        }
        this.edgeMultBound = edgeMultBound;
    }

    /** Basic setter method. */
    public void setUseThreeValues(boolean useThreeValues) {
        this.useThreeValues = useThreeValues;
    }

}
