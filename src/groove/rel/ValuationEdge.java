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
/* $Id: ValuationEdge.java,v 1.1.1.2 2007-03-20 10:42:54 kastenberg Exp $ */
package groove.rel;

import java.util.Map;

import groove.graph.Label;
import groove.graph.Node;

/**
 * Special relation edge of type {@link RelationType#VALUATION},
 * used to store a valuation.
 * @author Arend Rensink
 * @version $Revision $
 */
public class ValuationEdge extends RelationEdge<Map<String,Label>> {
	/** 
	 * Creates an edge for a given source and target node and valuation.
	 * The resulting edge is of type {@link RelationType#VALUATION}. 
	 */
	public ValuationEdge(Node source, Node target, Map<String,Label> valuation) {
		super(source, RelationType.VALUATION, target, valuation);
	}
}
