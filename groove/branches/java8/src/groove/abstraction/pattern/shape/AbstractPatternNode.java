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
import groove.grammar.host.HostEdge;
import groove.grammar.host.HostGraph;
import groove.grammar.host.HostNode;
import groove.grammar.type.TypeLabel;
import groove.graph.ANode;
import groove.util.Fixable;

import java.util.Comparator;
import java.util.Set;

/**
 * Common implementation of pattern nodes of a pattern graph.
 *
 * @author Eduardo Zambon
 */
public abstract class AbstractPatternNode extends ANode implements Fixable {

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /**
     * Constructs a new pattern node, with the given number.
     */
    public AbstractPatternNode(int nr) {
        super(nr);
    }

    // ------------------------------------------------------------------------
    // Overridden methods
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    /** Returns the simple graph pattern associated with this node. */
    abstract public HostGraph getPattern();

    /** Returns the layer where this node should be placed. */
    abstract public int getLayer();

    /** Returns true if this pattern node is associated with a single node. */
    abstract public boolean isNodePattern();

    /** Returns true if this pattern node is associated with a edge node. */
    abstract public boolean isEdgePattern();

    /** Returns the Id of this edge as a string. */
    public String getIdStr() {
        return super.toString();
    }

    /** Returns the single node associated with this pattern node. */
    public HostNode getSimpleNode() {
        assert isNodePattern();
        return getPattern().nodeSet().iterator().next();
    }

    /** Returns the labels of the single node associated with this pattern node. */
    public Set<TypeLabel> getNodeLabels() {
        assert isNodePattern();
        return Util.getNodeLabels(getPattern(), getSimpleNode());
    }

    /** Returns the single edge associated with this pattern node. */
    public HostEdge getSimpleEdge() {
        assert isEdgePattern();
        return Util.getBinaryEdges(getPattern()).iterator().next();
    }

    /** Returns the source of the single edge associated with this pattern node. */
    public HostNode getSource() {
        assert isEdgePattern();
        return getSimpleEdge().source();
    }

    /** Returns the target of the single edge associated with this pattern node. */
    public HostNode getTarget() {
        assert isEdgePattern();
        return getSimpleEdge().target();
    }

    /** String to be used when displaying the node in graphical mode.*/
    public String getAdornment() {
        return super.toString();
    }

    /** Returns true if this node is a layer 1 node with the given edge. */
    public boolean introduces(HostEdge sEdge) {
        return getSimpleEdge().equals(sEdge);
    }

    // ------------------------------------------------------------------------
    // Inner classes
    // ------------------------------------------------------------------------

    /** Sorting of nodes by layer. */
    private static final class PatternNodeComparator implements Comparator<AbstractPatternNode> {

        @Override
        public int compare(AbstractPatternNode o1, AbstractPatternNode o2) {
            if (o1.getLayer() < o2.getLayer()) {
                return -1;
            } else if (o1.getLayer() > o2.getLayer()) {
                return 1;
            } else { // Same layer, use the node number to distinct.
                return o1.getNumber() - o2.getNumber();
            }
        }
    }

    /**
     * Singleton instance of the comparator.
     */
    public static final PatternNodeComparator comparator = new PatternNodeComparator();
}
