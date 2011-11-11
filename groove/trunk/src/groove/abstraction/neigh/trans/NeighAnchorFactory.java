// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente

// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// http://www.apache.org/licenses/LICENSE-2.0

// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
// either express or implied. See the License for the specific
// language governing permissions and limitations under the License.
/*
 * $Id: MinimalAnchorFactory.java,v 1.8 2008-02-29 11:02:20 fladder Exp $
 */
package groove.abstraction.neigh.trans;

import groove.abstraction.neigh.Parameters;
import groove.trans.AnchorFactory;
import groove.trans.Rule;
import groove.trans.RuleEdge;
import groove.trans.RuleGraph;
import groove.trans.RuleNode;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Anchor factory used in abstraction.
 * The anchor is formed by elements of the LHS that are within the abstraction
 * radius of eraser elements.
 * 
 * @author Eduardo Zambon
 */
public final class NeighAnchorFactory implements AnchorFactory<Rule> {

    // ------------------------------------------------------------------------
    // Static Fields
    // ------------------------------------------------------------------------

    /** The singleton instance of this class. */
    private static final NeighAnchorFactory instance = new NeighAnchorFactory();

    // ------------------------------------------------------------------------
    // Static Methods
    // ------------------------------------------------------------------------

    /**
     * Returns the singleton instance of this class.
     */
    public static NeighAnchorFactory getInstance() {
        return instance;
    }

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** Private empty constructor to make this a singleton class. */
    private NeighAnchorFactory() {
        // Empty constructor.
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    /**
     * This implementation assumes that the rule is an <tt>SPORule</tt>, and
     * that the rule's internal sets of <tt>lhsOnlyNodes</tt> etc. have been
     * initialised already.
     */
    public RuleGraph newAnchor(Rule rule) {
        // EZ says: for simplicity this method assumes that the abstraction
        // radius is one.
        assert Parameters.getAbsRadius() == 1;
        RuleGraph result = rule.lhs().newGraph(rule.getName() + "-anchor");
        // List of nodes that need to be in a singleton equivalence class
        // after materialisation.
        ArrayList<RuleNode> singularNodes = new ArrayList<RuleNode>();
        singularNodes.addAll(Arrays.asList(rule.getEraserNodes()));
        singularNodes.addAll(rule.getModifierEnds());
        // Everything that is within radius distance of the singular nodes
        // is also part of the anchor.
        for (RuleNode singularNode : singularNodes) {
            for (RuleEdge incidentEdge : rule.lhs().edgeSet(singularNode)) {
                result.addEdge(incidentEdge);
            }
        }
        return result;
    }

}
