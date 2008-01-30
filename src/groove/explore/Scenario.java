package groove.explore;

import groove.explore.result.Result;
import groove.lts.GTS;
import groove.lts.GraphState;

/** A scenario for exploring a (a part of) graph transition system
 * yielding a result.
 * A scenario is a combination of a {@link groove.explore.strategy.Strategy},
 * a {@link groove.explore.result.Acceptor} and a {@link groove.explore.result.Result}.
 * Playing a scenario consists in repeating the {@link groove.explore.strategy.Strategy#next()}
 * method as long as it returns <code>true</code> and the result is 
 * not {@link groove.explore.result.Result#done()}.
 * A scenario works on a {@link groove.lts.GTS} and starts exploration in a pre-defined state. 
 *  
 * @author Iovka Boneva
 * @author Tom Staijen
 * @param <T> The type of the result.
 */
public interface Scenario<T> {

	
	/** Sets the  {@link groove.lts.GTS} on which this scenario works. 
	 * @param gts the  {@link groove.lts.GTS} on which this scenario works. 
	 */
	public void setGTS(GTS gts);
	/** Sets the start state for this scenario. 
	 * @param state the start state for this scenario. 
	 */
	public void setState(GraphState state);
	
	/** Plays the scenario, yielding a result.
	 * @return the result of the scenario.
	 */
	public Result<T> play();
	
	
//	public void setResult(Result<T> prototype);
}
