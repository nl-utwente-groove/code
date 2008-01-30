/* $Id: GraphPredicateOutcome.java,v 1.4 2008-01-30 09:32:42 iovka Exp $ */
package groove.trans;

/**
 * A specialised interface that models the outcome of a graph predicate.
 * @author Arend Rensink
 * @version $Revision $
 */
@Deprecated
public interface GraphPredicateOutcome extends GraphTestOutcome<GraphCondition,Matching> {
	// nothing but a specialised interface
}
