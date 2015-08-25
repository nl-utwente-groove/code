package groove.gui.action;

/** 
 * Interface for visual components that should be refreshed upon changes in the simulator model. 
 */
public interface Refreshable {
    /**
     * Callback method to refresh attributes of the component such as its name
     * and enabledness status.
     */
    void refresh();
}
