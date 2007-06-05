/* $Id: StateFunction.java,v 1.1 2007-04-27 22:06:59 rensink Exp $ */
package groove.calc;

import groove.lts.GraphState;

/**
 * Specialisation of {@link Function} that takes a {@link GraphState} as
 * input.
 * @author Arend Rensink
 * @version $Revision $
 */
abstract public class StateFunction<T> extends Function<GraphState,T> {
	// empty
}
