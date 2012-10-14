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

import groove.abstraction.AbsParameters;
import groove.abstraction.MyHashSet;
import groove.graph.TypeLabel;

import java.util.Set;

/**
 * Class that stores the parameters of neighbourhood abstraction.
 * 
 * @author Eduardo Zambon
 */
public final class NeighAbsParam extends AbsParameters {

    // ------------------------------------------------------------------------
    // Static fields
    // ------------------------------------------------------------------------

    private static final NeighAbsParam instance = new NeighAbsParam();

    // ------------------------------------------------------------------------
    // Object fields
    // ------------------------------------------------------------------------

    /** The radius of the abstraction (i). Is a natural number. Defaults to 1. */
    private int absRadius = 1;
    /**
     * Labels to be used in the abstraction. Taken from the grammar properties.
     */
    private Set<TypeLabel> absLabels = new MyHashSet<TypeLabel>();

    // ------------------------------------------------------------------------
    // Static methods
    // ------------------------------------------------------------------------

    /** Returns the singleton instance of this class. */
    public static NeighAbsParam getInstance() {
        return instance;
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    /** Basic getter method. */
    public int getAbsRadius() {
        return this.absRadius;
    }

    /** Basic getter method. */
    public Set<TypeLabel> getAbsLabels() {
        return this.absLabels;
    }

    /** Basic setter method. The radius given must be positive. */
    public void setAbsRadius(int absRadius) {
        assert absRadius == 1 : "Invalid abstraction radius. Current implementation only allows radius 1.";
        this.absRadius = absRadius;
    }

    /** Basic setter method. */
    public void setAbsLabels(Set<TypeLabel> absLabels) {
        this.absLabels.clear();
        this.absLabels.addAll(absLabels);
    }

}
