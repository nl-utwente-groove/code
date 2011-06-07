package groove.gui.action;

import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.trans.ResourceKind;

/**
 * Action to cancel editing the currently displayed control program.
 */
public class CancelEditAction extends SimulatorAction {
    /** Constructs a new action, for a given control panel. */
    public CancelEditAction(Simulator simulator, ResourceKind resource) {
        super(simulator, Options.CANCEL_EDIT_ACTION_NAME, Icons.CANCEL_ICON,
            null, resource);
    }

    @Override
    public boolean execute() {
        getDisplay().cancelEditResource(
            getSimulatorModel().getSelected(getResourceKind()), true);
        return false;
    }

    @Override
    public void refresh() {
        setEnabled(true);
    }
}
