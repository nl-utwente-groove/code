/* $Id: VarEdgeSearchItem.java,v 1.1 2007-08-24 17:34:56 rensink Exp $ */
package groove.match;

import groove.graph.BinaryEdge;
import groove.graph.DefaultEdge;
import groove.graph.DefaultFlag;
import groove.graph.Edge;
import groove.graph.Label;
import groove.graph.Node;
import groove.rel.RegExprLabel;
import groove.rel.VarNodeEdgeMap;

import static groove.match.SearchPlanStrategy.Search;

/**
 * A search item that searches an image for an edge.
 * @author Arend Rensink
 * @version $Revision $
 */
public class VarEdgeSearchItem extends EdgeSearchItem {
	/** Record for this type of search item. */
	protected class VarEdgeRecord extends EdgeRecord {
		/** Constructs a new record, for a given matcher. */
		protected VarEdgeRecord(Search search) {
			super(search);
			varPreMatched = search.getResult().getVar(var) != null;
		}
		
		/**
		 * Tests if a given edge can be accepted as image.
		 * @param image the edge image to be tested
		 * @return <code>true</code> if <code>image</code> is an acceptable image
		 * for {@link VarEdgeSearchItem#edge}
		 */
		@Override
		boolean select(Edge image) {
			boolean result = image.endCount() == edge.endCount() && super.select(image);
			if (result) {
				assert ! varPreMatched || getResult().getVar(var) != null;
				if (!varPreMatched) {
                    getResult().putVar(var, image.label());
				}
			}
			return result;
		}

		@Override
		void undo() {
			assert getResult().getVar(var).equals(selected.label()) : String.format("Wrong image %s for variable %s: should be %s", getResult().getVar(var), var, selected.label());
			super.undo();
			if (!varPreMatched) {
				Label oldImage = getResult().getValuation().remove(var);
				assert oldImage != null;
			}
		}

		@Override
		void init() {
			if (varPreMatched && isAllPreMatched()) {
				Edge image = edge.imageFor(getResult());
				if (getTarget().containsElement(image)) {
					setSingular(image);
				} else {
					setSingular(null);
				}
			} else if (varPreMatched) {
		        setMultiple(getTarget().labelEdgeSet(edge.endCount(), getResult().getVar(var)));
			} else {
				setMultiple(getTarget().edgeSet());
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

	/** 
	 * Constructs a new search item.
	 * The item will match any edge between the end images, and record
	 * the edge label as value of the wildcard variable.
	 */
	public VarEdgeSearchItem(Edge edge, boolean... matched) {
		super(edge, matched);
		this.var = RegExprLabel.getWildcardId(edge.label());
		assert this.var != null : String.format("Edge %s is not a variable edge", edge);
		assert edge.endCount() <= BinaryEdge.END_COUNT : String.format("Search item undefined for hyperedge", edge);
	}
	
	@Override
	public EdgeRecord getRecord(Search search) {
		return new VarEdgeRecord(search);
	}
	
	/** The variable bound in the wildcard (not <code>null</code>). */
	protected final String var;
}
