/* $Id: VarEdgeSearchItem.java,v 1.1.1.2 2007-03-20 10:42:54 kastenberg Exp $ */
package groove.rel.match;

import groove.graph.Edge;
import groove.graph.Label;
import groove.graph.match.EdgeSearchItem;
import groove.graph.match.Matcher;
import groove.rel.VarEdge;

/**
 * A search item that searches an image for an edge.
 * @author Arend Rensink
 * @version $Revision $
 */
public class VarEdgeSearchItem extends EdgeSearchItem<VarEdge> {
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
//
//		@Override
//		protected void setSelected(Edge image) {
//			assert ! varPreMatched || matcher.getVar(var) != null;
//			if (!varPreMatched) {
//				matcher.putVar(var, image.label());
//			}
//			super.setSelected(image);
//		}
//		
//		@Override
//		protected void resetSelected() {
//			assert matcher.getVar(var).equals(getSelected().label()) : String.format("Wrong image %s for variable %s: should be %s", matcher.getVar(var), var, getSelected().label());
//			if (!varPreMatched) {
//				Label oldImage = matcher.getValuation().remove(var);
//				assert oldImage != null;
//			}
//			super.resetSelected();
//		}

		/** 
		 * Flag indicating if {@link VarEdgeSearchItem#var} has received
		 * a fresh image in this record.
		 */
		private final boolean varPreMatched;
	}

	public VarEdgeSearchItem(VarEdge edge, boolean... matched) {
		super(edge, matched);
		this.var = edge.var();
	}
	
	public Record get(Matcher matcher) {
		return new VarEdgeRecord((RegExprMatcher) matcher);
	}
	
	protected final String var;
}
