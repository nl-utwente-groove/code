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
public class EdgeSigWrapper {

    // ------------------------------------------------------------------------
    // Object Fields
    // ------------------------------------------------------------------------

    private EdgeSignature es;
    private boolean outgoing;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** Standard constructor that just fills in the object fields. */
    public EdgeSigWrapper(EdgeSignature es, boolean outgoing) {
        this.es = es;
        this.outgoing = outgoing;
    }

    // ------------------------------------------------------------------------
    // Overridden methods
    // ------------------------------------------------------------------------

    @Override
    public String toString() {
        return this.outgoing ? "out: " + this.es.toString() : "in: "
            + this.es.toString();
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    /** EDUARDO: Comment this... */
    public EdgeSignature getEdgeSig() {
        return this.es;
    }

    /** EDUARDO: Comment this... */
    public boolean isOutgoing() {
        return this.outgoing;
    }

}
