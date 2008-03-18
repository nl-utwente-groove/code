package groove.explore.result;

import groove.lts.LTSAdapter;
 
/** Listens to a GTS and adds accepted elements to a result.
 * @author 
 *
 * @param <T> The type of the elements added to the result. 
 * May be e.g. states of the GTS, paths of the GTS
 */
public abstract class Acceptor<T> extends LTSAdapter {

	private Result<T> result;
	
	/** Sets the result that collects accepted elements. 
	 * @param result the result that collects accepted elements
	 */
	public void setResult(Result<T> result) {
		this.result = result;
	}
	
	/** Retrieves the result.
	 * @return The result
	 */
	public Result<T> getResult() {
		return this.result;
	}
}
