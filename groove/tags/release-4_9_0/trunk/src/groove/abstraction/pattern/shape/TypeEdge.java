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

import groove.grammar.host.HostEdge;
import groove.grammar.host.HostGraph;
import groove.grammar.host.HostNode;
import groove.graph.plain.PlainLabel;

/**
 * Pattern edge of a pattern type graph.
 * 
 * @author Eduardo Zambon
 */
public final class TypeEdge extends AbstractPatternEdge<TypeNode> {

    // ------------------------------------------------------------------------
    // Static Fields
    // ------------------------------------------------------------------------

    /** Prefix for string representations. */
    static final String PREFIX = "m";

    // ------------------------------------------------------------------------
    // Object Fields
    // ------------------------------------------------------------------------

    /** The simple graph morphism between patterns of source and target nodes. */
    private final SimpleMorphism morph;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** 
     * Constructs a new type edge, with the given number, source and target.
     */
    public TypeEdge(int nr, TypeNode source, TypeNode target,
            SimpleMorphism morph) {
        super(nr, source, PlainLabel.createLabel(PREFIX + nr), target);
        assert morph.getSource().equals(source)
            && morph.getTarget().equals(target);
        this.morph = morph;
    }

    // ------------------------------------------------------------------------
    // Overridden methods
    // ------------------------------------------------------------------------

    @Override
    public boolean setFixed() {
        return getMorphism().setFixed();
    }

    @Override
    public boolean isFixed() {
        return getMorphism().isFixed();
    }

    @Override
    protected String getToStringPrefix() {
        return PREFIX;
    }

    @Override
    public SimpleMorphism getMorphism() {
        return this.morph;
    }

    @Override
    public String getPrintableLabel() {
        return getIdStr();
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    /** Extends the morphism to edges based on the node morphisms. */
    public void extendMorphism() {
        HostGraph srcPattern = source().getPattern();
        HostGraph tgtPattern = target().getPattern();
        for (HostEdge edge1 : srcPattern.edgeSet()) {
            HostNode src2 = getImage(edge1.source());
            HostNode tgt2 = getImage(edge1.target());
            for (HostEdge edge2 : tgtPattern.edgeSet(edge1.label())) {
                if (edge2.source().equals(src2) && edge2.target().equals(tgt2)) {
                    getMorphism().putEdge(edge1, edge2);
                }
            }
        }
    }

}
