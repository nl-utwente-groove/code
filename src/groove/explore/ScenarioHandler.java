package groove.explore;

import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.ProductGTS;

import java.util.Collection;

/** An object holding a scenario. Should be implemented for any pre-defined scenario.
 * Is used as an interface between tools using a scenario (e.g. {@link groove.util.Generator}, 
 * {@link groove.gui.Simulator}) and the pre-defined scenario.
 * @author Iovka Boneva
 *
 */
public interface ScenarioHandler {

	/** Creates and launches the scenario this handler is responsible for. 
	 * @throws InterruptedException 
	 */
	public void playScenario() throws InterruptedException;
	
	/** A short description of the scenario. 
	 * @return A short description of the scenario. 
	 */
	public String getDescription();
	
	/** A short name for the scenario. 
	 * @return A short name for the scenario.
	 */
	public String getName();
	
	/** Returns the result of the scenario. 
	 * Is valid only after the Scenario is completed. 
	 * @return 
	 */
	public Collection<? extends Object> getResult();
	
	/** The type of the result of this scenario.
	 * @return
	 */
	public Class<?> resultType();
	
	/** Sets the state where the scenario should start exploring. 
	 * @param state The state where the scenario should start exploring.
	 */
	public void setState (GraphState state);
	
	/** Sets the transition system on which the scenario works. 
	 * @param gts The transition system on which the scenario works. 
	 */
	public void setGTS (GTS gts);	

	public ProductGTS getProductGTS();
}
