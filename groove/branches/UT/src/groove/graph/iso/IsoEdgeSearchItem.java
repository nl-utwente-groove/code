/* $Id: IsoEdgeSearchItem.java,v 1.1.1.2 2007-03-20 10:42:44 kastenberg Exp $ */
package groove.graph.iso;

import groove.graph.Edge;
import groove.graph.Node;
import groove.graph.match.EdgeSearchItem;
import groove.graph.match.Matcher;
import groove.graph.match.NodeSearchItem;

/**
 * A search item that searches an image for an edge.
 * @author Arend Rensink
 * @version $Revision $
 */
public class IsoEdgeSearchItem extends EdgeSearchItem<Edge> {
	private class IsoEdgeRecord extends EdgeRecord<IsoMatcher> {
		protected IsoEdgeRecord(IsoMatcher matcher) {
			super(matcher);
		}
//
//		@Override
//		public void undo() {
//			boolean isNew = matcher.getUsedImages().remove(getSelected());
//			assert isNew : String.format("Image %s was already in used images %s", edge, getSelected());
//			super.undo();
//		}

		/**
		 * The search plan should have made sure that all 
		 * end nodes have been matched.
		 */
		protected void initImages() {
			Edge image = edge.imageFor(matcher.getSingularMap());
			setSingular(image);
		}
//		/**
//		 * The images are here determined by the certificates of the 
//		 * domain and codomain.
//		 */
//		protected void initImages() {
//			Object edgeMatch = matcher.getCertEquivalent(edge);
//	        if (edgeMatch == null) {
//	        	setSingular(null);
//	        } else if (edgeMatch instanceof Element) {
//	        	setSingular((Edge) edgeMatch);
//	        } else {
//	        	setMultiple((Collection<Edge>) edgeMatch);
//	        }
//		}
//		
//		/**
//		 * The images are here determined by the certificates of the 
//		 * domain and codomain.
//		 */
//		protected Collection<? extends Edge> computeImageSet() {
//			Object edgeMatch = matcher.getCertEquivalent(edge);
//	        if (edgeMatch == null) {
//	            return Collections.<Edge>emptySet();
//	        } else if (edgeMatch instanceof Element) {
//	            return Collections.singleton((Edge) edgeMatch);
//	        } else {
//	            return ((Collection<Edge>) edgeMatch);
//	        }
//		}
	}
	
	/**
	 * Creates a search item for a given edge.
	 * @param edge the edge from the domain for which we search images
	 */
	public IsoEdgeSearchItem(Edge edge) {
		super(edge, null);
	}
	
	public Record get(Matcher matcher) {
		return new IsoEdgeRecord((IsoMatcher) matcher);
	}

	/** This implementation returns an {@link IsoNodeSearchItem}. */
	@Override
	protected NodeSearchItem createNodeSearchItem(Node node) {
		return new IsoNodeSearchItem(node);
	}	
}
