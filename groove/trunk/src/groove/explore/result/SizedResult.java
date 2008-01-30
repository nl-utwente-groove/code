package groove.explore.result;


/** A result that is completed when the number of collected
 * elements is big enough.
 * @author 
 *
 * @param <T>
 */
public class SizedResult<T> extends Result<T> {

	/** The number of elements to be collected. */
	protected int size;
	
	
	/** Creates a sized result by specifying the number of elements to be collected.
	 * @param size The number of elements to be collected. 
	 */
	public SizedResult(int size) {
		this.size = size;
	}
	
	@Override
	public boolean done() {
		return (elements.size() >= this.size);
	}
}
