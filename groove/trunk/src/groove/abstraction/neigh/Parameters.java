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
package groove.abstraction.neigh;

import groove.graph.TypeLabel;

import java.util.Set;

/**
 * Class that stores the parameters of the abstraction.
 * 
 * @author Eduardo Zambon
 */
public final class Parameters {

    // ------------------------------------------------------------------------
    // Static fields
    // ------------------------------------------------------------------------

    /** Node multiplicity bound (\nu). Is a natural number. Defaults to 1. */
    private static int nodeMultBound = 1;
    /** Edge multiplicity bound (\mu). Is a natural number. Defaults to 1. */
    private static int edgeMultBound = 1;
    /** The radius of the abstraction (i). Is a natural number. Defaults to 1. */
    private static int absRadius = 1;
    /**
     * Labels to be used in the abstraction. Taken from the grammar properties.
     */
    private static Set<TypeLabel> absLabels = new MyHashSet<TypeLabel>();
    /**
     * Flag to indicate if only three values should be used for multiplicities.
     * This limit the multiplicity values to either 0, 1 or 0+. Setting this
     * flag to true decreases the precision of the abstraction but it can be
     * useful to limit the size of the abstract state space.
     * Defaults to false.
     */
    private static boolean useThreeValues = false;

    // ------------------------------------------------------------------------
    // Static methods
    // ------------------------------------------------------------------------

    /** Basic getter method. */
    public static int getNodeMultBound() {
        return nodeMultBound;
    }

    /** Basic getter method. */
    public static int getEdgeMultBound() {
        return edgeMultBound;
    }

    /** Basic getter method. */
    public static int getAbsRadius() {
        return absRadius;
    }

    /** Basic getter method. */
    public static Set<TypeLabel> getAbsLabels() {
        return absLabels;
    }

    /** Basic setter method. The bound given must be positive. */
    public static void setNodeMultBound(int nodeMultBound) {
        assert nodeMultBound > 0 : "Invalid node multiplicity bound.";
        Parameters.nodeMultBound = nodeMultBound;
    }

    /** Basic setter method. The bound given must be positive. */
    public static void setEdgeMultBound(int edgeMultBound) {
        assert edgeMultBound > 0 : "Invalid edge multiplicity bound.";
        Parameters.edgeMultBound = edgeMultBound;
    }

    /** Basic setter method. The radius given must be positive. */
    public static void setAbsRadius(int absRadius) {
        assert absRadius == 1 : "Invalid abstraction radius. Current implementation only allows radius 1.";
        Parameters.absRadius = absRadius;
    }

    /** Basic setter method. */
    public static void setAbsLabels(Set<TypeLabel> absLabels) {
        Parameters.absLabels.clear();
        Parameters.absLabels.addAll(absLabels);
    }

    /** Basic setter method. */
    public static void setUseThreeValues(boolean useThreeValues) {
        Parameters.useThreeValues = useThreeValues;
    }

    /** Basic inspection method. */
    public static boolean isUseThreeValues() {
        return useThreeValues;
    }

}
