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
 * EDUARDO: Comment this...
 * @author Eduardo Zambon
 */
public class FrozenEdge {

    private ShapeEdge edge;
    private Multiplicity outMult;
    private Multiplicity inMult;

    /** EDUARDO: Comment this... */
    public FrozenEdge(ShapeEdge edge, Multiplicity outMult, Multiplicity inMult) {
        this.edge = edge;
        this.outMult = outMult;
        this.inMult = inMult;
    }

    /** EDUARDO: Comment this... */
    public ShapeEdge getEdge() {
        return this.edge;
    }

    /** EDUARDO: Comment this... */
    public Multiplicity getOutMult() {
        return this.outMult;
    }

    /** EDUARDO: Comment this... */
    public Multiplicity getInMult() {
        return this.inMult;
    }
}
