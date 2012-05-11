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
package groove.abstraction.pattern.shape;

/**
 * Pattern node of a pattern graph.
 * 
 * @author Eduardo Zambon
 */
public final class PatternNode extends AbstractPatternNode {

    // ------------------------------------------------------------------------
    // Static Fields
    // ------------------------------------------------------------------------

    /** Prefix for string representations. */
    public static final String PREFIX = "p";

    // ------------------------------------------------------------------------
    // Object Fields
    // ------------------------------------------------------------------------

    /** The type associated with this node. */
    private final TypeNode type;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** 
     * Constructs a new pattern node, with the given number.
     */
    public PatternNode(int nr, TypeNode type) {
        super(nr);
        this.type = type;
    }

    // ------------------------------------------------------------------------
    // Overridden methods
    // ------------------------------------------------------------------------

    @Override
    public void setFixed() {
        assert this.type.isFixed();
    }

    @Override
    public boolean isFixed() {
        return this.type.isFixed();
    }

    @Override
    protected String getToStringPrefix() {
        return PREFIX;
    }

    @Override
    public String toString() {
        return super.toString() + ":" + this.type.toString();
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    /** Return the type associated with this node. */
    public TypeNode getType() {
        return this.type;
    }

}
