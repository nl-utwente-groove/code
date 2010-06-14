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

/**
 * EDUARDO
 * @author Eduardo Zambon
 * @version $Revision $
 */
public final class Parameters {

    // ------------------------------------------------------------------------
    // Static fields
    // ------------------------------------------------------------------------

    /** Node multiplicity bound (\nu). Is a natural number. Defaults to 1. */
    private static int nodeMultBound = 1;
    /** Edge multiplicity bound (\mu). Is a natural number. Defaults to 1. */
    private static int edgeMultBound = 1;
    /** The radius of the abstraction (i). Is a positive natural number.
     *  Defaults to 1. */
    private static int absRadius = 1;

    // ------------------------------------------------------------------------
    // Static methods
    // ------------------------------------------------------------------------

    /** EDUARDO */
    public static int getNodeMultBound() {
        return nodeMultBound;
    }

    /** EDUARDO */
    public static int getEdgeMultBound() {
        return edgeMultBound;
    }

    /** EDUARDO */
    public static int getAbsRadius() {
        return absRadius;
    }

    /** EDUARDO */
    public static void setNodeMultBound(int nodeMultBound) {
        Parameters.nodeMultBound = nodeMultBound;
    }

    /** EDUARDO */
    public static void setEdgeMultBound(int edgeMultBound) {
        Parameters.edgeMultBound = edgeMultBound;
    }

    /** EDUARDO */
    public static void setAbsRadius(int absRadius) {
        Parameters.absRadius = absRadius;
    }

}
