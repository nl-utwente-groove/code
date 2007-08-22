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
 * $Id: ComplexMatching.java,v 1.1 2007-08-22 09:19:49 kastenberg Exp $
 */
package groove.nesting;

import groove.graph.Graph;
import groove.trans.DefaultMatching;
import groove.trans.GraphCondition;
import groove.trans.GraphTestOutcome;
import groove.trans.Matching;

/**
 *
 * @author kramor
 * @version 0.1 $Revision: 1.1 $ $Date: 2007-08-22 09:19:49 $
 */
@Deprecated
public class ComplexMatching extends DefaultMatching {

	private GraphTestOutcome outcome;
	
	/**
	 * Constructs a new ComplexMatching
	 * @param condition
	 * @param graph
	 */
	public ComplexMatching(GraphCondition condition, Graph graph, GraphTestOutcome<GraphCondition, Matching> outcome) {
		super(condition, graph);
		this.outcome = outcome;
	}
	
}
