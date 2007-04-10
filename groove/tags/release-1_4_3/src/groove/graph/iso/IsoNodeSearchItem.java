/* $Id: IsoNodeSearchItem.java,v 1.2 2007-03-30 15:50:46 rensink Exp $ */
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
public class IsoNodeSearchItem extends NodeSearchItem {
	private class IsoNodeRecord extends NodeRecord<IsoMatcher> {
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
