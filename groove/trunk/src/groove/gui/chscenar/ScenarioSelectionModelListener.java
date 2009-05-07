package groove.gui.chscenar;

/** To be implemented by classes that 
 * want to be notified of changes on a {@link ScenarioSelectionModel} 
 * @author Iovka Boneva
 *
 */
public interface ScenarioSelectionModelListener {
	
	/** Indicates a state change. */
	public void stateChanged (ScenarioSelectionModel model);

}
