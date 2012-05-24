package groove.gui.action;

import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;

/**
 * Action for filtering the LTS JGraph. 
 */
public class FilterLTSAction extends SimulatorAction {
    /** Constructs a new action, for a given simulator. */
    public FilterLTSAction(Simulator simulator) {
        super(simulator, Options.FILTER_LTS_ACTION_NAME, Icons.FILTER_LTS_ICON);
    }

    @Override
    public void execute() {
        getLtsDisplay().getLTSTab().toggleFilterLts();
    }

    @Override
    public void refresh() {
        boolean enabled =
            getSimulatorModel().getGts() != null
                && !getLtsDisplay().isHiddingLts();
        setEnabled(enabled);
    }

}