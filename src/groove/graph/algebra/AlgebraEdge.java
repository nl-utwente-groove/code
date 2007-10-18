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
 * $Id: AlgebraEdge.java,v 1.8 2007-10-18 14:57:42 rensink Exp $
 */
package groove.graph.algebra;

import groove.graph.AbstractBinaryEdge;
import groove.graph.WrapperLabel;

/**
 * Instances of this class are edges between {@link groove.graph.algebra.ProductNode}s
 * and {@link groove.graph.algebra.ValueNode}s.
 * 
 * @author Harmen Kastenberg
 * @version $Revision 1.0$ $Date: 2007-10-18 14:57:42 $
 */
// AREND I would call this ArgumentEdge, and include the 
// number (as derived from the label); in fact, I think a constructor 
// with an int rather than a label would be more appropriate
public class AlgebraEdge extends AbstractBinaryEdge<ProductNode,WrapperLabel<Integer>,ValueNode> {
	/** Constructs a fresh edge. */
    public AlgebraEdge(ProductNode source, int number, ValueNode target) {
        super(source, new WrapperLabel<Integer>(number), target);
    }
    
	/** Returns the argument number of this edge. */
	public int getNumber() {
		return label().getContent();
	}
}
