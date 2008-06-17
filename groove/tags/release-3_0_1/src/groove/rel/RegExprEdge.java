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
 * $Id: RegExprEdge.java,v 1.2 2008-01-30 09:32:27 iovka Exp $
 */
package groove.rel;

import groove.graph.AbstractBinaryEdge;
import groove.graph.DefaultNode;

/**
 * Edge wrapping a regular expression on its label.
 * @author Arend Rensink
 * @version $Revision $
 */
public class RegExprEdge extends AbstractBinaryEdge<DefaultNode,RegExprLabel,DefaultNode> {
	/**
	 * Constructs an edge between two given nodes from a regular expression.
	 * @param source source node; should not be <code>null</code>
	 * @param expr regular expression; not <code>null</code>
	 * @param target target node; should not be <code>null</code>
	 */
	public RegExprEdge(DefaultNode source, RegExpr expr, DefaultNode target) {
		super(source, expr.toLabel(), target);
	}
	
	/**
	 * Constructs an edge between two given nodes from a regular expression label.
	 * @param source 
	 * @param label
	 * @param target
	 */
	public RegExprEdge(DefaultNode source, RegExprLabel label, DefaultNode target) {
		super(source, label, target);
	}
	
	/** Returns the regular expression wrapped in this edge. */
	public RegExpr getRegExpr() {
		return label().getRegExpr();
	}
}
