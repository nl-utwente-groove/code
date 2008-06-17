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
 * $Id: EdgeContent.java,v 1.3 2008-01-30 09:33:13 iovka Exp $
 */
package groove.gui.jgraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import groove.graph.Edge;

/**
 * Content for a JCell consisting of a set of edges.
 * @author Arend Rensink
 * @version $Revision $
 */
public class EdgeContent extends JCellContent<Edge> {
	/** This implementation returns the labels of the edges. */
	@Override
	public Collection<String> getLabelSet() {
		List<String> result = new ArrayList<String>();
		for (Edge edge: this) {
			result.add(edge.label().text());
		}
		return result;
	}
}
