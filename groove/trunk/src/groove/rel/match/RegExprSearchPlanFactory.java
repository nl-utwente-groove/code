/*
 * $Id: RegExprSearchPlanFactory.java,v 1.3 2007-04-18 08:36:08 rensink Exp $
 */
package groove.rel.match;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import groove.graph.DefaultEdge;
import groove.graph.Edge;
import groove.graph.Label;
import groove.graph.Node;
import groove.graph.match.DefaultSearchPlanFactory;
import groove.graph.match.SearchItem;
import groove.rel.RegExpr;
import groove.rel.RegExprLabel;
import groove.rel.match.RegExprEdgeSearchItem;
import groove.rel.match.VarEdgeSearchItem;

/**
 * Strategy that yields the edges in order of ascending indegree of
 * their source nodes.
 * Furthermore, regular expression edges are saved to the last.
 * @author Arend Rensink
 * @version $Revision: 1.3 $
 */
public class RegExprSearchPlanFactory extends DefaultSearchPlanFactory {
	/**
	 * Edge comparator for regular expression edges.
	 * An edge is better if it is not regular, or if the automaton is not reflexive.
	 * @author Arend Rensink
	 * @version $Revision $
	 */
    private class RegExprComparator implements Comparator<Edge> {
		/**
		 * Compares two labels, with the purpose of determining which one should
		 * be tried first. The rules are as follows:
		 * <ul>
		 * <li> Regular expression labels are worse than others
		 * <li> Reflexive regular expressions are worse than others
		 * </ul>
		 */
		public int compare(Edge o1, Edge o2) {
			Label first = o1.label();
			Label second = o2.label();
			int result = compare(second instanceof RegExprLabel,
					first instanceof RegExprLabel);
			if (result == 0 && first instanceof RegExprLabel) {
				// remove the potential outer negation from the labels
				if (RegExprLabel.isNeg(first)) {
					first = RegExprLabel.getNegOperand(first).toLabel();
				}
				if (RegExprLabel.isNeg(second)) {
					second = RegExprLabel.getNegOperand(second).toLabel();
				}
				result = compare(!((RegExprLabel) first).getAutomaton().isAcceptsEmptyWord(),
						!((RegExprLabel) second).getAutomaton().isAcceptsEmptyWord());
			}
			return result;
		}

		/**
		 * Compares two booleans, and returns the result of the comparison as an integer.
		 * @return <code>+1</code> if <code>first</code> is <code>true</code>
		 * but <code>second</code> is not, <code>-1</code> if the reverse is the case,
		 * and <code>0</code> if their values are equal.
		 */
		protected int compare(boolean first, boolean second) {
		    return first ? (second ? 0 : +1) : (second ? -1 : 0);
		}
	}
    
    /**
     * Adds a comparator that makes sure regular expressions are scheduled later.
     */
    @Override
	protected List<Comparator<Edge>> createComparators(Set<? extends Node> nodeSet, Set<? extends Edge> edgeSet) {
    	List<Comparator<Edge>> result = super.createComparators(nodeSet, edgeSet);
    	result.add(0, new RegExprComparator());
    	return result;
	}

    /**
     * Creates a {@link VarEdgeSearchItem} or {@link RegExprEdgeSearchItem} as
     * dictated by the parameter, or calls the super method otherwise.
     */
    @Override
    protected SearchItem createEdgeSearchItem(Edge edge, boolean[] matched) {
    	Label label = edge.label();
    	RegExpr negOperand = RegExprLabel.getNegOperand(label);
    	if (negOperand instanceof RegExpr.Empty) {
    		return createInjectionSearchItem(Arrays.asList(edge.ends()));
    	} else if (negOperand != null) {
    		Edge negatedEdge = DefaultEdge.createEdge(edge.source(), negOperand.toLabel(), edge.opposite());
    		return createNegatedSearchItem(createEdgeSearchItem(negatedEdge, matched));
//    	} else if (RegExprLabel.isEmpty(label)) {
//    		return createInjectionSearchItem(Arrays.asList(edge.ends()));
    	} else if (RegExprLabel.getWildcardId(label) != null) {
    		return new VarEdgeSearchItem(edge, matched);
    	} else if (RegExprLabel.isAtom(label)) {
    		Edge defaultEdge = DefaultEdge.createEdge(edge.source(), RegExprLabel.getAtomText(label), edge.opposite());
    		return super.createEdgeSearchItem(defaultEdge, matched);
    	} else if (label instanceof RegExprLabel) {
    		return new RegExprEdgeSearchItem(edge, matched);
    	} else {
        	return super.createEdgeSearchItem(edge, matched);
    	}
    }
    
    /**
     * Callback factory method for a negated search item.
     * @param inner the internal search item which this one negates
     * @return an instance of {@link NegatedSearchItem}
     */
    protected NegatedSearchItem createNegatedSearchItem(SearchItem inner) {
    	return new NegatedSearchItem(inner);
    }
    
    /**
     * Callback factory method for an injection search item.
     * @param injection the first node to be matched injectively
     * @return an instance of {@link InjectionSearchItem}
     */
    protected InjectionSearchItem createInjectionSearchItem(Collection<? extends Node> injection) {
    	return new InjectionSearchItem(injection);
    }    
}
