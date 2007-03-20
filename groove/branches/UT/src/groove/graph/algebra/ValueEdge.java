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
 * $Id: ValueEdge.java,v 1.1.1.2 2007-03-20 10:42:43 kastenberg Exp $
 */

package groove.graph.algebra;

import groove.algebra.Constant;
import groove.graph.DefaultEdge;
import groove.graph.Node;

/**
 * This class is used when visualizing a node representing an algebraic data
 * value. Such nodes would appear as unlabelled nodes for which it is then
 * unclear what they represent.
 *
 * @author Harmen Kastenberg
 */
public class ValueEdge extends DefaultEdge {

    /**
     * Creates a <code>ValueEdge</code> from a given <code>ValueNode</code>.
     * @param node the <code>ValueNode</code> from which to create a <code>ValueEdge</code>
     */
    public ValueEdge(ValueNode node) {
        this(node, node.getConstant().prefix() + node.getConstant().symbol(), node);
        this.constant = node.getConstant();
    }

    /**
     * Creates a <code>ValueEdge</code> from a given source node, string, and target node.
     * @param source the source node
     * @param string the string for the label
     * @param target the target node
     */
    public ValueEdge(Node source, String string, Node target) {
        super(source, string, target);
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return constant.symbol();
    }

    /**
     * Constant represented by this <code>ValueEdge</code>.
     */
    private Constant constant;
}