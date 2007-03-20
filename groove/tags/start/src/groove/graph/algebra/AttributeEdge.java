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
 * $Id: AttributeEdge.java,v 1.1.1.2 2007-03-20 10:42:43 kastenberg Exp $
 */

package groove.graph.algebra;

import groove.graph.AbstractBinaryEdge;
import groove.graph.BinaryEdge;
import groove.graph.Label;
import groove.graph.Node;

/**
 * Special edge connecting ordinary nodes to nodes representing algebraic
 * data values.
 * 
 * @author Harmen Kastenberg
 * @version $Revision: 1.1.1.2 $ $Date: 2007-03-20 10:42:43 $
 */
public class AttributeEdge extends AbstractBinaryEdge {

    /**
     * Constructor.
     * @param source the source node
     * @param label the label
     * @param target the target node
     */
    public AttributeEdge(Node source, Label label, Node target) {
        super(source, label, target);
	}

    /* (non-Javadoc)
     * @see groove.graph.AbstractBinaryEdge#newEdge(groove.graph.Node, groove.graph.Label, groove.graph.Node)
     */
    public BinaryEdge newEdge(Node source, Label label, Node target) {
        return new AttributeEdge(source, label, target);
    }
}