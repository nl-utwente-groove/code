package groove.gui.action;

import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;

/**
 * Action to cancel editing the currently displayed control program.
 */
public class CancelEditControlAction extends SimulatorAction {
    /** Constructs a new action, for a given control panel. */
    public CancelEditControlAction(Simulator simulator) {
        super(simulator, Options.CANCEL_EDIT_ACTION_NAME, Icons.CANCEL_ICON);
        getControlPanel().addRefreshable(this);
    }

    @Override
    public boolean execute() {
        getControlPanel().cancelEditing(true);
        return false;
    }

    @Override
    public void refresh() {
        setEnabled(getControlPanel().isEditing());
    }
}