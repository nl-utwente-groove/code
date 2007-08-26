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
 * $Id: IsoNodeSearchItem.java,v 1.5 2007-08-26 07:23:11 rensink Exp $
 */
package groove.graph.iso;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import groove.graph.Node;
import groove.graph.Element;
import groove.graph.match.Matcher;
import groove.graph.match.NodeSearchItem;

/**
 * A search item that searches an image for an edge for the purpose
 * of isomorphism checking, using the certificates computed for the domain and codomain.
 * @author Arend Rensink
 * @version $Revision $
 */
@Deprecated
public class IsoNodeSearchItem extends NodeSearchItem {
    @Deprecated
	private class IsoNodeRecord extends NodeRecord<IsoMatcher> {
		/** Constructs a new record for this search item. */
		protected IsoNodeRecord(IsoMatcher matcher) {
			super(matcher);
		}

		@Override
		public boolean select(Node image) {
			Set<Element> usedImages = matcher.getUsedImages();
			if (!usedImages.contains(image) && super.select(image)) {
				usedImages.add(image);
				return true;
			} else {
				return false;
			}
		}

		@Override
		public void undo() {
			matcher.getUsedImages().remove(getSelected());
			super.undo();
		}

		/**
		 * The images are here determined by the certificates of the 
		 * domain and codomain.
		 */
		@Override
		protected Collection<? extends Node> computeImageSet() {
			Object nodeMatch = matcher.getCertEquivalent(node);
	        if (nodeMatch == null) {
	            return Collections.emptySet();
	        } else if (nodeMatch instanceof Element) {
	            return Collections.singleton((Node) nodeMatch);
	        } else {
	            return ((Collection<Node>) nodeMatch);
	        }
		}
	}

	/**
	 * Creates a search item for a given edge.
	 * @param edge the edge from the domain for which we search images
	 */
	public IsoNodeSearchItem(Node edge) {
		super(edge);
	}
	
	/**
	 * Returns a fresh search item record for the given edge.
	 * The matcher is required to be an {@link IsoMatcher}.
	 * @param matcher the matcher for which the record is to be created;
	 * should be an {@link IsoMatcher}
	 */
	@Override
	public NodeRecord<IsoMatcher> get(Matcher matcher) {
		return new IsoNodeRecord((IsoMatcher) matcher);
	}
}
