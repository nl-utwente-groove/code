/* $Id: GraphPredicateOutcome.java,v 1.3 2007-10-05 08:31:38 rensink Exp $ */
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
