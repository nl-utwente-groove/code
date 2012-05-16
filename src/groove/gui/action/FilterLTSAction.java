package groove.gui.action;

import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;

/**
 * Action for filtering the LTS JGraph. 
 */
public class FilterLTSAction extends SimulatorAction {
    /** Constructs a new action, for a given simulator. */
    public FilterLTSAction(Simulator simulator, boolean animated) {
        super(simulator, Options.FILTER_LTS_NAME, Icons.FILTER_LTS_ICON);
    }

    @Override
    public void execute() {
        if (getLtsDisplay().getLtsModel() != null) {
            getLtsDisplay().getLtsModel().hideGTS();
            getLtsDisplay().getLtsJGraph().refreshAllCells();
        }
    }

    @Override
    public void refresh() {
        if (getLtsDisplay().getLtsModel() != null
            && !getLtsDisplay().getLtsModel().getGraph().getResultStates().isEmpty()) {
            setEnabled(true);
        }
    }
}