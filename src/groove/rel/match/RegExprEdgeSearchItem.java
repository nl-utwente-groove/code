/* $Id: RegExprEdgeSearchItem.java,v 1.1.1.2 2007-03-20 10:42:54 kastenberg Exp $ */
package groove.rel.match;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import groove.graph.Edge;
import groove.graph.Label;
import groove.graph.Node;
import groove.graph.NodeEdgeMap;
import groove.graph.match.EdgeSearchItem;
import groove.graph.match.Matcher;
import groove.rel.Automaton;
import groove.rel.NodeRelation;
import groove.rel.RegExprLabel;
import groove.rel.ValuationEdge;
import groove.rel.VarAutomaton;

/**
 * A search item that searches an image for an edge.
 * @author Arend Rensink
 * @version $Revision $
 */
public class RegExprEdgeSearchItem extends EdgeSearchItem<Edge> {
	protected class RegExprEdgeRecord extends EdgeRecord<RegExprMatcher> {
		protected RegExprEdgeRecord(RegExprMatcher matcher) {
			super(matcher);
		}

		@Override
		public boolean select(Edge image) {
			if (super.select(image)) {
				if (image instanceof ValuationEdge) {
					// there's something more to be optimized here:
					// the search plan can predict wich variables are fresh
					// in the current valuation
					Map<String, Label> newValuation = ((ValuationEdge) image).getValue();
					freshVars = new HashSet<String>(newValuation.keySet());
					freshVars.removeAll(matcher.getValuation().keySet());
					matcher.putAllVar(newValuation);
				}
				return true;
			} else {
				return false;
			}
		}

		@Override
		public void undo() {
			super.undo();
			if (freshVars != null) {
	    		for (String var : freshVars) {
					matcher.getValuation().remove(var);
				}
			}
			freshVars = null;
		}
		
		@Override
		protected void initImages() {
			setMultiple(computeImageSet());
		}

		//
//		/**
//		 * Tests if a given edge can be accepted as image.
//		 * @param image the edge image to be tested
//		 * @return <code>true</code> if <code>image</code> is an acceptable image
//		 * for {@link RegExprEdgeSearchItem#edge}
//		 */
//		protected boolean approves(Edge image) {
//			return super.approves(image);
//		}
//		
		protected Collection<? extends Edge> computeImageSet() {
	    	NodeEdgeMap elementMap = matcher.getSingularMap();
	    	Node imageSource = elementMap.getNode(edge.source());
	    	Set<Node> imageSourceSet = imageSource == null ? null : Collections.singleton(imageSource);
	    	Node imageTarget = elementMap.getNode(edge.opposite());
	    	Set<Node> imageTargetSet = imageTarget == null ? null : Collections.singleton(imageTarget);
	        NodeRelation matches;
	        if (labelAutomaton instanceof VarAutomaton) {
	            matches = ((VarAutomaton) labelAutomaton).getMatches(matcher.cod(), imageSourceSet, imageTargetSet, matcher.getValuation());            
	        } else {
	            matches = labelAutomaton.getMatches(matcher.cod(), imageSourceSet, imageTargetSet);
	        }
	        return matches.getAllRelated();
		}
		
		private Set<String> freshVars;
	}

	public RegExprEdgeSearchItem(Edge edge, boolean... matched) {
		super(edge, matched);
		this.labelAutomaton = ((RegExprLabel) edge.label()).getAutomaton();
	}
	
	public Record get(Matcher matcher) {
		return new RegExprEdgeRecord((RegExprMatcher) matcher);
	}
	
	/**
	 * The automaton that computes the matches for the underlying edge.
	 */
	protected final Automaton labelAutomaton;
}
