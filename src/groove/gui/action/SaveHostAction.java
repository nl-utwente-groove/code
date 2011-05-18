package groove.gui.action;

import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.io.FileType;

/**
 * Action to save the host graph.
 */
public class SaveHostAction extends SimulatorAction {
    /** Constructs an instance of the action. */
    public SaveHostAction(Simulator simulator) {
        super(simulator, Options.SAVE_STATE_ACTION_NAME, Icons.SAVE_ICON);
        putValue(ACCELERATOR_KEY, Options.SAVE_KEY);
    }

    @Override
    public boolean execute() {
        getActions().getSaveGraphAction().actionForGraphs(
            getModel().getHost().getAspectGraph(), FileType.STATE_FILTER);
        return false;
    }

    /**
     * Tests if the action should be enabled according to the current state
     * of the simulator, and also modifies the action name.
     */
    @Override
    public void refresh() {
        setEnabled(getModel().getState() != null
            || getModel().getHost() != null);
        if (getModel().getHost() == null) {
            putValue(NAME, Options.SAVE_STATE_ACTION_NAME);
            putValue(SHORT_DESCRIPTION, Options.SAVE_STATE_ACTION_NAME);
        } else {
            putValue(NAME, Options.SAVE_GRAPH_ACTION_NAME);
            putValue(SHORT_DESCRIPTION, Options.SAVE_GRAPH_ACTION_NAME);
        }
    }
}