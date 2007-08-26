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
 * $Id: IsoEdgeSearchItem.java,v 1.5 2007-08-26 07:23:10 rensink Exp $
 */
package groove.graph.iso;

import groove.graph.Edge;
import groove.graph.match.EdgeSearchItem;
import groove.graph.match.Matcher;

/**
 * A search item that searches an image for an edge.
 * @author Arend Rensink
 * @version $Revision $
 */
@Deprecated
public class IsoEdgeSearchItem extends EdgeSearchItem<Edge> {
    @Deprecated
	private class IsoEdgeRecord extends EdgeRecord<IsoMatcher> {
        /** Creates a record for a given matcher. */
		protected IsoEdgeRecord(IsoMatcher matcher) {
			super(matcher);
		}

		/**
		 * The search plan should have made sure that all 
		 * end nodes have been matched.
		 */
		@Override
		protected void initImages() {
			Edge image = edge.imageFor(matcher.getSingularMap());
			setSingular(image);
		}
	}
	
	/**
	 * Creates a search item for a given edge.
	 * @param edge the edge from the domain for which we search images
	 */
	public IsoEdgeSearchItem(Edge edge) {
		super(edge, null);
	}
	
	@Override
	public Record get(Matcher matcher) {
		return new IsoEdgeRecord((IsoMatcher) matcher);
	}
}
