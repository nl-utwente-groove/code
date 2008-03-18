package groove.explore.result;


/** This result does not collect anything and is never done.
 * Thus exploration is done when there's nothing left to explore.
 * To be combined with an {@link EmptyAcceptor}.
 * @author 
 */
public class EmptyResult<T> extends Result<T> {

	@Override
	public boolean done() {
		// this result does not collect anything and is never done
		// thus exploration is done when there's nothing left to explore
		return false;
	}

}
