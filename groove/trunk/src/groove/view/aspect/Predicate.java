/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2010 University of Twente
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
package groove.view.aspect;

import groove.algebra.Constant;

/**
 * @author Eduardo Zambon
 */
public class Predicate {

    private final String name;
    private final Constant value;

    /** Default constructor. */
    public Predicate(String name, Constant value) {
        this.name = name;
        this.value = value;
    }

    /** Basic getter method. */
    public String getName() {
        return this.name;
    }

    /** Basic getter method. */
    public String getSignature() {
        return this.getValue().getSignature();
    }

    /** Basic getter method. */
    public Constant getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return this.getName() + AspectParser.ASSIGN
            + this.getValue().toString();
    }

    /** Returns the string to be used by the GUI. */
    public String getContentString() {
        return this.getName() + " " + AspectParser.ASSIGN + " "
            + this.getValue().getSymbol();
    }

}
