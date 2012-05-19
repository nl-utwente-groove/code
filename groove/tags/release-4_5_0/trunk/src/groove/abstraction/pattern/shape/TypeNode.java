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

import groove.abstraction.pattern.Util;
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
    /** The layer for this node. */
    private int layer;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** 
     * Constructs a new type node, with the given number.
     */
    public TypeNode(int nr, HostGraph pattern) {
        super(nr);
        this.pattern = pattern;
        this.layer = -1;
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

    @Override
    public HostGraph getPattern() {
        return this.pattern;
    }

    @Override
    public int getLayer() {
        int result = this.layer;
        if (result < 0) {
            assert isFixed();
            result = Util.getBinaryEdgesCount(this.pattern);
            this.layer = result;
        }
        return result;
    }

    @Override
    public boolean isNodePattern() {
        return getLayer() == 0;
    }

    @Override
    public boolean isEdgePattern() {
        return getLayer() == 1;
    }

}
