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
import groove.graph.AbstractNode;
import groove.graph.TypeLabel;
import groove.trans.HostEdge;
import groove.trans.HostGraph;
import groove.trans.HostNode;
import groove.util.Fixable;

import java.util.Set;

/**
 * Common implementation of pattern nodes of a pattern graph.
 * 
 * @author Eduardo Zambon
 */
public abstract class AbstractPatternNode extends AbstractNode implements
        Fixable {

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

    @Override
    abstract public boolean setFixed();

    @Override
    abstract public boolean isFixed();

    @Override
    public void testFixed(boolean fixed) {
        if (isFixed() != fixed) {
            throw new IllegalStateException();
        }
    }

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
}
