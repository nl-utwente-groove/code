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
 * $Id: PatternGraphJModel.java,v 1.1 2007-11-28 16:08:18 iovka Exp $
 */
package groove.gui.jgraph;

import java.util.HashSet;
import java.util.Set;

import groove.abs.GraphPattern;
import groove.gui.Options;

/** A JModel for a graph pattern. 
 * @author Iovka Boneva
 * @version $Revision $
 */
public class PatternGraphJModel extends GraphJModel {

	/** Creates a model from a pattern graph and options */
	PatternGraphJModel(GraphPattern graph, Options options) {
		super(graph, JAttr.DEFAULT_NODE_ATTR, JAttr.DEFAULT_EDGE_ATTR, options);
	}

	public static PatternGraphJModel getInstance(GraphPattern graph, Options options) {
		PatternGraphJModel result = new PatternGraphJModel(graph, options);
		result.reload();
		Set<JCell> centerNodeCell = new HashSet<JCell>(1);
		centerNodeCell.add(result.getJCell(graph.central()));
		result.setEmphasized(centerNodeCell);		
		return result;
	}
	
	@Override
	/** Specialises return type */
	public GraphPattern getGraph() {
		// TODO Auto-generated method stub
		return (GraphPattern) super.getGraph();
	}
	
	
}
