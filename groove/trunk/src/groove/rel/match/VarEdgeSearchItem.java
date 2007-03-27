/* $Id: VarEdgeSearchItem.java,v 1.2 2007-03-27 14:18:34 rensink Exp $ */
package groove.rel.match;

import groove.graph.BinaryEdge;
import groove.graph.DefaultEdge;
import groove.graph.DefaultFlag;
import groove.graph.Edge;
import groove.graph.Label;
import groove.graph.Node;
import groove.graph.match.EdgeSearchItem;
import groove.graph.match.Matcher;
import groove.rel.RegExprLabel;
import groove.rel.VarNodeEdgeMap;

/**
 * A search item that searches an image for an edge.
 * @author Arend Rensink
 * @version $Revision $
 */
public class VarEdgeSearchItem extends EdgeSearchItem<Edge> {
	protected class VarEdgeRecord extends EdgeRecord<RegExprMatcher> {
		protected VarEdgeRecord(RegExprMatcher matcher) {
			super(matcher);
			varPreMatched = matcher.getVar(var) != null;
		}
		
		/**
		 * Tests if a given edge can be accepted as image.
		 * @param image the edge image to be tested
		 * @return <code>true</code> if <code>image</code> is an acceptable image
		 * for {@link VarEdgeSearchItem#edge}
		 */
		@Override
		public boolean select(Edge image) {
			boolean result = image.endCount() == edge.endCount() && super.select(image);
			if (result) {
				assert ! varPreMatched || matcher.getVar(var) != null;
				if (!varPreMatched) {
					matcher.putVar(var, image.label());
				}
			}
			return result;
		}

		@Override
		public void undo() {
			assert matcher.getVar(var).equals(selected.label()) : String.format("Wrong image %s for variable %s: should be %s", matcher.getVar(var), var, selected.label());
			super.undo();
			if (!varPreMatched) {
				Label oldImage = matcher.getValuation().remove(var);
				assert oldImage != null;
			}
		}

		@Override
		protected void initImages() {
			if (varPreMatched && isAllEndsPreMatched()) {
				Edge image = edge.imageFor(matcher.getSingularMap());
				if (matcher.cod().containsElement(image)) {
					setSingular(image);
				} else {
					setSingular(null);
				}
			} else if (varPreMatched) {
		        setMultiple(matcher.cod().labelEdgeSet(edge.endCount(), matcher.getVar(var)));
			} else {
				setMultiple(matcher.cod().edgeSet());
			}
		}
		
		/** 
		 * Callback factory method to constructs an image of the search item's edge under a given mapping. 
		 */
		protected Edge computeEdgeImage(VarNodeEdgeMap elementMap) {
			Edge result;
			Node sourceImage = elementMap.getNode(edge.source());
			if (edge.endCount() == BinaryEdge.END_COUNT) {
				Node targetImage = elementMap.getNode(edge.opposite());
				result = createBinaryEdge(sourceImage, elementMap.getVar(var), targetImage);
			} else {
				result = createUnaryEdge(sourceImage, elementMap.getVar(var));
			}
			return result;
		}
		
		/** Callback factory method for a binary edge with a given source, label and target. */
		protected Edge createBinaryEdge(Node source, Label label, Node target) {
			return DefaultEdge.createEdge(source, label, target);
		}
		
		/** Callback factory method for a unary edge with a given source and label. */
		protected Edge createUnaryEdge(Node source, Label label) {
			return new DefaultFlag(source, label);
		}

		/** 
		 * Flag indicating if {@link VarEdgeSearchItem#var} has received
		 * a fresh image in this record.
		 */
		private final boolean varPreMatched;
	}

	public VarEdgeSearchItem(Edge edge, boolean... matched) {
		super(edge, matched);
		this.var = RegExprLabel.getWildcardId(edge.label());
		assert this.var != null : String.format("Edge %s is not a variable edge", edge);
		assert edge.endCount() <= BinaryEdge.END_COUNT : String.format("Search item undefined for hyperedge", edge);
	}
	
	@Override
	public Record get(Matcher matcher) {
		return new VarEdgeRecord((RegExprMatcher) matcher);
	}
	
	protected final String var;
}
