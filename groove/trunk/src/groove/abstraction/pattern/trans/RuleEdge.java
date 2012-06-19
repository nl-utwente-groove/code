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
package groove.abstraction.pattern.trans;

import groove.abstraction.pattern.shape.AbstractPatternEdge;
import groove.abstraction.pattern.shape.SimpleMorphism;
import groove.abstraction.pattern.shape.TypeEdge;

/**
 * Pattern edge of rules.
 * 
 * @author Eduardo Zambon
 */
public final class RuleEdge extends AbstractPatternEdge<RuleNode> {

    // ------------------------------------------------------------------------
    // Static Fields
    // ------------------------------------------------------------------------

    /** Prefix for string representations. */
    public static final String PREFIX = "s";

    // ------------------------------------------------------------------------
    // Object Fields
    // ------------------------------------------------------------------------

    /** The type associated with this edge. */
    private final TypeEdge type;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** 
     * Constructs a new type edge, with the given number, source and target.
     */
    public RuleEdge(int nr, RuleNode source, TypeEdge type, RuleNode target) {
        super(nr, source, type.label(), target);
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
        return RuleNode.PREFIX + this.source.getNumber() + "--"
            + getPrintableLabel() + "-->" + RuleNode.PREFIX
            + this.target.getNumber();
    }

    @Override
    public SimpleMorphism getMorphism() {
        return getType().getMorphism();
    }

    @Override
    public String getPrintableLabel() {
        return PREFIX + getNumber() + ":" + this.type.getIdStr();
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    /** Return the type associated with this edge. */
    public TypeEdge getType() {
        return this.type;
    }

}
