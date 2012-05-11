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

import groove.trans.HostGraph;

/**
 * Pattern node of a pattern type graph.
 * 
 * @author Eduardo Zambon
 */
public final class TypeNode extends AbstractPatternNode {

    // ------------------------------------------------------------------------
    // Static Fields
    // ------------------------------------------------------------------------

    /** Prefix for string representations. */
    public static final String PREFIX = "t";

    // ------------------------------------------------------------------------
    // Object Fields
    // ------------------------------------------------------------------------

    /** The simple graph pattern associated with this node. */
    private final HostGraph pattern;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** 
     * Constructs a new type node, with the given number.
     */
    public TypeNode(int nr, HostGraph pattern) {
        super(nr);
        this.pattern = pattern;
    }

    // ------------------------------------------------------------------------
    // Overridden methods
    // ------------------------------------------------------------------------

    @Override
    public void setFixed() {
        getPattern().setFixed();
    }

    @Override
    public boolean isFixed() {
        return getPattern().isFixed();
    }

    @Override
    protected String getToStringPrefix() {
        return PREFIX;
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    /** Return the simple graph pattern associated with this node. */
    public HostGraph getPattern() {
        return this.pattern;
    }

}
